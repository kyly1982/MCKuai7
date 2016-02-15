package com.mckuai.imc.Activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.AppCompatRadioButton;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import com.mckuai.imc.Adapter.LeaderFragmentAdapter;
import com.mckuai.imc.Base.BaseActivity;
import com.mckuai.imc.Fragment.LeadFragment;
import com.mckuai.imc.R;

import java.util.ArrayList;

public class LeadActivity extends BaseActivity implements ViewPager.OnPageChangeListener, View.OnClickListener {
    private ViewPager vp;
    private AppCompatImageView camer;
    private AppCompatRadioButton step1;
    private AppCompatRadioButton step2;
    private AppCompatRadioButton step3;
    private AppCompatButton start;
    private AppCompatImageView widget1;
    private AppCompatImageView widget2;
    private AppCompatImageView dlg1;
    private AppCompatImageView dlg2;



    private Animation fadein;
    private Animation fadeout;
    private ArrayList<Fragment> fragments;
    private LeaderFragmentAdapter adapter;
    private boolean isFirstBoot = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lead);
        readdata();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (null == vp) {
            initView();
            initFragment();
            initAnimation();
            mApplication.init();
            showData();
        }
    }

    private void initView() {
        vp = (ViewPager) findViewById(R.id.vp);
        camer = (AppCompatImageView) findViewById(R.id.camer);
        step1 = (AppCompatRadioButton) findViewById(R.id.step1);
        step2 = (AppCompatRadioButton) findViewById(R.id.step2);
        step3 = (AppCompatRadioButton) findViewById(R.id.step3);
        start = (AppCompatButton) findViewById(R.id.start);

        widget1 = (AppCompatImageView) findViewById(R.id.widget1);
        widget2 = (AppCompatImageView) findViewById(R.id.widget2);
        dlg1 = (AppCompatImageView) findViewById(R.id.dialog1);
        dlg2 = (AppCompatImageView) findViewById(R.id.dialog2);

        //camer.setVisibility(View.GONE);
        widget1.setVisibility(View.GONE);
        widget2.setVisibility(View.GONE);
        dlg1.setVisibility(View.GONE);
        dlg2.setVisibility(View.GONE);

        vp.addOnPageChangeListener(this);
        start.setOnClickListener(this);
    }

    private void initFragment() {
        fragments = new ArrayList<>(3);
        fragments.add(LeadFragment.newInstance(0));
        fragments.add(LeadFragment.newInstance(1));
        fragments.add(LeadFragment.newInstance(2));
    }

    private void initAnimation() {
        fadein = AnimationUtils.loadAnimation(this, R.anim.camera_alpha_fadein);
        fadeout = AnimationUtils.loadAnimation(this, R.anim.camera_alpha_fadeout);
        fadein.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                //camer.setVisibility(View.VISIBLE);

                switch (vp.getCurrentItem()) {
                    case 0:
                        camer.startAnimation(fadeout);
                        break;
                    case 1:
                        break;
                    case 2:
                        break;
                }
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        fadeout.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                camer.setAlpha(0f);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
    }

    private void showData() {
        if (isFirstBoot) {
            if (null == adapter) {
                adapter = new LeaderFragmentAdapter(getSupportFragmentManager(), fragments);
                vp.setAdapter(adapter);
                //vp.setCurrentItem(0);
                //camer.startAnimation(fadein);
            }
        } else {

            Intent intent = new Intent(this, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
        }
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {
        switch (position) {
            case 0:
                step1.setChecked(true);
                camer.setVisibility(View.VISIBLE);
                camer.setAlpha(1f);
                widget1.setVisibility(View.GONE);
                widget2.setVisibility(View.GONE);
                break;
            case 1:
                step2.setChecked(true);
                camer.setAlpha(0f);
                dlg2.setVisibility(View.GONE);
                dlg1.setVisibility(View.GONE);
                widget1.setVisibility(View.VISIBLE);
                widget2.setVisibility(View.VISIBLE);
                start.setVisibility(View.GONE);
                break;
            case 2:
                step3.setChecked(true);
                dlg1.setVisibility(View.VISIBLE);
                dlg2.setVisibility(View.VISIBLE);
                start.setVisibility(View.VISIBLE);
                break;
        }
    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }

    @Override
    public void onClick(View v) {
        Intent intent = new Intent(this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }

    private void readdata() {
        SharedPreferences preferences = getSharedPreferences(getString(R.string.preferences_filename), 0);
        isFirstBoot = preferences.getBoolean(getString(R.string.preferences_isFirstBoot), true);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean(getString(R.string.preferences_isFirstBoot), false);
        editor.commit();
    }
}
