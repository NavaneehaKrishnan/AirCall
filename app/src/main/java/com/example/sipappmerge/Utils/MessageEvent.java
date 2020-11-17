package com.example.sipappmerge.Utils;

public class MessageEvent {
    public String mMessage;
    public String mKey;

    public MessageEvent(String message,String key) {
        mMessage = message;
        mKey = key;
    }

    public String getMessage() {
        return mMessage;
    }
    public String getKey() {
        return mKey;
    }
}
