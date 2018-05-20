package fr.nectarlab.catchup;

import android.util.Log;
import java.util.ArrayList;

/**
 * FriendsListHelper
 * Permet de determiner quels amis ont ete choisis dans l'activite de creation d'invitation
 * Classe appelee par FriendsListAdapter.onBindViewHolder
 * Stock dans un array la liste des amis choisis. Sera finalement renvoye dans un Intent
 * a l'activite appelante de RegisteredUsersActivity_test
 */
public class FriendsListHelper {
    private static final String TAG = "FriendsListHelper";
    private static ArrayList<String> pickedFriends = new ArrayList<String>();

    /*
     * Logique pour dynamiquement remplir un array en fonction des checkBox cochees
     */
    public static void addChoosenFriend(String newFriend){
        pickedFriends.add(newFriend);
        Log.i(TAG, "FriendsListHelper, pickedFriends: "+pickedFriends);
    }

    public static void removeChoosenFriend(String newFriend){
        if(pickedFriends.size()>0) {
            for (int i = 0;i<pickedFriends.size(); i++){
                if(newFriend.equals(pickedFriends.get(i))){
                    pickedFriends.remove(i);
                }
            }
        }
        Log.i(TAG, "FriendsListHelper, pickedFriends: "+pickedFriends);
    }

    //Getters et Setters
    public static ArrayList<String> getPickedFriends() {
        return pickedFriends;
    }

    public static void setPickedFriends() {
        pickedFriends.clear();
    }
}

