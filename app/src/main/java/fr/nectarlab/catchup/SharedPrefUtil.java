package fr.nectarlab.catchup;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * SharedPrefUtil
 * Consigne les cles utilise par le dictionnaire
 * permettant de sauvegarder des valeurs dans SharedPref
 */

public class SharedPrefUtil {
    final static String SHAREDPREF_ID = "User_ID";
    final static String SHAREDPREF_EMAIL = "User_EMAIL";
    final static String SHAREDPREF_USERNAME = "User_USERNAME";
    final static String isACCOUNT_ON_TERMINAL = "isACCOUNT_ON_TERMINAL";
    final static String SHAREDPREF_PROFILE_PICTURE = "User_PROFILE_PICTURE";


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

    public static String getSharedprefProfilePicture() {
        return SHAREDPREF_PROFILE_PICTURE;
    }
}
