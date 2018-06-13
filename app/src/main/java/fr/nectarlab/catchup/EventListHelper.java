package fr.nectarlab.catchup;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import fr.nectarlab.catchup.Database.EventDB;

/**
 *
 */
public class EventListHelper extends Activity {
    Context ctx;

    public void launchInsights(){
        Intent i = new Intent (this, Insights.class);
        startActivity(i);
    }
    public Context getContext(){
        return getApplicationContext();
    }


}
