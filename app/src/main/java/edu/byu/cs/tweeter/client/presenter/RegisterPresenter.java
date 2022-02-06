package edu.byu.cs.tweeter.client.presenter;

import android.util.Log;
import android.widget.ImageView;

import edu.byu.cs.tweeter.client.cache.Cache;
import edu.byu.cs.tweeter.client.service.UserService;
import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.User;

public class RegisterPresenter extends Presenter<RegisterPresenter.RegisterView, User> {
    private static final String LOG_TAG = "LoginPresenter";

    public interface RegisterView extends View{
        void register(User user);
    }

    public RegisterPresenter(RegisterView view) {
        super(view);
    }

    @Override
    public void handleRegisterSuccess(User user, AuthToken authToken) {
        Cache.getInstance().setCurrUser(user);
        Cache.getInstance().setCurrUserAuthToken(authToken);
        view.register(user);
    }

    @Override
    public void logError(String message) {
        Log.e(LOG_TAG, message);
    }

    @Override
    public void logError(String message, Exception ex) {
        Log.e(LOG_TAG, message, ex);
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
}
