package fr.nectarlab.catchup;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import fr.nectarlab.catchup.Database.RegisteredFriendsDB;

/**
 * Displays the data in a RecyclerView
 */

public class FriendsListAdapter extends RecyclerView.Adapter<FriendsListAdapter.FriendsViewHolder>{
    private final String TAG = "FriendsListAdapter";
    class FriendsViewHolder extends RecyclerView.ViewHolder{
        private final TextView friendItemView;
        private final TextView friendUsernameView;
        private CheckBox friendPickedBox;


        private FriendsViewHolder(View itemView){
            super(itemView);
            friendItemView = itemView.findViewById(R.id.FriendsListAdapter_item_tv);
            friendUsernameView = itemView.findViewById(R.id.FriendsListAdapter_itemUsername_tv);
            friendPickedBox = itemView.findViewById((R.id.FriendsListAdapter_FriendsChecked_checkBox));
        }
    }

    private final LayoutInflater mInflater;
    private List<RegisteredFriendsDB> mRegFriends;

    FriendsListAdapter(Context context){mInflater = LayoutInflater.from(context);}

    @Override
    public FriendsViewHolder onCreateViewHolder(ViewGroup parent, int viewType){
        Log.i("FriendsListAdapter", "OnCreateViewHolder: Start");
        final View view = mInflater.inflate(R.layout.recyclerview_item, parent, false);
        return new FriendsViewHolder(view);
    }

    @Override
    public void onBindViewHolder(FriendsViewHolder holder, int pos){
        if(mRegFriends != null){
            Log.i("FriendsListAdapter", "onBindViewHolder: Start");
            RegisteredFriendsDB current = mRegFriends.get(pos);
            final RegisteredFriendsDB choosenFriend = current;
            holder.friendItemView.setText(current.getEMAIL());
            holder.friendUsernameView.setText(current.getUSERNAME());
            Log.i("FriendListAdapter", ""+current.getUSERNAME());
            holder.friendPickedBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if(isChecked){
                        Log.i("onCheckedChanged", "isChecked: "+isChecked+" UserEMAIL: "+choosenFriend.getEMAIL());
                        //Arraylist.add
                        FriendsListHelper.addChoosenFriend(choosenFriend.getEMAIL());
                    }
                    else{
                        //ArrayList.remove
                        Log.i("onCheckedChanged", "isChecked: "+isChecked+" UserEMAIL: "+choosenFriend.getEMAIL()+" removed");
                        FriendsListHelper.removeChoosenFriend(choosenFriend.getEMAIL());
                    }
                }
            });
        }
        else{
            holder.friendItemView.setText("No Friend");
            Log.i("FriendsListAdapter", "onBindViewHolder: no friend listed");
        }
    }

    void setFriends(List<RegisteredFriendsDB>RegisteredFriends){
        Log.i("FriendsListAdapter", "setFriends: Start");
        this.mRegFriends = RegisteredFriends;
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount(){
        if (mRegFriends !=null){
            Log.i("FriendsListAdapter", "getItemCount: "+mRegFriends.size());
            return mRegFriends.size();
        }
        else return 0;
    }
/*
    public class FriendsListHelper{
        ArrayList<String> pickedFriends = new ArrayList<String>();

        private void addChoosenFriend(String newFriend){
            pickedFriends.add(newFriend);
            Log.i(TAG, "FriendsListHelper, pickedFriends: "+pickedFriends);
        }

       private void removeChoosenFriend(String newFriend){
            if(pickedFriends.size()>0) {
                for (int i = 0;i<pickedFriends.size(); i++){
                    if(newFriend.equals(pickedFriends.get(i))){
                        pickedFriends.remove(i);
                    }
                }
            }
            Log.i(TAG, "FriendsListHelper, pickedFriends: "+pickedFriends);
        }
    }
    */
}
