package com.mckuai.imc.Fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputLayout;
import android.support.v7.widget.AppCompatAutoCompleteTextView;
import android.support.v7.widget.AppCompatEditText;
import android.support.v7.widget.AppCompatImageButton;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.mckuai.imc.Base.BaseFragment;
import com.mckuai.imc.Base.MCKuai;
import com.mckuai.imc.Bean.MCUser;
import com.mckuai.imc.R;
import com.nostra13.universalimageloader.core.ImageLoader;

/**
 * Created by kyly on 2016/2/2.
 */
public class ProfileEditerFragment extends BaseFragment {
    private MCUser user ;
    private ImageLoader loader;
    private AppCompatImageButton usercover;
    private AppCompatAutoCompleteTextView useraddress;
    private TextInputLayout usernick;
    private AppCompatEditText nickediter;
    private View view;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (null != view){
            container.removeView(view);
        }
        view = inflater.inflate(R.layout.fragment_profile_editer,container,false);
        loader = ImageLoader.getInstance();
        user = MCKuai.instence.user;
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (null == usercover){
            initView();
        }
        showData();
    }

    private void initView(){
        usercover = (AppCompatImageButton) view.findViewById(R.id.usercover);
        useraddress = (AppCompatAutoCompleteTextView) view.findViewById(R.id.useraddr);
        usernick = (TextInputLayout) view.findViewById(R.id.usernick);
        nickediter = (AppCompatEditText) usernick.getEditText();
    }

    public void showData(){
        loader.displayImage(user.getHeadImg(),usercover,MCKuai.instence.getCircleOptions());
        nickediter.setText(user.getNike());
        useraddress.setText(user.getAddr());
        usernick.setHint("昵称");
    }
}
