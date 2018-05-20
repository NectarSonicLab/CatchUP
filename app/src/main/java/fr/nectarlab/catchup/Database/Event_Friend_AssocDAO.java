package fr.nectarlab.catchup.Database;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

import java.util.ArrayList;

/**
 * Interface pour l'insertion dans une table referencant
 * dans quel evenement se trouve quel ami
 */
@Dao
public interface Event_Friend_AssocDAO {
    @Insert
    void insert (Event_Friend_AssocDB event_friend_assocDB);
    //@Query("SELECT * FROM Event_Friend_AssocDB ORDER BY ref_friend_email ASC")
   // ArrayList<String> getFriendsInGroup();
}
