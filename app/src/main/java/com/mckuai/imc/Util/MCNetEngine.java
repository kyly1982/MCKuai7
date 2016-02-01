package com.mckuai.imc.Util;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.annotation.NonNull;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.mckuai.imc.Base.MCKuai;
import com.mckuai.imc.Bean.Cartoon;
import com.mckuai.imc.Bean.CartoonMessage;
import com.mckuai.imc.Bean.CommunityDynamic;
import com.mckuai.imc.Bean.CommunityMessage;
import com.mckuai.imc.Bean.ForumInfo;
import com.mckuai.imc.Bean.MCUser;
import com.mckuai.imc.Bean.Page;
import com.mckuai.imc.Bean.Post;
import com.mckuai.imc.Bean.User;
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
    private Gson gson;

    public MCNetEngine() {
        httpClient = new AsyncHttpClient();
        httpClient.setTimeout(10);
        gson = new Gson();
    }

    public void cancle(){
        httpClient.cancelAllRequests(true);
    }

    public interface OnLoginServerResponseListener {
        void onLoginSuccess(MCUser user);
        void onLoginFailure(String msg);
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
                ParseResponseResult result = new ParseResponseResult(context, response);
                if (result.isSuccess) {
                    MCUser userinfo = gson.fromJson(result.msg, MCUser.class);
                    if (null != userinfo && userinfo.getName().equals(user.getName())) {
                        listener.onLoginSuccess(userinfo);
                    } else {
                        listener.onLoginFailure(context.getString(R.string.error_parsefalse));
                    }
                } else {
                    listener.onLoginFailure(result.msg);
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                listener.onLoginFailure(context.getString(R.string.error_requestfalse, throwable.getLocalizedMessage()));
            }
        });
    }



    /***************************************************************************
     * 获取版块列表
     ***************************************************************************/

    public interface OnForumListResponseListener{
        public void onLoadForumListSuccess(ArrayList<ForumInfo> forums);
        public void onLoadForumListFailure(String msg);
    }

    public void loadFroumList(final Context context,final OnForumListResponseListener listener){

    }


    /***************************************************************************
     * 获取帖子列表
     ***************************************************************************/

    public interface OnPostListResponseListener{
        public void onLoadPostListSuccess(ArrayList<Post> posts);
        public void onLoadPostListFailure(String msg);
    }

    public void loadPostList(final  Context context,int forumId,Page page,final OnPostListResponseListener listener){

    }


    /***************************************************************************
     * 获取人物列表
     ***************************************************************************/

    public interface OnCharacterListResponseListener{
        public void onLoadCharacterListSuccess(ArrayList<String> characters);
        public void onLoadCharacterListFailure(String msg);
    }

    public void loadCharacterList(final Context context,Page page,final OnCharacterListResponseListener listener){

    }



    /***************************************************************************
     * 对获取工具列表
     ***************************************************************************/

    public interface OnToolListResponseListener{
        public void onLoadToolListSuccess(ArrayList<String> tools);
        public void onLoadToolListFailure(String msg);
    }

    public void loadToolList(final Context context,Page page,final OnToolListResponseListener listener){

    }

    /***************************************************************************
     * 上传图片
     ***************************************************************************/

    public interface OnUploadImageResponseListener{
        public void onImageUploadSuccess(String url);
        public void onImageUploadFailure(String msg);
    }

    public void uploadImage(final Context context,ArrayList<Bitmap> bitmaps, final OnUploadImageResponseListener listener){
        //String url = "http://www.mckuai.com/" + context.getString(R.string.interface_uploadimage);
        String url= "http://192.168.10.104/" + context.getString(R.string.interface_uploadimage_cartoon);
        RequestParams params = new RequestParams();
        if (null != bitmaps && !bitmaps.isEmpty()){
            String fileName =null;
            for (int i = 0;i < bitmaps.size();i++){
                fileName = i+".jpg";
                params.put("upload",Bitmap2IS(bitmaps.get(i)),fileName,"image/jpeg");
            }
        }
        httpClient.post(url, params, new JsonHttpResponseHandler(){
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                ParseResponseResult result = new ParseResponseResult(context,response);
                if (result.isSuccess){
                    listener.onImageUploadSuccess(result.msg);
                } else {
                    listener.onImageUploadFailure(result.msg);
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                listener.onImageUploadFailure(throwable.getLocalizedMessage());
            }
        });
    }

    /***************************************************************************
     * 上传动漫
     ***************************************************************************/

    public interface OnUploadCartoonResponseListener{
        public void onUploadCartoonSuccess();
        public void onUploadCartoonFailure(String msg);
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
                               listener.onUploadCartoonSuccess();
                           }
                        } catch (Exception e){
                            e.printStackTrace();
                        }
                    }
                }
                listener.onUploadCartoonFailure("上传失败");
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                super.onFailure(statusCode, headers, responseString, throwable);
                listener.onUploadCartoonFailure(throwable.getLocalizedMessage());
            }
        });
    }

    /***************************************************************************
     * 打赏动漫
     ***************************************************************************/

    public interface OnRewardCartoonResponseListener{
        public void onRewardCartoonSuccess();
        public void onRewardaCartoonFailure(String msg);
    }

    /**
     * 打赏
     *
     * @param context
     * @param isCartoon 如果是打赏动漫，则设置为true,否则设置为false
     * @param targetId 要打赏的目标的id
     * @param listener 打赏响应监听
     */
    public void rewardCartoon(final Context context,boolean isCartoon,int targetId, final OnRewardCartoonResponseListener listener){
        String url = context.getString(R.string.interface_domainName) + context.getString(R.string.interface_reward);
        RequestParams params = new RequestParams();
        if (isCartoon){
            params.put("type","cartoon");
        }
        params.put("userId", MCKuai.instence.user.getId());
        params.put("talkId", targetId);
        httpClient.post(url,params,new JsonHttpResponseHandler(){
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                ParseResponseResult result = new ParseResponseResult(context,response);
                if (result.isSuccess) {
                    listener.onRewardCartoonSuccess();
                } else {
                    listener.onRewardaCartoonFailure(result.msg);
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                listener.onRewardaCartoonFailure(throwable.getLocalizedMessage());
            }
        });
    }

    /***************************************************************************
     * 动漫评论
     ***************************************************************************/

    public interface OnCommentCartoonResponseListener{
        public void onCommentCartoonSuccess();
        public void onCommentCartoonFailure(String msg);
    }

    public void commentCartoon(final Context context,int cartoonId,String content, final OnCommentCartoonResponseListener listener){
        String url = context.getString(R.string.interface_domainName) +context.getString(R.string.interface_commentcartoon);
        RequestParams params = new RequestParams();
        params.put("id",cartoonId);
        params.put("userId",MCKuai.instence.user.getId());
        params.put("content",content);
        httpClient.post(url,params,new JsonHttpResponseHandler(){
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                ParseResponseResult result = new ParseResponseResult(context,response);
                if (result.isSuccess){
                    listener.onCommentCartoonSuccess();
                } else {
                    listener.onCommentCartoonFailure(result.msg);
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                listener.onCommentCartoonFailure(throwable.getLocalizedMessage());
            }
        });
    }

    /***************************************************************************
     * 获取推荐动漫(仅显示一次)
     ***************************************************************************/

    public interface OnLoadPushlCartoonListListener{
        public void onLoadPushCartoonSuccess(ArrayList<Cartoon> cartoons);
        public void onLoadPushCartoonFailure(String msg);
    }

    public void loadPushCartoonList(final Context context,Integer lastCartoonId, final OnLoadPushlCartoonListListener listListener){
        String url = context.getString(R.string.interface_domainName) + context.getString(R.string.interface_pushcartoon);
        RequestParams params = new RequestParams();
        if (null != lastCartoonId){
            params.put("ids",lastCartoonId+"");
        }
        httpClient.post(url,params,new JsonHttpResponseHandler(){
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                ParseResponseResult result = new ParseResponseResult(context,response);
                if (result.isSuccess){
                    ArrayList<Cartoon> cartoons = gson.fromJson(result.msg, new TypeToken<ArrayList<Cartoon>>() {
                    }.getType());
                    listListener.onLoadPushCartoonSuccess(cartoons);
                } else {
                    listListener.onLoadPushCartoonFailure(result.msg);
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                listListener.onLoadPushCartoonFailure(throwable.getLocalizedMessage());
            }
        });
    }

    /***************************************************************************
     * 获取动漫列表(一直显示)
     ***************************************************************************/

    public interface OnLoadCartoonListResponseListener{
        public void onLoadCartoonListSuccess(ArrayList<Cartoon> cartoons);
        public void onLoadCartoonListFailure(String msg);
    }

    public void loadCartoonList(final Context context,String cartoonType,Integer lastCartoonId,final OnLoadCartoonListResponseListener listener){
        String url = context.getString(R.string.interface_domainName) + context.getString(R.string.interface_loadcartoonlist);
        RequestParams params = new RequestParams();
        if (lastCartoonId != null){
            params.put("id",lastCartoonId);
        }
        //params.put("type", cartoonType);
        httpClient.get(context, url, params, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                ParseResponseResult result = new ParseResponseResult(context, response);
                if (result.isSuccess) {
                    ArrayList<Cartoon> cartoons = gson.fromJson(result.msg, new TypeToken<ArrayList<Cartoon>>() {
                    }.getType());
                    listener.onLoadCartoonListSuccess(cartoons);
                } else {
                    listener.onLoadCartoonListFailure(result.msg);
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                super.onFailure(statusCode, headers, throwable, errorResponse);
                listener.onLoadCartoonListFailure("onFailure");
            }
        });
    }

    /***************************************************************************
     * 个人中心动漫消息
     ***************************************************************************/

    public interface OnLoadCartoonMessageResponseListener{
        void onLoadCartoonMessageSuccess(ArrayList<CartoonMessage> cartoons,Page page);
        void onLoadCartoonMessageFailure(String msg);
    }

    public void loadCartoonMessage(final Context context,int userId,int nextpage, final OnLoadCartoonMessageResponseListener listener){
        String url = context.getString(R.string.interface_domainName) + context.getString(R.string.interface_cartoonmessage);
        RequestParams params = new RequestParams();
        params.put("userId",userId);
        params.put("page",nextpage);
        httpClient.post(url,params,new JsonHttpResponseHandler(){
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                ParseResponseResult result = new ParseResponseResult(context,response);
                if (result.isSuccess){
                    ArrayList<CartoonMessage> messages = gson.fromJson(result.msg, new TypeToken<ArrayList<CartoonMessage>>() {}.getType());
                    Page page = gson.fromJson(result.pageBean,Page.class);
                    listener.onLoadCartoonMessageSuccess(messages,page);
                } else {
                    if (null != listener){
                        listener.onLoadCartoonMessageFailure(result.msg);
                    }
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                listener.onLoadCartoonMessageFailure(throwable.getLocalizedMessage());
            }
        });
    }

    /***************************************************************************
     * 个人中心动漫动态
     ***************************************************************************/

    public interface OnLoadCartoonDynamicResponseListener{
        void onLoadCartoonDynamicSuccess(ArrayList<CartoonMessage> dynamics,Page page);
        void onLoadCartoonDynamicFailure(String msg);
    }

    public void loadCartoonDyanmic(final Context context,int userId,int nextpage, final OnLoadCartoonDynamicResponseListener listener){
        String url = context.getString(R.string.interface_domainName) + context.getString(R.string.interface_cartooncynamic);
        RequestParams params = new RequestParams();
        params.put("userId",userId);
        params.put("page",nextpage);
        httpClient.post(url,params,new JsonHttpResponseHandler(){
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                ParseResponseResult result = new ParseResponseResult(context,response);
                if (result.isSuccess){
                    ArrayList<CartoonMessage> dynamics = gson.fromJson(result.msg, new TypeToken<ArrayList<CartoonMessage>>() {}.getType());
                    Page page = gson.fromJson(result.pageBean,Page.class);
                    listener.onLoadCartoonDynamicSuccess(dynamics,page);
                } else {
                    listener.onLoadCartoonDynamicFailure(result.msg);
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                listener.onLoadCartoonDynamicFailure(throwable.getLocalizedMessage());
            }
        });
    }

    /***************************************************************************
     * 个人中心动漫作品
     ***************************************************************************/
    public interface OnLoadCartoonWorkResponseListener{
        void onLoadCartoonWorkSuccess(ArrayList<Cartoon> work,Page page);
        void onLoadCartoonWorkFailure(String msg);
    }

    public void loadCartoonWork(final Context context,int userId,int nextPage, final OnLoadCartoonWorkResponseListener listener){
        String url = context.getString(R.string.interface_domainName) + context.getString(R.string.interface_cartoonwork);
        RequestParams params = new RequestParams();
        params.put("userId",userId);
        params.put("page",nextPage);
        httpClient.post(url,params,new JsonHttpResponseHandler(){
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                ParseResponseResult result = new ParseResponseResult(context,response);
                if (result.isSuccess){
                    ArrayList<Cartoon> cartoons = gson.fromJson(result.msg, new TypeToken<ArrayList<Cartoon>>() {}.getType());
                    Page page = gson.fromJson(result.pageBean,Page.class);
                    listener.onLoadCartoonWorkSuccess(cartoons,page);
                } else {
                    listener.onLoadCartoonWorkFailure(result.msg);
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                listener.onLoadCartoonWorkFailure(throwable.getLocalizedMessage());
            }
        });
    }

    /***************************************************************************
     * 个人中心社区消息
     ***************************************************************************/
    public interface OnLoadCommunityMessageResponseListener{
        void onLoadCommunityMessageSuccess(ArrayList<CommunityMessage> messages,Page page);
        void onLoadCommunityMessageFailure(String msg);
    }

    public void loadCommunityMessage(Context context,int userId,int nextPage,OnLoadCommunityMessageResponseListener listener){

    }

    /***************************************************************************
     * 个人中心社区动态
     ***************************************************************************/
    public interface OnLoadCommunityDynamicResponseListener{
        void onLoadCommunityDynamicSuccess(ArrayList<CommunityDynamic> dynamics,Page page);
        void onLoadCommunityDynamicFailure(String msg);
    }

    public void loadCommunityDynamic(Context context,int userId,int nextPage,OnLoadCommunityDynamicResponseListener listener){

    }


    /***************************************************************************
     * 个人中心社区作品
     ***************************************************************************/
    public interface OnloadCommunityWorkResponseListener{
        void onLoadCommunityWorkSuccess(ArrayList<Post> works,Page page);
        void onLoadCommunityWorkFailure(String msg);
    }

    public void loadCommunityWork(Context context,int userId,int nextPage,OnloadCommunityWorkResponseListener listener){

    }

    /***************************************************************************
     * 个人中心好友
     ***************************************************************************/
    public interface OnloadFriendResponseListener{
        void onLoadFriendSuccess(ArrayList<User> friends,Page page);
        void OnloadFriendFailure(String msg);
    }

    public void loadFriendList(Context context,int nextPage,OnloadFriendResponseListener listener){

    }


    private static InputStream Bitmap2IS(Bitmap bm)
    {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bm.compress(Bitmap.CompressFormat.JPEG, 80, baos);
        InputStream sbs = new ByteArrayInputStream(baos.toByteArray());
        return sbs;
    }
}
