package com.mckuai.imc.Bean;

import android.graphics.Bitmap;

/**
 * Created by kyly on 2016/4/21.
 */
public class CacheBitmap {
    private int id;
    private String url;
    private Bitmap bitmap;

    public CacheBitmap(int id, String url,Bitmap bitmap) {
        this.bitmap = bitmap;
        this.id = id;
        this.url = url;
    }

    public CacheBitmap(int id, String url) {
        this.id = id;
        this.url = url;
    }

    public void addData(Bitmap bitmap){
        this.bitmap = bitmap;
    }

    public Bitmap getBitmap() {
        return bitmap;
    }

    public void setBitmap(Bitmap bitmap) {
        this.bitmap = bitmap;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
