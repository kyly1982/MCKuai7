package com.mckuai.imc.Widget;

import android.content.Context;
import android.graphics.Point;
import android.support.v4.widget.ViewDragHelper;
import android.support.v7.widget.AppCompatImageButton;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.AppCompatTextView;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.mckuai.imc.Base.MCKuai;
import com.mckuai.imc.Bean.Cartoon;
import com.mckuai.imc.Bean.User;
import com.mckuai.imc.R;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.umeng.socialize.utils.Log;

import java.util.ArrayList;

/**
 * Created by kyly on 2016/3/16.
 */
public class CompetitionLayout extends RelativeLayout implements View.OnClickListener{
    private ViewDragHelper mDragger;
    private AppCompatImageView middleView,cartoon_top,cartoon_bottom,bg_top,bg_bottom,bg_middle;
    private AppCompatTextView title;
    private LinearLayout root_top,root_bottom,cartoonlayout_top,cartoonlayout_bottom;
    private Point middle;
    private OnActionListener listener;
    private ImageLoader loader;
    private ArrayList<Cartoon> cartoons;
    private boolean isFirstSetData = true;
    private boolean isLoadingData = false;

    private int imageWidth;
    private int offset = 0;
    private boolean isVoted = false;
    private int drageState = 0;
    private int distanceY;
    private int drageDirection;
    private final int DRAGE_STOP=0;
    private final int DRAGE_UP = 1;
    private final int DRAGE_DOWN = 2;

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
        isVoted = false;
        if (null == cartoons || cartoons.isEmpty()){
            if (null != listener && !isLoadingData){
                isLoadingData = true;
                listener.EOF();
            } else if (isLoadingData){
                Toast.makeText(getContext(),"正在加载，请稍候...",Toast.LENGTH_SHORT).show();
            }
        } else {
            if (null != title){
                if (null == loader){
                    loader = ImageLoader.getInstance();
                }

                title.setText(getThemeName(cartoons.get(0).getContent()));
                loader.displayImage(cartoons.get(0).getImage(), cartoon_top);
                loader.displayImage(cartoons.get(1).getImage(),cartoon_bottom);
                cartoon_top.setTag(cartoons.get(0));
                cartoon_bottom.setTag(cartoons.get(1));

                showVoteUser(root_top, cartoons.get(0).getRewardList());
                showVoteUser(root_bottom, cartoons.get(1).getRewardList());
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

    public void setActionListener(OnActionListener listener){
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
            public void onViewDragStateChanged(int state) {
                super.onViewDragStateChanged(state);
                drageState = state;
            }

            @Override
            public void onViewPositionChanged(View changedView, int left, int top, int dx, int dy) {
                super.onViewPositionChanged(changedView, left, top, dx, dy);
                switch (drageState){
                    case 0:
                        dy = 0;
                        break;
                    case 1:
                        if (distanceY == 0){
                            if (dy > 0){
                                drageDirection = DRAGE_DOWN;
                            } else {
                                drageDirection = DRAGE_UP;
                            }
                        }
                        distanceY += dy;
                        setViewState(dy);
                        break;
                    case 2:
                        distanceY += dy;
                        if (distanceY == 0){
                            drageDirection = DRAGE_STOP;
                        }
                        setViewState(dy);
                        break;
                }
                if (changedView.getId() == middleView.getId()){
                    if (dy > 0){
                        Log.e("向下:"+distanceY+",dy="+dy);
                    } else {
                        Log.e("向上:"+distanceY+",dy="+dy);
                    }
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
        root_top = (LinearLayout) getChildAt(2);
        root_bottom = (LinearLayout) getChildAt(3);
        cartoonlayout_top = (LinearLayout) getChildAt(6);
        cartoonlayout_bottom = (LinearLayout) getChildAt(7);
        cartoon_top = (AppCompatImageView) cartoonlayout_top.getChildAt(0);
        cartoon_bottom = (AppCompatImageView) cartoonlayout_bottom.getChildAt(0);

        bg_top = (AppCompatImageView) getChildAt(4);
        bg_bottom = (AppCompatImageView) getChildAt(5);
        bg_middle = (AppCompatImageView) getChildAt(8);
        middleView = (AppCompatImageView) getChildAt(9);

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
            offset = (getResources().getDimensionPixelSize(R.dimen.competition_centerdivier_width) + getResources().getDimensionPixelSize(R.dimen.competition_imagewidth)) / 2;
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
                    showData();
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
            if (root.getChildCount() > 1){
                for (int i = 0; getChildCount() > 1;i++){
                    if (getChildAt(0) instanceof ImageView){
                        root.removeViewAt(0);
                    } else {
                        break;
                    }
                }
            }
            if (null != root.getChildAt(0) && getChildAt(0) instanceof AppCompatImageButton){

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

    private void setViewState(int dy){
        switch (drageState){
            case 0:
                //回到原位
                cartoonlayout_bottom.bringToFront();
                cartoonlayout_top.bringToFront();
                bg_middle.bringToFront();
                middleView.bringToFront();
                postInvalidate();
                if (isVoted){
                    showData();
                }
                break;
            case 1:
                //拖拽钻石
                if (dy > 0){
                    //向下拉
                    if (distanceY >= offset && null != listener && !isVoted){
                        listener.onVote((Cartoon) cartoon_bottom.getTag());
                        isVoted = true;
                    }
                } else {
                    if (distanceY >= offset && null != listener && !isVoted){
                        listener.onVote((Cartoon) cartoon_top.getTag());
                        isVoted = true;
                    }
                }
                break;
            case 2:
                //放开钻石
                break;
        }
        switch (drageDirection){
            case DRAGE_UP:
                bg_top.bringToFront();
                bg_top.setAlpha(getViewAlpha());
                bg_top.postInvalidate();
                break;
            case DRAGE_DOWN:
                bg_bottom.bringToFront();
                bg_bottom.setAlpha(getViewAlpha());
                bg_bottom.postInvalidate();
                break;
        }
    }

    private int getViewAlpha(){
        if (distanceY < offset){
            return (int) (255* distanceY / offset);
        }
        return (int)255;
    }

    private String getThemeName(String name){
        String title = null;
        if (null != name && !name.isEmpty()){
            title = new String(name);
            StringBuilder stringBuilder = new StringBuilder(name);
            int length = name.length();
            for (int i = 0;i < length - 1;i++){
                stringBuilder.insert(2*i + 1,"\n");
            }
            return stringBuilder.toString();
        }
        return title;
    }
}
