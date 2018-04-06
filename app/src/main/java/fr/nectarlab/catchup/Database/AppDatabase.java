package fr.nectarlab.catchup.Database;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.RoomDatabase;

/**
 * Created by ThomasPiaczinski on 06/04/18.
 */
@Database(entities = {Friend.class}, version = 1)
public abstract class AppDatabase extends RoomDatabase{
    public abstract FriendDao friendDao();
}
