package com.example.sipappmerge.Merge;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.ContextMenu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
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
import com.example.sipappmerge.Utils.RadioAlertDialog;
import com.example.sipappmerge.adapter.CallAdapter;
import com.example.sipappmerge.Utils.CallApi;
import com.example.sipappmerge.Utils.CommonAlertDialog;
import com.example.sipappmerge.adapter.DashboardAdaptor;
import com.example.sipappmerge.Utils.ExpandableHeightGridView;
import com.example.sipappmerge.Utils.MyApplication;
import com.example.sipappmerge.R;
import com.example.sipappmerge.Utils.Util;
import com.example.sipappmerge.Utils.VolleyResponseListener;
import com.simplealertdialog.SimpleAlertDialog;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import static com.example.sipappmerge.Utils.Util.AppErrorLog;
import static com.example.sipappmerge.Utils.Util.BASE;

import static com.example.sipappmerge.Utils.Util.GetBreak;
import static com.example.sipappmerge.Utils.Util.GetDashboard;
import static com.example.sipappmerge.Utils.Util.GetData;
import static com.example.sipappmerge.Utils.Util.LOGOUTUSER;
import static com.example.sipappmerge.Utils.Util.Ready;
import static com.example.sipappmerge.Utils.Util.recording;

public class NewModel extends BaseActivity implements View.OnClickListener, SimpleAlertDialog.OnItemClickListener {
    private HashMap<String, String> DataHashMap, DDataHashMap;
    private List<Map<String, String>> ListCollection, DListCollection;
    ListView listview;
    CallAdapter adapter;
    private static final int LOGOUT = 5;
    CommonAlertDialog alert;
    ExpandableHeightGridView gridview;
    public DashboardAdaptor dashboardadapter;
    TextView TxtTitle;
    LinearLayout LyBreak, LyReady, LyLogout,lyChatBot;
    private RelativeLayout rlResetPassword;
    private boolean isCalled;

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.new_model);
        listview = findViewById(R.id.collect_listview);
        if (recording) {
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE);
        }//Thread.setDefaultUncaughtExceptionHandler(new CustomizedExceptionHandler(Environment.getExternalStorageDirectory() + "/ARM/"));
        ListCollection = new ArrayList<>();
        DListCollection = new ArrayList<>();
        alert = new CommonAlertDialog(this);
        rlResetPassword = findViewById(R.id.rlResetPassword);
        gridview = findViewById(R.id.grid);
        TxtTitle = findViewById(R.id.title);

        LyBreak = findViewById(R.id.lybreak);
        LyBreak.setOnClickListener(this);
        LyReady = findViewById(R.id.lyready);
        LyReady.setOnClickListener(this);
        LyLogout = findViewById(R.id.lylogout);
        LyLogout.setOnClickListener(this);
        lyChatBot = findViewById(R.id.lyChatBot);
        lyChatBot.setOnClickListener(this);
        rlResetPassword.setOnClickListener(this);

        gridview.setExpanded(true);


        //updateStatus("");
        //updateDataSpeed("");
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

                        UpdateError(ex.toString());
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } finally {
                    defHandler.uncaughtException(t, ex);
                }
            }
        });

        GetDashBoard();
       /* if(Util.dodont) {
            Util.dodont=false;
            Intent faq = new Intent(this, FAQActivity.class);
            faq.putExtra("url", Util.DODONT);
            faq.putExtra("btnname", "Agree and Continue");
            startActivity(faq);
        }*/
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

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.rlResetPassword:
                if (!Util.getData("BREAK", getApplicationContext()).isEmpty()) {
                    String hai = "You are in " + Util.getData("BREAK", getApplicationContext()) + " mode. Please click ready button below to release your break";
                    alert.build(hai);
                } else {
                    startActivity(new Intent(NewModel.this,ChangePassword.class).putExtra("isFirstLogin","no"));
                }

                break;
            case R.id.lybreak:
                if(Util.getData("BREAK", getApplicationContext()).isEmpty())
                    LoadBreakPopup();
                break;
            case R.id.lyready:
                if(!Util.getData("BREAK", getApplicationContext()).isEmpty())
                    ReleaseBreak("Ready");

                break;

            case R.id.lyChatBot:
                startActivity(new Intent(NewModel.this,ChatBot.class));
                break;
            case R.id.lylogout:
                if (!Util.getData("BREAK", getApplicationContext()).isEmpty()) {
                    String hai = "You are in " + Util.getData("BREAK", getApplicationContext()) + " mode. Please click ready button below to release your break";
                    alert.build(hai);
                } else {
                    Logout();
                }
                break;
            default:
                break;

        }
    }

    @Override
    public void onBackPressed() {
        alert.build("Do logout to Exit App");
    }

    private void LoadBreakPopup() {

        try {
            JSONObject params = new JSONObject();

            params.put("CRM_id",  Util.getData("crm_id", getApplicationContext()));
            params.put("agent_id",  Util.getData("AgentName", getApplicationContext()));
            Util.Logcat.e("API GetBreak" + GetBreak+", Params : "+params.toString());
            CallApi.postResponse(NewModel.this, params.toString(), GetBreak, new VolleyResponseListener() {
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
                        RadioAlertDialog alert = new RadioAlertDialog(NewModel.this);
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

    //d_columninfo
    private void ReleaseBreak(String action) {

        try {
            JSONObject params = new JSONObject();

            params.put("Break", Util.getData("BREAK", getApplicationContext()));
            params.put("agent_id", Util.getData("AgentName", getApplicationContext()));
            Util.saveData("BREAK", "", getApplicationContext());
            CallApi.postResponse(NewModel.this, params.toString(), Ready, new VolleyResponseListener() {
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
               /* @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    Map<String, String>  params = new HashMap<String, String>();
                    params.put("Content-Type","application/json");
                    params.put("access_token","PROD_MIAGCSqGSIb3DQEHAqCAMIACAQExC");
                    return params;
                }*/
        };

          /* req.setRetryPolicy(new DefaultRetryPolicy(30000,
                    0,
                    DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
            //Creating request queue
            RequestQueue requestQueue = Volley.newRequestQueue(context);
            //Adding request to the queue
            requestQueue.add(req);*/
        MyApplication.getInstance().getRequestQueue().add(req);

    }

    @Override
    public void onResume() {
        super.onResume();
       // GetDashBoard();
        // GetCalllist();
    }

    private void GetDashBoard() {
        DListCollection.clear();
        try {
            JSONObject params = new JSONObject();
            params.put("agent_id", Util.getData("AgentName", getApplicationContext()));

            CallApi.postResponse(NewModel.this, params.toString(), GetDashboard, new VolleyResponseListener() {
                @Override
                public void onError(String message) {
                    Util.Logcat.e("onError" + message);
                   /* if(message.contains("TimeoutError")){
                        alert.build("Time out");
                    }else if(message.contains("Connection Reset")){
                        alert.build("Connection reset");
                    }else {
                        alert.build("Server Error");//
                    }*/

                    if(!isCalled) {
                        isCalled = true;
                        refreshdashboard();
                    }
                }

                @Override
                public void onResponse(JSONObject response) {
                    Util.Logcat.e("onResponse" + response);

                    if (!Util.getData("BREAK", getApplicationContext()).equals("") && !Util.getData("BREAK", getApplicationContext()).isEmpty()) {

                        Toast.makeText(getApplicationContext(),Util.getData("BREAK", getApplicationContext())+"-Stored.",Toast.LENGTH_SHORT).show();
                        alert.build("You already in break. Please click ready to resume work");
                    }
                    try {
                        JSONObject resobject = response;
                        JSONArray array = new JSONArray(resobject.getString("ResponseData"));


                        if (array.length() > 0) {
                            JSONObject object;
                            Map<String, String> map;
                            //  StringBuilder values=new StringBuilder();
                            for (int i = 0; i < array.length(); i++) {
                                object = new JSONObject(array.getJSONObject(i).toString());
                                map = new HashMap<String, String>();
                                Iterator<?> iter = object.keys();
                                while (iter.hasNext()) {
                                    String key = (String) iter.next();
                                    String value = object.getString(key);
                                    DDataHashMap = new HashMap<String, String>();
                                    DDataHashMap.put("key", key);
                                    DDataHashMap.put("value", value);
                                    //  values.append(key).append(":").append(value).append("\n");
                                    DListCollection.add(DDataHashMap);

                                    map.put(key, value);
                                    if(key.equalsIgnoreCase("New"))
                                        GetCalllist(key);
                                }
                                System.out.println(map.toString());
                            }
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    if (DListCollection.size() > 0) {
                                        dashboardadapter = new DashboardAdaptor(NewModel.this, DListCollection);
                                        gridview.setAdapter(dashboardadapter);
                                    } else {
                                        refreshdashboard();
                                        //txtnodata.setVisibility(View.VISIBLE);
                                    }
                                }
                            });
                            gridview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                @Override
                                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                    //GetDashBoard();
                                    ListCollection.clear();
                                    GetCalllist(((TextView) view.findViewById(R.id.name)).getText().toString());
                                    TxtTitle.setText(((TextView) view.findViewById(R.id.name)).getText().toString());
                                }
                            });
                        } else {
                            alert.build("No data for Dashboard");
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

    private void GetCalllist(final String title) {
        ListCollection.clear();

        try {
            JSONObject params = new JSONObject();
            //params.put("action", "Get_Data");
            params.put("Dispo", title);
            params.put("agent_id", Util.getData("AgentName", getApplicationContext()));
            Util.Logcat.e(GetData+"\n "+params.toString());

            CallApi.postResponse(NewModel.this, params.toString(), GetData, new VolleyResponseListener() {
                @Override
                public void onError(String message) {
                    Util.Logcat.e("onError" + message);
                    refresh();
                }

                @Override
                public void onResponse(JSONObject response) {
                    Util.Logcat.e("onResponse" + response);
                    try {
                        JSONObject resobject = response;

                        JSONArray array = new JSONArray(resobject.getString("ResponseData"));
                        for (int i = 0; i < array.length(); i++) {

                            JSONObject jsonObject = array.getJSONObject(i);

                            DataHashMap = new HashMap<String, String>();
                            if (!jsonObject.optString("mobile_number").isEmpty()) {
                                DataHashMap.put("mobile_number", jsonObject.optString("mobile_number"));
                                DataHashMap.put("lead_id", jsonObject.optString("lead_id"));
                                //DataHashMap.put("status", jsonObject.optString("status"));
                                DataHashMap.put("process_name", jsonObject.optString("process_name"));
                                DataHashMap.put("crm_name", jsonObject.optString("crm_name"));
                                if (title.equalsIgnoreCase("ptp")) {
                                    DataHashMap.put("status", jsonObject.optString("ptpdatetime"));
                                } else if (title.equalsIgnoreCase("callback")) {
                                    DataHashMap.put("status", jsonObject.optString("callbackdatetime"));
                                } else {
                                    DataHashMap.put("status", "1");
                                }
                                ListCollection.add(DataHashMap);
                            }
                        }
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                if (ListCollection.size() > 0) {
                                    adapter = new CallAdapter(NewModel.this, ListCollection);
                                    listview.setAdapter(adapter);
                                } else {
                                    // refresh();
                                    if (adapter != null) {
                                        adapter.notifyDataSetChanged();
                                    }
                                    alert.build("No Data");
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

    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {

            case LOGOUT:
                //  GetStatus();
                //FAQ
                Intent faq = new Intent(NewModel.this, FAQActivity.class);
                faq.putExtra("url","http://123.63.108.199:36004/faq/");
                faq.putExtra("btnname", "Close");
                startActivity(faq);
                break;

        }
        return true;
    }



    private void Logout() {

        try {
            JSONObject params = new JSONObject();
            params.put("agent_id",  Util.getData("AgentName", getApplicationContext()));
            params.put("user_type", "Agent");
            params.put("user", Util.getData("AgentName", getApplicationContext()));
            Util.Logcat.e("API" + LOGOUTUSER+", params:"+params.toString());
            CallApi.postResponse(NewModel.this, params.toString(), LOGOUTUSER, new VolleyResponseListener() {
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
                            Toast.makeText(NewModel.this,"User Successfully Logged Out.", Toast.LENGTH_LONG);
                            Intent main = new Intent(NewModel.this, Login.class);
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

    private void LogoutKill() {

        try {
            JSONObject params = new JSONObject();

            params.put("user_type", "Agent");
            params.put("Agent", "");
            params.put("refno", "");
            params.put("user", Util.getData("AgentName", getApplicationContext()));
            params.put("password", "");
            params.put("station", "");

            CallApi.postResponse(NewModel.this, params.toString(), LOGOUTUSER, new VolleyResponseListener() {
                @Override
                public void onError(String message) {
                    Util.Logcat.e("onError" + message);
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
                            Toast.makeText(NewModel.this,"User Successfully Logged Out.", Toast.LENGTH_LONG);
                            Intent main = new Intent(NewModel.this, Login.class);
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

   /* public boolean onCreateOptionsMenu(Menu menu) {
        //   menu.add(0, CALL_ADDRESS, 0, "Call someone");
        // menu.add(0, SET_AUTH_INFO, 0, "Edit your SIP Info.");
        //  menu.add(0, HANG_UP, 0, "End Current Call.");
        menu.add(0, LOGOUT, 0, "FAQ");
        return true;

    }*/

    private void refresh() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.alertDialog);
        builder.setMessage("No Data. Please referesh to get dialer list")
                .setCancelable(false)
                .setPositiveButton("Reload", new DialogInterface.OnClickListener() {
                    public void onClick(@SuppressWarnings("unused") final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                        GetCalllist(TxtTitle.getText().toString());
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                        dialog.cancel();
                    }
                });
        final AlertDialog alert = builder.create();
        alert.show();
    }

    private void refreshdashboard() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.alertDialog);
        builder.setMessage("No Data. Please referesh to get Dashboard")
                .setCancelable(false)
                .setPositiveButton("Reload", new DialogInterface.OnClickListener() {
                    public void onClick(@SuppressWarnings("unused") final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                        GetDashBoard();
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                        dialog.cancel();
                    }
                });
        final AlertDialog alert = builder.create();
        alert.show();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        // LogoutKill();
        // startService(new Intent(this,CloseCall.class));
    }

    @Override
    public void onItemClick(SimpleAlertDialog dialog, int requestCode, int which) {

    }


    private void updateDataSpeed(String URL) {

        try {

            JSONObject params = new JSONObject();
            params.put("Event", "Flight Mode");
            params.put("TimeDuration", "10");
            params.put("AgentId",Util.getData("AgentName", getApplicationContext()) );



            CallApi.postResponse(NewModel.this, params.toString(), BASE+"Dialer/AppEvents", new VolleyResponseListener() {

                @Override
                public void onError(String message) {
                    Util.Logcat.e("updateDataSpeed onError" + message);
                   /* if (message.contains("TimeoutError")) {
                       // alert.build("Time out");
                       } else if (message.contains("Connection Reset")) {
                        alert.build("Connection reset");
                    } else {
                        alert.build("Server Error");//
                    }*/

                }

                @Override
                public void onResponse(JSONObject response) {
                    Util.Logcat.e("CallUpdateDataSpeed" + response);

                }
            });

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


}
