package fr.nectarlab.catchup.Database;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.RoomDatabase;

/**
 * Created by ThomasPiaczinski on 06/04/18.
 */
@Database(entities = {FriendDB.class}, version = 1)
public abstract class AppDatabase extends RoomDatabase{
    private static AppDatabase sInstance;
    public abstract FriendDao friendDao();

}
