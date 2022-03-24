package edu.byu.cs.tweeter.server.service;

import edu.byu.cs.tweeter.model.net.request.FeedRequest;
import edu.byu.cs.tweeter.model.net.request.PostStatusRequest;
import edu.byu.cs.tweeter.model.net.request.StoryRequest;
import edu.byu.cs.tweeter.model.net.response.FeedResponse;
import edu.byu.cs.tweeter.model.net.response.PostStatusResponse;
import edu.byu.cs.tweeter.model.net.response.StoryResponse;
import edu.byu.cs.tweeter.server.dao.FeedDAO;
import edu.byu.cs.tweeter.server.dao.StoryDAO;
import edu.byu.cs.tweeter.server.factory.DAOFactory;

public class StatusService {
    private DAOFactory factory;
    public StatusService (DAOFactory factory) {this.factory = factory;}

    public FeedResponse getFeed(FeedRequest request) {
        if(request.getTarget() == null) {
            throw new RuntimeException("[BadRequest] Request needs to have a target alias");
        } else if(request.getLimit() <= 0) {
            throw new RuntimeException("[BadRequest] Request needs to have a positive limit");
        }

        FeedDAO fDAO = factory.getFeedDAO();
        return fDAO.getFeed(request);
    }

    public StoryResponse getStory(StoryRequest request) {
        if(request.getTarget() == null) {
            throw new RuntimeException("[BadRequest] Request needs to have a target alias");
        } else if(request.getLimit() <= 0) {
            throw new RuntimeException("[BadRequest] Request needs to have a positive limit");
        }
        StoryDAO sDAO = factory.getStoryDAO();
        return sDAO.getStory(request);
    }

    public PostStatusResponse postStatus(PostStatusRequest request) {
        if (request.getStatus() == null) {
            throw new RuntimeException("[BadRequest] Request needs to have a status");
        }
        else if (request.getAuthToken() == null) {
            throw new RuntimeException("[BadRequest] Request needs to have an authToken");
        }
        StoryDAO sDAO = factory.getStoryDAO();
        FeedDAO fDAO = factory.getFeedDAO();
        if (sDAO.post(request) && fDAO.post(request)) {
            return new PostStatusResponse(true);
        }
        return new PostStatusResponse(false);
    }
}
