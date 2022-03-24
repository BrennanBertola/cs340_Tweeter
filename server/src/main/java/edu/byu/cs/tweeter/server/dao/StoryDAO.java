package edu.byu.cs.tweeter.server.dao;

import edu.byu.cs.tweeter.model.net.request.PostStatusRequest;
import edu.byu.cs.tweeter.model.net.request.StoryRequest;
import edu.byu.cs.tweeter.model.net.response.StoryResponse;

public interface StoryDAO {
    StoryResponse getStory(StoryRequest request);
    boolean post(PostStatusRequest request);
}
