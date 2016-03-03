package com.mckuai.imc.Widget.CreateCartoonStepView;

import android.content.Context;
import android.support.v7.widget.AppCompatEditText;
import android.support.v7.widget.AppCompatTextView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.mckuai.imc.Base.MCKuai;
import com.mckuai.imc.R;

/**
 * Created by kyly on 2016/1/26.
 */
public class StepView_4 extends RelativeLayout {

    public interface OnShareButtonClickedListener{
        void onShareButtonClicked();

        void onTitleChanged(String title);

        void onSend();
    }

    public StepView_4(Context context,OnShareButtonClickedListener listener) {
        super(context);
        initView(context,listener);
    }


    private void initView(final Context context, final OnShareButtonClickedListener listener) {
        inflate(context, R.layout.createcartoon_step4, this);
        AppCompatTextView sync = (AppCompatTextView) findViewById(R.id.createcartoon_sync);
        //inputLayout.setHint("给大作起个名");
        final AppCompatEditText editText = (AppCompatEditText) findViewById(R.id.createcartoon_talk);
        sync.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != listener){
                    listener.onShareButtonClicked();
                }
            }
        });

        editText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEND) {
                    if (0 < editText.getText().toString().length() && null != listener) {
                        listener.onSend();
                        MCKuai.instence.hideSoftKeyboard(editText);
                    }
                }
                return false;
            }
        });

        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                if (25 == count) {
                    Toast.makeText(context, "最多只能输入25个字", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (null != listener) {
                    int length = s.length();
                    if (0 == length) {
                        listener.onTitleChanged("");
                    } else {
                        listener.onTitleChanged(s.toString().substring(0, length > 25 ? 25 : length));
                    }
                }
            }
        });
    }
}
