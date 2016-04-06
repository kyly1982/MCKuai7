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
public class CompetitionLayout extends RelativeLayout implements View.OnClickListener {
    private ViewDragHelper mDragger;
    private AppCompatImageView middleView, cartoon_top, cartoon_bottom, bg_top, bg_bottom, bg_middle;
    private AppCompatTextView title;
    private LinearLayout root_top, root_bottom;
//    private CardView root;
    //private RelativeLayout cartoonlayout_top, cartoonlayout_bottom;
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
    //private int drageDirection;
    private final int DRAGE_STOP = 0;
    private final int DRAGE_UP = 1;
    private final int DRAGE_DOWN = 2;

    private int topOffset,bottomOffset;

    public interface OnActionListener {
        void onShowDetile(Cartoon cartoon);

        void onVote(Cartoon cartoon);

        void onShowUser(User user);

        void EOF();
    }

    public void setData(ArrayList<Cartoon> cartoons) {
        isLoadingData = false;
        this.cartoons = cartoons;
        if (isFirstSetData) {
            showData();
        }
    }

    private void showData() {
        isFirstSetData = false;
        isVoted = false;
        bg_top.setAlpha(0);
        bg_bottom.setAlpha(0);
        if (null == cartoons || cartoons.isEmpty()) {
            if (null != listener && !isLoadingData) {
                isLoadingData = true;
                listener.EOF();
            } else if (isLoadingData) {
                Toast.makeText(getContext(), "正在加载，请稍候...", Toast.LENGTH_SHORT).show();
            }
        } else {
            if (null != title) {
                if (null == loader) {
                    loader = ImageLoader.getInstance();
                }

                title.setText(getThemeName(cartoons.get(0).getContent()));
                loader.displayImage(cartoons.get(0).getImage(), cartoon_top);
                loader.displayImage(cartoons.get(1).getImage(), cartoon_bottom);
                cartoon_top.setTag(cartoons.get(0));
                cartoon_bottom.setTag(cartoons.get(1));

                showVoteUser(root_top, cartoons.get(0).getRewardList());
                showVoteUser(root_bottom, cartoons.get(1).getRewardList());
                if (2 >= cartoons.size()) {
                    cartoons.clear();
                    if (null != listener) {
                        listener.EOF();
                    }
                } else {
                    cartoons.remove(0);
                    cartoons.remove(1);
                }
            }
        }
    }

