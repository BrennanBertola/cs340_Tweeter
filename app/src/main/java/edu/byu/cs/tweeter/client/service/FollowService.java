package edu.byu.cs.tweeter.client.service;

import android.os.Bundle;

import edu.byu.cs.tweeter.client.backgroundTask.BackgroundTaskUtils;
import edu.byu.cs.tweeter.client.backgroundTask.FollowTask;
import edu.byu.cs.tweeter.client.backgroundTask.GetCountTask;
import edu.byu.cs.tweeter.client.backgroundTask.GetFollowersCountTask;
import edu.byu.cs.tweeter.client.backgroundTask.GetFollowersTask;
import edu.byu.cs.tweeter.client.backgroundTask.GetFollowingCountTask;
import edu.byu.cs.tweeter.client.backgroundTask.GetFollowingTask;
import edu.byu.cs.tweeter.client.backgroundTask.IsFollowerTask;
import edu.byu.cs.tweeter.client.backgroundTask.UnfollowTask;
import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.User;

public class FollowService {

    public interface FollowObserver<T> extends PagedObserver<T> {
        void handleFollowSuccess();
        void handleUnfollowSuccess();
        void handleFollowerCountSuccess(int count);
        void handleFollowingCountSuccess(int count);
        void handleIsFollowerSuccess(boolean isFollower);
    }

    public FollowService() {}

    public void getFollowers(AuthToken authToken, User targetUser, int limit, User lastFollower,
                             FollowObserver observer) {
        GetFollowersTask followerTask = getGetFollowersTask(authToken, targetUser, limit,
                lastFollower, observer);
        BackgroundTaskUtils.runTask(followerTask);
    }
    public GetFollowersTask getGetFollowersTask(AuthToken authToken, User targetUser, int limit,
                                                User lastFollowee, FollowObserver observer) {
        return new GetFollowersTask(authToken, targetUser, limit, lastFollowee,
                new GetFollowerHandler(observer));
    }

    public void getFollowees(AuthToken authToken, User targetUser, int limit, User lastFollowee,
                             FollowObserver observer) {
        GetFollowingTask followingTask = getGetFollowingTask(authToken, targetUser, limit,
                lastFollowee, observer);
        BackgroundTaskUtils.runTask(followingTask);
    }
    public GetFollowingTask getGetFollowingTask(AuthToken authToken, User targetUser, int limit,
                                                User lastFollowee, FollowObserver observer) {
        return new GetFollowingTask(authToken, targetUser, limit, lastFollowee,
                new GetFolloweeHandler(observer));
    }

    public void follow(AuthToken authToken, User followee, FollowObserver observer) {
        FollowTask task = getFollowTask(authToken, followee, observer);
        BackgroundTaskUtils.runTask(task);
    }
    public FollowTask getFollowTask(AuthToken authToken, User followee, FollowObserver observer) {
        return new FollowTask(authToken, followee, new FollowHandler(observer));
    }

    public void unfollow(AuthToken authToken, User followee, FollowObserver observer) {
        UnfollowTask task = getUnfollowTask(authToken, followee, observer);
        BackgroundTaskUtils.runTask(task);
    }
    public UnfollowTask getUnfollowTask (AuthToken authToken, User followee, FollowObserver observer) {
        return new UnfollowTask(authToken, followee, new UnfollowHandler(observer));
    }

    public void getFollowerCount(AuthToken authToken, User user, FollowObserver observer) {
        GetFollowersCountTask task = getGetFollowersCountTask(authToken, user, observer);
        BackgroundTaskUtils.runTask(task);
    }
    public GetFollowersCountTask getGetFollowersCountTask (AuthToken authToken, User user,
                                                           FollowObserver observer) {
        return new GetFollowersCountTask(authToken, user, new GetFollowerCountHandler(observer));
    }

    public void getFollowingCount(AuthToken authToken, User user, FollowObserver observer) {
        GetFollowingCountTask task = getGetFollowingCountTask(authToken, user, observer);
        BackgroundTaskUtils.runTask(task);
    }
    public GetFollowingCountTask getGetFollowingCountTask (AuthToken authToken, User user,
                                                           FollowObserver observer) {
        return new GetFollowingCountTask(authToken, user, new GetFollowingCountHandler(observer));
    }

