package edu.byu.cs.tweeter.client.presenter;

import android.util.Pair;

import java.util.List;

import edu.byu.cs.tweeter.client.service.FollowService;
import edu.byu.cs.tweeter.client.service.StatusService;
import edu.byu.cs.tweeter.client.service.UserService;
import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.User;

//U is the view type, T is Statuses or Users
public abstract class Presenter<U, T> implements UserService.UserObserver,
        StatusService.StatusObserver<T>, FollowService.FollowObserver<T> {
    protected final U view;

    protected UserService userService;
    protected StatusService statusService;
    protected FollowService followService;

    protected Presenter(U view) {
        this.view = view;
    }

    protected abstract void logError (String message);
    protected abstract void logError (String message, Exception exception);

    @Override
    public void handleFailure(String message) {
        View tmpView = (View) view;
        tmpView.displayMessage(message);
        //logError(message);
    }

    @Override
    public void handleException(String message, Exception exception) {
        View tmpView = (View) view;
        tmpView.displayMessage(message);
        //logError(message, exception);
    }

    // all the observer functions are declared in here so the real presenters can only override the ones they need to

    @Override
    public void handleFollowSuccess() {}

    @Override
    public void handleUnfollowSuccess() {}

    @Override
    public void handleFollowerCountSuccess(int count) {}

    @Override
    public void handleFollowingCountSuccess(int count) {}

    @Override
    public void handleIsFollowerSuccess(boolean isFollower) {}

    @Override
    public void handlePagedSuccess(Pair<List<T>, Boolean> pair) {}

    @Override
    public void handlePostSuccess() {}

    @Override
    public void handleUserSuccess(User user) {}

    @Override
    public void handleLoginSuccess(User user, AuthToken authToken) {
    }

    @Override
    public void handleLogoutSuccess() {}

    @Override
    public void handleRegisterSuccess(User user, AuthToken authToken) {}


    public UserService getUserService() {
        if (userService == null) {
            userService = new UserService();
        }
        return userService;
    }
    public FollowService getFollowService() {
        if (followService == null) {
            followService = new FollowService();
        }
        return followService;
    }
    public StatusService getStatusService() {
        if (statusService == null) {
            statusService = new StatusService();
        }
        return statusService;
    }
}
