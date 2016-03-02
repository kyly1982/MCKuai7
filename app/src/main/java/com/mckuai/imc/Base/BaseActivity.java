package com.mckuai.imc.Base;

import android.app.Fragment;
import android.app.FragmentManager;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import com.mckuai.imc.Activity.CartoonActivity;
import com.mckuai.imc.Activity.ConversationActivity;
import com.mckuai.imc.Activity.ConversationListActivity;
import com.mckuai.imc.Activity.CreateCartoonActivity;
import com.mckuai.imc.Activity.LoginActivity;
import com.mckuai.imc.Activity.MainActivity;
import com.mckuai.imc.Activity.PostActivity;
import com.mckuai.imc.Activity.ProfileEditerActivity;
import com.mckuai.imc.Activity.PublishPostActivity;
import com.mckuai.imc.Activity.RecommendActivity;
import com.mckuai.imc.Activity.SearchActivity;
import com.mckuai.imc.Activity.UserCenterActivity;
import com.mckuai.imc.BuildConfig;
import com.mckuai.imc.R;
import com.mckuai.imc.Widget.autoupdate.AppUpdate;
import com.mckuai.imc.Widget.autoupdate.AppUpdateService;
import com.mckuai.imc.Widget.autoupdate.ResponseParser;
import com.mckuai.imc.Widget.autoupdate.Version;
import com.mckuai.imc.Widget.autoupdate.internal.SimpleJSONParser;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.umeng.analytics.MobclickAgent;
import com.umeng.message.PushAgent;
import com.umeng.socialize.ShareAction;
import com.umeng.socialize.UMShareListener;
import com.umeng.socialize.bean.SHARE_MEDIA;
import com.umeng.socialize.media.UMImage;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.util.ArrayList;

