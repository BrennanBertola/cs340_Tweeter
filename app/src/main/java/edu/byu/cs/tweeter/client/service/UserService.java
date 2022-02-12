package edu.byu.cs.tweeter.client.service;

import android.os.Bundle;

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
        return new GetUserTask(authToken, alias, new GetUserHandler(observer));
    }

    public void login(String username, String password, UserObserver observer) {
        LoginTask loginTask = getLoginTask(username, password, observer);
        BackgroundTaskUtils.runTask(loginTask);
    }
    public LoginTask getLoginTask(String username, String password, UserObserver observer) {
        return new LoginTask(username, password, new LoginHandler(observer));
    }

    public void logout(AuthToken authToken, UserObserver observer) {
        LogoutTask task = getLogoutTask(authToken, observer);
        BackgroundTaskUtils.runTask(task);
    }
    public LogoutTask getLogoutTask(AuthToken authToken, UserObserver observer) {
        return new LogoutTask(authToken, new LogoutHandler(observer));
    }

    public void register(String first, String last, String username, String password,
                         String image, UserObserver observer) {
        RegisterTask registerTask = getRegisterTask(first, last, username, password, image, observer);
        BackgroundTaskUtils.runTask(registerTask);
    }
    public RegisterTask getRegisterTask(String first, String last, String username, String password,
                                        String image, UserObserver observer) {
        return new RegisterTask(first, last, username, password, image, new RegisterHandler(observer));
    }

    public static class LogoutHandler extends MessageHandler {
        private final UserObserver observer;

        public LogoutHandler(UserObserver observer) {
            super();
            this.observer = observer;
        }

        @Override
        protected void success(Bundle bundle) {
            observer.handleLogoutSuccess();
        }

        @Override
        protected void fail (String message) {
            observer.handleFailure("Logout Request failed: " + message);
        }

        @Override
        protected void exception (String message, Exception ex) {
            observer.handleException("Exception during logout request: " + message, ex);
        }
    }

    public static class LoginHandler extends MessageHandler {
        private final UserObserver observer;

        public LoginHandler(UserObserver observer) {
            super();
            this.observer = observer;
        }

        @Override
        protected void success(Bundle bundle) {
            User user = (User) bundle.getSerializable(AuthenticateTask.USER_KEY);
            AuthToken authToken = (AuthToken) bundle.getSerializable(AuthenticateTask.AUTH_TOKEN_KEY);
            observer.handleLoginSuccess(user, authToken);
        }

        @Override
        protected void fail (String message) {
            observer.handleFailure("Login Request failed: " + message);
        }

        @Override
        protected void exception (String message, Exception ex) {
            observer.handleException("Exception during Login request: " + message, ex);
        }
    }

    public static class RegisterHandler extends MessageHandler {
        private final UserObserver observer;

        public RegisterHandler(UserObserver observer) {
            super();
            this.observer = observer;
        }

        @Override
        protected void success(Bundle bundle) {
            User user = (User) bundle.getSerializable(AuthenticateTask.USER_KEY);
            AuthToken authToken = (AuthToken) bundle.getSerializable(AuthenticateTask.AUTH_TOKEN_KEY);
            observer.handleRegisterSuccess(user, authToken);
        }

        @Override
        protected void fail (String message) {
            observer.handleFailure("Register Request failed: " + message);
        }

        @Override
        protected void exception (String message, Exception ex) {
            observer.handleException("Exception during register request: " + message, ex);
        }
    }

    public static class GetUserHandler extends MessageHandler {
        private final UserObserver observer;

        public GetUserHandler(UserObserver observer) {
            super();
            this.observer = observer;
        }

        @Override
        protected void success(Bundle bundle) {
            User user = (User) bundle.getSerializable(AuthenticateTask.USER_KEY);
            observer.handleUserSuccess(user);
        }

        @Override
        protected void fail (String message) {
            observer.handleFailure("Get user Request failed: " + message);
        }

        @Override
        protected void exception (String message, Exception ex) {
            observer.handleException("Exception during get user request: " + message, ex);
        }
    }
}
