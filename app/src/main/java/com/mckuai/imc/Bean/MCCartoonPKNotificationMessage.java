package com.mckuai.imc.Bean;

import android.os.Parcel;
import android.util.Log;

import com.google.gson.Gson;

import java.io.UnsupportedEncodingException;

import io.rong.common.ParcelUtils;
import io.rong.imlib.MessageTag;
import io.rong.imlib.model.MessageContent;
import io.rong.imlib.model.UserInfo;

/**
 * Created by kyly on 2016/5/4.
 */


@MessageTag(value="mckuai:cartoonpk",flag = MessageTag.NONE)
public class MCCartoonPKNotificationMessage extends MessageContent {

    private Cartoon winner;
    private Cartoon loser;


    public MCCartoonPKNotificationMessage(Cartoon winCartoon,Cartoon loseCartoon ){
        this.winner = winCartoon;
        this.loser = loseCartoon;
    }

    /**
     * 该方法将对收到的消息进行解析，先由 byte 转成 json 字符串，再将 json 中内容取出赋值给消息属性
     * @param data
     */
    public MCCartoonPKNotificationMessage(byte[] data) {
        //super(data);
        String jsonStr = null;
        try {
            jsonStr = new String(data,"UTF-8");
        } catch (Exception e){
            e.printStackTrace();
        }
        if (null != jsonStr && jsonStr.length() > 10){
            Gson gson = new Gson();
            MCCartoonPKNotificationMessage msg = gson.fromJson(jsonStr,MCCartoonPKNotificationMessage.class);
            this.winner = msg.getWinner();
            this.loser = msg.getLoser();
        }
    }

    /**
     * 给消息赋值,实现 Parcelable 接口中的方法
     * @param in
     */
    public MCCartoonPKNotificationMessage(Parcel in){
        String stl = ParcelUtils.readFromParcel(in);//该类为工具类，消息属性，由writeToParcel写入
        setUserInfo(ParcelUtils.readFromParcel(in,UserInfo.class));
        Log.e("MCPKN_P","in="+stl);
    }

    /**
     * 读取接口，目的是要从Parcel中构造一个实现了Parcelable的类的实例处理。
     */
    public static final Creator<MCCartoonPKNotificationMessage> CREATOR = new Creator<MCCartoonPKNotificationMessage>() {
        @Override
        public MCCartoonPKNotificationMessage createFromParcel(Parcel source) {
            return new MCCartoonPKNotificationMessage(source);
        }

        @Override
        public MCCartoonPKNotificationMessage[] newArray(int size) {
            return new MCCartoonPKNotificationMessage[size];
        }
    };

    /**
     * 将消息属性封装成json串，再将json串转成byte数组
     * 此方法会在发送消息时被调用
     * @return 转换后的byte数组
     */
    @Override
    public byte[] encode() {
        Gson gson = new Gson();
        String jsonStr = gson.toJson(this);
        if (null != jsonStr && jsonStr.length() > 10) {
            try {
                return jsonStr.getBytes("UTF-8");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }
        return new byte[0];
    }

    /**
     * 描述了包含在 Parcelable 对象排列信息中的特殊对象的类型。
     * @return 一个标志位，表明Parcelable对象特殊对象类型集合的排列。
     */
    @Override
    public int describeContents() {
        return 0;
    }

    /**
     * 将类的数据写入外部提供的 Parcel 中。
     * @param dest  对象被写入的 Parcel。
     * @param flags 对象如何被写入的附加标志。
     */
    @Override
    public void writeToParcel(Parcel dest, int flags) {
        ParcelUtils.writeToParcel(dest,"测试测试");
        ParcelUtils.writeToParcel(dest,getUserInfo());
    }

    public Cartoon getLoser() {
        return loser;
    }

    public void setLoser(Cartoon loser) {
        this.loser = loser;
    }

    public Cartoon getWinner() {
        return winner;
    }

    public void setWinner(Cartoon winner) {
        this.winner = winner;
    }
}
