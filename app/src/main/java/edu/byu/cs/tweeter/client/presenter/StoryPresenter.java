package edu.byu.cs.tweeter.client.presenter;

import android.util.Log;
import android.util.Pair;

import java.util.List;

import edu.byu.cs.tweeter.client.service.StatusService;
import edu.byu.cs.tweeter.client.service.UserService;
import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.Status;
import edu.byu.cs.tweeter.model.domain.User;

public class StoryPresenter implements StatusService.Observer, UserService.Observer {

    private static final String LOG_TAG = "StoryPresenter";
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

    public StoryPresenter(View view, User user, AuthToken authToken) {
        this.view = view;
        this.user = user;
        this.authToken = authToken;
    }

    //====== remove when fixing presenters ======//
    @Override
    public void handleFeedSuccess(Pair<List<Status>, Boolean> pair) {

    }

    @Override
    public void handleLoginSuccess(User user, AuthToken authToken) {

    }

    @Override
    public void handleLogoutSuccess() {

    }

    @Override
    public void handleRegisterSuccess(User user, AuthToken authToken) {

    }

    @Override
    public void handlePostSuccess() {

    }
    //========================================//

    @Override
    public void handleStorySuccess(Pair<List<Status>, Boolean> pair) {
        List<Status> statuses = pair.first;
        if (statuses.size() > 0) {
            setLastStatus(statuses.get(statuses.size() - 1));
        }
        else {
            setLastStatus(null);
        }

        setHasMorePages(pair.second);
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
    public void handleFailure(String message) {
        Log.e(LOG_TAG, message);

        view.setLoading(false);
        view.displayErrorMessage(message);
        setLoading(false);
    }

    @Override
    public void handleException(String message, Exception exception) {
        Log.e(LOG_TAG, message, exception);

        view.setLoading(false);
        view.displayErrorMessage(message);
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
        getUserService().getSelectedUser(authToken, alias, this);
    }

    public void getStatuses(AuthToken authToken, User targetUser, int limit, Status lastStatus) {
        getStatusService().getStory(authToken, targetUser, limit, lastStatus, this);
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

    public StatusService getStatusService() {
        return new StatusService();
    }
    public UserService getUserService() {return new UserService();}
}
