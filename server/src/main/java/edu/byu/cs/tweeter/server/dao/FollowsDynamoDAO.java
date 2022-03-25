package edu.byu.cs.tweeter.server.dao;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.document.DynamoDB;
import com.amazonaws.services.dynamodbv2.document.Index;
import com.amazonaws.services.dynamodbv2.document.Item;
import com.amazonaws.services.dynamodbv2.document.ItemCollection;
import com.amazonaws.services.dynamodbv2.document.PrimaryKey;
import com.amazonaws.services.dynamodbv2.document.QueryOutcome;
import com.amazonaws.services.dynamodbv2.document.Table;
import com.amazonaws.services.dynamodbv2.document.spec.QuerySpec;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.User;
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
    boolean getOrder() {
        return true;
    }

    @Override
    public FollowingResponse getFollowing(FollowingRequest request) {
        List<User> users = getItems(request.getAuthToken(), request.getLast(), request.getTarget(), request.getLimit());
        return new FollowingResponse(users, hasMore);
    }

    @Override
    public FollowerResponse getFollower(FollowerRequest request) {
        AuthToken token = request.getAuthToken();
        String last = request.getLast();
        String target = request.getTarget();
        int pageSize = request.getLimit();
        AuthTokenDynamoDAO aDAO = new AuthTokenDynamoDAO();

        if (! aDAO.checkAuthToken(token)) {
            throw new RuntimeException("[InternalServerError] invalid authtoken");
        }

        QuerySpec querySpec = new QuerySpec()
                .withScanIndexForward(true)
                .withHashKey("followee_handle", target)
                .withMaxResultSize(pageSize);

        if (last != null) {
            querySpec.withExclusiveStartKey(getLast(last, target));
        }

        ItemCollection<QueryOutcome> items = null;
        Iterator<Item> iterator = null;
        Item item = null;

        Table table = dynamoDB.getTable(TableName);
        Index index = table.getIndex("follows_index");

        items = index.query(querySpec);
        iterator = items.iterator();

        List<User> users = new ArrayList<>(pageSize);

        if (last != null) {
            iterator.next();
        }
        while (iterator.hasNext()) {
            item = iterator.next();
            users.add(getUser(item.getString("follower_handle")));
        }

        Map<String, AttributeValue> lastKey = items.getLastLowLevelResult().getQueryResult().getLastEvaluatedKey();

        hasMore = false;
        if (lastKey != null) {
            hasMore = true;
        }


        return new FollowerResponse(users, hasMore);
    }

    @Override
    public FollowResponse follow(FollowRequest request) {
        AuthToken token = request.getAuthToken();
        AuthTokenDynamoDAO aDAO = new AuthTokenDynamoDAO();

        if (! aDAO.checkAuthToken(token)) {
            throw new RuntimeException("[InternalServerError] invalid authtoken");
        }

        String alias = aDAO.getUserWToken(token);

        Table table = dynamoDB.getTable(TableName);
        Item item = new Item().withPrimaryKey(getPK(), alias, "followee_handle", request.getTargetUserAlias());
        table.putItem(item);

        new FeedDynamoDAO().addAllToFeed(alias, request.getTargetUserAlias());

        return new FollowResponse(true);
    }

    @Override
    public UnfollowResponse unfollow(UnfollowRequest request) {
        AuthToken token = request.getAuthToken();
        AuthTokenDynamoDAO aDAO = new AuthTokenDynamoDAO();

        if (! aDAO.checkAuthToken(token)) {
            throw new RuntimeException("[InternalServerError] invalid authtoken");
        }
        String alias = aDAO.getUserWToken(token);

        Table table = dynamoDB.getTable(TableName);
        table.deleteItem("follower_handle", alias, "followee_handle", request.getTargetUserAlias());

        new FeedDynamoDAO().removeAllFromFeed(alias, request.getTargetUserAlias());

        return new UnfollowResponse(true);
    }

    @Override
    public IsFollowerResponse isFollower(IsFollowerRequest request) {
        AuthToken token = request.getAuthToken();
        AuthTokenDynamoDAO aDAO = new AuthTokenDynamoDAO();

        if (! aDAO.checkAuthToken(token)) {
            throw new RuntimeException("[InternalServerError] invalid authtoken");
        }

        int isFollower;
        Table table = dynamoDB.getTable(TableName);
        Item item = table.getItem("follower_handle", request.getFollower(), "followee_handle", request.getFollowee());

        if (item == null) {
            isFollower = 1;
        }
        else {
            isFollower = 0;
        }
        return new IsFollowerResponse(isFollower);
    }

    @Override
    public FollowerCountResponse followerCount(FollowerCountRequest request) {
        AuthToken token = request.getAuthToken();
        AuthTokenDynamoDAO aDAO = new AuthTokenDynamoDAO();

        if (! aDAO.checkAuthToken(token)) {
            throw new RuntimeException("[InternalServerError] invalid authtoken");
        }

        Table table = dynamoDB.getTable(TableName);
        Index index = table.getIndex("follows_index");

        QuerySpec query = new QuerySpec().withHashKey("followee_handle", request.getTargetUserAlias());
        ItemCollection<QueryOutcome> items = null;
        items = index.query(query);

        Iterator<Item> it = items.iterator();

        int size = 0;
        while (it.hasNext()) {
            it.next();
            size++;
        }

        return new FollowerCountResponse(size);
    }

    @Override
    public FolloweeCountResponse followeeCount(FolloweeCountRequest request) {
        AuthToken token = request.getAuthToken();
        AuthTokenDynamoDAO aDAO = new AuthTokenDynamoDAO();

        if (! aDAO.checkAuthToken(token)) {
            throw new RuntimeException("[InternalServerError] invalid authtoken");
        }

        Table table = dynamoDB.getTable(TableName);
        QuerySpec query = new QuerySpec().withHashKey("follower_handle", request.getTarget());
        ItemCollection<QueryOutcome> items = table.query(query);
        Iterator<Item> it = items.iterator();

        int size = 0;
        while (it.hasNext()) {
            it.next();
            size++;
        }

        return new FolloweeCountResponse(size);
    }

    public ItemCollection<QueryOutcome> getFollowers(String target) {
        Table table = dynamoDB.getTable(TableName);
        Index index = table.getIndex("follows_index");
        QuerySpec query = new QuerySpec().withHashKey("followee_handle", target);
        return index.query(query);
    }
}
