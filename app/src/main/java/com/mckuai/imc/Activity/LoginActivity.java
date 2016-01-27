package com.mckuai.imc.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.AppCompatTextView;
import android.view.View;
import android.widget.TextView;

import com.google.gson.Gson;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.mckuai.imc.Base.BaseActivity;
import com.mckuai.imc.Bean.MCUser;
import com.mckuai.imc.Bean.Token;
import com.mckuai.imc.R;
import com.mckuai.imc.Util.MCNetEngine;
import com.mckuai.imc.Util.QQLoginListener;
import com.tencent.tauth.Tencent;
import com.umeng.analytics.MobclickAgent;

import org.apache.http.Header;
import org.json.JSONObject;


/**
 * A login screen that offers login via email/password.
 */
public class LoginActivity extends BaseActivity implements View.OnClickListener, QQLoginListener.OnQQResponseListener,MCNetEngine.OnLoginServerResponseListener {


    private final String TAG = "LoginActivity";

    private AppCompatTextView tv_title;

    private static Tencent mTencent;
    private QQLoginListener mQQListener;
    String title = "登录";

//    UMSocialService mController = UMServiceFactory.getUMSocialService("com.umeng.login");

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
        mApplication.netEngine.loginServer(this,user,this);
    }

    /*private void loginToMC(Token token, MCUser user) {
        final String url = getString(R.string.interface_domainName) + getString(R.string.interface_login);
        final RequestParams params = new RequestParams();
        // 添加参数
        params.put("accessToken", token.getToken());
        params.put("openId", user.getName());
        params.put("nickName", user.getNike());
        params.put("gender", user.getGender());
        params.put("headImg", user.getHeadImg());
        mClint.get(url, params, new JsonHttpResponseHandler() {

            @Override
            public void onStart() {
                // TODO Auto-generated method stub
                super.onStart();
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                // TODO Auto-generated method stub
                super.onSuccess(statusCode, headers, response);
                Gson gson = new Gson();
                MCUser tempUser = null;
                if (response.has("state")) {
                    try {
                        if (response.getString("state").equalsIgnoreCase("ok")) {
                            // 开始解析
                            tempUser = gson.fromJson(response.getString("dataObject"), MCUser.class);
                        }
                    } catch (Exception e) {
                        // TODO: handle exception
                        return;
                    }
                    if (null != tempUser) {
                        tempUser.setGender(mApplication.user.getGender());
                        mApplication.user.clone(tempUser);
                    }
                    mApplication.saveProfile();
                    handleResult(true);
                    return;
                }
                logoutQQ();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                // TODO Auto-generated method stub
                super.onFailure(statusCode, headers, responseString, throwable);
                logoutQQ();
            }
        });
    }*/

    private void handleResult(Boolean result) {
        setResult(true == result ? RESULT_OK : RESULT_CANCELED);
        this.finish();
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.login_qqlogin:
                if (null != mApplication.user && mApplication.user.isUserTokenValid()) {
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
        handleResult(true);
    }

    @Override
    public void onLoginFalse(String msg) {
        if (null != mTencent){
            mTencent.logout(this);
            showMessage("登录失败！",null,null);
        }
    }

    @Override
    public void onQQLoginSuccess( MCUser user) {
        if (null == mApplication.user){
            mApplication.user = user;
        } else {
            mApplication.user.clone(user);
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

