package edu.byu.cs.tweeter.model.net.request;

import edu.byu.cs.tweeter.model.domain.AuthToken;

public class StoryRequest  {

    private AuthToken authToken;
    private String target;
    private int limit;
    private String last;

    private StoryRequest() {}


    public StoryRequest(AuthToken authToken, String target, int limit, String last) {
        this.authToken = authToken;
        this.target = target;
        this.limit = limit;
        this.last = last;
    }


    public AuthToken getAuthToken() {
        return authToken;
    }


    public void setAuthToken(AuthToken authToken) {
        this.authToken = authToken;
    }



    public int getLimit() {
        return limit;
    }


    public void setLimit(int limit) {
        this.limit = limit;
    }

    public String getTarget() {
        return target;
    }

    public void setTarget(String target) {
        this.target = target;
    }

    public String getLast() {
        return last;
    }

    public void setLast(String last) {
        this.last = last;
    }
}
