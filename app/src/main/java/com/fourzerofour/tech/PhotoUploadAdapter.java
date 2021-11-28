package com.fourzerofour.tech;

import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.util.List;

public class PhotoUploadAdapter extends RecyclerView.Adapter<PhotoUploadAdapter.FileHolder> {

    public List<String> photoUrlList;
    public List<String> fileNameList;
    public List<String> fileDoneList;
    private RecylerviewClickInterface recylerviewClickInterface;

    public PhotoUploadAdapter(List<String>photoUrlList, List<String>fileNameList, List<String>fileDoneList){
        this.photoUrlList =photoUrlList;
        this.fileDoneList =fileDoneList;
        this.fileNameList = fileNameList;
        this.recylerviewClickInterface = recylerviewClickInterface;
    }


    @NonNull
    @Override
    public FileHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_level_e_photo_item, parent,false);
        return new FileHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FileHolder holder, int position) {

        Log.e("Level_E_Add", "onResult:FileToUpload: Adapter position "+position+ "photoUrlList.get(position)"+photoUrlList.get(position)+" fileDoneList.get(position)  = "+fileDoneList.get(position)  +  " fileNameList.get(position)  "+fileNameList.get(position) );
        if(fileDoneList.get(position) != null   &&  fileNameList.get(position) != null){
            String dFileName = fileNameList.get(position);
            holder.mFileNameText.setText(dFileName);
            String dFileDone = fileDoneList.get(position);
            if(photoUrlList.get(position) != null){
                String dsPhotoUrl = photoUrlList.get(position);
                if(TextUtils.isEmpty(dsPhotoUrl)){
                    dsPhotoUrl = "NO";
                    Log.e("Level_E_Add", "onResult:FileToUpload: TXTUTILIS");

                }if(!dsPhotoUrl.equals("NO"))
                    Picasso.get().load(dsPhotoUrl).fit().centerCrop().into(holder.mFileProductImage);
            }


            if(dFileDone.equals("uploading")){
                holder.mFileProgressText.setText("Progress : uploading...");
            }else if(dFileDone.equals("done")){
                holder.mFileProgressText.setText("Progress : done");
                holder.mFileCloudImg.setImageResource(R.drawable.ic_baseline_cloud_done_24);
            }else if(dFileDone.equals("retrive")){
                holder.mFileProgressText.setText("Progress : retrive");
                holder.mFileCloudImg.setImageResource(R.drawable.ic_baseline_cloud_done_24);
            }else{
                holder.mFileProgressText.setText("Progress : wait");
            }
        }

    }

    @Override
    public int getItemCount() {
        return fileNameList.size();
    }

    public class FileHolder extends  RecyclerView.ViewHolder{
        View mView;
        TextView mFileNameText, mFileProgressText, mFileTypeText, mFileSizeText;
        ImageView mFileCloudImg, mFileDeleteImageBtn, mFileProductImage;
        public FileHolder(View itemview){
            super(itemview);
            mView = itemview;
            mFileNameText = (TextView)itemview.findViewById(R.id.sdbcf_add_filename);
            mFileProgressText = (TextView)itemview.findViewById(R.id.sdbcf_add_progress);
            mFileCloudImg = (ImageView)itemview.findViewById(R.id.sdbcf_add_file_cloud_icon);
            mFileProductImage = (ImageView)itemview.findViewById(R.id.sdbcf_add_file_image_view);
            mFileDeleteImageBtn = (ImageView)itemview.findViewById(R.id.sdbcf_add_file_delete_btn);
            mFileTypeText = (TextView)itemview.findViewById(R.id.sdbcf_add_file_type);
            mFileSizeText = (TextView)itemview.findViewById(R.id.sdbcf_add_file_size);
            mFileDeleteImageBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    recylerviewClickInterface.onItemClick(getAdapterPosition());
                }
            });
        }
    }
}
