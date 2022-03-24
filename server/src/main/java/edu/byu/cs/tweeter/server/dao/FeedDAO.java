package edu.byu.cs.tweeter.server.dao;

import edu.byu.cs.tweeter.model.net.request.FeedRequest;
import edu.byu.cs.tweeter.model.net.request.PostStatusRequest;
import edu.byu.cs.tweeter.model.net.response.FeedResponse;

public interface FeedDAO {
    FeedResponse getFeed(FeedRequest request);
    boolean post(PostStatusRequest request);
}
