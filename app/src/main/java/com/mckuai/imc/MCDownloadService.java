package com.mckuai.imc;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Binder;
import android.os.Environment;
import android.os.IBinder;
import android.os.RemoteException;
import android.support.v7.app.NotificationCompat;

import com.umeng.analytics.MobclickAgent;
import com.umeng.socialize.utils.Log;

import java.io.File;

import cn.finalteam.okhttpfinal.FileDownloadCallback;
import cn.finalteam.okhttpfinal.HttpRequest;

public class MCDownloadService extends Service {
    private String title,url;
    private String fileName,filePath;
    private String packageName,luncherActivity;
    private File apkFile;
    private MCDownloadReciver reciver;
    private NotificationManager notificationManager;
    private NotificationCompat.Builder builder;

    private final String action_download_start = "ACTION_MCDOWNLOAD_START";
    private final String action_download_pause = "ACTION_MCDOWNLOAD_PAUSE";
    private final String action_download_cancle = "ACTION_MCDOWNLOAD_CANCLE";
    private final String action_install = "ACTION_INSTALL";
    private final String action_run = "ACTION_RUN";
    private final int id = 57361;
    private long lastUpdateTime = 0;
    private int lastProgress = 0;

    public MCDownloadService() {
    }

    @Override
    public void onCreate() {
        android.os.Debug.waitForDebugger();
        System.out.println("onCreate");
        Log.e("onCreate");
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        android.os.Debug.waitForDebugger();
        System.out.println("onStartCommand");
        Log.e("onCreate");
        title = intent.getStringExtra("NAME");
        url = intent.getStringExtra("URL");
        url = "https://qd.myapp.com/myapp/qqteam/AndroidQQ/mobileqq_android.apk";
        initNotification();
        registerReceiver();
        initDonwloadInfo();
        startDownload();
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        //throw new UnsupportedOperationException("Not yet implemented");
        android.os.Debug.waitForDebugger();
        Log.e("onBind");
        System.out.println("onBind");
        return null;
      /*  return new DownloadInterface.Stub() {
            @Override
            public void addDownload(String appName, String downloadUrl) throws RemoteException {
                Log.e("addDownload");
                System.out.println("addDownload");
                if (null == title) {
                    title = appName;
                    url = downloadUrl;
                    url = "https://qd.myapp.com/myapp/qqteam/AndroidQQ/mobileqq_android.apk";
                    initNotification();
                    registerReceiver();
                    initDonwloadInfo();
                    startDownload();
                }
            }
        };*/
    }

    @Override
    public boolean onUnbind(Intent intent) {
        android.os.Debug.waitForDebugger();
        Log.e("onUnbind");
        System.out.println("onUnbind");
        return super.onUnbind(intent);
    }

    @Override
    public void onDestroy() {
        unRegisterReceiver();
        Log.e("onDestroy");
        super.onDestroy();
    }

    private void registerReceiver(){
        Log.e("registerReceiver");
        if (null == reciver) {
            System.out.println("registerReceiver");
            reciver = new MCDownloadReciver();
            IntentFilter filter = new IntentFilter(Intent.ACTION_PACKAGE_ADDED);
            filter.addAction(action_download_start);
            filter.addAction(action_download_pause);
            filter.addAction(action_download_cancle);
            //filter.addDataScheme("MCADDownloader");
            registerReceiver(reciver, filter);
        }
    }

    private void unRegisterReceiver(){
        Log.e("unRegisterReceiver");
        if (null != reciver){
            unregisterReceiver(reciver);
            reciver = null;
            MobclickAgent.onKillProcess(this);
        }
    }

    private void initNotification(){
        Log.e("initNotification");
        System.out.println("initNotification");
        if(null == notificationManager){
            notificationManager = (NotificationManager) super.getSystemService(NOTIFICATION_SERVICE);
        }
        builder = new NotificationCompat.Builder(this);
        builder.setProgress(100, 0, false)
                .setSmallIcon(android.R.drawable.stat_sys_download)
                .setOngoing(true)
                .setDeleteIntent(getStopIntent())
                //.setContentIntent(getDefaultIntent(0))//暂停，启动
                .setContentTitle(title)
                .setContentText("等待下载...");
        notificationManager.notify(id, builder.build());
    }

    private void updateNotification(int status){
        Log.e("updateNotification");
        if (0 <= status) {
            switch (status) {
                case 0:
                    builder.setContentText("下载失败，点击重试");
                    builder.setContentIntent(getDefaultIntent(2));//重新下载
                    break;
                case 1:
                    builder.setContentText("开始下载……");
                    break;
                case 2:
                    builder.setContentText("下载中，请稍候……");
                    builder.setProgress(100, lastProgress, false);
                    break;
                case 3:
                    builder.setContentText("下载完成，点击安装");
                    builder.setContentIntent(getDefaultIntent(3));//安装
                    break;
                case 4:
                    builder.setContentText("安装完成，点击运行");
                    builder.setContentIntent(getDefaultIntent(4));//运行
                    break;
            }
            notificationManager.notify(id, builder.build());
        } else {
            notificationManager.cancel(id);
        }
    }

