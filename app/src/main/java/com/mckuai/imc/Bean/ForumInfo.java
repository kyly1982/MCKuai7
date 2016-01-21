package com.mckuai.imc.Bean;

import com.mckuai.imc.Type;

import java.io.Serializable;
import java.util.ArrayList;

public class ForumInfo implements Serializable {
    private static final long serialVersionUID = 3311829041014984013L;
    private int id;
    private int talkNum;
    private String name;
    private String icon;
    private ArrayList<Type> includeType;

    public ForumInfo(int id, String name, String icon, int talkNum, ArrayList<Type> types) {
        this.icon = icon;
        this.id = id;
        this.name = name;
        this.talkNum = talkNum;
        this.includeType = types;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getTalkNum() {
        return talkNum;
    }

    public void setTalkNum(int talkNum) {
        this.talkNum = talkNum;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }


    public ArrayList<Type> getIncludeType() {
        return includeType;
    }

    public void setIncludeType(ArrayList<Type> includeType) {
        this.includeType = includeType;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }
}
