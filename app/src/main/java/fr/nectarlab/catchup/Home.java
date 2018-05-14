package fr.nectarlab.catchup;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
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

    @Override
    public void onCreate (Bundle b){
        super.onCreate(b);
        Log.i(TAG, "onCreate: Debut");
        setContentView(R.layout.home_test);

        mDrawerLayout = findViewById(R.id.drawer_layout);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionbar = getSupportActionBar();
        actionbar.setDisplayHomeAsUpEnabled(true);
        actionbar.setHomeAsUpIndicator(R.drawable.ic_menu_black_24dp);

        mFabMain = findViewById(R.id.home_mainFab_fab);
        mFabExpand = findViewById(R.id.home_fabGroup_fab);
        fabDescription = findViewById(R.id.home_fabGroupDescription_fab);
        /**
         * Tentative pour recuperer la textView contenue dans la NavigationView et la mettre a jour avec le contenu de sharedPref (user login email, username)
         */
        mNavigationView = findViewById(R.id.nav_view);
        View mHeaderView = mNavigationView.getHeaderView(0);
        /**
         * Reference aux textView pour pouvoir y inserer les noms et Username enregistres dans les SharedPref
         */
        TextView tvUsername = mHeaderView.findViewById(R.id.navHeader_username_tv);
        TextView tvEmail = mHeaderView.findViewById(R.id.navHeader_email_tv);
        SharedPreferences sharedPref  = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
        String USERNAME = sharedPref.getString(getString(R.string.SharedPrefUSERNAME_KEY), "Key not saved");
        Log.i(TAG, "Shared USERNAME: "+USERNAME);
        String EMAIL = sharedPref.getString(getString(R.string.SharedPrefUserEMAIL_KEY), "Key not saved");
        Log.i(TAG, "Shared EMAIL: "+EMAIL);
        tvUsername.setText(USERNAME);
        tvEmail.setText(EMAIL);


        fabOpen = AnimationUtils.loadAnimation(this, R.anim.fab_open);
        fabClose = AnimationUtils.loadAnimation(this, R.anim.fab_close);
        fabRClock= AnimationUtils.loadAnimation(this, R.anim.rotate_clockwise);
        fabRAntiClock = AnimationUtils.loadAnimation(this, R.anim.rotate_anticlockwise);



        if(b==null){
            launchSplashScreen();
        }
        Log.i(TAG, "onCreate: Fin");
        /**
         * I/Home: onCreate: Fin
         04-20 08:57:43.797 6326-6326/fr.nectarlab.catchup I/Choreographer: Skipped 30 frames!  The application may be doing too much work on its main thread.
         */
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
        fabPressed (mFabMain);
        //findViewById(R.id.splash_image_img).setVisibility(View.GONE);//pas necessaire
        Log.i(TAG, "onRestart: Fin");
    }


    @Override
    public void onSaveInstanceState(Bundle b){
        Log.i(TAG, "onSaveInstanceState: Debut");
        super.onSaveInstanceState(b);
        Log.i(TAG, "onSaveInstanceState: Fin");
       // b.putBoolean("RanOnce", true);
    }
    public void launchSplashScreen(){
        new CountDownTimer(3000, 1000){
            @Override
            public void onTick(long millisUntilFinished) {
                findViewById(R.id.splash_image_img).setVisibility(View.VISIBLE);
                findViewById(R.id.home_mainFab_fab).setVisibility(View.GONE);
            }

            @Override
            public void onFinish() {
                findViewById(R.id.splash_image_img).setVisibility(View.GONE);
                findViewById(R.id.home_mainFab_fab).setVisibility(View.VISIBLE);
            }
        }.start();
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
}
