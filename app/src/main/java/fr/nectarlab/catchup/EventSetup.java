package fr.nectarlab.catchup;

import android.Manifest;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

import java.util.ArrayList;
import java.util.HashMap;

import fr.nectarlab.catchup.Database.EventDB;
import fr.nectarlab.catchup.model.EventModel;
import fr.nectarlab.catchup.server_side.FirebaseHelper;
import fr.nectarlab.catchup.server_side.ServerUtil;

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

    private FirebaseDatabase mDatabase;
    private DatabaseReference mReference;
    private FirebaseAuth mAuth;

    private EventModel mEventModel;
    private LatLng coordinates;
    private double longitude, latitude;
    ArrayList<String>pickedFriends;
    HashMap<String, String> registeredFriends;
    private TextView retrievePlace, retrieveName, retrieveEventType, retrieveDay, retrieveFriends, retrieveEventDescription;
    private boolean [] saveState = new boolean [5];
    //private TextView retrieveDay;
    @Override
    public void onCreate (Bundle b){
        super.onCreate(b);
        Log.i(TAG, "onCreate");
        /*
         * Remise a zero de la liste d'amis (statique) sauvegardee puis renvoyee a cette activite
         * sinon s'incremente a chaque instanciation de l'activite RegisteredUsersActivity
         */
        FriendsListHelper.setPickedFriends();
        setContentView(R.layout.event_setup);



        mDatabase = FirebaseDatabase.getInstance();
        mAuth = FirebaseAuth.getInstance();

        retrieveEventDescription = findViewById(R.id.eventSetup_eventName_et);
        retrievePlace = findViewById(R.id.eventSetup_retrievePlace_tv);
        retrieveName = findViewById(R.id.eventSetup_retrieveName_tv);
        retrieveDay = findViewById(R.id.eventSetUp_day_tv);
        retrieveEventType = findViewById(R.id.eventSetup_eventType_tv);
        retrieveFriends = findViewById(R.id.eventSetUp_friendsPicked_tv);

        mEventModel = ViewModelProviders.of(this).get(EventModel.class);

        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
                // No explanation needed; request the permission
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
        }
        /*
         * Recuperation des infos en cas de changement d'orientation
         */
        if (b!=null){
            //pour le lieu
            retrievePlace.setText(b.getString("adresse"));
            Log.i(TAG, "onCreate savedAddress: "+b.getString("adresse"));
            retrieveName.setText(b.getString("nom"));
            Log.i(TAG, "onCreate savedPlace: "+b.getString("nom"));
            longitude = b.getDouble("longitude");
            latitude = b.getDouble ("latitude");
            //pour la date
            retrieveDay.setText(b.getString("date"));
            Log.i(TAG, "onCreate savedDate: "+b.getString("date"));
            //pour le type d'evenement
            retrieveEventType.setText(b.getString("type"));
            Log.i(TAG, "onCreate savedEvent: "+b.getString("type"));
            retrieveFriends.setText(b.getString("savedFriends"));
            Log.i(TAG, "onCreate savedFriends: "+b.getString("savedFriends"));
            saveState = b.getBooleanArray("saveState");
            Log.i(TAG, "onCreate saveState: "+b.getString("saveState"));
        }
    }



    @Override
    public void onStart(){
        super.onStart();
        Log.i(TAG, "onStart: debut");

    }

    @Override
    public void onResume(){
        super.onResume();
        Log.i(TAG, "onResume: debut");
        checkSaveStatus();
        }



    @Override
    public void onSaveInstanceState (Bundle b){
        super.onSaveInstanceState(b);
        Log.i(TAG, "onSaveInstanceState: Debut");

        /*
         * Sauvegarde de l'endroit
         */
        String savedAddress = (String) retrievePlace.getText();
        b.putString("adresse", savedAddress);
        String savedPlaceName = (String) retrieveName.getText();
        b.putString ("nom", savedPlaceName);
        if (null!=coordinates){
            longitude = coordinates.longitude;
            latitude = coordinates.latitude;
        }
        b.putDouble("longitude", longitude);
        b.putDouble("latitude", latitude);
        Log.i(TAG, "onSave AddressSaved: "+savedAddress);
        Log.i(TAG, "onSave PlaceSaved: "+savedPlaceName);
        Log.i(TAG, "onSave Longitude: "+longitude);
        Log.i(TAG, "onSave Latitude: "+latitude);

        /*
         * Sauvegarde de la date
         */
        String savedDate = (String) retrieveDay.getText();
        b.putString ("date", savedDate);
        Log.i(TAG, "onSave DateSaved: "+savedDate);

        /*
         * Sauvegarde du type d'event
         */
        String eventType = (String) retrieveEventType.getText();
        b.putString("type", eventType);
        Log.i(TAG, "onSave eventTypeSaved: "+eventType);

        Log.i(TAG, "onSaveInstanceState: Fin");

        /*
         * Sauvegarde de la liste d'amis
         */
        String savedFriends = (String) retrieveFriends.getText();
        b.putString("savedFriends", savedFriends);
        Log.i(TAG, "onSave savedFriends: "+savedFriends);

        /*
         * Sauvegarde du tableau saveState
         */
        b.putBooleanArray("saveState", saveState);
        Log.i(TAG, "onSave savedFriends: "+saveState);
    }

    /**
     * Intent pour lancer Google places API et recuperer des infos sur l'endroit choisi
     * @param v :Bouton qui lance l'intent
     */

    public void pickPlace(View v){
        Log.i(TAG, "PickPlace()");
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
     * @param v: Bouton qui lance l'intent vers DateChooser.class
     */
    public void pickDate(View v){
        Log.i(TAG, "PickDate()");
        Intent i = new Intent(this, DateChooser.class);
        startActivityForResult(i, DATE_PICKER_REQUEST);
    }


    /**
     * On lance l'activite EventChooser pour ensuite recuperer le choix de l'utilisateur
     * Ce choix se base sur des RadioButtons
     * @param v: Bouton qui lance l'intent vers EventChooser.class
     */
    public void chooseEventType(View v){
        Log.i(TAG, "chooseEventType()");
        Intent i = new Intent (this, EventChooser.class);
        startActivityForResult(i, EVENT_TYPE_REQUEST);
    }

    /**
     * On lance l'activite RegisteredUsers_test pour trouver des amis
     * @param v Bouton qui lance l'intent vers RegisteredUsersActivity_test.class
     */
    public void pickFriends (View v){
        Log.i(TAG, "pickFriends()");
        /**
         *READ_CONTACTS fait partie des "dangerous permissions", elle doit explicitement etre
         * demandee a l'utilisateur. Nous faisons donc une "request permission".
         * Peut en plus etre explicitee (avant la "requestPermissions()") avec la methode
         * shouldShowRequestPermissionRationale() qui propose un explication a l'user sur le
         * besoin de cette permission.
         */

        if(ContextCompat.checkSelfPermission(this, Manifest.permission.READ_CONTACTS)!=PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this, new String []{Manifest.permission.READ_CONTACTS}, 0 );
        }
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.READ_CONTACTS)==PackageManager.PERMISSION_GRANTED){
            Intent i = new Intent (this, RegisteredUsersActivity_test.class);
            startActivityForResult(i, FRIENDS_PICKER_REQUEST);
        }
