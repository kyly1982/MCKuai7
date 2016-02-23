package com.mckuai.imc.Fragment;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.malinskiy.superrecyclerview.SuperRecyclerView;
import com.mckuai.imc.Activity.LoginActivity;
import com.mckuai.imc.Adapter.ConversationAdapter;
import com.mckuai.imc.Base.BaseFragment;
import com.mckuai.imc.Base.MCKuai;
import com.mckuai.imc.Bean.User;
import com.mckuai.imc.R;
import com.mckuai.imc.Util.MCNetEngine;

import java.util.ArrayList;

import io.rong.imkit.RongIM;
import io.rong.imlib.RongIMClient;
import io.rong.imlib.model.Conversation;

public class MainFragment_Chat extends BaseFragment implements ConversationAdapter.OnItemClickListener {
    private ArrayList<Conversation> conversations;
    private ArrayList<User> users;
    private ArrayList<String> lastMessages;
    private ArrayList<User> waitUsers;
    private View view;
    private SuperRecyclerView conversationList;
    private SuperRecyclerView userList;
    private AppCompatTextView unloginHint;
    private ConversationAdapter adapter;
    private MCKuai application;

    public MainFragment_Chat() {
        mTitleResId = R.string.fragment_chat;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        application = MCKuai.instence;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        if (null != view) {
            container.removeView(view);
        }
        view = inflater.inflate(R.layout.fragment_main_chat, container, false);
        if (null == unloginHint) {
            unloginHint = (AppCompatTextView) view.findViewById(R.id.unlogin);
        }
        return view;
    }


    @Override
    public void onResume() {
        super.onResume();
        if (null != view && null == conversationList) {
            initView();
        }
        //showData();
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (!hidden) {
            showData();
        }
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

        if (MCKuai.instence.isLogin()) {
            unloginHint.setVisibility(View.GONE);
            conversationList.setVisibility(View.VISIBLE);
            if (null != RongIM.getInstance() && null != RongIM.getInstance().getRongIMClient()) {
                if (RongIM.getInstance().getRongIMClient().getCurrentConnectionStatus() == RongIMClient.ConnectionStatusListener.ConnectionStatus.CONNECTED) {
                    conversations = (ArrayList<Conversation>) RongIM.getInstance().getRongIMClient().getConversationList();
                    if (null == adapter) {
                        adapter = new ConversationAdapter(getActivity(), this);
                        conversationList.setAdapter(adapter);
                    }
                    if (null == users) {
                        users = new ArrayList<>(10);
                    }
                    if (null == lastMessages) {
                        lastMessages = new ArrayList<>(10);
                    }
                    if (null != conversations) {
                        for (Conversation conversation : conversations) {
                            //聊天对象信息
                            String id = conversation.getTargetId();
                            User tempUser = application.daoHelper.getUserByName(id);
                            if (null == tempUser) {
                                tempUser = new User();
                                tempUser.setName(id);
                                application.netEngine.loadUserInfo(getActivity(), id, new MCNetEngine.OnLoadUserInfoResponseListener() {
                                    @Override
                                    public void onLoadUserInfoSuccess(User user) {
                                        if (null != user) {
                                            updateUserInfo(user);
                                        }
                                    }

                                    @Override
                                    public void onLoadUserInfoFailure(String msg) {

                                    }
                                });
                            }
                            users.add(tempUser);

                        }
                    }
                    adapter.setData(conversations, users);
                    return;
                } else {
                    //聊天服务器未连接上
                    showMessage("正在连接聊天服务器,请稍后!", null, null);
                    RongIM.connect(MCKuai.instence.user.getToken(), new RongIMClient.ConnectCallback() {
                        @Override
                        public void onTokenIncorrect() {
                            showMessage("用户令牌失效，需要重新登录，是否登录？", "登录", new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    callLogin();
                                }
                            });
                        }

                        @Override
                        public void onSuccess(String s) {
                            showData();
                        }

                        @Override
                        public void onError(RongIMClient.ErrorCode errorCode) {
                            showMessage("连接聊天服务器失败，原因：" + errorCode.getMessage(), null, null);
                        }
                    });
                    return;
                }
            } else {
                showMessage("", null, null);
            }
        } else {
            unloginHint.setVisibility(View.VISIBLE);
            conversationList.setVisibility(View.GONE);
        }
        callLogin();
    }

    private void callLogin() {
        Intent intent = new Intent(getActivity(), LoginActivity.class);
        getActivity().startActivityForResult(intent, 0);
    }

    @Override
    public void onItemClicked(Conversation conversation) {
        String id = conversation.getTargetId();
        RongIM.getInstance().startPrivateChat(getActivity(), id, id);
    }

    private void updateUserInfo(User user) {
        for (int i = 0; i < users.size(); i++) {
            User tempUser = users.get(i);
            if (tempUser.getId() == user.getId()) {
                tempUser.update(user);
                if (null != adapter) {
                    adapter.notifyItemChanged(i);
                }
            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            showData();
        }
    }
}
