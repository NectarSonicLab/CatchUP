package fr.nectarlab.catchup.server_side;

import android.app.Application;
import android.content.Context;
import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

import java.util.UUID;

import fr.nectarlab.catchup.Database.EventDB;
import fr.nectarlab.catchup.Database.Event_Friend_AssocDB;
import fr.nectarlab.catchup.Database.Message;
import fr.nectarlab.catchup.model.EventModel;
import fr.nectarlab.catchup.model.Friend;
import fr.nectarlab.catchup.model.FriendAssocEventModel;
import fr.nectarlab.catchup.model.MessageModel;
import fr.nectarlab.catchup.notfication.NotificationFromEvent;

/**
 * FirebaseHelper
 * Objet contenant une reference vers la DB enrgistree sur le serveur de Firebase
 * Cette classe va definir des methodes qui vont s'occuper des differentes requetes a effectuer
 * NB: ces methodes devront a terme etre deportees sur un serveur
 * pour ne pas surcharger l'app une charge de travail trop importante (on utilisera Firebase Function pour ca)
 */

public class FirebaseHelper {
    private final String TAG = "FirebaseHelper";
    private FirebaseAuth mAuth;
    private EventModel mEventModel;
    private Query mQuery;
    /*
     * A Firebase reference represents a particular location in your Database and can be used for reading or writing data to that Database location.
     */
    private DatabaseReference mDatabaseRef;

    /*
     * The entry point for accessing a Firebase Database. You can get an instance by calling getInstance(). To access a location in the database and read or write data, use getReference()
     */
    private FirebaseDatabase mDatabase;
    private ChildEventListener listener;

    //constructeur public
    public FirebaseHelper(FirebaseDatabase mDatabase){
        this.mDatabase = mDatabase;
    }

    public FirebaseHelper(FirebaseDatabase mDatabase, DatabaseReference reference){
        this.mDatabase = mDatabase;
        this.mDatabaseRef = reference;
        this.mDatabase = FirebaseDatabase.getInstance();
    }


    /*
     * Methode pour envoyer un message sur Firebase
     */
    public void sendMessageToFirebase (String ID, Message message){
        if (null!=this.mDatabase){
            this.mDatabaseRef = this.mDatabase.getReference(ServerUtil.getFirebaseServer_Message());
            this.mDatabaseRef.push();
            this.mDatabaseRef.child(ID).setValue(message);
        }
    }


