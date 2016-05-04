package com.mckuai.imc.Widget.TouchableLayout;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.RectF;
import android.net.Uri;
import android.support.v7.widget.AppCompatTextView;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.FrameLayout;

import com.mckuai.imc.Bean.Lable;
import com.mckuai.imc.R;
import com.mckuai.imc.Util.BitmapUtil;

import java.util.ArrayList;
import java.util.List;


public class TouchableLayout extends FrameLayout {
    private Context context;

    private boolean isImageFrozen = false;
    private boolean isBuilderFrozen = false;

    /**
     * 最大放大倍数
     */
    public static final float MAX_SCALE_SIZE = 10.0f;
    public static final float MIN_SCALE_SIZE = 0.1f;

    private RectF mViewRect;//当前视图的范围

    private float mLastPointX, mLastPointY, deviation;

    private Bitmap mControllerBitmap;//控制旋转缩放的图标
    private Bitmap mDeleteBitmap;//删除图标
    private Bitmap mMirrorController;//镜像翻转控制图标

    private float textSize = 0;
    private int indicatorHeight = 0;

    /**
     * 背景图
     */
    private Bitmap bgBitmap;
    /**
     * 控制图标长和宽
     */
    private float mControllerWidth, mControllerHeight;
    /**
     * 删除图标长和宽
     */
    private float mDeleteWidth, mDeleteHeight;//操作图标长和宽
    /**
     * 镜像翻转控制图标长宽
     */
    private float mMirrorControllerWidth, mMirrorControllerHeight;
    /**
     * 控制模式
     */
    private boolean mInController;
    /**
     * 移动模式
     */
    private boolean mInMove;

    /**
     * 删除模式
     */
    private boolean mInDelete = false;

    //    private Sticker currentSticker;
    /**
     * 帖纸
     */
    private List<Sticker> stickers = new ArrayList<Sticker>();
    /**
     * 标签
     */
    private List<Lable> labels = new ArrayList<>();

    /**
     * 焦点贴纸索引
     */
    private int focusStickerPosition = -1;
    private int focusLablePosition = 0;

    public TouchableLayout(Context context) {
        this(context, null);
        this.context = context;
    }

    public int getWidgetCount() {
        return null == stickers ? 0:stickers.size();
    }

