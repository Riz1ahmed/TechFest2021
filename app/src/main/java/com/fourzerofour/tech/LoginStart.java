package com.fourzerofour.tech;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.IdpResponse;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Arrays;
import java.util.List;

public class LoginStart extends AppCompatActivity {

    //Firebase AUth
    private FirebaseUser user;
    private FirebaseAuth mFirebaseAuth;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener; //For going to Account Activity Page
    //Firebase Checking User Data Saved or Not


    ///EMAIL
    private static final String TAGO = "LoginStart";
    int AUTHUI_REQUEST_CODE = 10001;
    public static final int RC_SIGN_IN = 1;
    // creating an auth listener for our Firebase auth
    private FirebaseAuth.AuthStateListener mAuthStateListner;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_start);

        // below line is for getting instance
        // for our firebase auth
        mFirebaseAuth = FirebaseAuth.getInstance();
        // display inside our app.
        List<AuthUI.IdpConfig> providers = Arrays.asList(

                // below is the line for adding
                // email and password authentication.
                new AuthUI.IdpConfig.EmailBuilder().build(),

                // below line is used for adding google
                // authentication builder in our app.
                //new AuthUI.IdpConfig.GoogleBuilder().build(),

                // below line is used for adding phone
                // authentication builder in our app.
                new AuthUI.IdpConfig.PhoneBuilder().build());
        // below line is used for calling auth listener
        // for oue Firebase authentication.
        mAuthStateListner = new FirebaseAuth.AuthStateListener() {
            @SuppressLint("ResourceType")
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {

                // we are calling method for on authentication state changed.
                // below line is used for getting current user which is
                // authenticated previously.
                FirebaseUser user = firebaseAuth.getCurrentUser();

                // checking if the user
                // is null or not.
                if (user != null) {
                    Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                    startActivity(intent);
                    // if the user is already authenticated then we will
                    // redirect our user to next screen which is our home screen.
                    // we are redirecting to new screen via an intent.
                    //Intent i = new Intent(this, MainActivity.class);
                    //startActivity(i);
                    // we are calling finish method to kill or
                    // mainactivity which is displaying our login ui.
                    //finish();
                } else {
                    // this method is called when our
                    // user is not authenticated previously.
                    startActivityForResult(
                            // below line is used for getting
                            // our authentication instance.
                            AuthUI.getInstance()
                                    // below line is used to
                                    // create our sign in intent
                                    .createSignInIntentBuilder()

                                    // below line is used for adding smart
                                    // lock for our authentication.
                                    // smart lock is used to check if the user
                                    // is authentication through different devices.
                                    // currently we are disabling it.
                                    .setIsSmartLockEnabled(false)

                                    // we are adding different login providers which
                                    // we have mentioned above in our list.
                                    // we can add more providers according to our
                                    // requirement which are available in firebase.
                                    .setAvailableProviders(providers)

                                    // below line is for customizing our theme for
                                    // login screen and set logo method is used for
                                    // adding logo for our login page.
                                    //.setLogo(R.drawable.gfgimage).setTheme(R.style.Theme)

                                    // after setting our theme and logo
                                    // we are calling a build() method
                                    // to build our login screen.
                                    .build(),
                            // and lastly we are passing our const
                            // integer which is declared above.
                            RC_SIGN_IN
                    );
                }
            }
        };
    }
    /*@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Log.d(TAGO, "Quiz: Login Start");
        if(FirebaseAuth.getInstance().getCurrentUser() == null){
            handleLoginRegister();
        }else{
            Intent intent = new Intent(LoginStart.this, MainActivity.class);
            startActivity(intent);
        }

    }*/

    @Override
    protected void onResume() {
        super.onResume();
        // we are calling our auth
        // listener method on app resume.
        mFirebaseAuth.addAuthStateListener(mAuthStateListner);
    }

    @Override
    protected void onPause() {
        super.onPause();
        // here we are calling remove auth
        // listener method on stop.
        mFirebaseAuth.removeAuthStateListener(mAuthStateListner);
    }

    public void handleLoginRegister(){     //EMAIL
        Log.d(TAGO, "Quiz: handleLoginRegister()");
        List<AuthUI.IdpConfig> provider = Arrays.asList(
                new AuthUI.IdpConfig.EmailBuilder().build(),
                new AuthUI.IdpConfig.GoogleBuilder().build(),
                new AuthUI.IdpConfig.PhoneBuilder().build()
        );

        Intent intent = AuthUI.getInstance()
                .createSignInIntentBuilder()
                .setAvailableProviders(provider)
                .setTosAndPrivacyPolicyUrls("https://google.com", "https://facebook.com")
                .setLogo(R.drawable.logo)
               .setTheme(R.style.LoginTheme)
                .build(),RC_SIGN_IN;

        Log.d(TAGO,"Quiz: handleLoginRegister End ()");
        startActivityForResult(intent,AUTHUI_REQUEST_CODE);
    }


    @Override   //handleLoginRegister will call this method
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {   //EMAIL
        Log.d(TAGO,"Quiz:" +" onActivityResult()");
        if(requestCode == AUTHUI_REQUEST_CODE){     //EMAIL
            Log.d(TAGO,"Quiz:" +" AUTHUI_REQUEST_CODE");
            if(resultCode == RESULT_OK){
                //We have signed in the user or new user
                user = FirebaseAuth.getInstance().getCurrentUser();
                Log.d(TAGO,"onActivityResult:" + user.getEmail());
                if(user.getMetadata().getCreationTimestamp() == user.getMetadata().getLastSignInTimestamp()){
                    Toast.makeText(LoginStart.this,"Welcome New User", Toast.LENGTH_SHORT).show();;
                    Log.d(TAGO, "onActivityResult: Welcome New User");
                    checkVerfication();
                }else{
                    Toast.makeText(LoginStart.this,"Welcome back Again", Toast.LENGTH_SHORT).show();
                    Log.d(TAGO, "onActivityResult: Welcome Back Again User");
                    checkVerfication();
                }
            }else{
                // Signing in Failed
                IdpResponse response = IdpResponse.fromResultIntent(data);
                if(response == null){
                    finishAffinity();
                    Toast.makeText(LoginStart.this,"user cancelled", Toast.LENGTH_SHORT).show();
                    Log.d(TAGO, "onActivityResult: the user has cancelled the sign in request");
                    finishAffinity();   //It Will Close the app if user try to back or cancel login
                }else{
                    Toast.makeText(LoginStart.this,"ERROR "+response.getError(), Toast.LENGTH_SHORT).show();
                    Log.e(TAGO,"onActivityResult: ",response.getError());
                }
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
    private void checkVerfication() {   //onActivityResult will call this method
        Log.d(TAGO, "Quiz: checkVerfication() dUserUID found");
        if(user.isEmailVerified()){
            //EMAIL IS VERIFIED
            checkUserData();
            Toast.makeText(LoginStart.this,"Verified Email", Toast.LENGTH_SHORT).show();;
        }else{  //EMAIL IS NOT VERIFIED
            //verfication_email();  //disable now
            checkUserData();
            //Toast.makeText(LoginStart.this,"Passing unverified Email", Toast.LENGTH_SHORT).show();;
        }
    }
    private String dUserUID = "NO";
    private void checkUserData() {
        Log.d(TAGO, "Quiz: checkUserData()");
        dUserUID = FirebaseAuth.getInstance().getUid();
        //Toast.makeText(LoginStart.this,"checkUserData()", Toast.LENGTH_SHORT).show();;
        if(dUserUID.equals("")  || dUserUID == null ){
            Toast.makeText(LoginStart.this,"Logged in but UID 404", Toast.LENGTH_SHORT).show();;
        }else{
            Log.d(TAGO, "Quiz: checkUserData() dUserUID found");
            //Please Modify Database Auth READ WRITE Condition if its not connect to database
            //Toast.makeText(LoginStart.this, "Checking Database", Toast.LENGTH_SHORT).show();;
            FirebaseFirestore db = FirebaseFirestore.getInstance();
           // DocumentReference user_data_ref = db.collection("QuizMate").document("All_USER");
            db.collection("All_USER").document(dUserUID).get()
                    .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                        @Override
                        public void onSuccess(DocumentSnapshot documentSnapshot) {
                            Log.d(TAGO, "Quiz: checkUserData() onSuccess");
                            if(documentSnapshot.exists()){
                                Log.d(TAGO, "Quiz: checkUserData() documentSnapshot exists");
                                Toast.makeText(getApplicationContext(),"User Information Found", Toast.LENGTH_SHORT).show();;
                                Intent intent = new Intent(LoginStart.this,MainActivity.class);
                                startActivity(intent);
                                //finish();

                            }else{
                                Log.d(TAGO, "Quiz: checkUserData() documentSnapshot not exists");
                                Toast.makeText(getApplicationContext(),"User Information 404", Toast.LENGTH_SHORT).show();;

                                //Errorx
                                 Intent intent = new Intent(LoginStart.this, LoginRegistration.class);
                                //intent.setFlags(intent.getFlags() | Intent.FLAG_ACTIVITY_NO_HISTORY);
                                startActivity(intent);

                            }
                        }
                    });
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        Log.d(TAGO, "Quiz: onStart()");
        //mAuth.addAuthStateListener(mAuthListener);
    }
    @Override
    public void onStop() {
        super.onStop();
        Log.d(TAGO, "Quiz: onStop()");
        if(mAuthListener != null){
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }
    int backPressedTime = 0;

    @Override
    public void onBackPressed() {
        finishAffinity();
    }


    }