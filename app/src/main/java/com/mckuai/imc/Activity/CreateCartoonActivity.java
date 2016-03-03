package com.mckuai.imc.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v7.widget.AppCompatTextView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;

import com.mckuai.imc.Base.BaseActivity;
import com.mckuai.imc.Base.BaseFragment;
import com.mckuai.imc.Fragment.CreateCartoonFragment;
import com.mckuai.imc.R;
import com.umeng.analytics.MobclickAgent;

import java.util.ArrayList;

public class CreateCartoonActivity extends BaseActivity
        implements NavigationView.OnNavigationItemSelectedListener
        , BaseFragment.OnFragmentEventListener
        , CreateCartoonFragment.OnBackgroundSetListener {
    CreateCartoonFragment createFragment;
    private MenuItem menu_next;
    private MenuItem menu_publish;
    private int currentStep = 0;
    private boolean isBackgroundSet = false;
    public AppCompatTextView title;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_normal);
        initToolbar(R.id.toolbar, 0, null);
        //initDrawer();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        initView();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (null == createFragment) {
            createFragment = new CreateCartoonFragment();
            createFragment.setOnBackgroundSetListener(this);
            createFragment.setFragmentEventListener(this);
            if (null == fragments) {
                fragments = new ArrayList<>(1);
            }
            fragments.add(createFragment);
            currentFragmentIndex = 0;
            setContentFragment(R.id.context, createFragment);
        }
    }

    private void initView() {
        title = (AppCompatTextView) findViewById(R.id.actionbar_title);
        title.setText("创作");
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_createcartoon, menu);
        menu_next = menu.findItem(R.id.menu_cartoonaction_next);
        menu_publish = menu.findItem(R.id.menu_cartoonaction_publish);
        menu_publish.setVisible(false);
        menu_next.setVisible(true);
        menu_next.setTitle("下一步");
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (!isBackgroundSet) {
            showMessage("还未设置场景，创造场景后才能下一步", null, null);
            return super.onOptionsItemSelected(item);
        }
        switch (item.getItemId()) {
            case R.id.menu_cartoonaction_next:
                MobclickAgent.onEvent(this, "createCartoon_next");
                currentStep++;
                createFragment.showNextStep(currentStep);
                if (3 == currentStep) {
                    menu_next.setVisible(false);
                    menu_publish.setVisible(true);
                    getWindow().invalidatePanelMenu(Window.FEATURE_OPTIONS_PANEL);
                }
                break;
            case R.id.menu_cartoonaction_publish:
                MobclickAgent.onEvent(this, "createCartoon_publish");
                if (mApplication.isLogin()) {
                    createFragment.uploadCartoon(null);
                } else {
                    callLogin(1);
                }
                break;
        }

        return super.onOptionsItemSelected(item);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (RESULT_OK == resultCode) {
            switch (requestCode) {
                //上传
                case 1:
                    createFragment.uploadCartoon(null);
                    break;
                case 2:
                    break;
            }
        }
    }

    @Override
    public void onBackPressed() {
        if (!createFragment.onBackPressed()) {
            super.onBackPressed();
        }
    }

    @Override
    public void onFragmentAction(Object object) {
        this.finish();
    }

    @Override
    public void onFragmentShow(int titleResId) {

    }

    @Override
    public void onFragmentAttach(int titleResId) {

    }

    @Override
    public void onBackgroundSet() {
        isBackgroundSet = true;
    }
}
