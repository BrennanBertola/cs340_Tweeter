package edu.byu.cs.tweeter.server.dao;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.document.AttributeUpdate;
import com.amazonaws.services.dynamodbv2.document.DynamoDB;
import com.amazonaws.services.dynamodbv2.document.Item;
import com.amazonaws.services.dynamodbv2.document.ItemCollection;
import com.amazonaws.services.dynamodbv2.document.ScanOutcome;
import com.amazonaws.services.dynamodbv2.document.Table;
import com.amazonaws.services.dynamodbv2.document.spec.UpdateItemSpec;

import java.security.SecureRandom;
import java.util.Base64;
import java.util.Date;
import java.util.Iterator;

import edu.byu.cs.tweeter.model.domain.AuthToken;

public class AuthTokenDynamoDAO implements AuthTokenDAO{
    private static AmazonDynamoDB amazonDynamoDB = AmazonDynamoDBClientBuilder
            .standard()
            .withRegion("us-west-2")
            .build();
    private static DynamoDB dynamoDB = new DynamoDB(amazonDynamoDB);
    private static final String TableName = "AuthToken";
    long timeToExpire = 7200000;


    @Override
    public AuthToken createToken(String alias) {
        byte[] rand = new byte[24];
        SecureRandom secure = new SecureRandom();
        Base64.Encoder encoder = Base64.getUrlEncoder();
        secure.nextBytes(rand);
        String token = encoder.encodeToString(rand);

        Date date = new Date();
        long timestamp = date.getTime();

        Table table = dynamoDB.getTable(TableName);

        table = dynamoDB.getTable(TableName);
        Item item = new Item().withPrimaryKey("AuthToken", token)
                .withNumber("timestamp", timestamp)
                .withString("alias", alias);
        table.putItem(item);

        AuthToken authToken = new AuthToken(token, String.valueOf(timestamp));
        return authToken;
    }

    @Override
    public void deleteToken(AuthToken token) {
        deleteTokens(token);
    }

    public boolean checkAuthToken(AuthToken token) {
        Table table = dynamoDB.getTable(TableName);
        Item item = table.getItem("AuthToken", token.getToken());
        if (item == null) {
            return false;
        }

        Date date = new Date();
        if ((date.getTime() - item.getLong("timestamp")) > timeToExpire) {
            deleteTokens(token);
            return false;
        }

        UpdateItemSpec update = new UpdateItemSpec()
                .withPrimaryKey("AuthToken", token.getToken())
                .addAttributeUpdate(new AttributeUpdate("timestamp").put(new Date().getTime()));
        table.updateItem(update);

        return true;
    }

    public void deleteTokens(AuthToken token) { //r
        Table table = dynamoDB.getTable(TableName);
        ItemCollection<ScanOutcome> items = null;
        Iterator<Item> iterator = null;
        Item item = null;
        Date date = new Date();

        items = table.scan();
        iterator = items.iterator();

        while (iterator.hasNext()) {
            item = iterator.next();
            if ((date.getTime() - item.getLong("timestamp")) > timeToExpire) {
                table.deleteItem("AuthToken", item.getString("AuthToken"));
            }
        }

        table.deleteItem("AuthToken", token.getToken());
    }

    public String getUserWToken(AuthToken token) {
        Table table = dynamoDB.getTable(TableName);
        Item item = table.getItem("AuthToken", token.getToken());
        return item.getString("alias");
    }
}
