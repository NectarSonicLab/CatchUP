package fr.nectarlab.catchup.BackgroundTasks;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

/**
 * Created by ThomasBene on 6/8/2018.
 */

public class ConnectivitySupervisor extends BroadcastReceiver {
    private String TAG = "ConnectivitySupervisor";
    @Override
    public void onReceive(Context context, Intent intent) {
        Log.i(TAG, "onReceive");
        try{
            if(isOnline(context)){
                Log.i(TAG, "onReceive: online");
            }
            else{
                Log.i(TAG, "onReceive: no connection available");
            }
        }
        catch (NullPointerException npe){
            npe.printStackTrace();
        }
    }

    private boolean isOnline(Context context){
        try{
            ConnectivityManager cm = (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo ni = cm.getActiveNetworkInfo();
            return (ni != null && ni.isConnected());
        }
        catch (NullPointerException npe){
            Log.i(TAG, "NPException raised");
            return false;
        }
    }
}
