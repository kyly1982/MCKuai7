package com.mckuai.imc.Bean;

import java.util.ArrayList;

/**
 * Created by kyly on 2016/2/15.
 */
public class CartoonWorkList {
    private ArrayList<Cartoon> list;
    private MCUser user;

    public ArrayList<Cartoon> getList() {
        return list;
    }

    public void setList(ArrayList<Cartoon> list) {
        this.list = list;
    }

    public MCUser getUser() {
        return user;
    }

    public void setUser(MCUser user) {
        this.user = user;
    }
}
