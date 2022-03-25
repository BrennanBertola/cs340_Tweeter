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

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.Status;
import edu.byu.cs.tweeter.model.domain.User;

public abstract class PagedDynamoDAO<T> {
    private static AmazonDynamoDB amazonDynamoDB = AmazonDynamoDBClientBuilder
            .standard()
            .withRegion("us-west-2")
            .build();
    private static DynamoDB dynamoDB = new DynamoDB(amazonDynamoDB);
    boolean hasMore;

    public List<T> getItems(AuthToken token, String last, String target, int pageSize) {
        AuthTokenDynamoDAO aDAO = new AuthTokenDynamoDAO();

        if (! aDAO.checkAuthToken(token)) {
            throw new RuntimeException("[InternalServerError] invalid authtoken");
        }

        QuerySpec querySpec = new QuerySpec()
                .withScanIndexForward(getOrder())
                .withHashKey(getPK(), target)
                .withMaxResultSize(pageSize);

        if (last != null) {
            querySpec.withExclusiveStartKey(getLast(last, target));
        }

        ItemCollection<QueryOutcome> items = null;
        Iterator<Item> iterator = null;
        Item item = null;
        Table table = dynamoDB.getTable(getTable());

        items = table.query(querySpec);
        iterator = items.iterator();

        List<T> obj = new ArrayList<>(pageSize);

//        if (last != null && iterator.hasNext()) {
//            iterator.next();
//        }
        while (iterator.hasNext()) {
            item = iterator.next();
            obj.add(make(item));
        }

        Map<String, AttributeValue> lastKey = items.getLastLowLevelResult().getQueryResult().getLastEvaluatedKey();

        hasMore = false;
        if (lastKey != null) {
            hasMore = true;
        }

        return obj;
    }

    abstract T make(Item item);

    public User getUser(String alias) {
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



    abstract PrimaryKey getLast(String last, String target);
    abstract String getTable();
    abstract String getPK();
    abstract boolean getOrder();
}
