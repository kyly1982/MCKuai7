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
import android.os.Build;
import android.os.Environment;
import android.os.IBinder;
import android.support.v7.app.NotificationCompat;
import android.webkit.MimeTypeMap;
import android.widget.RemoteViews;

import com.mckuai.imc.Bean.Ad;
import com.mckuai.imc.R;
import com.umeng.analytics.MobclickAgent;

import java.io.File;

import okhttp3.OkHttpClient;

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
    private OkHttpClient client;

    private final String action_download_start = "ACTION_MCDOWNLOAD_START";
    private final String action_download_pause = "ACTION_MCDOWNLOAD_PAUSE";
    private final String action_download_cancle = "ACTION_MCDOWNLOAD_CANCLE";
    private final int id = 57361;

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
                setFileInfo(ad);
                download(ad);
            }
        }
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        unRegisterReceiver();
        super.onDestroy();
    }

    private void registerReceiver() {
        if (null == receiver) {
            receiver = new MCDownloadReciver();
        }
        registerReceiver(receiver, new IntentFilter(""));
    }

    private void unRegisterReceiver() {
        if (null != receiver) {
            unregisterReceiver(receiver);
            receiver = null;
        }
    }

    private void initNotification() {
        android.os.Debug.waitForDebugger();
        if (Build.VERSION.SDK_INT > 15) {
            RemoteViews contentView = new RemoteViews(getPackageName(), R.layout.notification_download_big);
            contentView.setImageViewUri(R.id.download_cover, Uri.parse(ad.getImageUrl()));
            contentView.setTextViewText(R.id.download_title, ad.getDownName());
            contentView.setProgressBar(R.id.download_progress, 100, 0, false);
            contentView.setOnClickPendingIntent(R.id.download_opration, getDefaultIntent());
            builder = new NotificationCompat.Builder(getApplicationContext());
            builder.setSmallIcon(android.R.drawable.stat_sys_download)
                    .setDeleteIntent(getStopIntent())
                    .setContentTitle(ad.getDownName())
                    .setContentText("正在下载，请稍候...");
            notification = builder.build();
            notification.bigContentView = contentView;

            //notification.contentIntent = getDefaultIntent();
        } else {
            builder = new NotificationCompat.Builder(getApplicationContext());
            builder.setProgress(100, 0, false)
                    .setSmallIcon(android.R.drawable.stat_sys_download)
                    .setDeleteIntent(getStopIntent())
                    .setContentIntent(getDefaultIntent())
                    .setContentTitle(ad.getDownName())
                    .setContentText("正在下载，请稍候...");
            notification = builder.build();
        }
        notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(id,notification);
    }

    private void updateNotificationProgress(int progress) {
        if (Build.VERSION.SDK_INT > 15) {
            notification.bigContentView.setProgressBar(R.id.download_progress, 100, progress, false);
        } else {
            builder.setProgress(100, progress, false);
            notificationManager.notify(id,builder.build());
        }
    }

    private void updateNotificationState(){
        android.os.Debug.waitForDebugger();
        if (Build.VERSION.SDK_INT > 15){
            /*switch (downloadTask.getStatus()){
                case FileDownloadStatus.completed:
                    //完成
                    notification.bigContentView.setProgressBar(R.id.download_progress, 100, 100, false);
                    notification.bigContentView.setTextViewText(R.id.download_title, ad.getDownName() + "下载完成，点击安装！");
                    notification.bigContentView.setImageViewResource(R.id.download_opration,android.R.drawable.stat_sys_download_done);
                    break;
                case FileDownloadStatus.paused:
                    //暂停
                    notification.bigContentView.setImageViewResource(R.id.download_opration,android.R.drawable.ic_media_play);
                    break;
                case FileDownloadStatus.error:
                    //出错
                    notification.bigContentView.setImageViewResource(R.id.download_opration,android.R.drawable.ic_media_play);
                    notification.bigContentView.setTextViewText(R.id.download_title, ad.getDownName() + "下载失败，点击重试！");
                    break;
                default:
                    if (downloadTask.getStatus() >= FileDownloadStatus.pending && downloadTask.getStatus() <= FileDownloadStatus.retry){
                        //正常下载
                        notification.bigContentView.setTextViewText(R.id.download_title, ad.getDownName());
                        notification.bigContentView.setImageViewResource(R.id.download_opration, android.R.drawable.ic_media_pause);
                    }
            }*/
        } else {
            /*switch (downloadTask.getStatus()){
                case FileDownloadStatus.completed:
                    //完成
                    builder.setProgress(100,100,false);
                    builder.setContentText("下载完成，点击安装！");
                    break;
                case FileDownloadStatus.paused:
                    //暂停
                    builder.setContentText("已暂停，点击继续下载！");
                    break;
                case FileDownloadStatus.error:
                    //出错
                    builder.setContentText("下载失败，点击重试！");
                    break;
                default:
                    if (downloadTask.getStatus() >= FileDownloadStatus.pending && downloadTask.getStatus() <= FileDownloadStatus.retry){
                        //正常下载
                        builder.setContentText("正在下载，请稍候...");
                    }
            }*/
            notification = builder.build();
        }
        notificationManager.notify(id,notification);
    }


    private void setFileInfo(Ad ad) {
        android.os.Debug.waitForDebugger();
        File file = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
        if (!file.exists()) {
            file.mkdirs();
        }
        filePath = file.getPath() + "/";
        fileName = ad.getDownUrl().substring(ad.getDownUrl().lastIndexOf("/"));
        extension = MimeTypeMap.getFileExtensionFromUrl(ad.getDownUrl());
        apkFile = new File(filePath, fileName);
    }


    private void download(Ad ad) {
        android.os.Debug.waitForDebugger();
        /*if (null == downloadManager) {

            downloadManager = FileDownloader.getImpl();
            FileDownloadUtils.setDefaultSaveRootPath(filePath);
            //downloadManager.setGlobalPost2UIInterval(1000);//每隔1秒处理一次listener回调
            //downloadManager.setGlobalHandleSubPackageSize(2);
        }
        if (null == downloadTask) {
            downloadTask = downloadManager.create(ad.getDownUrl()).setPath(filePath).setListener(new FileDownloadListener() {
                @Override
                protected void pending(BaseDownloadTask task, int soFarBytes, int totalBytes) {
                    if (null != task){

                    }
                }

                @Override
                protected void progress(BaseDownloadTask task, int soFarBytes, int totalBytes) {
                    updateNotificationProgress(100 * soFarBytes / totalBytes);
                }

                @Override
                protected void blockComplete(BaseDownloadTask task) {
                    if (null != task){

                    }
                }

                @Override
                protected void completed(BaseDownloadTask task) {
                    notificationManager.cancel(id);
                    updateNotificationState();
                    MobclickAgent.onEvent(getApplicationContext(),"MCAD_Downloaded");
                    installApk();
                }

                @Override
                protected void paused(BaseDownloadTask task, int soFarBytes, int totalBytes) {
                    updateNotificationState();
                }

                @Override
                protected void error(BaseDownloadTask task, Throwable e) {
                    updateNotificationState();
                }

                @Override
                protected void warn(BaseDownloadTask task) {
                    if (null != task){

                    }
                }
            });
            downloadTask.start();
        }*/
    }



    private void stopDownload() {
        android.os.Debug.waitForDebugger();

        MobclickAgent.onEvent(getApplicationContext(),"MCAD_CancleDownload");
        notificationManager.cancel(id);
        stopSelf();
    }

    private void installApk() {
        android.os.Debug.waitForDebugger();
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_VIEW);
        intent.setDataAndType(Uri.fromFile(apkFile), "application/vnd.android.package-archive");
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }


    private PendingIntent getDefaultIntent(){
        android.os.Debug.waitForDebugger();
        Intent intent = new Intent();
        /*if (null != downloadTask){
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
        PendingIntent pendingIntent = PendingIntent.getBroadcast(getApplicationContext(),0,intent,0);
        return pendingIntent;
    }

    private PendingIntent getStopIntent(){
        android.os.Debug.waitForDebugger();
        Intent intent = new Intent(action_download_cancle);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(getApplicationContext(),0,intent,0);
        return pendingIntent;
    }


    public class MCDownloadReciver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            android.os.Debug.waitForDebugger();
            /*switch (intent.getAction()){
                case action_download_pause:
                    if (null != downloadTask){
                        downloadTask.pause();
                        updateNotificationState();
                    }

                    break;
                case action_download_start:
                    if (null != downloadTask){
                        downloadTask.start();
                        updateNotificationState();
                    }
                    break;
                case action_download_cancle:
                    stopDownload();
                    break;
                case Intent.ACTION_PACKAGE_ADDED:
                    MobclickAgent.onEvent(getApplicationContext(),"MCAD_Installed");
                    notificationManager.cancel(id);
                    stopSelf();
                    break;
            }*/
        }
    }

}
