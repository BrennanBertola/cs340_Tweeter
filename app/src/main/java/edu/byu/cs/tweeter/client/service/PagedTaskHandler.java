package edu.byu.cs.tweeter.client.service;

import android.os.Bundle;
import android.util.Pair;

import java.util.List;

import edu.byu.cs.tweeter.client.backgroundTask.PagedTask;

public class PagedTaskHandler<T>  {
    Bundle bundle;

    public PagedTaskHandler(Bundle bundle) {
        this.bundle = bundle;
    }

    public Pair<List<T>, Boolean> handle() {
        List<T> items = (List<T>) bundle.getSerializable(PagedTask.ITEMS_KEY);
        Boolean hasMorePages = bundle.getBoolean(PagedTask.MORE_PAGES_KEY);
        Pair<List<T>, Boolean> pair = new Pair<>(items, hasMorePages);
        return pair;
    }
}
