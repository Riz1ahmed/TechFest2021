package com.fourzerofour.tech;

import android.util.Log;
import android.widget.Toast;

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

public class MainActivityVM extends ViewModel {

    public MainActivityVM() {

    }
    String dsSellerBtn = "NO";
    public MutableLiveData mLiveData;
    public MutableLiveData<List<ProductModel>> LoadPostList(String userType, String userPaidType, String dUserUID) {
        List<ProductModel> listSubjectItem ; listSubjectItem =new ArrayList<>();
        CollectionReference notebookRef;
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        Log.d("ViewModel", "allViewModel:4 LoadLevel4List start");

        notebookRef = db.collection("ProductList");
        Query query;
        if(userType.equals("Seller")){

             query = notebookRef
                    //.orderBy("dUploadDate", Query.Direction.DESCENDING)
                    .whereEqualTo("UidOwner",dUserUID);
        }else{
            query = notebookRef
                    .orderBy("dUploadDate", Query.Direction.DESCENDING);
        }

        if(mLiveData == null) {
            mLiveData = new MutableLiveData();
        }
            query.get()
                    .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                        @Override
                        public void onSuccess(QuerySnapshot queryDocumentSnapshots) {   //documnet er vitore je multiple document query ache er jonno for loop
                            String data = "";
                            if(queryDocumentSnapshots.isEmpty()) {
                                List<String>  dArrayPost_PhotoUrl = new ArrayList<>();
                                listSubjectItem.add(
                                        new ProductModel(null,null,"NULL",null, null,
                                                "uidCategory",   "uidBuyerFinal", null,   "name",
                                                "about",   "tagWord",   "bidMode",
                                                "extraA",   "extraB",  0,  0,  0,  0 )
                                );
                                mLiveData.postValue(listSubjectItem);
                                Log.d("ViewModel", "allViewModel:4 queryDocumentSnapshots empty");
                            }else {

                                for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                                    ProductModel product_model = documentSnapshot.toObject(ProductModel.class);
                                    //messageModel.setDocumentID(documentSnapshot.getId());
                                    String dsPost_UID = documentSnapshot.getId();
                                    String dsPost_User_UID = product_model.getProductUID();
                                    List<String>  dArrayPost_PhotoUrl = product_model.getPhotoArrayUrl();

                                    Date dExpairDate = product_model.getdExpairDate();
                                    Date dUploadDate = product_model.getdUploadDate();
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

                                    if(userType.equals("Seller")) {
                                        dsSellerBtn = "YES";
                                    }

                                    /*

                                    long diLike = post_model.getiLike();
                                    long diComment = post_model.getiComment();
                                    long diUnlike = post_model.getiUnlike();
                                    Date ddDate = post_model.getdDate();*/
                                    //Date dExpairDate, Date dUploadDate, String productUID, String uidOwner, String uidProduct,
                                    //                        String uidCategory, String uidBuyerFinal, List<String> photoArrayUrl, String name, String about, String tagWords, String bidMode,
                                    //                        String extraA, String extraB, long iLowestBidPrice, long iHighestBidPrice, long iTotalBider, long iExtra
                                    listSubjectItem.add(new ProductModel(dExpairDate,dUploadDate,dsPost_UID,uidOwner, uidProduct,
                                            uidCategory,   uidBuyerFinal, photoArrayUrl,   name,   about,   tagWord,   bidMode,
                                            extraA,   dsSellerBtn,  iLowestBidPrice,  iHighestBidPrice,  iTotalBider,  iExtra ));
                                    //Toast.makeText(getApplicationContext(),"PRODUCTS"+name, Toast.LENGTH_SHORT).show();;

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