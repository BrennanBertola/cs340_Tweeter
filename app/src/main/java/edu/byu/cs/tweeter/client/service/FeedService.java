package edu.byu.cs.tweeter.client.service;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;

import java.util.List;

import edu.byu.cs.tweeter.client.backgroundTask.BackgroundTaskUtils;
import edu.byu.cs.tweeter.client.backgroundTask.GetFeedTask;
import edu.byu.cs.tweeter.client.backgroundTask.GetUserTask;
import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.Status;
import edu.byu.cs.tweeter.model.domain.User;

public class FeedService {

    public interface Observer {
        void handleFeedSuccess(List<Status> statuses, boolean hasMorePages);
        void handleFeedFailure(String message);
        void handleFeedException(Exception exception);
        void handleUserSuccess(User user);
        void handleUserFailure(String message);
        void handleUserException(Exception exception);
    }

    public FeedService() {
    }

    public void getFeed(AuthToken authToken, User targetUser, int limit, Status lastStatus,
                        Observer observer) {
        GetFeedTask feedTask = getGetFeedTask(authToken, targetUser, limit,
                lastStatus, observer);
        BackgroundTaskUtils.runTask(feedTask);
    }

    public GetFeedTask getGetFeedTask(AuthToken authToken, User targetUser, int limit,
                                      Status lastStatus, Observer observer) {
        return new GetFeedTask(authToken, targetUser, limit, lastStatus,
                new MessageHandler(observer, true, false));
    }

    public void getSelectedUser(AuthToken authToken, String alias, Observer observer) {
        GetUserTask userTask = getGetUserTask(authToken, alias, observer);
        BackgroundTaskUtils.runTask(userTask);
    }

    public GetUserTask getGetUserTask(AuthToken authToken, String alias, Observer observer) {
        return new GetUserTask(authToken, alias, new MessageHandler(observer, false, true));
    }

    public static class MessageHandler extends Handler {

        private final Observer observer;
        private final boolean feedTask;
        private final boolean userTask;

        public MessageHandler(Observer observer, boolean feed, boolean user) {
            super(Looper.getMainLooper());
            this.observer = observer;
            feedTask = feed;
            userTask = user;
        }

        @Override
        public void handleMessage(Message message) {
            Bundle bundle = message.getData();
            if (feedTask) {
                boolean success = bundle.getBoolean(GetFeedTask.SUCCESS_KEY);
                if (success) {
                    List<Status> statuses = (List<Status>) bundle.getSerializable(GetFeedTask.STATUSES_KEY);
                    boolean hasMorePages = bundle.getBoolean(GetFeedTask.MORE_PAGES_KEY);
                    observer.handleFeedSuccess(statuses, hasMorePages);
                } else if (bundle.containsKey(GetFeedTask.MESSAGE_KEY)) {
                    String errorMessage = bundle.getString(GetFeedTask.MESSAGE_KEY);
                    observer.handleFeedFailure(errorMessage);
                } else if (bundle.containsKey(FollowingService.GetFollowingTask.EXCEPTION_KEY)) {
                    Exception ex = (Exception) bundle.getSerializable(GetFeedTask.EXCEPTION_KEY);
                    observer.handleFeedException(ex);
                }
            } else if (userTask) {
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
