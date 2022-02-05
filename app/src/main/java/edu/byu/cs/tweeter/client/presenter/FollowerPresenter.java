package edu.byu.cs.tweeter.client.presenter;

import android.util.Log;

import java.util.List;

import edu.byu.cs.tweeter.client.service.FollowService;
import edu.byu.cs.tweeter.client.service.UserService;
import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.User;

public class FollowerPresenter implements FollowService.Observer, UserService.Observer {

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

    //====== remove when fixing presenters ======//
    @Override
    public void handleFolloweeSuccess(List<User> followers, boolean hasMorePages) {

    }

    @Override
    public void handleFollowerCountSuccess(int count) {

    }

    @Override
    public void handleFollowingCountSuccess(int count) {

    }

    @Override
    public void handleIsFollowerSuccess(boolean isFollower) {

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
    public void handleFollowSuccess() {

    }

    @Override
    public void handleUnfollowSuccess() {

    }
    //========================================//

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
        getUserService().getSelectedUser(authToken, alias, this);
    }

    public void getFollowers(AuthToken authToken, User targetUser, int limit, User lastFollowee) {
        getFollowService().getFollowers(authToken, targetUser, limit, lastFollowee, this);
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

    public FollowService getFollowService() {return new FollowService();}
    public UserService getUserService() {return new UserService();}
}
