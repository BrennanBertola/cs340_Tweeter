package edu.byu.cs.tweeter.client.backgroundTask;

import android.os.Handler;
import android.util.Log;

import java.io.IOException;

import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.User;
import edu.byu.cs.tweeter.model.net.TweeterRemoteException;
import edu.byu.cs.tweeter.model.net.request.FollowerCountRequest;
import edu.byu.cs.tweeter.model.net.response.FollowerCountResponse;

/**
 * Background task that queries how many followers a user has.
 */
public class GetFollowersCountTask extends GetCountTask {
    static final String URL_PATH = "/followercount";

    public GetFollowersCountTask(AuthToken authToken, User targetUser, Handler messageHandler) {
        super(authToken, targetUser, messageHandler);
    }

    @Override
    protected String runCountTask() {
        try {
            String targetUserAlias = targetUser == null ? null : targetUser.getAlias();

            FollowerCountRequest request = new FollowerCountRequest(authToken, targetUserAlias);
            FollowerCountResponse response = getServerFacade().getFollowerCount(request, URL_PATH);

            if(response.isSuccess()) {
                count = response.getCount();
                return null;
            }
            return response.getMessage();
        }
        catch (IOException | TweeterRemoteException ex) {
            Log.e(LOG_TAG, "Exception in GetFollowerCountTask.", ex);
            sendExceptionMessage(ex);
        }
        return "error";
    }
}
