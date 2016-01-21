package com.mckuai.imc.Bean;

import java.io.Serializable;

/**
 * Created by kyly on 2016/1/21.
 */
public class MCBasicUser implements Serializable {
    protected int id;
    protected String name;//openId
    protected String headImage;//头像
    protected String nick;//昵称

    public String getHeadImage() {
        return headImage;
    }

    public void setHeadImage(String headImage) {
        this.headImage = headImage;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getNick() {
        return nick;
    }

    public void setNick(String nick) {
        this.nick = nick;
    }
}
