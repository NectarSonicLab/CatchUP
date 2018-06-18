package fr.nectarlab.catchup;

import android.arch.lifecycle.LifecycleOwner;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.List;

import fr.nectarlab.catchup.Database.EventDB;
import fr.nectarlab.catchup.Database.Event_Friend_AssocDAO;
import fr.nectarlab.catchup.Database.Event_Friend_AssocDB;
import fr.nectarlab.catchup.model.FriendAssocEventModel;
import fr.nectarlab.catchup.server_side.FirebaseHelper;

/**
 * EventInfo
 * Tous les details du groupe
 */

public class EventInfo extends FragmentActivity implements OnMapReadyCallback {

    private String TAG ="EventInfo";
    EventDB mEventDB;
    private GoogleMap googleMap;
    private MapView mapView;
    private double longitude, latitude;
    private TextView eventDescription, eventLocation, eventDate, eventType;
    private FirebaseDatabase mDatabase;
    private DatabaseReference mReference;
    private FriendAssocEventModel assocEventModel;
    LiveData<List<Event_Friend_AssocDB>> filteredFriends;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent i = getIntent();
        mEventDB = (EventDB)i.getSerializableExtra(IntentUtils.getEventAdapter_CurrentObject());
        Log.i(TAG, "onCreate eventID "+ mEventDB.getEventID());
        setContentView(R.layout.event_info);
        this.assocEventModel = ViewModelProviders.of(this).get(FriendAssocEventModel.class);
        if(savedInstanceState==null) {
            FirebaseHelper helper = new FirebaseHelper(mDatabase, mReference);
            helper.whoIsInvited(mEventDB.getEventID(), assocEventModel);
        }
        /*
         * On inclut les champs de l'objet mEventDB recupere dans les TextView correspondates
         */
        eventDescription = findViewById(R.id.eventInfo_eventDescription);
        String description = mEventDB.getEventName();
        eventDescription.setText(description);

        eventLocation = findViewById(R.id.eventInfo_eventLocation);
        String location = mEventDB.getLocation();
        eventLocation.setText(location);

        eventDate = findViewById(R.id.eventInfo_eventDate);
        String Date = mEventDB.getDate();
        eventDate.setText(Date);

        eventType = findViewById(R.id.eventInfo_eventType);
        String type = mEventDB.getEventType();
        eventType.setText(type);
        //Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.fragment_map);
        mapFragment.getMapAsync(this);

        mDatabase = FirebaseDatabase.getInstance();

        //filteredFriends = classifiedFriends();


    }

    /**
     * Affiche une map a l'utilisateur
   //  * @param gMap la GoogleMap qui recevra les coordonnees recuperees sur l'event


     */
    @Override
    public void onMapReady(GoogleMap gMap) {
        //On recupere la longitude latitude pour creer une nouvelle coordonnee pour la carte
        Log.i(TAG, "onMapReady()");
        longitude = mEventDB.getLongitude();
        latitude = mEventDB.getLatitude();
        googleMap = gMap;

        this.googleMap.setMinZoomPreference(16);

        LatLng eventLocation = new LatLng(latitude, longitude);

        googleMap.moveCamera(CameraUpdateFactory.newLatLng(eventLocation));
        googleMap.addMarker(new MarkerOptions().position(eventLocation).title(mEventDB.getEventName())).showInfoWindow();
    }

    @Override
    public void onResume(){
        super.onResume();
        Log.i(TAG, "onResume()");
        FriendEventListAdapter adapter = new FriendEventListAdapter(this);
        this.assocEventModel = ViewModelProviders.of(this).get(FriendAssocEventModel.class);
        RecyclerView recyclerView = findViewById(R.id.eventInfo_recycler);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        getFriends wait = new getFriends(this, filteredFriends, adapter);
        wait.start();
    }

    /*
    * Methode pour filtrer les amis invites par event
    */
    private LiveData classifiedFriends(){
        Log.i(TAG, "classifiedFriends()");
        assocEventModel = new FriendAssocEventModel(this.getApplication());
        final Event_Friend_AssocDAO assocDAO= assocEventModel.getmRepository().getmFriendAssoDAO();
        final LiveData<List<Event_Friend_AssocDB>> filtered = assocDAO.getFriendsInGroup(mEventDB.getEventID());
        Log.i(TAG, "return");
        return filtered;
    }

    private class getFriends extends Thread{
        LifecycleOwner ctx;
        LiveData<List<Event_Friend_AssocDB>> filter;
        FriendEventListAdapter adapter;
        public getFriends(LifecycleOwner context, LiveData<List<Event_Friend_AssocDB>> filtered, FriendEventListAdapter adapter){
            this.ctx=context;
            this.filter=filtered;
            this.adapter = adapter;
        }
        @Override
        public void run(){
            Log.i(TAG, "(class getFriends) run()");
            try{
                filter = classifiedFriends();
                sleep(200);
                filter.observe(ctx, new Observer<List<Event_Friend_AssocDB>>() {
                    @Override
                    public void onChanged(@Nullable List<Event_Friend_AssocDB> event_friend_assocDBS) {
                        //Log.i(TAG, "onChanged " + filteredFriends.getValue().get(0).getUSERNAME());
                        Log.i(TAG, "onChanged " + event_friend_assocDBS.size());
                        adapter.setmEventFriendAsso(event_friend_assocDBS);
                    }
                });
            }
            catch (Exception e){
                Log.i(TAG, "Exception raised in (class getFriends) run()");
            }
        }
    }

}
