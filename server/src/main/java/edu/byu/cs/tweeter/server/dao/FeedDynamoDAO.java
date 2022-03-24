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
import com.amazonaws.services.dynamodbv2.document.spec.DeleteItemSpec;
import com.amazonaws.services.dynamodbv2.document.spec.QuerySpec;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
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
        Date date = new Date(item.getNumber("Timestamp").longValue());
        DateFormat df = new SimpleDateFormat("MMM dd yyyy, HH:mm");
        String dateString = df.format(date);
        String post = item.getString("post");
        List<String> urls = item.getList("urls");
        List<String> mentions = item.getList("mentions");

        Status status = new Status(post, user, dateString, urls, mentions);

        return status;
    }

    @Override
    PrimaryKey getLast(String last, String target) {
        Date date;
        try {
            date = new SimpleDateFormat("MMM dd yyyy, HH:mm").parse(last);
        }catch (Exception ex) {
            throw new RuntimeException("[InternalServerError] could not parse date");
        }

        long lastLong = date.getTime();
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
        AuthToken token = request.getAuthToken();
        if (!checkAuthToken(token)) {
            throw new RuntimeException("[InternalServerError] invalid authtoken");
        }
        Status post = request.getStatus();
        long timeMil = new Date().getTime();


        Table table = dynamoDB.getTable("follows");
        Index index = table.getIndex("follows_index");
        QuerySpec query = new QuerySpec().withHashKey("followee_handle", post.getUser().getAlias());
        ItemCollection<QueryOutcome> items = index.query(query);
        Iterator<Item> it = items.iterator();
        Item feedToAdd;

        table = dynamoDB.getTable(TableName);
        while (it.hasNext()) {
            feedToAdd = it.next();
            String alias = feedToAdd.getString("follower_handle");
            Item item = new Item().withPrimaryKey("UserAlias", alias, "Timestamp", timeMil)
                    .withString("post", post.getPost())
                    .withString("creator", post.getUser().getAlias())
                    .withList("urls", post.getUrls())
                    .withList("mentions", post.getMentions());
            table.putItem(item);
        }

        return true;
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
        Table table = dynamoDB.getTable("Story");
        QuerySpec query = new QuerySpec().withHashKey("UserAlias", target);
        ItemCollection<QueryOutcome> items = null;
        Iterator<Item> iterator = null;
        Item item = null;

        Table fTable = dynamoDB.getTable(TableName);
        items = table.query(query);
        iterator = items.iterator();
        while(iterator.hasNext()) {
            item = iterator.next();
            item.withPrimaryKey("UserAlias", user, "TimeStamp", item.getNumber("TimeStamp"));
            fTable.putItem(item);
        }
    }
}
