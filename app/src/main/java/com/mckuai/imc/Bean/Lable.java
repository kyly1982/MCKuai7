package com.mckuai.imc.Bean;

import android.graphics.Point;

import java.io.Serializable;

/**
 * 动画中的标签（对话）信息
 * Created by kyly on 2016/1/21.
 */
public class Lable implements Serializable {
    private int id;
    private Point coordinate;  //标签坐标
    private String content;     //标签内容

    public Lable() {
    }

    public Lable(int id,Point coordinate, String content) {
        this.content = content;
        this.id = id;
        if (null != coordinate) {
            this.coordinate = coordinate;
        } else {
            this.coordinate = new Point(0, 0);
        }
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Point getCoordinate() {
        return coordinate;
    }

    public void setCoordinate(Point coordinate) {
        this.coordinate = coordinate;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}
