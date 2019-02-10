package bach.project.service;

import bach.project.apis.APIConnector;
import bach.project.apis.CommentAPIConnector;
import bach.project.bean.model.Comment;
import bach.project.bean.model.Link;
import bach.project.bean.model.User;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.util.stream.IntStream.range;

@Service
public class PredictionServiceImpl implements PredictionService {
    private final CommentAPIConnector commentAPIConnector;
    private final CommentService commentService;
    private final APIConnector apiConnector;
    private final String API_URL;
    private final String API_SECRET;

    public PredictionServiceImpl(CommentService commentService, CommentAPIConnector commentAPIConnector, APIConnector apiConnector,@Value("${ml.api.url}") String api_url, @Value("${ml.api.user}") String user_name, @Value("${ml.api.secret}") String api_secret) {
        this.commentService = commentService;
        this.commentAPIConnector=commentAPIConnector;
        this.apiConnector=apiConnector;
        this.API_URL = api_url.replace("{u_id}", user_name);
        this.API_SECRET = api_secret;
    }

    @Override
    public void predictSingleComment(User user, Optional<String> threadTitle, String singleComment) {
        Link link = new Link();
        link.setThreadTitle(threadTitle.filter(s -> s.length() != 0).orElse(null));

        List<Comment> result = parseResponse(apiConnector.sendRequestToAPI(Collections.singletonList(new AbstractMap.SimpleEntry<>("0", singleComment)), API_URL, API_SECRET));
        result.get(0).setBody(singleComment);
        commentService.addNewPrediction(user.getId(), link, result);

    }

    @Override
    public void predictWholeLink(User user, String link, int percentage, int amount) {
        List<String> commentBodyArrayList = commentAPIConnector.getComments(link,amount);

        Stream<CompletableFuture<List<Comment>>> listStreamCF = range(0, commentBodyArrayList.size()).boxed().collect(Collectors.groupingBy(index -> index / 10)).values().stream()
                .map(chunk -> chunk.stream()
                        .map(chunkElt -> new AbstractMap.SimpleEntry<>(chunkElt.toString(), commentBodyArrayList.get(chunkElt))).collect(Collectors.toList()))
                .map(chunk -> CompletableFuture.supplyAsync(() -> apiConnector.sendRequestToAPI(chunk, API_URL, API_SECRET))).map(cF -> cF.orTimeout(25, TimeUnit.SECONDS))
                        .map(cF->cF.thenApply(this::parseResponse));

        String threadTitle = commentAPIConnector.getTitle(link);

        Stream<List<Comment>> listStream = listStreamCF.map(CompletableFuture::join);

        List<Comment> result = new ArrayList<>();
        listStream.forEach(result::addAll);
        result=result.stream().sorted(Comparator.comparingLong(Comment::getScore).reversed()).limit((percentage*result.size()/100)+1).collect(Collectors.toList());
        result.forEach(c -> c.setBody(commentBodyArrayList.get(Integer.parseInt(c.getBody()))));

        Link resultLink = new Link();
        resultLink.setPercentage(percentage);
        resultLink.setAmmount(amount);
        resultLink.setThreadTitle(threadTitle);

        commentService.addNewPrediction(user.getId(), resultLink, result);
    }

    private List<Comment> parseResponse(byte[] response) {
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
