package com.mckuai.imc.Activity;

import android.os.Bundle;

import com.mckuai.imc.Base.BaseActivity;
import com.mckuai.imc.Bean.Cartoon;
import com.mckuai.imc.R;
import com.mckuai.imc.Widget.CartoonView;

public class CartoonActivity extends BaseActivity implements CartoonView.OnCartoonElementClickListener {
    private Cartoon cartoon;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cartoon);
        initToolbar(R.id.toolbar, 0, null);
        initDrawer();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (null == cartoon){
           cartoon = (Cartoon) getIntent().getExtras().getSerializable(getString(R.string.cartoondetail_tag_cartoon));
            initView();
        }
    }

    private void initView(){
        CartoonView cartoonView = (CartoonView) findViewById(R.id.cartoon);
        cartoonView.setOnCartoonElementClickListener(this);
        if (null != cartoon){
            cartoonView.setData(cartoon,true);
        }
    }

    @Override
    public void onCommentCartoon(Cartoon cartoon) {
        //输入评论
    }

    @Override
    public void onOwnerClicked(int ownerId) {
        //显示作者的个人中心

    }

    @Override
    public void onShareCartoon(Cartoon cartoon) {
        //分享
    }

    @Override
    public void onRewardCartoon(Cartoon cartoon) {
        //奖励
    }
}
