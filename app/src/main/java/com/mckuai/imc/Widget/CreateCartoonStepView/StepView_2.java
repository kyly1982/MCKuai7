package com.mckuai.imc.Widget.CreateCartoonStepView;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;

import com.malinskiy.superrecyclerview.SuperRecyclerView;
import com.mckuai.imc.Adapter.WidgetAdapter;
import com.mckuai.imc.Base.MCKuai;
import com.mckuai.imc.Bean.Page;
import com.mckuai.imc.R;
import com.mckuai.imc.Util.MCNetEngine;

import java.util.ArrayList;

/**
 * Created by kyly on 2016/1/25.
 */
public class StepView_2 extends RelativeLayout implements RadioGroup.OnCheckedChangeListener,MCNetEngine.OnCharacterListResponseListener,MCNetEngine.OnToolListResponseListener,WidgetAdapter.OnItemSelectedListener {
    private Context context;
    private int widgetPageIndex = 0;
    private Page page_character;
    private Page page_tool;
    private ArrayList<Integer> characters;
    private ArrayList<Integer> tools;
    private WidgetAdapter adapter;
    private OnWidgetCheckedListener listener;

    private SuperRecyclerView widgetList;


    public interface OnWidgetCheckedListener{
        public void onWidgetChecked(int widgetId);
    }

    public StepView_2(Context context) {
        super(context);
        this.context = context;
        initView(context);
    }

    public StepView_2(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        initView(context);
    }

    public StepView_2(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context = context;
        initView(context);
    }

    public void setOnWidgetSelectedListener(OnWidgetCheckedListener listener){
        this.listener = listener;
    }

    private void initView(Context context){
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.createcartoon_step2,this,true);
        widgetList = (SuperRecyclerView) view.findViewById(R.id.createcartoon_widget);
        ((RadioGroup)view.findViewById(R.id.createcartoon_widgets)).setOnCheckedChangeListener(this);
        RecyclerView.LayoutManager manager = new StaggeredGridLayoutManager(2,StaggeredGridLayoutManager.HORIZONTAL);
        widgetList.setLayoutManager(manager);


        showData();
    }

    private void loadData(){
        switch (widgetPageIndex){
            case 0:
                MCKuai.instence.netEngine.loadCharacterList(context, page_character, this);
                break;
            case 1:
                MCKuai.instence.netEngine.loadToolList(context, page_character, this);
                break;
        }
    }

    private void showData(){
        if (null == adapter){
            adapter = new WidgetAdapter(context,this);
            widgetList.setAdapter(adapter);
        }
        switch (widgetPageIndex){
            case 0:
                if (null == characters){
                    loadData();
                }
                adapter.setData(characters);
                break;
            case 1:
                if (null == tools){
                    loadData();
                }
                adapter.setData(tools);
                break;
        }
    }

    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {
        switch (checkedId){
            case R.id.createcartoon_character:
                widgetPageIndex = 0;
                loadData();
                break;
            case R.id.createcartoon_tool:
                widgetPageIndex = 1;
                loadData();
                break;
        }
    }

    @Override
    public void onFaile(String msg) {


    }

    @Override
    public void onSuccess(ArrayList<String> data) {
        switch (widgetPageIndex){
            case 0:
                break;
            case 1:
                break;
        }
        showData();
    }

    @Override
    public void onWidgetSelected(int widgetId) {
        if (null != listener){
            listener.onWidgetChecked(widgetId);
        }
    }
}
