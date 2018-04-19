package fr.nectarlab.catchup.Database;

import android.arch.persistence.db.SupportSQLiteDatabase;
import android.arch.persistence.db.SupportSQLiteOpenHelper;
import android.arch.persistence.room.Database;
import android.arch.persistence.room.DatabaseConfiguration;
import android.arch.persistence.room.InvalidationTracker;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Process;
import android.support.annotation.NonNull;

import fr.nectarlab.catchup.ExecutionThreads.AppExecutors;

/**
 * Created by ThomasPiaczinski on 06/04/18.
 */
@Database(entities = {UserDB.class, FriendDB.class, GroupDB.class, Group_Friend_AssocDB.class, EventDB.class, RegisteredFriendsDB.class}, version = 1)
public abstract class AppDatabase extends RoomDatabase {
    public static final String DATABASE_NAME = "CatchUP_DB";
    private static AppDatabase DBInstance;

    public abstract FriendDao friendDao();//autre DAO a creer
    public abstract RegisteredFriendsDAO mRegisteredFriendsDAO();

    public static AppDatabase getInstance(final Context context) {
        if (DBInstance == null) {
            synchronized (AppDatabase.class) {
                if (DBInstance == null) {
                    DBInstance = Room.databaseBuilder(context.getApplicationContext(), AppDatabase.class, DATABASE_NAME)
                            .addCallback(sAppDatabaseCallback).build();
                             // .build();
                }
            }
        }
        return DBInstance;
    }

    /*private static AppDatabase buildDatabase(final Context appContext) {
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
        }).addCallback(sAppDatabaseCallback).build();
    }*/

    private static AppDatabase.Callback sAppDatabaseCallback = new AppDatabase.Callback(){
        @Override
        public void onOpen(@NonNull SupportSQLiteDatabase db){
            super.onOpen(db);
            new PopulateDbAsync(DBInstance).execute();
        }
    };

    private static class PopulateDbAsync extends AsyncTask<Void, Void, Void> {
        private final RegisteredFriendsDAO mRegisteredFriendsDAO;
        PopulateDbAsync(AppDatabase appDB){
            mRegisteredFriendsDAO = appDB.mRegisteredFriendsDAO();
        }
        @Override
        protected Void doInBackground(final Void... params){

            //mRegisteredFriendsDAO.deleteAll();
            /*
            RegisteredFriendsDB Friend1 = new RegisteredFriendsDB("toto@email.com");
            mRegisteredFriendsDAO.insert(Friend1);
            RegisteredFriendsDB Friend2 = new RegisteredFriendsDB("Alberto@email.com");
            mRegisteredFriendsDAO.insert(Friend2);
            RegisteredFriendsDB Friend3 = new RegisteredFriendsDB("jojo@email.com");
            mRegisteredFriendsDAO.insert(Friend3);
            */
            return null;
        }
    }
}