    /*
     * Methode qui ecoute les messages recus par event
     * Les ajoute dans la DB locale
     *
     */
    public void MessageListener (final MessageModel messageModel, String ID){
        Log.i ("MessageListener", "Start");

        this.mDatabaseRef = this.mDatabase.getReference(ServerUtil.getFirebaseServer_Message());
        mQuery=this.mDatabaseRef.orderByChild(ServerUtil.getRef_event_ID()).equalTo(ID);
        if(FirebaseDatabase.getInstance()!=null)
            assert mQuery != null;//
        mQuery.addChildEventListener(listener = new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {

                Message message = dataSnapshot.getValue(Message.class);
                if(null!=message){
                    Log.i(TAG, "MessageListener() "+message.getRef_user_EMAIL());
                    final String messageId,timeStamp, contenu, userPK, eventID;
                    messageId = message.getMessageID();
                    timeStamp = message.getTimeStamp();
                    contenu = message.getContenu();
                    userPK = message.getRef_user_EMAIL();
                    eventID = message.getRef_event_ID();
                    Log.i ("MessageListener", "Message recu dans l'event "+eventID);
                    Runnable runnable = new Runnable() {
                        @Override
                        public void run() {
                            final Message retrievedMessage = new Message (messageId, timeStamp, contenu, userPK, eventID);
                            messageModel.insert(retrievedMessage);
                        }
                    };
                    //final Message retrievedMessage = new Message (messageId, timeStamp, contenu, userPK, eventID);
                    //messageModel.insert(retrievedMessage);
                    Thread myThread = new Thread (runnable);
                    myThread.start();
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

    /** amIInvited: Methode pour ecouter la database de Firebase et reperer si l'user
     * a ete invite par un autre user
     * methode utilisee par l'activite Home
     * @param userEMAIL l'email de l'user du terminal
     * @param context le contexte de l'application
     */
    public void amIInvited(final String userEMAIL, final Context context){
        Log.i(TAG, "amIInvited: init ");
          /*
         * Recuperer les amis par Events:
         * Si l'user fait partie de la requete alors lui notifier
         */
         this.mDatabaseRef = this.mDatabase.getReference();
         mQuery = this.mDatabaseRef.child(ServerUtil.getFirebaseServer_Event_Friend_Asso());
         final String reference = this.mDatabaseRef.child(ServerUtil.getFirebaseServer_Event_Friend_Asso()).getKey();
         Log.i(TAG, "Reference au serveur: "+reference);
         if(this.mDatabase!=null)
             assert mQuery!=null;
         mQuery.addChildEventListener(listener = new ChildEventListener() {
             @Override
             public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                 Log.i(TAG, "amIInvited: retrieving infos: ");
                 long nbChild = dataSnapshot.getChildrenCount();
                 for (DataSnapshot child: dataSnapshot.getChildren()) {
                     //Recuperation de l'event ID
                     String eventID = dataSnapshot.getKey().toString();
                     Log.i(TAG, "onChildAdded: eventID: "+eventID);
                     //On se place pour chaque evenement a tous les amis invites pour cet evenement
                     String newRef = child.getKey();
                     DataSnapshot imbrique = dataSnapshot.child(newRef);
                     Log.i(TAG, "onChildAdded: newRef: "+newRef);
                     //On affiche dans le Logcat les infos recuperes (cle unique, valeur USERNAME, EMAIL)
                     //ex: onChildAdded: child: DataSnapshot { key = -LDqUtoGRvII1Mv797UL, value = {USERNAME=TestEmail1Username, EMAIL=testEmail@gmail.com} }
                     //On "match" avec un objet Friend.java
                     Friend user = imbrique.getValue(Friend.class);
                     Log.i(TAG, "onChildAdded: child: "+child);
                     //Personne ne nous a invite
                     if (user == null){
                         Log.i(TAG, "amIInvited: no invitation yet! ");
                     }
                     //Quelqu'un nous a invite, il va falloir envoyer une notification
                     else{
                         //Pour tester: quels amis ont ete invites a quels evenements...
                         Log.i(TAG, "amIInvited: found these invited Friends on Firebase: "+user.getEMAIL()+" invited at this event: "+eventID);
                         if(userEMAIL.equals(user.getEMAIL())){
                             //Notre email a ete trouve sur le serveur, on peut envoyer une requete
                             Log.i(TAG, "amIInvited: found you on Firebase: "+user.getEMAIL()+" invited at this event: "+eventID);
                             Log.i(TAG, "notificationConsumed: "+user.getIS_NOTIFICATION_CONSUMED()+" InvitePending: "+user.getIS_INVITE_PENDING());
                             /*
                              * envoyer une notification dans le cas ou l'email de l'user est trouve
                              * faire une seconde requete Firebase pour recuperer l'event
                              * faire un systeme pour ne pas avoir la meme invitation plusieurs fois
                              *
                              */
                             if(!user.getIS_NOTIFICATION_CONSUMED()) {
                                 NotificationFromEvent NFE = new NotificationFromEvent(context);
                                 NFE.setNotificationContent(eventID);
                                 mDatabaseRef.child(reference).child(eventID).child(newRef).child(ServerUtil.getIsNotificationConsumed()).setValue(true);
                             }
                         }
                     }
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

    /**
     * whoIsInvited: retrouver sur Firebase qui a ete invite pour quel evenement
     * methode utilisee par l'activite EventInfo
     * @param eventID l'id de l'event en question
     * @param friendAssocEventModel l'objet java a rajouter dans la db locale si la requete est fructueuse
     */
    public void whoIsInvited(final String eventID, final FriendAssocEventModel friendAssocEventModel){

        Log.i(TAG, "whoIsInvited: init");
        this.mDatabaseRef = this.mDatabase.getReference();
        mQuery = this.mDatabaseRef.child(ServerUtil.getFirebaseServer_Event_Friend_Asso());
        final String reference = this.mDatabaseRef.child(ServerUtil.getFirebaseServer_Event_Friend_Asso()).getKey();
        Log.i(TAG, "whoIsInvited Reference au serveur: "+reference);
        if(this.mDatabase!=null)
            assert mQuery!=null;
        mQuery.addChildEventListener(listener = new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                for (DataSnapshot child: dataSnapshot.getChildren()) {
                    //Recuperation de l'event ID
                    String serverEventID = dataSnapshot.getKey();
                    Log.i(TAG, "whoIsInvited: retrieving infos: "+serverEventID);
                    String newRef = child.getKey();
                    DataSnapshot imbrique = dataSnapshot.child(newRef);
                    Log.i(TAG, "whoIsInvited onChildAdded: newRef: "+newRef);
                    //On affiche dans le Logcat les infos recuperes (cle unique, valeur USERNAME, EMAIL)
                    //ex: onChildAdded: child: DataSnapshot { key = -LDqUtoGRvII1Mv797UL, value = {USERNAME=TestEmail1Username, EMAIL=testEmail@gmail.com} }
                    Friend friend = imbrique.getValue(Friend.class);
                    Log.i(TAG, "whoIsInvited serventId: "+serverEventID+" eventID: "+eventID);
                    if(serverEventID.equals(eventID)){
                        Log.i(TAG, "whoIsInvited Found these Friends Invited: "+friend.getEMAIL()+" for that event: "+eventID);
                        //Une fois les infos recuperes on les insere dans la dbLocale avec comme id ce qui a ete sauvegarde sur le serveur (newRef)
                        Event_Friend_AssocDB friend_assocDB = new Event_Friend_AssocDB(newRef, friend.getEMAIL(), friend.getUSERNAME(), eventID);
                        Log.i(TAG, "toString "+friend_assocDB.toString());
                        friendAssocEventModel.insert(friend_assocDB);
                    }
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

    /**
     * retrieveEvent: Methode utilisee par l'activity Invitation pour retrouver
     * l'event correspondant a la notification recue
     */
    public void retrieveEvent (final String eventID, final Application application){
        Log.i(TAG, "retrieveEvent: debut");

        Runnable getThatEvent = new Runnable() {
            @Override
            public void run() {

                Log.i(TAG, "retrieveEvent insideRun()");
                android.os.Process.setThreadPriority(android.os.Process.THREAD_PRIORITY_BACKGROUND);
                mDatabaseRef = mDatabase.getReference(ServerUtil.getFirebaseServer_Event());
                mQuery = mDatabaseRef.orderByChild("eventID").equalTo(eventID);
                Log.i(TAG, ""+(mDatabaseRef.child(eventID).toString()));
                mQuery.addChildEventListener(listener = new ChildEventListener() {
                    @Override
                    public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                       EventDB eventDB = dataSnapshot.getValue(EventDB.class);
                        String eventIDFound = dataSnapshot.getKey();
                        Log.i(TAG, "retrieveEvent eventIdFound: "+eventIDFound);

                        if (eventDB != null) {
                            Log.i(TAG, "retrieveEvent onChildAdded: eventID " + eventDB.getEventID() + " eventID admin: " + eventDB.getAdmin());
                            EventDB retrievedEvent = new EventDB(eventDB.getEventID(), eventDB.getAdmin(), eventDB.getEventName(), eventDB.getDate(), eventDB.getDebutTime(), eventDB.getEventType(),
                                    eventDB.getLocation(), eventDB.getLongitude(), eventDB.getLatitude());
                            /*
                             * sauvegarde en locale en tant que PendingEvent
                             * pour laisser le choix a l'utilisateur d'accepter ou refuser une invitation
                             */

                            /*
                             * QuickFix l'inclure directement dans les evenements
                             */
                            mEventModel = new EventModel(application);
                            Log.i(TAG, "toString "+retrievedEvent.toString());
                            mEventModel.insert(retrievedEvent);
                        }
                        else{
                            Log.i(TAG, "nothing found");
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
        };
        Thread search = new Thread (getThatEvent);
        search.start();
        Log.i(TAG, "retrieveEvent end");
    }

    public void removeListener(){
        if (this.listener!=null) {
            this.mQuery.removeEventListener(this.listener);
        }
    }

    /**
     * setUniqueID: mecanisme simple pour generer une ID unique
     * @return cette id generee
     */
    public String setUniqueID (){
        mAuth = FirebaseAuth.getInstance();
        FirebaseUser user = mAuth.getCurrentUser();
        String userId = user.getUid();
        UUID unique = java.util.UUID.randomUUID();
        Log.i(TAG, "setUniqueID: "+unique);
        final String ID = (userId+System.currentTimeMillis()).hashCode()+"";
        Log.i(TAG, "setUniqueID: userID_hashCode: "+ID);
        return unique.toString();
    }

    public DatabaseReference getmDatabaseRef() {
        return this.mDatabaseRef;
    }

    public FirebaseDatabase getmDatabase() {
        return this.mDatabase;
    }

    public ChildEventListener getListener() {
        return listener;
    }
}
