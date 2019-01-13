package bach.project.service;

import bach.project.bean.model.Comment;
import bach.project.bean.model.Link;
import bach.project.bean.model.User;
import bach.project.utils.AppProperties;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import jdk.incubator.http.HttpClient;
import jdk.incubator.http.HttpRequest;
import jdk.incubator.http.HttpResponse;
import net.dean.jraw.RedditClient;
import net.dean.jraw.models.PublicContribution;
import net.dean.jraw.references.CommentsRequest;
import net.dean.jraw.tree.CommentNode;
import org.bson.internal.Base64;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.access.AuthorizationServiceException;
import org.springframework.stereotype.Service;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.util.stream.IntStream.range;

@Service
public class PredictionServiceImpl implements PredictionService {
    private final RedditClient redditClient;
    private final CommentService commentService;
    private final String API_URL;
    private final String API_SECRET;
    private static final String REGEXP = AppProperties.REDDIT_LINK_REGEXP;

    public PredictionServiceImpl(CommentService commentService, RedditClient redditClient, @Value("${ml.api.url}") String api_url, @Value("${ml.api.user}") String user_name, @Value("${ml.api.secret}") String api_secret) {
        this.commentService = commentService;
        this.redditClient = redditClient;
        this.API_URL = api_url.replace("{u_id}", user_name);
        this.API_SECRET = api_secret;
    }

    @Override
    public void predictSingleComment(User user, Optional<String> threadTitle, String singleComment) {
        Link link = new Link();
        link.setThreadTitle(threadTitle.filter(s -> s.length() != 0).orElse(null));

        List<Comment> result = parseResponse(sendRequestToAPI(Collections.singletonList(new AbstractMap.SimpleEntry<>("0", singleComment)), API_URL, API_SECRET));
        result.get(0).setBody(singleComment);
        commentService.addNewPrediction(user.getId(), link, result);

    }

    @Override
    public void predictWholeLink(User user, String link, int percentage, int ammount) {
        CommentsRequest commentsRequest = new CommentsRequest.Builder().depth(1).limit(ammount).build();
        ArrayList<String> commentBodyArrayList = new ArrayList<>();
        redditClient.submission(parseLink(link)).comments(commentsRequest).walkTree().iterator().forEachRemaining(e -> commentBodyArrayList.add(e.getSubject().getBody()));

        Stream<CompletableFuture<List<Comment>>> listStreamCF = range(0, commentBodyArrayList.size()).boxed().collect(Collectors.groupingBy(index -> index / 10)).values().stream()
                .map(chunk -> chunk.stream()
                        .map(chunkElt -> new AbstractMap.SimpleEntry<>(chunkElt.toString(), commentBodyArrayList.get(chunkElt))).collect(Collectors.toList()))
                .map(chunk -> CompletableFuture.supplyAsync(() -> sendRequestToAPI(chunk, API_URL, API_SECRET))).map(cF -> cF.orTimeout(25, TimeUnit.SECONDS))
                        .map(cF->cF.thenApply(PredictionServiceImpl::parseResponse));

        String threadTitle = redditClient.submission(parseLink(link)).inspect().getTitle();

        Stream<List<Comment>> listStream = listStreamCF.map(CompletableFuture::join);

        List<Comment> result = new ArrayList<>();
        listStream.forEach(result::addAll);
        result=result.stream().sorted(Comparator.comparingLong(Comment::getScore).reversed()).limit((percentage*result.size()/100)+1).collect(Collectors.toList());
        result.forEach(c -> c.setBody(commentBodyArrayList.get(Integer.parseInt(c.getBody()))));

        Link resultLink = new Link();
        resultLink.setPercentage(percentage);
        resultLink.setAmmount(ammount);
        resultLink.setThreadTitle(threadTitle);

        commentService.addNewPrediction(user.getId(), resultLink, result);
    }


    private String parseLink(String link) {
        return link.replaceAll(REGEXP, "$1");
    }

    private static byte[] sendRequestToAPI(List<AbstractMap.SimpleEntry<String, String>> comments, String apiURL, String apiSecret) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode rootNode = objectMapper.createObjectNode();
            comments.stream().filter(e->e.getValue()!=null && !e.getValue().equals("[deleted]") && !e.getValue().equals("[removed]")).forEach(entry -> ((ObjectNode) rootNode).put(entry.getKey(), entry.getValue()));
            byte[] bodyContentJSON = objectMapper.writeValueAsBytes(rootNode);

            SecretKeySpec signingKey = new SecretKeySpec(Base64.decode(apiSecret), "HmacSHA256");
            Mac mac = Mac.getInstance("HmacSHA256");
            mac.init(signingKey);
            String authHeader = Base64.encode(mac.doFinal(bodyContentJSON));
            System.out.println("HASH: " + authHeader);

            HttpRequest httpRequest = HttpRequest.newBuilder(new URI(apiURL)).header("Content-Type", "application/json").header("Auth", authHeader)
                    .POST(HttpRequest.BodyPublisher.fromByteArray(bodyContentJSON)).build();

            HttpResponse<byte[]> response = HttpClient.newBuilder().build().send(httpRequest, HttpResponse.BodyHandler.asByteArray());
            if (response.statusCode() == 401)
                throw new AuthorizationServiceException("Authorization with REST API failed");
            return response.body();
        } catch (NoSuchAlgorithmException | InvalidKeyException | URISyntaxException | IOException | InterruptedException e) {
            e.printStackTrace();
        }
        return new byte[]{};
    }

    private static List<Comment> parseResponse(byte[] response) {
        Iterator<Map.Entry<String, JsonNode>> fields;
        try {
            fields = new ObjectMapper().readTree(response).fields();
        } catch (IOException e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
        List<Comment> commentList = new LinkedList<>();
        while (fields.hasNext()) {
            Map.Entry<String, JsonNode> elt = fields.next();
            Comment comment = new Comment();
            comment.setBody(elt.getKey());
            comment.setRemoved(elt.getValue().get("shouldBeRemoved").asBoolean(false));
            comment.setScore(elt.getValue().get("score").asLong(-1));
            commentList.add(comment);
        }
        return commentList;
    }
}
