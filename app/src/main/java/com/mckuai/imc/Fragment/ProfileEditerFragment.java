package com.mckuai.imc.Fragment;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.widget.AppCompatAutoCompleteTextView;
import android.support.v7.widget.AppCompatEditText;
import android.support.v7.widget.AppCompatImageView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.LinearLayout;
import android.widget.TextView;

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
    private MCUser user;
    private ImageLoader loader;
    private AppCompatImageView usercover;
    private AppCompatAutoCompleteTextView useraddress;
    private AppCompatEditText usernick;
    private View view;
    private BaseActivity parentActivity;
    private LinearLayout uploadView;

    boolean isCoverChange = false;
    boolean isNickChange = false;
    boolean isAddressChange = false;
    boolean isCoverUpload = false;
    boolean isCoverUpdate = false;
    long lastBackPressTime = 0;

    private String coverUrl;
    private Bitmap newCover;
    private MCKuai application = MCKuai.instence;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (null == view) {
            view = inflater.inflate(R.layout.fragment_profile_editer, container, false);
            loader = ImageLoader.getInstance();
            user = MCKuai.instence.user;
            parentActivity = (BaseActivity) getActivity();
        }
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (null == usercover) {
            initView();
        }
        showData();
    }

    private void initView() {
        usercover = (AppCompatImageView) view.findViewById(R.id.usercover);
        useraddress = (AppCompatAutoCompleteTextView) view.findViewById(R.id.useraddr);
        usernick = (AppCompatEditText) view.findViewById(R.id.usernick);
        uploadView = (LinearLayout) view.findViewById(R.id.uploadview);

        usercover.setOnClickListener(this);
        usernick.setOnFocusChangeListener(this);
        useraddress.setOnFocusChangeListener(this);
        usernick.addTextChangedListener(this);

        usernick.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    handleEditNick();
                    parentActivity.toggleSoftKeyPad();
                    return true;
                } else {
                    return false;
                }
            }
        });

        useraddress.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    handleEditAddress();
                    parentActivity.toggleSoftKeyPad();
                    return true;
                } else {
                    return false;
                }
            }
        });
    }

    private void handleEditNick() {
        setFocus();
        if (usernick.getText().toString().length() == 0) {
            resetNickView(null);
            isNickChange = false;
        } else {
            if (usernick.getText().toString().equals(user.getNike())) {
                resetNickView(null);
                isNickChange = false;
            } else {
                resetNickView(usernick.getText().toString());
                isNickChange = true;
            }
        }
    }

    private void handleEditAddress() {
        setFocus();
        if (useraddress.getText().toString().trim().length() == 0) {
            resetAddressView(null);
            isAddressChange = false;
        } else if (useraddress.getText().toString().trim().equals(user.getAddr())) {
            resetAddressView(null);
            isAddressChange = false;
        } else {
            resetAddressView(useraddress.getText().toString().trim());
            isAddressChange = true;
        }
    }

    private void resetNickView(String nick) {
        if (null == nick) {
            usernick.setText(user.getNike());
            usernick.setTextColor(getResources().getColor(R.color.textColorPrimary));
        } else {
            if (nick.equals(user.getNike())) {
                usernick.setText(user.getNike());
                usernick.setTextColor(getResources().getColor(R.color.textColorPrimary));
            } else {
                usernick.setText(nick);
                usernick.setTextColor(getResources().getColor(R.color.textColorAccentr));
            }
        }
    }

    private void resetAddressView(String address) {
        if (null == address) {
            useraddress.setText(user.getAddr());
            useraddress.setTextColor(getResources().getColor(R.color.textColorPrimary));
        } else if (address.equals(user.getAddr())) {
            useraddress.setText(user.getAddr());
            useraddress.setTextColor(getResources().getColor(R.color.textColorPrimary));
        } else {
            useraddress.setText(address);
            useraddress.setTextColor(getResources().getColor(R.color.textColorAccentr));
        }
    }


    private void setFocus() {
        usercover.setFocusable(true);
        usercover.requestFocus();
    }

    public void showData() {
        loader.displayImage(user.getHeadImg(), usercover, MCKuai.instence.getCircleOptions());
        usernick.setText(user.getNike());
        useraddress.setText(user.getAddr());
        usernick.setHint("昵称");
    }

    private void changeCover() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, 0);
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
        opts.inSampleSize = computeSampleSize(opts, -1, 1080 * 1080);
        opts.inJustDecodeBounds = false;
        Bitmap bmp = null;
        try {
            bmp = BitmapFactory.decodeFile(picturePath, opts);
        } catch (OutOfMemoryError err) {
            ((BaseActivity) getActivity()).showMessage("图片太大了", null, null);
            return;
        }
        loader.displayImage(picturePath, usercover, MCKuai.instence.getCircleOptions());
        //usercover.setImageBitmap(bmp);
        //usercover.postInvalidate();
        //usercover.setImageBitmap(bmp);
