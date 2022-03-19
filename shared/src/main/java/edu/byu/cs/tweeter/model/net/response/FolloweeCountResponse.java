package edu.byu.cs.tweeter.model.net.response;

public class FolloweeCountResponse extends Response{
    private int count;

    FolloweeCountResponse(boolean success) {
        super(success);
    }

    FolloweeCountResponse(boolean success, String message) {
        super(success, message);
    }

    public FolloweeCountResponse(int count) {
        super(true);
        this.count = count;
    }

    public int getCount() {
        return count;
    }
}
