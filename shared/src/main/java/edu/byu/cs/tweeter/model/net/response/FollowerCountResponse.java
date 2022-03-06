package edu.byu.cs.tweeter.model.net.response;

public class FollowerCountResponse extends Response{
    private int count;

    FollowerCountResponse(boolean success) {
        super(success);
    }

    FollowerCountResponse(boolean success, String message) {
        super(success, message);
    }

    public FollowerCountResponse(int count) {
        super(true);
        this.count = count;
    }

    public int getCount() {
        return count;
    }
}