    public TouchableLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
        this.context = context;
        init();
    }

    public TouchableLayout(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        this.context = context;
        init();
    }

    private OnFocusChangeListener listener;

    public void setOnFocusChangeListener(OnFocusChangeListener listener) {
        this.listener = listener;
    }

    public interface OnFocusChangeListener {
        void onFocusChange(Point point);
    }

    /**
     * 初始化控制图标和删除图标
     */
    private void init() {

        mControllerBitmap = BitmapFactory.decodeResource(getResources(), R.mipmap.ic_sticker_control);
        mControllerWidth = mControllerBitmap.getWidth();
        mControllerHeight = mControllerBitmap.getHeight();

        mDeleteBitmap = BitmapFactory.decodeResource(getResources(), R.mipmap.ic_sticker_delete);
        mDeleteWidth = mDeleteBitmap.getWidth();
        mDeleteHeight = mDeleteBitmap.getHeight();

        mMirrorController = BitmapFactory.decodeResource(getResources(), R.mipmap.ic_sticker_mirror);
        mMirrorControllerWidth = mMirrorController.getWidth();
        mMirrorControllerHeight = mMirrorController.getHeight();
    }

    public void frozenWidget(boolean frozen) {
        this.isImageFrozen = frozen;
        postInvalidate();
    }

    public void frozenBuilder(boolean frozen) {
        this.isBuilderFrozen = frozen;
        postInvalidate();
    }


    public void setBitmapBackground(Uri uri) {
        bgBitmap = BitmapUtil.decodeFile(context, uri, getWidth(), getHeight());
        invalidate();
    }

    public void setBitmapBackground(String filePath) {
        bgBitmap = BitmapUtil.decodeFile(filePath, getWidth(), getWidth());
    }

    public void setBitmapBackground(int drawableResId) {
        bgBitmap = BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher);
    }

    public void setBitmapBackground(Bitmap bitmap) {
        this.bgBitmap = bitmap;
    }

    public void addBitMap(Bitmap bitmap) {
        Point point = Utils.getDisplayWidthPixels(getContext());
        Sticker sticker = new Sticker(bitmap, point.x, point.x);
        stickers.add(sticker);
        focusStickerPosition = stickers.size() - 1;
        setFocusSticker(focusStickerPosition);
        postInvalidate();
    }


    public void addLable(Lable lable) {
        if (null == labels) {
            labels = new ArrayList<>(3);
        }
        labels.add(lable);
        Sticker sticker = stickers.get(focusStickerPosition);
        if (null == sticker.getLable()) {
            View view = inflate(context, R.layout.element_cartoonlable, null);
            AppCompatTextView content = (AppCompatTextView) view.findViewById(R.id.content);
            textSize = content.getTextSize();

            if (0 == indicatorHeight) {
                //indicatorHeight = view.findViewById(R.id.ic_indicator).getHeight();
                final View indicator = view.findViewById(R.id.ic_indicator);
                indicator.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                    @Override
                    public void onGlobalLayout() {
                        indicatorHeight = indicator.getHeight();
                        indicator.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                        postInvalidate();
                    }
                });
            }
            content.setText(lable.getContent());
            sticker.setLable(view);
            sticker.setLableContentView(content);
            view.setTag(lable);//将位置信息存入
            addView(view);
            view.invalidate();
            postInvalidate();
        } else {
            sticker.getLableContentView().setText(lable.getContent());
        }
    }


    @Override
    protected void dispatchDraw(Canvas canvas) {
        if (null != bgBitmap) {
            Matrix matrix = new Matrix();
            matrix.setTranslate(55f, 100f);
            matrix.setScale(getWidth() / bgBitmap.getWidth(), getHeight() / bgBitmap.getHeight());
            canvas.drawBitmap(bgBitmap, 0, 0, null);
        }

        if (stickers.size() <= 0 && getChildCount() <= 0) {
            return;
        }

        for (int i = 0; i < stickers.size(); i++) {
            stickers.get(i).getmMatrix().mapPoints(stickers.get(i).getMapPointsDst(), stickers.get(i).getMapPointsSrc());
            canvas.drawBitmap(stickers.get(i).getBitmap(), stickers.get(i).getmMatrix(), null);
            if (stickers.get(i).isFocusable() && !isBuilderFrozen) {
                canvas.drawLine(stickers.get(i).getMapPointsDst()[0], stickers.get(i).getMapPointsDst()[1], stickers.get(i).getMapPointsDst()[2], stickers.get(i).getMapPointsDst()[3], stickers.get(i).getmBorderPaint());
                canvas.drawLine(stickers.get(i).getMapPointsDst()[2], stickers.get(i).getMapPointsDst()[3], stickers.get(i).getMapPointsDst()[4], stickers.get(i).getMapPointsDst()[5], stickers.get(i).getmBorderPaint());
                canvas.drawLine(stickers.get(i).getMapPointsDst()[4], stickers.get(i).getMapPointsDst()[5], stickers.get(i).getMapPointsDst()[6], stickers.get(i).getMapPointsDst()[7], stickers.get(i).getmBorderPaint());
                canvas.drawLine(stickers.get(i).getMapPointsDst()[6], stickers.get(i).getMapPointsDst()[7], stickers.get(i).getMapPointsDst()[0], stickers.get(i).getMapPointsDst()[1], stickers.get(i).getmBorderPaint());
                if (!isImageFrozen) {
                    canvas.drawBitmap(mControllerBitmap, stickers.get(i).getMapPointsDst()[4] - mControllerWidth / 2, stickers.get(i).getMapPointsDst()[5] - mControllerHeight / 2, null);
                    canvas.drawBitmap(mDeleteBitmap, stickers.get(i).getMapPointsDst()[0] - mDeleteWidth / 2, stickers.get(i).getMapPointsDst()[1] - mDeleteHeight / 2, null);
                    canvas.drawBitmap(mMirrorController, stickers.get(i).getMapPointsDst()[2] - mMirrorControllerWidth / 2, stickers.get(i).getMapPointsDst()[3] - mMirrorControllerHeight / 2, null);
                }
            }
        }
        super.dispatchDraw(canvas);
    }

    /**
     * 是否在控制点区域
     *
     * @param x
     * @param y
     * @return
     */
    private boolean isInController(float x, float y) {
        int position = 4;
        float rx = stickers.get(focusStickerPosition).getMapPointsDst()[position];
        float ry = stickers.get(focusStickerPosition).getMapPointsDst()[position + 1];
        RectF rectF = new RectF(rx - mControllerWidth / 2,
                ry - mControllerHeight / 2,
                rx + mControllerWidth / 2,
                ry + mControllerHeight / 2);
        return rectF.contains(x, y);

    }

    private boolean isMirror(float x, float y) {
        int position = 2;
        float rx = stickers.get(focusStickerPosition).getMapPointsDst()[position];
        float ry = stickers.get(focusStickerPosition).getMapPointsDst()[position + 1];
        RectF rectF = new RectF(rx - mMirrorControllerWidth / 2,
                ry - mMirrorControllerHeight / 2,
                rx + mMirrorControllerWidth / 2,
                ry + mMirrorControllerHeight / 2);
        return rectF.contains(x, y);
    }

    /**
     * 是否在删除点区域
     *
     * @param x
     * @param y
     * @return
     */
    private boolean isInDelete(float x, float y) {
        int position = 0;
        float rx = stickers.get(focusStickerPosition).getMapPointsDst()[position];
        float ry = stickers.get(focusStickerPosition).getMapPointsDst()[position + 1];
        RectF rectF = new RectF(rx - mDeleteWidth / 2,
                ry - mDeleteHeight / 2,
                rx + mDeleteWidth / 2,
                ry + mDeleteHeight / 2);
        return rectF.contains(x, y);

    }

    private boolean isInLable(float x, float y) {
        if (getChildCount() <= 0) {
            return false;
        }
        for (int i = 0; i < getChildCount(); i++) {
            View view = getChildAt(i);
            if (view.getLeft() <= x && view.getRight() >= x && view.getTop() <= y && view.getBottom() >= y) {
                focusLablePosition = i;
                return true;
            }
        }

        return true;
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        if (mViewRect == null) {
            mViewRect = new RectF(0f, 0f, getMeasuredWidth(), getMeasuredHeight());
        }

        if (stickers.size() <= 0 || focusStickerPosition < 0) {
            return true;
        }
        float x = event.getX();
        float y = event.getY();
        if (!isBuilderFrozen) {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    //检查是否是在控制区域
                    if (isInController(x, y)) {
                        mInController = true;
                        mLastPointY = y;
                        mLastPointX = x;

                        float nowLenght = caculateLength(stickers.get(focusStickerPosition).getMapPointsDst()[0], stickers.get(focusStickerPosition).getMapPointsDst()[1]);
                        float touchLenght = caculateLength(x, y);
                        deviation = touchLenght - nowLenght;
                        break;
                    }
                    //检查是否在删除按钮上
                    if (isInDelete(x, y)) {
                        mInDelete = true;
                        break;
                    }

                    if (isMirror(x, y)) {
                        stickers.get(focusStickerPosition).mirror(false);
                        invalidate();
                        break;
                    }

                    //检查是否在焦点卡片上
                    if (isFocusSticker(x, y)) {
                        mLastPointY = y;
                        mLastPointX = x;
                        mInMove = true;
                        invalidate();
                    } else {
                        invalidate();
                    }
                    break;
                case MotionEvent.ACTION_UP:
                    if (isInDelete(x, y) && mInDelete) {
                        doDeleteSticker();
                    } else if (mInMove) {
                        callBackPoint(stickers.get(focusStickerPosition));
                    }
                case MotionEvent.ACTION_CANCEL:
                    mLastPointX = 0;
                    mLastPointY = 0;
                    mInController = false;
                    mInMove = false;
                    mInDelete = false;
                    break;
                case MotionEvent.ACTION_MOVE:
                    //缩放旋转控制
                    if (mInController) {
                        Sticker sticker = stickers.get(focusStickerPosition);
                        sticker.getmMatrix().postRotate(rotation(event), sticker.getMapPointsDst()[8], sticker.getMapPointsDst()[9]);
                        float nowLenght = caculateLength(sticker.getMapPointsDst()[0], sticker.getMapPointsDst()[1]);
                        float touchLenght = caculateLength(x, y) - deviation;
                        if (Math.sqrt((nowLenght - touchLenght) * (nowLenght - touchLenght)) > 0.0f) {
                            float scale = touchLenght / nowLenght;
                            float nowsc = sticker.getScaleSize() * scale;
                            if (nowsc >= MIN_SCALE_SIZE && nowsc <= MAX_SCALE_SIZE) {
                                sticker.getmMatrix().postScale(scale, scale, sticker.getMapPointsDst()[8], sticker.getMapPointsDst()[9]);
                                sticker.setScaleSize(nowsc);
                            }
                            resetLable(sticker);
                        }

                        invalidate();
                        mLastPointX = x;
                        mLastPointY = y;
                        break;

                    }
                    //移动
                    if (mInMove == true) {
                        float cX = x - mLastPointX;
                        float cY = y - mLastPointY;
                        mInController = false;

                        if (Math.sqrt(cX * cX + cY * cY) > 2.0f && canStickerMove(cX, cY)) {
                            Sticker sticker = stickers.get(focusStickerPosition);
                            sticker.getmMatrix().postTranslate(cX, cY);
                            resetLable(sticker);
                            postInvalidate();
                            mLastPointX = x;
                            mLastPointY = y;
                        }
                        break;
                    }
                    return true;
            }
        }
        return true;
    }

    private void resetLable(Sticker sticker){
        if (null != sticker.getLable()) {
            int childWidth = sticker.getLable().getWidth();
            int childHeight = sticker.getLable().getHeight();
            Point point = sticker.getLeftTopPoint();
            int left = point.x;
            int top = point.y - childHeight;
            int right = point.x + childWidth;
            int bottom = point.y;

            if (left < getLeft()){
                left = getLeft();
                right = left + childWidth;
            }

            if (right > getRight()){
                right = getRight();
                left =right -  childWidth;
            }

            sticker.getLable().layout(left, top, right, bottom);
            Lable lable = (Lable) sticker.getLable().getTag();
            if (null != lable){
                for (Lable temp:labels){
                    if (temp.getId() == lable.getId()){
                        temp.setCoordinate(point);
                        return;
                    }
                }
            }
        }
    }

    /**
     * 删除贴纸
     */
    private void doDeleteSticker() {
        Sticker sticker = stickers.remove(focusStickerPosition);
        if (null != sticker.getLable()) {
            Lable lable = (Lable) sticker.getLable().getTag();
            if (null != lable) {
                labels.remove(lable);
            }
            removeView(sticker.getLable());
        }
        focusStickerPosition = stickers.size() - 1;

        invalidate();
    }

    private boolean canStickerMove(float cx, float cy) {
        float px = cx + stickers.get(focusStickerPosition).getMapPointsDst()[8];
        float py = cy + stickers.get(focusStickerPosition).getMapPointsDst()[9];
        return mViewRect.contains(px, py);
    }


    private float caculateLength(float x, float y) {
        return (float) Utils.lineSpace(x, y, stickers.get(focusStickerPosition).getMapPointsDst()[8], stickers.get(focusStickerPosition).getMapPointsDst()[9]);
    }


    private float rotation(MotionEvent event) {
        float originDegree = calculateDegree(mLastPointX, mLastPointY);
        float nowDegree = calculateDegree(event.getX(), event.getY());
        return nowDegree - originDegree;
    }

    private float calculateDegree(float x, float y) {
        double delta_x = x - stickers.get(focusStickerPosition).getMapPointsDst()[8];
        double delta_y = y - stickers.get(focusStickerPosition).getMapPointsDst()[9];
        double radians = Math.atan2(delta_y, delta_x);
        return (float) Math.toDegrees(radians);
    }

    /**
     * 是否点击在贴纸区域,如果是点击在帖纸上，则设置其获得焦点，并返回真
     * 如果没在帖纸上，则将所有的焦点移除
     *
     * @param x
     * @param y
     * @return
     */
    private boolean isFocusSticker(double x, double y) {
        for (int i = stickers.size() - 1; i >= 0; i--) {
            Sticker sticker = stickers.get(i);
            if (isInContent(x, y, sticker)) {
                setFocusSticker(i);
                return true;
            }
        }
        setFocusSticker(-1);
        return false;
    }

    /**
     * 判断点是否在指定区域内
     *
     * @param x
     * @param y
     * @return
     */
    private boolean isInContent(double x, double y, Sticker currentSticker) {
        //long startTime = System.currentTimeMillis();
        float[] pointsDst = currentSticker.getMapPointsDst();
        PointD pointF_1 = Utils.getMidpointCoordinate(pointsDst[0], pointsDst[1], pointsDst[2], pointsDst[3]);
        double a1 = Utils.lineSpace(pointsDst[8], pointsDst[9], pointF_1.getX(), pointF_1.getY());
        double b1 = Utils.lineSpace(pointsDst[8], pointsDst[9], x, y);
        if (b1 <= a1) {
            return true;
        }
        double c1 = Utils.lineSpace(pointF_1.getX(), pointF_1.getY(), x, y);
        double p1 = (a1 + b1 + c1) / 2;
        double s1 = Math.sqrt(p1 * (p1 - a1) * (p1 - b1) * (p1 - c1));
        double d1 = 2 * s1 / a1;
        if (d1 > a1) {
            return false;
        }

        PointD pointF_2 = Utils.getMidpointCoordinate(pointsDst[2], pointsDst[3], pointsDst[4], pointsDst[5]);
        double a2 = a1;
        double b2 = b1;
        double c2 = Utils.lineSpace(pointF_2.getX(), pointF_2.getY(), x, y);
        double p2 = (a2 + b2 + c2) / 2;
        double temp = p2 * (p2 - a2) * (p2 - b2) * (p2 - c2);
        double s2 = Math.sqrt(temp);
        double d2 = 2 * s2 / a2;
        if (d2 > a1) {
            return false;
        }
   /*     long endTime = System.currentTimeMillis();
        long time = endTime - startTime;*/

        return d1 <= a1 && d2 <= a1;

    }

    /**
     * 设置焦点贴纸
     *
     * @param position
     */
    private void setFocusSticker(int position) {
        int focusPosition = stickers.size() - 1;
        for (int i = 0; i < stickers.size(); i++) {
            if (i == position) {
                focusPosition = i;
                stickers.get(i).setFocusable(true);

            } else {
                stickers.get(i).setFocusable(false);
            }
        }
        Sticker sticker = stickers.remove(focusPosition);
        stickers.add(sticker);
        focusStickerPosition = stickers.size() - 1;
        callBackPoint(sticker);
    }

    private void callBackPoint(Sticker sticker) {
        if (null != listener && null != sticker) {
            float value[] = {0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f};
            sticker.getmMatrix().getValues(value);
            Point point = new Point();
            point.x = (int) value[Matrix.MTRANS_X];
            point.y = (int) value[Matrix.MTRANS_Y];
            listener.onFocusChange(point);
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {

        //super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        /**
         * 获取此ViewGroup上级容器为其推荐的宽高和计算模式
         */
        int parentWidth = MeasureSpec.getSize(widthMeasureSpec);
        int parentHeight = MeasureSpec.getSize(heightMeasureSpec);
        int modWidth = MeasureSpec.getMode(widthMeasureSpec);
        int modHeight = MeasureSpec.getMode(heightMeasureSpec);


        /**
         * 设置为wrap_content时的宽高
         */
        int width = 0;
        int height = 0;

        /**
         * 子控件的长宽
         */
        int childWidth;
        int childHeight;

        View childView;

        /**
         * 计算出所有的childView的宽和高
         */
        for (int childIndex = 0; childIndex < getChildCount(); childIndex++) {
            childView = getChildAt(childIndex);
            measureChild(childView, widthMeasureSpec, heightMeasureSpec);
            childWidth = childView.getMeasuredWidth();
            childHeight = childView.getMeasuredHeight();
            width = width > childWidth ? width : childWidth;
            height = height > childHeight ? height : childHeight;
        }

        setMeasuredDimension((modWidth == MeasureSpec.EXACTLY) ? parentWidth : width, (modHeight == MeasureSpec.EXACTLY) ? parentHeight : height);

    }


    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        final int childCount = getChildCount();

        /**
         * 只将子控件放在其所设置的位置上，不用管其位置关系
         */
        for (int i = 0; i < childCount; i++) {
            View childview = getChildAt(i);
            Lable lable = labels.get(i);
            //int childWidth1 = childview.getMeasuredWidth();
            int childWidth = (int) textSize* lable.getContent().length() + 2*getResources().getDimensionPixelOffset(R.dimen.createcartoon_lable_padding);
            //int childHeight = childview.getMeasuredHeight();
            int childHeight = getTextViewHeight();
            childHeight = context.getResources().getDimensionPixelOffset(R.dimen.createcartoon_lable_height);
            int childLeft = lable.getCoordinate().x;
            int childTop = lable.getCoordinate().y - childHeight;
            int childRight = lable.getCoordinate().x + childWidth;
            int childBottom = lable.getCoordinate().y;

            if (childLeft < getLeft()){
                int d = getLeft() - childLeft;
                childLeft = getLeft();
                childRight += d;
            }

            if (childRight > getRight()){
                int d = childRight - getRight();
                childRight = getRight();
                childLeft -= d;
            }

            childview.layout(childLeft, childTop, childRight, childBottom);
            childview.postInvalidate();
        }
    }

    private int getTextViewHeight(){
        Paint paint = new Paint();
        paint.setTextSize(textSize);
        Paint.FontMetrics fontMetrics = paint.getFontMetrics();

        return (int)(Math.ceil(fontMetrics.bottom - fontMetrics.top)) + 2*getResources().getDimensionPixelOffset(R.dimen.createcartoon_lable_padding) + indicatorHeight;
    }

}