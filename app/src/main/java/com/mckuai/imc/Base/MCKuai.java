package com.mckuai.imc.Base;

import android.app.Application;
import android.content.SharedPreferences;
import android.os.Environment;

import com.mckuai.imc.Bean.MCUser;
import com.mckuai.imc.Bean.Token;
import com.mckuai.imc.R;
import com.nostra13.universalimageloader.cache.disc.impl.UnlimitedDiskCache;
import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;
import com.nostra13.universalimageloader.core.download.BaseImageDownloader;

import java.io.File;

import io.rong.imkit.RongIM;

/**
 * Created by kyly on 2016/1/18.
 */
public class MCKuai extends Application {
    public static MCKuai instence;
    public MCUser user;
    public boolean isFirstBoot = true;

    private final int IMAGE_POOL_SIZE = 3;// 线程池数量
    private final int CONNECT_TIME = 15 * 1000;// 连接时间
    private final int TIME_OUT = 30 * 1000;// 超时时间

    private String mCacheDir;

    @Override
    public void onCreate() {
        super.onCreate();
        init();
        instence = this;
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
    }

    private void init() {
        RongIM.init(this);
    }

    private void initImageLoader() {
        ImageLoaderConfiguration configuration = new ImageLoaderConfiguration.Builder(getApplicationContext())
                .memoryCacheExtraOptions(750, 480)
                .threadPoolSize(IMAGE_POOL_SIZE)
                .threadPriority(Thread.NORM_PRIORITY - 2)
                .denyCacheImageMultipleSizesInMemory()
                        // 对于同一url只缓存一个图
                        //.memoryCache(new UsingFreqLimitedMemoryCache(MEM_CACHE_SIZE)).memoryCacheSize(MEM_CACHE_SIZE)
                .discCacheFileNameGenerator(new Md5FileNameGenerator()).tasksProcessingOrder(QueueProcessingType.FIFO)
                .discCache(new UnlimitedDiskCache(new File(getImageCacheDir())))
                .defaultDisplayImageOptions(DisplayImageOptions.createSimple())
                .imageDownloader(new BaseImageDownloader(getApplicationContext(), CONNECT_TIME, TIME_OUT))
                .writeDebugLogs().build();
        ImageLoader.getInstance().init(configuration);
    }

    public MCUser readPreference() {
        SharedPreferences preferences = getSharedPreferences(getString(R.string.preferences_filename), 0);
        isFirstBoot = preferences.getBoolean(getString(R.string.preferences_isFirstBoot), true);
        if (!isFirstBoot) {
            Token token = new Token();
            token.setBirthday(preferences.getLong(getString(R.string.preferences_tokentime), 0));
            token.setExpires(preferences.getLong(getString(R.string.preferences_tokenexpires), 0));
            // 检查qq的token是否有效如果在有效期内则获取qqtoken
            if (token.isTokenSurvival()) {
                token.setToken(preferences.getString(getString(R.string.preferences_token), null));
                token.setType(preferences.getInt(getString(R.string.preferences_tokentype), 0));
                user = new MCUser();
                user.setToken(token);
                user.setId(preferences.getInt(getString(R.string.preferences_id), 0));                    //id
                user.setName(preferences.getString(getString(R.string.preferences_username), null));      //姓名,实为wx的access_token或者qq的openId
                user.setNike(preferences.getString(getString(R.string.preferences_nickname), null));      // 显示名
                user.setHeadImg(preferences.getString(getString(R.string.preferences_cover), null));      // 用户头像
                user.setGender(preferences.getString(getString(R.string.preferences_gender), null));         // 性别
                user.setProcess(preferences.getFloat(getString(R.string.preferences_process), 0));         //进度
                user.setScore(preferences.getInt(getString(R.string.preferences_score), 0));              //积分
                user.setLevel(preferences.getInt(getString(R.string.preferences_level), 0));              //level
                user.setAddr(preferences.getString(getString(R.string.preferences_addr), null));           //地址
                user.setAllScore(preferences.getLong(getString(R.string.preferences_wa_score), 0));       //mcwa积分
                user.setRanking(preferences.getLong(getString(R.string.preferences_wa_ranking), 0));      //mcwa积分排行
                user.setScoreRank(preferences.getInt(getString(R.string.preferences_wa_scorerank), 0));   //mcwa积分排行
                user.setAnswerNum(preferences.getInt(getString(R.string.preferences_wa_answercount), 0)); //mcwa回答数
                user.setUploadNum(preferences.getInt(getString(R.string.preferences_wa_uploadcount), 0)); //mcwa贡献数
            }
        }
        return user;
    }

    public void saveProfile() {
        SharedPreferences preferences = getSharedPreferences(getString(R.string.preferences_filename), 0);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean(getString(R.string.preferences_isFirstBoot), false);
        if (null != user && null != user.getToken()) {
            editor.putInt(getString(R.string.preferences_tokentype), user.getToken().getType());
            editor.putLong(getString(R.string.preferences_tokentime), user.getToken().getBirthday());
            editor.putLong(getString(R.string.preferences_tokenexpires), user.getToken().getExpires());
            editor.putString(getString(R.string.preferences_token), user.getToken().getToken());
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
            editor.putLong(getString(R.string.preferences_wa_score), user.getAllScore());   //mcwa积分
            editor.putLong(getString(R.string.preferences_wa_ranking), user.getRanking());   //mcwa积分排行
            editor.putInt(getString(R.string.preferences_wa_scorerank), user.getScoreRank());//mcwa积分排行
            editor.putInt(getString(R.string.preferences_wa_answercount), user.getAnswerNum());//mcwa回答数
            editor.putInt(getString(R.string.preferences_wa_uploadcount), user.getUploadNum());//mcwa贡献数
            editor.commit();
        }
    }

    public String getImageCacheDir() {
        return getCacheRoot() + File.separator + getString(R.string.imagecache_dir) + File.separator;
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

    public void logout() {
        user.getToken().setExpires(0);
        saveProfile();
        user = null;
    }
}
