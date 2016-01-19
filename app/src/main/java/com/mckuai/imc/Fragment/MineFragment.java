package com.mckuai.imc.Fragment;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.mckuai.imc.Base.BaseFragment;
import com.mckuai.imc.R;

public class MineFragment extends BaseFragment {

    public MineFragment() {
        mTitleResId = R.string.fragment_mine;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_mine, container, false);
    }

}
