package com.mckuai.imc.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.AppCompatImageButton;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.AppCompatRadioButton;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RadioGroup;

import com.malinskiy.superrecyclerview.OnMoreListener;
import com.malinskiy.superrecyclerview.SuperRecyclerView;
import com.mckuai.imc.Adapter.CartoonDynamicAdapter;
import com.mckuai.imc.Adapter.CartoonMessageAdapter;
import com.mckuai.imc.Adapter.CartoonWorkAdapter;
import com.mckuai.imc.Adapter.CommunityDynamicAdapter;
import com.mckuai.imc.Adapter.CommunityMessageAdapter;
import com.mckuai.imc.Adapter.FriendAdapter;
import com.mckuai.imc.Adapter.PostAdapter;
import com.mckuai.imc.Base.BaseActivity;
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

public class UserCenterActivity extends BaseActivity
        implements View.OnClickListener, RadioGroup.OnCheckedChangeListener,
        MCNetEngine.OnLoadCartoonDynamicResponseListener,
        MCNetEngine.OnLoadCartoonMessageResponseListener,
        MCNetEngine.OnLoadCartoonWorkResponseListener,
        MCNetEngine.OnLoadCommunityDynamicResponseListener,
        MCNetEngine.OnLoadCommunityMessageResponseListener,
        MCNetEngine.OnloadCommunityWorkResponseListener,
        MCNetEngine.OnloadFriendResponseListener,
        MCNetEngine.OnAddFriendResponseListener,
        OnMoreListener,
        SwipeRefreshLayout.OnRefreshListener {
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


    private AppCompatImageView userCover;
    private AppCompatTextView userLevel;
    private AppCompatTextView userName;
    private LinearLayout operation;
    private AppCompatImageButton chat;
    private AppCompatImageButton addFriend;
    private RadioGroup group;
    private RadioGroup type;
    private SuperRecyclerView list;
    private SuperRecyclerView work;//作品布局不一样
    private AppCompatRadioButton cartoon;
    private AppCompatRadioButton message;
    private AppCompatRadioButton dynamic;
    private AppCompatRadioButton friend;
    private View spaceRight;
    private boolean checkedFriendship = false;
    private boolean isFriendShip = false;
    private boolean isLoading = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_usercenter);
        initToolbar(R.id.toolbar, 0, null);
        //initDrawer();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        getParams();
        initView();
        loader = ImageLoader.getInstance();
    }

    @Override
    protected void onResume() {
        super.onResume();
        showData();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case 1:
                    addFriend();
                    break;
                case 2:
                    startChat(null);
                    break;
            }
        }
    }

    private void initView() {
        userCover = (AppCompatImageView) findViewById(R.id.usercover);
        userLevel = (AppCompatTextView) findViewById(R.id.userlevel);
        userName = (AppCompatTextView) findViewById(R.id.actionbar_title);
        operation = (LinearLayout) findViewById(R.id.layut_opeartion);
        chat = (AppCompatImageButton) findViewById(R.id.chat);
        addFriend = (AppCompatImageButton) findViewById(R.id.addfriend);
        group = (RadioGroup) findViewById(R.id.group);
        friend = (AppCompatRadioButton) findViewById(R.id.friend);
        type = (RadioGroup) findViewById(R.id.type);
        cartoon = (AppCompatRadioButton) findViewById(R.id.cartoon);
        message = (AppCompatRadioButton) findViewById(R.id.message);
        dynamic = (AppCompatRadioButton) findViewById(R.id.dynamic);
        list = (SuperRecyclerView) findViewById(R.id.list);
        work = (SuperRecyclerView) findViewById(R.id.worklist);
        spaceRight = findViewById(R.id.space_right);

        RecyclerView.LayoutManager linearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        list.setLayoutManager(linearLayoutManager);
        RecyclerView.LayoutManager manager = new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);
        work.setLayoutManager(manager);

        userName.setTextColor(getResources().getColor(R.color.icons));

        cartoon.setChecked(true);
        changeUIByUser();

        addFriend.setOnClickListener(this);
        chat.setOnClickListener(this);
        group.setOnCheckedChangeListener(this);
        type.setOnCheckedChangeListener(this);

        list.setupMoreListener(this, 1);
        list.setRefreshListener(this);
        work.setupMoreListener(this, 1);
        work.setRefreshListener(this);

    }

    private void getParams() {
        Intent intent = getIntent();
        if (null != intent) {
            int userId = intent.getIntExtra(getString(R.string.usercenter_tag_userid), 0);
            if (mApplication.isLogin() && mApplication.user.getId() == userId) {
                user = new User(mApplication.user);
            } else {
                user = new User((long) userId);
            }
        }
    }

    private void changeUIByUser() {
        if (isMySelf()) {
            operation.setVisibility(View.GONE);
            message.setVisibility(View.VISIBLE);
            friend.setVisibility(View.VISIBLE);
            spaceRight.setVisibility(View.VISIBLE);
            message.setChecked(true);
            contentType = 36;
        } else {
            operation.setVisibility(View.VISIBLE);
            message.setVisibility(View.GONE);
            friend.setVisibility(View.GONE);
            spaceRight.setVisibility(View.GONE);
            dynamic.setChecked(true);
            contentType = 34;
        }
    }

    private boolean isMySelf() {
        return mApplication.isLogin() && mApplication.user.getId() == user.getId();
    }

    private void loadData(boolean isRefresh) {
        if (isLoading) {
            return;
        }
        switch (contentType) {
            case 36:
                if (null == cartoonMessagePage) {
                    cartoonMessagePage = new Page();
                }
                if (isRefresh) {
                    cartoonMessagePage.setPage(0);
                }
                mApplication.netEngine.loadCartoonMessage(this, user.getId().intValue(), cartoonMessagePage.getNextPage(), this);
                break;
            case 34:
                if (null == cartoonDynamicPage) {
                    cartoonDynamicPage = new Page();
                }
                if (isRefresh) {
                    cartoonDynamicPage.setPage(0);
                }
                mApplication.netEngine.loadCartoonDyanmic(this, user.getId().intValue(), cartoonDynamicPage.getNextPage(), this);
                break;
            case 33:
                if (null == cartoonWorkPage) {
                    cartoonWorkPage = new Page();
                }
                if (isRefresh) {
                    cartoonWorkPage.setPage(0);
                }
                mApplication.netEngine.loadCartoonWork(this, user.getId().intValue(), cartoonWorkPage.getNextPage(), this);
                break;
            case 20:
                if (null == communityMessagePage) {
                    communityMessagePage = new Page();
                }
                if (isRefresh) {
                    communityMessagePage.setPage(0);
                }
                mApplication.netEngine.loadCommunityMessage(this, user.getId().intValue(), communityMessagePage.getNextPage(), this);
                break;
            case 18:
                if (null == communityDynamicPage) {
                    communityDynamicPage = new Page();
                }
                if (isRefresh) {
                    communityDynamicPage.setPage(0);
                }
                mApplication.netEngine.loadCommunityDynamic(this, user.getId().intValue(), communityDynamicPage.getNextPage(), this);
                break;
            case 17:
                if (null == communityWorkPage) {
                    communityWorkPage = new Page();
                }
                if (isRefresh) {
                    communityWorkPage.setPage(0);
                }
                mApplication.netEngine.loadCommunityWork(this, user.getId().intValue(), communityWorkPage.getNextPage(), this);
                break;
            default:
                if (8 == (contentType & 8)){
                    if (null == friendPage) {
                        friendPage = new Page();
                    }
                    if (isRefresh) {
                        friendPage.setPage(0);
                    }
                    mApplication.netEngine.loadFriendList(this, friendPage.getNextPage(), this);
                }
                break;
        }
    }

    private void showData() {
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
                    list.setAdapter(cartoonDynamicAdapter);
                    cartoonDynamicAdapter.notifyDataSetChanged();
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
                if (8 == (contentType & 8)){
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
    }

    private void showUserInfo() {
        if (null != user && 0 != user.getId() && null != user.getName() && null != user.getHeadImage()) {
            if (null == userCover.getTag() || !userCover.getTag().equals(user.getHeadImage()))
                loader.displayImage(user.getHeadImage(), userCover, mApplication.getCircleOptions());
            userName.setText(user.getNickEx());
            userLevel.setText(getString(R.string.usercenter_userlevel,user.getLevel()));
        }
    }

    private void resetUser(User user) {
        this.user = user;
        if (user.getId() != mApplication.user.getId()) {
            contentType = 34;
        } else {
            contentType = 36;
        }
        isLoading = true;
        cartoon.setChecked(true);
        dynamic.setChecked(true);
        isLoading = false;
        cartoonMessageAdapter = null;
        cartoonDynamicAdapter = null;
        cartoonWorkAdapter = null;
        communityMessageAdapter = null;
        communityDynamicAdapter = null;
        communityWorkAdapter = null;
        friendAdapter = null;
        cartoonMessagePage = null;
        cartoonDynamicPage = null;
        cartoonWorkPage = null;
        communityMessagePage = null;
        communityDynamicPage = null;
        communityWorkPage = null;
        friendPage = null;
        work.setVisibility(View.GONE);
        list.setVisibility(View.VISIBLE);
        changeUIByUser();
        checkedFriendship = false;
        isFriendShip = false;
        loadData(false);
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
                contentType = (contentType & 56)|4;//111,100
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
                addFriend();
                break;
            case R.id.chat:
                startChat(null);
                break;
        }
    }

    private void addFriend() {
        if (mApplication.isLogin()) {
            mApplication.netEngine.addFriend(this, user.getId().intValue(), this);
        } else {
            callLogin(1);
        }
    }

    private void startChat(MCUser chatUser) {
        final User target;
        if (null == chatUser) {
            target = this.user;
        } else {
            target = new User(chatUser);
        }
        if (mApplication.isLogin()) {
            if (RongIM.getInstance().getRongIMClient().getCurrentConnectionStatus() == RongIMClient.ConnectionStatusListener.ConnectionStatus.CONNECTED) {
                RongIM.getInstance().startPrivateChat(this, target.getName(), target.getNickEx());
            } else {
                mApplication.loginIM(new RongIMClient.ConnectCallback() {
                    @Override
                    public void onTokenIncorrect() {

                    }

                    @Override
                    public void onSuccess(String s) {
                        RongIM.getInstance().startPrivateChat(UserCenterActivity.this, target.getName(), target.getNickEx());
                    }

                    @Override
                    public void onError(RongIMClient.ErrorCode errorCode) {

                    }
                });
            }
        } else {
            callLogin(2);
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
            cartoonDynamicAdapter = new CartoonDynamicAdapter(this, new CartoonDynamicAdapter.OnItemClickListener() {
                @Override
                public void onItemClicked(CartoonMessage dynamic) {
                    Intent intent = new Intent(UserCenterActivity.this, CartoonActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putSerializable(getString(R.string.cartoondetail_tag_cartoon), dynamic.getCartoon());
                    intent.putExtras(bundle);
                    startActivity(intent);
                }
            });
            list.setAdapter(cartoonDynamicAdapter);
        }
        if (1 == page.getPage()) {
            cartoonDynamicAdapter.setData(dynamics);
        } else {
            cartoonDynamicAdapter.addData(dynamics);
        }
        if (null != user) {
            this.user.clone(user);
            showUserInfo();
        }
        list.postInvalidate();
    }

    @Override
    public void onLoadCartoonMessageSuccess(MCUser user, ArrayList<CartoonMessage> messages, Page page) {
        cartoonMessagePage = page;
        if (null == cartoonMessageAdapter) {
            cartoonMessageAdapter = new CartoonMessageAdapter(this, new CartoonMessageAdapter.OnItemClickListener() {
                @Override
                public void onItemClicked(CartoonMessage message) {
                    Intent intent = new Intent(UserCenterActivity.this, CartoonActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putSerializable(getString(R.string.cartoondetail_tag_cartoon), message.getCartoon());
                    intent.putExtras(bundle);
                    startActivity(intent);
                }
            });
            list.setAdapter(cartoonMessageAdapter);
        }
        if (1 == page.getPage()) {
            cartoonMessageAdapter.setData(messages);
        } else {
            cartoonMessageAdapter.addData(messages);
        }
        if (null != user) {
            this.user.clone(user);
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
            cartoonWorkAdapter = new CartoonWorkAdapter(this, new CartoonWorkAdapter.OnItemClickListener() {
                @Override
                public void onItemClicked(Cartoon cartoon) {
                    Intent intent = new Intent(UserCenterActivity.this, CartoonActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putSerializable(getString(R.string.cartoondetail_tag_cartoon), cartoon);
                    intent.putExtras(bundle);
                    startActivity(intent);
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
            showUserInfo();
        }
    }

    @Override
    public void onLoadCartoonWorkFailure(String msg) {
        showMessage(msg, null, null);
    }

    @Override
    public void onLoadCommunityDynamicSuccess(ArrayList<CommunityDynamic> dynamics,User user, Page page) {
        communityDynamicPage = page;
        if (null == communityDynamicAdapter) {
            communityDynamicAdapter = new CommunityDynamicAdapter(this, new CommunityDynamicAdapter.OnItemClickListener() {
                @Override
                public void onItemClicked(CommunityDynamic dynamic) {
                    Post post = new Post();
                    post.setId(dynamic.getId());
                    Intent intent = new Intent(UserCenterActivity.this, PostActivity.class);
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
        if (null != user){
            this.user = user;
            showUserInfo();
        }
    }

    @Override
    public void onLoadCommunityDynamicFailure(String msg) {
        showMessage(msg, null, null);
    }

    @Override
    public void onLoadCommunityMessageSuccess(ArrayList<CommunityMessage> messages,User user, Page page) {
        communityMessagePage = page;
        if (null == communityMessageAdapter) {
            communityMessageAdapter = new CommunityMessageAdapter(this, new CommunityMessageAdapter.OnItemClickListener() {
                @Override
                public void onItemClicked(CommunityMessage message) {
                    Post post = new Post();
                    post.setId(message.getId());
                    Intent intent = new Intent(UserCenterActivity.this, PostActivity.class);
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
        if (null != user){
            this.user = user;
            showUserInfo();
        }
    }

    @Override
    public void onLoadCommunityMessageFailure(String msg) {
        showMessage(msg, null, null);
    }

    @Override
    public void onLoadCommunityWorkSuccess(ArrayList<Post> works,User user, Page page) {
        communityWorkPage = page;
        if (null == communityWorkAdapter) {
            communityWorkAdapter = new PostAdapter(this, new PostAdapter.OnItemClickListener() {
                @Override
                public void onItemClicked(Post post) {
                    Intent intent = new Intent(UserCenterActivity.this, PostActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putSerializable(getString(R.string.tag_post), post);
                    intent.putExtras(bundle);
                    startActivity(intent);
                }

                @Override
                public void onUserClicked(User user) {
                    resetUser(user);
                }
            });
            list.setAdapter(communityWorkAdapter);
        }
        if (1 == page.getPage()){
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
            friendAdapter = new FriendAdapter(this, new FriendAdapter.OnItemClickListener() {
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
        if (1 == page.getPage()){
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
    public void onAddFriendFailure(String msg) {
        showMessage(msg, null, null);
    }

    @Override
    public void onAddFriendSuccess() {
        showMessage("添加好友成功", null, null);
        addFriend.setEnabled(false);
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
