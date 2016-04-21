package com.mckuai.imc.Widget;

import android.content.Context;
import android.support.v7.widget.AppCompatImageButton;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.AppCompatTextView;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.mckuai.imc.Base.MCKuai;
import com.mckuai.imc.Bean.Cartoon;
import com.mckuai.imc.Bean.Comment;
import com.mckuai.imc.Bean.User;
import com.mckuai.imc.R;
import com.mckuai.imc.Widget.CommentView.CommentView;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.ArrayList;

/**
 * Created by kyly on 2016/1/28.
 */
public class CartoonView extends FrameLayout implements View.OnClickListener {
    private AppCompatImageView userCover;
    private AppCompatImageButton share;
    private RelativeLayout comment;
    private RelativeLayout prise;
    private AppCompatTextView userName;
    private AppCompatTextView time;
    private AppCompatTextView name;
    private AppCompatImageView image;
    private LinearLayout commentList;
    private LinearLayout rewardList;
    private AppCompatTextView commentCount;
    private AppCompatTextView priseCount;
    private AppCompatTextView pkCount;
    private AppCompatTextView pkWinCount;
    private AppCompatTextView pkRate;

    private ImageLoader loader;
    private OnCartoonElementClickListener listener;

    private boolean isDetailed = true;
    private Cartoon cartoon;
    private Context context;

    public interface OnCartoonElementClickListener {
        void onOwnerClicked(int ownerId);

        void onShareCartoon(Cartoon cartoon);

        void onCommentCartoon(Cartoon cartoon);

        void onRewardCartoon(Cartoon cartoon);
    }

    public CartoonView(Context context) {
        super(context);
        this.context = context;
        loader = ImageLoader.getInstance();
        initView(context);
    }

