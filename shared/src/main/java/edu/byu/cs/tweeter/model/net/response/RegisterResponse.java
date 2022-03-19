package edu.byu.cs.tweeter.model.net.response;

import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.User;

public class RegisterResponse extends Response {
    private User user;
    private AuthToken authToken;

    public RegisterResponse(boolean success) {
        super(success);
    }

    public RegisterResponse(boolean success, String message) {
        super(success, message);
    }

    public RegisterResponse(User user, AuthToken authToken) {
        super(true, null);
        this.user = user;
        this.authToken = authToken;
    }

    public User getUser() {
        return user;
    }

    public AuthToken getAuthToken() {
        return authToken;
    }
}
