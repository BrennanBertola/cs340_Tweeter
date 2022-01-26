package edu.byu.cs.tweeter.client.presenter;

import android.util.Log;

import edu.byu.cs.tweeter.client.cache.Cache;
import edu.byu.cs.tweeter.client.service.RegisterService;
import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.User;

public class RegisterPresenter implements RegisterService.Observer {
    private static final String LOG_TAG = "LoginPresenter";

    private final View view;

    public interface View {
        void register(User user);
        void displayErrorMessage(String message);
    }

    public RegisterPresenter(View view) {
        this.view = view;
    }

    @Override
    public void handleRegisterSuccess(User user, AuthToken authToken) {
        Cache.getInstance().setCurrUser(user);
        Cache.getInstance().setCurrUserAuthToken(authToken);
        view.register(user);
    }

    @Override
    public void handleRegisterFailure(String message) {
        String eMsg = "Failed to register: " + message;
        Log.e(LOG_TAG, eMsg);
        view.displayErrorMessage(eMsg);
    }

    @Override
    public void handleRegisterException(Exception ex) {
        String errorMessage = "Failed to register because of exception: " + ex.getMessage();
        Log.e(LOG_TAG, errorMessage, ex);
        view.displayErrorMessage(errorMessage);
    }

    public void register(String first, String last, String username, String password, String image) {
        getRegisterService().register(first, last, username, password, image, this);
    }

    public RegisterService getRegisterService() {return new RegisterService();}
}
