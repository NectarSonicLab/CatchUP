package fr.nectarlab.catchup.Database;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

import java.util.List;

import static android.arch.persistence.room.OnConflictStrategy.IGNORE;
import static android.arch.persistence.room.OnConflictStrategy.REPLACE;

/**
 * EventDAO
 * Requetes SQL concernant le schema EventDB
 */
@Dao
public interface EventDAO {
    @Insert(onConflict = IGNORE)//Replace?
    void insert (EventDB event);
    @Query("SELECT * FROM EventDB ORDER BY eventName ASC")
    LiveData<List<EventDB>> getAllEvent();

}
