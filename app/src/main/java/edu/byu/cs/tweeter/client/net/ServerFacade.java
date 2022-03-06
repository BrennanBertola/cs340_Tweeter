package edu.byu.cs.tweeter.client.net;

import java.io.IOException;

import edu.byu.cs.tweeter.client.backgroundTask.GetFollowersCountTask;
import edu.byu.cs.tweeter.model.net.TweeterRemoteException;
import edu.byu.cs.tweeter.model.net.request.FeedRequest;
import edu.byu.cs.tweeter.model.net.request.FollowRequest;
import edu.byu.cs.tweeter.model.net.request.FolloweeCountRequest;
import edu.byu.cs.tweeter.model.net.request.FollowerCountRequest;
import edu.byu.cs.tweeter.model.net.request.FollowerRequest;
import edu.byu.cs.tweeter.model.net.request.FollowingRequest;
import edu.byu.cs.tweeter.model.net.request.LoginRequest;
import edu.byu.cs.tweeter.model.net.request.StoryRequest;
import edu.byu.cs.tweeter.model.net.response.FeedResponse;
import edu.byu.cs.tweeter.model.net.response.FollowResponse;
import edu.byu.cs.tweeter.model.net.response.FolloweeCountResponse;
import edu.byu.cs.tweeter.model.net.response.FollowerCountResponse;
import edu.byu.cs.tweeter.model.net.response.FollowerResponse;
import edu.byu.cs.tweeter.model.net.response.FollowingResponse;
import edu.byu.cs.tweeter.model.net.response.LoginResponse;
import edu.byu.cs.tweeter.model.net.response.StoryResponse;

/**
 * Acts as a Facade to the Tweeter server. All network requests to the server should go through
 * this class.
 */
public class ServerFacade {

    // TODO: Set this to the invoke URL of your API. Find it by going to your API in AWS, clicking
    //  on stages in the right-side menu, and clicking on the stage you deployed your API to.
    private static final String SERVER_URL = "https://fw9u71tmdh.execute-api.us-west-2.amazonaws.com/tweeter";

    private final ClientCommunicator clientCommunicator = new ClientCommunicator(SERVER_URL);

    /**
     * Performs a login and if successful, returns the logged in user and an auth token.
     *
     * @param request contains all information needed to perform a login.
     * @return the login response.
     */
    public LoginResponse login(LoginRequest request, String urlPath) throws IOException, TweeterRemoteException {
        LoginResponse response = clientCommunicator.doPost(urlPath, request, null, LoginResponse.class);

        if(response.isSuccess()) {
            return response;
        } else {
            throw new RuntimeException(response.getMessage());
        }
    }

    /**
     * Returns the users that the user specified in the request is following. Uses information in
     * the request object to limit the number of followees returned and to return the next set of
     * followees after any that were returned in a previous request.
     *
     * @param request contains information about the user whose followees are to be returned and any
     *                other information required to satisfy the request.
     * @return the followees.
     */
    public FollowingResponse getFollowing(FollowingRequest request, String urlPath)
            throws IOException, TweeterRemoteException {

        FollowingResponse response = clientCommunicator.doPost(urlPath, request, null, FollowingResponse.class);

        if(response.isSuccess()) {
            return response;
        } else {
            throw new RuntimeException(response.getMessage());
        }
    }

    public FollowerResponse getFollower(FollowerRequest request, String urlPath)
            throws IOException, TweeterRemoteException {

        FollowerResponse response = clientCommunicator.doPost(urlPath, request, null, FollowerResponse.class);

        if(response.isSuccess()) {
            return response;
        } else {
            throw new RuntimeException(response.getMessage());
        }
    }

    public FollowResponse follow(FollowRequest request, String urlPath) throws IOException, TweeterRemoteException {
        FollowResponse response = clientCommunicator.doPost(urlPath, request, null, FollowResponse.class);

        if (response.isSuccess()) {
            return response;
        }
        else {
            throw new RuntimeException(response.getMessage());
        }
    }

    public FeedResponse getFeed(FeedRequest request, String urlPath) throws IOException, TweeterRemoteException {
        FeedResponse response = clientCommunicator.doPost(urlPath, request, null, FeedResponse.class);

        if (response.isSuccess()) {
            return response;
        }
        else {
            throw new RuntimeException(response.getMessage());
        }
    }

    public StoryResponse getStory(StoryRequest request, String urlPath) throws IOException, TweeterRemoteException {
        StoryResponse response = clientCommunicator.doPost(urlPath, request, null, StoryResponse.class);

        if (response.isSuccess()) {
            return response;
        }
        else {
            throw new RuntimeException(response.getMessage());
        }
    }

    public FollowerCountResponse getFollowerCount(FollowerCountRequest request, String urlPath) throws IOException, TweeterRemoteException {
        FollowerCountResponse response = clientCommunicator.doPost(urlPath, request, null, FollowerCountResponse.class);

        if (response.isSuccess()) {
            return response;
        }
        else {
            throw new RuntimeException(response.getMessage());
        }
    }

    public FolloweeCountResponse getFolloweeCount(FolloweeCountRequest request, String urlPath) throws IOException, TweeterRemoteException {
        FolloweeCountResponse response = clientCommunicator.doPost(urlPath, request, null, FolloweeCountResponse.class);

        if (response.isSuccess()) {
            return response;
        }
        else {
            throw new RuntimeException(response.getMessage());
        }
    }
}