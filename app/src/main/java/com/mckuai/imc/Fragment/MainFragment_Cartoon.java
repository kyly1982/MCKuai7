package com.mckuai.imc.Fragment;

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

import com.malinskiy.superrecyclerview.OnMoreListener;
import com.malinskiy.superrecyclerview.SuperRecyclerView;
import com.mckuai.imc.Activity.CartoonActivity;
import com.mckuai.imc.Activity.LoginActivity;
import com.mckuai.imc.Activity.UserCenterActivity;
import com.mckuai.imc.Adapter.CartoonAdapter;
import com.mckuai.imc.Base.BaseActivity;
import com.mckuai.imc.Base.BaseFragment;
import com.mckuai.imc.Base.MCKuai;
import com.mckuai.imc.Bean.Cartoon;
import com.mckuai.imc.Bean.User;
import com.mckuai.imc.R;
import com.mckuai.imc.Util.MCNetEngine;
import com.umeng.socialize.media.UMImage;
import com.umeng.socialize.utils.Log;

import java.util.ArrayList;

/**
 * A placeholder fragment containing a simple view.
 */
public class MainFragment_Cartoon extends BaseFragment implements RadioGroup.OnCheckedChangeListener,
        View.OnClickListener,
        MCNetEngine.OnLoadCartoonListResponseListener
        , MCNetEngine.OnRewardCartoonResponseListener,
        OnMoreListener,
        SwipeRefreshLayout.OnRefreshListener,
        CartoonAdapter.OnItemClickListener {
    private String[] mCartoonType;
    private ArrayList<Cartoon> mHotCartoon;
    private ArrayList<Cartoon> mNewCartoon;
    private CartoonAdapter mAdapter;
    private int typeIndex = 0;
    private int lastId_new = 0;
    private int lastId_hot = 0;
    private MCKuai mApplication = MCKuai.instence;
    private boolean isRefreshNeed = false;
    private boolean isNewEOF = false;
    private boolean isHotEOF = false;

    private SuperRecyclerView mCartoonListView;
    private View view;

    public MainFragment_Cartoon() {
        mTitleResId = R.string.fragment_cartoon;
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
        if (null != view) {
            container.removeView(view);
        }
        view = inflater.inflate(R.layout.fragment_main_cartoon, container, true);
        mCartoonType = getResources().getStringArray(R.array.cartoon_ordertype);
        return mCartoonListView;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (null != getActivity() && null == mCartoonListView) {
            initView();
        }
        showData();
    }

    private void initView() {

        mCartoonListView = (SuperRecyclerView) view.findViewById(R.id.cartoonlist);
        RecyclerView.LayoutManager manager = new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false);
        mCartoonListView.setLayoutManager(manager);
        mCartoonListView.setupMoreListener(this, 1);
        mCartoonListView.setRefreshListener(this);
    }

    private void loadData() {
        switch (typeIndex) {
            case 0:
                mApplication.netEngine.loadCartoonList(getActivity(), mCartoonType[typeIndex], lastId_new, this);
                break;
            case 1:
                mApplication.netEngine.loadCartoonList(getActivity(), mCartoonType[typeIndex], lastId_hot, this);
                break;
        }
    }

    private void showData() {
        if (null == mAdapter) {
            Log.e("showData", "init adapter....");
            mAdapter = new CartoonAdapter(getActivity(), this);
            mCartoonListView.setAdapter(mAdapter);
            Log.e("showData", "init adapter commpelt");
        }
        switch (typeIndex) {
            case 0:
                if (null != mNewCartoon) {
                    Log.e("showData", "set data 0");
                    mAdapter.setData(mNewCartoon);
                } else {
                    loadData();
                }
                break;
            case 1:
                if (null != mHotCartoon) {
                    Log.e("showData", "set data 1");
                    mAdapter.setData(mHotCartoon);
                } else {
                    loadData();
                }
                break;
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

    /*点击item里的控件的回调*/
    @Override
    public void onClick(final View v) {
        Intent intent;
        Cartoon cartoon = (Cartoon) v.getTag();
        switch (v.getId()) {
            case R.id.cartoon_usercover://头像
                //((BaseActivity) getActivity()).showMessage("跳转个人中心", null, null);
                Long userId = cartoon.getOwner().getId();
                if (userId > 0) {
                    intent = new Intent(getActivity(), UserCenterActivity.class);
                    intent.putExtra(getString(R.string.usercenter_tag_userid), userId.intValue());
                    startActivity(intent);
                }
                break;
            case R.id.cartoon_shar://分享
                ((BaseActivity) getActivity()).share("标题", "内容", getString(R.string.appdownload_url), null);
                break;
            case R.id.cartoon_comment:
                if (null != cartoon) {
                    intent = new Intent(getActivity(), CartoonActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putSerializable(getString(R.string.cartoondetail_tag_cartoon), cartoon);
                    intent.putExtras(bundle);
                    startActivity(intent);
                }
                break;
            case R.id.cartoon_prise:

                if (mApplication.isLogin()) {
                    if (null != cartoon) {
                        rewardCartoon(cartoon);
                    }
                } else {
                    intent = new Intent(getActivity(), LoginActivity.class);
                    startActivityForResult(intent, 5);
                }
                break;
        }
    }


    private void rewardCartoon(Cartoon cartoon) {
        mApplication.netEngine.rewardCartoon(getActivity(), true, cartoon.getId(), this);
    }

    @Override
    public void onLoadCartoonListFailure(String msg) {
        Log.e("onLoadCartoonListSuccess", "false...");
        Snackbar.make(mCartoonListView, msg, Snackbar.LENGTH_SHORT).show();
        switch (typeIndex) {
            case 0:
                isNewEOF = true;
                break;
            case 1:
                isHotEOF = true;
                break;
        }
    }

    @Override
    public void onLoadCartoonListSuccess(ArrayList<Cartoon> cartoons) {
        Log.e("onLoadCartoonListSuccess", "ok...");
        if (cartoons.isEmpty() || 1 == cartoons.size() || 1 == cartoons.get(cartoons.size() - 1).getId()) {
            switch (typeIndex) {
                case 0:
                    isNewEOF = true;
                    break;
                case 1:
                    isHotEOF = true;
                    break;
            }
        }
        switch (typeIndex) {
            case 0:
                lastId_new = cartoons.get(cartoons.size() - 1).getId();
                if (null == mNewCartoon) {
                    mNewCartoon = cartoons;
                } else {
                    mNewCartoon.addAll(cartoons);
                }
                break;
            case 1:
                lastId_hot = cartoons.get(cartoons.size() - 1).getId();
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
    }

    @Override
    public void onRewardaCartoonFailure(String msg) {
        Snackbar.make(mCartoonListView, "打赏失败!", Snackbar.LENGTH_LONG).show();
    }


    @Override
    public void onMoreAsked(int overallItemsCount, int itemsBeforeMore, int maxLastVisiblePosition) {
        switch (typeIndex) {
            case 0:
                if (!isNewEOF) {
                    loadData();
                } else {
                    showMessage("已经没有更多了！", null, null);
                    mCartoonListView.hideMoreProgress();
                }
                break;
            case 1:
                if (!isHotEOF) {
                    loadData();
                } else {
                    showMessage("已经没有更多了！", null, null);
                    mCartoonListView.hideMoreProgress();
                }
                break;
        }
    }

    @Override
    public void onRefresh() {
        switch (typeIndex) {
            case 0:
                mNewCartoon.clear();
                isNewEOF = false;
                loadData();
                break;
            case 1:
                mHotCartoon.clear();
                isHotEOF = false;
                loadData();
                break;
        }
    }

    //点击item
    @Override
    public void onCommentClick(Cartoon cartoon) {
        Intent intent = new Intent(getActivity(), CartoonActivity.class);
        Bundle bundle = new Bundle();
        bundle.putSerializable(getString(R.string.cartoondetail_tag_cartoon), cartoon);
        intent.putExtras(bundle);
        startActivity(intent);
    }

    @Override
    public void onItemClick(Cartoon cartoon) {
        Intent intent = new Intent(getActivity(), CartoonActivity.class);
        Bundle bundle = new Bundle();
        bundle.putSerializable(getString(R.string.cartoondetail_tag_cartoon), cartoon);
        intent.putExtras(bundle);
        startActivity(intent);
    }

    @Override
    public void onShareClick(Cartoon cartoon) {
        if (null != cartoon) {
            UMImage image = new UMImage(getActivity(), cartoon.getImage());
            ((BaseActivity) getActivity()).share(cartoon.getContent(), "我刚通过《麦块我的世界盒子》制作了一部大作，快来膜拜吧！", getString(R.string.appdownload_url), image);
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
}
