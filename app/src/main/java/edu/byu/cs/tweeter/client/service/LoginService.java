package edu.byu.cs.tweeter.client.service;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import edu.byu.cs.tweeter.client.backgroundTask.BackgroundTaskUtils;
import edu.byu.cs.tweeter.client.backgroundTask.LoginTask;
import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.User;

public class LoginService {

    public interface Observer {
        void handleLoginSuccess(User user, AuthToken authToken);
        void handleLoginFailure(String message);
        void handleLoginException(Exception exception);
    }

    public LoginService() {}

    public void login(String username, String password, Observer observer) {
        LoginTask loginTask = getLoginTask(username, password, observer);
        BackgroundTaskUtils.runTask(loginTask);
    }

    public LoginTask getLoginTask(String username, String password, Observer observer) {
        return new LoginTask(username, password, new MessageHandler(observer));
    }

    public class MessageHandler extends Handler {
        private final Observer observer;

        public MessageHandler(Observer observer) {
            this.observer = observer;
        }

        @Override
        public void handleMessage(Message message) {
            Bundle bundle = message.getData();
            boolean success = bundle.getBoolean(LoginTask.SUCCESS_KEY);
            if (success) {
                User user = (User) message.getData().getSerializable(LoginTask.USER_KEY);
                AuthToken authToken = (AuthToken) message.getData().getSerializable(LoginTask.AUTH_TOKEN_KEY);
                observer.handleLoginSuccess(user, authToken);
            }
            else if (message.getData().containsKey(LoginTask.MESSAGE_KEY)) {
                String msg = message.getData().getString(LoginTask.MESSAGE_KEY);
                observer.handleLoginFailure(msg);
            }
            else if (message.getData().containsKey(LoginTask.EXCEPTION_KEY)) {
                Exception ex = (Exception) message.getData().getSerializable(LoginTask.EXCEPTION_KEY);
                observer.handleLoginException(ex);
            }
        }
    }
}
