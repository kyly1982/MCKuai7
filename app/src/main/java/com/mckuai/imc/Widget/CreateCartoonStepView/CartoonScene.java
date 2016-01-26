package com.mckuai.imc.Widget.CreateCartoonStepView;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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
public class CartoonScene extends RelativeLayout implements CartoonSceneAdapter.OnItemSelectedListener {
    private Context context;
    private ArrayList<Bitmap> scenes;
    private CartoonSceneAdapter adapter;


    private SuperRecyclerView sceneList;

    public interface OnSceneSelectedListener{
        public void onSelected(Bitmap scene);
    }

    public CartoonScene(Context context,boolean isMCScene) {
        super(context);
        this.context = context;
        initView(context);
        if (isMCScene){
            loadSceneIds();
            showData();
        }
    }

    public void setData(ArrayList<Bitmap> scenes){
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
        scenes = new ArrayList<>(20);
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
            Bitmap bitmap = null;
            try {
                bitmap= BitmapFactory.decodeResource(getResources(),id);
            } catch (Exception  e){
                e.printStackTrace();
            }
            if (null != bitmap){
                scenes.add(bitmap);
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
    public void onItemSelected(Bitmap scene) {

    }
}
