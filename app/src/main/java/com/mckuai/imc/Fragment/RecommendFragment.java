package com.mckuai.imc.Fragment;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.malinskiy.superrecyclerview.SuperRecyclerView;
import com.mckuai.imc.Activity.CartoonActivity;
import com.mckuai.imc.Adapter.MessageAdapter;
import com.mckuai.imc.Base.BaseActivity;
import com.mckuai.imc.Base.BaseFragment;
import com.mckuai.imc.Base.MCKuai;
import com.mckuai.imc.Bean.Cartoon;
import com.mckuai.imc.R;
import com.mckuai.imc.Util.MCNetEngine;

import java.util.ArrayList;

import io.rong.imkit.RongIM;
import io.rong.imlib.RongIMClient;

public class RecommendFragment extends BaseFragment implements MessageAdapter.OnItemClickListener, MCNetEngine.OnLoadMessageResponseListener, MCNetEngine.OnAddFriendResponseListener {
    public RecommendFragment() {
        mTitleResId = R.string.fragment_mine;
        application = MCKuai.instence;
    }

    private MessageAdapter adapter;
    private SuperRecyclerView list;
    private View view;
    private ArrayList<Cartoon> messages;
    private Cartoon cartoon;
    private MCKuai application;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (null == view) {
            view = inflater.inflate(R.layout.fragment_main_mine, container, false);
        }
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (null == list) {
            initView();
        }
        showData();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            switch (requestCode) {
                case 1:
                    MCKuai.instence.netEngine.addFriend(getActivity(), cartoon.getOwner().getId().intValue(), this);
                    break;
                case 2:
                    RongIM.getInstance().startPrivateChat(getActivity(), cartoon.getOwner().getName(), cartoon.getOwner().getNickEx());
                    break;
            }
        }
    }

    private void initView() {
        list = (SuperRecyclerView) view.findViewById(R.id.messagelist);
        LinearLayoutManager manager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        list.setLayoutManager(manager);
        adapter = new MessageAdapter(getActivity(), this);
        list.setAdapter(adapter);
    }

    private void loadData() {
        if (application.isLogin()) {
            application.netEngine.loadRecommend(getActivity(), application.user.getId(), messages, this);
        } else {
            application.netEngine.loadRecommend(getActivity(), null, messages, this);
        }
    }

    private void showData() {
        if (null == messages) {
            loadData();
        } else {
            adapter.setData(messages);
        }
    }

    @Override
    public void OnAddFriendClick(Cartoon cartoon) {
        if (MCKuai.instence.isLogin()) {
            MCKuai.instence.netEngine.addFriend(getActivity(), cartoon.getOwner().getId().intValue(), this);
        } else {
            this.cartoon = cartoon;
            ((BaseActivity) getActivity()).callLogin(1);
        }
    }

    @Override
    public void OnItemClicked(Cartoon cartoon) {
        if (null != cartoon) {
            Intent intent = new Intent(getActivity(), CartoonActivity.class);
            Bundle bundle = new Bundle();
            bundle.putSerializable(getString(R.string.cartoondetail_tag_cartoon), cartoon);
            intent.putExtras(bundle);
            getActivity().startActivity(intent);
        }
    }

    @Override
    public void OnChatClick(Cartoon cartoon) {
        if (MCKuai.instence.isLogin()) {
            if (null != RongIM.getInstance() && null != RongIM.getInstance().getRongIMClient() && RongIM.getInstance().getRongIMClient().getCurrentConnectionStatus() == RongIMClient.ConnectionStatusListener.ConnectionStatus.CONNECTED) {
                RongIM.getInstance().startPrivateChat(getActivity(), cartoon.getOwner().getName(), cartoon.getOwner().getNickEx());
            } else {
                this.cartoon = cartoon;
                ((BaseActivity) getActivity()).callLogin(2);
            }
        } else {
            this.cartoon = cartoon;
            ((BaseActivity) getActivity()).callLogin(2);
        }
    }

    @Override
    public void onLoadMessageFailure(String msg) {
        ((BaseActivity) getActivity()).showWarning(msg, null, null);
    }

    @Override
    public void onLoadMessageSuccess(ArrayList<Cartoon> messages) {
        this.messages = messages;
        showData();
    }

    @Override
    public void onAddFriendFailure(String msg) {
        ((BaseActivity) getActivity()).showMessage(msg, null, null);
    }

    @Override
    public void onAddFriendSuccess() {
        ((BaseActivity) getActivity()).showMessage("添加好友成功", null, null);
    }
}
