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
import com.mckuai.imc.Bean.CartoonMessageList;
import com.mckuai.imc.Bean.CartoonWorkList;
import com.mckuai.imc.Bean.CommunityDynamic;
import com.mckuai.imc.Bean.CommunityDynamicBean;
import com.mckuai.imc.Bean.CommunityMessage;
import com.mckuai.imc.Bean.CommunityMessageBean;
import com.mckuai.imc.Bean.CommunityMessageList;
import com.mckuai.imc.Bean.CommunityWorkBean;
import com.mckuai.imc.Bean.ForumInfo;
import com.mckuai.imc.Bean.FriendBean;
import com.mckuai.imc.Bean.MCUser;
import com.mckuai.imc.Bean.Page;
import com.mckuai.imc.Bean.Post;
import com.mckuai.imc.Bean.PostListBean;
import com.mckuai.imc.Bean.User;
import com.mckuai.imc.R;

import org.apache.http.Header;
import org.json.JSONObject;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.lang.reflect.Type;
import java.util.ArrayList;

/**
 * Created by kyly on 2016/1/22.
 */
public class MCNetEngine {
    private AsyncHttpClient httpClient;
    private Gson gson;
    private String domainName = "http://api.mckuai.com/";

    public MCNetEngine() {
        httpClient = new AsyncHttpClient();
        httpClient.setTimeout(10);
        gson = new Gson();
    }

