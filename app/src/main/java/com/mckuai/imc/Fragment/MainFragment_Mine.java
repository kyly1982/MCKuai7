package com.mckuai.imc.Fragment;


import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.malinskiy.superrecyclerview.SuperRecyclerView;
import com.mckuai.imc.Adapter.MessageAdapter;
import com.mckuai.imc.Base.BaseActivity;
import com.mckuai.imc.Base.BaseFragment;
import com.mckuai.imc.Base.MCKuai;
import com.mckuai.imc.Bean.Cartoon;
import com.mckuai.imc.R;
import com.mckuai.imc.Util.MCNetEngine;

import java.util.ArrayList;

public class MainFragment_Mine extends BaseFragment implements MessageAdapter.OnItemClickListener, MCNetEngine.OnLoadMessageResponseListener {

    public MainFragment_Mine() {
        mTitleResId = R.string.fragment_mine;
    }

    private MessageAdapter adapter;
    private SuperRecyclerView list;
    private View view;
    private ArrayList<Cartoon> messages;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
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

    private void initView() {
        list = (SuperRecyclerView) view.findViewById(R.id.messagelist);
        LinearLayoutManager manager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        list.setLayoutManager(manager);
        adapter = new MessageAdapter(getActivity(), this);
        list.setAdapter(adapter);
    }

    private void loadData() {
        MCKuai.instence.netEngine.loadMessage(getActivity(), null, this);
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
        ((BaseActivity) getActivity()).showWarning("添加好友", null, null);
    }

    @Override
    public void OnItemClicked(Cartoon cartoon) {
        ((BaseActivity) getActivity()).showWarning(cartoon.getContent(), null, null);
    }

    @Override
    public void OnChatClick(Cartoon cartoon) {
        ((BaseActivity) getActivity()).showWarning("聊天", null, null);
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
}
