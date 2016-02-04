package com.mckuai.imc.Widget.TouchableLayout;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Point;
import android.graphics.RectF;
import android.net.Uri;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.mckuai.imc.Bean.Lable;
import com.mckuai.imc.R;
import com.mckuai.imc.Util.BitmapUtil;

import java.util.ArrayList;
import java.util.List;


public class TouchableLayout extends RelativeLayout {
    private Context context;

    /**
     * 最大放大倍数
     */
    public static final float MAX_SCALE_SIZE = 10.0f;
    public static final float MIN_SCALE_SIZE = 0.1f;

    private RectF mViewRect;//当前视图的范围

    private float mLastPointX, mLastPointY, deviation;

    private Bitmap mControllerBitmap;//控制旋转缩放的图标
    private Bitmap mDeleteBitmap;//删除图标
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

    }


    public void setBitmapBackground(Uri uri){
        bgBitmap = BitmapUtil.decodeFile(context, uri, getWidth(), getHeight());
        if (null != bgBitmap){
            postInvalidate();
        }
    }

    public void setBitmapBackground(int drawableResId) {
        bgBitmap = BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher);
        postInvalidate();
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
        TextView textView = new TextView(context);
        textView.setText(lable.getContent());
        addView(textView);
        postInvalidate();
    }


    /**
     * 设置水印
     *
     * @param bitmap
     * @param bgBitmap
     */
    public void setWaterMark(Bitmap bitmap, Bitmap bgBitmap) {
        this.bgBitmap = bgBitmap;
        addBitMap(bitmap);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
    }

    @Override
    protected void dispatchDraw(Canvas canvas) {
        super.dispatchDraw(canvas);
        if (null != bgBitmap) {
            Matrix matrix = new Matrix();
            matrix.setTranslate(55f, 100f);
            //matrix.setScale(1.5f, 1.5f);
            matrix.setScale(getWidth() / bgBitmap.getWidth(),getHeight() / bgBitmap.getHeight());
            //canvas.drawBitmap(bgBitmap, 0, 0, null);
            //canvas.drawBitmap(bgBitmap, matrix, null);
            canvas.drawBitmap(bgBitmap,0,0,null);
        }

        if (stickers.size() <= 0 && getChildCount() <= 0) {
            return;
        }

        for (int i = 0; i < stickers.size(); i++) {
            stickers.get(i).getmMatrix().mapPoints(stickers.get(i).getMapPointsDst(), stickers.get(i).getMapPointsSrc());
            canvas.drawBitmap(stickers.get(i).getBitmap(), stickers.get(i).getmMatrix(), null);
            if (stickers.get(i).isFocusable()) {
                canvas.drawLine(stickers.get(i).getMapPointsDst()[0], stickers.get(i).getMapPointsDst()[1], stickers.get(i).getMapPointsDst()[2], stickers.get(i).getMapPointsDst()[3], stickers.get(i).getmBorderPaint());
                canvas.drawLine(stickers.get(i).getMapPointsDst()[2], stickers.get(i).getMapPointsDst()[3], stickers.get(i).getMapPointsDst()[4], stickers.get(i).getMapPointsDst()[5], stickers.get(i).getmBorderPaint());
                canvas.drawLine(stickers.get(i).getMapPointsDst()[4], stickers.get(i).getMapPointsDst()[5], stickers.get(i).getMapPointsDst()[6], stickers.get(i).getMapPointsDst()[7], stickers.get(i).getmBorderPaint());
                canvas.drawLine(stickers.get(i).getMapPointsDst()[6], stickers.get(i).getMapPointsDst()[7], stickers.get(i).getMapPointsDst()[0], stickers.get(i).getMapPointsDst()[1], stickers.get(i).getmBorderPaint());

                canvas.drawBitmap(mControllerBitmap, stickers.get(i).getMapPointsDst()[4] - mControllerWidth / 2, stickers.get(i).getMapPointsDst()[5] - mControllerHeight / 2, null);
                canvas.drawBitmap(mDeleteBitmap, stickers.get(i).getMapPointsDst()[0] - mDeleteWidth / 2, stickers.get(i).getMapPointsDst()[1] - mDeleteHeight / 2, null);
            }
        }

        if (null != labels && !labels.isEmpty()) {
            /*Paint p = new Paint();
            p.setColor(Color.YELLOW);
            p.setTextSize(50);
            p.setAntiAlias(true);
            Paint.FontMetrics fm = p.getFontMetrics();
            int textHeight = (int) (Math.ceil(fm.descent - fm.ascent) + 2);
            float width = 500;
            float baseline = 100f;
            p.setColor(Color.WHITE);
            float offsetAscent = baseline + fm.ascent;
            float offsetDescent = baseline + fm.descent;
            float offsetTop = baseline + fm.top;
            float offsetBottom = baseline + fm.bottom;


            RectF rectf = new RectF(100,200,500,400);
            canvas.drawRect(rectf,p);
            canvas.drawText(labels.get(0),100,baseline,p);*/
            int count = getChildCount();
            for (int i = 0; i < count; i++) {
                View view = getChildAt(i);
                if (null != view) {
                    drawChild(canvas, view, getDrawingTime());
                }
            }
        }
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
                }
            case MotionEvent.ACTION_CANCEL:
                mLastPointX = 0;
                mLastPointY = 0;
                mInController = false;
                mInMove = false;
                mInDelete = false;
                break;
            case MotionEvent.ACTION_MOVE:
                if (mInController) {
                    stickers.get(focusStickerPosition).getmMatrix().postRotate(rotation(event), stickers.get(focusStickerPosition).getMapPointsDst()[8], stickers.get(focusStickerPosition).getMapPointsDst()[9]);
                    float nowLenght = caculateLength(stickers.get(focusStickerPosition).getMapPointsDst()[0], stickers.get(focusStickerPosition).getMapPointsDst()[1]);
                    float touchLenght = caculateLength(x, y) - deviation;
                    if (Math.sqrt((nowLenght - touchLenght) * (nowLenght - touchLenght)) > 0.0f) {
                        float scale = touchLenght / nowLenght;
                        float nowsc = stickers.get(focusStickerPosition).getScaleSize() * scale;
                        if (nowsc >= MIN_SCALE_SIZE && nowsc <= MAX_SCALE_SIZE) {
                            stickers.get(focusStickerPosition).getmMatrix().postScale(scale, scale, stickers.get(focusStickerPosition).getMapPointsDst()[8], stickers.get(focusStickerPosition).getMapPointsDst()[9]);
                            stickers.get(focusStickerPosition).setScaleSize(nowsc);
                        }
                    }

                    invalidate();
                    mLastPointX = x;
                    mLastPointY = y;
                    break;

                }

                if (mInMove == true) {
                    float cX = x - mLastPointX;
                    float cY = y - mLastPointY;
                    mInController = false;

                    if (Math.sqrt(cX * cX + cY * cY) > 2.0f && canStickerMove(cX, cY)) {
                        stickers.get(focusStickerPosition).getmMatrix().postTranslate(cX, cY);
                        postInvalidate();
                        mLastPointX = x;
                        mLastPointY = y;
                    }
                    break;
                }


                return true;

        }
        return true;
    }

    /**
     * 删除贴纸
     */
    private void doDeleteSticker() {
        stickers.remove(focusStickerPosition);
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
     * 是否点击在贴纸区域
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
        long startTime = System.currentTimeMillis();
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
        long endTime = System.currentTimeMillis();
        long time = endTime - startTime;

        return d1 <= a1 && d2 <= a1;

    }

    public void saveBitmapToFile() {
//        int bgWidth = bgBitmap.getWidth();
//        int bgHeight = bgBitmap.getHeight();
//        Bitmap newbmp = Bitmap.createBitmap(bgWidth, bgHeight, Bitmap.Config.ARGB_8888);
//        Canvas cv = new Canvas(newbmp);
//        cv.drawBitmap(bgBitmap, 0, 0, null);
//        cv.drawBitmap(stickers.get(focusStickerPosition).getBitmap(), stickers.get(focusStickerPosition).getmMatrix(), null);
//        cv.save(Canvas.ALL_SAVE_FLAG);
//        cv.restore();
//        bgBitmap = newbmp;

        stickers.clear();
        focusStickerPosition = -1;
        invalidate();
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
        if (null != listener) {
            Point point = new Point();
            point.x = (int) sticker.getMapPointsSrc()[2];
            point.y = (int) sticker.getMapPointsSrc()[4];
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
        final int childCount = labels.size();

        /**
         * 只将子控件放在其所设置的位置上，不用管其位置关系
         */
        for (int i = 0; i < childCount; i++) {
            View childview = getChildAt(i);
            Lable lable = labels.get(i);
            int childWidth = childview.getMeasuredWidth();
            int childHeight = childview.getMeasuredHeight();
            childview.layout(lable.getCoordinate().x, top, lable.getCoordinate().x + childWidth, lable.getCoordinate().y + childHeight);
        }
    }

    public static class LayoutParams extends ViewGroup.LayoutParams {


        public LayoutParams(Context c, AttributeSet attrs) {
            super(c, attrs);
        }

        public LayoutParams(ViewGroup.LayoutParams source) {
            super(source);
        }


        public LayoutParams(int width, int height) {
            super(width, height);
        }

    }
}