package com.example.sipappmerge.Utils;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.provider.Settings;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public abstract class ConnectivityReceiver extends BroadcastReceiver {
    private Context context;
    public ConnectivityReceiver(Context context) {
        this.context = context;
    }
    @Override
    public void onReceive(Context context, Intent intent) {

        boolean isAirplaneModeOn = intent.getBooleanExtra("state", false);
        if(Settings.System.getInt(
                context.getContentResolver(),
                Settings.Global.AIRPLANE_MODE_ON, 0
        ) == 0) {

            airplaneModeChanged(false);
        } else {
            airplaneModeChanged(true);
        }

    }
    public void register() {
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(Intent.ACTION_AIRPLANE_MODE_CHANGED);
        context.registerReceiver(this, intentFilter);
    }

    /**
     * Used to unregister the airplane mode reciever.
     */
    public void unregister() {
        try {
            context.unregisterReceiver(this);
        }catch (Exception e)
        {
            e.printStackTrace();
        }
    }
    public abstract void airplaneModeChanged(boolean enabled);
}
