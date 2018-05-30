package fr.nectarlab.catchup;

import android.app.Activity;

import android.app.ProgressDialog;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
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

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import fr.nectarlab.catchup.Database.EventDB;
import fr.nectarlab.catchup.Database.Media;
import fr.nectarlab.catchup.Database.Message;
import fr.nectarlab.catchup.model.EventModel;
import fr.nectarlab.catchup.model.MediaModel;
import fr.nectarlab.catchup.model.MessageModel;
import fr.nectarlab.catchup.server_side.FirebaseHelper;
import fr.nectarlab.catchup.server_side.ServerUtil;

/**
 * Insights
 * Va montrer a l'utilisateur le contenu de cet evenement:
 * ChatRoom, media enregistres
 */

public class Insights extends AppCompatActivity implements Serializable{
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
    private static Bitmap scaledBM;
    private static String contentUriToString;
    private static Uri selectedImgUri, downloadUrl;
    private RecyclerView mediaRecyclerView, messageRecyclerView;
    MediaListAdapter adapter;
    MessageListAdapter messageListAdapter;
    private ProgressDialog mDialog;
    private EditText messageEditText;
    static SharedPreferences sharedPref;
    Activity activity;


    @Override
    public void onCreate(Bundle b){
        super.onCreate(b);

        Intent i = getIntent();
        mEventDB = (EventDB)i.getSerializableExtra(IntentUtils.getEventAdapter_CurrentObject());
        eventID = mEventDB.getEventID();
        setContentView(R.layout.insights);
        activity = this;
        mDialog = new ProgressDialog(this);
        adapter = new MediaListAdapter(this);
        messageListAdapter = new MessageListAdapter(this);

        registeredMedias = new ArrayList<>();
        messageArrayList = new ArrayList<>();
        messageEditText = findViewById(R.id.messageTab_messageCompletion_et);

        sharedPref  = PreferenceManager.getDefaultSharedPreferences(getBaseContext());

        Log.i(TAG, "onCreate: Debut");
        String ID = mEventDB.getEventID();
        Log.i(TAG, "onCreate ID de l'event: "+ID);

        TextView appName = findViewById(R.id.insights_appName_tv);
        appName.setText(getString(R.string.app_name));
        TextView eventName = findViewById(R.id.Insignts_eventName);
        eventName.setText(mEventDB.getEventName());
        mTabHost=findViewById(R.id.Insights_tabHost_main);

        mTabHost.setup();
        TabHost.TabSpec tab1 = mTabHost.newTabSpec(getString(R.string.Insights_media));
        TabHost.TabSpec tab2 = mTabHost.newTabSpec(getString(R.string.Insights_Message));
        //TabHost.TabSpec tab3 = mTabHost.newTabSpec(getString(R.string.Insights_Liens));
        tab1.setIndicator(getString(R.string.Insights_media));
        tab1.setContent(R.id.Insights_tab1);
        tab2.setIndicator(getString(R.string.Insights_Message));
        tab2.setContent(R.id.Insights_tab2);
       // tab3.setIndicator(getString(R.string.Insights_Liens));
       // tab3.setContent(R.id.Insights_tab3);
        mTabHost.addTab(tab1);
        mTabHost.addTab(tab2);
       // mTabHost.addTab(tab3);


        /*
         * Initialisation de Firebase
         */

        mDatabase=FirebaseDatabase.getInstance();
        mStorage=FirebaseStorage.getInstance();
        mAuth=FirebaseAuth.getInstance();

    }

