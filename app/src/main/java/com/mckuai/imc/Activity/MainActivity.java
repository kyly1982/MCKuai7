package com.mckuai.imc.Activity;

import android.app.FragmentTransaction;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.AppCompatRadioButton;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.RadioGroup;

import com.mckuai.imc.Base.BaseActivity;
import com.mckuai.imc.Base.BaseFragment;
import com.mckuai.imc.Fragment.CartoonFragment;
import com.mckuai.imc.Fragment.ChatFragment;
import com.mckuai.imc.Fragment.CommunityFragment;
import com.mckuai.imc.Fragment.MineFragment;
import com.mckuai.imc.R;

import java.util.ArrayList;

public class MainActivity extends BaseActivity implements View.OnClickListener, NavigationView.OnNavigationItemSelectedListener, RadioGroup.OnCheckedChangeListener, BaseFragment.OnFragmentEventListener {
    private RadioGroup nav;
    private ArrayList<BaseFragment> fragments;
    private int fragmentIndex = 0;
    private ActionBar mActionBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initToolbar(R.id.toolbar, R.mipmap.ic_launcher, this);
        initDrawer(R.id.drawer_layout, R.id.nav_view, this);
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
        nav.setVisibility(View.VISIBLE);
        nav.setOnCheckedChangeListener(this);
        ((AppCompatRadioButton) findViewById(R.id.nav_cartoon)).setChecked(true);
        mActionBar = getSupportActionBar();
        mToolbar.setVisibility(View.GONE);
        mToolbar.setTitle("");
        mActionBar.setWindowTitle("aaaaaa");
        //mActionBar.hide();
    }

    private void initFragment() {
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
        /*for (BaseFragment fragment:fragments){
            transaction.add(R.id.main_context,fragment,fragment.getTitleResId()).hide(fragment);
        }*/
        transaction.replace(R.id.context, cartoonFragment);
        transaction.commit();
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

    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        switch (item.getItemId()) {

        }
        return true;
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
                    fragmentIndex = 0;
                    mActionBar.hide();
                    break;
                case R.id.nav_chat:
                    fragmentIndex = 1;
                    mActionBar.show();
                    break;
                case R.id.nav_community:
                    fragmentIndex = 2;
                    mActionBar.show();
                    break;
                case R.id.nav_mine:
                    fragmentIndex = 3;
                    mActionBar.show();
                    break;
            }
            transaction.replace(R.id.context, fragments.get(fragmentIndex)).commit();
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