//        usercover.setImageDrawable(new BitmapDrawable(bmp));
        newCover = bmp;
        isCoverChange = true;
    }


    public void upload() {
        if (isCoverChange) {
            if (!isCoverUpload) {
                uploadView.setVisibility(View.VISIBLE);
                uploadCover();
            } else if (!isCoverUpdate) {
                updateCover();
            }
            return;
        }
        if (isNickChange) {
            uploadView.setVisibility(View.VISIBLE);
            updateNick();
            return;
        }
        if (isAddressChange) {
            uploadView.setVisibility(View.VISIBLE);
            updateAddress();
            return;
        }
        if (uploadView.getVisibility() == View.VISIBLE) {
            uploadView.setVisibility(View.GONE);
            parentActivity.finish();
        }
    }

    private void uploadCover() {
        MCKuai.instence.netEngine.uploadUserCover(getActivity(), newCover, this);
    }

    private void updateCover() {
        MCKuai.instence.netEngine.updateUserCover(getActivity(), coverUrl, this);
    }

    private void updateNick() {
        MCKuai.instence.netEngine.updateNickName(getActivity(), usernick.getText().toString(), new MCNetEngine.OnUpdateUserNickResponseListener() {
            @Override
            public void onUpdateUserNickSuccess() {
                isNickChange = false;
                application.user.setNike(usernick.getText().toString());
                upload();
            }

            @Override
            public void onUpdateUserNickFailure(String msg) {
                showMessage("更新昵称失败，原因：" + msg, "重试", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        upload();
                    }
                });
            }
        });
    }


    private void updateAddress() {
        MCKuai.instence.netEngine.updateUserAddress(getActivity(), useraddress.getText().toString().trim(), new MCNetEngine.OnUpdateUserAddressResponseListener() {
            @Override
            public void onUpdateAddressSuccess() {
                isAddressChange = false;
                application.user.setAddr(useraddress.getText().toString().trim());
                upload();
            }

            @Override
            public void onUpdateAddressFailure(String msg) {
                showMessage("更新地址失败，原因：" + msg, "重试", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        upload();
                    }
                });
            }
        });
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
        int time = (int) (System.currentTimeMillis() - lastBackPressTime);
        if ((isCoverChange || isNickChange || isAddressChange)) {
            if (time > 3000) {
                lastBackPressTime = System.currentTimeMillis();
                parentActivity.showMessage("你的修改还未保存，是否保存？", "保存", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        upload();
                    }
                });
                return true;
            } else {
                return false;
            }
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
            case R.id.usernick:
                usernick.setEnabled(true);
                break;

        }
    }

    @Override
    public void onFocusChange(View v, boolean hasFocus) {
        if (!hasFocus) {
            switch (v.getId()) {
                case R.id.usernick:
                    handleEditNick();
                    break;
                case R.id.useraddr:
                    handleEditAddress();
                    break;
            }
        }
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
        showMessage("上传头像失败，原因：" + msg, "重试", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                upload();
            }
        });
    }

    @Override
    public void onUploadCoverSuccess(String url) {
        coverUrl = url;
        application.user.setHeadImg(url);
        isCoverUpload = true;
        upload();
    }

    @Override
    public void onUpdateUserCoverFailure(String msg) {
        showMessage("更新头像失败，原因：" + msg, "重试", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                upload();
            }
        });
    }

    @Override
    public void onUpdateUserCoverSuccess() {
        isCoverChange = false;
        isCoverUpload = false;
        upload();
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
