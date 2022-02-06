package edu.byu.cs.tweeter.client.service;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Pair;

import java.util.List;

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
                new FollowHandler(observer, "getFollowers"));
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
                new FollowHandler(observer, "getFollowing"));
    }

    public void follow(AuthToken authToken, User followee, FollowObserver observer) {
        FollowTask task = getFollowTask(authToken, followee, observer);
        BackgroundTaskUtils.runTask(task);
    }
    public FollowTask getFollowTask(AuthToken authToken, User followee, FollowObserver observer) {
        return new FollowTask(authToken, followee, new FollowHandler(observer, "follow"));
    }

    public void unfollow(AuthToken authToken, User followee, FollowObserver observer) {
        UnfollowTask task = getUnfollowTask(authToken, followee, observer);
        BackgroundTaskUtils.runTask(task);
    }
    public UnfollowTask getUnfollowTask (AuthToken authToken, User followee, FollowObserver observer) {
        return new UnfollowTask(authToken, followee, new FollowHandler(observer, "unfollow"));
    }

    public void getFollowerCount(AuthToken authToken, User user, FollowObserver observer) {
        GetFollowersCountTask task = getGetFollowersCountTask(authToken, user, observer);
        BackgroundTaskUtils.runTask(task);
    }
    public GetFollowersCountTask getGetFollowersCountTask (AuthToken authToken, User user,
                                                           FollowObserver observer) {
        return new GetFollowersCountTask(authToken, user, new FollowHandler(observer,
                "followerCount"));
    }

    public void getFollowingCount(AuthToken authToken, User user, FollowObserver observer) {
        GetFollowingCountTask task = getGetFollowingCountTask(authToken, user, observer);
        BackgroundTaskUtils.runTask(task);
    }
    public GetFollowingCountTask getGetFollowingCountTask (AuthToken authToken, User user,
                                                           FollowObserver observer) {
        return new GetFollowingCountTask(authToken, user, new FollowHandler(observer,
                "followingCount"));
    }

    public void isFollower(AuthToken authToken, User user, User selected, FollowObserver observer) {
        IsFollowerTask task = getIsFollowerTask(authToken, user, selected, observer);
        BackgroundTaskUtils.runTask(task);
    }
    public IsFollowerTask getIsFollowerTask(AuthToken authToken, User user, User selected,
                                            FollowObserver observer) {
        return new IsFollowerTask(authToken, user, selected, new FollowHandler(observer,
                "isFollower"));
    }

    public static class FollowHandler extends MessageHandler {
        private final FollowObserver observer;

        public FollowHandler(FollowObserver observer, String task) {
            super(task);
            this.observer = observer;
        }

        @Override
        protected void success (Bundle bundle) {
            if (task.equals("getFollowers") || task.equals("getFollowing")) {
                PagedTaskHandler<User> handler = new PagedTaskHandler<>(bundle);
                observer.handlePagedSuccess(handler.handle());
            }
            else if (task.equals("follow")) {
                observer.handleFollowSuccess();
            }
            else if (task.equals("unfollow")) {
                observer.handleUnfollowSuccess();
            }
            else if (task.equals("followerCount") || task.equals("followingCount")) {
                int count = bundle.getInt(GetCountTask.COUNT_KEY);
                if (task.equals("followingCount")) {
                    observer.handleFollowingCountSuccess(count);
                }
                else {
                    observer.handleFollowerCountSuccess(count);
                }
            }
            else if (task.equals("isFollower")) {
                boolean isFollower = bundle.getBoolean(IsFollowerTask.IS_FOLLOWER_KEY);
                observer.handleIsFollowerSuccess(isFollower);
            }
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
}