package fr.nectarlab.catchup;

import android.Manifest;
import android.app.Activity;
import android.app.Application;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import fr.nectarlab.catchup.Database.AppDatabase;

import fr.nectarlab.catchup.Database.AppRepository;
import fr.nectarlab.catchup.Database.RegisteredFriendsDAO;
import fr.nectarlab.catchup.Database.RegisteredFriendsDB;
import fr.nectarlab.catchup.Database.ResponseListener;
import fr.nectarlab.catchup.model.RegFriendsModel;

import static android.widget.Toast.LENGTH_SHORT;
import static fr.nectarlab.catchup.Database.AppRepository.getNumFriendsAsyncTask;

import static java.security.AccessController.getContext;


/**
 * RegisteredUsersActivity_test
 * Nous cherchons à determiner si parmi les contacts de l'utilisateur certains font partie des
 * utilisateurs enregistrés sur le serveur. Si oui, ils font partie des amis de l'utilisateur.
 * Sinon on écoute le serveur et pour chaque nouvel utilisateur on détermine si celui-ci fait
 * partie des contacts de l'utilisateur.
 * Une fois un ami trouvé, on lui créé une View pour pouvoir interagir avec lui(chat, invitation).
 */

public class RegisteredUsersActivity_test extends AppCompatActivity implements getNumFriendsAsyncTask.ResponseListener{
    private final String TAG = "CatchUP_RegUsers_test";
    ArrayList<ContactsContract.Contacts> allContact = new ArrayList<>();
    //Reference a la DB de Firebase
    DatabaseReference mDatabase;
    AppDatabase roomDB;
    RecyclerView allUserRecycle;
    //Reference aux amis enregistres sur la DB locale
    ArrayList<RegisteredFriendsDB>listedFriends = new ArrayList<>();
    //Reference aux amis enregistres sur Firebase
    ArrayList<RegisteredFriendsDB>listedFriendsOnline = new ArrayList();
    //Array contenant les noms des contacts de l'user
    ArrayList<String> namesFound = new ArrayList<String>();
    //Reference a l'objet RegFriendsModel
    private RegFriendsModel mRegFriendModel;
    //Champ servant a recuperer le nombre d'amis enregistres dans la DB
    public int registeredFriends;
    private static RegisteredUsersActivity_test RUAInstance;
    private boolean hasRanOnce;



    /*
     * Inutile
     */
   public static RegisteredUsersActivity_test getInstance(Context context){
       //Log.i(TAG, "Objet Instance = "+ context.toString());
       return RegisteredUsersActivity_test.RUAInstance;
   }

    @Override
    protected void onCreate(Bundle b) {
        super.onCreate(b);
        Log.i(TAG, "OnCreate: Debut");

        /*
         *Logique pour ne pas relancer le cursor a chaque changement d'orientation
         */
        if (b!=null){
            hasRanOnce = b.getBoolean("HasRanOnce");
            Log.i(TAG, "SavedBoolean: hasRanOnce value "+hasRanOnce);
        }

        setContentView(R.layout.friends_show_activity);
        mRegFriendModel = ViewModelProviders.of(this).get(RegFriendsModel.class);

        /*
         * Reference aux Users inscrits dans Firebase
         */
        mDatabase = FirebaseDatabase.getInstance().getReference("Users");

        /*
         * Logique pour recuperer le nombre d'amis enrgistres dans la DB depuis l'async task de la classe AppRepository via une interface
         */
        getNumFriendsAsyncTask.ResponseListener listener = new getNumFriendsAsyncTask.ResponseListener() {
            @Override
            public void onResponseReceive(int result) {
                Log.i(TAG, "onResponseReceive "+result);
                registeredFriends = result;
            }
        };
        getNumFriendsAsyncTask asyncTask = new getNumFriendsAsyncTask(mRegFriendModel.getmRepository().getmRegisteredFriendsDAO(), listener);
        asyncTask.execute();

        Log.i(TAG, "OnCreate: Fin");

    }


