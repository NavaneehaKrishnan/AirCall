package com.example.sipappmerge.Utils;


import com.example.sipappmerge.model.ListItemBean;

import java.util.ArrayList;

public interface OnItemUpdateListener {
    void onUpdateList(ArrayList<ListItemBean> arrayList, int position);
}
