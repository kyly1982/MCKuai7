package com.mckuai.imc.Util;

import java.util.Date;

/**
 * Created by kyly on 2016/1/22.
 */
public class TimestampConverter {
    public static String toString(long timestamp){
        int time = (int)(System.currentTimeMillis() - timestamp) / 60000;
        if (60 > time){
            return time +"分钟前";
        } else {
            Date last = new Date(timestamp);
            Date current = new Date(System.currentTimeMillis());
            int year = current.getYear() - last.getYear();
            int month = current.getMonth() - last.getMonth();
            int day = current.getDay() - last.getDate();

            if (year > 0){
                switch (year){
                    case 1:
                        return "去年";
                    default:
                        return year+"年前";
                }
            } else if (month > 0){
                switch (month){
                    case 1:
                        return "上个月";
                    default:
                        return month +"月前";
                }
            } else {
                switch (day){
                    case 1:
                        return "昨天";
                    case 2:
                        return "前天";
                    default:
                        return day +"天前";
                }
            }
        }
    }
}
