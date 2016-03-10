package com.mckuai.imc.service;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Environment;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.FileAsyncHttpResponseHandler;
import com.mckuai.imc.Bean.Ad;
import com.mckuai.imc.R;
import com.umeng.analytics.MobclickAgent;

import org.apache.http.Header;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;


public class DLSerivce extends Service {
    private NotificationManager nm;
    private NotificationCompat.Builder builder;
    private AsyncHttpClient client;
    private Ad ad;
    private DownloadReceiver receiver;
    private boolean isDownload = false;
//    private String path;
    //private DLManager manager;
    private long lasttime = 0;
    private File apkFile;
    private String error;
    private int lastprogress=0;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public void onDestroy() {
        unRegisterDownloadReceiver();
        cancleDownload(false);
        super.onDestroy();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        //android.os.Debug.waitForDebugger();
        boolean isStop = intent.getBooleanExtra("STOP", false);
        ad = (Ad) intent.getSerializableExtra("AD");
        if (isStop) {
            cancleDownload(true);
        } else {
            if (null != ad) {
                if (!isDownload) {
                    registerDownloadReceiver();
                    initNotification();
                    //startDownloadTask();
                    downloadFile();
                    isDownload = true;
                }

            } else {
                stopSelf();
            }
        }
        return super.onStartCommand(intent, flags, startId);
    }

    private void registerDownloadReceiver() {
        //android.os.Debug.waitForDebugger();
        if (null == receiver) {
            receiver = new DownloadReceiver() {
                @Override
                public void onReceive(Context context, Intent intent) {
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

                @Override
                public void onCancle() {
                    //android.os.Debug.waitForDebugger();
                    cancleDownload(true);
                }

                @Override
                public void onInstall() {
                    //android.os.Debug.waitForDebugger();
                    hideNotification();
                    installFile(apkFile);
                }
            };
            this.registerReceiver(receiver, getFilter());
        }
    }

    private void unRegisterDownloadReceiver() {
        if (null != receiver) {
            this.unregisterReceiver(receiver);
        }
    }

    private IntentFilter getFilter() {
        return new IntentFilter("DOWNLOAD_FILTER");
    }

    private void initNotification() {
        if (null == nm) {
            nm = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            builder = new NotificationCompat.Builder(getApplicationContext());
            builder.setSmallIcon(R.mipmap.ic_launcher)
                    .setContentTitle("下载" + ad.getDownName())
                    .setContentText("准备下载，请稍候...")
                    .setProgress(100, 0, false)
                    .setDeleteIntent(getStopDownloadIntent())
                    .setOngoing(false);

            nm.notify(ad.getId(), builder.build());
        }
    }

    private void hideNotification() {
        if (null != nm) {
            nm.cancel(ad.getId());
        }
    }

    private void updateNotification(int type) {
        switch (type) {
            case 0:
                builder.setContentTitle("正在下载，请稍候...");
                break;
            case 1:
                MobclickAgent.onEvent(getApplicationContext(), "ExitDialog_DS");
                builder.setProgress(0, 0, false);
                builder.setContentText("下载完成!");
                builder.setContentTitle("点击安装" + ad.getDownName());
                builder.setContentIntent(getInstallFileIntent());
                nm.notify(ad.getId(), builder.build());
                break;
            case 2:
                MobclickAgent.onEvent(getApplicationContext(), "ExitDialog_DE");
                builder.setProgress(0, 0, false);
                builder.setContentTitle("下载失败！");
                builder.setContentText(error);
                nm.notify(ad.getId(), builder.build());
                stopSelf();
                break;
            case 3:
                builder.setProgress(100,lastprogress,false);
                break;
        }
        nm.notify(ad.getId(), builder.build());
    }

    private void startDownloadTask() {
        if (!isDownload) {
            if (null == client) {
                client = new AsyncHttpClient();
            }
            Thread thread = new Thread(new Runnable() {
                @Override
                public void run() {
                    downloadFile();
                }
            });
            thread.start();
            isDownload = true;
        }
    }


    private void downloadFile() {
        if (isDownload){
            return;
        }
        if (null == client){
            client = new AsyncHttpClient();
        }
        String[] allowedContentTypes = new String[]{"application/vnd.android.package-archive"};
        String path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS) + "/";
        String fileName = ad.getDownUrl().substring(ad.getDownUrl().lastIndexOf("/"));
        apkFile = new File(path, fileName);
        client.get(ad.getDownUrl(), new FileAsyncHttpResponseHandler(apkFile) {
            @Override
            public void onStart() {
                //super.onStart();
                updateNotification(0);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, File file) {
                error = "下载文件失败，原因：" + throwable.getLocalizedMessage();
                updateNotification(2);
                isDownload = false;
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, File file) {
                isDownload = false;
                if (statusCode == 200 && null != file && file.exists()) {
                    apkFile = file;
                    if (null != apkFile) {
                        updateNotification(1);
                    } else {
                        error = "保存文件失败！";
                        updateNotification(2);
                    }
                }
            }

            @Override
            public void onProgress(long bytesWritten, long totalSize) {
                //super.onProgress(bytesWritten, totalSize);
                long time = System.currentTimeMillis();
                if (time - lasttime > 1000) {
                    int progress = (int) (100 * bytesWritten / totalSize);
                    if (progress > lastprogress + 5 || progress == 100 ) {
                        updateNotification(3);
                        lasttime = time;
                        lastprogress = progress;
                    }
                }
            }

        });
        /*client.get(ad.getDownUrl(), new BinaryHttpResponseHandler(allowedContentTypes) {

            @Override
            public void onStart() {
                super.onStart();
                updateNotification(0);
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] binaryData) {
                isDownload = false;
                if (statusCode == 200 && null != binaryData && binaryData.length > 10) {
                    saveFile(binaryData);
                    if (null != apkFile) {
                        updateNotification(1);
                    } else {
                        error = "保存文件失败！";
                        updateNotification(2);
                    }
                }
            }


            @Override
            public void onProgress(long bytesWritten, long totalSize) {
                super.onProgress(bytesWritten, totalSize);
                long time = System.currentTimeMillis();
                if (time - lasttime > 1000) {
                    int progress = (int) (100 * bytesWritten / totalSize);
                    builder.setProgress(100, progress, false);
                    nm.notify(ad.getId(), builder.build());
                    lasttime = time;
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] binaryData, Throwable errorCode) {
                error = "下载文件失败，原因：" + errorCode.getLocalizedMessage();
                updateNotification(2);
                isDownload = false;
            }
        });*/

    }


