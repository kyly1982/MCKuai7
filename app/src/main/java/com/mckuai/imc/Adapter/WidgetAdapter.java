package com.mckuai.imc.Adapter;

import android.content.Context;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.mckuai.imc.R;

import java.util.ArrayList;

/**
 * Created by kyly on 2016/1/25.
 */
public class WidgetAdapter extends RecyclerView.Adapter<WidgetAdapter.ViewHolder> implements View.OnClickListener {
    private ArrayList<Integer> widgetIds;
    private Context context;
    private OnItemSelectedListener listener;

    public WidgetAdapter(Context context, OnItemSelectedListener listener) {
        this.context = context;
        this.listener = listener;
    }

    public interface OnItemSelectedListener{
        public void onWidgetSelected(int widgetId);
    }

    public void setData(ArrayList<Integer> widgetIds){
        this.widgetIds = widgetIds;
        notifyDataSetChanged();
    }

    public void setOnItemSececterListener(OnItemSelectedListener listener){
        this.listener = listener;
    }


    @Override
    public int getItemCount() {
        return null == widgetIds ? 0:widgetIds.size();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_widget,parent,false);
        ViewHolder holder = new ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        if (null != widgetIds && -1 < position && position < widgetIds.size()){
            holder.widget.setImageResource(widgetIds.get(position));
            if (null != listener){
                holder.widget.setTag(widgetIds.get(position));
                holder.widget.setOnClickListener(this);
            }
        }
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{
        public AppCompatImageView widget;

        public ViewHolder(View itemView) {
            super(itemView);
            widget = (AppCompatImageView) itemView.findViewById(R.id.widget);
        }
    }

    @Override
    public void onClick(View v) {
        if (v instanceof AppCompatImageView){
            int id = (int) v.getTag();
            if (0 < id) {
                listener.onWidgetSelected(id);
            }
        }
    }
}
