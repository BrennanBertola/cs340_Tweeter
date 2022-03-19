package edu.byu.cs.tweeter.client.net;

import com.google.gson.Gson;

public class JsonSerializer {

    public static String serialize(Object requestInfo) {
        String tmp = (new Gson()).toJson(requestInfo);
        return tmp;
    }

    public static <T> T deserialize(String value, Class<T> returnType) {
        return (new Gson()).fromJson(value, returnType);
    }
}
