package edu.byu.cs.tweeter.server.dao;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.document.DynamoDB;
import com.amazonaws.services.dynamodbv2.document.Item;
import com.amazonaws.services.dynamodbv2.document.Table;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.Status;
import edu.byu.cs.tweeter.model.domain.User;

public abstract class DynamoDAO {
    private static AmazonDynamoDB amazonDynamoDB = AmazonDynamoDBClientBuilder
            .standard()
            .withRegion("us-west-2")
            .build();
    private static DynamoDB dynamoDB = new DynamoDB(amazonDynamoDB);

    public boolean checkAuthToken(AuthToken token, String alias) {
        long timeToExpire = 7200000;

        Table table = dynamoDB.getTable("AuthToken");
        Item item = table.getItem("AuthToken", token.getToken());
        if (item == null) {
            return false;
        }

        Date date = new Date();
        if ((date.getTime() - item.getLong("timestamp")) > timeToExpire) {
            table.deleteItem("AuthToken", token.getToken());
            return false;
        }

        return true;
    }


}


