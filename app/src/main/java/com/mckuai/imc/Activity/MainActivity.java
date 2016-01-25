package com.mckuai.imc.Activity;

import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.AppCompatImageButton;
import android.support.v7.widget.AppCompatRadioButton;
import android.support.v7.widget.AppCompatTextView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;

import com.mckuai.imc.Base.BaseActivity;
import com.mckuai.imc.Base.BaseFragment;
import com.mckuai.imc.Fragment.CartoonFragment;
import com.mckuai.imc.Fragment.ChatFragment;
import com.mckuai.imc.Fragment.CommunityFragment;
import com.mckuai.imc.Fragment.MineFragment;
import com.mckuai.imc.R;

import java.util.ArrayList;

public class MainActivity extends BaseActivity implements View.OnClickListener,  RadioGroup.OnCheckedChangeListener, BaseFragment.OnFragmentEventListener {
    private RadioGroup nav;
    private ArrayList<BaseFragment> fragments;
    private int fragmentIndex = 0;
    private RelativeLayout content;
    private AppCompatTextView title;
    private AppCompatImageButton createCartoon;
    private RadioGroup cartoonType;
    private AppCompatRadioButton mNewType;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initToolbar(R.id.toolbar, 0, null);
        initDrawer();
        initView();
        initFragment();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (0 <= fragmentIndex && fragmentIndex < fragments.size()) {
            mFragmentManager.beginTransaction().show(fragments.get(fragmentIndex));
        }
    }

    private void initView() {
        nav = (RadioGroup) findViewById(R.id.nav);
        title = (AppCompatTextView) findViewById(R.id.actionbar_title);
        cartoonType = (RadioGroup) findViewById(R.id.actionbar_cartoon_rg);
        content = (RelativeLayout) findViewById(R.id.context);
        mNewType = (AppCompatRadioButton) findViewById(R.id.cartoon_type_new);
        createCartoon = (AppCompatImageButton) findViewById(R.id.nav_create);

        cartoonType.setVisibility(View.VISIBLE);
        nav.setVisibility(View.VISIBLE);
        nav.setOnCheckedChangeListener(this);
        ((AppCompatRadioButton) findViewById(R.id.nav_cartoon)).setChecked(true);
        cartoonType.setOnCheckedChangeListener(this);
        createCartoon.setOnClickListener(this);
        mNewType.setChecked(true);
    }

    private void initFragment() {
        if (null== fragments) {
            CartoonFragment cartoonFragment = new CartoonFragment();
            ChatFragment chatFragment = new ChatFragment();
            CommunityFragment communityFragment = new CommunityFragment();
            MineFragment mineFragment = new MineFragment();

            cartoonFragment.setFragmentEventListener(this);

            fragments = new ArrayList<>(4);
            fragments.add(cartoonFragment);
            fragments.add(chatFragment);
            fragments.add(communityFragment);
            fragments.add(mineFragment);

            if (null == mFragmentManager) {
                mFragmentManager = getFragmentManager();
            }
            FragmentTransaction transaction = mFragmentManager.beginTransaction();
            for (BaseFragment fragment : fragments) {
                transaction.add(content.getId(), fragment);
            }
            transaction.show(fragments.get(0));
            for (int i = 1; i < 4; i++) {
                transaction.hide(fragments.get(i));
            }
            transaction.commit();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View v) {
        Snackbar.make(mToolbar, "" + v.toString(), Snackbar.LENGTH_LONG).show();
        switch (v.getId()){
            case R.id.nav_create:
                Intent intent = new Intent(this,CreateActivity.class);
                startActivity(intent);
                break;
        }
    }

    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {
        if (checkedId == R.id.nav_create) {
            callCreateActivity();
        } else if (null != fragments && !fragments.isEmpty()) {
            FragmentTransaction transaction = mFragmentManager.beginTransaction();
            transaction.hide(fragments.get(fragmentIndex));
            switch (checkedId) {
                case R.id.nav_cartoon:
                    cartoonType.setVisibility(View.VISIBLE);
                    title.setVisibility(View.GONE);
                    fragmentIndex = 0;
                    break;
                case R.id.nav_chat:
                    cartoonType.setVisibility(View.GONE);
                    title.setVisibility(View.VISIBLE);
                    fragmentIndex = 1;
                    break;
                case R.id.nav_community:
                    cartoonType.setVisibility(View.GONE);
                    title.setVisibility(View.VISIBLE);
                    fragmentIndex = 2;
                    break;
                case R.id.nav_mine:
                    cartoonType.setVisibility(View.GONE);
                    title.setVisibility(View.VISIBLE);
                    fragmentIndex = 3;
                    break;
                case R.id.cartoon_type_hot:
                    ((CartoonFragment)fragments.get(0)).setTypeChanged(1);
                    break;
                case R.id.cartoon_type_new:
                    ((CartoonFragment)fragments.get(0)).setTypeChanged(0);
                    break;
            }
            transaction.show(fragments.get(fragmentIndex)).commit();
        }

    }

    public void callCreateActivity() {

    }

    @Override
    public void onAttach(int titleResId) {
      /*  if (0 == fragmentIndex && titleResId == R.string.fragment_cartoon){
            mFragmentManager.beginTransaction().show(fragments.get(0)).commit();
        }*/
        switch (titleResId) {
            case R.string.fragment_cartoon:
                break;
            case R.string.fragment_chat:
                break;
            case R.string.fragment_community:
                break;
            case R.string.fragment_mine:
                break;
        }
    }

    @Override
    public void onShow(int titleResId) {
        switch (titleResId) {
            case R.string.fragment_cartoon:
                break;
            case R.string.fragment_chat:
                break;
            case R.string.fragment_community:
                break;
            case R.string.fragment_mine:
                break;
        }
    }
}
