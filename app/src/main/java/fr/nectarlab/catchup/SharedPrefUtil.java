package fr.nectarlab.catchup;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * SharedPrefUtil
 * Consigne les cles utilise par le dictionnaire
 * permettant de sauvegarder des valeurs dans SharedPref
 */

public class SharedPrefUtil {
    private final String SHAREDPREF_ID = "User_ID";
    private final String SHAREDPREF_EMAIL = "User_EMAIL";
    private final static String SHAREDPREF_USERNAME = "User_USERNAME";

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

    public static String getSHAREDPREF_USERNAME() {
        return SHAREDPREF_USERNAME;
    }

}
