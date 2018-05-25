package fr.nectarlab.catchup;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Process;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Layout;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.HeaderViewListAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

import java.util.List;

import fr.nectarlab.catchup.Database.EventDB;
import fr.nectarlab.catchup.Database.RegisteredFriendsDB;
import fr.nectarlab.catchup.model.EventModel;
import fr.nectarlab.catchup.model.Users;


/**
 * Created by ThomasBene on 4/18/2018.
 */

public class Home extends AppCompatActivity {
    private final String TAG = "Home";
    private DrawerLayout mDrawerLayout;
    private FloatingActionButton mFabMain,mFabExpand;
    private TextView fabDescription;
    private NavigationView mNavigationView;
    Animation fabOpen, fabClose, fabRClock, fabRAntiClock;
    private boolean isFabOpen = false;
    private EventModel mEventModel;
    private static int itemCount;
    private int serverEventCount;
    DatabaseReference databaseReference;
    private DatabaseReference userReference;
    private FirebaseAuth mAuth;
    private Query mQuery;
    private ChildEventListener mChildEventListener;
    private ChildEventListener forUserRefChildListener;
    private String userEMAIL, userNAME;

    @Override
    public void onCreate (Bundle b){
        super.onCreate(b);
        Log.i(TAG, "onCreate: Debut");
        setContentView(R.layout.home);

        mEventModel = ViewModelProviders.of(this).get(EventModel.class);
        RecyclerView recyclerView = findViewById(R.id.eventRecycler_RV);
        final EventListAdapter adapter = new EventListAdapter(this);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        mEventModel.getAllEvents().observe(this, new Observer<List<EventDB>>() {
            @Override
            public void onChanged(@Nullable List<EventDB> eventDBS) {
                adapter.setEvents(eventDBS);
                itemCount = adapter.getItemCount();
                Log.i(TAG, "onCreate: itemCount: "+itemCount);
            }
        });

        mDrawerLayout = findViewById(R.id.drawer_layout);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionbar = getSupportActionBar();
        actionbar.setDisplayHomeAsUpEnabled(true);
        actionbar.setHomeAsUpIndicator(R.drawable.ic_menu_black_24dp);

        mFabMain = findViewById(R.id.home_mainFab_fab);
        mFabExpand = findViewById(R.id.home_fabGroup_fab);
        fabDescription = findViewById(R.id.home_fabGroupDescription_fab);



        fabOpen = AnimationUtils.loadAnimation(this, R.anim.fab_open);
        fabClose = AnimationUtils.loadAnimation(this, R.anim.fab_close);
        fabRClock= AnimationUtils.loadAnimation(this, R.anim.rotate_clockwise);
        fabRAntiClock = AnimationUtils.loadAnimation(this, R.anim.rotate_anticlockwise);


        if(b==null){
           // launchSplashScreen();
        }
        Log.i(TAG, "onCreate: Fin");

    }
    @Override
    public void onStart(){
        super.onStart();
        Log.i(TAG, "onStart: Debut");
        Log.i(TAG, "onStart: Fin");
    }

    @Override
    public void onRestart(){
        super.onRestart();
        Log.i(TAG, "onRestart: Debut");
        serverEventCount=0;
        fabPressed (mFabMain);
        Log.i(TAG, "onRestart: Fin");
    }

    @Override
    public void onResume(){
        super.onResume();
        setUserInfo();
        CacheTask task = new CacheTask();
        CacheFromFirebase cacheFromFirebase = new CacheFromFirebase(task);
        cacheFromFirebase.start();

    }

    @Override
    public void onSaveInstanceState(Bundle b){
        Log.i(TAG, "onSaveInstanceState: Debut");
        super.onSaveInstanceState(b);
        Log.i(TAG, "onSaveInstanceState: Fin");
       // b.putBoolean("RanOnce", true);
    }


