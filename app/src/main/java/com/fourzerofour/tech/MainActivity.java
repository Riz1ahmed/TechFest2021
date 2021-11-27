package com.fourzerofour.tech;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.MutableLiveData;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class MainActivity extends AppCompatActivity implements  RecylerviewClickInterface {

    private Button mMyProfileBtn;
    private RecyclerView mPostRecyclerView;
    //List<ProductModel> listSubjectItem;
    List<ProductModel> listSubjectItem ;
    MainActivityAdapter mPost_adapter;

    //Firebase Auth
    private String dUserUID = "NO", dUserEmail = "NO";
    private FirebaseUser user;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener; //For going to Account Activity Page

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mPostRecyclerView = (RecyclerView)findViewById(R.id.main_product_recyclerview);
        listSubjectItem =new ArrayList<>();

        mMyProfileBtn = (Button) findViewById(R.id.main_myprofile_btn);
        mAuth = FirebaseAuth.getInstance();
        mAuthListener = new FirebaseAuth.AuthStateListener() { ///for going to Account Activity Page
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                user = firebaseAuth.getCurrentUser();
                if(user != null){
                    dUserUID = user.getUid();
                    dUserEmail = user.getEmail();
                    //mLoginBtn.setText("Logout");
                    //mUserEmailText.setText(dUserEmail);
                    /*if(dUserEmail.equals(dsAdminEmail)){
                        mAddSubject.setVisibility(View.VISIBLE);
                    }else{
                        mAddSubject.setVisibility(View.GONE);
                    }*/

                    checkUserData();
                    //callViewModel();  //ERRORX
                }else{
                    Toast.makeText(getApplicationContext(),"User NOT LOGIN", Toast.LENGTH_SHORT).show();;
                    Intent intent = new Intent(getApplicationContext(), LoginStart.class);
                    startActivity(intent);
                }
            }
        };

        mMyProfileBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), LoginProfile.class);
                startActivity(intent);
            }
        });
        callData();
    }

    private void callData() {

        CollectionReference notebookRef;
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        Log.d("ViewModel", "allViewModel:4 LoadLevel4List start");

        notebookRef = db.collection("ProductList");
            notebookRef
                    //.orderBy("dDate", Query.Direction.DESCENDING)
                    .get()
                    .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                        @Override
                        public void onSuccess(QuerySnapshot queryDocumentSnapshots) {   //documnet er vitore je multiple document query ache er jonno for loop
                            String data = "";
                            if(queryDocumentSnapshots.isEmpty()) {
                                Toast.makeText(getApplicationContext(),"NOT FOUND ANY PRODUCTS", Toast.LENGTH_SHORT).show();;
                            }else {

                                for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                                    ProductModel product_model = documentSnapshot.toObject(ProductModel.class);
                                    //messageModel.setDocumentID(documentSnapshot.getId());
                                    String dsPost_UID = documentSnapshot.getId();
                                    String dsPost_User_UID = product_model.getProductUID();
                                    List<String>  dArrayPost_PhotoUrl = product_model.getPhotoArrayUrl();

                                    Date dExpairDate = null;//product_model.getdExpairDate();
                                    Date dUploadDate = null;//product_model.getdUploadDate();
                                    String productUID = dsPost_UID;//product_model.getProductUID() ;
                                    String uidOwner = product_model.getUidOwner();
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
                                    long iLowestBidPrice = product_model.getiLowestBidPrice();
                                    long iHighestBidPrice = product_model.getiHighestBidPrice();
                                    long iTotalBider = product_model.getiTotalBider();
                                    long iExtra = product_model.getiExtra();

                                    /*

                                    long diLike = post_model.getiLike();
                                    long diComment = post_model.getiComment();
                                    long diUnlike = post_model.getiUnlike();
                                    Date ddDate = post_model.getdDate();*/
                                    //Date dExpairDate, Date dUploadDate, String productUID, String uidOwner, String uidProduct,
                                    //                        String uidCategory, String uidBuyerFinal, List<String> photoArrayUrl, String name, String about, String tagWords, String bidMode,
                                    //                        String extraA, String extraB, long iLowestBidPrice, long iHighestBidPrice, long iTotalBider, long iExtra
                                    listSubjectItem.add(new ProductModel(dExpairDate,dUploadDate,productUID,uidOwner, uidProduct,
                                                               uidCategory,   uidBuyerFinal, photoArrayUrl,   name,   about,   tagWord,   bidMode,
                                                                    extraA,   extraB,  iLowestBidPrice,  iHighestBidPrice,  iTotalBider,  iExtra ));
                                    Toast.makeText(getApplicationContext(),"PRODUCTS"+name, Toast.LENGTH_SHORT).show();;
                                }

                            }
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {

                        }
                    });


        mPost_adapter = new MainActivityAdapter(getApplicationContext(), listSubjectItem,this);
                    mPost_adapter.notifyDataSetChanged();
                    mPostRecyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.VERTICAL,false));
                    mPostRecyclerView.setAdapter(mPost_adapter);
    }

    private static final String TAGO = "MainActivity";
    private void checkUserData() {
        Log.d(TAGO, "onActivityResult: checkUserData()");
        dUserUID = FirebaseAuth.getInstance().getUid();
        //Toast.makeText(getApplicationContext(),"checkUserData()", Toast.LENGTH_SHORT).show();;
        if(dUserUID.equals("")  || dUserUID == null ){
            Toast.makeText(getApplicationContext(),"Logged in but UID 404", Toast.LENGTH_SHORT).show();;
        }else{
            Log.d(TAGO, "onActivityResult: checkUserData() dUserUID found");
            //Please Modify Database Auth READ WRITE Condition if its not connect to database
            FirebaseFirestore db = FirebaseFirestore.getInstance();
            //DocumentReference user_data_ref = db.collection("All_USER").document("All_USER");
            db.collection("All_USER").document(dUserUID).get()
                    .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                        @Override
                        public void onSuccess(DocumentSnapshot documentSnapshot) {
                            if(documentSnapshot.exists()){
                                String msName = documentSnapshot.getString("name");
                                /*String msuniversity = documentSnapshot.getString("university");
                                String msUserType = documentSnapshot.getString("userType");
                                String msUserPhotoURL = documentSnapshot.getString("photoURL");
                                long mlPoints = documentSnapshot.getLong("points");

                                Picasso.get().load(msUserPhotoURL).fit().centerCrop().into(mUserImageView);
                                mUserName.setText(msName);*/

                            }else{
                                Toast.makeText(getApplicationContext(),"User Information 404", Toast.LENGTH_SHORT).show();;
                                Intent intent = new Intent(getApplicationContext(), LoginRegistration.class);
                                startActivity(intent);

                            }
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull  Exception e) {
                    Log.d(TAGO, "onActivityResult: checkUserData() onFailure "+e);

                }
            });
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

    @Override
    public void onItemClick(int position) {

    }

    @Override
    public void onBitNow(int position) {

    }
}