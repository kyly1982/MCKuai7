package com.mckuai.imc.Adapter;

import android.content.Context;
import android.support.v7.widget.AppCompatImageButton;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.mckuai.imc.Bean.User;
import com.mckuai.imc.R;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.ArrayList;

/**
 * Created by kyly on 2016/2/1.
 */
public class FriendAdapter extends RecyclerView.Adapter<FriendAdapter.ViewHolder> {
    private ArrayList<User> friends;
    private Context context;
    private ImageLoader loader;
    private OnItemClickListener listener;

    public FriendAdapter(Context context, OnItemClickListener listener) {
        this.context = context;
        this.listener = listener;
        loader = ImageLoader.getInstance();
    }

    public void setData(ArrayList<User> friends){
        this.friends = friends;
        notifyDataSetChanged();
    }


    public interface OnItemClickListener{
        void onItemClicked(User user);
        void onChatClicked(User user);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_friend,parent);
        ViewHolder holder = new ViewHolder(view);
        if (null != listener){
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    User user = (User) v.getTag();
                    if (null != user){
                        listener.onItemClicked(user);
                    }
                }
            });
        }
        return holder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        if (null != friends && -1 < position && position < friends.size()){
            User user = friends.get(position);
            holder.itemView.setTag(user);
            holder.chat.setTag(user);
            if (null != user) {
                loader.displayImage(user.getHeadImage(), holder.cover);
                holder.name.setText(user.getNickEx());
                holder.level.setText(user.getLevel() + "");
                if (null != listener){
                    holder.chat.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            User user = (User) v.getTag();
                            if (null != user){
                                listener.onChatClicked(user);
                            }
                        }
                    });
                }
            }
        }
    }

    @Override
    public int getItemCount() {
        return null == friends ? 0:friends.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{
        private AppCompatImageView cover;
        private AppCompatImageButton chat;
        private AppCompatTextView name;
        private AppCompatTextView level;

        public ViewHolder(View itemView) {
            super(itemView);
            cover = (AppCompatImageView) itemView.findViewById(R.id.usercover);
            chat = (AppCompatImageButton) itemView.findViewById(R.id.chat);
            name = (AppCompatTextView) itemView.findViewById(R.id.username);
            level = (AppCompatTextView) itemView.findViewById(R.id.level);
        }
    }
}
