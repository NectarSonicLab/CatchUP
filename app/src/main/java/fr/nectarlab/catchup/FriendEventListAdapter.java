package fr.nectarlab.catchup;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import fr.nectarlab.catchup.Database.Event_Friend_AssocDB;

/**
 * Created by ThomasBene on 6/13/2018.
 */

public class FriendEventListAdapter extends RecyclerView.Adapter<FriendEventListAdapter.FriendEventViewHolder>{
    private final String TAG = "FriendEventListAdapter";
    private final LayoutInflater mInflater;
    private List<Event_Friend_AssocDB> mEventFriendAsso;

    class FriendEventViewHolder extends RecyclerView.ViewHolder {
        TextView email, username;
        private FriendEventViewHolder(View itemView){
            super(itemView);
            this.username = itemView.findViewById(R.id.friendAsso_Username);
            this.email = itemView.findViewById(R.id.friendAsso_Email);
        }
    }

    FriendEventListAdapter (Context context){
        this.mInflater = LayoutInflater.from(context);


    }
    @Override
    public FriendEventViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Log.i(TAG, "FriendEventViewHolder onCreateView");
        final View view = mInflater.inflate(R.layout.event_info_friends_by_event_items, parent, false);
        return new FriendEventViewHolder(view);
    }

    @Override
    public void onBindViewHolder(FriendEventViewHolder holder, int position) {
        Log.i(TAG, "FriendEventViewHolder onBindView");
        if(mEventFriendAsso!=null){
            final Event_Friend_AssocDB current = mEventFriendAsso.get(position);
            holder.username.setText(current.getUSERNAME());
            holder.email.setText(current.getRef_friend_email());//Attention etait pense comme cle etrangere
        }
    }

    @Override
    public int getItemCount() {
        if(mEventFriendAsso !=null) {
            return mEventFriendAsso.size();
        }
        else{
            return 0;
        }
    }

    public void setmEventFriendAsso(List<Event_Friend_AssocDB>listedAsso){
        this.mEventFriendAsso=listedAsso;
        notifyDataSetChanged();
    }


}

