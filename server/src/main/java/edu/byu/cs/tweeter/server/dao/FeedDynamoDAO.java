package edu.byu.cs.tweeter.server.dao;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.document.DynamoDB;
import com.amazonaws.services.dynamodbv2.document.Expected;
import com.amazonaws.services.dynamodbv2.document.Index;
import com.amazonaws.services.dynamodbv2.document.Item;
import com.amazonaws.services.dynamodbv2.document.ItemCollection;
import com.amazonaws.services.dynamodbv2.document.PrimaryKey;
import com.amazonaws.services.dynamodbv2.document.QueryOutcome;
import com.amazonaws.services.dynamodbv2.document.Table;
import com.amazonaws.services.dynamodbv2.document.TableWriteItems;
import com.amazonaws.services.dynamodbv2.document.spec.DeleteItemSpec;
import com.amazonaws.services.dynamodbv2.document.spec.QuerySpec;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.sqs.AmazonSQS;
import com.amazonaws.services.sqs.AmazonSQSClientBuilder;
import com.amazonaws.services.sqs.model.SendMessageRequest;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.PriorityQueue;

import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.Status;
import edu.byu.cs.tweeter.model.domain.User;
import edu.byu.cs.tweeter.model.net.request.FeedRequest;
import edu.byu.cs.tweeter.model.net.request.PostStatusRequest;
import edu.byu.cs.tweeter.model.net.response.FeedResponse;
import edu.byu.cs.tweeter.server.SQS.FeedMessage;

public class FeedDynamoDAO extends PagedDynamoDAO implements FeedDAO {
    private static AmazonS3 s3 = AmazonS3ClientBuilder
            .standard()
            .withRegion("us-west-2")
            .build();
    private static AmazonDynamoDB amazonDynamoDB = AmazonDynamoDBClientBuilder
            .standard()
            .withRegion("us-west-2")
            .build();
    private static DynamoDB dynamoDB = new DynamoDB(amazonDynamoDB);
    private static final String TableName = "Feed";
    private static final String SQS = "https://sqs.us-west-2.amazonaws.com/287264978271/Tweeter";

    @Override
    public String getTable() {return TableName;}

    @Override
    String getPK() {
        return "UserAlias";
    }

    @Override
    boolean getOrder() {
        return false;
    }

    @Override
    Object make(Item item) {
        User user = getUser(item.getString("creator"));
        long dbTimestamp = item.getNumber("Timestamp").longValue();
        Date date = new Date(dbTimestamp);
        String post = item.getString("post");
        List<String> urls = item.getList("urls");
        List<String> mentions = item.getList("mentions");
        Status status = new Status(post, user, date.toString(), urls, mentions);
        status.timestamp = dbTimestamp;

        return status;
    }

    @Override
    PrimaryKey getLast(String last, String target) {
        long lastLong = Long.parseLong(last);
        PrimaryKey lastKey = new PrimaryKey("UserAlias", target, "Timestamp", lastLong);
        return lastKey;
    }

    @Override
    public FeedResponse getFeed(FeedRequest request) {
        List<Status> posts = getItems(request.getAuthToken(), request.getLast(), request.getTarget(), request.getLimit());
        return new FeedResponse(posts, hasMore);
    }

    @Override
    public boolean post(PostStatusRequest request) {

        Status post = request.getStatus();
        long timeMil = new Date().getTime();
        post.timestamp = timeMil;
        Gson gson = new GsonBuilder()
                .setPrettyPrinting()
                .create();
        String json = gson.toJson(post);

        SendMessageRequest send = new SendMessageRequest()
                .withQueueUrl(SQS)
                .withMessageBody(json);
        AmazonSQS sqs = AmazonSQSClientBuilder.defaultClient();
        sqs.sendMessage(send);

//
//        long timeMil = new Date().getTime();
//
//
//        FollowsDynamoDAO fDAO = new FollowsDynamoDAO();
//        ItemCollection<QueryOutcome> items = fDAO.getFollowers(post.getUser().getAlias());
//        Iterator<Item> it = items.iterator();
//        Item feedToAdd;
//
//        Table table = dynamoDB.getTable(TableName);
//        while (it.hasNext()) {
//            feedToAdd = it.next();
//            String alias = feedToAdd.getString("follower_handle");
//            Item item = new Item().withPrimaryKey("UserAlias", alias, "Timestamp", timeMil)
//                    .withString("post", post.getPost())
//                    .withString("creator", post.getUser().getAlias())
//                    .withList("urls", post.getUrls())
//                    .withList("mentions", post.getMentions());
//            table.putItem(item);
//        }

        return true;
    }

    @Override
    public void updateFeed(FeedMessage feedMsg) {
        Table table = dynamoDB.getTable(TableName);
        List<String> toAdd = feedMsg.getToUpdate();
        Status post = feedMsg.getPost();
        Collection<Item> items = new ArrayList<>();
        for (int i = 0; i < toAdd.size(); ++i) {
            Item item = new Item().withPrimaryKey("UserAlias", toAdd.get(i),
                    "Timestamp", post.timestamp)
                    .withString("post", post.getPost())
                    .withString("creator", post.getUser().getAlias())
                    .withList("urls", post.getUrls())
                    .withList("mentions", post.getMentions());
            items.add(item);
        }
        TableWriteItems batchWrite = new TableWriteItems(TableName).withItemsToPut(items);
        dynamoDB.batchWriteItem(batchWrite);
    }

    public void removeAllFromFeed(String user, String target) {
        Table table = dynamoDB.getTable(TableName);
        QuerySpec query = new QuerySpec().withHashKey(getPK(), user);
        ItemCollection<QueryOutcome> items = null;
        Iterator<Item> iterator = null;
        Item item = null;

        items = table.query(query);
        iterator = items.iterator();
        while(iterator.hasNext()) {
            item = iterator.next();
            if (item.getString("creator").equals(target)) {
                table.deleteItem("UserAlias", item.getString("UserAlias"),
                        "Timestamp", item.getNumber("Timestamp"),
                        new Expected("creator").eq(target));
            }

        }
    }

    public void addAllToFeed(String user, String target) {
        StoryDynamoDAO sDAO = new StoryDynamoDAO();
        ItemCollection<QueryOutcome> items = sDAO.getStory(target);
        Iterator<Item> iterator = null;
        Item item = null;

        Table table = dynamoDB.getTable(TableName);
        iterator = items.iterator();
        while(iterator.hasNext()) {
            item = iterator.next();
            item.withPrimaryKey("UserAlias", user, "TimeStamp", item.getNumber("TimeStamp"));
            table.putItem(item);
        }
    }
}
