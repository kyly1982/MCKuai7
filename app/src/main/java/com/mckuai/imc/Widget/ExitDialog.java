package com.mckuai.imc.Widget;

import android.app.DialogFragment;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.AppCompatTextView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;

import com.mckuai.imc.Base.MCKuai;
import com.mckuai.imc.Bean.Ad;
import com.mckuai.imc.R;
import com.mckuai.imc.service.DownloadService;
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
                        //download(getActivity());
                        mListener.onDownloadPressed();
                        startDownloadService();
                        //download();
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
                    mListener.onPicturePressed();
                    if (null!= ad) {
                        //download(getActivity());
                        startDownloadService();
                        //download();
                    }
                    this.dismiss();
                    break;
            }
    }

    private void startDownloadService(){
        Intent intent = new Intent(getActivity(), DownloadService.class);
        Bundle bundle = new Bundle();
        bundle.putSerializable("AD",ad);
        intent.putExtras(bundle);
        getActivity().startService(intent);
    }

    private String getDownloadPath(){
        File file = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
        if (!file.exists()) {
            file.mkdirs();
        }
        return file.getPath()+"/";
    }




}
