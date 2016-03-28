package com.mckuai.imc.Adapter;

import android.content.Context;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.mckuai.imc.Bean.Theme;
import com.mckuai.imc.R;

import java.util.ArrayList;

/**
 * Created by kyly on 2016/3/28.
 */
public class ThemeAdapter extends RecyclerView.Adapter<ThemeAdapter.ViewHolder>{
    private Context context;
    private ArrayList<Theme> themes;
    private OnItemClickListener listener;

    public ThemeAdapter(Context context, OnItemClickListener listener) {
        this.context = context;
        this.listener = listener;
    }

    public interface OnItemClickListener{
        void onItemClicked(Theme theme);
    }

    public void setData(ArrayList<Theme> themes,boolean isRefresh){
        if (isRefresh){
            this.themes = themes;
            notifyDataSetChanged();
        } else if (null != themes && themes.size() > 0){
            this.themes.addAll(themes);
            notifyItemRangeChanged(this.themes.size() - themes.size(),themes.size());
        }
    }

    @Override
    public int getItemCount() {
        return null == themes ? 0:themes.size();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_theme,parent,false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        if (null != themes && -1 < position && position < themes.size()){
            final Theme theme = themes.get(position);
            holder.name.setText(theme.getName());
            if (null != listener) {
                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        listener.onItemClicked(theme);
                    }
                });
            }
        }
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        private AppCompatTextView name;

        public ViewHolder(View itemView) {
            super(itemView);
            name = (AppCompatTextView) itemView.findViewById(R.id.themename);
        }
    }

}
