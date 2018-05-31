package fr.nectarlab.catchup;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import fr.nectarlab.catchup.Database.EventDB;

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



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent i = getIntent();
        mEventDB = (EventDB)i.getSerializableExtra(IntentUtils.getEventAdapter_CurrentObject());
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    @Override
    public void onMapReady(GoogleMap gMap) {
        //On recupere la longitude latitude pour creer une nouvelle coordonnee pour la carte

        longitude = mEventDB.getLongitude();
        latitude = mEventDB.getLatitude();
        googleMap = gMap;

        this.googleMap.setMinZoomPreference(12);

        LatLng eventLocation = new LatLng(latitude, longitude);

        googleMap.moveCamera(CameraUpdateFactory.newLatLng(eventLocation));
        googleMap.addMarker(new MarkerOptions().position(eventLocation).title(mEventDB.getEventName()));
    }

}
