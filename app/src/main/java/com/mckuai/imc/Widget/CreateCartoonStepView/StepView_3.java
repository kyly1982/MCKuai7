package com.mckuai.imc.Widget.CreateCartoonStepView;

import android.content.Context;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.AppCompatEditText;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.mckuai.imc.R;

/**
 * Created by kyly on 2016/1/26.
 */
public class StepView_3 extends RelativeLayout {
    private OnTalkAddedListener listener;
    private AppCompatEditText editText;

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
        editText = (AppCompatEditText) view.findViewById(R.id.createcartoon_talk);
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
        editText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    sendTalk();
                    hideSoftKeyboard();
                    return true;
                }
                return false;
            }
        });

        addTalk.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                sendTalk();
                hideSoftKeyboard();
            }
        });
    }

    private void sendTalk() {
        if (null != listener && 0 != editText.getText().toString().trim().length()) {
            listener.onTalkAdded(editText.getText().toString());
            editText.setText("");
        }
    }

    private void hideSoftKeyboard() {
        InputMethodManager manager = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        manager.hideSoftInputFromWindow(editText.getWindowToken(), 0);
    }
}
