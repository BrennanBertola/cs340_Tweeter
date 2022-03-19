package edu.byu.cs.tweeter.client.IntegrationTests;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;

import edu.byu.cs.tweeter.client.net.ServerFacade;
import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.net.TweeterRemoteException;
import edu.byu.cs.tweeter.model.net.request.FollowerRequest;
import edu.byu.cs.tweeter.model.net.request.RegisterRequest;
import edu.byu.cs.tweeter.model.net.response.FollowerResponse;

public class GetFollowers {
    FollowerRequest request;
    ServerFacade serverFacade = new ServerFacade();
    FollowerResponse response;
    AuthToken authToken = new AuthToken("1", "1");
    String target = "@allen";
    int limit = 10;
    String last = null;


    @Test
    public void GetFollowers_Success() {
        last = null;

        request = new FollowerRequest(authToken, target, limit, last);
        try {
            response = serverFacade.getFollower(request, "/getfollowers");


            Assert.assertTrue(response.isSuccess());
            Assert.assertNull(response.getMessage());
            Assert.assertTrue(response.getFollowers().size() <= limit);

            while (response.getHasMorePages()) {
                last = response.getFollowers().get(response.getFollowers().size() - 1).getAlias();
                request = new FollowerRequest(authToken, target, limit, last);
                response = serverFacade.getFollower(request, "/getfollowers");
                Assert.assertTrue(response.isSuccess());
                Assert.assertNull(response.getMessage());
                Assert.assertTrue(response.getFollowers().size() <= limit);
            }

        }catch (IOException | TweeterRemoteException ex) {
            Assert.assertTrue(false); //causes test to fail;
        }
    }

    @Test
    public void GetFollowers_Fail() {
        last = null;
        request = new FollowerRequest(authToken, null, limit, last);
        try {
            response = serverFacade.getFollower(request, "/getfollowers");
        }
        catch (IOException | TweeterRemoteException ex) {
            Assert.assertNotNull(ex.getMessage());
            Assert.assertEquals("[BadRequest] Request needs to have a follower alias", ex.getMessage());
            return;
        }
        Assert.assertTrue(false); //causes test to fail;
    }
}
