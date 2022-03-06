package edu.byu.cs.tweeter.model.net.response;

public class IsFollowerResponse extends Response{
    boolean isFollower;

    IsFollowerResponse(boolean success) {
        super(success);
    }

    public IsFollowerResponse(boolean success, String message) {
        super(success, message);
    }

    public IsFollowerResponse(String isFollower) {
        super(true);
        if (isFollower.equals("yes")) {
            this.isFollower = true;
        }
        else {
            this.isFollower = false;
        }
    }

    public boolean isFollower() {
        return isFollower;
    }
}
