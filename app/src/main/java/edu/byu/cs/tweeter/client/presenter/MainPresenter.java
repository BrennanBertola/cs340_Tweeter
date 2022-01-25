package edu.byu.cs.tweeter.client.presenter;

import android.util.Log;

import edu.byu.cs.tweeter.client.service.MainService;
import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.User;

public class MainPresenter implements MainService.Observer {
    private static final String LOG_TAG = "MainPresenter";

    private final View view;

    public interface View {
        void follow();
        void unfollow();
        void updateFollowButton();
        void updateFollowerCount(int count);
        void updateFollowingCount(int count);
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
    public void handleFollowerCountSuccess(int count) {
        view.updateFollowerCount(count);
    }

    @Override
    public void handleFollowingCountSuccess(int count) {
        view.updateFollowingCount(count);
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

    public void getFollowingCOunt (AuthToken authToken, User user) {
        getMainService().getFollowingCount(authToken, user, this);
    }

    public MainService getMainService() {return new MainService();}
}
