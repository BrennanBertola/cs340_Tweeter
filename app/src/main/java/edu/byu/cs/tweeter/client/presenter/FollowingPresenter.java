package edu.byu.cs.tweeter.client.presenter;

import android.util.Log;

import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.User;

public class FollowingPresenter extends PagedPresenter<FollowingPresenter.FollowingView, User> {
    private static final String LOG_TAG = "FollowingPresenter";

    public interface FollowingView extends PagedPresenter.PagedView<User> {}

    public FollowingPresenter(FollowingView view, User user, AuthToken authToken) {
        super(view, user, authToken);
    }

    @Override
    public void logError(String message) {Log.e(LOG_TAG, message);}

    @Override
    public void logError(String message, Exception exception) { Log.e(LOG_TAG, message, exception);}

    @Override
    protected void loadItems() {getFollowing(authToken, user, PAGE_SIZE, last);}

    public void getFollowing(AuthToken authToken, User targetUser, int limit, User lastFollowee) {
        getFollowService().getFollowees(authToken, targetUser, limit, lastFollowee, this);
    }
}
