package com.mckuai.imc.Widget.TouchableLayout;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.support.v7.widget.AppCompatTextView;
import android.view.View;

/**
 * Created by Yangchen on 2015/9/7.
 */
public class Sticker {

    private Bitmap bitmap;
    private View lable;
    private AppCompatTextView lableContentView;

    /**
     * 是否获取焦点
     */
    private boolean focusable;

    private Matrix mMatrix;

    /**
     * 边框画笔
     */
    private Paint mBorderPaint;

    private float[] mapPointsSrc;

    private float[] mapPointsDst = new float[10];

    private float scaleSize = 1.0f;

    public void setScaleSize(float scaleSize) {
        this.scaleSize = scaleSize;
    }

    public Sticker(Bitmap bitmap, int bgWidth, int bgHeight) {
        this.bitmap = bitmap;

        mBorderPaint = new Paint();
        mBorderPaint.setAntiAlias(true);
        mBorderPaint.setFilterBitmap(true);
        mBorderPaint.setStyle(Paint.Style.STROKE);
        mBorderPaint.setStrokeWidth(2.0f);
        mBorderPaint.setColor(Color.WHITE);

        mMatrix = new Matrix();
        float initLeft = (bgWidth - bitmap.getWidth()) / 2;
        float initTop = (bgHeight - bitmap.getHeight()) / 2;
        mMatrix.postTranslate(initLeft, initTop);

        float px = bitmap.getWidth();
        float py = bitmap.getHeight();
        mapPointsSrc = new float[]{0, 0, px, 0, px, py, 0, py, px / 2, py / 2};
    }

    public float[] getMapPointsDst() {
        return mapPointsDst;
    }

    public float getScaleSize() {
        return scaleSize;
    }

    public Bitmap getBitmap() {
        return bitmap;
    }

    public boolean isFocusable() {
        return focusable;
    }

    public Matrix getmMatrix() {
        return mMatrix;
    }

    public Paint getmBorderPaint() {
        return mBorderPaint;
    }

    public float[] getMapPointsSrc() {
        return mapPointsSrc;
    }

    public void setBitmap(Bitmap bitmap) {
        this.bitmap = bitmap;
    }

    public void setMapPointsSrc(float[] mapPointsSrc) {
        this.mapPointsSrc = mapPointsSrc;

    }

    public void setFocusable(boolean focusable) {
        this.focusable = focusable;
    }

    public void setmMatrix(Matrix mMatrix) {
        this.mMatrix = mMatrix;
    }

    public void setmBorderPaint(Paint mBorderPaint) {
        this.mBorderPaint = mBorderPaint;
    }

    public View getLable() {
        return lable;
    }

    public void setLable(View lable) {
        this.lable = lable;
    }

    public AppCompatTextView getLableContentView() {
        return lableContentView;
    }

    public void setLableContentView(AppCompatTextView lableContentView) {
        this.lableContentView = lableContentView;
    }

    public void mirror(boolean isVertical) {
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        Matrix matrix = new Matrix();
        if (isVertical) {
            matrix.postScale(1, -1);
        } else {
            matrix.postScale(-1, 1);
        }
        bitmap = Bitmap.createBitmap(bitmap, 0, 0, width, height, matrix, true);
    }
}
