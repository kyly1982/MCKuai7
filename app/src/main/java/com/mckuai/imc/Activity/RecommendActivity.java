package com.mckuai.imc.Activity;

import android.os.Bundle;
import android.support.v7.widget.AppCompatTextView;
import android.view.View;

import com.mckuai.imc.Base.BaseActivity;
import com.mckuai.imc.Fragment.MainFragment_Mine;
import com.mckuai.imc.R;

public class RecommendActivity extends BaseActivity {
    private AppCompatTextView title;
    private MainFragment_Mine fragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_normal);
        initToolbar(R.id.toolbar, 0, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (null == title) {
            initView();
        }
        if (null == fragment) {
            fragment = new MainFragment_Mine();
            setContentFragment(R.id.context, fragment);
        }
    }

    private void initView() {
        title = (AppCompatTextView) findViewById(R.id.actionbar_title);
        title.setText(R.string.fragment_recommend);
    }

}
