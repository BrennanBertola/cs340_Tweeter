package edu.byu.cs.tweeter.client.service;


import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import edu.byu.cs.tweeter.client.backgroundTask.BackgroundTaskUtils;
import edu.byu.cs.tweeter.client.backgroundTask.RegisterTask;
import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.User;

public class RegisterService {

    public interface Observer {
        void handleRegisterSuccess(User user, AuthToken authToken);
        void handleRegisterFailure(String message);
        void handleRegisterException(Exception exception);
    }

    public RegisterService() {}

    public void register(String first, String last, String username, String password,
                      String image, Observer observer) {
        RegisterTask registerTask = getRegisterTask(first, last, username, password, image, observer);
        BackgroundTaskUtils.runTask(registerTask);
    }

    public RegisterTask getRegisterTask(String first, String last, String username, String password,
                                        String image, Observer observer) {
        return new RegisterTask(first, last, username, password, image, new MessageHandler(observer));
    }

    public class MessageHandler extends Handler {
        private final Observer observer;

        public MessageHandler(Observer observer) {
            this.observer = observer;
        }

        @Override
        public void handleMessage(Message message) {
            Bundle bundle = message.getData();
            boolean success = bundle.getBoolean(RegisterTask.SUCCESS_KEY);
            if (success) {
                User user = (User) message.getData().getSerializable(RegisterTask.USER_KEY);
                AuthToken authToken = (AuthToken) message.getData().getSerializable(RegisterTask.AUTH_TOKEN_KEY);
                observer.handleRegisterSuccess(user, authToken);
            }
            else if (message.getData().containsKey(RegisterTask.MESSAGE_KEY)) {
                String msg = message.getData().getString(RegisterTask.MESSAGE_KEY);
                observer.handleRegisterFailure(msg);
            }
            else if (message.getData().containsKey(RegisterTask.EXCEPTION_KEY)) {
                Exception ex = (Exception) message.getData().getSerializable(RegisterTask.EXCEPTION_KEY);
                observer.handleRegisterException(ex);
            }
        }
    }
}
