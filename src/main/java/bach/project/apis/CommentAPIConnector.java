package bach.project.apis;

import java.util.Collection;
import java.util.List;

public interface CommentAPIConnector {
    List<String> getComments(String link,int amount);
    String getTitle(String link);
}
