package com.mckuai.imc.Bean;

import java.util.ArrayList;

public class ForumList {
    private String state;
    private ArrayList<ForumInfo> dataObject;

    public ArrayList<ForumInfo> getDataObject() {
        return dataObject;
    }

    public void setDataObject(ArrayList<ForumInfo> dataObject) {
        this.dataObject = dataObject;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }
}
