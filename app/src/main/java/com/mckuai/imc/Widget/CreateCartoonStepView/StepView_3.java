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

import java.util.ArrayList;

/**
 * Created by kyly on 2016/1/26.
 */
public class StepView_3 extends RelativeLayout {
    private ArrayList<String> talks;


    public StepView_3(Context context) {
        super(context);
        talks = new ArrayList<>(5);
        initView(context);
    }

    public ArrayList<String> getTalks(){
        return talks;
    }

    private void initView(Context context){
        //LayoutInflater inflater = LayoutInflater.from(context);
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
                talks.add(editText.getText().toString());
            }
        });
    }
}
