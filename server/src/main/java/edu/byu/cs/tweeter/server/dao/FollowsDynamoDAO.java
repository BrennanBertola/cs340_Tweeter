package edu.byu.cs.tweeter.server.dao;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.document.BatchWriteItemOutcome;
import com.amazonaws.services.dynamodbv2.document.DynamoDB;
import com.amazonaws.services.dynamodbv2.document.Index;
import com.amazonaws.services.dynamodbv2.document.Item;
import com.amazonaws.services.dynamodbv2.document.ItemCollection;
import com.amazonaws.services.dynamodbv2.document.PrimaryKey;
import com.amazonaws.services.dynamodbv2.document.QueryOutcome;
import com.amazonaws.services.dynamodbv2.document.Table;
import com.amazonaws.services.dynamodbv2.document.TableWriteItems;
import com.amazonaws.services.dynamodbv2.document.spec.QuerySpec;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.amazonaws.services.dynamodbv2.model.Get;
import com.amazonaws.services.dynamodbv2.model.WriteRequest;
import com.amazonaws.services.sqs.AmazonSQS;
import com.amazonaws.services.sqs.AmazonSQSClientBuilder;
import com.amazonaws.services.sqs.model.SendMessageRequest;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.Status;
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
import edu.byu.cs.tweeter.server.SQS.FeedMessage;

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
            querySpec.withExclusiveStartKey(new PrimaryKey("followee_handle", target, "follower_handle", last));
        }

        ItemCollection<QueryOutcome> items = null;
        Iterator<Item> iterator = null;
        Item item = null;

        Table table = dynamoDB.getTable(TableName);
        Index index = table.getIndex("follows_index");

        items = index.query(querySpec);
        iterator = items.iterator();

        List<User> users = new ArrayList<>(pageSize);


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

    @Override
    public void addFollowersBatch(List<String> followers, String target) {
        TableWriteItems items = new TableWriteItems(TableName);

        for (String follower : followers) {
            Item item = new Item()
                    .withPrimaryKey("follower_handle", follower, "followee_handle", target);
            items.addItemToPut(item);

            if (items.getItemsToPut() != null && items.getItemsToPut().size() == 25) {
                loopBatchWrite(items);
                items = new TableWriteItems(TableName);
            }
        }

        if (items.getItemsToPut() != null && items.getItemsToPut().size() > 0) {
            loopBatchWrite(items);
        }
    }

    @Override
    public void postUpdateFeedMessages(Status post) {
        final String sqsUrl = "https://sqs.us-west-2.amazonaws.com/287264978271/FeedUpdate";
        final int batchSize = 25;
        String creator = post.getUser().getAlias();
        Table table = dynamoDB.getTable(TableName);
        Index index = table.getIndex("follows_index");
        QuerySpec query = new QuerySpec()
                .withScanIndexForward(true)
                .withHashKey("followee_handle", creator)
                .withMaxResultSize(batchSize);

        ArrayList<String> toUpdate = new ArrayList<>();
        ItemCollection<QueryOutcome> items = index.query(query);
        Iterator<Item> it = items.iterator();

        while (it.hasNext()) {
            Item curr = it.next();
            toUpdate.add(curr.getString("follower_handle"));
        }

        FeedMessage msg = new FeedMessage(toUpdate, post);
        Gson gson = new GsonBuilder().create();
        String json = gson.toJson(msg);

        SendMessageRequest send = new SendMessageRequest()
                .withQueueUrl(sqsUrl)
                .withMessageBody(json);
        AmazonSQS sqs = AmazonSQSClientBuilder.defaultClient();
        sqs.sendMessage(send);

        toUpdate.clear();
    }


    private void loopBatchWrite(TableWriteItems items) {

        // The 'dynamoDB' object is of type DynamoDB and is declared statically in this example
        BatchWriteItemOutcome outcome = dynamoDB.batchWriteItem(items);

        // Check the outcome for items that didn't make it onto the table
        // If any were not added to the table, try again to write the batch
        while (outcome.getUnprocessedItems().size() > 0) {
            Map<String, List<WriteRequest>> unprocessedItems = outcome.getUnprocessedItems();
            outcome = dynamoDB.batchWriteItemUnprocessed(unprocessedItems);
        }
    }

    public ItemCollection<QueryOutcome> getFollowers(String target) {
        Table table = dynamoDB.getTable(TableName);
        Index index = table.getIndex("follows_index");
        QuerySpec query = new QuerySpec().withHashKey("followee_handle", target);
        return index.query(query);
    }
}
