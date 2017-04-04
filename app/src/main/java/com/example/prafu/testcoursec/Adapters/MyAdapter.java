package com.example.prafu.testcoursec.Adapters;

import android.app.Activity;
import android.content.Context;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.prafu.testcoursec.R;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.util.ArrayList;

/**
 * Created by Area51 on 17-Mar-17.
 */

public class MyAdapter extends RecyclerView.Adapter<MyAdapter.ViewHolder>  {


    private ArrayList<String> galleryList;
    private Context context;

    public MyAdapter(ArrayList<String> galleryList, Context context) {
        this.galleryList = galleryList;
        this.context = context;

    }

    @Override
    public MyAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.gallery_layout, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MyAdapter.ViewHolder holder, final int position) {

        holder.img.setScaleType(ImageView.ScaleType.CENTER_CROP);
        Glide.with(context).load(galleryList.get(position)).fitCenter().into(holder.img);
        holder.bTon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(context, "Clincked", Toast.LENGTH_SHORT).show();
                Uri uri = Uri.parse("file://"+galleryList.get(position));
                Log.d("Current",uri.toString());
                CropImage.activity(uri)
                        .setGuidelines(CropImageView.Guidelines.ON)
                        .setOutputUri(uri)
                        .start((Activity)context);

            }
        });

    }


    @Override
    public int getItemCount() {
        return galleryList.size();
    }



    public class ViewHolder extends RecyclerView.ViewHolder{
        private ImageView img;
        private Button bTon;
        public ViewHolder(View view) {
            super(view);

            img = (ImageView) view.findViewById(R.id.img);
            bTon = (Button) view.findViewById(R.id.bTon);
        }
    }

}
