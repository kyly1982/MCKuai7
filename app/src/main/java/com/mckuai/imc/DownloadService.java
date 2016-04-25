package com.mckuai.imc;

import android.app.DownloadManager;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Environment;
import android.os.IBinder;
import android.util.Log;

import com.umeng.analytics.MobclickAgent;

import java.io.File;
import java.io.StringReader;

public class DownloadService extends Service {
    private DownloadManager dm;
    private long enqueue;
    private BroadcastReceiver receiver;
    private String url,name,filePath,fileName;
    private File apkFile;

    public DownloadService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        url = intent.getStringExtra("URL");
        name = intent.getStringExtra("NAME");

        initDonwloadInfo();

        receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                switch (intent.getAction()) {
                    case DownloadManager.ACTION_DOWNLOAD_COMPLETE:
                        MobclickAgent.onEvent(getApplicationContext(),"MCAD_Downloaded");
                        MobclickAgent.onKillProcess(getApplicationContext());
                        intent = new Intent(Intent.ACTION_VIEW);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        intent.setDataAndType(Uri.fromFile(apkFile), "application/vnd.android.package-archive");
                        startActivity(intent);
                        stopSelf();
                        break;
                    case Intent.ACTION_PACKAGE_ADDED:
                        Log.e("","");
                        stopSelf();
                        break;
                    case Intent.ACTION_INSTALL_PACKAGE:
                        Log.e("","");
                        stopSelf();
                        break;
                }
            }
        };
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(DownloadManager.ACTION_DOWNLOAD_COMPLETE);
        intentFilter.addAction(Intent.ACTION_PACKAGE_ADDED);
        intentFilter.addAction(Intent.ACTION_INSTALL_PACKAGE);
        registerReceiver(receiver, intentFilter);
        startDownload();
        return Service.START_STICKY;
    }

    @Override
    public void onDestroy() {
        unregisterReceiver(receiver);
        super.onDestroy();
    }

    private void initDonwloadInfo(){
        com.umeng.socialize.utils.Log.e("initDonwloadInfo");
        File file = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
        if (!file.exists()) {
            file.mkdirs();
        }
        filePath = file.getPath() + "/";
        fileName = url.substring(url.lastIndexOf("/"));
        apkFile = new File(filePath,fileName);
        if (apkFile.isFile() && apkFile.exists()){
            apkFile.delete();
        }
    }

    private void startDownload() {
        dm = (DownloadManager) getSystemService(DOWNLOAD_SERVICE);
        DownloadManager.Request request = new DownloadManager.Request(
                Uri.parse(url));
        request.setMimeType("application/vnd.android.package-archive");
        request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, fileName);
        request.allowScanningByMediaScanner();
        request.setVisibleInDownloadsUi(true);
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
        enqueue = dm.enqueue(request);
    }
}
