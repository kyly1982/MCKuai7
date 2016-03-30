package com.mckuai.imc.Widget;

import android.content.Context;
import android.graphics.Point;
import android.support.v4.widget.ViewDragHelper;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.AppCompatTextView;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.mckuai.imc.Base.MCKuai;
import com.mckuai.imc.Bean.Cartoon;
import com.mckuai.imc.Bean.User;
import com.mckuai.imc.R;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.ArrayList;

/**
 * Created by kyly on 2016/3/16.
 */
public class CompetitionLayout extends RelativeLayout implements View.OnClickListener{
    private ViewDragHelper mDragger;
    private AppCompatImageView middleView,cartoon_top,cartoon_bottom,bg_top,bg_bottom;
    private AppCompatTextView title;
    private LinearLayout root_top,root_bottom;
    private Point middle;
    private OnActionListener listener;
    private ImageLoader loader;
    private ArrayList<Cartoon> cartoons;
    private boolean isFirstSetData = true;
    private boolean isLoadingData = false;

    private int imageWidth;

    public interface OnActionListener{
        void onShowDetile(Cartoon cartoon);
        void onVote(Cartoon cartoon);
        void onShowUser(User user);
        void EOF();
    }

    public void setData(ArrayList<Cartoon> cartoons){
        isLoadingData = false;
        this.cartoons = cartoons;
        if (isFirstSetData){
            showData();
        }
    }

    private void showData(){
        isFirstSetData = false;
        if (null == cartoons || cartoons.isEmpty()){
            if (null != listener && !isLoadingData){
                isLoadingData = true;
                listener.EOF();
            }
        } else {
            if (null != title){
                if (null == loader){
                    loader = ImageLoader.getInstance();
                }
                title.setText(cartoons.get(0).getContent());
                loader.displayImage(cartoons.get(0).getImage(),cartoon_top);
                loader.displayImage(cartoons.get(1).getImage(),cartoon_bottom);
                showVoteUser(root_top,cartoons.get(0).getRewardList());
                showVoteUser(root_bottom,cartoons.get(1).getRewardList());
                if (2 >= cartoons.size()){
                    cartoons.clear();
                    if (null != listener){
                        listener.EOF();
                    }
                } else {
                    cartoons.remove(0);
                    cartoons.remove(1);
                }
            }
        }
    }

    public void setOnActionListener(OnActionListener listener){
        this.listener = listener;
    }

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
                switch (child.getId()){
                    /*case R.id.cartoon_bottom:
                    case R.id.cartoon_top:
                    case R.id.layout_top:
                    case R.id.layout_bottom:
                        adjustmentLayer(child);*/
                    case R.id.diamond_middle:
                        return true;
                    default:
                        return false;
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
                switch (releasedChild.getId()){
                    /*case R.id.cartoon_top:
                        mDragger.settleCapturedViewAt(top.x,top.y);
                        break;
                    case R.id.cartoon_bottom:
                        mDragger.settleCapturedViewAt(bottom.x,bottom.y);
                        break;*/
                    case R.id.diamond_middle:
                        mDragger.settleCapturedViewAt(middle.x,middle.y);
                        break;
                }
                invalidate();
            }
        });
    }

/*    private void adjustmentLayer(View currentview){
        currentview.bringToFront();
        if (null != middle && null != middleView_background) {
            middleView_background.bringToFront();
            middleView.bringToFront();
        }
    }*/

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
        if (null == title) {
            initView();
        }
    }

    private void initView(){
        title = (AppCompatTextView) getChildAt(0);
        middleView = (AppCompatImageView) getChildAt(9);
        root_top = (LinearLayout) getChildAt(2);
        root_bottom = (LinearLayout) getChildAt(3);
        cartoon_top = (AppCompatImageView) ((LinearLayout) getChildAt(6)).getChildAt(0);
        cartoon_bottom = (AppCompatImageView) ((LinearLayout) getChildAt(7)).getChildAt(0);
        bg_top = (AppCompatImageView) getChildAt(4);
        bg_bottom = (AppCompatImageView) getChildAt(5);

        root_top.getChildAt(0).setOnClickListener(this);
        root_bottom.getChildAt(0).setOnClickListener(this);
        cartoon_top.setOnClickListener(this);
        cartoon_bottom.setOnClickListener(this);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);
        if (null == middle) {
            middle = new Point(middleView.getLeft(),middleView.getTop());
            imageWidth = getResources().getDimensionPixelSize(R.dimen.sidlewidth_competition);

            //bg_top.setTop(cartoon_top.getTop() + (cartoon_top.getBottom() - cartoon_top.getTop()) / 2);
            RelativeLayout.LayoutParams params = (LayoutParams) bg_top.getLayoutParams();
            params.setMargins(bg_top.getLeft(),(cartoon_top.getBottom() - bg_top.getBottom() - cartoon_top.getTop() + bg_top.getTop()) / 2,0,0);
           // bg_bottom.setTop(cartoon_bottom.getTop() + (cartoon_bottom.getBottom() - cartoon_bottom.getTop()) / 2);
            params = (LayoutParams) bg_bottom.getLayoutParams();
            params.setMargins(bg_bottom.getLeft(),0,0,(cartoon_bottom.getBottom() - bg_bottom.getBottom() - cartoon_bottom.getTop() + bg_bottom.getTop()) / 2);
            bg_top.postInvalidate();
            bg_bottom.postInvalidate();
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

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.cartoon_top:
                if (null != listener){
                    listener.onShowDetile((Cartoon) v.getTag());
                }
                break;
            case R.id.cartoon_bottom:
                if (null != listener){
                    listener.onShowDetile((Cartoon) v.getTag());
                }
                break;
            case R.id.vote_top:
                if (null != listener){
                    listener.onVote((Cartoon) v.getTag());
                }
                break;
            case R.id.vote_bottom:
                if (null != listener){
                    listener.onVote((Cartoon) v.getTag());
                }
                break;
        }
    }

    private void showVoteUser(LinearLayout root,ArrayList<User> users){
        if (null != root && null != users && !users.isEmpty()){
            if (null == loader){
                loader = ImageLoader.getInstance();
            }
            for (User user:users){
                final User temp = user;
                ImageView imageView = new ImageView(getContext());
                imageView.setLayoutParams(getImageLayoutParsms());
                imageView.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (null != listener) {
                            listener.onShowUser(temp);
                        }
                    }
                });
                loader.displayImage(user.getHeadImage(),imageView, MCKuai.instence.getCircleOptions());
                root.addView(imageView, 0);
            }
        }
    }

    private LinearLayout.LayoutParams getImageLayoutParsms(){
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(imageWidth,imageWidth);
        return params;
    }
}
