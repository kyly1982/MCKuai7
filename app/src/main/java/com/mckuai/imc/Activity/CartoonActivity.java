package com.mckuai.imc.Activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.AppCompatEditText;
import android.view.View;
import android.widget.RelativeLayout;

import com.mckuai.imc.Base.BaseActivity;
import com.mckuai.imc.Bean.Cartoon;
import com.mckuai.imc.Bean.Comment;
import com.mckuai.imc.Bean.User;
import com.mckuai.imc.R;
import com.mckuai.imc.Util.MCNetEngine;
import com.mckuai.imc.Widget.CartoonView;
import com.umeng.socialize.media.UMImage;

import java.util.ArrayList;

public class CartoonActivity extends BaseActivity implements CartoonView.OnCartoonElementClickListener, View.OnClickListener, MCNetEngine.OnRewardCartoonResponseListener, MCNetEngine.OnCommentCartoonResponseListener, MCNetEngine.OnLoadCartoonDetailResponseListener {
    private Cartoon cartoon;
    private AppCompatEditText commentEditer;
    private RelativeLayout commentLayout;
    private CartoonView cartoonView;
    private boolean isRefreshNeed = true;
    private boolean isReawrdSuccess = false;
    private boolean isCommentSuccess = false;

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
        //initDrawer();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (null == cartoon){
           cartoon = (Cartoon) getIntent().getExtras().getSerializable(getString(R.string.cartoondetail_tag_cartoon));
            initView();
        }
        if (isRefreshNeed) {
            showData();
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
    }

    private void initView(){
        cartoonView = (CartoonView) findViewById(R.id.cartoon);
        commentEditer = (AppCompatEditText) findViewById(R.id.commentediter);
        commentLayout = (RelativeLayout) findViewById(R.id.comment_layout);
        findViewById(R.id.commentcartoon).setOnClickListener(this);
        cartoonView.setOnCartoonElementClickListener(this);

    }

    private void commentCartoon(){
        String comtent = commentEditer.getText().toString();
        if (null != comtent && !comtent.isEmpty()){
            mApplication.netEngine.commentCartoon(this,cartoon.getId(),comtent,this);
        }
    }

    private void rewardCartoon(){
        mApplication.netEngine.rewardCartoon(this,true,cartoon.getId(),this);
    }

    private void showCommentLayout(){
        commentLayout.setVisibility(View.VISIBLE);
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
            share("麦块漫画来了", "我刚在《麦块我的世界盒子》上发现一个在非常有意思的漫画，跟我来膜拜大神！", getString(R.string.shareCartoon) + cartoon.getId(), image);
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
        Snackbar.make(commentEditer,"评论成功",Snackbar.LENGTH_SHORT).show();
        cartoon.setReplyNum(cartoon.getReplyNum() + 1);
        Comment comment = new Comment(new User(mApplication.user), commentEditer.getText().toString());
        if (null == cartoon.getComments()) {
            ArrayList<Comment> comments = new ArrayList<>(1);
            cartoon.setComments(comments);
        }
        cartoon.getComments().add(comment);
        isCommentSuccess = true;
        commentEditer.setText("");
        showData();
        hideCommentLayout();
    }

    @Override
    public void onCommentCartoonFailure(String msg) {
        Snackbar.make(commentEditer,"评论失败，原因："+msg,Snackbar.LENGTH_SHORT).show();
    }

    @Override
    public void onRewardCartoonSuccess() {
        Snackbar.make(commentEditer,"打赏成功！",Snackbar.LENGTH_SHORT).show();
        cartoon.setPrise(cartoon.getPrise() + 1);
        isReawrdSuccess = true;
        showData();
    }

    @Override
    public void onRewardaCartoonFailure(String msg) {
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
