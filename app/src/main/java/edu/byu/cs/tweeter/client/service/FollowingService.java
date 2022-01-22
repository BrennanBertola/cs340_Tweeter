package edu.byu.cs.tweeter.client.service;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;

import java.io.Serializable;
import java.util.List;

import edu.byu.cs.tweeter.client.backgroundTask.BackgroundTask;
import edu.byu.cs.tweeter.client.backgroundTask.BackgroundTaskUtils;
import edu.byu.cs.tweeter.client.backgroundTask.GetUserTask;
import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.User;
import edu.byu.cs.tweeter.util.FakeData;
import edu.byu.cs.tweeter.util.Pair;

public class FollowingService {

    public interface Observer {
        void handleFolloweeSuccess(List<User> followees, boolean hasMorePages);
        void handleFolloweeFailure(String message);
        void handleFolloweeException(Exception exception);
        void handleUserSuccess(User user);
        void handleUserFailure(String message);
        void handleUserException(Exception exception);
    }

    public FollowingService() {}

    public void getFollowees(AuthToken authToken, User targetUser, int limit, User lastFollowee,
                             Observer observer) {
        GetFollowingTask followingTask = getGetFollowingTask(authToken, targetUser, limit,
                lastFollowee, observer);
        BackgroundTaskUtils.runTask(followingTask);
    }

    public GetFollowingTask getGetFollowingTask(AuthToken authToken, User targetUser, int limit,
                                                User lastFollowee, Observer observer) {
        return new GetFollowingTask(authToken, targetUser, limit, lastFollowee,
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
        private final boolean followeeTask;
        private final boolean userTask;

        public MessageHandler(Observer observer, boolean followee, boolean user) {
            super(Looper.getMainLooper());
            this.observer = observer;
            followeeTask = followee;
            userTask = user;
        }

        @Override
        public void handleMessage(Message message) {
            Bundle bundle = message.getData();
            if (followeeTask) {
                boolean success = bundle.getBoolean(GetFollowingTask.SUCCESS_KEY);
                if (success) {
                    List<User> followees = (List<User>) bundle.getSerializable(GetFollowingTask.FOLLOWEES_KEY);
                    boolean hasMorePages = bundle.getBoolean(GetFollowingTask.MORE_PAGES_KEY);
                    observer.handleFolloweeSuccess(followees, hasMorePages);
                } else if (bundle.containsKey(GetFollowingTask.MESSAGE_KEY)) {
                    String errorMessage = bundle.getString(GetFollowingTask.MESSAGE_KEY);
                    observer.handleFolloweeFailure(errorMessage);
                } else if (bundle.containsKey(GetFollowingTask.EXCEPTION_KEY)) {
                    Exception ex = (Exception) bundle.getSerializable(GetFollowingTask.EXCEPTION_KEY);
                    observer.handleFolloweeException(ex);
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

    public class GetFollowingTask extends BackgroundTask {

        private static final String LOG_TAG = "GetFollowingTask";

        public static final String FOLLOWEES_KEY = "followees";
        public static final String MORE_PAGES_KEY = "more-pages";

        /**
         * Auth token for logged-in user.
         */
        protected AuthToken authToken;
        /**
         * The user whose following is being retrieved.
         * (This can be any user, not just the currently logged-in user.)
         */
        protected User targetUser;
        /**
         * Maximum number of followed users to return (i.e., page size).
         */
        protected int limit;
        /**
         * The last person being followed returned in the previous page of results (can be null).
         * This allows the new page to begin where the previous page ended.
         */
        protected User lastFollowee;

        /**
         * The followee users returned by the server.
         */
        private List<User> followees;
        /**
         * If there are more pages, returned by the server.
         */
        private boolean hasMorePages;

        public GetFollowingTask(AuthToken authToken, User targetUser, int limit, User lastFollowee,
                                Handler messageHandler) {
            super(messageHandler);

            this.authToken = authToken;
            this.targetUser = targetUser;
            this.limit = limit;
            this.lastFollowee = lastFollowee;
        }

        protected void loadSuccessBundle(Bundle msgBundle) {
            msgBundle.putSerializable(FOLLOWEES_KEY, (Serializable) this.followees);
            msgBundle.putBoolean(MORE_PAGES_KEY, this.hasMorePages);
        }

        @Override
        protected void runTask() {
            try {
                Pair<List<User>, Boolean> pageOfUsers = getFollowees();
                this.followees = pageOfUsers.getFirst();
                this.hasMorePages = pageOfUsers.getSecond();

                sendSuccessMessage();
            }
            catch (Exception ex) {
                Log.e(LOG_TAG, "Failed to get followees", ex);
                sendExceptionMessage(ex);
            }
        }

        // This method is public so it can be accessed by test cases
        public FakeData getFakeData() {
            return new FakeData();
        }

        // This method is public so it can be accessed by test cases
        public Pair<List<User>, Boolean> getFollowees() {
            return getFakeData().getPageOfUsers(lastFollowee, limit, targetUser);
        }
    }
}
