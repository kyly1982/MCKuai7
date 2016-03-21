package com.mckuai.imc.Widget;

import android.content.Context;
import android.support.v4.widget.ViewDragHelper;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.CardView;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.RelativeLayout;

import com.mckuai.imc.R;

/**
 * Created by kyly on 2016/3/16.
 */
public class CompetitionLayout extends RelativeLayout{
    private ViewDragHelper mDragger;
    private AppCompatImageView middle,middle_background;
    private CardView cartoon_top,cartoon_bottom;

    public CompetitionLayout(Context context) {
        super(context);
        initDragger();
    }

    public CompetitionLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        initDragger();
    }

    public CompetitionLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initDragger();
    }

    public void setView(AppCompatImageView middle,AppCompatImageView middle_background,CardView top,CardView bottom){
        this.middle = middle;
        this.middle_background = middle_background;
        this.cartoon_top = top;
        this.cartoon_bottom = bottom;
    }


    private void initDragger(){
        mDragger = ViewDragHelper.create(this, 1.0f, new ViewDragHelper.Callback() {
            @Override
            public boolean tryCaptureView(View child, int pointerId) {
                int id = child.getId();
                if (id == R.id.diamond_middle_background || id == R.id.diamond_bottom || id == R.id.diamond_top){
                    return false;
                } else {
                    child.bringToFront();
                    if (null != middle && null != middle_background) {
                        middle_background.bringToFront();
                        middle.bringToFront();
                    }
                    return true;
                }
            }

            @Override
            public int clampViewPositionHorizontal(View child, int left, int dx) {
                return left - dx;
            }

            @Override
            public int clampViewPositionVertical(View child, int top, int dy) {
                if (top < 0){
                    return 0;
                }
                if (top >(getHeight()) - child.getHeight()){
                    return  getHeight() - child.getHeight();
                }
                return top;
            }

            @Override
            public void onViewReleased(View releasedChild, float xvel, float yvel) {
                //super.onViewReleased(releasedChild, xvel, yvel);
                if (releasedChild == cartoon_top){
                    mDragger.settleCapturedViewAt((int)cartoon_top.getX(),(int)cartoon_top.getY());
                }
                //invalidate();
                postInvalidate();
            }
        });
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        return mDragger.shouldInterceptTouchEvent(ev);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        mDragger.processTouchEvent(event);
        return true;
    }


}
