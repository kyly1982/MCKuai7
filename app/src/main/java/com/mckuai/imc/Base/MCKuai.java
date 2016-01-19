package com.mckuai.imc.Base;

import android.app.Application;

/**
 * Created by kyly on 2016/1/18.
 */
public class MCKuai extends Application {
    public static MCKuai instence;

    @Override
    public void onCreate() {
        super.onCreate();
        init();
        instence = this;
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
    }

    private void init() {

    }

}
