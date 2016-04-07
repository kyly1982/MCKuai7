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
    private int groupId;                    //组id，供pk时使用
    private int prise;                      //赞
    private int replyNum;                   //评论数
    private int allPk;                      //总pk数
    private int winPk;                      //pk胜数
    private String content;                 //内容
    private String kinds;                   //主题
    private String insertTime;              //创建时间
    private String image;                   //图片url
    private User owner;                     //所有者
    //private ArrayList<Lable> lables;        //标签
    private ArrayList<Comment> comments;    //评论
    private ArrayList<User> rewardList;    //打赏用户
    private Page pageBean;

    public Cartoon() {
    }

    public Cartoon(int id) {
        this.id = id;
    }

    public Cartoon(String image, int prise, String time) {
        this.image = image;
        this.prise = prise;
        this.insertTime = time;
    }

    public Cartoon(String image,MCUser owner,ArrayList<Lable> lables){
        this.image = image;
        this.owner = new User(owner);
        //this.lables = lables;
    }

    public Cartoon(String title, String image, MCUser owner, ArrayList<Lable> lables) {
        this.content = title;
        this.image = image;
        this.owner = new User(owner);
        //this.lables = lables;
    }

    public Cartoon(String image, ArrayList<Lable> lables, User owner, ArrayList<Comment> comments, int prise) {
        this.comments = comments;
        this.image = image;
        //this.lables = lables;
        this.owner = owner;
        this.prise = prise;
    }

    public Cartoon(String image, ArrayList<Lable> lables, User owner, ArrayList<Comment> comments, int prise, Page page) {
        this.comments = comments;
        this.image = image;
        //this.lables = lables;
        this.owner = owner;
        this.prise = prise;
        this.pageBean = page;
    }

    public Cartoon(ArrayList<Comment> comments, String content, int id, String image, String insertTime, ArrayList<Lable> lables, User owner, Page pageBean, int prise, int replyNum, ArrayList<User> rewardList) {
        this.comments = comments;
        this.content = content;
        this.id = id;
        this.image = image;
        this.insertTime = insertTime;
        //this.lables = lables;
        this.owner = owner;
        this.pageBean = pageBean;
        this.prise = prise;
        this.replyNum = replyNum;
        this.rewardList = rewardList;
    }

    public ArrayList<Comment> getComments() {
        return comments;
    }

    public void setComments(ArrayList<Comment> comments) {
        this.comments = comments;
    }

/*    public ArrayList<Lable> getLables() {
        return lables;
    }

    public void setLables(ArrayList<Lable> lables) {
        this.lables = lables;
    }*/

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

    public ArrayList<User> getRewardList() {
        return rewardList;
    }

    public void setRewardList(ArrayList<User> rewardList) {
        this.rewardList = rewardList;
    }

    public Page getPageBean() {
        return pageBean;
    }

    public void setPageBean(Page pageBean) {
        this.pageBean = pageBean;
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

    public String getKinds() {
        return kinds;
    }

    public void setKinds(String kinds) {
        this.kinds = kinds;
    }

    public String getKindsEx(){
        if (null != kinds){
            return kinds;
        } else {
            return content;
        }
    }

    public int getAllPk() {
        return allPk;
    }

    public void setAllPk(int allPk) {
        this.allPk = allPk;
    }

    public int getWinPk() {
        return winPk;
    }

    public void setWinPk(int winPk) {
        this.winPk = winPk;
    }

    public int getGroupId() {
        return groupId;
    }

    public void setGroupId(int groupId) {
        this.groupId = groupId;
    }
}
