package edu.byu.cs.tweeter.client.presenter;

import android.util.Log;
import android.util.Pair;

import java.util.List;

import edu.byu.cs.tweeter.client.service.FollowService;
import edu.byu.cs.tweeter.client.service.UserService;
import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.User;

public class FollowerPresenter extends PagedPresenter<FollowerPresenter.FollowerView, User> {
    private static final String LOG_TAG = "FollowerPresenter";

    public interface FollowerView extends PagedPresenter.PagedView<User> {}

    public FollowerPresenter (FollowerView view, User user, AuthToken authToken) {
        super(view, user, authToken);
    }

    @Override
    public void logError(String message) { Log.e(LOG_TAG, message);}

    @Override
    public void logError(String message, Exception exception) { Log.e(LOG_TAG, message, exception);}

    @Override
    public void loadItems() { getFollowers(authToken, user, PAGE_SIZE, last); }

    public void getFollowers(AuthToken authToken, User targetUser, int limit, User lastFollowee) {
        getFollowService().getFollowers(authToken, targetUser, limit, lastFollowee, this);
    }
}