    public void setActionListener(OnActionListener listener) {
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


    private void initDragger() {
        mDragger = ViewDragHelper.create(this, 1.0f, new ViewDragHelper.Callback() {
            @Override
            public boolean tryCaptureView(View child, int pointerId) {
                switch (child.getId()) {
                    case R.id.diamond_middle:
                        return true;
                    default:
                        return false;
                }
            }


            @Override
            public void onViewDragStateChanged(int state) {
                super.onViewDragStateChanged(state);
                boolean isReset = (state == 0 && drageState == 2 && isVoted);

                drageState = state;
                if (isReset){
                    isVoted = false;
                    showData();
                }
            }

            @Override
            public void onViewPositionChanged(View changedView, int left, int top, int dx, int dy) {

                distanceY += dy;
                //Log.e("top="+top+",\tdy="+dy + ",\tdistanceY="+distanceY +",\tisVoted="+isVoted);
                if (distanceY > 0) {
                    //向下
                    bg_bottom.setAlpha(getViewAlpha());
                } else {
                    //向上
                    bg_top.setAlpha(getViewAlpha());
                }
                switch (drageState) {
                    case 1:
                        if (distanceY > 0) {
                            //向下
                            if (!isVoted && null != listener && Math.abs(distanceY) >= offset) {
                                isVoted = true;
                                Log.e("喜欢下");
                                listener.onVote((Cartoon) cartoon_bottom.getTag());
                            }
                        } else {
                            //向上
                            if (!isVoted && null != listener && Math.abs(distanceY) >= offset){
                                isVoted = true;
                                Log.e("喜欢上");
                                listener.onVote((Cartoon) cartoon_top.getTag());
                            }
                        }

                        break;
                }
                super.onViewPositionChanged(changedView, left, top, dx, dy);
            }

            @Override
            public int clampViewPositionHorizontal(View child, int left, int dx) {
                return left - dx;
            }

            @Override
            public int clampViewPositionVertical(View child, int top, int dy) {
                if (top <  getTop() ) {
                    //return 0;
                    return getTop()  ;
                }
                if (top > (getBottom() ) ) {
                    return getBottom();
                }
                return top;
            }

            @Override
            public void onViewReleased(View releasedChild, float xvel, float yvel) {
                //super.onViewReleased(releasedChild, xvel, yvel);
                switch (releasedChild.getId()) {
                    case R.id.diamond_middle:
                        mDragger.settleCapturedViewAt(middle.x, middle.y);
                        break;
                }
                invalidate();
            }
        });
    }



    @Override
    public void computeScroll() {
        super.computeScroll();
        if (mDragger.continueSettling(true)) {
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

    private void initView() {
        title = (AppCompatTextView) findViewById(R.id.theme);
        root_top = (LinearLayout) findViewById(R.id.root_top);
        root_bottom = (LinearLayout) findViewById(R.id.root_bottom);
        //cartoonlayout_top = (RelativeLayout) findViewById(R.id.layout_top);
        //cartoonlayout_bottom = (RelativeLayout) findViewById(R.id.layout_bottom);
        cartoon_top = (AppCompatImageView) findViewById(R.id.cartoon_top);
        cartoon_bottom = (AppCompatImageView) findViewById(R.id.cartoon_top);

        bg_top = (AppCompatImageView) findViewById(R.id.diamond_top);
        bg_bottom = (AppCompatImageView) findViewById(R.id.diamond_bottom);
        bg_middle = (AppCompatImageView) findViewById(R.id.diamond_middle_background);
        middleView = (AppCompatImageView) findViewById(R.id.diamond_middle);

        bg_top.setAlpha(0);
        bg_bottom.setAlpha(0);

        root_top.getChildAt(0).setOnClickListener(this);
        root_bottom.getChildAt(0).setOnClickListener(this);
        cartoon_top.setOnClickListener(this);
        cartoon_bottom.setOnClickListener(this);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);
        //if (null == middle) {
            middle = new Point(middleView.getLeft(), middleView.getTop());
            imageWidth = getResources().getDimensionPixelSize(R.dimen.sidlewidth_competition);
            offset = (getResources().getDimensionPixelSize(R.dimen.competition_centerdivier_width) + getResources().getDimensionPixelSize(R.dimen.competition_imagewidth)) / 2;
            RelativeLayout.LayoutParams params = (LayoutParams) bg_top.getLayoutParams();
            params.setMargins(bg_top.getLeft(), (cartoon_top.getBottom() - bg_top.getBottom() - cartoon_top.getTop() + bg_top.getTop()) / 2, 0, 0);
            params = (LayoutParams) bg_bottom.getLayoutParams();

            params.setMargins(bg_bottom.getLeft(), 0, 0, (cartoon_bottom.getBottom() - bg_bottom.getBottom() - cartoon_bottom.getTop() + bg_bottom.getTop()) / 2);
            bg_top.postInvalidate();
            bg_bottom.postInvalidate();

            topOffset = getResources().getDimensionPixelOffset(R.dimen.competition_imagewidth) / 2;
            bottomOffset = topOffset;
        //}
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
        switch (v.getId()) {
            case R.id.cartoon_top:
                if (null != listener) {
                    listener.onShowDetile((Cartoon) v.getTag());
                }
                break;
            case R.id.cartoon_bottom:
                if (null != listener) {
                    listener.onShowDetile((Cartoon) v.getTag());
                }
                break;
            case R.id.vote_top:
                if (null != listener) {
                    listener.onVote((Cartoon) v.getTag());
                    showData();
                }
                break;
            case R.id.vote_bottom:
                if (null != listener) {
                    listener.onVote((Cartoon) v.getTag());
                    showData();
                }
                break;
        }
    }

    private void showVoteUser(LinearLayout root, ArrayList<User> users) {
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

    private LinearLayout.LayoutParams getImageLayoutParsms() {
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(imageWidth, imageWidth);
        return params;
    }


    private int getViewAlpha() {
        if (distanceY < offset) {
            return Math.abs(255 * distanceY / offset);
        }
        return 255;
    }

    private String getThemeName(String name) {
        String title = null;
        if (null != name && !name.isEmpty()) {
            title = new String(name);
            StringBuilder stringBuilder = new StringBuilder(name);
            int length = name.length();
            for (int i = 0; i < length - 1; i++) {
                stringBuilder.insert(2 * i + 1, "\n");
            }
            return stringBuilder.toString();
        }
        return title;
    }
}
