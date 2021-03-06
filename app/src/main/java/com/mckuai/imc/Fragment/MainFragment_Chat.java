package com.mckuai.imc.Fragment;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.malinskiy.superrecyclerview.SuperRecyclerView;
import com.mckuai.imc.Activity.LoginActivity;
import com.mckuai.imc.Adapter.ConversationAdapter;
import com.mckuai.imc.Adapter.WaitUserAdapter;
import com.mckuai.imc.Base.BaseFragment;
import com.mckuai.imc.Base.MCKuai;
import com.mckuai.imc.Bean.Conversation;
import com.mckuai.imc.Bean.User;
import com.mckuai.imc.R;
import com.mckuai.imc.Util.MCNetEngine;

import java.util.ArrayList;

import io.rong.imkit.RongIM;
import io.rong.imlib.RongIMClient;

public class MainFragment_Chat extends BaseFragment implements ConversationAdapter.OnItemClickListener, MCNetEngine.OnLoadRecommendUserListener, WaitUserAdapter.OnItemClickListener{
    private ArrayList<Conversation> conversations;
    // private ArrayList<User> users;
    private ArrayList<User> waitUsers;
    private View view;
    private SuperRecyclerView conversationList;
    private SuperRecyclerView userList;
    private AppCompatTextView unloginHint;
    private ConversationAdapter adapter;
    private WaitUserAdapter waitUserAdapter;
    private MCKuai application;
    private User user;
    //private boolean isRegReciver = false;

    public MainFragment_Chat() {
        mTitleResId = R.string.fragment_chat;
        application = MCKuai.instence;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        application = MCKuai.instence;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (null == view) {
            view = inflater.inflate(R.layout.fragment_main_chat, container, false);
        }

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
       /* if (!isRegReciver && application.isIMLogined){
            RongIM.setOnReceiveMessageListener(this);
            isRegReciver = true;
        }*/

        if (application.isLogin()){
            showData();
        }
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (null != view && null == conversationList) {
            initView();
        }
        if (!hidden) {

            showData();
            showUser();
        }
    }

    private void initView() {
        conversationList = (SuperRecyclerView) view.findViewById(R.id.conversationlist);
        userList = (SuperRecyclerView) view.findViewById(R.id.waituserlist);
        conversationList.getRecyclerView().setHasFixedSize(true);
        userList.getRecyclerView().setHasFixedSize(true);
        RecyclerView.LayoutManager manager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        conversationList.setLayoutManager(manager);
        conversationList.hideProgress();
        conversationList.hideMoreProgress();
        RecyclerView.LayoutManager manager1 = new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false);
        userList.setLayoutManager(manager1);
        conversationList.setRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getConversation();
            }
        });

        userList.hideMoreProgress();
        userList.hideProgress();
    }

    private void showData() {

        if (MCKuai.instence.isLogin()) {
            unloginHint.setVisibility(View.GONE);
            conversationList.setVisibility(View.VISIBLE);
            getConversation();
            if (null == adapter) {
                adapter = new ConversationAdapter(getActivity(), this);
            } else {
                adapter.setData(conversations);
                conversationList.setAdapter(adapter);
                conversationList.hideMoreProgress();
                conversationList.hideProgress();
            }
        } else {
            unloginHint.setVisibility(View.VISIBLE);
            conversationList.setVisibility(View.GONE);
            callLogin(0);
        }

    }

    private void showUser() {
        //显示等待用户
        if (null == waitUserAdapter) {
            waitUserAdapter = new WaitUserAdapter(getActivity(), this);
            application.netEngine.loadRecommendUser(getActivity(), application.isLogin() ? application.user.getId() : null, this);
        } else {
            userList.setAdapter(waitUserAdapter);
            waitUserAdapter.setData(waitUsers);
        }

    }

    private void getConversation() {
        if (null != RongIM.getInstance() && null != RongIM.getInstance().getRongIMClient()) {
            if (RongIM.getInstance().getRongIMClient().getCurrentConnectionStatus() == RongIMClient.ConnectionStatusListener.ConnectionStatus.CONNECTED) {
                ArrayList<io.rong.imlib.model.Conversation> list = (ArrayList<io.rong.imlib.model.Conversation>) RongIM.getInstance().getRongIMClient().getConversationList();

                if (null != list) {
                    conversations = new ArrayList<>(list.size());
                    for (io.rong.imlib.model.Conversation imConversation : list) {
                        Conversation conversation = new Conversation();
                        conversation.setConversation(imConversation);
                        //聊天对象信息
                        String id = imConversation.getTargetId();
                        User tempUser = application.daoHelper.getUserByName(id);
                        if (null == tempUser) {
                            tempUser = new User();
                            tempUser.setName(id);
                            conversation.setTarget(tempUser);
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
                        } else {
                            conversation.setTarget(tempUser);
                        }
                        if (conversation.getConversation().getUnreadMessageCount() > 0){
                            conversations.add(0,conversation);
                        } else {
                            conversations.add(0 == conversations.size() ? 0:conversations.size() - 1,conversation);
                        }
                    }
                    if (null != conversationList) {
                        conversationList.hideProgress();
                    }
                } else {
                    conversations = new ArrayList<Conversation>(0);
                }
            } else {
                //聊天服务器未连接上
                //showMessage("正在连接聊天服务器,请稍后!", null, null);
                RongIM.connect(MCKuai.instence.user.getToken(), new RongIMClient.ConnectCallback() {
                    @Override
                    public void onTokenIncorrect() {
                        showMessage("用户令牌失效，需要重新登录，是否登录？", "登录", new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                callLogin(2);
                                return;
                            }
                        });
                    }

                    @Override
                    public void onSuccess(String s) {
                        getConversation();
                    }

                    @Override
                    public void onError(RongIMClient.ErrorCode errorCode) {
                        showMessage("连接聊天服务器失败，原因：" + errorCode.getMessage(), null, null);
                        return;
                    }
                });
            }
        } else {
            showMessage("聊天服务故障，请重新启动软件！", null, null);
            unloginHint.setVisibility(View.VISIBLE);
            conversationList.setVisibility(View.GONE);
        }
    }

    private void callLogin(int requestcode) {
        Intent intent = new Intent(getActivity(), LoginActivity.class);
        getActivity().startActivityForResult(intent, requestcode);
    }

    @Override
    public void onItemClicked(Conversation conversation) {
        String id = conversation.getConversation().getTargetId();
        RongIM.getInstance().startPrivateChat(getActivity(), id, id);
    }

    @Override
    public void onItemClicked(User user) {
        if (application.isLogin()) {
            String id = user.getName();
            RongIM.getInstance().startPrivateChat(getActivity(), id, user.getNickEx());
        } else {
            this.user = user;
            callLogin(1);
        }

    }

    private void updateUserInfo(User user) {
        for (int i = 0; i < conversations.size(); i++) {
            User tempUser = conversations.get(i).getTarget();
            if (tempUser.getId() == user.getId()) {
                tempUser.update(user);
                if (null != adapter) {
                    adapter.notifyItemChanged(i);
                }
            }
        }
    }

    @Override
    public void onLoadUserFailure(String msg) {
        showMessage("获取用户列表失败，原因：" + msg, null, null);
    }

    @Override
    public void onLoadUserSuccess(ArrayList<User> users) {
        this.waitUsers = users;
        showUser();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            switch (requestCode) {
                case 0:
                    showData();
                    showUser();
                    break;
                case 1:
                    onItemClicked(user);
                    break;
                case 2:
                    getConversation();
                    break;
            }
        }
    }

    public void onNewMsgRecived(){
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                showData();
            }
        });
    }


}
