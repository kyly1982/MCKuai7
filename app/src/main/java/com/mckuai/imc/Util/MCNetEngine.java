package com.mckuai.imc.Util;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.support.annotation.NonNull;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.mckuai.imc.Base.MCKuai;
import com.mckuai.imc.Bean.Ad;
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
import com.mckuai.imc.Bean.Theme;
import com.mckuai.imc.Bean.User;
import com.mckuai.imc.R;
import com.umeng.socialize.utils.Log;

import org.apache.http.Header;
import org.json.JSONObject;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Type;
import java.util.ArrayList;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by kyly on 2016/1/22.
 */
public class MCNetEngine {
    private AsyncHttpClient httpClient;
    private OkHttpClient client;
    private Gson gson;
    private JsonCache cache;
    private final String domainName = "http://api.mckuai.com/";
//    private final String domainName = "http://221.237.152.39:8081/";
//    private final String domainName = "http://192.168.10.66/";
    private MCDaoHelper daoHelper;
    private MCKuai application;

    public MCNetEngine() {
        client = new OkHttpClient();
        httpClient = new AsyncHttpClient();
        httpClient.setTimeout(10);
        cache = new JsonCache();
        gson = new Gson();
        daoHelper = MCKuai.instence.daoHelper;
        application = MCKuai.instence;
    }

    public void exit() {
        cancle();
        if (null != cache) {
            cache.saveCacheFile();
        }
    }

