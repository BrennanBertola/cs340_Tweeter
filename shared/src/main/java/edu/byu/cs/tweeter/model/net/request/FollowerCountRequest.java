package edu.byu.cs.tweeter.model.net.request;

import edu.byu.cs.tweeter.model.domain.AuthToken;

public class FollowerCountRequest extends Request{
    private FollowerCountRequest () {};

    public FollowerCountRequest(AuthToken authToken, String target) {
        this.authToken = authToken;
        this.targetUserAlias = target;
    }
}
