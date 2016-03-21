package com.mckuai.imc.Fragment;

import android.os.Bundle;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.CardView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.mckuai.imc.Base.BaseFragment;
import com.mckuai.imc.Bean.Cartoon;
import com.mckuai.imc.R;
import com.mckuai.imc.Widget.CompetitionLayout;

import java.util.ArrayList;


public class MainFragment_Competition extends BaseFragment {
    private CompetitionLayout view;
    private CardView cartoon_top;
    private CardView cartoon_bottom;
    private AppCompatImageView diamond;
    private AppCompatImageView diamond_shandow;
    private AppCompatImageView diamon_top;
    private AppCompatImageView diamon_bottom;

    private ArrayList<Cartoon> cartoons;
    private int margin_H,margin_V;
    private int width;


    public MainFragment_Competition() {
        // Required empty public constructor
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        if (null == view) {
            view = (CompetitionLayout) inflater.inflate(R.layout.fragment_main_fragment__competition, container, false);
            initView();
        }
        return view;
    }

    private void initView(){
        cartoon_top = (CardView) view.findViewById(R.id.cartoon_top);
        cartoon_bottom = (CardView) view.findViewById(R.id.cartoon_bottom);
        diamond = (AppCompatImageView) view.findViewById(R.id.diamond_middle);
        diamond_shandow = (AppCompatImageView) view.findViewById(R.id.diamond_middle_background);
        diamon_top = (AppCompatImageView) view.findViewById(R.id.diamond_top);
        diamon_bottom = (AppCompatImageView) view.findViewById(R.id.diamond_bottom);
        view.setView(diamond,diamond_shandow,cartoon_top,cartoon_bottom);

      /*  view.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                if (view.getHeight() > view.getWidth() * 2){
                    width = view.getWidth();
                    margin_H = (view.getHeight() - 2* view.getWidth()) / 2;
                    margin_V = 0;
                } else {
                    width = view.getHeight() / 2;
                    margin_H = 0;
                    margin_V = (view.getWidth() - width) / 2;
                }
                view.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                reSizeView();
            }
        });*/
    }

    private void reSizeView(){
        calculationLayoutParams(cartoon_top,0);
        calculationLayoutParams(cartoon_bottom,1);
    }

    private void calculationLayoutParams(View view,int i){
        if (view instanceof CardView) {
            FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) view.getLayoutParams();
            params.width = width;
            params.height = width;
            switch (i){
                case 0:
                    params.setMargins(margin_H, margin_V, margin_H, margin_V + width);
                    break;
                case 1:
                    params.setMargins(margin_H, width + margin_V, margin_H, 0);
                    break;
            }
            view.setLayoutParams(params);
        }
    }

    private void loadData(){

    }

    private void showData(){

    }

}
