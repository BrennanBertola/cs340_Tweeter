package edu.byu.cs.tweeter.model.net.response;

public class IsFollowerResponse extends Response{
    public boolean isFollower = true;

    IsFollowerResponse(boolean success) {
        super(success);
    }

    public IsFollowerResponse(boolean success, String message) {
        super(success, message);
    }

    public IsFollowerResponse(int isFollower) {
        super(true);
        if (isFollower == 1) {
            this.isFollower = false;
        }
        else {
            this.isFollower = true;
        }
    }

    public boolean isFollower() {
        return isFollower;
    }
}
