package com.example.sipappmerge.Backup;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.JsonObjectRequest;

import com.example.sipappmerge.Merge.Login;
import com.example.sipappmerge.Merge.ProgressiveCall;
import com.example.sipappmerge.R;
import com.example.sipappmerge.Utils.CallApi;
import com.example.sipappmerge.Utils.CommonAlertDialog;
import com.example.sipappmerge.Utils.MyApplication;
import com.example.sipappmerge.Utils.RadioAlertDialog;
import com.example.sipappmerge.Utils.Util;
import com.example.sipappmerge.Utils.VolleyResponseListener;
import com.example.sipappmerge.adapter.CallAdapterPreview;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.example.sipappmerge.Utils.Util.AppErrorLog;
import static com.example.sipappmerge.Utils.Util.ApplyBreak;
import static com.example.sipappmerge.Utils.Util.BASE;
import static com.example.sipappmerge.Utils.Util.Callcloseapi;
import static com.example.sipappmerge.Utils.Util.FetchPROGRESSIVECALLS;
import static com.example.sipappmerge.Utils.Util.GetBreak;
import static com.example.sipappmerge.Utils.Util.GetCustomerData;
import static com.example.sipappmerge.Utils.Util.GetDispo;
import static com.example.sipappmerge.Utils.Util.LOGOUTUSER;
import static com.example.sipappmerge.Utils.Util.recording;


public class ProgressiveCallEx extends AppCompatActivity implements View.OnClickListener {

