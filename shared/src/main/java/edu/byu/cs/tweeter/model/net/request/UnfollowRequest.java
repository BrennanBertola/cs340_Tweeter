package edu.byu.cs.tweeter.model.net.request;

import edu.byu.cs.tweeter.model.domain.AuthToken;

public class UnfollowRequest extends Request {
    public UnfollowRequest() {}

    public UnfollowRequest(AuthToken authToken, String targetUserAlias) {
        super(targetUserAlias, authToken);
    }
}
