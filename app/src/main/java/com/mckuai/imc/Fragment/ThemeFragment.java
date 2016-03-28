package com.mckuai.imc.Fragment;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.malinskiy.superrecyclerview.OnMoreListener;
import com.malinskiy.superrecyclerview.SuperRecyclerView;
import com.mckuai.imc.Adapter.ThemeAdapter;
import com.mckuai.imc.Base.BaseFragment;
import com.mckuai.imc.Base.MCKuai;
import com.mckuai.imc.Bean.Theme;
import com.mckuai.imc.R;
import com.mckuai.imc.Util.MCNetEngine;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 */
public class ThemeFragment extends BaseFragment implements OnMoreListener,SwipeRefreshLayout.OnRefreshListener,ThemeAdapter.OnItemClickListener,MCNetEngine.OnGetThemeListListener {
    private View view;
    private SuperRecyclerView list;
    private ArrayList<Theme> themes;
    private ThemeAdapter adapter;
    private OnThemeSelectedListener listener;


    public ThemeFragment() {
        // Required empty public constructor
    }

    public void setOnThemeSelectedListener(OnThemeSelectedListener listener){
        this.listener = listener;
    }

    public interface OnThemeSelectedListener{
        void onThemeSelected(Theme theme);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (null == view) {
            view = inflater.inflate(R.layout.fragment_theme, container, false);
            initView();
        }
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        initView();
        if (!isHidden()) {
            if (null == themes) {
                loadData();
            } else {
                showData();
            }
        }
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (!hidden){
            if (null == themes){
                loadData();
            } else {
                showData();
            }
        }
    }

    private void initView(){
        if (null != view && null == list) {
            list = (SuperRecyclerView) view.findViewById(R.id.themelist);
            //list.setOnMoreListener(this);
            list.setRefreshListener(this);
            RecyclerView.LayoutManager manager = new LinearLayoutManager(getActivity(),LinearLayoutManager.VERTICAL,false);
            list.setLayoutManager(manager);
        }
    }

    private void loadData(){
        MCKuai.instence.netEngine.getThemeList(getActivity(),this);
    }

    private void showData(){
        if (null != list) {
            if (null == adapter) {
                adapter = new ThemeAdapter(getActivity(), this);
                list.setAdapter(adapter);
            }
            adapter.setData(themes, true);
        }
    }

    @Override
    public void onMoreAsked(int overallItemsCount, int itemsBeforeMore, int maxLastVisiblePosition) {

    }

    @Override
    public void onRefresh() {
        loadData();
    }

    @Override
    public void onItemClicked(Theme theme) {
        if (null != listener){
            listener.onThemeSelected(theme);
        }
    }

    @Override
    public void onGetThemeListFailure(String msg) {
        showMessage(msg,null,null);
    }

    @Override
    public void onGetThemeListSuccess(ArrayList<Theme> themes) {
        //this.themes = themes;
        this.themes = new ArrayList<>(10);
        for (int i = 0;i < 10;i++){
            Theme theme = new Theme();
            theme.setId(i);
            theme.setName("这是第"+i+"个主题");
            this.themes.add(theme);
        }
        showData();
    }
}
