package edu.byu.cs.tweeter.server.dao;

import edu.byu.cs.tweeter.model.net.request.FollowRequest;
import edu.byu.cs.tweeter.model.net.request.FollowingRequest;
import edu.byu.cs.tweeter.model.net.response.FollowingResponse;

public interface FollowsDAO {
    FollowingResponse getFollowing(FollowingRequest request);
}