    private void startDownload(){
        System.out.println("startDownload");
        Log.e("startDownload");
      /*  Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {*/
                HttpRequest.download(url, apkFile, new FileDownloadCallback() {
                    @Override
                    public void onStart() {
                        updateNotification(1);
                    }

                    @Override
                    public void onProgress(int progress, long networkSpeed) {

                        long time = System.currentTimeMillis();
                        if (progress != lastProgress && time > lastUpdateTime + 1000) {
                            updateNotification(2);
                            lastProgress = progress;
                            lastUpdateTime = time;
                        }
                    }

                    @Override
                    public void onDone() {
                        Log.e("下载完成");
                        lastProgress = 100;
                        updateNotification(3);
                        MobclickAgent.onEvent(getApplicationContext(),"MCAD_Downloaded");
                        installApk();
                    }

                    @Override
                    public void onFailure() {
                        updateNotification(0);
                        Log.e("下载失败");
                    }
                });
    /*        }
        });
        thread.start();*/

    }

    private void pauseDownload(){

    }

    private void stopDownload(){
        Log.e("stopDownload");
        HttpRequest.cancel(url);
        title = null;
        url = null;
        stopSelf();
    }

    private void initDonwloadInfo(){
        Log.e("initDonwloadInfo");
        File file = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
        if (!file.exists()) {
            file.mkdirs();
        }
        filePath = file.getPath() + "/";
        fileName = url.substring(url.lastIndexOf("/"));
        apkFile = new File(filePath,fileName);
    }

    private void installApk(){
        Log.e("installApk");
        System.out.println("installApk");
        android.os.Debug.waitForDebugger();
        if (null != apkFile && apkFile.exists()) {
            Intent intent = new Intent();
            intent.setAction(Intent.ACTION_VIEW);
            intent.setDataAndType(Uri.fromFile(apkFile), "application/vnd.android.package-archive");
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        }
    }

    private void runApk(){
        Log.e("runApk");
        System.out.println("runApk");
        Intent intent = new Intent();
        intent.setComponent(new ComponentName(packageName,luncherActivity));
        intent.setAction(Intent.ACTION_VIEW);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    private PendingIntent getStopIntent(){
        android.os.Debug.waitForDebugger();
        Intent intent = new Intent(action_download_cancle);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(getApplicationContext(), 0, intent, 0);
        return pendingIntent;
    }

    private PendingIntent getDefaultIntent(int status){
        android.os.Debug.waitForDebugger();
        Intent intent = new Intent();
        switch (status){
            case 0://暂停
                break;
            case 1://继续
                break;
            case 2://重新下载
                startDownload();
                break;
            case 3://安装
                installApk();
                break;
            case 4://运行
                runApk();
                break;
        }
        PendingIntent pendingIntent = PendingIntent.getBroadcast(getApplicationContext(), 0, intent, 0);
        return pendingIntent;
    }

   /* DownloadInterface.Stub dlStub = new DownloadInterface.Stub() {
        @Override
        public void addDownload(String appName, String downloadUrl) throws RemoteException {
            Log.e("addDownload");
            System.out.println("addDownload");
            if (null == title) {
                title = appName;
                url = downloadUrl;
                url = "https://qd.myapp.com/myapp/qqteam/AndroidQQ/mobileqq_android.apk";
                try {
                    initNotification();
                    registerReceiver();
                    initDonwloadInfo();
                } catch (Exception e){
                    e.printStackTrace();
                }
                startDownload();
            }
        }
    };*/

    public class MCDownloadReciver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.e("onReceive");
            System.out.println("onReceive");
            android.os.Debug.waitForDebugger();
            switch (intent.getAction()){
                case action_download_pause:
                    //暂停
                    pauseDownload();
                    break;
                case action_download_start:
                    //开始下载
                    //startDownload();
                    break;
                case action_download_cancle:
                    Log.e("收到停止下载消息");
                    stopDownload();
                    break;
                case action_install:
                    installApk();
                    break;
                case Intent.ACTION_PACKAGE_ADDED:
                    Log.e("收到安装完成消息");
                    if (null != intent.getData() && intent.getData().getScheme().equals("MCKuaiAdInstaller")) {
                        MobclickAgent.onEvent(getApplicationContext(), "MCAD_Installed");
                        updateNotification(4);
                    }
                    break;
                case action_run:
                    runApk();
                    updateNotification(-1);
                    break;
            }
        }
    }
}
