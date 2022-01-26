package edu.byu.cs.tweeter.client.presenter;

import android.util.Log;

import java.util.List;

import edu.byu.cs.tweeter.client.service.FollowerService;
import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.User;

public class FollowerPresenter implements FollowerService.Observer {

    private static final String LOG_TAG = "FollowerPresenter";
    public static final int PAGE_SIZE = 10;

    private final View view;
    private final User user;
    private final AuthToken authToken;

    private User lastFollower;
    private boolean hasMorePages = true;
    private boolean isLoading = false;


    public interface View {
        void setLoading(boolean value);
        void selectUser(User user);
        void addItems(List<User> newUsers);
        void displayErrorMessage(String message);
    }

    public FollowerPresenter (View view, User user, AuthToken authToken) {
        this.view = view;
        this.user = user;
        this.authToken = authToken;
    }

    @Override
    public void handleFollowerSuccess(List<User> followers, boolean hasMorePages) {
        if (followers.size() > 0) {
            setLastFollower(followers.get(followers.size() - 1));
        }
        else {
            setLastFollower(null);
        }

        setHasMorePages(hasMorePages);
        view.setLoading(false);
        view.addItems(followers);
        setLoading(false);
    }

    @Override
    public void handleUserSuccess(User user) {
        view.setLoading(false);
        view.selectUser(user);
        setLoading(false);
    }

    @Override
    public void handleFollowerFailure(String message) {
        String eMsg = "Failed to retrieve followers: " + message;
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
    public void handleFollowerException(Exception exception) {
        String errorMessage = "Failed to retrieve followers because of exception: " + exception.getMessage();
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

    public User getLastFollower() {
        return lastFollower;
    }

    public boolean isHasMorePages() {
        return hasMorePages;
    }

    public boolean isLoading() {
        return isLoading;
    }

    public void setLastFollower(User lastFollower) {
        this.lastFollower = lastFollower;
    }

    public void setHasMorePages(boolean hasMorePages) {
        this.hasMorePages = hasMorePages;
    }

    public void setLoading(boolean loading) {
        isLoading = loading;
    }

    public void loadMoreItems() {
        if (!isLoading && hasMorePages) {
            setLoading(true);
            view.setLoading(true);

            getFollowers(authToken, user, PAGE_SIZE, lastFollower);
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
        getFollowerService().getSelectedUser(authToken, alias, this);
    }

    public void getFollowers(AuthToken authToken, User targetUser, int limit, User lastFollowee) {
        getFollowerService().getFollowers(authToken, targetUser, limit, lastFollowee, this);
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

    public FollowerService getFollowerService() {return new FollowerService();}
}
