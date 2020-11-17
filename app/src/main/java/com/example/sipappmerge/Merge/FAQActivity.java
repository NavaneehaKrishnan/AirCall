package com.example.sipappmerge.Merge;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.example.sipappmerge.R;


public class FAQActivity extends AppCompatActivity {

    WebView webView;
    //  String Weburl="http://123.63.108.199:36004/faq/";
    Button btn_exit;

    public static void log(String message) {
        Log.i("gi", message);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.web_activity);

        webView = findViewById(R.id.webview);
        btn_exit = findViewById(R.id.btn_exit);
        WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        String Weburl = getIntent().getStringExtra("url");
        String name = getIntent().getStringExtra("btnname");
        webView.loadUrl(Weburl);
        Log.e("URL",Weburl);
        webView.setWebViewClient(new MyWebViewClient());
        /*final Handler handler = new Handler();
        final Runnable r = new Runnable() {
            public void run() {
                handler.postDelayed(this, 1000);
            }
        };*/
        btn_exit.setText(name);
        btn_exit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
//
    }

    private class MyWebViewClient extends WebViewClient {

        @Override
        public void onPageStarted(WebView webview, String url, Bitmap favicon) {

        }

        @Override
        public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
            log("Error in onreceived error");
            try {
                webView.stopLoading();
            } catch (Exception e) {
            }
            try {
                webView.clearView();
            } catch (Exception e) {
            }
            if (webView.canGoBack()) {
                webView.goBack();
            }
            // webView.loadUrl("file:///android_asset/path/to/your/missing-page-template.html");

            super.onReceivedError(view, request, error);
        }

        @Override
        public void onPageFinished(WebView view, String url) {

            log("finished url " + url);
            super.onPageFinished(view, url);

        }

        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            if (isConnected()) {
                log("Net connected in should overrideurl loading.");
            } else {
                log("No internet");
            }
            log("override url " + url);
            view.loadUrl(url);
            return false;
        }

        @Override
        public void onLoadResource(WebView view, String url) {
            super.onLoadResource(view, url);
            if (isConnected()) {
                webView.setVisibility(View.VISIBLE);
            } else {
                webView.setVisibility(View.INVISIBLE);
                showErrorDialog();
                log("No Internet connection");

            }
            log("Load Resource url " + url);
        }
    }

    @Override
    public void onBackPressed() {
        if (webView.canGoBack()) {
            // If web view have back history, then go to the web view back history
            webView.goBack();
        } else {
            showAppExitDialog();
            //this.finish();
        }
    }


    public void showAppExitDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setTitle("EXIT");
        builder.setMessage("Do you want to go to Exit");
        builder.setCancelable(true);

        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                // Do something when user want to exit the app
                // Let allow the system to handle the event, such as exit the app
                FAQActivity.super.onBackPressed();
            }
        });

        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                // Do something when want to stay in the app

            }
        });

        // Create the alert dialog using alert dialog builder
        AlertDialog dialog = builder.create();

        // Finally, display the dialog when user press back button
        dialog.show();
    }

    public boolean isConnected() {
        ConnectivityManager cm = (ConnectivityManager) this.getSystemService(Context.CONNECTIVITY_SERVICE);

        if (null != cm) {
            NetworkInfo info = cm.getActiveNetworkInfo();
            return (info != null && info.isConnected());
        }

        return false;

    }

    void showErrorDialog() {
        AlertDialog alertDialog = new AlertDialog.Builder(FAQActivity.this).create();
        alertDialog.setTitle("GiRetail");
        alertDialog.setMessage("Check your internet connection and try again.");
        alertDialog.setButton(DialogInterface.BUTTON_POSITIVE, "Try Again", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                finish();
                startActivity(getIntent());
            }
        });

        alertDialog.show();
    }
}
