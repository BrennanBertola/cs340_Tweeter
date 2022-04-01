package edu.byu.cs.tweeter.server.BatchWrite;

import java.util.ArrayList;
import java.util.List;

import edu.byu.cs.tweeter.model.domain.User;
import edu.byu.cs.tweeter.server.dao.FollowsDAO;
import edu.byu.cs.tweeter.server.dao.UserDAO;
import edu.byu.cs.tweeter.server.factory.DynamoDAOFactory;

public class Filler {

    public static void main(String[] args) {
        fillDatabase();
    }

    // How many follower users to add
    // We recommend you test this with a smaller number first, to make sure it works for you
    private final static int NUM_USERS = 10000;

    // The alias of the user to be followed by each user created
    // This example code does not add the target user, that user must be added separately.
    private final static String FOLLOW_TARGET = "@test";


    public static void fillDatabase() {
        DynamoDAOFactory factory = new DynamoDAOFactory();
        String url = "https://brennan-tweeter-images.s3.us-west-2.amazonaws.com/sadge.jpg";

        // Get instance of DAOs by way of the Abstract Factory Pattern
        UserDAO userDAO = factory.getUserDAO();
        FollowsDAO followDAO = factory.getFollowDAO();

        List<String> followers = new ArrayList<>();
        List<User> users = new ArrayList<>();

        // Iterate over the number of users you will create
        for (int i = 1; i <= NUM_USERS; i++) {

            String first = "first " + i;
            String last = "last " + i;
            String alias = "@guy" + i;

            // Note that in this example, a UserDTO only has a name and an alias.
            // The url for the profile image can be derived from the alias in this example
            User user = new User(first, last, alias, url);

            // Note that in this example, to represent a follows relationship, only the aliases
            // of the two users are needed
            users.add(user);
            followers.add(alias);
        }

        // Call the DAOs for the database logic
        if (users.size() > 0) {
            userDAO.addUserBatch(users);
        }
        System.out.println("users added");
//        if (followers.size() > 0) {
//            followDAO.addFollowersBatch(followers, FOLLOW_TARGET);
//        }
//        System.out.println("followers added");
    }
}
