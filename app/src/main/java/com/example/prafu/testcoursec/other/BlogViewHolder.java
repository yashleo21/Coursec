package com.example.prafu.testcoursec.other;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.example.prafu.testcoursec.R;


public class BlogViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

    View mView;
    TextView textTitle;
    TextView textDesc;
    TextView textCat;
    
    public BlogViewHolder(View itemView) {
        super(itemView);
        mView = itemView;

    }

    public void setTitle(String title){
        textTitle = (TextView)mView.findViewById(R.id.title);
        textTitle.setText(title);
    }
//    public void setDesc(String description){
//        textDesc = (TextView)mView.findViewById(R.id.description);
//        textDesc.setText(description);
//    }

    public void setCat(String category){
       textCat = (TextView)mView.findViewById(R.id.category);
        textCat.setText(category);
    }


    @Override
    public void onClick(View view) {

    }
}