package edu.byu.cs.tweeter.client.backgroundTask;


import android.os.Handler;
import android.util.Log;

import java.io.IOException;
import java.util.List;

import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.Status;
import edu.byu.cs.tweeter.model.domain.User;
import edu.byu.cs.tweeter.model.net.TweeterRemoteException;
import edu.byu.cs.tweeter.model.net.request.FeedRequest;
import edu.byu.cs.tweeter.model.net.request.FollowerRequest;
import edu.byu.cs.tweeter.model.net.response.FeedResponse;
import edu.byu.cs.tweeter.model.net.response.FollowerResponse;
import edu.byu.cs.tweeter.util.Pair;

/**
 * Background task that retrieves a page of statuses from a user's feed.
 */
public class GetFeedTask extends PagedStatusTask {
    static final String URL_PATH = "/getfeed";

    public GetFeedTask(AuthToken authToken, User targetUser, int limit, Status lastStatus,
                       Handler messageHandler) {
        super(authToken, targetUser, limit, lastStatus, messageHandler);
    }

    @Override
    protected Pair<List<Status>, Boolean> getItems() {
        return getFakeData().getPageOfStatus(getLastItem(), getLimit());
    }

    @Override
    protected final void runTask() {
        try {
            String targetUserAlias = targetUser == null ? null : targetUser.getAlias();
            String lastStatus = lastItem == null ? null : lastItem.getPost();

            FeedRequest request = new FeedRequest(authToken, targetUserAlias, limit, lastStatus);
            FeedResponse response = getServerFacade().getFeed(request, URL_PATH);

            if(response.isSuccess()) {
                this.items = response.getPosts();
                this.hasMorePages = response.getHasMorePages();
                sendSuccessMessage();
            }
            else {
                sendFailedMessage(response.getMessage());
            }
        } catch (IOException | TweeterRemoteException ex) {
            Log.e(LOG_TAG, "Exception in GetFeedTask.", ex);
            sendExceptionMessage(ex);
        }
    }
}
