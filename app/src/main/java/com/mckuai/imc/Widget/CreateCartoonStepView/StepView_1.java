package com.mckuai.imc.Widget.CreateCartoonStepView;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RelativeLayout;

import com.mckuai.imc.R;

/**
 * Created by kyly on 2016/1/25.
 */
public class StepView_1 extends RelativeLayout implements View.OnClickListener {
    private OnButtonClickListener listener;

    public interface OnButtonClickListener{
        public void onStoragerClicked();
        public void onPhotoClicked();
        public void onTakePhotoClicked();
    }

    public StepView_1(Context context) {
        super(context);
        initView(context);
    }

    public StepView_1(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView(context);
    }

    public StepView_1(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(context);
    }

    public void setOnButtonClickListener(OnButtonClickListener listener){
        this.listener = listener;
    }

    private void initView(Context context){
        View view = LayoutInflater.from(context).inflate(R.layout.createcartoon_step1,this,true);
        view.findViewById(R.id.createcartoon_mcstorager).setOnClickListener(this);
        view.findViewById(R.id.createcartoon_photo).setOnClickListener(this);
        view.findViewById(R.id.createcartoon_takephoto).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.createcartoon_mcstorager:
                if (null != listener){
                    listener.onStoragerClicked();
                }
                break;
            case R.id.createcartoon_photo:
                if (null != listener){
                    listener.onPhotoClicked();
                }
                break;
            case R.id.createcartoon_takephoto:
                if (null != listener){
                    listener.onTakePhotoClicked();
                }
                break;
        }
    }
}
