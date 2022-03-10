package edu.byu.cs.tweeter.server.service;

import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.User;
import edu.byu.cs.tweeter.model.net.request.LoginRequest;
import edu.byu.cs.tweeter.model.net.request.LogoutRequest;
import edu.byu.cs.tweeter.model.net.request.RegisterRequest;
import edu.byu.cs.tweeter.model.net.request.UserRequest;
import edu.byu.cs.tweeter.model.net.response.LoginResponse;
import edu.byu.cs.tweeter.model.net.response.LogoutResponse;
import edu.byu.cs.tweeter.model.net.response.RegisterResponse;
import edu.byu.cs.tweeter.model.net.response.UserResponse;
import edu.byu.cs.tweeter.util.FakeData;

public class UserService {

    public LoginResponse login(LoginRequest request) {
        if(request.getUsername() == null){
            throw new RuntimeException("[BadRequest] Missing a username");
        } else if(request.getPassword() == null) {
            throw new RuntimeException("[BadRequest] Missing a password");
        }

        // TODO: Generates dummy data. Replace with a real implementation.
        User user = getDummyLogin();
        AuthToken authToken = getDummyAuthToken();
        return new LoginResponse(user, authToken);
    }

    public LogoutResponse logout(LogoutRequest request) {
        if (request.getAuthToken() == null) {
            throw new RuntimeException("[BadRequest] Missing authToken");
        }

        return new LogoutResponse(true);
    }

    public RegisterResponse register(RegisterRequest request) {
        if (request.getFirstName() == null) {
            throw new RuntimeException("[BadRequest] Missing a first name");
        }
        else if (request.getLastName() == null) {
            throw new RuntimeException("[BadRequest] Missing a last name");
        }
        else if (request.getUsername() == null) {
            throw new RuntimeException("[BadRequest] Missing a username");
        }
        else if (request.getPassword() == null) {
            throw new RuntimeException("[BadRequest] Missing a password");
        }
        else if (request.getImage() == null) {
            throw new RuntimeException("[BadRequest] Missing an image");
        }

        User user = getDummyRegister();
        AuthToken authToken = getDummyAuthToken();
        return new RegisterResponse(user, authToken);
    }

    public UserResponse getUser(UserRequest request) {
        if (request.getTargetUserAlias() == null) {
            throw new RuntimeException("[BadRequest] Request needs to have a target alias");
        }
        return new UserResponse(getDummyUser(request.getTargetUserAlias()));
    }
    /**
     * Returns the dummy user to be returned by the login operation.
     * This is written as a separate method to allow mocking of the dummy user.
     *
     * @return a dummy user.
     */
    User getDummyLogin() {
        return getFakeData().getFirstUser();
    }
    User getDummyRegister() {
        return getFakeData().getFirstUser();
    }
    User getDummyUser(String alias) {return getFakeData().findUserByAlias(alias);}
    /**
     * Returns the dummy auth token to be returned by the login operation.
     * This is written as a separate method to allow mocking of the dummy auth token.
     *
     * @return a dummy auth token.
     */
    AuthToken getDummyAuthToken() {
        return getFakeData().getAuthToken();
    }

    /**
     * Returns the {@link FakeData} object used to generate dummy users and auth tokens.
     * This is written as a separate method to allow mocking of the {@link FakeData}.
     *
     * @return a {@link FakeData} instance.
     */
    FakeData getFakeData() {
        return new FakeData();
    }
}
