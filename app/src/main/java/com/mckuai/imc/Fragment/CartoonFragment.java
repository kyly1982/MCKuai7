package com.mckuai.imc.Fragment;

import android.graphics.Point;
import android.os.Bundle;
import android.support.v7.widget.AppCompatImageButton;
import android.support.v7.widget.AppCompatRadioButton;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioGroup;

import com.malinskiy.superrecyclerview.SuperRecyclerView;
import com.mckuai.imc.Adapter.CartoonAdapter;
import com.mckuai.imc.Base.BaseActivity;
import com.mckuai.imc.Base.BaseFragment;
import com.mckuai.imc.Base.MCKuai;
import com.mckuai.imc.Bean.Cartoon;
import com.mckuai.imc.Bean.Comment;
import com.mckuai.imc.Bean.Lable;
import com.mckuai.imc.Bean.Page;
import com.mckuai.imc.Bean.User;
import com.mckuai.imc.R;
import com.mckuai.imc.Util.MCNetEngine;

import java.util.ArrayList;

/**
 * A placeholder fragment containing a simple view.
 */
public class CartoonFragment extends BaseFragment implements RadioGroup.OnCheckedChangeListener, View.OnClickListener, MCNetEngine.OnCartoonListResponseListener {
    private String[] mCartoonType;
    private ArrayList<Cartoon> mHotCartoon;
    private ArrayList<Cartoon> mNewCartoon;
    private CartoonAdapter mAdapter;
    private int typeIndex = 0;
    private MCKuai mApplication = MCKuai.instence;


    private View mView;
    private SuperRecyclerView mCartoonListView;
    private RadioGroup mType;
    private AppCompatRadioButton mNew;
    private AppCompatImageButton mNav;


    public CartoonFragment() {
        mTitleResId = R.string.fragment_cartoon;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (null != mView){
            container.removeView(mView);
        }
        mView = inflater.inflate(R.layout.fragment_cartoon, container, false);
        return mView;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (null != getActivity() && null == mCartoonListView) {
            initView();
        }
    }

    private void initView() {
        mCartoonType = getResources().getStringArray(R.array.cartoon_ordertype);

        mCartoonListView = (SuperRecyclerView) mView.findViewById(R.id.cartoonlist);
        mType = (RadioGroup) mView.findViewById(R.id.cartoon_type);
        //mHot = (AppCompatRadioButton) mView.findViewById(R.id.cartoon_type_hot);
        mNew = (AppCompatRadioButton) mView.findViewById(R.id.cartoon_type_new);
        mNav = (AppCompatImageButton) mView.findViewById(R.id.cartoon_toolbar_nav);

        RecyclerView.LayoutManager manager = new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false);
        manager.offsetChildrenVertical(100);
        //mAdapter = new CartoonAdapter(getActivity().getApplicationContext(),this);
        mCartoonListView.setLayoutManager(manager);
        //mCartoonListView.setAdapter(mAdapter);

        mType.setOnCheckedChangeListener(this);
        mNav.setOnClickListener(this);
        mNew.setChecked(true);
    }

    private void loadData() {
        mApplication.netEngine.loadCartoonList(getActivity(), mCartoonType[typeIndex], this);
    }

    private void showData() {
        if (null == mAdapter) {
            mAdapter = new CartoonAdapter(getActivity(), this);
            mCartoonListView.setAdapter(mAdapter);
        }
        switch (typeIndex) {
            case 0:
                mAdapter.setData(mNewCartoon);
                break;
            case 1:
                mAdapter.setData(mHotCartoon);
                break;
        }
    }

    /*点击类型切换的回调*/
    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {
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
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.cartoon_toolbar_nav://导航栏
                ((BaseActivity) getActivity()).showMessage("导航栏", null, null);
                break;
            case R.id.cartoon_usercover://头像
                ((BaseActivity) getActivity()).showMessage("头像", null, null);
                break;
            case R.id.cartoon_shar://分享
                ((BaseActivity) getActivity()).showMessage("分享", null, null);
                break;
            case R.id.cartoon_comment://评论
                ((BaseActivity) getActivity()).showMessage("评论", null, null);
                break;
            case R.id.cartoon_prise://赞
                ((BaseActivity) getActivity()).showMessage("赞", null, null);
                break;
        }
    }

    /*加载数据回调*/
    @Override
    public void onFaile(String msg) {
        dataMaker();
        showData();
    }

    @Override
    public void onSuccess(ArrayList<Cartoon> cartoons) {
        showData();
    }

    private void dataMaker() {
        ArrayList<Cartoon> cartoons = new ArrayList<>(10);
        for (int i = 0; i < 10; i++) {
            Cartoon cartoon = new Cartoon("http://e.hiphotos.baidu.com/zhidao/wh%3D450%2C600/sign=ddc1e95dc3cec3fd8b6baf71e3b8f809/2e2eb9389b504fc2b1b1a494e5dde71191ef6db0.jpg", 5, 116 * 365 * 24 * 60 * 1000);
            cartoon.setCommentPage(pageMaker());
            cartoon.setLables(lableMaker());
            cartoon.setOwner(ownerMaker());
            cartoon.setComments(commentMaker());
            cartoons.add(cartoon);
        }
        if (0 == typeIndex) {
            mNewCartoon = cartoons;
        } else {
            mHotCartoon = cartoons;
        }
    }

    private Page pageMaker() {
        Page page = new Page(10, 1, 20);
        return page;
    }

    private ArrayList<Lable> lableMaker() {
        ArrayList<Lable> lables = new ArrayList<>(2);
        for (int i = 0; i < 2; i++) {
            Point point = new Point(10 * (i + 1), 20 * (i + 1));
            Lable lable = new Lable(point, "这是一个测试" + i);
            lables.add(lable);
        }
        return lables;
    }

    private User ownerMaker() {
        User user = new User((long) 5, 2, 0.9f, "name", "nick", "http://img4.duitang.com/uploads/item/201509/04/20150904232025_LC2w4.thumb.224_0.jpeg", false);
        return user;
    }

    private ArrayList<Comment> commentMaker() {
        ArrayList<Comment> comments = new ArrayList<>(5);
        for (int i = 0; i < 5; i++) {
            Comment comment = new Comment(ownerMaker(), "哈哈哈" + i);
            comments.add(comment);
        }
        return comments;
    }
}