    public void isFollower(AuthToken authToken, User user, User selected, FollowObserver observer) {
        IsFollowerTask task = getIsFollowerTask(authToken, user, selected, observer);
        BackgroundTaskUtils.runTask(task);
    }
    public IsFollowerTask getIsFollowerTask(AuthToken authToken, User user, User selected,
                                            FollowObserver observer) {
        return new IsFollowerTask(authToken, user, selected, new IsFollowerHandler(observer));
    }

    public static class FollowHandler extends MessageHandler {
        private final FollowObserver observer;

        public FollowHandler(FollowObserver observer) {
            super();
            this.observer = observer;
        }

        @Override
        protected void success (Bundle bundle) {
            observer.handleFollowSuccess();
        }

        @Override
        protected void fail (String message) {
            observer.handleFailure("Follow request failed: " + message);
        }

        @Override
        protected void exception (String message, Exception ex) {
            observer.handleException("Exception during follow request" + message, ex);
        }
    }

    public static class UnfollowHandler extends MessageHandler {
        private final FollowObserver observer;

        public UnfollowHandler(FollowObserver observer) {
            this.observer = observer;
        }

        @Override
        protected void success (Bundle bundle) {
            observer.handleUnfollowSuccess();
        }

        @Override
        protected void fail (String message) {
            observer.handleFailure("Unfollow request failed: " + message);
        }

        @Override
        protected void exception (String message, Exception ex) {
            observer.handleException("Exception during unfollow request" + message, ex);
        }
    }

    public static class GetFollowerHandler extends MessageHandler {
        private final FollowObserver observer;

        public GetFollowerHandler(FollowObserver observer) {
            super();
            this.observer = observer;
        }

        @Override
        protected void success (Bundle bundle) {
                PagedTaskHandler<User> handler = new PagedTaskHandler<>(bundle);
                observer.handlePagedSuccess(handler.handle());
        }

        @Override
        protected void fail (String message) {
            observer.handleFailure("Get follower request failed: " + message);
        }

        @Override
        protected void exception (String message, Exception ex) {
            observer.handleException("Exception during get follower request" + message, ex);
        }
    }

    public static class GetFolloweeHandler extends MessageHandler {
        private final FollowObserver observer;

        public GetFolloweeHandler(FollowObserver observer) {
            super();
            this.observer = observer;
        }

        @Override
        protected void success (Bundle bundle) {
            PagedTaskHandler<User> handler = new PagedTaskHandler<>(bundle);
            observer.handlePagedSuccess(handler.handle());
        }

        @Override
        protected void fail (String message) {
            observer.handleFailure("Get followee request failed: " + message);
        }

        @Override
        protected void exception (String message, Exception ex) {
            observer.handleException("Exception during get followee request" + message, ex);
        }
    }

    public static class GetFollowerCountHandler extends MessageHandler {
        private final FollowObserver observer;

        public GetFollowerCountHandler(FollowObserver observer) {
            super();
            this.observer = observer;
        }

        @Override
        protected void success (Bundle bundle) {
            int count = bundle.getInt(GetCountTask.COUNT_KEY);
            observer.handleFollowerCountSuccess(count);
        }

        @Override
        protected void fail (String message) {
            observer.handleFailure("Get follower count request failed: " + message);
        }

        @Override
        protected void exception (String message, Exception ex) {
            observer.handleException("Exception during get follower count request" + message, ex);
        }
    }

    public static class GetFollowingCountHandler extends MessageHandler {
        private final FollowObserver observer;

        public GetFollowingCountHandler(FollowObserver observer) {
            super();
            this.observer = observer;
        }

        @Override
        protected void success (Bundle bundle) {
            int count = bundle.getInt(GetCountTask.COUNT_KEY);
            observer.handleFollowingCountSuccess(count);
        }

        @Override
        protected void fail (String message) {
            observer.handleFailure("Get followee count request failed: " + message);
        }

        @Override
        protected void exception (String message, Exception ex) {
            observer.handleException("Exception during get followee count request" + message, ex);
        }
    }

    public static class IsFollowerHandler extends MessageHandler {
        private final FollowObserver observer;

        public IsFollowerHandler(FollowObserver observer) {
            super();
            this.observer = observer;
        }

        @Override
        protected void success (Bundle bundle) {
            boolean isFollower = bundle.getBoolean(IsFollowerTask.IS_FOLLOWER_KEY);
            observer.handleIsFollowerSuccess(isFollower);
        }

        @Override
        protected void fail (String message) {
            observer.handleFailure("Is follower request failed: " + message);
        }

        @Override
        protected void exception (String message, Exception ex) {
            observer.handleException("Exception during is follower request" + message, ex);
        }
    }
}