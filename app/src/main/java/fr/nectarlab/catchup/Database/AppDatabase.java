package fr.nectarlab.catchup.Database;

import android.arch.persistence.db.SupportSQLiteDatabase;
import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.arch.persistence.room.migration.Migration;
import android.content.Context;
import android.os.AsyncTask;
import android.support.annotation.NonNull;

/**
 * Base de donnees qui etend la classe RoomDatabase
 *
 */
@Database(entities = {UserDB.class, FriendDB.class, GroupDB.class, Event_Friend_AssocDB.class, EventDB.class, RegisteredFriendsDB.class, Media.class, Message.class}, version = 1)
public abstract class AppDatabase extends RoomDatabase {
    public static final String DATABASE_NAME = "CatchUP_DB";
    private static AppDatabase DBInstance;

    /*
     * Reference aux interfaces utilises pour effectuer les differentes requetes SQL necessaires
     */
    public abstract FriendDao friendDao();//autre DAO a creer
    public abstract RegisteredFriendsDAO mRegisteredFriendsDAO();
    public abstract EventDAO mEventDAO();
    public abstract Event_Friend_AssocDAO mEvent_Friend_AssocDAO();

    /*
     * Recuperation de l'instance statique de la base de donnees
     * avec en option la possibilite de faire un callback avant le build (ici commente)
     */
    public static AppDatabase getInstance(final Context context) {
        if (DBInstance == null) {
            synchronized (AppDatabase.class) {
                if (DBInstance == null) {
                    DBInstance = Room.databaseBuilder(context.getApplicationContext(), AppDatabase.class, DATABASE_NAME)
                            //.addCallback(sAppDatabaseCallback).build();
                             .build();
                }
            }
        }
        return DBInstance;
    }

    /*
     * Methode pour populer la BD de maniere asynchrone pendant le callback
     */
    private static AppDatabase.Callback sAppDatabaseCallback = new AppDatabase.Callback(){
        @Override
        public void onOpen(@NonNull SupportSQLiteDatabase db){
            super.onOpen(db);
            new PopulateDbAsync(DBInstance).execute();
        }
    };

    /*
     * Methode (non implanter) pour effectuer une migration entre versions de BD
     */
    static final Migration MIGRATION_1_2 = new Migration(1, 2) {
        @Override
        public void migrate(SupportSQLiteDatabase database) {
            database.execSQL("ALTER TABLE RegisteredFriendsDB "
                    + " ADD COLUMN USERNAME TEXT");
        }
    };

    /*
     * L'async Task appelee par la methode AppDatabase.Callback
     * pour partir avec un etat vide de la BD au lancement de l'app
     * utile pendant le developpement pour tester les insertions
     */
    private static class PopulateDbAsync extends AsyncTask<Void, Void, Void> {
        private final RegisteredFriendsDAO mRegisteredFriendsDAO;
        PopulateDbAsync(AppDatabase appDB){
            mRegisteredFriendsDAO = appDB.mRegisteredFriendsDAO();
        }
        @Override
        protected Void doInBackground(final Void... params){

            mRegisteredFriendsDAO.deleteAll();
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

