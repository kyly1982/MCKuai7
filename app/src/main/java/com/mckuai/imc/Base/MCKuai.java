package com.mckuai.imc.Base;

import android.app.ActivityManager;
import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Environment;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import com.mckuai.imc.Bean.MCCartoonPKNotificationMessage;
import com.mckuai.imc.Bean.MCUser;
import com.mckuai.imc.Bean.Token;
import com.mckuai.imc.R;
import com.mckuai.imc.Util.MCDaoHelper;
import com.mckuai.imc.Util.MCNetEngine;
import com.nostra13.universalimageloader.cache.disc.impl.UnlimitedDiskCache;
import com.nostra13.universalimageloader.cache.disc.naming.HashCodeFileNameGenerator;
import com.nostra13.universalimageloader.cache.memory.impl.UsingFreqLimitedMemoryCache;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;
import com.nostra13.universalimageloader.core.display.CircleBitmapDisplayer;
import com.nostra13.universalimageloader.core.download.BaseImageDownloader;
import com.tencent.tauth.Tencent;
import com.umeng.socialize.Config;
import com.umeng.socialize.PlatformConfig;
import com.zhy.http.okhttp.OkHttpUtils;

import java.io.File;
import java.util.concurrent.TimeUnit;

import cn.finalteam.okhttpfinal.OkHttpFinal;
import cn.finalteam.okhttpfinal.OkHttpFinalConfiguration;
import io.rong.imkit.RongIM;
import io.rong.imlib.RongIMClient;
import okhttp3.OkHttpClient;

/**
 * Created by kyly on 2016/1/18.
 */
public class MCKuai extends Application {
    public static MCKuai instence;
    public MCUser user;
    public MCDaoHelper daoHelper;
    public MCNetEngine netEngine;
    private DisplayImageOptions circleOptions;

    private final int IMAGE_POOL_SIZE = 3;// 线程池数量
    private final int CONNECT_TIME = 5 * 1000;// 连接时间
    private final int TIME_OUT = 10 * 1000;// 超时时间
    private final int MEM_CACHE_SIZE = 8 * 1024 * 1024;//内存缓存大小
    private boolean isInited = false;

    private String mCacheDir;
    public boolean isFirstBoot = true;
    public boolean isIMInited = false;
    private int imLoginTryCount = 0;
    public boolean isIMLogined = false;
    public long leadTag;


    public interface IMLoginListener {
        void onInitError();

        void onTokenIncorrect();

        void onLoginFailure(String msg);

        void onLoginSuccess(String msg);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        instence = this;
        init();
    }


    public void init() {
        if (!isInited) {
            isInited = true;
            readPreference();
            daoHelper = new MCDaoHelper(this);
            netEngine = new MCNetEngine();
            initUMPlatform();
            initImageLoader();
            initRongIM();
            if (isIMInited && null != user && user.isUserValid() && null != user.getToken() && 10 < user.getToken().length()) {
                loginIM(null);
            }
            OkHttpClient.Builder builder = new OkHttpClient.Builder();
            builder.connectTimeout(TIME_OUT, TimeUnit.MILLISECONDS)
                    .retryOnConnectionFailure(true);
            OkHttpUtils.getInstance(builder.build());
            OkHttpFinalConfiguration.Builder builder1 = new OkHttpFinalConfiguration.Builder().setRetryOnConnectionFailure(false);
            OkHttpFinal.getInstance().init(builder1.build());
        }
    }

    /**
     * 阻塞初始化融云，如果三秒内还未完成初始化，则退出
     */
    public void initRongIM() {
        if (getApplicationInfo().packageName.equals(getCurProcessName(getApplicationContext())) ||
                "io.rong.push".equals(getCurProcessName(getApplicationContext()))) {
            RongIM.init(this);
            long time = System.currentTimeMillis();
            while (null == RongIM.getInstance()) {
                //超过3秒仍未初始化则退出
                if (System.currentTimeMillis() - time > 3000) {
                    return;
                }
            }
            if (null != RongIM.getInstance()) {
                RongIM.registerMessageType(MCCartoonPKNotificationMessage.class);
                isIMInited = true;
            }
        }
    }

