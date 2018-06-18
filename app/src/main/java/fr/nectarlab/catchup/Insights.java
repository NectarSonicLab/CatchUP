package fr.nectarlab.catchup;

import android.app.Activity;
import android.app.ProgressDialog;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;

import android.content.Intent;
import android.content.SharedPreferences;

import android.graphics.Bitmap;

import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;

import android.support.annotation.Nullable;

import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import android.util.Log;

import android.view.View;
import android.view.inputmethod.InputMethodManager;

import android.widget.EditText;
import android.widget.TabHost;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.Serializable;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import fr.nectarlab.catchup.CacheTasks.MediaCacheTask;
import fr.nectarlab.catchup.CacheTasks.MessageCacheTask;
import fr.nectarlab.catchup.Database.EventDB;
import fr.nectarlab.catchup.Database.Media;
import fr.nectarlab.catchup.Database.MediaDAO;
import fr.nectarlab.catchup.Database.Message;
import fr.nectarlab.catchup.Database.MessageDAO;

import fr.nectarlab.catchup.model.MediaModel;
import fr.nectarlab.catchup.model.MessageModel;

import fr.nectarlab.catchup.server_side.FirebaseHelper;
import fr.nectarlab.catchup.server_side.ServerUtil;

/**
 * Insights
 * Va montrer a l'utilisateur le contenu de cet evenement:
 * ChatRoom, media enregistres
 */

public class Insights extends AppCompatActivity implements Serializable {
    private TabHost mTabHost;
    private String TAG = "Insights";
    EventDB mEventDB;
    private Media mMedia;
    private MediaModel mMediaModel;
    private MessageModel mMessageModel;
    private ArrayList<Message> messageArrayList;
    public ArrayList<Media> registeredMedias;
    private FirebaseStorage mStorage;
    private FirebaseDatabase mDatabase;
    private FirebaseAuth mAuth;
    private DatabaseReference mDatabaseRef;
    private StorageReference mStorageRef;
    private String eventID;
    private final int RC_PHOTO_PICKER = 1;
    private Query mQuery;
    private ChildEventListener mChildEventListener;
    private ChildEventListener messageChildEventListener;
    private static Bitmap scaledBM;
    private static String contentUriToString;
    private static Uri selectedImgUri, downloadUrl;
    private RecyclerView mediaRecyclerView, messageRecyclerView;
    MediaListAdapter adapter;
    MessageListAdapter messageListAdapter;
    private ProgressDialog mDialog;
    private EditText messageEditText;
    static SharedPreferences sharedPref;
    private int mediaServerCount;
    LiveData<List<Media>> filtered;
    LiveData<List<Message>> test;
    private MediaCacheTask mediaCacheTask;
    private MessageCacheTask messageCacheTask;
    private FirebaseHelper listener;

    //Activity activity;
    MessageDAO messageDAO;


    @Override
    public void onCreate(Bundle b) {
        super.onCreate(b);
        Log.i(TAG, "onCreate: Debut");
        /*
         * On recupere l'intent passe depuis l'activite Home
         * en extra l'intent contient l'objet event qui va alimenter les Views
         * de cette activite
         */
        Intent i = getIntent();
        this.mEventDB = (EventDB) i.getSerializableExtra(IntentUtils.getEventAdapter_CurrentObject());
        this.eventID = mEventDB.getEventID();

        this.setContentView(R.layout.insights);

        //activity = this;
        this.mDialog = new ProgressDialog(this);
        this.adapter = new MediaListAdapter(this);
        this.messageListAdapter = new MessageListAdapter(this);

        this.registeredMedias = new ArrayList<>();

        this.messageArrayList = new ArrayList<>();
        this.messageEditText = findViewById(R.id.messageTab_messageCompletion_et);

        sharedPref = PreferenceManager.getDefaultSharedPreferences(getBaseContext());


        String ID = mEventDB.getEventID();
        Log.i(TAG, "onCreate ID de l'event: " + ID);

        //Initialisation de la vue avec des elements recuperes sur l'objet arrive par intent
        TextView appName = findViewById(R.id.insights_appName_tv);
        appName.setText(getString(R.string.app_name));
        TextView eventName = findViewById(R.id.Insignts_eventName);
        eventName.setText(mEventDB.getEventName());

        //Initialisation du TabHost
        this.mTabHost = findViewById(R.id.Insights_tabHost_main);
        this.mTabHost.setup();
        TabHost.TabSpec tab1 = mTabHost.newTabSpec(getString(R.string.Insights_media));
        TabHost.TabSpec tab2 = mTabHost.newTabSpec(getString(R.string.Insights_Message));
        tab1.setIndicator(getString(R.string.Insights_media));
        tab1.setContent(R.id.Insights_tab1);
        tab2.setIndicator(getString(R.string.Insights_Message));
        tab2.setContent(R.id.Insights_tab2);

        this.mTabHost.addTab(tab1);
        this.mTabHost.addTab(tab2);



        /*
         * Initialisation de Firebase
         */

        this.mDatabase = FirebaseDatabase.getInstance();
        this.mStorage = FirebaseStorage.getInstance();
        this.mAuth = FirebaseAuth.getInstance();


    }

