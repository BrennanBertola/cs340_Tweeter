package edu.byu.cs.tweeter.client.presenter;

import android.util.Log;

import edu.byu.cs.tweeter.client.service.MainService;
import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.Status;
import edu.byu.cs.tweeter.model.domain.User;

public class MainPresenter implements MainService.Observer {
    private static final String LOG_TAG = "MainPresenter";

    private final View view;

    public interface View {
        void follow();
        void unfollow();
        void updateFollowButton();
        void logout();
        void postDone();
        void updateFollowerCount(int count);
        void updateFollowingCount(int count);
        void isFollower(boolean isFollower);
        void displayErrorMessage(String message);
    }

    public MainPresenter(View view) {this.view = view;}

    @Override
    public void handleFollowSuccess() {
        view.follow();
        view.updateFollowButton();
    }

    @Override
    public void handleUnfollowSuccess() {
        view.unfollow();
        view.updateFollowButton();
    }

    @Override
    public void handlePostSuccess() {
        view.postDone();
    }

    @Override
    public void handleLogoutSuccess() {
        view.logout();
    }

    @Override
    public void handleFollowerCountSuccess(int count) {
        view.updateFollowerCount(count);
    }

    @Override
    public void handleFollowingCountSuccess(int count) {
        view.updateFollowingCount(count);
    }

    @Override
    public void handleIsFollowerSuccess(boolean isFollower) {
        view.isFollower(isFollower);
    }

    @Override
    public void handleFailure (String message) {
        Log.e(LOG_TAG, message);
        view.displayErrorMessage(message);
    }

    @Override
    public void handleException (String message, Exception ex) {
        Log.e(LOG_TAG, message, ex);
        view.displayErrorMessage(message);
    }

    public void follow(AuthToken authToken, User followee) {
        getMainService().follow(authToken, followee, this);
    }

    public void  unfollow(AuthToken authToken, User followee) {
        getMainService().unfollow(authToken, followee, this);
    }

    public void getFollowerCount(AuthToken authToken, User user) {
        getMainService().getFollowerCount(authToken, user, this);
    }

    public void getFollowingCount (AuthToken authToken, User user) {
        getMainService().getFollowingCount(authToken, user, this);
    }

    public void logout (AuthToken authToken) {
        getMainService().logout(authToken, this);
    }

    public void getFollowStatus (AuthToken authToken, User user, User selected) {
        getMainService().isFollower(authToken, user, selected, this);
    }

    public void post(AuthToken authToken, Status status) {
        getMainService().post(authToken, status, this);
    }

    public MainService getMainService() {return new MainService();}
}
