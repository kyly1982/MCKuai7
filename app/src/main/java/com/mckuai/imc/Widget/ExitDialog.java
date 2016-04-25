package com.mckuai.imc.Widget;

import android.app.DialogFragment;
import android.app.DownloadManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.AppCompatTextView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;

import com.mckuai.imc.Base.MCKuai;
import com.mckuai.imc.Bean.Ad;
import com.mckuai.imc.DownloadService;
import com.mckuai.imc.MCDownloadService;
import com.mckuai.imc.R;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.io.File;

/**
 * Created by kyly on 2016/3/8.
 */
public class ExitDialog extends DialogFragment implements View.OnClickListener {
    private View view;
    private AppCompatImageView mContent;
    private AppCompatTextView mTitle;
    private AppCompatButton mDownload;
    private AppCompatButton mCancle;
    private ImageLoader mImageLoader;

    private OnClickListener mListener;
    private Ad ad;




    public interface OnClickListener {
        void onCanclePressed();

        void onExitPressed();

        void onDownloadPressed();

        void onPicturePressed();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        getDialog().requestWindowFeature(Window.FEATURE_NO_TITLE);
        view = inflater.inflate(R.layout.dialog_exit, container, false);
        if (null != view && null == mContent) {
            initView();
        }
        return view;
    }

    private void initView() {
        mContent = (AppCompatImageView) view.findViewById(R.id.exitdialog_content);
        mDownload = (AppCompatButton) view.findViewById(R.id.exitdialog_download);
        mCancle = (AppCompatButton) view.findViewById(R.id.exitdialog_cancle);
        mTitle = (AppCompatTextView) view.findViewById(R.id.exitdialog_title);

        view.setOnClickListener(this);
        mCancle.setOnClickListener(this);
        mDownload.setOnClickListener(this);
        mContent.setOnClickListener(this);
            showData();
    }


    public void setData(Ad ad, OnClickListener listener) {
        this.ad = ad;
        this.mListener = listener;
        if (null != view && null != mTitle){
            showData();
        }
    }

    private void showData() {
        if (null != ad) {
            mTitle.setText(ad.getTitle());
            mDownload.setText("下载" + ad.getDownName());
            mCancle.setText("退出");
            if (null == mImageLoader) {
                mImageLoader = ImageLoader.getInstance();
            }
            mImageLoader.displayImage(ad.getImageUrl(), mContent, MCKuai.instence.getNormalOptions());
        } else {
            mTitle.setText("退出麦块");
            mDownload.setText("退出");
            mCancle.setText("取消");
            //mContent.setVisibility(View.GONE);
            mContent.setImageResource(R.mipmap.ic_launcher);
        }
    }

    @Override
    public void onClick(View v) {
            switch (v.getId()) {
                case R.id.exitdialog:
                    mListener.onCanclePressed();
                    this.dismiss();
                    break;
                case R.id.exitdialog_download:
                    if (null != ad) {
                        //startDownloadService();
                        //startDownload();
                        startDownloadServer();
                        mListener.onDownloadPressed();
                    } else {
                        mListener.onExitPressed();
                    }
                    this.dismiss();
                    break;
                case R.id.exitdialog_cancle:
                    if (null != ad){
                        mListener.onExitPressed();
                    } else {
                        mListener.onCanclePressed();
                    }
                    this.dismiss();
                    break;
                case R.id.exitdialog_content:
                    if (null!= ad) {
//                        startDownloadService();
//                        startDownload();
                        startDownloadServer();
                    }
                    this.dismiss();
                    mListener.onPicturePressed();
                    break;
            }
    }

    private void startDownloadServer(){
        Intent intent = new Intent(getActivity(), DownloadService.class);
        intent.putExtra("URL",ad.getDownUrl());
        intent.putExtra("NAME",ad.getDownName());
        getActivity().startService(intent);
    }

    private void startDownload(){
        DownloadManager manager = (DownloadManager) getActivity().getSystemService(Context.DOWNLOAD_SERVICE);
        DownloadManager.Request request = new DownloadManager.Request(Uri.parse(ad.getDownUrl()));
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
        request.setTitle(ad.getDownName());
        File file = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
        if (file.isDirectory() && !file.exists()){
            file.mkdirs();
        }
        String fileName = ad.getDownUrl().substring(ad.getDownUrl().lastIndexOf("/"));
        File apkFile = new File(file,fileName);
        if (apkFile.isFile() && apkFile.exists()){
            apkFile.delete();
        }
        request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS,fileName);
        request.setMimeType("application/vnd.android.package-archive");
        request.allowScanningByMediaScanner();
        request.setVisibleInDownloadsUi(true);
        manager.equals(request);
    }

    private void startDownloadService(){
//        Intent intent = new Intent("com.mckuai.imc.mcdownload");
        Intent intent = new Intent(getActivity(), MCDownloadService.class);//action需与清单文件中的一致
        intent.setAction("com.mckuai.imc.MCDownloadService");
//        intent.setPackage("com.mckuai.imc");
        intent.putExtra("URL",ad.getDownUrl());
        intent.putExtra("NAME",ad.getDownName());
        getActivity().startService(intent);
        //getActivity().startService(intent);
        //intent.setClass(getActivity().getApplicationContext(),MCDownloadService.class);//兼容5.0及更高版本
        //intent.setClassName("com.mckuai.imc.service","MCDownloadService");//针对5.0及之后必须调用
       /* if (null == conn){
            conn  = new ServiceConnection() {
                public void onServiceConnected(ComponentName name, IBinder service) {
                    Log.e("startDownloadService","onServiceConnected");
                    downloadService = DownloadInterface.Stub.asInterface(service);
                    try {
                        downloadService.addDownload(ad.getDownName(),ad.getDownUrl());
                    } catch (Exception e){
                        e.printStackTrace();
                        Log.e("startDownloadService","addDownload failed");
                    }
                }

                @Override
                public void onServiceDisconnected(ComponentName name) {
                    Log.e("startDownloadService","onServiceDisconnected");
                    downloadService = null;
                    conn = null;
                }
            };
        }
        //ComponentName name = getActivity().startService(intent);
        boolean result = getActivity().getApplicationContext().bindService(intent,conn, Context.BIND_AUTO_CREATE);*/

    }


}
