package com.mckuai.imc.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.AppCompatTextView;
import android.view.View;

import com.mckuai.imc.Base.BaseActivity;
import com.mckuai.imc.Base.MCKuai;
import com.mckuai.imc.Bean.MCUser;
import com.mckuai.imc.R;
import com.mckuai.imc.Util.MCNetEngine;
import com.mckuai.imc.Util.QQLoginListener;
import com.tencent.tauth.Tencent;
import com.umeng.analytics.MobclickAgent;


/**
 * A login screen that offers login via email/password.
 */
public class LoginActivity extends BaseActivity implements View.OnClickListener, QQLoginListener.OnQQResponseListener,MCNetEngine.OnLoginServerResponseListener {


    private final String TAG = "LoginActivity";

    private AppCompatTextView tv_title;
    private AppCompatTextView loginMsg;

    private static Tencent mTencent;
    private QQLoginListener mQQListener;
    String title = "登录";
    private boolean isFullLoginNeed = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        initToolbar(R.id.toolbar, 0, this);
        mTencent = Tencent.createInstance("101155101", getApplicationContext());
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                handleResult(false);
            }
        });
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    protected void onResume() {
        super.onResume();
        MobclickAgent.onPageStart(title);
        initView();
    }

    private void initView() {
        tv_title = (AppCompatTextView) findViewById(R.id.actionbar_title);
        tv_title.setText(title);
        findViewById(R.id.login_qqlogin).setOnClickListener(this);
        findViewById(R.id.login_anonymous).setOnClickListener(this);
        loginMsg = (AppCompatTextView) findViewById(R.id.loginmsg);
    }

    private void logoutQQ() {
        if (null != mTencent) {
            mTencent.logout(LoginActivity.this);
        }
    }


    private void loginToQQ() {
        loginMsg.setText("获取账号信息...");
        MobclickAgent.onEvent(this, "qqLogin");
        if (null == mTencent) {
            mTencent = Tencent.createInstance("101155101", getApplicationContext());
        } else if (mTencent.isSessionValid()) {
            mTencent.logout(getApplicationContext());
        }
        if (!mTencent.isSessionValid()) {
            mQQListener = new QQLoginListener(this, mTencent, this);
            mTencent.login(this, "all", mQQListener);
        }
    }

    private void loginToMC(MCUser user){
        MobclickAgent.onEvent(this, "login");
        loginMsg.setText("登录服务器...");
        mApplication.netEngine.loginServer(this, user, this);
    }

    public void loginIM() {
        MobclickAgent.onEvent(this, "loginChatServer");
        loginMsg.setText("登录聊天服务器...");
        mApplication.loginIM(new MCKuai.IMLoginListener() {
            @Override
            public void onInitError() {
                MobclickAgent.onEvent(LoginActivity.this, "chatLogin_F");
                loginMsg.setText("聊天模块功能异常，请重启软件！");
            }

            @Override
            public void onTokenIncorrect() {
                MobclickAgent.onEvent(LoginActivity.this, "chatLogin_F");
                loginMsg.setText("登录聊天服务器时失败，原因:令牌无效！");
                showMessage("是否重新登录？", "重新登录", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mApplication.logout();
                        isFullLoginNeed = true;
                        onClick(findViewById(R.id.login_qqlogin));
                    }
                });
        }

            @Override
            public void onLoginFailure(String msg) {
                MobclickAgent.onEvent(LoginActivity.this, "chatLogin_F");
                loginMsg.setText("登录聊天服务器失败，原因：" + msg);
            }

            @Override
            public void onLoginSuccess(String msg) {
                handleResult(true);
                MobclickAgent.onEvent(LoginActivity.this, "chatLogin_S");
            }
        });

    }
    private void handleResult(Boolean result) {
        setResult(true == result ? RESULT_OK : RESULT_CANCELED);
        this.finish();
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.login_qqlogin:
                if (null != mApplication.user && mApplication.user.isUserTokenValid() && !isFullLoginNeed) {
                    loginToMC( mApplication.user);
                } else {
                    loginToQQ();
                }
                break;
            default:
                handleResult(false);
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Tencent.onActivityResultData(requestCode, resultCode, data, mQQListener);
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onLoginSuccess(MCUser user) {
        MobclickAgent.onEvent(this, "login_S");
        mApplication.user.clone(user);
        mApplication.saveProfile();
        loginIM();
    }

    @Override
    public void onLoginFailure(String msg) {
        MobclickAgent.onEvent(this, "login_F");
        if (null != mTencent){
            mTencent.logout(this);
            loginMsg.setText("登录服务器时失败，原因:" + msg);
        }
    }

    @Override
    public void onQQLoginSuccess( MCUser user) {
        MobclickAgent.onEvent(this, "qqLogin_S");
        if (null == mApplication.user){
            mApplication.user = user;
        } else {
            mApplication.user.clone(user);
            mApplication.user.setLoginToken(user.getLoginToken());
        }
        loginToMC(user);
    }

    @Override
    public void onQQLoginFaile(String msg) {
        MobclickAgent.onEvent(this, "qqLogin_F");
        if (null != msg) {
            loginMsg.setText("登录到QQ失败，原因:" + msg);
        }
    }
}

