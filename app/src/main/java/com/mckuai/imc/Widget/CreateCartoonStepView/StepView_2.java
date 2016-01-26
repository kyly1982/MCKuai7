package com.mckuai.imc.Widget.CreateCartoonStepView;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;

import com.malinskiy.superrecyclerview.SuperRecyclerView;
import com.mckuai.imc.Adapter.WidgetAdapter;
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

    public StepView_2(Context context, OnWidgetCheckedListener listener) {
        super(context);
        this.context = context;
        if (null != listener){
            this.listener = listener;
        }
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
        loadData();
    }

    private void loadData(){
       /* switch (widgetPageIndex){
            case 0:
                MCKuai.instence.netEngine.loadCharacterList(context, page_character, this);
                break;
            case 1:
                MCKuai.instence.netEngine.loadToolList(context, page_character, this);
                break;
        }*/
        switch (widgetPageIndex){
            case 0:
                loadCharacter();
                showData();
                break;
            case 1:
                loadTools();
                showData();
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

    private void loadTools(){
        //直接读取本地的数据
        Integer[] wepoan = {R.mipmap.ic_weapon_bow_1,
                R.mipmap.ic_weapon_bow_2,
                R.mipmap.ic_weapon_bow_3,
                R.mipmap.ic_weapon_bow_4,
                R.mipmap.ic_weapon_sword_1,
                R.mipmap.ic_weapon_sword_2,
                R.mipmap.ic_weapon_sword_3,
                R.mipmap.ic_weapon_sword_4,
                R.mipmap.ic_weapon_sword_5,
                R.mipmap.ic_wepaon_arrow,
                R.mipmap.ic_wepaon_quiver };
        Integer[] armor = {R.mipmap.ic_armor_boots_1,
                R.mipmap.ic_armor_boots_2,
                R.mipmap.ic_armor_boots_3,
                R.mipmap.ic_armor_boots_4,
                R.mipmap.ic_armor_boots_5,
                R.mipmap.ic_armor_chestplate_1,
                R.mipmap.ic_armor_chestplate_2,
                R.mipmap.ic_armor_chestplate_3,
                R.mipmap.ic_armor_chestplate_4,
                R.mipmap.ic_armor_chestplate_5,
                R.mipmap.ic_armor_helmet_2,
                R.mipmap.ic_armor_helmet_3,
                R.mipmap.ic_armor_helmet_4,
                R.mipmap.ic_armor_helmet_5,
                R.mipmap.ic_armor_horse_1,
                R.mipmap.ic_armor_horse_2,
                R.mipmap.ic_armor_horse_3,
                R.mipmap.ic_armor_leggings_2,
                R.mipmap.ic_armor_leggings_3,
                R.mipmap.ic_armor_leggings_4,
                R.mipmap.ic_armor_leggings_5
        };
        Integer[] collection = {R.mipmap.ic_collection_axe_1,
                R.mipmap.ic_collection_axe_2,
                R.mipmap.ic_collection_axe_3,
                R.mipmap.ic_collection_axe_4,
                R.mipmap.ic_collection_axe_5,
                R.mipmap.ic_collection_fishingrod_1,
                R.mipmap.ic_collection_fishingrod_2,
                R.mipmap.ic_collection_fishingrod_3,
                R.mipmap.ic_collection_hoe_1,
                R.mipmap.ic_collection_hoe_2,
                R.mipmap.ic_collection_hoe_3,
                R.mipmap.ic_collection_hoe_4,
                R.mipmap.ic_collection_hoe_5,
                R.mipmap.ic_collection_pickaxe_1,
                R.mipmap.ic_collection_pickaxe_2,
                R.mipmap.ic_collection_pickaxe_3,
                R.mipmap.ic_collection_pickaxe_4,
                R.mipmap.ic_collection_pickaxe_5,
                R.mipmap.ic_collection_shears,
                R.mipmap.ic_collection_shovel_1,
                R.mipmap.ic_collection_shovel_2,
                R.mipmap.ic_collection_shovel_3,
                R.mipmap.ic_collection_shovel_4,
                R.mipmap.ic_collection_shovel_5};
        tools = new ArrayList<>(57);

        for (Integer id:wepoan){
            tools.add(id);
        }

        for (Integer id:armor){
            tools.add(id);
        }

        for (Integer id:collection){
            tools.add(id);
        }
    }

    private void loadCharacter(){
        Integer[] character = {R.mipmap.ic_character_bat,
                R.mipmap.ic_character_cat,
                R.mipmap.ic_character_chicken_1,
                R.mipmap.ic_character_chicken_2,
                R.mipmap.ic_character_cow_1,
                R.mipmap.ic_character_cow_2,
                R.mipmap.ic_character_cow_3,
                R.mipmap.ic_character_cow_4,
                R.mipmap.ic_character_dragon,
                R.mipmap.ic_character_horse_1,
                R.mipmap.ic_character_horse_2,
                R.mipmap.ic_character_horse_3,
                R.mipmap.ic_character_horse_4,
                R.mipmap.ic_character_horse_5,
                R.mipmap.ic_character_ocelot,
                R.mipmap.ic_character_pig,
                R.mipmap.ic_character_pig_2,
                R.mipmap.ic_character_pig_3,
                R.mipmap.ic_character_player_1,
                R.mipmap.ic_character_player_2,
                R.mipmap.ic_character_rabbit_1,
                R.mipmap.ic_character_rabbit_2,
                R.mipmap.ic_character_rabbit_3,
                R.mipmap.ic_character_rabbit_4,
                R.mipmap.ic_character_rabbit_5,
                R.mipmap.ic_character_rabbit_6,
                R.mipmap.ic_character_rabbit_7,
                R.mipmap.ic_character_sheep_1,
                R.mipmap.ic_character_sheep_2,
                R.mipmap.ic_character_sheep_3,
                R.mipmap.ic_character_spider_1,
                R.mipmap.ic_character_spider_2,
                R.mipmap.ic_character_spider_3,
                R.mipmap.ic_character_villager_1,
                R.mipmap.ic_character_villager_2,
                R.mipmap.ic_character_villager_3,
                R.mipmap.ic_character_witch,
                R.mipmap.ic_character_withered,
                R.mipmap.ic_character_wolf_1,
                R.mipmap.ic_character_wolf_2,
                R.mipmap.ic_character_wolf_3,
                R.mipmap.ic_character_wolf_4,
                R.mipmap.ic_character_wolf_5,
                R.mipmap.ic_character_zombie_1,
                R.mipmap.ic_character_zombie_10,
                R.mipmap.ic_character_zombie_11,
                R.mipmap.ic_character_zombie_12,
                R.mipmap.ic_character_zombie_13,
                R.mipmap.ic_character_zombie_14,
                R.mipmap.ic_character_zombie_15,
                R.mipmap.ic_character_zombie_16,
                R.mipmap.ic_character_zombie_17,
                R.mipmap.ic_character_zombie_18,
                R.mipmap.ic_character_zombie_19,
                R.mipmap.ic_character_zombie_2,
                R.mipmap.ic_character_zombie_20,
                R.mipmap.ic_character_zombie_21,
                R.mipmap.ic_character_zombie_22,
                R.mipmap.ic_character_zombie_23,
                R.mipmap.ic_character_zombie_24,
                R.mipmap.ic_character_zombie_3,
                R.mipmap.ic_character_zombie_4,
                R.mipmap.ic_character_zombie_5,
                R.mipmap.ic_character_zombie_6,
                R.mipmap.ic_character_zombie_7,
                R.mipmap.ic_character_zombie_8,
                R.mipmap.ic_character_zombie_9,
        };

        characters = new ArrayList<>(86);
        for (Integer id:character){
            characters.add(id);
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
