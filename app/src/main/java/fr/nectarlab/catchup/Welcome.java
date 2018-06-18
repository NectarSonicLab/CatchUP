package fr.nectarlab.catchup;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

/**
 * Welcome
 * Cette activite s'occupe de rediriger l'utilisateur suivant qu'il soit enregistre sur le serveur ou pas
 * Il envoit l'user soit vers SignUpActivity, soit vers Home
 */

public class Welcome extends Activity {
    private final String TAG = "Welcome";
    private FirebaseUser user;
    private ImageView screen;
    @Override
    public void onCreate(Bundle b){
        super.onCreate(b);
        Log.i(TAG, "onCreate: debut");
        FirebaseAuth mAuth;
        this.setContentView(R.layout.welcome);
        this.screen = findViewById(R.id.Welcome_image_img);
        //Recuperation d'une instance de FirebaseAuth
        mAuth = FirebaseAuth.getInstance();
        //Recuperation de l'utilisateur au sens Firebase (null si inexistant0
        this.user = mAuth.getCurrentUser();
        Exec executor = new Exec (this, new Splash(this, this.user, this.screen));
        executor.start();
        Log.i(TAG, "onCreate: fin");
    }

    @Override
    public void onResume(){
        super.onResume();
        Log.i(TAG, "onResume: debut");
        Log.i(TAG, "onResume: fin");
    }


    /**
     * Redirection: Lance par un Intent soit SignUpAcivity, soit Home en fonction de user
     * @param user l'usager Firebase, si null: il n'est pas sur le serveur
     */
    private void Redirection (FirebaseUser user){
        Intent i;
        if(this.user==null){
            i = new Intent (this, SignUpActivity.class);
            Log.i(TAG, "Redirection: user==null");
            startActivity(i);
        }
        else{
            i = new Intent (this, Home.class);
            Log.i(TAG, "Redirection: user!=null");
            startActivity(i);
        }
    }

    /**
     * Affiche sur le UI Thread un 'splash Screen'
     */
    private class Exec extends Thread {
        Activity activity;
        Runnable runnable;

        private Exec(Activity activity, Runnable runnable) {
            this.activity = activity;
            this.runnable = runnable;
        }

        @Override
        public void run() {
            try {
                Log.i(TAG, "Exec inside try{}");
                sleep(2000);
                activity.runOnUiThread(this.runnable);
            }
            catch (InterruptedException IE) {
            }
        }
    }


    private class Splash implements Runnable{
        Activity activity;
        FirebaseUser user;
        ImageView img;
        private Splash (Activity myActivity, FirebaseUser user, ImageView img){
            this.activity = myActivity;
            this.user = user;
            this.img = img;
        }
        @Override
        public void run(){
            Log.i(TAG, "Splash inside run()");
            //this.img.setImageResource(R.drawable.catchup_splashscreen_title);
            activity.setContentView(R.layout.welcome);
            Redirection(this.user);
        }
    }
}