//        else{Intent i = new Intent (this, RegisteredUsersActivity_test.class);
//            startActivityForResult(i, FRIENDS_PICKER_REQUEST);
//        }
    }

    /**
     * Recuperation des differentes donnees envoyes en retour avec les differents Intents
     * @param requestCode le code correspondant a un intent particulier
     * @param resultCode le resultat renvoye par l'activite appelee
     * @param data l'intent renvoye et les donnees qu'il transporte
     */
    @Override
    public void onActivityResult (int requestCode, int resultCode, Intent data){
        Log.i(TAG, "onActivityResult()");
        int mYear;
        int mMonth;
        int mDay;
        String choiceSaved;
        //Recuperation de l'endroit
        if(requestCode == PLACE_PICKER_REQUEST){
            if(resultCode == RESULT_OK){

                Place place = PlacePicker.getPlace(this, data);
                retrievePlace.setText(place.getAddress());
                retrieveName.setText(place.getName());
                coordinates = place.getLatLng();
                Log.i(TAG, "Data from intent: "+place.getAddress());
                Log.i(TAG, "Data from intent: "+place.getName());
                Log.i(TAG, "Data from intent: "+place.getLatLng().toString());
                saveState[0]=true;
            }
        }
        //Recuperation de la date
        if(requestCode == DATE_PICKER_REQUEST){
            if(resultCode == RESULT_OK){
                mYear = data.getIntExtra("year", -1);
                mMonth = data.getIntExtra("month", -1);
                mDay = data.getIntExtra("day", -1);


                //SimpleDateFormat formater = new SimpleDateFormat("dd-MM-yy", Locale.FRANCE);
                //Date date = SimpleDateFormat.parse(mDay+"-"+mMonth+"-"+mYear);   //parse est une methode non statique qui ne peut pas etre appelee dans ce contexte

                //retrieveDay.setText(String.format("%d",mDay));                      //quickFix
                /*
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
                saveState[1]=true;
            }
            else{
                Log.i(TAG, "Result not ok");
            }
        }
        //Recuperation du type d'evenement
        if (requestCode == EVENT_TYPE_REQUEST){
            if (resultCode == RESULT_OK){
                //boolean eventTypeSaved = true;
                choiceSaved = data.getStringExtra("eventChoosen");
                retrieveEventType = findViewById(R.id.eventSetup_eventType_tv);
                retrieveEventType.setText(choiceSaved);
                saveState[2]=true;
            }
        }
        //Recuperation de la liste d'amis
        if(requestCode == FRIENDS_PICKER_REQUEST){
            if(resultCode==RESULT_OK){
                pickedFriends = data.getStringArrayListExtra("savedFriends");//Ne pas coder en dur
                registeredFriends = (HashMap<String, String>) data.getSerializableExtra("Friends_EMAIL");

                Log.i (TAG, "registeredFriends: "+registeredFriends);
                Log.i(TAG, "pickedFriends_size" +pickedFriends.size());
                StringBuilder sb = new StringBuilder();
                for (int i=0; i<pickedFriends.size(); i++){
                    if(i!=pickedFriends.size()-1) {
                        sb.append(pickedFriends.get(i));
                        sb.append(", ");
                    }
                    else{
                        sb.append(pickedFriends.get(i));
                    }
                }
                String savedFriends = sb.toString();
                retrieveFriends.setText(savedFriends);
                saveState[3] = true;
            }
        }
    }


    /**
     * ******************************************************************Devrait etre deplace en dehors de l'activite
     */

    /**
     * Methode pour creer un identifiant unique a chaque evenement
     * @return l'identifiant unique
     */
    public String setEventID (){
        Log.i(TAG, "setEventID()");
        mAuth = FirebaseAuth.getInstance();
        FirebaseUser user = mAuth.getCurrentUser();
        String userId = user.getUid();
        Log.i(TAG, "EventSetup: userID: "+userId);
        final String ID = (userId+System.currentTimeMillis()).hashCode()+"";
        Log.i(TAG, "EventSetup: userID_hashCode: "+ID);
        return ID;
    }

    /**
     * Enregistre sur Firebase un evenement avec toutes les infos recoltees des
     * differentes activites appelees par les intents
     * @param v Bouton "Sauvegarder"
     */
    public void saveToDB (View v){
        Log.i(TAG, "saveToDB()");
        //S'assurer que tous les champs soient remplis
        mAuth = FirebaseAuth.getInstance();
        FirebaseUser user = mAuth.getCurrentUser();

        final String ID = setEventID();
        String email = user.getEmail();
        String eventName = retrieveEventDescription.getText().toString();
        String date = retrieveDay.getText().toString();
        String debutTime = "";//Gerer l'horaire
        String eventType = retrieveEventType.getText().toString();
        String location = retrieveName.getText().toString()+": "+retrievePlace.getText().toString();
        EventDB myEvent = new EventDB(ID, email, eventName, date, debutTime,eventType, location, longitude, latitude);
        //Insertion dans la DB locale
        if(checkSaveStatus()) {
            mEventModel.insert(myEvent);
            //Insertion sur le serveur en utilisant des threads dedies
            SaveFriendsBackTask backTask = new SaveFriendsBackTask(ID, pickedFriends);
            SaveEventBackTask eventBackTask = new SaveEventBackTask(myEvent, ID);
            Sender sender = new Sender(backTask);
            Sender sender2 = new Sender(eventBackTask);
            sender.start();
            sender2.start();

            this.finish();
        }
        else{
            Toast.makeText(this, getString(R.string.eventSetupFalseSave), Toast.LENGTH_SHORT).show();
        }
    }

    private boolean checkSaveStatus(){
        Log.i(TAG, "checkSaveStatus()");
        String hint = getString(R.string.eventSetup);
        boolean isAllChecked =false;
        Log.i(TAG, "Hint: "+hint+"  EditText: "+retrieveEventDescription.getText().toString());
        if(!retrieveEventDescription.getText().toString().isEmpty()){
            saveState[4]=true;
        }

        for (int i = 0; i<saveState.length; i++){
            if(!saveState[i]){
                isAllChecked = false;
            }
            else{
                isAllChecked = true;
            }
            Log.i(TAG, "saveState: "+i+" :"+saveState[i]);
        }
        return isAllChecked;
    }

    /**
     * L'utilisateur revient vers l'activite Home
     * @param v Bouton "Annuler"
     */
    public void cancelEvent(View v){
        Log.i(TAG, "cancelEvent()");
        finish();
    }


    /**
     * Classe privee etendant Thread
     * Va servir a envoyer sur le serveur l'evenement enregistre
     * ainsi que les amis choisis
     */
    private class Sender extends Thread {
        private Runnable runnable;
        private Sender (Runnable task){
            this.runnable=task;
        }
    }

    /**
     * Runnable SaveEventBackTask
     * la tache appelee par le Thread Sender
     * va envoyer l'evenement sur le serveur
     */
    private class SaveEventBackTask implements Runnable{
        private EventDB mEvent;
        private String eventId;

        private SaveEventBackTask (EventDB eventDB, String ID){
            this.mEvent = eventDB;
            this.eventId = ID;
            run();
        }
        @Override
        public void run() {
            Log.i(TAG, "SaveEventBackTask run()");
            android.os.Process.setThreadPriority(android.os.Process.THREAD_PRIORITY_BACKGROUND);
            sendToFirebase(mEvent, eventId);
        }
    }

    /**
     * Runnable SaveFriendsBackTask
     * la tache appelee par le Thread Sender
     * va envoyer la liste d'amis sur le serveur
     */
    private class SaveFriendsBackTask implements Runnable{
        private String eventID;
        private ArrayList<String> choosenFriends;

        private SaveFriendsBackTask (String ID, ArrayList<String> pickedFriends){
            this.eventID = ID;
            this.choosenFriends = pickedFriends;
            run();
        }
        @Override
        public void run() {
            Log.i(TAG, "SaveFriendsBackTask run()");
            android.os.Process.setThreadPriority(android.os.Process.THREAD_PRIORITY_BACKGROUND);
            sendToFirebase(eventID, choosenFriends);
        }
    }


    /**
     * Methode transmise au Runnable de la classe SaveEventBackTask
     * @param eventDB l'objet evenement
     * @param ID l'identifiant de l'evenement
     */
    public void sendToFirebase(EventDB eventDB, String ID){
        Log.i(TAG, "sendToFirebase(Event)");
        FirebaseHelper firebaseHelper = new FirebaseHelper(mDatabase);
        FirebaseDatabase firebaseDB = firebaseHelper.getmDatabase();
        if (firebaseDB!=null) {
            DatabaseReference myRef = firebaseDB.getReference(ServerUtil.getFirebaseServer_Event()); //Creer le repertoire Event s'il n'existe pas
            myRef.push();
            myRef.child(ID).setValue(eventDB);
        }
        else{
            Toast.makeText(this, getString(R.string.FirebaseErrorToast), Toast.LENGTH_SHORT).show();
        }
    }


    /**
     * Methode transmise au Runnable de la classe SaveFriendBackTask
     * gere l'insertion dans la DB de firebase
     * @param ID l'identifiant de l'evenement
     * @param pickedFriends les amis choisis (! pas utilise)
     */
    public void sendToFirebase(String ID, ArrayList<String> pickedFriends){
        Log.i(TAG, "sendToFirebase(Friends)");
        FirebaseHelper firebaseHelper = new FirebaseHelper(mDatabase);
        FirebaseDatabase firebaseDB = firebaseHelper.getmDatabase();
        if (null!=firebaseDB){
            DatabaseReference myRef = firebaseDB.getReference(ServerUtil.getFirebaseServer_Event_Friend_Asso());
                for (String key:registeredFriends.keySet()) {
                    myRef.child(ID);
                    String randomID = myRef.child(ID).push().getKey();
                    myRef.child(ID).child(randomID).child(ServerUtil.getUserEmailByEvent()).setValue(key);
                    myRef.child(ID).child(randomID).child(ServerUtil.getUserNameByEvent()).setValue(registeredFriends.get(key));
                    myRef.child(ID).child(randomID).child(ServerUtil.getIsNotificationConsumed()).setValue(false);
                    myRef.child(ID).child(randomID).child(ServerUtil.getIsInvitePending()).setValue(true);
                    Log.i(TAG, "Reference key: "+randomID);
                    Log.i(TAG, "key: "+key+" value: "+ registeredFriends.get(key));
                }
        }
        else{
            Toast.makeText(this, getString(R.string.FirebaseErrorToast), Toast.LENGTH_SHORT).show();
        }
    }
}
