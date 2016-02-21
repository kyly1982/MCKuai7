package com.mckuai.imc.Activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.widget.AppCompatTextView;
import android.view.View;

import com.mckuai.imc.Base.BaseActivity;
import com.mckuai.imc.Bean.User;
import com.mckuai.imc.R;
import com.mckuai.imc.Util.MCDaoHelper;
import com.mckuai.imc.Util.MCNetEngine;

import java.util.Locale;

import io.rong.imkit.fragment.ConversationFragment;
import io.rong.imlib.model.Conversation;

public class ConversationActivity extends BaseActivity implements MCNetEngine.OnLoadUserInfoResponseListener {
    private MCDaoHelper daoHelper;
    private AppCompatTextView title;

    /**
     * 目标 Id
     */
    private String mTargetId;

    /**
     * 刚刚创建完讨论组后获得讨论组的id 为targetIds，需要根据 为targetIds 获取 targetId
     */
    // private String mTargetIds;

    /**
     * 会话类型
     */
    private Conversation.ConversationType mConversationType;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.conversation);
        initToolbar(R.id.toolbar, 0, null);
        title = (AppCompatTextView) findViewById(R.id.actionbar_title);
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        daoHelper = mApplication.daoHelper;
        Intent intent = getIntent();
        getIntentDate(intent);
    }


    /**
     * 展示如何从 Intent 中得到 融云会话页面传递的 Uri
     */
    private void getIntentDate(Intent intent) {

        mTargetId = intent.getData().getQueryParameter("targetId");
        //mTargetIds = intent.getData().getQueryParameter("targetIds");
        //intent.getData().getLastPathSegment();//获得当前会话类型
        getUser();
        mConversationType = Conversation.ConversationType.valueOf(intent.getData().getLastPathSegment().toUpperCase(Locale.getDefault()));

        enterFragment(mConversationType, mTargetId);
    }

    private void getUser() {
        if (null != mTargetId) {
            User user = daoHelper.getUserByName(mTargetId);
            if (null != user) {
                //从网络获取用户信息
                mApplication.netEngine.loadUserInfo(this, mTargetId, this);
            } else {
                showUser(user);
            }
        }
    }

    private void showUser(User user) {
        title.setText(user.getNickEx());
    }

    /**
     * 加载会话页面 ConversationFragment
     *
     * @param mConversationType 会话类型
     * @param mTargetId         目标 Id
     */
    private void enterFragment(Conversation.ConversationType mConversationType, String mTargetId) {

        ConversationFragment fragment = (ConversationFragment) getSupportFragmentManager().findFragmentById(R.id.conversation);

        Uri uri = Uri.parse("rong://" + getApplicationInfo().packageName).buildUpon()
                .appendPath("conversation").appendPath(mConversationType.getName().toLowerCase())
                .appendQueryParameter("targetId", mTargetId).build();

        fragment.setUri(uri);
    }

    @Override
    public void onLoadUserInfoSuccess(User user) {
        showUser(user);
    }

    @Override
    public void onLoadUserInfoFailure(String msg) {
        title.setText("未知");
        showMessage(msg, null, null);
    }
}
