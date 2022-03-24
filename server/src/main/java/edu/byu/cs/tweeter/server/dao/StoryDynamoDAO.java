package edu.byu.cs.tweeter.server.dao;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.document.DynamoDB;
import com.amazonaws.services.dynamodbv2.document.Item;
import com.amazonaws.services.dynamodbv2.document.PrimaryKey;
import com.amazonaws.services.dynamodbv2.document.Table;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.Status;
import edu.byu.cs.tweeter.model.domain.User;
import edu.byu.cs.tweeter.model.net.request.PostStatusRequest;
import edu.byu.cs.tweeter.model.net.request.StoryRequest;
import edu.byu.cs.tweeter.model.net.response.StoryResponse;

public class StoryDynamoDAO extends PagedDynamoDAO implements StoryDAO {
    private static AmazonS3 s3 = AmazonS3ClientBuilder
            .standard()
            .withRegion("us-west-2")
            .build();
    private static AmazonDynamoDB amazonDynamoDB = AmazonDynamoDBClientBuilder
            .standard()
            .withRegion("us-west-2")
            .build();
    private static DynamoDB dynamoDB = new DynamoDB(amazonDynamoDB);
    private static final String TableName = "Story";

    @Override
    public StoryResponse getStory(StoryRequest request) {
        List<Status> posts = getItems(request.getAuthToken(), request.getLast(), request.getTarget(), request.getLimit());
        return new StoryResponse(posts, hasMore);
    }

    @Override
    public boolean post(PostStatusRequest request) {
        AuthToken token = request.getAuthToken();
        if (!checkAuthToken(token)) {
            throw new RuntimeException("[InternalServerError] invalid authtoken");
        }
        String user = getUserWToken(request.getAuthToken());
        Status post = request.getStatus();
        Date date = new Date();

        Item item = new Item().withPrimaryKey("UserAlias", user, "Timestamp", date.getTime())
                .withString("post", post.getPost())
                .withString("creator", post.getUser().getAlias())
                .withList("urls", post.getUrls())
                .withList("mentions", post.getMentions());

        Table table = dynamoDB.getTable(TableName);
        table.putItem(item);

        return true;
    }

    @Override
    Object make(Item item) {
        User user = getUser(item.getString("creator"));
        Date date = new Date(item.getNumber("Timestamp").longValue());
        DateFormat df = new SimpleDateFormat("MMM dd yyyy, HH:mm");
        String dateString = df.format(date) + " GMT";
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
    public String getTable() {return TableName;}

    @Override
    String getPK() {
        return "UserAlias";
    }

    @Override
    boolean getOrder() {
        return false;
    }


}
