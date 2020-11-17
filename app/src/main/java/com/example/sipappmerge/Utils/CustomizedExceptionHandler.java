package com.example.sipappmerge.Utils;

import android.util.Log;

import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;

import static com.example.sipappmerge.Utils.Util.AppErrorLog;
import static com.example.sipappmerge.Utils.Util.BASE;


public class CustomizedExceptionHandler implements Thread.UncaughtExceptionHandler {

    private Thread.UncaughtExceptionHandler defaultUEH;
    private String localPath;

    public CustomizedExceptionHandler(String localPath) {
        this.localPath = localPath;
        //Getting the the default exception handler
        //that's executed when uncaught exception terminates a thread
        this.defaultUEH = Thread.getDefaultUncaughtExceptionHandler();
      //  this.conx=conx.getApplicationContext();
    }

    public void uncaughtException(Thread t, Throwable e) {

        //Write a printable representation of this Throwable
        //The StringWriter gives the lock used to synchronize access to this writer.
        final Writer stringBuffSync = new StringWriter();
        final PrintWriter printWriter = new PrintWriter(stringBuffSync);
        e.printStackTrace(printWriter);
        String stacktrace = stringBuffSync.toString();
        printWriter.close();
        Log.e("currentStacktrace",stacktrace);
        if (localPath != null) {

            try {
                writeToFile(stacktrace);
            } catch (JSONException ex) {
                ex.printStackTrace();
            }

            // SendMail(stacktrace);
        }

//Used only to prevent from any code getting executed.
        // Not needed in this example
        defaultUEH.uncaughtException(t, e);
    }

    private void writeToFile(String currentStacktrace) throws JSONException {
        Log.e("saddsadsa","coming");

            JSONObject params = new JSONObject();
            try {
                Log.e("username",Util.getData("username", MyApplication.getInstance().getApplicationContext()));
                if (!Util.getData("username", MyApplication.getInstance().getApplicationContext()).isEmpty()) {
                    params.put("UserId", Util.getData("username", MyApplication.getInstance().getApplicationContext()));
                }else {
                    params.put("UserId", "");
                }
                params.put("DeviceId", "Get_Data");
                params.put("ErrorInfo", currentStacktrace);
                Log.e("ERROR","\n" + params.toString());
            } catch (JSONException e) {
                e.printStackTrace();
            }


            JsonObjectRequest req = new JsonObjectRequest(Request.Method.POST, Util.getData("Domain", MyApplication.getInstance().getApplicationContext())+AppErrorLog, new JSONObject(params.toString()),
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            Log.e("ERROR response","\n" + response.toString());

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


      /*  File sdCard = Environment.getExternalStorageDirectory();
        File dir = new File (sdCard.getAbsolutePath() + "/ARM");
        dir.mkdirs();
        File file = new File(dir, "logcat.txt");
        Log.e("file",file.getAbsolutePath());

        try {
            FileOutputStream fOut = new FileOutputStream(file);
            OutputStreamWriter osw = new OutputStreamWriter(fOut);
            osw.write(currentStacktrace);
            osw.flush();
            osw.close();

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }*/
    }

}