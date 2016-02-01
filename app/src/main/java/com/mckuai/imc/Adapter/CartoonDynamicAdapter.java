package com.mckuai.imc.Adapter;

import android.content.Context;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.mckuai.imc.Bean.CartoonMessage;
import com.mckuai.imc.R;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.ArrayList;

/**
 * Created by kyly on 2016/2/1.
 */
public class CartoonDynamicAdapter extends RecyclerView.Adapter<CartoonDynamicAdapter.ViewHolder> {
    private ArrayList<CartoonMessage> dynamics;
    private Context context;
    private ImageLoader loader;
    private OnItemClickListener listener;

    public interface OnItemClickListener{
        void onItemClicked(CartoonMessage dynamic);
    }

    public CartoonDynamicAdapter(Context context,OnItemClickListener listener) {
        this.context = context;
        this.listener = listener;
        this.loader = ImageLoader.getInstance();
    }

    public void setData(ArrayList<CartoonMessage> dynamics){
        this.dynamics = dynamics;
        notifyDataSetChanged();
    }

    public void addData(ArrayList<CartoonMessage> dynamics){
        if (null == dynamics) {
            this.dynamics = dynamics;
            notifyDataSetChanged();
        } else if (null != dynamics && !dynamics.isEmpty()){
            int position = dynamics.size();
            this.dynamics.addAll(dynamics);
            notifyItemRangeInserted(position,dynamics.size());
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_cartoon_dynamic,parent);
        ViewHolder holder = new ViewHolder(view);
        if (null != listener){
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    CartoonMessage dynamic = (CartoonMessage) v.getTag();
                    if (null != dynamic){
                        listener.onItemClicked(dynamic);
                    }
                }
            });
        }
        return holder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        if (null != dynamics && -1 < position && position < dynamics.size()){
            CartoonMessage dynamic = dynamics.get(position);
            if (null != dynamic){
                loader.displayImage(dynamic.getCartoon().getImage(),holder.cartoonCover);
                holder.ownerName.setText(dynamic.getOwner().getNickEx());
                holder.content.setText(dynamic.getContent()+"");
                holder.time.setText(dynamic.getTimeEx());
                holder.itemView.setTag(dynamic);
            }
        }
    }

    @Override
    public int getItemCount() {
        return null == dynamics ? 0:dynamics.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{
        public AppCompatTextView ownerName;
        public AppCompatTextView content;
        public AppCompatTextView time;
        public AppCompatImageView cartoonCover;

        public ViewHolder(View itemView) {
            super(itemView);
            ownerName = (AppCompatTextView) itemView.findViewById(R.id.cartoon_ownername);
            content = (AppCompatTextView) itemView.findViewById(R.id.dynamiccontent);
            time = (AppCompatTextView) itemView.findViewById(R.id.time);
            cartoonCover = (AppCompatImageView) itemView.findViewById(R.id.cartooncover);
        }
    }
}
