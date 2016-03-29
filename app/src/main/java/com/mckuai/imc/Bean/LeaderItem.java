package com.mckuai.imc.Bean;

import android.graphics.Bitmap;
import android.graphics.Point;
import android.view.View;

/**
 * Created by kyly on 2016/3/29.
 */
public class LeaderItem {
    private View baseView;
    private Bitmap image;
    private Point point;

    public View getBaseView() {
        return baseView;
    }

    public void setBaseView(View baseView) {
        this.baseView = baseView;
    }

    public Bitmap getImage() {
        return image;
    }

    public void setImage(Bitmap image) {
        this.image = image;
    }

    public Point getPoint() {
        return point;
    }

    public void setPoint(Point point) {
        this.point = point;
    }
}
