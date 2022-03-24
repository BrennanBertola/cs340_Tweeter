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
import edu.byu.cs.tweeter.server.dao.FollowDAO;
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

    /**
     * Returns the users that the user specified in the request is following. Uses information in
     * the request object to limit the number of followees returned and to return the next set of
     * followees after any that were returned in a previous request. Uses the {@link FollowDAO} to
     * get the followees.
     *
     * @param request contains the data required to fulfill the request.
     * @return the followees.
     */
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

        return getFollowDAO().unfollow(request);
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

        return getFollowDAO().isFollower(request);
    }

    public FollowerCountResponse getFollowerCount(FollowerCountRequest request) {
        if (request.getTargetUserAlias() == null) {
            throw new RuntimeException("[BadRequest] Request needs to have a target alias");
        }
        return getFollowDAO().getFollowerCount(request);
    }

    public FolloweeCountResponse getFolloweeCount(FolloweeCountRequest request) {
        if (request.getTarget() == null) {
            throw new RuntimeException("[BadRequest] Request needs to have a target alias");
        }
        return getFollowDAO().getFolloweeCount(request);
    }



    /**
     * Returns an instance of {@link FollowDAO}. Allows mocking of the FollowDAO class
     * for testing purposes. All usages of FollowDAO should get their FollowDAO
     * instance from this method to allow for mocking of the instance.
     *
     * @return the instance.
     */
    FollowDAO getFollowDAO() {
        return new FollowDAO();
    }
}
