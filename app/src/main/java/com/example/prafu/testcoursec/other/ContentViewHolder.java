package com.example.prafu.testcoursec.other;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.example.prafu.testcoursec.R;

/**
 * Created by Area51 on 30-Mar-17.
 */

public class ContentViewHolder extends RecyclerView.ViewHolder {

    View mView;
    TextView subName;
    TextView titleTextView;
    TextView uploaderName;



    public ContentViewHolder(View itemView) {
        super(itemView);
        mView = itemView;

    }

    public void setSubject(String subject){
        subName = (TextView)mView.findViewById(R.id.subName);
        subName.setText(subject);
    }

    public  void setTitle(String title){
        titleTextView = (TextView) mView.findViewById(R.id.titleTextView);
        titleTextView.setText(title);

    }

    public void setUploaderName(String name){
        uploaderName = (TextView) mView.findViewById(R.id.uploaderName);
        uploaderName.setText(name);
    }


}
