package edu.byu.cs.tweeter.client.presenter;


import android.util.Log;
import android.util.Pair;

import java.util.List;

import edu.byu.cs.tweeter.client.service.StatusService;
import edu.byu.cs.tweeter.client.service.UserService;
import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.Status;
import edu.byu.cs.tweeter.model.domain.User;

public class FeedPresenter extends PagedPresenter<FeedPresenter.FeedView, Status> {
    private static final String LOG_TAG = "FeedPresenter";


    public interface FeedView extends PagedPresenter.PagedView<Status> {}

    public FeedPresenter(FeedView view, User user, AuthToken authToken) {
        super(view, user, authToken);
    }

    @Override
    public void logError(String message) {
        Log.e(LOG_TAG, message);
    }

    @Override
    public void logError(String message, Exception exception) {
        Log.e(LOG_TAG, message, exception);
    }

    @Override
    public void loadItems() {
        getStatuses(authToken, user, PAGE_SIZE, last);
    }

    public void getStatuses(AuthToken authToken, User targetUser, int limit, Status lastStatus) {
        getStatusService().getFeed(authToken, targetUser, limit, lastStatus, this);
    }
}
