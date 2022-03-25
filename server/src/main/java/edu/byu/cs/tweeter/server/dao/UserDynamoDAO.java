package edu.byu.cs.tweeter.server.dao;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.document.DynamoDB;
import com.amazonaws.services.dynamodbv2.document.Item;
import com.amazonaws.services.dynamodbv2.document.Table;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;

import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.spec.KeySpec;
import java.util.Base64;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;

import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.User;
import edu.byu.cs.tweeter.model.net.request.LoginRequest;
import edu.byu.cs.tweeter.model.net.request.RegisterRequest;
import edu.byu.cs.tweeter.model.net.request.UserRequest;
import edu.byu.cs.tweeter.model.net.response.UserResponse;

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


        String hashedPass;
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            md.update(request.getPassword().getBytes());
            byte[] bytes = md.digest();
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < bytes.length; ++i) {
                sb.append(Integer.toString((bytes[i] & 0xff) + 0x100, 16).substring(1));
            }
            hashedPass = sb.toString();
        }catch (Exception e) {
            throw new RuntimeException("[InternalServerError] problem hashing password");
        }

        String tablePass = item.getString("password");
        if (!tablePass.equals(hashedPass)) {
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

        byte[] image = Base64.getDecoder().decode(request.getImage());
        ByteArrayInputStream bIStream = new ByteArrayInputStream(image);
        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setContentType("image/png");
        String bucketName = "brennan-tweeter-images";
        String key = request.getUsername() + "_avatar.png";
        PutObjectRequest put = new PutObjectRequest(bucketName, key, bIStream, metadata);

        s3.putObject(put);
        String imageUrl = s3.getUrl(bucketName, key).toExternalForm();

        if (imageUrl == null || imageUrl == "") {
            imageUrl = "https://brennan-tweeter-images.s3.us-west-2.amazonaws.com/sadge.jpg";
        }


        String hashedPass;
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            md.update(request.getPassword().getBytes());
            byte[] bytes = md.digest();
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < bytes.length; ++i) {
                sb.append(Integer.toString((bytes[i] & 0xff) + 0x100, 16).substring(1));
            }
            hashedPass = sb.toString();
        }catch (Exception e) {
            throw new RuntimeException("[InternalServerError] problem hashing password" + e.getMessage());
        }

        item = new Item().withPrimaryKey("UserAlias", request.getUsername())
                .withString("password", hashedPass)
                .withString("firstName", request.getFirstName())
                .withString("lastName", request.getLastName())
                .withString("imageUrl", imageUrl);

        table.putItem(item);

        String first = request.getFirstName();
        String last = request.getLastName();
        String alias = request.getUsername();
        User user = new User(first, last, alias, imageUrl);
        return user;
    }

    @Override
    public UserResponse getUser(UserRequest request) {
        AuthToken token = request.getAuthToken();
        AuthTokenDynamoDAO aDAO = new AuthTokenDynamoDAO();

        if (! aDAO.checkAuthToken(token)) {
            throw new RuntimeException("[InternalServerError] invalid authtoken");
        }

        Table table = dynamoDB.getTable(TableName);
        Item item = table.getItem("UserAlias", request.getTargetUserAlias());

        String first = item.getString("firstName");
        String last = item.getString("lastName");
        String alias = item.getString("UserAlias");
        String img = item.getString("imageUrl");
        User user = new User(first, last, alias, img);

        return new UserResponse(user);
    }




}
