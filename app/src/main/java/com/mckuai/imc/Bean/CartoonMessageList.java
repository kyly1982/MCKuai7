package com.mckuai.imc.Bean;

import java.util.ArrayList;

/**
 * Created by kyly on 2016/2/15.
 */
public class CartoonMessageList {
    private ArrayList<CartoonMessage> list;
    private MCUser user;

    public ArrayList<CartoonMessage> getList() {
        return list;
    }

    public void setList(ArrayList<CartoonMessage> list) {
        this.list = list;
    }

    public MCUser getUser() {
        return user;
    }

    public void setUser(MCUser user) {
        this.user = user;
    }
}
