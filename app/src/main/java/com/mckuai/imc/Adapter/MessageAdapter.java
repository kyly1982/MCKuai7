package com.mckuai.imc.Adapter;

import android.content.Context;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.mckuai.imc.Base.MCKuai;
import com.mckuai.imc.Bean.Cartoon;
import com.mckuai.imc.R;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.ArrayList;

/**
 * Created by kyly on 2016/2/4.
 */
public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.ViewHolder> {
    private Context context;
    private ImageLoader loader;
    private ArrayList<Cartoon> cartoons;
    private OnItemClickListener listener;

    public interface OnItemClickListener {
        void OnItemClicked(Cartoon cartoon);

        void OnAddFriendClick(Cartoon cartoon);

        void OnChatClick(Cartoon cartoon);
    }

    public MessageAdapter(Context context, OnItemClickListener listener) {
        this.context = context;
        this.listener = listener;
        this.loader = ImageLoader.getInstance();
    }

    public void setData(ArrayList<Cartoon> cartoons) {
        this.cartoons = cartoons;
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return null == cartoons ? 0 : cartoons.size();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_message, parent, false);
        ViewHolder holder = new ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        if (null != cartoons) {
            final Cartoon cartoon = cartoons.get(position);
            if (null != cartoon) {
                loader.displayImage(cartoon.getOwner().getHeadImage(), holder.usercover, MCKuai.instence.getCircleOptions());
                loader.displayImage(cartoon.getImage(), holder.cartooncover);
                holder.username.setText(cartoon.getOwner().getNickEx());
                holder.time.setText(cartoon.getTimeEx());
                if (null != listener) {
                    holder.itemView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            listener.OnItemClicked(cartoon);
                        }
                    });

                    holder.chat.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            listener.OnChatClick(cartoon);
                        }
                    });
                    holder.addfriend.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            listener.OnAddFriendClick(cartoon);
                        }
                    });
                }
            }
        }
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private AppCompatImageView usercover;
        private AppCompatImageView cartooncover;
        private AppCompatButton addfriend;
        private AppCompatButton chat;
        private AppCompatTextView time;
        private AppCompatTextView username;

        public ViewHolder(View itemView) {
            super(itemView);
            usercover = (AppCompatImageView) itemView.findViewById(R.id.usercover);
            cartooncover = (AppCompatImageView) itemView.findViewById(R.id.cartooncover);
            addfriend = (AppCompatButton) itemView.findViewById(R.id.addfriend);
            chat = (AppCompatButton) itemView.findViewById(R.id.chat);
            time = (AppCompatTextView) itemView.findViewById(R.id.time);
            username = (AppCompatTextView) itemView.findViewById(R.id.username);
        }
    }
}
