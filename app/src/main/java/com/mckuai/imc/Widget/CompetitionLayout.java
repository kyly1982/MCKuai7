package com.mckuai.imc.Widget;

import android.content.Context;
import android.graphics.Point;
import android.support.v4.widget.ViewDragHelper;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.AppCompatTextView;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.mckuai.imc.R;

/**
 * Created by kyly on 2016/3/16.
 */
public class CompetitionLayout extends RelativeLayout{
    private ViewDragHelper mDragger;
    private AppCompatImageView middleView,middleView_background;
    private AppCompatTextView title;
    private AppCompatButton vote_top,vote_bottom;
    private LinearLayout cartoon_top,cartoon_bottom;
    private Point top,middle,bottom;


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
/*
    public void setView(AppCompatImageView middle,AppCompatImageView middle_background,AppCompatImageView top,AppCompatImageView bottom){
        this.middleView = middle;
        this.middleView_background = middle_background;
        this.cartoon_top = top;
        this.cartoon_bottom = bottom;
    }*/


    private void initDragger(){
        mDragger = ViewDragHelper.create(this, 1.0f, new ViewDragHelper.Callback() {
            @Override
            public boolean tryCaptureView(View child, int pointerId) {
                switch (child.getId()){
                    case R.id.layout_top:
                    case R.id.layout_bottom:
                        adjustmentLayer(child);
                    case R.id.diamond_middle:
                        return true;
                    default:
                        return false;

                }
                /*if (id == R.id.diamond_middle_background || id == R.id.diamond_bottom || id == R.id.diamond_top){
                    return false;
                } else {
                    child.bringToFront();
                    if (null != middle && null != middleView_background) {
                        middleView_background.bringToFront();
                        middleView.bringToFront();
                    }
                    return true;
                }*/
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
                switch (releasedChild.getId()){
                    case R.id.cartoon_top:
                        mDragger.settleCapturedViewAt(top.x,top.y);
                        break;
                    case R.id.cartoon_bottom:
                        mDragger.settleCapturedViewAt(bottom.x,bottom.y);
                        break;
                    case R.id.diamond_middle:
                        mDragger.settleCapturedViewAt(middle.x,middle.y);
                        break;
                }
                    //mDragger.settleCapturedViewAt((int)-xvel , (int)-yvel);
                invalidate();
            }
        });
    }

    private void adjustmentLayer(View currentview){
        currentview.bringToFront();
        if (null != middle && null != middleView_background) {
            middleView_background.bringToFront();
            middleView.bringToFront();
        }
    }

    @Override
    public void computeScroll() {
        super.computeScroll();
        if (mDragger.continueSettling(true)){
            invalidate();
        }
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        cartoon_top = (LinearLayout) getChildAt(6);
        cartoon_bottom = (LinearLayout) getChildAt(7);
        middleView_background = (AppCompatImageView) getChildAt(8);
        middleView = (AppCompatImageView) getChildAt(9);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);
        if (null == top) {
            top = new Point(cartoon_top.getLeft(),cartoon_top.getTop());
            bottom = new Point(cartoon_bottom.getLeft(),cartoon_bottom.getTop());
            middle = new Point(middleView.getLeft(),middleView.getTop());
        }
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
