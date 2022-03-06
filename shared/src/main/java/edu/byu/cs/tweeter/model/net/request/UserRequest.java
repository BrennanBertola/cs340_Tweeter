package edu.byu.cs.tweeter.model.net.request;

import edu.byu.cs.tweeter.model.domain.AuthToken;

public class UserRequest extends Request {
    private UserRequest () {};

    public UserRequest(AuthToken authToken, String target) {
        this.authToken = authToken;
        this.targetUserAlias = target;
    }
}
