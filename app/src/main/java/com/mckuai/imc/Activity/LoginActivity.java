package com.mckuai.imc.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.AppCompatTextView;
import android.view.View;

import com.mckuai.imc.Base.BaseActivity;
import com.mckuai.imc.Bean.MCUser;
import com.mckuai.imc.R;
import com.mckuai.imc.Util.MCNetEngine;
import com.mckuai.imc.Util.QQLoginListener;
import com.tencent.tauth.Tencent;
import com.umeng.analytics.MobclickAgent;

import io.rong.imlib.RongIMClient;


/**
 * A login screen that offers login via email/password.
 */
public class LoginActivity extends BaseActivity implements View.OnClickListener, QQLoginListener.OnQQResponseListener,MCNetEngine.OnLoginServerResponseListener {


    private final String TAG = "LoginActivity";

    private AppCompatTextView tv_title;

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
    }

    private void logoutQQ() {
        if (null != mTencent) {
            mTencent.logout(LoginActivity.this);
        }
    }


    private void loginToQQ() {
        MobclickAgent.onEvent(this, "qqLogin");
        if (null == mTencent) {
            mTencent = Tencent.createInstance("101155101", getApplicationContext());
        }
        if (!mTencent.isSessionValid()) {
            mQQListener = new QQLoginListener(this, mTencent, this);
            mTencent.login(this, "all", mQQListener);
        }
    }

    private void loginToMC(MCUser user){
        mApplication.netEngine.loginServer(this, user, this);
    }

    public void loginIM() {
        mApplication.loginIM(new RongIMClient.ConnectCallback() {
            @Override
            public void onTokenIncorrect() {
                showMessage("登录融云时失败，令牌无效，请重新登录！", null, null);
                isFullLoginNeed = true;
            }

            @Override
            public void onSuccess(String s) {
                handleResult(true);
            }

            @Override
            public void onError(RongIMClient.ErrorCode errorCode) {
                showMessage("登录聊天服务器失败，原因：" + errorCode.getMessage(), null, null);
                handleResult(false);
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
        mApplication.user.clone(user);
        mApplication.saveProfile();
        loginIM();
        //handleResult(true);
    }

    @Override
    public void onLoginFailure(String msg) {
        if (null != mTencent){
            mTencent.logout(this);
            showMessage("登录服务器时失败！" + msg, null, null);
        }
    }

    @Override
    public void onQQLoginSuccess( MCUser user) {
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
        if (null != msg) {
            showMessage("登录到QQ失败，原因:"+msg,null,null);
        }
    }
}

