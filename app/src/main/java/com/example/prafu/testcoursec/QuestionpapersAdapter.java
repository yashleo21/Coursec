package com.example.prafu.testcoursec;

/**
 * Created by prafu on 3/28/2017.
 */

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;


public class QuestionpapersAdapter extends RecyclerView.Adapter<QuestionpapersAdapter.ViewHolder>{

    String[] values;
    Context context1;
int [] icons;
    public QuestionpapersAdapter(Context context2,String[] values2){

        values = values2;

        context1 = context2;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{

        public TextView textView;
        public ImageView icon_notes;

        public ViewHolder(View v){

            super(v);

            textView = (TextView) v.findViewById(R.id.category_question);
            icon_notes=(ImageView)v.findViewById(R.id.img_questionpapers_icon);

        }
    }

    @Override
    public QuestionpapersAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType){

        View view1 = LayoutInflater.from(context1).inflate(R.layout.questionpapers_recycleritems,parent,false);

        ViewHolder viewHolder1 = new ViewHolder(view1);

        return viewHolder1;
    }

    @Override
    public void onBindViewHolder(ViewHolder Vholder, int position){

        Vholder.textView.setText(values[position]);

        Vholder.textView.setBackgroundColor(Color.WHITE);

        Vholder.textView.setTextColor(Color.BLACK);
//        Vholder.icon_notes.setImageResource(icons[position]);

    }

    @Override
    public int getItemCount(){

        return values.length;
    }
}