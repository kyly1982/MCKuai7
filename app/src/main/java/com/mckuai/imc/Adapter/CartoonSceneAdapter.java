package com.mckuai.imc.Adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.mckuai.imc.R;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.io.InputStream;
import java.util.ArrayList;

/**
 * Created by kyly on 2016/1/26.
 */
public class CartoonSceneAdapter extends RecyclerView.Adapter<CartoonSceneAdapter.ViewHolder> implements View.OnClickListener {
    private ArrayList<Object> scenes;
    private OnSceneSelectedListener listener;
    private Context context;
    private ImageLoader imageLoader;

    public interface OnSceneSelectedListener {
        void onSceneSelected(Object scene);
    }

    public CartoonSceneAdapter(Context context, OnSceneSelectedListener listener) {
        this.context = context;
        this.listener = listener;
    }

    public void setData(ArrayList<Object> scenes) {
        this.scenes = scenes;
        notifyDataSetChanged();
    }


    @Override
    public int getItemCount() {
        return null == scenes ? 0 : scenes.size();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_createcartoon_scene, parent,false);
        int width = parent.getWidth();
        width = width / 3 - 10;
        ViewGroup.LayoutParams params =  view.getLayoutParams();
        params.width = width;
        params.height = width;
        view.setLayoutParams(params);

        ViewHolder holder = new ViewHolder(view);


        return holder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        if (null != scenes && -1 < position && position < scenes.size() && null != scenes.get(position)) {
            if (scenes.get(position) instanceof Integer) {
                //holder.scene.setImageResource((Integer) scenes.get(position));
                int resid = (int) scenes.get(position);
                holder.scene.setImageBitmap(readBitMap(resid));
            } else {
                holder.scene.setImageURI((Uri) scenes.get(position));
            }
            holder.scene.setTag(scenes.get(position));
            holder.scene.setOnClickListener(this);
        }
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public AppCompatImageView scene;

        public ViewHolder(View itemView) {
            super(itemView);
            scene = (AppCompatImageView) itemView.findViewById(R.id.sceneimage);
        }
    }

    @Override
    public void onClick(View v) {
        Object scene =  v.getTag();
        if (null != scene && null != listener) {
            listener.onSceneSelected(scene);
        }
    }

    /**
     * 以最省内存的方式读取本地资源的图片
     *
     * @param resId
     * @return
     */
    public Bitmap readBitMap(int resId) {
        BitmapFactory.Options opt = new BitmapFactory.Options();
        opt.inPreferredConfig = Bitmap.Config.RGB_565;
        opt.inPurgeable = true;
        opt.inInputShareable = true;
        //获取资源图片
        InputStream is = context.getResources().openRawResource(resId);
        return BitmapFactory.decodeStream(is, null, opt);
    }
}
