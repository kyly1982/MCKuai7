package com.mckuai.imc.Adapter;

import android.content.Context;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.AppCompatCheckBox;
import android.support.v7.widget.AppCompatImageButton;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.mckuai.imc.Bean.Cartoon;
import com.mckuai.imc.Bean.Comment;
import com.mckuai.imc.R;
import com.mckuai.imc.Widget.CommentView.CommentView;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.ArrayList;

/**
 * Created by kyly on 2016/1/22.
 */
public class CartoonAdapter extends RecyclerView.Adapter<CartoonAdapter.ViewHolder> implements View.OnClickListener{
    private Context mContext;
    private ArrayList<Cartoon> mCartoons;

    private LayoutInflater inflater;
    private ImageLoader imageLoader;
    private View.OnClickListener listener;

    public CartoonAdapter(Context context) {
        if (null != context) {
            this.mContext = context;
            inflater = LayoutInflater.from(context);
            imageLoader = ImageLoader.getInstance();
        }
    }

    public CartoonAdapter(Context context, View.OnClickListener listener) {
        if (null != context) {
            this.mContext = context;
            this.listener = listener;
            inflater = LayoutInflater.from(context);
            imageLoader = ImageLoader.getInstance();
        }
    }

    public void setData(ArrayList<Cartoon> cartoons) {
        this.mCartoons = cartoons;
        notifyDataSetChanged();
    }

    public void setOnClickedListener(View.OnClickListener listener){
        if (null != listener){
            this.listener = listener;
        }
    }

    @Override
    public int getItemCount() {
        return null == mCartoons ? 0:mCartoons.size();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, final int viewType) {
        View view = inflater.inflate(R.layout.element_cartoon, parent, false);
        final ViewHolder holder = new ViewHolder(view);
        int height = parent.getHeight();
        int width = height * 9 / 16;
        ViewGroup.LayoutParams layoutParams = view.getLayoutParams();
        layoutParams.width = width;
        layoutParams.height = height;
        view.setLayoutParams(layoutParams);

        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) holder.image.getLayoutParams();
        params.height = width;
        holder.image.setLayoutParams(params);
        return holder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        if (null != mCartoons && !mCartoons.isEmpty() && -1 < position && position < mCartoons.size()){
            Cartoon cartoon = mCartoons.get(position);
            bindData(cartoon,holder);
        }
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public AppCompatImageView userCover;
        public AppCompatImageButton share;
        public AppCompatButton comment;
        public AppCompatCheckBox prise;
        public AppCompatTextView userName;
        public AppCompatTextView time;
        public AppCompatImageView image;
        public LinearLayout commentList;

        public ViewHolder(View itemView) {
            super(itemView);
            userCover = (AppCompatImageView) itemView.findViewById(R.id.cartoon_usercover);
            userName = (AppCompatTextView) itemView.findViewById(R.id.cartoon_username);
            time = (AppCompatTextView) itemView.findViewById(R.id.cartoon_createtime);
            share = (AppCompatImageButton) itemView.findViewById(R.id.cartoon_shar);
            comment = (AppCompatButton) itemView.findViewById(R.id.cartoon_comment);
            prise = (AppCompatCheckBox) itemView.findViewById(R.id.cartoon_prise);
            image = (AppCompatImageView) itemView.findViewById(R.id.cartoon_image);
            commentList = (LinearLayout) itemView.findViewById(R.id.cartoon_comment_root);


        }
    }

    private void bindData(Cartoon cartoon,ViewHolder holder){
        showImage(cartoon.getImage(), holder.image, false);
        showImage(cartoon.getOwner().getHeadImage(), holder.userCover, true);
        if (null != cartoon.getComments()) {
            showComment(cartoon.getComments(), holder.commentList);
        }
        holder.userName.setText(cartoon.getOwner().getNickEx());
        holder.time.setText(cartoon.getTimeEx());
        holder.comment.setText(cartoon.getReplyNum() + "");
        holder.prise.setText(cartoon.getPrise() + "");

        holder.comment.setTag(cartoon);
        holder.prise.setTag(cartoon);
        holder.share.setTag(cartoon);
        holder.userCover.setTag(cartoon);


        holder.userCover.setOnClickListener(this);
        holder.comment.setOnClickListener(this);
        holder.prise.setOnClickListener(this);
        holder.share.setOnClickListener(this);


    }


    private void showImage(String url,AppCompatImageView imageView,boolean isCircle){
        if (null != url && null != imageView && 10 < url.length()){
            imageLoader.displayImage(url,imageView);
        }
    }

    private void showComment(ArrayList<Comment> comments,LinearLayout root){
        if (null == root.getTag()) {
            for (Comment comment : comments) {
                CommentView commentView = new CommentView(mContext);
                commentView.setData(comment);
                root.addView(commentView);
            }
            root.setTag(comments);
        }

    }

    @Override
    public void onClick(View v) {
        if (null != listener){
            listener.onClick(v);
        }
    }
}
