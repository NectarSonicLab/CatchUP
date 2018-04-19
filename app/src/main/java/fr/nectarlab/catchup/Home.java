package fr.nectarlab.catchup;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;

/**
 * Created by ThomasBene on 4/18/2018.
 */

public class Home extends AppCompatActivity {
    private final String TAG = "Home";
    private DrawerLayout mDrawerLayout;
    private FloatingActionButton mFabMain,mFabExpand;
    private TextView fabDescription;
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

        fabOpen = AnimationUtils.loadAnimation(this, R.anim.fab_open);
        fabClose = AnimationUtils.loadAnimation(this, R.anim.fab_close);
        fabRClock= AnimationUtils.loadAnimation(this, R.anim.rotate_clockwise);
        fabRAntiClock = AnimationUtils.loadAnimation(this, R.anim.rotate_anticlockwise);

        if(b==null){
            launchSplashScreen();
        }
        Log.i(TAG, "onCreate: Fin");
    }
    @Override
    public void onStart(){
        super.onStart();
        //findViewById(R.id.splash_image_img).setVisibility(View.GONE);//pas necessaire
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
