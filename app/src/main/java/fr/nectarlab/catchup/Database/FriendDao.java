package fr.nectarlab.catchup.Database;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Query;

import java.util.List;

/**
 * Created by ThomasPiaczinski on 06/04/18.
 */

@Dao
public interface FriendDao {
    @Query("SELECT * FROM Friend")
    List<Friend> getAll();
}
