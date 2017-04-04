package com.example.prafu.testcoursec.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.prafu.testcoursec.MainActivity;
import com.example.prafu.testcoursec.R;
import com.example.prafu.testcoursec.other.BlogViewHolder;
import com.example.prafu.testcoursec.other.Data;
import com.example.prafu.testcoursec.other.RecyclerItemClickListener;
import com.example.prafu.testcoursec.other.Utils;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

public class Pending extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private RecyclerView pending_list;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;
    private ProgressBar mprogressBar;
    private Query mQuery;
    private TextView empty_view;

    private RecyclerView.AdapterDataObserver mObserver;

    public static final int MSG_HIDE_PBAR = 0;
    static final long PBAR_DELAY = 10 * 1000;
    FirebaseRecyclerAdapter<Data,BlogViewHolder> firebaseRecyclerAdapter;
    private void setProgressVisible(Boolean b){
        if(b== true){
            mprogressBar.setVisibility(View.VISIBLE);
        }
        else if(b == false){
            mprogressBar.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<Data, BlogViewHolder>(

                Data.class,
                R.layout.row_layout,
                BlogViewHolder.class,
                mQuery
        ) {
            @Override
            protected void populateViewHolder(BlogViewHolder viewHolder, Data model, int position) {
                viewHolder.setTitle(model.getTitle());
//                viewHolder.setDesc(model.getDescription());
                viewHolder.setCat(model.getCategory());

                setProgressVisible(false);
            }


        };

        pending_list.setAdapter(firebaseRecyclerAdapter);
        firebaseRecyclerAdapter.registerAdapterDataObserver(mObserver);
    }



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pending);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setTitle("Pending Documents");
        firebaseDatabase = Utils.getDatabase();
        databaseReference = firebaseDatabase.getReference().child("Temp").child("Pending");
        databaseReference.keepSynced(true);
        mQuery = databaseReference.orderByChild("approved").equalTo(0);

        mprogressBar = (ProgressBar)findViewById(R.id.pBar);
        setProgressVisible(true);
        empty_view = (TextView) findViewById(R.id.empty_view);
        pending_list = (RecyclerView)findViewById(R.id.pending_list);
        pending_list.setHasFixedSize(true);
        pending_list.setLayoutManager(new LinearLayoutManager(this));

        mObserver = new RecyclerView.AdapterDataObserver() {
            @Override
            public void onItemRangeInserted(int positionStart, int itemCount) {
                empty_view.setVisibility(View.GONE);
                super.onItemRangeInserted(positionStart, itemCount);
            }

            @Override
            public void onItemRangeRemoved(int positionStart, int itemCount) {
                if(firebaseRecyclerAdapter.getItemCount()<1){
                    empty_view.setVisibility(View.VISIBLE);
                }
                super.onItemRangeRemoved(positionStart, itemCount);
            }
        };

        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                setProgressVisible(false);
                if(dataSnapshot.hasChildren()== false){
                    empty_view.setVisibility(View.VISIBLE);
                }
                else
                    empty_view.setVisibility(View.GONE);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        pending_list.addOnItemTouchListener(new RecyclerItemClickListener(this, pending_list ,new RecyclerItemClickListener.OnItemClickListener() {
                    @Override public void onItemClick(View view, int position) {
                        DatabaseReference dRef = firebaseRecyclerAdapter.getRef(position);
                        String key = dRef.getKey();
                        Intent transfer = new Intent(Pending.this,ReviewFile.class);
                        transfer.putExtra("key",key);
                        startActivity(transfer);
                    }

                    @Override public void onLongItemClick(View view, int position) {
                        // do whatever
                    }
                })
        );


    }

    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        switch (menuItem.getItemId()) {
            case android.R.id.home:
                Intent homeIntent = new Intent(this, MainActivity.class);
                homeIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(homeIntent);
        }
        return (super.onOptionsItemSelected(menuItem));
    }
}
