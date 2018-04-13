package fr.nectarlab.catchup.Database;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

import java.util.List;

/**
 * Created by ThomasPiaczinski on 12/04/18.
 */
@Dao
public interface RegisteredFriendsDAO {
    @Insert
    void insert (RegisteredFriendsDB registeredFriends);
    @Query("SELECT * FROM RegisteredFriendsDB ORDER BY EMAIL ASC")
    LiveData<List<RegisteredFriendsDB>>getAllFriends();
    @Query("DELETE FROM RegisteredFriendsDB")
    void deleteAll();
    /*@Query("SELECT COUNT (EMAIL) FROM RegisteredFriendsDB")
    public int getNumberOfFriends ();*/
}