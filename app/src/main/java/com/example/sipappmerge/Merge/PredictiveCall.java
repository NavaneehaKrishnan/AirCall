package com.example.sipappmerge.Merge;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
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
import com.example.sipappmerge.Utils.CustomerModel;
import com.example.sipappmerge.Utils.MyApplication;
import com.example.sipappmerge.Utils.OnItemUpdateListener;
import com.example.sipappmerge.Utils.RadioAlertDialog;
import com.example.sipappmerge.Utils.UpdateService;
import com.example.sipappmerge.Utils.Util;
import com.example.sipappmerge.Utils.VolleyResponseListener;
import com.example.sipappmerge.adapter.PredictiveCusAdapter;
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
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import static com.example.sipappmerge.Utils.Util.AppErrorLog;
import static com.example.sipappmerge.Utils.Util.BASE;
import static com.example.sipappmerge.Utils.Util.Callcloseapi;
import static com.example.sipappmerge.Utils.Util.FetchCalls;
import static com.example.sipappmerge.Utils.Util.GetBreak;
import static com.example.sipappmerge.Utils.Util.GetCustomerData;
import static com.example.sipappmerge.Utils.Util.GetCustomerData_v1;
import static com.example.sipappmerge.Utils.Util.GetDispo;
import static com.example.sipappmerge.Utils.Util.HangupCalls;
import static com.example.sipappmerge.Utils.Util.LOGOUTUSER;
import static com.example.sipappmerge.Utils.Util.Ready;
import static com.example.sipappmerge.Utils.Util.recording;

public class PredictiveCall extends AppCompatActivity implements View.OnClickListener {
    private TextView txtOne,txtCustomerDetailsTitle;
    private BroadcastReceiver updateUIReceiver;
    private RelativeLayout rlLogout;
    private CommonAlertDialog alert;;
    private Intent serviceIntent;
    private LinearLayout llParent,cusLinear,lybreak,lyready,lylogout;

