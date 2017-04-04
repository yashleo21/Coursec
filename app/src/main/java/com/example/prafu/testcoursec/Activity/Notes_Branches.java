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

import com.example.prafu.testcoursec.Adapters.BranchAdapter;
import com.example.prafu.testcoursec.MainActivity;
import com.example.prafu.testcoursec.R;
import com.example.prafu.testcoursec.other.RecyclerItemClickListener;
import com.example.prafu.testcoursec.other.Utils;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

public class Notes_Branches extends AppCompatActivity {

    private String category;
    private RecyclerView branchRecycler;
    private ArrayList<String> branchList;
    private TextView tv_no_data;
    private BranchAdapter branchAdapter;

    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;

    private ProgressBar mProgressBar;

    private void setProgressVisible(Boolean b){
        if(b== true){
            mProgressBar.setVisibility(View.VISIBLE);
        }
        else if(b == false){
            mProgressBar.setVisibility(View.INVISIBLE);
        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notes__branches);
        Bundle b = getIntent().getExtras();
        category = b.getString("selectedCategory");
        setTitle(category);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        branchList = new ArrayList<>();
        tv_no_data = (TextView) findViewById(R.id.tv_no_data);
        mProgressBar = (ProgressBar) findViewById(R.id.pBar6);
        setProgressVisible(true);

       branchRecycler = (RecyclerView)findViewById(R.id.branchRecycler);
        branchAdapter = new BranchAdapter(branchList,this);
        branchRecycler.setAdapter(branchAdapter);
        branchRecycler.setLayoutManager(new LinearLayoutManager(this));

        branchRecycler.addOnItemTouchListener(new RecyclerItemClickListener(this,branchRecycler,new RecyclerItemClickListener.OnItemClickListener(){
            @Override
            public void onItemClick(View view, int position) {
                Intent to_notes = new Intent(Notes_Branches.this, Engineering_Notes.class);
                to_notes.putExtra("category",branchList.get(position));
                startActivity(to_notes);
            }

            @Override
            public void onLongItemClick(View view, int position) {

            }
        }));

        firebaseDatabase = Utils.getDatabase();
        databaseReference = firebaseDatabase.getReference();
        DatabaseReference ref_to_branches = databaseReference.child("Temp").child("Category").child("Notes").child(category);
        ref_to_branches.keepSynced(true);

        ref_to_branches.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                setProgressVisible(false);
                if(!dataSnapshot.exists()){
                    tv_no_data.setVisibility(View.VISIBLE);
                }
                else {
                    tv_no_data.setVisibility(View.GONE);
                    branchList.add(dataSnapshot.getKey().toString());
                    branchAdapter.notifyDataSetChanged();
//                    branchRecycler.invalidate();
                }
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
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
