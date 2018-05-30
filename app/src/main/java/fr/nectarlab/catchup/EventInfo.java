package fr.nectarlab.catchup;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import fr.nectarlab.catchup.Database.EventDB;

/**
 * EventInfo
 * Tous les details du groupe
 */

public class EventInfo extends AppCompatActivity implements OnMapReadyCallback {
    private String TAG ="EventInfo";
    EventDB mEventDB;
    private GoogleMap googleMap;
    private MapView mapView;
    private double longitude, latitude;



    @Override
    public void onCreate (Bundle b){
        super.onCreate(b);
        setContentView(R.layout.event_info);
        Intent i = getIntent();
        mEventDB = (EventDB) i.getSerializableExtra(IntentUtils.getEventAdapter_CurrentObject());
        String ID = mEventDB.getEventID();
        Log.i(TAG, "onCreate ID de l'event: "+ID);
        Bundle mapViewBundle = null;
        mapView = findViewById(R.id.eventInfo_map_mv);
        mapView.onCreate(mapViewBundle);
        mapView.getMapAsync(this);
    }

    @Override
    public void onMapReady(GoogleMap gMap) {
        //On recupere la longitude/latitude pour creer une nouvelle coordonnee pour la carte
        longitude = mEventDB.getLongitude();
        latitude = mEventDB.getLatitude();
        this.googleMap = gMap;
        //this.googleMap.setMinZoomPreference(12);
        LatLng eventLocation = new LatLng(longitude, latitude);
        this.googleMap.moveCamera(CameraUpdateFactory.newLatLng(eventLocation));
        this.googleMap.addMarker(new MarkerOptions().position(eventLocation));
    }
}