        @Override
        public void onPause(){
            super.onPause();
            Log.i(TAG, "onPause: Debut");
            //clear de l'array listed friends incremente dans onChildAdded pour repartir de zero
            listedFriends.clear();
            Log.i(TAG, "onPause: Fin");
        }
        @Override
        public void onSaveInstanceState(Bundle b){
            super.onSaveInstanceState(b);
            Log.i(TAG, "onSaveInstanceState: Debut");
            //logique pour garder dans le bundle le fait que la requete a tourne une fois
            b.putBoolean("HasRanOnce", true);
            Log.i(TAG, "onSaveInstanceState: Fin");
        }
        @Override
        public void onResume() {
            super.onResume();
            Log.i(TAG, "onResume: Debut");
            FriendsListHelper.setPickedFriends();
            /*
             * Ne faire la recherche qu'apres le premier OnCreate (Variable dans bundle (HasRanOnce) qui nous
             * indique si la requete a deja tourne une fois)
             * la requete ne sera pas relancee a chaque changement d'orientation
             */
            if (!hasRanOnce){
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        namesFound = getNameEmailDetails();
                        Log.i(TAG, "ThreadStart");
                    }
                }).start();
             }

            /*
             * MAJ de l'affichage
             */
            RecyclerView recyclerView = findViewById(R.id.recyclerview);
            final FriendsListAdapter adapter = new FriendsListAdapter(this);
            recyclerView.setAdapter(adapter);
            recyclerView.setLayoutManager(new LinearLayoutManager(this));

            //mRegFriendModel.getNumFriends();//lance la requete pour obtenir le nombre d'amis enregistres
            mRegFriendModel.getAllFriends().observe(this, new Observer<List<RegisteredFriendsDB>>(){
                @Override
                public void onChanged(@Nullable final List<RegisteredFriendsDB> mRegisteredFriendsDB){
                    Log.i(TAG, "onChanged");
                    adapter.setFriends(mRegisteredFriendsDB);
                }
            });
            Log.i(TAG, "onResume: Fin");
        }

    /*
     * Confrontation des emails recuperes depuis les contacts
         * avec ceux enregistres dans Firebase
         * Si ca match, on enregistre dans la DB locale un nouvel objet RegisteredFriendDB
     */
    public void retrieveUsers(String email ) {
        Log.i ("retrieveUsers", "Start");
        Query mQuery=mDatabase.orderByChild("EMAIL").equalTo(email).limitToFirst(1);
        if(FirebaseDatabase.getInstance()!=null)
        assert mQuery != null;//
        mQuery.addChildEventListener(new ChildEventListener() {

            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                Log.i(TAG, "onChildAdded: Start");
                RegisteredFriendsDB u = dataSnapshot.getValue(RegisteredFriendsDB.class);

                if (u != null)
                    Log.i("retrieveUsers_Found", ""+u.getEMAIL()+" Username: "+u.getUSERNAME());
                String mail = u.getEMAIL();
                String username = u.getUSERNAME();
                listedFriends.add(u);
                Log.i(TAG, "listedFriends: "+listedFriends.size()+" DB registeredFriends: "+registeredFriends);
                if (listedFriends.size()>registeredFriends) {
                    Log.i(TAG, "if (listedFriends.size()>registeredFriends)");
                    RegisteredFriendsDB friend = new RegisteredFriendsDB(mail, username);
                    listedFriendsOnline.add(friend);
                    //mRegFriendModel.insert(friend);//Conflit avec @Unique sauf si insert avec le dernier de l'array
                    mRegFriendModel.insert(listedFriendsOnline.get(listedFriendsOnline.size()-1));
                    //registeredFriends=mRegFriendModel.getNumFriends();
               }
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
            //Reverifier si l'email correspond toujours
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
            //Enlever l'ami des contacts dans l'app
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }

        });
        Log.i("retrieveUsers", "End");
        }


    /**getNameEmailDetails() est la fonction qui cherche dans les Contacts les noms et
     * emails de tous les contacts de l'utilisateur. S'ils possedent un email alors
     * on teste s'ils sont egalement inscrits sur le serveur. Cette methode fait appel
     * à retrieveUsers qui prend en argument l'email trouvé dans la fiche de contacts
     */
    public ArrayList<String> getNameEmailDetails(){
        Log.i("getNameEmailDetails", "Start");
        ArrayList<String> names = new ArrayList<String>();
        /**getContentResolver() est une methode de la classe android.content.ContextWrapper
         *et implémente une méthode de la classe android.content.Context
         **/
        ContentResolver cr = getContentResolver();
        Cursor cur = cr.query(ContactsContract.Contacts.CONTENT_URI,null, null, null, null);
        if (cur.getCount() > 0) {
            while (cur.moveToNext()) {
                String id = cur.getString(cur.getColumnIndex(ContactsContract.Contacts._ID));
                Cursor cur1 = cr.query(
                        ContactsContract.CommonDataKinds.Email.CONTENT_URI, null,
                        ContactsContract.CommonDataKinds.Email.CONTACT_ID + " = ?",
                        new String[]{id}, null);

                while (cur1.moveToNext()) {
                    //to get the contact names
                    String name=cur1.getString(cur1.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
                    Log.i("Name :", name);
                    String email = cur1.getString(cur1.getColumnIndex(ContactsContract.CommonDataKinds.Email.DATA));
                    Log.i("Email", email);
                    if(email!=null){
                        names.add(name);
                        Log.i("getNameEmailDetails", ""+email);
                        /**
                         * Ici on confronte les emails trouves dans les contacts avec ceux de Firebase
                         */
                        retrieveUsers (email);
                    }
                }
                cur1.close();
            }
            Log.i("getNameEmailDetails", "End");
        }
        return names;
    }

    /*
     * Methode abstraite de l'interface ResponseListener
     * qui renvoit de l'AsyncTask (AppRepository getNumFriendsAsyncTask)
     * le nombre d'amis sauves dans la DB locale
     */
    @Override
    public void onResponseReceive(int result) {
        Log.i(TAG, "onResponseReceive");
        this.registeredFriends = result;
        setRegisteredFriends(result);
    }
    //Setter pour le champ registeredFriends
    public void setRegisteredFriends(int value){
        Log.i(TAG, "setRegisteredFriends");
        this.registeredFriends = value;
    }

    /*
     * Renvoit via un Intent a l'activite appelante (EventSetup)
     * les amis invites pour l'evenement
     */
    public void saveEvent(View v){
        Log.i(TAG, "saveEvent()");
        /*
         * savedFriends recoit un array correspondant aux amis coches via les
         * CheckBox de la RecyclerView
         */
        HashMap<String, String> friends = FriendsListHelper.getFriends();
        ArrayList <String> savedFriends = FriendsListHelper.getPickedFriends();
        switch (v.getId()){
            case R.id.friends_show_activity_save_btn:{
                if(savedFriends.size()!=0){
                    //Intent de retour vers EventSetup avec la liste des amis choisis
                    Intent intent = new Intent();
                    intent.putStringArrayListExtra("savedFriends", savedFriends);
                    intent.putExtra("Friends_EMAIL", friends);
                    this.setResult(RESULT_OK, intent);
                    this.finish();
                    break;
                }
                else{
                    Toast.makeText(this, R.string.ToastNoFriends ,LENGTH_SHORT ).show();
                }
                break;
            }
            case R.id.friends_show_activity_cancel_btn:{
                this.finish();
            }
        }
    }
}

