package com.example.sipappmerge.Utils;

import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;

import com.example.sipappmerge.Merge.Login;
import com.example.sipappmerge.Merge.SplashActivity;
import com.example.sipappmerge.R;
import com.google.firebase.crashlytics.internal.common.CommonUtils;

public class MyService extends Service {
    Handler handler;
    Runnable test;

    public MyService() {
        handler = new Handler();
        test = new Runnable() {
            @Override
            public void run() {
                if(CommonUtils.isRooted(getApplicationContext()))
                {
                    new CallApi.AsyncTaskUpdateIsDeviceRooted(getApplicationContext()).execute();
                    CommonAlertDialog commonAlertDialog = new CommonAlertDialog(SplashActivity.splashActivity);
                    commonAlertDialog.ExitAlert(getString(R.string.rooted_app_alert));
                }
                else if(RootUtil.isDeviceRooted())
                {
                    new CallApi.AsyncTaskUpdateIsDeviceRooted(getApplicationContext()).execute();
                    CommonAlertDialog commonAlertDialog = new CommonAlertDialog(SplashActivity.splashActivity);
                    commonAlertDialog.ExitAlert(getString(R.string.rooted_app_alert));
                }
                else {


                }
                handler.postDelayed(test, 1000*60*10);
            }
        };

        handler.postDelayed(test, 0);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
