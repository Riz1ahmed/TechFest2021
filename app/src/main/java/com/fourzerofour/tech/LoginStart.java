package com.fourzerofour.tech;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

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
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Log.d(TAGO, "Quiz: Login Start");
        if(FirebaseAuth.getInstance().getCurrentUser() == null){
            handleLoginRegister();
        }else{
            Intent intent = new Intent(LoginStart.this, MainActivity.class);
            startActivity(intent);
        }

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
                //.setLogo(R.drawable.logo)
               // .setTheme(R.style.LoginTheme)
                .build();

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
            DocumentReference user_data_ref = db.collection("QuizMate").document("All_USER");
            user_data_ref.collection("Reg_USER").document(dUserUID).get()
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