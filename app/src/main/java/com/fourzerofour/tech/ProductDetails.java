package com.fourzerofour.tech;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.denzcoskun.imageslider.ImageSlider;
import com.denzcoskun.imageslider.constants.ScaleTypes;
import com.denzcoskun.imageslider.models.SlideModel;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.squareup.picasso.Picasso;

import org.jetbrains.annotations.NotNull;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ProductDetails extends AppCompatActivity {
    private TextView mProductName, mProductCategory, mProductOwnerName,
           mProductExpairDate, mProductAbout, mProductMinimumBid, mProductWinningBid;

    ImageSlider mProductImage;
    private EditText mEditBidType;
    private Button mAutoBidBtn, mBidNowBtn, mBtnListBtn;

    //Firebase Auth
    private String dUserUID = "NO", dUserEmail = "NO";
    private FirebaseUser user;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener; //For going to Account Activity Page
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_details);
        getIntentMethod();

        mProductImage = (ImageSlider) findViewById(R.id.product_image_slider);
        mProductName = (TextView) findViewById(R.id.product_name);
        mProductCategory= (TextView) findViewById(R.id.product_category);
        mProductOwnerName= (TextView) findViewById(R.id.product_owner);
        mProductExpairDate = (TextView) findViewById(R.id.product_bid_expair_date);
        mProductAbout = (TextView) findViewById(R.id.product_profile_info_about);
        mProductMinimumBid= (TextView) findViewById(R.id.product_minimum_bid);
        mProductWinningBid= (TextView) findViewById(R.id.product_wining_bid);
        mEditBidType= (EditText) findViewById(R.id.product_edit_bid_type);
        mBidNowBtn = (Button) findViewById(R.id.product_bid_nowbtn);
        mAutoBidBtn = (Button) findViewById(R.id.product_autobid_btn);
        mBtnListBtn = (Button) findViewById(R.id.product_bid_list_btn);

        mAuth = FirebaseAuth.getInstance();
        mAuthListener = new FirebaseAuth.AuthStateListener() { ///for going to Account Activity Page
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                user = firebaseAuth.getCurrentUser();
                if(user != null){
                    dUserUID = user.getUid();
                    dUserEmail = user.getEmail();
                    checkUserData();
                }else{
                    Toast.makeText(getApplicationContext(),"User NOT LOGIN", Toast.LENGTH_SHORT).show();;
                    Intent intent = new Intent(getApplicationContext(), LoginStart.class);
                    startActivity(intent);
                }
            }
        };

        mBtnListBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), BidderList.class);
                intent.putExtra("dsProductUID", dsProductUID);
                startActivity(intent);
            }
        });

        mAutoBidBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                List<BidderModel> listSubjectItem ; listSubjectItem =new ArrayList<>();
                CollectionReference notebookRef;
                FirebaseFirestore db = FirebaseFirestore.getInstance();
                notebookRef = db.collection("ProductList").document(dsProductUID).collection("BidderList");
                notebookRef
                        .orderBy("BidDate", Query.Direction.DESCENDING).get()
                        .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                            @Override
                            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {   //documnet er vitore je multiple document query ache er jonno for loop
                                String data = "";
                                if (queryDocumentSnapshots.isEmpty()) {

                                } else {
                                    long maxAmount  = 0;
                                    for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                                        BidderModel bidderModel = documentSnapshot.toObject(BidderModel.class);
                                        //messageModel.setDocumentID(documentSnapshot.getId());
                                        String bidItemUID = documentSnapshot.getId();
                                        long BidAmount = bidderModel.getBidAmount();
                                        if(maxAmount < BidAmount){
                                            maxAmount = BidAmount;
                                        }
                                    }
                                    BidderDataAddonList(maxAmount);
                                }
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {

                            }
                        });
            }
        });

        mBidNowBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                db.collection("ProductList").document(dsProductUID).collection("BidderList")
                        .document(dUserUID).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        if(!documentSnapshot.exists()){
                            String dsBidAmount = mEditBidType.getText().toString();
                            if(dsBidAmount.equals("")){
                                Toast.makeText(getApplicationContext(),"Type Bid Amount", Toast.LENGTH_SHORT).show();
                            }else{
                                long diBidAmount = Long.parseLong(dsBidAmount);
                                if(diBidAmount < iLowestBidPrice){
                                    Toast.makeText(getApplicationContext(),"Not Enough Bid Amount", Toast.LENGTH_SHORT).show();;;

                                }else if( diBidAmount > iHighestBidPrice){
                                    //Winner
                                    db.collection("ProductList").document(dsProductUID).update("BidMode", "Disabled");
                                    db.collection("ProductList").document(dsProductUID).update("UidBuyerFinal", dUserUID);
                                    BidderDataAddonList(diBidAmount);
                                    mBidNowBtn.setEnabled(false);
                                    mEditBidType.setVisibility(View.GONE);
                                    mAutoBidBtn.setVisibility(View.GONE);
                                    AlertDialog.Builder builder1 = new AlertDialog.Builder(ProductDetails.this);
                                    builder1.setMessage("Seller Review");
                                    builder1.setCancelable(true);

                                    builder1.setPositiveButton(
                                            "Good",
                                            new DialogInterface.OnClickListener() {
                                                public void onClick(DialogInterface dialog, int id) {

                                                    db.collection("All_USER").document(uidProductOwner).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                                        @Override
                                                        public void onSuccess(DocumentSnapshot documentSnapshot) {
                                                            if(documentSnapshot.exists()){
                                                                long diProductOwnerPresenttPoints = documentSnapshot.getLong("diPoint");
                                                                db.collection("All_USER").document(uidProductOwner).update("diPoint",diProductOwnerPresenttPoints+5);

                                                                db.collection("All_USER").document(dUserUID).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                                                    @Override
                                                                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                                                                        if(documentSnapshot.exists()){
                                                                            long diUserPresenttPoints = documentSnapshot.getLong("diPoint");
                                                                            db.collection("All_USER").document(dUserUID).update("diPoint",diUserPresenttPoints+5);

                                                                            Toast.makeText(getApplicationContext(),"Seller got  5 points", Toast.LENGTH_SHORT).show();;;
                                                                            Toast.makeText(getApplicationContext(),"User got  5 points", Toast.LENGTH_SHORT).show();;;
                                                                            dialog.cancel();
                                                                        }
                                                                    }
                                                                });
                                                                Toast.makeText(getApplicationContext(),"Seller got  5 points", Toast.LENGTH_SHORT).show();;;
                                                                dialog.cancel();
                                                            }
                                                        }
                                                    }).addOnFailureListener(new OnFailureListener() {
                                                        @Override
                                                        public void onFailure(@NonNull @NotNull Exception e) {
                                                            Toast.makeText(getApplicationContext(),"Seller Not Found", Toast.LENGTH_SHORT).show();;;
                                                            dialog.cancel();
                                                        }
                                                    });
                                                }
                                            });

                                    builder1.setNegativeButton(
                                            "Bad",
                                            new DialogInterface.OnClickListener() {
                                                public void onClick(DialogInterface dialog, int id) {
                                                    db.collection("All_USER").document(uidProductOwner).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                                        @Override
                                                        public void onSuccess(DocumentSnapshot documentSnapshot) {
                                                            if(documentSnapshot.exists()){
                                                                long diUserPresentPoints = documentSnapshot.getLong("diPoint");
                                                                db.collection("All_USER").document(uidProductOwner).update("diPoint",diUserPresentPoints-1);

                                                                db.collection("All_USER").document(dUserUID).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                                                    @Override
                                                                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                                                                        if(documentSnapshot.exists()){
                                                                            long diUserPresenttPoints = documentSnapshot.getLong("diPoint");
                                                                            db.collection("All_USER").document(dUserUID).update("diPoint",diUserPresenttPoints-1);

                                                                            Toast.makeText(getApplicationContext(),"Seller missed good points", Toast.LENGTH_SHORT).show();;;
                                                                            Toast.makeText(getApplicationContext(),"Seller missed good points", Toast.LENGTH_SHORT).show();;;
                                                                            dialog.cancel();
                                                                        }
                                                                    }
                                                                });
                                                                Toast.makeText(getApplicationContext(),"Seller missed good points", Toast.LENGTH_SHORT).show();;;
                                                                dialog.cancel();
                                                            }
                                                        }
                                                    }).addOnFailureListener(new OnFailureListener() {
                                                        @Override
                                                        public void onFailure(@NonNull @NotNull Exception e) {
                                                            Toast.makeText(getApplicationContext(),"Seller Not Found", Toast.LENGTH_SHORT).show();;;
                                                            dialog.cancel();
                                                        }
                                                    });

                                                }
                                            });

                                    AlertDialog alert11 = builder1.create();
                                    alert11.show();

                                }else{
                                    //Add New Bidder
                                    BidderDataAddonList(diBidAmount);

                                }
                            }

                        }else{
                            Toast.makeText(getApplicationContext(),"You all ready bid on it ", Toast.LENGTH_SHORT).show();
                            Toast.makeText(getApplicationContext(),"dsProductUID "+dsProductUID, Toast.LENGTH_SHORT).show();

                        }
                    }
                });

            }
        });
    }

    private void BidderDataAddonList(long diBidAmount) {
        FieldValue ddDate = FieldValue.serverTimestamp();
        Map<String, Object> note = new HashMap<>();
        note.put("BidBy", dUserUID);
        note.put("BidPoints", 0);
        note.put("BidAmount", diBidAmount);
        note.put("BidDate", ddDate);
        db.collection("ProductList").document(dsProductUID).collection("BidderList")
                .document(dUserUID).set(note).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                Toast.makeText(getApplicationContext(),"Adding your Bid", Toast.LENGTH_SHORT).show();;;
                mBidNowBtn.setEnabled(false);
                mAutoBidBtn.setEnabled(false);
                mEditBidType.setVisibility(View.GONE);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull @NotNull Exception e) {
                Toast.makeText(getApplicationContext(),"Server Failed", Toast.LENGTH_SHORT).show();;;
                mBidNowBtn.setText("FAILED");
                mBidNowBtn.setEnabled(false);
                mAutoBidBtn.setEnabled(false);
                mEditBidType.setVisibility(View.GONE);
            }
        });
    }

    private void checkUserData() {
        dUserUID = FirebaseAuth.getInstance().getUid();
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("All_USER").document(dUserUID).get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        if(documentSnapshot.exists()){
                            String msName = documentSnapshot.getString("name");
                            String userValidNID = documentSnapshot.getString("userValidNID");

                            if(userValidNID.equals("NO")){
                                mEditBidType.setEnabled(false);
                                mAutoBidBtn.setEnabled(false);
                                mBidNowBtn.setEnabled(false);
                            }else if(userValidNID.equals("YES")){
                                if(mBidNowBtn.isEnabled()){
                                    mEditBidType.setEnabled(true);
                                    mAutoBidBtn.setEnabled(true);
                                    mBidNowBtn.setEnabled(true);
                                }
                            }
                            /*userPaidType = documentSnapshot.getString("userPaidType");
                            userType = documentSnapshot.getString("userType");

                            String dsUserPhotoURL = documentSnapshot.getString("photoURL");
                            Picasso.get().load(dsUserPhotoURL).fit().centerCrop().into(toolbarUserImage);
                            callViewModel();*/

                        }else{
                            Toast.makeText(getApplicationContext(),"User Information 404", Toast.LENGTH_SHORT).show();;
                            Intent intent = new Intent(getApplicationContext(), LoginRegistration.class);
                            startActivity(intent);

                        }

                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull  Exception e) {
               // Log.d(TAGO, "onActivityResult: checkUserData() onFailure "+e);

            }
        });


    }

    String dsCategoryUID = "NO", dsProductUID = "NO";
    private boolean intentFoundError = true;
    private void getIntentMethod() {
        //////////////GET INTENT DATA
        final Intent intent = getIntent();
        if(intent.getExtras() != null)
        {
            dsProductUID = intent.getExtras().getString("dsProductUID");
            if(dsProductUID.equals("NO")){
                Toast.makeText(getApplicationContext(),"Intent error", Toast.LENGTH_SHORT).show();;;
            }else{
                getProductData(dsProductUID);

            }
           // Toast.makeText(getApplicationContext(),""+dsProductUID, Toast.LENGTH_SHORT).show();

        }else{
            dsCategoryUID = "NO";
            dsProductUID = "NO";
            Toast.makeText(getApplicationContext(),"Intent null", Toast.LENGTH_SHORT).show();
        }

    }
    long iLowestBidPrice = 0;
    long iHighestBidPrice = 0;
    String uidProductOwner = "NO";
            FirebaseFirestore db = FirebaseFirestore.getInstance();
    private void getProductData(String dsProductUID) {

        db.collection("ProductList").document(dsProductUID).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if(documentSnapshot.exists()){
                    ProductModel product_model = documentSnapshot.toObject(ProductModel.class);
                    //messageModel.setDocumentID(documentSnapshot.getId());
                    String dsPost_UID = documentSnapshot.getId();
                    String dsPost_User_UID = product_model.getProductUID();
                    List<String> dArrayPost_PhotoUrl = product_model.getPhotoArrayUrl();

                    Date dExpairDate = product_model.getdExpairDate();
                    Date dUploadDate = null;//product_model.getdUploadDate();
                    String productUID = dsPost_UID;//product_model.getProductUID() ;
                    uidProductOwner = product_model.getUidOwner();
                    String uidProduct = product_model.getUidProduct();
                    String uidCategory = product_model.getUidCategory();
                    String uidBuyerFinal= product_model.getUidBuyerFinal();
                    List<String> photoArrayUrl= product_model.getPhotoArrayUrl();
                    String name= product_model.getName();
                    String about= product_model.getAbout();
                    String tagWord= product_model.getTagWords();
                    String bidMode= product_model.getBidMode();

                    String extraA = product_model.getExtraA();
                    String extraB = product_model.getExtraB();
                     iLowestBidPrice = product_model.getiLowestBidPrice();
                     iHighestBidPrice = product_model.getiHighestBidPrice();
                    long iTotalBider = product_model.getiTotalBider();
                    long iExtra = product_model.getiExtra();
                    //
                    mProductImage = (ImageSlider) findViewById(R.id.product_image_slider);
                    mProductName.setText(name);
                    mProductCategory.setText("Category: "+tagWord);

                    if(dExpairDate != null){
                        SimpleDateFormat df2 = new SimpleDateFormat("hh:mma  dd/MMM/yy");
                        String dateText = df2.format(dExpairDate);
                        mProductExpairDate.setText("Expair Date: "+dateText);

                        //Bidding Date Expair Date Compare
                        long dlPresentDate = System.currentTimeMillis();
                        long dlProductExpairML = dExpairDate.getTime();
                        if(dlPresentDate > dlProductExpairML){
                            mAutoBidBtn.setVisibility(View.GONE);
                            mEditBidType.setVisibility(View.GONE);
                            mBidNowBtn.setText("Time Expaired");
                            mBidNowBtn.setEnabled(false);
                        }
                    }else{
                        mProductExpairDate.setText("");
                    }

                    mProductAbout.setText(about);
                    mProductMinimumBid.setText("Minimum Bidding Price "+iLowestBidPrice);
                    mProductWinningBid.setText(iHighestBidPrice+"");
                    int diPhotoListSize = photoArrayUrl.size();
                    List<SlideModel> imageList = new ArrayList<>();
                    for(int i = 0; i<diPhotoListSize; i++){
                        imageList.add(new SlideModel(photoArrayUrl.get(i), ScaleTypes.CENTER_INSIDE));
                    }
                    mProductImage.setImageList(imageList,ScaleTypes.CENTER_INSIDE);
                  //  Toast.makeText(getApplicationContext(),"size"+diPhotoListSize, Toast.LENGTH_SHORT).show();


                    if(bidMode.equals("Enable")){
                        mEditBidType.setVisibility(View.VISIBLE);
                        mBidNowBtn.setVisibility(View.VISIBLE);
                        mAutoBidBtn.setVisibility(View.VISIBLE);
                    }else{
                        mEditBidType.setVisibility(View.GONE);
                        mBidNowBtn.setVisibility(View.GONE);
                        mAutoBidBtn.setVisibility(View.GONE);
                    }

                    getProductOwnerData(uidProductOwner);
                }else{
                    Toast.makeText(getApplicationContext(),"No Data Found", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
    private void getProductOwnerData(String uidProductOwner){
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("All_USER").document(uidProductOwner).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if(documentSnapshot.exists()){
                    String dsUserNameFirst = documentSnapshot.getString("nameFirst");
                    String dsUserNameLast = documentSnapshot.getString("nameLast");
                    String dsUserPhotoURl = documentSnapshot.getString("photoURL");
                    String dsUserPaidType = documentSnapshot.getString("userPaidType");
                    mProductOwnerName.setText("by "+dsUserNameFirst+" "+dsUserNameLast);
                    //Picasso.get().load(dsUserPhotoURl).fit().centerCrop().into(holder.mUserImage);
                    if(dsUserPaidType.equals("Freemium")){
                        mBtnListBtn.setVisibility(View.GONE);
                    }else{
                        mBtnListBtn.setVisibility(View.VISIBLE);
                    }

                }else{
                    mProductOwnerName.setText("NO Name");
                }
            }
        });
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