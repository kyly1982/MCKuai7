package com.mckuai.imc.Fragment;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.AppCompatRadioButton;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;

import com.malinskiy.superrecyclerview.OnMoreListener;
import com.malinskiy.superrecyclerview.SuperRecyclerView;
import com.mckuai.imc.Activity.CartoonActivity;
import com.mckuai.imc.Activity.LoginActivity;
import com.mckuai.imc.Activity.PostActivity;
import com.mckuai.imc.Activity.UserCenterActivity;
import com.mckuai.imc.Adapter.CartoonDynamicAdapter;
import com.mckuai.imc.Adapter.CartoonMessageAdapter;
import com.mckuai.imc.Adapter.CartoonWorkAdapter;
import com.mckuai.imc.Adapter.CommunityDynamicAdapter;
import com.mckuai.imc.Adapter.CommunityMessageAdapter;
import com.mckuai.imc.Adapter.FriendAdapter;
import com.mckuai.imc.Adapter.PostAdapter;
import com.mckuai.imc.Base.BaseFragment;
import com.mckuai.imc.Base.MCKuai;
import com.mckuai.imc.Bean.Cartoon;
import com.mckuai.imc.Bean.CartoonMessage;
import com.mckuai.imc.Bean.CommunityDynamic;
import com.mckuai.imc.Bean.CommunityMessage;
import com.mckuai.imc.Bean.MCUser;
import com.mckuai.imc.Bean.Page;
import com.mckuai.imc.Bean.Post;
import com.mckuai.imc.Bean.User;
import com.mckuai.imc.R;
import com.mckuai.imc.Util.MCNetEngine;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.ArrayList;

import io.rong.imkit.RongIM;
import io.rong.imlib.RongIMClient;

