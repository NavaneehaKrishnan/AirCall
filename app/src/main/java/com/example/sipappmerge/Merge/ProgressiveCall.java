package com.example.sipappmerge.Merge;

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
import android.view.Menu;
import android.view.MenuItem;
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
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.JsonObjectRequest;

import com.example.sipappmerge.R;
import com.example.sipappmerge.Utils.CallApi;
import com.example.sipappmerge.Utils.CommonAlertDialog;
import com.example.sipappmerge.Utils.MyApplication;
import com.example.sipappmerge.Utils.OnItemUpdateListener;
import com.example.sipappmerge.Utils.RadioAlertDialog;
import com.example.sipappmerge.Utils.Util;
import com.example.sipappmerge.Utils.VolleyResponseListener;
import com.example.sipappmerge.adapter.AccAdapter;
import com.example.sipappmerge.adapter.CallAdapterPreview;
import com.example.sipappmerge.adapter.RecyclerViewAdapter;
import com.example.sipappmerge.model.ListItemBean;
import com.google.gson.Gson;

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
import static com.example.sipappmerge.Utils.Util.GetAlternateNo;
import static com.example.sipappmerge.Utils.Util.GetBreak;
import static com.example.sipappmerge.Utils.Util.GetCallclose_v1;
import static com.example.sipappmerge.Utils.Util.GetCustomerData_v1;
import static com.example.sipappmerge.Utils.Util.GetDispo;
import static com.example.sipappmerge.Utils.Util.LOGOUTUSER;
import static com.example.sipappmerge.Utils.Util.PROGRESSIVECALLS;
import static com.example.sipappmerge.Utils.Util.callapi;
import static com.example.sipappmerge.Utils.Util.getcallhistory;
import static com.example.sipappmerge.Utils.Util.recording;


public class ProgressiveCall extends BaseActivity implements View.OnClickListener {

    Spinner SpinDispo, SpinDispoSub, SpinMobileno;
    private HashMap<String, String> DataHashMap, DataHashMapAcc;
    private List<Map<String, String>> ListCollection, ListCollectionAcc;
    ListView listview, acc_listview;
    CallAdapterPreview adapter;
    AccAdapter Accadapter;
    List<String> spinnerlist, spinnerlistSub;
    String StrDispo = "", StrDispoSub = "", StrAltMobileno = "";
    EditText edit_number, edit_text, edit_comments;
    CommonAlertDialog alert;
    String LEADID = "", MOBILENO = "", DATAS = "";
    Button BtnEndCall;
    JSONObject RESPONSE;
    ImageView imgdate, imgdatetime;
    boolean spinner = true;
    CheckBox chckLogout, chckBreak;
    LinearLayout lydate, lydatetime, BtnRefresh, mobilely;
    TextView label, edit_date, edit_datetime;
    RelativeLayout lysubdispo;
    private static final int LOGOUT = 5;
    private ScrollView scrollView;

