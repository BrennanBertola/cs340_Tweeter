package edu.byu.cs.tweeter.client.service;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;

import java.util.List;

import edu.byu.cs.tweeter.client.backgroundTask.BackgroundTaskUtils;
import edu.byu.cs.tweeter.client.backgroundTask.GetFeedTask;
import edu.byu.cs.tweeter.client.backgroundTask.GetStoryTask;
import edu.byu.cs.tweeter.client.backgroundTask.PostStatusTask;
import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.Status;
import edu.byu.cs.tweeter.model.domain.User;

public class StatusService {

    public interface Observer {
        void handleFeedSuccess(List<Status> statuses, boolean hasMorePages);
        void handlePostSuccess();
        void handleStorySuccess(List<Status> statuses, boolean hasMorePages);
        void handleFailure(String message);
        void handleException(String message, Exception exception);
    }

    public StatusService() {
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
                new MessageHandler(observer, "getFeed"));
    }

    public void post (AuthToken authToken, Status status, Observer observer) {
        PostStatusTask task = getPostStatusTask(authToken, status, observer);
        BackgroundTaskUtils.runTask(task);
    }
    public PostStatusTask getPostStatusTask(AuthToken authToken, Status status, Observer observer) {
        return new PostStatusTask(authToken, status, new MessageHandler(observer, "post"));
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
                new MessageHandler(observer, "getStory"));
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
            if (task.equals("getFeed")) {
                boolean success = bundle.getBoolean(GetFeedTask.SUCCESS_KEY);
                if (success) {
                    List<Status> statuses = (List<Status>) bundle.getSerializable(GetFeedTask.ITEMS_KEY);
                    boolean hasMorePages = bundle.getBoolean(GetFeedTask.MORE_PAGES_KEY);
                    observer.handleFeedSuccess(statuses, hasMorePages);
                } else if (bundle.containsKey(GetFeedTask.MESSAGE_KEY)) {
                    String msg = bundle.getString(GetFeedTask.MESSAGE_KEY);
                    msg = "Failed to fetch feed: " + msg;
                    observer.handleFailure(msg);
                } else if (bundle.containsKey(GetFeedTask.EXCEPTION_KEY)) {
                    Exception ex = (Exception) bundle.getSerializable(GetFeedTask.EXCEPTION_KEY);
                    String msg = "Exception fetching feed: " + ex.getMessage().toString();
                    observer.handleException(msg, ex);
                }
            }
            else if (task.equals("post")) {
                boolean success = bundle.getBoolean(PostStatusTask.SUCCESS_KEY);
                if (success) {
                    observer.handlePostSuccess();
                }
                else if (message.getData().containsKey(PostStatusTask.MESSAGE_KEY)) {
                    String eMsg = message.getData().getString(PostStatusTask.MESSAGE_KEY);
                    observer.handleFailure("Failed to get post status: " + eMsg);
                }
                else if (message.getData().containsKey(PostStatusTask.EXCEPTION_KEY)) {
                    Exception ex = (Exception) message.getData().getSerializable(PostStatusTask.EXCEPTION_KEY);
                    String eMsg = "Failed to post status because of exception: " + ex.getMessage();
                    observer.handleException(eMsg, ex);
                }
            }
            else if (task.equals("getStory")) {
                boolean success = bundle.getBoolean(GetStoryTask.SUCCESS_KEY);
                if (success) {
                    List<Status> statuses = (List<Status>) bundle.getSerializable(GetStoryTask.ITEMS_KEY);
                    boolean hasMorePages = bundle.getBoolean(GetStoryTask.MORE_PAGES_KEY);
                    observer.handleStorySuccess(statuses, hasMorePages);
                } else if (bundle.containsKey(GetStoryTask.MESSAGE_KEY)) {
                    String eMsg = bundle.getString(GetStoryTask.MESSAGE_KEY);
                    eMsg = "Failed to get story: " + eMsg;
                    observer.handleFailure(eMsg);
                } else if (bundle.containsKey(GetStoryTask.EXCEPTION_KEY)) {
                    Exception ex = (Exception) bundle.getSerializable(GetStoryTask.EXCEPTION_KEY);
                    String msg = "Failed to get story due to exception: " + ex.getMessage().toString();
                    observer.handleException(msg, ex);
                }
            }
        }
    }
}
