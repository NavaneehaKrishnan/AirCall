package com.example.sipappmerge.Merge;

import android.Manifest;
import android.app.ActivityManager;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.media.MediaRecorder;
import android.net.Uri;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.StatFs;
import android.provider.Settings;
import android.telecom.TelecomManager;
import android.telephony.SubscriptionInfo;
import android.telephony.SubscriptionManager;
import android.telephony.TelephonyManager;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.view.autofill.AutofillManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.JsonObjectRequest;
import com.example.sipappmerge.Utils.CallApi;
import com.example.sipappmerge.Utils.CommonAlertDialog;
import com.example.sipappmerge.Utils.CustomerModel;
import com.example.sipappmerge.Utils.MessageEvent;
import com.example.sipappmerge.Utils.MyApplication;
import com.example.sipappmerge.R;
import com.example.sipappmerge.Utils.RootUtil;
import com.example.sipappmerge.Utils.ScheduledJobService;
import com.example.sipappmerge.Utils.ShaUtilss;
import com.example.sipappmerge.Utils.Util;
import com.example.sipappmerge.Utils.VolleyResponseListener;
import com.example.sipappmerge.adapter.PredictiveCusAdapter;
import com.example.sipappmerge.crash.CallActivity;
import com.firebase.jobdispatcher.Constraint;
import com.firebase.jobdispatcher.FirebaseJobDispatcher;
import com.firebase.jobdispatcher.GooglePlayDriver;
import com.firebase.jobdispatcher.Job;
import com.firebase.jobdispatcher.Lifetime;
import com.firebase.jobdispatcher.RetryStrategy;
import com.firebase.jobdispatcher.Trigger;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.safetynet.SafetyNet;
import com.google.android.gms.safetynet.SafetyNetApi;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.crashlytics.internal.common.CommonUtils;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.lang.reflect.Field;
import java.nio.charset.StandardCharsets;
import java.sql.Array;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

import fr.bmartel.speedtest.SpeedTestReport;
import fr.bmartel.speedtest.SpeedTestSocket;
import fr.bmartel.speedtest.inter.ISpeedTestListener;
import fr.bmartel.speedtest.model.SpeedTestError;


import static com.example.sipappmerge.Utils.Util.AppErrorLog;
import static com.example.sipappmerge.Utils.Util.BASE;

import static com.example.sipappmerge.Utils.Util.LOGINNew;

import static com.example.sipappmerge.Utils.Util.LOGOUTUSER;
import static com.example.sipappmerge.Utils.Util.MobileAPPLogin;
import static com.example.sipappmerge.Utils.Util.recording;


public class Login extends AppCompatActivity implements View.OnClickListener {
    private MediaRecorder mRecorder;
    private static String mFileName = null;
    public static final int REQUEST_AUDIO_PERMISSION_CODE = 1;
    EditText EdUserName, EdPassWord;
    long availableMegs;
    Button BtnLogin;
    CommonAlertDialog alert;
    CheckBox chkbox;
    public MyApplication app;
    List<ActivityManager.RunningAppProcessInfo> processes;
    ActivityManager amg;
    PackageManager pm;
    //ProgressDialog pd;
    private static final int PERMISSION_REQUEST_CODE = 200;
    boolean available = false;

    private String strAndroid_id = "",strCountry = "",strWifiId = "",strInstalledApps = "",strIMEI = "",strSerial = "",strState = "",strOperator = "",strCount = "",stDataNWType = "",stNWType = "";
    //FETCHPROGRESSIVECALLS