    CheckBox chkmobile;
    private RecyclerView recyclerView;
    private ArrayList<ListItemBean> customerDetails;
    private RecyclerViewAdapter availMeetingsAdapter;
    private ArrayList<ListItemBean> finalList;
    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.call_progressive);

        if (recording) {
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE);
        }//  Thread.setDefaultUncaughtExceptionHandler(new CustomizedExceptionHandler(Environment.getExternalStorageDirectory() + "/ARM/"));
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

        scrollView = findViewById(R.id.scrollView);
        listview = findViewById(R.id.collect_listview_call);
        recyclerView = findViewById(R.id.recyclerView);
        acc_listview = findViewById(R.id.acc_listview);
        SpinDispo = findViewById(R.id.dispo_spinner);
        SpinDispoSub = findViewById(R.id.sub_dispo_spinner);
        SpinMobileno = findViewById(R.id.mobile_spinner);
        mobilely = findViewById(R.id.mobilely);
        BtnEndCall = findViewById(R.id.end_call);
        BtnEndCall.setOnClickListener(this);
        BtnRefresh = findViewById(R.id.btn_refresh);
        chckLogout = findViewById(R.id.chckbox);
        chckBreak = findViewById(R.id.chk_break);
        chkmobile = findViewById(R.id.chkmobile);

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
        chkmobile.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    mobilely.setVisibility(View.VISIBLE);
                } else {
                    mobilely.setVisibility(View.GONE);
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

        ListCollection = new ArrayList<>();
        ListCollectionAcc = new ArrayList<>();
        spinnerlist = new ArrayList<>();
        spinnerlist.add("Select");
        spinnerlistSub = new ArrayList<>();
        alert = new CommonAlertDialog(this);

        BtnRefresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //StartCall();
                LoadDetails(LEADID, MOBILENO, DATAS);
                LoadMobileNo(LEADID,MOBILENO,DATAS);
                Toast.makeText(ProgressiveCall.this, "Redialling...", Toast.LENGTH_LONG);
            }
        });

        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                BtnRefresh.setVisibility(View.GONE);
            }
        }, 20000);

        SpinDispo.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            //@Override
            public void onItemSelected(AdapterView<?> parent, View arg1, int arg2, long arg3) {

                StrDispo = SpinDispo.getSelectedItem().toString();
                lydate.setVisibility(View.GONE);
                lydatetime.setVisibility(View.GONE);
                edit_number.setVisibility(View.GONE);
                edit_text.setVisibility(View.GONE);
                label.setText("");
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

                lydate.setVisibility(View.GONE);
                lydatetime.setVisibility(View.GONE);
                edit_number.setVisibility(View.GONE);
                edit_text.setVisibility(View.GONE);
                label.setText("");
                try {
                    JSONObject obj = RESPONSE.getJSONObject("subdispo");
                    JSONArray dispo = obj.getJSONArray(StrDispo);

                    for (int j = 0; j < dispo.length(); j++) {
                        JSONObject object = dispo.getJSONObject(j);

                        if (object.getString("subname").equalsIgnoreCase(StrDispoSub)) {

                            if (object.getString("Type").equalsIgnoreCase("DATE")) {
                                label.setText(object.getString("Label"));

                                lydate.setVisibility(View.VISIBLE);
                                break;
                            } else if (object.getString("Type").equalsIgnoreCase("DATETIME")) {
                                label.setText(object.getString("Label"));
                                lydatetime.setVisibility(View.VISIBLE);

                                break;
                            } else if (object.getString("Type").equalsIgnoreCase("NUMBER")) {
                                label.setText(object.getString("Label"));
                                edit_number.setVisibility(View.VISIBLE);

                                break;
                            } else if (object.getString("Type").equalsIgnoreCase("TEXT")) {
                                label.setText(object.getString("Label"));
                                edit_text.setVisibility(View.VISIBLE);

                                break;
                            } else {

                            }

                        }
                    }

                } catch (JSONException e) {

                }
            }

            public void onNothingSelected(AdapterView<?> arg0) {

            }
        });
        SpinMobileno.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            //@Override
            public void onItemSelected(AdapterView<?> parent, View arg1, int arg2, long arg3) {
                StrAltMobileno = SpinMobileno.getSelectedItem().toString();
            }

            public void onNothingSelected(AdapterView<?> arg0) {
            }
        });
         login("Login Success", "Start Calling");
       /* if (Util.dodont) {
            Util.dodont = false;
            Intent faq = new Intent(this, FAQActivity.class);
            faq.putExtra("url", Util.DODONT);
            faq.putExtra("btnname", "Agree and Continue");
            startActivity(faq);
        } else {
            login("Login Success", "Start Calling");
        }*/
    }

    private void Loadsubspinner(String st) {

        final List<String> spinnerlistSub = new ArrayList<>();

        try {
            JSONObject obj = RESPONSE.getJSONObject("subdispo");
            JSONArray dispo = obj.getJSONArray(st);
            for (int j = 0; j < dispo.length(); j++) {
                JSONObject object = dispo.getJSONObject(j);

                spinnerlistSub.add(object.getString("subname"));
            }

            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<>
                            (ProgressiveCall.this, R.layout.spinner_textview,
                                    spinnerlistSub);
                    spinnerArrayAdapter.setDropDownViewResource(android.R.layout
                            .simple_spinner_dropdown_item);
                    SpinDispoSub.setAdapter(spinnerArrayAdapter);
                }
            });
        } catch (JSONException e) {

        }

    }

    private void mail(String toString) {

        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("message/rfc822");
        intent.putExtra(Intent.EXTRA_EMAIL, new String[]{"syediffran1441@gmail.com"});
        intent.putExtra(Intent.EXTRA_SUBJECT, Util.getData("DEVICEDETAILS", getApplicationContext()));
        intent.putExtra(Intent.EXTRA_TEXT, toString);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK); // required when starting from Application
        startActivity(intent);

    }

    private void LoadBreakPopup() {

        try {
            JSONObject params = new JSONObject();

            params.put("CRM_id", "");
            params.put("agent_id",  Util.getData("AgentName", getApplicationContext()));
            CallApi.postResponse(ProgressiveCall.this, params.toString(), GetBreak, new VolleyResponseListener() {
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
                        String data = response.getString("ResponseData").replaceAll("\\\\","");
                        JSONObject dat = new JSONObject(data);
                        JSONArray st = dat.getJSONArray("Break");
                        // RadioAlertDialog alert = new RadioAlertDialog(ProgressiveCall.this);
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
        final Dialog dialog = new Dialog(ProgressiveCall.this);
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
                    Toast.makeText(ProgressiveCall.this, "Select Option", Toast.LENGTH_SHORT).show();
                } else {
                    dialog.dismiss();

                }
            }
        });
        try {
            for (int i = 0; i < array.length(); i++) {
                RadioButton rb = new RadioButton(ProgressiveCall.this); // dynamically creating RadioButton and adding to RadioGroup.
                rb.setText(array.getString(i));
                rb.setPadding(5, 5, 5, 5);
                rb.setTextAppearance(ProgressiveCall.this, android.R.style.TextAppearance_Large);
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


            CallApi.postResponse(ProgressiveCall.this, params.toString(), ApplyBreak, new VolleyResponseListener() {
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
                            JSONArray jsonArray = new JSONArray(new JSONObject(responseData).optString("DATA"));
                            if(jsonArray.length()>0)
                            {
                                if (jsonArray.getJSONObject(0).getString("Status").equalsIgnoreCase("0")) {
                                    Intent ibreak = new Intent(ProgressiveCall.this, Ibreak.class);
                                    ibreak.putExtra("break", "true");
                                    startActivity(ibreak);
                                    finish();
                                } else {
                                    alert.build(jsonArray.getJSONObject(0).getString("Statusdesc"));
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

    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {

            case LOGOUT:
                //  GetStatus();
                //FAQ
                Intent faq = new Intent(ProgressiveCall.this, FAQActivity.class);
                faq.putExtra("url", "http://123.63.108.199:36004/faq/");
                faq.putExtra("btnname", "Close");
                startActivity(faq);
                break;

        }
        return true;
    }

    /* public boolean onCreateOptionsMenu(Menu menu) {
         //   menu.add(0, CALL_ADDRESS, 0, "Call someone");
         // menu.add(0, SET_AUTH_INFO, 0, "Edit your SIP Info.");
         //  menu.add(0, HANG_UP, 0, "End Current Call.");
         menu.add(0, LOGOUT, 0, "FAQ");
         return true;

     }*/
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
                } else if (StrDispo.equalsIgnoreCase("Select")) {
                    alert.build("Select Dispo");
                } else if (edit_comments.getEditableText().toString().isEmpty()) {
                    alert.build("Enter Comments");
                } else {
                    EndCall();
                }
                break;
            case R.id.imgdate:
                DatePickerDialog datepicker = new DatePickerDialog(ProgressiveCall.this, R.style.DatePickerDialogTheme, new DatePickerDialog.OnDateSetListener() {
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
                //datepicker.getDatePicker().setMinDate(System.currentTimeMillis() + 24 * 60 * 60 * 1000);
                break;
            case R.id.imgdatetime:

                DatePickerDialog datePickerDialog = new DatePickerDialog(ProgressiveCall.this, R.style.DatePickerDialogTheme, new DatePickerDialog.OnDateSetListener() {
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
                        TimePickerDialog timePickerDialog = new TimePickerDialog(ProgressiveCall.this, R.style.DatePickerDialogTheme, new TimePickerDialog.OnTimeSetListener() {
                            @Override
                            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {

                                String _dataHora = edit_datetime.getText().toString() + " " + String.format("%02d:%02d", hourOfDay, minute) + ":" + "00";
                                edit_datetime.setText(_dataHora);

                            }
                        }, 0, 0, true);
                        timePickerDialog.setCancelable(false);
                        timePickerDialog.show();
                        timePickerDialog.getButton(DatePickerDialog.BUTTON_NEGATIVE).setVisibility(View.GONE);
                    }
                }, mYear, mMonth, mDay);
                datePickerDialog.setCancelable(false);
                datePickerDialog.show();
                //   datePickerDialog.getButton(DatePickerDialog.BUTTON_NEGATIVE).setVisibility(View.GONE);
                // datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis() + 24 * 60 * 60 * 1000);
                break;
            default:
                break;
        }

    }

    //Please
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
    private void Dial(final String leadid, final String mobileno, final String alt) {

        try {
            JSONObject params = new JSONObject();

            params.put("alt_dial", alt);
            params.put("Agent_no", Util.getData("mobile_no", getApplicationContext()));
            params.put("Customer_no", mobileno);
            params.put("lead_id", leadid);
            params.put("user_agent", Util.getData("AgentName", getApplicationContext()));
            // params.put("server_ip", "");
            params.put("Process", Util.getData("process_name", getApplicationContext()));

            params.put("agent_id",  Util.getData("AgentName", getApplicationContext()));
            //  alert.build(params.toString());
            CallApi.postResponse(ProgressiveCall.this, params.toString(), callapi, new VolleyResponseListener() {
                @Override
                public void onError(String message) {
                    Util.Logcat.e("onError" + message);
                }

                @Override
                public void onResponse(JSONObject response) {
                    Util.Logcat.e("GetAlternateCall Response" + response);

                    try {
                        JSONObject resobject = response;

                        if (resobject.getString("Status").equalsIgnoreCase("0")) {
                            //   GetPreviewCall(leadid, mobileno);
                           /* Toast.makeText(CallDispo.this, resobject.getString("Message"), Toast.LENGTH_LONG);
                            Intent main = new Intent(activity, CallDispo.class);
                            main.putExtra("leadid", leadid);
                            main.putExtra("mobileno", mobileno);
                            main.putExtra("crm_name", crm_name);
                            activity.startActivity(main);*/
                            //alert.build(resobject.getString("Message"));
                            //BtnEndCall.setVisibility(View.VISIBLE);
                        } else {
                            //Redial(resobject.getString("Message"));
                            //alert.build(resobject.getString("Message"));
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
    private void StartCall() {

        final String[] leadId = {""};
        final String[] lead_mobile = {""};
        try {
            JSONObject params = new JSONObject();
            // params.put("action", "preview_call_app");


            params.put("agent_id", Util.getData("AgentName", getApplicationContext()));
             params.put("username", Util.getData("AgentName", getApplicationContext()));
             Log.e("StartCall",FetchPROGRESSIVECALLS+"\n"+params.toString());


            CallApi.postResponse(ProgressiveCall.this, params.toString(), FetchPROGRESSIVECALLS, new VolleyResponseListener() {
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


                        if(!resobject.isNull("ResponseData")) {
                            JSONObject ResponseData = new JSONObject(resobject.getString("ResponseData"));
                            JSONArray array = ResponseData.optJSONObject("d_columninfo").optJSONArray("Values");
                            if (array.length() > 0) {
                                for (int i = 0; i < array.length(); i++) {
                                    JSONObject obj = array.getJSONObject(i);
                                    DataHashMap = new HashMap<>();
                                    DataHashMap.put("text", obj.getString("key"));
                                    DataHashMap.put("value", obj.getString("value"));
                                    DataHashMap.put("IsEdit", obj.getString("is_edit"));
                                    DataHashMap.put("lenght", String.valueOf(array.length()));
                                    DataHashMap.put("pos", String.valueOf(i + 1));
                                    ListCollection.add(DataHashMap);
                                    if (obj.getString("key").equalsIgnoreCase("lead_id"))
                                        leadId[0] = obj.getString("value");
                                    if (obj.getString("key").equalsIgnoreCase("mobile_number"))
                                        lead_mobile[0] = obj.getString("value");
                                }

                                LoadDetails(leadId[0], lead_mobile[0], resobject.getString("ResponseData").replace("\\\\r\\\\n", "").replaceAll("\\\\", ""));
                                LoadMobileNo(leadId[0], lead_mobile[0], resobject.getString("ResponseData").replace("\\\\r\\\\n", "").replaceAll("\\\\", ""));
                            /*runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    if (ListCollection.size() > 0) {
                                        //    BtnRefresh.setVisibility(View.GONE);
                                        adapter = new CallAdapterPreview(ProgressiveCall.this, ListCollection);
                                        listview.setAdapter(adapter);
                                        setListViewHeightBasedOnItems(listview);
                                    } else {
                                        //   BtnRefresh.setVisibility(View.VISIBLE);
                                    }
                                }
                            });*/
                            } else {
                                Intent ibreak = new Intent(ProgressiveCall.this, Ibreak.class);
                                ibreak.putExtra("break", "false");
                                startActivity(ibreak);
                                finish();
                            }
                        }else{
                            if(resobject.has("Status")&&!resobject.isNull("Status") && resobject.getString("Status").equals("3"))
                            {
                             AlertDialog.Builder builder = new AlertDialog.Builder(ProgressiveCall.this, R.style.alertDialog);
                                // builder.setTitle(title);
                                builder.setMessage(resobject.getString("StatusDesc"));
                                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();
                                        Util.saveData("username", "", getApplicationContext());
                                        Util.saveData("password", "", getApplicationContext());
                                        Util.saveData("AgentName", "", getApplicationContext());
                                        Util.saveData("mobile_no", "", getApplicationContext());
                                        Util.saveData("crm_id", "", getApplicationContext());
                                        Util.saveData("callmode", "", getApplicationContext());
                                        Util.saveData("process_name", "", getApplicationContext());
                                        //Toast.makeText(ProgressiveCall.this,"User Successfully Logged Out.", Toast.LENGTH_LONG);
                                        Intent main = new Intent(ProgressiveCall.this, Login.class);
                                        main.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                        startActivity(main);
                                        finish();
                                    }
                                });
                                AlertDialog alert = builder.create();
                                alert.setCancelable(false);
                                alert.show();
                            }else{
                                Toast.makeText(getApplicationContext(),resobject.getString("StatusDesc"),Toast.LENGTH_LONG).show();
                                Intent ibreak = new Intent(ProgressiveCall.this, Ibreak.class);
                                ibreak.putExtra("break", "false");
                                startActivity(ibreak);
                                finish();

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
            CallApi.postResponse(ProgressiveCall.this, params.toString(), LOGOUTUSER, new VolleyResponseListener() {
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
                            Toast.makeText(ProgressiveCall.this,"User Successfully Logged Out.", Toast.LENGTH_LONG);
                            Intent main = new Intent(ProgressiveCall.this, Login.class);
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
    private void LoadMobileNo(String leadid, String mobileno, final String Data) {
        try {
            JSONObject params = new JSONObject();

            params.put("Mobile_no", mobileno);
            params.put("Lead_id", leadid);
            params.put("agent_id",  Util.getData("AgentName", getApplicationContext()));

            CallApi.postResponse(ProgressiveCall.this, params.toString(), GetAlternateNo, new VolleyResponseListener() {
                @Override
                public void onError(String message) {
                    Util.Logcat.e("onError" + message);
                }

                @Override
                public void onResponse(JSONObject response) {
                    Util.Logcat.e("ALT MOBILENO Response" + response);
                    try {
                        final List<String> spinnerlistSub = new ArrayList<>();
                        String data = response.getString("ResponseData");

                        JSONArray array = new JSONArray(response.getString("ResponseData").replace("\\\\r\\\\n", "").replaceAll("\\\\",""));
                        for (int i = 0; i < array.length(); i++) {
                            JSONObject object = array.getJSONObject(i);

                            if (!object.getString("phone_1").isEmpty())
                                spinnerlistSub.add(object.getString("phone_1"));
                            if (!object.getString("phone_2").isEmpty())
                                spinnerlistSub.add(object.getString("phone_2"));
                            if (!object.getString("phone_3").isEmpty())
                                spinnerlistSub.add(object.getString("phone_3"));
                            if (!object.getString("phone_4").isEmpty())
                                spinnerlistSub.add(object.getString("phone_4"));
                            if (!object.getString("phone_5").isEmpty())
                                spinnerlistSub.add(object.getString("phone_5"));
                        }
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<>
                                        (ProgressiveCall.this, R.layout.spinner_textview,
                                                spinnerlistSub);
                                spinnerArrayAdapter.setDropDownViewResource(android.R.layout
                                        .simple_spinner_dropdown_item);
                                SpinMobileno.setAdapter(spinnerArrayAdapter);
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
    private void LoadDetails(String leadid, String mobileno, final String Data) {
        LEADID = leadid;
        MOBILENO = mobileno;
        DATAS = mobileno;
        GetDispo(leadid, mobileno);
        //  GetPreviewCall(leadid, mobileno,  Util.getData("crm_id",  getApplicationContext()));
        ListCollection.clear();
        customerDetails = new ArrayList<ListItemBean>();
        LoadAccDetails();
        try {
            JSONObject params = new JSONObject();
            params.put("Mobile_no", mobileno);
            params.put("Acct_no", "");
            params.put("ALt_dial", "0");
            params.put("Lead_id", leadid);
            params.put("agent_id", Util.getData("AgentName", getApplicationContext()));
            Log.e("GetCustomerData_v1", GetCustomerData_v1 + "\n" + params.toString());
            CallApi.postResponse(ProgressiveCall.this, params.toString(), GetCustomerData_v1, new VolleyResponseListener() {
                @Override
                public void onError(String message) {
                    Util.Logcat.e("onError" + message);
                }

                @Override
                public void onResponse(JSONObject response) {
                    Util.Logcat.e("GetCustomerData_v1" + response);

                    try {
                        //  JSONObject resobject = response;

                        JSONObject resobject = new JSONObject(response.getString("ResponseData"));

                        JSONArray array = resobject.getJSONArray("Values");
                        Log.e("d_columninfo", array.toString());
                        Log.e("lenght", String.valueOf(array.length()));
                        for (int i = 0; i < array.length(); i++) {
                           /* JSONObject obj = array.getJSONObject(i);
                            DataHashMap = new HashMap<String, String>();
                            DataHashMap.put("text", obj.getString("key"));
                            DataHashMap.put("value", obj.getString("value"));
                            DataHashMap.put("IsEdit", obj.getString("is_edit"));
                            DataHashMap.put("lenght", String.valueOf(array.length()));
                            DataHashMap.put("pos", String.valueOf(i + 1));
                            //Log.e("lenght",String.valueOf(array.length()));
                            Log.e("pos", String.valueOf(i + 1));
                            ListCollection.add(DataHashMap);*/
                            JSONObject jsonObject = array.getJSONObject(i);
                            ListItemBean customerModel = new ListItemBean();
                            customerModel.setKey(jsonObject.getString("key"));
                            customerModel.setValue(jsonObject.getString("value"));
                            customerModel.setIs_edit(jsonObject.getString("is_edit"));
                            customerModel.setType(jsonObject.getString("Type"));
                            customerModel.setOption(jsonObject.getString("option"));
                            customerDetails.add(customerModel);
                        }

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                finalList = customerDetails;
                                if (customerDetails.size() > 0) {

                                    //   BtnRefresh.setVisibility(View.GONE);
                                    /*adapter = new CallAdapterPreview(CallDispo.this, ListCollection);
                                    listview.setAdapter(adapter);
                                    setListViewHeightBasedOnItems(listview);*/
                                    availMeetingsAdapter = new RecyclerViewAdapter(customerDetails, ProgressiveCall.this);
                                    recyclerView.setHasFixedSize(true);
                                    recyclerView.setLayoutManager(new LinearLayoutManager(ProgressiveCall.this));
                                    recyclerView.setAdapter(availMeetingsAdapter);
                                    availMeetingsAdapter.setOnItemUpdateListener(new OnItemUpdateListener() {
                                        @Override
                                        public void onUpdateList(ArrayList<ListItemBean> arrayList,int position) {
                                            finalList = arrayList;
                                            Log.e("onUpdateList", new Gson().toJson(finalList));
                                        }
                                    });
                                } else {
                                    //   BtnRefresh.setVisibility(View.VISIBLE);
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

    private void LoadAccDetails() {
        ListCollectionAcc.clear();
        try {
            JSONObject params = new JSONObject();

            params.put("Mobile_no", MOBILENO);
            params.put("lead_id", Util.getData("LEADID", getApplicationContext()));
            params.put("agent_id",  Util.getData("AgentName", getApplicationContext()));
            Log.e("ACC DETAILS", getcallhistory + "\n" + params.toString());

            CallApi.postResponse(ProgressiveCall.this, params.toString(), getcallhistory, new VolleyResponseListener() {
                @Override
                public void onError(String message) {
                    Util.Logcat.e("onError" + message);

                }

                @Override
                public void onResponse(JSONObject response) {
                    Util.Logcat.e("LoadAccDetails" + response);
                    try {
                        String data = response.getString("ResponseData");
                        Log.e("data", data);
                        JSONArray array = new JSONArray(data);
                        if(array.length()>0) {
                            for (int i = 0; i < array.length(); i++) {
                                JSONObject obj = array.getJSONObject(i);
                                DataHashMapAcc = new HashMap<>();
                                Log.e("call_time", obj.getString("call_time"));
                                DataHashMapAcc.put("call_time", obj.getString("call_time"));
                                DataHashMapAcc.put("status", obj.getString("status"));
                                DataHashMapAcc.put("agent_id", obj.getString("agent_id"));
                                DataHashMapAcc.put("process", obj.getString("process"));
                                DataHashMapAcc.put("disposition", obj.getString("disposition"));
                                DataHashMapAcc.put("comments", obj.getString("comments"));
                                DataHashMapAcc.put("call_attempt", obj.getString("noofattempt"));
                                ListCollectionAcc.add(DataHashMap);
                            }
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    if (ListCollectionAcc.size() > 0) {
                                        //    BtnRefresh.setVisibility(View.GONE);
                                        Accadapter = new AccAdapter(ProgressiveCall.this, ListCollectionAcc);
                                        acc_listview.setAdapter(Accadapter);
                                        setListViewHeightBasedOnItems(acc_listview);
                                    } else {
                                        //   BtnRefresh.setVisibility(View.VISIBLE);
                                    }
                                }
                            });
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

    //Please check your internet
    private void GetDispo(final String leadid, final String mobileno) {

        spinnerlist = new ArrayList<>();
        spinnerlist.add("Select");
        try {
            JSONObject params = new JSONObject();
            //params.put("action", "Get_Dynamic_CRM_Dispo");
            params.put("CRM_id", Util.getData("crm_id", getApplicationContext()));
            params.put("agent_id",  Util.getData("AgentName", getApplicationContext()));
            Log.e("GET DISPO", GetDispo + "\n" + params.toString());

            CallApi.postResponse(ProgressiveCall.this, params.toString(), GetDispo, new VolleyResponseListener() {
                @Override
                public void onError(String message) {
                    Util.Logcat.e("onError" + message);
                }

                @Override
                public void onResponse(JSONObject response) {
                    Util.Logcat.e("GET DISPO" + response);
                    try {
                        // JSONObject data = new JSONObject(response.getString("Data"));

                        MakeCaLL(leadid, mobileno);

                        RESPONSE = new JSONObject(response.getString("ResponseData").replace("\\\\r\\\\n", "").replaceAll("\\\\",""));

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
                                        (ProgressiveCall.this, R.layout.spinner_textview,
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

    private void MakeCaLL(String lead_id, String phone_number) {

        LEADID = lead_id;
        MOBILENO = phone_number;
        try {
            JSONObject params = new JSONObject();

            params.put("lead_id", lead_id);
            params.put("phone_number", phone_number);
            params.put("user", Util.getData("AgentName", getApplicationContext()));
            params.put("agent_id",  Util.getData("AgentName", getApplicationContext()));
            Log.e("MakeCaLL", PROGRESSIVECALLS + "\n" + params.toString());

            CallApi.postResponse(ProgressiveCall.this, params.toString(), PROGRESSIVECALLS, new VolleyResponseListener() {
                @Override
                public void onError(String message) {
                    Util.Logcat.e("onError" + message);
                    if (message.contains("TimeoutError")) {
                        Redial("Time out");
                    } else if (message.contains("Connection Reset")) {
                        Redial("Connection Reset");
                    } else {
                        Redial("Server Error");
                    }
                }

                @Override
                public void onResponse(JSONObject response) {
                    Util.Logcat.e("MakeCaLL" + response);

                    try {
                        JSONObject resobject = response;
                        // {"Agent_status":0,"Process":"RBLDemo","crm_id":"DEMOCRM","Lead_id":"6013469","phone_number":"9944412666"}
                        // resobject.getString("");
                        if(resobject.getString("Status").equals("0")) {
                            if (resobject.getJSONObject("PROGRESSIVECALLSResponse").getString("Agent_status").equalsIgnoreCase("0")) {
                                alert.build(resobject.getString("StatusDesc"));
                            } else {
                                Redial("We cannot process your request. Please try again");
                            }
                        }else{
                            alert.build(resobject.getString("StatusDesc"));
                        }

                      /* if (resobject.getString("Agent_status").equalsIgnoreCase("0")) {

                            GetPreviewCall(resobject.getString("Lead_id"), resobject.getString("phone_number"), resobject.getString("crm_id"));
                            leadid = resobject.getString("Lead_id");
                            mobileno = resobject.getString("phone_number");
                            crmname = resobject.getString("crm_id");

                        } else if (resobject.getString("Agent_status").equalsIgnoreCase("ONCALL")) {

                            final AlertDialog.Builder builder = new AlertDialog.Builder(ProgressiveCall.this, R.style.alertDialog);
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
                            final AlertDialog.Builder builder = new AlertDialog.Builder(ProgressiveCall.this, R.style.alertDialog);
                            builder.setMessage("Please Check your Internet")
                                    .setCancelable(false)
                                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                        public void onClick(@SuppressWarnings("unused") final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                                            StartCall();
                                        }
                                    });

                            final AlertDialog alert = builder.create();
                            alert.show();

                        }*/

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });

        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    private void setListViewHeightBasedOnItems(ListView listView) {
        ListAdapter listAdapter = listView.getAdapter();
        if (listAdapter == null) {
            // pre-condition
            return;
        }

        int totalHeight = 0;
        int desiredWidth = View.MeasureSpec.makeMeasureSpec(listView.getWidth(), View.MeasureSpec.AT_MOST);
        for (int i = 0; i < listAdapter.getCount(); i++) {
            View listItem = listAdapter.getView(i, null, listView);
            listItem.measure(desiredWidth, View.MeasureSpec.UNSPECIFIED);
            totalHeight += listItem.getMeasuredHeight();
        }

        ViewGroup.LayoutParams params = listView.getLayoutParams();
        params.height = totalHeight + (listView.getDividerHeight() * (listAdapter.getCount() - 1));
        listView.setLayoutParams(params);
        listView.requestLayout();
    }

    private void EndCall() {



        try {
            JSONObject params = new JSONObject();

            JSONArray jsonArray = new JSONArray();
            if(finalList.size()>0)
            {
                for(int i=0;i<finalList.size();i++)
                {
                    JSONObject jsonObject = new JSONObject();
                    jsonObject.put("key",finalList.get(i).getKey());
                    jsonObject.put("value",finalList.get(i).getValue());
                    jsonObject.put("Type",finalList.get(i).getType());
                    jsonObject.put("is_edit",finalList.get(i).getIs_edit());
                    jsonObject.put("option",finalList.get(i).getOption());
                    jsonArray.put(i,jsonObject);
                }
            }
            params.put("endcall_type", "CLOSE");
            params.put("crmData", jsonArray);
            params.put("refno", "");
            params.put("process_name", "");

            String dynamicdispo = edit_date.getText().toString() + edit_datetime.getText().toString() + edit_number.getEditableText().toString() + edit_text.getEditableText().toString();
            params.put("disposition", StrDispo + "~" + StrDispoSub + "~" + dynamicdispo);
            params.put("list_comments", edit_comments.getEditableText().toString());
            params.put("convoxid", LEADID);
            params.put("mobile_number", MOBILENO);
            params.put("agent_id", Util.getData("AgentName", getApplicationContext()));
            params.put("process_agent", Util.getData("AgentName", getApplicationContext()));
            Log.e("END CALL", GetCallclose_v1 + "\n" + params.toString());
            // alert.build(params.toString());
            CallApi.postResponse(ProgressiveCall.this, params.toString(), GetCallclose_v1, new VolleyResponseListener() {
                @Override
                public void onError(String message) {
                    Util.Logcat.e("onError" + message);
                }

                @Override
                public void onResponse(JSONObject response) {
                    Util.Logcat.e("Callcloseapi" + response);
                    try {
                        final JSONObject resobject = response;
                        if (resobject.getString("Status").equalsIgnoreCase("EC00")) {
                            Toast.makeText(ProgressiveCall.this, resobject.getString("StatusDesc"), Toast.LENGTH_LONG);
                            //alert.build(resobject.getString("StatusDesc"));
                            // LEADID = "";
                            // MOBILENO = "";
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
                            SpinDispoSub.setSelection(SpinDispoSub.getSelectedItemPosition()>=0?0:0);
                            scrollView.smoothScrollTo(0,0);

                            if (chckLogout.isChecked()) {
                                Logout();
                            } else if (chckBreak.isChecked()) {
                                ReleaseBreak("ApplyBreak");
                            } else {
                                if(chkmobile.isChecked() && !StrAltMobileno.isEmpty())
                                    Dial(LEADID,StrAltMobileno,"Y");
                                else
                                    StartCall();
                            }
                        } else {
                            alert.build(resobject.getString("StatusDesc"));
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

    private void Redial(String Msg) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.alertDialog);
        builder.setMessage(Msg)
                .setCancelable(false)
                .setPositiveButton("Close", new DialogInterface.OnClickListener() {
                    public void onClick(final DialogInterface dialog, final int id) {
                        dialog.dismiss();
                        //MakeCaLL(LEADID, MOBILENO);
                    }
                })
                /*.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(final DialogInterface dialog, final int id) {
                        dialog.dismiss();
                    }
                })*/;
        final AlertDialog alert = builder.create();
        alert.show();
    }
}
