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
import edu.byu.cs.tweeter.server.dao.AuthTokenDAO;
import edu.byu.cs.tweeter.server.dao.UserDAO;
import edu.byu.cs.tweeter.server.factory.DAOFactory;
import edu.byu.cs.tweeter.util.FakeData;

public class UserService {
    private DAOFactory factory;
    public UserService (DAOFactory factory) {
        this.factory = factory;
    }

    public LoginResponse login(LoginRequest request) {
        if(request.getUsername() == null){
            throw new RuntimeException("[BadRequest] Missing a username");
        } else if(request.getPassword() == null) {
            throw new RuntimeException("[BadRequest] Missing a password");
        }

        UserDAO uDAO = factory.getUserDAO();
        AuthTokenDAO aDAO = factory.getAuthTokenDAO();
        User user = uDAO.login(request);
        AuthToken token = aDAO.createToken(request.getUsername());

        return new LoginResponse(user, token);
    }

    public LogoutResponse logout(LogoutRequest request) {
        if (request.getAuthToken() == null) {
            throw new RuntimeException("[BadRequest] Missing authToken");
        }

        AuthTokenDAO aDAO = factory.getAuthTokenDAO();
        aDAO.deleteToken(request.getAuthToken());
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

        UserDAO uDAO = factory.getUserDAO();
        AuthTokenDAO aDAO = factory.getAuthTokenDAO();
        User user = uDAO.register(request);
        AuthToken token = aDAO.createToken(request.getUsername());
        return new RegisterResponse(user, token);
    }

    public UserResponse getUser(UserRequest request) {
        if (request.getTargetUserAlias() == null) {
            throw new RuntimeException("[BadRequest] Request needs to have a target alias");
        }
        UserDAO uDAO = factory.getUserDAO();
        return uDAO.getUser(request);
    }
}
