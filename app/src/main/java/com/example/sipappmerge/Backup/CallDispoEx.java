package com.example.sipappmerge.Backup;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Spinner;
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
import com.example.sipappmerge.Merge.NewModel;
import com.example.sipappmerge.Utils.CallApi;
import com.example.sipappmerge.Utils.CommonAlertDialog;
import com.example.sipappmerge.Utils.MyApplication;
import com.example.sipappmerge.R;
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
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import static com.example.sipappmerge.Utils.Util.AppErrorLog;
import static com.example.sipappmerge.Utils.Util.BASE;
import static com.example.sipappmerge.Utils.Util.Callcloseapi;
import static com.example.sipappmerge.Utils.Util.GetCustomerData;
import static com.example.sipappmerge.Utils.Util.GetDispo;
import static com.example.sipappmerge.Utils.Util.callapi;
import static com.example.sipappmerge.Utils.Util.recording;


public class CallDispoEx extends AppCompatActivity {

    Spinner SpinDispo, SpinDispoSub;
    private HashMap<String, String> DataHashMap;
    private List<Map<String, String>> ListCollection;
    ListView listview;
    CallAdapterPreview adapter;
    List<String> spinnerlist, spinnerlistSub;
    String StrDispo = "", StrDispoSub = "";
    EditText EdComments;
    CommonAlertDialog alert;
    String leadid, mobileno, crmname;
    Button BtnEndCall;
    JSONObject Subdispo;
    ImageView BtnRefresh;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.call_dispo);

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
        EdComments = findViewById(R.id.comments);
        BtnEndCall = findViewById(R.id.end_call);
        BtnRefresh = findViewById(R.id.btn_refresh);
        ListCollection = new ArrayList<Map<String, String>>();
        spinnerlist = new ArrayList<>();
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
                GetPreviewCall(Util.getData("LEADID", getApplicationContext()), Util.getData("MOBILENO", getApplicationContext()), Util.getData("CRMNAME", getApplicationContext()));
            }
        });

        SpinDispo.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            //@Override
            public void onItemSelected(AdapterView<?> parent, View arg1, int arg2, long arg3) {

                StrDispo = SpinDispo.getSelectedItem().toString();
                try {
                    JSONArray hai = Subdispo.getJSONArray(StrDispo);

                    Loadsubspinner(hai);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }

            public void onNothingSelected(AdapterView<?> arg0) {

            }
        });
        SpinDispoSub.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            //@Override
            public void onItemSelected(AdapterView<?> parent, View arg1, int arg2, long arg3) {
                StrDispoSub = SpinDispoSub.getSelectedItem().toString();
            }

            public void onNothingSelected(AdapterView<?> arg0) {

            }
        });

        BtnEndCall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                EndCall();

            }
        });

        GetPreviewCall(leadid, mobileno, crmname);
        //  GetPreviewCall(leadid, mobileno, crmname);

    }

    private void Dial(final String leadid, final String mobileno, final String crm_name) {

        try {
            JSONObject params = new JSONObject();

            params.put("Agent_no", Util.getData("mobile_no", getApplicationContext()));
            params.put("Customer_no", mobileno);
            params.put("lead_id", leadid);
            params.put("user_agent", Util.getData("AgentName", getApplicationContext()));
            // params.put("server_ip", "");
            params.put("Process", Util.getData("process_name", getApplicationContext()));

            Log.e("GetPreviewCall", callapi + "\n" + params.toString());

            CallApi.postResponse(CallDispoEx.this, params.toString(), callapi, new VolleyResponseListener() {
                @Override
                public void onError(String message) {
                    Util.Logcat.e("onError" + message);
                }

                @Override
                public void onResponse(JSONObject response) {
                    Util.Logcat.e("GetPreviewCall" + response);

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
                            alert.build(resobject.getString("Message"));
                        } else {
                            alert.build(resobject.getString("Message"));
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
            @SuppressLint("HardwareIds") String device_id = Settings.Secure.getString(getContentResolver(),
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

    private void Loadsubspinner(JSONArray st) {

        final List<String> spinnerlistSub = new ArrayList<String>();

        try {
            for (int i = 0; i < st.length(); i++) {
                String street = st.getString(i);
                Log.e("..........", "" + street);
                spinnerlistSub.add(street);
                // loop and add it to array or arraylist
            }

            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<String>
                            (CallDispoEx.this, R.layout.spinner_textview,
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
            CallApi.postResponse(CallDispoEx.this, params.toString(), GetCustomerData, new VolleyResponseListener() {
                @Override
                public void onError(String message) {
                    Util.Logcat.e("onError" + message);
                }

                @Override
                public void onResponse(JSONObject response) {
                    Util.Logcat.e("CUSTOMER DETAILS" + response);
                    GetDispo(crm_name);
                    try {
                        //  JSONObject resobject = response;
                        Dial(leadid, mobileno, crmname);
                        JSONObject resobject = new JSONObject(response.getString("d_columninfo"));

                        JSONArray array = resobject.getJSONArray("Values");
                        Log.e("d_columninfo", array.toString());
                        for (int i = 0; i < array.length(); i++) {
                            JSONObject obj = array.getJSONObject(i);
                            DataHashMap = new HashMap<String, String>();
                            DataHashMap.put("text", obj.getString("key"));
                            DataHashMap.put("value", obj.getString("Value"));
                            DataHashMap.put("IsEdit", obj.getString("IsEdit"));
                            ListCollection.add(DataHashMap);
                        }

                       /* try {
                            array = new JSONArray(resobject.getString("d_columninfo"));
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
                                    DataHashMap = new HashMap<String, String>();
                                    DataHashMap.put("text", key + " : " + value);
                                    DataHashMap.put("value", value);
                                    //  values.append(key).append(":").append(value).append("\n");
                                    ListCollection.add(DataHashMap);

                                    map.put(key, value);
                                }
                                System.out.println(map.toString());
                            }

                            // alert.build(values.toString());
                            // BuildAlert(JSONArray array);
                            //  BuildAlert(values.toString());
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }*/

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                if (ListCollection.size() > 0) {
                                    BtnRefresh.setVisibility(View.GONE);
                                    adapter = new CallAdapterPreview(CallDispoEx.this, ListCollection);
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
            // params.put("action", "Get_Disposition");
            //params.put("action", "Get_CRM_Dispo");
            params.put("CRM_id", crm_id);
            //Util.getData("crm_name",getApplicationContext());
            Log.e("GET DISPO", GetDispo + "\n" + params.toString());
            CallApi.postResponse(CallDispoEx.this, params.toString(), GetDispo, new VolleyResponseListener() {
                @Override
                public void onError(String message) {
                    Util.Logcat.e("onError" + message);
                }

                @Override
                public void onResponse(JSONObject response) {
                    Util.Logcat.e("GET DISPO" + response);
                    try {
                        // JSONObject data = new JSONObject(response.getString("Data"));
                        String data = response.getString("Data");
                        JSONObject dat = new JSONObject(data);
                        JSONArray st = dat.getJSONArray("Dispo");

                        Subdispo = dat.getJSONObject("Sub Dispo");
                        for (int i = 0; i < st.length(); i++) {
                            String street = st.getString(i);
                            Log.e("..........", "" + street);
                            spinnerlist.add(street);
                            // loop and add it to array or arraylist
                        }

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<>
                                        (CallDispoEx.this, R.layout.spinner_textview,
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
            params.put("action", "CLOSE");
            params.put("endcall_type", "CLOSE");
            params.put("refno", "");
            params.put("process_name", "");
            params.put("disposition", StrDispo + "~" + StrDispoSub);
            params.put("list_comments", EdComments.getEditableText().toString());
            params.put("convoxid", leadid);
            params.put("mobile_number", mobileno);
            params.put("agent_id", Util.getData("AgentName", getApplicationContext()));
            params.put("process_agent", Util.getData("AgentName", getApplicationContext()));
            Log.e("END CALL", Callcloseapi + "\n" + params.toString());
            CallApi.postResponse(CallDispoEx.this, params.toString(), Callcloseapi, new VolleyResponseListener() {
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
                            //  Toast.makeText(CallDispo.this, resobject.getString("MESSAGE"), Toast.LENGTH_LONG);
                            //alert.build(resobject.getString("MESSAGE"));
                            Util.saveData("LEADID", "", getApplicationContext());
                            Util.saveData("MOBILENO", "", getApplicationContext());
                            Util.saveData("CRMNAME", "", getApplicationContext());
                            refresh(resobject.getString("MESSAGE"));
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

    private void refresh(String msg) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.alertDialog);
        builder.setMessage(msg)
                .setCancelable(false)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(@SuppressWarnings("unused") final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                        dialog.dismiss();
                        Intent main = new Intent(CallDispoEx.this, NewModel.class);
                        startActivity(main);
                        finish();
                    }
                });
        final AlertDialog alert = builder.create();
        alert.show();
    }

}
