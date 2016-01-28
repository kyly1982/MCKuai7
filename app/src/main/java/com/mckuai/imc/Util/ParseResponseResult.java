package com.mckuai.imc.Util;

import android.content.Context;

import com.mckuai.imc.R;

import org.json.JSONObject;

/**
 * Created by kyly on 2016/1/28.
 * 对接口返回的结果进行预处理
 */
public class ParseResponseResult {
    public boolean isSuccess = false;
    public String pageBean = null;
    public String msg =null;

    /**
     * 将JSONObject类型的返回数据解析成预处理后的类
     * 会检查JSONObject的长度
     * 如果长度低于10个字符，则认为是失败
     * @param context
     * @param response
     */
    public ParseResponseResult(Context context,JSONObject response){
        this(context,response,false);
    }

    /**
     * 将JSONObject类型的返回数据解析成预处理后的类
     * @param context
     * @param response 返回的JSONObject
     * @param ignoreLength 是否检查返回的长度
     */
    public ParseResponseResult(Context context,JSONObject response,boolean ignoreLength) {
        if (checkLength(context, response, ignoreLength) && checkState(context,response)){
            setData(context,response);
        }

        if (checkLength(context,response,ignoreLength)){
            if (checkState(context,response)){
                setData(context,response);
                setPage(context,response);
            }
        }

    }

    private boolean checkLength(Context context,JSONObject response,boolean ignoreLength){
        if ((null == response && response.length() > 0) || (!ignoreLength && response.toString().length() < 10)){
            isSuccess = false;
            msg = context.getString(R.string.error_pretreatmentres_nullerror);
            return false;
        } else {
            return true;
        }
    }

    private boolean checkState(Context context,JSONObject response){
        if (response.has("state")){
            try {
                if (response.getString("state").equals("ok")){
                    isSuccess = true;
                    return true;
                }
            } catch (Exception e){
                e.printStackTrace();
                isSuccess = false;
                msg = context.getString(R.string.error_serverreturn_unkonw);
                return false;
            }
        }
        if (response.has("msg")) {
            try {
                msg = response.getString("msg");
            } catch (Exception e){
                e.printStackTrace();
                msg = context.getString(R.string.error_serverfalse);
            }

        } else {
            msg = context.getString(R.string.error_serverfalse_unknow);
        }
        return false;
    }

    private boolean setData(Context context,JSONObject response){
        if (response.has("dataObject")){
            try {
                msg = response.getString("dataObject");
                return true;
            } catch (Exception e){
                e.printStackTrace();
            }

        } else if (response.has("msg")){
            try {
                msg = response.getString("msg");
                return true;
            }catch (Exception e){
                e.printStackTrace();
            }
        }
        return false;
    }


    private boolean setPage(Context context,JSONObject response){
        if (response.has("pageBean")){
            try {
                pageBean = response.getString("pageBean");
                return true;
            } catch (Exception e){
                e.printStackTrace();
            }
        }
        return false;
    }
}
