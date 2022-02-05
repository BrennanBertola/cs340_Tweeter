package edu.byu.cs.tweeter.client.service;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;

import java.util.List;

import edu.byu.cs.tweeter.client.backgroundTask.BackgroundTaskUtils;
import edu.byu.cs.tweeter.client.backgroundTask.FollowTask;
import edu.byu.cs.tweeter.client.backgroundTask.GetFollowersCountTask;
import edu.byu.cs.tweeter.client.backgroundTask.GetFollowersTask;
import edu.byu.cs.tweeter.client.backgroundTask.GetFollowingCountTask;
import edu.byu.cs.tweeter.client.backgroundTask.GetFollowingTask;
import edu.byu.cs.tweeter.client.backgroundTask.IsFollowerTask;
import edu.byu.cs.tweeter.client.backgroundTask.UnfollowTask;
import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.User;

public class FollowService {

    public interface Observer {
        void handleFollowSuccess();
        void handleUnfollowSuccess();
        void handleFollowerSuccess(List<User> followers, boolean hasMorePages);
        void handleFolloweeSuccess(List<User> followees, boolean hasMorePages);
        void handleFollowerCountSuccess(int count);
        void handleFollowingCountSuccess(int count);
        void handleIsFollowerSuccess(boolean isFollower);
        void handleFailure(String message);
        void handleException(String message, Exception exception);
    }

    public FollowService() {
    }

    public void getFollowers(AuthToken authToken, User targetUser, int limit, User lastFollower,
                             Observer observer) {
        GetFollowersTask followerTask = getGetFollowersTask(authToken, targetUser, limit,
                lastFollower, observer);
        BackgroundTaskUtils.runTask(followerTask);
    }
    public GetFollowersTask getGetFollowersTask(AuthToken authToken, User targetUser, int limit,
                                                User lastFollowee, Observer observer) {
        return new GetFollowersTask(authToken, targetUser, limit, lastFollowee,
                new MessageHandler(observer, "getFollowers"));
    }

    public void getFollowees(AuthToken authToken, User targetUser, int limit, User lastFollowee,
                             Observer observer) {
        GetFollowingTask followingTask = getGetFollowingTask(authToken, targetUser, limit,
                lastFollowee, observer);
        BackgroundTaskUtils.runTask(followingTask);
    }
    public GetFollowingTask getGetFollowingTask(AuthToken authToken, User targetUser, int limit,
                                                User lastFollowee, Observer observer) {
        return new GetFollowingTask(authToken, targetUser, limit, lastFollowee,
                new MessageHandler(observer, "getFollowing"));
    }

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
    public GetFollowersCountTask getGetFollowersCountTask (AuthToken authToken, User user,
                                                           Observer observer) {
        return new GetFollowersCountTask(authToken, user, new MessageHandler(observer,
                "followerCount"));
    }

    public void getFollowingCount(AuthToken authToken, User user, Observer observer) {
        GetFollowingCountTask task = getGetFollowingCountTask(authToken, user, observer);
        BackgroundTaskUtils.runTask(task);
    }
    public GetFollowingCountTask getGetFollowingCountTask (AuthToken authToken, User user,
                                                           Observer observer) {
        return new GetFollowingCountTask(authToken, user, new MessageHandler(observer,
                "followingCount"));
    }

    public void isFollower(AuthToken authToken, User user, User selected, Observer observer) {
        IsFollowerTask task = getIsFollowerTask(authToken, user, selected, observer);
        BackgroundTaskUtils.runTask(task);
    }
    public IsFollowerTask getIsFollowerTask(AuthToken authToken, User user, User selected,
                                            Observer observer) {
        return new IsFollowerTask(authToken, user, selected, new MessageHandler(observer,
                "isFollower"));
    }

    public static class MessageHandler extends Handler {

        private final Observer observer;
        private final String task;

        public MessageHandler(Observer observer, String task) {
            super(Looper.getMainLooper());
            this.observer = observer;
            this.task = task;
        }

