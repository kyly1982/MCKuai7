package com.mckuai.imc.Base;

import android.app.Fragment;
import android.app.FragmentManager;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.mckuai.imc.R;

public class BaseActivity extends AppCompatActivity {

    public MCKuai mApplication = MCKuai.instence;
    public FragmentManager mFragmentManager;
    public android.support.v4.app.FragmentManager mFragmentManager_V4;
    public Toolbar mToolbar;
    private DrawerLayout mDrawer;



    @Override
    protected void onResume() {
        super.onResume();
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
            if (null != navigationOnClickListener) {
                mToolbar.setNavigationOnClickListener(navigationOnClickListener);
            }
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

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, mDrawer, mToolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        mDrawer.setDrawerListener(toggle);
        toggle.syncState();

        if (null != listener) {
            navigationView.setNavigationItemSelectedListener(listener);
        }
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
        if (mDrawer.isDrawerOpen(GravityCompat.START)) {
            mDrawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    /**
     * 调用登录界面
     *
     * @param requestId 请求Id
     */
    public void callLogin(int requestId) {

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

}
