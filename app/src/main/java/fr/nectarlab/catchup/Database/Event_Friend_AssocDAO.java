package fr.nectarlab.catchup.Database;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

import java.util.ArrayList;
import java.util.List;

import static android.arch.persistence.room.OnConflictStrategy.IGNORE;

/**
 * Interface pour l'insertion dans une table referencant
 * dans quel evenement se trouve quel ami
 */
@Dao
public interface Event_Friend_AssocDAO {
    @Insert (onConflict = IGNORE)
    void insert (Event_Friend_AssocDB event_friend_assocDB);
    @Query("SELECT * FROM Event_Friend_AssocDB")
    LiveData<List<Event_Friend_AssocDB>> getAllFriendsInGroup();
    @Query("SELECT * FROM Event_Friend_AssocDB WHERE ref_event_ID LIKE :Search")
    LiveData<List<Event_Friend_AssocDB>> getFriendsInGroup(String Search);
}
