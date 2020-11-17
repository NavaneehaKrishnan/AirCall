package com.example.sipappmerge.Utils;

import android.annotation.SuppressLint;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;

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
import com.example.sipappmerge.Merge.NewModel;
import com.firebase.jobdispatcher.JobParameters;
import com.firebase.jobdispatcher.JobService;


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

import static com.example.sipappmerge.Utils.Util.AppNetworkStrength;


public class ScheduledJobService extends JobService{
    public static String strDataSpeed = "";

    BackgroundTask backgroundTask;

    @Override
    public boolean onStartJob(final JobParameters job) {

        backgroundTask = new BackgroundTask()
        {
            @SuppressLint("StaticFieldLeak")
            @Override
            protected void onPostExecute(String s) {
                try {
                    if (Util.getSignalStrength(getApplicationContext()) != null && !Util.getSignalStrength(getApplicationContext()).isEmpty()) {
                        JSONObject params = new JSONObject();
                        params.put("NetworkStrength", Util.getSignalStrength(getApplicationContext()));
                        params.put("NetSpeed", strDataSpeed);
                        params.put("DeviceId", Build.MODEL);
                        params.put("DeviceModel", Build.ID);
                        params.put("Brand", Build.BRAND);
                        params.put("Version", Build.VERSION.RELEASE);
                        params.put("AgentId", Util.getData("AgentName", getApplicationContext()));
                        JsonObjectRequest req = new JsonObjectRequest(Request.Method.POST, AppNetworkStrength, params,
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
                            @Override
                            public Map<String, String> getHeaders() throws AuthFailureError {
                                Map<String, String>  params = new HashMap<String, String>();
                                //params.put("Content-Type", "application/x-www-form-urlencoded");
                                params.put("Authorization","Bearer "+Util.getData("JWTToken", getApplicationContext()));

                                Log.e("header",params.toString());
                                return params;
                            }

                        };


                        req.setRetryPolicy(new DefaultRetryPolicy(
                                50000,
                                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
                        //Creating request queue
                        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
                        //Adding request to the queue
                        requestQueue.add(req);
                    }
                    }catch(JSONException js)
                    {
                        js.printStackTrace();
                    }

                jobFinished(job,false);
                    }


        };

        backgroundTask.execute();
        return true;
    }



    @Override
    public boolean onStopJob(JobParameters job) {
        return true;
    }


    public  class BackgroundTask extends AsyncTask<Void,Void,String>
    {

        @Override
        protected String doInBackground(Void... voids) {
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

                    strDataSpeed = String.valueOf(dsdas);
                    Log.e("strDataSpeed ", strDataSpeed);




                }

                @Override
                public void onError(SpeedTestError speedTestError, String errorMessage) {
                    // called when a download/upload error occur
                }

                @Override
                public void onProgress(float percent, SpeedTestReport report) {

                }
            });
            speedTestSocket.startDownload("http://ipv4.ikoula.testdebit.info/1M.iso");

            return "Hello from background job";
        }


    }



}
