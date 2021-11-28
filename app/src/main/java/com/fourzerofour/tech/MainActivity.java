package com.fourzerofour.tech;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.snackbar.Snackbar;
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

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class MainActivity extends AppCompatActivity implements  RecylerviewClickInterface {


    private RecyclerView mPostRecyclerView;
    //List<ProductModel> listSubjectItem;
    List<ProductModel> listSubjectItem ;
    MainActivityAdapter mPost_adapter;

    //Firebase Auth
    private String dUserUID = "NO", dUserEmail = "NO";
    private FirebaseUser user;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener; //For going to Account Activity Page
    private MainActivityVM homeViewModel;

    ////////Toolbar
    private Toolbar mainToolBar;
    private ImageView toolbarUserImage;
    private TextView toolbarTextView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        homeViewModel =
                new ViewModelProvider(this).get(MainActivityVM.class);

        mPostRecyclerView = (RecyclerView)findViewById(R.id.main_product_recyclerview);
        listSubjectItem =new ArrayList<>();



        /////////Toolbar Start
        mainToolBar= findViewById(R.id.mainToolbarId);
        toolbarUserImage= findViewById(R.id.user_image);
        toolbarTextView= findViewById(R.id.toolbarSearchTextId);
        //toolbarTextView.setText("Player List");
        setSupportActionBar(mainToolBar);
        //toolbar setup hoye gelo


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

                    checkUserData();
                }else{
                    Toast.makeText(getApplicationContext(),"User NOT LOGIN", Toast.LENGTH_SHORT).show();;
                    Intent intent = new Intent(getApplicationContext(), LoginStart.class);
                    startActivity(intent);
                    callViewModel();
                }
            }
        };
        toolbarUserImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(userType.equals("Seller")    &&  userPaidType.equals("Freemium")){
                    int SellerTotalProduct = listSubjectItem.size();

                    for(int i = 0; i<SellerTotalProduct; i++){
                        ProductModel productModel = listSubjectItem.get(i);
                        long productUploadDate = productModel.getdUploadDate().getTime();
                        long todayDate = System.currentTimeMillis();
                        if(productUploadDate >= todayDate-2592000000L){
                            diProdcutIn30Days++;
                        }
                    }
                   // Toast.makeText(getApplicationContext(), "30 Day Total Post"+diProdcutIn30Days, Toast.LENGTH_SHORT).show();
                }
                //Toast.makeText(getApplicationContext(), userType+" "+userPaidType, Toast.LENGTH_SHORT).show();
                String dsProductIn30Days = String.valueOf(diProdcutIn30Days);
                Intent intent = new Intent(MainActivity.this, LoginProfile.class);
                intent.putExtra("dsProductIn30Days", dsProductIn30Days);
                startActivity(intent);
            }
        });

        //SPinner
        //Spinner
        /*Spinner staticSpinner = (Spinner) findViewById(R.id.spinner2);

        // Create an ArrayAdapter using the string array and a default spinner
        ArrayAdapter<CharSequence> staticAdapter = ArrayAdapter
                .createFromResource(this, R.array.brew_array,
                        android.R.layout.simple_spinner_item);

        // Specify the layout to use when the list of choices appears
        staticAdapter
                .setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // Apply the adapter to the spinner
        staticSpinner.setAdapter(staticAdapter);*/

        Spinner dynamicSpinner = (Spinner) findViewById(R.id.spinner2);

        String[] itemsd = new String[] { "T-Shirt","Full Shirt","Pant","Shari","Panjabi","Trouser","Baby", "All Products" };

        ArrayAdapter<String> adaptered = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, itemsd);

        dynamicSpinner.setAdapter(adaptered);

        dynamicSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view,
                                       int position, long id) {
                dsCategoryName = (String) parent.getItemAtPosition(position);
                if(dsCategoryName.equals("All Products")){
                    listSubjectItem.clear();
                    userType = "Buyer";
                    callViewModel();
                }else{
                    filter(dsCategoryName);
                }

               // Toast.makeText(getApplicationContext(),  " "+ (String) parent.getItemAtPosition(position), Toast.LENGTH_SHORT).show();
                Log.v("item", (String) parent.getItemAtPosition(position));
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // TODO Auto-generated method stub
            }
        });



    }
    String dsCategoryName = "NO";
    //////////Toolbar CODE
    SearchView searchView;

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.normal_menu,menu);
        ////////////SEARCHING CODE///////////////////////////
        MenuItem myActionMenuItem = menu.findItem( R.id.action_search);
        MenuItem myActionMenuItem2 = menu.findItem( R.id.action_cart);
        searchView = (SearchView) myActionMenuItem.getActionView();

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                // Toast like print
               // Toast.makeText(getApplicationContext(),"S : "+query,Toast.LENGTH_SHORT).show();
                //serachType(query);
                //LoadLevel5List(dsLevel1_Name, dsLevel2_Name, dsLevel3_UID, query);
                filter(query);
                if( ! searchView.isIconified()) {
                    searchView.setIconified(true);
                }
                myActionMenuItem.collapseActionView();
                return false;
            }
            @Override
            public boolean onQueryTextChange(String s) {
                //Toast.makeText(getApplicationContext(),"X: "+s,Toast.LENGTH_SHORT).show();

                return false;
            }
        });
        return true;
    }
    List<ProductModel> listL5Filtered = new ArrayList<>();;
    public void  filter(String dsGetSearchKey){

        listL5Filtered = new ArrayList<>();;

        int list_size = listSubjectItem.size();
        for(int i = 0; i<list_size; i++){
            ProductModel mkey = listSubjectItem.get(i);
            String item = mkey.getName().toLowerCase();
            if(item.contains(dsGetSearchKey.toLowerCase())){
                listL5Filtered.add(mkey);
            }
        }
        mPost_adapter = new MainActivityAdapter(MainActivity.this,listL5Filtered,MainActivity.this,userType);
        mPost_adapter.notifyDataSetChanged();
        mPostRecyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.VERTICAL,false));
        mPostRecyclerView.setAdapter(mPost_adapter);
    }
    /////////////Toolbar


    private void callViewModel() {
        listL5Filtered = null;
        Log.d("ViewModel", "allViewModel:1 homeViewModel start");
        homeViewModel = new ViewModelProvider(this).get(MainActivityVM.class);
        homeViewModel.LoadPostList(userType, userPaidType,dUserUID).observe(this, new Observer<List<ProductModel>>() {
            @Override
            public void onChanged(List<ProductModel> post_models) {
                Log.d("ViewModel", "allViewModel:1 onChanged listview4 size = "+post_models.size());
                if (post_models.get(0).getProductUID().equals("NULL")){
                    View parentLayout = findViewById(android.R.id.content);
                    Snackbar.make(parentLayout, "No Product Found", Snackbar.LENGTH_LONG)
                            .setAction("CLOSE", new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {

                                }
                            })
                            .setActionTextColor(getResources().getColor(android.R.color.holo_red_light ))
                            .show();
                    Toast.makeText(getApplicationContext(),"No Post Found",Toast.LENGTH_SHORT).show();
                }else{

                    //List<ProductModel> listBoostSorted  =new ArrayList<>();
                    int listSize = post_models.size();
                    for(int i = 0 ; i<listSize; i++){
                        ProductModel productModel = post_models.get(i);
                        long BoostValue = productModel.getiExtra();
                        if(BoostValue == 1){
                            listSubjectItem.add(productModel);
                        }
                    }
                    for(int i = 0 ; i<listSize; i++){
                        ProductModel productModel = post_models.get(i);
                        long BoostValue = productModel.getiExtra();
                        if(BoostValue == 0){
                            listSubjectItem.add(productModel);
                        }
                    }
                    //listSubjectItem = listBoostSorted;

                    mPost_adapter = new MainActivityAdapter(getApplicationContext(),listSubjectItem,MainActivity.this, userType);
                    mPost_adapter.notifyDataSetChanged();
                   // Toast.makeText(getApplicationContext(),"ID "+listSubjectItem.get(0).getUidProduct(),Toast.LENGTH_SHORT).show();
                    //It will swap from right to left
                    mPostRecyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.VERTICAL,false));
                    mPostRecyclerView.setAdapter(mPost_adapter);

                }
            }
        });
    }

    String userPaidType = "NO", userType = "NO";
    private static final String TAGO = "MainActivity";
    private void checkUserData() {
        dUserUID = FirebaseAuth.getInstance().getUid();

            FirebaseFirestore db = FirebaseFirestore.getInstance();
            db.collection("All_USER").document(dUserUID).get()
                    .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                        @Override
                        public void onSuccess(DocumentSnapshot documentSnapshot) {
                            if(documentSnapshot.exists()){
                                callViewModel();
                                String msName = documentSnapshot.getString("name");
                                userPaidType = documentSnapshot.getString("userPaidType");
                                userType = documentSnapshot.getString("userType");

                                String dsUserPhotoURL = documentSnapshot.getString("photoURL");
                                Picasso.get().load(dsUserPhotoURL).fit().centerCrop().into(toolbarUserImage);


                            }else{
                                Toast.makeText(getApplicationContext(),"User Information 404", Toast.LENGTH_SHORT).show();;
                                Intent intent = new Intent(MainActivity.this, LoginRegistration.class);
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
    int diProdcutIn30Days = 0;
    @Override
    public void onItemClick(int position) {
        String dsProductUID = "NO";
        if(listL5Filtered.size() > 0){
            dsProductUID = listL5Filtered.get(position).getProductUID();    //Post UID
        }else{
            dsProductUID = listSubjectItem.get(position).getProductUID();    //Post UID
        }

        //Toast.makeText(getApplicationContext(), dsProductUID+ "", Toast.LENGTH_SHORT).show();
        ;
            Intent intent = new Intent(getApplicationContext(), ProductDetails.class);
            intent.putExtra("dsProductUID", dsProductUID);

            startActivity(intent);

    }

    @Override
    public void onBitNow(int position) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        AlertDialog.Builder builder1 = new AlertDialog.Builder(MainActivity.this);
        builder1.setMessage("Are you sure to extend 7Days");
        builder1.setCancelable(true);

        builder1.setPositiveButton(
                "Yes",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        String dsProductUID = listSubjectItem.get(position).getProductUID();
                        db.collection("ProductList").document(dsProductUID).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                            @Override
                            public void onSuccess(DocumentSnapshot documentSnapshot) {
                                if(documentSnapshot.exists()){
                                    Date date = documentSnapshot.getDate("dExpairDate");
                                    long dlDate = date.getTime();
                                    dlDate = dlDate +604800000L;


                                }
                            }
                        });

                    }
                });

        builder1.setNegativeButton(
                "No",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {


                    }
                });

        AlertDialog alert11 = builder1.create();
        alert11.show();

    }
}