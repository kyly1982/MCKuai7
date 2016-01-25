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
import com.mckuai.imc.Bean.ForumInfo;
import com.mckuai.imc.Bean.Page;
import com.mckuai.imc.Bean.Post;
import com.mckuai.imc.R;

import java.util.ArrayList;


public class CommunityFragment extends BaseFragment {
    private View view;
    private Page page;
    private ArrayList<ForumInfo> mForums;
    private ArrayList<Post> mPosts;
    private String[] listType = {"lastChangeTime", "isJing", "isDing"};
    private int listTypeIndex = 0;
    private ForumAdapter forumAdapter;
    private NormalPostAdapter postAdapter;

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
        }
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    private void initView(){
    }
}
