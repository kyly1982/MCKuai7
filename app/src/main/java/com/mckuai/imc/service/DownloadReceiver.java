package com.mckuai.imc.service;

import android.app.DownloadManager;
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
            /*int type = intent.getIntExtra("operationType", -1);
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
            }*/
            switch (intent.getAction()){
                case DownloadManager.ACTION_DOWNLOAD_COMPLETE:
                    long taskId = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID,0);
                    if (0 != taskId) {
                        onCompleted(taskId);
                    }
                    break;
                case DownloadManager.ACTION_NOTIFICATION_CLICKED:
                    long[] tasksId = intent.getLongArrayExtra(DownloadManager.EXTRA_NOTIFICATION_CLICK_DOWNLOAD_IDS);
                    onClicked(tasksId);
                    break;
            }
        }
    }

 /*   public void onCancle(){

    }
    public void onInstall(){

    }*/

    public void onCompleted(long id){

    }

    public void onClicked(long[] tasksId){

    }


}