    @Override
    public void onResume() {
        super.onResume();
        Log.i(TAG, "Resume: Debut");
        /*
         * Mise en place des deux adapters correspondants aux deux tab du TabHost
         * Medias et Message.
         */
        //LiveData<List<Message>> messageForThatEvent = messageDAO.getAllMessagesForThatEvent(eventID);
       // mTabHost.setOnTabChangedListener(new TabHost.OnTabChangeListener() {
           // @Override
           // public void onTabChanged(String tabId) {
                //Log.i(TAG, tabId);
                //if (tabId.equals(getString(R.string.Insights_media))) {
        filtered = classifyMedias();
        this.mMediaModel = ViewModelProviders.of(this).get(MediaModel.class);
        this.mediaRecyclerView = findViewById(R.id.Insights_recycler_rv);
        this.mediaRecyclerView.setAdapter(adapter);
        this.mediaRecyclerView.setLayoutManager(new GridLayoutManager(this, 2));
        filtered.observe(this, new Observer<List<Media>>() {
                        @Override
                        public void onChanged(@Nullable List<Media> media) {
                            adapter.setMedia(media);
                            int mediaLocalCount = adapter.getItemCount();
                            mediaCacheTask = new MediaCacheTask(eventID, mChildEventListener, mediaServerCount, mediaLocalCount, mMediaModel);
                            mediaCacheTask.start();
                            Log.i(TAG, "mediaLocalCount: "+mediaLocalCount);
                            Log.i(TAG, "test: "+media.size());

                        }
                    });


                //if (tabId.equals(getString(R.string.Insights_Message))) {
        test= fetchData();
        this.mMessageModel = ViewModelProviders.of(Insights.this).get(MessageModel.class);
        this.messageRecyclerView = findViewById(R.id.Insights_messageRecycler_rv);
        this.messageRecyclerView.setAdapter(messageListAdapter);
        this.messageRecyclerView.setLayoutManager(new LinearLayoutManager(Insights.this));
        test.observe(this, new Observer<List<Message>>() {//mMediaModel.getAllMessages().observe....
                        @Override
                        public void onChanged(@Nullable List<Message> messages) {//////////////////////////////////messageListAdapter.setMessages(messages);
                            messageListAdapter.setMessages(messages);
                            int messageLocalCount = messageListAdapter.getItemCount();
                            messageCacheTask = new MessageCacheTask(eventID, messageChildEventListener, 0, messageLocalCount, mMessageModel);
                            messageCacheTask.start();
                            Log.i(TAG, "mediaLocalCount: "+messageLocalCount);
                        }
                    });
                    //On cree une instance de FirebaseHelper qui va ecouter les messages recus
                    //pour cette evenement pour les stocker en DB locale
        listener = new FirebaseHelper(mDatabase, mDatabaseRef);
        listener.MessageListener(mMessageModel, eventID);


    }

    @Override
    public void onPause(){
        Log.i(TAG, "onPause: Debut");
        super.onPause();
        filtered.removeObservers(this);
        test.removeObservers(this);
        mediaCacheTask.unregisterListener();
        messageCacheTask.unregisterListener();
        listener.removeListener();
    }


