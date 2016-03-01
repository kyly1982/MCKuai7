package com.mckuai.imc.Activity;

import android.app.FragmentTransaction;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.widget.AppCompatRadioButton;
import android.support.v7.widget.AppCompatTextView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewConfiguration;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.mckuai.imc.Base.BaseActivity;
import com.mckuai.imc.Base.BaseFragment;
import com.mckuai.imc.Bean.User;
import com.mckuai.imc.Fragment.MainFragment_Cartoon;
import com.mckuai.imc.Fragment.MainFragment_Chat;
import com.mckuai.imc.Fragment.MainFragment_Community;
import com.mckuai.imc.Fragment.MainFragment_Mine;
import com.mckuai.imc.R;
import com.mckuai.imc.Util.MCNetEngine;
import com.umeng.analytics.MobclickAgent;
import com.umeng.message.PushAgent;

import java.lang.reflect.Field;
import java.util.ArrayList;

import io.rong.imkit.RongIM;
import io.rong.imlib.RongIMClient;
import io.rong.imlib.model.UserInfo;

public class MainActivity extends BaseActivity implements View.OnClickListener, RadioGroup.OnCheckedChangeListener, BaseFragment.OnFragmentEventListener, MCNetEngine.OnLoadUserInfoResponseListener, RongIMClient.ConnectionStatusListener, RongIM.UserInfoProvider {
    private RadioGroup nav;
    private RelativeLayout content;
    private AppCompatTextView title;
    private RadioGroup cartoonType;
    private AppCompatRadioButton mNewType;


    private boolean isCheckUpgread = false;
    private boolean isIMListenerInit = false;
    private long lastBlckPressTime = 0;
    private boolean isRecommendPressed = false;

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
    }

    private void initView() {
        nav = (RadioGroup) findViewById(R.id.nav);
        title = (AppCompatTextView) findViewById(R.id.actionbar_title);
        cartoonType = (RadioGroup) findViewById(R.id.actionbar_cartoon_rg);
        mNewType = (AppCompatRadioButton) findViewById(R.id.cartoon_type_new);

        findViewById(R.id.nav_create).setOnClickListener(this);
        cartoonType.setVisibility(View.VISIBLE);
        nav.setVisibility(View.VISIBLE);
        nav.setOnCheckedChangeListener(this);
        cartoonType.setOnCheckedChangeListener(this);
    }

    private void initFragment() {
        if (null == fragments) {
            MainFragment_Cartoon cartoonFragment = new MainFragment_Cartoon();
            MainFragment_Chat chatFragment = new MainFragment_Chat();
            MainFragment_Community communityFragment = new MainFragment_Community();
            MainFragment_Mine mineFragment = new MainFragment_Mine();

            cartoonFragment.setFragmentEventListener(this);

            fragments = new ArrayList<>(4);
            fragments.add(cartoonFragment);
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
            RongIM.getInstance().getRongIMClient().setConnectionStatusListener(this);
            isIMListenerInit = true;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public void onBackPressed() {
        if (!isSlidingMenuShow) {
            if (System.currentTimeMillis() - lastBlckPressTime < 3000) {
                mApplication.handleExit();
                //MobclickAgent.onKillProcess(MainActivity.this);
                System.exit(0);
            } else {
                lastBlckPressTime = System.currentTimeMillis();
                showMessage("再次点击退出软件", null, null);
            }
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_recommend) {
            if (!isRecommendPressed) {
                item.setIcon(R.drawable.ic_menu_recommend);
                isRecommendPressed = true;
            }
            Intent intent = new Intent(this, RecommendActivity.class);
            startActivity(intent);
            return true;
        }

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
        switch (checkedId) {

            case R.id.cartoon_type_hot:
                if (null != fragments) {
                    ((MainFragment_Cartoon) fragments.get(0)).setType(1);
                }
                break;
            case R.id.cartoon_type_new:
                if (null != fragments) {
                    ((MainFragment_Cartoon) fragments.get(0)).setType(0);
                }
                break;
            default:
                if (null != fragments && !fragments.isEmpty()) {
                    FragmentTransaction transaction = mFragmentManager.beginTransaction();
                    if (0 <= currentFragmentIndex) {
                        transaction.hide(fragments.get(currentFragmentIndex));
                    }
                    switch (checkedId) {
                        case R.id.nav_cartoon:
                            MobclickAgent.onEvent(this, "clickCartoon");
                            cartoonType.setVisibility(View.VISIBLE);
                            title.setVisibility(View.GONE);
                            currentFragmentIndex = 0;
                            break;
                        case R.id.nav_chat:
                            MobclickAgent.onEvent(this, "clickChat");
                            cartoonType.setVisibility(View.GONE);
                            title.setVisibility(View.VISIBLE);
                            title.setText("聊天");
                            currentFragmentIndex = 1;
                            break;
                        case R.id.nav_community:
                            MobclickAgent.onEvent(this, "clickForum");
                            cartoonType.setVisibility(View.GONE);
                            title.setVisibility(View.VISIBLE);
                            title.setText("社区");
                            currentFragmentIndex = 2;
                            break;
                        case R.id.nav_mine:
                            MobclickAgent.onEvent(this, "clickMine");
                            cartoonType.setVisibility(View.GONE);
                            title.setVisibility(View.VISIBLE);
                            title.setText("我的");
                            currentFragmentIndex = 3;
                            break;
                    }
                    transaction.show(fragments.get(currentFragmentIndex)).commit();
                }
                break;
        }
    }



    @Override
    public void onFragmentAttach(int titleResId) {
      /*  if (0 == fragmentIndex && titleResId == R.string.fragment_main_cartoon){
            mFragmentManager.beginTransaction().show(fragments.get(0)).commit();
        }*/
        switch (titleResId) {
            case R.string.fragment_cartoon:
                mNewType.setChecked(true);
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
    public void onFragmentAction(Object object) {

    }

    @Override
    public void onLoadUserInfoFailure(String msg) {
        showMessage("获取用户信息失败，原因：" + msg, null, null);
    }

    @Override
    public void onLoadUserInfoSuccess(User user) {
        UserInfo userInfo = new UserInfo(user.getName(), user.getNickEx(), Uri.parse(user.getHeadImage()));
    }

    @Override
    public void onChanged(ConnectionStatus connectionStatus) {
        switch (connectionStatus) {
            case CONNECTED:
                //Toast.makeText(MainActivity.this, "已连接上聊天服务器！", Toast.LENGTH_SHORT).show();
                break;
            case DISCONNECTED:
                Toast.makeText(MainActivity.this, "聊天服务器断开！", Toast.LENGTH_SHORT).show();
                break;
            case CONNECTING:
                //Toast.makeText(MainActivity.this, "正在连接...", Toast.LENGTH_SHORT).show();
                break;
            case NETWORK_UNAVAILABLE:
                Toast.makeText(MainActivity.this, "网络不可用！", Toast.LENGTH_SHORT).show();
                break;
            case KICKED_OFFLINE_BY_OTHER_CLIENT:
                Toast.makeText(MainActivity.this, "账号在其它设备上登录！", Toast.LENGTH_SHORT).show();
                break;
        }
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
                    UserInfo userInfo = new UserInfo(user.getName(), user.getNickEx(), Uri.parse(user.getHeadImage()));
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

}
