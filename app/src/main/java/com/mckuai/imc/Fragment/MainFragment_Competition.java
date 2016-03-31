package com.mckuai.imc.Fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.mckuai.imc.Activity.UserCenterActivity;
import com.mckuai.imc.Base.BaseFragment;
import com.mckuai.imc.Base.MCKuai;
import com.mckuai.imc.Bean.Cartoon;
import com.mckuai.imc.Bean.Page;
import com.mckuai.imc.Bean.User;
import com.mckuai.imc.R;
import com.mckuai.imc.Util.MCNetEngine;
import com.mckuai.imc.Widget.CompetitionLayout;

import java.util.ArrayList;


public class MainFragment_Competition extends BaseFragment implements CompetitionLayout.OnActionListener,MCNetEngine.OnLoadCartoonListResponseListener {
    private View view;
    private CompetitionLayout competitionLayout;

    private Page page;


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

    @Override
    public void onResume() {
        super.onResume();
        if (null == page){
            loadData();
        }
    }

    private void initView(){
        competitionLayout = (CompetitionLayout) view.findViewById(R.id.competition);
        competitionLayout.setActionListener(this);
    }


    private void loadData(){
        if (null == page){
            page= new Page();
        }
        MCKuai.instence.netEngine.loadCartoonList(getActivity(),"",page,this);
    }


    @Override
    public void EOF() {
        loadData();
    }

    @Override
    public void onShowDetile(Cartoon cartoon) {

    }

    @Override
    public void onVote(Cartoon cartoon) {
        showMessage("投票"+cartoon.getContent(),null,null);
    }

    @Override
    public void onShowUser(User user) {
        Intent intent = new Intent(getActivity(), UserCenterActivity.class);
        Bundle bundle = new Bundle();
        bundle.putSerializable(getString(R.string.cartoondetail_tag_cartoon),user);
        intent.putExtras(bundle);
        getActivity().startActivity(intent);
    }

    @Override
    public void onLoadCartoonListFailure(String msg) {

    }

    @Override
    public void onLoadCartoonListSuccess(ArrayList<Cartoon> cartoons, Page page) {
        this.page = page;
        competitionLayout.setData(cartoons);
    }
}
