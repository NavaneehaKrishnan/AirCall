package com.example.sipappmerge.adapter;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.sipappmerge.R;
import com.example.sipappmerge.Utils.CustomerModel;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class PredictiveCusAdapter  extends BaseAdapter {
    private static LayoutInflater inflater = null;
    private Activity activity;
    private List<Map<String, String>> listdetails;
    private ArrayList<CustomerModel> customerModels;
    ProgressDialog progressDialog;

    public PredictiveCusAdapter(Activity context, ArrayList<CustomerModel> customerModels) {

        this.activity = context;
        this.customerModels = customerModels;

        inflater = (LayoutInflater) activity
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);


    }

    @Override
    public int getCount() {
        return customerModels.size();
    }

    @Override
    public Object getItem(int position) {
        return customerModels.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public void pos(int position) {

        customerModels.remove(customerModels.get(position));

    }

    @Override
    public View getView(final int position, final View convertView, final ViewGroup parent) {
        View vi = convertView;
        final PredictiveCusAdapter.ViewHolder holder;

        if (vi == null) {
            vi = inflater.inflate(R.layout.call_adapter_preview, null);
            holder = new PredictiveCusAdapter.ViewHolder();

            holder.TxtKey = vi
                    .findViewById(R.id.txt);
            holder.TxtValue = vi
                    .findViewById(R.id.txt_value);
            holder.lypadding = vi
                    .findViewById(R.id.padding);

            vi.setTag(holder);
        } else {
            holder = (PredictiveCusAdapter.ViewHolder) vi.getTag();
        }
        holder.TxtKey.setText(customerModels.get(position).getStrTitle());
        holder.TxtValue.setEnabled(customerModels.get(position).getStrIsEdit().equals("Y"));
        /*if(listdetails.get(position).get(
                "text").equalsIgnoreCase("lenght")){
        }*/
        holder.TxtValue.setText(customerModels.get(position).getStrValue());
        /*if (listdetails.get(position).get(
                "is_edit").equalsIgnoreCase("N")) {
            holder.TxtValue.setClickable(false);
            holder.TxtValue.setEnabled(false);
        } else {
            holder.TxtValue.setClickable(true);
            holder.TxtValue.setEnabled(true);
        }*/

        if (listdetails.get(position).get(
                "lenght").equalsIgnoreCase(listdetails.get(position).get(
                "pos"))) {
            holder.lypadding.setPadding(0, 0, 0, 40);
            
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
