package com.fourzerofour.tech;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.firestore.FirebaseFirestore;

import org.jetbrains.annotations.NotNull;

import java.util.List;

public class VerifyNidList extends AppCompatActivity implements  RecylerviewClickInterface{

    RecyclerView mUserNidRecyclerView;
    List<VerifyModel> listSubjectItem ;
    VerifyListAdapter mVerify_adapter;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verify_nid_list);

        mUserNidRecyclerView = (RecyclerView)findViewById(R.id.users_nid_recyclerview);
        homeViewModel =
                new ViewModelProvider(this).get(VerifyNIDVM.class);

        callViewModel();
    }

    private VerifyNIDVM homeViewModel;
    private void callViewModel() {
        Log.d("ViewModel", "allViewModel:1 homeViewModel start");
        homeViewModel = new ViewModelProvider(this).get(VerifyNIDVM.class);
        homeViewModel.LoadNIDList().observe(this, new Observer<List<VerifyModel>>() {
            @Override
            public void onChanged(List<VerifyModel> post_models) {
                Log.d("ViewModel", "allViewModel:1 onChanged listview4 size = "+post_models.size());
                if (post_models.get(0).getUIDverify().equals("NULL")){
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

                    listSubjectItem = post_models;

                    mVerify_adapter = new VerifyListAdapter(getApplicationContext(),listSubjectItem,VerifyNidList.this);
                    mVerify_adapter.notifyDataSetChanged();
                    //It will swap from right to left
                    mUserNidRecyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.VERTICAL,false));
                    mUserNidRecyclerView.setAdapter(mVerify_adapter);

                }
            }
        });
    }

    @Override
    public void onItemClick(int position) {
        String dUserUID = listSubjectItem.get(position).getUID();
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("All_USER").document(dUserUID).update("userValidNID","YES").addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                Toast.makeText(getApplicationContext(),"NID Approved",Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull @NotNull Exception e) {
                Toast.makeText(getApplicationContext(),"NID not Approved",Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onBitNow(int position) {

    }
}