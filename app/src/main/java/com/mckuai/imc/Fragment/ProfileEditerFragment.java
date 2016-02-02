package com.mckuai.imc.Fragment;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.design.widget.TextInputLayout;
import android.support.v7.widget.AppCompatAutoCompleteTextView;
import android.support.v7.widget.AppCompatEditText;
import android.support.v7.widget.AppCompatImageButton;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.mckuai.imc.Base.BaseActivity;
import com.mckuai.imc.Base.BaseFragment;
import com.mckuai.imc.Base.MCKuai;
import com.mckuai.imc.Bean.MCUser;
import com.mckuai.imc.R;
import com.mckuai.imc.Util.MCNetEngine;
import com.nostra13.universalimageloader.core.ImageLoader;

/**
 * Created by kyly on 2016/2/2.
 */
public class ProfileEditerFragment extends BaseFragment implements View.OnClickListener, View.OnFocusChangeListener, TextWatcher, MCNetEngine.OnUploadUserCoverResponseListener, MCNetEngine.OnUpdateUserCoverResponseListener {
    private MCUser user ;
    private ImageLoader loader;
    private AppCompatImageButton usercover;
    private AppCompatAutoCompleteTextView useraddress;
    private TextInputLayout usernick;
    private AppCompatEditText nickediter;
    private View view;

    boolean isChange = false;
    boolean isUpdata = false;

    private Bitmap newCover;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (null != view){
            container.removeView(view);
        }
        view = inflater.inflate(R.layout.fragment_profile_editer,container,false);
        loader = ImageLoader.getInstance();
        user = MCKuai.instence.user;
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (null == usercover){
            initView();
        }
        showData();
    }

    private void initView(){
        usercover = (AppCompatImageButton) view.findViewById(R.id.usercover);
        useraddress = (AppCompatAutoCompleteTextView) view.findViewById(R.id.useraddr);
        usernick = (TextInputLayout) view.findViewById(R.id.usernick);
        nickediter = (AppCompatEditText) usernick.getEditText();

        useraddress.setEnabled(false);
        nickediter.setEnabled(false);

        usercover.setOnClickListener(this);
        nickediter.setOnFocusChangeListener(this);
        useraddress.setOnFocusChangeListener(this);
        nickediter.addTextChangedListener(this);
    }

    public void showData(){
        loader.displayImage(user.getHeadImg(), usercover, MCKuai.instence.getCircleOptions());
        nickediter.setText(user.getNike());
        useraddress.setText(user.getAddr());
        usernick.setHint("昵称");
    }

    private void changeCover() {
//        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
//        startActivityForResult(intent, 0);
        updateCover(null);
    }

    private void getNewCover(Intent data) {
        Uri selectedImage = data.getData();
        String[] filePathColumn = {MediaStore.Images.Media.DATA};

        Cursor cursor = getActivity().getContentResolver().query(selectedImage, filePathColumn, null, null, null);
        cursor.moveToFirst();

        int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
        String picturePath = cursor.getString(columnIndex);
        cursor.close();

        // 获取图片
        BitmapFactory.Options opts = new BitmapFactory.Options();
        opts.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(picturePath, opts);
        opts.inSampleSize = computeSampleSize(opts, -1, 1080 * 700);
        opts.inJustDecodeBounds = false;
        Bitmap bmp = null;
        try {
            bmp = BitmapFactory.decodeFile(picturePath, opts);
        } catch (OutOfMemoryError err) {
            ((BaseActivity) getActivity()).showMessage("图片太大了", null, null);
            return;
        }
        loader.displayImage(picturePath, usercover, MCKuai.instence.getCircleOptions());
        usercover.setImageBitmap(bmp);
        usercover.postInvalidate();
        // isCoverChanged = true;
        newCover = bmp;
        uploadCover();
    }


    public void upload() {

    }

    private void uploadNick() {

    }

    private void uploadCover() {
        MCKuai.instence.netEngine.uploadUserCover(getActivity(), newCover, this);
    }

    private void updateCover(String url) {
        MCKuai.instence.netEngine.updateUserCover(getActivity(), "http://a.hiphotos.baidu.com/zhidao/pic/item/faf2b2119313b07e42e3be070fd7912397dd8c67.jpg", this);
    }

    private void uploadAddress() {

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (Activity.RESULT_OK == resultCode && 0 == requestCode) {
            if (null != data) {
                getNewCover(data);
            }
        }
    }

    @Override
    public boolean onBackPressed() {
        if (isChange && !isUpdata) {
            ((BaseActivity) getActivity()).showMessage("你的修改还未保存，是否退出？", "退出", new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    getActivity().finish();
                }
            });
            return true;
        } else {
            return false;
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.usercover:
                changeCover();
                break;
        }
    }

    @Override
    public void onFocusChange(View v, boolean hasFocus) {

    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {

    }

    @Override
    public void afterTextChanged(Editable s) {

    }

    @Override
    public void onUploadCoverFailure(String msg) {

    }

    @Override
    public void onUploadCoverSuccess(String url) {
        updateCover(url);
    }

    @Override
    public void onUpdateUserCoverFailure(String msg) {

    }

    @Override
    public void onUpdateUserCoverSuccess() {

    }

    // 加载大图时,计算缩放比例,以免出现OOM
    public static int computeSampleSize(BitmapFactory.Options options, int minSideLength, int maxNumOfPixels) {
        int initialSize = computeInitialSampleSize(options, minSideLength, maxNumOfPixels);

        int roundedSize;
        if (initialSize <= 8) {
            roundedSize = 1;
            while (roundedSize < initialSize) {
                roundedSize <<= 1;
            }
        } else {
            roundedSize = (initialSize + 7) / 8 * 8;
        }

        return roundedSize;
    }

    private static int computeInitialSampleSize(BitmapFactory.Options options, int minSideLength, int maxNumOfPixels) {
        double w = options.outWidth;
        double h = options.outHeight;

        int lowerBound = (maxNumOfPixels == -1) ? 1 : (int) Math.ceil(Math.sqrt(w * h / maxNumOfPixels));
        int upperBound = (minSideLength == -1) ? 128 : (int) Math.min(Math.floor(w / minSideLength),
                Math.floor(h / minSideLength));

        if (upperBound < lowerBound) {
            // return the larger one when there is no overlapping zone.
            return lowerBound;
        }

        if ((maxNumOfPixels == -1) && (minSideLength == -1)) {
            return 1;
        } else if (minSideLength == -1) {
            return lowerBound;
        } else {
            return upperBound;
        }
    }
}
