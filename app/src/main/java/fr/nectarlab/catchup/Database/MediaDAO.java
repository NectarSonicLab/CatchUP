package fr.nectarlab.catchup.Database;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

import java.util.List;

import static android.arch.persistence.room.OnConflictStrategy.IGNORE;

/**
 * MediaDAO
 * Requetes SQL concernant le schema Media
 */

@Dao
public interface MediaDAO {
    @Insert(onConflict = IGNORE)
    void insert (Media media);
    @Query("SELECT * FROM Media")
    LiveData<List<Media>> getAllMedias();
}
