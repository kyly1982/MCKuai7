package com.mckuai.imc.service;

import android.app.DownloadManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.os.IBinder;
import android.webkit.MimeTypeMap;

import com.mckuai.imc.Bean.Ad;

import java.io.File;

public class DownloadService extends Service {

    private Ad ad;
    private long downloadTaskId = 0;
    private DownloadReceiver receiver;
    private String filePath;
    private File apkFile;
    private String fileName;
    private String extension;
    private DownloadManager downloadManager;
    private boolean isCompleted = false;

    public DownloadService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (ad == null){
            ad = (Ad) intent.getSerializableExtra("AD");
            if (null != ad){
                //registerReceiver();
                setFileInfo(ad);
                download(ad);
            } else {
                stopSelf();
            }
        }
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        unRegisterReceiver();
        super.onDestroy();
    }

    private void registerReceiver(){
        if (null == receiver){
            receiver = new DownloadReceiver(){
                @Override
                public void onClicked(long[] tasksId) {
                    if (0 < tasksId.length){
                        for (long id:tasksId){
                            if (id == downloadTaskId){
                                stopDownload();
                            }
                        }
                    }
                }

                @Override
                public void onCompleted(long id) {
                    if (id == downloadTaskId){
                        isCompleted = true;
                        installApk();
                        stopSelf();
                    }
                    super.onCompleted(id);
                }
            };
        }
    }

    private void unRegisterReceiver(){
        if (null != receiver){
            unregisterReceiver(receiver);
            receiver = null;
        }
    }

    private void setFileInfo(Ad ad){
        File file =Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
        if (!file.exists()){
            file.mkdirs();
        }
        filePath = file.getPath() +"/";
        fileName = ad.getDownUrl().substring(ad.getDownUrl().lastIndexOf("/"));
        extension = MimeTypeMap.getFileExtensionFromUrl(ad.getDownUrl());
        apkFile = new File(filePath,fileName);
    }


    private void download(Ad ad){
        Uri uri = Uri.parse(ad.getDownUrl());
        downloadManager = (DownloadManager)getApplicationContext().getSystemService(Context.DOWNLOAD_SERVICE);

        DownloadManager.Request request = new DownloadManager.Request(uri);
        request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI | DownloadManager.Request.NETWORK_MOBILE);
        request.setAllowedOverRoaming(false);
        request.setDescription("正在下载" + ad.getDownName() + ",请稍候...");
        request.setTitle("下载" + ad.getDownName());
        request.setMimeType("application/vnd.android.package-archive");
        request.setDestinationInExternalPublicDir("Download", fileName);
        request.allowScanningByMediaScanner();
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE | DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
        request.setVisibleInDownloadsUi(false);

        downloadTaskId = downloadManager.enqueue(request);
    }

    private void stopDownload(){
        if (0 != downloadTaskId){
            downloadManager.remove(downloadTaskId);
        }
        stopSelf();
    }

    private void installApk(){
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_VIEW);
        intent.setDataAndType(Uri.fromFile(apkFile), "application/vnd.android.package-archive");
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }
}