    /**
     * setMediaID(): s'occupe de donner un ID unique a chaque nouveau media
     * @return l'identifiant unique (String)
     */
    public String setMediaID() {
        Log.i(TAG, "setMediaID()");
        mAuth = FirebaseAuth.getInstance();
        FirebaseUser user = mAuth.getCurrentUser();
        String userId = user.getUid();
        Log.i(TAG, "Insights: userID: " + userId);
        final String ID = (userId + System.currentTimeMillis()).hashCode() + "";
        Log.i(TAG, "Insights: userID_hashCode: " + ID);
        return ID;
    }


    /**
     * getUserEmail(): recupere l'email enregistree sur l'instance de Firebase
     * @return l'email de l'utilisateur (String)
     */
    public String getUserEmail() {
        Log.i(TAG, "getUserEmail()");
        mAuth = FirebaseAuth.getInstance();
        FirebaseUser user = mAuth.getCurrentUser();
        String userEmail = user.getEmail();
        return userEmail;
    }

    /**
     * ShowGroupInfo: On lance l'activite EventInfo
     * @param v L'icone servant a lancer la nouvelle activite
     */
    public void ShowGroupInfo(View v) {
        Log.i(TAG, "ShowGroupInfo()");
        Intent i = new Intent(this, EventInfo.class);
        i.putExtra(IntentUtils.getEventAdapter_CurrentObject(), this.mEventDB);
        startActivity(i);
    }

    /**
     * pickPhoto: lance un intent implicite pour recuperer une
     * photo a inserer dans Medias
     * @param v L'icone servant a lancer l'intent
     */
    public void pickPhoto(View v) {
        Log.i(TAG, "pickPhoto()");
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/jpeg");
        intent.putExtra(Intent.EXTRA_LOCAL_ONLY, true);
        startActivityForResult(Intent.createChooser(intent, "Complete action using"), RC_PHOTO_PICKER);
    }

    /*
     * On recupere la photo et on l'insere dans la db locale et distante
     * via sendToFirebase. Lance un dialogue pendant tout le processus
     */
    public void onActivityResult(int requestCode, int resCode, Intent data) {
        Log.i(TAG, "onActivityResult()");
        if (requestCode == RC_PHOTO_PICKER && resCode == RESULT_OK) {
            String progressTitle = getString(R.string.InsightsProgressTitle) + " " + mEventDB.getEventName();
            mDialog.setTitle(progressTitle);
            mDialog.setMessage(getString(R.string.InsightsProgressMessage));
            mDialog.show();
            Date date = Calendar.getInstance().getTime();
            DateFormat formatter = new SimpleDateFormat("EEEE, dd MMMM yyyy, hh:mm:ss.SSS a");

            selectedImgUri = data.getData();
            String mediaID = setMediaID();
            String timeStamp = formatter.format(date);
            contentUriToString = selectedImgUri.getPath();

            Log.i("Bitmap", "Pathname: " + contentUriToString);
            String userEmail = getUserEmail();

            sendToStorage(mediaID, timeStamp, userEmail);
        }
    }

    //TODO deplacer ce qui suit hors de l'activite pour respecter l'architecture

