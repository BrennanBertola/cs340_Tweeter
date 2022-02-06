package edu.byu.cs.tweeter.client.presenter;

import android.util.Log;

import edu.byu.cs.tweeter.client.cache.Cache;
import edu.byu.cs.tweeter.client.service.UserService;
import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.User;

public class LoginPresenter implements UserService.UserObserver {
    private static final String LOG_TAG = "LoginPresenter";

    private final View view;

    public interface View {
        void login(User user);
        void displayErrorMessage(String message);
    }

    public LoginPresenter(View view) {
        this.view = view;
    }

    //====== remove when fixing presenters ======//
    @Override
    public void handleUserSuccess(User user) {
        //remove when fixing presenter
    }

    @Override
    public void handleLogoutSuccess() {

    }

    @Override
    public void handleRegisterSuccess(User user, AuthToken authToken) {

    }
    //========================================//

    @Override
    public void handleLoginSuccess(User user, AuthToken authToken) {
        Cache.getInstance().setCurrUser(user);
        Cache.getInstance().setCurrUserAuthToken(authToken);
        view.login(user);
    }

    @Override
    public void handleFailure(String message) {
        Log.e(LOG_TAG, message);
        view.displayErrorMessage(message);
    }

    @Override
    public void handleException(String message, Exception ex) {
        Log.e(LOG_TAG, message, ex);
        view.displayErrorMessage(message);
    }

    public void login(String username, String password) {
        getUserService().login(username, password, this);
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

    public UserService getUserService() {return new UserService();}
}
