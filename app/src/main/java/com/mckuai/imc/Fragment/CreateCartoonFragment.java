package com.mckuai.imc.Fragment;

import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.v7.widget.AppCompatTextView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ViewFlipper;

import com.mckuai.imc.Base.BaseFragment;
import com.mckuai.imc.R;
import com.mckuai.imc.Widget.CreateCartoonStepView.StepView_1;
import com.mckuai.imc.Widget.CreateCartoonStepView.StepView_2;
import com.mckuai.imc.Widget.CreateCartoonStepView.StepView_3;
import com.mckuai.imc.Widget.CreateCartoonStepView.StepView_4;
import com.mckuai.imc.Widget.TouchableLayout.TouchableLayout;

import java.util.ArrayList;

public class CreateCartoonFragment extends BaseFragment implements StepView_4.OnShareButtonClickedListener,StepView_1.OnButtonClickListener,StepView_2.OnWidgetCheckedListener {

    private ViewFlipper flipper;
    private Bitmap bitmap;
    private ArrayList<String> talks;
    private View view;
    private TouchableLayout cartoonBuilder;
    private StepView_3 step3;
    private AppCompatTextView builderHint;



    public CreateCartoonFragment() {
    }


    public void showNextStep(int currentStep){
        flipper.showNext();
        switch (currentStep){
            case 0:
                builderHint.setText(R.string.createcartoon_hint_step1);
                break;
            case 1:
                builderHint.setText(R.string.createcartoon_hint_step2);
                break;
            case 2:
                builderHint.setText(R.string.createcartoon_hint_step3);
                break;
            case 3:
                builderHint.setText(R.string.createcartoon_hint_step4);
                break;
        }
    }

    public void upload(){

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (null != view && null != container){
            container.removeView(view);
        }
        view = inflater.inflate(R.layout.fragment_createcartoon,container,false);
        if (null == flipper){
            initView();
        }
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    private void initView(){
        flipper = (ViewFlipper) view.findViewById(R.id.createcartoon_operation);
        builderHint = (AppCompatTextView) view.findViewById(R.id.createcartoon_buildhint);
        cartoonBuilder = (TouchableLayout) view.findViewById(R.id.createcartoon_imagebuilder);
        StepView_1 step1 = new StepView_1(getActivity(),this);
        StepView_2 step2 = new StepView_2(getActivity(),this);
        step3 = new StepView_3(getActivity());
        StepView_4 step4 = new StepView_4(getActivity(),this);

        flipper.addView(step1);
        flipper.addView(step2);
        flipper.addView(step3);
        flipper.addView(step4);
    }

    private void getCartoon(){
        cartoonBuilder.setDrawingCacheEnabled(true);
        cartoonBuilder.buildDrawingCache();
        bitmap = cartoonBuilder.getDrawingCache();
        talks = step3.getTalks();
    }

    private void uploadImage(){

    }

    private void uploadCartoon(){

    }


    @Override
    public void onPhotoClicked() {
        //打开图库
    }

    @Override
    public void onStoragerClicked() {
        //打开MC背景库
    }

    @Override
    public void onTakePhotoClicked() {
        //拍照
    }

    @Override
    public void onWidgetChecked(int widgetId) {
        //已选择物品或者工具
        BitmapDrawable drawable = (BitmapDrawable) getResources().getDrawable(widgetId);
        if (null != drawable){
            bitmap = drawable.getBitmap();
            if (null != bitmap){
                cartoonBuilder.addBitMap(bitmap);
            }
        }
    }

    @Override
    public void onShareButtonClicked() {

    }
}
