package edu.byu.cs.tweeter.client.backgroundTask;


import android.os.Bundle;
import android.os.Handler;
import android.util.Log;

import java.io.IOException;

import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.User;
import edu.byu.cs.tweeter.model.net.TweeterRemoteException;
import edu.byu.cs.tweeter.model.net.request.UserRequest;
import edu.byu.cs.tweeter.model.net.response.UserResponse;

/**
 * Background task that returns the profile for a specified user.
 */
public class GetUserTask extends AuthenticatedTask {
    static final String URL_PATH = "/getuser";

    public static final String USER_KEY = "user";

    /**
     * Alias (or handle) for user whose profile is being retrieved.
     */
    private final String alias;

    private User user;

    public GetUserTask(AuthToken authToken, String alias, Handler messageHandler) {
        super(authToken, messageHandler);
        this.alias = alias;
    }

    @Override
    protected void runTask() {
        try {
            UserRequest request = new UserRequest(authToken, alias);
            UserResponse response = getServerFacade().getUser(request, URL_PATH);

            // Call sendSuccessMessage if successful
            if (response.isSuccess()) {
                this.user = response.getUser();
                sendSuccessMessage();
            }
            else {
                sendFailedMessage(response.getMessage());
            }
            // or call sendFailedMessage if not successful
            // sendFailedMessage()
        } catch (IOException | TweeterRemoteException ex) {
            Log.e(LOG_TAG, "Exception in GetUserTask. " + ex.getMessage(), ex);
            sendExceptionMessage(ex);
        }
    }

    @Override
    protected void loadSuccessBundle(Bundle msgBundle) {
        msgBundle.putSerializable(USER_KEY, user);
    }
}
