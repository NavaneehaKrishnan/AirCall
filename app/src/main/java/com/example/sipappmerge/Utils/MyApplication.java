package com.example.sipappmerge.Utils;

import android.app.Application;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

public class MyApplication extends Application {

    public static MyApplication sInstance;
    private RequestQueue mRequestQueue;

    @Override
    public void onCreate() {

        super.onCreate();
       /* new UCEHandler.Builder(getApplicationContext())
                .setTrackActivitiesEnabled(true)
                .addCommaSeparatedEmailAddresses("comma separated email addresses")
                .build();*/
        mRequestQueue = Volley.newRequestQueue(this);
        sInstance = this;

    }

    public synchronized static MyApplication getInstance() {
        return sInstance;
    }

    public RequestQueue getRequestQueue() {
        return mRequestQueue;
    }
}
