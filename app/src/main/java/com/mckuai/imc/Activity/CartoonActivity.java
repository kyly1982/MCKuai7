package com.mckuai.imc.Activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.AppCompatEditText;
import android.support.v7.widget.AppCompatTextView;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.mckuai.imc.Base.BaseActivity;
import com.mckuai.imc.Bean.Cartoon;
import com.mckuai.imc.Bean.Comment;
import com.mckuai.imc.Bean.User;
import com.mckuai.imc.R;
import com.mckuai.imc.Util.MCNetEngine;
import com.mckuai.imc.Widget.CartoonView;
import com.umeng.socialize.media.UMImage;

import java.util.ArrayList;

import io.rong.imkit.RongIM;
import io.rong.imlib.RongIMClient;
import io.rong.imlib.model.Conversation;
import io.rong.imlib.model.Message;
import io.rong.message.TextMessage;

public class CartoonActivity extends BaseActivity implements CartoonView.OnCartoonElementClickListener, View.OnClickListener, MCNetEngine.OnRewardCartoonResponseListener, MCNetEngine.OnCommentCartoonResponseListener, MCNetEngine.OnLoadCartoonDetailResponseListener {
    private Cartoon cartoon;
    private AppCompatEditText commentEditer;
    private RelativeLayout commentLayout;
    private CartoonView cartoonView;
    private AppCompatTextView title;