    @Override
    public void onPause(){
        super.onPause();
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        switch (item.getItemId()){
            case android.R.id.home:
                mDrawerLayout.openDrawer(GravityCompat.START);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void fabPressed (View v){
        if (isFabOpen){
            mFabExpand.setVisibility(View.INVISIBLE);
            mFabExpand.startAnimation(fabClose);
            fabDescription.setVisibility(View.INVISIBLE);
            fabDescription.startAnimation(fabClose);
            mFabMain.startAnimation(fabRAntiClock);
            isFabOpen = false;

        }
        else{
            mFabExpand.setVisibility(View.VISIBLE);
            mFabExpand.startAnimation(fabOpen);
            fabDescription.setVisibility(View.VISIBLE);
            fabDescription.startAnimation(fabOpen);
            mFabMain.startAnimation(fabRClock);
            isFabOpen = true;

        }
    }

    public void launchEventCreation(View v){
        Intent i = new Intent (this, EventSetup.class);
        startActivity(i);
    }

    private class CacheFromFirebase extends Thread{
        private Runnable task;
        public CacheFromFirebase(Runnable runnable){
            this.task=runnable;
        }

    }
    private class CacheTask implements Runnable{
        public CacheTask(){
            run();
        }
        @Override
        public void run(){
            Log.i(TAG, "Runnable run()");
            mAuth = FirebaseAuth.getInstance();
            FirebaseUser user = mAuth.getCurrentUser();
            try{
                String email = user.getEmail();
                Log.i(TAG, email);
                android.os.Process.setThreadPriority(android.os.Process.THREAD_PRIORITY_BACKGROUND);
                databaseReference = FirebaseDatabase.getInstance().getReference("EVENT");

                mQuery=databaseReference.orderByChild("admin").equalTo(email);//.limitToFirst(1)
                if(FirebaseDatabase.getInstance()!=null)
                    assert mQuery != null;//
                mQuery.addChildEventListener(mChildEventListener = new ChildEventListener() {
                    @Override
                    public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                        Log.i(TAG, "onChildAdded: Start");
                        EventDB eventDB = dataSnapshot.getValue(EventDB.class);
                        String token = databaseReference.getKey();
                        if(eventDB!=null){
                            Log.i(TAG, "onChildAdded: newEventRecorded "+eventDB.getEventID()+" ,"+eventDB.getAdmin()+" token: "+token);
                            /*
                             * Faire le cache entre la DB et Firebase au cas ou l'user reinstalle l'app:
                             * Il perd ses events en local mais ils restent sur Firebase donc on va les recuperer ici
                             */
                            serverEventCount++;
                            Log.i(TAG, "events en local: "+itemCount+", events sur serveur: "+serverEventCount);
                            if(serverEventCount>itemCount) {
                                String eventID, admin, eventName, date, debutTime, eventType, location;
                                eventID = eventDB.getEventID();
                                admin = eventDB.getAdmin();
                                eventName = eventDB.getEventName();
                                date = eventDB.getDate();
                                debutTime = eventDB.getDebutTime();
                                eventType = eventDB.getEventType();
                                location = eventDB.getLocation();
                                EventDB cachedEvent = new EventDB(eventID, admin, eventName, date, debutTime, eventType, location);
                                mEventModel.insert(cachedEvent);
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
            catch (NullPointerException NPE){
                Log.i(TAG, "CacheTask Exception");
            }

        }
    }

    private class CacheTaskForUserRetrieval implements Runnable{
        private CacheTaskForUserRetrieval(){this.run();}
        String mail, username;

        @Override
        public void run(){
            Log.i(TAG, "CacheTaskForUserRetrieval() run()" );
            mAuth = FirebaseAuth.getInstance();
            FirebaseUser user = mAuth.getCurrentUser();
            try{
                String email = user.getEmail();
                Log.i(TAG, email);

                android.os.Process.setThreadPriority(android.os.Process.THREAD_PRIORITY_BACKGROUND);
                userReference = FirebaseDatabase.getInstance().getReference("Users");
                mQuery=userReference.orderByChild("EMAIL").equalTo(email);
                if(FirebaseDatabase.getInstance()!=null)
                    assert mQuery != null;//
                mQuery.addChildEventListener(forUserRefChildListener = new ChildEventListener() {
                    @Override
                    public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                        Log.i(TAG, "CacheTaskForUserRetrieval() onChildAdded" );
                        RegisteredFriendsDB u = dataSnapshot.getValue(RegisteredFriendsDB.class);
                        if (u != null) {
                            Log.i("CacheTask Found", "" + u.getEMAIL() + " Username: " + u.getUSERNAME());
                            mail = u.getEMAIL();
                            username = u.getUSERNAME();
                            setUserEMAIL(mail);
                            setUserNAME(username);
                            //On veut envoyer les infos de la requete dans les sharedPref pour sauvegarde locale
                            //pb les recupere apres l'affichage
                        }
                        else{
                            Log.i(TAG, "onChildAdded NoUserRetrieved" );
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
            catch(Exception e){}

        }

    }

    @Override
    public void onDestroy(){
        super.onDestroy();
        mQuery.removeEventListener(mChildEventListener);
        mQuery.removeEventListener(forUserRefChildListener);
    }

    @Override
    public void onBackPressed(){
        this.finish();
    }

    private void setUserInfo(){
         /*
         * Tentative pour recuperer la textView contenue dans la NavigationView et la mettre a jour avec le contenu de sharedPref (user login email, username)
         */
        mNavigationView = findViewById(R.id.nav_view);
        View mHeaderView = mNavigationView.getHeaderView(0);
        /*
         * Reference aux textView pour pouvoir y inserer les noms et Username enregistres dans les SharedPref
         */
        TextView tvUsername = mHeaderView.findViewById(R.id.navHeader_username_tv);
        TextView tvEmail = mHeaderView.findViewById(R.id.navHeader_email_tv);
        SharedPreferences sharedPref  = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
        boolean isFreshlyInstalled = sharedPref.getBoolean(SharedPrefUtil.isACCOUNT_ON_TERMINAL, false);

        if(!isFreshlyInstalled){
            //Recuperer les infos depuis firebase
            Log.i(TAG, "isFreshlyInstalled: "+isFreshlyInstalled+ " si false Query dans Firebase");
            //initialisation du runnable et du thread pour traiter ca en background
            CacheTaskForUserRetrieval taskForUserRetrieval= new CacheTaskForUserRetrieval();
            CacheFromFirebase loginRetrieval = new CacheFromFirebase(taskForUserRetrieval);
            loginRetrieval.start();
            //on met a jour les infos retrouvees dans les sharedPref
            SharedPreferences.Editor editor = sharedPref.edit();
            editor.putString(SharedPrefUtil.SHAREDPREF_EMAIL,getUserEMAIL());
            editor.putString(SharedPrefUtil.SHAREDPREF_USERNAME, getUserNAME());
            if (getUserEMAIL()!=null &&getUserNAME()!=null){
                //la requete vers Firebase ne tournera donc qu'une fois
                editor.putBoolean(SharedPrefUtil.isACCOUNT_ON_TERMINAL, true);
            }
            editor.commit();
        }
        //probleme l'affichage se fait alors que la requete n'a pas encore publie son resultat
        String USERNAME = sharedPref.getString(SharedPrefUtil.SHAREDPREF_USERNAME, "Key not saved");
        String EMAIL = sharedPref.getString(SharedPrefUtil.SHAREDPREF_EMAIL, "Key not saved");
        Log.i(TAG, "Shared USERNAME: "+USERNAME);
        Log.i(TAG, "Shared EMAIL: "+EMAIL);

        ThreadUpdate update = new ThreadUpdate(tvUsername, tvEmail);
        update.start();
        //tvUsername.setText(USERNAME);
        //tvEmail.setText(EMAIL);
    }

    private void setTextInfo(final TextView tvEmail, final TextView tvUsername){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                tvEmail.setText(getUserEMAIL());
                tvUsername.setText(getUserNAME());
            }
        });
    }

    private class ThreadUpdate extends Thread{
       TextView tv1, tv2;
        public ThreadUpdate(TextView tv1, TextView tv2){
            this.tv1=tv1;
            this.tv2=tv2;
        }
        @Override
        public void run(){
            try {
                sleep(1000);
                setTextInfo(tv1, tv2);
            }
            catch(InterruptedException IE){}
        }
    }


    public void setUserEMAIL(String userEMAIL) {
        this.userEMAIL = userEMAIL;
        Log.i(TAG, "userEmail "+userEMAIL);
    }

    public void setUserNAME(String userNAME) {
        this.userNAME = userNAME;
        Log.i(TAG, "userNAME "+userNAME);
    }

    public String getUserEMAIL() {
        Log.i(TAG, "getUserEmail "+this.userEMAIL);
        return userEMAIL;
    }

    public String getUserNAME() {
        Log.i(TAG, "getUserNAME "+this.userNAME);
        return userNAME;
    }
}
