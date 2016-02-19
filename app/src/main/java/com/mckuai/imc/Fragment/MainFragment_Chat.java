package com.mckuai.imc.Fragment;


import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.malinskiy.superrecyclerview.SuperRecyclerView;
import com.mckuai.imc.Adapter.ConversationAdapter;
import com.mckuai.imc.Base.BaseFragment;
import com.mckuai.imc.Base.MCKuai;
import com.mckuai.imc.R;

import java.util.ArrayList;

import io.rong.imkit.RongIM;
import io.rong.imlib.model.Conversation;

public class MainFragment_Chat extends BaseFragment implements ConversationAdapter.OnItemClickListener {
    private ArrayList<Conversation> conversations;
    private View view;
    private SuperRecyclerView conversationList;
    private SuperRecyclerView userList;
    private ConversationAdapter adapter;

    public MainFragment_Chat() {
        mTitleResId = R.string.fragment_chat;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        if (null != view) {
            container.removeView(view);
        }
        view = inflater.inflate(R.layout.fragment_main_chat, container, false);
        return view;
    }


    @Override
    public void onResume() {
        super.onResume();
        if (null != view && null == conversationList) {
            initView();
        }
        showData();
    }

    private void initView() {
        conversationList = (SuperRecyclerView) view.findViewById(R.id.conversationlist);
        userList = (SuperRecyclerView) view.findViewById(R.id.waituserlist);
        RecyclerView.LayoutManager manager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        conversationList.setLayoutManager(manager);

        conversationList.hideProgress();
        conversationList.hideMoreProgress();
        userList.hideMoreProgress();
        userList.hideProgress();
    }

    private void showData() {

        if (MCKuai.instence.isLogin() && null != RongIM.getInstance() && null != RongIM.getInstance().getRongIMClient()) {
            conversations = (ArrayList<Conversation>) RongIM.getInstance().getRongIMClient().getConversationList();
            if (null == adapter) {
                adapter = new ConversationAdapter(getActivity(), this);
                conversationList.setAdapter(adapter);
            }
            adapter.setData(conversations);
        }
    }

    @Override
    public void onItemClicked(Conversation conversation) {

    }
}
