package edu.byu.cs.tweeter.server.service;

import edu.byu.cs.tweeter.model.net.request.FollowRequest;
import edu.byu.cs.tweeter.model.net.request.FolloweeCountRequest;
import edu.byu.cs.tweeter.model.net.request.FollowerCountRequest;
import edu.byu.cs.tweeter.model.net.request.FollowerRequest;
import edu.byu.cs.tweeter.model.net.request.FollowingRequest;
import edu.byu.cs.tweeter.model.net.request.IsFollowerRequest;
import edu.byu.cs.tweeter.model.net.request.UnfollowRequest;
import edu.byu.cs.tweeter.model.net.response.FollowResponse;
import edu.byu.cs.tweeter.model.net.response.FolloweeCountResponse;
import edu.byu.cs.tweeter.model.net.response.FollowerCountResponse;
import edu.byu.cs.tweeter.model.net.response.FollowerResponse;
import edu.byu.cs.tweeter.model.net.response.FollowingResponse;
import edu.byu.cs.tweeter.model.net.response.IsFollowerResponse;
import edu.byu.cs.tweeter.model.net.response.UnfollowResponse;
import edu.byu.cs.tweeter.server.dao.FollowsDAO;
import edu.byu.cs.tweeter.server.factory.DAOFactory;

/**
 * Contains the business logic for getting the users a user is following.
 */
public class FollowService {
    private DAOFactory factory;

    public FollowService(DAOFactory factory) {
        this.factory = factory;
    }

    public FollowingResponse getFollowees(FollowingRequest request) {
        if(request.getTarget() == null) {
            throw new RuntimeException("[BadRequest] Request needs to have a follower alias");
        } else if(request.getLimit() <= 0) {
            throw new RuntimeException("[BadRequest] Request needs to have a positive limit");
        }
        FollowsDAO fDAO = factory.getFollowDAO();
        return fDAO.getFollowing(request);
    }

    public FollowerResponse getFollowers(FollowerRequest request) {
        if(request.getTarget() == null) {
            throw new RuntimeException("[BadRequest] Request needs to have a follower alias");
        } else if(request.getLimit() <= 0) {
            throw new RuntimeException("[BadRequest] Request needs to have a positive limit");
        }
        FollowsDAO fDAO = factory.getFollowDAO();
        return fDAO.getFollower(request);
    }

    public FollowResponse follow(FollowRequest request) {
        if(request.getTargetUserAlias() == null) {
            throw new RuntimeException("[BadRequest] Request needs to have a target alias");
        } else if(request.getAuthToken() == null) {
            throw new RuntimeException("[BadRequest] Request needs to have an authtoken");
        }

        FollowsDAO fDAO = factory.getFollowDAO();
        return fDAO.follow(request);
    }

    public UnfollowResponse unfollow(UnfollowRequest request) {
        if(request.getTargetUserAlias() == null) {
            throw new RuntimeException("[BadRequest] Request needs to have a target alias");
        } else if(request.getAuthToken() == null) {
            throw new RuntimeException("[BadRequest] Request needs to have an authtoken");
        }

        FollowsDAO fDAO = factory.getFollowDAO();
        return fDAO.unfollow(request);
    }

    public IsFollowerResponse isFollower(IsFollowerRequest request) {
        if(request.getFollower() == null) {
            throw new RuntimeException("[BadRequest] Request needs to have a follower alias");
        } else if(request.getFollowee() == null) {
            throw new RuntimeException("[BadRequest] Request needs to have an followee");
        }
        else if (request.getAuthToken() == null) {
            throw new RuntimeException("[BadRequest] Request needs to have an authtoken");
        }

        FollowsDAO fDAO = factory.getFollowDAO();
        return fDAO.isFollower(request);
    }

    public FollowerCountResponse getFollowerCount(FollowerCountRequest request) {
        if (request.getTargetUserAlias() == null) {
            throw new RuntimeException("[BadRequest] Request needs to have a target alias");
        }
        FollowsDAO fDAO = factory.getFollowDAO();
        return fDAO.followerCount(request);
    }

    public FolloweeCountResponse getFolloweeCount(FolloweeCountRequest request) {
        if (request.getTarget() == null) {
            throw new RuntimeException("[BadRequest] Request needs to have a target alias");
        }
        FollowsDAO fDAO = factory.getFollowDAO();
        return fDAO.followeeCount(request);
    }
}
