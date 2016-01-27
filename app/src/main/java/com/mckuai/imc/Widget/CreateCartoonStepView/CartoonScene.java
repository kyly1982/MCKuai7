package com.mckuai.imc.Widget.CreateCartoonStepView;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.widget.RelativeLayout;

import com.malinskiy.superrecyclerview.SuperRecyclerView;
import com.mckuai.imc.Adapter.CartoonSceneAdapter;
import com.mckuai.imc.R;

import java.util.ArrayList;

/**
 * Created by kyly on 2016/1/26.
 */
public class CartoonScene extends RelativeLayout implements CartoonSceneAdapter.OnSceneSelectedListener {
    private Context context;
    private ArrayList<Object> scenes;
    private CartoonSceneAdapter adapter;
    private OnSceneSelectedListener listener;


    private SuperRecyclerView sceneList;

    public interface OnSceneSelectedListener{
        public void onSelected(Object scene);
    }

    public CartoonScene(Context context,OnSceneSelectedListener listener,boolean isMCScene) {
        super(context);
        this.context = context;
        this.listener = listener;
        initView(context);
        if (isMCScene){
            loadSceneIds();
            showData();
        }
    }

    public void setData(ArrayList<Object> scenes){
        this.scenes = scenes;
        showData();
    }


    private void initView(Context context){
        inflate(context, R.layout.createcartoon_scene,this);
        sceneList = (SuperRecyclerView) findViewById(R.id.createcartoon_scenelist);
        RecyclerView.LayoutManager manager = new StaggeredGridLayoutManager(3,StaggeredGridLayoutManager.VERTICAL);
        manager.offsetChildrenHorizontal(30);
        manager.offsetChildrenVertical(30);
        sceneList.setLayoutManager(manager);
    }


    private void loadSceneIds(){
        scenes = new ArrayList<Object>(20);
        Integer[] backgrounds = {R.mipmap.cartoon_bg_black,
                R.mipmap.cartoon_bg_blue,
                R.mipmap.cartoon_bg_farm,
                R.mipmap.cartoon_bg_green,
                R.mipmap.cartoon_bg_lava,
                R.mipmap.cartoon_bg_leak,
                R.mipmap.cartoon_bg_oasis,
                R.mipmap.cartoon_bg_orage,
                R.mipmap.cartoon_bg_purple,
                R.mipmap.cartoon_bg_red,
                R.mipmap.cartoon_bg_river,
                R.mipmap.cartoon_bg_sand,
                R.mipmap.cartoon_bg_skyblue,
                R.mipmap.cartoon_bg_sunrise,
                R.mipmap.cartoon_bg_sunset,
                R.mipmap.cartoon_bg_warm,
                R.mipmap.cartoon_bg_wild,
                R.mipmap.cartoon_bg_yellow,
                R.mipmap.cartoon_bg_young};

        for (Integer id:backgrounds){

            if (0 != id){
                scenes.add(id);
            }

        }

    }

    private void showData(){
        if (null == adapter){
            adapter = new CartoonSceneAdapter(context,this);
            sceneList.setAdapter(adapter);
        }
        adapter.setData(scenes);
    }

    @Override
    public void onSceneSelected(Object scene) {
        if (null != listener){
            listener.onSelected(scene);
        }
    }
}
