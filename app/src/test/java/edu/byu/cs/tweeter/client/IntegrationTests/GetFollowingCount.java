package edu.byu.cs.tweeter.client.IntegrationTests;

import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;

import edu.byu.cs.tweeter.client.net.ServerFacade;
import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.net.TweeterRemoteException;
import edu.byu.cs.tweeter.model.net.request.FolloweeCountRequest;
import edu.byu.cs.tweeter.model.net.request.FollowerCountRequest;
import edu.byu.cs.tweeter.model.net.request.RegisterRequest;
import edu.byu.cs.tweeter.model.net.response.FolloweeCountResponse;
import edu.byu.cs.tweeter.model.net.response.FollowerCountResponse;

public class GetFollowingCount {
    FolloweeCountRequest request;
    FolloweeCountResponse response;
    ServerFacade serverFacade = new ServerFacade();
    AuthToken authToken = new AuthToken("1", "1");
    String target = "@allen";

    @Test
    public void GetFollowingCount_Success () {


        request = new FolloweeCountRequest(authToken, target);
        try {
            response = serverFacade.getFolloweeCount(request, "/followeecount");
        }
        catch (IOException | TweeterRemoteException ex) {
            Assert.assertTrue(false); //causes test to fail;
        }

        Assert.assertTrue(response.isSuccess());
        Assert.assertNull(response.getMessage());
        Assert.assertNotNull(response.getCount());
        Assert.assertEquals(21, response.getCount());
    }

    @Test
    public void GetFollowingCount_Fail() {
        request = new FolloweeCountRequest(authToken, null);
        try {
            response = serverFacade.getFolloweeCount(request, "/followeecount");
        }
        catch (IOException | TweeterRemoteException ex) {
            Assert.assertNotNull(ex.getMessage());
            Assert.assertEquals("[BadRequest] Request needs to have a target alias", ex.getMessage());
            return;
        }
        Assert.assertTrue(false); //causes test to fail;
    }
}
