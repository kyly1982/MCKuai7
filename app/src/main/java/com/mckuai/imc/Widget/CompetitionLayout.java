package com.mckuai.imc.Widget;

import android.content.Context;
import android.support.v4.widget.ViewDragHelper;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;

import com.mckuai.imc.R;

/**
 * Created by kyly on 2016/3/16.
 */
public class CompetitionLayout extends FrameLayout{
    private ViewDragHelper mDragger;

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





    private void initDragger(){
        mDragger = ViewDragHelper.create(this, 1.0f, new ViewDragHelper.Callback() {
            @Override
            public boolean tryCaptureView(View child, int pointerId) {
                int id = child.getId();
                if (id == R.id.diamond_middle_shandow || id == R.id.diamond_bottom || id == R.id.diamond_top){
                    return false;
                } else {
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
                super.onViewReleased(releasedChild, xvel, yvel);
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
