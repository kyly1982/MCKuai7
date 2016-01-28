package com.mckuai.imc.Bean;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

/**
 * Created by kyly on 2016/1/21.
 */
public class Cartoon implements Serializable {
    private int id;                         //id
    private int prise;                      //赞
    private int replyNum;                   //评论数
    private String content;                 //名字
    private String insertTime;              //创建时间
    private String image;                   //图片url
    private User owner;                     //所有者
    private ArrayList<Lable> lables;        //标签
    private ArrayList<Comment> comments;    //评论
    private ArrayList<User> rewardUsers;    //打赏用户
    private Page rewardPage;                //打赏分页
    private Page commentPage;               //评论分页信息

    public Cartoon() {
    }

    public Cartoon(String image, int prise, String time) {
        this.image = image;
        this.prise = prise;
        this.insertTime = time;
    }

    public Cartoon(String image,MCUser owner,ArrayList<Lable> lables){
        this.image = image;
        this.owner = new User(owner);
        this.lables = lables;
    }

    public Cartoon(String image, ArrayList<Lable> lables, User owner, ArrayList<Comment> comments, int prise) {
        this.comments = comments;
        this.image = image;
        this.lables = lables;
        this.owner = owner;
        this.prise = prise;
    }

    public Cartoon(String image, ArrayList<Lable> lables, User owner, ArrayList<Comment> comments, int prise, Page page) {
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

    public User getOwner() {
        return owner;
    }

    public void setOwner(User owner) {
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

    public String getTime() {
        return insertTime;
    }

    public void setTime(String time) {
        this.insertTime = time;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getInsertTime() {
        return insertTime;
    }

    public void setInsertTime(String insertTime) {
        this.insertTime = insertTime;
    }

    public int getReplyNum() {
        return replyNum;
    }

    public void setReplyNum(int replyNum) {
        this.replyNum = replyNum;
    }

    public ArrayList<User> getRewardUsers() {
        return rewardUsers;
    }

    public void setRewardUsers(ArrayList<User> rewardUsers) {
        this.rewardUsers = rewardUsers;
    }

    public Page getRewardPage() {
        return rewardPage;
    }

    public void setRewardPage(Page rewardPage) {
        this.rewardPage = rewardPage;
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

    public String getCommentEx(){
        return null == comments ? "0":comments.size() +"";
    }
}
