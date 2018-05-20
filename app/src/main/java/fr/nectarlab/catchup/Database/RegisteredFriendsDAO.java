package fr.nectarlab.catchup.Database;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

import java.util.List;

import static android.arch.persistence.room.OnConflictStrategy.IGNORE;
import static android.arch.persistence.room.OnConflictStrategy.REPLACE;

/**
 * RegisteredFriendsDAO
 * Interface gerant les requetes du schema RegisteredFriendsDB
 */
@Dao
public interface RegisteredFriendsDAO {
    @Insert (onConflict = REPLACE)//IGNORE?
    void insert (RegisteredFriendsDB registeredFriends);
    @Query("SELECT * FROM RegisteredFriendsDB ORDER BY EMAIL ASC")
    LiveData<List<RegisteredFriendsDB>>getAllFriends();
    @Query("DELETE FROM RegisteredFriendsDB")
    void deleteAll();
    @Query("SELECT COUNT (EMAIL) FROM RegisteredFriendsDB")
    int getNumberOfFriends ();
}
