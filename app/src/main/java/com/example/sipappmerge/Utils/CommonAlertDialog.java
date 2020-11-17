package com.example.sipappmerge.Utils;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.view.WindowManager;

import androidx.appcompat.app.AlertDialog;

import com.example.sipappmerge.R;

public class CommonAlertDialog {
    private Activity mActivity;
    private Context mContext;

    public CommonAlertDialog(Activity a) {
        this.mActivity = a;
    }


    @SuppressWarnings("InflateParams")
    public void build(String title) {
        try {
        AlertDialog.Builder builder = new AlertDialog.Builder(mActivity,     R.style.alertDialog);
       // builder.setTitle(title);
        builder.setMessage(title);
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        AlertDialog alert = builder.create();
        alert.setCancelable(false);
        alert.show();
    }catch (Exception e)
    {
        e.printStackTrace();
    }
    }


    public void ExitAlert(String title) {
        try {
            AlertDialog.Builder builder = new AlertDialog.Builder(mActivity, R.style.alertDialog);
            // builder.setTitle(title);
            builder.setMessage(title);
            builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                    mActivity.finishAffinity();

                }
            });
            AlertDialog alert = builder.create();
            alert.setCancelable(false);
            alert.show();

        }catch (Exception e)
        {
            e.printStackTrace();
        }
    }

}
