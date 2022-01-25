package edu.byu.cs.tweeter.client.service;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import edu.byu.cs.tweeter.client.backgroundTask.BackgroundTaskUtils;
import edu.byu.cs.tweeter.client.backgroundTask.FollowTask;
import edu.byu.cs.tweeter.client.backgroundTask.GetFollowersCountTask;
import edu.byu.cs.tweeter.client.backgroundTask.GetFollowingCountTask;
import edu.byu.cs.tweeter.client.backgroundTask.UnfollowTask;
import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.User;

public class MainService {

    public interface Observer {
        void handleFollowSuccess();
        void handleUnfollowSuccess();
        void handleFollowerCountSuccess(int count);
        void handleFollowingCountSuccess(int count);
        void handleFailure(String message);
        void handleException(String message, Exception ex);
    }

    public MainService() {}

    public void follow(AuthToken authToken, User followee, Observer observer) {
        FollowTask task = getFollowTask(authToken, followee, observer);
        BackgroundTaskUtils.runTask(task);
    }
    public FollowTask getFollowTask(AuthToken authToken, User followee, Observer observer) {
        return new FollowTask(authToken, followee, new MessageHandler(observer, "follow"));
    }

    public void unfollow(AuthToken authToken, User followee, Observer observer) {
        UnfollowTask task = getUnfollowTask(authToken, followee, observer);
        BackgroundTaskUtils.runTask(task);
    }
    public UnfollowTask getUnfollowTask (AuthToken authToken, User followee, Observer observer) {
        return new UnfollowTask(authToken, followee, new MessageHandler(observer, "unfollow"));
    }

    public void getFollowerCount(AuthToken authToken, User user, Observer observer) {
        GetFollowersCountTask task = getGetFollowersCountTask(authToken, user, observer);
        BackgroundTaskUtils.runTask(task);
    }
    public GetFollowersCountTask getGetFollowersCountTask (AuthToken authToken, User user, Observer observer) {
        return new GetFollowersCountTask(authToken, user, new MessageHandler(observer, "followerCount"));
    }

    public void getFollowingCount(AuthToken authToken, User user, Observer observer) {
        GetFollowingCountTask task = getGetFollowingCountTask(authToken, user, observer);
        BackgroundTaskUtils.runTask(task);
    }
    public GetFollowingCountTask getGetFollowingCountTask (AuthToken authToken, User user, Observer observer) {
        return new GetFollowingCountTask(authToken, user, new MessageHandler(observer, "followingCount"));
    }

    public class MessageHandler extends Handler {
        private final Observer observer;
        private final String type;

        public MessageHandler(Observer observer, String type) {
            this.observer = observer;
            this.type = type;
        }

        @Override
        public void handleMessage(Message msg) {
            Bundle bundle = msg.getData();
            if (type == "follow") {
                boolean success = bundle.getBoolean(FollowTask.SUCCESS_KEY);
                if (success) {
                    observer.handleFollowSuccess();
                }
                else if (msg.getData().containsKey(FollowTask.MESSAGE_KEY)) {
                    String eMsg = msg.getData().getString(FollowTask.MESSAGE_KEY);
                    observer.handleFailure("Failed to follow: " + eMsg);
                }
                else if (msg.getData().containsKey(FollowTask.EXCEPTION_KEY)) {
                    Exception ex = (Exception) msg.getData().getSerializable(FollowTask.EXCEPTION_KEY);
                    String eMsg = "Failed to follow because of exception: " + ex.getMessage();
                    observer.handleException(eMsg, ex);
                }
            }

            if (type == "unfollow") {
                boolean success = bundle.getBoolean(UnfollowTask.SUCCESS_KEY);
                if (success) {
                    observer.handleUnfollowSuccess();
                }
                else if (msg.getData().containsKey(UnfollowTask.MESSAGE_KEY)) {
                    String eMsg = msg.getData().getString(UnfollowTask.MESSAGE_KEY);
                    observer.handleFailure("Failed to unfollow: " + eMsg);
                }
                else if (msg.getData().containsKey(UnfollowTask.EXCEPTION_KEY)) {
                    Exception ex = (Exception) msg.getData().getSerializable(UnfollowTask.EXCEPTION_KEY);
                    String eMsg = "Failed to unfollow because of exception: " + ex.getMessage();
                    observer.handleException(eMsg, ex);
                }
            }

            if (type == "followerCount") {
                boolean success = bundle.getBoolean(GetFollowersCountTask.SUCCESS_KEY);
                if (success) {
                    int count = msg.getData().getInt(GetFollowersCountTask.COUNT_KEY);
                    observer.handleFollowerCountSuccess(count);
                }
                else if (msg.getData().containsKey(GetFollowersCountTask.MESSAGE_KEY)) {
                    String eMsg = msg.getData().getString(GetFollowersCountTask.MESSAGE_KEY);
                    observer.handleFailure("Failed to get follower count: " + eMsg);
                }
                else if (msg.getData().containsKey(GetFollowersCountTask.EXCEPTION_KEY)) {
                    Exception ex = (Exception) msg.getData().getSerializable( GetFollowersCountTask.EXCEPTION_KEY);
                    String eMsg = "Failed to follower count because of exception: " + ex.getMessage();
                    observer.handleException(eMsg, ex);
                }
            }

            if (type == "followingCount") {
                boolean success = bundle.getBoolean(GetFollowingCountTask.SUCCESS_KEY);
                if (success) {
                    int count = msg.getData().getInt(GetFollowingCountTask.COUNT_KEY);
                    observer.handleFollowingCountSuccess(count);
                }
                else if (msg.getData().containsKey(GetFollowingCountTask.MESSAGE_KEY)) {
                    String eMsg = msg.getData().getString(GetFollowingCountTask.MESSAGE_KEY);
                    observer.handleFailure("Failed to get following count: " + eMsg);
                }
                else if (msg.getData().containsKey(GetFollowingCountTask.EXCEPTION_KEY)) {
                    Exception ex = (Exception) msg.getData().getSerializable( GetFollowingCountTask.EXCEPTION_KEY);
                    String eMsg = "Failed to following count because of exception: " + ex.getMessage();
                    observer.handleException(eMsg, ex);
                }
            }
        }
    }
}
