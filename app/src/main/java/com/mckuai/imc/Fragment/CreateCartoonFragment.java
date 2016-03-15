package com.mckuai.imc.Fragment;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ViewFlipper;

import com.malinskiy.superrecyclerview.SuperRecyclerView;
import com.mckuai.imc.Activity.CartoonActivity;
import com.mckuai.imc.Adapter.CartoonSceneAdapter;
import com.mckuai.imc.Base.BaseFragment;
import com.mckuai.imc.Base.MCKuai;
import com.mckuai.imc.Bean.Cartoon;
import com.mckuai.imc.Bean.Lable;
import com.mckuai.imc.R;
import com.mckuai.imc.Util.BitmapUtil;
import com.mckuai.imc.Util.MCNetEngine;
import com.mckuai.imc.Widget.CreateCartoonStepView.StepView_1;
import com.mckuai.imc.Widget.CreateCartoonStepView.StepView_2;
import com.mckuai.imc.Widget.CreateCartoonStepView.StepView_3;
import com.mckuai.imc.Widget.CreateCartoonStepView.StepView_4;
import com.mckuai.imc.Widget.TouchableLayout.TouchableLayout;
import com.umeng.analytics.MobclickAgent;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class CreateCartoonFragment extends BaseFragment implements StepView_4.OnShareButtonClickedListener, StepView_1.OnButtonClickListener, StepView_2.OnWidgetCheckedListener,
        StepView_3.OnTalkAddedListener, CartoonSceneAdapter.OnSceneSelectedListener, MCNetEngine.OnUploadImageResponseListener, MCNetEngine.OnUploadCartoonResponseListener {

    private ViewFlipper flipper;
    private ArrayList<String> talks;
    private CartoonSceneAdapter adapter;
    private String title;
    private String imagePath;
    private String fileName;
    private boolean isUploading = false;


    private View view;
    private TouchableLayout cartoonBuilder;
    private AppCompatTextView builderHint;
    private SuperRecyclerView sceneList;
    private Point lastPoint;
    private OnBackgroundSetListener listener;
    private int talkCount = 0;

    public interface OnBackgroundSetListener {
        void onBackgroundSet();
        void onWidgetset();
    }

    public CreateCartoonFragment() {
    }

    public void setOnBackgroundSetListener(OnBackgroundSetListener listener) {
        this.listener = listener;
    }


    public void showNextStep(int currentStep) {
        flipper.showNext();
        switch (currentStep) {
            case 0:
                MobclickAgent.onEvent(getActivity(), "createCartoon_step1");
                builderHint.setText(R.string.createcartoon_hint_step1);
                break;
            case 1:
                MobclickAgent.onEvent(getActivity(), "createCartoon_step2");
                builderHint.setText(R.string.createcartoon_hint_step2);
                break;
            case 2:
                MobclickAgent.onEvent(getActivity(), "createCartoon_step3");
                builderHint.setText(R.string.createcartoon_hint_step3);
                break;
            case 3:
                MobclickAgent.onEvent(getActivity(), "createCartoon_step4");
                cartoonBuilder.frozenBuilder(true);
                builderHint.setText(R.string.createcartoon_hint_step4);
                break;
        }
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (null != view && null != container) {
            container.removeView(view);
        }
        view = inflater.inflate(R.layout.fragment_createcartoon, container, false);
        if (null == flipper) {
            initView();
        }
        return view;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (Activity.RESULT_OK == resultCode) {
            switch (requestCode) {
                case 10:
                    //相机
                    File file = new File(imagePath, fileName);
                    if (file.isFile() && file.exists()) {
                        MobclickAgent.onEvent(getActivity(), "createCartoon_takephoto_S");
                        Bitmap bitmap = BitmapUtil.decodeFile(imagePath + "/" + fileName, cartoonBuilder.getWidth(), cartoonBuilder.getHeight());
                        if (null != bitmap) {
                            cartoonBuilder.setBackgroundDrawable(null);
                            cartoonBuilder.setBitmapBackground(bitmap);
                            if (null != listener) {
                                listener.onBackgroundSet();
                            }
                            return;
                        }
                    }
                    MobclickAgent.onEvent(getActivity(), "createCartoon_takephoto_F");
                    showMessage("未获取到照片！", null, null);
                    break;
                case 11:
                    //相册
                    if (null != data && null != data.getData()) {
                        MobclickAgent.onEvent(getActivity(), "createCartoon_takepicture_S");
                        Bitmap bitmap = BitmapUtil.decodeFile(getActivity(), data.getData(), cartoonBuilder.getWidth(), cartoonBuilder.getHeight());
                        if (null != bitmap) {
                            cartoonBuilder.setBackgroundDrawable(null);
                            cartoonBuilder.setBitmapBackground(bitmap);
                            if (null != listener) {
                                listener.onBackgroundSet();
                            }
                            return;
                        }

                    }
                    showMessage("未获取到图片！", null, null);
                    MobclickAgent.onEvent(getActivity(), "createCartoon_takepicture_F");
                    break;
            }
        } else {
            //Snackbar.make(cartoonBuilder, "未获取到图片", Snackbar.LENGTH_SHORT).show();
        }
    }

    private void initView() {
        flipper = (ViewFlipper) view.findViewById(R.id.createcartoon_operation);
        builderHint = (AppCompatTextView) view.findViewById(R.id.createcartoon_buildhint);
        cartoonBuilder = (TouchableLayout) view.findViewById(R.id.createcartoon_imagebuilder);


        sceneList = (SuperRecyclerView) view.findViewById(R.id.createcartoon_scenelist);
        cartoonBuilder.setOnFocusChangeListener(new TouchableLayout.OnFocusChangeListener() {
            @Override
            public void onFocusChange(Point point) {
                lastPoint = point;
            }
        });
        cartoonBuilder.setBackgroundResource(R.mipmap.bg_builder_default);

        StepView_1 step1 = new StepView_1(getActivity(), this);
        StepView_2 step2 = new StepView_2(getActivity(), this);
        StepView_3 step3 = new StepView_3(getActivity(), this);
        StepView_4 step4 = new StepView_4(getActivity(), this);

        flipper.addView(step1);
        flipper.addView(step2);
        flipper.addView(step3);
        flipper.addView(step4);

        RecyclerView.LayoutManager manager = new StaggeredGridLayoutManager(3, StaggeredGridLayoutManager.VERTICAL);
        manager.offsetChildrenHorizontal(30);
        manager.offsetChildrenVertical(30);
        sceneList.setLayoutManager(manager);
    }

    private void showScene(ArrayList<Object> scenes) {
        if (null != scenes && !scenes.isEmpty()) {
            if (null == adapter) {
                adapter = new CartoonSceneAdapter(getActivity(), this);
                sceneList.setAdapter(adapter);
            }
            adapter.setData(scenes);
            sceneList.setVisibility(View.VISIBLE);
        }
    }

    private void hideScene() {
        sceneList.setVisibility(View.GONE);
    }

    private ArrayList<Object> getScene(boolean isStorager) {
        ArrayList<Object> scenes = null;
        if (isStorager) {
            Integer[] backgrounds = {
                    R.mipmap.cartoon_bg_black,
                    R.mipmap.cartoon_bg_blue,
                    R.mipmap.cartoon_bg_farm,
                    R.mipmap.cartoon_bg_green,
                    R.mipmap.cartoon_bg_lava,
                    R.mipmap.cartoon_bg_leak,
                    R.mipmap.cartoon_bg_oasis,
                    R.mipmap.cartoon_bg_orage,
                    R.mipmap.cartoon_bg_purple,
                    R.mipmap.cartoon_bg_red,
                    R.mipmap.cartoon_bg_river,
                    R.mipmap.cartoon_bg_sand,
                    R.mipmap.cartoon_bg_skyblue,
                    R.mipmap.cartoon_bg_sunrise,
                    R.mipmap.cartoon_bg_sunset,
                    R.mipmap.cartoon_bg_warm,
                    R.mipmap.cartoon_bg_wild,
                    R.mipmap.cartoon_bg_yellow,
                    R.mipmap.cartoon_bg_young};

            scenes = new ArrayList<>(19);
            for (Integer id : backgrounds) {
                scenes.add(id);
            }
        } else {

        }

        return scenes;
    }


    @Override
    public void onPhotoClicked() {
        //打开相册
        MobclickAgent.onEvent(getActivity(), "createCartoon_takepicture");
        Intent intent = new Intent(Intent.ACTION_PICK, null);
        intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
        startActivityForResult(intent, 11);
    }

    /**
     * 打开MC背景库
     */
    @Override
    public void onStoragerClicked() {
        MobclickAgent.onEvent(getActivity(), "createCartoon_openscene");
        showScene(getScene(true));
    }

    @Override
    public void onTakePhotoClicked() {
        //拍照
        MobclickAgent.onEvent(getActivity(), "createCartoon_takephoto");
        Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");
        Date date = new Date(System.currentTimeMillis());
        if (null == imagePath) {
            imagePath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES) + "/MCKuai";
        }
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd_HHmmss");
        fileName = sdf.format(date) + ".jpg";
        File file = new File(imagePath, fileName);
        if (file.exists()) {
            file.delete();
        } else {
            file.getParentFile().mkdirs();
        }

        intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(file));
        intent.putExtra(MediaStore.EXTRA_VIDEO_QUALITY, 0);
        intent.putExtra(MediaStore.EXTRA_MEDIA_TITLE, "拍取一张照片做为背景");
        intent.putExtra("outputX", cartoonBuilder.getWidth());
        intent.putExtra("outputY", cartoonBuilder.getWidth());
        startActivityForResult(intent, 10);
    }

    @Override
    public void onWidgetChecked(int widgetId) {
        //已选择物品或者工具
        MobclickAgent.onEvent(getActivity(), "createCartoon_checkwidget");
        BitmapDrawable drawable = (BitmapDrawable) getResources().getDrawable(widgetId);
        if (null != drawable) {
            Bitmap bitmap = drawable.getBitmap();
            if (null != bitmap) {
                cartoonBuilder.addBitMap(bitmap);
                if (null != listener){
                    listener.onWidgetset();
                }
            }
        }
    }

    @Override
    public void onShareButtonClicked() {

    }

    @Override
    public void onTitleChanged(String title) {
        this.title = title;
    }

    @Override
    public void onSend() {
        uploadCartoon(null);
    }

    @Override
    public void onSceneSelected(Object scene) {
        MobclickAgent.onEvent(getActivity(), "createCartoon_checkdscene");
        if (null != scene) {
            if (scene instanceof Integer) {
                cartoonBuilder.setBackgroundResource((int) scene);
            } else {

            }
        }
        hideScene();
    }

    @Override
    public void onTalkAdded(String talk) {
        MobclickAgent.onEvent(getActivity(), "createCartoon_addtalk");
        talkCount++;
        if (0 < cartoonBuilder.getWidgetCount()) {
            Lable lable = new Lable(talkCount,lastPoint, talk);
            cartoonBuilder.addLable(lable);
        } else {
            showMessage("你还未添加有人物或工具", null, null);
        }
    }

    public void uploadCartoon(Cartoon cartoon) {
        if (!isUploading) {
            if (null == cartoon) {
                uploadImage();
                return;
            } else {
                MCKuai.instence.netEngine.uploadCartoon(getActivity(), cartoon, this);
            }
        } else {
            showMessage("正在发布中，请稍候再试！", null, null);
        }
    }

    private void uploadImage() {
        MobclickAgent.onEvent(getActivity(), "createCartoon_uploadpic");
        Bitmap bitmap = getCartoonBitmap();
        if (null != bitmap) {
            ArrayList<Bitmap> bitmaps = new ArrayList<>(1);
            bitmaps.add(bitmap);
            MCKuai.instence.netEngine.uploadImage(getActivity(), bitmaps, this);
        } else {
            Snackbar.make(cartoonBuilder, "错误，不能获取图片内容!", Snackbar.LENGTH_SHORT).show();
        }
    }

    private Bitmap getCartoonBitmap() {
        cartoonBuilder.setDrawingCacheEnabled(true);
        cartoonBuilder.buildDrawingCache();
        return cartoonBuilder.getDrawingCache();
    }


    @Override
    public void onUploadCartoonFailure(String msg) {
        isUploading = false;
        Snackbar.make(cartoonBuilder, msg, Snackbar.LENGTH_LONG).show();
        MobclickAgent.onEvent(getActivity(), "createCartoon_publish_F");
    }

    @Override
    public void onUploadCartoonSuccess(int cartoonId) {
        isUploading = false;
        MobclickAgent.onEvent(getActivity(), "createCartoon_publish_S");
        Cartoon cartoon = new Cartoon(cartoonId);
        Intent intent = new Intent(getActivity(), CartoonActivity.class);
        Bundle bundle = new Bundle();
        bundle.putSerializable(getResources().getString(R.string.cartoondetail_tag_cartoon), cartoon);
        intent.putExtras(bundle);
        getActivity().startActivity(intent);
        if (null != mOnFragmentEventListener) {
            mOnFragmentEventListener.onFragmentAction(null);
        }
    }

    @Override
    public void onImageUploadSuccess(String url) {
        MobclickAgent.onEvent(getActivity(), "createCartoon_uploadpic_S");
        Cartoon cartoon = new Cartoon(title, url, MCKuai.instence.user, null);
        uploadCartoon(cartoon);
    }

    @Override
    public void onImageUploadFailure(String msg) {
        MobclickAgent.onEvent(getActivity(), "createCartoon_uploadpic_F");
        showMessage(msg, null, null);
    }

    @Override
    public boolean onBackPressed() {
        if (null != sceneList && View.VISIBLE == sceneList.getVisibility()) {
            hideScene();
            return true;
        } else {
            return false;
        }
    }
}
