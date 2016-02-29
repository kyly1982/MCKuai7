package com.mckuai.imc.Widget.CommentView;

import android.content.Context;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.AppCompatTextView;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;

import com.mckuai.imc.Base.MCKuai;
import com.mckuai.imc.Bean.Comment;
import com.mckuai.imc.R;
import com.nostra13.universalimageloader.core.ImageLoader;

/**
 * Created by kyly on 2016/1/22.
 */
public class CommentView extends FrameLayout {

    private View view;
    private AppCompatImageView cover;
    private AppCompatTextView name;
    private AppCompatTextView time;
    private AppCompatTextView content;

    private ImageLoader imageLoader;

    public CommentView(Context context, boolean isSimpleLayout) {
        super(context);
        initView(context, isSimpleLayout);
        addView(view);
        imageLoader = ImageLoader.getInstance();
    }

    private void initView(Context context, boolean isSimpleLayout) {
        if (isSimpleLayout) {
            view = LayoutInflater.from(context).inflate(R.layout.element_comment_simple, this, false);
        } else {
            view = LayoutInflater.from(context).inflate(R.layout.element_comment, this, false);
        }

        cover = (AppCompatImageView) view.findViewById(R.id.comment_usercover);
        name = (AppCompatTextView) view.findViewById(R.id.comment_username);
        time = (AppCompatTextView) view.findViewById(R.id.comment_time);
        content = (AppCompatTextView) view.findViewById(R.id.comment_content);
    }

    public void setData(Comment comment) {
        imageLoader.displayImage(comment.getOwner().getHeadImage(), cover, MCKuai.instence.getCircleOptions());
        name.setText(comment.getOwner().getNickEx());
        content.setText(comment.getContent());
        time.setText(comment.getTimeEx());
        invalidate();
    }

}
