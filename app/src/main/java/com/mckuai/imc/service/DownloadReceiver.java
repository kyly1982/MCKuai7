package com.mckuai.imc.service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class DownloadReceiver extends BroadcastReceiver {

    public DownloadReceiver(){
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        // TODO: This method is called when the BroadcastReceiver is receiving
        // an Intent broadcast.
        //throw new UnsupportedOperationException("Not yet implemented");
        if (null != intent) {
            int type = intent.getIntExtra("operationType", -1);
            switch (type) {
                case 0:
                    //开始下载
                    break;
                case 1:
                    onCancle();
                    //取消下载
                    break;
                case 2:
                    //安装
                    onInstall();
                    break;
            }
        }
    }

    public void onCancle(){

    }
    public void onInstall(){

    }


}
