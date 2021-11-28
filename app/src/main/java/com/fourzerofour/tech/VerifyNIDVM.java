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

public class VerifyNIDVM  extends ViewModel {

    public VerifyNIDVM() {
    }

    public MutableLiveData mLiveData;
    public MutableLiveData<List<VerifyModel>> LoadNIDList() {
        List<VerifyModel> listSubjectItem ; listSubjectItem =new ArrayList<>();
        CollectionReference notebookRef;
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        Log.d("ViewModel", "allViewModel:4 LoadLevel4List start");

        notebookRef = db.collection("All_NID");
        
        if(mLiveData == null) {
            mLiveData = new MutableLiveData();
            notebookRef.orderBy("DDate", Query.Direction.DESCENDING).get()
                    .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                        @Override
                        public void onSuccess(QuerySnapshot queryDocumentSnapshots) {   //documnet er vitore je multiple document query ache er jonno for loop
                            String data = "";
                            if (queryDocumentSnapshots.isEmpty()) {
                                List<String> dArrayPost_PhotoUrl = new ArrayList<>();
                                listSubjectItem.add(
                                        new VerifyModel(null,
                                                "NULL", "uidBuyerFinal", "null", "null", "null")
                                );
                                mLiveData.postValue(listSubjectItem);
                                Log.d("ViewModel", "allViewModel:4 queryDocumentSnapshots empty");
                            } else {

                                for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                                    VerifyModel product_model = documentSnapshot.toObject(VerifyModel.class);
                                    //messageModel.setDocumentID(documentSnapshot.getId());
                                    String dsUIDverify = documentSnapshot.getId();
                                    Date ddDate = product_model.getDDate();
                                    String valid = product_model.getValid();
                                    String UserUID = product_model.getUID();
                                    String nidOneImgURL = product_model.getNidOne();
                                    String nidTwoImgURL = product_model.getNidTwo();
                                    //Date DDate, String UIDverify, String valid, String UID, String nidOne, String nidTwo
                                    listSubjectItem.add(new VerifyModel(ddDate, dsUIDverify, valid, UserUID, nidOneImgURL, nidTwoImgURL));


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
/*
    public MutableLiveData<List<VerifyModel>> LoadNIDList() {
        List<VerifyModel> listSubjectItem;
        listSubjectItem = new ArrayList<>();
        CollectionReference notebookRef;
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        notebookRef = db.collection("ProductList");


        if (mLiveData == null) {
            mLiveData = new MutableLiveData();
            notebookRef.get()
                    .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                        @Override
                        public void onSuccess(QuerySnapshot queryDocumentSnapshots) {   //documnet er vitore je multiple document query ache er jonno for loop
                            String data = "";
                            if (queryDocumentSnapshots.isEmpty()) {
                                List<String> dArrayPost_PhotoUrl = new ArrayList<>();
                                listSubjectItem.add(
                                        new VerifyModel(null,
                                                "NULL", "uidBuyerFinal", "null", "null", "null")
                                );
                                mLiveData.postValue(listSubjectItem);
                                Log.d("ViewModel", "allViewModel:4 queryDocumentSnapshots empty");
                            } else {

                                for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                                    VerifyModel product_model = documentSnapshot.toObject(VerifyModel.class);
                                    //messageModel.setDocumentID(documentSnapshot.getId());
                                    String dsUIDverify = documentSnapshot.getId();
                                    Date ddDate = product_model.getDDate();
                                    String valid = product_model.getValid();
                                    String UserUID = product_model.getUID();
                                    String nidOneImgURL = product_model.getValid();
                                    String nidTwoImgURL = product_model.getValid();
                                    //Date DDate, String UIDverify, String valid, String UID, String nidOne, String nidTwo
                                    listSubjectItem.add(new VerifyModel(ddDate, dsUIDverify, valid, UserUID, nidOneImgURL, nidTwoImgURL));


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

            return mLiveData;
        }

    }

*/

