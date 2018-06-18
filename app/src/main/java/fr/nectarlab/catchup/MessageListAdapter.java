package fr.nectarlab.catchup;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.List;

import fr.nectarlab.catchup.Database.Message;

/**
 * Created by ThomasBene on 5/28/2018.
 */

public class MessageListAdapter extends RecyclerView.Adapter<MessageListAdapter.MessageViewHolder> {
    private final String TAG = "MediaListAdapter";
    private List<Message> mMessage;
    private final LayoutInflater mInflater;
    Context context;



    class MessageViewHolder extends RecyclerView.ViewHolder {
        LinearLayout main;
        TextView textView, username, timeStamp;
        private MessageViewHolder (View itemView){
            super(itemView);
            this.textView=itemView.findViewById(R.id.MessageItem_textView_tv);
            this.main = itemView.findViewById(R.id.message_mainContainer_layout);
            this.username = itemView.findViewById(R.id.MessageItem_username_tv);
            this.timeStamp = itemView.findViewById(R.id.MessageItem_time_tv);
        }
    }


    MessageListAdapter(Context context){
        this.mInflater = LayoutInflater.from(context);
    }

    @Override
    public MessageListAdapter.MessageViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        final View view = mInflater.inflate(R.layout.message_recyclerview_item, parent, false);
        context = mInflater.getContext();
        return new MessageListAdapter.MessageViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MessageListAdapter.MessageViewHolder holder, int position) {
        if(mMessage!=null){
            final Message current = mMessage.get(position);
            holder.textView.setText(current.getContenu());
            holder.username.setText(current.getRef_user_EMAIL());
            holder.timeStamp.setText(current.getTimeStamp());
            if(!Insights.whoIsSending(current)){
                /*
                 * Simple test pour voir si la textView s'adapte aux differents users
                 * fonctionne pour la textView
                 */
                holder.main.setBackgroundColor(R.color.expandingFab);
            }
        }
    }

    @Override
    public void onViewDetachedFromWindow(MessageListAdapter.MessageViewHolder holder){
        //testing
        holder.textView.setVisibility(View.GONE);
    }

    @Override
    public void onViewAttachedToWindow(MessageListAdapter.MessageViewHolder holder){
        //testing
        holder.textView.setVisibility(View.VISIBLE);
    }

    @Override
    public int getItemCount() {
        if(mMessage!=null){
            Log.i(TAG, "getItemCount()" +mMessage.size());
            return mMessage.size();

        }
        else{
            return 0;
        }
    }
    public void setMessages(List<Message>listedMessages){
        this.mMessage=listedMessages;
        notifyDataSetChanged();
    }
}

