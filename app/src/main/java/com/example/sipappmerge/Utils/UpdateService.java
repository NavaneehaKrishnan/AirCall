package com.example.sipappmerge.Utils;

import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;

import com.example.sipappmerge.Merge.PredictiveCall;
import com.example.sipappmerge.R;

import org.json.JSONException;
import org.json.JSONObject;

import static com.example.sipappmerge.Utils.Util.FetchCalls;
import static com.example.sipappmerge.Utils.Util.HangupCalls;


public class UpdateService extends Service {

    Handler handler = new Handler();

    public UpdateService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
    }
    @Override
    public void onDestroy() {
        stopSelf();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        handler.postDelayed(new Runnable() {
            public void run() {
                updateHangupCalls();
                handler.postDelayed(this,  5 * 1000);
            }

        }, 0);

        return START_STICKY;
    }

    private void updateHangupCalls() {
        try {
            JSONObject params = new JSONObject();
            params.put("username", Util.getData("AgentName", getApplicationContext()));
            params.put("password", Util.getData("password", getApplicationContext()));
            params.put("refno", "");
            params.put("phone_number", Util.getData("mobile_no",getApplicationContext()));
            params.put("process_name", Util.getData("process_name", getApplicationContext()));
            params.put("convoxid","");
            params.put("Local_IP", "");
            params.put("crm_id", Util.getData("crm_id", getApplicationContext()));
            params.put("autowrapup", "");
            Log.e("API", FetchCalls+ "\n" + params.toString());

            CallApi.READYNOPROGRESS(getApplicationContext(), params.toString(), FetchCalls, new VolleyResponseListener() {

                @Override
                public void onError(String message) {
                    Util.Logcat.e("FetchCalls onError" + message);


                }

                @Override
                public void onResponse(JSONObject response) {
                    Util.Logcat.e("FetchCalls Response" + response);
                    Intent local = new Intent();

                    local.setAction("com.app.predictive.action");
                    local.putExtra("response",response.toString());

                    sendBroadcast(local);

                }
            });

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
