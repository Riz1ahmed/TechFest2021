package com.fourzerofour.tech;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class BidderListAdapter  extends RecyclerView.Adapter<BidderListAdapter.BidderListAdapter_Holder> {
    private Context mContext;
    private List<BidderModel> mData;
    private RecylerviewClickInterface recylerviewClickInterface;
    public BidderListAdapter (android.content.Context mContext, List<BidderModel> mData, RecylerviewClickInterface recylerviewClickInterface) {
        this.mContext = mContext;
        this.mData = mData;
        this.recylerviewClickInterface = recylerviewClickInterface;
    }

    @NonNull
    @Override
    public BidderListAdapter.BidderListAdapter_Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        LayoutInflater mInflater = LayoutInflater.from(mContext);
        view = mInflater.inflate(R.layout.card_rank_item,parent,false); //connecting to cardview
        return new BidderListAdapter.BidderListAdapter_Holder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull BidderListAdapter.BidderListAdapter_Holder holder, int position) {
        String BidderUID = mData.get(position).getBidBy();
        getUserData(BidderUID, holder);
        long BiddingAmount = mData.get(position).getBidAmount();
       holder.mItemTotalScore.setText(BiddingAmount+" TK");
    }

    private void getUserData(String dUserUID, BidderListAdapter.BidderListAdapter_Holder holder) {

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("All_USER").document(dUserUID).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if(documentSnapshot.exists()){
                    String dsUserNameFirst = documentSnapshot.getString("nameFirst");
                    String dsUserNameLast = documentSnapshot.getString("nameLast");
                    String dsUserPhotoURl = documentSnapshot.getString("photoURL");
                    holder.mItemUserName.setText(dsUserNameFirst+" "+dsUserNameLast);
                    Picasso.get().load(dsUserPhotoURl).fit().centerCrop().into(holder.mItemUserImage);
                }else{
                    holder.mItemUserName.setText("Name 404");
                }
            }
        });

    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    private boolean  dLikedPress = false;
    class BidderListAdapter_Holder extends RecyclerView.ViewHolder {

        ImageView mItemUserImage;
        TextView mItemUserName;
        TextView mItemRankNo;
        TextView mItemTotalScore;

        public BidderListAdapter_Holder(@NonNull View itemView) {
            super(itemView);
            //mSliderCard = (CardView) itemView.findViewById(R.id.post_image_slider_cardview) ;
            mItemUserImage = (ImageView) itemView.findViewById(R.id.card_rank_user_im);
            mItemUserName = (TextView)itemView.findViewById(R.id.card_rank_user_name);
            mItemRankNo = (TextView)itemView.findViewById(R.id.card_rank_no_text);
            mItemTotalScore = (TextView)itemView.findViewById(R.id.card_rank_right_ans_text);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    recylerviewClickInterface .onItemClick(getAdapterPosition());
                }
            });

        }
    }



}
