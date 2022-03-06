package edu.byu.cs.tweeter.server.service;

import edu.byu.cs.tweeter.model.net.request.FeedRequest;
import edu.byu.cs.tweeter.model.net.request.StoryRequest;
import edu.byu.cs.tweeter.model.net.response.FeedResponse;
import edu.byu.cs.tweeter.model.net.response.StoryResponse;
import edu.byu.cs.tweeter.server.dao.StatusDAO;

public class StatusService {

    public FeedResponse getFeed(FeedRequest request) {
        if(request.getTarget() == null) {
            throw new RuntimeException("[BadRequest] Request needs to have a target alias");
        } else if(request.getLimit() <= 0) {
            throw new RuntimeException("[BadRequest] Request needs to have a positive limit");
        }
        return getStatusDAO().getFeed(request);
    }

    public StoryResponse getStory(StoryRequest request) {
        if(request.getTarget() == null) {
            throw new RuntimeException("[BadRequest] Request needs to have a target alias");
        } else if(request.getLimit() <= 0) {
            throw new RuntimeException("[BadRequest] Request needs to have a positive limit");
        }
        return getStatusDAO().getStory(request);
    }

    private StatusDAO getStatusDAO() {return new StatusDAO();}
}
