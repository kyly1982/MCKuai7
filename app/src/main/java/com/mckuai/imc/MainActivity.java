package com.mckuai.imc;

import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.mckuai.imc.Base.BaseActivity;

public class MainActivity extends BaseActivity implements View.OnClickListener, NavigationView.OnNavigationItemSelectedListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initToolbar(R.id.toolbar, R.mipmap.ic_launcher, this);
        initDrawer(R.id.drawer_layout, R.id.nav_view, this);
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
        return false;
    }
}
