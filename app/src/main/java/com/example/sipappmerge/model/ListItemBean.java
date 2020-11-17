package com.example.sipappmerge.model;

public class ListItemBean {
//Experience in other Industries (Names)
    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getIs_edit() {
        return is_edit;
    }

    public void setIs_edit(String is_edit) {
        this.is_edit = is_edit;
    }

    String key,value,is_edit;

    public String getType() {
        return Type;
    }

    public void setType(String type) {
        Type = type;
    }


    public String getOption() {
        return option;
    }

    public void setOption(String option) {
        this.option = option;
    }

    String Type,option;
}
