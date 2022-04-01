package edu.byu.cs.tweeter.server.dao;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.document.DynamoDB;
import com.amazonaws.services.dynamodbv2.document.Item;
import com.amazonaws.services.dynamodbv2.document.ItemCollection;
import com.amazonaws.services.dynamodbv2.document.PrimaryKey;
import com.amazonaws.services.dynamodbv2.document.QueryOutcome;
import com.amazonaws.services.dynamodbv2.document.Table;
import com.amazonaws.services.dynamodbv2.document.spec.QuerySpec;
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
        AuthTokenDynamoDAO aDAO = new AuthTokenDynamoDAO();

        String user = aDAO.getUserWToken(request.getAuthToken());
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
    public String getTable() {return TableName;}

    @Override
    String getPK() {
        return "UserAlias";
    }

    @Override
    boolean getOrder() {
        return false;
    }

    public ItemCollection<QueryOutcome> getStory(String target) {
        Table table = dynamoDB.getTable(TableName);
        QuerySpec query = new QuerySpec().withHashKey("UserAlias", target);
        return table.query(query);
    }


}
