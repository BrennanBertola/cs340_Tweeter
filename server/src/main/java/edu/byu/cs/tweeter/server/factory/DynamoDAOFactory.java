package edu.byu.cs.tweeter.server.factory;

import edu.byu.cs.tweeter.server.dao.AuthTokenDAO;
import edu.byu.cs.tweeter.server.dao.AuthTokenDynamoDAO;
import edu.byu.cs.tweeter.server.dao.FeedDAO;
import edu.byu.cs.tweeter.server.dao.FeedDynamoDAO;
import edu.byu.cs.tweeter.server.dao.FollowsDAO;
import edu.byu.cs.tweeter.server.dao.FollowsDynamoDAO;
import edu.byu.cs.tweeter.server.dao.StoryDAO;
import edu.byu.cs.tweeter.server.dao.StoryDynamoDAO;
import edu.byu.cs.tweeter.server.dao.UserDAO;
import edu.byu.cs.tweeter.server.dao.UserDynamoDAO;

public class DynamoDAOFactory implements DAOFactory {

    @Override
    public AuthTokenDAO getAuthTokenDAO() {
        return new AuthTokenDynamoDAO();
    }

    @Override
    public FeedDAO getFeedDAO() {
        return new FeedDynamoDAO();
    }

    @Override
    public FollowsDAO getFollowDAO() {
        return new FollowsDynamoDAO();
    }

    @Override
    public StoryDAO getStoryDAO() {
        return new StoryDynamoDAO();
    }

    @Override
    public UserDAO getUserDAO() {
        return new UserDynamoDAO();
    }
}
