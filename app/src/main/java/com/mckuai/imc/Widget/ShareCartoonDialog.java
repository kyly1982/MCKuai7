package com.mckuai.imc.Widget;

import android.app.DialogFragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.AppCompatImageButton;
import android.support.v7.widget.AppCompatImageView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.mckuai.imc.Bean.Cartoon;
import com.mckuai.imc.R;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.umeng.socialize.ShareAction;
import com.umeng.socialize.UMShareListener;
import com.umeng.socialize.bean.SHARE_MEDIA;
import com.umeng.socialize.media.UMImage;

/**
 * Created by kyly on 2016/4/6.
 */
public class ShareCartoonDialog extends DialogFragment implements View.OnClickListener,UMShareListener {
    private View view;
    private AppCompatImageView image;
    private AppCompatImageButton qZone, qqFriend, wxZone;
    private AppCompatImageButton close;

    private Cartoon cartoon;
    private OnDismiss listener;

    public interface OnDismiss {
        void onDismissed();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
//        return super.onCreateView(inflater, container, savedInstanceState);
        view = inflater.inflate(R.layout.dialog_publishcartoon_success, container, false);
        initView();
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        initView();
        showData();
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (!hidden && null != view) {
            showData();
        }
    }

    public void setData(Cartoon cartoon, OnDismiss listener) {
        this.cartoon = cartoon;
        this.listener = listener;
        if (getUserVisibleHint()) {
            showData();
        }
    }

    private void showData() {
        if (null != cartoon) {
            if (null != image) {
                ImageLoader loader = ImageLoader.getInstance();
                loader.displayImage(cartoon.getImage(), image);
            }

        }
    }

    private void initView() {
        if (null != view && null == image) {
            image = (AppCompatImageView) view.findViewById(R.id.cartoonimage);
            qqFriend = (AppCompatImageButton) view.findViewById(R.id.share_qq);
            qZone = (AppCompatImageButton) view.findViewById(R.id.share_qqzone);
            wxZone = (AppCompatImageButton) view.findViewById(R.id.share_wx);
            close = (AppCompatImageButton) view.findViewById(R.id.close);
            qqFriend.setOnClickListener(this);
            wxZone.setOnClickListener(this);
            qqFriend.setOnClickListener(this);
            view.setOnClickListener(this);
            close.setOnClickListener(this);
        }
    }

    @Override
    public void onDestroy() {
        if (null != listener) {
            listener.onDismissed();
        }
        super.onDestroy();
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.share_qq:
                new ShareAction(getActivity())
                        .setPlatform(SHARE_MEDIA.QQ)
                        .setCallback(this)
                        .withText("")
                        .withTitle("")
                        .withTargetUrl("")
                        .withMedia(new UMImage(getActivity(),cartoon.getImage()))
                        .share();
                dismiss();
                break;
            case R.id.share_qqzone:
                new ShareAction(getActivity())
                        .setPlatform(SHARE_MEDIA.QZONE)
                        .setCallback(this)
                        .withText("")
                        .withTitle("")
                        .withTargetUrl("")
                        .withMedia(new UMImage(getActivity(),cartoon.getImage()))
                        .share();
                dismiss();
                break;
            case R.id.share_wx:
                new ShareAction(getActivity())
                        .setPlatform(SHARE_MEDIA.WEIXIN_CIRCLE)
                        .setCallback(this)
                        .withTitle("")
                        .withTargetUrl("")
                        .withText("")
                        .withMedia(new UMImage(getActivity(),cartoon.getImage()))
                        .share();
                dismiss();
                break;
            case R.id.shareroot:
                dismiss();
                break;
            case R.id.close:
                dismiss();
                break;
        }
    }

    @Override
    public void onCancel(SHARE_MEDIA share_media) {

    }

    @Override
    public void onResult(SHARE_MEDIA share_media) {

    }

    @Override
    public void onError(SHARE_MEDIA share_media, Throwable throwable) {

    }
}
