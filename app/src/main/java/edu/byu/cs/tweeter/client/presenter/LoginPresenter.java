package edu.byu.cs.tweeter.client.presenter;

import android.util.Log;

import edu.byu.cs.tweeter.client.cache.Cache;
import edu.byu.cs.tweeter.client.service.LoginService;
import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.User;

public class LoginPresenter implements LoginService.Observer {
    private static final String LOG_TAG = "LoginPresenter";

    private final View view;

    public interface View {
        void login(User user);
        void displayErrorMessage(String message);
    }

    public LoginPresenter(View view) {
        this.view = view;
    }

    @Override
    public void handleLoginSuccess(User user, AuthToken authToken) {
        Cache.getInstance().setCurrUser(user);
        Cache.getInstance().setCurrUserAuthToken(authToken);
        view.login(user);
    }

    @Override
    public void handleLoginFailure(String message) {
        String eMsg = "Failed to login: " + message;
        Log.e(LOG_TAG, eMsg);
        view.displayErrorMessage(eMsg);
    }

    @Override
    public void handleLoginException(Exception ex) {
        String errorMessage = "Failed to login because of exception: " + ex.getMessage();
        Log.e(LOG_TAG, errorMessage, ex);
        view.displayErrorMessage(errorMessage);
    }

    public void login(String username, String password) {
        getLoginService().login(username, password, this);
    }

    public void validateLogin(String alias, String password) throws IllegalArgumentException {
        if (alias.charAt(0) != '@') {
            throw new IllegalArgumentException("Alias must begin with @.");
        }
        if (alias.length() < 2) {
            throw new IllegalArgumentException("Alias must contain 1 or more characters after the @.");
        }
        if (password.length() == 0) {
            throw new IllegalArgumentException("Password cannot be empty.");
        }
    }

    public LoginService getLoginService() {return new LoginService();}
}
