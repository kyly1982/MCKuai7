package com.mckuai.imc.Activity;

import android.os.Bundle;
import android.support.v7.widget.AppCompatTextView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.mckuai.imc.Base.BaseActivity;
import com.mckuai.imc.Base.BaseFragment;
import com.mckuai.imc.Fragment.ProfileEditerFragment;
import com.mckuai.imc.R;

import java.util.ArrayList;

public class ProfileEditerActivity extends BaseActivity implements BaseFragment.OnFragmentEventListener, View.OnClickListener {
    private ProfileEditerFragment fragment;

    private AppCompatTextView title;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_normal);
        initToolbar(R.id.toolbar, 0, null);
        mToolbar.setNavigationOnClickListener(this);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        title = (AppCompatTextView) findViewById(R.id.actionbar_title);
        title.setText("设置");
        //initDrawer();
        initFragment();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    private void initFragment(){
        fragment = new ProfileEditerFragment();
        fragment.setFragmentEventListener(this);
        fragments = new ArrayList<>(1);
        fragments.add(fragment);
        setContentFragment(R.id.context, fragment);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_profile, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_save:
                fragment.upload();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onFragmentShow(int titleResId) {

    }

    @Override
    public void onFragmentAttach(int titleResId) {
        //fragment.showData();
    }

    @Override
    public void onFragmentAction(Object object) {

    }

    @Override
    public void onClick(View v) {
        if (!fragment.onBackPressed()) {
            finish();
        }
    }
}
