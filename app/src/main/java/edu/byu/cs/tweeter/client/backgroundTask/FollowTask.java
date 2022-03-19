package edu.byu.cs.tweeter.client.backgroundTask;

import android.os.Handler;
import android.util.Log;

import java.io.IOException;

import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.User;
import edu.byu.cs.tweeter.model.net.TweeterRemoteException;
import edu.byu.cs.tweeter.model.net.request.FollowRequest;
import edu.byu.cs.tweeter.model.net.response.FollowResponse;

/**
 * Background task that establishes a following relationship between two users.
 */
public class FollowTask extends AuthenticatedTask {
    static final String URL_PATH = "/follow";
    /**
     * The user that is being followed.
     */
    private final User targetUser;

    public FollowTask(AuthToken authToken, User followee, Handler messageHandler) {
        super(authToken, messageHandler);
        this.targetUser = followee;
    }

    @Override
    protected void runTask() {
        try {
            String targetUserAlias = targetUser == null ? null : targetUser.getAlias();

            FollowRequest request = new FollowRequest(authToken, targetUserAlias);
            FollowResponse response = getServerFacade().follow(request, URL_PATH);
            // We could do this from the presenter, without a task and handler, but we will
            // eventually access the database from here when we aren't using dummy data.

            // Call sendSuccessMessage if successful
            if (response.isSuccess()) {
                sendSuccessMessage();
            }
            else {
                sendFailedMessage(response.getMessage());
            }
            // or call sendFailedMessage if not successful
            // sendFailedMessage()
        }
        catch (IOException | TweeterRemoteException ex) {
            Log.e(LOG_TAG, "Failed to follow", ex);
            sendExceptionMessage(ex);
        }
    }

}