package edu.byu.cs.tweeter.model.net.request;

import edu.byu.cs.tweeter.model.domain.AuthToken;

public class FollowRequest extends Request {
    private FollowRequest() {}

    public FollowRequest(AuthToken authToken, String targetUserAlias) {
        this.authToken = authToken;
        this.targetUserAlias = targetUserAlias;
    }
}
