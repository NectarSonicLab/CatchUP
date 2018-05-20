package fr.nectarlab.catchup.Database;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

import java.util.List;

/**
 * FriendDAO
 * Interface gerant les requetes du schema FriendDB
 */

@Dao
public interface FriendDao {
    @Insert
    public void insertNewFriend(FriendDB friendDB);

    @Delete
    public void deletedFriend(FriendDB friendDB);

    @Query("SELECT * FROM FriendDB")
    List<FriendDB> getAll();

    @Query("SELECT COUNT (EMAIL) FROM FriendDB")
    public int numberOfFriends ();
}