    public void cancle() {
        if (null != httpClient) {
            httpClient.cancelAllRequests(true);
        }
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
    /*    Request.Builder builder = new Request.Builder();
        builder.url(url);
        client.newCall(builder.build()).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                listener.onLoginFailure(e.getLocalizedMessage());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {

            }
        });*/
        httpClient.get(url, params, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                ParseResponse result = new ParseResponse(context, response);
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

    public interface OnForumListResponseListener {
        void onLoadForumListSuccess(ArrayList<ForumInfo> forums);

        void onLoadForumListFailure(String msg);
    }

    public void loadFroumList(final Context context, final OnForumListResponseListener listener) {
        final String url = domainName + context.getString(R.string.interface_forumlist);
        httpClient.get(url, null, new JsonHttpResponseHandler() {
            @Override
            public void onStart() {
                super.onStart();
                String result = cache.get(url);
                if (null != result && result.length() > 10) {
                    ArrayList<ForumInfo> forums = gson.fromJson(result, new TypeToken<ArrayList<ForumInfo>>() {
                    }.getType());
                    listener.onLoadForumListSuccess(forums);
                }
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                ParseResponse result = new ParseResponse(context, response);
                if (result.isSuccess) {
                    ArrayList<ForumInfo> forums = gson.fromJson(result.msg, new TypeToken<ArrayList<ForumInfo>>() {
                    }.getType());
                    listener.onLoadForumListSuccess(forums);
                    cache.put(url, result.msg);
                } else {
                    listener.onLoadForumListFailure(result.msg);
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

    public void loadPostList(final Context context, int forumId, String postType, int nextPage, final OnPostListResponseListener listener) {
        final String url = domainName + context.getString(R.string.interface_postlist);
        final RequestParams params = new RequestParams();
        params.put("forumId", forumId);
        params.put("page", nextPage);
        if (null != postType) {
            params.put("type", postType);
        }
        httpClient.get(url, params, new JsonHttpResponseHandler() {
            @Override
            public void onStart() {
                String result = cache.get(url, params);
                if (null != result && result.length() > 10) {
                    PostListBean bean = gson.fromJson(result, PostListBean.class);
                    Page page = new Page(bean.getAllCount(), bean.getPage(), bean.getPageSize());
                    listener.onLoadPostListSuccess(bean.getdata(), page);
                }
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                ParseResponse result = new ParseResponse(context, response);
                if (result.isSuccess) {
                    PostListBean bean = gson.fromJson(result.msg, PostListBean.class);
                    Page page = new Page(bean.getAllCount(), bean.getPage(), bean.getPageSize());
                    listener.onLoadPostListSuccess(bean.getdata(), page);
                    if (1 == page.getPage()) {
                        cache.put(url, params, result.msg);
                    }
                } else {
                    listener.onLoadPostListFailure(result.msg);
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
                ParseResponse result = new ParseResponse(context, response);
                if (result.isSuccess) {
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

    public interface OnUploadCartoonResponseListener {
        void onUploadCartoonSuccess(int cartoonId);

        void onUploadCartoonFailure(String msg);
    }

    public void uploadCartoon(final Context context, Cartoon cartoon, final OnUploadCartoonResponseListener listener) {
        String url = domainName + context.getString(R.string.interface_uploadcartoon_withtheme);
        RequestParams params = new RequestParams();
        params.put("userId", cartoon.getOwner().getId());
        //params.put("title", cartoon.getContent());
        params.put("imageUrl", cartoon.getImage());
        //添加主题
        params.put("kinds",cartoon.getKindsEx());
        httpClient.post(context, url, params, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                ParseResponse result = new ParseResponse(context, response);
                if (result.isSuccess) {
                    int id = Integer.valueOf(result.msg);
                    if (0 != id) {
                        listener.onUploadCartoonSuccess(id);
                    } else {
                        listener.onUploadCartoonFailure("返回数据不正确！");
                    }
                } else {
                    listener.onUploadCartoonFailure(result.msg);
                }
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
     * @param cartoon 胜利的漫画
     * @param listener  打赏响应监听
     */
    public void rewardCartoon(final Context context,int userId ,Cartoon cartoon, final OnRewardCartoonResponseListener listener) {
        String url = domainName + context.getString(R.string.interface_reward_new);
        RequestParams params = new RequestParams();
            params.put("type", "cartoon");
        params.put("userId", userId);
            params.put("groupId",cartoon.getGroupId());
        params.put("winId", cartoon.getId());
        httpClient.post(url, params, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                ParseResponse result = new ParseResponse(context, response);
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

    public interface OnCommentCartoonResponseListener {
        void onCommentCartoonSuccess();

        void onCommentCartoonFailure(String msg);
    }

    public void commentCartoon(final Context context,int userId, int cartoonId, String content, final OnCommentCartoonResponseListener listener) {
        String url = domainName + context.getString(R.string.interface_commentcartoon);
        RequestParams params = new RequestParams();
        params.put("id", cartoonId);
        params.put("userId", userId);
        params.put("content", content);
        httpClient.post(url, params, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                ParseResponse result = new ParseResponse(context, response);
                if (result.isSuccess) {
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
                ParseResponse result = new ParseResponse(context, response);
                if (result.isSuccess) {
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

    public interface OnLoadCartoonListResponseListener {
        void onLoadCartoonListSuccess(ArrayList<Cartoon> cartoons);

        void onLoadCartoonListFailure(String msg);
    }

    public void loadCartoonList(final Context context, Page page, final OnLoadCartoonListResponseListener listener) {
        String url = domainName + context.getString(R.string.interface_loadcartoonlist_new);
        RequestParams params = new RequestParams();
        params.put("page", page.getPage()+1);
        httpClient.get(context, url, params, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                ParseResponse result = new ParseResponse(context, response);
                if (result.isSuccess) {
                    try {
                        Type type = new TypeToken<ArrayList<Cartoon>>() {
                        }.getType();
                        ArrayList<Cartoon> cartoons = gson.fromJson(result.msg, type);
                        listener.onLoadCartoonListSuccess(cartoons);
                        return;
                    } catch (Exception e) {
                        e.printStackTrace();
                        listener.onLoadCartoonListFailure(e.getLocalizedMessage());
                    }
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

    public interface OnLoadCartoonMessageResponseListener {
        void onLoadCartoonMessageSuccess(MCUser user, ArrayList<CartoonMessage> cartoons, Page page);

        void onLoadCartoonMessageFailure(String msg);
    }

    public void loadCartoonMessage(final Context context, int userId, int nextpage, final OnLoadCartoonMessageResponseListener listener) {
        final String url = domainName + context.getString(R.string.interface_cartoonmessage);
        final RequestParams params = new RequestParams();
        params.put("userId", userId);
        params.put("page", nextpage);
        httpClient.post(url, params, new JsonHttpResponseHandler() {
            @Override
            public void onStart() {
                String result = cache.get(url, params);
                if (null != result && result.length() > 10) {
                    CartoonMessageList bean = gson.fromJson(result, CartoonMessageList.class);
                    listener.onLoadCartoonMessageSuccess(bean.getUser(), bean.getList(), new Page(20, 1, 20));
                }
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                ParseResponse result = new ParseResponse(context, response);
                if (result.isSuccess) {
                    CartoonMessageList bean = gson.fromJson(result.msg, CartoonMessageList.class);
                    Page page = gson.fromJson(result.pageBean, Page.class);
                    MCUser user = bean.getUser();
                    daoHelper.addUser(user);
                    listener.onLoadCartoonMessageSuccess(bean.getUser(), bean.getList(), page);
                    if (page.getPage() == 1 && application.isLogin() && application.user.getId() == bean.getUser().getId()) {
                        cache.put(url, params, result.msg);
                    }
                } else {
                    if (null != listener) {
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

    public interface OnLoadCartoonDynamicResponseListener {
        void onLoadCartoonDynamicSuccess(MCUser user, ArrayList<CartoonMessage> dynamics, Page page);

        void onLoadCartoonDynamicFailure(String msg);
    }

    public void loadCartoonDyanmic(final Context context, int userId, int nextpage, final OnLoadCartoonDynamicResponseListener listener) {
        final String url = domainName + context.getString(R.string.interface_cartooncynamic);
        final RequestParams params = new RequestParams();
        params.put("userId", userId);
        params.put("page", nextpage);
        httpClient.post(url, params, new JsonHttpResponseHandler() {
            @Override
            public void onStart() {
                String result = cache.get(url, params);
                if (null != result && result.length() > 10) {
                    CartoonMessageList bean = gson.fromJson(result, CartoonMessageList.class);
                    listener.onLoadCartoonDynamicSuccess(bean.getUser(), bean.getList(), new Page(20, 1, 20));
                }
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                ParseResponse result = new ParseResponse(context, response);
                if (result.isSuccess) {
                    CartoonMessageList bean = gson.fromJson(result.msg, CartoonMessageList.class);
                    Page page = gson.fromJson(result.pageBean, Page.class);
                    MCUser user = bean.getUser();
                    daoHelper.addUser(user);
                    listener.onLoadCartoonDynamicSuccess(bean.getUser(), bean.getList(), page);
                    if (1 == page.getPage() && application.isLogin() && application.user.getId() == user.getId()) {
                        cache.put(url, params, result.msg);
                    }
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
    public interface OnLoadCartoonWorkResponseListener {
        void onLoadCartoonWorkSuccess(MCUser user, ArrayList<Cartoon> work, Page page);

        void onLoadCartoonWorkFailure(String msg);
    }

    public void loadCartoonWork(final Context context, int userId, int nextPage, final OnLoadCartoonWorkResponseListener listener) {
        final String url = domainName + context.getString(R.string.interface_cartoonwork);
        final RequestParams params = new RequestParams();
        params.put("userId", userId);
        params.put("page", nextPage);
        httpClient.post(url, params, new JsonHttpResponseHandler() {
            @Override
            public void onStart() {
                String result = cache.get(url, params);
                if (null != result && result.length() > 10) {
                    CartoonWorkList bean = gson.fromJson(result, CartoonWorkList.class);
                    listener.onLoadCartoonWorkSuccess(bean.getUser(), bean.getList(), new Page(20, 1, 20));
                }
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                ParseResponse result = new ParseResponse(context, response);
                if (result.isSuccess) {
                    CartoonWorkList bean = gson.fromJson(result.msg, CartoonWorkList.class);
                    Page page = gson.fromJson(result.pageBean, Page.class);
                    MCUser user = bean.getUser();
                    listener.onLoadCartoonWorkSuccess(bean.getUser(), bean.getList(), page);
                    daoHelper.addUser(user);
                    if (page.getPage() == 1 && application.isLogin() && application.user.getId() == user.getId()) {
                        cache.put(url, params, result.msg);
                    }
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
    public interface OnLoadCommunityMessageResponseListener {
        void onLoadCommunityMessageSuccess(ArrayList<CommunityMessage> messages, User user, Page page);

        void onLoadCommunityMessageFailure(String msg);
    }

    public void loadCommunityMessage(final Context context, int userId, int nextPage, final OnLoadCommunityMessageResponseListener listener) {
        final String url = domainName + context.getString(R.string.interface_usercenter);
        final RequestParams params = new RequestParams();
        params.put("type", "message");
        params.put("id", userId);
        params.put("messageType", "all");
        params.put("page", nextPage);
        httpClient.post(url, params, new JsonHttpResponseHandler() {
            @Override
            public void onStart() {
                String result = cache.get(url, params);
                if (null != result && result.length() > 10) {
                    CommunityMessageBean bean = gson.fromJson(result, CommunityMessageBean.class);
                    CommunityMessageList list = bean.getList();
                    Page page = new Page(list.getAllCount(), list.getPage(), list.getPageSize());
                    listener.onLoadCommunityMessageSuccess(list.getData(), bean.getUser(), page);
                }
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                ParseResponse result = new ParseResponse(context, response);
                if (result.isSuccess) {
                    CommunityMessageBean bean = gson.fromJson(result.msg, CommunityMessageBean.class);
                    if (null != bean && null != bean.getList() && null != bean.getUser()) {
                        CommunityMessageList list = bean.getList();
                        Page page = new Page(list.getAllCount(), list.getPage(), list.getPageSize());
                        listener.onLoadCommunityMessageSuccess(list.getData(), bean.getUser(), page);
                        daoHelper.addUser(bean.getUser());
                        cache.put(url, params, result.msg);
                    } else {
                        listener.onLoadCommunityMessageFailure("转换数据失败！");
                    }
                } else {
                    listener.onLoadCommunityMessageFailure(result.msg);
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
        void onLoadCommunityDynamicSuccess(ArrayList<CommunityDynamic> dynamics, User user, Page page);

        void onLoadCommunityDynamicFailure(String msg);
    }

    public void loadCommunityDynamic(final Context context, int userId, int nextPage, final OnLoadCommunityDynamicResponseListener listener) {
        final String url = domainName + context.getString(R.string.interface_usercenter);
        final RequestParams params = new RequestParams();
        params.put("type", "dynamic");
        params.put("id", userId);
        params.put("page", nextPage);
        httpClient.post(url, params, new JsonHttpResponseHandler() {
            @Override
            public void onStart() {
                String result = cache.get(url, params);
                if (null != result && result.length() > 10) {
                    CommunityDynamicBean bean = gson.fromJson(result, CommunityDynamicBean.class);
                    Page page = new Page(bean.getList().getAllCount(), bean.getList().getPage(), bean.getList().getPageSize());
                    listener.onLoadCommunityDynamicSuccess(bean.getList().getData(), bean.getUser(), page);
                }
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                ParseResponse result = new ParseResponse(context, response);
                if (result.isSuccess) {
                    CommunityDynamicBean bean = gson.fromJson(result.msg, CommunityDynamicBean.class);
                    if (null != bean && null != bean.getList() && null != bean.getUser()) {
                        Page page = new Page(bean.getList().getAllCount(), bean.getList().getPage(), bean.getList().getPageSize());
                        listener.onLoadCommunityDynamicSuccess(bean.getList().getData(), bean.getUser(), page);
                        daoHelper.addUser(bean.getUser());
                        if (page.getPage() == 1 && application.isLogin() && application.user.getId() == bean.getUser().getId()) {
                            cache.put(url, params, result.msg);
                        }
                    } else {
                        listener.onLoadCommunityDynamicFailure("转换数据失败！");
                    }
                } else {
                    listener.onLoadCommunityDynamicFailure(result.msg);
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
        void onLoadCommunityWorkSuccess(ArrayList<Post> works, User user, Page page);

        void onLoadCommunityWorkFailure(String msg);
    }

    public void loadCommunityWork(final Context context, int userId, int nextPage, final OnloadCommunityWorkResponseListener listener) {
        final String url = domainName + context.getString(R.string.interface_usercenter);
        final RequestParams params = new RequestParams();
        params.put("type", "work");
        params.put("id", userId);
        params.put("page", nextPage);
        httpClient.post(url, params, new JsonHttpResponseHandler() {
            @Override
            public void onStart() {
                String result = cache.get(url,params);
                if (null != result && result.length() > 10){
                    CommunityWorkBean bean = gson.fromJson(result, CommunityWorkBean.class);
                    Page page = new Page(bean.getList().getAllCount(), bean.getList().getPage(), bean.getList().getPageSize());
                    listener.onLoadCommunityWorkSuccess(bean.getList().getdata(), bean.getUser(), page);
                }
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                ParseResponse result = new ParseResponse(context, response);
                if (result.isSuccess) {
                    CommunityWorkBean bean = gson.fromJson(result.msg, CommunityWorkBean.class);
                    if (null != bean && null != bean.getList() && null != bean.getUser()) {
                        Page page = new Page(bean.getList().getAllCount(), bean.getList().getPage(), bean.getList().getPageSize());
                        listener.onLoadCommunityWorkSuccess(bean.getList().getdata(), bean.getUser(), page);
                        daoHelper.addUser(bean.getUser());
                        cache.put(url, params, result.msg);
                    } else {
                        listener.onLoadCommunityWorkFailure("转换数据失败！");
                    }
                } else {
                    listener.onLoadCommunityWorkFailure(result.msg);
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
                ParseResponse result = new ParseResponse(context, response);
                if (result.isSuccess) {
                    listener.onAddFriendSuccess();
                } else {
                    listener.onAddFriendFailure(result.msg);
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

    public void loadFriendList(final Context context, final int nextPage, final OnloadFriendResponseListener listener) {
        final String url = domainName + context.getString(R.string.interface_fellowuserlist);
        final RequestParams params = new RequestParams();
        params.put("id", MCKuai.instence.user.getId());
        params.put("page", nextPage);
        httpClient.post(url, params, new JsonHttpResponseHandler() {
            @Override
            public void onStart() {
                String result = cache.get(url, params);
                if (null != url && result.length() > 10) {
                    FriendBean bean = gson.fromJson(result, FriendBean.class);
                    listener.onLoadFriendSuccess(bean.getData(), new Page(20,1,20));
                }
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                ParseResponse result = new ParseResponse(context, response);
                if (result.isSuccess) {
                    FriendBean bean = gson.fromJson(result.msg, FriendBean.class);
                    if (null != bean) {
                        Page page = new Page(bean.getAllCount(), bean.getPage(), bean.getPageSize());
                        ArrayList<MCUser> users = bean.getData();
                        listener.onLoadFriendSuccess(bean.getData(), page);
                        if (null != users && !users.isEmpty()) {
                            for (MCUser user : users) {
                                User tempuser = new User(user);
                                tempuser.setIsFriend(true);
                                daoHelper.addUser(tempuser);
                            }
                        }
                        if (1 == nextPage) {
                            cache.put(url, params, result.msg);
                        }
                    } else {
                        listener.OnloadFriendFailure("转换数据失败！");
                    }
                } else {
                    listener.OnloadFriendFailure(result.msg);
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
                ParseResponse result = new ParseResponse(context, response);
                if (result.isSuccess) {
                    listener.onUploadCoverSuccess(result.msg);
                } else {
                    listener.onUploadCoverFailure(result.msg);
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
                ParseResponse result = new ParseResponse(context, response);
                if (result.isSuccess) {
                    listener.onUpdateUserCoverSuccess();
                } else {
                    listener.onUpdateUserCoverFailure(result.msg);
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
                ParseResponse result = new ParseResponse(context, response);
                if (result.isSuccess) {
                    listener.onUpdateUserNickSuccess();
                } else {
                    listener.onUpdateUserNickFailure(result.msg);
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
                ParseResponse result = new ParseResponse(context, response);
                if (result.isSuccess) {
                    listener.onUpdateAddressSuccess();
                } else {
                    listener.onUpdateAddressFailure(result.msg);
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

    public void loadRecommend(final Context context, Integer userId, ArrayList<Cartoon> cartoons, final OnLoadMessageResponseListener listener) {
        final String url = domainName + context.getString(R.string.itnerface_loadmessage);
        final RequestParams params = new RequestParams();
        if (null != userId) {
            params.put("userId", userId);
        }
        if (null != cartoons && !cartoons.isEmpty()) {
            String ids = "";
            for (Cartoon cartoon : cartoons) {
                ids += cartoon.getId() + ",";
            }
            ids.subSequence(1, ids.length());
            params.put("ids", ids);
        }
        httpClient.get(url, params, new JsonHttpResponseHandler() {
            @Override
            public void onStart() {
                String result = cache.get(url, params);
                if (null != result && result.length() > 10) {
                    ArrayList<Cartoon> cartoons = gson.fromJson(result, new TypeToken<ArrayList<Cartoon>>() {
                    }.getType());
                    listener.onLoadMessageSuccess(cartoons);
                }
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                ParseResponse result = new ParseResponse(context, response);
                if (result.isSuccess) {
                    ArrayList<Cartoon> cartoons = gson.fromJson(result.msg, new TypeToken<ArrayList<Cartoon>>() {
                    }.getType());
                    listener.onLoadMessageSuccess(cartoons);
                    cache.put(url, params, result.msg);
                } else {
                    listener.onLoadMessageFailure(result.msg);
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                listener.onLoadMessageFailure(throwable.getLocalizedMessage());
            }
        });
    }

    /***************************************************************************
     * 推荐用户
     ***************************************************************************/
    public interface OnLoadRecommendUserListener {
        void onLoadUserSuccess(ArrayList<User> users);

        void onLoadUserFailure(String msg);
    }

    public void loadRecommendUser(final Context context, Integer userId, final OnLoadRecommendUserListener listener) {
        String url = domainName + "interface.do?act=recPeople";
        if (null != userId) {
            url += ("&userId=" + userId);
        }
        final String finalUrl = url;
        httpClient.get(url, new JsonHttpResponseHandler() {
            @Override
            public void onStart() {
                String result = cache.get(finalUrl);
                if (null != result && result.length() >10){
                    ArrayList<User> users = gson.fromJson(result, new TypeToken<ArrayList<User>>() {
                    }.getType());
                    listener.onLoadUserSuccess(users);
                }
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                //super.onSuccess(statusCode, headers, response);
                ParseResponse result = new ParseResponse(context, response);
                if (result.isSuccess) {
                    ArrayList<User> users = gson.fromJson(result.msg, new TypeToken<ArrayList<User>>() {
                    }.getType());
                    listener.onLoadUserSuccess(users);
                    for (User user:users){
                        daoHelper.addUser(user);
                    }
                    cache.put(finalUrl,result.msg);
                } else {
                    listener.onLoadUserFailure(result.msg);
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                //super.onFailure(statusCode, headers, responseString, throwable);
                listener.onLoadUserFailure(throwable.getLocalizedMessage());
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
                ParseResponse result = new ParseResponse(context, response);
                if (result.isSuccess) {
                    Cartoon cartoon = gson.fromJson(result.msg, Cartoon.class);
                    if (null != cartoon && null != listener) {
                        listener.onLoadDetailSuccess(cartoon);
                    } else {
                        listener.onLoadDetailFailure("转换数据失败！");
                    }
                } else {
                    listener.onLoadDetailFailure(result.msg);
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

    /**
     * 获取用户信息
     */
    public interface OnLoadUserInfoResponseListener {
        void onLoadUserInfoSuccess(User user);

        void onLoadUserInfoFailure(String msg);
    }

    public void loadUserInfo(final Context context, String userName, final OnLoadUserInfoResponseListener listener) {
        String url = domainName + "interface.do?act=newUserInfo";
        RequestParams params = new RequestParams();
        params.put("userName", userName);
        httpClient.post(url, params, new JsonHttpResponseHandler() {
            @Override
            public void onStart() {
                super.onStart();
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                ParseResponse result = new ParseResponse(context, response);
                if (result.isSuccess) {
                    MCUser user = gson.fromJson(result.msg, MCUser.class);
                    if (null != user) {
                        listener.onLoadUserInfoSuccess(new User(user));
                        daoHelper.addUser(user);
                    } else {
                        listener.onLoadUserInfoFailure("返回数据不正确！");
                    }
                } else {
                    listener.onLoadUserInfoFailure(result.msg);
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                listener.onLoadUserInfoFailure(throwable.getLocalizedMessage());
            }

        });
    }

    public interface OnCheckFriendshipResponseListener {
        void onIsFriendShip();

        void onIsStrangerShip();

        void onError(String msg);
    }

    public void checkFriendship(final Context context, int userId, final OnCheckFriendshipResponseListener listener) {
        String url = domainName + "interface.do?act=isAttention";
        RequestParams params = new RequestParams();
        params.put("ownerId", MCKuai.instence.user.getId());
        params.put("otherId", userId);
        httpClient.get(url, params, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                ParseResponse result = new ParseResponse(context, response);
                if (result.isSuccess) {
                    listener.onIsFriendShip();
                } else {
                    listener.onIsStrangerShip();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                listener.onError(throwable.getLocalizedMessage());
            }
        });
    }

    /**
     * 获取退出广告
     */
    public interface OnGetAdResponse {
        void onGetAdSuccess(Ad ad);

        void onGetAdFailure(String msg);
    }

    public void getAd(final Context context, final OnGetAdResponse listener) {
        String url = "http://api.mckuai.com/interface.do?act=adv";
        httpClient.get(url, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                ParseResponse result = new ParseResponse(context, response);
                if (result.isSuccess) {
                    Gson gson = new Gson();
                    Ad ad = gson.fromJson(result.msg, Ad.class);
                    if (null != ad) {
                        listener.onGetAdSuccess(ad);
                    } else {
                        listener.onGetAdFailure("返回数据解析失败！");
                    }
                } else {
                    listener.onGetAdFailure(result.msg);
                }

            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                listener.onGetAdFailure(throwable.getLocalizedMessage());
            }
        });
    }


    /***************************************************************************
     * 获取主题列表
     ***************************************************************************/

    public interface OnGetThemeListListener{
        void onGetThemeListSuccess(ArrayList<Theme> themes);
        void onGetThemeListFailure(String msg);
    }

    public void getThemeList(final Activity context,@NonNull final OnGetThemeListListener listener){
        String url = "http://api.mckuai.com/interface.do?act=adv";
        Request.Builder builder = new Request.Builder();
        builder.url(url);
        client.newCall(builder.build()).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {

            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (null != response) {
                    final ParseResponse parseResponse = new ParseResponse(response, false);
                    if (parseResponse.isSuccess) {
                        String result = response.body().string();
                        Log.d(result);
                        context.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                listener.onGetThemeListSuccess(new ArrayList<Theme>(0));
                            }
                        });

                    } else {
                        context.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                listener.onGetThemeListFailure(parseResponse.msg);
                            }
                        });
                    }
                }
            }
        });


    }



    public void rewardCartoon(){

    }


    private static InputStream Bitmap2IS(Bitmap bm) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bm.compress(Bitmap.CompressFormat.JPEG, 80, baos);
        InputStream sbs = new ByteArrayInputStream(baos.toByteArray());
        return sbs;
    }
}
