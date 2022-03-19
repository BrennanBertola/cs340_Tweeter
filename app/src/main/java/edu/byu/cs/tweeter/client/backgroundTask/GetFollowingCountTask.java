package edu.byu.cs.tweeter.client.backgroundTask;


import android.os.Handler;
import android.util.Log;

import java.io.IOException;

import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.User;
import edu.byu.cs.tweeter.model.net.TweeterRemoteException;
import edu.byu.cs.tweeter.model.net.request.FolloweeCountRequest;
import edu.byu.cs.tweeter.model.net.response.FolloweeCountResponse;

/**
 * Background task that queries how many other users a specified user is following.
 */
public class GetFollowingCountTask extends GetCountTask {
    static final String URL_PATH = "/followeecount";

    public GetFollowingCountTask(AuthToken authToken, User targetUser, Handler messageHandler) {
        super(authToken, targetUser, messageHandler);
    }

    @Override
    protected String runCountTask() {
        try {
            String targetUserAlias = targetUser == null ? null : targetUser.getAlias();

            FolloweeCountRequest request = new FolloweeCountRequest(authToken, targetUserAlias);
            FolloweeCountResponse response = getServerFacade().getFolloweeCount(request, URL_PATH);

            if(response.isSuccess()) {
                count = response.getCount();
                return null;
            }
            return response.getMessage();
        }
        catch (IOException | TweeterRemoteException ex) {
            Log.e(LOG_TAG, "Exception in GetFolloweeCountTask.", ex);
            sendExceptionMessage(ex);
        }
        return "error";
    }
}
