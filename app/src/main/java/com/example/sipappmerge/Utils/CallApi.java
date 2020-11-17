package com.example.sipappmerge.Utils;

import android.Manifest;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Build;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.WindowManager;

import androidx.core.app.ActivityCompat;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.sipappmerge.Merge.Login;
import com.google.android.gms.common.internal.service.Common;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Map;

import fr.bmartel.speedtest.SpeedTestReport;
import fr.bmartel.speedtest.SpeedTestSocket;
import fr.bmartel.speedtest.inter.ISpeedTestListener;
import fr.bmartel.speedtest.model.SpeedTestError;

public class CallApi {


    public static void READY(final Context context, String mObject, String Api, final VolleyResponseListener listener) throws JSONException {
        //updateNetworkStrength(context, mObject, Api, listener);
//            final ProgressDialog loading = new ProgressDialog(context);
//            loading.setMessage("Loading...");
//            loading.setCancelable(false);
//            loading.show();
            JsonObjectRequest req = new JsonObjectRequest(Request.Method.POST, Api, new JSONObject(mObject),
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            //loading.dismiss();
                            listener.onResponse(response);
                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    //loading.dismiss();
                    listener.onError(error.toString());

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
                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    Map<String, String>  params = new HashMap<String, String>();
                    params.put("Content-Type","application/json");
                    params.put("access_token","PROD_MIAGCSqGSIb3DQEHAqCAMIACAQExC");
                    return params;
                }
            };

