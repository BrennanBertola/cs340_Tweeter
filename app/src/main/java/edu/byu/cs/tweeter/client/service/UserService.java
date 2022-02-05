package edu.byu.cs.tweeter.client.service;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;

import edu.byu.cs.tweeter.client.backgroundTask.BackgroundTaskUtils;
import edu.byu.cs.tweeter.client.backgroundTask.GetUserTask;
import edu.byu.cs.tweeter.client.backgroundTask.LoginTask;
import edu.byu.cs.tweeter.client.backgroundTask.LogoutTask;
import edu.byu.cs.tweeter.client.backgroundTask.RegisterTask;
import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.User;

public class UserService {

    public interface Observer {
        void handleUserSuccess(User user);
        void handleLoginSuccess(User user, AuthToken authToken);
        void handleLogoutSuccess();
        void handleRegisterSuccess(User user, AuthToken authToken);
        void handleFailure(String message);
        void handleException(String message, Exception exception);
    }

    public UserService() {
    }

    public void getSelectedUser(AuthToken authToken, String alias, Observer observer) {
        GetUserTask userTask = getGetUserTask(authToken, alias, observer);
        BackgroundTaskUtils.runTask(userTask);
    }
    public GetUserTask getGetUserTask(AuthToken authToken, String alias, Observer observer) {
        return new GetUserTask(authToken, alias, new MessageHandler(observer, "getUser"));
    }

    public void login(String username, String password, Observer observer) {
        LoginTask loginTask = getLoginTask(username, password, observer);
        BackgroundTaskUtils.runTask(loginTask);
    }
    public LoginTask getLoginTask(String username, String password, Observer observer) {
        return new LoginTask(username, password, new MessageHandler(observer, "login"));
    }

    public void logout(AuthToken authToken, Observer observer) {
        LogoutTask task = getLogoutTask(authToken, observer);
        BackgroundTaskUtils.runTask(task);
    }
    public LogoutTask getLogoutTask(AuthToken authToken, Observer observer) {
        return new LogoutTask(authToken, new MessageHandler(observer, "logout"));
    }

    public void register(String first, String last, String username, String password,
                         String image, Observer observer) {
        RegisterTask registerTask = getRegisterTask(first, last, username, password, image, observer);
        BackgroundTaskUtils.runTask(registerTask);
    }
    public RegisterTask getRegisterTask(String first, String last, String username, String password,
                                        String image, Observer observer) {
        return new RegisterTask(first, last, username, password, image, new MessageHandler(observer, "register"));
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
            if (task.equals("getUser")) {
                boolean success = bundle.getBoolean(GetUserTask.SUCCESS_KEY);
                if (success) {
                    User user = (User) bundle.getSerializable(GetUserTask.USER_KEY);
                    observer.handleUserSuccess(user);
                } else if (bundle.containsKey(GetUserTask.MESSAGE_KEY)) {
                    String eMsg = bundle.getString(GetUserTask.MESSAGE_KEY);
                    eMsg = "Failed to get User: " + eMsg;
                    observer.handleFailure(eMsg);
                } else if (bundle.containsKey(GetUserTask.EXCEPTION_KEY)) {
                    Exception ex = (Exception) bundle.getSerializable(GetUserTask.EXCEPTION_KEY);
                    String eMsg = ex.getMessage().toString();
                    eMsg = "Exception when getting User: " + eMsg;
                    observer.handleException(eMsg, ex);
                }
            }
            else if (task.equals("login")) {
                boolean success = bundle.getBoolean(LoginTask.SUCCESS_KEY);
                if (success) {
                    User user = (User) message.getData().getSerializable(LoginTask.USER_KEY);
                    AuthToken authToken = (AuthToken) message.getData().getSerializable(LoginTask.AUTH_TOKEN_KEY);
                    observer.handleLoginSuccess(user, authToken);
                }
                else if (message.getData().containsKey(LoginTask.MESSAGE_KEY)) {
                    String msg = message.getData().getString(LoginTask.MESSAGE_KEY);
                    msg = "Failed to login: " + msg;
                    observer.handleFailure(msg);
                }
                else if (message.getData().containsKey(LoginTask.EXCEPTION_KEY)) {
                    Exception ex = (Exception) message.getData().getSerializable(LoginTask.EXCEPTION_KEY);
                    String msg = "Exception when logging in: " + ex.getMessage().toString();
                    observer.handleException(msg, ex);
                }
            }
            else if (task.equals("logout")) {
                boolean success = bundle.getBoolean(LogoutTask.SUCCESS_KEY);
                if (success) {
                    observer.handleLogoutSuccess();
                }
                else if (message.getData().containsKey(LogoutTask.MESSAGE_KEY)) {
                    String eMsg = message.getData().getString(LogoutTask.MESSAGE_KEY);
                    observer.handleFailure("Failed to logout: " + eMsg);
                }
                else if (message.getData().containsKey(LogoutTask.EXCEPTION_KEY)) {
                    Exception ex = (Exception) message.getData().getSerializable(LogoutTask.EXCEPTION_KEY);
                    String eMsg = "Failed to logout because of exception: " + ex.getMessage();
                    observer.handleException(eMsg, ex);
                }
            }
            else if (task.equals("register")) {
                boolean success = bundle.getBoolean(RegisterTask.SUCCESS_KEY);
                if (success) {
                    User user = (User) message.getData().getSerializable(RegisterTask.USER_KEY);
                    AuthToken authToken = (AuthToken) message.getData().getSerializable(RegisterTask.AUTH_TOKEN_KEY);
                    observer.handleRegisterSuccess(user, authToken);
                }
                else if (message.getData().containsKey(RegisterTask.MESSAGE_KEY)) {
                    String msg = message.getData().getString(RegisterTask.MESSAGE_KEY);
                    msg = "Failed to register: " + msg;
                    observer.handleFailure(msg);
                }
                else if (message.getData().containsKey(RegisterTask.EXCEPTION_KEY)) {
                    Exception ex = (Exception) message.getData().getSerializable(RegisterTask.EXCEPTION_KEY);
                    String msg = "Exception during registration: " + ex.getMessage().toString();
                    observer.handleException(msg, ex);
                }
            }
        }
    }
}
