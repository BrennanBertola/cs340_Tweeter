package edu.byu.cs.tweeter.model.net.request;

import edu.byu.cs.tweeter.model.domain.AuthToken;

public class FolloweeCountRequest {

    private AuthToken authToken;
    private String target;


    private FolloweeCountRequest() {}

    public FolloweeCountRequest(AuthToken authToken, String target) {
        this.authToken = authToken;
        this.target = target;
    }


    public AuthToken getAuthToken() {
        return authToken;
    }


    public void setAuthToken(AuthToken authToken) {
        this.authToken = authToken;
    }

    public String getTarget() {
        return target;
    }

    public void setTarget(String target) {
        this.target = target;
    }
}
