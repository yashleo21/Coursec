package com.example.prafu.testcoursec.Adapters;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.prafu.testcoursec.R;

/**
 * Created by Area51 on 30-Mar-17.
 */

public class NotesAdapter extends RecyclerView.Adapter<NotesAdapter.NotesViewHolder> {

    String[] categories;
    Context context;

    public NotesAdapter(String[] categories, Context context) {
        this.categories = categories;
        this.context = context;
    }




    @Override
    public NotesViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.notes_recycleritem, parent, false);
        NotesViewHolder holder = new NotesViewHolder(v);
        return holder;
    }

    @Override
    public void onBindViewHolder(NotesViewHolder holder, int position) {

        holder.notes_category.setText(categories[position]);
    }

    @Override
    public int getItemCount() {
        return categories.length;
    }

    public static class  NotesViewHolder extends RecyclerView.ViewHolder{

        CardView notesCardView;
        TextView notes_category;

        public NotesViewHolder(View itemView) {
            super(itemView);

            notesCardView = (CardView) itemView.findViewById(R.id.notesCardView);
            notes_category = (TextView) itemView.findViewById(R.id.notes_category);

        }
    }



}


