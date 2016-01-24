package com.mckuai.imc.Adapter;

import android.content.Context;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.mckuai.imc.Bean.ForumInfo;
import com.mckuai.imc.R;

import java.util.ArrayList;

/**
 * Created by kyly on 2016/1/25.
 */
public class ForumAdapter extends RecyclerView.Adapter<ForumAdapter.ViewHolder> {
    private ArrayList<ForumInfo> forums;
    private Context context;
    private View.OnClickListener listener;


    public ForumAdapter(Context context, View.OnClickListener listener) {
        this.context = context;
        this.listener = listener;
    }

    public void setData(ArrayList<ForumInfo> forums){
        this.forums = forums;
        notifyDataSetChanged();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_forum,parent);
        if (null != listener) {
            view.setOnClickListener(listener);
        }
        ViewHolder holder = new ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        if (null != forums && -1 < position && position < forums.size()){
            ForumInfo forum = forums.get(position);
            if (null != forum){
                String name = forum.getName();
                int length = name.length();
                StringBuilder builder = new StringBuilder(name);
                builder.insert(length / 2,"\r\n");
                holder.name.setText(name);
            }
        }
    }

    @Override
    public int getItemCount() {
        return null == forums ? 0:forums.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{
        public AppCompatTextView name;
        private CardView forum;

        public ViewHolder(View itemView) {
            super(itemView);
            name = (AppCompatTextView) itemView.findViewById(R.id.community_forumname);
        }
    }
}
