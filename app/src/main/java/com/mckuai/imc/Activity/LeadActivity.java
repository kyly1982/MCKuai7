package com.mckuai.imc.Activity;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.AppCompatRadioButton;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import com.mckuai.imc.Adapter.LeaderFragmentAdapter;
import com.mckuai.imc.Fragment.LeadFragment;
import com.mckuai.imc.R;

import java.util.ArrayList;

public class LeadActivity extends AppCompatActivity implements ViewPager.OnPageChangeListener {
    private ViewPager vp;
    private AppCompatImageView camer;
    private AppCompatRadioButton step1;
    private AppCompatRadioButton step2;
    private AppCompatRadioButton step3;


    private Animation fadein;
    private Animation fadeout;
    private ArrayList<Fragment> fragments;
    private LeaderFragmentAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lead);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (null == vp) {
            initView();
            initFragment();
            initAnimation();
            showData();
        }
    }

    private void initView() {
        vp = (ViewPager) findViewById(R.id.vp);
        camer = (AppCompatImageView) findViewById(R.id.camer);
        step1 = (AppCompatRadioButton) findViewById(R.id.step1);
        step2 = (AppCompatRadioButton) findViewById(R.id.step2);
        step3 = (AppCompatRadioButton) findViewById(R.id.step3);
        vp.addOnPageChangeListener(this);
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
                camer.setVisibility(View.VISIBLE);
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
                camer.setVisibility(View.GONE);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
    }

    private void showData() {
        if (null == adapter) {
            adapter = new LeaderFragmentAdapter(getSupportFragmentManager(), fragments);
            vp.setAdapter(adapter);
            //vp.setCurrentItem(0);
            camer.setVisibility(View.VISIBLE);
            camer.startAnimation(fadein);
        }
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {
        switch (position) {
            case 0:
                if (camer.getVisibility() == View.GONE) {
                    //camer.setAnimation(fadein);
                    camer.startAnimation(fadein);
                }
                step1.setChecked(true);
                break;
            case 1:
                if (camer.getVisibility() == View.VISIBLE) {
                    //camer.setAnimation(fadeout);
                    camer.startAnimation(fadeout);
                }
                step2.setChecked(true);
                break;
            case 2:
                step3.setChecked(true);
                break;
        }
    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }


}
