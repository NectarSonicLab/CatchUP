package fr.nectarlab.catchup;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import fr.nectarlab.catchup.Database.EventDB;
import fr.nectarlab.catchup.server_side.FirebaseHelper;

/**
 * Invitation est l'activite lancee par les notifications recues
 * On y voit les evenements auquels nous avons ete invites.
 */

public class Invitation extends Activity {
    private final String TAG = "Invitation";
    private String extraEventID;
    private FirebaseDatabase mDatabase;
    private DatabaseReference mReference;
    @Override
    public void onCreate(Bundle b){
        super.onCreate(b);
        this.setContentView(R.layout.invitation);
        /*
         * On recupere l'event ID recu par la notification (class NotificationFromEvent) pour ensuite pouvoir recuperer
         * l'objet Event stocke sur Firebase
         */
        Intent i = this.getIntent();
        extraEventID = i.getStringExtra(IntentUtils.getPendingIntentEventKey());
        Log.i(TAG, ""+extraEventID);
        mDatabase = FirebaseDatabase.getInstance();

    }

    @Override
    public void onResume(){
        super.onResume();
        FirebaseHelper helper = new FirebaseHelper(mDatabase, mReference);
        if(null!=extraEventID) {
           helper.retrieveEvent(extraEventID, getApplication());
        }
    }

    @Override
    public void onPause(){
        super.onPause();

    }
    @Override
    public void onBackPressed(){
        this.finish();
    }
}
