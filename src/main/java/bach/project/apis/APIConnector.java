package bach.project.apis;

import java.util.AbstractMap;
import java.util.List;

public interface APIConnector {
    byte[] sendRequestToAPI(List<AbstractMap.SimpleEntry<String, String>> comments, String apiURL, String apiSecret);
}
