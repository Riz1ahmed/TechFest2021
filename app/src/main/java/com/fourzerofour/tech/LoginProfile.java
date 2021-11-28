package com.fourzerofour.tech;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.squareup.picasso.Picasso;

public class LoginProfile extends AppCompatActivity {

    private ImageView mUserProfileImage;
    private TextView mHeadText, mUserNameText, mUserUniversityName, mUserTypeText, mUserPoints;
    private Button mSignOutBtn, mSaleProductBtn, mVerifyBtn,mVerifyListBtn;
    private Button mGoPremiumBtn, mEditProfileBtn;

    //Firebase Auth
    private FirebaseUser user;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener; //For going to Account Activity Page

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_profile);

        mUserProfileImage = (ImageView)findViewById(R.id.profile_picture);
        mHeadText =         (TextView)findViewById(R.id.profile_head_text);
        mUserNameText =     (TextView)findViewById(R.id.profile_name_text);
        mUserUniversityName =   (TextView)findViewById(R.id.profile_university_name_text);
        mUserTypeText =     (TextView)findViewById(R.id.profile_user_type_text);
        mUserPoints =     (TextView)findViewById(R.id.text_points);

        mSignOutBtn = (Button)findViewById(R.id.profile_sign_out_btn);
        mSaleProductBtn = (Button)findViewById(R.id.profile_sale_product_btn) ;
        mVerifyBtn = (Button)findViewById(R.id.profile_verify_btn) ;
        mVerifyListBtn = (Button)findViewById(R.id.profile_verify_list_btn) ;
        mGoPremiumBtn = (Button)findViewById(R.id.go_premiumbtn) ;
        mEditProfileBtn = (Button)findViewById(R.id.go_edit_profile) ;

        //Login Check
        mAuth = FirebaseAuth.getInstance();
        mAuthListener = new FirebaseAuth.AuthStateListener() { ///for going to Account Activity Page
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                user = firebaseAuth.getCurrentUser();
                if(user != null){
                    String dsUserName = user.getDisplayName();
                    if(user.getEmail().equals("u1@gmail.com")){
                        mVerifyListBtn.setVisibility(View.VISIBLE);
                    }else{
                        mVerifyListBtn.setVisibility(View.GONE);
                    }
                    //mUserNameText.setText(dsUserName);
                    Toast.makeText(getApplicationContext(),"Welcome "+dsUserName, Toast.LENGTH_SHORT).show();;
                    getUserData();
                    ///getUserData();
                }else{
                    Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                    intent.setFlags(intent.getFlags() | Intent.FLAG_ACTIVITY_NO_HISTORY);
                    startActivity(intent);
                }
            }
        };
        mGoPremiumBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseFirestore db = FirebaseFirestore.getInstance();
                db.collection("All_USER").document(dUserUID).update("userPaidType", "Premium");
                Intent intent = new Intent(LoginProfile.this, APremium.class);
                startActivity(intent);
            }
        });
        mEditProfileBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(LoginProfile.this, LoginRegistration.class);
                intent.putExtra("dsEditMode", "ON");
                startActivity(intent);
            }
        });
        mSaleProductBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), ProductUpload.class);
                intent.putExtra("userPaidType", userPaidType);
                startActivity(intent);
            }
        });
        mSignOutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseAuth.getInstance().signOut();
                Intent intent = new Intent(getApplicationContext(), LoginStart.class);
                startActivity(intent);
            }
        });
        mVerifyBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), VerifyAddPhoto.class);
                startActivity(intent);
            }
        });
        mVerifyListBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), VerifyNidList.class);
                startActivity(intent);
            }
        });
        getIntentMethod();
    }


    private String dUserUID = "NO",userPaidType = "NO";
    private void getUserData() {
        dUserUID = FirebaseAuth.getInstance().getUid();
        if(dUserUID.equals("")){
            Toast.makeText(getApplicationContext(),"Logged in but UID 404", Toast.LENGTH_SHORT).show();;
        }else{
//Please Modify Database Auth READ WRITE Condition if its not connect to database
            Toast.makeText(getApplicationContext(), "Checking Database", Toast.LENGTH_SHORT).show();;
            FirebaseFirestore db = FirebaseFirestore.getInstance();
            //DocumentReference user_data_ref = db.collection("All_USER").document("REGISTER");
            db.collection("All_USER").document(dUserUID).get()
                    .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                        @Override
                        public void onSuccess(DocumentSnapshot documentSnapshot) {
                            if(documentSnapshot.exists()){
                                //Toast.makeText(getApplicationContext(),"User Information Found", Toast.LENGTH_SHORT).show();;

                                String dUserName = documentSnapshot.getString("nameFirst");
                                String dUserPhotoURL = documentSnapshot.getString("photoURL");
                                String dAddress = documentSnapshot.getString("address");
                                String dUserType = documentSnapshot.getString("userType");
                                userPaidType = documentSnapshot.getString("userPaidType");
                                String userValidNID = documentSnapshot.getString("userValidNID");

                                if(userValidNID.equals("NO")){
                                    mSaleProductBtn.setEnabled(false);
                                    mGoPremiumBtn.setEnabled(false);
                                }else if(userValidNID.equals("YES")){
                                    mSaleProductBtn.setEnabled(true);
                                }
                                mUserNameText.setText(dUserName);
                                /*String dUserEmail = documentSnapshot.getString("email");
                                String dRegTime = documentSnapshot.getString("reg_date");
                                String UserUID = documentSnapshot.getString("uid");
                                String dUserPhoneNo = documentSnapshot.getString("phone_no");
                                String dTotal = documentSnapshot.getString("total");
                                String dHomeAddress = documentSnapshot.getString("homeAddress");
                                String dUserType = documentSnapshot.getString("userType");*/

                                long diPoints = documentSnapshot.getLong("diPoint");
                                if(diPoints < 0 ){
                                    mSaleProductBtn.setEnabled(false);
                                }
                                mUserPoints.setText(""+diPoints);
                                if(!dUserPhotoURL.equals("NO")){
                                    Picasso.get().load(dUserPhotoURL).into(mUserProfileImage);
                                }else{
                                    Toast.makeText(getApplicationContext(),"User Inforamtion Not Matched", Toast.LENGTH_SHORT).show();;
                                }
                                if(dUserType.equals("Seller")){
                                    mSaleProductBtn.setVisibility(View.VISIBLE);
                                }else{
                                    mSaleProductBtn.setVisibility(View.GONE);
                                }
                                if(userPaidType.equals("Freemium")){

                                }else{
                                    mGoPremiumBtn.setText("Premium Subscribed");
                                    mSaleProductBtn.setText("Sale Product");
                                    mSaleProductBtn.setEnabled(true);
                                }

                                mUserTypeText.setText("User Type: "+dUserType);
                                mUserUniversityName.setText(dAddress);
                            }else{
                                //User has no data saved
                                Toast.makeText(getApplicationContext(),"User Inforamtion 404", Toast.LENGTH_SHORT).show();;
                                Intent intent = new Intent(getApplicationContext(), LoginRegistration.class);
                                //intent.setFlags(intent.getFlags() | Intent.FLAG_ACTIVITY_NO_HISTORY);
                                startActivity(intent);
                                //finish();
                            }
                        }
                    });
        }
    }

    long diProdcutIn30Days = 0;
    private boolean intentFoundError = true;
    private void getIntentMethod() {
        //////////////GET INTENT DATA
        final Intent intent = getIntent();
        if(intent.getExtras() != null)
        {
            String dsProductIn30Days = intent.getExtras().getString("dsProductIn30Days");
            diProdcutIn30Days = Integer.parseInt(dsProductIn30Days);
            if(diProdcutIn30Days < 10){

            }else{
                mSaleProductBtn.setText("Sale Limit Crossed");
                mSaleProductBtn.setEnabled(false);
            }
            Toast.makeText(getApplicationContext(),"Total Post in 30 Days"+diProdcutIn30Days, Toast.LENGTH_SHORT).show();

        }else{
            Toast.makeText(getApplicationContext(),"Intent null", Toast.LENGTH_SHORT).show();
        }

    }

    @Override
    public void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
    }
    @Override
    public void onStop() {
        super.onStop();
        if(mAuthListener != null){
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }
}