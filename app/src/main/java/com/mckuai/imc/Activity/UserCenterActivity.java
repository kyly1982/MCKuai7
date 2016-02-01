package com.mckuai.imc.Activity;

import android.os.Bundle;

import com.mckuai.imc.Base.BaseActivity;
import com.mckuai.imc.Bean.User;
import com.mckuai.imc.R;

public class UserCenterActivity extends BaseActivity {
    private User user;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_usercenter);
        initToolbar(R.id.toolbar, 0, null);
        initDrawer();
    }

    private boolean isMySelf(){
        return mApplication.isLogin() && mApplication.user.getId() == user.getId();
    }

    private void loadData(){

    }
}
