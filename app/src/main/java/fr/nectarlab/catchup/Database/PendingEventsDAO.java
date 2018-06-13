package fr.nectarlab.catchup.Database;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

import java.util.List;

import static android.arch.persistence.room.OnConflictStrategy.IGNORE;

/**
 * Created by ThomasBene on 6/7/2018.
 */


@Dao
public interface PendingEventsDAO {
    @Insert (onConflict = IGNORE)
    void insert (PendingEvents pendingEvents);
    @Query("SELECT * FROM PendingEvents")
    LiveData<List<PendingEvents>> getAllPendingEvents();
}
