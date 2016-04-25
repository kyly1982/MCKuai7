package com.mckuai.imc.Widget;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.ViewDragHelper;
import android.support.v7.widget.AppCompatImageButton;
import android.support.v7.widget.AppCompatTextView;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.mckuai.imc.Base.MCKuai;
import com.mckuai.imc.Bean.Cartoon;
import com.mckuai.imc.Bean.User;
import com.mckuai.imc.R;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;

import java.util.ArrayList;

/**
 * Created by kyly on 2016/3/16.
 */
public class CompetitionLayout extends ViewGroup implements View.OnClickListener {
    private ViewDragHelper mDragger;
    private ImageView cartoon_top, cartoon_bottom, bg_top, bg_bottom;
    private AppCompatImageButton voteTop, voteBottom;
    private LinearLayout usersRoot_top, usersRoot_bottom;
    private Point middle;
    private OnActionListener listener;
    private ImageLoader loader;
    private ArrayList<Cartoon> cartoons;
    private boolean isFirstSetData = true;
    private boolean isLoadingData = false;
    private boolean isShowing = false;
    private DisplayImageOptions options;


    private int userCoverWidth = 0;
    private int coverDivier = 0;
    private boolean isVoted = false;
    private int drageState = 0;

    private Bitmap cacheTop, cacheBottom;//预先加载的图片

    private RelativeLayout cartoonRoot_top, cartoonRoot_bottom, voteRoot_top, voteRoot_bottom;
    private AppCompatTextView title;
    private View slipReadStone, slipReadStone_bg;

    private int cartoonRoot_Width;//漫画根布局的宽度
    private int margin_v;//垂直边距
    private int sidebarRoot_Width, sidebarRoot_offset = 0;//左右两侧条的宽度及由于两个方案间变化导致的侧边宽度调整
    private int slipRedstoneWidth_half;
    private int voteTop_top, voteBottom_top;
    private boolean isMeasureNeed = false;
    private int voteIndex = 0;


    public interface OnActionListener {
        void onShowDetile(Cartoon cartoon);

        void onVote(Cartoon win, Cartoon fail);

        void onShowUser(User user);

        void EOF();
    }

    public void setData(ArrayList<Cartoon> cartoons) {
        isLoadingData = false;

        this.cartoons = cartoons;
        if (isFirstSetData) {
            showData();
            isFirstSetData = false;
        } else {
            preLoadBitmap();
        }
    }

