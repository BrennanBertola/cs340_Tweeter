package edu.byu.cs.tweeter.client.presenter;

import android.util.Pair;

import java.util.List;

import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.User;

//U is the view type, T is Statuses or Users
public abstract class PagedPresenter<U, T> extends Presenter<U, T> {
    public final int PAGE_SIZE = 10;
    protected PagedView<T> pagedView;

    protected final User user;
    protected final AuthToken authToken;

    protected T last;
    protected boolean hasMorePages = true;
    protected boolean isLoading = false;

    public interface PagedView <T> extends View {
        void setLoading(boolean value);
        void selectUser(User user);
        void addItems(List<T> items);
    }

    public PagedPresenter(U view, User user, AuthToken authToken) {
        super(view);
        this.user = user;
        this.authToken = authToken;
        pagedView = (PagedView<T>) view;
    }

    protected abstract void loadItems();

    @Override
    public void handlePagedSuccess(Pair<List<T>, Boolean> pair) {
        List<T> items = pair.first;
        if (items.size() > 0) {
            last = items.get(items.size() - 1);
        }
        else {
            last = null;
        }

        hasMorePages = pair.second;
        pagedView.setLoading(false);
        pagedView.addItems(items);
        isLoading = false;
    }

    @Override
    public void handleUserSuccess(User user) {
        pagedView.setLoading(false);
        pagedView.selectUser(user);
        isLoading = false;
    }

    @Override
    public void handleFailure(String message) {
        pagedView.setLoading(false);
        pagedView.displayMessage(message);
        logError(message);
        isLoading = false;
    }

    @Override
    public void handleException(String message, Exception exception) {
        pagedView.setLoading(false);
        pagedView.displayMessage(message);
        logError(message);
        isLoading = false;
    }

    public void loadMoreItems() {
        if (!isLoading && hasMorePages) {
            isLoading = true;
            pagedView.setLoading(true);

            loadItems();
        }
    }

    public void loadSelectedUser(String alias) {
        if (!isLoading) {
            isLoading = true;
            pagedView.setLoading(true);
            getSelectedUser(authToken, alias);
        }
    }

    public void  getSelectedUser(AuthToken authToken, String alias) {
        getUserService().getSelectedUser(authToken, alias, this);
    }

    public boolean loadMore(int visible, int first, int total) {
        if (!isLoading && hasMorePages) {
            if ((visible + first) >=
                    total && first >= 0) {
                return true;
            }
        }
        return false;
    }
}
