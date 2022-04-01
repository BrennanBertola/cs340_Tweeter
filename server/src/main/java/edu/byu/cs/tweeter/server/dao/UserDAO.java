package edu.byu.cs.tweeter.server.dao;

import java.util.List;

import edu.byu.cs.tweeter.model.domain.User;
import edu.byu.cs.tweeter.model.net.request.FollowRequest;
import edu.byu.cs.tweeter.model.net.request.LoginRequest;
import edu.byu.cs.tweeter.model.net.request.RegisterRequest;
import edu.byu.cs.tweeter.model.net.request.UnfollowRequest;
import edu.byu.cs.tweeter.model.net.request.UserRequest;
import edu.byu.cs.tweeter.model.net.response.UserResponse;

public interface UserDAO extends DAO {
    User login(LoginRequest request);
    User register(RegisterRequest request);
    UserResponse getUser(UserRequest request);
    void addUserBatch(List<User> users);
    int getFollowerCount(String target);
    int getFollowingCount(String target);
    void addFollowCount(FollowRequest request);
    void subFollowCount(UnfollowRequest request);

}
