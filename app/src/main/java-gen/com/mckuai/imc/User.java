package com.mckuai.imc;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT. Enable "keep" sections if you want to edit. 

import android.support.annotation.NonNull;

import com.mckuai.imc.Bean.MCUser;

/**
 * Entity mapped to table "User".
 */
public class User {

    private Long id;
    private Integer score;
    private Integer isServerActor;
    private Integer postCount;
    private Integer chatRoomNum;
    private Integer dynamicNum;
    private Integer messageNum;
    private Integer workNum;
    private Integer level;
    private Integer token;
    private Float process;
    /**
     * Not-null value.
     */
    private String name;
    private String nickName;
    private String cover;
    private String gender;
    private String city;

    public User() {
    }

    public User(Long id) {
        this.id = id;
    }

    public User(Long id, Integer score, Integer isServerActor, Integer postCount, Integer chatRoomNum, Integer dynamicNum, Integer messageNum, Integer workNum, Integer level, Integer token, Float process, String name, String nickName, String cover, String gender, String city) {
        this.id = id;
        this.score = score;
        this.isServerActor = isServerActor;
        this.postCount = postCount;
        this.chatRoomNum = chatRoomNum;
        this.dynamicNum = dynamicNum;
        this.messageNum = messageNum;
        this.workNum = workNum;
        this.level = level;
        this.token = token;
        this.process = process;
        this.name = name;
        this.nickName = nickName;
        this.cover = cover;
        this.gender = gender;
        this.city = city;
    }

    public User(@NonNull MCUser user) {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getScore() {
        return score;
    }

    public void setScore(Integer score) {
        this.score = score;
    }

    public Integer getIsServerActor() {
        return isServerActor;
    }

    public void setIsServerActor(Integer isServerActor) {
        this.isServerActor = isServerActor;
    }

    public Integer getPostCount() {
        return postCount;
    }

    public void setPostCount(Integer postCount) {
        this.postCount = postCount;
    }

    public Integer getChatRoomNum() {
        return chatRoomNum;
    }

    public void setChatRoomNum(Integer chatRoomNum) {
        this.chatRoomNum = chatRoomNum;
    }

    public Integer getDynamicNum() {
        return dynamicNum;
    }

    public void setDynamicNum(Integer dynamicNum) {
        this.dynamicNum = dynamicNum;
    }

    public Integer getMessageNum() {
        return messageNum;
    }

    public void setMessageNum(Integer messageNum) {
        this.messageNum = messageNum;
    }

    public Integer getWorkNum() {
        return workNum;
    }

    public void setWorkNum(Integer workNum) {
        this.workNum = workNum;
    }

    public Integer getLevel() {
        return level;
    }

    public void setLevel(Integer level) {
        this.level = level;
    }

    public Integer getToken() {
        return token;
    }

    public void setToken(Integer token) {
        this.token = token;
    }

    public Float getProcess() {
        return process;
    }

    public void setProcess(Float process) {
        this.process = process;
    }

    /**
     * Not-null value.
     */
    public String getName() {
        return name;
    }

    /**
     * Not-null value; ensure this value is available before it is saved to the database.
     */
    public void setName(String name) {
        this.name = name;
    }

    public String getNickName() {
        return nickName;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }

    public String getCover() {
        return cover;
    }

    public void setCover(String cover) {
        this.cover = cover;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

}
