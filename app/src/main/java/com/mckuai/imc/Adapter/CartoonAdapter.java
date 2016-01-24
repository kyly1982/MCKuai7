package com.mckuai.imc.Adapter;

import android.content.Context;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.AppCompatImageButton;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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

    public static int width;
    private int height;

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
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.item_cartoon, parent, false);
        width = parent.getWidth();
        height = parent.getHeight();
        ViewGroup.LayoutParams layoutParams = view.getLayoutParams();
        layoutParams.width = width - width / 5;
        layoutParams.height = height;
        view.setLayoutParams(layoutParams);
        final ViewHolder holder = new ViewHolder(view);
        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) holder.image.getLayoutParams();
        params.width = width - width / 5;
        params.height = width - width / 5;
        holder.image.setLayoutParams(params);
        return holder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        if (null != mCartoons && !mCartoons.isEmpty() && -1 < position && position < mCartoons.size()){
            Cartoon cartoon = mCartoons.get(position);
            bindData(cartoon,holder);
            setListener(cartoon,holder,position);
        }
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public AppCompatImageView userCover;
        public AppCompatImageButton share;
        public AppCompatButton comment;
        public AppCompatButton prise;
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
            prise = (AppCompatButton) itemView.findViewById(R.id.cartoon_prise);
            image = (AppCompatImageView) itemView.findViewById(R.id.cartoon_image);
            commentList = (LinearLayout) itemView.findViewById(R.id.cartoon_comment_root);


        }
    }

    private void bindData(Cartoon cartoon,ViewHolder holder){
        showImage(cartoon.getImage(), holder.image, false);
        showImage(cartoon.getOwner().getCover(), holder.userCover, true);
        showComment(cartoon.getComments(), holder.commentList);
        holder.userName.setText(cartoon.getOwner().getNickEx());
        holder.time.setText(cartoon.getTimeEx());
       /* ViewGroup.LayoutParams layoutParams = holder.image.getLayoutParams();
        layoutParams.width = width;
        layoutParams.height = width;
        holder.image.setLayoutParams(layoutParams);*/
        holder.comment.setTag(cartoon);
        holder.prise.setTag(cartoon);
        holder.userCover.setTag(cartoon.getOwner());
        holder.share.setTag(cartoon);
    }

    private void setListener(Cartoon cartoon,ViewHolder holder,int position){
        if (null != listener){
            holder.userCover.setOnClickListener(this);
            holder.comment.setOnClickListener(this);
            holder.prise.setOnClickListener(this);
            holder.share.setOnClickListener(this);
        }
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

/*    public static class CommentViewHolder{
        public AppCompatImageView commentUserCover;
        public AppCompatTextView commentUserName;
        public AppCompatTextView commentContent;
        public AppCompatTextView commentTime;

        public CommentViewHolder(View itemView) {
            commentUserCover = (AppCompatImageView) itemView.findViewById(R.id.comment_usercover);
            commentUserName = (AppCompatTextView) itemView.findViewById(R.id.comment_username);
            commentContent = (AppCompatTextView) itemView.findViewById(R.id.comment_content);
            commentTime = (AppCompatTextView) itemView.findViewById(R.id.comment_time);
        }
    }*/

    @Override
    public void onClick(View v) {
        if (null != listener){
            listener.onClick(v);
        }
    }
}
