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
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class VerifyAddPhoto extends AppCompatActivity {
    private ImageView mFrontImage, mBackImage;
    private Button mSubmitBtn;

    //Photo Selecting and Croping
    private final int CODE_IMG_GALLERY = 1;
    private final String SAMPLE_CROPPED_IMG_NAME = "SampleCropIng";
    Uri imageUri_storage;
    Uri imageUriResultCrop;

    private  int ImageA = 0;
    private  int ImageB = 0;

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
        setContentView(R.layout.activity_verify_add_photo);

        mFrontImage = (ImageView)findViewById(R.id.nid_front_image);
        mBackImage = (ImageView)findViewById(R.id.nid_back_image);
        mSubmitBtn = (Button)findViewById(R.id.nid_submit_btn);

        mAuth = FirebaseAuth.getInstance();
        mAuthListener = new FirebaseAuth.AuthStateListener() { ///for going to Account Activity Page
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                user = firebaseAuth.getCurrentUser();

                //mUserInfoName.setText(dsUserName);
                if(user != null){
                    dUserUID = user.getUid();
                    dUserName = user.getDisplayName();
                    Toast.makeText(getApplicationContext(),"Update Profile of "+dUserName, Toast.LENGTH_SHORT).show();;

                }else{
                    Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                    intent.setFlags(intent.getFlags() | Intent.FLAG_ACTIVITY_NO_HISTORY);
                    startActivity(intent);
                }
            }
        };

        mFrontImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ImageA = 1;
                startActivityForResult(new Intent() //Image Selecting
                        .setAction(Intent.ACTION_GET_CONTENT)
                        .setType("image/*"), CODE_IMG_GALLERY);
                //go to this method >> onActivityResult(int requestCode, int resultCode, @Nullable Intent data)
            }
        });
        mBackImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ImageB = 1;
                startActivityForResult(new Intent() //Image Selecting
                        .setAction(Intent.ACTION_GET_CONTENT)
                        .setType("image/*"), CODE_IMG_GALLERY);
                //go to this method >> onActivityResult(int requestCode, int resultCode, @Nullable Intent data)
            }
        });
        mSubmitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String dsGetBtnText = mSubmitBtn.getText().toString();
                if(dsGetBtnText.equals("FINISH")){
                    Intent intent = new Intent(VerifyAddPhoto.this, MainActivity.class);
                    intent.setFlags(intent.getFlags() | Intent.FLAG_ACTIVITY_NO_HISTORY);
                    startActivity(intent);
                    finish();
                }else{
                    if(imageUri_storage != null && imageUri_storageB != null){
                        //uploadFilesToServer(imageUri_storage, "NidOne");
                        UploadData();
                    }else{
                        Toast.makeText(getApplicationContext(), "Upload Failed Photo Not Found ", Toast.LENGTH_SHORT).show();
                    }
                }

            }
        });
    }
    List<String> PhotoURL = new ArrayList<>();
    int UploadSucces = 0;
    boolean boolUploadStop = false;
    String dUserUID = "NO",dUserName = "NO";
    public void uploadFilesToServer(Uri filePath, String dsNIDNO){
        if(filePath != null){
            dUserUID = FirebaseAuth.getInstance().getUid();
            final ProgressDialog progressDialog = new ProgressDialog(this);
            progressDialog.setTitle("Uploading...");
            progressDialog.show();
            long time = System.currentTimeMillis();
            ref = storageReference.child("NID/"+time +"."+getFileExtention(filePath));
            ref.putFile(filePath)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        //Photo Uploaded now get the URL
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            float dProPicServerSize = taskSnapshot.getTotalByteCount() /1024 ;
                            ref.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {

                                    String dPhotoURL = uri.toString();
                                    PhotoURL.add(dPhotoURL);
                                    progressDialog.dismiss();
                                }
                            });
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            progressDialog.dismiss();
                            mSubmitBtn.setText("Failed Photo Upload");
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
            Toast.makeText(getApplicationContext(), "File Null ", Toast.LENGTH_SHORT).show();

        }
    }

    public void UploadData(){
        Toast.makeText(getApplicationContext(), "Photo Uploaded", Toast.LENGTH_SHORT).show();
        ++UploadSucces;

        Map<String, Object> note = new HashMap<>();

        //String
        FieldValue ddDate = FieldValue.serverTimestamp();
        note.put("Valid","NO");       //String
        note.put("UID",dUserUID);       //String
        note.put("DDate",ddDate);       //String
        note.put("NidOne",PhotoURL.get(0));       //String
        note.put("NidTwo",PhotoURL.get(1));       //String

        db.collection("All_NID").document(dUserUID).set(note)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(getApplicationContext(),"Successfully Uploaded", Toast.LENGTH_SHORT).show();
                        //progressDialog.dismiss();
                        mSubmitBtn.setText("FINISH");
                        Intent intent = new Intent(VerifyAddPhoto.this, MainActivity.class);
                        intent.setFlags(intent.getFlags() | Intent.FLAG_ACTIVITY_NO_HISTORY);
                        startActivity(intent);
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getApplicationContext(),"Failed Please Try Again", Toast.LENGTH_SHORT).show();
                mSubmitBtn.setText("FAILED Information Sent");
                UploadSucces = 0;
            }
        });
    }
    Uri imageUri_storageB;
    Uri imageUriResultCropB;
    //Dont forget to add class code on MainfestXml
    @Override   //Selecting Image
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == CODE_IMG_GALLERY && resultCode == RESULT_OK &&  data.getData() != null && data != null    && ImageA == 1){
            //Photo Successfully Selected
            imageUri_storage = data.getData();
            String dFileSize = getSize(imageUri_storage);       //GETTING IMAGE FILE SIZE
            double  dFileSizeDouble = Double.parseDouble(dFileSize);
            int dMB = 1000;
            dFileSizeDouble =  dFileSizeDouble/dMB;
            //dFileSizeDouble =  dFileSizeDouble/dMB;

            if(dFileSizeDouble <= 5000){
                Picasso.get().load(imageUri_storage).resize(200, 200).centerCrop().into(mFrontImage);
                Toast.makeText(getApplicationContext(),"Selected",Toast.LENGTH_SHORT).show();
                //startCrop(imageUri_storage);

                imageUriResultCrop = imageUri_storage;
                Picasso.get().load(imageUriResultCrop).into(mFrontImage);

            }else{
                Toast.makeText(this, "Failed! (File is Larger Than 5MB)",Toast.LENGTH_SHORT).show();
            }
            ImageA = 0;
            uploadFilesToServer(imageUri_storage, "NidOne");

        }else if(requestCode == CODE_IMG_GALLERY && resultCode == RESULT_OK &&  data.getData() != null && data != null    && ImageB == 1){
            //Photo Successfully Selected
            imageUri_storageB = data.getData();
            String dFileSize = getSize(imageUri_storageB);       //GETTING IMAGE FILE SIZE
            double  dFileSizeDouble = Double.parseDouble(dFileSize);
            int dMB = 1000;
            dFileSizeDouble =  dFileSizeDouble/dMB;
            //dFileSizeDouble =  dFileSizeDouble/dMB;

            if(dFileSizeDouble <= 5000){
                Picasso.get().load(imageUri_storageB).resize(200, 200).centerCrop().into(mBackImage);
                Toast.makeText(getApplicationContext(),"Selected",Toast.LENGTH_SHORT).show();
                //startCrop(imageUri_storageB);

                imageUriResultCropB = imageUri_storageB;
                Picasso.get().load(imageUriResultCropB).into(mBackImage);

            }else{
                Toast.makeText(this, "Failed! (File is Larger Than 5MB)",Toast.LENGTH_SHORT).show();
            }
            ImageB = 0;
            uploadFilesToServer(imageUri_storageB, "NidTwo");
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