    public void showData() {
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
                isShowing = true;
                if (null == loader) {
                    loader = ImageLoader.getInstance();
                }
                if (isFirstSetData) {
                    loader.displayImage(cartoons.get(0).getImage(), cartoon_top, getDisplayImageOptions());
                    loader.displayImage(cartoons.get(1).getImage(), cartoon_bottom, getDisplayImageOptions());
                    cartoon_top.setTag(R.id.tag_flag,false);
                    cartoon_bottom.setTag(R.id.tag_flag,false);
                } else {
                    showBigBitmap(true);
                    showBigBitmap(false);
                }
                title.setText(getThemeName(cartoons.get(0).getKindsEx()));
                cartoon_top.setTag(R.id.tag_cartoon,cartoons.get(0));
                cartoon_bottom.setTag(R.id.tag_cartoon,cartoons.get(1));
                voteTop.setTag(R.id.tag_cartoon,cartoons.get(0));
                voteBottom.setTag(R.id.tag_cartoon,cartoons.get(1));

                showVoteUser(usersRoot_top, cartoons.get(0).getRewardList());
                showVoteUser(usersRoot_bottom, cartoons.get(1).getRewardList());
                slipReadStone.bringToFront();
                if (2 >= cartoons.size()) {
                    cartoons.clear();
                    if (null != listener) {
                        listener.EOF();
                    }
                } else {
                    cartoons.remove(0);
                    cartoons.remove(0);
                    preLoadBitmap();
                }
            }
        }
        if (isVoted) {
            isVoted = false;
            postInvalidate();
        }
    }

    private void preLoadBitmap() {
        if (null == loader) {
            loader = ImageLoader.getInstance();
        }

        for (int i = 0; i < 2; i++) {
            loader.loadImage(cartoons.get(i).getImage(), new ImageLoadingListener() {
                @Override
                public void onLoadingStarted(String imageUri, View view) {
                    if (imageUri.equals(cartoons.get(0).getImage())) {
                        cacheTop = null;
                    } else {
                        cacheBottom = null;
                    }
                }

                @Override
                public void onLoadingFailed(String imageUri, View view, FailReason failReason) {

                }

                @Override
                public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                    boolean isTopNeedDraw = false,isBottomNeedDraw = false;
                    if (null != cartoon_top) {
                        isTopNeedDraw = (boolean) cartoon_top.getTag(R.id.tag_flag);
                    }
                    if (null !=cartoon_bottom) {
                        isBottomNeedDraw = (boolean) cartoon_bottom.getTag(R.id.tag_flag);
                    }

                    if (isTopNeedDraw){
                        Cartoon cartoon = (Cartoon) cartoon_top.getTag(R.id.tag_cartoon);
                        if (cartoon.getImage().equals(imageUri)){
                            cartoon_top.setImageBitmap(loadedImage);
                            cartoon_top.setTag(R.id.tag_flag,false);
                            return;
                        }
                    }

                    if (isBottomNeedDraw){
                        Cartoon cartoon = (Cartoon) cartoon_bottom.getTag(R.id.tag_cartoon);
                        if (cartoon.getImage().equals(imageUri)){
                            cartoon_bottom.setImageBitmap(loadedImage);
                            cartoon_bottom.setTag(R.id.tag_flag,false);
                            return;
                        }
                    }

                    if (imageUri.equals(cartoons.get(0).getImage())){
                        cacheTop = loadedImage;
                    } else {
                        cacheBottom = loadedImage;
                    }
                }

                @Override
                public void onLoadingCancelled(String imageUri, View view) {
                }
            });
        }
    }

    private void showBigBitmap(boolean isTop) {
        Bitmap bitmap = isTop ? cacheTop : cacheBottom;
        ImageView imageView = isTop ? cartoon_top : cartoon_bottom;

        if (null != bitmap) {
            imageView.setImageBitmap(bitmap);
            imageView.setTag(R.id.tag_flag,false);
        } else {
            imageView.setImageResource(R.mipmap.image_default);
            imageView.setTag(R.id.tag_flag,true);
        }
    }

    public void setActionListener(OnActionListener listener) {
        this.listener = listener;
    }

    public CompetitionLayout(Context context) {
        super(context);
        initDragger();
        init();
    }

    public CompetitionLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        initDragger();
        init();
    }

    public CompetitionLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initDragger();
        init();
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
                drageState = state;
                super.onViewDragStateChanged(state);
            }

            @Override
            public void onViewPositionChanged(View changedView, int left, int top, int dx, int dy) {
                if (top - voteTop_top < voteBottom_top - top) {
                    //向上
                    bg_top.setAlpha(getViewAlpha(top, true));
                    if (drageState == ViewDragHelper.STATE_DRAGGING && !isVoted && null != listener && top == voteTop_top) {
                        isVoted = true;
                        voteIndex = 0;
                    }
                } else {
                    //向下
                    bg_bottom.setAlpha(getViewAlpha(top, false));
                    if (drageState == 1 && !isVoted && null != listener && top == voteBottom_top) {
                        voteIndex = 1;
                        isVoted = true;
                    }
                }
                super.onViewPositionChanged(changedView, left, top, dx, dy);
            }


            private int getViewAlpha(int top, boolean isTop) {
                int distance = isTop ? middle.y - slipRedstoneWidth_half - top : top - middle.y - slipRedstoneWidth_half;
                distance = (int)(distance * 255f / (middle.y - slipRedstoneWidth_half - voteTop_top));
                return distance > 255 ? 255:distance;
            }

            @Override
            public int clampViewPositionHorizontal(View child, int left, int dx) {
                return left - dx;
            }


            @Override
            public int getViewVerticalDragRange(View child) {
                return child.getId() == R.id.diamond_middle ? child.getHeight() : 0;//防止滑动时点击其下的view
            }

            @Override
            public int clampViewPositionVertical(View child, int top, int dy) {
                if (child.getId() == R.id.diamond_middle) {
                    if (top < voteTop_top) {
                        return voteTop_top;
                    } else if (top > voteBottom_top) {
                        return voteBottom_top;
                    } else {
                        return top;
                    }
                } else {
                    return super.clampViewPositionVertical(child, top, dy);
                }
            }

            @Override
            public void onViewReleased(View releasedChild, float xvel, float yvel) {
                switch (releasedChild.getId()) {
                    case R.id.diamond_middle:
                        if (!isVoted) {
                            mDragger.settleCapturedViewAt(middle.x - (slipReadStone.getWidth() / 2), middle.y - (slipReadStone.getHeight() / 2));
                            invalidate();
                        } else {
                            switch (voteIndex){
                                case 0:
                                    listener.onVote((Cartoon) cartoon_top.getTag(R.id.tag_cartoon), (Cartoon) cartoon_bottom.getTag(R.id.tag_cartoon));
                                    break;
                                case 1:
                                    listener.onVote((Cartoon) cartoon_bottom.getTag(R.id.tag_cartoon), (Cartoon) cartoon_top.getTag(R.id.tag_cartoon));
                                    break;
                            }
                            invalidate();
                        }
                        break;
                }
            }
        });
    }

    private void init() {
        coverDivier = getResources().getDimensionPixelSize(R.dimen.competition_usercover_divier);
        slipRedstoneWidth_half = getResources().getDimensionPixelSize(R.dimen.competition_votebutton_width) / 2;
        sidebarRoot_Width = getResources().getDimensionPixelOffset(R.dimen.competition_voteuser_coverwidth);
        margin_v = getResources().getDimensionPixelSize(R.dimen.competition_centerdivier_width);
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
        cartoonRoot_top = (RelativeLayout) getChildAt(0);
        cartoonRoot_bottom = (RelativeLayout) getChildAt(1);
        slipReadStone_bg = getChildAt(2);
        slipReadStone = getChildAt(3);
        voteRoot_top = (RelativeLayout) getChildAt(4);
        voteRoot_bottom = (RelativeLayout) getChildAt(5);
        title = (AppCompatTextView) getChildAt(6);

        cartoon_top = (ImageView) cartoonRoot_top.getChildAt(0);
        bg_top = (ImageView) cartoonRoot_top.getChildAt(1);
        cartoon_bottom = (ImageView) cartoonRoot_bottom.getChildAt(0);
        bg_bottom = (ImageView) cartoonRoot_bottom.getChildAt(1);

        voteTop = (AppCompatImageButton) voteRoot_top.getChildAt(0);
        usersRoot_top = (LinearLayout) voteRoot_top.getChildAt(1);
        voteBottom = (AppCompatImageButton) voteRoot_bottom.getChildAt(0);
        usersRoot_bottom = (LinearLayout) voteRoot_bottom.getChildAt(1);

        bg_top.setAlpha(0);
        bg_bottom.setAlpha(0);

        voteTop.setOnClickListener(this);
        voteBottom.setOnClickListener(this);
        cartoon_top.setOnClickListener(this);
        cartoon_bottom.setOnClickListener(this);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        int width = r - l;
        int height = b - t;
        int halfofMargin = margin_v / 2;

        //计算各种宽度高度
        if (changed) {

            middle = new Point((r - l) / 2, (b - t) / 2);

            //计算漫画的宽度
            if ((height - (2 * margin_v)) > ((2 * width) - (4 * sidebarRoot_Width))) {
                //高度是宽度的2倍以上，以宽度为基准
                cartoonRoot_Width = width - (2 * sidebarRoot_Width);
                sidebarRoot_offset = 0;
//                Log.e("onLayout", "方案一，cartoonWidth =" + cartoonRoot_Width);
            } else {
                //高度不足宽度的两部，以高度为基准
                cartoonRoot_Width = height / 2 - margin_v;
                //此时会影响到侧边，需调整侧边宽度
                sidebarRoot_offset = sidebarRoot_Width - ((width - cartoonRoot_Width) / 2);
//                Log.e("onLayout", "方案二，cartoonWidth =" + cartoonRoot_Width);
            }
            voteTop_top = middle.y - (cartoonRoot_Width / 2) - halfofMargin - slipRedstoneWidth_half;
            voteBottom_top = middle.y + halfofMargin + (cartoonRoot_Width / 2) - slipRedstoneWidth_half;
            userCoverWidth = sidebarRoot_Width - Math.abs(sidebarRoot_offset) - getResources().getDimensionPixelSize(R.dimen.competition_voteuser_padding);
            isMeasureNeed = true;
        }
        //对各组件进行布局
        if (null != cartoonRoot_top) {
            int crr = width - sidebarRoot_Width + sidebarRoot_offset;
            cartoonRoot_top.layout(sidebarRoot_Width - sidebarRoot_offset, middle.y - cartoonRoot_Width - halfofMargin, crr, middle.y - halfofMargin);
            cartoonRoot_bottom.layout(sidebarRoot_Width - sidebarRoot_offset, middle.y + halfofMargin, crr, middle.y + halfofMargin + cartoonRoot_Width);

            slipReadStone_bg.layout(middle.x - slipRedstoneWidth_half, middle.y - slipRedstoneWidth_half, middle.x + slipRedstoneWidth_half, middle.y + slipRedstoneWidth_half);
            slipReadStone.layout(middle.x - slipRedstoneWidth_half, middle.y - slipRedstoneWidth_half, middle.x + slipRedstoneWidth_half, middle.y + slipRedstoneWidth_half);

            voteRoot_top.layout(crr, middle.y - cartoonRoot_Width - halfofMargin, width, middle.y - halfofMargin);
            voteRoot_bottom.layout(crr, middle.y + halfofMargin, width, middle.y + halfofMargin + cartoonRoot_Width);

            title.layout(0, halfofMargin, sidebarRoot_Width - sidebarRoot_offset, height - halfofMargin);
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        if (isMeasureNeed && null != cartoonRoot_top) {
            int cartoonRootWidthSpec = MeasureSpec.makeMeasureSpec(cartoonRoot_Width, MeasureSpec.EXACTLY);
            cartoonRoot_top.measure(cartoonRootWidthSpec, cartoonRootWidthSpec);
            cartoonRoot_bottom.measure(cartoonRootWidthSpec, cartoonRootWidthSpec);

            int sidebarWidthSpec = MeasureSpec.makeMeasureSpec(sidebarRoot_Width - sidebarRoot_offset, MeasureSpec.EXACTLY);
            voteRoot_top.measure(sidebarWidthSpec, cartoonRootWidthSpec);
            voteRoot_bottom.measure(sidebarWidthSpec, cartoonRootWidthSpec);

            int titleHeightSpec = MeasureSpec.makeMeasureSpec(2 * cartoonRoot_Width + margin_v, MeasureSpec.EXACTLY);
            title.measure(sidebarWidthSpec, titleHeightSpec);

            int slipReadStoneWidthSpec = MeasureSpec.makeMeasureSpec(slipRedstoneWidth_half * 2, MeasureSpec.EXACTLY);
            slipReadStone.measure(slipReadStoneWidthSpec, slipReadStoneWidthSpec);
            slipReadStone_bg.measure(slipReadStoneWidthSpec, slipReadStoneWidthSpec);
            isMeasureNeed = false;
        }
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
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
        Cartoon cartoon = (Cartoon) v.getTag(R.id.tag_cartoon);
        switch (v.getId()) {
            case R.id.cartoon_top:
                if (null != listener && null != cartoon) {
                    listener.onShowDetile(cartoon);
                }
                break;
            case R.id.cartoon_bottom:
                if (null != listener && null != cartoon) {
                    listener.onShowDetile(cartoon);
                }
                break;
            case R.id.vote_top:
                if (null != listener) {
                    listener.onVote((Cartoon) cartoon_top.getTag(R.id.tag_cartoon), (Cartoon) cartoon_bottom.getTag(R.id.tag_cartoon));
                }
                break;
            case R.id.vote_bottom:
                if (null != listener) {
                    listener.onVote((Cartoon) cartoon_bottom.getTag(R.id.tag_cartoon), (Cartoon) cartoon_top.getTag(R.id.tag_cartoon));
                }
                break;
        }
    }


    private void showVoteUser(LinearLayout root, ArrayList<User> users) {
        if (null != root && 0 < root.getChildCount()) {
            root.removeAllViews();
        }
        if (null != root && null != users && !users.isEmpty()) {
            if (null == loader) {
                loader = ImageLoader.getInstance();
            }
            int count = (3 > users.size() ? users.size() : 3);
            for (int i = 0; i < count; i++) {
                ImageView imageView = new ImageView(getContext());
                imageView.setLayoutParams(getImageLayoutParsms());
                imageView.setTag(users.get(i));
                imageView.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (null != listener) {
                            listener.onShowUser((User) v.getTag());
                        }
                    }
                });
                loader.displayImage(users.get(i).getHeadImage(), imageView, MCKuai.instence.getCircleOptions());
                root.addView(imageView, 0);
            }
        }
    }

    private LinearLayout.LayoutParams getImageLayoutParsms() {
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(userCoverWidth, userCoverWidth);
        int margins = (sidebarRoot_Width - sidebarRoot_offset - userCoverWidth) / 2;
        params.setMargins(margins, 0, margins, coverDivier);
        return params;
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


    private DisplayImageOptions getDisplayImageOptions() {
        if (null == options) {
            options = new DisplayImageOptions
                    .Builder()
                    .cacheOnDisk(true)
                    .cacheInMemory(true)
                    .showImageOnLoading(R.mipmap.image_default)
                    .imageScaleType(ImageScaleType.EXACTLY)
                    .bitmapConfig(Bitmap.Config.RGB_565)
                    .delayBeforeLoading(150)
                    .build();
        }
        return options;
    }


}
