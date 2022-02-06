package edu.byu.cs.tweeter.client.presenter;

import android.util.Log;

import edu.byu.cs.tweeter.client.cache.Cache;
import edu.byu.cs.tweeter.client.service.UserService;
import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.User;

public class LoginPresenter extends Presenter<LoginPresenter.LoginView, User> {
    private static final String LOG_TAG = "LoginPresenter";

    public interface LoginView extends View {
        void login(User user);
    }

    public LoginPresenter(LoginView view) {
        super(view);
    }

    @Override
    public void handleLoginSuccess(User user, AuthToken authToken) {
        Cache.getInstance().setCurrUser(user);
        Cache.getInstance().setCurrUserAuthToken(authToken);
        view.login(user);
    }

    @Override
    protected void logError(String message) {
        Log.e(LOG_TAG, message);
    }

    @Override
    protected void logError(String message, Exception exception) {
        Log.e(LOG_TAG, message, exception);
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
}
