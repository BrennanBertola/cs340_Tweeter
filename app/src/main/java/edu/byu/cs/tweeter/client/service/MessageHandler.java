package edu.byu.cs.tweeter.client.service;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;

import edu.byu.cs.tweeter.client.backgroundTask.BackgroundTask;
import edu.byu.cs.tweeter.client.backgroundTask.PostStatusTask;

public abstract class MessageHandler extends Handler {
    protected abstract void success(Bundle bundle);
    protected abstract void fail (String message);
    protected abstract void exception (String message, Exception ex);

    public MessageHandler() {
        super(Looper.getMainLooper());
    }

    @Override
    public void handleMessage(Message message) {
        Bundle bundle = message.getData();
        boolean success = bundle.getBoolean(BackgroundTask.SUCCESS_KEY);
        if (success) {
            success(bundle);
        }
        else if (bundle.containsKey(BackgroundTask.MESSAGE_KEY)) {
            String eMsg = bundle.getString(BackgroundTask.MESSAGE_KEY);
            fail(eMsg);
        }
        else if (bundle.containsKey(BackgroundTask.EXCEPTION_KEY)) {
            Exception ex = (Exception) bundle.getSerializable(PostStatusTask.EXCEPTION_KEY);
            String eMsg = ex.getMessage();
            exception(eMsg, ex);
        }
    }
}
