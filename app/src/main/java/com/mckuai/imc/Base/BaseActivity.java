package com.mckuai.imc.Base;

import android.app.Fragment;
import android.app.FragmentManager;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
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

import com.mckuai.imc.Activity.LoginActivity;
import com.mckuai.imc.Activity.ProfileEditerActivity;
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
import com.umeng.socialize.ShareAction;
import com.umeng.socialize.UMShareAPI;
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

    protected ArrayList<BaseFragment> fragments;
    protected int currentFragmentIndex = -1;
    private ImageLoader loader = ImageLoader.getInstance();
    private boolean isSlidingMenuShow = false;
    private UMShareAPI mShareAPI = UMShareAPI.get(this);
    private AppUpdate appUpdate;


    @Override
    protected void onResume() {
        super.onResume();
        if (mApplication.isLogin()) {

        }

    }

    @Override
    protected void onPause() {
        super.onPause();

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
                if (mApplication.isLogin()) {
                    Intent intent;
                    intent = new Intent(BaseActivity.this, UserCenterActivity.class);
                    intent.putExtra(getString(R.string.usercenter_tag_userid), mApplication.user.getId());
                    startActivity(intent);
                } else {
                    callLogin(0);
                }
            }
        });


        Menu menu = navigationView.getMenu();
        logout = menu.findItem(R.id.nav_logout);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, mDrawer, mToolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close) {
            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
            }

            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);

            }

            @Override
            public void onDrawerStateChanged(int newState) {
                super.onDrawerStateChanged(newState);
                if (mApplication.isLogin()) {
                    String url = (String) userCover.getTag();
                    if (null == url || !url.equals(mApplication.user.getHeadImg())) {
                        handleUserLogin();
                    }
                } else {
                    handleUserLogout();
                }
            }
        };
        mDrawer.setDrawerListener(toggle);
        toggle.syncState();
        navigationView.setNavigationItemSelectedListener(this);
    }

    public void share(String title, String content, String url, UMImage image) {
        SHARE_MEDIA[] displayList = new SHARE_MEDIA[]{
                SHARE_MEDIA.WEIXIN_CIRCLE, SHARE_MEDIA.WEIXIN, SHARE_MEDIA.QQ, SHARE_MEDIA.QZONE
        };
        ShareAction action = new ShareAction(this).setDisplayList(displayList);
        action.withTitle(title);
        action.withText(content);
        action.withTargetUrl(url);
        if (null != image) {
            action.withMedia(image);
        }
        action.open();
    }

    private void handleUserLogin() {
        loader.displayImage(mApplication.user.getHeadImg(), userCover, mApplication.getCircleOptions());
        userName.setText(mApplication.user.getNike());
        userLevel.setText(mApplication.user.getLevel() + "");
        userCover.setTag(mApplication.user.getHeadImg());
        logout.setVisible(true);
    }

    private void handleUserLogout() {
        userName.setText("未登录");
        userLevel.setText("");
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


    @Override
    public void onBackPressed() {
        if (null != mDrawer) {
            if (mDrawer.isDrawerOpen(GravityCompat.START)) {
                mDrawer.closeDrawer(GravityCompat.START);
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
                if (mApplication.isLogin()) {
                    intent = new Intent(this, ProfileEditerActivity.class);
                    startActivity(intent);
                } else {
                    showMessage("你还未登录", null, null);
                }
                break;
            case R.id.nav_share:
                share("麦块", "我正在玩麦块，你也一起来玩吧", "http://www.mckuai.com", null);
                break;
            case R.id.nav_prise:
                Uri uri = Uri.parse("market://details?id=" + getPackageName());
                intent = new Intent(Intent.ACTION_VIEW, uri);
                try {
                    startActivity(intent);
                } catch (ActivityNotFoundException e) {
                    showMessage("你还未安装市场", null, null);
                }
                break;
            case R.id.checkUpgread:
                checkUpgrade(false);
                break;
            case R.id.nav_logout:
                if (null != mApplication.user && null != mApplication.user.getLoginToken()) {
                    mApplication.user.getLoginToken().setExpires(0);
                    mApplication.saveProfile();
                    mApplication.user = null;
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
            snackbar.setAction(action, listener).setActionTextColor(getResources().getColor(R.color.colorPrimary));
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


}
