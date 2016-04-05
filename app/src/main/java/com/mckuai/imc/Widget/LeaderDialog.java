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
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;

import com.mckuai.imc.R;

/**
 * Created by kyly on 2016/3/29.
 */
public class LeaderDialog extends DialogFragment implements View.OnClickListener {
    private View view;
    private Point screenSize;
    private AppCompatImageView top, middle, bottom, finger;
    private AppCompatTextView hint_top, hint_bottom;
    private int step = 0;
    private float distanceY;
    private long time = 1500;
    private Animation flipup, flipdown;

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

    private void initView() {
        if (null == top) {
            top = (AppCompatImageView) view.findViewById(R.id.lead_top);
            middle = (AppCompatImageView) view.findViewById(R.id.lead_center);
            bottom = (AppCompatImageView) view.findViewById(R.id.lead_bottom);
            finger = (AppCompatImageView) view.findViewById(R.id.lead_finger);
            hint_top = (AppCompatTextView) view.findViewById(R.id.lead_hint_top);
            hint_bottom = (AppCompatTextView) view.findViewById(R.id.lead_hint_bottom);
            view.setOnClickListener(this);
        }
    }

    private void showNext() {
        switch (step) {
            case 0:
                finger.setVisibility(View.VISIBLE);
                hint_top.setText("向上滑动红石，支持上面作品");
                hint_bottom.setVisibility(View.VISIBLE);
                middle.startAnimation(getAnimation(true));
                finger.startAnimation(getAnimation(true));
                break;
            case 1:
                hint_top.setText("向下滑动红石，支持下面作品");
                middle.startAnimation(getAnimation(false));
                finger.setBackgroundResource(R.mipmap.flip_down);
                finger.startAnimation(getAnimation(false));
                break;
            case 2:
                this.dismiss();
                break;
        }
        step++;
    }

    @Override
    public void onClick(View v) {
        showNext();
    }

    public Animation getAnimation(boolean isUp) {
        if (isUp) {
            if (null == flipup) {
                if (distanceY == 0) {
                    getdistance();
                }
                flipup = new TranslateAnimation(0, 0, 0, -distanceY);
                flipup.setDuration(time);
            }
            return flipup;
        } else {
            if (null == flipdown) {
                if (distanceY == 0) {
                    getdistance();
                }
                flipdown = new TranslateAnimation(0,0,0, distanceY);
                flipdown.setDuration(time);
            }
            return flipdown;

        }
    }

    private void getdistance() {
        distanceY = (getResources().getDimensionPixelOffset(R.dimen.competition_imagewidth_middle) + getResources().getDimensionPixelOffset(R.dimen.competition_centerdivier_width_big)) / 2;
    }

}
