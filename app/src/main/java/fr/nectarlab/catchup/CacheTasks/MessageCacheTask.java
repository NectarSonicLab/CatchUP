package fr.nectarlab.catchup.CacheTasks;

import android.util.Log;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

import fr.nectarlab.catchup.Database.Message;
import fr.nectarlab.catchup.model.MessageModel;
import fr.nectarlab.catchup.server_side.ServerUtil;

/**
 * Created by ThomasBene on 6/13/2018.
 */

public class MessageCacheTask extends Thread {
    private String TAG= "MessageCacheTask";
    private FirebaseDatabase mDatabase;
    private String eventID;
    private ChildEventListener listener;
    private int serverCount, localCount;
    private MessageModel mMessageModel;
    private Query mQuery;

    public MessageCacheTask (String id, ChildEventListener eventListener, int serverCount, int messageCount, MessageModel messageModel){
        this.eventID = id;
        this.listener = eventListener;
        this.serverCount = serverCount;
        this.localCount = messageCount;
        this.mMessageModel = messageModel;
    }

    @Override
    public void run() {
        Log.i(TAG, "run()");
        mDatabase = FirebaseDatabase.getInstance();
        DatabaseReference reference = mDatabase.getReference(ServerUtil.getFirebaseServer_Message());
        mQuery = reference.orderByChild(ServerUtil.getRef_event_ID()).equalTo(eventID);
        mQuery.addChildEventListener(listener = new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                Message message = dataSnapshot.getValue(Message.class);
                if(null != message){
                    Log.i(TAG, "new message found using MessageCacheTask: "+message.getContenu());
                    serverCount++;
                    Log.i(TAG, "messagess sur serveur: "+serverCount+" messsagesLocalCount: "+localCount);
                    if(serverCount>=localCount){
                        Message cachedMessage = new Message(message.getMessageID(), message.getTimeStamp(), message.getContenu(), message.getRef_user_EMAIL(), message.getRef_event_ID());
                        //On insere dans la DB locale sur un autre Thread
                        Register register = new Register (mMessageModel, cachedMessage);
                        register.start();
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
    private class Register extends Thread{
        private MessageModel messageModel;
        private Message message;
        protected Register(MessageModel model, Message message){
            this.messageModel = model;
            this.message = message;
        }
        @Override
        public void run(){
            Log.i(TAG, "Register: run()");
            this.messageModel.insert(message);
        }
    }

    public void unregisterListener(){
        if(null != this.listener){
            mQuery.removeEventListener(this.listener);
        }
    }
}
