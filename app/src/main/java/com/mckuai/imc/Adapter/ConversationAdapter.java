package com.mckuai.imc.Adapter;

import android.content.Context;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.mckuai.imc.Base.MCKuai;
import com.mckuai.imc.R;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.ArrayList;

import io.rong.imlib.model.Conversation;
import io.rong.imlib.model.MessageContent;

/**
 * Created by kyly on 2016/2/2.
 */
public class ConversationAdapter extends RecyclerView.Adapter<ConversationAdapter.ViewHolder> {
    private Context context;
    private ImageLoader loader;
    private ArrayList<Conversation> conversations;
    private OnItemClickListener listener;

    public ConversationAdapter(Context context, OnItemClickListener listener) {
        this.context = context;
        this.listener = listener;
        loader = ImageLoader.getInstance();
    }

    public void setData(ArrayList<Conversation> conversations){
        this.conversations = conversations;
        notifyDataSetChanged();
    }

    public interface OnItemClickListener{
        void onItemClicked(Conversation conversation);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_conversation,parent,false);
        ViewHolder holder = new ViewHolder(view);
        return holder;
    }


    /*NONE(0, "none"),
    PRIVATE(1, "private"),
    DISCUSSION(2, "discussion"),
    GROUP(3, "group"),
    CHATROOM(4, "chatroom"),
    CUSTOMER_SERVICE(5, "customer_service"),
    SYSTEM(6, "system"),
    APP_PUBLIC_SERVICE(7, "app_public_service"),
    PUBLIC_SERVICE(8, "public_service"),
    PUSH_SERVICE(9, "push_service");*/

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        if (null != conversations && -1 < position && position < conversations.size()){
            Conversation conversation = conversations.get(position);
            MessageContent message = conversation.getLatestMessage();

            if (null != conversation && conversation.getConversationType() == Conversation.ConversationType.PRIVATE){
                loader.displayImage(conversation.getPortraitUrl(), holder.usercover);
                if (conversation.getSenderUserId().equalsIgnoreCase(MCKuai.instence.user.getName())) {
                    holder.username.setText(conversation.getTargetId());
                } else {
                    holder.username.setText(conversation.getSenderUserName());
                }
                String name = conversation.getObjectName();
                holder.time.setText(conversation.getSentTime() + "");
            }
        }
    }

    @Override
    public int getItemCount() {
        return null == conversations ? 0:conversations.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{
        public AppCompatImageView usercover;
        public AppCompatTextView username;
        public AppCompatTextView time;
        public AppCompatTextView lastmessage;

        public ViewHolder(View itemView) {
            super(itemView);
            usercover = (AppCompatImageView) itemView.findViewById(R.id.usercover);
            username = (AppCompatTextView) itemView.findViewById(R.id.username);
            time = (AppCompatTextView) itemView.findViewById(R.id.time);
            lastmessage = (AppCompatTextView) itemView.findViewById(R.id.lastmessage);
        }
    }
}
