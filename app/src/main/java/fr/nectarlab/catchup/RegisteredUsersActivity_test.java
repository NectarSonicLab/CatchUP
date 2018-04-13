package fr.nectarlab.catchup;

import android.Manifest;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.ContentResolver;
import android.content.Context;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

import java.util.ArrayList;
import java.util.List;

import fr.nectarlab.catchup.Database.AppDatabase;
import fr.nectarlab.catchup.Database.RegisteredFriendsDB;
import fr.nectarlab.catchup.model.RegFriendsModel;
import fr.nectarlab.catchup.model.Users;


/**
 * Created by ThomasPiaczinski on 03/04/18.
 * Nous cherchons à determiner si parmi les contacts de l'utilisateur certains font partie des
 * utilisateurs enregistrés sur le serveur. Si oui, ils font partie des amis de l'utilisateur.
 * Sinon on écoute le serveur et pour chaque nouvel utilisateur on détermine si celui-ci fait
 * partie des contacts de l'utilisateur.
 * Une fois un ami trouvé, on lui créé une View pour pouvoir interagir avec lui(chat, invitation).
 */

public class RegisteredUsersActivity_test extends AppCompatActivity {
    ArrayList<ContactsContract.Contacts> allContact = new ArrayList<>();
    DatabaseReference mDatabase;
    AppDatabase roomDB;
    RecyclerView allUserRecycle;
    ArrayList<RegisteredFriendsDB>listedFriends = new ArrayList();
    ArrayList<String> namesFound = new ArrayList<String>();
    private RegFriendsModel mRegFriendModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i("OnCreate", "Debut");
        /**
        *READ_CONTACTS fait partie des "dangerous permissions", elle doit explicitement etre
         * demandee a l'utilisateur. Nous faisons donc une "request permission".
         * Peut etre explicitee (avant la "requestPermissions()") avec la methode
         * shouldShowRequestPermissionRationale() qui propose un explication a l'user sur le
         * besoin de cette permission.
         */
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.READ_CONTACTS)!=PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this, new String []{Manifest.permission.READ_CONTACTS}, 0 );
        }
        //TODO Prevoir le cas ou l'user refuse la permission
        setContentView(R.layout.friends_show_activity);
        RecyclerView recyclerView = findViewById(R.id.recyclerview);
        final FriendsListAdapter adapter = new FriendsListAdapter(this);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        mRegFriendModel = ViewModelProviders.of(this).get(RegFriendsModel.class);
        mRegFriendModel.getAllFriends().observe(this, new Observer<List<RegisteredFriendsDB>>(){
            @Override
            public void onChanged(@Nullable final List<RegisteredFriendsDB> mRegisteredFriendsDB){
                adapter.setFriends(mRegisteredFriendsDB);
            }
        });

        mDatabase = FirebaseDatabase.getInstance().getReference("Users");
        roomDB.getInstance(getApplicationContext());
    }
        /**getNameEmailDetails() est la fonction qui cherche dans les Contacts les noms et
         * emails de tous les contacts de l'utilisateur. S'ils possedent un email alors
         * on teste s'ils sont egalement inscrits sur le serveur. Cette methode fait appel
         * à retrieveUsers qui prend en argument l'email trouvé dans la fiche de contacts
         */
        public void onResume(){
            Log.i("Register", "onResumeStart");
            super.onResume();
            new Thread(new Runnable() {
                @Override
                public void run() {
                namesFound = getNameEmailDetails();
                }
            }).start();
            Log.i("Register", "onResumeEnd");
        }

        //TODO Ne pas relancer la requête à chaque onResume.
        // Les fois suivantes "matcher" entre chaque nouvel utilisateur et la totalité des contacts.


    public void retrieveUsers(String email ) {
        Log.i ("retrieveUsers", "Start");
        Query mQuery=mDatabase.orderByChild("EMAIL").equalTo(email).limitToFirst(1);
        if(FirebaseDatabase.getInstance()!=null)
        assert mQuery != null;//
        mQuery.addChildEventListener(new ChildEventListener() {

           /**
             * Pour la premiere fois on est obligé de confronter tous les contacts du serveur
             * avec ceux du terminal.
             */
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                RegisteredFriendsDB u = dataSnapshot.getValue(RegisteredFriendsDB.class);
                if (u != null)
                    Log.i("retrieveUsers_Found", ""+u.getEMAIL());
                String mail = u.getEMAIL();
                listedFriends.add(u);
                //if (listedFriends.size()>mRegFriendModel.getNumFriends()) {
                    RegisteredFriendsDB friend = new RegisteredFriendsDB(mail);
                    mRegFriendModel.insert(friend);//Conflit avec @Unique a
                //}
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
                        retrieveUsers (email);
                    }
                }
                cur1.close();
            }
            Log.i("getNameEmailDetails", "End");
        }
        return names;
    }
}

