package com.mckuai.imc.Bean;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by kyly on 2016/2/1.
 */
public class CartoonMessage {
    private Cartoon cartoon;
    private String content;
    private String insertTime;
    private String type;
    private User owner;

    public Cartoon getCartoon() {
        return cartoon;
    }

    public void setCartoon(Cartoon cartoon) {
        this.cartoon = cartoon;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getInsertTime() {
        return insertTime;
    }

    public void setInsertTime(String insertTime) {
        this.insertTime = insertTime;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public User getOwner() {
        return owner;
    }

    public void setOwner(User owner) {
        this.owner = owner;
    }

    public String getTimeEx()  {
        SimpleDateFormat sdf_input = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
        SimpleDateFormat sdf_output = new SimpleDateFormat("MM月dd日");
        try {
            Date date = sdf_input.parse(insertTime);
            return sdf_output.format(date);
        } catch (Exception e){
            return "未知";
        }
    }

}
