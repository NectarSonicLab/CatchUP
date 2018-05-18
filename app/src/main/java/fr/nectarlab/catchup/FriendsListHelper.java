package fr.nectarlab.catchup;

import android.util.Log;
import java.util.ArrayList;


public class FriendsListHelper {
    private static final String TAG = "FriendsListHelper";
    private static ArrayList<String> pickedFriends = new ArrayList<String>();

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

    public static ArrayList<String> getPickedFriends() {
        return pickedFriends;
    }

    public static void setPickedFriends() {
        pickedFriends.clear();
    }
}

