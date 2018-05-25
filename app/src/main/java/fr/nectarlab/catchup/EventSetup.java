package fr.nectarlab.catchup;

import android.Manifest;
import android.app.Service;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
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
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GetTokenResult;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;

import fr.nectarlab.catchup.Database.EventDB;
import fr.nectarlab.catchup.Database.Event_Friend_AssocDB;
import fr.nectarlab.catchup.model.EventModel;
import fr.nectarlab.catchup.model.Friend;
import fr.nectarlab.catchup.server_side.FirebaseFriendsByEvent;
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
    String token;

    //private boolean eventTypeSaved;
    ArrayList<String>pickedFriends;
    HashMap<String, String> registeredFriends;
    private TextView retrievePlace, retrieveName, retrieveEventType, retrieveDay, retrieveFriends, retrieveEventDescription;
    //private TextView retrieveDay;
    @Override
    public void onCreate (Bundle b){
        super.onCreate(b);
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
        /*
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
            Log.i(TAG, "onCreate savedEvent: "+b.getString("type"));
            retrieveFriends.setText(b.getString("savedFriends"));
            Log.i(TAG, "onCreate savedFriends: "+b.getString("savedFriends"));
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

        mReference = FirebaseDatabase.getInstance().getReference();
        String test = mReference.child(ServerUtil.getFirebaseServer_Event_Friend_Asso()).getKey();
        Log.i(TAG, "Reference au serveur: "+test);
        Query query = mReference.child(ServerUtil.getFirebaseServer_Event_Friend_Asso());
        HashMap<String, String> userNameEmail = new HashMap<>();
        HashMap<String, HashMap> listedFriends = new HashMap<>();
        final FirebaseFriendsByEvent firebaseFriendsByEvent = new FirebaseFriendsByEvent(listedFriends, userNameEmail);
        if (mDatabase!=null)
            assert query != null;

            query.addChildEventListener(new ChildEventListener() {
                @Override
                public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                    //String v = dataSnapshot.getChildren().iterator().next().getKey();
                    long nbChildren = dataSnapshot.getChildrenCount();
                    Log.i(TAG, "onChildAdded: nbChilder: " + nbChildren);
                    FirebaseFriendsByEvent friend = dataSnapshot.getValue(FirebaseFriendsByEvent.class);
                    for (DataSnapshot child: dataSnapshot.getChildren()) {
                        String newRef = child.getKey();

                       //String v = dataSnapshot.getChildren().iterator().next().getKey();
                      // String newRef = dataSnapshot.child(v).toString();
                       Log.i(TAG, "onChildAdded: Friend's email: " + firebaseFriendsByEvent.getListedFriends());
                      // Log.i(TAG, "onChildAdded: FirebaseFriends " + v);
                       Log.i(TAG, "onChildAdded: newRef: "+newRef);
                       Log.i(TAG, "onChildAdded: child: "+child);

                   }
                }

                @Override
                public void onChildChanged(DataSnapshot dataSnapshot, String s) {

                }

                @Override
                public void onChildRemoved(DataSnapshot dataSnapshot) {

                }

                @Override
                public void onChildMoved(DataSnapshot dataSnapshot, String s) {

                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
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
        Log.i(TAG, "onSave AddressSaved: "+savedAddress);
        Log.i(TAG, "onSave PlaceSaved: "+savedPlaceName);

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
    }

    @Override
    public void onActivityResult (int requestCode, int resultCode, Intent data){
        int mYear;
        int mMonth;
        int mDay;
        String choiceSaved;
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
            }
            else{
                Log.i(TAG, "Result not ok");
            }
        }

        if (requestCode == EVENT_TYPE_REQUEST){
            if (resultCode == RESULT_OK){
                //boolean eventTypeSaved = true;
                choiceSaved = data.getStringExtra("eventChoosen");
                retrieveEventType = findViewById(R.id.eventSetup_eventType_tv);
                retrieveEventType.setText(choiceSaved);
            }
        }

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
            }
        }
    }
    public String setEventID (){
        mAuth = FirebaseAuth.getInstance();
        FirebaseUser user = mAuth.getCurrentUser();
        String userId = user.getUid();
        Log.i(TAG, "EventSetup: userID: "+userId);
        final String ID = (userId+System.currentTimeMillis()).hashCode()+"";
        Log.i(TAG, "EventSetup: userID_hashCode: "+ID);
        return ID;
    }
    public void saveToDB (View v){
        //S'assurer que tous les champs soient remplis
        mAuth = FirebaseAuth.getInstance();
        FirebaseUser user = mAuth.getCurrentUser();

        final String ID = setEventID();
        String email = user.getEmail();
        //String email = "testEmail";
        String eventName = retrieveEventDescription.getText().toString();
        String date = retrieveDay.getText().toString();
        String debutTime = "";//Gerer l'horaire
        String eventType = retrieveEventType.getText().toString();
        String location = retrievePlace.getText().toString();
        EventDB myEvent = new EventDB(ID, email, eventName, date, debutTime,eventType, location);
        //Insertion dans la DB locale
        mEventModel.insert(myEvent);
        //sendNotification("Hey!!!", myEvent);
        //Insertion sur le serveur
        SaveFriendsBackTask backTask = new SaveFriendsBackTask(ID, pickedFriends);
        //SaveEventBackTask eventBackTask = new SaveEventBackTask(ID, email, eventName, date, debutTime, eventType, location);
        SaveEventBackTask eventBackTask = new SaveEventBackTask(myEvent, ID);
        Sender sender = new Sender(backTask);
        Sender sender2 = new Sender(eventBackTask);
        sender.start();
        sender2.start();
        //sendToFirebase(myEvent, ID);
        //sendToFirebase(ID, email, eventName, date, debutTime, eventType, location);
        //sendToFirebase(ID, pickedFriends);
       // sendNotification();
        finish();

    }
    public void cancelEvent(View v){
        finish();
    }


    /*
     * Thread
     */
    private class Sender extends Thread {
        private Runnable runnable;
        private Sender (Runnable task){
            this.runnable=task;
        }
    }

    /*
     * Runnable
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


    /*
     * Methodes a transmettre aux runnables
     */
    public void sendToFirebase(String ID, ArrayList<String> pickedFriends){
        FirebaseHelper firebaseHelper = new FirebaseHelper(mDatabase);
        FirebaseDatabase firebaseDB = firebaseHelper.getmDatabase();
        if (null!=firebaseDB){
            DatabaseReference myRef = firebaseDB.getReference(ServerUtil.getFirebaseServer_Event_Friend_Asso());
                for (String key:registeredFriends.keySet()) {
                    myRef.child(ID);
                    //myRef.push().child(key).setValue(registeredFriends.get(key));
                    String randomID = myRef.child(ID).push().getKey();
                    myRef.child(ID).child(randomID).child("EMAIL").setValue(key);
                    myRef.child(ID).child(randomID).child("USERNAME").setValue(registeredFriends.get(key));
                    Log.i(TAG, "Reference key: "+randomID);
                    Log.i(TAG, "key: "+key+" value: "+ registeredFriends.get(key));
                }
        }
        else{
            Toast.makeText(this, getString(R.string.FirebaseErrorToast), Toast.LENGTH_SHORT).show();
        }
    }

    public void sendToFirebase(EventDB eventDB, String ID){
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


    /*
     * Error Prone...

    public void sendToFirebase(String ID, String email, String eventName, String date, String debutTime, String eventType, String location){
        FirebaseHelper firebaseHelper = new FirebaseHelper(mDatabase);
        FirebaseDatabase firebaseDB = firebaseHelper.getmDatabase();
        if (firebaseDB!=null) {
            DatabaseReference myRef = firebaseDB.getReference(ServerUtil.getFirebaseServer_Event()); //Creer le repertoire Event s'il n'existe pas
            myRef.push();
            // old version
            myRef.child(ID);
            myRef.child(ID).child(ServerUtil.getEMAIL()).setValue(email);
            myRef.child(ID).child(ServerUtil.getEventName()).setValue(eventName);
            myRef.child(ID).child(ServerUtil.getDATE()).setValue(date);
            myRef.child(ID).child(ServerUtil.getDebutTime()).setValue(debutTime);
            myRef.child(ID).child(ServerUtil.getEventType()).setValue(eventType);
            myRef.child(ID).child(ServerUtil.getLOCATION()).setValue(location);
        }
        else{
            Toast.makeText(this, getString(R.string.FirebaseErrorToast), Toast.LENGTH_SHORT).show();
        }
    }


    */


/*

    private class SaveEventBackTask implements Runnable{
        String ID, email, eventName, date, debutTime, eventType, location;
        private SaveEventBackTask (String ID, String email, String eventName, String date, String debutTime, String eventType, String location){
            Log.i(TAG, "SaveEventBackTask created");
            this.ID=ID;
            this.email=email;
            this.eventName=eventName;
            this.date=date;
            this.debutTime=debutTime;
            this.eventType=eventType;
            this.location=location;
            run();
        }

        @Override
        public void run() {
            Log.i(TAG, "SaveEventBackTask run()");
            android.os.Process.setThreadPriority(android.os.Process.THREAD_PRIORITY_BACKGROUND);
            sendToFirebase(ID, email, eventName, date, debutTime, eventType, location);
        }
    }
    */

}
