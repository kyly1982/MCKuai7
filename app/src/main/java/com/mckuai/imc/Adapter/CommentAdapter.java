package com.mckuai.imc.Adapter;

import android.content.Context;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.mckuai.imc.Bean.Comment;
import com.mckuai.imc.R;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.ArrayList;

/**
 * Created by kyly on 2016/1/22.
 */
public class CommentAdapter extends RecyclerView.Adapter<CommentAdapter.ViewHolder>  {
    private Context mContext;
    private LayoutInflater mInflater;

    private ImageLoader mImageLoader;

    private ArrayList<Comment> mComments;
    private int mLayout_resId;


    public CommentAdapter(Context context,int layout_resId) {
        this.mContext = context;
        this.mLayout_resId = layout_resId;
        mInflater = LayoutInflater.from(context);
        mImageLoader = ImageLoader.getInstance();
    }

    public void setData(ArrayList<Comment> comments){
        this.mComments = comments;
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return null == mComments ? 0:mComments.size();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mInflater.inflate(mLayout_resId,parent);
        final ViewHolder holder = new ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        if (null != mComments && -1 < position && position < mComments.size()){
            Comment comment = mComments.get(position);
            if (null != comment){
                mImageLoader.displayImage(comment.getOwner().getCover(),holder.cover);
                holder.name.setText(comment.getOwner().getNickEx());
                holder.time.setText(comment.getTimeEx());
                holder.content.setText(comment.getContent());
            }
        }
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{
        public AppCompatImageView cover;
        public AppCompatTextView name;
        public AppCompatTextView time;
        public AppCompatTextView content;

        public ViewHolder(View itemView) {
            super(itemView);
            this.cover = (AppCompatImageView) itemView.findViewById(R.id.comment_usercover);
            this.name = (AppCompatTextView) itemView.findViewById(R.id.comment_username);
            this.content = (AppCompatTextView) itemView.findViewById(R.id.comment_content);
            this.time = (AppCompatTextView) itemView.findViewById(R.id.comment_time);
        }
    }
}
