package bach.project.apis;

import bach.project.utils.AppProperties;
import net.dean.jraw.RedditClient;
import net.dean.jraw.references.CommentsRequest;

import java.util.ArrayList;
import java.util.List;

public class RedditAPIConnector implements CommentAPIConnector{
    private static final String REGEXP = AppProperties.REDDIT_LINK_REGEXP;
    private final RedditClient redditClient;

    public RedditAPIConnector(RedditClient redditClient){
        this.redditClient=redditClient;
    }

    private String parseLink(String link) {
        return link.replaceAll(REGEXP, "$1");
    }

    @Override
    public List<String> getComments(String link, int amount) {
        CommentsRequest commentsRequest = new CommentsRequest.Builder().depth(1).limit(amount).build();
        ArrayList<String> commentBodyArrayList = new ArrayList<>();
        redditClient.submission(parseLink(link)).comments(commentsRequest).walkTree().iterator().forEachRemaining(e -> commentBodyArrayList.add(e.getSubject().getBody()));

        return commentBodyArrayList;
    }

    @Override
    public String getTitle(String link) {
        return redditClient.submission(parseLink(link)).inspect().getTitle();
    }
}
