package edu.byu.cs.tweeter.model.net.response;

import edu.byu.cs.tweeter.model.domain.User;
import edu.byu.cs.tweeter.model.net.request.UserRequest;

public class UserResponse extends Response {
    private User user;

    UserResponse(boolean success) {
        super(success);
    }

    UserResponse(boolean success, String message) {
        super(success, message);
    }

    public UserResponse(User user) {
        super(true);
        this.user = user;
    }

    public User getUser() {
        return user;
    }
}
