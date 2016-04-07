package com.mckuai.imc.service;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Environment;
import android.os.IBinder;
import android.support.v7.app.NotificationCompat;
import android.webkit.MimeTypeMap;

import com.mckuai.imc.Bean.Ad;
import com.umeng.analytics.MobclickAgent;

import java.io.File;

import cn.finalteam.okhttpfinal.FileDownloadCallback;
import cn.finalteam.okhttpfinal.HttpRequest;

public class DownloadService extends Service {

    private Ad ad;
    private MCDownloadReciver receiver;
    private String filePath;
    private File apkFile;
    private String fileName;
    private String extension;
    private NotificationManager notificationManager;
    private Notification notification;
    private NotificationCompat.Builder builder;

    private final String action_download_start = "ACTION_MCDOWNLOAD_START";
    private final String action_download_pause = "ACTION_MCDOWNLOAD_PAUSE";
    private final String action_download_cancle = "ACTION_MCDOWNLOAD_CANCLE";
    private final int id = 57361;

    private long lastUpdateTime = 0;
    private int lastProgress = 0;

    public DownloadService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        android.os.Debug.waitForDebugger();
        if (ad == null) {
            ad = (Ad) intent.getSerializableExtra("AD");
            if (null != ad) {
                registerReceiver();
                initNotification();
                download();
            }
        }
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        unRegisterReceiver();
        notificationManager.cancel(id);
        super.onDestroy();
    }

    private void registerReceiver() {
        if (null == receiver) {
            receiver = new MCDownloadReciver();
            IntentFilter filter = new IntentFilter(Intent.ACTION_PACKAGE_ADDED);
            filter.addDataScheme("MCKuaiAdInstaller");
            registerReceiver(receiver, filter);
        }
    }

    private void unRegisterReceiver() {
        if (null != receiver) {
            unregisterReceiver(receiver);
            receiver = null;
        }
    }

    private void initNotification() {
        android.os.Debug.waitForDebugger();
        builder = new NotificationCompat.Builder(getApplicationContext());
        builder.setProgress(100, 0, false)
                .setSmallIcon(android.R.drawable.stat_sys_download)
                .setDeleteIntent(getStopIntent())
                .setContentIntent(getDefaultIntent())
                .setContentTitle(ad.getDownName())
                .setContentText("等待下载...");
        notification = builder.build();
        notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(id, notification);
    }

    private void updateNotificationProgress() {
        builder.setProgress(100, lastProgress, false);
        notificationManager.notify(id, builder.build());
    }

    private void updateNotificationState(int state) {
        android.os.Debug.waitForDebugger();
        switch (state) {
            case -1:
                builder.setContentText("下载出错！");
                break;
            case 0:
                builder.setContentText("下载中，请稍候...");
                break;
            case 1:
                builder.setContentText("下载完成，点击开始安装！");
                break;
        }
        notificationManager.notify(id, builder.build());
    }


    private void download() {
        android.os.Debug.waitForDebugger();
        HttpRequest.download(ad.getDownUrl(), getSaveFile(), new FileDownloadCallback() {
            @Override
            public void onStart() {
                updateNotificationState(0);
            }

            @Override
            public void onProgress(int progress, long networkSpeed) {

                long time = System.currentTimeMillis();
                if (progress != lastProgress && time > lastUpdateTime + 1000) {
                    if (0 == lastProgress){
                        updateNotificationState(0);
                    }
                    updateNotificationProgress();
                    lastProgress = progress;
                    lastUpdateTime = time;
                }
            }

            @Override
            public void onDone() {
                lastProgress = 100;
                updateNotificationProgress();
                updateNotificationState(1);
                MobclickAgent.onEvent(getApplicationContext(),"MCAD_Downloaded");
                installApk();
            }

            @Override
            public void onFailure() {
                updateNotificationState(-1);
            }
        });

    }

    private void stopDownload() {
        android.os.Debug.waitForDebugger();

        MobclickAgent.onEvent(getApplicationContext(), "MCAD_CancleDownload");
        notificationManager.cancel(id);
        stopSelf();
    }

    private void pauseDownload() {

    }

    private void startDownload() {

    }


    private File getSaveFile() {
        if (null == apkFile) {
            File file = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
            if (!file.exists()) {
                file.mkdirs();
            }
            filePath = file.getPath() + "/";
            fileName = ad.getDownUrl().substring(ad.getDownUrl().lastIndexOf("/"));
            extension = MimeTypeMap.getFileExtensionFromUrl(ad.getDownUrl());
            apkFile = new File(filePath, fileName);
        }
        return apkFile;
    }


    private void installApk() {
        android.os.Debug.waitForDebugger();
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_VIEW);
        intent.setDataAndType(Uri.fromFile(apkFile), "application/vnd.android.package-archive");
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    /**
     * 点击通知
     * 根据情况来实现不同的操作
     * 在下载时，会暂停下载
     * 暂停下载时，会启动下载
     * 下载完成时，会安装
     *
     * @return
     */
    private PendingIntent getDefaultIntent() {
        android.os.Debug.waitForDebugger();
        Intent intent = new Intent();
        //暂不支持
       /* if (null != downloadTask){
            switch (downloadTask.getStatus()){
                case FileDownloadStatus.error:
                    intent.setAction(action_download_start);
                    break;
                case FileDownloadStatus.paused:
                    intent.setAction(action_download_start);
                    break;
                default:
                    break;
            }
        }*/
        PendingIntent pendingIntent = PendingIntent.getBroadcast(getApplicationContext(), 0, intent, 0);
        return pendingIntent;
    }

    /**
     * 取消通知
     * 会停止服务
     *
     * @return
     */
    private PendingIntent getStopIntent() {
        android.os.Debug.waitForDebugger();
        Intent intent = new Intent(action_download_cancle);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(getApplicationContext(), 0, intent, 0);
        return pendingIntent;
    }


    public class MCDownloadReciver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            android.os.Debug.waitForDebugger();
            switch (intent.getAction()){
                case action_download_pause:
                   //暂停

                    break;
                case action_download_start:
                    //开始下载
                    break;
                case action_download_cancle:
                    stopDownload();
                    break;
                case Intent.ACTION_PACKAGE_ADDED:
                    if (null != intent.getData() && intent.getData().getScheme().equals("MCKuaiAdInstaller")) {
                        MobclickAgent.onEvent(getApplicationContext(), "MCAD_Installed");
                        //notificationManager.cancel(id);
                        stopSelf();
                    }
                    break;
            }
        }
    }


}
