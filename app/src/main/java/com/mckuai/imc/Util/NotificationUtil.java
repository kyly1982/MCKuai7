package com.mckuai.imc.Util;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.mckuai.imc.Bean.Ad;
import com.mckuai.imc.service.DLSerivce;


/**
 * Created by kyly on 2016/3/9.
 */
public class NotificationUtil {
    public static void startDownload(Context context,Ad ad){
        Intent intent = new Intent(context, DLSerivce.class);
        Bundle bundle = new Bundle();
        bundle.putSerializable("AD",ad);
        intent.putExtra("STOP",false);
        //intent.putExtra("path", Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)+"/");
        intent.putExtras(bundle);
        context.startService(intent);
    }



    public static void stopDownload(Context context,Ad ad){
        Intent intent = new Intent(context, DLSerivce.class);
        Bundle bundle = new Bundle();
        bundle.putSerializable("AD",ad);
        intent.putExtra("STOP",true);
        //intent.putExtra("path", Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)+"/");
        intent.putExtras(bundle);
        context.stopService(intent);
    }

}