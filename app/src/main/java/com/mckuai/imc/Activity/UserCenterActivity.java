package com.mckuai.imc.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.AppCompatRadioButton;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.View;
import android.widget.RadioGroup;

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
import com.mckuai.imc.Bean.Page;
import com.mckuai.imc.Bean.Post;
import com.mckuai.imc.Bean.User;
import com.mckuai.imc.R;
import com.mckuai.imc.Util.MCNetEngine;

import java.util.ArrayList;

public class UserCenterActivity extends BaseActivity
        implements View.OnClickListener, RadioGroup.OnCheckedChangeListener,
        MCNetEngine.OnLoadCartoonDynamicResponseListener,
        MCNetEngine.OnLoadCartoonMessageResponseListener,
        MCNetEngine.OnLoadCartoonWorkResponseListener,
        MCNetEngine.OnLoadCommunityDynamicResponseListener,
        MCNetEngine.OnLoadCommunityMessageResponseListener,
        MCNetEngine.OnloadCommunityWorkResponseListener,
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


    private AppCompatImageView userCover;
    private AppCompatTextView userLevel;
    private AppCompatButton chat;
    private AppCompatButton addFriend;
    private RadioGroup group;
    private RadioGroup type;
    private SuperRecyclerView list;
    private SuperRecyclerView work;//作品布局不一样
    private AppCompatRadioButton cartoon;
    private AppCompatRadioButton message;
    private AppCompatRadioButton dynamic;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_usercenter);
        initToolbar(R.id.toolbar, 0, null);
        initDrawer();
        getParams();
        initView();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    private void initView() {
        userCover = (AppCompatImageView) findViewById(R.id.usercover);
        userLevel = (AppCompatTextView) findViewById(R.id.userlevel);
        chat = (AppCompatButton) findViewById(R.id.chat);
        addFriend = (AppCompatButton) findViewById(R.id.addfriend);
        group = (RadioGroup) findViewById(R.id.group);
        type = (RadioGroup) findViewById(R.id.type);
        cartoon = (AppCompatRadioButton) findViewById(R.id.cartoon);
        message = (AppCompatRadioButton) findViewById(R.id.message);
        dynamic = (AppCompatRadioButton) findViewById(R.id.dynamic);
        list = (SuperRecyclerView) findViewById(R.id.list);
        work = (SuperRecyclerView) findViewById(R.id.worklist);

        RecyclerView.LayoutManager linearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        list.setLayoutManager(linearLayoutManager);
        RecyclerView.LayoutManager manager = new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);
        work.setLayoutManager(manager);

        cartoon.setChecked(true);
        changeUIByUser();

        addFriend.setOnClickListener(this);
        chat.setOnClickListener(this);
        group.setOnCheckedChangeListener(this);
        type.setOnCheckedChangeListener(this);
    }

    private void getParams() {
        Intent intent = getIntent();
        if (null != intent) {
            int userId = (int) intent.getIntExtra(getString(R.string.usercenter_tag_userid), 0);
            if (mApplication.isLogin() && mApplication.user.getId() == userId) {
                user = new User(mApplication.user);
            } else {
                user = new User((long) userId);
            }
        }
    }

    private void changeUIByUser() {
        if (isMySelf()) {
            chat.setVisibility(View.GONE);
            addFriend.setVisibility(View.GONE);
            message.setVisibility(View.VISIBLE);
            message.setChecked(true);
            contentType = 36;
        } else {
            chat.setVisibility(View.VISIBLE);
            addFriend.setVisibility(View.VISIBLE);
            message.setVisibility(View.GONE);
            dynamic.setChecked(true);
            contentType = 34;
        }
    }

    private boolean isMySelf() {
        return mApplication.isLogin() && mApplication.user.getId() == user.getId();
    }

    private void loadData() {
        switch (contentType) {
            case 36:
                if (null == cartoonMessagePage) {
                    cartoonMessagePage = new Page();
                }
                mApplication.netEngine.loadCartoonMessage(this, user.getId().intValue(), cartoonMessagePage.getNextPage(), this);
                break;
            case 34:
                if (null == cartoonDynamicPage) {
                    cartoonDynamicPage = new Page();
                }
                mApplication.netEngine.loadCartoonDyanmic(this, user.getId().intValue(), cartoonDynamicPage.getNextPage(), this);
                break;
            case 33:
                if (null == cartoonWorkPage) {
                    cartoonWorkPage = new Page();
                }
                mApplication.netEngine.loadCartoonWork(this, user.getId().intValue(), cartoonWorkPage.getNextPage(), this);
                break;
            case 20:
                if (null == communityMessagePage) {
                    communityMessagePage = new Page();
                }
                mApplication.netEngine.loadCommunityMessage(this, user.getId().intValue(), communityMessagePage.getNextPage(), this);
                break;
            case 18:
                if (null == communityDynamicPage) {
                    communityDynamicPage = new Page();
                }
                mApplication.netEngine.loadCommunityDynamic(this, user.getId().intValue(), communityDynamicPage.getNextPage(), this);
                break;
            case 17:
                if (null == communityWorkPage) {
                    communityWorkPage = new Page();
                }
                mApplication.netEngine.loadCommunityWork(this, user.getId().intValue(), communityWorkPage.getNextPage(), this);
                break;
            case 8:
                if (null == friendPage) {
                    friendPage = new Page();
                }
                mApplication.netEngine.loadFriendList(this, friendPage.getNextPage(), this);
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
                } else {
                    loadData();
                }
                break;
            case 34://动漫动态
                list.setVisibility(View.VISIBLE);
                work.setVisibility(View.GONE);
                if (null != cartoonDynamicAdapter) {
                    list.setAdapter(cartoonDynamicAdapter);
                } else {
                    loadData();
                }
                break;
            case 33://动漫作品
                list.setVisibility(View.GONE);
                work.setVisibility(View.VISIBLE);
                if (null != cartoonWorkAdapter) {
                    work.setAdapter(cartoonWorkAdapter);
                } else {
                    loadData();
                }
                break;
            case 20://社区消息
                list.setVisibility(View.VISIBLE);
                work.setVisibility(View.GONE);
                if (null != communityMessageAdapter) {
                    list.setAdapter(communityMessageAdapter);
                } else {
                    loadData();
                }
                break;
            case 18://社区动态
                list.setVisibility(View.VISIBLE);
                work.setVisibility(View.GONE);
                if (null != communityDynamicAdapter) {
                    list.setAdapter(communityDynamicAdapter);
                } else {
                    loadData();
                }
                break;
            case 17://社区作品
                list.setVisibility(View.VISIBLE);
                work.setVisibility(View.GONE);
                if (null != communityWorkAdapter) {
                    list.setAdapter(communityWorkAdapter);
                } else {
                    loadData();
                }
                break;
            case 8://朋友
                list.setVisibility(View.VISIBLE);
                work.setVisibility(View.GONE);
                if (null != friendAdapter) {
                    list.setAdapter(friendAdapter);
                } else {
                    loadData();
                }
                break;
        }
    }

    private void showUserInfo() {

    }

    private void resetUser(User user) {
        this.user = user;
        contentType = 36;
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
        loadData();
    }

    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {
        switch (checkedId) {
            case R.id.cartoon:
                type.setVisibility(View.VISIBLE);
                contentType = contentType & 39;//100,111
                break;
            case R.id.community:
                type.setVisibility(View.VISIBLE);
                contentType = contentType & 23;//010,111
                break;
            case R.id.friend:
                type.setVisibility(View.GONE);//001,111
                contentType = 8;
                break;
            case R.id.message:
                contentType = contentType & 60;//111,100
                break;
            case R.id.dynamic:
                contentType = contentType & 58;//111,010
                break;
            case R.id.work:
                contentType = contentType & 57;//111,001
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
    public void onLoadCartoonDynamicSuccess(final ArrayList<CartoonMessage> dynamics, Page page) {
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
        }
        if (1 == page.getPage()) {
            cartoonDynamicAdapter.setData(dynamics);
        } else {
            cartoonDynamicAdapter.addData(dynamics);
        }
    }

    @Override
    public void onLoadCartoonMessageSuccess(ArrayList<CartoonMessage> messages, Page page) {
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
        }
        if (1 == page.getPage()) {
            cartoonMessageAdapter.setData(messages);
        } else {
            cartoonMessageAdapter.addData(messages);
        }
    }

    @Override
    public void onLoadCartoonMessageFailure(String msg) {
        showMessage(msg, null, null);
    }

    @Override
    public void onLoadCartoonWorkSuccess(ArrayList<Cartoon> work, Page page) {
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
        }
        if (1 == page.getPage()) {
            cartoonWorkAdapter.setData(work);
        } else {
            cartoonWorkAdapter.addData(work);
        }
    }

    @Override
    public void onLoadCartoonWorkFailure(String msg) {
        showMessage(msg, null, null);
    }

    @Override
    public void onLoadCommunityDynamicSuccess(ArrayList<CommunityDynamic> dynamics, Page page) {
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
        }
        if (1 == page.getPage()) {
            communityDynamicAdapter.setData(dynamics);
        } else {
            communityDynamicAdapter.addData(dynamics);
        }
    }

    @Override
    public void onLoadCommunityDynamicFailure(String msg) {
        showMessage(msg, null, null);
    }

    @Override
    public void onLoadCommunityMessageSuccess(ArrayList<CommunityMessage> messages, Page page) {
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
        }
        if (1 == page.getPage()) {
            communityMessageAdapter.setData(messages);
        } else {
            communityMessageAdapter.addData(messages);
        }
    }

    @Override
    public void onLoadCommunityMessageFailure(String msg) {
        showMessage(msg, null, null);
    }

    @Override
    public void onLoadCommunityWorkSuccess(ArrayList<Post> works, Page page) {
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
        }
    }

    @Override
    public void onLoadCommunityWorkFailure(String msg) {
        showMessage(msg, null, null);
    }

    @Override
    public void onLoadFriendSuccess(ArrayList<User> friends, Page page) {
        friendPage = page;
        if (null == friendAdapter) {
            friendAdapter = new FriendAdapter(this, new FriendAdapter.OnItemClickListener() {
                @Override
                public void onItemClicked(User user) {
                    resetUser(user);
                }

                @Override
                public void onChatClicked(User user) {
                    showMessage("和它聊天", null, null);
                }
            });
        }
    }

    @Override
    public void OnloadFriendFailure(String msg) {
        showMessage(msg, null, null);
    }
}
