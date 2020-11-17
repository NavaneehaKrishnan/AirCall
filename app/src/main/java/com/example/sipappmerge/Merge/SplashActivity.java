package com.example.sipappmerge.Merge;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.Settings;
import android.telephony.SubscriptionInfo;
import android.telephony.SubscriptionManager;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.sipappmerge.R;
import com.example.sipappmerge.Utils.CallApi;
import com.example.sipappmerge.Utils.CommonAlertDialog;
import com.example.sipappmerge.Utils.MyBackgroundService;
import com.example.sipappmerge.Utils.MyService;
import com.example.sipappmerge.Utils.RootUtil;

import com.example.sipappmerge.Utils.ShaUtilss;
import com.example.sipappmerge.Utils.Util;
import com.example.sipappmerge.Utils.VolleyResponseListener;
import com.google.firebase.crashlytics.internal.common.CommonUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;
import java.util.UUID;

import static android.Manifest.permission.ACCESS_COARSE_LOCATION;
import static android.Manifest.permission.CALL_PHONE;
import static android.Manifest.permission.CAMERA;
import static android.Manifest.permission.KILL_BACKGROUND_PROCESSES;
import static android.Manifest.permission.READ_PHONE_STATE;
import static android.Manifest.permission.RECORD_AUDIO;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;
import static android.telecom.TelecomManager.ACTION_CHANGE_DEFAULT_DIALER;
import static com.example.sipappmerge.Utils.Util.Authenticate;



public class SplashActivity extends AppCompatActivity {

    private static final long DELAY_TIME = 3000;

    private Timer timer;

    private TimerTask timerTask;

    private Handler handler = new Handler();

    public static SplashActivity splashActivity ;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splashscreen);
        getSupportActionBar().hide();
        splashActivity = this;



        startService(new Intent(this, MyService.class));
            if (checkPermission()) {
                // Toast.makeText(SplashActivity.this, "Permission already granted.", Toast.LENGTH_SHORT).show();
                //initialize();

                init();

            } else {
                requestPermission();
            }


    }

    private void getToken() {
        try {
            JSONObject params = new JSONObject();
            params.put("Username", getString(R.string.pOne));
            params.put("Password", getString(R.string.pTwo));
            Log.e("Authenticate", Authenticate+ "\n" + params.toString());

            CallApi.postResponseNonHeader(SplashActivity.this, params.toString(), Authenticate, new VolleyResponseListener() {

                @Override
                public void onError(String message) {
                    Util.Logcat.e("Authenticate onError" + message);



                }

                @Override
                public void onResponse(JSONObject response) {
                    Util.Logcat.e("Authenticate Response" + response);
//
                    try {
                        if(response.getString("Status").equals("Sucess"))
                        {
                            Util.saveData("JWTToken",response.getString("Message"), getApplicationContext());
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void requestPermission() {
        ActivityCompat.requestPermissions(this, new String[]{ACCESS_COARSE_LOCATION, CAMERA, READ_PHONE_STATE, WRITE_EXTERNAL_STORAGE,KILL_BACKGROUND_PROCESSES,RECORD_AUDIO}, 200);
    }

    private boolean checkPermission() {
        int result = ContextCompat.checkSelfPermission(getApplicationContext(), ACCESS_COARSE_LOCATION);
        int result1 = ContextCompat.checkSelfPermission(getApplicationContext(), CALL_PHONE);
        int result2 = ContextCompat.checkSelfPermission(getApplicationContext(), READ_PHONE_STATE);
        int result3 = ContextCompat.checkSelfPermission(getApplicationContext(), WRITE_EXTERNAL_STORAGE);
        int result4 = ContextCompat.checkSelfPermission(getApplicationContext(), KILL_BACKGROUND_PROCESSES);
        int result5 = ContextCompat.checkSelfPermission(getApplicationContext(), RECORD_AUDIO);
        int result6= ContextCompat.checkSelfPermission(getApplicationContext(), TELEPHONY_SERVICE);

        return result == PackageManager.PERMISSION_GRANTED && result1 == PackageManager.PERMISSION_GRANTED
                && result2 == PackageManager.PERMISSION_GRANTED && result3 == PackageManager.PERMISSION_GRANTED&& result4 == PackageManager.PERMISSION_GRANTED && result5 == PackageManager.PERMISSION_GRANTED&& result6 == PackageManager.PERMISSION_GRANTED;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case 200:
                if (grantResults.length > 0) {

                    boolean locationAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                    boolean cameraAccepted = grantResults[1] == PackageManager.PERMISSION_GRANTED;
                    boolean phoneAccepted = grantResults[2] == PackageManager.PERMISSION_GRANTED;
                    boolean storageAccepted = grantResults[3] == PackageManager.PERMISSION_GRANTED;
                    boolean killprocess = grantResults[4] == PackageManager.PERMISSION_GRANTED;
                    boolean teleServices = grantResults[5] == PackageManager.PERMISSION_GRANTED;
                   // boolean access = grantResults[5] == PackageManager.PERMISSION_GRANTED;

                    if (locationAccepted && cameraAccepted && phoneAccepted && storageAccepted&&killprocess&&teleServices) {
                        //Toast.makeText(SplashActivity.this, "Permission Granted, Now you can access location data,camera,phone and storage", Toast.LENGTH_SHORT).show();
                        init();

                    } else {
                        init();

                        //Snackbar.make(view, "Permission Denied, You cannot access location data and camera.", Snackbar.LENGTH_LONG).show();
                       // Toast.makeText(SplashActivity.this, "Permission Denied, You cannot access location data,camera,phone and storage.", Toast.LENGTH_SHORT).show();

                       /* if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                            if (shouldShowRequestPermissionRationale(ACCESS_COARSE_LOCATION)) {
                                showMessageOKCancel("You need to allow access to both the permissions",
                                        new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                                    requestPermissions(new String[]{ACCESS_FINE_LOCATION, CAMERA, READ_PHONE_STATE, WRITE_EXTERNAL_STORAGE},
                                                            200);
                                                }
                                            }
                                        });
                                return;
                            }
                        }*/

                    }
                }


                break;
        }
    }

    private void init() {

       /* if(CommonUtils.isRooted(this))//28.09.2020
        {
            CommonAlertDialog commonAlertDialog = new CommonAlertDialog(this);
            commonAlertDialog.ExitAlert(getString(R.string.rooted_app_alert));
        }
        else if(RootUtil.isDeviceRooted())
        {
            CommonAlertDialog commonAlertDialog = new CommonAlertDialog(this);
            commonAlertDialog.ExitAlert(getString(R.string.rooted_app_alert));
        }
        else {*/
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    Intent home = new Intent(getApplicationContext(), Login.class);
                    startActivity(home);
                    finish();
                }
            }, DELAY_TIME);
        /*}*///28.09.2020
    }

    private void showMessageOKCancel(String message, DialogInterface.OnClickListener okListener) {
        new AlertDialog.Builder(SplashActivity.this)
                .setMessage(message)
                .setPositiveButton("OK", okListener)
                .setNegativeButton("Cancel", null)
                .create()
                .show();
    }

}
