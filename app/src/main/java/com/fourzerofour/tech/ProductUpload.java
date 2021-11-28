package com.fourzerofour.tech;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;


import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Date;
import java.util.Map;

public class ProductUpload extends AppCompatActivity implements DatePickerDialog.OnDateSetListener {


    private Button mStatusAddPicBtn, mStatusPostBtn;
    private TextView mExpairDate;
    private EditText mProductEditName, mProductEditAbout, mProductEditLowestBidingRate,
                     mProductEditHighestBidding, mProductEditTypeTags;
    private RecyclerView mL5A_FilesRecyclerview;
    //Image Selecting Code
    private static final int RESULT_LOAD_IMAGE1 = 1;

    private String dsProductName = "NO", dsProductAbout= "NO", dsProductLowestBidingRate= "NO",
            dsProductHighestBidding= "NO", dsProductTypeTags= "NO";


    private List<String> dFileNameList;
    private List<String> dFileDoneList;
    private List<String> dsaPhotoUrlStringList;
    private String dsStatus;
    private PhotoUploadAdapter file_add_list_adapter;  //Errorx

    private RadioGroup mRadioBoostGroup;
    private RadioButton mRadioBoostOn, mRadioBoostOff;

    //Firebase Storage
    private StorageReference mStorage = FirebaseStorage.getInstance().getReference();
    private StorageReference FileToUpload;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    //Auth
    private String dUserUID = "NO";
    private FirebaseUser user;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener; //For going to Account Activity Page

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_upload);
        mExpairDate = (TextView)findViewById(R.id.product_expair_text);
        mL5A_FilesRecyclerview = (RecyclerView) findViewById(R.id.level_e_photo_recyclerview);
        mProductEditName = (EditText) findViewById(R.id.product_name);
        mProductEditAbout = (EditText) findViewById(R.id.product_about);
        mProductEditLowestBidingRate = (EditText) findViewById(R.id.product_lowest_bid_price);
        mProductEditHighestBidding = (EditText) findViewById(R.id.product_highest_bid_price);
        mProductEditTypeTags = (EditText) findViewById(R.id.product_tag_words);

        mStatusPostBtn = (Button) findViewById(R.id.status_post_btn) ;
        mStatusAddPicBtn = (Button) findViewById(R.id.status_add_pic_btn) ;

        mRadioBoostGroup = (RadioGroup) findViewById(R.id.product_boost_group) ;
        mRadioBoostOn = (RadioButton) findViewById(R.id.product_boost_on) ;
        mRadioBoostOff = (RadioButton) findViewById(R.id.product_boost_off) ;
        getIntentMethod();
        mAuth = FirebaseAuth.getInstance();
        mAuthListener = new FirebaseAuth.AuthStateListener() { ///for going to Account Activity Page
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                user = firebaseAuth.getCurrentUser();
                if(user != null){
                    dUserUID = user.getUid();
                    //dUserEmail = user.getEmail();
                    //mLoginBtn.setText("Logout");
                    //mUserEmailText.setText(dUserEmail);
                    /*if(dUserEmail.equals(dsAdminEmail)){
                        mAddSubject.setVisibility(View.VISIBLE);
                    }else{
                        mAddSubject.setVisibility(View.GONE);
                    }*/

                   // checkUserData();
                    //callViewModel();  //ERRORX
                }else{
                    Toast.makeText(getApplicationContext(),"User NOT LOGIN", Toast.LENGTH_SHORT).show();;
                    Intent intent = new Intent(getApplicationContext(), LoginStart.class);
                    startActivity(intent);
                }
            }
        };

        //Spinner
        //get the spinner from the xml.
        Spinner dropdown = findViewById(R.id.spinner1);
        String[] items = new String[]{"List", "2", "three"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, items);

        dropdown.setAdapter(adapter);

        mExpairDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DialogFragment datePicker = new DatePickerFragment();
                datePicker.show(getSupportFragmentManager(), "date picker");
            }
        });
        mStatusAddPicBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Toast.makeText(getContext(),"Select Image Files",Toast.LENGTH_SHORT).show();;
                //Log.d("AddStatus","Image Gallery");
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent,"Select Picture"), RESULT_LOAD_IMAGE1);
            }
        });

        mStatusPostBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dsStatus = mProductEditName.getText().toString();
                int diTotalPhotoAdded = dsaPhotoUrlStringList.size();
                int diStatusTotalChar = dsStatus.length();

                dsProductName = mProductEditName.getText().toString();
                dsProductAbout= mProductEditAbout.getText().toString();
                dsProductTypeTags = mProductEditTypeTags.getText().toString();
                dsProductLowestBidingRate= mProductEditLowestBidingRate.getText().toString();
                dsProductHighestBidding = mProductEditHighestBidding.getText().toString();


                if(dsProductName.equals("") ||dsProductAbout.equals("") || dsProductTypeTags.equals("")||dsProductLowestBidingRate.equals("")||dsProductHighestBidding.equals("") ){
                    Toast.makeText(getApplicationContext(),"Fill All Boxes Please",Toast.LENGTH_SHORT).show();
                }else if(diTotalPhotoAdded == 0 && diStatusTotalChar == 0){
                    Toast.makeText(getApplicationContext(),"Fill up Please",Toast.LENGTH_SHORT).show();
                }else if(ddDateExpair == null ){
                    Toast.makeText(getApplicationContext(),"Select Expire Date",Toast.LENGTH_SHORT).show();
                }else if(dsCategoryName.equals("NO") ){
                    Toast.makeText(getApplicationContext(),"Select Any Category",Toast.LENGTH_SHORT).show();
                }else if(diTotalPhotoAdded >= 1 ){
                    UploadToFirebase(dsStatus, dsaPhotoUrlStringList);
                    mStatusPostBtn.setEnabled(false);
                }else{
                    Toast.makeText(getApplicationContext(),"Please Fillup Fail",Toast.LENGTH_SHORT).show();
                }

                /*else if(diStatusTotalChar >= 1){
                    dsaPhotoUrlStringList.add("NO");
                    UploadToFirebase(dsStatus, dsaPhotoUrlStringList);
                }else if(diTotalPhotoAdded >= 1){
                    dsStatus = "NO";
                    UploadToFirebase(dsStatus, dsaPhotoUrlStringList);
                }*/
            }
        });
        //Recycler View
        //mL5A_FilesRecyclerview = binding.levelEPhotoRecyclerview;
        dsaPhotoUrlStringList = new ArrayList<>();
        dFileNameList = new ArrayList<>();
        dFileDoneList = new ArrayList<>();
        file_add_list_adapter = new PhotoUploadAdapter(dsaPhotoUrlStringList, dFileNameList,dFileDoneList);
        mL5A_FilesRecyclerview.setLayoutManager(new GridLayoutManager(getApplicationContext(),2));
        mL5A_FilesRecyclerview.setHasFixedSize(true);
        mL5A_FilesRecyclerview.setAdapter(file_add_list_adapter);



        //Spinner
        Spinner staticSpinner = (Spinner) findViewById(R.id.spinner1);

        // Create an ArrayAdapter using the string array and a default spinner
        ArrayAdapter<CharSequence> staticAdapter = ArrayAdapter
                .createFromResource(this, R.array.brew_array,
                        android.R.layout.simple_spinner_item);

        // Specify the layout to use when the list of choices appears
        staticAdapter
                .setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // Apply the adapter to the spinner
        staticSpinner.setAdapter(staticAdapter);

        Spinner dynamicSpinner = (Spinner) findViewById(R.id.spinner1);

        String[] itemsd = new String[] { "T-Shirt","Full Shirt","Pant","Shari","Panjabi","Trouser","Baby" };

        ArrayAdapter<String> adaptered = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, itemsd);

        dynamicSpinner.setAdapter(adaptered);

        dynamicSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view,
                                       int position, long id) {
                dsCategoryName = (String) parent.getItemAtPosition(position);
                Toast.makeText(getApplicationContext(),  " "+ (String) parent.getItemAtPosition(position), Toast.LENGTH_SHORT).show();
                Log.v("item", (String) parent.getItemAtPosition(position));
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // TODO Auto-generated method stub
            }
        });
    }

    private String dsCategoryName = "NO";

    private void UploadToFirebase(String dsStatus, List<String> dsaPhotoUrlStringList) {
        Toast.makeText(getApplicationContext(), "Uploading Information", Toast.LENGTH_SHORT).show();
        dUserUID = FirebaseAuth.getInstance().getUid();
        /*final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Uploading...");
        progressDialog.show();*/




        FieldValue ddDate = FieldValue.serverTimestamp();
        Map<String, Object> note = new HashMap<>();
        note.put("UidOwner", dUserUID);
        note.put("UidProduct", "NO");
        note.put("UidCategory", "NO");
        note.put("UidBuyerFinal", "NO");
        note.put("PhotoArrayUrl", dsaPhotoUrlStringList);
        note.put("Name", dsProductName);
        note.put("About", dsProductAbout);
        note.put("TagWords", dsCategoryName);
        note.put("BidMode", "Enable");
        note.put("ExtraA", dsProductName+ " " + dsProductAbout + " "+dsCategoryName);
        note.put("ExtraB", "NO");
        note.put("dExpairDate", ddDateExpair);  //Date Type
        note.put("dUploadDate", ddDate);
        note.put("iLowestBidPrice", Integer.parseInt(dsProductLowestBidingRate));
        note.put("iHighestBidPrice", Integer.parseInt(dsProductHighestBidding));
        note.put("iTotalBider", 0);

        if(mRadioBoostOn.isChecked()){
            note.put("iExtra", 1);      //Boost Mode
        }else
            note.put("iExtra", 0);      //Boost Mode OFF

        //Toast.makeText(getContext(),"Uploading New Item", Toast.LENGTH_SHORT).show();
        db.collection("ProductList").add(note)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        Toast.makeText(getApplicationContext(),"Successfully Uploaded", Toast.LENGTH_SHORT).show();
                        //progressDialog.dismiss();
                        mStatusPostBtn.setText("Submitted");
                        mProductEditName.setText("");
                        dsaPhotoUrlStringList.clear();

                        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                        startActivity(intent);
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                //progressDialog.dismiss();
                mStatusPostBtn.setText("FAILED");
                mProductEditName.setText("");
                dsaPhotoUrlStringList.clear();
                Toast.makeText(getApplicationContext(),"Failed Please Try Again", Toast.LENGTH_SHORT).show();

            }
        });

    }

    // ProgressDialog progressDialog = new ProgressDialog(ProductUpload.this);
    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d("AddStatus","Image onActivityResult");
        if(requestCode == RESULT_LOAD_IMAGE1 && resultCode == Activity.RESULT_OK){
            Log.d("AddStatus","Image requestCode == RESULT_LOAD_IMAGE1");
            if(data.getClipData() != null){
                ///Multiple file selected

                int diRetriveFileListSize = dFileNameList.size();
                int dTotalItemSelected = data.getClipData().getItemCount();
                Log.d("AddStatus","Image Multiple file selected "+dTotalItemSelected);
                //ProgressBar Visible

                final ProgressDialog progressDialog = new ProgressDialog(this);
                progressDialog.setTitle("Uploading...");
                progressDialog.show();

                for(int i = 0; i<dTotalItemSelected; i++){      //LoopStart
                    Uri dFileUri = data.getClipData().getItemAt(i).getUri();
                    final int dFinali = i+diRetriveFileListSize;
                    final String dDate = String.valueOf(System.currentTimeMillis());
                    ///RecyclerView Ready
                    final String dFileNmae = getFileName(dFileUri);
                    //dFileNameList.add(dFileNmae);
                    //dFileDoneList.add("uploading");
                    //dsaPhotoUrlStringList.add("NO");
                    //file_add_list_adapter.notifyDataSetChanged();
                    //Firebase Code Start
                    Log.d("AddStatus","Image Multiple uploading start "+dUserUID);
                    String dsTimeMiliSeconds = String.valueOf(System.currentTimeMillis());
                    FileToUpload = mStorage.child("ProductSale/"+dUserUID+"/AllPhotos/"+ dsTimeMiliSeconds +"."+getFileExtention(dFileUri));
                    FileToUpload.putFile(dFileUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            Log.d("AddStatus","Image Multiple file Uploaded");

                            //Get Photo Download URl
                            Task<Uri> result = taskSnapshot.getStorage().getDownloadUrl();
                            result.addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    String imageUrl = uri.toString();
                                    Log.d("AddStatus","Image Multiple file URL Found");
                                    if(!imageUrl.equals("NO")){
                                        int diFileSize = dFileDoneList.size();
                                        //Log.e("Level_E_Add", "onResult:FileToUpload: diFileSize = "+diFileSize +  " dFinali "+dFinali);
                                        if(dFinali <= diFileSize){
                                            Log.d("AddStatus","Image Multiple file selected"+diFileSize +  " dFinali "+dFinali);
                                            dFileNameList.add(dFileNmae);
                                            dsaPhotoUrlStringList.add(imageUrl);
                                            dFileDoneList.add(dFinali, "done"); //insted of uploading word we set don
                                            file_add_list_adapter.notifyDataSetChanged();;
                                            showToast("File Added "+dsaPhotoUrlStringList.size());
                                        }else{
                                            Log.d("AddStatus","Image FileToUpload: xDONE diFileSize = "+diFileSize +  " dFinali "+dFinali);
                                            dFileNameList.add(dFileNmae);
                                            dsaPhotoUrlStringList.add(imageUrl);
                                            dFileDoneList.add(diFileSize, "done"); //insted of uploading word we set don
                                            showToast(dFinali+" i no File Done ");
                                        }

                                        progressDialog.cancel();
                                    }else{
                                        int diFileSize = dFileDoneList.size();
                                        Log.e("Level_E_Add", "onResult:FileToUpload: Failed diFileSize = "+diFileSize +  " dFinali "+dFinali);
                                        if(dFinali <= diFileSize){
                                            dFileDoneList.add(dFinali, "Failed. (no url)"); //insted of uploading word we set don
                                            file_add_list_adapter.notifyDataSetChanged();;
                                            showToast(" Failed to UPLAOD ");
                                            mStatusPostBtn.setText("FAILED");
                                            mStatusPostBtn.setEnabled(false);
                                        }else{
                                            showToast(" Failed to UPLAOD ");
                                            mStatusPostBtn.setText("FAILED");
                                            mStatusPostBtn.setEnabled(false);
                                        }
                                       progressDialog.cancel();
                                    }

                                }
                            });


                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.d("AddStatus","Image Multiple file Failed Upload");
                            Toast.makeText(getApplicationContext(), "Failed File Uplaod !" , Toast.LENGTH_SHORT).show();
                            //Recycler View Update
                            int diFileSize = dFileDoneList.size();
                            if(dFinali <= diFileSize){{
                                dFileNameList.add(dFileNmae);
                                dsaPhotoUrlStringList.add("NX");
                                dFileDoneList.add(dFinali, "failed");
                                file_add_list_adapter.notifyDataSetChanged();;
                            }}else{
                                dFileNameList.add(dFileNmae+"Failed");
                                dsaPhotoUrlStringList.add("NX");
                                file_add_list_adapter.notifyDataSetChanged();;
                            }
                            progressDialog.cancel();

                        }
                    });
                }   //loop End
            }else if(data.getData() != null){
                //selected single file
                Toast.makeText(getApplicationContext(), "Single File Selected" , Toast.LENGTH_SHORT).show();
                // share_file_to_another_app(FileUri);
                Uri dFileUri = data.getData();
                final String dDate = String.valueOf(System.currentTimeMillis());
                ///RecyclerView Ready
                final String dFileNmae = getFileName(dFileUri);
                dFileNameList.add(dFileNmae);
                dFileDoneList.add("Processing");
                dsaPhotoUrlStringList.add("NO");
                file_add_list_adapter.notifyDataSetChanged();
                //Firebase Start
                String dsTimeMiliSeconds = String.valueOf(System.currentTimeMillis());
                FileToUpload = mStorage.child("Whispers/"+dUserUID+"/"+"AllPhotos"+"/"+ dsTimeMiliSeconds +"."+getFileExtention(dFileUri));
                FileToUpload.putFile(dFileUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        Toast.makeText(getApplicationContext(), "Single File Uploaded" , Toast.LENGTH_SHORT).show();

                        //Get Photo Download URl
                        FileToUpload.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                String dFileUrl = uri.toString();
                                dsaPhotoUrlStringList.remove(dFileNameList.size()-1);
                                dsaPhotoUrlStringList.add(dFileUrl);
                                //RecyclerView Update
                                dFileDoneList.remove(dFileNameList.size()-1);
                                dFileDoneList.add(dFileNameList.size()-1, "done"); //insted of uploading word we set done
                                file_add_list_adapter.notifyDataSetChanged();;
                            }
                        });
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getApplicationContext(), "Failed File Uplaod !" , Toast.LENGTH_SHORT).show();
                        //Recycler View Update
                        dFileDoneList.remove(dFileNameList.size()-1);
                        dFileDoneList.add(dFileNameList.size()-1, "failed");

                        file_add_list_adapter.notifyDataSetChanged();;
                    }
                });


            }else{
                Toast.makeText(getApplicationContext(), "File Not Selected" , Toast.LENGTH_SHORT).show();
            }
        }
    }


