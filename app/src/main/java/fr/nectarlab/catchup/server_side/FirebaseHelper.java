package fr.nectarlab.catchup.server_side;

import android.app.Activity;
import android.arch.lifecycle.ViewModelProviders;
import android.support.v7.app.AppCompatActivity;
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
import fr.nectarlab.catchup.Database.Message;
import fr.nectarlab.catchup.Insights;
import fr.nectarlab.catchup.model.MessageModel;

/**
 * FirebaseHelper
 * Objet contenant une reference vers la DB enrgistree sur le serveur de Firebase
 */

public class FirebaseHelper extends AppCompatActivity{
    private final String TAG = "FirebaseHelper";
    private FirebaseAuth mAuth;
    /*
     * A Firebase reference represents a particular location in your Database and can be used for reading or writing data to that Database location.
     */
    private DatabaseReference mDatabaseRef;

    /*
     * The entry point for accessing a Firebase Database. You can get an instance by calling getInstance(). To access a location in the database and read or write data, use getReference()
     */
    private FirebaseDatabase mDatabase;


    //constructeur public
    public FirebaseHelper(FirebaseDatabase mDatabase){
        this.mDatabase = mDatabase;
    }

    public FirebaseHelper(FirebaseDatabase mDatabase, DatabaseReference reference){
        this.mDatabase = mDatabase;
        this.mDatabaseRef = reference;
        this.mDatabase = FirebaseDatabase.getInstance();
    }



    public void sendMessageToFirebase (String ID, Message message){
        if (null!=this.mDatabase){
            this.mDatabaseRef = this.mDatabase.getReference(ServerUtil.getFirebaseServer_Message());
            this.mDatabaseRef.push();
            this.mDatabaseRef.child(ID).setValue(message);
        }
    }

    public void MessageListener (final MessageModel messageModel){
        Log.i ("MessageListener", "Start");
        this.mDatabaseRef = this.mDatabase.getReference(ServerUtil.getFirebaseServer_Message());

//       / Query mQuery=this.mDatabaseRef.orderByChild(ServerUtil.getRef_event_ID()).equalTo(email);
//        if(FirebaseDatabase.getInstance()!=null)
//            assert mQuery != null;//
        this.mDatabaseRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                Message message = dataSnapshot.getValue(Message.class);
                if(null!=message){
                    Log.i(TAG, message.getRef_user_EMAIL());
                    String messageId,timeStamp, contenu, userPK, eventID;
                    messageId = message.getMessageID();
                    timeStamp = message.getTimeStamp();
                    contenu = message.getContenu();
                    userPK = message.getRef_user_EMAIL();
                    eventID = message.getRef_event_ID();
                    Message retrievedMessage = new Message (messageId, timeStamp, contenu, userPK, eventID);

                    messageModel.insert(retrievedMessage);
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
        return mDatabaseRef;
    }

    public FirebaseDatabase getmDatabase() {
        return mDatabase;
    }
}
