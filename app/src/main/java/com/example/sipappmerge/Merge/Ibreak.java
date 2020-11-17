package com.example.sipappmerge.Merge;

import android.content.DialogInterface;
import android.content.Intent;
import android.media.Image;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.sipappmerge.R;
import com.example.sipappmerge.Utils.CallApi;
import com.example.sipappmerge.Utils.CommonAlertDialog;
import com.example.sipappmerge.Utils.Util;
import com.example.sipappmerge.Utils.VolleyResponseListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import static com.example.sipappmerge.Utils.Util.LOGOUTUSER;
import static com.example.sipappmerge.Utils.Util.Ready;

public class Ibreak extends BaseActivity {

    TextView TxtBreak;
    Button BtnReady;
    CommonAlertDialog alert;
    ImageView imgLogout;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ibreak);

        alert = new CommonAlertDialog(this);
        TxtBreak = findViewById(R.id.txtbreak);
        BtnReady = findViewById(R.id.ready_btn);
        imgLogout = findViewById(R.id.imgLogout);

        if (getIntent().getStringExtra("break").equalsIgnoreCase("true")) {
            String hai = "You are in " + Util.getData("BREAK", getApplicationContext()) + " mode. Please click ready button below to release your break";
            TxtBreak.setText(hai);
            BtnReady.setText("Ready");

        } else {

            String hai = "Your progressive list empty. Please contact your supervisor";
            TxtBreak.setText(hai);
            BtnReady.setText("Reload");

        }

        BtnReady.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (BtnReady.getText().toString().equalsIgnoreCase("Ready")) {
                    ReleaseBreak("Ready");
                } else {
                    Intent ibreak = new Intent(Ibreak.this, ProgressiveCall.class);
                    startActivity(ibreak);
                    finish();
                }
            }
        });
        imgLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(Ibreak.this, R.style.alertDialog);
                // builder.setTitle(title);
                builder.setMessage("Are You Sure to Logout?");
                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        Logout();

                    }
                });
                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();

                    }
                });
                AlertDialog alert = builder.create();
                alert.setCancelable(false);
                alert.show();

            }
        });

    }

    private void ReleaseBreak(String action) {

        try {
            JSONObject params = new JSONObject();

            params.put("Break", Util.getData("BREAK", getApplicationContext()));
            params.put("agent_id", Util.getData("AgentName", getApplicationContext()));

            CallApi.postResponse(Ibreak.this, params.toString(), Ready, new VolleyResponseListener() {
                @Override
                public void onError(String message) {
                    Util.Logcat.e("onError" + message);
                }

                @Override
                public void onResponse(JSONObject response) {
                    Util.Logcat.e("RELEASE BREAK" + response);
                    try {

                        if(response.getString("Status").equals("1")&& response.getString("StatusDesc").equals("Sucess"))
                        {
                            String responseData = response.getString("ResponseData");
                            JSONArray jsonArray = new JSONArray(responseData);
                            if(jsonArray.length()>0)
                            {
                                if (jsonArray.getJSONObject(0).optString("Status").equalsIgnoreCase("0")) {
                                    Util.saveData("BREAK", "", getApplicationContext());
                                    Intent ibreak = new Intent(Ibreak.this, ProgressiveCall.class);
                                    startActivity(ibreak);
                                    finish();
                                } else {
                                    alert.build(jsonArray.getJSONObject(0).optString("Statusdesc"));
                                }
                            }
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

    @Override
    public void onBackPressed() {
        alert.build("Do logout to Exit App");
    }

    private void Logout()
    {
    try {
        JSONObject params = new JSONObject();
        params.put("agent_id",  Util.getData("AgentName", getApplicationContext()));
        params.put("user_type", "Agent");
        params.put("user", Util.getData("AgentName", getApplicationContext()));
        Util.Logcat.e("API" + LOGOUTUSER+", params:"+params.toString());
        CallApi.postResponse(Ibreak.this, params.toString(), LOGOUTUSER, new VolleyResponseListener() {
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
                        Util.saveData("username", "", getApplicationContext());
                        Util.saveData("password", "", getApplicationContext());
                        Util.saveData("AgentName", "", getApplicationContext());
                        Util.saveData("mobile_no", "", getApplicationContext());
                        Util.saveData("crm_id", "", getApplicationContext());
                        Util.saveData("callmode", "", getApplicationContext());
                        Util.saveData("process_name", "", getApplicationContext());
                        Toast.makeText(Ibreak.this,"User Successfully Logged Out.", Toast.LENGTH_LONG);
                        Intent main = new Intent(Ibreak.this, Login.class);
                        main.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(main);
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
