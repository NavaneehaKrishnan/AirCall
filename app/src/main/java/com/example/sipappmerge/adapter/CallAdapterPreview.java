package com.example.sipappmerge.adapter;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.sipappmerge.R;

import java.util.List;
import java.util.Map;

public class CallAdapterPreview extends BaseAdapter {
    private static LayoutInflater inflater = null;
    private Activity activity;
    private List<Map<String, String>> listdetails;
    ProgressDialog progressDialog;

    public CallAdapterPreview(Activity context, List<Map<String, String>> listCollectionone) {

        activity = context;
        listdetails = listCollectionone;

        inflater = (LayoutInflater) activity
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

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
            vi = inflater.inflate(R.layout.call_adapter_preview, null);
            holder = new ViewHolder();

            holder.TxtKey = vi
                    .findViewById(R.id.txt);
            holder.TxtValue = vi
                    .findViewById(R.id.txt_value);
            holder.lypadding = vi
                    .findViewById(R.id.padding);

            vi.setTag(holder);
        } else {
            holder = (ViewHolder) vi.getTag();
        }
        holder.TxtKey.setText(listdetails.get(position).get(
                "text"));
        /*if(listdetails.get(position).get(
                "text").equalsIgnoreCase("lenght")){
        }*/
        holder.TxtValue.setText(listdetails.get(position).get(
                "value"));
        if (listdetails.get(position).get("IsEdit")!= null && !listdetails.get(position).get("IsEdit").equalsIgnoreCase("N")) {
            holder.TxtValue.setClickable(true);
            holder.TxtValue.setEnabled(true);

        } else {
            holder.TxtValue.setClickable(false);
            holder.TxtValue.setEnabled(false);
        }

        if (listdetails.get(position).get(
                "lenght").equalsIgnoreCase(listdetails.get(position).get(
                "pos"))) {
            holder.lypadding.setPadding(0, 0, 0, 40);
            Log.e("HAI", listdetails.get(position).get(
                    "value"));
        }
        if ("1".equalsIgnoreCase(listdetails.get(position).get(
                "pos"))) {
            holder.lypadding.setPadding(0, 20, 0, 0);
        }
        return vi;
    }

    public static class ViewHolder {
        TextView TxtKey;
        EditText TxtValue;
        LinearLayout lypadding;

    }
}

