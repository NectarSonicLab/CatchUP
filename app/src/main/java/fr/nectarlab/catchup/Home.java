package fr.nectarlab.catchup;


import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;

import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;

import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
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

import android.util.Log;

import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

import java.util.List;

import fr.nectarlab.catchup.BackgroundTasks.ConnectivitySupervisor;
import fr.nectarlab.catchup.Database.EventDB;
import fr.nectarlab.catchup.Database.RegisteredFriendsDB;
import fr.nectarlab.catchup.model.EventModel;
import fr.nectarlab.catchup.server_side.FirebaseHelper;



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
    private FirebaseDatabase mDatabase;
    DatabaseReference databaseReference;
    private DatabaseReference userReference;
    private FirebaseAuth mAuth;
    private Query mQuery;
    private ChildEventListener mChildEventListener;
    private ChildEventListener forUserRefChildListener;
    ConnectivitySupervisor receiver;
    private String userEMAIL, userNAME;
    private final String IS_FAB_OPEN="isFabOpen";
    static SharedPreferences sharedPref;



    @Override
    public void onCreate (Bundle b){
        super.onCreate(b);
        Log.i(TAG, "onCreate: Debut");
        setContentView(R.layout.home);
        setTitle(R.string.Home_CatchUpEvents);

        //Lancement du broadcast filter qui ecoute l'etat de la connexion internet
        IntentFilter filter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        receiver = new ConnectivitySupervisor();
        this.registerReceiver(receiver, filter);

        //Mise en place de l'adapteur qui gere la RecyclerView comprenant tous les evenements (amins/invites)
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

        //Reference au drawerLayout et a ses options de menu
        mDrawerLayout = findViewById(R.id.drawer_layout);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionbar = getSupportActionBar();
        try{
            actionbar.setDisplayHomeAsUpEnabled(true);
        }
        catch(NullPointerException npe){
            Log.i(TAG, "NPE on actionbar.setDisplayHomeAsUpEnabled");
        }
        try{
            actionbar.setHomeAsUpIndicator(R.drawable.ic_menu_black_24dp);
        }
        catch(NullPointerException npe){
            Log.i(TAG, "NPE on actionbar.setHomeAsUpIndicator(R.drawable.ic_menu_black_24dp)");
        }


        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                item.setChecked(false);
                mDrawerLayout.closeDrawers();
                item.getItemId();
                Log.i(TAG, "id: "+item.getItemId());
                switch (item.getItemId()){
                    case R.id.nav_groups:{
                        Log.i(TAG, "Boite a souvenirs: click");
                        openMemoryBox();
                        break;
                    }
                    case R.id.nav_properties:{
                        Log.i(TAG,"Proprietes: click");
                        mAuth.signOut();
                        showToast();
                        finish();
                        break;
                    }
                }
                return true;
            }
        });

        //Reference au FloatingActionButton (FAB) qui sert a creer des evenements
        mFabMain = findViewById(R.id.home_mainFab_fab);
        mFabExpand = findViewById(R.id.home_fabGroup_fab);
        fabDescription = findViewById(R.id.home_fabGroupDescription_fab);

        //chargement des animations du FloatingActionButton
        fabOpen = AnimationUtils.loadAnimation(this, R.anim.fab_open);
        fabClose = AnimationUtils.loadAnimation(this, R.anim.fab_close);
        fabRClock= AnimationUtils.loadAnimation(this, R.anim.rotate_clockwise);
        fabRAntiClock = AnimationUtils.loadAnimation(this, R.anim.rotate_anticlockwise);

        //Reference aux sharedPref
        sharedPref = PreferenceManager.getDefaultSharedPreferences(getBaseContext());

        //Reference a la DB de Firebase
        mDatabase = FirebaseDatabase.getInstance();

        //On recupere l'etat de l'animation du FAB en cas de basculement
        if(b!=null){
            isFabOpen = b.getBoolean(IS_FAB_OPEN);
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
        Log.i(TAG, "onRestart: Fin");
    }

    @Override
    public void onResume(){
        super.onResume();
        Log.i(TAG, "onResume: Debut");
        setUserInfo();
        CacheTask task = new CacheTask();
        CacheFromFirebase cacheFromFirebase = new CacheFromFirebase(task);
        cacheFromFirebase.start();

        FirebaseUser user = mAuth.getCurrentUser();
        try {
            String email = user.getEmail();
            FirebaseHelper helper = new FirebaseHelper(this.mDatabase, this.databaseReference);
            helper.amIInvited(email, this);
        }
        catch (NullPointerException e){
            Log.i(TAG, "NPE on String email = user.getEmail();");
        }
        Log.i(TAG, "onResume: Fin");
    }

    @Override
    public void onSaveInstanceState(Bundle b){
        Log.i(TAG, "onSaveInstanceState: Debut");
        super.onSaveInstanceState(b);
        b.putBoolean(IS_FAB_OPEN, !isFabOpen);
        Log.i(TAG, "onSaveInstanceState: Fin");
       // b.putBoolean("RanOnce", true);
    }

    @Override
    public void onRestoreInstanceState (Bundle b){
        Log.i(TAG, "onRestoreInstanceState: Debut");
        if(b!=null){
        isFabOpen=b.getBoolean(IS_FAB_OPEN);
        }
        fabPressed(mFabMain);
    }

    @Override
    public void onPause(){
        super.onPause();
        Log.i(TAG, "onPause: Debut");
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        Log.i(TAG, "onOptionsItemSelected()");
        switch (item.getItemId()){
            case android.R.id.home:
                mDrawerLayout.openDrawer(GravityCompat.START);
                if(sharedPref.getString(SharedPrefUtil.getSharedprefProfilePicture(), "default")==null){
                    //L'user n'a pas encore choisi sa photo de profil
                    Log.i(TAG, "profilePicturePath: default");
                }
                else{
                    //Recuperation de l'imageView contenant la photo de l'user
                    ImageView contactPicture = findViewById(R.id.quickContactBadge);
                    String path = sharedPref.getString(SharedPrefUtil.getSharedprefProfilePicture(), "default");
                    Uri uri = Uri.parse(path);
                    Log.i(TAG, "profilePicturePath: "+path);
                    //Insertion du Thumbnail avec glide
                    Glide.with(this).load(uri).apply(RequestOptions.circleCropTransform()).into(contactPicture);
                }
                return true;
        }
        return super.onOptionsItemSelected(item);
    }


    /**
    * openMemoryBox: lance l'activite MemoryBox depuis le menu
    *
    */
    public void openMemoryBox(){
        Log.i(TAG, "openMemoryBox()");
        startActivity(new Intent (this, MemoryBox.class));
    }


    /**
     * showToast: lance un Toast quand l'user click sur l'icone du menu correspondante au SignOut
     */
    public void showToast(){
        Toast.makeText(this, R.string.signOut, Toast.LENGTH_SHORT).show();
    }

    /**
     * fabPressed: Gere l'animation du FloatingActionButton
     * en rendant visible un second button
     * utilise par la methode launcheventCreation
     * @param v le FAB
     */
    public void fabPressed (View v){
        if (isFabOpen){
            Log.i(TAG, "fabPressed(): isFabOpen: "+isFabOpen);
            mFabExpand.setVisibility(View.INVISIBLE);
            mFabExpand.startAnimation(fabClose);
            fabDescription.setVisibility(View.INVISIBLE);
            fabDescription.startAnimation(fabClose);
            mFabMain.startAnimation(fabRAntiClock);
            isFabOpen = false;

        }
        else{
            Log.i(TAG, "fabPressed(): isFabOpen: "+isFabOpen);
            mFabExpand.setVisibility(View.VISIBLE);
            mFabExpand.startAnimation(fabOpen);
            fabDescription.setVisibility(View.VISIBLE);
            fabDescription.startAnimation(fabOpen);
            mFabMain.startAnimation(fabRClock);
            isFabOpen = true;
        }
    }

    /**
     * launchEventCreation: Envoi d'un Intent vers l'activite EventSetup
     * @param v le second button rendu visible par fabPressed()
     */
    public void launchEventCreation(View v){
        Log.i(TAG, "launchEventCreation()");
        Intent i = new Intent (this, EventSetup.class);
        startActivity(i);
    }

