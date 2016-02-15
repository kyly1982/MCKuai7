package com.mckuai.imc.Fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioGroup;

import com.malinskiy.superrecyclerview.OnMoreListener;
import com.malinskiy.superrecyclerview.SuperRecyclerView;
import com.mckuai.imc.Activity.PostActivity;
import com.mckuai.imc.Activity.PublishPostActivity;
import com.mckuai.imc.Activity.UserCenterActivity;
import com.mckuai.imc.Adapter.ForumAdapter;
import com.mckuai.imc.Adapter.PostAdapter;
import com.mckuai.imc.Base.BaseFragment;
import com.mckuai.imc.Base.MCKuai;
import com.mckuai.imc.Bean.ForumInfo;
import com.mckuai.imc.Bean.Page;
import com.mckuai.imc.Bean.Post;
import com.mckuai.imc.Bean.User;
import com.mckuai.imc.R;
import com.mckuai.imc.Util.MCNetEngine;

import java.util.ArrayList;


public class MainFragment_Community extends BaseFragment
        implements View.OnClickListener,MCNetEngine.OnForumListResponseListener,MCNetEngine.OnPostListResponseListener,ForumAdapter.OnItemClickListener,PostAdapter.OnItemClickListener,RadioGroup.OnCheckedChangeListener {
    private View view;
    private Page page;
    private ArrayList<ForumInfo> mForums;
    private ArrayList<Post> mPosts;
    private String[] postType = {"lastChangeTime", "isJing", "isDing"};
    private int typeindex = 0;
    private ForumAdapter forumAdapter;
    private PostAdapter postAdapter;
    private MCNetEngine mNetEngine;
    private int currentForumIndex = 0;

    private SuperRecyclerView mForumList;
    private SuperRecyclerView mPostList;
    private FloatingActionButton mCreatePost;
    private RadioGroup postGroup;



    public MainFragment_Community() {
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
        view =  inflater.inflate(R.layout.fragment_main_community, container, false);
        if (null == mForumList){
            initView();
            mNetEngine = MCKuai.instence.netEngine;
        }

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        showForum();
    }

    private void initView(){
        mForumList = (SuperRecyclerView) view.findViewById(R.id.community_forumlist);
        mPostList = (SuperRecyclerView) view.findViewById(R.id.community_postlist);
        mCreatePost = (FloatingActionButton) view.findViewById(R.id.community_createpost);
        postGroup = (RadioGroup) view.findViewById(R.id.posttype_indicator);


        LinearLayoutManager manager = new LinearLayoutManager(getActivity(),LinearLayoutManager.HORIZONTAL,false);
        mForumList.setLayoutManager(manager);
        LinearLayoutManager manager1  = new LinearLayoutManager(getActivity(),LinearLayoutManager.VERTICAL,false);
        mPostList.setLayoutManager(manager1);
        mForumList.setRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                loadForumList();
            }
        });

        mPostList.setRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {

            }
        });

        mPostList.setupMoreListener(new OnMoreListener() {
            @Override
            public void onMoreAsked(int overallItemsCount, int itemsBeforeMore, int maxLastVisiblePosition) {
                loadPostList();
            }
        },1);

        postGroup.setOnCheckedChangeListener(this);
        mCreatePost.setOnClickListener(this);
    }

    private void loadForumList(){
        mNetEngine.loadFroumList(getActivity(), this);
    }

    private void loadPostList(){
        mNetEngine.loadPostList(getActivity(), mForums.get(currentForumIndex).getId(), postType[typeindex], page.getNextPage(), this);
    }

    private void showForum(){
        if (null == mForums){
            loadForumList();
        } else {
            if (null == forumAdapter) {
                forumAdapter = new ForumAdapter(getActivity(), this);
                mForumList.setAdapter(forumAdapter);
            }
            forumAdapter.setData(mForums);
        }
    }

    private void showPost(){
        if (null == mPosts){
            loadPostList();
        } else {
            if (null == postAdapter){
                postAdapter = new PostAdapter(getActivity(),this);
                mPostList.setAdapter(postAdapter);
            }
            postAdapter.setData(mPosts);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.community_createpost:
                Intent intent = new Intent(getActivity(), PublishPostActivity.class);
                Bundle bundle = new Bundle();
                bundle.putSerializable("FORUM_LIST", mForums);
                intent.putExtras(bundle);
                getActivity().startActivity(intent);
                break;
        }
    }

    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {
        switch (checkedId){
            case R.id.lastpost:
                typeindex = 0;
                break;
            case R.id.essencepost:
                typeindex = 1;
                break;
            case R.id.toppost:
                typeindex = 2;
                break;
        }
        page.setPage(0);
        loadPostList();
    }

    @Override
    public void onLoadForumListFailure(String msg) {

    }

    @Override
    public void onLoadForumListSuccess(ArrayList<ForumInfo> forums) {
        if (null == page){
            this.mForums = forums;
            page = new Page(0,0,20);
            mCreatePost.setVisibility(View.VISIBLE);
            showForum();
            showPost();
        }
    }

    @Override
    public void onLoadPostListSuccess(ArrayList<Post> posts,Page page) {
        this.page = page;
        if (1 == page.getPage()){
            mPosts = posts;
        } else {
            mPosts.addAll(posts);
        }
        showPost();
    }

    @Override
    public void onLoadPostListFailure(String msg) {

    }


    @Override
    public void onItemClicked(int forumPosition) {
        currentForumIndex = forumPosition;
        forumAdapter.notifyDataSetChanged();
        page.setPage(0);
        loadPostList();
    }

    @Override
    public void onItemClicked(Post post) {
        Intent intent = new Intent(getActivity(), PostActivity.class);
        Bundle bundle = new Bundle();
        bundle.putSerializable(getString(R.string.tag_post),post);
        intent.putExtras(bundle);
        startActivity(intent);
    }

    @Override
    public void onUserClicked(User user) {
        Intent intent = new Intent(getActivity(), UserCenterActivity.class);
        intent.putExtra(getString(R.string.usercenter_tag_userid),user.getId().intValue());
        startActivity(intent);
    }
}
