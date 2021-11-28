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
import com.denzcoskun.imageslider.constants.ScaleTypes;
import com.denzcoskun.imageslider.models.SlideModel;
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
    String userType;
    public MainActivityAdapter (android.content.Context mContext, List<ProductModel> mData, RecylerviewClickInterface recylerviewClickInterface, String userType) {
        this.mContext = mContext;
        this.mData = mData;
        this.recylerviewClickInterface = recylerviewClickInterface;
        this.userType = userType;
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
        List<String> dPhotoURLArray = new ArrayList<String>();
        List<SlideModel> imageList = new ArrayList<>();
        dPhotoURLArray = mData.get(position).getPhotoArrayUrl();

        //Date Code
        Date date = mData.get(position).getdUploadDate();
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
            holder.mPostItemImageView.setImageList(imageList,ScaleTypes.CENTER_INSIDE);

        }

        String dsBidMode = mData.get(position).getBidMode();
        if(dsBidMode.equals("Enable")){
            //Hide ALL
            //holder.setVisibility(View.GONE);
        }
        //mPostItemImageView.setImageList(imageList, ScaleTypes.CENTER_CROP);
        //Picasso.get().load(dPhotoURL).fit().centerCrop().into(holder.mPostItemImageView);
        //String dsAddress = mData.get(position).getAd();


        //holder.mPostItemUserAddress.setText(dsBio);
        String dPostOwnerUID = mData.get(position).getUidOwner();
        getUserData(dPostOwnerUID, holder);


        holder.mProductAbout.setText(mData.get(position).getAbout());
        holder.mProductName.setText(mData.get(position).getName());
        //Extra Time;
        String dsExtraB = mData.get(position).getExtraB();
        if(userType.equals("Seller")){
            holder.mProductTimeExtentImage.setVisibility(View.VISIBLE);
        }else{
            holder.mProductTimeExtentImage.setVisibility(View.GONE);
        }

    }

    private void getUserData(String dUserUID, MainActivityAdapter_Holder holder) {

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
    class MainActivityAdapter_Holder extends RecyclerView.ViewHolder {

        CardView mSliderCard;
        ImageSlider mPostItemImageView;
        ImageView mUserImage;
        TextView mPostItemUserName, mPostStatusText ;
        TextView mPostItemTime,mPostItemUserAddress;

        TextView mProductName, mProductAbout;
        ImageView mProductTimeExtentImage;
        ImageView mPostLoveIcon;
        TextView mPostLoveCount;
        public MainActivityAdapter_Holder(@NonNull View itemView) {
            super(itemView);
            mSliderCard = (CardView) itemView.findViewById(R.id.post_image_slider_cardview) ;
            mUserImage = (ImageView)itemView.findViewById(R.id.post_user_image) ;
            mProductTimeExtentImage = (ImageView)itemView.findViewById(R.id.add_time_img_btn) ;
            mPostItemImageView = (ImageSlider) itemView.findViewById(R.id.post_image_slider);
            mPostItemUserName = (TextView)itemView.findViewById(R.id.item_home_user_name);
            mPostStatusText = (TextView)itemView.findViewById(R.id.post_status_text);

            mPostItemUserAddress = (TextView)itemView.findViewById(R.id.post_address_text);
            mPostItemTime = (TextView)itemView.findViewById(R.id.post_time_text);

            mPostLoveIcon = (ImageView)itemView.findViewById(R.id.post_love_image);
            mPostLoveCount = (TextView)itemView.findViewById(R.id.post_love_count_txt);
            mProductName = (TextView)itemView.findViewById(R.id.product_name);
            mProductAbout = (TextView)itemView.findViewById(R.id.product_about);
            mPostItemUserName.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                   // recylerviewClickInterface.onItemClickUserID(getAdapterPosition());
                }
            });
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    recylerviewClickInterface.onItemClick(getAdapterPosition());
                }
            });
            mProductTimeExtentImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    recylerviewClickInterface.onBitNow(getAdapterPosition());
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
