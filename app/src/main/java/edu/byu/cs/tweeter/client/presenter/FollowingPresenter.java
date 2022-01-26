package edu.byu.cs.tweeter.client.presenter;

import android.util.Log;

import java.util.List;

import edu.byu.cs.tweeter.client.service.FollowingService;
import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.User;

public class FollowingPresenter implements FollowingService.Observer {

    private static final String LOG_TAG = "FollowingPresenter";
    public static final int PAGE_SIZE = 10;

    private final View view;
    private final User user;
    private final AuthToken authToken;

    private User lastFollowee;
    private boolean hasMorePages = true;
    private boolean isLoading = false;

    public interface View {
        void setLoading(boolean value);
        void selectUser(User user);
        void addItems(List<User> newUsers);
        void displayErrorMessage(String message);
    }

    public FollowingPresenter(View view, User user, AuthToken authToken) {
        this.view = view;
        this.user = user;
        this.authToken = authToken;
    }

    @Override
    public void handleFolloweeSuccess(List<User> followees, boolean hasMorePages) {
        if (followees.size() > 0) {
            setLastFollowee(followees.get(followees.size() - 1));
        }
        else {
            setLastFollowee(null);
        }

        setHasMorePages(hasMorePages);
        view.setLoading(false);
        view.addItems(followees);
        setLoading(false);
    }

    @Override
    public void handleUserSuccess(User user) {
        view.setLoading(false);
        view.selectUser(user);
        setLoading(false);
    }

    @Override
    public void handleFolloweeFailure(String message) {
        String eMsg = "Failed to retrieve followees: " + message;
        Log.e(LOG_TAG, eMsg);

        view.setLoading(false);
        view.displayErrorMessage(eMsg);
        setLoading(false);
    }

    @Override
    public void handleUserFailure(String message) {
        String eMsg = "Failed to retrieve user: " + message;
        Log.e(LOG_TAG, eMsg);

        view.setLoading(false);
        view.displayErrorMessage(eMsg);
        setLoading(false);
    }

    @Override
    public void handleFolloweeException(Exception exception) {
        String errorMessage = "Failed to retrieve followees because of exception: " + exception.getMessage();
        Log.e(LOG_TAG, errorMessage, exception);

        view.setLoading(false);
        view.displayErrorMessage(errorMessage);
        setLoading(false);
    }

    @Override
    public void handleUserException(Exception exception) {
        String errorMessage = "Failed to retrieve user because of exception: " + exception.getMessage();
        Log.e(LOG_TAG, errorMessage, exception);

        view.setLoading(false);
        view.displayErrorMessage(errorMessage);
        setLoading(false);
    }

    public User getUser() {
        return user;
    }

    public User getLastFollowee() {
        return lastFollowee;
    }

    public void setLastFollowee(User lastFollowee) {
        this.lastFollowee = lastFollowee;
    }

    public boolean isHasMorePages() {
        return hasMorePages;
    }

    public void setHasMorePages(boolean hasMorePages) {
        this.hasMorePages = hasMorePages;
    }

    public boolean isLoading() {
        return isLoading;
    }

    private void setLoading(boolean loading) {
        isLoading = loading;
    }

    public void loadMoreItems() {
        if (!isLoading && hasMorePages) {
            setLoading(true);
            view.setLoading(true);

            getFollowing(authToken, user, PAGE_SIZE, lastFollowee);
        }
    }

    public void loadSelectedUser(String alias) {
        if (!isLoading) {
            setLoading(true);
            view.setLoading(true);

            getSelectedUser(authToken, alias);
        }
    }

    public void  getSelectedUser(AuthToken authToken, String alias) {
        getFollowingService().getSelectedUser(authToken, alias, this);
    }

    public void getFollowing(AuthToken authToken, User targetUser, int limit, User lastFollowee) {
        getFollowingService().getFollowees(authToken, targetUser, limit, lastFollowee, this);
    }

    public boolean loadMore(int visible, int first, int total) {
        if (!isLoading() && isHasMorePages()) {
            if ((visible + first) >=
                    total && first >= 0) {
                return true;
            }
        }
        return false;
    }

    public FollowingService getFollowingService() {
        return new FollowingService();
    }
}
