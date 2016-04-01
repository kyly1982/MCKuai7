package com.mckuai.imc.Widget;

import android.app.DialogFragment;
import android.content.Context;
import android.graphics.Point;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.AppCompatTextView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;

import com.mckuai.imc.R;

/**
 * Created by kyly on 2016/3/29.
 */
public class LeaderDialog  extends DialogFragment {
    private View view;
    private Point screenSize;
    private AppCompatImageView top,middle,bottom,finger;
    private AppCompatTextView hint_top,hint_bottom;
    private int step = 0;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NORMAL, android.R.style.Theme_Black_NoTitleBar_Fullscreen);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        if (null == view) {
            view = inflater.inflate(R.layout.fragment_leader, container, false);
        }
        WindowManager manager = (WindowManager) getActivity().getSystemService(Context.WINDOW_SERVICE);
        screenSize = new Point();
        manager.getDefaultDisplay().getSize(screenSize);
        initView();
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        initView();
    }

    private void initView(){
        if (null == top) {
            top = (AppCompatImageView) view.findViewById(R.id.lead_top);
            middle = (AppCompatImageView) view.findViewById(R.id.lead_center);
            bottom = (AppCompatImageView) view.findViewById(R.id.lead_bottom);
            finger = (AppCompatImageView) view.findViewById(R.id.lead_finger);
            hint_top = (AppCompatTextView) view.findViewById(R.id.lead_hint_top);
            hint_top = (AppCompatTextView) view.findViewById(R.id.lead_hint_bottom);
        }
    }

    private void showNext(){
        switch (step){
            case 0:
                break;
            case 1:
                break;
            case 2:
                break;
        }
    }

}
