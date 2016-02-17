package com.mckuai.imc.Bean;

import com.mckuai.imc.Util.TimestampConverter;

import java.io.Serializable;

/**
 * 动画的评论
 * Created by kyly on 2016/1/21.
 */
public class Comment implements Serializable {
    private User owner;  //评论者
    private String content;     //评论内容
    private long time;
    private String insertTime;

    public Comment() {
    }

    public Comment(User owner, String content) {
        this.content = content;
        this.owner = owner;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public User getOwner() {
        return owner;
    }

    public void setOwner(User owner) {
        this.owner = owner;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public String getTimeEx(){
        return TimestampConverter.toString(insertTime);
    }

    public String getInsertTime() {
        return insertTime;
    }

    public void setInsertTime(String insertTime) {
        this.insertTime = insertTime;
    }

    public String getInsertTimeEx() {
        return TimestampConverter.toString(insertTime);
    }


}