/*    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }*/
    private String getFileExtention(Uri uri){   //IMAGE
        ContentResolver cR = getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        //Not worked in Croped File so i constant it
        return "JPEG";
    }
    //Date, File Name , File Size, File Type -- > Database
    public String getFileName(Uri uri) {    //File Name from URI METHOD
        String result = null;

       /* if (uri.getScheme().equals("content")) {
            Cursor cursor = getContentResolver().query(uri, null, null, null, null);
            try {
                if (cursor != null && cursor.moveToFirst()) {
                    result = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME));
                }
            } finally {
                cursor.close();
            }
        }
        if (result == null) {
            result = uri.getPath();
            int cut = result.lastIndexOf('/');
            if (cut != -1) {
                result = result.substring(cut + 1);
            }
        }*/
        result = "FileName";
        return result;
    }
    public void showToast(String ToastWord){
        Toast.makeText(getApplicationContext(), ToastWord , Toast.LENGTH_SHORT).show();
    }

    private long dExpairDateLong = 0;
    Date ddDateExpair = null;
    @Override   //Date Picker, add implements also
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
        Calendar c = Calendar.getInstance();
        c.set(Calendar.YEAR, year);
        c.set(Calendar.MONTH, month);
        c.set(Calendar.DAY_OF_MONTH, dayOfMonth);
        String currentDateString = DateFormat.getDateInstance(DateFormat.FULL).format(c.getTime());
        dExpairDateLong  = c.getTimeInMillis();
        ddDateExpair  = c.getTime();

        mExpairDate.setText(currentDateString);
    }

    private void getIntentMethod() {
        //////////////GET INTENT DATA
        final Intent intent = getIntent();
        if(intent.getExtras() != null)
        {
            String userPaidType = intent.getExtras().getString("userPaidType");
            if(userPaidType.equals("Freemium")){
                mRadioBoostGroup.setVisibility(View.GONE);
            }else{
                mRadioBoostGroup.setVisibility(View.VISIBLE);
            }
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