    private boolean isRefreshNeed = true;
    private boolean isReawrdSuccess = false;
    private boolean isCommentSuccess = false;
    boolean isUpdateComment = false;
    boolean isUpdateReawrd = false;
    boolean isCommentMode = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cartoon);
        initToolbar(R.id.toolbar, 0, null);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (null == cartoon){
           cartoon = (Cartoon) getIntent().getExtras().getSerializable(getString(R.string.cartoondetail_tag_cartoon));
            isCommentMode = getIntent().getBooleanExtra("COMMENTCAROON",false);
            initView();
        }
        if (isRefreshNeed) {
            showData();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK){
            switch (requestCode){
                case 1:
                    commentCartoon();
                    break;
                case 2:
                    rewardCartoon();
                    break;
            }
        } else {
            switch (requestCode) {
                case 1:
                    showMessage("未登录不能评论！", "登录", new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            callLogin(1);
                        }
                    });
                    break;
                case 2:
                    showMessage("未登录不能赞！", "登录", new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            callLogin(2);
                        }
                    });
                    break;
            }
        }
    }

    private void showData() {
        if (null != cartoon) {
            if (null == cartoon.getOwner()) {
                mApplication.netEngine.loadCartoonDetail(this, cartoon.getId(), this);
                return;
            }
            cartoonView.setData(cartoon, true);
        }
        isRefreshNeed = false;
        if (isCommentMode){
            showCommentLayout();
        }
    }

    private void initView(){
        cartoonView = (CartoonView) findViewById(R.id.cartoon);
        commentEditer = (AppCompatEditText) findViewById(R.id.commentediter);
        commentLayout = (RelativeLayout) findViewById(R.id.comment_layout);
        findViewById(R.id.commentcartoon).setOnClickListener(this);
        title = (AppCompatTextView) findViewById(R.id.actionbar_title);
        cartoonView.setOnCartoonElementClickListener(this);
        commentEditer.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEND) {
                    if (0 < commentEditer.getText().length()) {
                        commentCartoon();
                        return true;
                    }
                }
                return false;
            }
        });
        title.setText("详情");
    }

    private void commentCartoon(){
        if (!isUpdateComment) {
            isUpdateComment = true;
            mApplication.hideSoftKeyboard(commentEditer);
            String comtent = commentEditer.getText().toString();
            if (null != comtent && !comtent.isEmpty()) {
                if (mApplication.isLogin()) {
                    mApplication.netEngine.commentCartoon(this,mApplication.user.getId() ,cartoon.getId(), comtent, this);
                } else {
                    callLogin(1);
                }
            }
        }
    }

    private void rewardCartoon(){
        if (!isUpdateReawrd) {
            if (mApplication.isLogin()) {
                isUpdateReawrd = true;
                mApplication.netEngine.rewardCartoon(this,mApplication.user.getId(), cartoon, this);
            } else {
                callLogin(2);
            }
        }
    }

    private void showCommentLayout(){
        commentLayout.setVisibility(View.VISIBLE);
        commentEditer.setFocusable(true);
        commentEditer.setFocusableInTouchMode(true);
    }

    private void hideCommentLayout(){
        commentLayout.setVisibility(View.GONE);
    }

    @Override
    public void onCommentCartoon(Cartoon cartoon) {
        //输入评论
        if (commentLayout.getVisibility() == View.GONE) {
            showCommentLayout();
        } else {
            hideCommentLayout();
        }
    }

    @Override
    public void onOwnerClicked(int ownerId) {
        //显示作者的个人中心
        if (ownerId > 0) {
            Intent intent = new Intent(this, UserCenterActivity.class);
            intent.putExtra(getString(R.string.usercenter_tag_userid),ownerId);
            startActivity(intent);
        }
    }

    @Override
    public void onShareCartoon(Cartoon cartoon) {
        //分享
        if (null != cartoon) {
            UMImage image = new UMImage(this, cartoon.getImage());
            share("麦块漫画", "我刚在《麦块我的世界盒子》上发现一个在非常有意思的漫画，跟我来膜拜大神！", getString(R.string.shareCartoon) + cartoon.getId(), image);
        }
    }

    @Override
    public void onRewardCartoon(Cartoon cartoon) {
        //打赏
        if (mApplication.isLogin()){
            rewardCartoon();
        } else {
            callLogin(1);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.commentcartoon:
                if (mApplication.isLogin()){
                    commentCartoon();
                } else callLogin(0);
                break;
        }
    }

    @Override
    public void onCommentCartoonSuccess() {
        isUpdateComment = false;
        cartoon.setReplyNum(cartoon.getReplyNum() + 1);
        Comment comment = new Comment(new User(mApplication.user), commentEditer.getText().toString());
        if (null == cartoon.getComments()) {
            ArrayList<Comment> comments = new ArrayList<>(1);
            cartoon.setComments(comments);
        }
        cartoon.getComments().add(comment);
        isCommentSuccess = true;
        if (null != RongIM.getInstance() && null != RongIM.getInstance().getRongIMClient()) {
            TextMessage message = TextMessage.obtain(commentEditer.getText().toString());
            RongIM.getInstance().getRongIMClient().sendMessage(Conversation.ConversationType.PRIVATE, cartoon.getOwner().getName(), message, mApplication.user.getNike() + "评论了你的作品，快来看看吧！", "", new RongIMClient.SendMessageCallback() {
                @Override
                public void onError(Integer integer, RongIMClient.ErrorCode errorCode) {

                }

                @Override
                public void onSuccess(Integer integer) {

                }
            }, new RongIMClient.ResultCallback<Message>() {
                @Override
                public void onSuccess(Message message) {

                }

                @Override
                public void onError(RongIMClient.ErrorCode errorCode) {

                }
            });
        }
        showData();
        commentEditer.setText("");
        hideCommentLayout();
    }

    @Override
    public void onCommentCartoonFailure(String msg) {
        isUpdateComment = false;
        Snackbar.make(commentEditer,"评论失败，原因："+msg,Snackbar.LENGTH_SHORT).show();
    }

    @Override
    public void onRewardCartoonSuccess() {
        isUpdateReawrd = false;
        //Snackbar.make(commentEditer,"打赏成功！",Snackbar.LENGTH_SHORT).show();
        cartoon.setPrise(cartoon.getPrise() + 1);
        isReawrdSuccess = true;
        if (null != RongIM.getInstance() && null != RongIM.getInstance().getRongIMClient()) {
            TextMessage message = TextMessage.obtain("你的《" + cartoon.getContent() + "》真是太棒了，赞一个！");
            RongIM.getInstance().getRongIMClient().sendMessage(Conversation.ConversationType.PRIVATE, cartoon.getOwner().getName(), message, mApplication.user.getNike() + "给你的漫画点了赞！", "", new RongIMClient.SendMessageCallback() {
                @Override
                public void onError(Integer integer, RongIMClient.ErrorCode errorCode) {

                }

                @Override
                public void onSuccess(Integer integer) {

                }
            }, new RongIMClient.ResultCallback<Message>() {
                @Override
                public void onSuccess(Message message) {

                }

                @Override
                public void onError(RongIMClient.ErrorCode errorCode) {

                }
            });
        }
        showData();
    }

    @Override
    public void onRewardaCartoonFailure(String msg) {
        isUpdateReawrd = false;
        Snackbar.make(commentEditer,"打赏失败，原因："+msg,Snackbar.LENGTH_SHORT).show();
    }

    @Override
    public void onLoadDetailFailure(String msg) {
        showMessage(msg, null, null);
    }

    @Override
    public void onLoadDetailSuccess(Cartoon cartoon) {
        this.cartoon = cartoon;
        isRefreshNeed = true;
        showData();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Intent intent = new Intent();
        if (isReawrdSuccess) {
            intent.putExtra("REWARD", 1);
        }
        if (isCommentSuccess) {
            intent.putExtra("COMMENT", 1);
        }
        if (isCommentSuccess || isReawrdSuccess) {
            setResult(Activity.RESULT_OK, intent);
        } else {
            setResult(Activity.RESULT_CANCELED);
        }
    }
}
