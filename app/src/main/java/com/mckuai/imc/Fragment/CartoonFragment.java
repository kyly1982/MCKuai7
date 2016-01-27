package com.mckuai.imc.Fragment;

import android.graphics.Point;
import android.os.Bundle;
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

    private SuperRecyclerView mCartoonListView;
    private View view;

    public CartoonFragment() {
        mTitleResId = R.string.fragment_cartoon;
    }

    public void setType(int type){
        if (typeIndex != type){
            typeIndex = type;
            switch (type){
                case 0:
                    if (null == mNewCartoon || mNewCartoon.isEmpty()){
                        loadData();
                    } else {
                        showData();
                    }
                    break;
                case 1:
                    if (null == mHotCartoon || mHotCartoon.isEmpty()){
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
        if (null != view){
            container.removeView(view);
        }
        view = inflater.inflate(R.layout.fragment_cartoon, container, true);
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
        manager.offsetChildrenVertical(100);
        //mAdapter = new CartoonAdapter(getActivity().getApplicationContext(),this);
        mCartoonListView.setLayoutManager(manager);
        //mCartoonListView.setAdapter(mAdapter);

    }

    private void loadData() {
        //mApplication.netEngine.loadCartoonList(getActivity(), mCartoonType[typeIndex], this);
        dataMaker();
        showData();
    }

    private void showData() {
        if (null == mAdapter) {
            mAdapter = new CartoonAdapter(getActivity(), this);
            mCartoonListView.setAdapter(mAdapter);
        }
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

    }

    @Override
    public void onSuccess(ArrayList<Cartoon> cartoons) {
        showData();
    }

    private void dataMaker() {
        ArrayList<String> image = new ArrayList<>(3);
        image.add("http://h.hiphotos.baidu.com/image/h%3D300/sign=33c2003d1ed8bc3ed90800cab28ba6c8/e7cd7b899e510fb35067c290de33c895d1430cbe.jpg");
        image.add("http://f.hiphotos.baidu.com/image/h%3D300/sign=8e8ffcee8a1001e9513c120f880e7b06/a71ea8d3fd1f4134ba49b079221f95cad1c85ebe.jpg");
        image.add("http://b.hiphotos.bdimg.com/imgad/pic/item/a71ea8d3fd1f413476aefc7d221f95cad1c85e1c.jpg");
        image.add("http://g.hiphotos.bdimg.com/imgad/pic/item/c83d70cf3bc79f3d16bc1d33bda1cd11728b2950.jpg");
        image.add("http://e.hiphotos.baidu.com/zhidao/wh%3D450%2C600/sign=ddc1e95dc3cec3fd8b6baf71e3b8f809/2e2eb9389b504fc2b1b1a494e5dde71191ef6db0.jpg");

        ArrayList<Cartoon> cartoons = new ArrayList<>(10);
        for (int i = 0; i < 10; i++) {
            Cartoon cartoon = new Cartoon(image.get(i % 5), 5, 116 * 365 * 24 * 60* 60 * 1000);
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
            comment.setTime(System.currentTimeMillis() - 1000 * 60 *30);
            comments.add(comment);
        }
        return comments;
    }
}
