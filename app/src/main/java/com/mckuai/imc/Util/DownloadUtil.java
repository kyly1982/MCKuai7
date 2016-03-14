package com.mckuai.imc.Util;

import android.app.DownloadManager;
import android.content.Context;
import android.net.Uri;
import android.os.Environment;

import com.mckuai.imc.Bean.Ad;

import java.io.File;

/**
 * Created by kyly on 2016/3/9.
 */
public class DownloadUtil {

    public static void download(Context context,Ad ad){
        Uri uri = Uri.parse(ad.getDownUrl());
        DownloadManager downloadManager = (DownloadManager)context.getSystemService(Context.DOWNLOAD_SERVICE);
        File file = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
        if (!file.exists()){
            file.mkdirs();
        }
        String filePath = file.getPath();
        String fileName = ad.getDownUrl().substring(ad.getDownUrl().lastIndexOf("/"));
        filePath = filePath.substring(filePath.lastIndexOf("/"))+"/";

        DownloadManager.Request request = new DownloadManager.Request(uri);
        request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI | DownloadManager.Request.NETWORK_MOBILE);
        request.setAllowedOverRoaming(false);
        request.setDescription("正在下载" + ad.getDownName() + ",请稍候...");
        request.setTitle("下载" + ad.getDownName());
        request.setMimeType("application/vnd.android.package-archive");
        request.setDestinationInExternalPublicDir(filePath, fileName);
        request.allowScanningByMediaScanner();
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE | DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
        request.setVisibleInDownloadsUi(false);

        downloadManager.enqueue(request);
    }


}