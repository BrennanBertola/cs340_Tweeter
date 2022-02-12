package edu.byu.cs.tweeter.client.service;

import android.os.Bundle;

import edu.byu.cs.tweeter.client.backgroundTask.BackgroundTaskUtils;
import edu.byu.cs.tweeter.client.backgroundTask.GetFeedTask;
import edu.byu.cs.tweeter.client.backgroundTask.GetStoryTask;
import edu.byu.cs.tweeter.client.backgroundTask.PostStatusTask;
import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.Status;
import edu.byu.cs.tweeter.model.domain.User;

public class StatusService {

    public interface StatusObserver<T> extends PagedObserver<T> {
        void handlePostSuccess();
    }

    public StatusService() {
    }

    public void getFeed(AuthToken authToken, User targetUser, int limit, Status lastStatus,
                        StatusObserver observer) {
        GetFeedTask feedTask = getGetFeedTask(authToken, targetUser, limit,
                lastStatus, observer);
        BackgroundTaskUtils.runTask(feedTask);
    }

    public GetFeedTask getGetFeedTask(AuthToken authToken, User targetUser, int limit,
                                      Status lastStatus, StatusObserver observer) {
        return new GetFeedTask(authToken, targetUser, limit, lastStatus,
                new GetFeedHandler(observer));
    }

    public void post (AuthToken authToken, Status status, StatusObserver observer) {
        PostStatusTask task = getPostStatusTask(authToken, status, observer);
        BackgroundTaskUtils.runTask(task);
    }
    public PostStatusTask getPostStatusTask(AuthToken authToken, Status status, StatusObserver observer) {
        return new PostStatusTask(authToken, status, new PostHandler(observer));
    }

    public void getStory(AuthToken authToken, User targetUser, int limit, Status lastStatus,
                         StatusObserver observer) {
        GetStoryTask storyTask = getGetStoryTask(authToken, targetUser, limit,
                lastStatus, observer);
        BackgroundTaskUtils.runTask(storyTask);
    }
    public GetStoryTask getGetStoryTask(AuthToken authToken, User targetUser, int limit,
                                        Status lastStatus, StatusObserver observer) {
        return new GetStoryTask(authToken, targetUser, limit, lastStatus,
                new GetStoryHandler(observer));
    }

    public static class GetFeedHandler extends MessageHandler {
        private final StatusObserver observer;

        public GetFeedHandler(StatusObserver observer) {
            super();
            this.observer = observer;
        }

        @Override
        protected void success(Bundle bundle) {
            PagedTaskHandler<Status> handler = new PagedTaskHandler<>(bundle);
            observer.handlePagedSuccess(handler.handle());
        }

        @Override
        protected void fail (String message) {
            observer.handleFailure("Get feed Request failed: " + message);
        }

        @Override
        protected void exception (String message, Exception ex) {
            observer.handleException("Exception during get feed request: " + message, ex);
        }
    }

    public static class GetStoryHandler extends MessageHandler {
        private final StatusObserver observer;

        public GetStoryHandler(StatusObserver observer) {
            super();
            this.observer = observer;
        }

        @Override
        protected void success(Bundle bundle) {
            PagedTaskHandler<Status> handler = new PagedTaskHandler<>(bundle);
            observer.handlePagedSuccess(handler.handle());
        }

        @Override
        protected void fail (String message) {
            observer.handleFailure("Get story Request failed: " + message);
        }

        @Override
        protected void exception (String message, Exception ex) {
            observer.handleException("Exception during get story request: " + message, ex);
        }
    }

    public static class PostHandler extends MessageHandler {
        private final StatusObserver observer;

        public PostHandler(StatusObserver observer) {
            super();
            this.observer = observer;
        }

        @Override
        public void success(Bundle bundle) {
            observer.handlePostSuccess();
        }

        @Override
        public void fail (String message) {
            observer.handleFailure("Post request failed: " + message);
        }

        @Override
        public void exception (String message, Exception ex) {
            observer.handleException("Exception during post request: " + message, ex);
        }
    }
}
