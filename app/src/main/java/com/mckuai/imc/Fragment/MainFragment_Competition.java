package com.mckuai.imc.Fragment;

import android.os.Bundle;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.CardView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.FrameLayout;

import com.mckuai.imc.Base.BaseFragment;
import com.mckuai.imc.Bean.Cartoon;
import com.mckuai.imc.R;

import java.util.ArrayList;


public class MainFragment_Competition extends BaseFragment {
    private View view;
    private CardView cartoon_top;
    private CardView cartoon_bottom;
    private AppCompatImageView diamond;
    private AppCompatImageView diamond_shandow;
    private AppCompatImageView diamon_top;
    private AppCompatImageView diamon_bottom;

    private ArrayList<Cartoon> cartoons;
    private int margin;
    private int width;


    public MainFragment_Competition() {
        // Required empty public constructor
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        if (null == view) {
            view =  inflater.inflate(R.layout.fragment_main_fragment__competition, container, false);
            initView();
        }
        return view;
    }

    private void initView(){
        cartoon_top = (CardView) view.findViewById(R.id.cartoon_top);
        cartoon_bottom = (CardView) view.findViewById(R.id.cartoon_bottom);
        diamond = (AppCompatImageView) view.findViewById(R.id.diamond_middle);
        diamond_shandow = (AppCompatImageView) view.findViewById(R.id.diamond_middle_shandow);
        diamon_top = (AppCompatImageView) view.findViewById(R.id.diamond_top);
        diamon_bottom = (AppCompatImageView) view.findViewById(R.id.diamond_bottom);

        view.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                width = view.getHeight() / 2;
                margin = (view.getWidth() - width) / 2;
                view.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                reSizeView();
            }
        });
    }

    private void reSizeView(){
        calculationLayoutParams(cartoon_top);
        calculationLayoutParams(cartoon_bottom);
    }

    private void calculationLayoutParams(View view){
        if (view instanceof CardView) {
            FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) view.getLayoutParams();
            params.width = width;
            params.height = width;
            params.setMargins(margin, 0, margin, 0);
            view.setLayoutParams(params);
        }
    }

    private void loadData(){

    }

    private void showData(){

    }

}
