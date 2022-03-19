package edu.byu.cs.tweeter.server.dao;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.document.*;
import com.amazonaws.services.dynamodbv2.model.*;
import com.amazonaws.services.dynamodbv2.model.KeySchemaElement;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;

import java.io.File;
import java.security.SecureRandom;
import java.security.Timestamp;
import java.util.Base64;
import java.util.Date;

import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.User;
import edu.byu.cs.tweeter.model.net.request.LoginRequest;
import edu.byu.cs.tweeter.model.net.request.LogoutRequest;
import edu.byu.cs.tweeter.model.net.request.RegisterRequest;
import edu.byu.cs.tweeter.model.net.response.LoginResponse;
import edu.byu.cs.tweeter.model.net.response.LogoutResponse;
import edu.byu.cs.tweeter.model.net.response.RegisterResponse;

public class UserDynamoDAO implements UserDAO {
    private static AmazonS3 s3 = AmazonS3ClientBuilder
            .standard()
            .withRegion("us-west-2")
            .build();
    private static AmazonDynamoDB amazonDynamoDB = AmazonDynamoDBClientBuilder
            .standard()
            .withRegion("us-west-2")
            .build();
    private static DynamoDB dynamoDB = new DynamoDB(amazonDynamoDB);
    private static final String TableName = "User";

    @Override
    public User login(LoginRequest request) {
        Table table = dynamoDB.getTable(TableName);

        if (table == null) {
            throw new RuntimeException("[InternalServerError] could not find table");
        }
        Item item = table.getItem("UserAlias", request.getUsername());
        if (item == null) {
            throw new RuntimeException("[InternalServerError] could not find item");
        }
        String tablePass = item.getString("password");
        if (!tablePass.equals(request.getPassword())) {
            throw new RuntimeException("[BadRequest] incorrect password");
        }

        String first = item.getString("firstName");
        String last = item.getString("lastName");
        String alias = item.getString("UserAlias");
        String img = item.getString("imageUrl");
        User user = new User(first, last, alias, img);
        return user;
    }

    @Override
    public User register(RegisterRequest request) {
        Table table = dynamoDB.getTable("User");
        Item item = table.getItem("UserAlias", request.getUsername());
        if (item != null) {
            throw new RuntimeException("[InternalServerError] user already exists");
        }

//        byte[] bytes = Base64.getDecoder().decode(request.getImage());
//
//        String bucketName = "brennan-tweeter-images";
//        s3.putObject(bucketName, request.getUsername(), String.valueOf(bytes));
//        String imageUrl = s3.getUrl(bucketName, request.getUsername()).toExternalForm();

        String defaultURL = "https://brennan-tweeter-images.s3.us-west-2.amazonaws.com/sadge.jpg";

        item = new Item().withPrimaryKey("UserAlias", request.getUsername())
                .withString("password", request.getPassword())
                .withString("firstName", request.getFirstName())
                .withString("lastName", request.getLastName())
                .withString("imageUrl", defaultURL);

        table.putItem(item);

        String first = request.getFirstName();
        String last = request.getLastName();
        String alias = request.getUsername();
        User user = new User(first, last, alias, defaultURL);
        return user;
    }
}
