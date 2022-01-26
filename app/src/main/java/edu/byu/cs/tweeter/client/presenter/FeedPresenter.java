package edu.byu.cs.tweeter.client.presenter;


import android.util.Log;

import java.util.List;

import edu.byu.cs.tweeter.client.service.FeedService;
import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.Status;
import edu.byu.cs.tweeter.model.domain.User;

public class FeedPresenter implements FeedService.Observer {

    private static final String LOG_TAG = "FeedPresenter";
    public static final int PAGE_SIZE = 10;

    private final View view;
    private final User user;
    private final AuthToken authToken;

    private Status lastStatus;
    private boolean hasMorePages = true;
    private boolean isLoading = false;

    public interface View {
        void setLoading(boolean value);
        void selectUser(User user);
        void addItems(List<Status> newStatuses);
        void displayErrorMessage(String message);
    }

    public FeedPresenter(View view, User user, AuthToken authToken) {
        this.view = view;
        this.user = user;
        this.authToken = authToken;
    }

    @Override
    public void handleFeedSuccess(List<Status> statuses, boolean hasMorePages) {
        if (statuses.size() > 0) {
            setLastStatus(statuses.get(statuses.size() - 1));
        }
        else {
            setLastStatus(null);
        }

        setHasMorePages(hasMorePages);
        view.setLoading(false);
        view.addItems(statuses);
        setLoading(false);
    }

    @Override
    public void handleUserSuccess(User user) {
        view.setLoading(false);
        view.selectUser(user);
        setLoading(false);
    }

    @Override
    public void handleFeedFailure(String message) {
        String eMsg = "Failed to retrieve feed: " + message;
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
    public void handleFeedException(Exception exception) {
        String errorMessage = "Failed to retrieve feed because of exception: " + exception.getMessage();
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

    public Status getLastStatus() {
        return lastStatus;
    }

    public void setLastStatus(Status lastStatus) {
        this.lastStatus = lastStatus;
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

            getStatuses(authToken, user, PAGE_SIZE, lastStatus);
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
        getFeedService().getSelectedUser(authToken, alias, this);
    }

    public void getStatuses(AuthToken authToken, User targetUser, int limit, Status lastStatus) {
        getFeedService().getFeed(authToken, targetUser, limit, lastStatus, this);
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

    public FeedService getFeedService() {
        return new FeedService();
    }
}
