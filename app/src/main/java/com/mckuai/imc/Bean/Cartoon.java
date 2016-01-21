package com.mckuai.imc.Bean;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by kyly on 2016/1/21.
 */
public class Cartoon implements Serializable {
    private int prise;                      //赞
    private String image;                   //图片url
    private MCBasicUser owner;              //所有者
    private ArrayList<Lable> lables;        //标签
    private ArrayList<Comment> comments;    //评论
    private Page commentPage;               //评论分页信息

    public Cartoon() {
    }

    public Cartoon(String image, ArrayList<Lable> lables, MCBasicUser owner, ArrayList<Comment> comments, int prise) {
        this.comments = comments;
        this.image = image;
        this.lables = lables;
        this.owner = owner;
        this.prise = prise;
    }

    public Cartoon(String image, ArrayList<Lable> lables, MCBasicUser owner, ArrayList<Comment> comments, int prise, Page page) {
        this.comments = comments;
        this.image = image;
        this.lables = lables;
        this.owner = owner;
        this.prise = prise;
        this.commentPage = page;
    }

    public ArrayList<Comment> getComments() {
        return comments;
    }

    public void setComments(ArrayList<Comment> comments) {
        this.comments = comments;
    }

    public ArrayList<Lable> getLables() {
        return lables;
    }

    public void setLables(ArrayList<Lable> lables) {
        this.lables = lables;
    }

    public MCBasicUser getOwner() {
        return owner;
    }

    public void setOwner(MCBasicUser owner) {
        this.owner = owner;
    }

    public int getPrise() {
        return prise;
    }

    public void setPrise(int prise) {
        this.prise = prise;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public Page getCommentPage() {
        return commentPage;
    }

    public void setCommentPage(Page commentPage) {
        this.commentPage = commentPage;
    }
}
