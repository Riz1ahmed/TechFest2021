package com.fourzerofour.tech;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.android.material.snackbar.Snackbar;

import java.util.List;

public class BidderList extends AppCompatActivity implements  RecylerviewClickInterface {
    RecyclerView mUserNidRecyclerView;
    List<BidderModel> listSubjectItem ;
    BidderListAdapter mBidderListAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bidder_list);

        mUserNidRecyclerView = (RecyclerView)findViewById(R.id.bidder_list_recyclerview);
        bidderViewModel =
                new ViewModelProvider(this).get(BidderVM.class);
        getIntentMethod();
        callViewModel();

    }
    String dsProductUID = "NO";
    private void getIntentMethod() {
        //////////////GET INTENT DATA
        final Intent intent = getIntent();
        if(intent.getExtras() != null)
        {
            dsProductUID = intent.getExtras().getString("dsProductUID");
            if(dsProductUID.equals("NO")){
                Toast.makeText(getApplicationContext(),"Intent error", Toast.LENGTH_SHORT).show();;;
            }else{
                //getProductData(dsProductUID);
            }
            Toast.makeText(getApplicationContext(),""+dsProductUID, Toast.LENGTH_SHORT).show();

        }else{
            dsProductUID = "NO";
            Toast.makeText(getApplicationContext(),"Intent null", Toast.LENGTH_SHORT).show();
        }

    }
    private BidderVM bidderViewModel;
    private void callViewModel() {
        Log.d("ViewModel", "allViewModel:1 bidderViewModel start");
        bidderViewModel = new ViewModelProvider(this).get(BidderVM.class);
        bidderViewModel.LoadBidList(dsProductUID).observe(this, new Observer<List<BidderModel>>() {
            @Override
            public void onChanged(List<BidderModel> post_models) {
                Log.d("ViewModel", "allViewModel:1 onChanged listview4 size = "+post_models.size());
                if (post_models.get(0).getBidItemUID().equals("NULL")){
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

                    mBidderListAdapter = new BidderListAdapter(getApplicationContext(),listSubjectItem,BidderList.this);
                    mBidderListAdapter.notifyDataSetChanged();
                    //It will swap from right to left
                    mUserNidRecyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.VERTICAL,false));
                    mUserNidRecyclerView.setAdapter(mBidderListAdapter);

                }
            }
        });
    }

    @Override
    public void onItemClick(int position) {

    }

    @Override
    public void onBitNow(int position) {

    }
}