        @Override
        public void handleMessage(Message message) {
            Bundle bundle = message.getData();
            if (task.equals("getFollowers")) {
                boolean success = bundle.getBoolean(GetFollowersTask.SUCCESS_KEY);
                if (success) {
                    List<User> followers = (List<User>) bundle.getSerializable(GetFollowersTask.ITEMS_KEY);
                    boolean hasMorePages = bundle.getBoolean(GetFollowersTask.MORE_PAGES_KEY);
                    observer.handleFollowerSuccess(followers, hasMorePages);
                } else if (bundle.containsKey(GetFollowersTask.MESSAGE_KEY)) {
                    String msg = bundle.getString(GetFollowersTask.MESSAGE_KEY);
                    msg = "Failed to fetch followers: " + msg;
                    observer.handleFailure(msg);
                } else if (bundle.containsKey(GetFollowersTask.EXCEPTION_KEY)) {
                    Exception ex = (Exception) bundle.getSerializable(GetFollowersTask.EXCEPTION_KEY);
                    String msg = "Exception when fetching followers: " + ex.getMessage().toString();
                    observer.handleException(msg, ex);
                }
            }
            else if (task.equals("getFollowing")) {
                boolean success = bundle.getBoolean(GetFollowingTask.SUCCESS_KEY);
                if (success) {
                    List<User> followers = (List<User>) bundle.getSerializable(GetFollowingTask.ITEMS_KEY);
                    boolean hasMorePages = bundle.getBoolean(GetFollowingTask.MORE_PAGES_KEY);
                    observer.handleFolloweeSuccess(followers, hasMorePages);
                } else if (bundle.containsKey(GetFollowingTask.MESSAGE_KEY)) {
                    String msg = bundle.getString(GetFollowingTask.MESSAGE_KEY);
                    msg = "Failed to fetch followees: " + msg;
                    observer.handleFailure(msg);
                } else if (bundle.containsKey(GetFollowingTask.EXCEPTION_KEY)) {
                    Exception ex = (Exception) bundle.getSerializable(GetFollowingTask.EXCEPTION_KEY);
                    String msg = "Exception when fetching followees: " + ex.getMessage().toString();
                    observer.handleException(msg, ex);
                }
            }
            else if (task.equals("follow")) {
                boolean success = bundle.getBoolean(FollowTask.SUCCESS_KEY);
                if (success) {
                    observer.handleFollowSuccess();
                }
                else if (message.getData().containsKey(FollowTask.MESSAGE_KEY)) {
                    String eMsg = message.getData().getString(FollowTask.MESSAGE_KEY);
                    observer.handleFailure("Failed to follow: " + eMsg);
                }
                else if (message.getData().containsKey(FollowTask.EXCEPTION_KEY)) {
                    Exception ex = (Exception) message.getData().getSerializable(FollowTask.EXCEPTION_KEY);
                    String eMsg = "Failed to follow because of exception: " + ex.getMessage();
                    observer.handleException(eMsg, ex);
                }
            }
            else if (task.equals("unfollow")) {
                boolean success = bundle.getBoolean(UnfollowTask.SUCCESS_KEY);
                if (success) {
                    observer.handleUnfollowSuccess();
                }
                else if (message.getData().containsKey(UnfollowTask.MESSAGE_KEY)) {
                    String eMsg = message.getData().getString(UnfollowTask.MESSAGE_KEY);
                    observer.handleFailure("Failed to unfollow: " + eMsg);
                }
                else if (message.getData().containsKey(UnfollowTask.EXCEPTION_KEY)) {
                    Exception ex = (Exception) message.getData().getSerializable(UnfollowTask.EXCEPTION_KEY);
                    String eMsg = "Failed to unfollow because of exception: " + ex.getMessage();
                    observer.handleException(eMsg, ex);
                }
            }
            else if (task.equals("followerCount")) {
                boolean success = bundle.getBoolean(GetFollowersCountTask.SUCCESS_KEY);
                if (success) {
                    int count = message.getData().getInt(GetFollowersCountTask.COUNT_KEY);
                    observer.handleFollowerCountSuccess(count);
                }
                else if (message.getData().containsKey(GetFollowersCountTask.MESSAGE_KEY)) {
                    String eMsg = message.getData().getString(GetFollowersCountTask.MESSAGE_KEY);
                    observer.handleFailure("Failed to get follower count: " + eMsg);
                }
                else if (message.getData().containsKey(GetFollowersCountTask.EXCEPTION_KEY)) {
                    Exception ex = (Exception) message.getData().getSerializable( GetFollowersCountTask.EXCEPTION_KEY);
                    String eMsg = "Failed to follower count because of exception: " + ex.getMessage();
                    observer.handleException(eMsg, ex);
                }
            }
            else if (task.equals("followingCount")) {
                boolean success = bundle.getBoolean(GetFollowingCountTask.SUCCESS_KEY);
                if (success) {
                    int count = message.getData().getInt(GetFollowingCountTask.COUNT_KEY);
                    observer.handleFollowingCountSuccess(count);
                }
                else if (message.getData().containsKey(GetFollowingCountTask.MESSAGE_KEY)) {
                    String eMsg = message.getData().getString(GetFollowingCountTask.MESSAGE_KEY);
                    observer.handleFailure("Failed to get following count: " + eMsg);
                }
                else if (message.getData().containsKey(GetFollowingCountTask.EXCEPTION_KEY)) {
                    Exception ex = (Exception) message.getData().getSerializable( GetFollowingCountTask.EXCEPTION_KEY);
                    String eMsg = "Failed to following count because of exception: " + ex.getMessage();
                    observer.handleException(eMsg, ex);
                }
            }
            else if (task.equals("isFollower")) {
                boolean success = bundle.getBoolean(IsFollowerTask.SUCCESS_KEY);
                if (success) {
                    boolean isFollower = message.getData().getBoolean(IsFollowerTask.IS_FOLLOWER_KEY);
                    observer.handleIsFollowerSuccess(isFollower);
                }
                else if (message.getData().containsKey(IsFollowerTask.MESSAGE_KEY)) {
                    String eMsg = message.getData().getString(IsFollowerTask.MESSAGE_KEY);
                    observer.handleFailure("Failed to get follow status: " + eMsg);
                }
                else if (message.getData().containsKey(IsFollowerTask.EXCEPTION_KEY)) {
                    Exception ex = (Exception) message.getData().getSerializable(IsFollowerTask.EXCEPTION_KEY);
                    String eMsg = "Failed to follow status because of exception: " + ex.getMessage();
                    observer.handleException(eMsg, ex);
                }
            }
        }
    }
}