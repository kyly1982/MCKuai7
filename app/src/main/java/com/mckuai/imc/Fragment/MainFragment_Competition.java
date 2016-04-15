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

import io.rong.imkit.RongIM;
import io.rong.imlib.RongIMClient;
import io.rong.imlib.model.Conversation;
import io.rong.imlib.model.Message;
import io.rong.message.TextMessage;


public class MainFragment_Competition extends BaseFragment implements CompetitionLayout.OnActionListener,MCNetEngine.OnLoadCartoonListResponseListener,MCNetEngine.OnRewardCartoonResponseListener {
    private View view;
    private CompetitionLayout competitionLayout;
    private ArrayList<Cartoon> cartoons;
    private Page page;
    private Cartoon voteCartoon;
    private Cartoon failCartoon;

    private MCKuai application;


    public MainFragment_Competition() {
        application = MCKuai.instence;
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
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
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (!hidden){
            showData();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 2 && resultCode == Activity.RESULT_OK){
            onVote(voteCartoon,failCartoon);
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

    private void showData(){
        if (null == page){
            loadData();
        } else {
            if (null != cartoons) {
                competitionLayout.setData(cartoons);
            }
        }
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
    public void onVote(Cartoon win,Cartoon fail) {
        MobclickAgent.onEvent(getActivity(),"competitionCartoon");
        if (null != win && null != fail) {
            if (application.isLogin()) {
                MCKuai.instence.netEngine.rewardCartoon(getActivity(),application.user.getId(), win, this);
                if (null != RongIM.getInstance() && null != RongIM.getInstance().getRongIMClient()) {
                    TextMessage message = TextMessage.obtain("恭喜你，你的漫画在与 \""+fail.getOwner().getNickEx()+"\" 的漫画的PK中取得了胜利。");
                    RongIM.getInstance().getRongIMClient().sendMessage(Conversation.ConversationType.PRIVATE, win.getOwner().getName(), message, application.user.getNike() + "你的漫画在PK中取得了胜利！", "", new RongIMClient.SendMessageCallback() {
                        @Override
                        public void onError(Integer integer, RongIMClient.ErrorCode errorCode) {

                        }

                        @Override
                        public void onSuccess(Integer integer) {

                        }
                    }, new RongIMClient.ResultCallback<Message>() {
                        @Override
                        public void onSuccess(Message message) {

                        }

                        @Override
                        public void onError(RongIMClient.ErrorCode errorCode) {

                        }
                    });
                }
            } else {
                voteCartoon = win;
                failCartoon = fail;
                ((MainActivity)getActivity()).callLogin(2);
            }
        }
    }

    @Override
    public void onShowUser(User user) {
        Intent intent = new Intent(getActivity(), UserCenterActivity.class);
        intent.putExtra(getString(R.string.usercenter_tag_userid),user.getId().intValue());
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
    public void onLoadCartoonListSuccess(ArrayList<Cartoon> cartoons) {
        page.setPage(page.getPage()+1);
        competitionLayout.setData(cartoons);
    }

    @Override
    public void onRewardaCartoonFailure(String msg) {
        if (msg.equals("已赞")){
            MobclickAgent.onEvent(getActivity(),"competitionCartoon_FR");
            return;
        }
        showMessage(msg,null,null);
        MobclickAgent.onEvent(getActivity(), "competitionCartoon_F");
    }

    @Override
    public void onRewardCartoonSuccess() {
        MobclickAgent.onEvent(getActivity(),"competitionCartoon_S");
    }
}
