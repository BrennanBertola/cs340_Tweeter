package edu.byu.cs.tweeter.server.dao;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.document.AttributeUpdate;
import com.amazonaws.services.dynamodbv2.document.DynamoDB;
import com.amazonaws.services.dynamodbv2.document.Item;
import com.amazonaws.services.dynamodbv2.document.ItemCollection;
import com.amazonaws.services.dynamodbv2.document.QueryOutcome;
import com.amazonaws.services.dynamodbv2.document.ScanOutcome;
import com.amazonaws.services.dynamodbv2.document.Table;
import com.amazonaws.services.dynamodbv2.document.spec.QuerySpec;
import com.amazonaws.services.dynamodbv2.document.spec.ScanSpec;
import com.amazonaws.services.dynamodbv2.document.spec.UpdateItemSpec;
import com.amazonaws.services.dynamodbv2.document.utils.ValueMap;
import com.amazonaws.services.dynamodbv2.model.ReturnValue;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
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
    long timeToExpire = 7200000;

    public boolean checkAuthToken(AuthToken token) {
        Table table = dynamoDB.getTable("AuthToken");
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

    public String getUserWToken(AuthToken token) {
        Table table = dynamoDB.getTable("AuthToken");
        Item item = table.getItem("AuthToken", token.getToken());
        return item.getString("alias");
    }

    public void deleteTokens(AuthToken token) {
        Table table = dynamoDB.getTable("AuthToken");
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


}


