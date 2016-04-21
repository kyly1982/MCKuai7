package com.mckuai.imc.Activity;

import android.content.ContentValues;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.design.widget.NavigationView;
import android.support.v7.widget.AppCompatTextView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;

import com.mckuai.imc.Base.BaseActivity;
import com.mckuai.imc.Base.BaseFragment;
import com.mckuai.imc.Bean.Cartoon;
import com.mckuai.imc.Fragment.CreateCartoonFragment;
import com.mckuai.imc.Fragment.ThemeFragment;
import com.mckuai.imc.R;
import com.mckuai.imc.Util.TimestampConverter;
import com.mckuai.imc.Widget.ShareCartoonDialog;
import com.umeng.analytics.MobclickAgent;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;

public class CreateCartoonActivity extends BaseActivity
        implements NavigationView.OnNavigationItemSelectedListener
        , BaseFragment.OnFragmentEventListener
        , CreateCartoonFragment.OnActionListener
        , ThemeFragment.OnThemeSelectedListener {
    private CreateCartoonFragment createFragment;
    private ThemeFragment themeFragment;
    private MenuItem menu_next;
    private MenuItem menu_publish;
    private int currentStep = 0;
    private boolean isBackgroundSet = false;
    private boolean isWidgetSet = false;
    public AppCompatTextView title;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_normal);
        initToolbar(R.id.toolbar, 0, null);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!showNext(true)){
                    finish();
                }
            }
        });
        initView();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (null == createFragment) {
            initFragment();
            setContentFragment(R.id.context, themeFragment);
            currentStep++;
        }
    }

    private void initFragment() {

        themeFragment = new ThemeFragment();
        createFragment = new CreateCartoonFragment();

        themeFragment.setOnThemeSelectedListener(this);
        createFragment.setOnBackgroundSetListener(this);
        createFragment.setFragmentEventListener(this);
        if (null == fragments) {
            fragments = new ArrayList<>(1);
        }
        fragments.add(createFragment);
        currentFragmentIndex = 0;
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
        switch (item.getItemId()) {
            case R.id.menu_cartoonaction_next:
                MobclickAgent.onEvent(this, "createCartoon_next");
                if (!showNext(false)) {
                    return super.onOptionsItemSelected(item);
                }
                break;
            case R.id.menu_cartoonaction_publish:
                MobclickAgent.onEvent(this, "createCartoon_publish");
                saveCartoon();
                if (mApplication.isLogin()) {
                    createFragment.uploadCartoon(null);
                } else {
                    callLogin(1);
                }
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    private boolean showNext(boolean isBack) {
        if (isBack){
            switch (currentStep){
                case 1:
                    //关闭
                    return false;
                case 2:
                    //显示选择主题
                    currentStep--;
                    setContentFragment(R.id.context, themeFragment);
                    break;
                case 3:
                    currentStep--;
                    createFragment.showNextStep(currentStep,true);
                    break;
                case 4:
                    //菜单切换为显示下一步
                    menu_next.setVisible(true);
                    menu_publish.setVisible(false);
                    getWindow().invalidatePanelMenu(Window.FEATURE_OPTIONS_PANEL);

                default:
                    currentStep--;
                    createFragment.showNextStep(currentStep,true);
                    break;
            }
        } else {
            switch (currentStep){
                case 2:
                    if (!isBackgroundSet) {
                        showMessage("还未创建背景，创建背景后才能进入下一步", null, null);
                        return false;
                    } else {
                        currentStep++;
                        createFragment.showNextStep(currentStep,false);
                    }
                    break;
                case 3:
                    if (!isWidgetSet) {
                        showMessage("还未添加人物或工具，添加后才能进入下一步", null, null);
                        return false;
                    } else {
                        currentStep++;
                        menu_next.setVisible(false);
                        menu_publish.setVisible(true);
                        getWindow().invalidatePanelMenu(Window.FEATURE_OPTIONS_PANEL);
                        createFragment.showNextStep(currentStep,false);
                    }
                    break;
            }
        }
        return true;
    }

    private void saveCartoon() {
        Bitmap cartoon = createFragment.getCartoonBitmap();
        boolean isSuccess = false;
        if (null != cartoon) {
            String path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES) + "/MCKuai/";
            String filename = "麦块漫画" + TimestampConverter.getTime(System.currentTimeMillis()) + ".png";
            File file = new File(path, filename);
            FileOutputStream outputStream = null;
            try {
                outputStream = new FileOutputStream(file);
            } catch (Exception e) {
                e.printStackTrace();
            }
            if (null != outputStream) {
                cartoon.compress(Bitmap.CompressFormat.PNG, 90, outputStream);
                try {
                    outputStream.flush();
                    outputStream.close();
                    isSuccess = true;
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            if (isSuccess) {
                ContentValues values = new ContentValues();
                values.put(MediaStore.Images.ImageColumns.TITLE, "麦块漫画");
                values.put(MediaStore.Images.ImageColumns.DISPLAY_NAME, filename);
                values.put(MediaStore.Images.ImageColumns.DATE_TAKEN, System.currentTimeMillis());
                values.put(MediaStore.Images.ImageColumns.MIME_TYPE, "image/jpeg");
                values.put(MediaStore.Images.ImageColumns.ORIENTATION, 0);
                values.put(MediaStore.Images.ImageColumns.DATA, path + filename);
                values.put(MediaStore.Images.ImageColumns.WIDTH, cartoon.getWidth());
                values.put(MediaStore.Images.ImageColumns.HEIGHT, cartoon.getHeight());
                try {
                    Uri uri = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

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
        if (!showNext(true)) {
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

    @Override
    public void onWidgetset() {
        isWidgetSet = true;
    }

    @Override
    public void onPublishSuccess(Cartoon cartoon) {
        ShareCartoonDialog shareCartoonDialog = new ShareCartoonDialog();
        shareCartoonDialog.setData(cartoon, new ShareCartoonDialog.OnDismiss() {
            @Override
            public void onDismissed() {
                finish();
            }
        });
        shareCartoonDialog.show(getFragmentManager(), "SHARE");
    }

    @Override
    public void onThemeSelected(String theme) {
        MobclickAgent.onEvent(this, "createCartoon_selectedTheme");
        currentStep++;
        createFragment.setTheme(theme);
        setContentFragment(R.id.context, createFragment);
    }
}