    public void loginIM(final IMLoginListener listener) {
        //未初始化， 尝试初始化再登录
        if (!isIMInited) {
            if (imLoginTryCount < 3) {
                initRongIM();
                loginIM(listener);
                imLoginTryCount++;
            } else {
                if (null != listener) {
                    listener.onInitError();
                }
            }
        } else if (null != user && null != user.getToken()) {
            if (getApplicationInfo().packageName.equals(getCurProcessName(getApplicationContext()))) {
                RongIM.connect(user.getToken(), new RongIMClient.ConnectCallback() {
                    @Override
                    public void onTokenIncorrect() {
                        if (null != listener) {
                            listener.onTokenIncorrect();
                        }
                    }

                    @Override
                    public void onSuccess(String s) {
                        isIMLogined = true;
                        if (null != listener) {
                            listener.onLoginSuccess(s);
                        }
                    }

                    @Override
                    public void onError(RongIMClient.ErrorCode errorCode) {
                        if (null != listener) {
                            listener.onLoginFailure(errorCode.getMessage());
                        }
                    }
                });
                imLoginTryCount = 0;
            }
        }
    }

    private void initUMPlatform() {
        PlatformConfig.setWeixin("wx49ba2c7147d2368d", "85aa75ddb9b37d47698f24417a373134");
        PlatformConfig.setQQZone("101155101", "78b7e42e255512d6492dfd135037c91c");
        try {
            Config.isloadUrl = true;
        } catch (Exception e){
            e.printStackTrace();
        }

    }

    private void initImageLoader() {
        ImageLoaderConfiguration configuration = new ImageLoaderConfiguration.Builder(getApplicationContext())
                .threadPoolSize(IMAGE_POOL_SIZE)
                .threadPriority(Thread.NORM_PRIORITY - 2)
                .denyCacheImageMultipleSizesInMemory() // 对于同一url只缓存一个图
                .memoryCache(new UsingFreqLimitedMemoryCache(MEM_CACHE_SIZE))
                .memoryCacheSize(MEM_CACHE_SIZE)
                .diskCache(new UnlimitedDiskCache(new File(getImageCacheDir()), null, new HashCodeFileNameGenerator()))
                .diskCacheExtraOptions(1080, 1080, null)
                .diskCacheSize(100 * 1024 * 1024)
                .tasksProcessingOrder(QueueProcessingType.FIFO)
                .defaultDisplayImageOptions(getNormalOptions())
                .imageDownloader(new BaseImageDownloader(getApplicationContext(), CONNECT_TIME, TIME_OUT))
                .build();
        ImageLoader.getInstance().init(configuration);
    }


    public MCUser readPreference() {
        SharedPreferences preferences = getSharedPreferences(getString(R.string.preferences_filename), 0);
        isFirstBoot = preferences.getBoolean(getString(R.string.preferences_isFirstBoot), true);
        leadTag = preferences.getLong(getString(R.string.preferences_leadtag),0);
        if (!isFirstBoot) {
            Token token = new Token();
            token.setBirthday(preferences.getLong(getString(R.string.preferences_tokentime), 0));
            token.setExpires(preferences.getLong(getString(R.string.preferences_tokenexpires), 0));
            // 检查qq的token是否有效如果在有效期内则获取qqtoken
            if (token.isTokenSurvival()) {
                token.setToken(preferences.getString(getString(R.string.preferences_token), null));
                token.setType(preferences.getInt(getString(R.string.preferences_tokentype), 0));
                user = new MCUser();
                user.setLoginToken(token);
                user.setId(preferences.getInt(getString(R.string.preferences_id), 0));                    //id
                user.setName(preferences.getString(getString(R.string.preferences_username), null));      //姓名,实为wx的access_token或者qq的openId
                user.setNike(preferences.getString(getString(R.string.preferences_nickname), null));      // 显示名
                user.setHeadImg(preferences.getString(getString(R.string.preferences_cover), null));      // 用户头像
                user.setGender(preferences.getString(getString(R.string.preferences_gender), null));         // 性别
                user.setProcess(preferences.getFloat(getString(R.string.preferences_process), 0));         //进度
                user.setScore(preferences.getInt(getString(R.string.preferences_score), 0));              //积分
                user.setLevel(preferences.getInt(getString(R.string.preferences_level), 0));              //level
                user.setAddr(preferences.getString(getString(R.string.preferences_addr), null));           //地址
                user.setToken(preferences.getString(getString(R.string.preferences_token_rongcloud), null));//融云token
            }
        }
        return user;
    }

