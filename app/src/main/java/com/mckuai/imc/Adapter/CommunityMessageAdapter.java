package com.mckuai.imc.Adapter;

import android.content.Context;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.mckuai.imc.Bean.CommunityMessage;
import com.mckuai.imc.R;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.ArrayList;

/**
 * Created by kyly on 2016/2/1.
 */
public class CommunityMessageAdapter extends RecyclerView.Adapter<CommunityMessageAdapter.ViewHolder> {
    private ArrayList<CommunityMessage> messages;
    private Context context;
    private OnItemClickListener listener;
    private ImageLoader loader;


    public interface OnItemClickListener{
        void onItemClicked(CommunityMessage message);
    }

    public CommunityMessageAdapter(Context context, OnItemClickListener listener) {
        this.context = context;
        this.listener = listener;
        this.loader = ImageLoader.getInstance();
    }

    public void setData(ArrayList<CommunityMessage> messages){
        this.messages = messages;
        notifyDataSetChanged();
    }

    public void addData(ArrayList<CommunityMessage> messages){
        if (null == this.messages){
            this.messages = messages;
            notifyDataSetChanged();
        } else {
            int position = this.messages.size();
            this.messages.addAll(messages);
            notifyItemRangeInserted(position,messages.size());
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_community_message,parent);
        ViewHolder holder = new ViewHolder(view);
        if (null != listener){
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    CommunityMessage message = (CommunityMessage) v.getTag();
                    if (null != message){
                        listener.onItemClicked(message);
                    }
                }
            });
        }
        return holder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        if (null != messages && -1 < position && position < messages.size()){
            CommunityMessage message = messages.get(position);
            if (null != message){
                holder.itemView.setTag(message);
                switch (message.getTypeEx()){
                    case CommunityMessage.TYPE_REPLY:
                        showReplyMessage(holder,message);
                        break;
                    case CommunityMessage.TYPE_AT:
                        showAtMessage(holder,message);
                        break;
                    case CommunityMessage.TYPE_SYSTEM:
                        showSystemMessage(holder,message);
                        break;
                }
            }
        }
    }

    @Override
    public int getItemCount() {
        return null == messages ? 0:messages.size();
    }

    private void showReplyMessage(ViewHolder holder,CommunityMessage message){
        holder.layout_sysmsg.setVisibility(View.GONE);
        holder.tag_at.setVisibility(View.GONE);
        holder.layout_other.setVisibility(View.VISIBLE);
        loader.displayImage(message.getHeadImg(), holder.cover);
        holder.username.setText(message.getUserName());
        holder.time.setText(message.getInsertTimeEx());
        holder.title.setText(message.getTalkTitle());
        holder.content.setText(message.getCont());
    }

    private void showAtMessage(ViewHolder holder,CommunityMessage message){
        holder.layout_sysmsg.setVisibility(View.GONE);
        holder.tag_at.setVisibility(View.VISIBLE);
        holder.layout_other.setVisibility(View.VISIBLE);
        loader.displayImage(message.getHeadImg(), holder.cover);
        holder.username.setText(message.getUserName());
        holder.time.setText(message.getInsertTimeEx());
        holder.content_at.setText(message.getTalkTitle());
    }

    private void showSystemMessage(ViewHolder holder,CommunityMessage message){
        holder.layout_other.setVisibility(View.GONE);
        holder.tag_at.setVisibility(View.GONE);
        holder.layout_sysmsg.setVisibility(View.VISIBLE);
        holder.cover.setBackgroundResource(R.mipmap.ic_usercenter_community_sysmsg);
        holder.username.setText(message.getUserName());
        holder.time.setText(message.getInsertTimeEx());
        holder.content_at.setText(message.getShowText());
    }


    public static class ViewHolder extends RecyclerView.ViewHolder{
        public AppCompatImageView cover;
        public AppCompatTextView username;
        public AppCompatTextView time;
        public AppCompatTextView title;
        public AppCompatTextView content;
        public AppCompatTextView content_at;
        public AppCompatTextView tag_at;
        public RelativeLayout layout_sysmsg;
        public RelativeLayout layout_other;

       public ViewHolder(View itemView) {
           super(itemView);
           cover = (AppCompatImageView) itemView.findViewById(R.id.usercover);
           username = (AppCompatTextView) itemView.findViewById(R.id.username);
           time = (AppCompatTextView) itemView.findViewById(R.id.username);
           title = (AppCompatTextView) itemView.findViewById(R.id.username);
           content = (AppCompatTextView) itemView.findViewById(R.id.username);
           content_at = (AppCompatTextView) itemView.findViewById(R.id.username);
           tag_at = (AppCompatTextView) itemView.findViewById(R.id.tag_at);
           layout_other = (RelativeLayout) itemView.findViewById(R.id.message_other);
           layout_sysmsg = (RelativeLayout) itemView.findViewById(R.id.message_system);
       }
   }

}
