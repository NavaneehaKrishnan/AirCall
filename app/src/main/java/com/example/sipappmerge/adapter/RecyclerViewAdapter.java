package com.example.sipappmerge.adapter;

import android.content.Context;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;


import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.anurag.multiselectionspinner.MultiSelectionSpinnerDialog;
import com.anurag.multiselectionspinner.MultiSpinner;
import com.example.sipappmerge.R;
import com.example.sipappmerge.Utils.OnItemUpdateListener;
import com.example.sipappmerge.model.ListItemBean;
import com.google.gson.Gson;
import com.zcw.togglebutton.ToggleButton;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder>{
    private ArrayList<ListItemBean> availMeetingList;
    private Context context;
    OnItemUpdateListener onItemUpdateListener;

    public RecyclerViewAdapter(ArrayList<ListItemBean> availMeetingList, Context context) {
        this.availMeetingList = availMeetingList;
        this.context = context;
    }
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View listItem= layoutInflater.inflate(R.layout.list_item, parent, false);
        ViewHolder viewHolder = new ViewHolder(listItem);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        final ListItemBean myListData = availMeetingList.get(position);
        holder.txtTitle.setText(myListData.getKey());
        holder.txtView.setVisibility(View.GONE);
        holder.etView.setVisibility(View.GONE);
        holder.constrainMSContainer.setVisibility(View.GONE);
        holder.constrainToggleContainer.setVisibility(View.GONE);
        holder.rlSpinnerExp.setVisibility(View.GONE);
        if(myListData.getIs_edit().equals("Y") || myListData.getKey().equals("Interest"))
        {
            if(myListData.getType().equalsIgnoreCase("radio"))
            {
                holder.constrainToggleContainer.setVisibility(View.VISIBLE);
                holder.constrainMSContainer.setVisibility(View.GONE);
                holder.txtView.setVisibility(View.GONE);
                holder.txtOptions.setText(myListData.getKey());
                holder.etView.setVisibility(View.GONE);
                holder.rlSpinnerExp.setVisibility(View.GONE);
                if(myListData.getValue() != null && myListData.getValue().equals("Y")) {
                    holder.toggleOptions.setToggleOn(true);
                    myListData.setValue("Y");
                }
                else {
                    holder.toggleOptions.setToggleOff(true);
                    myListData.setValue("N");
                }

            }else if(myListData.getType().equalsIgnoreCase("select"))
            {
                ArrayList<String> spinList;
                holder.constrainToggleContainer.setVisibility(View.GONE);
                holder.constrainMSContainer.setVisibility(View.GONE);
                holder.txtView.setVisibility(View.GONE);
                holder.etView.setVisibility(View.GONE);
                holder.rlSpinnerExp.setVisibility(View.VISIBLE);
                if(myListData.getOption() != null)
                    spinList = new ArrayList<String>(Arrays.asList(myListData.getOption().split("~")));
                else
                {
                    spinList = new ArrayList<String>();
                    spinList.add("Select");
                }
                ArrayAdapter<String> spinAdapter = new ArrayAdapter<String>(context,R.layout.spinner_text,spinList);
                spinAdapter.setDropDownViewResource(R.layout.spinner_text);
                holder.spinnerExp.setAdapter(spinAdapter);
                if(myListData.getValue() != null && !myListData.getValue().isEmpty() && !myListData.getValue().equalsIgnoreCase("NA"))
                {
                    holder.spinnerExp.setSelection(spinList.indexOf(myListData.getValue()));
                }

            }else if(myListData.getType().equalsIgnoreCase("multiselect"))
            {
                ArrayList<String> spinList;
                holder.constrainToggleContainer.setVisibility(View.GONE);
                holder.constrainMSContainer.setVisibility(View.VISIBLE);
                holder.txtView.setVisibility(View.GONE);
                holder.etView.setVisibility(View.GONE);
                holder.rlSpinnerExp.setVisibility(View.GONE);
                if(myListData.getOption() != null)
                spinList = new ArrayList<String>(Arrays.asList(myListData.getOption().split("~")));
                else
                {
                    spinList = new ArrayList<String>();
                    spinList.add("Select");
                }
                holder.multiSpinner.setAdapterWithOutImage(context, spinList, new MultiSelectionSpinnerDialog.OnMultiSpinnerSelectionListener() {
                    @Override
                    public void OnMultiSpinnerItemSelected(List<String> chosenItems) {
                        myListData.setValue(new Gson().toJson(chosenItems).substring(1,new Gson().toJson(chosenItems).length()-1).replaceAll(",","~"));
                        onItemUpdateListener.onUpdateList(availMeetingList,position);
                    }
                });
                holder.multiSpinner.initMultiSpinner(context,holder.multiSpinner);



            }else {
                holder.etView.setText(myListData.getValue().equals("NA")?"-":myListData.getValue());
                holder.constrainToggleContainer.setVisibility(View.GONE);
                holder.txtView.setVisibility(View.GONE);
                holder.rlSpinnerExp.setVisibility(View.GONE);
                holder.constrainMSContainer.setVisibility(View.GONE);
                holder.toggleOptions.setVisibility(View.GONE);
                holder.etView.setVisibility(myListData.getType() != null && myListData.getType().equals("Date")?View.GONE:View.VISIBLE);
                holder.txtView.setVisibility(myListData.getType() != null && myListData.getType().equals("Date")?View.VISIBLE:View.GONE);
                holder.etView.setInputType(myListData.getType() != null && myListData.getType().equals("number")? InputType.TYPE_CLASS_NUMBER:InputType.TYPE_CLASS_TEXT);
            }
        }else{
            holder.txtView.setText(myListData.getValue().equals("NA")?"-":myListData.getValue());
            holder.constrainToggleContainer.setVisibility(View.GONE);
            holder.txtView.setVisibility(View.VISIBLE);
            holder.toggleOptions.setVisibility(View.GONE);
            holder.constrainMSContainer.setVisibility(View.GONE);
            holder.etView.setVisibility(View.GONE);
            holder.rlSpinnerExp.setVisibility(View.GONE);
        }

        holder.etView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if(myListData.getIs_edit().equals("Y")&& !myListData.getKey().equals("Experience in other Industries (Names)")) {
                    myListData.setValue(editable.toString().isEmpty() ? "" : editable.toString());
                    onItemUpdateListener.onUpdateList(availMeetingList,position);
                }
            }
        });


        holder.spinnerExp.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if(myListData.getType().equalsIgnoreCase("select")) {
                    myListData.setValue(holder.spinnerExp.getSelectedItem().toString());
                    onItemUpdateListener.onUpdateList(availMeetingList,position);
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        holder.toggleOptions.setOnToggleChanged(new ToggleButton.OnToggleChanged() {
            @Override
            public void onToggle(boolean on) {
                if(myListData.getType().equalsIgnoreCase("radio"))
                {
                    myListData.setValue(on?"Y":"N");
                    onItemUpdateListener.onUpdateList(availMeetingList,position);
                }
            }
        });

    }

    @Override
    public int getItemCount() {
        return availMeetingList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView txtView,txtTitle,txtOptions;
        private EditText etView;
        private Spinner spinnerExp;
        private ToggleButton toggleOptions;
        private MultiSpinner multiSpinner;
        private ConstraintLayout rlSpinnerExp,constrainToggleContainer,constrainMSContainer;
        private RadioButton radioYes,radioNo;
        public ViewHolder(View itemView) {
            super(itemView);
            this.txtTitle =  itemView.findViewById(R.id.txtTitle);
            this.txtOptions =  itemView.findViewById(R.id.txtOptions);
            this.txtView =  itemView.findViewById(R.id.txtView);
            this.etView =  itemView.findViewById(R.id.etView);
            this.constrainToggleContainer =  itemView.findViewById(R.id.constrainToggleContainer);
            this.constrainMSContainer =  itemView.findViewById(R.id.constrainMSContainer);
            this.toggleOptions =  itemView.findViewById(R.id.toggleOptions);
            this.multiSpinner =  itemView.findViewById(R.id.multiSpinner);
            //this.radioGroup =  itemView.findViewById(R.id.radioGroup);
            this.rlSpinnerExp =  itemView.findViewById(R.id.rlSpinnerExp);
            //this.radioYes =  itemView.findViewById(R.id.radioYes);
            //this.radioNo =  itemView.findViewById(R.id.radioNo);
            this.spinnerExp =  itemView.findViewById(R.id.spinnerExp);

        }
    }

    public void setOnItemUpdateListener(OnItemUpdateListener onItemUpdateListener)
    {
        this.onItemUpdateListener = onItemUpdateListener;
    }
}
