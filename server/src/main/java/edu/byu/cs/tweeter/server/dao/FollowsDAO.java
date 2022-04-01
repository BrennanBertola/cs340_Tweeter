package edu.byu.cs.tweeter.server.dao;

import java.util.List;

import edu.byu.cs.tweeter.model.domain.Status;
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
import edu.byu.cs.tweeter.server.SQS.FeedMessage;

public interface FollowsDAO {
    FollowingResponse getFollowing(FollowingRequest request);
    FollowerResponse getFollower(FollowerRequest request);
    FollowResponse follow(FollowRequest request);
    UnfollowResponse unfollow(UnfollowRequest request);
    IsFollowerResponse isFollower(IsFollowerRequest request);
    FollowerCountResponse followerCount(FollowerCountRequest request);
    FolloweeCountResponse followeeCount(FolloweeCountRequest request);
    void addFollowersBatch(List<String> followers, String target);
    void postUpdateFeedMessages(Status post);
}
