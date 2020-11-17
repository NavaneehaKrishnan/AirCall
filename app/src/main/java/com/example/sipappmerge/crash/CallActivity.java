package com.example.sipappmerge.crash;

import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.JsonObjectRequest;
import com.example.sipappmerge.Utils.CallApi;
import com.example.sipappmerge.Utils.CommonAlertDialog;
import com.example.sipappmerge.Merge.Login;
import com.example.sipappmerge.Utils.MyApplication;
import com.example.sipappmerge.R;
import com.example.sipappmerge.Utils.Util;
import com.example.sipappmerge.Utils.VolleyResponseListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;

import static com.example.sipappmerge.Utils.Util.AppErrorLog;
import static com.example.sipappmerge.Utils.Util.BASE;


public class CallActivity extends AppCompatActivity {

    EditText EdMobileNo;
    Button BtnCall;
    CommonAlertDialog alert;
    private static final int LOGOUT = 5;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.call_activity);
        EdMobileNo=findViewById(R.id.mobileno);
        BtnCall=findViewById(R.id.callbtn);
        alert = new CommonAlertDialog(this);
        final String fromno=getIntent().getStringExtra("fromno");
        final String CompanyId=getIntent().getStringExtra("CompanyId");
        BtnCall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                InitateCall(fromno,CompanyId);
            }
        });
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

                    try {
                        // UpdateError(stacktrace);
                        UpdateError(ex.toString());
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } finally {
                    defHandler.uncaughtException(t, ex);
                }
            }
        });

    }

    private void InitateCall(String from_mobileno,String CompanyId) {

        try {
            JSONObject params = new JSONObject();
            params.put("from_mobileno", from_mobileno);
            params.put("destination_mobileno", EdMobileNo.getEditableText().toString());
            params.put("company_id", CompanyId);


            CallApi.postResponse(CallActivity.this, params.toString(), Util.getData("Domain", getApplicationContext()), new VolleyResponseListener() {
                @Override
                public void onError(String message) {
                    Util.Logcat.e("onError" + message);
                    if (message.contains("TimeoutError")) {
                        alert.build("Time out");
                    } else if (message.contains("Connection Reset")) {
                        alert.build("Connection reset");
                    } else  if(message.contains("success")){
                        alert.build("Success");
                        EdMobileNo.setText("");
                    }else {
                        alert.build("Server Error");//
                    }
                }

                @Override
                public void onResponse(JSONObject response) {
                    Util.Logcat.e("onResponse" + response);
                    try {
                        JSONObject resobject = response;

                        if (resobject.getString("Status").equalsIgnoreCase("0")) {
                            //  alert.build(resobject.getString("MESSAGE"));
                            Toast.makeText(CallActivity.this, resobject.getString("Description"), Toast.LENGTH_LONG);

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
    public boolean onCreateOptionsMenu(Menu menu) {
        //   menu.add(0, CALL_ADDRESS, 0, "Call someone");
        // menu.add(0, SET_AUTH_INFO, 0, "Edit your SIP Info.");
        //  menu.add(0, HANG_UP, 0, "End Current Call.");
        menu.add(0, LOGOUT, 0, "Logout");
        return true;

    }


    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {

            case LOGOUT:

                Intent main = new Intent(CallActivity.this, Login.class);
                startActivity(main);
                finish();

                break;

        }
        return true;
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
            params.put("DeviceId", "AIRCRM" + device_id);
            params.put("ErrorInfo", error);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        JsonObjectRequest req = new JsonObjectRequest(Request.Method.POST, AppErrorLog, new JSONObject(params.toString()),
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {


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
}
