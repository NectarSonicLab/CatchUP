package fr.nectarlab.catchup;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import fr.nectarlab.catchup.Database.RegisteredFriendsDB;

/**
 * Displays the data in a RecyclerView
 */

public class FriendsListAdapter extends RecyclerView.Adapter<FriendsListAdapter.FriendsViewHolder>{
    class FriendsViewHolder extends RecyclerView.ViewHolder{
        private final TextView friendItemView;
        private final TextView friendUsernameView;

        private FriendsViewHolder(View itemView){
            super(itemView);
            friendItemView = itemView.findViewById(R.id.FriendsListAdapter_item_tv);
            friendUsernameView = itemView.findViewById(R.id.FriendsListAdapter_itemUsername_tv);
        }
    }

    private final LayoutInflater mInflater;
    private List<RegisteredFriendsDB> mRegFriends;

    FriendsListAdapter(Context context){mInflater = LayoutInflater.from(context);}

    @Override
    public FriendsViewHolder onCreateViewHolder(ViewGroup parent, int viewType){
        final View view = mInflater.inflate(R.layout.recyclerview_item, parent, false);
        return new FriendsViewHolder(view);
    }

    @Override
    public void onBindViewHolder(FriendsViewHolder holder, int pos){
        if(mRegFriends != null){
            RegisteredFriendsDB current = mRegFriends.get(pos);
            holder.friendItemView.setText(current.getEMAIL());
            holder.friendUsernameView.setText(current.getUSERNAME());

            Log.i("FriendListAdapter", ""+current.getUSERNAME());
        }
        else{
            holder.friendItemView.setText("No Friend");
        }
    }

    void setFriends(List<RegisteredFriendsDB>RegisteredFriends){
        this.mRegFriends = RegisteredFriends;
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount(){
        if (mRegFriends !=null){
            return mRegFriends.size();
        }
        else return 0;
    }
}
