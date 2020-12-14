package com.example.sipappmerge.Utils;

import android.app.Activity;
import android.app.Dialog;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.example.sipappmerge.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import static com.example.sipappmerge.Utils.Util.ApplyBreak;

public class RadioAlertDialog {
    private Activity mActivity;

    public RadioAlertDialog(Activity a) {
        this.mActivity = a;
    }

    public void build(JSONArray array) {
        final String[] strSelected = {""};
        Util.saveData("BREAK", "", mActivity.getApplicationContext());
        final Dialog dialog = new Dialog(mActivity);
        //dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setTitle("Select Option");
        dialog.setContentView(R.layout.radiobutton_dialog);

        RadioGroup rg = dialog.findViewById(R.id.radio_group);
        Button BTN = dialog.findViewById(R.id.btn);
        BTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (strSelected[0].equalsIgnoreCase("")) {
                    Toast.makeText(mActivity, "Select Option", Toast.LENGTH_SHORT).show();
                } else {
                    dialog.dismiss();

                    ReleaseBreak("ApplyBreak",strSelected[0]);
                }
            }
        });
        try {
            for (int i = 0; i < array.length(); i++) {
                RadioButton rb = new RadioButton(mActivity); // dynamically creating RadioButton and adding to RadioGroup.
                rb.setText(array.getString(i));
                rb.setPadding(5, 5, 5, 5);
                rb.setTextAppearance(mActivity, android.R.style.TextAppearance_Large);
                rg.addView(rb);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        dialog.show();
        rg.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                int childCount = group.getChildCount();
                for (int x = 0; x < childCount; x++) {
                    RadioButton btn = (RadioButton) group.getChildAt(x);
                    if (btn.getId() == checkedId) {
                        strSelected[0] = btn.getText().toString();

                    }
                }
            }
        });
    }

    private void ReleaseBreak(String action, final String s) {

        try {
            JSONObject params = new JSONObject();
            params.put("Break", s);
            params.put("agent_id", Util.getData("AgentName", mActivity.getApplicationContext()));
            Util.Logcat.e("RELEASE BREAK" + ", param :"+params.toString());
            CallApi.postResponse(mActivity, params.toString(), ApplyBreak, new VolleyResponseListener() {
                @Override
                public void onError(String message) {
                    Util.Logcat.e("ApplyBreak onError" + message);
                }

                @Override
                public void onResponse(JSONObject response) {
                    Util.Logcat.e("ApplyBreak Response" + response);
                    try {
                        if(response.getString("Status").equals("1")&& response.getString("StatusDesc").equals("Sucess"))
                        {
                            String responseData = response.getString("ResponseData");
                            JSONArray jsonArray = new JSONArray(new JSONObject(responseData).optString("DATA"));
                            if(jsonArray.length()>0)
                            {
                                if (jsonArray.getJSONObject(0).getString("Status").equalsIgnoreCase("0")) {
                                    Toast.makeText(mActivity, jsonArray.getJSONObject(0).getString("Statusdesc"), Toast.LENGTH_LONG).show();
                                    Util.saveData("BREAK", s, mActivity.getApplicationContext());
                                } else {
                                    Toast.makeText(mActivity, jsonArray.getJSONObject(0).getString("Statusdesc"), Toast.LENGTH_LONG).show();
                                }
                            }
                        }


                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });

        } catch (JSONException e) {
            e.printStackTrace();
        }

    }
}