public class BaseActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    public MCKuai mApplication = MCKuai.instence;
    public FragmentManager mFragmentManager;
    public android.support.v4.app.FragmentManager mFragmentManager_V4;
    public Toolbar mToolbar;
    protected DrawerLayout mDrawer;
    private AppCompatImageView userCover;
    private AppCompatTextView userName;
    private AppCompatTextView userLevel;
    private MenuItem logout;
    protected int mTitleResId;

    public boolean isSlidingMenuShow = false;
    protected ArrayList<BaseFragment> fragments;
    protected int currentFragmentIndex = -1;
    private ImageLoader loader = ImageLoader.getInstance();
    private AppUpdate appUpdate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (this instanceof CartoonActivity) {
            mTitleResId = R.string.activity_cartoondetial;
        } else if (this instanceof ConversationActivity) {
            mTitleResId = R.string.activity_conversation;
        } else if (this instanceof ConversationListActivity) {
            mTitleResId = R.string.activity_conversationlist;
        } else if (this instanceof CreateCartoonActivity) {
            mTitleResId = R.string.activity_createcartoon;
        } else if (this instanceof LoginActivity) {
            mTitleResId = R.string.activity_login;
        } else if (this instanceof MainActivity) {
            mTitleResId = R.string.activity_main;
            MobclickAgent.openActivityDurationTrack(false);//将页面和fragment分开统计
        } else if (this instanceof PostActivity) {
            mTitleResId = R.string.activity_postdetial;
        } else if (this instanceof ProfileEditerActivity) {
            mTitleResId = R.string.activity_profile;
        } else if (this instanceof PublishPostActivity) {
            mTitleResId = R.string.activity_createpost;
        } else if (this instanceof SearchActivity) {
            mTitleResId = R.string.activity_search;
        } else if (this instanceof UserCenterActivity) {
            mTitleResId = R.string.activity_usercenter;
        } else if (this instanceof RecommendActivity) {
            mTitleResId = R.string.activity_recommend;
        }
        PushAgent.getInstance(this).onAppStart();
    }

    @Override
    protected void onResume() {
        super.onResume();
        MobclickAgent.onResume(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        MobclickAgent.onPause(this);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == 99) {
                showUserInfo();
            }
        }
        if (-1 < currentFragmentIndex) {
            fragments.get(currentFragmentIndex).onActivityResult(requestCode, resultCode, data);
        }
    }


    /**
     * 配置actionBar,同时可以配置其图标和图标的点击事件（可能会被侧边栏的事件所覆盖）
     *
     * @param toolbarId                 toolbar的Id
     * @param navigationIconId          图标的资源ID，如里不需要设置，则置0
     * @param navigationOnClickListener 图标的点击事件，如果不需要设置，设置为null
     */
    public void initToolbar(int toolbarId, int navigationIconId, View.OnClickListener navigationOnClickListener) {
        mToolbar = (Toolbar) findViewById(toolbarId);
        mToolbar.setTitle("");
        setSupportActionBar(mToolbar);
        if (0 != navigationIconId) {
            mToolbar.setNavigationIcon(navigationIconId);
        }

        if (null != navigationOnClickListener) {
            mToolbar.setNavigationOnClickListener(navigationOnClickListener);
        }
    }


    /**
     * 配置侧边栏，既Drawer
     *
     * @param drawerLayoutId 侧边栏布局ID，既DrawerLayout布局的ID
     * @param navViewId      NavigationView控件的ID
     * @param listener       侧边栏条目选中监听器
     */
    public void initDrawer(int drawerLayoutId, int navViewId, NavigationView.OnNavigationItemSelectedListener listener) {
        mDrawer = (DrawerLayout) findViewById(drawerLayoutId);
        NavigationView navigationView = (NavigationView) findViewById(navViewId);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, mDrawer, mToolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close) {
            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                if (null == appUpdate) {
                    appUpdate = AppUpdateService.getAppUpdate(BaseActivity.this);
                }
                appUpdate.callOnResume();
                MobclickAgent.onEvent(BaseActivity.this, "openSlidmenu");
            }

            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
                if (null != appUpdate) {
                    appUpdate.callOnPause();
                }
            }
        };
        mDrawer.setDrawerListener(toggle);
        toggle.syncState();

        if (null != listener) {
            navigationView.setNavigationItemSelectedListener(listener);
        } else {
            navigationView.setNavigationItemSelectedListener(this);
        }
    }


    public void initDrawer() {
        mDrawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        View headerView = navigationView.getHeaderView(0);
        userCover = (AppCompatImageView) headerView.findViewById(R.id.usercover);
        userName = (AppCompatTextView) headerView.findViewById(R.id.username);
        userLevel = (AppCompatTextView) headerView.findViewById(R.id.userlevel);

        userCover.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                closeSlidmenu();
                MobclickAgent.onEvent(BaseActivity.this, "clickSlidmenu_usercover");
                if (mApplication.isLogin()) {
                    Intent intent;
                    intent = new Intent(BaseActivity.this, UserCenterActivity.class);
                    intent.putExtra(getString(R.string.usercenter_tag_userid), mApplication.user.getId());
                    startActivity(intent);
                } else {
                    callLogin(99);
                }
            }
        });


        Menu menu = navigationView.getMenu();
        logout = menu.findItem(R.id.nav_logout);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, mDrawer, mToolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close) {
            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
                isSlidingMenuShow = false;
            }

            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                isSlidingMenuShow = true;
                if (!mApplication.isLogin()) {
                    showUnLoginInfo();
                } else if (null == userCover.getTag()) {
                    showUserInfo();
                }
            }

            @Override
            public void onDrawerStateChanged(int newState) {
                super.onDrawerStateChanged(newState);
            }
        };
        mDrawer.setDrawerListener(toggle);
        toggle.syncState();
        navigationView.setNavigationItemSelectedListener(this);
    }

    /**
     * 分享
     *
     * @param title   分享的标题
     * @param content 分享内容
     * @param url     链拉地址
     * @param image   图片
     */
    public void share(String title, String content, String url, UMImage image) {
        SHARE_MEDIA[] displayList = new SHARE_MEDIA[]{
                SHARE_MEDIA.WEIXIN_CIRCLE, SHARE_MEDIA.WEIXIN, SHARE_MEDIA.QQ, SHARE_MEDIA.QZONE
        };
        ShareAction action = new ShareAction(this).setDisplayList(displayList);
        action.withTitle(title);
        action.withText(content);
        action.withTargetUrl(url);
        action.setCallback(new UMShareListener() {
            @Override
            public void onResult(SHARE_MEDIA share_media) {
                closeSlidmenu();
            }

            @Override
            public void onError(SHARE_MEDIA share_media, Throwable throwable) {
                showMessage(throwable.getLocalizedMessage(), null, null);
                closeSlidmenu();
            }

            @Override
            public void onCancel(SHARE_MEDIA share_media) {
                closeSlidmenu();
            }
        });
        if (null != image) {
            action.withMedia(image);
        }
        action.open();
    }

    private void showUserInfo() {
        loader.displayImage(mApplication.user.getHeadImg(), userCover, mApplication.getCircleOptions());
        userName.setText(mApplication.user.getNike());
        userLevel.setText(getString(R.string.usercenter_userlevel, mApplication.user.getLevel()));
        userCover.setTag(mApplication.user.getHeadImg());
        logout.setVisible(true);
        userCover.setTag(mApplication.user.getHeadImg());
    }

    private void showUnLoginInfo() {
        userName.setText("未登录");
        userLevel.setText("点击头像登录");
        userCover.setBackgroundResource(R.mipmap.ic_usercover_default);
        logout.setVisible(false);
    }

    /**
     * 设置fragment内容层，此方法用于支持非supportV4包中的fragment
     *
     * @param contentId 要被fragment替代的ViewGroup的ID
     * @param fragment  要显示的fragment
     */
    public void setContentFragment(int contentId, Fragment fragment) {
        if (null == mFragmentManager) {
            mFragmentManager = getFragmentManager();
        }
        mFragmentManager.beginTransaction().replace(contentId, fragment).commit();
    }

    /**
     * 设置fragment内容层，此方法用于支持supportV4包中的fragment
     *
     * @param contentId 要被fragment替代的ViewGroup的ID
     * @param fragment  要显示的fragment
     */
    public void setContentFragment(int contentId, android.support.v4.app.Fragment fragment) {
        if (null == mFragmentManager_V4) {
            mFragmentManager_V4 = getSupportFragmentManager();
        }
        mFragmentManager_V4.beginTransaction().replace(contentId, fragment).commit();
    }

    public void closeSlidmenu() {
        if (isSlidingMenuShow) {
            mDrawer.closeDrawers();
        }
    }

    public void openSlidmenu() {
        if (null != mDrawer && !isSlidingMenuShow) {

        }
    }


    @Override
    public void onBackPressed() {
        if (null != mDrawer) {
            if (mDrawer.isDrawerOpen(GravityCompat.START)) {
                mDrawer.closeDrawer(GravityCompat.START);
                return;
            }
        }
        if (null != fragments && 1 == fragments.size()) {
            if (fragments.get(0).onBackPressed()) {
                return;
            }
        }
        super.onBackPressed();
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        Intent intent;
        switch (item.getItemId()) {
            case R.id.nav_search:
                closeSlidmenu();
                intent = new Intent(this, SearchActivity.class);
                startActivity(intent);
                break;
            case R.id.nav_package:
                if (mApplication.isLogin()) {
                    intent = new Intent(this, SearchActivity.class);
                    startActivity(intent);
                } else {
                    showMessage("你还未登录", null, null);
                }
                break;
            case R.id.nav_setting:
                MobclickAgent.onEvent(this, "clickSlidmenu_setting");
                if (mApplication.isLogin()) {
                    closeSlidmenu();
                    intent = new Intent(this, ProfileEditerActivity.class);
                    startActivity(intent);
                } else {
                    showMessage("你还未登录", null, null);
                }
                break;
            case R.id.nav_share:
                MobclickAgent.onEvent(this, "clickSlidmenu_share");
                UMImage image = new UMImage(this, R.mipmap.ic_share_default);
                share("邀你一起玩麦块", "我正在使用最好用最好玩的麦块辅助工具——《麦块for我的世界盒子》，妈妈再也不用担心我玩不好麦块了，你也来看看吧！", getString(R.string.appdownload_url), image);
                closeSlidmenu();
                break;
            case R.id.nav_prise:
                MobclickAgent.onEvent(this, "clickSlidmenu_prise");
                closeSlidmenu();
                Uri uri = Uri.parse("market://details?id=" + getPackageName());
                intent = new Intent(Intent.ACTION_VIEW, uri);
                try {
                    startActivity(intent);
                } catch (ActivityNotFoundException e) {
                    showMessage("你还未安装市场", null, null);
                }
                break;
            case R.id.checkUpgread:
                MobclickAgent.onEvent(this, "clickSlidmenu_checkupgrade");
                closeSlidmenu();
                checkUpgrade(false);
                break;
            case R.id.nav_logout:
                MobclickAgent.onEvent(this, "clickSlidmenu_logout");
                closeSlidmenu();
                if (null != mApplication.user && null != mApplication.user.getLoginToken()) {
                    mApplication.user.getLoginToken().setExpires(0);
                    mApplication.saveProfile();
                    mApplication.user = null;
                    userCover.setImageResource(R.mipmap.ic_usercover_default);
                    userCover.setTag(null);
                    userName.setText("未登录");
                    userLevel.setText("点击头像登录");
                    item.setVisible(false);
                }
                break;
        }
        return true;
    }

    protected void checkUpgrade(boolean quilte) {
        if (null == appUpdate) {
            appUpdate = AppUpdateService.getAppUpdate(BaseActivity.this);
        }
        String url = getString(R.string.interface_domainName) + getString(R.string.interface_checkupgread);
        url = url + "&pushMan=" + BuildConfig.FLAVOR;
        if (quilte) {
            appUpdate.checkLatestVersionQuiet(url, new MyJsonParser());
        } else {
            appUpdate.checkLatestVersion(url, new MyJsonParser());
        }
    }

    /**
     * 调用登录界面
     *
     * @param requestId 请求Id
     */
    public void callLogin(int requestId) {
        Intent intent = new Intent(this, LoginActivity.class);
        startActivityForResult(intent, requestId);
    }


    public void showMessage(String msg, String action, View.OnClickListener listener) {
        Snackbar snackbar = Snackbar.make(mToolbar, msg, Snackbar.LENGTH_LONG);
        if (null != action) {
            snackbar.setAction(action, listener).setActionTextColor(getResources().getColor(R.color.colorAccent));
        }
        snackbar.show();
    }

    public void showWarning(String msg, String action, View.OnClickListener listener) {
        Snackbar snackbar = Snackbar.make(mToolbar, msg, Snackbar.LENGTH_LONG);
        if (null != action) {
            snackbar.setAction(action, listener).setActionTextColor(getResources().getColor(R.color.colorWarning));
        }
        snackbar.show();
    }

    public void showError(String msg, String action, View.OnClickListener listener) {
        Snackbar snackbar = Snackbar.make(mToolbar, msg, Snackbar.LENGTH_LONG);
        if (null != action && !action.isEmpty()) {
            snackbar.setAction(action, listener).setActionTextColor(getResources().getColor(R.color.colorError));
        }
        snackbar.show();
    }

    class MyJsonParser extends SimpleJSONParser implements ResponseParser {
        @Override
        public Version parser(String response) {
            try {
                JSONTokener jsonParser = new JSONTokener(response);
                JSONObject json = (JSONObject) jsonParser.nextValue();
                Version version = null;
                if (json.has("state") && json.has("dataObject")) {
                    JSONObject dataField = json.getJSONObject("dataObject");
                    int code = dataField.getInt("code");
                    String name = dataField.getString("name");
                    String feature = dataField.getString("feature");
                    String targetUrl = dataField.getString("targetUrl");
                    version = new Version(code, name, feature, targetUrl);
                }
                return version;
            } catch (JSONException exp) {
                exp.printStackTrace();
                return null;
            }
        }
    }

    public void toggleSoftKeyPad() {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
    }


}