    public void saveProfile() {
        SharedPreferences preferences = getSharedPreferences(getString(R.string.preferences_filename), 0);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean(getString(R.string.preferences_isFirstBoot), false);
        editor.putLong(getString(R.string.preferences_leadtag),leadTag);
        if (null != user && null != user.getLoginToken()) {
            editor.putInt(getString(R.string.preferences_tokentype), user.getLoginToken().getType());
            editor.putLong(getString(R.string.preferences_tokentime), user.getLoginToken().getBirthday());
            editor.putLong(getString(R.string.preferences_tokenexpires), user.getLoginToken().getExpires());
            editor.putString(getString(R.string.preferences_token), user.getLoginToken().getToken());
        }
        if (null != user && user.isUserValid()) {
            editor.putInt(getString(R.string.preferences_id), user.getId());          //id
            editor.putString(getString(R.string.preferences_username), user.getName() + "");//openid
            editor.putString(getString(R.string.preferences_nickname), user.getNike() + "");//显示名
            editor.putString(getString(R.string.preferences_cover), user.getHeadImg() + "");// 用户头像
            editor.putString(getString(R.string.preferences_gender), user.getGender() + "");// 性别
            editor.putInt(getString(R.string.preferences_score), user.getScore());         //积分
            editor.putFloat(getString(R.string.preferences_process), user.getProcess());    //进度
            editor.putInt(getString(R.string.preferences_level), user.getLevel());          //level
            editor.putString(getString(R.string.preferences_addr), user.getAddr());         //地址
            editor.putString(getString(R.string.preferences_token_rongcloud), user.getToken());//融云token
        }
        editor.apply();
    }

    public String getImageCacheDir() {
        return getCacheRoot() + File.separator + getString(R.string.imagecache_dir) + File.separator;
    }

    public String getJsonFile() {
        return getCacheRoot() + File.separator + getString(R.string.jsoncache_dir) + File.separator + getString(R.string.jsoncache_file);
    }

    private String getCacheRoot() {
        if (null == mCacheDir) {
            if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())
                    || !Environment.isExternalStorageRemovable()) {
                if (null != getExternalCacheDir()) {
                    mCacheDir = getExternalCacheDir().getPath();
                } else {
                    mCacheDir = getCacheDir().getPath();
                }
            } else {
                mCacheDir = getCacheDir().getPath();
            }
        }
        return mCacheDir;
    }

    public MCUser getUser() {
        return user;
    }

    public void setUser(MCUser user) {
        this.user = user;
    }

    public boolean isLogin() {
        return null != user && user.getId() > 0;
    }

    public DisplayImageOptions getCircleOptions() {
        if (null == circleOptions) {
            circleOptions = new DisplayImageOptions.Builder()
                    // 加载过程中显示的图片
                    .showStubImage(R.mipmap.ic_usercover_default)
                    .showImageForEmptyUri(R.mipmap.ic_usercover_default)
                    .showImageOnFail(R.mipmap.ic_usercover_default)
                    .cacheInMemory(true)
                    .cacheOnDisk(true)
                    .imageScaleType(ImageScaleType.EXACTLY)
                    .bitmapConfig(Bitmap.Config.RGB_565)
                    .delayBeforeLoading(150)
                    .displayer(new CircleBitmapDisplayer())// 此处需要修改大小
                    .build();
        }
        return circleOptions;
    }

    public DisplayImageOptions getNormalOptions() {
        DisplayImageOptions options = new DisplayImageOptions
                .Builder()
                .cacheOnDisk(true)
                .cacheInMemory(true)
                .imageScaleType(ImageScaleType.EXACTLY)
                .bitmapConfig(Bitmap.Config.RGB_565)
                .delayBeforeLoading(150)
                .build();
        return options;
    }


    public void logout() {
        if (isLogin() && null != user.getLoginToken()) {
            user.getLoginToken().setExpires(0);
            saveProfile();
            user = null;
            Tencent tencent = Tencent.createInstance("101155101", getApplicationContext());
            if (null != tencent && tencent.isSessionValid()) {
                tencent.logout(getApplicationContext());
            }
            if (null != RongIM.getInstance() && null != RongIM.getInstance().getRongIMClient()) {
                try {
                    RongIM.getInstance().logout();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public static String getCurProcessName(Context context) {

        int pid = android.os.Process.myPid();

        ActivityManager activityManager = (ActivityManager) context
                .getSystemService(Context.ACTIVITY_SERVICE);

        for (ActivityManager.RunningAppProcessInfo appProcess : activityManager
                .getRunningAppProcesses()) {

            if (appProcess.pid == pid) {
                return appProcess.processName;
            }
        }
        return null;
    }

    public void handleExit() {
        daoHelper.close();
        if (isLogin() && isIMLogined) {
            RongIM.getInstance().disconnect();
        }
        if (null != netEngine){
            netEngine.exit();
        }
        saveProfile();
    }

    public void hideSoftKeyboard(View view) {
        if (null != view) {
            InputMethodManager methodManager = (InputMethodManager) view.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
            methodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    public void setLeadTag(long tag){
        leadTag = leadTag | tag;
    }
}
