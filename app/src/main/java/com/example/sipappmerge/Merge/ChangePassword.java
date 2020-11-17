package com.example.sipappmerge.Merge;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.example.sipappmerge.R;
import com.example.sipappmerge.Utils.CallApi;
import com.example.sipappmerge.Utils.CommonAlertDialog;
import com.example.sipappmerge.Utils.ShaUtilss;
import com.example.sipappmerge.Utils.Util;
import com.example.sipappmerge.Utils.VolleyResponseListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static android.provider.ContactsContract.CommonDataKinds.Website.URL;

import static com.example.sipappmerge.Utils.Util.LOGOUTUSER;
import static com.example.sipappmerge.Utils.Util.UpdatePassword;

public class ChangePassword extends BaseActivity implements View.OnClickListener {

    private String isFirstLogin = "";
    private String strCNewPassword = "";
    private EditText password, confirmPassword;
    private Button change_password;
    private RelativeLayout rlBack;
    private CommonAlertDialog alert;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);

        init();
    }

    private void init() {
        rlBack = findViewById(R.id.rlBack);
        password = findViewById(R.id.password);
        confirmPassword = findViewById(R.id.confirmPassword);
        change_password = findViewById(R.id.change_password);
        isFirstLogin = getIntent().getStringExtra("isFirstLogin");
        alert = new CommonAlertDialog(this);
        change_password.setOnClickListener(this);
        rlBack.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId())
        {
            case R.id.change_password:
                if(isValid())
                    changePassword();
                break;

            case R.id.rlBack:
               finish();
                break;
        }
    }

    private void changePassword() {

        Log.e("Encrypted", ShaUtilss.encryptPassword(password.getEditableText().toString()));
            try {
                JSONObject params = new JSONObject();
                params.put("NewPassword", ShaUtilss.encryptPassword(password.getEditableText().toString()));
                params.put("AgentId", Util.getData("AgentName", getApplicationContext()));
                Log.e("changePassword", UpdatePassword+ "\n" + params.toString());

                CallApi.postResponse(ChangePassword.this, params.toString(), UpdatePassword, new VolleyResponseListener() {

                    @Override
                    public void onError(String message) {
                        Util.Logcat.e("changePassword onError" + message);
                    if (message.contains("TimeoutError")) {
                       // alert.build("Time out");
                       } else if (message.contains("Connection Reset")) {
                        alert.build("Connection reset");
                    } else {
                        alert.build("Server Error");//
                    }

                    }

                    @Override
                    public void onResponse(JSONObject response) {
                        Util.Logcat.e("changePassword Response" + response);
                        try {
                            if (response.getString("Status").equalsIgnoreCase("0")) {

                                Toast.makeText(ChangePassword.this, response.getString("Description"), Toast.LENGTH_LONG);
                                  /*  JSONObject resobject = response;
                                    JSONArray array = new JSONArray(resobject.getString("DATA"));
                                    JSONObject  object = new JSONObject(array.getJSONObject(0).toString());
                                    Map<String, String> map = new HashMap<String, String>();;
                                    Iterator<?> iter = object.keys();
                                    while (iter.hasNext()) {
                                        String key = (String) iter.next();
                                        String value = object.getString(key);

                                        map.put(key, value);
                                    }*/
                                    if (response.getString("Description").equalsIgnoreCase("Sucess")) {
                                        if(isFirstLogin.equals("yes"))
                                            clearSession();
                                        else
                                            Logout();
                                    }

                            }else
                            {
                                Toast.makeText(ChangePassword.this, response.getString("Description"), Toast.LENGTH_LONG);
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


    private void Logout() {

        try {
            JSONObject params = new JSONObject();

            params.put("user_type", "Agent");
            params.put("Agent", "");
            params.put("refno", "");
            params.put("user", Util.getData("AgentName", getApplicationContext()));
            params.put("password", "");
            params.put("station", "");
            params.put("agent_id",  Util.getData("AgentName", getApplicationContext()));
            Log.e("LOGOUT", LOGOUTUSER + "\n" + params.toString());
            CallApi.postResponse(ChangePassword.this, params.toString(), LOGOUTUSER, new VolleyResponseListener() {
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
                            clearSession();
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

    private void clearSession() {
        Util.saveData("username", "", getApplicationContext());
        Util.saveData("password", "", getApplicationContext());
        Util.saveData("AgentName", "", getApplicationContext());
        Util.saveData("mobile_no", "", getApplicationContext());
        Util.saveData("crm_id", "", getApplicationContext());
        Util.saveData("callmode", "", getApplicationContext());
        Util.saveData("process_name", "", getApplicationContext());
        Toast.makeText(ChangePassword.this,"User Successfully Logged Out.", Toast.LENGTH_LONG);
        Intent main = new Intent(ChangePassword.this, Login.class);
        main.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(main);
        finish();
    }

    private boolean isValid() {
        Log.e("Password",password.getText().toString());
        if(password.getText().toString().isEmpty()) {
            alert.build("Enter Password");
            return false;
        }
        else if(password.getText().toString().length()< 8 || !isValidPassword(password.getText().toString())){
            alert.build("Entered Password should be Eight character and use at least one special character and at least one capital letter and any one number");
            return false;
        }else if(confirmPassword.getText().toString().isEmpty()){
            alert.build("Enter Confirm Password");
            return false;
        }else if(!password.getText().toString().equals(confirmPassword.getText().toString())){
            alert.build("Password and Confirm password should be same");
            return false;
        }
        return true;
    }
    public static boolean isValidPassword(final String password) {

        Pattern pattern;
        Matcher matcher;
        final String PASSWORD_PATTERN = "^(?=.*[0-9])(?=.*[A-Z])(?=.*[@#$%^&+=!])(?=\\S+$).{4,}$";
        pattern = Pattern.compile(PASSWORD_PATTERN);
        matcher = pattern.matcher(password);

        return matcher.matches();

    }
}