public class MainFragment_Mine extends BaseFragment
        implements View.OnClickListener, RadioGroup.OnCheckedChangeListener,
        MCNetEngine.OnLoadCartoonDynamicResponseListener,
        MCNetEngine.OnLoadCartoonMessageResponseListener,
        MCNetEngine.OnLoadCartoonWorkResponseListener,
        MCNetEngine.OnLoadCommunityDynamicResponseListener,
        MCNetEngine.OnLoadCommunityMessageResponseListener,
        MCNetEngine.OnloadCommunityWorkResponseListener,
        OnMoreListener,
        SwipeRefreshLayout.OnRefreshListener,
        MCNetEngine.OnloadFriendResponseListener {
    private User user;
    private int contentType = 36;//默认显示动漫消息
    private CartoonDynamicAdapter cartoonDynamicAdapter;
    private CartoonMessageAdapter cartoonMessageAdapter;
    private CartoonWorkAdapter cartoonWorkAdapter;
    private CommunityDynamicAdapter communityDynamicAdapter;
    private CommunityMessageAdapter communityMessageAdapter;
    private PostAdapter communityWorkAdapter;
    private FriendAdapter friendAdapter;
    private Page cartoonDynamicPage;
    private Page cartoonMessagePage;
    private Page cartoonWorkPage;
    private Page communityDynamicPage;
    private Page communityMessagePage;
    private Page communityWorkPage;
    private Page friendPage;

    private ImageLoader loader;
    private MCKuai mApplication;


    private View view;
    private AppCompatImageView userCover;
    private AppCompatTextView userLevel;
    //    private AppCompatTextView userName;
    private RadioGroup group;
    private RadioGroup type;
    private SuperRecyclerView list;
    private SuperRecyclerView work;//作品布局不一样
    private AppCompatRadioButton cartoon;
    private AppCompatRadioButton message;
    private AppCompatRadioButton dynamic;
    private AppCompatRadioButton friend;
    private RelativeLayout unLoginView;

    public MainFragment_Mine() {
        mApplication = MCKuai.instence;
        loader = ImageLoader.getInstance();
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (null == view) {
//            container.removeView(view);
            view = inflater.inflate(R.layout.fragment_mine, container, false);
        }

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (null == list) {
            initView();
        }
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (null != view) {
            if (!hidden) {
                view.setVisibility(View.VISIBLE);
                showData();
            }
        }
    }

    private void initView() {
        userCover = (AppCompatImageView) view.findViewById(R.id.usercover);
        userLevel = (AppCompatTextView) view.findViewById(R.id.userlevel);
//        userName = (AppCompatTextView) view.findViewById(R.id.actionbar_title);
        group = (RadioGroup) view.findViewById(R.id.group);
        friend = (AppCompatRadioButton) view.findViewById(R.id.friend);
        type = (RadioGroup) view.findViewById(R.id.type);
        cartoon = (AppCompatRadioButton) view.findViewById(R.id.cartoon);
        message = (AppCompatRadioButton) view.findViewById(R.id.message);
        dynamic = (AppCompatRadioButton) view.findViewById(R.id.dynamic);
        list = (SuperRecyclerView) view.findViewById(R.id.list);
        work = (SuperRecyclerView) view.findViewById(R.id.worklist);
        unLoginView = (RelativeLayout) view.findViewById(R.id.empty);

        RecyclerView.LayoutManager linearLayoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        list.setLayoutManager(linearLayoutManager);
        RecyclerView.LayoutManager manager = new GridLayoutManager(getActivity(), 2, LinearLayoutManager.VERTICAL, false);
        work.setLayoutManager(manager);
        cartoon.setChecked(true);
        group.setOnCheckedChangeListener(this);
        type.setOnCheckedChangeListener(this);

        list.setupMoreListener(this, 0);
        list.setRefreshListener(this);
        work.setupMoreListener(this, 0);
        work.setRefreshListener(this);
    }


    private void loadData(boolean isRefresh) {
        switch (contentType) {
            case 36:
                if (null == cartoonMessagePage) {
                    cartoonMessagePage = new Page();
                } else {
                    if (cartoonMessagePage.getPage() == cartoonMessagePage.getNextPage() && !isRefresh) {
                        hideProgress();
                        return;
                    }
                }
                if (isRefresh) {
                    cartoonMessagePage.setPage(0);
                }
                mApplication.netEngine.loadCartoonMessage(getActivity(), user.getId().intValue(), cartoonMessagePage.getNextPage(), this);
                break;
            case 34:
                if (null == cartoonDynamicPage) {
                    cartoonDynamicPage = new Page();
                } else {
                    if (cartoonDynamicPage.getNextPage() == cartoonDynamicPage.getPage() && !isRefresh) {
                        hideProgress();
                        return;
                    }
                }
                if (isRefresh) {
                    cartoonDynamicPage.setPage(0);
                }
                mApplication.netEngine.loadCartoonDyanmic(getActivity(), user.getId().intValue(), cartoonDynamicPage.getNextPage(), this);
                break;
            case 33:
                if (null == cartoonWorkPage) {
                    cartoonWorkPage = new Page();
                } else {
                    if (cartoonWorkPage.getPage() == cartoonWorkPage.getNextPage() && !isRefresh) {
                        hideProgress();
                        return;
                    }
                }
                if (isRefresh) {
                    cartoonWorkPage.setPage(0);
                }
                mApplication.netEngine.loadCartoonWork(getActivity(), user.getId().intValue(), cartoonWorkPage.getNextPage(), this);
                break;
            case 20:
                if (null == communityMessagePage) {
                    communityMessagePage = new Page();
                } else {
                    if (communityMessagePage.getPage() == communityMessagePage.getNextPage() && !isRefresh) {
                        hideProgress();
                        return;
                    }
                }
                if (isRefresh) {
                    communityMessagePage.setPage(0);
                }
                mApplication.netEngine.loadCommunityMessage(getActivity(), user.getId().intValue(), communityMessagePage.getNextPage(), this);
                break;
            case 18:
                if (null == communityDynamicPage) {
                    communityDynamicPage = new Page();
                } else if (communityDynamicPage.getPage() == communityDynamicPage.getNextPage() && !isRefresh) {
                    hideProgress();
                    return;
                }
                if (isRefresh) {
                    communityDynamicPage.setPage(0);
                }
                mApplication.netEngine.loadCommunityDynamic(getActivity(), user.getId().intValue(), communityDynamicPage.getNextPage(), this);
                break;
            case 17:
                if (null == communityWorkPage) {
                    communityWorkPage = new Page();
                } else if (communityWorkPage.getPage() == communityWorkPage.getNextPage() && !isRefresh) {
                    hideProgress();
                    return;
                }
                if (isRefresh) {
                    communityWorkPage.setPage(0);
                }
                mApplication.netEngine.loadCommunityWork(getActivity(), user.getId().intValue(), communityWorkPage.getNextPage(), this);
                break;
            default:
                if (8 == (contentType & 8)) {
                    if (null == friendPage) {
                        friendPage = new Page();
                    } else if (friendPage.getPage() == friendPage.getNextPage() && !isRefresh) {
                        hideProgress();
                        return;
                    }
                    if (isRefresh) {
                        friendPage.setPage(0);
                    }
                    mApplication.netEngine.loadFriendList(getActivity(), friendPage.getNextPage(), this);
                }
                break;
        }
    }

    private void showData() {
        if (mApplication.isLogin()) {
            unLoginView.setVisibility(View.GONE);
            if (null == user) {
                user = new User(mApplication.user);
            }
            switch (contentType) {
                case 36://动漫消息
                    list.setVisibility(View.VISIBLE);
                    work.setVisibility(View.GONE);
                    if (null != cartoonMessageAdapter) {
                        list.setAdapter(cartoonMessageAdapter);
                        cartoonMessageAdapter.notifyDataSetChanged();
                    } else {
                        loadData(false);
                    }
                    break;
                case 34://动漫动态
                    if (null != cartoonDynamicAdapter) {
                        list.setVisibility(View.VISIBLE);
                        work.setVisibility(View.GONE);
                        list.hideMoreProgress();
                        list.hideProgress();
                        list.setAdapter(cartoonDynamicAdapter);
                        cartoonDynamicAdapter.notifyDataSetChanged();
//                    Toast.makeText(this,"11111",Toast.LENGTH_LONG).show();
                    } else {
                        loadData(false);
                    }
                    break;
                case 33://动漫作品
                    list.setVisibility(View.GONE);
                    work.setVisibility(View.VISIBLE);
                    if (null != cartoonWorkAdapter) {
                        work.setAdapter(cartoonWorkAdapter);
                        cartoonWorkAdapter.notifyDataSetChanged();
                    } else {
                        loadData(false);
                    }
                    break;
                case 20://社区消息
                    list.setVisibility(View.VISIBLE);
                    work.setVisibility(View.GONE);
                    if (null != communityMessageAdapter) {
                        list.setAdapter(communityMessageAdapter);
                        communityMessageAdapter.notifyDataSetChanged();
                    } else {
                        loadData(false);
                    }
                    break;
                case 18://社区动态
                    list.setVisibility(View.VISIBLE);
                    work.setVisibility(View.GONE);
                    if (null != communityDynamicAdapter) {
                        list.setAdapter(communityDynamicAdapter);
                        communityDynamicAdapter.notifyDataSetChanged();
                    } else {
                        loadData(false);
                    }
                    break;
                case 17://社区作品
                    list.setVisibility(View.VISIBLE);
                    work.setVisibility(View.GONE);
                    if (null != communityWorkAdapter) {
                        list.setAdapter(communityWorkAdapter);
                        communityWorkAdapter.notifyDataSetChanged();
                    } else {
                        loadData(false);
                    }
                    break;
                default:
                    if (8 == (contentType & 8)) {
                        list.setVisibility(View.VISIBLE);
                        work.setVisibility(View.GONE);
                        if (null != friendAdapter) {
                            list.setAdapter(friendAdapter);
                            friendAdapter.notifyDataSetChanged();
                        } else {
                            loadData(false);
                        }
                    }
                    break;
            }
            showUserInfo();

        } else {
            unLoginView.setVisibility(View.VISIBLE);
            Intent intent = new Intent(getActivity(), LoginActivity.class);
            getActivity().startActivityForResult(intent, 0);
        }
    }

    private void hideProgress() {
        list.hideProgress();
        list.hideMoreProgress();
        work.hideProgress();
        work.hideMoreProgress();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (Activity.RESULT_OK == resultCode && mApplication.isLogin()) {
            showData();
        }
    }


    private void showUserInfo() {
        if (null != user && 0 != user.getId() && null != user.getName() && null != user.getHeadImage()) {
            if (null == userCover.getTag() || !userCover.getTag().equals(user.getHeadImage()))
                loader.displayImage(user.getHeadImage(), userCover, mApplication.getCircleOptions());
            //userName.setText(user.getHeadImage());
            userLevel.setText(getString(R.string.usercenter_userlevel, user.getLevel()));
        }
    }

    private void resetUser(User user) {
        if (user.getId() != this.user.getId()) {
            Intent intent = new Intent(getActivity(), UserCenterActivity.class);
            intent.putExtra(getString(R.string.usercenter_tag_userid), user.getId().intValue());
            getActivity().startActivity(intent);
        }

    }

    private void startChat(MCUser chatUser) {
        final User target;
        if (null == chatUser) {
            target = this.user;
        } else {
            target = new User(chatUser);
        }
        if (RongIM.getInstance().getRongIMClient().getCurrentConnectionStatus() == RongIMClient.ConnectionStatusListener.ConnectionStatus.CONNECTED) {
            RongIM.getInstance().startPrivateChat(getActivity(), target.getName(), target.getNickEx());
        } else {
            mApplication.loginIM(new RongIMClient.ConnectCallback() {
                @Override
                public void onTokenIncorrect() {
                    showMessage("令牌已过期，请重新登录", null, null);
                }

                @Override
                public void onSuccess(String s) {
                    RongIM.getInstance().startPrivateChat(getActivity(), target.getName(), target.getNickEx());
                }

                @Override
                public void onError(RongIMClient.ErrorCode errorCode) {
                    showMessage("登录聊天服务器失败，原因：" + errorCode.getMessage(), null, null);
                }
            });
        }
    }

    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {
        switch (checkedId) {
            case R.id.cartoon:
                type.setVisibility(View.VISIBLE);
                contentType = (contentType & 7) | 32;//100,111
                break;
            case R.id.community:
                type.setVisibility(View.VISIBLE);
                contentType = (contentType & 7) | 16;//010,111
                break;
            case R.id.friend:
                type.setVisibility(View.GONE);//001,111
                contentType = (contentType & 7) | 8;
                break;
            case R.id.message:
                contentType = (contentType & 56) | 4;//111,100
                break;
            case R.id.dynamic:
                contentType = (contentType & 56) | 2;//111,010
                break;
            case R.id.work:
                contentType = (contentType & 56) | 1;//111,001
                break;
        }
        showData();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.addfriend:
                break;
            case R.id.chat:
                break;
        }
    }

    @Override
    public void onLoadCartoonDynamicFailure(String msg) {
        showMessage(msg, null, null);
    }

    @Override
    public void onLoadCartoonDynamicSuccess(MCUser user, final ArrayList<CartoonMessage> dynamics, Page page) {
        cartoonDynamicPage = page;
        if (null == cartoonDynamicAdapter) {
            cartoonDynamicAdapter = new CartoonDynamicAdapter(getActivity(), new CartoonDynamicAdapter.OnItemClickListener() {
                @Override
                public void onItemClicked(CartoonMessage dynamic) {
                    Intent intent = new Intent(getActivity(), CartoonActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putSerializable(getString(R.string.cartoondetail_tag_cartoon), dynamic.getCartoon());
                    intent.putExtras(bundle);
                    getActivity().startActivity(intent);
                }
            });
            list.setAdapter(cartoonDynamicAdapter);
        }
        if (1 == page.getPage()) {
            cartoonDynamicAdapter.setData(dynamics);
        } else {
            cartoonDynamicAdapter.addData(dynamics);
        }
        list.postInvalidate();
        if (null != user) {
            this.user.clone(user);
            mApplication.user.clone(user);
            showUserInfo();
        }
    }

    @Override
    public void onLoadCartoonMessageSuccess(MCUser user, ArrayList<CartoonMessage> messages, Page page) {
        cartoonMessagePage = page;
        if (null == cartoonMessageAdapter) {
            cartoonMessageAdapter = new CartoonMessageAdapter(getActivity(), new CartoonMessageAdapter.OnItemClickListener() {
                @Override
                public void onItemClicked(CartoonMessage message) {
                    Intent intent = new Intent(getActivity(), CartoonActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putSerializable(getString(R.string.cartoondetail_tag_cartoon), message.getCartoon());
                    intent.putExtras(bundle);
                    getActivity().startActivity(intent);
                }
            });
            list.setAdapter(cartoonMessageAdapter);
        }
        if (1 == page.getPage()) {
            cartoonMessageAdapter.setData(messages);
        } else {
            cartoonMessageAdapter.addData(messages);
            list.setAdapter(cartoonMessageAdapter);
        }
        if (null != user) {
            this.user.clone(user);
            mApplication.user.clone(user);
            showUserInfo();
        }
    }

    @Override
    public void onLoadCartoonMessageFailure(String msg) {
        showMessage(msg, null, null);
    }

    @Override
    public void onLoadCartoonWorkSuccess(MCUser user, ArrayList<Cartoon> works, Page page) {
        cartoonWorkPage = page;
        if (null == cartoonWorkAdapter) {
            cartoonWorkAdapter = new CartoonWorkAdapter(getActivity(), new CartoonWorkAdapter.OnItemClickListener() {
                @Override
                public void onItemClicked(Cartoon cartoon) {
                    Intent intent = new Intent(getActivity(), CartoonActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putSerializable(getString(R.string.cartoondetail_tag_cartoon), cartoon);
                    intent.putExtras(bundle);
                    getActivity().startActivity(intent);
                }
            });
            work.setAdapter(cartoonWorkAdapter);
        }
        if (1 == page.getPage()) {
            cartoonWorkAdapter.setData(works);
        } else {
            cartoonWorkAdapter.addData(works);
        }
        if (null != user) {
            this.user.clone(user);
            mApplication.user.clone(user);
            showUserInfo();
        }
    }

    @Override
    public void onLoadCartoonWorkFailure(String msg) {
        showMessage(msg, null, null);
    }

    @Override
    public void onLoadCommunityDynamicSuccess(ArrayList<CommunityDynamic> dynamics, User user, Page page) {
        communityDynamicPage = page;
        if (null == communityDynamicAdapter) {
            communityDynamicAdapter = new CommunityDynamicAdapter(getActivity(), new CommunityDynamicAdapter.OnItemClickListener() {
                @Override
                public void onItemClicked(CommunityDynamic dynamic) {
                    Post post = new Post();
                    post.setId(dynamic.getId());
                    Intent intent = new Intent(getActivity(), PostActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putSerializable(getString(R.string.tag_post), post);
                    intent.putExtras(bundle);
                    startActivity(intent);
                }
            });
            list.setAdapter(communityDynamicAdapter);
        }
        if (1 == page.getPage()) {
            communityDynamicAdapter.setData(dynamics);
        } else {
            communityDynamicAdapter.addData(dynamics);
        }
        if (null != user) {
            this.user = user;
            showUserInfo();
        }
    }

    @Override
    public void onLoadCommunityDynamicFailure(String msg) {
        showMessage(msg, null, null);
    }

    @Override
    public void onLoadCommunityMessageSuccess(ArrayList<CommunityMessage> messages, User user, Page page) {
        communityMessagePage = page;
        if (null == communityMessageAdapter) {
            communityMessageAdapter = new CommunityMessageAdapter(getActivity(), new CommunityMessageAdapter.OnItemClickListener() {
                @Override
                public void onItemClicked(CommunityMessage message) {
                    Post post = new Post();
                    post.setId(message.getId());
                    Intent intent = new Intent(getActivity(), PostActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putSerializable(getString(R.string.tag_post), post);
                    intent.putExtras(bundle);
                    startActivity(intent);
                }
            });
            list.setAdapter(communityMessageAdapter);
        }
        if (1 == page.getPage()) {
            communityMessageAdapter.setData(messages);
        } else {
            communityMessageAdapter.addData(messages);
        }
        if (null != user) {
            this.user = user;
            showUserInfo();
        }
    }

    @Override
    public void onLoadCommunityMessageFailure(String msg) {
        showMessage(msg, null, null);
    }

    @Override
    public void onLoadCommunityWorkSuccess(ArrayList<Post> works, User user, Page page) {
        communityWorkPage = page;
        if (null == communityWorkAdapter) {
            communityWorkAdapter = new PostAdapter(getActivity(), new PostAdapter.OnItemClickListener() {
                @Override
                public void onItemClicked(Post post) {
                    Intent intent = new Intent(getActivity(), PostActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putSerializable(getString(R.string.tag_post), post);
                    intent.putExtras(bundle);
                    getActivity().startActivity(intent);
                }

                @Override
                public void onUserClicked(User user) {
                    resetUser(user);
                }
            });
            list.setAdapter(communityWorkAdapter);
        }
        if (1 == page.getPage()) {
            communityWorkAdapter.setData(works);
        } else {
            communityWorkAdapter.addData(works);
        }
    }

    @Override
    public void onLoadCommunityWorkFailure(String msg) {
        showMessage(msg, null, null);
    }

    @Override
    public void onLoadFriendSuccess(ArrayList<MCUser> friends, Page page) {
        friendPage = page;
        if (null == friendAdapter) {
            friendAdapter = new FriendAdapter(getActivity(), new FriendAdapter.OnItemClickListener() {
                @Override
                public void onItemClicked(MCUser user) {
                    resetUser(new User(user));
                }

                @Override
                public void onChatClicked(MCUser user) {
                    startChat(user);
                }
            });
            list.setAdapter(friendAdapter);
        }
        if (1 == page.getPage()) {
            friendAdapter.setData(friends);
        } else {
            friendAdapter.addData(friends);
        }
    }

    @Override
    public void OnloadFriendFailure(String msg) {
        showMessage(msg, null, null);
    }

    @Override
    public void onMoreAsked(int overallItemsCount, int itemsBeforeMore, int maxLastVisiblePosition) {
        loadData(false);
    }

    @Override
    public void onRefresh() {
        loadData(true);
    }
}
