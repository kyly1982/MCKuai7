package com.mckuai.imc.Activity;

import android.app.FragmentTransaction;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.widget.AppCompatRadioButton;
import android.support.v7.widget.AppCompatTextView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewConfiguration;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;

import com.mckuai.imc.Base.BaseActivity;
import com.mckuai.imc.Base.BaseFragment;
import com.mckuai.imc.Base.MCKuai;
import com.mckuai.imc.Bean.Ad;
import com.mckuai.imc.Bean.User;
import com.mckuai.imc.Fragment.MainFragment_Chat;
import com.mckuai.imc.Fragment.MainFragment_Community;
import com.mckuai.imc.Fragment.MainFragment_Competition;
import com.mckuai.imc.Fragment.MainFragment_Mine;
import com.mckuai.imc.R;
import com.mckuai.imc.Util.MCNetEngine;
import com.mckuai.imc.Widget.ExitDialog;
import com.mckuai.imc.Widget.LeaderDialog;
import com.readystatesoftware.viewbadger.BadgeView;
import com.umeng.analytics.MobclickAgent;
import com.umeng.message.PushAgent;
import com.umeng.socialize.utils.Log;

import java.io.File;
import java.lang.reflect.Field;
import java.util.ArrayList;

import io.rong.imkit.RongIM;
import io.rong.imlib.RongIMClient;
import io.rong.imlib.model.Message;
import io.rong.imlib.model.UserInfo;

