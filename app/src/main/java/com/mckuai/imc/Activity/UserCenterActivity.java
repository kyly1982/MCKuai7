package com.mckuai.imc.Activity;

import android.os.Bundle;

import com.mckuai.imc.Base.BaseActivity;
import com.mckuai.imc.R;

public class UserCenterActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initToolbar(R.id.toolbar, 0, null);
        initDrawer();
    }
}
