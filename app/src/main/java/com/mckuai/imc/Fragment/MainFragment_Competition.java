package com.mckuai.imc.Fragment;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.mckuai.imc.Activity.CartoonActivity;
import com.mckuai.imc.Activity.MainActivity;
import com.mckuai.imc.Activity.UserCenterActivity;
import com.mckuai.imc.Base.BaseFragment;
import com.mckuai.imc.Base.MCKuai;
import com.mckuai.imc.Bean.Cartoon;
import com.mckuai.imc.Bean.Page;
import com.mckuai.imc.Bean.User;
import com.mckuai.imc.R;
import com.mckuai.imc.Util.MCNetEngine;
import com.mckuai.imc.Widget.CompetitionLayout;
import com.umeng.analytics.MobclickAgent;

import java.util.ArrayList;


public class MainFragment_Competition extends BaseFragment implements CompetitionLayout.OnActionListener,MCNetEngine.OnLoadCartoonListResponseListener,MCNetEngine.OnRewardCartoonResponseListener {
    private View view;
    private CompetitionLayout competitionLayout;
    private Page page;
    private Cartoon voteCartoon;

    private MCKuai application;


    public MainFragment_Competition() {
        // Required empty public constructor
        application = MCKuai.instence;
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        if (null == view) {
            view = inflater.inflate(R.layout.fragment_main_fragment_competition, container, false);
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

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 2 && resultCode == Activity.RESULT_OK){
            onVote(voteCartoon);
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
        application.netEngine.loadCartoonList(getActivity(), page, this);
    }


    @Override
    public void EOF() {
        loadData();
    }

    @Override
    public void onShowDetile(Cartoon cartoon) {
        Intent intent = new Intent(getActivity(), CartoonActivity.class);
        Bundle bundle = new Bundle();
        bundle.putSerializable(getString(R.string.cartoondetail_tag_cartoon),cartoon);
        intent.putExtras(bundle);
        getActivity().startActivity(intent);
    }

    @Override
    public void onVote(Cartoon cartoon) {
        MobclickAgent.onEvent(getActivity(),"competitionCartoon");
        if (null != cartoon) {
            if (application.isLogin()) {
                MCKuai.instence.netEngine.rewardCartoon(getActivity(),application.user.getId(), cartoon, this);
            } else {
                voteCartoon = cartoon;
                ((MainActivity)getActivity()).callLogin(2);
            }
        }
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
        showMessage(msg, "重试", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadData();
            }
        });
    }

    @Override
    public void onLoadCartoonListSuccess(ArrayList<Cartoon> cartoons, Page page) {
        this.page = page;
        competitionLayout.setData(cartoons);
    }

    @Override
    public void onRewardaCartoonFailure(String msg) {
        showMessage(msg,null,null);
        MobclickAgent.onEvent(getActivity(), "competitionCartoon_F");
    }

    @Override
    public void onRewardCartoonSuccess() {
        MobclickAgent.onEvent(getActivity(),"competitionCartoon_S");
    }
}