    @Override
    public void onResume(){
        super.onResume();
        mTabHost.setOnTabChangedListener(new TabHost.OnTabChangeListener() {
            @Override
            public void onTabChanged(String tabId) {
                Log.i(TAG, tabId);
                if(tabId.equals(getString(R.string.Insights_media))){
                    mMediaModel = ViewModelProviders.of(Insights.this).get(MediaModel.class);
                    mediaRecyclerView = findViewById(R.id.Insights_recycler_rv);
                    //adapter = new MediaListAdapter(this);
                    mediaRecyclerView.setAdapter(adapter);
                    mediaRecyclerView.setLayoutManager(new GridLayoutManager(Insights.this,2));
                    mMediaModel.getAllMedias().observe(Insights.this, new Observer<List<Media>>() {
                        @Override
                        public void onChanged(@Nullable List<Media> media) {
                            adapter.setMedia(media);
                        }
                    });
                }
                if(tabId.equals(getString(R.string.Insights_Message))){
                    mMessageModel = ViewModelProviders.of(Insights.this).get(MessageModel.class);
                    messageRecyclerView = findViewById(R.id.Insights_messageRecycler_rv);
                    messageRecyclerView.setAdapter(messageListAdapter);
                    messageRecyclerView.setLayoutManager(new LinearLayoutManager(Insights.this));
                    mMessageModel.getAllMessages().observe(Insights.this, new Observer<List<Message>>() {
                        @Override
                        public void onChanged(@Nullable List<Message> messages) {
                            messageListAdapter.setMessages(messages);
                        }
                    });
                    FirebaseHelper listener = new FirebaseHelper(mDatabase, mDatabaseRef);
                    listener.MessageListener(mMessageModel);
                }

            }

        });
//        mMediaModel = ViewModelProviders.of(this).get(MediaModel.class);
//        mediaRecyclerView = findViewById(R.id.Insights_recycler_rv);
//        //adapter = new MediaListAdapter(this);
//        mediaRecyclerView.setAdapter(adapter);
//        mediaRecyclerView.setLayoutManager(new GridLayoutManager(this,2));
//        mMediaModel.getAllMedias().observe(this, new Observer<List<Media>>() {
//            @Override
//            public void onChanged(@Nullable List<Media> media) {
//                adapter.setMedia(media);
//            }
//        });

//        mMessagetModel = ViewModelProviders.of(this).get(MessageModel.class);
//        messageRecyclerView = findViewById(R.id.Insights_messageRecycler_rv);
//        messageRecyclerView.setAdapter(messageListAdapter);
//        messageRecyclerView.setLayoutManager(new LinearLayoutManager(this));
//        mMessagetModel.getAllMessages().observe(this, new Observer<List<Message>>() {
//            @Override
//            public void onChanged(@Nullable List<Message> messages) {
//                messageListAdapter.setMessages(messages);
//            }
//        });





    }
    public String setMediaID (){
        mAuth = FirebaseAuth.getInstance();
        FirebaseUser user = mAuth.getCurrentUser();
        String userId = user.getUid();
        Log.i(TAG, "Insights: userID: "+userId);
        final String ID = (userId+System.currentTimeMillis()).hashCode()+"";
        Log.i(TAG, "Insights: userID_hashCode: "+ID);
        return ID;
    }


    public String getUserEmail(){
        mAuth = FirebaseAuth.getInstance();
        FirebaseUser user = mAuth.getCurrentUser();
        String userEmail = user.getEmail();
        return userEmail;
    }
    /*
     * On lance l'activite EventInfo
     */
    public void ShowGroupInfo(View v){
        Intent i = new Intent (this, EventInfo.class);
        i.putExtra(IntentUtils.getEventAdapter_CurrentObject(), this.mEventDB);
        startActivity(i);
    }
    /*
     *
     */
    public void pickPhoto(View v){
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/jpeg");
        intent.putExtra(Intent.EXTRA_LOCAL_ONLY, true);
        startActivityForResult(Intent.createChooser(intent, "Complete action using"), RC_PHOTO_PICKER);
    }

    /*

     */

