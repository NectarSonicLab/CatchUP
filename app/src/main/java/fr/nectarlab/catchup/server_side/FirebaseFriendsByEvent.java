package fr.nectarlab.catchup.server_side;

import java.util.HashMap;

/**
 * Objet pour recuperer les amis listes pour un evenement cree par l'utilisateur
 */

public class FirebaseFriendsByEvent {
     private HashMap<String, String> friendRef = new HashMap<>();
     private HashMap<String, HashMap> listedFriends = new HashMap<>();


    public FirebaseFriendsByEvent(){}


     public FirebaseFriendsByEvent(HashMap listedFriendsInFB, HashMap friendRef){
        this.friendRef = friendRef;
        this.listedFriends = listedFriendsInFB;
    }



    /*
    Getters and Setters
     */
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
