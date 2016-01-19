package com.mckuai.imc.Fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.mckuai.imc.Base.BaseFragment;
import com.mckuai.imc.R;

/**
 * A placeholder fragment containing a simple view.
 */
public class CartoonFragment extends BaseFragment {

    public CartoonFragment() {
        mTitleResId = R.string.fragment_cartoon;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_cartoon, container, false);
    }
}
