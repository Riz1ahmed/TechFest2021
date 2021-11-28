package com.fourzerofour.tech;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.denzcoskun.imageslider.ImageSlider;
import com.denzcoskun.imageslider.constants.ScaleTypes;
import com.denzcoskun.imageslider.models.SlideModel;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.squareup.picasso.Picasso;

import org.w3c.dom.Text;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class VerifyListAdapter  extends RecyclerView.Adapter<VerifyListAdapter.VerifyListAdapter_Holder> {
    private Context mContext;
    private List<VerifyModel> mData;
    private RecylerviewClickInterface recylerviewClickInterface;
    public VerifyListAdapter (android.content.Context mContext, List<VerifyModel> mData, RecylerviewClickInterface recylerviewClickInterface) {
        this.mContext = mContext;
        this.mData = mData;
        this.recylerviewClickInterface = recylerviewClickInterface;
    }

    @NonNull
    @Override
    public VerifyListAdapter.VerifyListAdapter_Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        LayoutInflater mInflater = LayoutInflater.from(mContext);
        view = mInflater.inflate(R.layout.item_verify_nid_list,parent,false); //connecting to cardview
        return new VerifyListAdapter.VerifyListAdapter_Holder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull VerifyListAdapter.VerifyListAdapter_Holder holder, int position) {
        

        //Date Code
        Date date = mData.get(position).getDDate();
        SimpleDateFormat df2 = new SimpleDateFormat("hh:mma  dd/MMM/yy");
        String dateText = df2.format(date);
        holder.mPostDateText.setText(dateText);
        String dsFrontImageURL = mData.get(position).getNidOne();
        String dsBackImageURL = mData.get(position).getNidTwo();
        String dUserUID = mData.get(position).getUID();

        Picasso.get().load(dsFrontImageURL).fit().centerCrop().into(holder.mPostItemImageView);
        Picasso.get().load(dsBackImageURL).fit().centerCrop().into(holder.mPostItemSecondImage);
        getUserData(dUserUID,  holder);

    }

    private void getUserData(String dUserUID, VerifyListAdapter.VerifyListAdapter_Holder holder) {

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("All_USER").document(dUserUID).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if(documentSnapshot.exists()){
                    String dsUserNameFirst = documentSnapshot.getString("nameFirst");
                    String dsUserNameLast = documentSnapshot.getString("nameLast");
                    String dsUserPhotoURl = documentSnapshot.getString("photoURL");
                    holder.mPostItemUserName.setText(dsUserNameFirst+" "+dsUserNameLast);
                    Picasso.get().load(dsUserPhotoURl).fit().centerCrop().into(holder.mUserImage);
                }else{
                    holder.mPostItemUserName.setText("Name 404");
                }
            }
        });

    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    private boolean  dLikedPress = false;
    class VerifyListAdapter_Holder extends RecyclerView.ViewHolder {

        //CardView mSliderCard;
        ImageView mPostItemImageView, mPostItemSecondImage;
        ImageView mUserImage;
        TextView mPostItemUserName, mPostDateText;
        Button mNidApproveBtn;

        public VerifyListAdapter_Holder(@NonNull View itemView) {
            super(itemView);
            //mSliderCard = (CardView) itemView.findViewById(R.id.post_image_slider_cardview) ;
            mUserImage = (ImageView)itemView.findViewById(R.id.post_nid_user_image) ;
            mPostItemUserName = (TextView) itemView.findViewById(R.id.nid_user_name) ;
            mPostItemImageView = (ImageView) itemView.findViewById(R.id.post_nid_front_image);
            mPostItemSecondImage = (ImageView) itemView.findViewById(R.id.post_nid_back_image);
            mNidApproveBtn = (Button) itemView.findViewById(R.id.nid_approve_btn);

            //mUserImage= (ImageView) itemView.findViewById(R.id.post_nid_user_image);
            mPostDateText = (TextView) itemView.findViewById(R.id.nide_user_time);
            mPostItemUserName.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    // recylerviewClickInterface.onItemClickUserID(getAdapterPosition());
                }
            });
            mNidApproveBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mNidApproveBtn.setText("Verified");
                    recylerviewClickInterface.onItemClick(getAdapterPosition());
                }
            });

        }
    }
    


}