           /* req.setRetryPolicy(new DefaultRetryPolicy(50000,
                    0,
                    DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));*/
        req.setRetryPolicy(new DefaultRetryPolicy(
                15000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
            //Creating request queue
            RequestQueue requestQueue = Volley.newRequestQueue(context);
            //Adding request to the queue
            requestQueue.add(req);
    }



    public static void READYNOPROGRESS(final Context context, String mObject, String Api, final VolleyResponseListener listener) throws JSONException {

        JsonObjectRequest req = new JsonObjectRequest(Request.Method.POST, Api, new JSONObject(mObject),
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {

                        listener.onResponse(response);
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                listener.onError(error.toString());

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
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String>  params = new HashMap<String, String>();
                //params.put("Content-Type", "application/x-www-form-urlencoded");
                params.put("Authorization","Bearer "+Util.getData("JWTToken", context));

                Log.e("header",params.toString());
                return params;
            }
        };

        /*req.setRetryPolicy(new DefaultRetryPolicy(50000,
                0,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));*/
        req.setRetryPolicy(new DefaultRetryPolicy(
                15000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        //Creating request queue
        RequestQueue requestQueue = Volley.newRequestQueue(context);
        //Adding request to the queue
        requestQueue.add(req);
    }


    public static void postResponse(final Context context, String mObject, String Api, final VolleyResponseListener listener) throws JSONException {
        if (Util.isOnline(context)) {
            //final ProgressDialog loading = ProgressDialog.show(context, "Loading...", "Please wait...", false, false);
            final ProgressDialog loading = new ProgressDialog(context);

            loading.setMessage("Loading...");
            loading.setCancelable(false);
            loading.show();

            JsonObjectRequest req = new JsonObjectRequest(Request.Method.POST, Api, new JSONObject(mObject),
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            loading.dismiss();
                            listener.onResponse(response);
                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    loading.dismiss();
                    listener.onError(error.toString());
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

                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    Map<String, String>  params = new HashMap<String, String>();
                    //params.put("Content-Type", "application/x-www-form-urlencoded");
                    params.put("Authorization","Bearer "+Util.getData("JWTToken", context));

                    Log.e("header",params.toString());
                    return params;
                }
            };

            req.setRetryPolicy(new DefaultRetryPolicy(
                    15000,
                    DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                    DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
            //Creating request queue
            RequestQueue requestQueue = Volley.newRequestQueue(context);
            //Adding request to the queue
            requestQueue.add(req);
        } else {

            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
            alertDialogBuilder.setMessage("Check Internet");
            alertDialogBuilder.setPositiveButton("OK",
                    new DialogInterface.OnClickListener() {

                        @Override
                        public void onClick(DialogInterface arg0, int arg1) {

                        }
                    });

            AlertDialog alertDialog = alertDialogBuilder.create();
            alertDialog.setCancelable(false);
            alertDialog.show();

        }
    }

    public static void postResponseNonHeader(final Context context, String mObject, String Api, final VolleyResponseListener listener) throws JSONException {

        if (Util.isOnline(context)) {
            //final ProgressDialog loading = ProgressDialog.show(context, "Loading...", "Please wait...", false, false);
            final ProgressDialog loading = new ProgressDialog(context);
            loading.setMessage("Checking...");
            loading.setCancelable(false);
            loading.show();
            JsonObjectRequest req = new JsonObjectRequest(Request.Method.POST, Api, new JSONObject(mObject),
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            loading.dismiss();
                            listener.onResponse(response);
                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    loading.dismiss();
                    listener.onError(error.toString());
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

            req.setRetryPolicy(new DefaultRetryPolicy(
                    15000,
                    DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                    DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
            //Creating request queue
            RequestQueue requestQueue = Volley.newRequestQueue(context);
            //Adding request to the queue
            requestQueue.add(req);
        } else {

            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
            alertDialogBuilder.setMessage("Check Internet");
            alertDialogBuilder.setPositiveButton("OK",
                    new DialogInterface.OnClickListener() {

                        @Override
                        public void onClick(DialogInterface arg0, int arg1) {

                        }
                    });

            AlertDialog alertDialog = alertDialogBuilder.create();
            alertDialog.setCancelable(false);
            alertDialog.show();

            //  Toast.makeText(context.getApplicationContext(), context.getResources().getString(R.string.check_internet_connection), Toast.LENGTH_SHORT).show();
        }
    }

    public static class SpeedTestTask extends AsyncTask<Void, Void, String> {
        Context context;
        public SpeedTestTask(Context context) {
            this.context = context;
        }

        @Override
        protected String doInBackground(Void... params) {

            SpeedTestSocket speedTestSocket = new SpeedTestSocket();
            // add a listener to wait for speedtest completion and progress
            speedTestSocket.addSpeedTestListener(new ISpeedTestListener() {

                @Override
                public void onCompletion(SpeedTestReport report) {
                    // called when download/upload is finished
                    //     Log.e("speedtest", "[COMPLETED] rate in octet/s : " + report.getTransferRateOctet());
                    //   Log.e("speedtest", "[COMPLETED] rate in bit/s   : " + report.getTransferRateBit());
                    //Util.formatFileSize(report.getTransferRateBit());
                    DecimalFormat dec = new DecimalFormat("0.00");
                    final double dsdas = Double.parseDouble(dec.format(report.getTransferRateBit())) / 1024;
                    Log.e("dsdas", String.valueOf(dsdas));
                    // double dsdas=dec.format(sads);
                   // new AsyncTaskExample(context);


                }

                @Override
                public void onError(SpeedTestError speedTestError, String errorMessage) {
                    // called when a download/upload error occur
                }

                @Override
                public void onProgress(float percent, SpeedTestReport report) {
                    // called to notify download/upload progress
                    //     pd.setMessage( ""+ percent+ "%");
                    //    Log.e("speedtest", "[PROGRESS] progress : " + percent + "%");
                    //   Log.e("speedtest", "[PROGRESS] rate in octet/s : " + report.getTransferRateOctet());
                    // Log.e("speedtest", "[PROGRESS] rate in bit/s   : " + report.getTransferRateBit());
                }
            });
            speedTestSocket.startDownload("http://ipv4.ikoula.testdebit.info/1M.iso");
            return null;
        }
    }

    public static class AsyncTaskUpdateIsDeviceRooted extends AsyncTask<String, String, String> {
        private Context context;

        public AsyncTaskUpdateIsDeviceRooted (Context context)
        {
            this.context = context;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

        }
        @Override
        protected String doInBackground(String... strings) {
            String strDeviceId = "",strEMI = "";
            try {
                TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
                strDeviceId = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
                if (ActivityCompat.checkSelfPermission(context, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
                    return "";
                }
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        strEMI  = telephonyManager.getPhoneCount()>1 ? telephonyManager.getDeviceId(0)+","+telephonyManager.getDeviceId(1):telephonyManager.getDeviceId(0);
                    }

                JSONObject params = new JSONObject();

                params.put("DevicId", strDeviceId);
                params.put("IMEI", strEMI);
                Log.e("UpdateIsDeviceRooted", Util.BASE+"Dialer/RootedDevice" + "\n" + params.toString());

                JsonObjectRequest req = new JsonObjectRequest(Request.Method.POST, Util.BASE+"Dialer/RootedDevice", params,
                        new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject response) {
                                Log.e("UpdateIsDeviceRooted","Response : "+response.toString());
                            }
                        }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e("UpdateIsDeviceRooted","error : "+error.getMessage());

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

                req.setRetryPolicy(new DefaultRetryPolicy(
                        15000,
                        DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                        DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
                //Creating request queue
                RequestQueue requestQueue = Volley.newRequestQueue(context);
                //Adding request to the queue
                requestQueue.add(req);

            } catch (JSONException e) {
                e.printStackTrace();
            }
            return "";
        }
        @Override
        protected void onPostExecute(String bitmap) {
            super.onPostExecute(bitmap);

        }
    }


}