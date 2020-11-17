package com.example.sipappmerge.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.example.sipappmerge.Merge.Contact;
import com.example.sipappmerge.R;

import java.util.ArrayList;

public class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.MyViewHolder> {

    private ArrayList<Contact> dataSet;
    private Context context;

    public  class MyViewHolder extends RecyclerView.ViewHolder {

        TextView txtName,txtTime,txtName1,txtTime1,txtWPS,txtRequest,Eidt,Delete,txtBR,txtBP,txtAttachment;
        ImageView imgProfile;
        ConstraintLayout constrainRight,constrainLeft;

        public MyViewHolder(View itemView) {
            super(itemView);
            this.txtName =  itemView.findViewById(R.id.txtName);
            this.txtTime =  itemView.findViewById(R.id.txtTime);
            this.txtName1=  itemView.findViewById(R.id.txtName1);
            this.txtTime1 =  itemView.findViewById(R.id.txtTime1);
            this.imgProfile =  itemView.findViewById(R.id.imgProfile);
            this.constrainRight =  itemView.findViewById(R.id.constrainRight);
            this.constrainLeft =  itemView.findViewById(R.id.constrainLeft);



        }
    }

    public ChatAdapter(ArrayList<Contact> data, Context context) {
        this.dataSet = data;
        this.context = context;
    }

    @Override
    public ChatAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent,
                                                          int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.chat_items, parent, false);



        ChatAdapter.MyViewHolder myViewHolder = new ChatAdapter.MyViewHolder(view);
        return myViewHolder;
    }

    @Override
    public void onBindViewHolder(final ChatAdapter.MyViewHolder holder, final int listPosition) {

        Contact c= dataSet.get(listPosition);


        if(c.getIsIn().equalsIgnoreCase("yes"))
        {
            holder.txtName1.setText(c.getStrMessage());
            holder.txtTime1.setText(c.getStrTime());
            holder.constrainRight.setVisibility(View.VISIBLE);
            holder.constrainLeft.setVisibility(View.GONE);
        }else
        {
            holder.txtName.setText(c.getStrMessage());
            holder.txtTime.setText(c.getStrTime());
            holder.constrainRight.setVisibility(View.GONE);
            holder.constrainLeft.setVisibility(View.VISIBLE);
        }

    }

    @Override
    public int getItemCount() {
        return dataSet.size();
    }

    public void updateValues(ArrayList<Contact> dataSet)
    {
        this.dataSet.addAll(dataSet);
        notifyDataSetChanged();

    }
}