    public static  String strNetSafety = "";
    public static final String GOOGLE_API_VERIFY_URL = "https://www.googleapis.com/androidcheck/v1/attestations/";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_new);
        getSupportActionBar().hide();
        if (recording) {
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE);
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            devicedetails();
        }
        checkRooted();//28.09.2020
        new AsyncTaskRequestNetSafety().execute();
        new SpeedTestTask().execute();


        amg = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
        final Thread.UncaughtExceptionHandler defHandler = Thread.getDefaultUncaughtExceptionHandler();

        Thread.setDefaultUncaughtExceptionHandler(new Thread.UncaughtExceptionHandler() {
            @Override
            public void uncaughtException(Thread t, Throwable ex) {

                try {
                    //your own addition
                    final Writer stringBuffSync = new StringWriter();
                    final PrintWriter printWriter = new PrintWriter(stringBuffSync);
                    ex.printStackTrace(printWriter);
                    String stacktrace = stringBuffSync.toString();
                    printWriter.close();
                    Log.e("currentStacktrace", stacktrace);
                    try {
                        // UpdateError(stacktrace);
                        // mail(ex.toString());
                        UpdateError(ex.toString());

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                } finally {
                    defHandler.uncaughtException(t, ex);
                }
            }
        });

        File dir = new File(Environment.getExternalStorageDirectory(), "ARM");

        if (!dir.exists()) {
            dir.mkdirs();
        }


        initialize();

    }

    private void checkRooted() {
        if(CommonUtils.isRooted(this))
        {
            CommonAlertDialog commonAlertDialog = new CommonAlertDialog(this);
            commonAlertDialog.ExitAlert(getString(R.string.rooted_app_alert));
        }
        else if(RootUtil.isDeviceRooted())
        {
            CommonAlertDialog commonAlertDialog = new CommonAlertDialog(this);
            commonAlertDialog.ExitAlert(getString(R.string.rooted_app_alert));
        }
    }

    private void newloop() {
        // ActivityManager manager =  (ActivityManager) getSystemService(ACTIVITY_SERVICE);
        List<ActivityManager.RunningAppProcessInfo> activityes = amg.getRunningAppProcesses();

        for (int iCnt = 0; iCnt < activityes.size(); iCnt++) {

            System.out.println("APP: " + iCnt + " " + activityes.get(iCnt).processName);

            if (!activityes.get(iCnt).processName.equalsIgnoreCase("com.example.sipappmerge")) {
                android.os.Process.sendSignal(activityes.get(iCnt).pid, android.os.Process.SIGNAL_KILL);
                android.os.Process.killProcess(activityes.get(iCnt).pid);
                //manager.killBackgroundProcesses("com.android.email");

                //manager.restartPackage("com.android.email");

                System.out.println("Inside if");
            }

        }
    }
    public  void scheduleJob() {
        String Job_Tag = "my_job_tag";
        FirebaseJobDispatcher jobDispatcher = new FirebaseJobDispatcher(new GooglePlayDriver(this));
        Job job = jobDispatcher.newJobBuilder().
                setService(ScheduledJobService.class).
                setLifetime(Lifetime.FOREVER).
                setRecurring(true).
                setTag(Job_Tag).
                setTrigger(Trigger.executionWindow(15*60,2*15*60)).
                setRetryStrategy(RetryStrategy.DEFAULT_EXPONENTIAL).
                setReplaceCurrent(false).
                setConstraints(Constraint.ON_ANY_NETWORK)
                .build();
        jobDispatcher.mustSchedule(job);
    }


    private void devicedetails() {
        /*String osName = "";
        String device_id = Settings.Secure.getString(getContentResolver(),
                Settings.Secure.ANDROID_ID);*/
       /* try {
            Field[] fields = Build.VERSION_CODES.class.getFields();
            osName = "Android" + fields[Build.VERSION.SDK_INT + 1].getName();
        } catch (ArrayIndexOutOfBoundsException e) {
        }*/

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
            return;
        }


        if(Build.VERSION.SDK_INT< Build.VERSION_CODES.Q && Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
        {
            TelephonyManager telephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
            strAndroid_id = Settings.Secure.getString(getApplicationContext().getContentResolver(), Settings.Secure.ANDROID_ID);

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                strCount = String.valueOf(telephonyManager.getPhoneCount());
                strIMEI = telephonyManager.getPhoneCount()>1 ? telephonyManager.getDeviceId(0)+","+telephonyManager.getDeviceId(1):telephonyManager.getDeviceId(0);
            }


            strCountry = telephonyManager.getSimCountryIso();
            strSerial = telephonyManager.getSimSerialNumber();
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                strState = String.valueOf(telephonyManager.getServiceState());
                stDataNWType = String.valueOf(telephonyManager.getDataNetworkType());
            }
            stNWType = String.valueOf(telephonyManager.getNetworkType());




            if (Build.VERSION.SDK_INT > 22) {
                //for dual sim mobile
                SubscriptionManager localSubscriptionManager = SubscriptionManager.from(this);

                if (localSubscriptionManager.getActiveSubscriptionInfoCount() > 1) {
                    //if there are two sims in dual sim mobile
                    List localList = localSubscriptionManager.getActiveSubscriptionInfoList();
                    SubscriptionInfo simInfo = (SubscriptionInfo) localList.get(0);
                    SubscriptionInfo simInfo1 = (SubscriptionInfo) localList.get(1);

                    strOperator= simInfo.getDisplayName().toString() +","+simInfo1.getDisplayName().toString();

                }else{
                    //if there is 1 sim in dual sim mobile
                    TelephonyManager tManager = (TelephonyManager) getBaseContext()
                            .getSystemService(Context.TELEPHONY_SERVICE);

                    strOperator = tManager.getNetworkOperatorName();

                }

            }else{
                //below android version 22
                TelephonyManager tManager = (TelephonyManager) getBaseContext()
                        .getSystemService(Context.TELEPHONY_SERVICE);

                strOperator = tManager.getNetworkOperatorName();

            }
            try {
                strWifiId = Util.getMacAddr();
                String appsList = Util.getAppNameList(getPackageManager()).toString();
                strInstalledApps = appsList.substring(1, appsList.length() - 1);
               /* ;
                Log.e("WIFI", Util.getMacAddr());
                Log.e("strInstalledApps", strInstalledApps);
                Log.e("ANDROID_ID", strAndroid_id);
                Log.e("IMEI No", strIMEI);
                Log.e("getSimSerialNumber ", strSerial);
                Log.e("getSimCountryIso ", strCountry);
                Log.e("getDataNetworkType ", stDataNWType);//N
                Log.e("getNetworkType ", stNWType);
                Log.e("getPhoneCount ", strCount);
                Log.e("strOperator", strOperator);
                Log.e("strState", strState);*/
            }catch (Exception e)
            {
                e.printStackTrace();
            }
        }else
        {

        }
        /*String device = osName + "|" + Build.VERSION.RELEASE + "|" + Build.MANUFACTURER + "|" + Build.MODEL;
        Util.saveData("DEVICEDETAILS", device, getApplicationContext());
        Util.Logcat.e("device>" + device);*/
    }

    private void UpdateError(String error) throws JSONException {

        JSONObject params = new JSONObject();

        try {

            if (!Util.getData("username", getApplicationContext()).isEmpty()) {
                params.put("UserId", Util.getData("username", getApplicationContext()));
            } else {
                params.put("UserId", "");
            }

            String device_id = Settings.Secure.getString(getContentResolver(),
                    Settings.Secure.ANDROID_ID);
            params.put("DeviceId", "NEW" + device_id);
            params.put("ErrorInfo", error);
            Log.e("ERROR", "\n" + params.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }

        JsonObjectRequest req = new JsonObjectRequest(Request.Method.POST, AppErrorLog, new JSONObject(params.toString()),
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.e("ERROR response", "\n" + response.toString());

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        }) {

            @Override
            protected Response<JSONObject> parseNetworkResponse(NetworkResponse response) {
                try {
                    String jsonString = new String(response.data,
                            HttpHeaderParser.parseCharset(response.headers));
                    return Response.success(new JSONObject(jsonString),
                            HttpHeaderParser.parseCacheHeaders(response));
                } catch (UnsupportedEncodingException | JSONException e) {
                    return Response.error(new ParseError(e));
                }
            }

        };
        MyApplication.getInstance().getRequestQueue().add(req);
    }

    @Override
    public void onResume() {
        super.onResume();

    }

    public  class SpeedTestTask extends AsyncTask<Void, Void, String> {

        @Override
        protected String doInBackground(Void... params) {

            SpeedTestSocket speedTestSocket = new SpeedTestSocket();
            // add a listener to wait for speedtest completion and progress
            speedTestSocket.addSpeedTestListener(new ISpeedTestListener() {

                @Override
                public void onCompletion(SpeedTestReport report) {

                    DecimalFormat dec = new DecimalFormat("0.00");
                    final double dsdas = Double.parseDouble(dec.format(report.getTransferRateBit())) / 1024;
                    ScheduledJobService.strDataSpeed = String.valueOf(dsdas);
                    // double dsdas=dec.format(sads);

                }

                @Override
                public void onError(SpeedTestError speedTestError, String errorMessage) {
                    // called when a download/upload error occur
                }

                @Override
                public void onProgress(float percent, SpeedTestReport report) {

                }

            });
            speedTestSocket.startDownload("http://ipv4.ikoula.testdebit.info/1M.iso");
            return null;
        }
    }

    private void showMessageOKCancel(String message) {
        ActivityManager.MemoryInfo mi = new ActivityManager.MemoryInfo();
        ActivityManager activityManager = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
        activityManager.getMemoryInfo(mi);
        availableMegs = mi.availMem / 1048576L;
        StatFs stat = new StatFs(Environment.getExternalStorageDirectory().getPath());
        long bytesAvailable = (long) stat.getBlockSize() * (long) stat.getBlockCount();
        long megAvailable = (bytesAvailable / 1048576);

        //  pd.cancel();
        new AlertDialog.Builder(getApplicationContext())
                //  .setMessage(message+"\n"+"Available RAM : "+availableMegs+" MB"+"\n"+"Available Storage :"+megAvailable)
                // .setMessage(message+"\n"+"Available RAM : "+availableMegs+" MB")
                .setMessage(message)
                .setPositiveButton("OK", null)
                // .setNegativeButton("Cancel", null)
                .create()
                .show();
       /* try {
//show dialog
        }
        catch (WindowManager.BadTokenException ex) {
            ex.printStackTrace();
        }*/
    }

    private void installapp(String msg) {

        final AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.alertDialog);
        builder.setMessage(msg)
                .setCancelable(false)
                .setPositiveButton("INSTALL", new DialogInterface.OnClickListener() {
                    public void onClick(final DialogInterface dialog, final int id) {
                        dialog.dismiss();
                        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=com.simplemobiletools.dialer"));
                        startActivity(intent);
                    }
                });
        final AlertDialog alert = builder.create();
        alert.show();

    }

    private void setdefault(String msg) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.alertDialog);
        builder.setMessage(msg)
                .setCancelable(false)
                .setPositiveButton("Settings", new DialogInterface.OnClickListener() {
                    public void onClick(final DialogInterface dialog, final int id) {
                        dialog.dismiss();
                        //startActivityForResult(new Intent(android.provider.Settings.ACTION_SETTINGS), 0);
                        startActivityForResult(new Intent(Settings.ACTION_MANAGE_DEFAULT_APPS_SETTINGS), 0);
                    }
                });
        final AlertDialog alert = builder.create();
        alert.show();
    }

    private void initialize() {

        EdUserName = findViewById(R.id.username);
        EdPassWord = findViewById(R.id.password);
        BtnLogin = findViewById(R.id.login);
        BtnLogin.setOnClickListener(this);
        alert = new CommonAlertDialog(this);
        chkbox = findViewById(R.id.rememberme);

       /* EdUserName.setText("TestPDMNW");
        EdPassWord.setText("Pass@123");*/

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            AutofillManager autofillManager = getSystemService(AutofillManager.class);
            autofillManager.disableAutofillServices();
            EdUserName.setImportantForAutofill(View.IMPORTANT_FOR_AUTOFILL_NO);
            EdPassWord.setImportantForAutofill(View.IMPORTANT_FOR_AUTOFILL_NO);
        }

        chkbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                if (isChecked) {
                    if (!Util.getData("username", getApplicationContext()).isEmpty()) {
                    EdUserName.setText(Util.getData("username", getApplicationContext()));
                    EdPassWord.setText(Util.getData("password", getApplicationContext()));
                    }
                } else {
                    EdUserName.setText("");
                    EdPassWord.setText("");
                }
            }
        });
    }

    @Override
    public void onClick(View v) {
        ActivityManager.MemoryInfo mi = new ActivityManager.MemoryInfo();
        ActivityManager activityManager = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
        activityManager.getMemoryInfo(mi);
        long RAM = mi.availMem / 1048576L;

        switch (v.getId()) {
            case R.id.login:
                /*Intent main = new Intent(Login.this, NewModel.class);
                startActivity(main);
                finish();*/
                if (EdUserName.getEditableText().toString().isEmpty()) {
                    alert.build("Enter Username");
                } else if (EdPassWord.getEditableText().toString().isEmpty()) {
                    alert.build("Enter Password");
                } else if (RAM < 500) {
                    alert.build("Not Enough RAM Space. Kindly Stop all other apps and try again");
                } else {
                    callSecondaryApi();
                }
                break;
            default:
                break;
        }
    }

    private void showAlert(final JSONObject resobject, final String allow) {

        List<String> packagesName = new ArrayList<String>();
        String strAppNames = "";


        androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(Login.this,     R.style.alertDialog);
        // builder.setTitle(title);
        try {

            packagesName = Arrays.asList(resobject.getString("BlockedApps").split(","));
            for (String packageInfo : packagesName) {
                strAppNames = strAppNames+" "+Util.getAppNameFromPkgName(Login.this,packageInfo);

            }
            builder.setMessage("You have apps like"+strAppNames+" on nYour phone which can be unsafe. Please be cautious.");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                if(allow.equalsIgnoreCase("Allow"))
                {
                    try {
                    if (chkbox.isChecked() == true) {
                        Util.saveData("username", EdUserName.getEditableText().toString(), getApplicationContext());
                        Util.saveData("password", EdPassWord.getEditableText().toString(), getApplicationContext());
                    }
                    Util.saveData("Domain", resobject.getString("Domain"), getApplicationContext());
                        if(resobject.has("pwdstatus") && resobject.getString("pwdstatus").equals("0"))
                        {

                            Util.saveData("AgentName", EdUserName.getEditableText().toString(), getApplicationContext());
                            EdUserName.setText("");
                            EdPassWord.setText("");
                            startActivity(new Intent(Login.this,ChangePassword.class).putExtra("isFirstLogin","yes"));
                        }else {


                            if (resobject.getString("Status").equalsIgnoreCase("LI000")) {
                                //  alert.build(resobject.getString("MESSAGE"));
                                if (chkbox.isChecked() == true) {
                                    Util.saveData("username", EdUserName.getEditableText().toString(), getApplicationContext());
                                    Util.saveData("password", EdPassWord.getEditableText().toString(), getApplicationContext());
                                }

                                Toast.makeText(Login.this, resobject.getString("statusdesc"), Toast.LENGTH_LONG);

                                Util.saveData("AgentName", EdUserName.getEditableText().toString(), getApplicationContext());
                                Util.saveData("mobile_no", resobject.getString("Mobile"), getApplicationContext());
                                Util.saveData("crm_id", resobject.getString("crm_id"), getApplicationContext());
                                Util.saveData("callmode", resobject.getString("callmode"), getApplicationContext());
                                Util.saveData("process_name", resobject.getString("process_name"), getApplicationContext());
                                shareData();
                                scheduleJob();
                                if (resobject.getString("callmode").equalsIgnoreCase("preview")) {
                                    Intent main = new Intent(Login.this, NewModel.class);
                                    startActivity(main);
                                    finish();
                                } else if (resobject.getString("callmode").equalsIgnoreCase("progressive")) {
                                    Intent main = new Intent(Login.this, ProgressiveCall.class);
                                    startActivity(main);
                                    finish();
                                }else if (resobject.has("callmode")&&resobject.getString("callmode").equalsIgnoreCase("predictive")) {
                                    Intent main = new Intent(Login.this, PredictiveCall.class);
                                    startActivity(main);
                                    finish();
                                }

                            } else if (resobject.getString("Status").equalsIgnoreCase("2")) {
                                if (chkbox.isChecked() == true) {
                                    Util.saveData("username",resobject.getString("AgentId"), getApplicationContext());
                                    Util.saveData("password", EdPassWord.getEditableText().toString(), getApplicationContext());
                                }
                                Util.saveData("AgentName", resobject.getString("AgentId"), getApplicationContext());
                                Util.saveData("mobile_no", resobject.getString("Mobile"), getApplicationContext());
                                Util.saveData("crm_id", resobject.getString("crm_id"), getApplicationContext());
                                Util.saveData("callmode", resobject.getString("callmode"), getApplicationContext());
                                Util.saveData("process_name", resobject.getString("process_name"), getApplicationContext());
                                shareData();
                                scheduleJob();
                                if (resobject.has("call_status")&&resobject.getString("call_status").equalsIgnoreCase("ONCALL")) {
                                    //GetPreviewCall(leadid, mobileno);
                                    if (resobject.has("lead_id")&&resobject.getString("lead_id").isEmpty() && resobject.getString("phone_number").isEmpty()) {
                                        alert.build("Your user id is locked. Please contact your supervisor to force logout");
                                    }else if (resobject.has("callmode")&&resobject.getString("callmode").equalsIgnoreCase("progressive")) {
                                        Intent main = new Intent(Login.this, ProgressiveCall.class);
                                        startActivity(main);
                                        finish();
                                    } else if (resobject.has("callmode")&&resobject.getString("callmode").equalsIgnoreCase("predictive")) {
                                        Intent main = new Intent(Login.this, PredictiveCall.class);
                                        startActivity(main);
                                        finish();
                                    }else {
                                        Intent main = new Intent(Login.this, CallDispo.class);
                                        main.putExtra("leadid", resobject.getString("lead_id"));
                                        main.putExtra("mobileno", resobject.getString("phone_number"));
                                        main.putExtra("dial", "false");
                                        main.putExtra("crm_name", resobject.getString("crm_id"));
                                        startActivity(main);
                                        finish();
                                    }
                                } else if (resobject.has("call_status")&&resobject.getString("call_status").equalsIgnoreCase("WRAPUP")) {
                                    //GetPreviewCall(leadid, mobileno);
                                    if (resobject.has("lead_id")&&resobject.getString("lead_id").isEmpty() && resobject.getString("phone_number").isEmpty()) {
                                        alert.build("Your user id is locked. Please contact your supervisor to force logout");
                                    }else if (resobject.has("callmode")&&resobject.getString("callmode").equalsIgnoreCase("progressive")) {
                                        Intent main = new Intent(Login.this, ProgressiveCall.class);
                                        startActivity(main);
                                        finish();
                                    }else if (resobject.has("callmode")&&resobject.getString("callmode").equalsIgnoreCase("predictive")) {
                                        Intent main = new Intent(Login.this, PredictiveCall.class);
                                        startActivity(main);
                                        finish();
                                    } else {

                                        Intent main = new Intent(Login.this, CallDispo.class);
                                        main.putExtra("leadid", resobject.getString("lead_id"));
                                        main.putExtra("mobileno", resobject.getString("phone_number"));
                                        main.putExtra("dial", "false");
                                        main.putExtra("crm_name", resobject.getString("crm_id"));
                                        startActivity(main);
                                        finish();

                                    }
                                } else if (resobject.has("call_status")&&resobject.getString("call_status").equalsIgnoreCase("MISSED")) {
                                    //GetPreviewCall(leadid, mobileno);
                                    if (resobject.has("lead_id")&&resobject.getString("lead_id").isEmpty() && resobject.getString("phone_number").isEmpty()) {
                                        alert.build("Your user id is locked. Please contact your supervisor to force logout");
                                    }else if (resobject.has("callmode")&&resobject.getString("callmode").equalsIgnoreCase("progressive")) {
                                        Intent main = new Intent(Login.this, ProgressiveCall.class);
                                        startActivity(main);
                                        finish();
                                    } else if (resobject.has("callmode")&&resobject.getString("callmode").equalsIgnoreCase("predictive")) {
                                        Intent main = new Intent(Login.this, PredictiveCall.class);
                                        startActivity(main);
                                        finish();
                                    }else {
                                        Intent main = new Intent(Login.this, CallDispo.class);
                                        main.putExtra("leadid", resobject.getString("lead_id"));
                                        main.putExtra("mobileno", resobject.getString("phone_number"));
                                        main.putExtra("dial", "false");
                                        main.putExtra("crm_name", resobject.getString("crm_id"));
                                        startActivity(main);
                                        finish();
                                    }
                                } else if (resobject.has("call_status")&&resobject.getString("call_status").equalsIgnoreCase("DIALING")) {
                                    //GetPreviewCall(leadid, mobileno);
                                    if (resobject.has("lead_id")&&resobject.getString("lead_id").isEmpty() && resobject.getString("phone_number").isEmpty()) {
                                        alert.build("Your user id is locked. Please contact your supervisor to force logout");
                                    }else if (resobject.has("callmode")&&resobject.getString("callmode").equalsIgnoreCase("progressive")) {
                                        Intent main = new Intent(Login.this, ProgressiveCall.class);
                                        startActivity(main);
                                        finish();
                                    } else if (resobject.has("callmode")&&resobject.getString("callmode").equalsIgnoreCase("predictive")) {
                                        Intent main = new Intent(Login.this, PredictiveCall.class);
                                        startActivity(main);
                                        finish();
                                    }else {
                                        Intent main = new Intent(Login.this, CallDispo.class);
                                        main.putExtra("leadid", resobject.getString("lead_id"));
                                        main.putExtra("mobileno", resobject.getString("phone_number"));
                                        main.putExtra("dial", "false");
                                        main.putExtra("crm_name", resobject.getString("crm_id"));
                                        startActivity(main);
                                        finish();
                                    }
                                } else if (resobject.has("call_status")&&resobject.getString("call_status").equalsIgnoreCase("RINGING")) {
                                    //GetPreviewCall(leadid, mobileno);
                                    if (resobject.has("lead_id")&&resobject.getString("lead_id").isEmpty() && resobject.getString("phone_number").isEmpty()) {
                                        alert.build("Your user id is locked. Please contact your supervisor to force logout");
                                    }else if (resobject.has("callmode")&&resobject.getString("callmode").equalsIgnoreCase("progressive")) {
                                        Intent main = new Intent(Login.this, ProgressiveCall.class);
                                        startActivity(main);
                                        finish();
                                    } else if (resobject.has("callmode")&&resobject.getString("callmode").equalsIgnoreCase("predictive")) {
                                        Intent main = new Intent(Login.this, PredictiveCall.class);
                                        startActivity(main);
                                        finish();
                                    }else {
                                        Intent main = new Intent(Login.this, CallDispo.class);
                                        main.putExtra("leadid", resobject.has("lead_id")?resobject.getString("lead_id"):"25601633");
                                        main.putExtra("mobileno", resobject.has("phone_number")?resobject.getString("phone_number"):"9047699053");
                                        main.putExtra("dial", "false");
                                        main.putExtra("crm_name", resobject.getString("crm_id"));
                                        startActivity(main);
                                        finish();
                                    }
                                } else {

                                    if (resobject.has("callmode")&&resobject.getString("callmode").equalsIgnoreCase("preview")) {
                                        Intent main = new Intent(Login.this, NewModel.class);
                                        startActivity(main);
                                        finish();
                                    } else if (resobject.has("callmode")&&resobject.getString("callmode").equalsIgnoreCase("progressive")) {
                                        Intent main = new Intent(Login.this, ProgressiveCall.class);
                                        startActivity(main);
                                        finish();
                                    }else if (resobject.has("callmode")&&resobject.getString("callmode").equalsIgnoreCase("predictive")) {
                                        Intent main = new Intent(Login.this, ProgressiveCall.class);
                                        startActivity(main);
                                        finish();
                                    }
                                }
                                // GetStatus();
                            } else if (resobject.getString("Status").equalsIgnoreCase("-1")) {
                                alert.build(resobject.getString("statusdesc"));
                            } else {
                                alert.build(resobject.getString("statusdesc"));
                            }
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }else{

                }
            }
        });
        androidx.appcompat.app.AlertDialog alert = builder.create();
        alert.setCancelable(false);
        alert.show();
    }

    private void CallDailer(String fromno, String CompanyId) {
        Intent main = new Intent(Login.this, CallActivity.class);
        main.putExtra("fromno", fromno);
        main.putExtra("CompanyId", CompanyId);
        startActivity(main);
        finish();
    }
    private void CallReady() {
       /* String device_id = Settings.Secure.getString(getContentResolver(),
                Settings.Secure.ANDROID_ID);*/
        WifiManager manager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        WifiInfo info = manager.getConnectionInfo();
        String macAddress = info.getMacAddress();
        Util.saveData("BREAK", "", getApplicationContext());

        try {
            Log.e("Password", EdPassWord.getEditableText().toString());
            JSONObject params = new JSONObject();

            params.put("MailId", EdUserName.getEditableText().toString());
            params.put("password", ShaUtilss.encryptPassword(EdPassWord.getEditableText().toString()));
            // params.put("station", "");
            params.put("DeviceModel", Build.MODEL);
            params.put("DeviceId", Build.ID);
            params.put("Brand", Build.BRAND);
            params.put("Version", Build.VERSION.RELEASE);
            params.put("Android_ID", strAndroid_id);
            params.put("PhoneCount", strCount);
            params.put("IMEI", strIMEI);
            params.put("OperatorName", strOperator);
            params.put("Country", strCountry);
            params.put("SerialNumber", strSerial);

            params.put("NetworkType", stNWType);
            params.put("DataNetworkType", stDataNWType);
            params.put("InstalledApps", /*strInstalledApps*/"");
            params.put("WifiId", strWifiId);
            params.put("deviceinfo", macAddress);
            params.put("Appversion", getString(R.string.app_version));
            params.put("ServiceState", strState);

            Log.e("LOGIN ", LOGINNew + "\n" + params.toString());
            CallApi.postResponseNonHeader(Login.this, params.toString(), LOGINNew, new VolleyResponseListener() {
                @Override
                public void onError(String message) {
                    Util.Logcat.e("CallReady onError" + message);
                    if (message.contains("TimeoutError")) {
                        alert.build("Time out");
                    } else if (message.contains("Connection Reset")) {
                        alert.build("Connection reset");
                    } else {
                        alert.build("Server Error");
                    }
                }

                @Override
                public void onResponse(JSONObject response) {
                    Util.Logcat.e("LOGIN 2 onResponse" + response);
                    try {
                        JSONObject resobject = response;
                        //LI000

                        if(resobject.getString("Status").equals("0"))
                        {
                            String strDomain = resobject.getString("Domain").replaceAll("\\\\","");
                            String Description = resobject.getString("Description");
                            String AgentName = resobject.getString("AgentName");
                            Util.saveData("Domain", strDomain+"/", getApplicationContext());
                            Util.saveData("AgentName", AgentName, getApplicationContext());

                        } else {
                        alert.build(resobject.getString("Description"));
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
    private void callSecondaryApi() {
       /* String device_id = Settings.Secure.getString(getContentResolver(), agent_4(preview),5,6(predictive)
                Settings.Secure.ANDROID_ID);*/
        WifiManager manager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        WifiInfo info = manager.getConnectionInfo();
        String macAddress = info.getMacAddress();
//


        try {
            Log.e("Password", EdPassWord.getEditableText().toString());
            JSONObject params = new JSONObject();

            params.put("MailId", EdUserName.getEditableText().toString());
            params.put("password", ShaUtilss.encryptPassword(EdPassWord.getEditableText().toString()));
            // params.put("station", "");
            params.put("DeviceModel", Build.MODEL);
            params.put("DeviceId", Build.ID);
            params.put("Brand", Build.BRAND);
            params.put("Version", Build.VERSION.RELEASE);
            params.put("Android_ID", strAndroid_id);
            params.put("PhoneCount", strCount);
            params.put("IMEI", strIMEI);
            params.put("OperatorName", strOperator);
            params.put("Country", strCountry);
            params.put("SerialNumber", strSerial);
            params.put("ServiceState", /*strState*/"");
            params.put("NetworkType", stNWType);
            params.put("DataNetworkType", stDataNWType);
            params.put("InstalledApps", strInstalledApps);
            params.put("WifiId", strWifiId);
            params.put("deviceinfo", macAddress);
            params.put("Appversion", getString(R.string.app_version));


            Log.e("LOGIN ", MobileAPPLogin + "\n" + params.toString());
            CallApi.postResponseNonHeader(Login.this, params.toString(), MobileAPPLogin, new VolleyResponseListener() {
                @Override
                public void onError(String message) {
                    Util.Logcat.e("CallReady onError" + message);
                    if (message.contains("TimeoutError")) {
                        alert.build("Time out");
                    } else if (message.contains("Connection Reset")) {
                        alert.build("Connection reset");
                    } else {
                        alert.build("Server Error");
                    }
                }

                @Override
                public void onResponse(JSONObject response) {
                    Util.Logcat.e("LOGIN 2 onResponse" + response);
                    try {
                        JSONObject resobject = response;
                        if (resobject.getString("Status").equalsIgnoreCase("LI000") || resobject.getString("Status").equalsIgnoreCase("2")){
                        if(resobject.isNull("BlockedApps") || resobject.getString("BlockedApps").equals("") || resobject.getString("BlockedApps").isEmpty() || resobject.getString("BlockedApps").length() ==0)
                        {
                            if (chkbox.isChecked() == true) {
                                Util.saveData("username", EdUserName.getEditableText().toString(), getApplicationContext());
                                Util.saveData("password", EdPassWord.getEditableText().toString(), getApplicationContext());
                            }
                            if(resobject.has("pwdstatus") && resobject.getString("pwdstatus").equals("0"))
                            {

                                Util.saveData("AgentName", EdUserName.getEditableText().toString(), getApplicationContext());
                                Util.saveData("JWTToken",resobject.getString("Token"), getApplicationContext());
                                EdUserName.setText("");
                                EdPassWord.setText("");
                                if(!resobject.getString("callmode").equalsIgnoreCase("predictive"))
                                    startActivity(new Intent(Login.this,ChangePassword.class).putExtra("isFirstLogin","yes"));
                                else
                                    Logout();
                            }else {

                                if (resobject.getString("Status").equalsIgnoreCase("LI000")) {
                                    if (chkbox.isChecked() == true) {
                                        Util.saveData("username", EdUserName.getEditableText().toString(), getApplicationContext());
                                        Util.saveData("password", EdPassWord.getEditableText().toString(), getApplicationContext());
                                    }

                                    Toast.makeText(Login.this, resobject.getString("statusdesc"), Toast.LENGTH_LONG);

                                    Util.saveData("JWTToken",resobject.getString("Token"), getApplicationContext());
                                    Util.saveData("AgentName", resobject.getString("AgentId"), getApplicationContext());
                                    Util.saveData("mobile_no", resobject.getString("Mobile"), getApplicationContext());
                                    Util.saveData("crm_id", resobject.getString("crm_id"), getApplicationContext());
                                    Util.saveData("callmode", resobject.getString("callmode"), getApplicationContext());
                                    Util.saveData("process_name", resobject.getString("process_name"), getApplicationContext());
                                    shareData();
                                    scheduleJob();
                                    if (resobject.getString("callmode").equalsIgnoreCase("preview")) {
                                        Intent main = new Intent(Login.this, NewModel.class);
                                        startActivity(main);
                                        finish();
                                    } else if (resobject.getString("callmode").equalsIgnoreCase("progressive")) {
                                        Intent main = new Intent(Login.this, ProgressiveCall.class);
                                        startActivity(main);
                                        finish();
                                    } else if (resobject.has("callmode")&&resobject.getString("callmode").equalsIgnoreCase("predictive")) {
                                        Intent main = new Intent(Login.this, PredictiveCall.class);
                                        startActivity(main);
                                        finish();
                                    }
                                } else if (resobject.getString("Status").equalsIgnoreCase("2")) {
                                    if (chkbox.isChecked() == true) {
                                        Util.saveData("username",EdUserName.getEditableText().toString(), getApplicationContext());
                                        Util.saveData("password", EdPassWord.getEditableText().toString(), getApplicationContext());
                                    }
                                    Util.saveData("JWTToken",resobject.getString("Token"), getApplicationContext());
                                    Util.saveData("AgentName", resobject.getString("AgentId"), getApplicationContext());
                                    Util.saveData("mobile_no", resobject.getString("Mobile"), getApplicationContext());
                                    Util.saveData("crm_id", resobject.getString("crm_id"), getApplicationContext());
                                    Util.saveData("callmode", resobject.getString("callmode"), getApplicationContext());
                                    Util.saveData("process_name", resobject.getString("process_name"), getApplicationContext());
                                    shareData();
                                    scheduleJob();
                                    if (resobject.has("call_status")&&resobject.getString("call_status").equalsIgnoreCase("ONCALL")) {
                                        //GetPreviewCall(leadid, mobileno);
                                        if (resobject.has("lead_id")&&resobject.getString("lead_id").isEmpty() && resobject.getString("phone_number").isEmpty()) {
                                            alert.build("Your user id is locked. Please contact your supervisor to force logout");
                                        }else if (resobject.has("callmode")&&resobject.getString("callmode").equalsIgnoreCase("progressive")) {
                                            Intent main = new Intent(Login.this, ProgressiveCall.class);
                                            startActivity(main);
                                            finish();
                                        }else if (resobject.has("callmode")&&resobject.getString("callmode").equalsIgnoreCase("predictive")) {
                                            Intent main = new Intent(Login.this, PredictiveCall.class);
                                            startActivity(main);
                                            finish();
                                        } else {
                                            Intent main = new Intent(Login.this, CallDispo.class);
                                            main.putExtra("leadid", resobject.getString("lead_id"));
                                            main.putExtra("mobileno", resobject.getString("phone_number"));
                                            main.putExtra("dial", "false");
                                            main.putExtra("crm_name", resobject.getString("crm_id"));
                                            startActivity(main);
                                            finish();
                                        }
                                    } else if (resobject.has("call_status")&&resobject.getString("call_status").equalsIgnoreCase("WRAPUP")) {
                                        //GetPreviewCall(leadid, mobileno);
                                        if (resobject.has("lead_id")&&resobject.getString("lead_id").isEmpty() && resobject.getString("phone_number").isEmpty()) {
                                            alert.build("Your user id is locked. Please contact your supervisor to force logout");
                                        }else if (resobject.has("callmode")&&resobject.getString("callmode").equalsIgnoreCase("progressive")) {
                                            Intent main = new Intent(Login.this, ProgressiveCall.class);
                                            startActivity(main);
                                            finish();
                                        } else if (resobject.has("callmode")&&resobject.getString("callmode").equalsIgnoreCase("predictive")) {
                                            Intent main = new Intent(Login.this, PredictiveCall.class);
                                            startActivity(main);
                                            finish();
                                        } else {

                                            Intent main = new Intent(Login.this, CallDispo.class);
                                            main.putExtra("leadid", resobject.getString("lead_id"));
                                            main.putExtra("mobileno", resobject.getString("phone_number"));
                                            main.putExtra("dial", "false");
                                            main.putExtra("crm_name", resobject.getString("crm_id"));
                                            startActivity(main);
                                            finish();

                                        }
                                    } else if (resobject.has("call_status")&&resobject.getString("call_status").equalsIgnoreCase("MISSED")) {
                                        //GetPreviewCall(leadid, mobileno);
                                        if (resobject.has("lead_id")&&resobject.getString("lead_id").isEmpty() && resobject.getString("phone_number").isEmpty()) {
                                            alert.build("Your user id is locked. Please contact your supervisor to force logout");
                                        }else if (resobject.has("callmode")&&resobject.getString("callmode").equalsIgnoreCase("progressive")) {
                                            Intent main = new Intent(Login.this, ProgressiveCall.class);
                                            startActivity(main);
                                            finish();
                                        } else if (resobject.has("callmode")&&resobject.getString("callmode").equalsIgnoreCase("predictive")) {
                                            Intent main = new Intent(Login.this, PredictiveCall.class);
                                            startActivity(main);
                                            finish();
                                        } else {
                                            Intent main = new Intent(Login.this, CallDispo.class);
                                            main.putExtra("leadid", resobject.getString("lead_id"));
                                            main.putExtra("mobileno", resobject.getString("phone_number"));
                                            main.putExtra("dial", "false");
                                            main.putExtra("crm_name", resobject.getString("crm_id"));
                                            startActivity(main);
                                            finish();
                                        }
                                    } else if (resobject.has("call_status")&&resobject.getString("call_status").equalsIgnoreCase("DIALING")) {
                                        //GetPreviewCall(leadid, mobileno);
                                        if (resobject.has("lead_id")&&resobject.getString("lead_id").isEmpty() && resobject.getString("phone_number").isEmpty()) {
                                            alert.build("Your user id is locked. Please contact your supervisor to force logout");
                                        }else if (resobject.has("callmode")&&resobject.getString("callmode").equalsIgnoreCase("progressive")) {
                                            Intent main = new Intent(Login.this, ProgressiveCall.class);
                                            startActivity(main);
                                            finish();
                                        } else if (resobject.has("callmode")&&resobject.getString("callmode").equalsIgnoreCase("predictive")) {
                                            Intent main = new Intent(Login.this, PredictiveCall.class);
                                            startActivity(main);
                                            finish();
                                        } else {
                                            Intent main = new Intent(Login.this, CallDispo.class);
                                            main.putExtra("leadid", resobject.getString("lead_id"));
                                            main.putExtra("mobileno", resobject.getString("phone_number"));
                                            main.putExtra("dial", "false");
                                            main.putExtra("crm_name", resobject.getString("crm_id"));
                                            startActivity(main);
                                            finish();
                                        }
                                    } else if (resobject.has("call_status")&&resobject.getString("call_status").equalsIgnoreCase("RINGING")) {
                                        //GetPreviewCall(leadid, mobileno);
                                        if (resobject.has("lead_id")&&resobject.getString("lead_id").isEmpty() && resobject.getString("phone_number").isEmpty()) {
                                            alert.build("Your user id is locked. Please contact your supervisor to force logout");
                                        }else if (resobject.has("callmode")&&resobject.getString("callmode").equalsIgnoreCase("progressive")) {
                                            Intent main = new Intent(Login.this, ProgressiveCall.class);
                                            startActivity(main);
                                            finish();
                                        } else if (resobject.has("callmode")&&resobject.getString("callmode").equalsIgnoreCase("predictive")) {
                                            Intent main = new Intent(Login.this, PredictiveCall.class);
                                            startActivity(main);
                                            finish();
                                        } else {
                                            Intent main = new Intent(Login.this, CallDispo.class);
                                            main.putExtra("leadid", resobject.has("lead_id")?resobject.getString("lead_id"):"25601633");
                                            main.putExtra("mobileno", resobject.has("phone_number")?resobject.getString("phone_number"):"9047699053");
                                            main.putExtra("dial", "false");
                                            main.putExtra("crm_name", resobject.getString("crm_id"));
                                            startActivity(main);
                                            finish();
                                        }
                                    } else {

                                        if (resobject.has("callmode")&&resobject.getString("callmode").equalsIgnoreCase("preview")) {
                                            Intent main = new Intent(Login.this, NewModel.class);
                                            startActivity(main);
                                            finish();
                                        } else if (resobject.has("callmode")&&resobject.getString("callmode").equalsIgnoreCase("progressive")) {
                                            Intent main = new Intent(Login.this, ProgressiveCall.class);
                                            startActivity(main);
                                            finish();
                                        } else if (resobject.has("callmode")&&resobject.getString("callmode").equalsIgnoreCase("predictive")) {
                                            Intent main = new Intent(Login.this, PredictiveCall.class);
                                            startActivity(main);
                                            finish();
                                        }

                                    }
                                    // GetStatus();
                                } else if (resobject.getString("Status").equalsIgnoreCase("-1")) {
                                    alert.build(resobject.getString("statusdesc"));
                                } else {
                                    alert.build(resobject.getString("statusdesc"));
                                }
                            }
                        }else{
                            if(resobject.getString("BlockFlag").equalsIgnoreCase("Y"))
                            {
                                showAlert(resobject,"Allow");


                            }else if((resobject.getString("BlockFlag").equalsIgnoreCase("N")))
                            {
                                showAlert(resobject,"Block");


                            }
                        }
                        } else {
                            alert.build(resobject.isNull("statusdesc")?resobject.getString("Description"):resobject.getString("statusdesc"));
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

    @RequiresApi(api = Build.VERSION_CODES.M)
    protected void askPermissions() {
        String[] permissions = {
                "android.permission.ACCESS_WIFI_STATE ",
                "android.permission.ACCESS_NETWORK_STATE ",
                "android.permission.READ_EXTERNAL_STORAGE ",
                "android.permission.WRITE_EXTERNAL_STORAGE ",
        };
        int requestCode = 200;
        requestPermissions(permissions, requestCode);
    }

    protected boolean shouldAskPermissions() {
        return (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP_MR1);
    }

   /* @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }*/



    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private void decodeJWS(String jwsString) {

        byte[] json = Base64.decode(jwsString.split("[.]")[1],Base64.DEFAULT);
        strNetSafety= new String(json, StandardCharsets.UTF_8);
        Log.e("Result",strNetSafety);

       /* Gson gson = new Gson();
        JWS jws = gson.fromJson(text, JWS.class);*/

    }

    /*private void verifyOnline(final String jws) {

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(GOOGLE_API_VERIFY_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        RetrofitInterface retrofitInterface = retrofit.create(RetrofitInterface.class);

        JWSRequest jwsRequest = new JWSRequest();
        jwsRequest.setSignedAttestation(jws);
        Call<com.example.sipappmerge.retrofit.Response> responseCall = retrofitInterface.getResult(jwsRequest, getString(R.string.api_key));

        responseCall.enqueue(new Callback<com.example.sipappmerge.retrofit.Response>() {
            @Override
            public void onResponse(Call<com.example.sipappmerge.retrofit.Response> call, retrofit2.Response<com.example.sipappmerge.retrofit.Response> response) {

                boolean result = response.body().isValidSignature();

                if (result) {

                    decodeJWS(jws);

                } else {
                    strNetSafety = "Verification Error !";
                    Toast.makeText(Login.this, "Verification Error !", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<com.example.sipappmerge.retrofit.Response> call, Throwable t) {

                Log.d("MainActivity", "onFailure: "+t.getLocalizedMessage());
                Toast.makeText(Login.this, t.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }*/
    private byte[] getRequestNonce() {

        String data = String.valueOf(System.currentTimeMillis());

        ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
        byte[] bytes = new byte[24];
        Random random = new Random();
        random.nextBytes(bytes);
        try {
            byteStream.write(bytes);
            byteStream.write(data.getBytes());
        } catch (IOException e) {
            return null;
        }

        return byteStream.toByteArray();
    }
    private void shareData() {
        try {
            JSONObject params = new JSONObject();

            params.put("AgentId", Util.getData("AgentName", getApplicationContext()));
            params.put("SafetyResponse", strNetSafety);
            Log.e("ShareNetSafety", BASE+"Dialer/NetSafety" + "\n" + params.toString());

            CallApi.postResponse(Login.this, params.toString(), BASE + "Dialer/NetSafety", new VolleyResponseListener() {

                @Override
                public void onError(String message) {
                    Log.e("ShareNetSafety onError",message);
                }

                @Override
                public void onResponse(JSONObject response) {
                    Log.e("ShareNetSafety Response" ,response.toString());

                }
            });

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private class AsyncTaskRequestNetSafety extends AsyncTask<String, String, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();

        }
        @Override
        protected String doInBackground(String... strings) {
            SafetyNet.getClient(Login.this).attest( getRequestNonce(), "AIzaSyAkRwNFqYcLtv0ULu8YlcGIwCjMx1Ox7m0")
                    .addOnSuccessListener(Login.this,
                            new OnSuccessListener<SafetyNetApi.AttestationResponse>() {
                                @Override
                                public void onSuccess(SafetyNetApi.AttestationResponse response) {
                                    // Indicates communication with the service was successful.
                                    // Use response.getJwsResult() to get the result data.
                                    Log.e("Response",response.getJwsResult());

                                    decodeJWS(response.getJwsResult());
                                   // verifyOnline(response.getJwsResult());


                                }
                            })
                    .addOnFailureListener(Login.this, new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            // An error occurred while communicating with the service.
                            if (e instanceof ApiException) {
                                // An error with the Google Play services API contains some
                                // additional details.
                                ApiException apiException = (ApiException) e;
                                // You can retrieve the status code using the
                                // apiException.getStatusCode() method.
                            } else {
                                // A different, unknown type of error occurred.

                            }
                        }
                    });
            return "";
        }
        @Override
        protected void onPostExecute(String bitmap) {
            super.onPostExecute(bitmap);

        }
    }

    @Override
    public void onStart() {
        super.onStart();

    }

    @Override
    public void onStop() {
        super.onStop();

    }

    @Override
    public void onBackPressed() {
        androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(Login.this,     R.style.alertDialog);
        // builder.setTitle(title);
        builder.setMessage("Are you sure to exit from the App?");
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                finishAffinity();
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        androidx.appcompat.app.AlertDialog alert = builder.create();
        alert.setCancelable(false);
        alert.show();
    }
    private void Logout() {

        try {
            JSONObject params = new JSONObject();
            params.put("agent_id",  Util.getData("AgentName", getApplicationContext()));
            params.put("user_type", "Agent");
            params.put("user", Util.getData("AgentName", getApplicationContext()));
            Util.Logcat.e("API" + LOGOUTUSER+", params:"+params.toString());
            CallApi.postResponse(Login.this, params.toString(), LOGOUTUSER, new VolleyResponseListener() {
                @Override
                public void onError(String message) {
                    Util.Logcat.e("onError" + message);
                    if (message.contains("TimeoutError")) {
                        alert.build("Time out");
                    } else if (message.contains("Connection Reset")) {
                        alert.build("Connection reset");
                    } else {
                        alert.build("Server Error");
                    }
                }

                @Override
                public void onResponse(JSONObject response) {
                    Util.Logcat.e("LOGOUT" + response);
                    try {
                        String  resobjectString = response.getString("ResponseData");
                        if (resobjectString.contains("LO000")) {
                            //Util.saveData("BREAK", "", getApplicationContext());
                            startActivity(new Intent(Login.this,ChangePassword.class).putExtra("isFirstLogin","yes"));
                            finish();

                        } else {
                            alert.build("Error in Logout");
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
}
