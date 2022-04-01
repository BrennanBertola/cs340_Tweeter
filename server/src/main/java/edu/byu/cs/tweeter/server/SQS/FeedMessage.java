package edu.byu.cs.tweeter.server.SQS;

import java.util.List;

import edu.byu.cs.tweeter.model.domain.Status;
import edu.byu.cs.tweeter.model.domain.User;

public class FeedMessage {
    List<String> toUpdate;
    Status post;

    public FeedMessage(List<String> toUpdate, Status post) {
        this.toUpdate = toUpdate;
        this.post = post;
    }

    public FeedMessage() {
    }


    public List<String> getToUpdate() {
        return toUpdate;
    }

    public void setToUpdate(List<String> toUpdate) {
        this.toUpdate = toUpdate;
    }

    public Status getPost() {
        return post;
    }

    public void setPost(Status post) {
        this.post = post;
    }
}
