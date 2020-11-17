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


public class AccAdapter extends BaseAdapter {

    private static LayoutInflater inflater = null;
    private Activity activity;
    private List<Map<String, String>> listdetails;
    ProgressDialog progressDialog;
    CommonAlertDialog alert;

    public AccAdapter(Activity context, List<Map<String, String>> listCollectionone) {

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

            vi = inflater.inflate(R.layout.acc_adapter, null);
            holder = new ViewHolder();

            holder.call_time = vi
                    .findViewById(R.id.call_time);
            holder.status = vi
                    .findViewById(R.id.status);
            holder.agent_id = vi
                    .findViewById(R.id.agent_id);
            holder.process = vi
                    .findViewById(R.id.process);
            holder.disposition = vi
                    .findViewById(R.id.disposition);
            holder.comments = vi
                    .findViewById(R.id.comments);
            holder.call_attempt = vi
                    .findViewById(R.id.call_attempt);

            vi.setTag(holder);

        } else {
            holder = (ViewHolder) vi.getTag();
        }

        holder.call_time.setText("Call Time : "+listdetails.get(position).get(
                "call_time"));
        holder.status.setText("Status : "+listdetails.get(position).get(
                "status"));
        holder.agent_id.setText("Agent Id : "+listdetails.get(position).get(
                "agent_id"));
        holder.process.setText("Process : "+listdetails.get(position).get(
                "process"));
        holder.disposition.setText("Disposition : "+listdetails.get(position).get(
                "disposition"));
        holder.comments.setText("Comments : "+listdetails.get(position).get(
                "comments"));
        holder.call_attempt.setText("No of Attempts : "+listdetails.get(position).get(
                "call_attempt"));

       //{"call_time":"2020-05-07 18:57:52","status":"ABANDONED","agent_id":"agent_2","process":"RBLDemo","disposition":"","comments":"0"},

        return vi;
    }

    public static class ViewHolder {
        private TextView call_time, status, agent_id,process,disposition,comments,call_attempt;
    }
}

