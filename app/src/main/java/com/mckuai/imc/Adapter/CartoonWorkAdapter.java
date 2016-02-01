package com.mckuai.imc.Adapter;

import android.content.Context;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.mckuai.imc.Bean.Cartoon;
import com.mckuai.imc.R;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.ArrayList;

/**
 * Created by kyly on 2016/2/1.
 */
public class CartoonWorkAdapter extends RecyclerView.Adapter<CartoonWorkAdapter.ViewHolder> {
    private ArrayList<Cartoon> cartoons;
    private Context context;
    private OnItemClickListener listener;
    private ImageLoader loader;

    public CartoonWorkAdapter(Context context, OnItemClickListener listener) {
        this.context = context;
        this.listener = listener;
        loader = ImageLoader.getInstance();
    }

    public interface OnItemClickListener{
        void onItemClicked(Cartoon cartoon);
    }

    public void setData(ArrayList<Cartoon> cartoons){
        this.cartoons = cartoons;
        notifyDataSetChanged();
    }

    public void addData(ArrayList<Cartoon> cartoons){
        if (null == this.cartoons){
            this.cartoons = cartoons;
            notifyDataSetChanged();
        } else {
            int position = this.cartoons.size();
            this.cartoons.addAll(cartoons);
            notifyItemRangeInserted(position,cartoons.size());
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_cartoon_work,parent,false);
        ViewHolder holder = new ViewHolder(view);
        if (null != listener){
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Cartoon cartoon = (Cartoon) v.getTag();
                    if (null != cartoon){
                        listener.onItemClicked(cartoon);
                    }
                }
            });
        }
        int width = parent.getWidth();
        width =width / 2;
        ViewGroup.LayoutParams params = view.getLayoutParams();
        params.width = width;
        params.height = width;
        view.setLayoutParams(params);
        return holder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        if (null != cartoons && -1 < position && position < cartoons.size()){
            String url = cartoons.get(position).getImage();
            if (null != url){
                loader.displayImage(url,holder.cover);
            }

        }
    }

    @Override
    public int getItemCount() {
        return null == cartoons ? 0:cartoons.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{
        public AppCompatImageView cover;

        public ViewHolder(View itemView) {
            super(itemView);
            cover = (AppCompatImageView) itemView.findViewById(R.id.cartooncover);
        }
    }
}
