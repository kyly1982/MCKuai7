package com.mckuai.imc.Widget.CreateCartoonStepView;

import android.content.Context;
import android.support.design.widget.TextInputLayout;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.AppCompatEditText;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.RelativeLayout;

import com.mckuai.imc.R;

/**
 * Created by kyly on 2016/1/26.
 */
public class StepView_3 extends RelativeLayout {
    private OnTalkAddedListener listener;

    public interface OnTalkAddedListener{
        void onTalkAdded(String talk);
    }


    public StepView_3(Context context,OnTalkAddedListener listener) {
        super(context);
        this.listener = listener;
        initView(context);
    }


    private void initView(Context context){
        View view = inflate(context, R.layout.createcartoon_step3, this);
        final TextInputLayout inputLayout = (TextInputLayout) view.findViewById(R.id.createcartoon_talkcontent);
        final AppCompatEditText editText = (AppCompatEditText) inputLayout.getEditText();
        final AppCompatButton addTalk = (AppCompatButton) view.findViewById(R.id.createcartoon_addtalk);
        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                int length = s.length();
                if (length == 0){
                    addTalk.setEnabled(true);
                    inputLayout.setHint("输入人物对话，或者点击下一步.");
                } else if (length > 16){
                    addTalk.setEnabled(false);
                    inputLayout.setHint("对话内容太长了!");
                } else {
                    addTalk.setEnabled(true);
                    inputLayout.setHint("还能输入"+(16 - length) + "个字.");
                }
            }
        });

        addTalk.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != listener && 0 != editText.getText().toString().trim().length()) {
                    listener.onTalkAdded(editText.getText().toString());
                    //editText.setFocusable(false);
                }
            }
        });
    }
}
