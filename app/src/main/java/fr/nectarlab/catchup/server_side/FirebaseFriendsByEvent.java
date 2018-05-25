package fr.nectarlab.catchup.server_side;

import java.util.HashMap;

/**
 * Created by ThomasBene on 5/25/2018.
 */

public class FirebaseFriendsByEvent {
     private HashMap<String, String> friendRef = new HashMap<>();
     private HashMap<String, HashMap> listedFriends = new HashMap<>();
     public FirebaseFriendsByEvent(){}
     public FirebaseFriendsByEvent(HashMap listedFriendsInFB, HashMap friendRef){
        this.friendRef = friendRef;
        this.listedFriends = listedFriendsInFB;
    }

    public HashMap<String, String> getFriendRef() {
        return friendRef;
    }

    public void setFriendRef(HashMap<String, String> friendRef) {
        this.friendRef = friendRef;
    }

    public HashMap<String, HashMap> getListedFriends() {
        return listedFriends;
    }

    public void setListedFriends(HashMap<String, HashMap> listedFriends) {
        this.listedFriends = listedFriends;
    }
}
