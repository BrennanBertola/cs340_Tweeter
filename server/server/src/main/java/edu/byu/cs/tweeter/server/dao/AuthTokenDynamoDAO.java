package edu.byu.cs.tweeter.server.dao;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.document.DynamoDB;
import com.amazonaws.services.dynamodbv2.document.Item;
import com.amazonaws.services.dynamodbv2.document.Table;

import java.security.SecureRandom;
import java.util.Base64;
import java.util.Date;

import edu.byu.cs.tweeter.model.domain.AuthToken;

public class AuthTokenDynamoDAO implements AuthTokenDAO{
    private static AmazonDynamoDB amazonDynamoDB = AmazonDynamoDBClientBuilder
            .standard()
            .withRegion("us-west-2")
            .build();
    private static DynamoDB dynamoDB = new DynamoDB(amazonDynamoDB);
    private static final String TableName = "AuthToken";


    @Override
    public AuthToken createToken() {
        byte[] rand = new byte[24];
        SecureRandom secure = new SecureRandom();
        Base64.Encoder encoder = Base64.getUrlEncoder();
        secure.nextBytes(rand);
        String token = encoder.encodeToString(rand);

        Date date = new Date();
        long timestamp = date.getTime();

        Table table = dynamoDB.getTable(TableName);

        table = dynamoDB.getTable("AuthToken");
        Item item = new Item().withPrimaryKey("AuthToken", token).withNumber("timestamp", timestamp);
        table.putItem(item);

        AuthToken authToken = new AuthToken(token, String.valueOf(timestamp));
        return authToken;
    }

    @Override
    public void deleteToken(AuthToken token) {
        Table table = dynamoDB.getTable(TableName);
        table.deleteItem("AuthToken", token.getToken());
    }
}
