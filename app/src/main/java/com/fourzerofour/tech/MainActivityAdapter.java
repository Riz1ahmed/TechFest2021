package com.fourzerofour.tech;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.denzcoskun.imageslider.ImageSlider;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class MainActivityAdapter  extends RecyclerView.Adapter<MainActivityAdapter.MainActivityAdapter_Holder> {
    private Context mContext;
    private List<ProductModel> mData;
    private RecylerviewClickInterface recylerviewClickInterface;
    public MainActivityAdapter (android.content.Context mContext, List<ProductModel> mData, RecylerviewClickInterface recylerviewClickInterface) {
        this.mContext = mContext;
        this.mData = mData;
        this.recylerviewClickInterface = recylerviewClickInterface;
    }

    @NonNull
    @Override
    public MainActivityAdapter_Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        LayoutInflater mInflater = LayoutInflater.from(mContext);
        view = mInflater.inflate(R.layout.item_home_post,parent,false); //connecting to cardview
        return new MainActivityAdapter_Holder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MainActivityAdapter_Holder holder, int position) {
       // List<String> dPhotoURLArray = new ArrayList<String>();
        /*List<SlideModel> imageList = new ArrayList<>();
        dPhotoURLArray = mData.get(position).getPhotoArrayUrl();
        String dsStatus = mData.get(position).getStatus();
        //Date Code
        Date date = mData.get(position).getdDate();
        SimpleDateFormat df2 = new SimpleDateFormat("hh:mma  dd/MMM/yy");
        String dateText = df2.format(date);
        long diTime = date.getTime();
        holder.mPostItemTime.setText(TimeAgo(diTime));
        //Time Code Finish
        int diPhotoListSize = dPhotoURLArray.size();
        if(diPhotoListSize == 1 && dPhotoURLArray.get(0).equals("NO")){
            holder.mSliderCard.setVisibility(View.GONE);
            holder.mPostStatusText.setTextSize(24);

        }else{
            for(int i = 0; i<diPhotoListSize; i++){
                imageList.add(new SlideModel(dPhotoURLArray.get(i), ScaleTypes.CENTER_INSIDE));
            }
            holder.mPostItemImageView.setImageList(imageList,ScaleTypes.CENTER_CROP);

        }
        holder.mPostStatusText.setText(dsStatus);
        if(dsStatus.equals("NO")){
            holder.mPostStatusText.setVisibility(View.GONE);
        }
        //mPostItemImageView.setImageList(imageList, ScaleTypes.CENTER_CROP);
        //Picasso.get().load(dPhotoURL).fit().centerCrop().into(holder.mPostItemImageView);
        String dsAddress = mData.get(position).getAddress();


        //holder.mPostItemUserAddress.setText(dsBio);
        String dUserUID = mData.get(position).getUserUID();
        getUserData(dUserUID, holder);*/
    }

    private void getUserData(String dUserUID, MainActivityAdapter_Holder holder) {

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("Reg_USER").document(dUserUID).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if(documentSnapshot.exists()){
                    String dsUserName = documentSnapshot.getString("name");
                    String dsUserPhotoURl = documentSnapshot.getString("photoURL");
                    holder.mPostItemUserName.setText(dsUserName);
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
    class MainActivityAdapter_Holder extends RecyclerView.ViewHolder {

        CardView mSliderCard;
        ImageSlider mPostItemImageView;
        ImageView mUserImage;
        TextView mPostItemUserName, mPostStatusText ;
        TextView mPostItemTime,mPostItemUserAddress;

        ImageView mPostLoveIcon;
        TextView mPostLoveCount;
        public MainActivityAdapter_Holder(@NonNull View itemView) {
            super(itemView);
            mSliderCard = (CardView) itemView.findViewById(R.id.post_image_slider_cardview) ;
            mUserImage = (ImageView)itemView.findViewById(R.id.post_user_image) ;
            mPostItemImageView = (ImageSlider) itemView.findViewById(R.id.post_image_slider);
            mPostItemUserName = (TextView)itemView.findViewById(R.id.item_home_user_name);
            mPostStatusText = (TextView)itemView.findViewById(R.id.post_status_text);

            mPostItemUserAddress = (TextView)itemView.findViewById(R.id.post_address_text);
            mPostItemTime = (TextView)itemView.findViewById(R.id.post_time_text);

            mPostLoveIcon = (ImageView)itemView.findViewById(R.id.post_love_image);
            mPostLoveCount = (TextView)itemView.findViewById(R.id.post_love_count_txt);
            mPostItemUserName.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                   // recylerviewClickInterface.onItemClickUserID(getAdapterPosition());
                }
            });
            mPostLoveIcon.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    //recylerviewClickInterface.onLikeImgCLick(getAdapterPosition());

                    /*if (dLikedPress == false) {
                        dLikedPress = true;
                        int dLikeCount = Integer.parseInt(mPostLoveCount.getText().toString());
                        mPostLoveCount.setText(String.valueOf(dLikeCount + 1));
                        mPostLoveIcon.setColorFilter(mContext.getResources().getColor(R.color.SweetRed));
                        mPostLoveCount.setTextColor(mContext.getResources().getColor(R.color.SweetRed));
                    } else {
                        dLikedPress = false;
                        //mCommentLikeImg.getContext().getResources().getColor(R.color.colorPrimaryDarkDEEP);
                        int dLikeCount = Integer.parseInt(mPostLoveCount.getText().toString());
                        mPostLoveCount.setText(String.valueOf(dLikeCount - 1));
                        mPostLoveIcon.setColorFilter(mContext.getResources().getColor(R.color.DarkGray));
                        mPostLoveCount.setTextColor(mContext.getResources().getColor(R.color.DarkGray));
                    }*/
                }
            });

        }
    }

    private String TimeAgo(long dltime){
        String dstime = " ";
        try
        {
            Long currentTime = dltime;
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy.MM.dd G 'at' HH:mm:ss");
            Date date = new Date(currentTime);
            String time = simpleDateFormat.format(date);

            SimpleDateFormat format = new SimpleDateFormat("yyyy.MM.dd G 'at' HH:mm:ss");
            Date past = format.parse(time);
            Date now = new Date();
            long seconds= TimeUnit.MILLISECONDS.toSeconds(now.getTime() - past.getTime());
            long minutes=TimeUnit.MILLISECONDS.toMinutes(now.getTime() - past.getTime());
            long hours=TimeUnit.MILLISECONDS.toHours(now.getTime() - past.getTime());
            long days=TimeUnit.MILLISECONDS.toDays(now.getTime() - past.getTime());

            if(seconds<60)
                dstime = seconds+" seconds ago";
            else if(minutes<60)
                dstime = minutes+" minutes ago";
            else if(hours<24)
                dstime = hours+" hours ago";
            else
                dstime = days+" days ago";


        }
        catch (Exception j){
            j.printStackTrace();
        }

        return dstime;
    }


}
