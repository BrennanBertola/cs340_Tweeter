package edu.byu.cs.tweeter.client.service;

public interface Observer {
    void handleFailure(String message);
    void handleException(String message, Exception exception);
}
