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
public class CartoonMessageAdapter extends RecyclerView.Adapter<CartoonMessageAdapter.ViewHolder> {
    private ArrayList<CartoonMessage> messages;
    private OnItemClickListener listener;
    private Context context;
    private ImageLoader loader;

    public CartoonMessageAdapter(Context context,OnItemClickListener listener) {
        this.context = context;
        this.listener = listener;
        loader = ImageLoader.getInstance();
    }

    public interface OnItemClickListener{
        void onItemClicked(CartoonMessage message);
    }

    public void setData(ArrayList<CartoonMessage> messages){
        this.messages = messages;
        notifyDataSetChanged();
    }

    public void addData(ArrayList<CartoonMessage> messages){
        if (this.messages != null && null != messages){
            int start = this.messages.size();
            this.messages.addAll(messages);
            notifyItemRangeInserted(start,messages.size());
        } else {
            this.messages = messages;
            notifyDataSetChanged();
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_cartoon_message,parent,false);
        ViewHolder holder = new ViewHolder(view);
        if (null != listener){
            view.setClickable(true);
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    CartoonMessage message = (CartoonMessage) v.getTag();
                    if (null != message) {
                        listener.onItemClicked(message);
                    }
                }
            });
        }
        return holder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        if (null != messages && -1 < position && position < messages.size()){
            CartoonMessage message = messages.get(position);
            if (null != message){
                if (null != message.getCartoon()){
                    loader.displayImage(message.getCartoon().getImage(),holder.cartoonCover);
                }
                if (null != message.getOwner()){
                    loader.displayImage(message.getOwner().getHeadImage(),holder.userCover);
                }
                holder.userName.setText(message.getOwner().getNickEx());
                holder.content.setText(message.getContent()+"");
                holder.time.setText(message.getTimeEx()+"");
                holder.itemView.setTag(message);
            }
        }
    }


    @Override
    public int getItemCount() {
        return null == messages ? 0:messages.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{
        public AppCompatImageView userCover;
        public AppCompatImageView cartoonCover;
        public AppCompatTextView userName;
        public AppCompatTextView content;
        public AppCompatTextView time;

        public ViewHolder(View itemView) {
            super(itemView);
            userCover = (AppCompatImageView) itemView.findViewById(R.id.usercover);
            cartoonCover = (AppCompatImageView) itemView.findViewById(R.id.cartooncover);
            userName = (AppCompatTextView) itemView.findViewById(R.id.username);
            content = (AppCompatTextView) itemView.findViewById(R.id.messagecontent);
            time = (AppCompatTextView) itemView.findViewById(R.id.time);
        }
    }
}
