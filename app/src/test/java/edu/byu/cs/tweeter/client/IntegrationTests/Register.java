package edu.byu.cs.tweeter.client.IntegrationTests;

import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;

import edu.byu.cs.tweeter.client.net.ServerFacade;
import edu.byu.cs.tweeter.model.domain.User;
import edu.byu.cs.tweeter.model.net.TweeterRemoteException;
import edu.byu.cs.tweeter.model.net.request.RegisterRequest;
import edu.byu.cs.tweeter.model.net.response.RegisterResponse;

public class Register {
    RegisterRequest request;
    RegisterResponse response;
    ServerFacade serverFacade = new ServerFacade();
    String first = "Allen";
    String last = "Anderson";
    String username = "@allen";
    String password = "test";
    String image = "image";

    @Test
    public void Register_Success() {
        request = new RegisterRequest(first, last, username, password, image);
        try {
            response = serverFacade.register(request, "/register");
        }catch (IOException | TweeterRemoteException ex) {
            Assert.assertTrue(false); //causes test to fail;
        }

        User user = response.getUser();

        Assert.assertTrue(response.isSuccess());
        Assert.assertNull(response.getMessage());

        Assert.assertEquals(first, user.getFirstName());
        Assert.assertEquals(last, user.getLastName());
        Assert.assertEquals(username, user.getAlias());
    }

    @Test
    public void Register_Fail() {
        request = new RegisterRequest(null, last, username, password, image);
        try {
            response = serverFacade.register(request, "/register");
        }
        catch (IOException | TweeterRemoteException ex) {
            Assert.assertNotNull(ex.getMessage());
            Assert.assertEquals("[BadRequest] Missing a first name", ex.getMessage());
            return;
        }
        Assert.assertTrue(false); //causes test to fail;
    }
}
