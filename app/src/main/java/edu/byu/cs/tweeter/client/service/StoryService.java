package edu.byu.cs.tweeter.client.service;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;

import java.util.List;

import edu.byu.cs.tweeter.client.backgroundTask.BackgroundTaskUtils;
import edu.byu.cs.tweeter.client.backgroundTask.GetStoryTask;
import edu.byu.cs.tweeter.client.backgroundTask.GetUserTask;
import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.Status;
import edu.byu.cs.tweeter.model.domain.User;

public class StoryService {

    public interface Observer {
        void handleStorySuccess(List<Status> statuses, boolean hasMorePages);
        void handleStoryFailure(String message);
        void handleStoryException(Exception exception);
        void handleUserSuccess(User user);
        void handleUserFailure(String message);
        void handleUserException(Exception exception);
    }

    public StoryService() {
    }

    public void getStory(AuthToken authToken, User targetUser, int limit, Status lastStatus,
                        Observer observer) {
        GetStoryTask storyTask = getGetStoryTask(authToken, targetUser, limit,
                lastStatus, observer);
        BackgroundTaskUtils.runTask(storyTask);
    }

    public GetStoryTask getGetStoryTask(AuthToken authToken, User targetUser, int limit,
                                      Status lastStatus, Observer observer) {
        return new GetStoryTask(authToken, targetUser, limit, lastStatus,
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
        private final boolean storyTask;
        private final boolean userTask;

        public MessageHandler(Observer observer, boolean story, boolean user) {
            super(Looper.getMainLooper());
            this.observer = observer;
            storyTask = story;
            userTask = user;
        }

        @Override
        public void handleMessage(Message message) {
            Bundle bundle = message.getData();
            if (storyTask) {
                boolean success = bundle.getBoolean(GetStoryTask.SUCCESS_KEY);
                if (success) {
                    List<Status> statuses = (List<Status>) bundle.getSerializable(GetStoryTask.STATUSES_KEY);
                    boolean hasMorePages = bundle.getBoolean(GetStoryTask.MORE_PAGES_KEY);
                    observer.handleStorySuccess(statuses, hasMorePages);
                } else if (bundle.containsKey(GetStoryTask.MESSAGE_KEY)) {
                    String errorMessage = bundle.getString(GetStoryTask.MESSAGE_KEY);
                    observer.handleStoryFailure(errorMessage);
                } else if (bundle.containsKey(GetStoryTask.EXCEPTION_KEY)) {
                    Exception ex = (Exception) bundle.getSerializable(GetStoryTask.EXCEPTION_KEY);
                    observer.handleStoryException(ex);
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

