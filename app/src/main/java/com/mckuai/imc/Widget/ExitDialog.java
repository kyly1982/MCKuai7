package com.mckuai.imc.Widget;

import android.app.DialogFragment;
import android.os.Bundle;
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
import com.nostra13.universalimageloader.core.ImageLoader;

/**
 * Created by kyly on 2016/3/8.
 */
public class ExitDialog extends DialogFragment implements View.OnClickListener {
    private View view;
    private AppCompatImageView mContent;
    private AppCompatTextView mTitle;
    private AppCompatButton mDownload;
    private AppCompatButton mExit;
    private ImageLoader mImageLoader;

    private OnClickListener mListener;

    private String title;
    private String btntitle;
    private String pictureurl;


    public interface OnClickListener {
        void onCanclePressed();

        void onExitPressed();

        void onDownloadPressed();
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
        mExit = (AppCompatButton) view.findViewById(R.id.exitdialog_exit);
        mTitle = (AppCompatTextView) view.findViewById(R.id.exitdialog_title);

        view.setOnClickListener(this);
        mExit.setOnClickListener(this);
        mDownload.setOnClickListener(this);
        showData();
    }


    public void init(Ad ad, OnClickListener listener) {
        mListener = listener;
        this.title = ad.getTitle();
        this.btntitle = ad.getDownName();
        this.pictureurl = ad.getImageUrl();
    }

    private void showData() {
        mTitle.setText(title);
        mDownload.setText("下载"+btntitle);
        if (null == mImageLoader) {
            mImageLoader = ImageLoader.getInstance();
        }
        //ImageSize size = new ImageSize(mContent.getWidth(), mContent.getHeight());
        mImageLoader.displayImage(pictureurl, mContent, MCKuai.instence.getNormalOptions());
    }

    @Override
    public void onClick(View v) {
        if (null != mListener) {
            switch (v.getId()) {
                case R.id.exitdialog:
                    this.dismiss();
                    mListener.onCanclePressed();
                    break;
                case R.id.exitdialog_download:
                    mListener.onDownloadPressed();
                    this.dismiss();
                    break;
                case R.id.exitdialog_exit:
                    mListener.onExitPressed();
                    this.dismiss();
                    break;
            }
        }
    }
}
