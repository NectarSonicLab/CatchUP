package fr.nectarlab.catchup.Database;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

import java.util.List;

import static android.arch.persistence.room.OnConflictStrategy.IGNORE;

/**
 * Created by ThomasBene on 5/28/2018.
 */

@Dao
public interface MessageDAO {
    @Insert(onConflict = IGNORE)
    void insert (Message message);
    @Query("SELECT * FROM Message")
    LiveData<List<Message>> getAllMessages();
    @Query("SELECT * FROM Message WHERE ref_event_ID LIKE :Search")
    LiveData<List<Message>> getAllMessagesForThatEvent (String Search);
    //List<Message> getAllMessagesForThatEvent (String Search);
}
