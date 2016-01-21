package com.mckuai.imc.Fragment;

import android.os.Bundle;
import android.support.v7.widget.AppCompatRadioButton;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioGroup;

import com.malinskiy.superrecyclerview.SuperRecyclerView;
import com.mckuai.imc.Base.BaseFragment;
import com.mckuai.imc.R;

import java.util.ArrayList;

/**
 * A placeholder fragment containing a simple view.
 */
public class CartoonFragment extends BaseFragment {
    private String[] mCartoonType;


    private View mView;
    private SuperRecyclerView mCartoonListView;
    private RadioGroup mType;
    private AppCompatRadioButton mHot;
    private AppCompatRadioButton mNew;

    public CartoonFragment() {
        mTitleResId = R.string.fragment_cartoon;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (null == mView) {
            mView = inflater.inflate(R.layout.fragment_cartoon, container, false);
        }
        return mView;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (null== mCartoonListView){
            initView();
        } else {
            showData();
        }
    }

    private void initView(){

    }

    private void loadData(){

    }

    private void showData(){

    }
}
