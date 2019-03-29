package bach.project.apis;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import jdk.incubator.http.HttpClient;
import jdk.incubator.http.HttpRequest;
import jdk.incubator.http.HttpResponse;
import org.bson.internal.Base64;
import org.springframework.security.access.AuthorizationServiceException;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.AbstractMap;
import java.util.List;

public class APIConnectorImpl implements APIConnector {
    public byte[] sendRequestToAPI(List<AbstractMap.SimpleEntry<String, String>> comments, String apiURL, String apiSecret) {
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
}
