package edu.byu.cs.tweeter.server.dao;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.document.DynamoDB;
import com.amazonaws.services.dynamodbv2.document.Item;
import com.amazonaws.services.dynamodbv2.document.PrimaryKey;

import java.util.List;

import edu.byu.cs.tweeter.model.domain.User;
import edu.byu.cs.tweeter.model.net.request.FollowRequest;
import edu.byu.cs.tweeter.model.net.request.FollowingRequest;
import edu.byu.cs.tweeter.model.net.response.FollowingResponse;

public class FollowsDynamoDAO extends PagedDynamoDAO<User> implements FollowsDAO {
    private static AmazonDynamoDB amazonDynamoDB = AmazonDynamoDBClientBuilder
            .standard()
            .withRegion("us-west-2")
            .build();
    private static DynamoDB dynamoDB = new DynamoDB(amazonDynamoDB);
    private static final String TableName = "follows";

    @Override
    User make(Item item) {
        User user = getUser(item.getString("followee_handle"));
        return user;
    }

    @Override
    PrimaryKey getLast(String last, String target) {
        return new PrimaryKey("follower_handle", target, "followee_handle", last);
    }

    @Override
    String getTable() {
        return TableName;
    }

    @Override
    String getPK() {
        return "follower_handle";
    }

    @Override
    public FollowingResponse getFollowing(FollowingRequest request) {
        List<User> users = getItems(request.getAuthToken(), request.getLast(), request.getTarget(), request.getLimit());
        return new FollowingResponse(users, hasMore);
    }
}
