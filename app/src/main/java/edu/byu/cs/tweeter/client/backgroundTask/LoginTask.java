package edu.byu.cs.tweeter.client.backgroundTask;

import android.os.Handler;
import android.util.Log;

import java.io.IOException;

import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.User;
import edu.byu.cs.tweeter.model.net.TweeterRemoteException;
import edu.byu.cs.tweeter.model.net.request.LoginRequest;
import edu.byu.cs.tweeter.model.net.response.LoginResponse;
import edu.byu.cs.tweeter.util.Pair;

/**
 * Background task that logs in a user (i.e., starts a session).
 */
public class LoginTask extends AuthenticateTask {
    static final String URL_PATH = "/login";

    public LoginTask(String username, String password, Handler messageHandler) {
        super(messageHandler, username, password);
    }

    @Override
    protected Pair<User, AuthToken> runAuthenticationTask() {
        try {
            LoginRequest request = new LoginRequest(username, password);
            LoginResponse response = getServerFacade().login(request, URL_PATH);

            if (response.isSuccess()) {
                User loggedInUser = response.getUser();
                AuthToken authToken = response.getAuthToken();
                return new Pair<>(loggedInUser, authToken);
            } else {
                sendFailedMessage(response.getMessage());
            }
        }
        catch (IOException | TweeterRemoteException ex) {
            Log.e(LOG_TAG, "Failed to login", ex);
            sendExceptionMessage(ex);
        }
        return null;
    }
}
