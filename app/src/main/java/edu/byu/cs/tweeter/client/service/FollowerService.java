package edu.byu.cs.tweeter.client.service;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;

import java.util.List;

import edu.byu.cs.tweeter.client.backgroundTask.BackgroundTaskUtils;
import edu.byu.cs.tweeter.client.backgroundTask.GetFollowersTask;
import edu.byu.cs.tweeter.client.backgroundTask.GetUserTask;
import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.User;

public class FollowerService {

    public interface Observer {
        void handleFollowerSuccess(List<User> followers, boolean hasMorePages);
        void handleFollowerFailure(String message);
        void handleFollowerException(Exception exception);
        void handleUserSuccess(User user);
        void handleUserFailure(String message);
        void handleUserException(Exception exception);
    }

    public FollowerService() {}

    public void getFollowers(AuthToken authToken, User targetUser, int limit, User lastFollower,
                             Observer observer) {
        GetFollowersTask followerTask = getGetFollowersTask(authToken, targetUser, limit,
                lastFollower, observer);
        BackgroundTaskUtils.runTask(followerTask);
    }

    public GetFollowersTask getGetFollowersTask(AuthToken authToken, User targetUser, int limit,
                                                User lastFollowee, Observer observer) {
        return new GetFollowersTask(authToken, targetUser, limit, lastFollowee,
                new MessageHandler(observer, true, false));
    }

    public void getSelectedUser (AuthToken authToken, String alias, Observer observer) {
        GetUserTask userTask = getGetUserTask(authToken, alias, observer);
        BackgroundTaskUtils.runTask(userTask);
    }

    public GetUserTask getGetUserTask(AuthToken authToken, String alias, Observer observer) {
        return  new GetUserTask(authToken, alias, new MessageHandler(observer, false, true));
    }

    public static class MessageHandler extends Handler {

        private final Observer observer;
        private final boolean followerTask;
        private final boolean userTask;

        public MessageHandler(Observer observer, boolean follower, boolean user) {
            super(Looper.getMainLooper());
            this.observer = observer;
            followerTask = follower;
            userTask = user;
        }

        @Override
        public void handleMessage(Message message) {
            Bundle bundle = message.getData();
            if(followerTask) {
                boolean success = bundle.getBoolean(GetFollowersTask.SUCCESS_KEY);
                if (success) {
                    List<User> followers = (List<User>) bundle.getSerializable(GetFollowersTask.FOLLOWERS_KEY);
                    boolean hasMorePages = bundle.getBoolean(GetFollowersTask.MORE_PAGES_KEY);
                    observer.handleFollowerSuccess(followers, hasMorePages);
                } else if (bundle.containsKey(GetFollowersTask.MESSAGE_KEY)) {
                    String errorMessage = bundle.getString(GetFollowersTask.MESSAGE_KEY);
                    observer.handleFollowerFailure(errorMessage);
                } else if (bundle.containsKey(GetFollowersTask.EXCEPTION_KEY)) {
                    Exception ex = (Exception) bundle.getSerializable(GetFollowersTask.EXCEPTION_KEY);
                    observer.handleFollowerException(ex);
                }
            }
            else if (userTask) {
                boolean success = bundle.getBoolean(GetUserTask.SUCCESS_KEY);
                if (success) {
                    User user = (User) bundle.getSerializable(GetUserTask.USER_KEY);
                    observer.handleUserSuccess(user);
                } else if (bundle.containsKey(GetUserTask.MESSAGE_KEY)) {
                    String eMsg = bundle.getString(GetUserTask.MESSAGE_KEY);
                    observer.handleUserFailure(eMsg);
                } else if (bundle.containsKey(GetUserTask.EXCEPTION_KEY)) {
                    Exception ex = (Exception) bundle.getSerializable(GetUserTask.EXCEPTION_KEY);
                    observer.handleUserException(ex);
                }
            }
        }
    }
}
