package fr.nectarlab.catchup;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.DatabaseReference;

import fr.nectarlab.catchup.server_side.ServerUtil;


/**
 * SignUpActivity
 * Permet le login ou le signUp a l'app
 */

public class SignUpActivity extends BaseActivity implements
        View.OnClickListener {

    private static final String TAG = "SignUpActivity";
    private final String SHAREDPREF_ID = "User_ID";
    private final String SHAREDPREF_EMAIL = "User_EMAIL";
    private final String SHAREDPREF_USERNAME = "User_USERNAME";

    private TextView mStatusTextView;
    private TextView mDetailTextView;
    private EditText mUsernameField;
    private EditText mEmailField;
    private EditText mPasswordField;
    // [START declare_auth]
    private FirebaseAuth mAuth;
    // [END declare_auth]
    //declaration de la FirebaseDatabase
    private FirebaseDatabase mDatabase;
    //fin de la declaration de la FirebaseDatabase
    private FirebaseAuth.AuthStateListener mAuthListener;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        Log.i(TAG, "onCreate");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_emailpassword);

        // Views
        mStatusTextView = findViewById(R.id.status);
        mDetailTextView = findViewById(R.id.detail);
        mUsernameField = findViewById(R.id.field_username);
        mEmailField = findViewById(R.id.field_email);
        mPasswordField = findViewById(R.id.field_password);

        // Buttons
        findViewById(R.id.email_sign_in_button).setOnClickListener(this);
        findViewById(R.id.email_create_account_button).setOnClickListener(this);
        findViewById(R.id.sign_out_button).setOnClickListener(this);
        findViewById(R.id.verify_email_button).setOnClickListener(this);

        // [START initialize_auth]
        mAuth = FirebaseAuth.getInstance();
        // [END initialize_auth]
        //Initialisation de la Database

        mDatabase = FirebaseDatabase.getInstance();
        //mDatabase.setPersistenceEnabled(true);
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    // User is signed in
                    Log.d("EmailPassword onCreate", "onAuthStateChanged:signed_in:" + user.getUid());
                } else {
                    // User is signed out
                    Log.d("EmailPassword onCreate", "onAuthStateChanged:signed_out");
                }
                // ...
            }
        };
    }

    // [START on_start_check_user]
    @Override
    public void onStart() {
        super.onStart();
        Log.i(TAG, "onStart");
        // Check if user is signed in (non-null) and update UI accordingly.
        mAuth.addAuthStateListener(mAuthListener);
        FirebaseUser currentUser = mAuth.getCurrentUser();
        updateUI(currentUser);
        Log.d(TAG, "OnStart currentUser: "+currentUser);
    }
    // [END on_start_check_user]


    @Override
    public void onStop() {
        super.onStop();
        Log.i(TAG, "onStop");
        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }

    private void createAccount(String email, String password) {
        Log.d(TAG, "createAccount:" + email);
        if (!validateForm()) {
            return;
        }
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        final SharedPreferences.Editor editor = sharedPref.edit();

        showProgressDialog();

        // [START create_user_with_email]
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "createUserWithEmail:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            updateUI(user);
                            DatabaseReference myRef = mDatabase.getReference(ServerUtil.getFirebaseServer_Users());//Creer le repertoire Users s'il n'existe pas
                            //Insertion dans firebase de element recuperes
                            myRef.child(user.getUid());
                            myRef.child(user.getUid()).child(ServerUtil.getFirebaseServer_UserEmail()).setValue(user.getEmail());
                            myRef.child(user.getUid()).child(ServerUtil.getFirebaseServer_UserUsername()).setValue(getUsername());
                            //Insertion dans les SharedPref des memes elements pour avoir une copie locale
                            editor.putString(SharedPrefUtil.SHAREDPREF_ID,user.getUid());
                            editor.putString(SharedPrefUtil.SHAREDPREF_EMAIL, user.getEmail());
                            editor.putString(SharedPrefUtil.SHAREDPREF_USERNAME, getUsername());
                            editor.putBoolean(SharedPrefUtil.isACCOUNT_ON_TERMINAL, true);
                            editor.commit();
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "createUserWithEmail:failure", task.getException());
                            Toast.makeText(SignUpActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                            updateUI(null);
                        }

                        // [START_EXCLUDE]
                        hideProgressDialog();
                        // [END_EXCLUDE]
                    }
                });
        // [END create_user_with_email]
    }

    private void signIn(String email, String password) {
        Log.d(TAG, "signIn:" + email);
        if (!validateForm()) {
            return;
        }

        showProgressDialog();

        // [START sign_in_with_email]
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithEmail:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            updateUI(user);
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithEmail:failure", task.getException());
                            Toast.makeText(SignUpActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                            updateUI(null);
                        }

                        // [START_EXCLUDE]
                        if (!task.isSuccessful()) {
                            mStatusTextView.setText(R.string.auth_failed);
                        }
                        hideProgressDialog();
                        // [END_EXCLUDE]
                    }
                });
        // [END sign_in_with_email]
    }

    private void signOut() {
        mAuth.signOut();
        updateUI(null);
    }

    private void sendEmailVerification() {
        Log.i(TAG, "sendEmailVerification()");
        // Disable button
        findViewById(R.id.verify_email_button).setEnabled(false);
        // Send verification email
        // [START send_email_verification]
        final FirebaseUser user = mAuth.getCurrentUser();
        user.sendEmailVerification()
                .addOnCompleteListener(this, new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        // [START_EXCLUDE]
                        // Re-enable button
                        findViewById(R.id.verify_email_button).setEnabled(true);
                        if (task.isSuccessful()) {
                            Toast.makeText(SignUpActivity.this,
                                    "Verification email sent to " + user.getEmail(),
                                    Toast.LENGTH_SHORT).show();
                        } else {
                            Log.e(TAG, "sendEmailVerification", task.getException());
                            Toast.makeText(SignUpActivity.this,
                                    "Failed to send verification email.",
                                    Toast.LENGTH_SHORT).show();
                        }
                        // [END_EXCLUDE]
                    }
                });
        // [END send_email_verification]
    }

    private boolean validateForm() {
        Log.i(TAG, "validateForm()");
        boolean valid = true;

        String username = mUsernameField.getText().toString();
        if(TextUtils.isEmpty(username)) {
            mUsernameField.setError("Required");
            valid = false;
        }  else{
                mUsernameField.setError(null);
        }

        String email = mEmailField.getText().toString();
        if (TextUtils.isEmpty(email)) {
            mEmailField.setError("Required.");
            valid = false;
        } else {
            mEmailField.setError(null);
        }

        String password = mPasswordField.getText().toString();
        if (TextUtils.isEmpty(password)) {
            mPasswordField.setError("Required.");
            valid = false;
        } else {
            mPasswordField.setError(null);
        }

        return valid;
    }

    private void updateUI(FirebaseUser user) {
        Log.i(TAG, "updateUI()");
        hideProgressDialog();
        if (user != null) {
            mStatusTextView.setText(getString(R.string.emailpassword_status_fmt,
                    user.getEmail(), user.isEmailVerified()));
            mDetailTextView.setText(getString(R.string.firebase_status_fmt, user.getUid()));

            findViewById(R.id.email_password_buttons).setVisibility(View.VISIBLE);//bouton signIn visible
            findViewById(R.id.signed_in_buttons).setVisibility(View.VISIBLE);
            findViewById(R.id.ready_to_continue).setVisibility(View.VISIBLE);
            findViewById(R.id.field_usernameLayout).setVisibility(View.GONE);
            findViewById(R.id.createAccountLayout).setVisibility(View.GONE);
            findViewById(R.id.ready_to_continue).setEnabled(user.isEmailVerified());
            findViewById(R.id.verify_email_button).setEnabled(!user.isEmailVerified());
        } else {
            mStatusTextView.setText(R.string.signed_out);
            mDetailTextView.setText(null);
            findViewById(R.id.email_password_buttons).setVisibility(View.GONE);
            findViewById(R.id.email_password_fields).setVisibility(View.VISIBLE);
            findViewById(R.id.signed_in_buttons).setVisibility(View.GONE);
            findViewById(R.id.ready_to_continue).setVisibility(View.GONE);//if email verified...
            findViewById(R.id.email_password_buttons).setVisibility(View.VISIBLE);
        }

    }

    @Override
    public void onClick(View v) {
        Log.i(TAG, "onClick()");
        int i = v.getId();
        if (i == R.id.email_create_account_button) {
            createAccount(mEmailField.getText().toString(), mPasswordField.getText().toString());
        } else if (i == R.id.email_sign_in_button) {
            signIn(mEmailField.getText().toString(), mPasswordField.getText().toString());
        } else if (i == R.id.sign_out_button) {
            signOut();
        } else if (i == R.id.verify_email_button) {
            sendEmailVerification();//pourrait aller avec createAccountButton
        }
    }

    public void goToHome(View v){
        Log.i(TAG, "goToHome()");
        Intent i = new Intent(this, Home.class);
        startActivity(i);
    }

    public void sendDB(View v){
        //pour tester la copie dans sharedPref>>>>Ok
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        final SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString("test", "TOM");
        editor.apply();
        editor.commit();
        String ID = sharedPref.getString(SHAREDPREF_ID, "Key not saved");
        Log.i(TAG, "Shared ID: "+ID);
        String EMAIL = sharedPref.getString(SHAREDPREF_EMAIL, "Key not saved");
        Log.i(TAG, "Shared EMAIL: "+EMAIL);
        String USERNAME = sharedPref.getString(SHAREDPREF_USERNAME, "Key not saved");
        Log.i(TAG, "Shared USERNAME: "+USERNAME);
        Log.i(TAG, "Shared test " + sharedPref.getString("test", "default"));

        /* pour tester l'insertion de donnees dans la base au debut du developpement
        DatabaseReference myRef = mDatabase.getReference("Users");//Creer le repertoire Users s'il n'existe pas
        myRef.child("UserA").child("ID").setValue(1231);//Dans Users, Creer UserA, puis creer un fils de UserA avec un nom ID et une valeur
        myRef.child("UserA").child("email").setValue("Email@email");//Creer un fils de UserA avec un nom email et une valeur
        myRef.child("UserB").setValue("Test2");//Creer un fils de Users, avec un nom de UserB et une valeur
        myRef.child("UserC").setValue("Test3");//idem
        */
    }

    public String getUsername(){
        return mUsernameField.getText().toString();
    }
}