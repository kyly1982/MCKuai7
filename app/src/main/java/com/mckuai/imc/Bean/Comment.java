package com.mckuai.imc.Bean;

import java.io.Serializable;

/**
 * 动画的评论
 * Created by kyly on 2016/1/21.
 */
public class Comment implements Serializable {
    private MCBasicUser owner;  //评论者
    private String content;     //评论内容

    public Comment() {
    }

    public Comment(MCBasicUser owner, String content) {
        this.content = content;
        this.owner = owner;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public MCBasicUser getOwner() {
        return owner;
    }

    public void setOwner(MCBasicUser owner) {
        this.owner = owner;
    }
}
