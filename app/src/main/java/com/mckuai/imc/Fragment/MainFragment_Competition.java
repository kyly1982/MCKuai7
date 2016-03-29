package com.mckuai.imc.Fragment;

import android.os.Bundle;
import android.support.v7.widget.AppCompatImageButton;
import android.support.v7.widget.AppCompatImageView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.mckuai.imc.Base.BaseFragment;
import com.mckuai.imc.Bean.Cartoon;
import com.mckuai.imc.R;

import java.util.ArrayList;


public class MainFragment_Competition extends BaseFragment implements View.OnClickListener {
    private View view;
    private AppCompatImageView cartoon_top,cartoon_bottom;
    private AppCompatImageView diamond,diamond_shandow;
    private AppCompatImageView diamon_top,diamon_bottom;
    private LinearLayout root_top,root_bottom;
    private AppCompatImageButton vote_top,vote_bottom;

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
            view = inflater.inflate(R.layout.fragment_main_fragment__competition, container, false);
            initView();
        }
        return view;
    }

    private void initView(){
        cartoon_top = (AppCompatImageView) view.findViewById(R.id.cartoon_top);
        cartoon_bottom = (AppCompatImageView) view.findViewById(R.id.cartoon_bottom);
        diamond = (AppCompatImageView) view.findViewById(R.id.diamond_middle);
        diamond_shandow = (AppCompatImageView) view.findViewById(R.id.diamond_middle_background);
        diamon_top = (AppCompatImageView) view.findViewById(R.id.diamond_top);
        diamon_bottom = (AppCompatImageView) view.findViewById(R.id.diamond_bottom);
        root_top = (LinearLayout) view.findViewById(R.id.root_top);
        root_bottom = (LinearLayout) view.findViewById(R.id.root_bottom);
        vote_top = (AppCompatImageButton) view.findViewById(R.id.vote_top);
        vote_bottom = (AppCompatImageButton) view.findViewById(R.id.vote_bottom);

        vote_top.setOnClickListener(this);
        vote_bottom.setOnClickListener(this);


        //view.setView(diamond,diamond_shandow,cartoon_top,cartoon_bottom);

    }


    private void loadData(){

    }

    private void showData(){

    }

    @Override
    public void onClick(View v) {

    }
}
