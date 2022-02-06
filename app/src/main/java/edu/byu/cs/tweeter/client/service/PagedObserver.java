package edu.byu.cs.tweeter.client.service;

import android.util.Pair;

import java.util.List;

public interface PagedObserver<T> extends Observer{
    void handlePagedSuccess(Pair<List<T>, Boolean> pair);
}