    public CartoonView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        loader = ImageLoader.getInstance();
        initView(context);
    }

    public CartoonView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context = context;
        loader = ImageLoader.getInstance();
        initView(context);
    }

    public void setData(Cartoon cartoon, boolean isDetailed) {
        if (null != cartoon) {
            this.isDetailed = isDetailed;
            this.cartoon = cartoon;
            bindData(cartoon);
            postInvalidate();
        }
    }

    public void setOnCartoonElementClickListener(OnCartoonElementClickListener listener) {
        this.listener = listener;
    }

    private void initView(Context context) {
        inflate(context, R.layout.element_cartoon, this);
        userCover = (AppCompatImageView) findViewById(R.id.cartoon_usercover);
        time = (AppCompatTextView) findViewById(R.id.cartoon_createtime);
        share = (AppCompatImageButton) findViewById(R.id.cartoon_shar);
        comment = (RelativeLayout) findViewById(R.id.cartoon_comment);
        prise = (RelativeLayout) findViewById(R.id.cartoon_prise);
        image = (AppCompatImageView) findViewById(R.id.cartoon_image);
        commentList = (LinearLayout) findViewById(R.id.cartoon_comment_root);
        userName = (AppCompatTextView) findViewById(R.id.cartoon_username);

        commentCount = (AppCompatTextView) findViewById(R.id.commentCount);
        priseCount = (AppCompatTextView) findViewById(R.id.priseCount);
        pkCount = (AppCompatTextView) findViewById(R.id.pkcount);
        pkWinCount = (AppCompatTextView) findViewById(R.id.wincount);
        pkRate = (AppCompatTextView) findViewById(R.id.pkrate);


        ViewTreeObserver viewTreeObserver = getViewTreeObserver();
        viewTreeObserver.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                getViewTreeObserver().removeGlobalOnLayoutListener(this);
                RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) image.getLayoutParams();
                params.height = getWidth();
                image.setLayoutParams(params);
            }
        });

        userCover.setOnClickListener(this);
        share.setOnClickListener(this);
        prise.setOnClickListener(this);
        comment.setOnClickListener(this);
    }

    private void bindData(Cartoon cartoon) {
        showImage(cartoon.getImage(), image, false);
        if (null != cartoon.getOwner()) {
            showImage(cartoon.getOwner().getHeadImage(), userCover, true);
        }
        if (null != cartoon.getComments()) {
            showComment(cartoon.getComments(), commentList);
        }
        userName.setText(cartoon.getOwner().getNickEx());
        time.setText(cartoon.getTimeEx());
        if (cartoon.getReplyNum() != 0) {
            commentCount.setText(cartoon.getReplyNum() + "");
        } else {
            commentCount.setText("");
        }
        if (cartoon.getPrise() != 0) {
            priseCount.setText(cartoon.getPrise() + "");
        } else {
            priseCount.setText("");
        }

        pkCount.setText(cartoon.getAllPk()+"");
        pkWinCount.setText(cartoon.getAllPrise()+"");
        if (0 == cartoon.getAllPk() || 0 == cartoon.getAllPrise()){
            pkRate.setText("0");
        } else {
            pkRate.setText((int) (100 * cartoon.getAllPrise() / cartoon.getAllPk()) + "%");
        }

        if (isDetailed) {
            name = (AppCompatTextView) findViewById(R.id.cartoon_name);
            name.setText(cartoon.getKindsEx() + "");
            name.setVisibility(VISIBLE);
            //
            if (null != cartoon.getRewardList() && !cartoon.getRewardList().isEmpty()) {
                showRewardUser(cartoon.getRewardList());
            }
        }

        userCover.setTag(cartoon.getOwner().getId());
        share.setTag(cartoon.getId());
        comment.setTag(cartoon.getId());
        prise.setTag(cartoon.getId());
    }


    private void showImage(String url, AppCompatImageView imageView, boolean isCircle) {
        if (null != url && null != imageView && 10 < url.length()) {
            if (isCircle) {
                loader.displayImage(url, imageView, MCKuai.instence.getCircleOptions());
            } else {
                loader.displayImage(url, imageView);
            }
        }
    }

    private void showComment(ArrayList<Comment> comments, LinearLayout root) {
        if (null == root.getTag()) {
            for (Comment comment : comments) {
                CommentView commentView = new CommentView(getContext(), !isDetailed);
                commentView.setData(comment);
                root.addView(commentView);
            }
            root.setTag(comments);
        }

    }

    private void showRewardUser(ArrayList<User> rewardUsers) {
        if (null == rewardList && !rewardUsers.isEmpty()) {
            rewardList = (LinearLayout) findViewById(R.id.cartoon_rewarduser);
            for (User user : rewardUsers) {
                AppCompatImageView cover = (AppCompatImageView) inflate(context, R.layout.element_rewarduser, null);
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(getResources().getDimensionPixelSize(R.dimen.usercover_width_small), getResources().getDimensionPixelSize(R.dimen.usercover_width_small));
                params.setMargins(getResources().getDimensionPixelSize(R.dimen.padding_content), getResources().getDimensionPixelSize(R.dimen.padding_content), getResources().getDimensionPixelSize(R.dimen.padding_content), getResources().getDimensionPixelSize(R.dimen.padding_content));
                cover.setLayoutParams(params);
                rewardList.addView(cover);
                //showImage(user.getHeadImage(), cover, true);
                //loader.displayImage(user.getHeadImage(), cover, MCKuai.instence.getCircleOptions());
                loader.displayImage(user.getHeadImage(), cover, MCKuai.instence.getCircleOptions());
            }
            rewardList.setVisibility(VISIBLE);
        }
    }

    @Override
    public void onClick(View v) {
        if (null != listener) {
            switch (v.getId()) {
                case R.id.cartoon_usercover:
                    listener.onOwnerClicked(cartoon.getOwner().getId().intValue());
                    break;
                case R.id.cartoon_shar:
                    listener.onShareCartoon(cartoon);
                    break;
                case R.id.cartoon_comment:
                    listener.onCommentCartoon(cartoon);
                    break;
                case R.id.cartoon_prise:
                    listener.onRewardCartoon(cartoon);
                    break;
            }
        }
    }

}
