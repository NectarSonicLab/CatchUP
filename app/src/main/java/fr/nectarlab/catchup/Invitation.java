package fr.nectarlab.catchup;

import android.app.Activity;
import android.arch.lifecycle.LifecycleOwner;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.Observer;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.List;

import fr.nectarlab.catchup.Database.EventDAO;
import fr.nectarlab.catchup.Database.EventDB;
import fr.nectarlab.catchup.model.EventModel;
import fr.nectarlab.catchup.server_side.FirebaseHelper;

/**
 * Invitation est l'activite lancee par les notifications recues
 * On y voit les evenements auquels nous avons ete invites.
 */

public class Invitation extends AppCompatActivity {
    private final String TAG = "Invitation";
    private String extraEventID;
    private FirebaseDatabase mDatabase;
    private DatabaseReference mReference;
    private EventModel mEventModel;
    private LiveData<List<EventDB>> filter;
    @Override
    public void onCreate(Bundle b){
        Log.i(TAG, "onCreate");
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
        Log.i(TAG, "onResume");
        super.onResume();
        EventFilterListAdapter adapter = new EventFilterListAdapter();
        FirebaseHelper helper = new FirebaseHelper(mDatabase, mReference);
        if(null!=extraEventID) {
           helper.retrieveEvent(extraEventID, getApplication());
        }
        getEvent Event = new getEvent(this, filter, adapter);
        Event.start();

    }

    @Override
    public void onPause(){
        Log.i(TAG, "onPause");
        super.onPause();

    }
    @Override
    public void onBackPressed(){
        super.onBackPressed();
        Log.i(TAG, "onBackPressed");
        this.finish();
    }

    private LiveData filteredEvent(){
        Log.i(TAG, "LiveData()");
        mEventModel = new EventModel(this.getApplication());
        final EventDAO eventDAO = mEventModel.getmRepository().getmEventDAO();
        final LiveData<List<EventDB>> invitationEvent = eventDAO.getParticularEvent(extraEventID);
        return invitationEvent;
    }
    private class getEvent extends Thread{
        LifecycleOwner ctx;
        LiveData<List<EventDB>> filteredEvent;
        EventFilterListAdapter adapter;
        private getEvent(LifecycleOwner context, LiveData<List<EventDB>> filteredEvent, EventFilterListAdapter efla){
            this.ctx = context;
            this.filteredEvent= filteredEvent;
            this.adapter=efla;
        }
        @Override
        public void run(){
            Log.i(TAG, "(class getEvent) run()");
            try{
                filteredEvent = filteredEvent();
                sleep(500);
                filteredEvent.observe(ctx, new Observer<List<EventDB>>() {
                    @Override
                    public void onChanged(@Nullable List<EventDB> eventDBS) {
                        Log.i(TAG, "onChanged " +eventDBS.size());
                        if(eventDBS.size()!=0) {
                            Log.i(TAG, "eventDBS " + eventDBS.get(0).getEventID());
                            //TextView tvID = findViewById(R.id.Invitation_eventID);
                            TextView tvAdmin = findViewById(R.id.Invitation_amin);
                            TextView tvName = findViewById(R.id.Invitation_eventName);
                            TextView tvLocation = findViewById(R.id.Invitation_location);
                            TextView tvDate = findViewById(R.id.Invitation_debutTime);
                            tvAdmin.setText(eventDBS.get(0).getAdmin());
                            tvName.setText(eventDBS.get(0).getEventName());
                            tvLocation.setText(eventDBS.get(0).getLocation());
                            tvDate.setText(eventDBS.get(0).getDate());
                        }
                        //adapter.setFilteredEvent(eventDBS);
                    }
                });
            }
            catch(Exception e){}
        }
    }
    public void Continue (View v){
        this.finish();
    }
}
