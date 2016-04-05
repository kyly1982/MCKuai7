package com.mckuai.imc.Fragment;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioGroup;

import com.marshalchen.ultimaterecyclerview.UltimateRecyclerView;
import com.mckuai.imc.Activity.CartoonActivity;
import com.mckuai.imc.Activity.UserCenterActivity;
import com.mckuai.imc.Adapter.CartoonAdapter;
import com.mckuai.imc.Base.BaseActivity;
import com.mckuai.imc.Base.BaseFragment;
import com.mckuai.imc.Base.MCKuai;
import com.mckuai.imc.Bean.Cartoon;
import com.mckuai.imc.Bean.Page;
import com.mckuai.imc.Bean.User;
import com.mckuai.imc.R;
import com.mckuai.imc.Util.MCNetEngine;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.umeng.socialize.media.UMImage;

import java.util.ArrayList;

/**
 * A placeholder fragment containing a simple view.
 */
public class MainFragment_Cartoon extends BaseFragment implements RadioGroup.OnCheckedChangeListener
        , MCNetEngine.OnLoadCartoonListResponseListener
        , MCNetEngine.OnRewardCartoonResponseListener
        , SwipeRefreshLayout.OnRefreshListener
        , UltimateRecyclerView.OnLoadMoreListener
        , CartoonAdapter.OnItemClickListener {
    private String[] mCartoonType;
    private ArrayList<Cartoon> mHotCartoon;
    private ArrayList<Cartoon> mNewCartoon;
    private CartoonAdapter mAdapter;
    private int typeIndex = 0;
    private Page pageNew = new Page();
    private Page pageHot = new Page();
    private MCKuai mApplication;
    private boolean isRefreshNeed = false;
    private Cartoon commentCartoon;
    private Cartoon priseCartoon;
    private int rewardCartoonId;

    private UltimateRecyclerView mCartoonListView;
    private View view;
    private boolean isPaused = false;

    public MainFragment_Cartoon() {
        mTitleResId = R.string.fragment_cartoon;
        mApplication = MCKuai.instence;
    }

    public void setType(int type) {
        if (typeIndex != type) {
            typeIndex = type;
            switch (type) {
                case 0:
                    if (null == mNewCartoon || mNewCartoon.isEmpty()) {
                        loadData();
                    } else {
                        showData();
                    }
                    break;
                case 1:
                    if (null == mHotCartoon || mHotCartoon.isEmpty()) {
                        loadData();
                    } else {
                        showData();
                    }
                    break;
            }
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (null == view) {
            view = inflater.inflate(R.layout.fragment_main_cartoon, container, false);
            if (null != getActivity() && null == mCartoonListView) {
                initView();
            }
        }
        mCartoonType = getResources().getStringArray(R.array.cartoon_ordertype);

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();

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

        mCartoonListView = (UltimateRecyclerView) view.findViewById(R.id.cartoonlist);
        mCartoonListView.setHasFixedSize(true);
        RecyclerView.LayoutManager manager = new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false);
        mCartoonListView.setLayoutManager(manager);
        //mCartoonListView.setEmptyView(R.layout.emptyview);
        //mCartoonListView.enableLoadmore();
        mCartoonListView.setDefaultOnRefreshListener(this);
        mCartoonListView.setOnLoadMoreListener(this);
        mCartoonListView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
            }

            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (1 == newState) {
                    if (!isPaused) {
                        ImageLoader.getInstance().pause();
                        isPaused = true;
                    }
                } else {
                    if (isPaused) {
                        ImageLoader.getInstance().resume();
                        isPaused = false;
                    }
                }
            }
        });
    }

    private void loadData() {
        mApplication.netEngine.loadCartoonList(getActivity(), pageNew, this);
    }

    private void showData() {
        if (null == mAdapter) {
            mAdapter = new CartoonAdapter(getActivity(), this);
            mCartoonListView.setAdapter(mAdapter);
            loadData();
        } else {
            switch (typeIndex) {
                case 0:
                    if (null != mNewCartoon) {
                        mAdapter.setData(mNewCartoon);
                    } else {
                        loadData();
                    }
                    break;
                case 1:
                    if (null != mHotCartoon) {
                        mAdapter.setData(mHotCartoon);
                    } else {
                        loadData();
                    }
                    break;
            }
        }

        if (isRefreshNeed) {
            //mAdapter.notifyDataSetChanged();
            // mCartoonListView.setTop(0);
            isRefreshNeed = false;
        }
    }

    /*点击类型切换的回调*/
    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {
        isRefreshNeed = true;
        switch (checkedId) {
            case R.id.cartoon_type_hot:
                typeIndex = 1;
                if (null == mHotCartoon || mHotCartoon.isEmpty()) {
                    loadData();
                } else {
                    showData();
                }
                break;
            case R.id.cartoon_type_new:
                typeIndex = 0;
                if (null == mNewCartoon || mNewCartoon.isEmpty()) {
                    loadData();
                } else {
                    showData();
                }
                break;
        }
    }

    private void rewardCartoon(Cartoon cartoon) {
        rewardCartoonId = cartoon.getId();
        mApplication.netEngine.rewardCartoon(getActivity(), true, cartoon.getId(), this);
    }

    @Override
    public void onLoadCartoonListFailure(String msg) {
        Snackbar.make(mCartoonListView, msg, Snackbar.LENGTH_SHORT).show();
    }

    @Override
    public void onLoadCartoonListSuccess(ArrayList<Cartoon> cartoons, Page page) {

        switch (typeIndex) {
            case 0:
                pageNew = page;
                if (null == mNewCartoon) {
                    mNewCartoon = cartoons;
                } else {
                    mNewCartoon.addAll(cartoons);
                }
                break;
            case 1:
                pageHot = page;
                if (null == mHotCartoon) {
                    mHotCartoon = cartoons;
                } else {
                    mHotCartoon.addAll(cartoons);
                }
                break;
        }
        showData();
    }

    @Override
    public void onRewardCartoonSuccess() {
        Snackbar.make(mCartoonListView, "打赏成功!", Snackbar.LENGTH_LONG).show();
        updateDate(priseCartoon, true);
        switch (typeIndex) {
            case 0:
                refreshData(mNewCartoon);
                break;
            case 1:
                refreshData(mHotCartoon);
                break;
        }
        mAdapter.notifyDataSetChanged();
        rewardCartoonId = -1;
    }

    private void refreshData(ArrayList<Cartoon> cartoons) {
        for (Cartoon cartoon : cartoons) {
            if (cartoon.getId() == rewardCartoonId) {
                if (null == cartoon.getRewardList()) {
                    ArrayList<User> users = new ArrayList<>(1);
                    users.add(new User(mApplication.user));
                    cartoon.setRewardList(users);
                } else {
                    cartoon.getRewardList().add(new User(mApplication.user));
                }
                break;
            }
        }
    }

    @Override
    public void onRewardaCartoonFailure(String msg) {
        Snackbar.make(mCartoonListView, "打赏失败!", Snackbar.LENGTH_LONG).show();
    }

    private void updateDate(Cartoon newCartoon, boolean isReward) {
        switch (typeIndex) {
            case 0:
                for (Cartoon cartoon : mNewCartoon) {
                    if (cartoon.getId() == newCartoon.getId()) {
                        if (isReward) {
                            cartoon.setPrise(cartoon.getPrise() + 1);
                        } else {
                            cartoon.setReplyNum(cartoon.getReplyNum() + 1);
                        }
                        mAdapter.notifyDataSetChanged();
                        break;
                    }
                }
                break;
            case 1:
                for (Cartoon cartoon : mHotCartoon) {
                    if (cartoon.getId() == newCartoon.getId()) {
                        if (isReward) {
                            cartoon.setPrise(cartoon.getPrise() + 1);
                        } else {
                            cartoon.setReplyNum(cartoon.getReplyNum() + 1);
                        }
                        mAdapter.notifyDataSetChanged();
                        break;
                    }
                }
                break;
        }
    }


    @Override
    public void loadMore(int itemsCount, int maxLastVisiblePosition) {
        switch (typeIndex) {
            case 0:
                if (pageNew.getPageCount() > pageNew.getPage()) {
                    loadData();
                } else {
                    showMessage("已经没有更多了！", null, null);
                }
                break;
            case 1:
                if (pageHot.getPageCount() > pageHot.getPage()) {
                    loadData();
                } else {
                    showMessage("已经没有更多了！", null, null);
                }
                break;
        }
    }

    @Override
    public void onRefresh() {
        switch (typeIndex) {
            case 0:
                mNewCartoon.clear();
                pageNew.setPage(0);
                loadData();
                break;
            case 1:
                mHotCartoon.clear();
                pageHot.setPage(0);
                loadData();
                break;
        }
    }

    //点击item
    @Override
    public void onCommentClick(Cartoon cartoon) {
        Intent intent = new Intent(getActivity(), CartoonActivity.class);
        Bundle bundle = new Bundle();
        commentCartoon = cartoon;
        bundle.putSerializable(getString(R.string.cartoondetail_tag_cartoon), cartoon);
        intent.putExtra("COMMENTCAROON", true);
        intent.putExtras(bundle);
        startActivityForResult(intent, 99);
    }

    @Override
    public void onItemClick(Cartoon cartoon) {
        Intent intent = new Intent(getActivity(), CartoonActivity.class);
        Bundle bundle = new Bundle();
        commentCartoon = cartoon;
        bundle.putSerializable(getString(R.string.cartoondetail_tag_cartoon), cartoon);
        intent.putExtras(bundle);
        startActivityForResult(intent, 99);
    }

    @Override
    public void onShareClick(Cartoon cartoon) {
        if (null != cartoon) {
            UMImage image = new UMImage(getActivity(), cartoon.getImage());
            ((BaseActivity) getActivity()).share("麦块漫画来了", "我刚在《麦块我的世界盒子》上发现一个在非常有意思的漫画，跟我来膜拜大神！", getString(R.string.shareCartoon) + cartoon.getId(), image);
        }
    }

    @Override
    public void onUserClick(User user) {
        if (null != user) {
            Intent intent = new Intent(getActivity(), UserCenterActivity.class);
            intent.putExtra(getString(R.string.usercenter_tag_userid), user.getId().intValue());
            startActivity(intent);
        }
    }

    @Override
    public void onPriseClick(Cartoon cartoon) {
        priseCartoon = cartoon;
        if (mApplication.isLogin()) {
            rewardCartoon(cartoon);
        } else {
            ((BaseActivity) getActivity()).callLogin(3);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (Activity.RESULT_OK == resultCode) {
            switch (requestCode) {
                case 3:
                    rewardCartoon(priseCartoon);
                    break;
                case 99:
                    if (null != data) {
                        int reward = data.getIntExtra("REWARD", -1);
                        int comment = data.getIntExtra("COMMENT", -1);
                        if (-1 != reward) {
                            updateDate(commentCartoon, true);
                        }
                        if (-1 != comment) {
                            updateDate(commentCartoon, false);
                        }
                    }
                    break;
            }
        }
    }
}
