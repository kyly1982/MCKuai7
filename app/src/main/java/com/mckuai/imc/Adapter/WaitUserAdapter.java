package com.mckuai.imc.Adapter;

import android.content.Context;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

import com.mckuai.imc.Bean.User;
import com.mckuai.imc.R;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.ArrayList;

/**
 * Created by kyly on 2016/2/17.
 */
public class WaitUserAdapter extends RecyclerView.Adapter<WaitUserAdapter.ViewHolder> {
    private Context context;
    private ArrayList<User> users;
    private OnItemClickListener listener;
    private ImageLoader loader;

    public WaitUserAdapter(Context context, OnItemClickListener listener) {
        this.context = context;
        this.listener = listener;
        loader = ImageLoader.getInstance();
    }

    public void setData(ArrayList<User> users) {
        this.users = users;
        notifyDataSetChanged();
    }

    public interface OnItemClickListener {
        void onItemClicked(User user);
    }

    @Override
    public int getItemCount() {
        return null == users ? 0 : users.size();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return null;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public AppCompatImageView cover;
        public AppCompatTextView name;

        public ViewHolder(View itemView) {
            super(itemView);
            cover = (AppCompatImageView) itemView.findViewById(R.id.usercover_big);
            name = (AppCompatTextView) itemView.findViewById(R.id.username);
        }
    }
}
