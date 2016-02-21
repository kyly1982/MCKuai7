package com.mckuai.imc.Activity;

import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.AppCompatRadioButton;
import android.support.v7.widget.AppCompatTextView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;

import com.mckuai.imc.Base.BaseActivity;
import com.mckuai.imc.Base.BaseFragment;
import com.mckuai.imc.Fragment.MainFragment_Cartoon;
import com.mckuai.imc.Fragment.MainFragment_Chat;
import com.mckuai.imc.Fragment.MainFragment_Community;
import com.mckuai.imc.Fragment.MainFragment_Mine;
import com.mckuai.imc.R;

import java.util.ArrayList;

public class MainActivity extends BaseActivity implements View.OnClickListener,  RadioGroup.OnCheckedChangeListener, BaseFragment.OnFragmentEventListener {
    private RadioGroup nav;
    private RelativeLayout content;
    private AppCompatTextView title;
    private RadioGroup cartoonType;
    private AppCompatRadioButton mNewType;
    private boolean isCheckUpgread = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initToolbar(R.id.toolbar, 0, null);
        initFragment();
        initDrawer();
        initView();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (0 <= currentFragmentIndex && currentFragmentIndex < fragments.size()) {
            mFragmentManager.beginTransaction().show(fragments.get(currentFragmentIndex));
        }
        if (!isCheckUpgread) {
            checkUpgrade(true);
            isCheckUpgread = true;
        }
    }

    private void initView() {
        nav = (RadioGroup) findViewById(R.id.nav);
        title = (AppCompatTextView) findViewById(R.id.actionbar_title);
        cartoonType = (RadioGroup) findViewById(R.id.actionbar_cartoon_rg);
        mNewType = (AppCompatRadioButton) findViewById(R.id.cartoon_type_new);
        //createCartoon = (AppCompatImageButton) findViewById(R.id.nav_create);
        findViewById(R.id.nav_create).setOnClickListener(this);
        cartoonType.setVisibility(View.VISIBLE);
        nav.setVisibility(View.VISIBLE);
        nav.setOnCheckedChangeListener(this);
        ((AppCompatRadioButton) findViewById(R.id.nav_cartoon)).setChecked(true);
        cartoonType.setOnCheckedChangeListener(this);
        //createCartoon.setOnClickListener(this);
    }

    private void initFragment() {
        if (null== fragments) {
            MainFragment_Cartoon cartoonFragment = new MainFragment_Cartoon();
            MainFragment_Chat chatFragment = new MainFragment_Chat();
            MainFragment_Community communityFragment = new MainFragment_Community();
            MainFragment_Mine mineFragment = new MainFragment_Mine();
            //Fragment_Mine mineFragment = new Fragment_Mine();

            cartoonFragment.setFragmentEventListener(this);

            fragments = new ArrayList<>(4);
            fragments.add(cartoonFragment);
            fragments.add(chatFragment);
            fragments.add(communityFragment);
            fragments.add(mineFragment);

            if (null == mFragmentManager) {
                mFragmentManager = getFragmentManager();
            }
            content = (RelativeLayout) findViewById(R.id.context);
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
        //getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if (!isSlidingMenuShow) {
            showMessage("退出软件？", "退出", new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mApplication.handleExit();
                    finish();
                }
            });
        }
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
        switch (v.getId()){
            case R.id.nav_create:
                Intent intent = new Intent(this,CreateActivity.class);
                startActivity(intent);
                break;
        }
    }

    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {
        switch (checkedId){

            case R.id.cartoon_type_hot:
                if (null != fragments){
                    ((MainFragment_Cartoon)fragments.get(0)).setType(1);
                }
                break;
            case R.id.cartoon_type_new:
                if (null != fragments){
                    ((MainFragment_Cartoon)fragments.get(0)).setType(0);
                }
                break;
            default:
                if (null != fragments && !fragments.isEmpty()){
                    FragmentTransaction transaction = mFragmentManager.beginTransaction();
                    if (0 <= currentFragmentIndex) {
                        transaction.hide(fragments.get(currentFragmentIndex));
                    }
                    switch (checkedId){
                        case R.id.nav_cartoon:
                            cartoonType.setVisibility(View.VISIBLE);
                            title.setVisibility(View.GONE);
                            currentFragmentIndex = 0;
                            break;
                        case R.id.nav_chat:
                            cartoonType.setVisibility(View.GONE);
                            title.setVisibility(View.VISIBLE);
                            title.setText("聊天");
                            currentFragmentIndex = 1;
                            break;
                        case R.id.nav_community:
                            cartoonType.setVisibility(View.GONE);
                            title.setVisibility(View.VISIBLE);
                            title.setText("社区");
                            currentFragmentIndex = 2;
                            break;
                        case R.id.nav_mine:
                            cartoonType.setVisibility(View.GONE);
                            title.setVisibility(View.VISIBLE);
                            title.setText("消息");
                            currentFragmentIndex = 3;
                            break;
                    }
                    transaction.show(fragments.get(currentFragmentIndex)).commit();
                }
                break;
        }
    }

/*    private FragmentTransaction hideFragment(){
        FragmentTransaction transaction = mFragmentManager.beginTransaction();
        transaction.hide(fragments.get(currentFragmentIndex));
        return transaction;
    }*/


    @Override
    public void onFragmentAttach(int titleResId) {
      /*  if (0 == fragmentIndex && titleResId == R.string.fragment_main_cartoon){
            mFragmentManager.beginTransaction().show(fragments.get(0)).commit();
        }*/
        switch (titleResId) {
            case R.string.fragment_cartoon:
                mNewType.setChecked(true);
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
    public void onFragmentShow(int titleResId) {
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
    public void onFragmentAction(Object object) {

    }

}
