package fr.nectarlab.catchup;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import fr.nectarlab.catchup.Database.EventDB;

/**
 * EventInfo
 * Tous les details du groupe
 */

public class EventInfo extends Activity {
    private String TAG ="EventInfo";
    EventDB mEventDB;
    @Override
    public void onCreate (Bundle b){
        super.onCreate(b);
        setContentView(R.layout.event_info);
        Intent i = getIntent();
        mEventDB = (EventDB) i.getSerializableExtra(IntentUtils.getEventAdapter_CurrentObject());
        String ID = mEventDB.getEventID();
        Log.i(TAG, "onCreate ID de l'event: "+ID);
    }
}