/**
 * ************************************************************************************************A deporter dans une autre classe
 */
    /**
     * CacheFromFirebase: Classe interne etendant Thread
     * son role principale est de recuperer les events
     * dans le cas ou l'utilisateur a plusieurs terminaux avec
     * l'app installee ou bien s'il desinstalle puis reinstalle l'app:
     * il peut retrouver ses evenements depuis le serveur
     */
    private class CacheFromFirebase extends Thread{
        private Runnable task;
        private CacheFromFirebase(Runnable runnable){
            this.task=runnable;
        }

    }

    /**
     * CacheTask: classe interne implementant Runnable
     * qui definit le comportement de la tache
     * utilisee pour recuperer les events depuis firebase en utilisant une requete sur le serveur
     */
    private class CacheTask implements Runnable{
        private CacheTask(){
            run();
        }
        @Override
        public void run(){
            Log.i(TAG, "(CacheTask) run()");
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
                            Log.i(TAG, "events sur serveur: "+serverEventCount+" events en local: "+itemCount);
                            if(serverEventCount>itemCount) {
                                String eventID, admin, eventName, date, debutTime, eventType, location;
                                double longitude, latitude;
                                eventID = eventDB.getEventID();
                                admin = eventDB.getAdmin();
                                eventName = eventDB.getEventName();
                                date = eventDB.getDate();
                                debutTime = eventDB.getDebutTime();
                                eventType = eventDB.getEventType();
                                location = eventDB.getLocation();
                                longitude = eventDB.getLongitude();
                                latitude =  eventDB.getLatitude();
                                EventDB cachedEvent = new EventDB(eventID, admin, eventName, date, debutTime, eventType, location, longitude, latitude);
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


    /**
     * CacheTaskForUserRetrieval: classe interne reprenant la meme idee que la classe CacheTask
     * Si l'user reinstalle ou utilise l'app depuis un autre terminal
     * il pourra recuperer ses infos depuis le serveur via cette tache
     */
    private class CacheTaskForUserRetrieval implements Runnable{
        private CacheTaskForUserRetrieval(){this.run();}
        String mail, username;

        @Override
        public void run(){
            final SharedPreferences sharedPref  = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
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
                            SharedPreferences.Editor editor = sharedPref.edit();
                            editor.putString(SharedPrefUtil.SHAREDPREF_EMAIL,mail);
                            editor.putString(SharedPrefUtil.SHAREDPREF_USERNAME, username);
                            editor.apply();
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
            catch(NullPointerException e){
                Log.i(TAG, "NPE on String email = user.getEmail();");
            }
        }

    }


    /**
     * On libere les listeners utilises pour ecouter
     * les donnees ajoutees dans la db de Firebase
     */
    @Override
    public void onDestroy(){
        Log.i(TAG, "onDestroy()");
        super.onDestroy();
        this.unregisterReceiver(receiver);
        mQuery.removeEventListener(mChildEventListener);
        if (forUserRefChildListener!=null) {
            mQuery.removeEventListener(forUserRefChildListener);
        }
    }


    /**
     * Home sera la derniere activite de la pile de tache
     * vu que l'activite SignUp a "clear history" comme parametre dans le Manifest
     */
    @Override
    public void onBackPressed(){
        Log.i(TAG, "onBackPressed()");
        this.finish();
    }


    /**
     * onActivityResult
     * Va traiter les donnees recues en retour d'intent
     */
    @Override
    public void onActivityResult (int reqCode, int resCode, Intent data){
        Log.i(TAG, "onActivityResult()");
        switch (reqCode){
            case IntentUtils.PICK_CONTACT_PHOTO:{
                if (RESULT_OK == resCode){
                    //On vient de recuperer une photo pour mettre a jour la photo de profile
                    ImageView contactPicture = findViewById(R.id.quickContactBadge);
                    final Uri path = data.getData();
                    Glide.with(this).load(path).apply(RequestOptions.circleCropTransform()).into(contactPicture);
                    //On stock l'uri dans les sharedPref
                    try {
                        String pathToPicture = path.toString();
                        SharedPreferences.Editor editor = sharedPref.edit();
                        editor.putString(SharedPrefUtil.getSharedprefProfilePicture(), pathToPicture);
                        editor.apply();
                    }
                    catch(NullPointerException NPE){
                        Log.i(TAG, "NPE on String pathToPicture = path.toString();");
                    }
                }
            }
        }
    }


    /**
     * changeProfilePicture: pour modifier la photo de profile
     * @param v la View editable representant la photo
     * de profile de l'usager
     */
    public void changeProfilePicture(View v){
        Log.i(TAG, "changeProfilePicture()");
        //Ouvrir une nouvelle activite par intent implicite
        Intent i = new Intent(Intent.ACTION_PICK, MediaStore.Images.Thumbnails.EXTERNAL_CONTENT_URI);
        i.setType("image/*");
        startActivityForResult(i, IntentUtils.getPickContactPhoto());

    }


    /**
     * isAdmin: determine en fonction d'un event donne
     * si l'user en est l'administrateur
     * @param event l'event qui sert a tester
     * @return le boolean donnant l'user comme administrateur ou non
     * sera utilise dans le recyclerView pour afficher "admin" ou "invite"
     */
    public static boolean isAdmin(EventDB event) {
        String userEmail = sharedPref.getString(SharedPrefUtil.SHAREDPREF_EMAIL, null);
        return event.getAdmin().equals(userEmail);
    }


    /**
     * setUserInfo: recupere les textView contenues dans la NavigationView et les met a jour avec le contenu de sharedPref (user login email, username)
     * Si les shared sont vides (cas de reinstallation) on va chercher les infos sur Firebase
     * via la classe CacheFromFirebase et le Runnable CacheTaskForUserRetrieval
     */
    private void setUserInfo(){
        Log.i(TAG, "setUserInfo()");
        mNavigationView = findViewById(R.id.nav_view);
        View mHeaderView = mNavigationView.getHeaderView(0);
        /*
         * Reference aux textView pour pouvoir y inserer les noms et Username enregistres dans les SharedPref
         */
        TextView tvUsername = mHeaderView.findViewById(R.id.navHeader_username_tv);
        TextView tvEmail = mHeaderView.findViewById(R.id.navHeader_email_tv);
        SharedPreferences sharedPref  = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
        boolean isFreshlyInstalled = sharedPref.getBoolean(SharedPrefUtil.isACCOUNT_ON_TERMINAL, false);

        //probleme l'affichage se fait alors que la requete n'a pas encore publie son resultat
        String USERNAME = sharedPref.getString(SharedPrefUtil.SHAREDPREF_USERNAME, null);
        String EMAIL = sharedPref.getString(SharedPrefUtil.SHAREDPREF_EMAIL, null);

        if(!isFreshlyInstalled){
            //Si l'utilisateur a re-installe l'app alors recuperer les infos depuis firebase
            Log.i(TAG, "isFreshlyInstalled: "+isFreshlyInstalled+ " si false Query dans Firebase");
            //initialisation du runnable et du thread pour traiter ca en background
            CacheTaskForUserRetrieval taskForUserRetrieval= new CacheTaskForUserRetrieval();
            CacheFromFirebase loginRetrieval = new CacheFromFirebase(taskForUserRetrieval);
            loginRetrieval.start();
            //on met a jour les infos retrouvees depuis Firebase dans les sharedPref
            SharedPreferences.Editor editor = sharedPref.edit();
            //editor.putString(SharedPrefUtil.SHAREDPREF_EMAIL,getUserEMAIL());
            //editor.putString(SharedPrefUtil.SHAREDPREF_USERNAME, getUserNAME());
            if (EMAIL!=null && USERNAME!=null){
                //Si on a bien recupere les infos ne plus refaire l'operation
                //en mettant un boolean dans SharedPref
                //la requete vers Firebase ne tournera donc qu'une fois
                editor.putBoolean(SharedPrefUtil.isACCOUNT_ON_TERMINAL, true);
            }
            editor.commit();
            /*
             Met a jour l'affichage dans le drawer en fonction des infos recuperees
             */
            ThreadUpdate update = new ThreadUpdate(tvUsername, tvEmail);
            update.start();
        }

        else{
            tvEmail.setText(EMAIL);
            tvUsername.setText(USERNAME);
        }


        Log.i(TAG, "Shared USERNAME: "+USERNAME);
        Log.i(TAG, "Shared EMAIL: "+EMAIL);
        Log.i(TAG, "Shared isFreshly "+isFreshlyInstalled);


    }


    /**
     * ThreadUpdate: Classe interne etendant Thread
     */
    private class ThreadUpdate extends Thread{
        TextView tv1, tv2;
        private ThreadUpdate(TextView tv1, TextView tv2){
            this.tv1=tv1;
            this.tv2=tv2;

        }
        @Override
        public void run(){
            try {
                Log.i(TAG, "ThreadUpdate(), inside run, before sleep");
                /*
                 * Temps de latance pour etre sur que la requete sur Firebase est terminee
                 */
                sleep(1000);
                setTextInfo(tv1, tv2);
                Log.i(TAG, "ThreadUpdate(), inside run, after sleep");
            }
            catch(InterruptedException IE){
                Log.i(TAG, "InterruptedException on ThreadUpdate() run() setTextInfo(tv1, tv2);");
            }
        }
    }


    /**
     * setTextInfo: met a jour via un Runnable l'UI
     * Repond a la necessite de mettre a jour deux textView apres un temps de latence
     * @param tvEmail la View contenant l'email dans le DrawerLayout
     * @param tvUsername la View contenant l'username dans le DrawerLayout
     */
    private void setTextInfo(final TextView tvEmail, final TextView tvUsername){
        Log.i(TAG, "setTextInfo()");
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                tvEmail.setText(getUserEMAIL());
                tvUsername.setText(getUserNAME());
            }
        });
    }


    /*
     * Getters et setters
     */
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
