package edu.byu.cs.tweeter.client.presenter;

import android.util.Log;

import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.Status;
import edu.byu.cs.tweeter.model.domain.User;

public class StoryPresenter extends PagedPresenter<StoryPresenter.StoryView, Status> {
    private static final String LOG_TAG = "StoryPresenter";

    public interface StoryView extends PagedPresenter.PagedView<Status> { }

    public StoryPresenter(StoryView view, User user, AuthToken authToken) {
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
        getStatusService().getStory(authToken, targetUser, limit, lastStatus, this);
    }
}