    public void cancle() {
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
        String url = domainName + context.getString(R.string.interface_login);
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
                if (ParseResponseResult.isSuccess) {
                    MCUser userinfo = gson.fromJson(ParseResponseResult.msg, MCUser.class);
                    if (null != userinfo && userinfo.getName().equals(user.getName())) {
                        listener.onLoginSuccess(userinfo);
                    } else {
                        listener.onLoginFailure(context.getString(R.string.error_parsefalse));
                    }
                } else {
                    listener.onLoginFailure(ParseResponseResult.msg);
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

    public interface OnForumListResponseListener {
        void onLoadForumListSuccess(ArrayList<ForumInfo> forums);

        void onLoadForumListFailure(String msg);
    }

    public void loadFroumList(final Context context, final OnForumListResponseListener listener) {
        String url = domainName + context.getString(R.string.interface_forumlist);
        httpClient.get(url,null,new JsonHttpResponseHandler(){
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                ParseResponseResult result = new ParseResponseResult(context,response);
                if (ParseResponseResult.isSuccess) {
                    ArrayList<ForumInfo> forums = gson.fromJson(ParseResponseResult.msg, new TypeToken<ArrayList<ForumInfo>>() {
                    }.getType());
                    listener.onLoadForumListSuccess(forums);
                } else {
                    listener.onLoadForumListFailure(ParseResponseResult.msg);
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                listener.onLoadForumListFailure(throwable.getLocalizedMessage());
            }
        });
    }


    /***************************************************************************
     * 获取帖子列表
     ***************************************************************************/

    public interface OnPostListResponseListener {
        void onLoadPostListSuccess(ArrayList<Post> posts, Page page);

        void onLoadPostListFailure(String msg);
    }

    public void loadPostList(final Context context, int forumId,String postType, int nextPage, final OnPostListResponseListener listener) {
        String url = domainName + context.getString(R.string.interface_postlist);
        RequestParams params = new RequestParams();
        params.put("forumId",forumId);
        params.put("page",nextPage);
        if (null != postType) {
            params.put("type", postType);
        }
        httpClient.get(url,params,new JsonHttpResponseHandler(){
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                ParseResponseResult result = new ParseResponseResult(context,response);
                if (ParseResponseResult.isSuccess) {
                    PostListBean bean = gson.fromJson(ParseResponseResult.msg, PostListBean.class);
                    Page page = new Page(bean.getAllCount(),bean.getPage(),bean.getPageSize());
                    listener.onLoadPostListSuccess(bean.getdata(), page);
                } else {
                    listener.onLoadPostListFailure(ParseResponseResult.msg);
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                listener.onLoadPostListFailure(throwable.getLocalizedMessage());
            }
        });
    }


    /***************************************************************************
     * 获取人物列表
     ***************************************************************************/

    public interface OnCharacterListResponseListener {
        void onLoadCharacterListSuccess(ArrayList<String> characters);

        void onLoadCharacterListFailure(String msg);
    }

    public void loadCharacterList(final Context context, Page page, final OnCharacterListResponseListener listener) {

    }


    /***************************************************************************
     * 对获取工具列表
     ***************************************************************************/

    public interface OnToolListResponseListener {
        void onLoadToolListSuccess(ArrayList<String> tools);

        void onLoadToolListFailure(String msg);
    }

    public void loadToolList(final Context context, Page page, final OnToolListResponseListener listener) {

    }

    /***************************************************************************
     * 上传图片
     ***************************************************************************/

    public interface OnUploadImageResponseListener {
        void onImageUploadSuccess(String url);

        void onImageUploadFailure(String msg);
    }

    public void uploadImage(final Context context, ArrayList<Bitmap> bitmaps, final OnUploadImageResponseListener listener) {
//        String url = "http://www.mckuai.com/" + context.getString(R.string.interface_uploadimage);
        String url = domainName + context.getString(R.string.interface_uploadimage_cartoon);
        RequestParams params = new RequestParams();
        if (null != bitmaps && !bitmaps.isEmpty()) {
            String fileName = null;
            for (int i = 0; i < bitmaps.size(); i++) {
                fileName = i + ".jpg";
                params.put("upload", Bitmap2IS(bitmaps.get(i)), fileName, "image/jpeg");
            }
        }
        httpClient.post(url, params, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                ParseResponseResult result = new ParseResponseResult(context, response);
                if (ParseResponseResult.isSuccess) {
                    listener.onImageUploadSuccess(ParseResponseResult.msg);
                } else {
                    listener.onImageUploadFailure(ParseResponseResult.msg);
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

    public interface OnUploadCartoonResponseListener {
        void onUploadCartoonSuccess();

        void onUploadCartoonFailure(String msg);
    }

    public void uploadCartoon(Context context, Cartoon cartoon, final OnUploadCartoonResponseListener listener) {
        String url = domainName + context.getString(R.string.interface_uploadcartoon);
        RequestParams params = new RequestParams();
        params.put("userId", cartoon.getOwner().getId());
        params.put("title", cartoon.getContent());
        params.put("imageUrl", cartoon.getImage());
        httpClient.post(context, url, params, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                if (null != response && response.toString().length() > 10) {
                    if (response.has("state")) {
                        try {
                            if ("ok".equals(response.getString("state"))) {
                                listener.onUploadCartoonSuccess();
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
                listener.onUploadCartoonFailure("上传失败");
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                listener.onUploadCartoonFailure(throwable.getLocalizedMessage());
            }
        });
    }

    /***************************************************************************
     * 打赏动漫
     ***************************************************************************/

    public interface OnRewardCartoonResponseListener {
        void onRewardCartoonSuccess();

        void onRewardaCartoonFailure(String msg);
    }

    /**
     * 打赏
     *
     * @param context
     * @param isCartoon 如果是打赏动漫，则设置为true,否则设置为false
     * @param targetId  要打赏的目标的id
     * @param listener  打赏响应监听
     */
    public void rewardCartoon(final Context context, boolean isCartoon, int targetId, final OnRewardCartoonResponseListener listener) {
        String url = domainName + context.getString(R.string.interface_reward);
        RequestParams params = new RequestParams();
        if (isCartoon) {
            params.put("type", "cartoon");
        }
        params.put("userId", MCKuai.instence.user.getId());
        params.put("talkId", targetId);
        httpClient.post(url, params, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                ParseResponseResult result = new ParseResponseResult(context, response);
                if (ParseResponseResult.isSuccess) {
                    listener.onRewardCartoonSuccess();
                } else {
                    listener.onRewardaCartoonFailure(ParseResponseResult.msg);
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

    public interface OnCommentCartoonResponseListener {
        void onCommentCartoonSuccess();

        void onCommentCartoonFailure(String msg);
    }

    public void commentCartoon(final Context context, int cartoonId, String content, final OnCommentCartoonResponseListener listener) {
        String url = domainName + context.getString(R.string.interface_commentcartoon);
        RequestParams params = new RequestParams();
        params.put("id", cartoonId);
        params.put("userId", MCKuai.instence.user.getId());
        params.put("content", content);
        httpClient.post(url, params, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                ParseResponseResult result = new ParseResponseResult(context, response);
                if (ParseResponseResult.isSuccess) {
                    listener.onCommentCartoonSuccess();
                } else {
                    listener.onCommentCartoonFailure(ParseResponseResult.msg);
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

    public interface OnLoadPushlCartoonListListener {
        void onLoadPushCartoonSuccess(ArrayList<Cartoon> cartoons);

        void onLoadPushCartoonFailure(String msg);
    }

    public void loadPushCartoonList(final Context context, Integer lastCartoonId, final OnLoadPushlCartoonListListener listListener) {
        String url = domainName + context.getString(R.string.interface_pushcartoon);
        RequestParams params = new RequestParams();
        if (null != lastCartoonId) {
            params.put("ids", lastCartoonId + "");
        }
        httpClient.post(url, params, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                ParseResponseResult result = new ParseResponseResult(context, response);
                if (ParseResponseResult.isSuccess) {
                    ArrayList<Cartoon> cartoons = gson.fromJson(ParseResponseResult.msg, new TypeToken<ArrayList<Cartoon>>() {
                    }.getType());
                    listListener.onLoadPushCartoonSuccess(cartoons);
                } else {
                    listListener.onLoadPushCartoonFailure(ParseResponseResult.msg);
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

    public interface OnLoadCartoonListResponseListener {
        void onLoadCartoonListSuccess(ArrayList<Cartoon> cartoons);

        void onLoadCartoonListFailure(String msg);
    }

    public void loadCartoonList(final Context context, String cartoonType, Integer lastCartoonId, final OnLoadCartoonListResponseListener listener) {
        String url = domainName + context.getString(R.string.interface_loadcartoonlist);
        RequestParams params = new RequestParams();
        if (lastCartoonId != null) {
            params.put("id", lastCartoonId);
        }
        params.put("type", cartoonType);
        httpClient.get(context, url, params, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                ParseResponseResult result = new ParseResponseResult(context, response);
                if (ParseResponseResult.isSuccess) {
                    try {
                        Type type = new TypeToken<ArrayList<Cartoon>>() {
                        }.getType();
                        ArrayList<Cartoon> cartoons = gson.fromJson(ParseResponseResult.msg, type);
                        listener.onLoadCartoonListSuccess(cartoons);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }


                } else {
                    listener.onLoadCartoonListFailure(ParseResponseResult.msg);
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

    public interface OnLoadCartoonMessageResponseListener {
        void onLoadCartoonMessageSuccess(MCUser user, ArrayList<CartoonMessage> cartoons, Page page);

        void onLoadCartoonMessageFailure(String msg);
    }

    public void loadCartoonMessage(final Context context, int userId, int nextpage, final OnLoadCartoonMessageResponseListener listener) {
        String url = domainName + context.getString(R.string.interface_cartoonmessage);
        RequestParams params = new RequestParams();
        params.put("userId", userId);
        params.put("page", nextpage);
        httpClient.post(url, params, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                ParseResponseResult result = new ParseResponseResult(context, response);
                if (ParseResponseResult.isSuccess) {
                    /*ArrayList<CartoonMessage> messages = gson.fromJson(result.msg, new TypeToken<ArrayList<CartoonMessage>>() {
                    }.getType());*/
                    CartoonMessageList bean = gson.fromJson(ParseResponseResult.msg, CartoonMessageList.class);
                    Page page = gson.fromJson(result.pageBean, Page.class);
                    listener.onLoadCartoonMessageSuccess(bean.getUser(), bean.getList(), page);
                } else {
                    if (null != listener) {
                        listener.onLoadCartoonMessageFailure(ParseResponseResult.msg);
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

    public interface OnLoadCartoonDynamicResponseListener {
        void onLoadCartoonDynamicSuccess(MCUser user, ArrayList<CartoonMessage> dynamics, Page page);

        void onLoadCartoonDynamicFailure(String msg);
    }

    public void loadCartoonDyanmic(final Context context, int userId, int nextpage, final OnLoadCartoonDynamicResponseListener listener) {
        String url = domainName + context.getString(R.string.interface_cartooncynamic);
        RequestParams params = new RequestParams();
        params.put("userId", userId);
        params.put("page", nextpage);
        httpClient.post(url, params, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                ParseResponseResult result = new ParseResponseResult(context, response);
                if (ParseResponseResult.isSuccess) {
                    /*ArrayList<CartoonMessage> dynamics = gson.fromJson(result.msg, new TypeToken<ArrayList<CartoonMessage>>() {
                    }.getType());*/
                    //listener.onLoadCartoonDynamicSuccess(dynamics, page);
                    CartoonMessageList bean = gson.fromJson(ParseResponseResult.msg, CartoonMessageList.class);
                    Page page = gson.fromJson(result.pageBean, Page.class);
                    listener.onLoadCartoonDynamicSuccess(bean.getUser(), bean.getList(), page);
                } else {
                    listener.onLoadCartoonDynamicFailure(ParseResponseResult.msg);
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
    public interface OnLoadCartoonWorkResponseListener {
        void onLoadCartoonWorkSuccess(MCUser user, ArrayList<Cartoon> work, Page page);

        void onLoadCartoonWorkFailure(String msg);
    }

    public void loadCartoonWork(final Context context, int userId, int nextPage, final OnLoadCartoonWorkResponseListener listener) {
        String url = domainName + context.getString(R.string.interface_cartoonwork);
        RequestParams params = new RequestParams();
        params.put("userId", userId);
        params.put("page", nextPage);
        httpClient.post(url, params, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                ParseResponseResult result = new ParseResponseResult(context, response);
                if (ParseResponseResult.isSuccess) {
                    /*ArrayList<Cartoon> cartoons = gson.fromJson(result.msg, new TypeToken<ArrayList<Cartoon>>() {
                    }.getType());*/
                    CartoonWorkList bean = gson.fromJson(ParseResponseResult.msg, CartoonWorkList.class);
                    Page page = gson.fromJson(result.pageBean, Page.class);
                    listener.onLoadCartoonWorkSuccess(bean.getUser(), bean.getList(), page);
                } else {
                    listener.onLoadCartoonWorkFailure(ParseResponseResult.msg);
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
    public interface OnLoadCommunityMessageResponseListener {
        void onLoadCommunityMessageSuccess(ArrayList<CommunityMessage> messages,User user, Page page);

        void onLoadCommunityMessageFailure(String msg);
    }

    public void loadCommunityMessage(final Context context, int userId, int nextPage, final OnLoadCommunityMessageResponseListener listener) {
        String url = domainName + context.getString(R.string.interface_usercenter);
        RequestParams params = new RequestParams();
        params.put("type", "message");
        params.put("id", userId);
        params.put("messageType", "all");
        params.put("page", nextPage);
        httpClient.post(url, params, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                ParseResponseResult result = new ParseResponseResult(context, response);
                if (ParseResponseResult.isSuccess) {
                    CommunityMessageBean bean = gson.fromJson(ParseResponseResult.msg, CommunityMessageBean.class);
                    if (null != bean && null != bean.getList() && null != bean.getUser()) {
                        CommunityMessageList list = bean.getList();
                        Page page = new Page(list.getAllCount(), list.getPage(), list.getPageSize());
                        listener.onLoadCommunityMessageSuccess(list.getData(),bean.getUser(), page);
                    } else {
                        listener.onLoadCommunityMessageFailure("转换数据失败！");
                    }
                } else {
                    listener.onLoadCommunityMessageFailure(ParseResponseResult.msg);
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                listener.onLoadCommunityMessageFailure(throwable.getLocalizedMessage());
            }
        });
    }

    /***************************************************************************
     * 个人中心社区动态
     ***************************************************************************/
    public interface OnLoadCommunityDynamicResponseListener {
        void onLoadCommunityDynamicSuccess(ArrayList<CommunityDynamic> dynamics,User user ,Page page);

        void onLoadCommunityDynamicFailure(String msg);
    }

    public void loadCommunityDynamic(final Context context, int userId, int nextPage, final OnLoadCommunityDynamicResponseListener listener) {
        String url = domainName + context.getString(R.string.interface_usercenter);
        RequestParams params = new RequestParams();
        params.put("type", "dynamic");
        params.put("id", userId);
        params.put("page", nextPage);
        httpClient.post(url,params,new JsonHttpResponseHandler(){
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                ParseResponseResult result = new ParseResponseResult(context,response);
                if (ParseResponseResult.isSuccess) {
                    CommunityDynamicBean bean = gson.fromJson(ParseResponseResult.msg, CommunityDynamicBean.class);
                    if (null != bean && null != bean.getList() && null != bean.getUser()){
                        Page page = new Page(bean.getList().getAllCount(),bean.getList().getPage(),bean.getList().getPageSize());
                        listener.onLoadCommunityDynamicSuccess(bean.getList().getData(), bean.getUser(), page);
                    } else {
                        listener.onLoadCommunityDynamicFailure("转换数据失败！");
                    }
                } else {
                    listener.onLoadCommunityDynamicFailure(ParseResponseResult.msg);
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                listener.onLoadCommunityDynamicFailure(throwable.getLocalizedMessage());
            }
        });
    }


    /***************************************************************************
     * 个人中心社区作品
     ***************************************************************************/
    public interface OnloadCommunityWorkResponseListener {
        void onLoadCommunityWorkSuccess(ArrayList<Post> works,User user, Page page);

        void onLoadCommunityWorkFailure(String msg);
    }

    public void loadCommunityWork(final Context context, int userId, int nextPage, final OnloadCommunityWorkResponseListener listener) {
        String url = domainName + context.getString(R.string.interface_usercenter);
        RequestParams params = new RequestParams();
        params.put("type", "work");
        params.put("id", userId);
        params.put("page", nextPage);
        httpClient.post(url,params,new JsonHttpResponseHandler(){
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                ParseResponseResult result = new ParseResponseResult(context,response);
                if (ParseResponseResult.isSuccess) {
                    CommunityWorkBean bean = gson.fromJson(ParseResponseResult.msg, CommunityWorkBean.class);
                    if (null != bean && null != bean.getList() && null != bean.getUser()){
                        Page page = new Page(bean.getList().getAllCount(),bean.getList().getPage(),bean.getList().getPageSize());
                        listener.onLoadCommunityWorkSuccess(bean.getList().getdata(), bean.getUser(), page);
                    } else {
                        listener.onLoadCommunityWorkFailure("转换数据失败！");
                    }
                } else {
                    listener.onLoadCommunityWorkFailure(ParseResponseResult.msg);
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                listener.onLoadCommunityWorkFailure(throwable.getLocalizedMessage());
            }
        });
    }

    /***************************************************************************
     * 添加好友
     ***************************************************************************/

    public interface OnAddFriendResponseListener {
        void onAddFriendSuccess();

        void onAddFriendFailure(String msg);
    }

    public void addFriend(final Context context, int userId, final OnAddFriendResponseListener listener) {
        String url = domainName + "interface.do?act=attentionUser";
        RequestParams params = new RequestParams();
        params.put("ownerId", MCKuai.instence.user.getId());
        params.put("id", userId);
        httpClient.get(url, params, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                ParseResponseResult result = new ParseResponseResult(context, response);
                if (ParseResponseResult.isSuccess) {
                    listener.onAddFriendSuccess();
                } else {
                    listener.onAddFriendFailure(ParseResponseResult.msg);
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                listener.onAddFriendFailure(throwable.getLocalizedMessage());
            }
        });
    }


    /***************************************************************************
     * 个人中心好友
     ***************************************************************************/
    public interface OnloadFriendResponseListener {
        void onLoadFriendSuccess(ArrayList<MCUser> friends, Page page);
        void OnloadFriendFailure(String msg);
    }

    public void loadFriendList(final Context context, int nextPage, final OnloadFriendResponseListener listener) {
        String url = domainName + context.getString(R.string.interface_fellowuserlist);
        RequestParams params = new RequestParams();
        params.put("id",MCKuai.instence.user.getId());
        params.put("page",nextPage);
        httpClient.post(url,params,new JsonHttpResponseHandler(){
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                ParseResponseResult result = new ParseResponseResult(context,response);
                if (ParseResponseResult.isSuccess) {
                    FriendBean bean = gson.fromJson(ParseResponseResult.msg, FriendBean.class);
                    if (null != bean){
                        Page page = new Page(bean.getAllCount(),bean.getPage(),bean.getPageSize());
                        listener.onLoadFriendSuccess(bean.getData(),page);
                    } else {
                        listener.OnloadFriendFailure("转换数据失败！");
                    }
                } else {
                    listener.OnloadFriendFailure(ParseResponseResult.msg);
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                listener.OnloadFriendFailure(throwable.getLocalizedMessage());
            }
        });
    }

    /***************************************************************************
     * 上传头像图片
     ***************************************************************************/

    public interface OnUploadUserCoverResponseListener {
        void onUploadCoverSuccess(String url);

        void onUploadCoverFailure(String msg);
    }

    public void uploadUserCover(final Context context, Bitmap cover, final OnUploadUserCoverResponseListener listener) {
        String url = "http://www.mckuai.com/" + context.getString(R.string.interface_uploadimage);
        RequestParams params = new RequestParams();
        params.put("fileHeadImg", Bitmap2IS(cover), "cover.jpg", "image/jpeg");
        httpClient.post(url, params, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                ParseResponseResult result = new ParseResponseResult(context, response);
                if (ParseResponseResult.isSuccess) {
                    listener.onUploadCoverSuccess(ParseResponseResult.msg);
                } else {
                    listener.onUploadCoverFailure(ParseResponseResult.msg);
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                listener.onUploadCoverFailure(throwable.getLocalizedMessage());
            }
        });
    }

    /***************************************************************************
     * 更新头像url
     ***************************************************************************/

    public interface OnUpdateUserCoverResponseListener {
        void onUpdateUserCoverSuccess();

        void onUpdateUserCoverFailure(String msg);
    }

    public void updateUserCover(final Context context, String coverUrl, final OnUpdateUserCoverResponseListener listener) {
        String url = domainName + context.getString(R.string.interface_update_userinfo);
        RequestParams params = new RequestParams();
        params.put("userId", MCKuai.instence.user.getId());
        params.put("flag", "headImg");
        params.put("headImg", coverUrl);
        httpClient.post(url, params, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                ParseResponseResult result = new ParseResponseResult(context, response);
                if (ParseResponseResult.isSuccess) {
                    listener.onUpdateUserCoverSuccess();
                } else {
                    listener.onUpdateUserCoverFailure(ParseResponseResult.msg);
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                listener.onUpdateUserCoverFailure(throwable.getLocalizedMessage());
            }
        });
    }

    /***************************************************************************
     * 更新昵称
     ***************************************************************************/

    public interface OnUpdateUserNickResponseListener {
        void onUpdateUserNickSuccess();

        void onUpdateUserNickFailure(String msg);
    }

    public void updateNickName(final Context context, String nick, final OnUpdateUserNickResponseListener listener) {
        String url = domainName + context.getString(R.string.interface_update_userinfo);
        RequestParams params = new RequestParams();
        params.put("userId", MCKuai.instence.user.getId());
        params.put("flag", "name");
        params.put("nickName", nick);
        httpClient.post(url, params, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                ParseResponseResult result = new ParseResponseResult(context, response);
                if (ParseResponseResult.isSuccess) {
                    listener.onUpdateUserNickSuccess();
                } else {
                    listener.onUpdateUserNickFailure(ParseResponseResult.msg);
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                listener.onUpdateUserNickFailure(throwable.getLocalizedMessage());
            }
        });
    }

    /***************************************************************************
     * 更新地址
     ***************************************************************************/

    public interface OnUpdateUserAddressResponseListener {
        void onUpdateAddressSuccess();

        void onUpdateAddressFailure(String msg);
    }

    public void updateUserAddress(final Context context, String address, final OnUpdateUserAddressResponseListener listener) {
        String url = domainName + context.getString(R.string.interface_updateLocation);
        RequestParams params = new RequestParams();
        params.put("id", MCKuai.instence.user.getId());
        params.put("addr", address);
        httpClient.post(url, params, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                ParseResponseResult result = new ParseResponseResult(context, response);
                if (ParseResponseResult.isSuccess) {
                    listener.onUpdateAddressSuccess();
                } else {
                    listener.onUpdateAddressFailure(ParseResponseResult.msg);
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                listener.onUpdateAddressFailure(throwable.getLocalizedMessage());
            }
        });
    }

    /***************************************************************************
     * 推送消息
     ***************************************************************************/

    public interface OnLoadMessageResponseListener {
        void onLoadMessageSuccess(ArrayList<Cartoon> messages);

        void onLoadMessageFailure(String msg);
    }

    public void loadMessage(final Context context, Integer lastId, final OnLoadMessageResponseListener listener) {
        String url = domainName + context.getString(R.string.itnerface_loadmessage);
        RequestParams params = new RequestParams();
        if (null != lastId) {
            params.put("ids", lastId);
        }
        httpClient.get(url, params, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                ParseResponseResult result = new ParseResponseResult(context, response);
                if (ParseResponseResult.isSuccess) {
                    ArrayList<Cartoon> cartoons = gson.fromJson(ParseResponseResult.msg, new TypeToken<ArrayList<Cartoon>>() {
                    }.getType());
                    listener.onLoadMessageSuccess(cartoons);
                } else {
                    listener.onLoadMessageFailure(ParseResponseResult.msg);
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                listener.onLoadMessageFailure(throwable.getLocalizedMessage());
            }
        });
    }

    /***************************************************************************
     * 取漫画详细信息
     ***************************************************************************/
    public interface OnLoadCartoonDetailResponseListener {
        void onLoadDetailSuccess(Cartoon cartoon);

        void onLoadDetailFailure(String msg);
    }

    public void loadCartoonDetail(final Context context, int cartoonId, final OnLoadCartoonDetailResponseListener listener) {
        String url = domainName + context.getString(R.string.interface_cartoondetial);
        RequestParams params = new RequestParams();
        params.put("id", cartoonId);
        httpClient.get(url, params, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                ParseResponseResult result = new ParseResponseResult(context, response);
                if (ParseResponseResult.isSuccess) {
                    Cartoon cartoon = gson.fromJson(ParseResponseResult.msg, Cartoon.class);
                    if (null != cartoon && null != listener) {
                        listener.onLoadDetailSuccess(cartoon);
                    } else {
                        listener.onLoadDetailFailure("转换数据失败！");
                    }
                } else {
                    listener.onLoadDetailFailure(ParseResponseResult.msg);
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                if (null != listener) {
                    listener.onLoadDetailFailure(throwable.getLocalizedMessage());
                }
            }
        });
    }


    /***************************************************************************
     * 取漫画详细信息
     ***************************************************************************/


    private static InputStream Bitmap2IS(Bitmap bm) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bm.compress(Bitmap.CompressFormat.JPEG, 80, baos);
        InputStream sbs = new ByteArrayInputStream(baos.toByteArray());
        return sbs;
    }
}
