package fr.nectarlab.catchup.Database;

import android.app.Application;
import android.arch.persistence.room.Room;


/**
 * non encore implantee
 * Cette classe fait les liens entre Firebase et Room
 */

public class FriendRepository extends Application{
   public static final String DATABASE_NAME = "MyDatabase";
   public static FriendRepository INSTANCE;
   private AppDatabase database;

    @Override
    public void onCreate(){
        super.onCreate();
        //create database
        database = Room.databaseBuilder(getApplicationContext(), AppDatabase.class, DATABASE_NAME).build();
        INSTANCE = this;
    }

    public AppDatabase getDB(){
        return database;
    }

}
