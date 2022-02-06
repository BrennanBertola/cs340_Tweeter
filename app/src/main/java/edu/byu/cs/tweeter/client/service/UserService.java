package edu.byu.cs.tweeter.client.service;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;

import edu.byu.cs.tweeter.client.backgroundTask.AuthenticateTask;
import edu.byu.cs.tweeter.client.backgroundTask.BackgroundTaskUtils;
import edu.byu.cs.tweeter.client.backgroundTask.GetUserTask;
import edu.byu.cs.tweeter.client.backgroundTask.LoginTask;
import edu.byu.cs.tweeter.client.backgroundTask.LogoutTask;
import edu.byu.cs.tweeter.client.backgroundTask.RegisterTask;
import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.User;

public class UserService {

    public interface UserObserver extends Observer {
        void handleUserSuccess(User user);
        void handleLoginSuccess(User user, AuthToken authToken);
        void handleLogoutSuccess();
        void handleRegisterSuccess(User user, AuthToken authToken);
    }

    public UserService() {
    }

    public void getSelectedUser(AuthToken authToken, String alias, UserObserver observer) {
        GetUserTask userTask = getGetUserTask(authToken, alias, observer);
        BackgroundTaskUtils.runTask(userTask);
    }
    public GetUserTask getGetUserTask(AuthToken authToken, String alias, UserObserver observer) {
        return new GetUserTask(authToken, alias, new UserHandler(observer, "getUser"));
    }

    public void login(String username, String password, UserObserver observer) {
        LoginTask loginTask = getLoginTask(username, password, observer);
        BackgroundTaskUtils.runTask(loginTask);
    }
    public LoginTask getLoginTask(String username, String password, UserObserver observer) {
        return new LoginTask(username, password, new UserHandler(observer, "login"));
    }

    public void logout(AuthToken authToken, UserObserver observer) {
        LogoutTask task = getLogoutTask(authToken, observer);
        BackgroundTaskUtils.runTask(task);
    }
    public LogoutTask getLogoutTask(AuthToken authToken, UserObserver observer) {
        return new LogoutTask(authToken, new UserHandler(observer, "logout"));
    }

    public void register(String first, String last, String username, String password,
                         String image, UserObserver observer) {
        RegisterTask registerTask = getRegisterTask(first, last, username, password, image, observer);
        BackgroundTaskUtils.runTask(registerTask);
    }
    public RegisterTask getRegisterTask(String first, String last, String username, String password,
                                        String image, UserObserver observer) {
        return new RegisterTask(first, last, username, password, image, new UserHandler(observer, "register"));
    }

    public static class UserHandler extends MessageHandler {
        private final UserObserver observer;

        public UserHandler(UserObserver observer, String task) {
            super(task);
            this.observer = observer;
        }

        @Override
        protected void success(Bundle bundle) {
            if (task.equals("logout")) {
                observer.handleLogoutSuccess();
            }
            else if (task.equals("getUser") || task.equals("login") || task.equals("register")) {
                User user = (User) bundle.getSerializable(AuthenticateTask.USER_KEY);
                if (task.equals("getUser")) {
                    observer.handleUserSuccess(user);
                }
                else {
                    AuthToken authToken = (AuthToken) bundle.getSerializable(AuthenticateTask.AUTH_TOKEN_KEY);
                    if (task.equals("login")) {
                        observer.handleLoginSuccess(user, authToken);
                    }
                    else {
                        observer.handleRegisterSuccess(user, authToken);
                    }
                }
            }
        }

        @Override
        protected void fail (String message) {
            observer.handleFailure("User Request failed: " + message);
        }

        @Override
        protected void exception (String message, Exception ex) {
            observer.handleException("Exception during user request: " + message, ex);
        }
    }
}
