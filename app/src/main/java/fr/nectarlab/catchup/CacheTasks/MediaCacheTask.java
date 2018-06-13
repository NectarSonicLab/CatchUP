package fr.nectarlab.catchup.CacheTasks;

import android.util.Log;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import fr.nectarlab.catchup.Database.Media;
import fr.nectarlab.catchup.model.MediaModel;
import fr.nectarlab.catchup.server_side.ServerUtil;

/**
 * Created by ThomasBene on 6/8/2018.
 */

public class MediaCacheTask extends Thread {
    private String TAG = "MediaCacheTask";
    private FirebaseDatabase mDatabase;
    private String eventID;
    private ChildEventListener listener;
    private int serverCount, localCount;
    private MediaModel mMediaModel;
    private Query mQuery;
    public MediaCacheTask (String id, ChildEventListener eventListener, int serverCount, int mediaCount, MediaModel mediaModel){
        this.eventID = id;
        this.listener = eventListener;
        this.serverCount = serverCount;
        this.localCount = mediaCount;
        this.mMediaModel = mediaModel;
    }
    @Override
    public void run(){
        Log.i(TAG, "run()");
        mDatabase = FirebaseDatabase.getInstance();
        DatabaseReference reference = mDatabase.getReference(ServerUtil.getMEDIA());
        mQuery = reference.orderByChild(ServerUtil.getRef_event_ID()).equalTo(eventID);
        mQuery.addChildEventListener(listener = new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                Media media = dataSnapshot.getValue(Media.class);
                if(null!=media){
                    Log.i(TAG, "new media found using MediaCacheTask: "+media.getContenu());
                    serverCount++;
                    Log.i(TAG, "medias sur serveur: "+serverCount+" mediaLocalCount: "+localCount);
                    if(serverCount>=localCount){
                        Media cachedMedia = new Media (media.getMediaID(), media.getTimeStamp(), media.getContenu(), media.getRef_user_EMAIL(), media.getRef_event_ID());
                        Register register = new Register (mMediaModel, cachedMedia);
                        //mMediaModel.insert(cachedMedia);
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
        private MediaModel mediaModel;
        private Media media;
        protected Register(MediaModel model, Media media){
            this.mediaModel = model;
            this.media = media;
        }
        @Override
        public void run(){
            Log.i(TAG, "Register: run()");
            this.mediaModel.insert(media);
        }
    }

    public void unregisterListener(){
       if(null != this.listener){
            mQuery.removeEventListener(this.listener);
        }
    }
}

