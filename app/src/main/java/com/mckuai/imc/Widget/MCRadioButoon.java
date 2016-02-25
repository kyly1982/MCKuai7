package com.mckuai.imc.Widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.AppCompatTextView;
import android.util.AttributeSet;
import android.view.View;
import android.widget.Checkable;
import android.widget.FrameLayout;

import com.mckuai.imc.R;

/**
 * Created by kyly on 2016/2/25.
 */
public class MCRadioButoon extends FrameLayout implements Checkable {
    private OnCheckedChangeListener mOnCheckedChangeListener;
    private boolean mChecked = false;
    private Drawable image_checked;
    private Drawable image_normal;
    private int textColor_checked;
    private int textColor_normal;
    private float textSize;
    private String text;


    private AppCompatImageView mImage;
    private AppCompatTextView mText;
    private View mMessage;

    public MCRadioButoon(Context context, AttributeSet attrs) {
        super(context, attrs);
        initAttars(context, 0, attrs);
        initView(context);
    }

    public MCRadioButoon(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.MCRadioButoon);
        initAttars(context, defStyleAttr, attrs);
        initView(context);
    }

    private void initView(Context context) {
        inflate(context, R.layout.mc_radiobutton, this);
        mImage = (AppCompatImageView) findViewById(R.id.icon);
        mText = (AppCompatTextView) findViewById(R.id.text);
        mMessage = findViewById(R.id.newmessage);
        mText.setTextSize(textSize);
        refreshDrawableState();

    }

    private void initAttars(Context context, int defStyleAttr, AttributeSet attrs) {
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.MCRadioButoon, defStyleAttr, 0);
        textColor_checked = a.getColor(R.styleable.MCRadioButoon_textColor_checked, 0xFFFFFF);
        textColor_normal = a.getColor(R.styleable.MCRadioButoon_textColor_normal, 0xFFFFFF);
        image_checked = a.getDrawable(R.styleable.MCRadioButoon_button_checked);
        image_normal = a.getDrawable(R.styleable.MCRadioButoon_button_normal);
        mChecked = a.getBoolean(R.styleable.MCRadioButoon_textColor_checked, false);
        text = a.getString(R.styleable.MCRadioButoon_text);
        textSize = a.getDimension(R.styleable.MCRadioButoon_textSize, 10);

        a.recycle();
    }

    public void showMessageIcon() {
        if (null == mMessage) {
            mMessage = findViewById(R.id.newmessage);
        }
        mMessage.setVisibility(VISIBLE);
    }

    public void hideMessageIcon() {
        if (null != mMessage) {
            mMessage.setVisibility(GONE);
        }
    }

    public void setOnCheckedChangeListener(OnCheckedChangeListener listener) {
        this.mOnCheckedChangeListener = listener;
    }


    public interface OnCheckedChangeListener {
        /**
         * Called when the checked state of a compound button has changed.
         *
         * @param buttonView The compound button view whose state has changed.
         * @param isChecked  The new checked state of buttonView.
         */
        void onCheckedChanged(View buttonView, boolean isChecked);
    }

    public void refreshDrawableState() {
        if (null != text && !mText.getText().equals(text)) {
            mText.setText(text);
        }
        if (mChecked) {
            if (null != image_checked) {
                mImage.setBackgroundDrawable(image_checked);
            }
            mText.setTextColor(textColor_checked);

        } else {
            if (null != image_normal) {
                mImage.setBackgroundDrawable(image_normal);
            } else {
                mImage.getBackground().setAlpha(0);
            }
            mText.setTextColor(textColor_normal);
        }
    }

    @Override
    public boolean isChecked() {
        return mChecked;
    }

    @Override
    public void setChecked(boolean checked) {
        if (checked != checked) {
            this.mChecked = checked;
            refreshDrawableState();
            postInvalidate();
            if (null != mOnCheckedChangeListener) {
                mOnCheckedChangeListener.onCheckedChanged(this, mChecked);
            }
        }
    }

    @Override
    public void toggle() {
        if (isChecked()) {
            setChecked(!mChecked);
        }
    }
}