    private ArrayList<ListItemBean> customerDetails;
    private RecyclerViewAdapter availMeetingsAdapter;
    private ArrayList<ListItemBean> finalList;
    private RecyclerView recyclerView;
    private ListView listview;
    private PredictiveCusAdapter adapter;
    private String leadid="", mobileno="", referNum="";
    private Button hangUp_call;
    Spinner SpinDispo, SpinDispoSub, SpinMobileno;
    List<String> spinnerlist, spinnerlistSub;
    String StrDispo = "", StrDispoSub = "", StrAltMobileno = "";
    LinearLayout lydate, lydatetime, BtnRefresh, mobilely,llDespoDetails;
    JSONObject RESPONSE;
    RelativeLayout lysubdispo;
    TextView label, edit_date, edit_datetime;
    EditText edit_number, edit_text, EdComments;
    Button end_call;
    Calendar c;
    Integer mYear,mMonth,mDay;
    ImageView imgdate, imgdatetime, imgLogo;
    boolean isLoadedCusDetails;
    boolean isLoadedWrapUp;
    boolean isLogoutCalled;
    boolean isShowingLogout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_predictive_call);
        init();
    }

    private void init() {

        if (recording) {
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE);
        }

        alert = new CommonAlertDialog(this);

        txtOne = findViewById(R.id.txtOne);
        txtCustomerDetailsTitle = findViewById(R.id.txtCustomerDetailsTitle);
        rlLogout = findViewById(R.id.rlLogout);
        llParent = findViewById(R.id.llParent);
        cusLinear = findViewById(R.id.cusLinear);

        rlLogout.setOnClickListener(this);

        serviceIntent = new Intent(PredictiveCall.this, UpdateService.class);
        detailInit();

    }

    private void detailInit() {
        c = Calendar.getInstance();
        mYear = c.get(Calendar.YEAR);
        mMonth = c.get(Calendar.MONTH);
        mDay = c.get(Calendar.DAY_OF_MONTH);
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
        end_call = findViewById(R.id.end_call);

        SpinDispo = findViewById(R.id.dispo_spinner);
        SpinDispoSub = findViewById(R.id.sub_dispo_spinner);
        SpinMobileno = findViewById(R.id.mobile_spinner);

        imgLogo = findViewById(R.id.imgLogo);
        edit_number = findViewById(R.id.edit_number);
        edit_text = findViewById(R.id.edit_text);
        EdComments = findViewById(R.id.comments);
        edit_date = findViewById(R.id.edit_date);
        edit_datetime = findViewById(R.id.edit_datetime);
        spinnerlist = new ArrayList<>();
        spinnerlist.add("Select");
        spinnerlistSub = new ArrayList<>();
        alert = new CommonAlertDialog(this);
        label = findViewById(R.id.label);
        llDespoDetails = findViewById(R.id.llDespoDetails);

        lydate = findViewById(R.id.lydate);
        lydatetime = findViewById(R.id.lydatetime);
        lysubdispo = findViewById(R.id.lysubdispo);
        imgdate = findViewById(R.id.imgdate);
        imgdatetime = findViewById(R.id.imgdatetime);
        lysubdispo = findViewById(R.id.lysubdispo);
        hangUp_call = findViewById(R.id.hangUp_call);
        lybreak = findViewById(R.id.lybreak);
        lyready = findViewById(R.id.lyready);
        lylogout = findViewById(R.id.lylogout);
        imgdate.setOnClickListener(this);
        imgdatetime.setOnClickListener(this);
        hangUp_call.setOnClickListener(this);
        lyready.setOnClickListener(this);
        lybreak.setOnClickListener(this);
        lylogout.setOnClickListener(this);
        end_call.setOnClickListener(this);
        GetDispo();
        hangUp_call.setVisibility(View.GONE);
        intentFilter();
        startService(serviceIntent);


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
                            (PredictiveCall.this, R.layout.spinner_textview,
                                    spinnerlistSub);
                    spinnerArrayAdapter.setDropDownViewResource(android.R.layout
                            .simple_spinner_dropdown_item);
                    SpinDispoSub.setAdapter(spinnerArrayAdapter);
                }
            });
        } catch (JSONException e) {

        }

    }
    private void GetDispo() {

        try {
            JSONObject params = new JSONObject();
            // params.put("action", "Get_Disposition");
            //params.put("action", "Get_CRM_Dispo");
            //params.put("action", "Get_Dynamic_CRM_Dispo");
            params.put("CRM_id", Util.getData("crm_id", getApplicationContext()));
            params.put("agent_id", Util.getData("AgentName", getApplicationContext()));
            //Util.getData("crm_name",getApplicationContext());
            Log.e("GET DISPO", GetDispo + "\n" + params.toString());
            CallApi.postResponse(PredictiveCall.this, params.toString(), GetDispo, new VolleyResponseListener() {
                @Override
                public void onError(String message) {
                    Util.Logcat.e("onError" + message);
                }

                @Override
                public void onResponse(JSONObject response) {
                    Util.Logcat.e("GET DISPO" + response);
                    try {

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
                                        (PredictiveCall.this, R.layout.spinner_textview,
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
    private void LoadAccDetails(String MobileNo,String leadId) {
        llParent.removeAllViews();
        customerDetails = new ArrayList<ListItemBean>();
        try {
            JSONObject params = new JSONObject();
            params.put("Mobile_no", MobileNo);
            params.put("agent_id", Util.getData("AgentName", getApplicationContext()));
            params.put("lead_id", leadId);
            Log.e("GetCustomerData", GetCustomerData_v1 + "\n" + params.toString());

            CallApi.postResponse(PredictiveCall.this, params.toString(), GetCustomerData_v1, new VolleyResponseListener() {
                @Override
                public void onError(String message) {
                    Util.Logcat.e("onError" + message);
                }

                @Override
                public void onResponse(JSONObject response) {
                    Util.Logcat.e("GetCustomerData" + response);
                    try {
                        String data = response.getString("ResponseData");
                        Log.e("data",data);
                        JSONArray array = new JSONObject(data).optJSONArray("Values");

                        if(array.length()>0)
                        {
                            for(int k=0;k<array.length();k++)
                            {
                                JSONObject jsonObject = array.getJSONObject(k);
                                ListItemBean customerModel = new ListItemBean();
                                customerModel.setKey(jsonObject.getString("key"));
                                customerModel.setValue(jsonObject.getString("value"));
                                customerModel.setIs_edit(jsonObject.getString("is_edit"));
                                customerModel.setType(jsonObject.getString("Type"));
                                customerModel.setOption(jsonObject.getString("option"));
                                customerDetails.add(customerModel);
                            }
                        }

                        llParent.removeAllViews();
                        /*runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                if (customerDetails.size() > 0) {
                                    for(int i=0;i<customerDetails.size();i++)
                                    {
                                        LayoutInflater inflater = LayoutInflater.from(PredictiveCall.this);

                                        View inflatedLayout= inflater.inflate(R.layout.customer_details_item, llParent, false);
                                        TextView txt = inflatedLayout.findViewById(R.id.txt);
                                        TextView txt_value = inflatedLayout.findViewById(R.id.txt_value);
                                        txt.setText(customerDetails.get(i).getStrTitle());
                                        txt_value.setText(customerDetails.get(i).getStrValue().equals("NA")?"-":customerDetails.get(i).getStrValue());
                                        llParent.addView(inflatedLayout);
                                    }
                                    *//*adapter = new PredictiveCusAdapter(PredictiveCall.this, customerDetails);
                                    listview.setAdapter(adapter);
                                    setListViewHeightBasedOnItems(listview);*//*
                                } else {
                                }
                            }
                        });*/
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {

                                if (customerDetails.size() > 0) {
                                    //   BtnRefresh.setVisibility(View.GONE);
                                    /*adapter = new CallAdapterPreview(CallDispo.this, ListCollection);
                                    listview.setAdapter(adapter);
                                    setListViewHeightBasedOnItems(listview);*/
                                    availMeetingsAdapter = new RecyclerViewAdapter(customerDetails, PredictiveCall.this);
                                    recyclerView.setHasFixedSize(true);
                                    recyclerView.setLayoutManager(new LinearLayoutManager(PredictiveCall.this));
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
    private void intentFilter() {
        IntentFilter filter = new IntentFilter();

        filter.addAction("com.app.predictive.action");

        updateUIReceiver = new BroadcastReceiver() {

            @Override
            public void onReceive(Context context, Intent intent) {
                //Break00:02:38#@@#2020-10-09 15:09:56^1-predictive_test1^^^#@@##FF0000
                Log.e("onReceive",intent.getStringExtra("response"));
                try {
                    JSONObject jsonObject = new JSONObject(intent.getStringExtra("response"));

                    if(jsonObject.getString("Status").equals("LO001"))
                    {
                        imgLogo.setVisibility(View.GONE);
                        String[] splitData = jsonObject.getString("MESSAGE").split("#@@#");
                        txtOne.setText(splitData[0]+"\n"+splitData[1].substring(0,19));
                        if(jsonObject.getString("MESSAGE").contains("DIALING") || jsonObject.getString("MESSAGE").equals("DIALING")||jsonObject.getString("MESSAGE").contains("ONCALL") || jsonObject.getString("MESSAGE").equals("ONCALL")||jsonObject.getString("MESSAGE").contains("WRAPUP ") || jsonObject.getString("MESSAGE").equals("WRAPUP ")||jsonObject.getString("MESSAGE").contains("RINGING ") || jsonObject.getString("MESSAGE").equals("RINGING "))
                        {//ONCALL 00:00:33~8056760909~predictive_test~6030951~Predictive~none~n~#@@#2020-10-12 12:01:06^1-predictive_test1^1-predictive_test1^^^33^^6030951^agent_6
                            String[] splitDas = jsonObject.getString("MESSAGE").split("~");
                            txtOne.setText(splitDas[0]+"\n"+splitDas[7].substring(4,23));
                            if(!isLoadedCusDetails && (!jsonObject.getString("MESSAGE").contains("WRAPUP ") && !jsonObject.getString("MESSAGE").equals("WRAPUP "))) {
                                mobileno = splitDas[1];
                                leadid = splitDas[3];
                                referNum = jsonObject.getString("refno");
                                LoadAccDetails(splitDas[1], splitDas[3]);
                                hangUp_call.setVisibility(View.VISIBLE);
                                txtCustomerDetailsTitle.setVisibility(View.VISIBLE);
                                cusLinear.setVisibility(View.VISIBLE);
                                imgLogo.setVisibility(View.GONE);
                                llDespoDetails.setVisibility(View.VISIBLE);
                                isLoadedCusDetails = true;
                                isLoadedWrapUp  = true;

                            }else if(jsonObject.getString("MESSAGE").contains("WRAPUP ") || jsonObject.getString("MESSAGE").equals("WRAPUP ")) {
                                llDespoDetails.setVisibility(View.VISIBLE);
                                imgLogo.setVisibility(View.GONE);
                                hangUp_call.setVisibility(View.GONE);
                                isLoadedWrapUp  = true;
                            }
                        }else {
                            if( !isLoadedWrapUp) {
                                hangUp_call.setVisibility(View.GONE);
                                llDespoDetails.setVisibility(View.GONE);
                                txtCustomerDetailsTitle.setVisibility(View.GONE);
                                cusLinear.setVisibility(View.GONE);
                                imgLogo.setVisibility(View.GONE);
                                isLoadedCusDetails = false;
                                isLoadedWrapUp = false;
                            }
                        }
                        if(jsonObject.getString("MESSAGE").contains("preview") || jsonObject.getString("MESSAGE").equals("preview"))
                        {

                            //finish();
                            navigationAlert("preview");
                        }else if(jsonObject.getString("MESSAGE").contains("progressive") || jsonObject.getString("MESSAGE").equals("progressive"))
                        {
                            Intent main = new Intent(PredictiveCall.this, ProgressiveCall.class);
                            startActivity(main);
                            navigationAlert("progressive");
                            //finish();
                        }

                    }
                    if(!isLogoutCalled&&jsonObject.getString("MESSAGE").contains("LOGGEDOUT") || jsonObject.getString("MESSAGE").equals("LOGGEDOUT") && !isLogoutCalled) {

                        alertLogout("Yes");
                    }
                    else if((jsonObject.getString("MESSAGE").contains("IDLE") || jsonObject.getString("MESSAGE").equals("IDLE")||jsonObject.getString("MESSAGE").contains("Break") || jsonObject.getString("MESSAGE").equals("Break")) && !isLoadedWrapUp) {
                            llDespoDetails.setVisibility(View.GONE);
                            txtCustomerDetailsTitle.setVisibility(View.GONE);
                            cusLinear.setVisibility(View.GONE);
                            imgLogo.setVisibility(View.VISIBLE);
                            llParent.removeAllViews();
                            isLoadedWrapUp = false;
                        }
                } catch (JSONException e) {
                    e.printStackTrace();
                }


            }
        };
        registerReceiver(updateUIReceiver,filter);
    }

    private void navigationAlert(final String callMode) {
        AlertDialog.Builder builder = new AlertDialog.Builder(PredictiveCall.this,     R.style.alertDialog);
        // builder.setTitle(title);
        builder.setMessage("Call mode has been Changed to "+callMode+".");
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                Intent main = new Intent(PredictiveCall.this, callMode.equals("preview")?NewModel.class:ProgressiveCall.class);
                startActivity(main);
            }
        });
        AlertDialog alert = builder.create();
        alert.setCancelable(false);
        alert.show();
    }

    private void HangupCalls() {
        try {
            JSONObject params = new JSONObject();
            params.put("agent_id", Util.getData("AgentName", getApplicationContext()));
            params.put("username", Util.getData("AgentName", getApplicationContext()));
            params.put("password", Util.getData("password", getApplicationContext()));
            params.put("refno", referNum);
            params.put("phone_number", Util.getData("mobile_no",getApplicationContext()));
            params.put("process_name", Util.getData("process_name", getApplicationContext()));
            params.put("process_agent", Util.getData("AgentName", getApplicationContext()));
            params.put("convoxid",leadid);
            params.put("Local_IP", "");
            params.put("crm_id", Util.getData("crm_id", getApplicationContext()));
            params.put("mobile_number", mobileno);
            Log.e("API", HangupCalls+ "\n" + params.toString());

            CallApi.READYNOPROGRESS(getApplicationContext(), params.toString(), HangupCalls, new VolleyResponseListener() {

                @Override
                public void onError(String message) {
                    Util.Logcat.e("HangupCalls onError" + message);
                }

                @Override
                public void onResponse(JSONObject response) {
                    Util.Logcat.e("HangupCalls Response" + response);
//

                }
            });

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
    public void onDestroy()
    {
        super.onDestroy();
        unregisterReceiver(updateUIReceiver);
        stopService(serviceIntent);
    }

    @Override
    public void onClick(View view) {

        switch (view.getId())
        {
            case R.id.lylogout:
                if(!txtOne.getText().toString().contains("DIALING") && !txtOne.getText().toString().contains("RINGING")&&!txtOne.getText().toString().contains("ONCALL") && !txtOne.getText().toString().contains("WRAPUP"))
                    alertLogout("No");
                break;

            case R.id.lybreak:
                if(!txtOne.getText().toString().contains("DIALING") && !txtOne.getText().toString().contains("RINGING")&&!txtOne.getText().toString().contains("ONCALL") && !txtOne.getText().toString().contains("WRAPUP")) {
                    if (Util.getData("BREAK", getApplicationContext()).isEmpty())
                        LoadBreakPopup();
                    else
                        Toast.makeText(PredictiveCall.this, "You are in " + Util.getData("BREAK", getApplicationContext()) + " Mode.", Toast.LENGTH_SHORT).show();
                }
                break;

            case R.id.lyready:
                if(!Util.getData("BREAK", getApplicationContext()).isEmpty())
                    ReleaseBreak("Ready");
                else
                    Toast.makeText(PredictiveCall.this,"You are in Release Mode.",Toast.LENGTH_SHORT).show();
                break;
                
            case R.id.hangUp_call:
                HangupCalls();
                break;

            case R.id.end_call:
                if(!txtOne.getText().toString().isEmpty()&&txtOne.getText().toString().contains("WRAPUP")) {
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
                }else{
                    alert.build("You can end call only in wrap up mode.");
                }
                break;
            case R.id.imgdate:
                DatePickerDialog datepicker = new DatePickerDialog(PredictiveCall.this, R.style.DatePickerDialogTheme, new DatePickerDialog.OnDateSetListener() {
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

                DatePickerDialog datePickerDialog = new DatePickerDialog(PredictiveCall.this, R.style.DatePickerDialogTheme, new DatePickerDialog.OnDateSetListener() {
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
                        TimePickerDialog timePickerDialog = new TimePickerDialog(PredictiveCall.this, R.style.DatePickerDialogTheme, new TimePickerDialog.OnTimeSetListener() {
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
        }
    }
    private void EndCall() {

       /* for (int i = 0; i < listview.getAdapter().getCount(); i++) {
            View data = listview.getChildAt(i);
            TextView txtview = data.findViewById(R.id.txt);
            EditText edtext = data.findViewById(R.id.txt_value);
            //key~value#key~value#
            sb.append(txtview.getText().toString() + "~").append(edtext.getEditableText().toString() + "#");

        }*/

        //
        try {
           /* StringBuilder sb = new StringBuilder();
            for (int i = 0; i < ListCollection.size(); i++) {
                View view = listview.getChildAt(i);
                TextView key = view.findViewById(R.id.txt);
                EditText value = view.findViewById(R.id.txt_value);
                Log.e("Position", String.valueOf(i) + key.getText().toString() + ":" + value.getEditableText().toString());
                //key~value#key~value#
                if (key != null && value != null) {
                    sb.append(key.getText().toString() + "~").append(value.getEditableText().toString() + "#");
                }

            }
            String crm_info = sb.toString();
            Log.e("crm_info", crm_info);*/
            JSONObject params = new JSONObject();

            params.put("endcall_type", "CLOSE");
            params.put("refno", "");
            params.put("process_name", "");
            params.put("crm_info", "");
            String dynamicdispo = edit_date.getText().toString() + edit_datetime.getText().toString() + edit_number.getEditableText().toString() + edit_text.getEditableText().toString();
            params.put("disposition", StrDispo + "~" + StrDispoSub + "~" + dynamicdispo);
            params.put("list_comments", EdComments.getEditableText().toString());
            params.put("convoxid", leadid);
            params.put("mobile_number", mobileno);
            params.put("agent_id", Util.getData("AgentName", getApplicationContext()));
            params.put("process_agent", Util.getData("AgentName", getApplicationContext()));
            // alert.build(params.toString());
            CallApi.postResponse(PredictiveCall.this, params.toString(), Callcloseapi, new VolleyResponseListener() {
                @Override
                public void onError(String message) {
                    Util.Logcat.e("EndCall onError" + message);
                }

                @Override
                public void onResponse(JSONObject response) {
                    Util.Logcat.e("EndCall" + response);
                    try {
                        final JSONObject resobject = response;
                        if (resobject.getString("Status").equalsIgnoreCase("EC00")) {
                            //Toast.makeText(CallDispo.this, resobject.getString("MESSAGE"), Toast.LENGTH_LONG);
                            //alert.build(resobject.getString("MESSAGE"));
                            //if (mobilely.getVisibility() == View.VISIBLE && FLAG == 0) {
                            isLoadedWrapUp  = false;
                            llParent.removeAllViews();
                            SpinDispo.setSelection(0);
                            SpinDispoSub.setSelection(0);
                            EdComments.setText("");
                            edit_date.setText("");
                            edit_datetime.setText("");
                            edit_number.setText("");
                            edit_text.setText("");
                            //label.setText("");
                            alert.build(resobject.getString("StatusDesc"));
                            updateHangupCalls();
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
            Log.e("updateHangupCalls", FetchCalls+ "\n" + params.toString());

            CallApi.READYNOPROGRESS(getApplicationContext(), params.toString(), FetchCalls, new VolleyResponseListener() {

                @Override
                public void onError(String message) {
                    Util.Logcat.e("FetchCalls onError" + message);


                }

                @Override
                public void onResponse(JSONObject jsonObject) {
                    Log.e("updateHangupCalls",jsonObject.toString());
                    try {
                        if (jsonObject.getString("Status").equals("LO001")) {
                            imgLogo.setVisibility(View.GONE);
                            String[] splitData = jsonObject.getString("MESSAGE").split("#@@#");
                            txtOne.setText(splitData[0] + "\n" + splitData[1].substring(0, 19));
                            if (jsonObject.getString("MESSAGE").contains("DIALING") || jsonObject.getString("MESSAGE").equals("DIALING") || jsonObject.getString("MESSAGE").contains("ONCALL") || jsonObject.getString("MESSAGE").equals("ONCALL") || jsonObject.getString("MESSAGE").contains("WRAPUP ") || jsonObject.getString("MESSAGE").equals("WRAPUP ") || jsonObject.getString("MESSAGE").contains("RINGING ") || jsonObject.getString("MESSAGE").equals("RINGING ")) {//ONCALL 00:00:33~8056760909~predictive_test~6030951~Predictive~none~n~#@@#2020-10-12 12:01:06^1-predictive_test1^1-predictive_test1^^^33^^6030951^agent_6
                                String[] splitDas = jsonObject.getString("MESSAGE").split("~");
                                txtOne.setText(splitDas[0] + "\n" + splitDas[7].substring(4, 23));
                                if (!jsonObject.getString("MESSAGE").contains("WRAPUP ") && !jsonObject.getString("MESSAGE").equals("WRAPUP ")){
                                    mobileno = splitDas[1];
                                    leadid = splitDas[3];
                                    referNum = jsonObject.getString("refno");
                                    LoadAccDetails(splitDas[1], splitDas[3]);
                                    hangUp_call.setVisibility(View.VISIBLE);
                                    txtCustomerDetailsTitle.setVisibility(View.VISIBLE);
                                    cusLinear.setVisibility(View.VISIBLE);
                                    imgLogo.setVisibility(View.GONE);
                                    llDespoDetails.setVisibility(View.VISIBLE);
                                    isLoadedCusDetails = true;
                                    isLoadedWrapUp = true;

                                } else if (jsonObject.getString("MESSAGE").contains("WRAPUP ") || jsonObject.getString("MESSAGE").equals("WRAPUP ")) {
                                    llDespoDetails.setVisibility(View.VISIBLE);
                                    imgLogo.setVisibility(View.GONE);
                                    hangUp_call.setVisibility(View.GONE);
                                    isLoadedWrapUp = true;
                                }
                            } else {
                                    hangUp_call.setVisibility(View.GONE);
                                    llDespoDetails.setVisibility(View.GONE);
                                    txtCustomerDetailsTitle.setVisibility(View.GONE);
                                    cusLinear.setVisibility(View.GONE);
                                    imgLogo.setVisibility(View.GONE);
                                    isLoadedCusDetails = false;
                                    isLoadedWrapUp = false;

                            }
                            if (jsonObject.getString("MESSAGE").contains("preview") || jsonObject.getString("MESSAGE").equals("preview")) {

                                //finish();
                                navigationAlert("preview");
                            } else if (jsonObject.getString("MESSAGE").contains("progressive") || jsonObject.getString("MESSAGE").equals("progressive")) {
                                Intent main = new Intent(PredictiveCall.this, ProgressiveCall.class);
                                startActivity(main);
                                navigationAlert("progressive");
                                //finish();
                            }

                        }
                        if (jsonObject.getString("MESSAGE").contains("LOGGEDOUT") || jsonObject.getString("MESSAGE").equals("LOGGEDOUT")) {

                            alertLogout("Yes");
                        } else if ((jsonObject.getString("MESSAGE").contains("IDLE") || jsonObject.getString("MESSAGE").equals("IDLE") || jsonObject.getString("MESSAGE").contains("Break") || jsonObject.getString("MESSAGE").equals("Break"))) {
                            llDespoDetails.setVisibility(View.GONE);
                            txtCustomerDetailsTitle.setVisibility(View.GONE);
                            cusLinear.setVisibility(View.GONE);
                            imgLogo.setVisibility(View.VISIBLE);
                            llParent.removeAllViews();
                            isLoadedWrapUp = false;
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
    private void LoadBreakPopup() {

        try {
            JSONObject params = new JSONObject();

            params.put("CRM_id",  Util.getData("crm_id", getApplicationContext()));
            params.put("agent_id",  Util.getData("AgentName", getApplicationContext()));
            Util.Logcat.e("API GetBreak" + GetBreak+", Params : "+params.toString());
            CallApi.postResponse(PredictiveCall.this, params.toString(), GetBreak, new VolleyResponseListener() {
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
                        String data = response.getString("ResponseData");
                        JSONObject dat = new JSONObject(data);
                        JSONArray st = dat.getJSONArray("Break");
                        // showpopup(st);
                        RadioAlertDialog alert = new RadioAlertDialog(PredictiveCall.this);
                        alert.build(st);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });

        } catch (JSONException e) {
            e.printStackTrace();
        }

    }
    private void ReleaseBreak(String action) {

        try {
            JSONObject params = new JSONObject();

            params.put("Break", Util.getData("BREAK", getApplicationContext()));
            params.put("agent_id", Util.getData("AgentName", getApplicationContext()));
            Util.saveData("BREAK", "", getApplicationContext());
            CallApi.postResponse(PredictiveCall.this, params.toString(), Ready, new VolleyResponseListener() {
                @Override
                public void onError(String message) {

                    Util.Logcat.e("onError" + message);
                }

                @Override
                public void onResponse(JSONObject response) {
                    Util.Logcat.e("RELEASE BREAK" + response);
                    try {
                        JSONObject resobject = response;
                        JSONArray array = new JSONArray(resobject.getString("ResponseData"));

                        JSONObject object;
                        Map<String, String> map = null;
                        //  StringBuilder values=new StringBuilder();
                        for (int i = 0; i < array.length(); i++) {
                            object = new JSONObject(array.getJSONObject(i).toString());
                            map = new HashMap<String, String>();
                            Iterator<?> iter = object.keys();
                            while (iter.hasNext()) {
                                String key = (String) iter.next();
                                String value = object.getString(key);


                                map.put(key, value);
                            }
                            System.out.println(map.toString());
                        }
                        if (map.get("Status").equalsIgnoreCase("0")) {
                            alert.build(array.getJSONObject(0).getString("Statusdesc"));
                            Util.saveData("BREAK", "", getApplicationContext());
                        } else {
                            alert.build(array.getJSONObject(0).getString("Statusdesc"));
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

    private void alertLogout(final String isForceLogout)
    {

        androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(PredictiveCall.this,     R.style.alertDialog);
        // builder.setTitle(title);
        builder.setMessage(isForceLogout.equals("No")?"Are you sure to logout?":"Session Out.");
        builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                isLogoutCalled = true;
                logout();
                /*if(!isForceLogout.equalsIgnoreCase("Yes"))

                else
                {
                    Util.saveData("username", "", getApplicationContext());
                    Util.saveData("password", "", getApplicationContext());
                    Util.saveData("AgentName", "", getApplicationContext());
                    Util.saveData("mobile_no", "", getApplicationContext());
                    Util.saveData("crm_id", "", getApplicationContext());
                    Util.saveData("callmode", "", getApplicationContext());
                    Util.saveData("process_name", "", getApplicationContext());
                    Toast.makeText(PredictiveCall.this,"Successfully Logged Out.", Toast.LENGTH_LONG);
                    Intent main = new Intent(PredictiveCall.this, Login.class);
                    main.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(main);
                    finish();
                }*/

            }
        });
        if(isForceLogout.equals("No")) {
            builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });
        }
        androidx.appcompat.app.AlertDialog alert = builder.create();
        alert.setCancelable(false);
        alert.show();

    }
    private void logout() {
        try {
            JSONObject params = new JSONObject();

            params.put("user_type", "Agent");
            params.put("agent_id",  Util.getData("AgentName", getApplicationContext()));
            params.put("user", Util.getData("AgentName", getApplicationContext()));
            Util.Logcat.e("API" + LOGOUTUSER+", params:"+params.toString());
            CallApi.postResponse(PredictiveCall.this, params.toString(), LOGOUTUSER, new VolleyResponseListener() {
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
                            Toast.makeText(PredictiveCall.this,"User Successfully Logged Out.", Toast.LENGTH_LONG);
                            Intent main = new Intent(PredictiveCall.this, Login.class);
                            main.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            startActivity(main);
                            finish();

                        } else {
                            Util.saveData("username", "", getApplicationContext());
                            Util.saveData("password", "", getApplicationContext());
                            Util.saveData("AgentName", "", getApplicationContext());
                            Util.saveData("mobile_no", "", getApplicationContext());
                            Util.saveData("crm_id", "", getApplicationContext());
                            Util.saveData("callmode", "", getApplicationContext());
                            Util.saveData("process_name", "", getApplicationContext());
                            Toast.makeText(PredictiveCall.this,"Successfully Logged Out.", Toast.LENGTH_LONG);
                            Intent main = new Intent(PredictiveCall.this, Login.class);
                            main.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            startActivity(main);
                            finish();
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

}