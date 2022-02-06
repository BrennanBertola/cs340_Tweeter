package edu.byu.cs.tweeter.client.presenter;

import android.util.Log;
import android.util.Pair;

import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.List;

import edu.byu.cs.tweeter.client.cache.Cache;
import edu.byu.cs.tweeter.client.service.FollowService;
import edu.byu.cs.tweeter.client.service.StatusService;
import edu.byu.cs.tweeter.client.service.UserService;
import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.Status;
import edu.byu.cs.tweeter.model.domain.User;

public class MainPresenter extends Presenter<MainPresenter.MainView, User> {
    private static final String LOG_TAG = "MainPresenter";

    public interface MainView extends View{
        void follow();
        void unfollow();
        void updateFollowButton();
        void logout();
        void postDone();
        void updateFollowerCount(int count);
        void updateFollowingCount(int count);
        void isFollower(boolean isFollower);
    }

    public MainPresenter(MainView view) {super(view);}

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
        Cache.getInstance().clearCache();
        view.logout();
    }

    @Override
    public void handleRegisterSuccess(User user, AuthToken authToken) {

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
    public void logError (String message) {
        Log.e(LOG_TAG, message);
    }

    @Override
    public void logError (String message, Exception ex) {
        Log.e(LOG_TAG, message, ex);
    }

    public void follow(AuthToken authToken, User followee) {
        getFollowService().follow(authToken, followee, this);
    }

    public void  unfollow(AuthToken authToken, User followee) {
        getFollowService().unfollow(authToken, followee, this);
    }

    public void getFollowerCount(AuthToken authToken, User user) {
        getFollowService().getFollowerCount(authToken, user, this);
    }

    public void getFollowingCount (AuthToken authToken, User user) {
        getFollowService().getFollowingCount(authToken, user, this);
    }

    public void logout (AuthToken authToken) {
        getUserService().logout(authToken, this);
    }

    public void getFollowStatus (AuthToken authToken, User user, User selected) {
        getFollowService().isFollower(authToken, user, selected, this);
    }

    public void post(AuthToken authToken, Status status) {
        getStatusService().post(authToken, status, this);
    }

    public List<String> parseURLs(String post) throws MalformedURLException {
        List<String> containedUrls = new ArrayList<>();
        for (String word : post.split("\\s")) {
            if (word.startsWith("http://") || word.startsWith("https://")) {

                int index = findUrlEndIndex(word);

                word = word.substring(0, index);

                containedUrls.add(word);
            }
        }

        return containedUrls;
    }

    public List<String> parseMentions(String post) {
        List<String> containedMentions = new ArrayList<>();

        for (String word : post.split("\\s")) {
            if (word.startsWith("@")) {
                word = word.replaceAll("[^a-zA-Z0-9]", "");
                word = "@".concat(word);

                containedMentions.add(word);
            }
        }

        return containedMentions;
    }

    public int findUrlEndIndex(String word) {
        if (word.contains(".com")) {
            int index = word.indexOf(".com");
            index += 4;
            return index;
        } else if (word.contains(".org")) {
            int index = word.indexOf(".org");
            index += 4;
            return index;
        } else if (word.contains(".edu")) {
            int index = word.indexOf(".edu");
            index += 4;
            return index;
        } else if (word.contains(".net")) {
            int index = word.indexOf(".net");
            index += 4;
            return index;
        } else if (word.contains(".mil")) {
            int index = word.indexOf(".mil");
            index += 4;
            return index;
        } else {
            return word.length();
        }
    }

}
