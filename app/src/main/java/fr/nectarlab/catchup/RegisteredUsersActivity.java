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

import fr.nectarlab.catchup.model.Users;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

import java.util.ArrayList;

import static android.R.attr.id;

/**
 * Created by ThomasPiaczinski on 03/04/18.
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
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.READ_CONTACTS)!=PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this, new String []{Manifest.permission.READ_CONTACTS}, 0 );
        }



        setContentView(R.layout.registered_users);

        mDatabase = FirebaseDatabase.getInstance().getReference().child("Users");

        namesFound = getNameEmailDetails();//Lancer sur un thread different

    }

    public void retrieveUsers(String email ) {
        Query mQuery=mDatabase.orderByChild("email").equalTo(email).limitToFirst(1);

        if(FirebaseDatabase.getInstance()!=null)
        assert mQuery != null;
        mQuery.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {

                Users u = dataSnapshot.getValue(Users.class);
                if (u != null)
                    Log.e("test", u.getEmail());
                allUsers.add(u);
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

    public ArrayList<String> getNameEmailDetails(){
        Log.i("getNameEmailDetails", "Start");
        ArrayList<String> names = new ArrayList<String>();
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

