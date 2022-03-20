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
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.amazonaws.services.dynamodbv2.model.QueryRequest;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import edu.byu.cs.tweeter.model.domain.Status;
import edu.byu.cs.tweeter.model.domain.User;
import edu.byu.cs.tweeter.model.net.request.FeedRequest;
import edu.byu.cs.tweeter.model.net.response.FeedResponse;

public class FeedDynamoDAO extends DynamoDAO implements FeedDAO {
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
    public FeedResponse getFeed(FeedRequest request) {
        String target = request.getTarget();

        int pageSize = request.getLimit();

        if (!checkAuthToken(request.getAuthToken(), request.getTarget())) {
            throw new RuntimeException("[InternalServerError] invalid authtoken");
        }

        QuerySpec querySpec = new QuerySpec()
                .withScanIndexForward(true)
                .withHashKey("UserAlias", target)
                .withMaxResultSize(pageSize);

        if (request.getLast() != null) {
            Date date;
            try {
                date = new SimpleDateFormat("MMM dd yyyy, HH:mm").parse(request.getLast());
            }catch (Exception ex) {
                throw new RuntimeException("[InternalServerError] could not parse date");
            }

            long last = date.getTime();
            PrimaryKey lastKey = new PrimaryKey("UserAlias", target, "Timestamp", last);
            querySpec.withExclusiveStartKey(lastKey);
        }

        ItemCollection<QueryOutcome> items = null;
        Iterator<Item> iterator = null;
        Item item = null;
        Table table = dynamoDB.getTable(TableName);

        items = table.query(querySpec);
        iterator = items.iterator();

        List<Status> posts = new ArrayList<>(request.getLimit());

        if (request.getLast() != null) {
            iterator.next();
        }
        while (iterator.hasNext()) {
            item = iterator.next();
            posts.add(makePost(item));
        }

        Map<String, AttributeValue> lastKey = items.getLastLowLevelResult().getQueryResult().getLastEvaluatedKey();

        boolean hasMore = false;
        if (lastKey != null) {
            hasMore = true;
        }

        return new FeedResponse(posts, hasMore);
    }

    private Status makePost(Item item) {
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

    private User getUser(String alias) {
        Table table = dynamoDB.getTable("User");

        if (table == null) {
            throw new RuntimeException("[InternalServerError] could not find table");
        }
        Item item = table.getItem("UserAlias", alias);
        if (item == null) {
            throw new RuntimeException("[InternalServerError] could not find item");
        }

        String first = item.getString("firstName");
        String last = item.getString("lastName");
        String img = item.getString("imageUrl");
        User user = new User(first, last, alias, img);
        return user;
    }



}
