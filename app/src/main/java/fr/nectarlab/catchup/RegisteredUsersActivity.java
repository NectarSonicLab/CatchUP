package fr.nectarlab.catchup;

import android.Manifest;
import android.content.ContentResolver;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

import fr.nectarlab.catchup.BackgroundTasks.FindingFriendsRunnable;
import fr.nectarlab.catchup.Database.AppDatabase;
import fr.nectarlab.catchup.R;
import fr.nectarlab.catchup.model.Users;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

import java.util.ArrayList;



/**
 * Created by ThomasPiaczinski on 03/04/18.
 * Nous cherchons à determiner si parmi les contacts de l'utilisateur certains font partie des
 * utilisateurs enregistrés sur le serveur. Si oui, ils font partie des amis de l'utilisateur.
 * Sinon on écoute le serveur et pour chaque nouvel utilisateur on détermine si celui-ci fait
 * partie des contacts de l'utilisateur.
 * Une fois un ami trouvé, on lui créé une View pour pouvoir interagir avec lui(chat, invitation).
 */

public class RegisteredUsersActivity extends AppCompatActivity {
    ArrayList<ContactsContract.Contacts> allContact = new ArrayList<>();
    DatabaseReference mDatabase;
    RecyclerView allUserRecycle;
    ArrayList<Users> allUsers =new ArrayList<>();
    ArrayList<String> namesFound = new ArrayList<String>();




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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
        setContentView(R.layout.registered_users);
        mDatabase = FirebaseDatabase.getInstance().getReference("Users");
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
            //namesFound = getNameEmailDetails();
            Log.i("Register", "onResumeEnd");
        }

        //TODO Ne pas relancer la requête à chaque onCreate. La lancer la permière fois sur un thread different.
        // Les fois suivantes "matcher" entre chaque nouvel utilisateur et la totalité des contacts.


    public void retrieveUsers(String email ) {
        Log.i ("retrieveUsers", "Start");
        Query mQuery=mDatabase.orderByChild("EMAIL").equalTo(email).limitToFirst(1);
        if(FirebaseDatabase.getInstance()!=null)
        assert mQuery != null;
        mQuery.addChildEventListener(new ChildEventListener() {

            /**
             * Fonctionne bien quand on ajoute un contact sur le serveur quand l'activité
             * est active et que le nouvel utilisateur est également un contact (mais doit
             * repasser par un onCreate...moyen...a vérifier)
             * tester si
             * 1)Si c'est la premiere fois que la requête tourne,
             * est-ce que la fonction trouve un ami déjà enregistré sur le serveur
             * 2)Que se passe t-il si aucun child n'est plus jamais ajouté?
             * Conclusion: onChildAdded idéal pour tester si un nouvel utilisateur est également
             * dans les contacts du terminal (Meme recuperer son email et le tester avec les contacts
             * plutot que tester tous les users du serveur avec tous les contacts!)
             * Pour la premiere fois on est obligé de confronter tous les contacts du serveur
             * avec ceux du terminal.
             */
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                //Au lieu de Users utiliser l'objet FriendDB
                Users u = dataSnapshot.getValue(Users.class);
                if (u != null)
                    Log.i("retrieveUsers_Found", ""+u.getEMAIL());
                allUsers.add(u);
                //Proceder a l'insertion dans la BD ici
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
        //getContentResolver() est une methode de la classe android.content.ContextWrapper
        //et implémente une méthode de la classe android.content.Context
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

