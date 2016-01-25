package com.mckuai.imc.Fragment;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.malinskiy.superrecyclerview.SuperRecyclerView;
import com.mckuai.imc.Adapter.ForumAdapter;
import com.mckuai.imc.Adapter.NormalPostAdapter;
import com.mckuai.imc.Base.BaseFragment;
import com.mckuai.imc.Base.MCKuai;
import com.mckuai.imc.Bean.ForumInfo;
import com.mckuai.imc.Bean.Page;
import com.mckuai.imc.Bean.Post;
import com.mckuai.imc.R;
import com.mckuai.imc.Util.MCNetEngine;

import java.util.ArrayList;


public class CommunityFragment extends BaseFragment implements View.OnClickListener,MCNetEngine.OnForumListResponseListener,MCNetEngine.OnPostListResponseListener {
    private View view;
    private Page page;
    private ArrayList<ForumInfo> mForums;
    private ArrayList<Post> mPosts;
    private String[] listType = {"lastChangeTime", "isJing", "isDing"};
    private int listTypeIndex = 0;
    private ForumAdapter forumAdapter;
    private NormalPostAdapter postAdapter;
    private MCNetEngine mNetEngine;

    private SuperRecyclerView mForumList;
    private SuperRecyclerView mPostList;
    private FloatingActionButton mCreatePost;


    public CommunityFragment() {
        mTitleResId = R.string.fragment_community;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        if (null != view){
            container.removeView(view);
        }
        view =  inflater.inflate(R.layout.fragment_community, container, false);
        if (null == mForumList){
            initView();
            mNetEngine = MCKuai.instence.netEngine;
        }

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    private void initView(){
        mForumList = (SuperRecyclerView) view.findViewById(R.id.community_forumlist);
        mPostList = (SuperRecyclerView) view.findViewById(R.id.community_showpublish);
        mCreatePost = (FloatingActionButton) view.findViewById(R.id.community_createpost);
        mCreatePost.setOnClickListener(this);
    }

    private void loadForumList(){
        mNetEngine.getForumList(getActivity(),this);
    }

    private void loadPostList(){

    }

    @Override
    public void onClick(View v) {

    }

    @Override
    public void onForumFaile(String msg) {

    }

    @Override
    public void onForumSuccess(ArrayList<ForumInfo> forums) {

    }

    @Override
    public void onPostSuccess(ArrayList<Post> posts) {

    }

    @Override
    public void onPostFaile(String msg) {

    }
}
