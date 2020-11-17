package com.example.sipappmerge.Merge;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
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
import android.widget.RelativeLayout;
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
import com.example.sipappmerge.Utils.CallApi;
import com.example.sipappmerge.Utils.CommonAlertDialog;
import com.example.sipappmerge.Utils.MyApplication;
import com.example.sipappmerge.R;
import com.example.sipappmerge.Utils.OnItemUpdateListener;
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
import static com.example.sipappmerge.Utils.Util.BASE;
import static com.example.sipappmerge.Utils.Util.Callcloseapi;
import static com.example.sipappmerge.Utils.Util.GetAlternateNo;
import static com.example.sipappmerge.Utils.Util.GetCallclose_v1;
import static com.example.sipappmerge.Utils.Util.GetCustomerData;
import static com.example.sipappmerge.Utils.Util.GetCustomerData_v1;
import static com.example.sipappmerge.Utils.Util.GetDispo;
import static com.example.sipappmerge.Utils.Util.callapi;
import static com.example.sipappmerge.Utils.Util.getcallhistory;
import static com.example.sipappmerge.Utils.Util.recording;

public class CallDispo extends BaseActivity implements View.OnClickListener {

    Spinner SpinDispo, SpinDispoSub, SpinMobileno;
    private HashMap<String, String> DataHashMap, DataHashMapAcc;
    private List<Map<String, String>> ListCollection, ListCollectionAcc;
    public ListView listview, acc_listview;
    CallAdapterPreview adapter;
    AccAdapter Accadapter;
    List<String> spinnerlist, spinnerlistSub;
    String StrDispo = "", StrDispoSub = "", StrAltMobileno = "";
    EditText edit_number, edit_text, EdComments;
    CommonAlertDialog alert;
    String leadid, mobileno, crmname, dial;
    Button BtnEndCall;
    JSONObject RESPONSE;
    ImageView imgdate, imgdatetime;
    LinearLayout lydate, lydatetime, BtnRefresh, mobilely;
    TextView label, edit_date, edit_datetime;
    RelativeLayout lysubdispo;
    private static final int LOGOUT = 5;
    CheckBox chkmobile;
    int FLAG = 0;
    //"FAQ"
    private ArrayList<ListItemBean> customerDetails;
    private RecyclerViewAdapter availMeetingsAdapter;
    private ArrayList<ListItemBean> finalList;
    private RecyclerView recyclerView;
    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.call_dispo);
        //setContentView(R.layout.call_progressive);
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
        listview = findViewById(R.id.collect_listview_call);
        recyclerView = findViewById(R.id.recyclerView);
        acc_listview = findViewById(R.id.acc_listview);
        SpinDispo = findViewById(R.id.dispo_spinner);
        SpinDispoSub = findViewById(R.id.sub_dispo_spinner);
        SpinMobileno = findViewById(R.id.mobile_spinner);
        chkmobile = findViewById(R.id.chkmobile);
        EdComments = findViewById(R.id.comments);
        BtnEndCall = findViewById(R.id.end_call);
        //BtnCancel = findViewById(R.id.cancel_btn);
        //BtnCall = findViewById(R.id.btn_call);
        BtnEndCall.setOnClickListener(this);
        // BtnCancel.setOnClickListener(this);
        // BtnCall.setOnClickListener(this);
        BtnRefresh = findViewById(R.id.btn_refresh);
        mobilely = findViewById(R.id.mobilely);
        //call_ly = findViewById(R.id.call_ly);
        ListCollection = new ArrayList<Map<String, String>>();
        ListCollectionAcc = new ArrayList<Map<String, String>>();
        spinnerlist = new ArrayList<>();
        spinnerlist.add("Select");
        spinnerlistSub = new ArrayList<>();
        alert = new CommonAlertDialog(this);

        //new
        edit_number = findViewById(R.id.edit_number);
        edit_text = findViewById(R.id.edit_text);
        edit_date = findViewById(R.id.edit_date);
        edit_datetime = findViewById(R.id.edit_datetime);

        imgdate = findViewById(R.id.imgdate);
        imgdatetime = findViewById(R.id.imgdatetime);
        label = findViewById(R.id.label);

        lydate = findViewById(R.id.lydate);
        lydatetime = findViewById(R.id.lydatetime);
        lysubdispo = findViewById(R.id.lysubdispo);

        imgdate.setOnClickListener(this);
        imgdatetime.setOnClickListener(this);
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

        leadid = getIntent().getStringExtra("leadid");
        mobileno = getIntent().getStringExtra("mobileno");

        crmname = getIntent().getStringExtra("crm_name");
        dial = getIntent().getStringExtra("dial");

        Util.saveData("LEADID", leadid, getApplicationContext());
        Util.saveData("MOBILENO", mobileno, getApplicationContext());
        Util.saveData("CRMNAME", crmname, getApplicationContext());

        BtnRefresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(CallDispo.this, "Redialling...", Toast.LENGTH_LONG);
                GetPreviewCall(Util.getData("LEADID", getApplicationContext()), mobileno, Util.getData("CRMNAME", getApplicationContext()));
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
                Log.e("StrDispoSub", StrDispoSub);
                lydate.setVisibility(View.GONE);
                lydatetime.setVisibility(View.GONE);
                edit_number.setVisibility(View.GONE);
                edit_text.setVisibility(View.GONE);
                label.setText("");
                try {

                    JSONObject obj = RESPONSE.getJSONObject("subdispo");
                    JSONArray dispo = obj.getJSONArray(StrDispo);
                    Log.e("dispo", "" + dispo.toString());
                    for (int j = 0; j < dispo.length(); j++) {
                        JSONObject object = dispo.getJSONObject(j);
                        if (object.getString("subname").equalsIgnoreCase(StrDispoSub)) {

                            if (object.getString("Type").equalsIgnoreCase("DATE")) {
                                label.setText(object.getString("Label"));
                                //Log.e("1", "DATE");
                                lydate.setVisibility(View.VISIBLE);
                                break;
                            } else if (object.getString("Type").equalsIgnoreCase("DATETIME")) {
                                label.setText(object.getString("Label"));
                                lydatetime.setVisibility(View.VISIBLE);
                                //Log.e("2", "DATETIME");
                                break;
                            } else if (object.getString("Type").equalsIgnoreCase("NUMBER")) {
                                label.setText(object.getString("Label"));
                                edit_number.setVisibility(View.VISIBLE);
                                //Log.e("3", "NUMBER");
                                break;
                            } else if (object.getString("Type").equalsIgnoreCase("TEXT")) {
                                label.setText(object.getString("Label"));
                                edit_text.setVisibility(View.VISIBLE);
                                //Log.e("4", "TEXT");
                                break;
                            } else {
                                //Log.e("5", "NOTHING");
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


        GetPreviewCall(leadid, mobileno, crmname);
        LoadMobileNo();
        //  GetPreviewCall(leadid, mobileno, crmname);

    }

    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {

            case LOGOUT:
                Intent faq = new Intent(CallDispo.this, FAQActivity.class);
                faq.putExtra("url", "http://123.63.108.199:36004/faq/");
                faq.putExtra("btnname", "Agree and Continue");
                startActivity(faq);
                break;

        }
        return true;
    }

    /*  public boolean onCreateOptionsMenu(Menu menu) {
           //   menu.add(0, CALL_ADDRESS, 0, "Call someone");
           // menu.add(0, SET_AUTH_INFO, 0, "Edit your SIP Info.");
           //  menu.add(0, HANG_UP, 0, "End Current Call.");
           menu.add(0, LOGOUT, 0, "FAQ");
           return true;
       }*/

    private void mail(String toString) {

        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("message/rfc822");
        intent.putExtra(Intent.EXTRA_EMAIL, new String[]{"syediffran1441@gmail.com"});
        intent.putExtra(Intent.EXTRA_SUBJECT, Util.getData("DEVICEDETAILS", getApplicationContext()));
        intent.putExtra(Intent.EXTRA_TEXT, toString);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK); // required when starting from Application
        startActivity(intent);
    }

    private void LoadMobileNo() {
        try {
            JSONObject params = new JSONObject();
            params.put("action", "GetAlternateNo");
            params.put("Mobile_no", mobileno);
            params.put("Lead_id", Util.getData("LEADID", getApplicationContext()));
            params.put("agent_id",  Util.getData("AgentName", getApplicationContext()));
            Log.e("ALT MOBILENO", GetAlternateNo + "\n" + params.toString());

            CallApi.postResponse(CallDispo.this, params.toString(), GetAlternateNo, new VolleyResponseListener() {
                @Override
                public void onError(String message) {
                    Util.Logcat.e("onError" + message);
                }

                @Override
                public void onResponse(JSONObject response) {
                    Util.Logcat.e("ALT MOBILENO" + response);
                    try {
                        final List<String> spinnerlistSub = new ArrayList<>();
                        String data = response.getString("ResponseData");
                        Log.e("data", data);
                        JSONArray array = new JSONArray(data);
                        for (int i = 0; i < array.length(); i++) {
                            JSONObject object = array.getJSONObject(i);
                            Log.e("phone_1", "" + object.getString("phone_1"));
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
                                        (CallDispo.this, R.layout.spinner_textview,
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

    private void LoadAccDetails(String MobileNo) {
        ListCollectionAcc.clear();
        try {
            JSONObject params = new JSONObject();

            params.put("Mobile_no", MobileNo);
            params.put("lead_id", Util.getData("LEADID", getApplicationContext()));
            params.put("agent_id",  Util.getData("AgentName", getApplicationContext()));
            Log.e("ACC DETAILS", getcallhistory + "\n" + params.toString());

            CallApi.postResponse(CallDispo.this, params.toString(), getcallhistory, new VolleyResponseListener() {
                @Override
                public void onError(String message) {
                    Util.Logcat.e("onError" + message);
                }

                @Override
                public void onResponse(JSONObject response) {
                      Util.Logcat.e("getcallhistory" + response);
                    try {
                        String data = response.getString("ResponseData");
                        JSONArray array = new JSONArray(data);
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
                            ListCollectionAcc.add(DataHashMapAcc);
                        }
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                if (ListCollectionAcc.size() > 0) {
                                    //    BtnRefresh.setVisibility(View.GONE);
                                    Accadapter = new AccAdapter(CallDispo.this, ListCollectionAcc);
                                    acc_listview.setAdapter(Accadapter);
                                    setListViewHeightBasedOnItems(acc_listview);
                                } else {
                                    // BtnRefresh.setVisibility(View.VISIBLE);
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


    @Override
    public void onClick(View v) {
        final Calendar c = Calendar.getInstance();
        Integer mYear = c.get(Calendar.YEAR);
        Integer mMonth = c.get(Calendar.MONTH);
        Integer mDay = c.get(Calendar.DAY_OF_MONTH);

        switch (v.getId()) {

           /* case R.id.btn_call:
                if (dial.equalsIgnoreCase("true")) {
                    Dial(leadid, mobileno, "N");
                } else {
                   // Dial(leadid, mobileno, "N");
                    call_ly.setVisibility(View.GONE);
                    BtnEndCall.setVisibility(View.VISIBLE);
                    alert.build("Please Complete last call status");
                }
                break;
            case R.id.cancel_btn:
                finish();
                break;*/

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
                } else if (EdComments.getEditableText().toString().isEmpty()) {
                    alert.build("Enter Comments");
                } else {
                    EndCall();
                }
                break;

            case R.id.imgdate:
                DatePickerDialog datepicker = new DatePickerDialog(CallDispo.this, R.style.DatePickerDialogTheme, new DatePickerDialog.OnDateSetListener() {
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

                DatePickerDialog datePickerDialog = new DatePickerDialog(CallDispo.this, R.style.DatePickerDialogTheme, new DatePickerDialog.OnDateSetListener() {
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
                        TimePickerDialog timePickerDialog = new TimePickerDialog(CallDispo.this, R.style.DatePickerDialogTheme, new TimePickerDialog.OnTimeSetListener() {
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
                //datePickerDialog.getButton(DatePickerDialog.BUTTON_NEGATIVE).setVisibility(View.GONE);
                //datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis() + 24 * 60 * 60 * 1000);
                break;
            default:
                break;
        }

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
            Log.e("callapi", callapi+ "\n" + params.toString());
            //  alert.build(params.toString());
            CallApi.postResponse(CallDispo.this, params.toString(), callapi, new VolleyResponseListener() {
                @Override
                public void onError(String message) {
                    Util.Logcat.e("onError" + message);
                }

                @Override
                public void onResponse(JSONObject response) {
                    Util.Logcat.e("callapi" + response);

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
                            alert.build(resobject.getString("StatusDesc"));
                            //BtnEndCall.setVisibility(View.VISIBLE);
                        } else {
                            Redial(resobject.getString("StatusDesc"));
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
               /* @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    Map<String, String>  params = new HashMap<String, String>();
                    params.put("Content-Type","application/json");
                    params.put("access_token","PROD_MIAGCSqGSIb3DQEHAqCAMIACAQExC");
                    return params;
                }*/
        };

          /*  req.setRetryPolicy(new DefaultRetryPolicy(30000,
                    0,
                    DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
            //Creating request queue
            RequestQueue requestQueue = Volley.newRequestQueue(context);
            //Adding request to the queue
            requestQueue.add(req);*/

        MyApplication.getInstance().getRequestQueue().add(req);

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
                            (CallDispo.this, R.layout.spinner_textview,
                                    spinnerlistSub);
                    spinnerArrayAdapter.setDropDownViewResource(android.R.layout
                            .simple_spinner_dropdown_item);
                    SpinDispoSub.setAdapter(spinnerArrayAdapter);
                }
            });
        } catch (JSONException e) {

        }

    }

    private void GetPreviewCall(final String leadid, final String mobileno, final String crm_name) {

        ListCollection.clear();
        customerDetails = new ArrayList<ListItemBean>();
        try {
            JSONObject params = new JSONObject();
            // params.put("action", "FetchCustomerData");

            params.put("Mobile_no", mobileno);
            Log.e("mobileno::: 2", mobileno);
            //params.put("crm_name", crm_name);
            params.put("Acct_no", "");
            params.put("ALt_dial", "0");
            params.put("Lead_id", leadid);
            params.put("agent_id", Util.getData("AgentName", getApplicationContext()));
            Log.e("GetCustomerData_v1", GetCustomerData_v1 + "\n" + params.toString());
            CallApi.postResponse(CallDispo.this, params.toString(), GetCustomerData_v1, new VolleyResponseListener() {
                @Override
                public void onError(String message) {
                    Util.Logcat.e("onError" + message);
                }

                @Override
                public void onResponse(JSONObject response) {
                    Util.Logcat.e("GetCustomerData_v1" + response);

                    try {
                        //  JSONObject resobject = response;
                        GetDispo(crm_name);

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
                                    availMeetingsAdapter = new RecyclerViewAdapter(customerDetails, CallDispo.this);
                                    recyclerView.setHasFixedSize(true);
                                    recyclerView.setLayoutManager(new LinearLayoutManager(CallDispo.this));
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

    private void GetDispo(String crm_id) {

        LoadAccDetails(mobileno);
        try {
            JSONObject params = new JSONObject();
            // params.put("action", "Get_Disposition");
            //params.put("action", "Get_CRM_Dispo");
            //params.put("action", "Get_Dynamic_CRM_Dispo");
            params.put("CRM_id", Util.getData("crm_id", getApplicationContext()));
            //Util.getData("crm_name",getApplicationContext());
            params.put("agent_id",  Util.getData("AgentName", getApplicationContext()));
            CallApi.postResponse(CallDispo.this, params.toString(), GetDispo, new VolleyResponseListener() {
                @Override
                public void onError(String message) {
                    Util.Logcat.e("onError" + message);
                }

                @Override
                public void onResponse(JSONObject response) {
                    Util.Logcat.e("GET DISPO" + response);
                    try {
                        if (dial.equalsIgnoreCase("true")) {
                            //  Dial(leadid, mobileno, crmname);
                            Dial(leadid, mobileno, "N");
                        } else {
                            alert.build("Please Complete last call status");
                        }
                        // JSONObject data = new JSONObject(response.getString("Data"));
                        RESPONSE = new JSONObject(response.getString("ResponseData"));
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
                                        (CallDispo.this, R.layout.spinner_textview,
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
            params.put("list_comments", EdComments.getEditableText().toString());
            params.put("convoxid", leadid);
            params.put("mobile_number", mobileno);
            params.put("agent_id", Util.getData("AgentName", getApplicationContext()));
            params.put("process_agent", Util.getData("AgentName", getApplicationContext()));
            Log.e("EndCall",GetCallclose_v1+"\n"+params.toString());
            CallApi.postResponse(CallDispo.this, params.toString(), GetCallclose_v1, new VolleyResponseListener() {
                @Override
                public void onError(String message) {
                    Util.Logcat.e("onError" + message);
                }

                @Override
                public void onResponse(JSONObject response) {
                    Util.Logcat.e("GET STATUS" + response);
                    try {
                        final JSONObject resobject = response;
                        if (resobject.getString("Status").equalsIgnoreCase("EC00")) {
                            //Toast.makeText(CallDispo.this, resobject.getString("MESSAGE"), Toast.LENGTH_LONG);
                            //alert.build(resobject.getString("MESSAGE"));
                            //if (mobilely.getVisibility() == View.VISIBLE && FLAG == 0) {
                            if (chkmobile.isChecked()) {
                                EdComments.setText("");
                                SpinDispo.setSelection(0);
                                Dial(Util.getData("LEADID", getApplicationContext()), StrAltMobileno, "Y");
                                FLAG = 1;
                                chkmobile.setChecked(false);
                            } else if (Util.getData("callmode", getApplicationContext()).equalsIgnoreCase("progressive")) {
                                Intent main = new Intent(CallDispo.this, ProgressiveCall.class);
                                startActivity(main);
                                finish();
                                Util.saveData("LEADID", "", getApplicationContext());
                                Util.saveData("MOBILENO", "", getApplicationContext());
                                Util.saveData("CRMNAME", "", getApplicationContext());
                                // alert.build(resobject.toString());
                            } else {
                                //dailer page
                                refresh(resobject.getString("StatusDesc"));
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

    private void refresh(String msg) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(CallDispo.this, R.style.alertDialog);
        builder.setMessage(msg)
                .setCancelable(false)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(@SuppressWarnings("unused") final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                        dialog.dismiss();
                        Intent main = new Intent(CallDispo.this, NewModel.class);
                        startActivity(main);
                        finish();
                    }
                });
        final AlertDialog alert = builder.create();
        alert.show();
    }

    private void Redial(String Msg) {

        final AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.alertDialog);
        builder.setMessage(Msg)
                .setCancelable(false)
                .setPositiveButton("Close", new DialogInterface.OnClickListener() {
                    public void onClick(final DialogInterface dialog, final int id) {
                        dialog.dismiss();
                        // GetPreviewCall(Util.getData("LEADID", getApplicationContext()), mobileno, Util.getData("CRMNAME", getApplicationContext()));
                    }
                });
        final AlertDialog alert = builder.create();
        alert.show();

    }

    @Override
    public void onBackPressed() {
        alert.build("Do logout to Exit App");
    }

}