    /**
     * sendToFirebase: s'occupe d'envoyer la photo de retour d'Intent implicite
     * vers Firebase et la DB locale. Libere le dialogue au succes de l'operation
     * @param mediaID l'id du media
     * @param timeStamp la date formatee d'enregistrement
     * @param userEmail l'email de l'user
     */
    private void sendToStorage(final String mediaID, final String timeStamp, final String userEmail) {
        Log.i(TAG, "sendToStorage()");
         /*Envoi de Medias sur FirebaseStorage
          * dans le dossier PHOTOS_FROM_EVENT(+ID de l'event en question)
          * on rajoute un fils avec un nom unique (selectedImgUri==>pas 2 fois la meme photo
          * dans ce dossier)
          * Si ok on peut recuperer l'url
          */
        mStorageRef = mStorage.getReference("PHOTOS_FROM_EVENT: " + eventID);
        final StorageReference photoRef = mStorageRef.child(selectedImgUri.getLastPathSegment());
        photoRef.putFile(selectedImgUri)
                .addOnSuccessListener(this, new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        // When the image has successfully uploaded, we get its download URL
                        downloadUrl = taskSnapshot.getDownloadUrl();
                        Log.i("onSuccess", "success:" + downloadUrl);
                        Log.i(TAG, registeredMedias.toString());
                        //On insere dans la db locale un objet media avec l'url retourne dans son champ content
                        Media mMedia = new Media(mediaID, timeStamp, downloadUrl.toString(), userEmail, eventID);
                        mMediaModel.insert(mMedia);
                        //on libere le dialog
                        mDialog.dismiss();

                        try {
                /*
                 * QuickFix pour voir si ca fonctionne
                 * le probleme est que ca demande trop de memoire
                 * Caused by: java.lang.OutOfMemoryError: Failed to allocate a 51840012 byte allocation with 16777216 free bytes and 37MB until OOM
                 */
                /*
                 * TODO Envoi sur la firebaseDatabase (devra etre dans un Thread)
                 */
                            //On insere dans la db distante le meme objet qu'on vient d'enregistrer en local
                            // pour recuperation future
                            mDatabaseRef = mDatabase.getReference(ServerUtil.getMEDIA());
                            mDatabaseRef.child(mediaID).setValue(mMedia);

                        } catch (NullPointerException npe) {
                            if (registeredMedias == null) {
                                boolean isNull = true;
                                Log.i(TAG, "npe registeredMedias is null " + isNull);
                            }
                        }

                    }
                });

    }


    /**
     * sendMessage: envoi de message sur le serveur
     * @param v
     */
    public void sendMessage(View v) {
        Log.i(TAG, "sendMessage()");
        FirebaseHelper helper = new FirebaseHelper(mDatabase, mDatabaseRef);
        String messageID = helper.setUniqueID();
        String userEmail = sharedPref.getString(SharedPrefUtil.SHAREDPREF_EMAIL, null);
        String messageContent = messageEditText.getText().toString();
        Date date = Calendar.getInstance().getTime();
        DateFormat formatter = new SimpleDateFormat("EEEE, dd MMMM yyyy, hh:mm:ss a");
        String timeStamp = formatter.format(date);
        if (messageContent.isEmpty()) {
            Toast.makeText(this, R.string.messageVide, Toast.LENGTH_SHORT).show();
        } else {
            Message message = new Message(messageID, timeStamp, messageContent, userEmail, this.eventID);
            messageArrayList.add(message);
            //Pour tester uniquement
            messageArrayList.add(new Message("ID", timeStamp, "Other", "test", "eventID"));
            messageListAdapter.setMessages(messageArrayList);
            //
            messageEditText.setText("");
            hideKeyboardFrom(this);

            helper.sendMessageToFirebase(messageID, message);
        }
    }

    /**
     * whoIsSending: Pour changer l'affichage en fonction de qui envoie
     */
    public static boolean whoIsSending(Message message) {
        String userEmail = sharedPref.getString(SharedPrefUtil.SHAREDPREF_EMAIL, null);
        return message.getRef_user_EMAIL().equals(userEmail);
    }

    public static void hideKeyboardFrom(Activity activity) {
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        View view = activity.getCurrentFocus();
        //If no view currently has focus, create a new one, just so we can grab a window token from it
        if (view == null) {
            view = new View(activity);
        }
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }


    /**
     * fetchData: Methode pour filtrer les messages par evenement
     * @return les messages pour cet event precis
     */
    private LiveData fetchData() {
        Log.i(TAG, "fetchData()");
        mMessageModel = new MessageModel(this.getApplication());
        final MessageDAO mDAO = mMessageModel.getmRepository().getmMessageDAO();
        final LiveData<List<Message>> filterMessages = mDAO.getAllMessagesForThatEvent(eventID);
        return filterMessages;
    }

    /**
     * classifyMedias: Methode pour filtrer les medias par event
     * @return les medias pour cet event precis
     */
    private LiveData classifyMedias(){
        Log.i(TAG, "classifyMedias()");
        mMediaModel = new MediaModel(this.getApplication());
        final MediaDAO mediaDAO = mMediaModel.getmRepository().getmMediaDAO();
        final LiveData<List<Media>> filteredMedias = mediaDAO.getAllMediasForThatEvent(eventID);
        return filteredMedias;
    }
}