    private void cancleDownload(boolean stopServer) {
        if (null != client) {
            hideNotification();
            client.cancelAllRequests(true);
           // manager.dlCancel(ad.getDownUrl());
            if (stopServer) {
                stopSelf();
            }
        }
    }

    private void saveFile(byte[] binaryData) {
        String path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS) + "/";
        String fileName = ad.getDownUrl().substring(ad.getDownUrl().lastIndexOf("/"));
        apkFile = new File(path, fileName);
        if (null != apkFile) {
            if (apkFile.exists() && apkFile.isFile()) {
                apkFile.delete();
            }
            try {
                FileOutputStream outputStream = new FileOutputStream(apkFile);
                BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(outputStream);
                bufferedOutputStream.write(binaryData);
                bufferedOutputStream.flush();
                outputStream.flush();
                if (null != bufferedOutputStream) {
                    bufferedOutputStream.close();
                }
                if (null != outputStream) {
                    outputStream.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
                apkFile = null;
            }
        }
    }


    private void installFile(File file) {
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_VIEW);
        intent.setDataAndType(Uri.fromFile(file), "application/vnd.android.package-archive");
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

/*    private PendingIntent getStopServiceIntent() {
        Intent intent = new Intent(getApplicationContext(), DLSerivce.class);
        intent.putExtra("STOP", true);
        return PendingIntent.getService(getApplicationContext(), 0, intent, PendingIntent.FLAG_CANCEL_CURRENT);
    }
    private PendingIntent getInstallIntent() {
        Intent intent = new Intent(getApplicationContext(), DownloadReceiver.class);
        intent.putExtra("operationType", 2);
        intent.setAction("DOWNLOAD_FILTER");
        return PendingIntent.getBroadcast(getApplicationContext(), 0, intent, 0);
    }*/

    private PendingIntent getStopDownloadIntent() {
        Intent intent = new Intent(getApplicationContext(), DownloadReceiver.class);
        intent.putExtra("operationType", 1);
        intent.setAction("DOWNLOAD_FILTER");
        return PendingIntent.getBroadcast(getApplicationContext(), 0, intent, 0);
    }


    private PendingIntent getInstallFileIntent() {
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_VIEW);
        intent.setDataAndType(Uri.fromFile(apkFile), "application/vnd.android.package-archive");
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        return PendingIntent.getActivity(getApplicationContext(), 0, intent, PendingIntent.FLAG_ONE_SHOT);
    }


}