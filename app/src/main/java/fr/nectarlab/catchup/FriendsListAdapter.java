package fr.nectarlab.catchup;

import android.arch.lifecycle.ViewModelProviders;
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
import java.util.HashMap;
import java.util.List;

import fr.nectarlab.catchup.Database.RegisteredFriendsDB;
import fr.nectarlab.catchup.model.RegFriendsModel;

/**
 * FriendsListAdapter
 * Displays the data in a RecyclerView
 */

public class FriendsListAdapter extends RecyclerView.Adapter<FriendsListAdapter.FriendsViewHolder>{
    private final String TAG = "FriendsListAdapter";
    private final LayoutInflater mInflater;
    private List<RegisteredFriendsDB> mRegFriends;

        /*
        * Classe interne gardant en reference les itemsView
        * necessaires a l'affichage
        */
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

    FriendsListAdapter(Context context){mInflater = LayoutInflater.from(context);}

    /*
     * Creation de la View (item)
     */
    @Override
    public FriendsViewHolder onCreateViewHolder(ViewGroup parent, int viewType){
        Log.i(TAG, "OnCreateViewHolder: Start");
        final View view = mInflater.inflate(R.layout.recyclerview_item, parent, false);
        return new FriendsViewHolder(view);
    }

    /*
     * Affichage des elements tires de l'objet dans des Views
     */
    @Override
    public void onBindViewHolder(FriendsViewHolder holder, int pos){
        if(mRegFriends != null){
            Log.i(TAG, "onBindViewHolder: Start");
            RegisteredFriendsDB current = mRegFriends.get(pos);
            final RegisteredFriendsDB choosenFriend = current;
            holder.friendItemView.setText(current.getEMAIL());
            holder.friendUsernameView.setText(current.getUSERNAME());
            Log.i(TAG, ""+current.getUSERNAME());
            holder.friendPickedBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if(isChecked){
                        Log.i("onCheckedChanged", "isChecked: "+isChecked+" UserEMAIL: "+choosenFriend.getEMAIL());
                        //Arraylist.add
                        FriendsListHelper.addChoosenFriend(choosenFriend.getEMAIL(), choosenFriend.getUSERNAME());
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
            Log.i(TAG, "onBindViewHolder: no friend listed");
        }
    }

    /*
     * Finalisation de l'affichage
     */
    void setFriends(List<RegisteredFriendsDB>RegisteredFriends){
        Log.i(TAG, "setFriends: Start");
        this.mRegFriends = RegisteredFriends;
        notifyDataSetChanged();
    }

    /*
     * Retourne le nombre d'objets enregistres
     */
    @Override
    public int getItemCount(){
        if (mRegFriends !=null){
            Log.i(TAG, "getItemCount: "+mRegFriends.size());
            return mRegFriends.size();
        }
        else return 0;
    }

    public void getFriendsInfo(String email, String username){
        HashMap <String, String> friendsListed = new HashMap<>();
        friendsListed.put(email, username);
    }
}
