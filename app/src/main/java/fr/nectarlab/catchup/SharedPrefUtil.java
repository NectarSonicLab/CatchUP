package fr.nectarlab.catchup;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by ThomasPiaczinski on 24/03/18.
 */

public class SharedPrefUtil {
    private final String SHAREDPREF_ID = "User_ID";
    private final String SHAREDPREF_EMAIL = "User_EMAIL";
    private final String SHAREDPREF_USERNAME = "User_USERNAME";

    private SharedPreferences sharedPref;


    public SharedPrefUtil (SharedPreferences sp){
        this.sharedPref = sp;
    }

    public String getSHAREDPREF_ID() {
        return SHAREDPREF_ID;
    }

    public String getSHAREDPREF_EMAIL() {
        return SHAREDPREF_EMAIL;
    }

    public String getSHAREDPREF_USERNAME() {
        return SHAREDPREF_USERNAME;
    }

}
