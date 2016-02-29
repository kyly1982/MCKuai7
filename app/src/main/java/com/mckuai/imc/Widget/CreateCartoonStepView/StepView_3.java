package com.mckuai.imc.Widget.CreateCartoonStepView;

import android.content.Context;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.AppCompatEditText;
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


    private void initView(final Context context) {
        View view = inflate(context, R.layout.createcartoon_step3, this);
        final AppCompatEditText editText = (AppCompatEditText) view.findViewById(R.id.createcartoon_talk);
        final AppCompatButton addTalk = (AppCompatButton) view.findViewById(R.id.createcartoon_addtalk);
        /*editText.addTextChangedListener(new TextWatcher() {
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
                } else if (length > 16){
                    addTalk.setEnabled(false);
                    Toast.makeText(context, "为了", Toast.LENGTH_SHORT).show();
                } else {
                    addTalk.setEnabled(true);
                }
            }
        });*/

        addTalk.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != listener && 0 != editText.getText().toString().trim().length()) {
                    listener.onTalkAdded(editText.getText().toString());
                    editText.setText("");
                }
            }
        });
    }
}
