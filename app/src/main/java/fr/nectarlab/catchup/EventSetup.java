package fr.nectarlab.catchup;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.provider.CalendarContract;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.DatePicker;
import android.widget.TextView;

import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlacePicker;

import java.net.URI;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Created by ThomasBene on 4/19/2018.
 */

public class EventSetup extends AppCompatActivity{
    private final String TAG = "EventSetup";
    private final int MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 0;
    private int PLACE_PICKER_REQUEST = 1;
    private int DATE_PICKER_REQUEST = 2;
    private int EVENT_TYPE_REQUEST = 3;
    private int FRIENDS_PICKER_REQUEST = 4;

    private int mYear;
    private int mMonth;
    private int mDay;

    private String choiceSaved;

    private boolean eventTypeSaved;

    private TextView retrievePlace, retrieveName, retrieveEventType, retrieveDay;
    //private TextView retrieveDay;
    @Override
    public void onCreate (Bundle b){
        super.onCreate(b);
        setContentView(R.layout.event_setup);
        retrievePlace = findViewById(R.id.eventSetup_retrievePlace_tv);
        retrieveName = findViewById(R.id.eventSetup_retrieveName_tv);
        retrieveDay = findViewById(R.id.eventSetUp_day_tv);
        retrieveEventType = findViewById(R.id.eventSetup_eventType_tv);

        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            // Permission is not granted
            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.ACCESS_FINE_LOCATION)) {

                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.

            } else {

                // No explanation needed; request the permission
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);

                // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
                // app-defined int constant. The callback method gets the
                // result of the request.
            }
        } else {
            // Permission has already been granted
        }
        /**
         * Recuperation des infos en cas de changement d'orientation
         */
        if (b!=null){
            //pour le lieu
            retrievePlace.setText(b.getString("adresse"));
            Log.i(TAG, "onCreate savedAddress: "+b.getString("adresse"));
            retrieveName.setText(b.getString("nom"));
            Log.i(TAG, "onCreate savedPlace: "+b.getString("nom"));
            //pour la date
            retrieveDay.setText(b.getString("date"));
            Log.i(TAG, "onCreate savedDate: "+b.getString("date"));
            //pour le type d'evenement
            retrieveEventType.setText(b.getString("type"));
            Log.i(TAG, "onCreate savedText: "+b.getString("type"));
        }
    }

    @Override
    public void onSaveInstanceState (Bundle b){
        super.onSaveInstanceState(b);
        Log.i(TAG, "onSaveInstanceState: Debut");
        /**
         * Mettre dans le bundle les elements qui ont ete sauvegardes par l'utilisateur CAD: place.getName, place.getAddress, mYear, mMonth, mDay et choiceSaved
         */

        /**
         * Sauvegarde de l'endroit
         */
        String savedAddress = (String) retrievePlace.getText();
        b.putString("adresse", savedAddress);
        String savedPlaceName = (String) retrieveName.getText();
        b.putString ("nom", savedPlaceName);
        Log.i(TAG, "onSave AddressSaved: "+savedAddress);
        Log.i(TAG, "onSave PlaceSaved: "+savedPlaceName);

        /**
         * Sauvegarde de la date
         */
        String savedDate = (String) retrieveDay.getText();
        b.putString ("date", savedDate);
        Log.i(TAG, "onSave DateSaved: "+savedDate);

        /**
         * Sauvegarde du type d'event
         */
        String eventType = (String) retrieveEventType.getText();
        b.putString("type", eventType);
        Log.i(TAG, "onSave eventTypeSaved: "+eventType);

        Log.i(TAG, "onSaveInstanceState: Fin");
    }

    /**
     * Intent pour lancer Google places API et recuperer des infos sur l'endroit choisi
     * @param v :Bouton qui lance l'intent
     */
    public void pickPlace(View v){
        PlacePicker.IntentBuilder builder = new PlacePicker.IntentBuilder();

        try{
            startActivityForResult(builder.build(EventSetup.this), PLACE_PICKER_REQUEST);
        }
        catch (GooglePlayServicesRepairableException e){
            e.printStackTrace();
        }
        catch (GooglePlayServicesNotAvailableException e){
            e.printStackTrace();
        }
    }

    /**
     * On lance l'activite DateChooser pour ensuite recuperer le choix de l'utilisateur
     * On utilise un DatePicker (Widget) pour definir la date
     * @param v: Bouton qui lance l'intent
     */
    public void pickDate(View v){
        Intent i = new Intent(this, DateChooser.class);
        startActivityForResult(i, DATE_PICKER_REQUEST);
    }


    /**
     * On lance l'activite EventChooser pour ensuite recuperer le choix de l'utilisateur
     * Ce choix se base sur des RadioButtons
     * @param v: Bouton qui lance l'intent
     */
    public void chooseEventType(View v){
        Intent i = new Intent (this, EventChooser.class);
        startActivityForResult(i, EVENT_TYPE_REQUEST);
    }

    public void pickFriends (View v){
        Intent i = new Intent (this, RegisteredUsersActivity_test.class);
        startActivityForResult(i, FRIENDS_PICKER_REQUEST);
        //pb avec l'affichage des amis, oblige de revenir pour les voir s'afficher
    }

    @Override
    public void onActivityResult (int requestCode, int resultCode, Intent data){
        if(requestCode == PLACE_PICKER_REQUEST){
            if(resultCode == RESULT_OK){
                Place place = PlacePicker.getPlace(this, data);
                retrievePlace.setText(place.getAddress());
                retrieveName.setText(place.getName());
                Log.i(TAG, "Data from intent: "+place.getAddress());
                Log.i(TAG, "Data from intent: "+place.getName());
            }
        }
        if(requestCode == DATE_PICKER_REQUEST){
            if(resultCode == RESULT_OK){
                mYear = data.getIntExtra("year", -1);
                mMonth = data.getIntExtra("month", -1);
                mDay = data.getIntExtra("day", -1);


                //SimpleDateFormat formater = new SimpleDateFormat("dd-MM-yy", Locale.FRANCE);
                //Date date = SimpleDateFormat.parse(mDay+"-"+mMonth+"-"+mYear);   //parse est une methode non statique qui ne peut pas etre appelee dans ce contexte

                //retrieveDay.setText(String.format("%d",mDay));                      //quickFix
                /**
                 * QuickFix
                 * Meilleure solution: Utiliser dateFormat et locale pour pouvoir avoir un format de date correspondant au pays ou l'app est utilisee...
                 */
                //TODO rajouter l'heure de debut (autre activite?) + ne pas permettre de choisir une date anterieure a la date du jour
                StringBuilder sb = new StringBuilder();
                sb.append(mDay);
                sb.append("/");
                sb.append(mMonth);
                sb.append("/");
                sb.append(mYear);

                String dateSaved = sb.toString();
                retrieveDay.setText(dateSaved);

                Log.i(TAG, "Year from intent: "+mYear);
                Log.i(TAG, "Month from intent: "+mMonth);
                Log.i(TAG, "Day from intent: "+mDay);
            }
            else{
                Log.i(TAG, "Result not ok");
            }
        }

        if (requestCode == EVENT_TYPE_REQUEST){
            if (resultCode == RESULT_OK){
                eventTypeSaved = true;
                choiceSaved = data.getStringExtra("eventChoosen");
                retrieveEventType = findViewById(R.id.eventSetup_eventType_tv);
                retrieveEventType.setText(choiceSaved);
            }
        }
    }
}
