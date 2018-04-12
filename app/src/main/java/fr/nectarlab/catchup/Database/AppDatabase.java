package fr.nectarlab.catchup.Database;

import android.arch.persistence.db.SupportSQLiteDatabase;
import android.arch.persistence.db.SupportSQLiteOpenHelper;
import android.arch.persistence.room.Database;
import android.arch.persistence.room.DatabaseConfiguration;
import android.arch.persistence.room.InvalidationTracker;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.content.Context;
import android.os.Process;
import android.support.annotation.NonNull;

import fr.nectarlab.catchup.ExecutionThreads.AppExecutors;

/**
 * Created by ThomasPiaczinski on 06/04/18.
 */
@Database(entities = {UserDB.class, FriendDB.class, GroupDB.class, Group_Friend_AssocDB.class, EventDB.class}, version = 1)
public abstract class AppDatabase extends RoomDatabase {
    public static final String DATABASE_NAME = "CatchUP_DB";
    private static AppDatabase DBInstance;
    private Boolean isDatabaseCreated;

    public abstract FriendDao friendDao();//autre DAO a creer
/*
    private AppDatabase() {

    }
*/


    public static AppDatabase getInstance(final Context context) {//Creer le pool de thread
        if (DBInstance == null) {
            synchronized (AppDatabase.class) {
                if (DBInstance == null) {
                    DBInstance = buildDatabase(context.getApplicationContext());
                }
            }
        }
        return DBInstance;
    }

    private static AppDatabase buildDatabase(final Context appContext) {
        return Room.databaseBuilder(appContext, AppDatabase.class, DATABASE_NAME).addCallback(new Callback() {
            @Override
            public void onCreate(@NonNull SupportSQLiteDatabase db) {
                super.onCreate(db);
                //thread qui execute ca
                Runnable myThread = new Runnable() {
                    @Override
                    public void run() {
                        android.os.Process.setThreadPriority(Process.THREAD_PRIORITY_BACKGROUND);
                        AppDatabase database = AppDatabase.getInstance(appContext);
                    }
                };
                myThread.run();
            }
        }).build();
    }


}

