package com.mckuai.imc.Adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.mckuai.imc.R;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.ArrayList;

/**
 * Created by kyly on 2016/1/26.
 */
public class CartoonSceneAdapter extends RecyclerView.Adapter<CartoonSceneAdapter.ViewHolder> implements View.OnClickListener{
    private ArrayList<Bitmap> scenes;
    private OnItemSelectedListener listener;
    private Context context;
    private ImageLoader imageLoader;

    public interface OnItemSelectedListener{
        public void onItemSelected(Bitmap scene);
    }

    public CartoonSceneAdapter(Context context, OnItemSelectedListener listener) {
        this.context = context;
        this.listener = listener;
        //imageLoader = ImageLoader.getInstance();
    }

    public void setData(ArrayList<Bitmap> scenes){
        this.scenes = scenes;
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return null == scenes ? 0:scenes.size();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_createcartoon_imageview,parent);
        ViewHolder holder = new ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        if (null != scenes && -1 < position && position < scenes.size()){
            Bitmap scene = scenes.get(position);
            if (null != scene){
                holder.scene.setImageBitmap(null);
                holder.scene.setTag(scene);
                holder.scene.setOnClickListener(this);
            }
        }
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{
        public AppCompatImageView scene;

        public ViewHolder(View itemView) {
            super(itemView);
            scene = (AppCompatImageView) itemView.findViewById(R.id.widget);
        }
    }

    @Override
    public void onClick(View v) {
        Bitmap scene = (Bitmap) v.getTag();
        if (null != scene && null != listener){
            listener.onItemSelected(scene);
        }
    }
}