    Spinner SpinDispo, SpinDispoSub;
    private HashMap<String, String> DataHashMap;
    private List<Map<String, String>> ListCollection;
    ListView listview;
    CallAdapterPreview adapter;
    List<String> spinnerlist, spinnerlistSub;
    String StrDispo = "", StrDispoSub = "";
    EditText edit_number, edit_text, edit_comments;
    CommonAlertDialog alert;
    String leadid, mobileno, crmname;
    Button BtnEndCall;
    JSONObject RESPONSE;
    ImageView BtnRefresh, imgdate, imgdatetime;
    boolean spinner = true;
    CheckBox chckLogout, chckBreak;
    LinearLayout lydate, lydatetime;
    TextView label, edit_date, edit_datetime;
    RelativeLayout lysubdispo;

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.call_progressive);

        if (recording) {
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE);
        }
        //  Thread.setDefaultUncaughtExceptionHandler(new CustomizedExceptionHandler(Environment.getExternalStorageDirectory() + "/ARM/"));
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
                        UpdateError(ex.toString());
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } finally {
                    defHandler.uncaughtException(t, ex);
                }
            }
        });

        listview = findViewById(R.id.collect_listview_call);
        SpinDispo = findViewById(R.id.dispo_spinner);
        SpinDispoSub = findViewById(R.id.sub_dispo_spinner);
        BtnEndCall = findViewById(R.id.end_call);
        BtnEndCall.setOnClickListener(this);
        BtnRefresh = findViewById(R.id.btn_refresh);
        chckLogout = findViewById(R.id.chckbox);
        chckBreak = findViewById(R.id.chk_break);

        chckBreak.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    chckLogout.setChecked(false);
                    LoadBreakPopup();
                }

            }
        });
        chckLogout.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    chckBreak.setChecked(false);
                }

            }
        });
        //new
        edit_number = findViewById(R.id.edit_number);
        edit_text = findViewById(R.id.edit_text);
        edit_date = findViewById(R.id.edit_date);
        edit_datetime = findViewById(R.id.edit_datetime);
        edit_comments = findViewById(R.id.comments);

        imgdate = findViewById(R.id.imgdate);
        imgdatetime = findViewById(R.id.imgdatetime);
        label = findViewById(R.id.label);

        lydate = findViewById(R.id.lydate);
        lydatetime = findViewById(R.id.lydatetime);
        lysubdispo = findViewById(R.id.lysubdispo);

        imgdate.setOnClickListener(this);
        imgdatetime.setOnClickListener(this);

        ListCollection = new ArrayList<Map<String, String>>();
        spinnerlist = new ArrayList<>();
        spinnerlist.add("Select");
        spinnerlistSub = new ArrayList<>();
        alert = new CommonAlertDialog(this);

        leadid = getIntent().getStringExtra("leadid");
        mobileno = getIntent().getStringExtra("mobileno");
        crmname = getIntent().getStringExtra("crm_name");

        Util.saveData("LEADID", leadid, getApplicationContext());
        Util.saveData("MOBILENO", mobileno, getApplicationContext());
        Util.saveData("CRMNAME", crmname, getApplicationContext());

        BtnRefresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                StartCall();
            }
        });

        SpinDispo.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            //@Override
            public void onItemSelected(AdapterView<?> parent, View arg1, int arg2, long arg3) {

                StrDispo = SpinDispo.getSelectedItem().toString();
                if (!StrDispo.equalsIgnoreCase("Select"))

                    Loadsubspinner(StrDispo);

            }

            public void onNothingSelected(AdapterView<?> arg0) {

            }
        });

        SpinDispoSub.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            //@Override
            public void onItemSelected(AdapterView<?> parent, View arg1, int arg2, long arg3) {
                StrDispoSub = SpinDispoSub.getSelectedItem().toString();
                Log.e("StrDispoSub", StrDispoSub);
                lydate.setVisibility(View.GONE);
                lydatetime.setVisibility(View.GONE);
                edit_number.setVisibility(View.GONE);
                edit_text.setVisibility(View.GONE);

                try {
                    JSONObject obj = RESPONSE.getJSONObject("subdispo");
                    JSONArray dispo = obj.getJSONArray(StrDispo);
                    Log.e("dispo", "" + dispo.toString());
                    for (int j = 0; j < dispo.length(); j++) {
                        JSONObject object = dispo.getJSONObject(j);
                        Log.e("final subname", "" + object.getString("subname"));
                        Log.e("final Type", "" + object.getString("Type"));
                        Log.e("final Label", "" + object.getString("Label"));
                        Log.e("final StrDispoSub", "" + StrDispoSub);
                        Log.e("final StrDispo", "" + StrDispo);
                        if (object.getString("subname").equalsIgnoreCase(StrDispoSub)) {

                            if (object.getString("Type").equalsIgnoreCase("DATE")) {
                                label.setText(object.getString("Label"));
                                Log.e("1", "DATE");
                                lydate.setVisibility(View.VISIBLE);
                                break;
                            } else if (object.getString("Type").equalsIgnoreCase("DATETIME")) {
                                label.setText(object.getString("Label"));
                                lydatetime.setVisibility(View.VISIBLE);
                                Log.e("2", "DATETIME");
                                break;
                            } else if (object.getString("Type").equalsIgnoreCase("NUMBER")) {
                                label.setText(object.getString("Label"));
                                edit_number.setVisibility(View.VISIBLE);
                                Log.e("3", "NUMBER");
                                break;
                            } else if (object.getString("Type").equalsIgnoreCase("TEXT")) {
                                label.setText(object.getString("Label"));
                                edit_text.setVisibility(View.VISIBLE);
                                Log.e("4", "TEXT");
                                break;
                            } else {
                                Log.e("5", "NOTHING");
                            }

                        }
                    }

                } catch (JSONException e) {

                }
            }

            public void onNothingSelected(AdapterView<?> arg0) {

            }
        });

        login("Login Success", "Start Calling");

    }

    private void Loadsubspinner(String st) {

        final List<String> spinnerlistSub = new ArrayList<>();

        try {
            JSONObject obj = RESPONSE.getJSONObject("subdispo");
            JSONArray dispo = obj.getJSONArray(st);
            for (int j = 0; j < dispo.length(); j++) {
                JSONObject object = dispo.getJSONObject(j);
                Log.e("subname1", "" + object.getString("subname"));
                spinnerlistSub.add(object.getString("subname"));
            }

            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<>
                            (ProgressiveCallEx.this, R.layout.spinner_textview,
                                    spinnerlistSub);
                    spinnerArrayAdapter.setDropDownViewResource(android.R.layout
                            .simple_spinner_dropdown_item);
                    SpinDispoSub.setAdapter(spinnerArrayAdapter);
                }
            });
        } catch (JSONException e) {

        }

    }

    private void LoadBreakPopup() {

        try {
            JSONObject params = new JSONObject();
           // params.put("action", "GetBreak");
            params.put("CRM_id", "");

            Log.e("BREAK", GetBreak + "\n" + params.toString());
            CallApi.postResponse(ProgressiveCallEx.this, params.toString(), GetBreak, new VolleyResponseListener() {
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
                    Util.Logcat.e("BREAK" + response);
                    try {
                        String data = response.getString("Data");
                        JSONObject dat = new JSONObject(data);
                        JSONArray st = dat.getJSONArray("Break");
                        // RadioAlertDialog alert = new RadioAlertDialog(ProgressiveCallEx.this);
                        build(st);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });

        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    public void build(JSONArray array) {
        Util.saveData("BREAK", "", getApplicationContext());
        final Dialog dialog = new Dialog(ProgressiveCallEx.this);
        //dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setTitle("Select Option");
        dialog.setContentView(R.layout.radiobutton_dialog);

        RadioGroup rg = dialog.findViewById(R.id.radio_group);
        Button BTN = dialog.findViewById(R.id.btn);
        BTN.setText("End Call");
        BTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Util.getData("BREAK", getApplicationContext()).equalsIgnoreCase("")) {
                    Toast.makeText(ProgressiveCallEx.this, "Select Option", Toast.LENGTH_SHORT).show();
                } else {
                    dialog.dismiss();
                    Log.e("safdsa", "sadA");
                    ReleaseBreak("ApplyBreak");
                }
            }
        });
        try {
            for (int i = 0; i < array.length(); i++) {
                RadioButton rb = new RadioButton(ProgressiveCallEx.this); // dynamically creating RadioButton and adding to RadioGroup.
                rb.setText(array.getString(i));
                rb.setPadding(5, 5, 5, 5);
                rb.setTextAppearance(ProgressiveCallEx.this, android.R.style.TextAppearance_Large);
                rg.addView(rb);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        dialog.show();

        rg.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                int childCount = group.getChildCount();
                for (int x = 0; x < childCount; x++) {
                    RadioButton btn = (RadioButton) group.getChildAt(x);
                    if (btn.getId() == checkedId) {
                        Util.saveData("BREAK", btn.getText().toString(), getApplicationContext());
                    }
                }
            }
        });
    }

    private void ReleaseBreak(String action) {

        try {
            JSONObject params = new JSONObject();

            params.put("Break", Util.getData("BREAK", getApplicationContext()));
            params.put("agent_id", Util.getData("AgentName", getApplicationContext()));
            Log.e("RELEASE BREAK",  "\n" + params.toString());
            CallApi.postResponse(ProgressiveCallEx.this, params.toString(), ApplyBreak, new VolleyResponseListener() {
                @Override
                public void onError(String message) {
                    Util.Logcat.e("onError" + message);
                }

                @Override
                public void onResponse(JSONObject response) {
                    Util.Logcat.e("RELEASE BREAK" + response);
                    try {
                        JSONObject resobject = response;
                        JSONArray array = new JSONArray(resobject.getString("DATA"));
                        Log.e("DATA", array.toString());
                        if (array.getJSONObject(0).getString("Status").equalsIgnoreCase("0")) {
                            Toast.makeText(ProgressiveCallEx.this, array.getJSONObject(0).getString("Statusdesc"), Toast.LENGTH_LONG).show();
                            EndCall();
                        } else {
                            Toast.makeText(ProgressiveCallEx.this, array.getJSONObject(0).getString("Statusdesc"), Toast.LENGTH_LONG).show();
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
    public void onClick(View v) {
        final Calendar c = Calendar.getInstance();
        Integer mYear = c.get(Calendar.YEAR);
        Integer mMonth = c.get(Calendar.MONTH);
        Integer mDay = c.get(Calendar.DAY_OF_MONTH);

        switch (v.getId()) {
            case R.id.end_call:
                if (edit_number.getVisibility() == View.VISIBLE && edit_number.getEditableText().toString().isEmpty()) {
                    alert.build("Enter " + label.getText().toString());
                } else if (edit_text.getVisibility() == View.VISIBLE && edit_text.getEditableText().toString().isEmpty()) {
                    alert.build("Enter " + label.getText().toString());
                } else if (lydate.getVisibility() == View.VISIBLE && edit_date.getText().toString().isEmpty()) {
                    alert.build("Enter " + label.getText().toString());
                } else if (lydatetime.getVisibility() == View.VISIBLE && edit_datetime.getText().toString().isEmpty()) {
                    alert.build("Enter " + label.getText().toString());
                } else {
                    EndCall();
                }
                break;
            case R.id.imgdate:
                DatePickerDialog datepicker = new DatePickerDialog(ProgressiveCallEx.this, R.style.DatePickerDialogTheme, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                        if (monthOfYear <= 8 && dayOfMonth > 9) {
                            String _data = dayOfMonth + "/" + "0" + (monthOfYear + 1) + "/" + year;
                            edit_date.setText(_data);
                        } else if (monthOfYear <= 8 && dayOfMonth <= 9) {
                            String _data = "0" + dayOfMonth + "/" + "0" + (monthOfYear + 1) + "/" + year;
                            edit_date.setText(_data);
                        } else {
                            if (dayOfMonth <= 9) {
                                String _data = "0" + dayOfMonth + "/" + (monthOfYear + 1) + "/" + year;
                                edit_date.setText(_data);
                            } else {
                                String _data = dayOfMonth + "/" + (monthOfYear + 1) + "/" + year;
                                edit_date.setText(_data);
                            }

                        }

                    }
                }, mYear, mMonth, mDay);
                datepicker.setCancelable(false);
                datepicker.show();
                datepicker.getDatePicker().setMinDate(System.currentTimeMillis() + 24 * 60 * 60 * 1000);
                break;
            case R.id.imgdatetime:

                DatePickerDialog datePickerDialog = new DatePickerDialog(ProgressiveCallEx.this, R.style.DatePickerDialogTheme, new DatePickerDialog.OnDateSetListener() {
                    @RequiresApi(api = Build.VERSION_CODES.N)
                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                        if (monthOfYear <= 8 && dayOfMonth > 9) {
                            String _data = dayOfMonth + "/" + "0" + (monthOfYear + 1) + "/" + year;
                            edit_datetime.setText(_data);
                        } else if (monthOfYear <= 8 && dayOfMonth <= 9) {
                            String _data = "0" + dayOfMonth + "/" + "0" + (monthOfYear + 1) + "/" + year;
                            edit_datetime.setText(_data);
                        } else {
                            if (dayOfMonth <= 9) {
                                String _data = "0" + dayOfMonth + "/" + (monthOfYear + 1) + "/" + year;
                                edit_datetime.setText(_data);
                            } else {
                                String _data = dayOfMonth + "/" + (monthOfYear + 1) + "/" + year;
                                edit_datetime.setText(_data);
                            }

                        }

                        /*String _data = dayOfMonth + "/" + (monthOfYear+1) + "/" + year;
                        TxtDate.setText(_data);*/
                        TimePickerDialog timePickerDialog = new TimePickerDialog(ProgressiveCallEx.this, R.style.DatePickerDialogTheme, new TimePickerDialog.OnTimeSetListener() {
                            @Override
                            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {

                                String _dataHora = edit_datetime.getText().toString() + " " + String.format("%02d:%02d", hourOfDay, minute) + ":" + "00";
                                edit_datetime.setText(_dataHora);

                            }
                        }, 0, 0, true);
                        timePickerDialog.setCancelable(false);
                        timePickerDialog.show();
                        //  timePickerDialog.getButton(DatePickerDialog.BUTTON_NEGATIVE).setVisibility(View.GONE);
                    }
                }, mYear, mMonth, mDay);
                datePickerDialog.setCancelable(false);
                datePickerDialog.show();
                //   datePickerDialog.getButton(DatePickerDialog.BUTTON_NEGATIVE).setVisibility(View.GONE);
                datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis() + 24 * 60 * 60 * 1000);
                break;
            default:
                break;
        }

    }

    private void login(String msg, String btnname) {

        final AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.alertDialog);
        builder.setMessage(msg)
                .setCancelable(false)
                .setPositiveButton(btnname, new DialogInterface.OnClickListener() {
                    public void onClick(final DialogInterface dialog, final int id) {

                        dialog.dismiss();
                        StartCall();

                    }
                });
        final AlertDialog alert = builder.create();
        alert.show();

    }

    private void StartCall() {

        try {
            JSONObject params = new JSONObject();
            // params.put("action", "preview_call_app");

            params.put("agent_id", Util.getData("AgentName", getApplicationContext()));
            // params.put("username", Util.getData("AgentName", getApplicationContext()));
            Log.e("START CALL", FetchPROGRESSIVECALLS + "\n" + params.toString());

            CallApi.postResponse(this, params.toString(), FetchPROGRESSIVECALLS, new VolleyResponseListener() {
                @Override
                public void onError(String message) {
                    Util.Logcat.e("onError" + message);
                    if (message.contains("TimeoutError")) {
                        login("Time out", "Reload");
                    }
                }

                @Override
                public void onResponse(JSONObject response) {

                    Util.Logcat.e("START CALL" + response);

                    try {
                        final JSONObject resobject = response;
                        Log.e("resobject", resobject.toString());
                        if (resobject.getString("Agent_status").equalsIgnoreCase("0")) {
                            GetPreviewCall(resobject.getString("Lead_id"), resobject.getString("phone_number"), resobject.getString("crm_id"));
                            leadid = resobject.getString("Lead_id");
                            mobileno = resobject.getString("phone_number");
                            crmname = resobject.getString("crm_id");

                        } else if (resobject.getString("Agent_status").equalsIgnoreCase("ONCALL")) {

                            final AlertDialog.Builder builder = new AlertDialog.Builder(ProgressiveCallEx.this, R.style.alertDialog);
                            builder.setMessage("Please close previous call status")
                                    .setCancelable(false)
                                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                        public void onClick(@SuppressWarnings("unused") final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                                            try {
                                                GetPreviewCall(resobject.getString("Lead_id"), resobject.getString("phone_number"), resobject.getString("crm_id"));
                                                leadid = resobject.getString("Lead_id");
                                                mobileno = resobject.getString("phone_number");
                                                crmname = resobject.getString("crm_id");
                                            } catch (JSONException e) {
                                                e.printStackTrace();
                                            }
                                        }
                                    });

                            final AlertDialog alert = builder.create();
                            alert.show();

                        } else if (resobject.getString("Agent_status").equalsIgnoreCase("-6")) {

                            final Handler handler = new Handler();
                            handler.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    //Do something after 100ms
                                    StartCall();
                                }
                            }, Long.parseLong(resobject.getString("Duration")));

                        } else {
                            //add alert then call StartCall();
                            final AlertDialog.Builder builder = new AlertDialog.Builder(ProgressiveCallEx.this, R.style.alertDialog);
                            builder.setMessage("Please Check your Internet")
                                    .setCancelable(false)
                                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                        public void onClick(@SuppressWarnings("unused") final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                                            StartCall();
                                        }
                                    });
                            final AlertDialog alert = builder.create();
                            alert.show();
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
            Log.e("LOGOUT", LOGOUTUSER + "\n" + params.toString());
            CallApi.postResponse(this, params.toString(), LOGOUTUSER, new VolleyResponseListener() {
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
                            Util.saveData("BREAK", "", getApplicationContext());
                            Util.saveData("username", "", getApplicationContext());
                            Util.saveData("password", "", getApplicationContext());
                            Util.saveData("AgentName", "", getApplicationContext());
                            Util.saveData("mobile_no", "", getApplicationContext());
                            Util.saveData("crm_id", "", getApplicationContext());
                            Util.saveData("callmode", "", getApplicationContext());
                            Util.saveData("process_name", "", getApplicationContext());
                            Toast.makeText(ProgressiveCallEx.this,"User Successfully Logged Out.", Toast.LENGTH_LONG);
                            Intent main = new Intent(ProgressiveCallEx.this, Login.class);
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

    private void GetPreviewCall(String leadid, String mobileno, final String crm_name) {
        ListCollection.clear();
        try {
            JSONObject params = new JSONObject();
            // params.put("action", "FetchCustomerData");

            params.put("Mobile_no", mobileno);
            //params.put("crm_name", crm_name);
            params.put("Acct_no", "0");
            params.put("ALt_dial", "0");
            params.put("Lead_id", leadid);
            params.put("agent_id", Util.getData("AgentName", getApplicationContext()));
            Log.e("GET CUSTOMER DETAILS", GetCustomerData + "\n" + params.toString());
            CallApi.postResponse(ProgressiveCallEx.this, params.toString(), GetCustomerData, new VolleyResponseListener() {
                @Override
                public void onError(String message) {
                    Util.Logcat.e("onError" + message);
                    BtnRefresh.setVisibility(View.VISIBLE);
                }

                @Override
                public void onResponse(JSONObject response) {
                    Util.Logcat.e("CUSTOMER DETAILS" + response);
                    if (spinner) {

                        GetDispo(crm_name);
                        spinner = false;
                    } else {

                    }
                    try {
                        //JSONObject resobject = response;
                        JSONObject resobject = new JSONObject(response.getString("d_columninfo"));

                        JSONArray array = resobject.getJSONArray("Values");

                        for (int i = 0; i < array.length(); i++) {
                            JSONObject obj = array.getJSONObject(i);
                            DataHashMap = new HashMap<String, String>();
                            DataHashMap.put("text", obj.getString("key"));
                            DataHashMap.put("value", obj.getString("value"));
                            DataHashMap.put("IsEdit", obj.getString("is_edit"));
                            ListCollection.add(DataHashMap);
                        }

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                if (ListCollection.size() > 0) {
                                    BtnRefresh.setVisibility(View.GONE);
                                    adapter = new CallAdapterPreview(ProgressiveCallEx.this, ListCollection);
                                    listview.setAdapter(adapter);
                                    setListViewHeightBasedOnItems(listview);
                                } else {
                                    BtnRefresh.setVisibility(View.VISIBLE);
                                }
                            }
                        });

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });

        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    private void GetDispo(String crm_id) {
        try {
            JSONObject params = new JSONObject();
            //params.put("action", "Get_Dynamic_CRM_Dispo");
            params.put("CRM_id", crm_id);
            CallApi.postResponse(ProgressiveCallEx.this, params.toString(), GetDispo, new VolleyResponseListener() {
                @Override
                public void onError(String message) {
                    Util.Logcat.e("onError" + message);
                }

                @Override
                public void onResponse(JSONObject response) {
                    Util.Logcat.e("GET DISPO" + response);
                    try {
                        // JSONObject data = new JSONObject(response.getString("Data"));
                        RESPONSE = new JSONObject(response.getString("Data"));

                        JSONArray array = RESPONSE.getJSONArray("Dispo");

                        for (int i = 0; i < array.length(); i++) {
                            JSONObject obj = array.getJSONObject(i);
                            Log.e("..........", "" + obj.getString("name"));
                            spinnerlist.add(obj.getString("name"));
                        }

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<>
                                        (ProgressiveCallEx.this, R.layout.spinner_textview,
                                                spinnerlist);
                                spinnerArrayAdapter.setDropDownViewResource(android.R.layout
                                        .simple_spinner_dropdown_item);
                                SpinDispo.setAdapter(spinnerArrayAdapter);
                            }
                        });

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });

        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    private boolean setListViewHeightBasedOnItems(ListView listView) {
        ListAdapter listAdapter = listView.getAdapter();
        if (listAdapter != null) {

            int numberOfItems = listAdapter.getCount();

            // Get total height of all items.
            int totalItemsHeight = 0;
            for (int itemPos = 0; itemPos < numberOfItems; itemPos++) {
                View item = listAdapter.getView(itemPos, null, listView);
                float px = 500 * (listView.getResources().getDisplayMetrics().density);
                item.measure(View.MeasureSpec.makeMeasureSpec((int) px, View.MeasureSpec.AT_MOST), View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
                totalItemsHeight += item.getMeasuredHeight();
            }

            // Get total height of all item dividers.
            int totalDividersHeight = listView.getDividerHeight() *
                    (numberOfItems - 1);
            // Get padding
            int totalPadding = listView.getPaddingTop() + listView.getPaddingBottom();

            // Set list height.
            ViewGroup.LayoutParams params = listView.getLayoutParams();
            params.height = totalItemsHeight + totalDividersHeight + totalPadding;
            listView.setLayoutParams(params);
            listView.requestLayout();
            //setDynamicHeight(listView);
            return true;

        } else {
            return false;
        }
    }

    private void EndCall() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < ListCollection.size(); i++) {
            View view = listview.getChildAt(i);
            TextView key = view.findViewById(R.id.txt);
            EditText value = view.findViewById(R.id.txt_value);
            //key~value#key~value#
            sb.append(key.getText().toString() + "~").append(value.getEditableText().toString() + "#");

        }
        String crm_info = sb.toString();
        try {
            JSONObject params = new JSONObject();

            params.put("endcall_type", "CLOSE");
            params.put("refno", "");
            params.put("process_name", "");
            params.put("crm_info", crm_info);
            String dynamicdispo = edit_date.getText().toString() + edit_datetime.getText().toString() + edit_number.getEditableText().toString() + edit_text.getEditableText().toString();
            params.put("disposition", StrDispo + "~" + StrDispoSub + "~" + dynamicdispo);
            params.put("list_comments", edit_comments.getEditableText().toString());
            params.put("convoxid", leadid);
            params.put("mobile_number", mobileno);
            params.put("agent_id", Util.getData("AgentName", getApplicationContext()));
            params.put("process_agent", Util.getData("AgentName", getApplicationContext()));
            CallApi.postResponse(ProgressiveCallEx.this, params.toString(), Callcloseapi, new VolleyResponseListener() {
                @Override
                public void onError(String message) {
                    Util.Logcat.e("onError" + message);
                }

                @Override
                public void onResponse(JSONObject response) {
                    Util.Logcat.e("GET STATUS" + response);
                    try {
                        final JSONObject resobject = response;
                        if (resobject.getString("STATUS").equalsIgnoreCase("EC00")) {
                            Toast.makeText(ProgressiveCallEx.this, resobject.getString("MESSAGE"), Toast.LENGTH_LONG);
                            //alert.build(resobject.getString("MESSAGE"));
                            Util.saveData("LEADID", "", getApplicationContext());
                            Util.saveData("MOBILENO", "", getApplicationContext());
                            Util.saveData("CRMNAME", "", getApplicationContext());

                            edit_number.setText("");
                            edit_text.setText("");
                            edit_date.setText("");
                            edit_datetime.setText("");
                            edit_comments.setText("");
                            label.setText("");

                            edit_text.setVisibility(View.GONE);
                            edit_number.setVisibility(View.GONE);
                            lydate.setVisibility(View.GONE);
                            lydatetime.setVisibility(View.GONE);

                            SpinDispo.setSelection(0);
                            if (chckLogout.isChecked()) {
                                Logout();
                            }else if(chckBreak.isChecked()){
                                Logout();
                            } else {
                                StartCall();
                            }

                        } else {
                            alert.build(resobject.getString("MESSAGE"));
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
