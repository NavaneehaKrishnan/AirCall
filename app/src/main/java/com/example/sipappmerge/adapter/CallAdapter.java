package com.example.sipappmerge.adapter;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.sipappmerge.Utils.CallApi;
import com.example.sipappmerge.Merge.CallDispo;
import com.example.sipappmerge.Utils.CommonAlertDialog;
import com.example.sipappmerge.R;
import com.example.sipappmerge.Utils.Util;
import com.example.sipappmerge.Utils.VolleyResponseListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;
import java.util.Map;


public class CallAdapter extends BaseAdapter {

    private static LayoutInflater inflater = null;
    private Activity activity;
    private List<Map<String, String>> listdetails;
    ProgressDialog progressDialog;
    CommonAlertDialog alert;

    public CallAdapter(Activity context, List<Map<String, String>> listCollectionone) {

        activity = context;
        listdetails = listCollectionone;

        inflater = (LayoutInflater) activity
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        alert = new CommonAlertDialog(activity);
        progressDialog = new ProgressDialog(activity);
        progressDialog.setMessage("Loading...");
        progressDialog.setCancelable(false);

    }

    @Override
    public int getCount() {
        return listdetails.size();
    }

    @Override
    public Object getItem(int position) {
        return listdetails.size();
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public void pos(int position) {

        listdetails.remove(listdetails.get(position));

    }

    @Override
    public View getView(final int position, final View convertView, final ViewGroup parent) {
        View vi = convertView;
        final ViewHolder holder;

        if (vi == null) {

            vi = inflater.inflate(R.layout.call_adapter, null);
            holder = new ViewHolder();

            holder.TxtMobileNo = vi
                    .findViewById(R.id.mobile_no);
            holder.TxtLeadID = vi
                    .findViewById(R.id.lead_id);
            holder.TxtStatus = vi
                    .findViewById(R.id.lead_status);
            holder.BtnCall = vi
                    .findViewById(R.id.btn_call);

            vi.setTag(holder);

        } else {
            holder = (ViewHolder) vi.getTag();
        }

        /*String MobileNo="XXXXX"+listdetails.get(position).get(
                "phone_number").substring(4,8);*/
        String MobileNo = "XXXXX" + listdetails.get(position).get(
                "mobile_number").substring(4, 8);
        holder.TxtMobileNo.setText(MobileNo);

        holder.TxtLeadID.setText("Lead ID : " + listdetails.get(position).get(
                "lead_id"));
        if (listdetails.get(position).get(
                "status").equalsIgnoreCase("1")) {
            holder.TxtStatus.setVisibility(View.INVISIBLE);
        } else {
            holder.TxtStatus.setText("DateTime : " + listdetails.get(position).get(
                    "status"));
        }
        holder.BtnCall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // ((ListView) parent).performItemClick(v, position, 0);
                /*GetStatus(listdetails.get(position).get(
                        "lead_id"),listdetails.get(position).get(
                        "phone_number"),listdetails.get(position).get(
                        "process_name"),listdetails.get(position).get(
                        "crm_name"));*/
              /*  Dial(listdetails.get(position).get(
                        "lead_id"),listdetails.get(position).get(
                        "phone_number"),listdetails.get(position).get(
                        "crm_name"));*/
                if (!Util.getData("BREAK", activity.getApplicationContext()).isEmpty()) {
                    String hai = "You are in " + Util.getData("BREAK", activity.getApplicationContext()) + " mode. Please click ready button below to release your break";
                    alert.build(hai);
                } else {
                    Intent main = new Intent(activity, CallDispo.class);
                    main.putExtra("leadid", listdetails.get(position).get(
                            "lead_id"));
                    main.putExtra("mobileno", listdetails.get(position).get(
                            "mobile_number"));
                    main.putExtra("crm_name", listdetails.get(position).get(
                            "crm_name"));
                    main.putExtra("dial", "true");
                    activity.startActivity(main);
                }
            }
        });

        return vi;
    }


    public static class ViewHolder {
        private TextView TxtMobileNo, TxtLeadID, TxtStatus;
        private ImageView BtnCall;
    }
}

