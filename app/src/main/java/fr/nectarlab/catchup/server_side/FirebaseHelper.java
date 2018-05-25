package fr.nectarlab.catchup.server_side;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

/**
 * FirebaseHelper
 * Objet contenant une reference vers la DB enrgistree sur le serveur de Firebase
 */

public class FirebaseHelper {
    /*
     * A Firebase reference represents a particular location in your Database and can be used for reading or writing data to that Database location.
     */
    private DatabaseReference mDatabaseRef;

    /*
     * The entry point for accessing a Firebase Database. You can get an instance by calling getInstance(). To access a location in the database and read or write data, use getReference()
     */
    private FirebaseDatabase mDatabase;


    //constructeur public
    public FirebaseHelper(FirebaseDatabase mDatabase){
        this.mDatabase = mDatabase;
    }

    public DatabaseReference getmDatabaseRef() {
        return mDatabaseRef;
    }

    public FirebaseDatabase getmDatabase() {
        return mDatabase;
    }
}
