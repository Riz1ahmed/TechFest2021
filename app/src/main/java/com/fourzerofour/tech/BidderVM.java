package com.fourzerofour.tech;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class BidderVM  extends ViewModel {

    public BidderVM() {
    }

    public MutableLiveData mLiveData;
    public MutableLiveData<List<BidderModel>> LoadBidList(String dsProductUID) {
        List<BidderModel> listSubjectItem ; listSubjectItem =new ArrayList<>();
        CollectionReference notebookRef;
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        Log.d("ViewModel", "allViewModel:4 LoadLevel4List start");

        notebookRef = db.collection("ProductList").document(dsProductUID).collection("BidderList");

        if(mLiveData == null) {
            mLiveData = new MutableLiveData();
            notebookRef
                    .orderBy("BidDate", Query.Direction.DESCENDING).get()
                    .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                        @Override
                        public void onSuccess(QuerySnapshot queryDocumentSnapshots) {   //documnet er vitore je multiple document query ache er jonno for loop
                            String data = "";
                            if (queryDocumentSnapshots.isEmpty()) {
                                List<String> dArrayPost_PhotoUrl = new ArrayList<>();
                                listSubjectItem.add(new BidderModel("NULL", "BidBy", 0, 0, null));
                                ;
                                mLiveData.postValue(listSubjectItem);
                                Log.d("ViewModel", "allViewModel:4 queryDocumentSnapshots empty");
                            } else {

                                for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                                    BidderModel bidderModel = documentSnapshot.toObject(BidderModel.class);
                                    //messageModel.setDocumentID(documentSnapshot.getId());
                                    String bidItemUID = documentSnapshot.getId();

                                    String BidBy = bidderModel.getBidBy();
                                    long UserPoints = bidderModel.getBidUserPoints();
                                    long BidAmount = bidderModel.getBidAmount();
                                    Date ddDate = bidderModel.getBidDate();

                                    //String bidItemUID, String bidBy, long bidUserPoints, long bidAmount, Date bidDate
                                    listSubjectItem.add(new BidderModel(bidItemUID, BidBy, UserPoints, BidAmount, ddDate));


                                    mLiveData.postValue(listSubjectItem);
                                }
                                mLiveData.postValue(listSubjectItem);    //All Items level 4 , it is a one type category

                            }
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {

                        }
                    });
        }

        return mLiveData;
    }
}