public class MainActivity extends BaseActivity implements View.OnClickListener
        , RadioGroup.OnCheckedChangeListener
        , BaseFragment.OnFragmentEventListener
        , RongIM.UserInfoProvider
        , MCNetEngine.OnGetAdResponse
        , RongIMClient.OnReceiveMessageListener {
    private RadioGroup nav;
    private RelativeLayout content;
    private AppCompatTextView title;
    private BadgeView badgeView; //脚标
    //private RadioGroup cartoonType;
    //private AppCompatRadioButton mNewType;
    private AppCompatRadioButton chat;


    private boolean isCheckUpgread = false;
    private boolean isIMListenerInit = false;
    private boolean isRecommendPressed = false;
    private Ad ad;
    private boolean isDownloaded = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initToolbar(R.id.toolbar, 0, null);
        initFragment();
        initDrawer();
        initView();
        //友盟推送
        PushAgent mPushAgent = PushAgent.getInstance(this);
        mPushAgent.enable();
        try {
            ViewConfiguration mconfig = ViewConfiguration.get(this);
            Field menuKeyField = ViewConfiguration.class.getDeclaredField("sHasPermanentMenuKey");
            if (menuKeyField != null) {
                menuKeyField.setAccessible(true);
                menuKeyField.setBoolean(mconfig, false);
            }
        } catch (Exception ex) {
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        mFragmentManager.beginTransaction().show(fragments.get(currentFragmentIndex)).commit();

        if (!isCheckUpgread) {
            checkUpgrade(true);
            isCheckUpgread = true;
        }
        if (!isIMListenerInit) {
            initIMListener();
        }
        if (null == ad) {
            mApplication.netEngine.getAd(this, this);
        }

        if ((MCKuai.instence.leadTag & 1) == 0) {
            LeaderDialog dialog = new LeaderDialog();
            dialog.show(getFragmentManager(), "DIALOG");
            mApplication.setLeadTag(1);
        }

    }

    private void initView() {
        nav = (RadioGroup) findViewById(R.id.nav);
        title = (AppCompatTextView) findViewById(R.id.actionbar_title);
        chat = (AppCompatRadioButton) findViewById(R.id.nav_chat);
        nav.setVisibility(View.VISIBLE);

        findViewById(R.id.nav_create).setOnClickListener(this);
        nav.setOnCheckedChangeListener(this);
        title.setText("暴力PK");
    }

    private void initFragment() {
        if (null == fragments) {
            //MainFragment_Cartoon cartoonFragment = new MainFragment_Cartoon();
            MainFragment_Chat chatFragment = new MainFragment_Chat();
            MainFragment_Community communityFragment = new MainFragment_Community();
            MainFragment_Mine mineFragment = new MainFragment_Mine();
            MainFragment_Competition competition = new MainFragment_Competition();

            //cartoonFragment.setFragmentEventListener(this);

            fragments = new ArrayList<>(4);
            //fragments.add(cartoonFragment);
            fragments.add(competition);
            fragments.add(chatFragment);
            fragments.add(communityFragment);
            fragments.add(mineFragment);

            if (null == mFragmentManager) {
                mFragmentManager = getFragmentManager();
            }
            content = (RelativeLayout) findViewById(R.id.context);
            FragmentTransaction transaction = mFragmentManager.beginTransaction();
            for (BaseFragment fragment : fragments) {
                transaction.add(content.getId(), fragment);
            }
            for (int i = 0; i < 4; i++) {
                transaction.hide(fragments.get(i));
            }
            transaction.commit();
            currentFragmentIndex = 0;
        }
    }

    private void initIMListener() {
        if (RongIM.getInstance() != null && RongIM.getInstance().getRongIMClient() != null) {
            RongIM.setUserInfoProvider(this, true);
            RongIM.setOnReceiveMessageListener(this);
            isIMListenerInit = true;
        }
    }

    private void checkUnReadMsg() {
        if (RongIM.getInstance() != null && RongIM.getInstance().getRongIMClient() != null && RongIM.getInstance().getRongIMClient().getCurrentConnectionStatus() == RongIMClient.ConnectionStatusListener.ConnectionStatus.CONNECTED) {
            switch (RongIM.getInstance().getRongIMClient().getTotalUnreadCount()) {
                case 0:
                    if (null != badgeView) {
                        badgeView.hide();
                    }
                    break;
                default:
                    if (null == badgeView) {
                        badgeView = new BadgeView(this, chat);
                    }
                    badgeView.setText(RongIM.getInstance().getRongIMClient().getTotalUnreadCount() + "");
                    badgeView.show();
                    break;
            }
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public void onBackPressed() {
        if (!isSlidingMenuShow) {
            ExitDialog exitDialog = new ExitDialog();
            MobclickAgent.onEvent(this, "showExitDialog");
            exitDialog.setData(isDownloaded == true ? null : ad, new ExitDialog.OnClickListener() {
                @Override
                public void onCanclePressed() {
                    MobclickAgent.onEvent(MainActivity.this, "ExitDialog_Cancle");
                }

                @Override
                public void onExitPressed() {
                    MobclickAgent.onEvent(MainActivity.this, "ExitDialog_Exit");
                    MobclickAgent.onKillProcess(MainActivity.this);
                    mApplication.handleExit();
                    System.exit(0);
                }

                @Override
                public void onDownloadPressed() {
                    MobclickAgent.onEvent(MainActivity.this, "ExitDialog_Download");
                    MobclickAgent.onKillProcess(MainActivity.this);
                    isDownloaded = true;
                    mApplication.handleExit();
                    System.exit(0);
                }

                @Override
                public void onPicturePressed() {
                    isDownloaded = true;
                    MobclickAgent.onEvent(MainActivity.this, "ExitDialog_Picture");
                }
            });

            exitDialog.show(getFragmentManager(), "exit");
        } else {
            super.onBackPressed();
        }
    }

    private String getDownloadPath() {
        File file = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
        if (!file.exists()) {
            file.mkdirs();
        }
        return file.getPath();
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        onBackPressed();
        /*int id = item.getItemId();
        if (id == R.id.action_recommend) {
            if (!isRecommendPressed) {
                item.setIcon(R.drawable.ic_menu_recommend);
                isRecommendPressed = true;
            }
            Intent intent = new Intent(this, RecommendActivity.class);
            startActivity(intent);
            return true;
        }*/

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.nav_create:
                MobclickAgent.onEvent(this, "clickCreateCartoon");
                Intent intent = new Intent(this, CreateCartoonActivity.class);
                startActivity(intent);
                break;
        }
    }

    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {

        if (null != fragments && !fragments.isEmpty()) {
            FragmentTransaction transaction = mFragmentManager.beginTransaction();
            if (0 <= currentFragmentIndex) {
                transaction.hide(fragments.get(currentFragmentIndex));
            }
            switch (checkedId) {
                case R.id.nav_cartoon:
                    MobclickAgent.onEvent(this, "clickCartoon");
                    title.setVisibility(View.GONE);
                    currentFragmentIndex = 0;
                    break;
                case R.id.nav_chat:
                    MobclickAgent.onEvent(this, "clickChat");
                    title.setVisibility(View.VISIBLE);
                    title.setText("聊天");
                    currentFragmentIndex = 1;
                    break;
                case R.id.nav_community:
                    MobclickAgent.onEvent(this, "clickForum");
                    title.setVisibility(View.VISIBLE);
                    title.setText("社区");
                    currentFragmentIndex = 2;
                    break;
                case R.id.nav_mine:
                    MobclickAgent.onEvent(this, "clickMine");
                    title.setVisibility(View.VISIBLE);
                    title.setText("我的");
                    currentFragmentIndex = 3;
                    break;
            }
            transaction.show(fragments.get(currentFragmentIndex)).commit();
        }
    }


    @Override
    public void onFragmentAttach(int titleResId) {
      /*  if (0 == fragmentIndex && titleResId == R.string.fragment_main_cartoon){
            mFragmentManager.beginTransaction().show(fragments.get(0)).commit();
        }*/
        switch (titleResId) {
            case R.string.fragment_cartoon:
                //mNewType.setChecked(true);
                break;
            case R.string.fragment_chat:
                break;
            case R.string.fragment_community:
                break;
            case R.string.fragment_mine:
                break;
        }
    }

    @Override
    public void onFragmentShow(int titleResId) {
        switch (titleResId) {
            case R.string.fragment_cartoon:
                break;
            case R.string.fragment_chat:
                break;
            case R.string.fragment_community:
                break;
            case R.string.fragment_mine:
                break;
        }
    }

    @Override
    public boolean onReceived(Message message, int i) {
        checkUnReadMsg();
        return false;
    }

    @Override
    public void onFragmentAction(Object object) {

    }


    @Override
    public UserInfo getUserInfo(String id) {
        if (id.equals(mApplication.user.getName())) {
            UserInfo userInfo = new UserInfo(id, mApplication.user.getNike(), Uri.parse(mApplication.user.getHeadImg()));
            return userInfo;
        }
        User user = mApplication.daoHelper.getUserByName(id);
        if (null == user) {
            mApplication.netEngine.loadUserInfo(this, id, new MCNetEngine.OnLoadUserInfoResponseListener() {
                @Override
                public void onLoadUserInfoSuccess(User user) {
                }

                @Override
                public void onLoadUserInfoFailure(String msg) {
                    showMessage(msg, null, null);
                }
            });
            return null;
        } else {
            UserInfo userInfo = new UserInfo(id, user.getNickEx(), Uri.parse(user.getHeadImage()));
            return userInfo;
        }
    }

    @Override
    public void onGetAdFailure(String msg) {
        Log.e(msg);
    }

    @Override
    public void onGetAdSuccess(Ad ad) {
        this.ad = ad;
    }
}
