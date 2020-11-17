package com.example.sipappmerge.Merge;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.content.pm.PackageManager;
import android.media.MediaRecorder;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.util.Log;
import android.view.View;

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
import com.example.sipappmerge.R;
import com.example.sipappmerge.Utils.CallApi;
import com.example.sipappmerge.Utils.ConnectivityReceiver;

import com.example.sipappmerge.Utils.Util;



import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import static android.Manifest.permission.ACCESS_COARSE_LOCATION;
import static android.Manifest.permission.CALL_PHONE;
import static android.Manifest.permission.CAMERA;
import static android.Manifest.permission.KILL_BACKGROUND_PROCESSES;
import static android.Manifest.permission.READ_PHONE_STATE;
import static android.Manifest.permission.RECORD_AUDIO;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;


public class BaseActivity extends AppCompatActivity {

    MediaRecorder recorder;
    File audiofile = null;
    static final String TAG = "MediaRecording";

    ConnectivityReceiver connectivityReceiver;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_base);
       /* if (checkPermission()) {
            // Toast.makeText(SplashActivity.this, "Permission already granted.", Toast.LENGTH_SHORT).show();
            //initialize();
            try {
                startRecording();
            } catch (IOException e) {
                e.printStackTrace();
            }

        } else {
            requestPermission();
        }*/

    }

    private void requestPermission() {
        ActivityCompat.requestPermissions(this, new String[]{ACCESS_COARSE_LOCATION, CAMERA, READ_PHONE_STATE, WRITE_EXTERNAL_STORAGE, KILL_BACKGROUND_PROCESSES, RECORD_AUDIO}, 200);
    }

    private boolean checkPermission() {

        int result5 = ContextCompat.checkSelfPermission(getApplicationContext(), RECORD_AUDIO);

        return result5 == PackageManager.PERMISSION_GRANTED;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case 200:
                if (grantResults.length > 0) {


                    boolean killprocess = grantResults[4] == PackageManager.PERMISSION_GRANTED;
                    // boolean access = grantResults[5] == PackageManager.PERMISSION_GRANTED;

                    if (killprocess) {
                        //Toast.makeText(SplashActivity.this, "Permission Granted, Now you can access location data,camera,phone and storage", Toast.LENGTH_SHORT).show();

                        try {
                            startRecording();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    } else {

                        try {
                            startRecording();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        //Snackbar.make(view, "Permission Denied, You cannot access location data and camera.", Snackbar.LENGTH_LONG).show();
                        // Toast.makeText(SplashActivity.this, "Permission Denied, You cannot access location data,camera,phone and storage.", Toast.LENGTH_SHORT).show();

                       /* if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                            if (shouldShowRequestPermissionRationale(ACCESS_COARSE_LOCATION)) {
                                showMessageOKCancel("You need to allow access to both the permissions",
                                        new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                                    requestPermissions(new String[]{ACCESS_FINE_LOCATION, CAMERA, READ_PHONE_STATE, WRITE_EXTERNAL_STORAGE},
                                                            200);
                                                }
                                            }
                                        });
                                return;
                            }
                        }*/

                    }
                }


                break;
        }
    }

    public void startRecording() throws IOException {

        //Creating file
        File dir = Environment.getExternalStorageDirectory();
        try {
            audiofile = File.createTempFile("sound", ".3gp", dir);
        } catch (IOException e) {
            Log.e(TAG, "external storage access error");
            return;
        }
        //Creating MediaRecorder and specifying audio source, output format, encoder & output format
        recorder = new MediaRecorder();
        recorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        recorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
        recorder.setOutputFile(audiofile.getAbsolutePath());
        recorder.prepare();
        recorder.start();
    }

    public void stopRecording() {

        //stopping recorder
        try {
            recorder.stop();
            recorder.release();

            if (audiofile.exists()) {
                if (audiofile.delete()) {
                    System.out.println("file Deleted :" + audiofile.getAbsolutePath());
                } else {
                    System.out.println("file not Deleted :" + audiofile.getAbsolutePath());
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        //after stopping the recorder, create the sound file and add it to media library.
        //addRecordingToMediaLibrary();
    }

    @Override
    public void onStart() {
        super.onStart();

    }

    @Override
    public void onStop() {
        super.onStop();
        try {
            if (connectivityReceiver != null)
                connectivityReceiver.unregister();
        }catch (Exception e)
        {
            e.printStackTrace();
        }

    }

    public void onDestroy() {
        super.onDestroy();
        /*stopRecording();*/
    }
    public void onResume() {
        super.onResume();
        connectivityReceiver = new ConnectivityReceiver(BaseActivity.this) {
            @Override
            public void airplaneModeChanged(boolean enabled) {
                Log.e(
                        "AirplaneModeReceiver",
                        "Airplane mode changed to: " +
                                ((enabled) ? "ACTIVE" : "NOT ACTIVE")
                );
                if(enabled){
                    DateFormat df = new SimpleDateFormat("d MMM yyyy, HH:mm");
                    String date = df.format(Calendar.getInstance().getTime());
                    Util.saveData("FlightModes", date, BaseActivity.this);
                }
                else
                {
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            new AsyncTaskUpdateEvents("Flight Mode").execute();
                        }
                    },10000);

                }
            }
        };
        connectivityReceiver.register();
    }

    public void onPause() {
        super.onPause();
        /*if (audiofile.exists()) {
            if (audiofile.delete()) {
                System.out.println("file Deleted :" + audiofile.getAbsolutePath());
            } else {
                System.out.println("file not Deleted :" + audiofile.getAbsolutePath());
            }
        }*/
    }





    private class AsyncTaskUpdateEvents extends AsyncTask<String, String, String> {
        String s;
        public AsyncTaskUpdateEvents(String s) {
            this.s = s;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

        }
        @Override
        protected String doInBackground(String... strings) {
            DateFormat df = new SimpleDateFormat("d MMM yyyy, HH:mm");
            String date = df.format(Calendar.getInstance().getTime());
            try {
                JSONObject params = new JSONObject();
                params.put("Event", s);
                params.put("TimeDuration", Util.getData("FlightModes", getApplicationContext())+" TO "+date);
                params.put("AgentId",Util.getData("AgentName", getApplicationContext()));
                Log.e("updateAppEvents"," Params > "+params.toString());
                JsonObjectRequest req = new JsonObjectRequest(Request.Method.POST, Util.BASE+"Dialer/AppEvents", params,
                        new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject response) {
                                Util.saveData("AIRPLANE_MODE_OFF", "", BaseActivity.this);
                                Log.e("AppEvents",response.toString());
                            }
                        }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                        Log.e("AppEvents",error.getMessage());
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
                        50000,
                        DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                        DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
                //Creating request queue
                RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
                //Adding request to the queue
                requestQueue.add(req);
            }catch (JSONException js)
            {
                js.printStackTrace();
            }
            return null;
        }
        @Override
        protected void onPostExecute(String bitmap) {
            super.onPostExecute(bitmap);

        }
    }


}