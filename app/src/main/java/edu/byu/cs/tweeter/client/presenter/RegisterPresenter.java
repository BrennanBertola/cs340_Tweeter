package edu.byu.cs.tweeter.client.presenter;

import android.util.Log;
import android.widget.ImageView;

import edu.byu.cs.tweeter.client.cache.Cache;
import edu.byu.cs.tweeter.client.service.UserService;
import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.User;

public class RegisterPresenter implements UserService.Observer {
    private static final String LOG_TAG = "LoginPresenter";

    private final View view;

    public interface View {
        void register(User user);
        void displayErrorMessage(String message);
    }

    public RegisterPresenter(View view) {
        this.view = view;
    }

    //====== remove when fixing presenters ======//
    @Override
    public void handleUserSuccess(User user) {

    }

    @Override
    public void handleLoginSuccess(User user, AuthToken authToken) {

    }

    @Override
    public void handleLogoutSuccess() {

    }
    //========================================//

    @Override
    public void handleRegisterSuccess(User user, AuthToken authToken) {
        Cache.getInstance().setCurrUser(user);
        Cache.getInstance().setCurrUserAuthToken(authToken);
        view.register(user);
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

    public void register(String first, String last, String username, String password, String image) {
        getUserService().register(first, last, username, password, image, this);
    }

    public void validateRegistration(String firstName, String lastName, String alias,
                                     String password, ImageView imageToUpload) throws IllegalArgumentException {
        if (firstName.length() == 0) {
            throw new IllegalArgumentException("First Name cannot be empty.");
        }
        if (lastName.length() == 0) {
            throw new IllegalArgumentException("Last Name cannot be empty.");
        }
        if (alias.length() == 0) {
            throw new IllegalArgumentException("Alias cannot be empty.");
        }
        if (alias.charAt(0) != '@') {
            throw new IllegalArgumentException("Alias must begin with @.");
        }
        if (alias.length() < 2) {
            throw new IllegalArgumentException("Alias must contain 1 or more characters after the @.");
        }
        if (password.length() == 0) {
            throw new IllegalArgumentException("Password cannot be empty.");
        }

        if (imageToUpload.getDrawable() == null) {
            throw new IllegalArgumentException("Profile image must be uploaded.");
        }
    }

    public UserService getUserService() {return new UserService();}
}