    public void onActivityResult(int requestCode, int resCode, Intent data){
        if(requestCode == RC_PHOTO_PICKER && resCode == RESULT_OK){
            String progressTitle=getString(R.string.InsightsProgressTitle)+" "+mEventDB.getEventName();
            mDialog.setTitle(progressTitle);
            mDialog.setMessage(getString(R.string.InsightsProgressMessage));
            mDialog.show();
            Date date = Calendar.getInstance().getTime();
            DateFormat formatter = new SimpleDateFormat("EEEE, dd MMMM yyyy, hh:mm:ss.SSS a");

            selectedImgUri = data.getData();
            String mediaID = setMediaID();
            String timeStamp = formatter.format(date);
            contentUriToString = selectedImgUri.getPath();

            Log.i("Bitmap", "Pathname: "+contentUriToString);
            String userEmail = getUserEmail();

            sendToStorage(mediaID, timeStamp, userEmail);



        }
    }

    public static Bitmap getBitmap(){
        return scaledBM;
    }

    private void sendToStorage(final String mediaID, final String timeStamp, final String userEmail){
         /*Envoi sur FirebaseStorage
          * dans le dossier PHOTOS_FROM_EVENT(+ID de l'event en question)
          * on rajoute un fils avec un nom unique (selectedImgUri==>pas 2 fois la meme photo
          * dans ce dossier)
          * Si ok on peut recuperer l'url
          */
        mStorageRef = mStorage.getReference("PHOTOS_FROM_EVENT: "+eventID);
        final StorageReference photoRef = mStorageRef.child(selectedImgUri.getLastPathSegment());
        photoRef.putFile(selectedImgUri)
                .addOnSuccessListener(this, new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        // When the image has successfully uploaded, we get its download URL
                        downloadUrl = taskSnapshot.getDownloadUrl();
                        // Set the download URL to the message box, so that the user can send it to the database
                        // messageTxt.setText(downloadUrl.toString());
                        Log.i("onSuccess", "success:" +downloadUrl);

                        Log.i(TAG, registeredMedias.toString());
                        Media mMedia = new Media(mediaID, timeStamp, downloadUrl.toString(), userEmail, eventID);
                        mMediaModel.insert(mMedia);
                        mDialog.dismiss();

                        try{
                /*
                 * QuickFix pour voir si ca fonctionne
                 * le probleme est que ca demande trop de memoire
                 * Caused by: java.lang.OutOfMemoryError: Failed to allocate a 51840012 byte allocation with 16777216 free bytes and 37MB until OOM
                 * remplacer par un insert dans la DB directement
                 *
                 *
                 */
                /*
                 * TODO Envoi sur la firebaseDatabase (devra etre dans un Thread)
                 */

                            mDatabaseRef = mDatabase.getReference(ServerUtil.getMEDIA());
                            mDatabaseRef.child(mediaID).setValue(mMedia);

                        }
                        catch (NullPointerException npe){
                            if(registeredMedias==null){
                                boolean isNull = true;
                                Log.i(TAG, "npe registeredMedias is null "+isNull);
                            }
                        }

                    }
                });

    }

    public void sendMessage(View v){
        FirebaseHelper helper = new FirebaseHelper(mDatabase, mDatabaseRef);
        String messageID = helper.setUniqueID();
        String userEmail = sharedPref.getString(SharedPrefUtil.SHAREDPREF_EMAIL, null);
        String messageContent = messageEditText.getText().toString();
        Date date = Calendar.getInstance().getTime();
        DateFormat formatter = new SimpleDateFormat("EEEE, dd MMMM yyyy, hh:mm:ss.SSS a");
        String timeStamp = formatter.format(date);
        if(messageContent.isEmpty()){
            Toast.makeText(this, R.string.messageVide, Toast.LENGTH_SHORT).show();
        }
        else{
            Message message = new Message(messageID, timeStamp, messageContent, userEmail, this.eventID);
            messageArrayList.add(message);
            messageArrayList.add(new Message("ID", timeStamp, "Other", "test", "eventID"));
            messageListAdapter.setMessages(messageArrayList);
            messageEditText.setText("");
            hideKeyboardFrom(activity);

            helper.sendMessageToFirebase(messageID, message);
        }
    }

    /*
     * Pour changer l'affichage en fonction de qui envoit
     */
    public static boolean whoIsSending(Message message){

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
}
