package com.fourzerofour.tech;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.OpenableColumns;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class LoginRegistration extends AppCompatActivity {


    private ImageView mUserProfilePic;
    private EditText mUserInfoName, mUserInfoNameLast, mUserInfoPhoneNo, mUserInfoHomeAddress;
    private RadioGroup mRadioGenderGroup;
    private RadioButton mRadioGenderMale, mRadioGenderFemale;
    private RadioGroup mRadioUserTypeGroup;
    private RadioButton mRadioTypeRider, mRadioTypeUser, mRadioTypeAdmin;
    private Button mUserInfoUpdateBtn;
    //private static final String NO = "NO";

    private String dUserName = "NO",dUserNameLast = "NO",dHomeAddress = "NO", dUserBio = "NO", dUserPhone = "NO", dUserType = "NO", dGender = "NO";
    private String dUserUID = "NO",dUserEmail = "NO", dUserRegistrationDate = "NO", dUserLastActivity = "NO"; private long diUserLastActivity = 0;
    private String dExtra = "NO"; int diSize = 0, diGender = 0;
    //Photo Selecting and Croping
    private final int CODE_IMG_GALLERY = 1;
    private final String SAMPLE_CROPPED_IMG_NAME = "SampleCropIng";
    Uri imageUri_storage;
    Uri imageUriResultCrop;

    //Firebase Storage
    StorageReference storageReference = FirebaseStorage.getInstance().getReference();;
    StorageReference ref;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private DocumentReference category_ref;

    private FirebaseUser user;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener; //For going to Account Activity Page

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_registration);

        mUserProfilePic = (ImageView)findViewById(R.id.image_add_user_name);
        mUserInfoName = (EditText)findViewById(R.id.edit_add_user_name);
        mUserInfoNameLast = (EditText)findViewById(R.id.edit_add_user_last_name);
        mUserInfoHomeAddress = (EditText)findViewById(R.id.edit_add_user_address_text);
        mUserInfoPhoneNo = (EditText)findViewById(R.id.edit_add_user_phone);
        mRadioGenderGroup =(RadioGroup)findViewById(R.id.user_add_info_radio_group);
        mRadioGenderMale =(RadioButton)findViewById(R.id.radio_male);
        mRadioGenderFemale =(RadioButton) findViewById(R.id.radio_female);
        mRadioUserTypeGroup =(RadioGroup)findViewById(R.id.user_add_info_radio_group_user_type);
        mRadioTypeRider =(RadioButton)findViewById(R.id.radio_rider);
        mRadioTypeUser =(RadioButton)findViewById(R.id.radio_user);
        mRadioTypeAdmin =(RadioButton) findViewById(R.id.radio_admin);

        mUserInfoUpdateBtn = (Button)findViewById(R.id.user_infor_update_btn);
        //Login Check
        mAuth = FirebaseAuth.getInstance();
        mAuthListener = new FirebaseAuth.AuthStateListener() { ///for going to Account Activity Page
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                user = firebaseAuth.getCurrentUser();
                String dsUserName = user.getDisplayName();
                //mUserInfoName.setText(dsUserName);
                if(user != null){
                    Toast.makeText(getApplicationContext(),"Update Profile of "+dsUserName, Toast.LENGTH_SHORT).show();;

                }else{
                    Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                    intent.setFlags(intent.getFlags() | Intent.FLAG_ACTIVITY_NO_HISTORY);
                    startActivity(intent);
                }
            }
        };

        mUserProfilePic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivityForResult(new Intent() //Image Selecting
                        .setAction(Intent.ACTION_GET_CONTENT)
                        .setType("image/*"), CODE_IMG_GALLERY);
                //go to this method >> onActivityResult(int requestCode, int resultCode, @Nullable Intent data)
            }
        });
        mUserInfoUpdateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(!mRadioGenderMale.isChecked() && !mRadioGenderFemale.isChecked()){
                    dGender = "NO";
                    Toast.makeText(getApplicationContext(),"Please Select Gender", Toast.LENGTH_SHORT).show();;
                }else if(mRadioGenderMale.isChecked()){
                    dGender = mRadioGenderMale.getText().toString();
                }else if(mRadioGenderFemale.isChecked()){
                    dGender = mRadioGenderFemale.getText().toString();
                }

                if(!mRadioTypeRider.isChecked() && !mRadioTypeUser.isChecked() && !mRadioTypeAdmin.isChecked()){
                    dUserType = "NO";
                    Toast.makeText(getApplicationContext(),"Please Select User Type", Toast.LENGTH_SHORT).show();;
                }else if(mRadioTypeRider.isChecked()){
                    dUserType = mRadioTypeRider.getText().toString();
                }else if(mRadioTypeUser.isChecked()){
                    dUserType = mRadioTypeUser.getText().toString();
                }else if(mRadioTypeAdmin.isChecked()){
                    dUserType = mRadioTypeAdmin.getText().toString();
                }
                dUserName = mUserInfoName.getText().toString();
                dUserNameLast = mUserInfoNameLast.getText().toString();
                dUserBio = mUserInfoHomeAddress.getText().toString();
                dHomeAddress = mUserInfoHomeAddress.getText().toString();
                dUserPhone = mUserInfoPhoneNo.getText().toString();
                if(imageUriResultCrop == null   && dRetrivePhotoURL.equals("NO")){
                    if(imageUri_storage == null){
                        Toast.makeText(getApplicationContext(),"Please Select Image", Toast.LENGTH_SHORT).show();;
                    }else{
                        Toast.makeText(getApplicationContext(),"Please Crop Image", Toast.LENGTH_SHORT).show();;
                    }

                }else if(dUserName.equals("") ||dUserNameLast.equals("") || dUserBio.equals("") || dGender.equals("") || dUserType.equals("") ){
                    Toast.makeText(getApplicationContext(),"Please Fill Up All", Toast.LENGTH_SHORT).show();;
                }else if(dUserName.equals("NO") ||dUserNameLast.equals("NO") || dUserBio.equals("NO") || dGender.equals("NO") || dUserType.equals("NO") ){
                    Toast.makeText(getApplicationContext(),"Please Fill Up All", Toast.LENGTH_SHORT).show();;
                }else{
                    //Start working to upload data
                    String date = String.valueOf(System.currentTimeMillis());
                    dUserRegistrationDate = date;
                    dUserLastActivity = date;
                    long diDate = System.currentTimeMillis();
                    long diDate2 = 1623163000000L;
                    diDate = diDate - diDate2 ;
                    diUserLastActivity = diDate;
                    if(dUserPhone.equals("")){
                        dUserPhone = "123";
                    }
                    if(dGender.equals("Female")){
                        diGender = 2;   //FEMALE
                    }else{
                        diGender = 1;   //MALE
                    }
                    if(dUserType.equals("User")){
                        diUserType = 1;
                    }else if(dUserType.equals("Rider")){
                        diUserType = 2;
                    }else if(dUserType.equals("Admin")){
                        diUserType = 3;
                    }else {
                        diUserType = 1;
                    }
                    UploadCropedImageFunction(imageUriResultCrop);
                }
            }
        });
        getIntentMethod();
    }

    String dsEditMode = "NO";
    private void getIntentMethod() {
        //////////////GET INTENT DATA
        final Intent intent = getIntent();
        if(intent.getExtras() != null)
        {
            dsEditMode = intent.getExtras().getString("dsEditMode", "OFF");
            if(dsEditMode.equals("ON")){
                Toast.makeText(getApplicationContext(),"Edit Mode On", Toast.LENGTH_SHORT).show();;;
                RetriveUserOldInfo();
            }else{

            }


        }else{
            dsEditMode = "NO";
            Toast.makeText(getApplicationContext(),"Intent null", Toast.LENGTH_SHORT).show();
        }

    }



    String dRetrivePhotoURL = "NO";
    private void RetriveUserOldInfo(){
        Toast.makeText(getApplicationContext(),"Getting User Data", Toast.LENGTH_SHORT).show();
        getUserData();
    }
    private void getUserData() {
        dUserUID = FirebaseAuth.getInstance().getUid();
        if(dUserUID.equals("")){
            Toast.makeText(getApplicationContext(),"Logged in but UID 404", Toast.LENGTH_SHORT).show();;
        }else{
            FirebaseFirestore db = FirebaseFirestore.getInstance();
            db.collection("All_USER").document(dUserUID).get()
                    .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                        @Override
                        public void onSuccess(DocumentSnapshot documentSnapshot) {
                            if(documentSnapshot.exists()){
                                //Toast.makeText(getApplicationContext(),"User Information Found", Toast.LENGTH_SHORT).show();;

                                String dUserNameFirst = documentSnapshot.getString("nameFirst");
                                String dUserNameLast = documentSnapshot.getString("nameLast");
                                String dUserPhotoURL = documentSnapshot.getString("photoURL");
                                String dAddress = documentSnapshot.getString("address");
                                String dUserType = documentSnapshot.getString("userType");
                                String dUserPhoneNo = documentSnapshot.getString("phone_no");

                                String userPaidType = documentSnapshot.getString("userType");
                                mUserInfoName.setText(dUserNameFirst);
                                mUserInfoNameLast.setText(dUserNameLast);
                                mUserInfoHomeAddress.setText(dAddress);
                                mUserInfoPhoneNo.setText(dUserPhoneNo);
                                if(dUserType.equals("Seller")){
                                    mRadioTypeUser.setChecked(true);
                                }
                                mRadioGenderMale.setChecked(true);




                                if(!dUserPhotoURL.equals("NO")) {
                                    Picasso.get().load(dUserPhotoURL).into(mUserProfilePic);
                                }

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
    private void RetriveUserOldInformation() {
        /*if(user != null){
            dUserUID = FirebaseAuth.getInstance().getUid();
            db.collection("Quiz").document("REGISTER").collection("NORMAL_USER")
                    .document(dUserUID).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                @Override
                public void onSuccess(DocumentSnapshot documentSnapshot) {
                    if(documentSnapshot.exists()){
                        dRetrivePhotoURL  = documentSnapshot.getString("photoURL");
                        dUserName = documentSnapshot.getString("name");
                        dHomeAddress = documentSnapshot.getString("homeAddress");
                        dUserPhone = documentSnapshot.getString("phone_no");
                        dUserType = documentSnapshot.getString("userType");

                        Picasso.get().load(dRetrivePhotoURL).into(mUserProfilePic);
                        mUserInfoHomeAddress.setText(dHomeAddress);
                        mUserInfoPhoneNo.setText(dUserPhone);
                        if(dUserType.equals("User")){
                            mRadioTypeUser.setChecked(true);
                        }else if(dUserType.equals("Rider")){
                            mRadioTypeRider.setChecked(true);
                        }else {
                            mRadioTypeAdmin.setChecked(true);
                        }
                    }else{
                        Toast.makeText(getApplicationContext(), "No User Data Exists", Toast.LENGTH_SHORT).show();
                    }

                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(getApplicationContext(), "Retrive Failed ", Toast.LENGTH_SHORT).show();
                }
            });
        }else{
            Toast.makeText(getApplicationContext(), "User Not Logged In", Toast.LENGTH_SHORT).show();
        }*/
    }

    private int diUserType = 1;


    //Uplaoding Photo to FireStorage
    private void UploadCropedImageFunction(Uri filePath) {
        if(!dRetrivePhotoURL.equals("NO")){
            /*//Setting PhotoURL for
            UserProfileChangeRequest userProfileChangeRequest = new UserProfileChangeRequest.Builder()
                    .setDisplayName(dUserName)
                    .build();
            FirebaseAuth.getInstance().getCurrentUser().updateProfile(userProfileChangeRequest).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    Toast.makeText(getApplicationContext(),"Photo URL Attached",Toast.LENGTH_SHORT).show();
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(getApplicationContext(),"Photo URL Attach Failed!",Toast.LENGTH_SHORT).show();
                }
            });


            Toast.makeText(getApplicationContext(), "Photo Uploaded", Toast.LENGTH_SHORT).show();
            String dTotal = methodSetUserTotalData(  diUserType,diGender,150,121,0,0,
                    diSize,0,0,0,0,
                    0,0,0,0);

            Map<String, Object> note = new HashMap<>();
            note.put("name", dUserName);
            note.put("email", dUserEmail); //map is done
            note.put("reg_date", dUserRegistrationDate+"R");
            note.put("uid",dUserUID);
            note.put("bio",dUserBio);
            note.put("photoURL",dRetrivePhotoURL);
            note.put("phone_no",dUserPhone);;
            note.put("total",dTotal);       //String
            note.put("homeAddress",dHomeAddress);   //address   new
            note.put("userType",dUserType);   //city            new
            note.put("lastActivity",diUserLastActivity); //modify

            //note.put("gender", dGender);
            //note.put("type","NORMAL");
            //note.put("follower","0");
            //note.put("post","0");
            //note.put("reg_date",dUserRegistrationDate);
            db.collection("Quiz").document("REGISTER").collection("NORMAL_USER").document(dUserUID).set(note)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Toast.makeText(getApplicationContext(),"Successfully Uploaded", Toast.LENGTH_SHORT).show();
                            mUserInfoUpdateBtn.setText("UPDATED");
                            mUserInfoName.setText("");
                            mUserInfoHomeAddress.setText("");
                            mUserInfoPhoneNo.setText("");
                            finish();
                            Intent intent = new Intent(LoginRegistration.this, LoginProfile.class);
                            intent.setFlags(intent.getFlags() | Intent.FLAG_ACTIVITY_NO_HISTORY);
                            startActivity(intent);

                        }
                    }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(getApplicationContext(),"Failed Please Try Again", Toast.LENGTH_SHORT).show();
                    mUserInfoUpdateBtn.setText("FAILED Information Sent");
                }
            });*/
        }else if(filePath != null){
            dUserUID = FirebaseAuth.getInstance().getUid();
            final ProgressDialog progressDialog = new ProgressDialog(this);
            progressDialog.setTitle("Uploading...");
            progressDialog.show();
            //System.currentTimeMills();
            ref = storageReference.child("All_USER_Profile_Pic/"+dUserName+"_"+ dUserUID +"."+getFileExtention(filePath));
            ref.putFile(filePath)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        //Photo Uploaded now get the URL
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            float dProPicServerSize = taskSnapshot.getTotalByteCount() /1024 ;
                            diSize = (int)dProPicServerSize;
                            ref.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {

                                    String dPhotoURL = uri.toString();

                                    //Setting PhotoURL for
                                    UserProfileChangeRequest userProfileChangeRequest = new UserProfileChangeRequest.Builder()
                                            .setDisplayName(dUserName)
                                            .setPhotoUri(uri)
                                            .build();
                                    FirebaseAuth.getInstance().getCurrentUser().updateProfile(userProfileChangeRequest).addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            Toast.makeText(getApplicationContext(),"Photo URL Attached",Toast.LENGTH_SHORT).show();
                                        }
                                    }).addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Toast.makeText(getApplicationContext(),"Photo URL Attach Failed!",Toast.LENGTH_SHORT).show();
                                        }
                                    });


                                    Toast.makeText(getApplicationContext(), "Photo Uploaded", Toast.LENGTH_SHORT).show();
                                    String dTotal = methodSetUserTotalData(  diUserType,diGender,150,121,0,0,
                                            diSize,0,0,0,0,
                                            0,0,0,0);

                                    Map<String, Object> note = new HashMap<>();
                                    note.put("nameFirst", dUserName);
                                    note.put("nameLast", dUserNameLast);
                                    note.put("email", dUserEmail); //map is done
                                    note.put("phone_no",dUserPhone);;
                                    note.put("reg_date", dUserRegistrationDate);
                                    note.put("uid",dUserUID);
                                    note.put("address",dUserBio);   //Address
                                    note.put("photoURL",dPhotoURL);
                                    note.put("phone_no",dUserPhone);;
                                    note.put("userType",dUserType);;  //Buyer OR Saler
                                    note.put("userPaidType","Freemium");;  //Freemium  OR Premium
                                    note.put("userValidNID","NO");;  //Freemium  OR Premium
                                    note.put("dsNotificationToken","NO");       //String
                                    note.put("dsExtra","1");       //String
                                    note.put("diPoint",0);   //intger coins        new
                                    note.put("diPointSelling",0);   //intger coins        new
                                    note.put("diPointGoodReview",0);   //intger coins        new
                                    note.put("diExtraA",0);       //String
                                    note.put("diExtraB",0);       //String

                                    //note.put("gender", dGender);
                                    //note.put("type","NORMAL");
                                    //note.put("follower","0");
                                    //note.put("post","0");
                                    //note.put("reg_date",dUserRegistrationDate);
                                    db.collection("All_USER").document(dUserUID).set(note)
                                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void aVoid) {
                                                    Toast.makeText(getApplicationContext(),"Successfully Uploaded", Toast.LENGTH_SHORT).show();
                                                    progressDialog.dismiss();
                                                    mUserInfoUpdateBtn.setText("UPDATED");
                                                    mUserInfoName.setText("");
                                                    mUserInfoHomeAddress.setText("");
                                                    mUserInfoPhoneNo.setText("");
                                                    finish();
                                                    Intent intent = new Intent(LoginRegistration.this, LoginProfile.class);
                                                    intent.setFlags(intent.getFlags() | Intent.FLAG_ACTIVITY_NO_HISTORY);
                                                    startActivity(intent);

                                                }
                                            }).addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Toast.makeText(getApplicationContext(),"Failed Please Try Again", Toast.LENGTH_SHORT).show();
                                            mUserInfoUpdateBtn.setText("FAILED Information Sent");
                                        }
                                    });


                                }
                            });
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            progressDialog.dismiss();
                            mUserInfoUpdateBtn.setText("Failed Photo Upload");
                            Toast.makeText(getApplicationContext(), "Failed "+e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                            double progress = (100.0*taskSnapshot.getBytesTransferred()/taskSnapshot
                                    .getTotalByteCount());
                            progressDialog.setMessage("Uploaded "+(int)progress+"%");
                        }
                    });
        }else{
            Toast.makeText(getApplicationContext(), "Upload Failed Photo Not Found ", Toast.LENGTH_SHORT).show();
        }
    }
    private String methodSetUserTotalData(int AdminLevel,      int GenderType,     int Follower,       int TotalPost,      int TotalDiscussion,
                                          int TotalTaka,       int TotalMegaByte,  int TotalFileView,  int TotalFileDownload,
                                          int TotalFileUplaod, int TotalReview,    int TotalLike,      int TotalComment,   int ExtraE,   int ExtraF){
        String sAdminLevel = String.valueOf(AdminLevel) + "A";
        String sGenderType = String.valueOf(GenderType) + "G";
        String sFollower = String.valueOf(Follower) + "F";
        String sTotalPost = String.valueOf(TotalPost) + "P";
        String sTotalDiscussion= String.valueOf(TotalDiscussion) + "D";
        String sTotalTaka = String.valueOf(TotalTaka) + "T";
        String sTotalMegaByte = String.valueOf(TotalMegaByte) + "M";
        String sTotalFileView = String.valueOf(TotalFileView) + "V";
        String sTotalFileDownload = String.valueOf(TotalFileDownload) + "D";
        String sTotalFileUpload = String.valueOf(TotalFileUplaod) + "U";

        String sTotalReview = String.valueOf(TotalReview) + "R";
        String sTotalLike = String.valueOf(TotalLike) + "L";
        String sTotalComment = String.valueOf(TotalComment) + "C";
        String sExtraE = String.valueOf(ExtraE) + "E";
        String sExtraF = String.valueOf(ExtraF) + "F";

        //"AGF PDT MVD URL CEF";
        String dTotalString = sAdminLevel+sGenderType+sFollower+sTotalPost+sTotalDiscussion+sTotalTaka+sTotalMegaByte+sTotalFileView+sTotalFileDownload+sTotalFileUpload+sTotalReview+sTotalLike+sTotalComment+sExtraE+sExtraF;
        int len = dTotalString.length();
        String target =  "AGFPDTMVDURLCEF";

        int i = 0; int j = 0;
        for(i = 0; i<len; i++){
            if(dTotalString.charAt(i) == target.charAt(j)){
                j++;
            }
        }
        if(j != 15){
            dTotalString = "0A0G0F0P0D0T0M0V0D0U0R0L0C0E0F";
        }
        return dTotalString;
    }
    //Dont forget to add class code on MainfestXml
    @Override   //Selecting Image
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == CODE_IMG_GALLERY && resultCode == RESULT_OK &&  data.getData() != null && data != null){
            //Photo Successfully Selected
            dRetrivePhotoURL = "NO";
            imageUri_storage = data.getData();
            String dFileSize = getSize(imageUri_storage);       //GETTING IMAGE FILE SIZE
            double  dFileSizeDouble = Double.parseDouble(dFileSize);
            int dMB = 1000;
            dFileSizeDouble =  dFileSizeDouble/dMB;
            //dFileSizeDouble =  dFileSizeDouble/dMB;

            if(dFileSizeDouble <= 5000){
                Picasso.get().load(imageUri_storage).resize(200, 200).centerCrop().into(mUserProfilePic);
                Toast.makeText(getApplicationContext(),"Selected",Toast.LENGTH_SHORT).show();
                //startCrop(imageUri_storage);
                dRetrivePhotoURL = "NO";
                imageUriResultCrop = imageUri_storage;
                Picasso.get().load(imageUriResultCrop).into(mUserProfilePic);

            }else{
                Toast.makeText(this, "Failed! (File is Larger Than 5MB)",Toast.LENGTH_SHORT).show();
            }


        }else{
            Toast.makeText(this, "onActivityResult! Error",Toast.LENGTH_SHORT).show();
        }
    }
    //Croping Function
    Random random = new Random();


    private String getFileExtention(Uri uri){   //IMAGE
        ContentResolver cR = getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        //Not worked in Croped File so i constant it
        return "JPEG";
    }

    public String getSize(Uri uri) {
        String fileSize = null;
        Cursor cursor = getContentResolver().query(uri, null, null, null, null, null);
        try {
            if (cursor != null && cursor.moveToFirst()) {

                // get file size
                int sizeIndex = cursor.getColumnIndex(OpenableColumns.SIZE);
                if (!cursor.isNull(sizeIndex)) {
                    fileSize = cursor.getString(sizeIndex);
                }
            }
        } finally {
            cursor.close();
        }
        return fileSize;
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