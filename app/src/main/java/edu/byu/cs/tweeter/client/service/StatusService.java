package edu.byu.cs.tweeter.client.service;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Pair;

import java.util.List;

import edu.byu.cs.tweeter.client.backgroundTask.BackgroundTask;
import edu.byu.cs.tweeter.client.backgroundTask.BackgroundTaskUtils;
import edu.byu.cs.tweeter.client.backgroundTask.GetFeedTask;
import edu.byu.cs.tweeter.client.backgroundTask.GetStoryTask;
import edu.byu.cs.tweeter.client.backgroundTask.PostStatusTask;
import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.Status;
import edu.byu.cs.tweeter.model.domain.User;

public class StatusService {

    public interface Observer {
        void handleFeedSuccess(Pair<List<Status>, Boolean> pair);
        void handlePostSuccess();
        void handleStorySuccess(Pair<List<Status>, Boolean> pair);
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
                new StatusHandler(observer, "getFeed"));
    }

    public void post (AuthToken authToken, Status status, Observer observer) {
        PostStatusTask task = getPostStatusTask(authToken, status, observer);
        BackgroundTaskUtils.runTask(task);
    }
    public PostStatusTask getPostStatusTask(AuthToken authToken, Status status, Observer observer) {
        return new PostStatusTask(authToken, status, new StatusHandler(observer, "post"));
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
                new StatusHandler(observer, "getStory"));
    }

    public static class StatusHandler extends MessageHandler {

        private final Observer observer;
        private final String task;

        public StatusHandler(Observer observer, String task) {
            super();
            this.observer = observer;
            this.task = task;
        }

        @Override
        protected void success(Bundle bundle) {
            if (task.equals("post")) {
                observer.handlePostSuccess();
            }
            else if (task.equals("getFeed") || task.equals("getStory")) {
                PagedTaskHandler<Status> handler = new PagedTaskHandler<>(bundle);
                if(task.equals("getFeed")) {
                    observer.handleFeedSuccess(handler.handle());
                }
                else {
                    observer.handleStorySuccess(handler.handle());
                }
            }
        }

        @Override
        protected void fail (String message) {
            observer.handleFailure("Status Request failed: " + message);
        }

        @Override
        protected void exception (String message, Exception ex) {
            observer.handleException("Exception during status request: " + message, ex);
        }
    }
}
