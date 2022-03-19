package edu.byu.cs.tweeter.server.factory;

import edu.byu.cs.tweeter.server.dao.AuthTokenDAO;
import edu.byu.cs.tweeter.server.dao.FeedDAO;
import edu.byu.cs.tweeter.server.dao.FollowsDAO;
import edu.byu.cs.tweeter.server.dao.StoryDAO;
import edu.byu.cs.tweeter.server.dao.UserDAO;

public interface DAOFactory {
    AuthTokenDAO getAuthTokenDAO();
    FeedDAO getFeedDAO();
    FollowsDAO getFollowDAO();
    StoryDAO getStoryDAO();
    UserDAO getUserDAO();
}
