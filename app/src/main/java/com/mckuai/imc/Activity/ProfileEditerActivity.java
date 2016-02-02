package com.mckuai.imc.Activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.mckuai.imc.Base.BaseActivity;
import com.mckuai.imc.Base.BaseFragment;
import com.mckuai.imc.Fragment.ProfileEditerFragment;
import com.mckuai.imc.R;

public class ProfileEditerActivity extends BaseActivity implements BaseFragment.OnFragmentEventListener {
    ProfileEditerFragment fragment;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_normal);
        initToolbar(R.id.toolbar, 0, null);
        initDrawer();
        initFragment();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    private void initFragment(){
        fragment = new ProfileEditerFragment();
        fragment.setFragmentEventListener(this);
        setContentFragment(R.id.context,fragment);
    }


    @Override
    public void onFragmentShow(int titleResId) {

    }

    @Override
    public void onFragmentAttach(int titleResId) {
        fragment.showData();
    }

    @Override
    public void onFragmentAction(Object object) {

    }
}
