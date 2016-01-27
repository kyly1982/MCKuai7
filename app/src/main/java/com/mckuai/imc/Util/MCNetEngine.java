package com.mckuai.imc.Util;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.annotation.NonNull;

import com.google.gson.Gson;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.mckuai.imc.Bean.Cartoon;
import com.mckuai.imc.Bean.ForumInfo;
import com.mckuai.imc.Bean.MCUser;
import com.mckuai.imc.Bean.Page;
import com.mckuai.imc.Bean.Post;
import com.mckuai.imc.R;

import org.apache.http.Header;
import org.json.JSONObject;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.ArrayList;

/**
 * Created by kyly on 2016/1/22.
 */
public class MCNetEngine {
    private AsyncHttpClient httpClient;


    public MCNetEngine() {
        httpClient = new AsyncHttpClient();
        httpClient.setTimeout(10);
    }

    public void cancle(){
        httpClient.cancelAllRequests(true);
    }

    public interface OnLoginServerResponseListener {
        void onLoginSuccess(MCUser user);
        void onLoginFalse(String msg);
    }


    /***************************************************************************
     * 登录
     ***************************************************************************/

    public void loginServer(@NonNull final Context context, @NonNull final MCUser user, @NonNull final OnLoginServerResponseListener listener) {
        String url = context.getString(R.string.interface_domainName) + context.getString(R.string.interface_login);
        RequestParams params = new RequestParams();
        params.put("accessToken", user.getLoginToken().getToken());
        params.put("openId", user.getName());
        params.put("nickName", user.getNike());
        params.put("gender", user.getGender());
        params.put("headImg", user.getHeadImg());
        httpClient.get(url, params, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                PretreatmentResult result = pretreatmentResponse(context, response);
                if (result.isSuccess) {
                    Gson gson = new Gson();
                    MCUser userinfo = gson.fromJson(result.msg, MCUser.class);
                    if (null != userinfo && userinfo.getName().equals(user.getName())) {
                        listener.onLoginSuccess(userinfo);
                    } else {
                        listener.onLoginFalse(context.getString(R.string.error_parsefalse));
                    }
                } else {
                    listener.onLoginFalse(result.msg);
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                listener.onLoginFalse(context.getString(R.string.error_requestfalse, throwable.getLocalizedMessage()));
            }
        });
    }

    /***************************************************************************
     * 获取动漫列表
     ***************************************************************************/

    public interface OnCartoonListResponseListener{
        public void onSuccess(ArrayList<Cartoon> cartoons);
        public void onFaile(String msg);
    }

    public void loadCartoonList(final Context context,String cartoonType,final OnCartoonListResponseListener listener){
        String url = context.getString(R.string.interface_domainName) + context.getString(R.string.interface_domainName);
        RequestParams params = new RequestParams();
        params.put("type", cartoonType);
        httpClient.get(context, url, params, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                super.onSuccess(statusCode, headers, response);
                if (null != listener) {
                    listener.onSuccess(new ArrayList<Cartoon>());
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                super.onFailure(statusCode, headers, responseString, throwable);
                if (null != listener) {
                    listener.onFaile("onFailure");
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                super.onFailure(statusCode, headers, throwable, errorResponse);
                listener.onFaile("onFailure");
            }
        });
    }

    /***************************************************************************
     * 获取版块列表
     ***************************************************************************/

    public interface OnForumListResponseListener{
        public void onForumSuccess(ArrayList<ForumInfo> forums);
        public void onForumFaile(String msg);
    }

    public void getForumList(final Context context,final OnForumListResponseListener listener){

    }


    /***************************************************************************
     * 获取帖子列表
     ***************************************************************************/

    public interface OnPostListResponseListener{
        public void onPostSuccess(ArrayList<Post> posts);
        public void onPostFaile(String msg);
    }

    public void getPostList(final  Context context,int forumId,Page page,final OnPostListResponseListener listener){

    }


    /***************************************************************************
     * 获取人物列表
     ***************************************************************************/

    public interface OnCharacterListResponseListener{
        public void onSuccess(ArrayList<String> characters);
        public void onFaile(String msg);
    }

    public void loadCharacterList(final Context context,Page page,final OnCharacterListResponseListener listener){

    }



    /***************************************************************************
     * 对获取工具列表
     ***************************************************************************/

    public interface OnToolListResponseListener{
        public void onSuccess(ArrayList<String> tools);
        public void onFaile(String msg);
    }

    public void loadToolList(final Context context,Page page,final OnToolListResponseListener listener){

    }

    public interface OnUploadImageResponseListener{
        public void onSuccess(String url);
        public void onFaile(String msg);
    }

    public void uploadImage(Context context,Bitmap image,OnUploadImageResponseListener listener){
        String url = context.getString(R.string.interface_domainName) + context.getString(R.string.interface_uploadimage);
        RequestParams params = new RequestParams();
        params.put("upload",Bitmap2IS(image),"01.jpg","image/jpeg");
        httpClient.post(url,params,new JsonHttpResponseHandler(){
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                super.onSuccess(statusCode, headers, response);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                super.onFailure(statusCode, headers, responseString, throwable);
            }
        });
    }


    public interface OnUploadCartoonResponseListener{
        public void onSuccess();
        public void onFaile(String msg);
    }

    public void uploadCartoon(Context context,Cartoon cartoon, final OnUploadCartoonResponseListener listener){
        String url = context.getString(R.string.interface_domainName) + context.getString(R.string.interface_uploadcartoon);
        RequestParams params = new RequestParams();
        params.put("userId",cartoon.getOwner().getId());
        params.put("title","title");
        params.put("imageUrl",cartoon.getImage());
        httpClient.post(context, url, params, new JsonHttpResponseHandler(){
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                super.onSuccess(statusCode, headers, response);
                if (null != response && response.toString().length() > 10){
                    if (response.has("state")){
                        try {
                           if ( "ok".equals(response.getString("state"))){
                               listener.onSuccess();
                           }
                        } catch (Exception e){
                            e.printStackTrace();
                        }
                    }
                }
                listener.onFaile("上传失败");
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                super.onFailure(statusCode, headers, responseString, throwable);
                listener.onFaile(throwable.getLocalizedMessage());
            }
        });
    }







    /***************************************************************************
     * 对获取的结果进行预处理
     ***************************************************************************/

    static class PretreatmentResult {
        boolean isSuccess = false;
        String msg;
    }

    private static PretreatmentResult pretreatmentResponse(@NonNull Context context, JSONObject response) {
        PretreatmentResult result = new PretreatmentResult();
        if (null == response || 10 > response.toString().length()) {
            result.msg = context.getString(R.string.error_pretreatmentres_nullerror);
            return result;
        }
        if (response.has("state")) {
            try {
                if (response.getString("state").equals("ok")) {
                    result.isSuccess = true;
                    if (response.has("dataObject")) {
                        result.msg = response.getString("dataObject");
                    }
                } else {
                    if (response.has("msg")) {
                        result.msg = response.getString("msg");
                    } else {
                        result.msg = context.getString(R.string.error_serverfalse_unknow);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
                result.msg = context.getString(R.string.error_pretreatmentres_ponsefalse, e.getLocalizedMessage());
            }
        } else {
            result.msg = context.getString(R.string.error_serverreturn_unkonw);
        }

        return result;
    }

    private static InputStream Bitmap2IS(Bitmap bm)
    {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bm.compress(Bitmap.CompressFormat.JPEG, 80, baos);
        InputStream sbs = new ByteArrayInputStream(baos.toByteArray());
        return sbs;
    }
}
