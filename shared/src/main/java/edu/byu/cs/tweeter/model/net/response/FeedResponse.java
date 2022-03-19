package edu.byu.cs.tweeter.model.net.response;

import java.util.List;
import java.util.Objects;

import edu.byu.cs.tweeter.model.domain.Status;

public class FeedResponse extends PagedResponse {

    private List<Status> posts;

    public FeedResponse (String message) {super(false, message, false);}

    public FeedResponse(List<Status> items, boolean hasMorePages) {
        super(true, hasMorePages);
        this.posts = items;
    }

    public List<Status> getPosts() {
        return posts;
    }

    @Override
    public boolean equals(Object param) {
        if (this == param) {
            return true;
        }

        if (param == null || getClass() != param.getClass()) {
            return false;
        }

        FeedResponse that = (FeedResponse) param;

        return (Objects.equals(posts, that.posts) &&
                Objects.equals(this.getMessage(), that.getMessage()) &&
                this.isSuccess() == that.isSuccess());
    }

    @Override
    public int hashCode() {
        return Objects.hash(posts);
    }
}
