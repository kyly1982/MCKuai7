package com.mckuai.imc.Activity;

import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;

import com.mckuai.imc.Base.BaseActivity;
import com.mckuai.imc.Base.BaseFragment;
import com.mckuai.imc.Fragment.CreateCartoonFragment;
import com.mckuai.imc.R;

public class CreateActivity extends BaseActivity
        implements NavigationView.OnNavigationItemSelectedListener,BaseFragment.OnFragmentEventListener {
    CreateCartoonFragment createFragment;
    private MenuItem menu_next;
    private MenuItem menu_publish;
    private int currentStep = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initToolbar(R.id.toolbar, 0, null);
        initDrawer();
        initView();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (null == createFragment){
            createFragment = new CreateCartoonFragment();
            createFragment.setFragmentEventListener(this);
            setContentFragment(R.id.context, createFragment);
        }
    }

    private void initView(){

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_createcartoon, menu);
        menu_next = menu.findItem(R.id.menu_cartoonaction_next);
        menu_publish = menu.findItem(R.id.menu_cartoonaction_publish);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case  R.id.menu_cartoonaction_next:
                currentStep++;
                createFragment.showNextStep(currentStep);
                if (3 == currentStep){
                    menu_next.setVisible(false);
                    menu_publish.setVisible(true);
                    getWindow().invalidatePanelMenu(Window.FEATURE_OPTIONS_PANEL);
                }
                break;
            case R.id.menu_cartoonaction_publish:
                createFragment.upload();
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onActon(Object object) {
        this.finish();
    }

    @Override
    public void onShow(int titleResId) {

    }

    @Override
    public void onAttach(int titleResId) {

    }
}
