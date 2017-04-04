package com.example.prafu.testcoursec;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.prafu.testcoursec.Activity.Notes_Branches;
import com.example.prafu.testcoursec.Activity.Show_Notes;
import com.example.prafu.testcoursec.Adapters.NotesAdapter;
import com.example.prafu.testcoursec.other.RecyclerItemClickListener;


public class notes_fragment extends Fragment {

    private RecyclerView notesRecycler;
    private String selectedCategory;
    String[] categories = {
            "Engineering",
            "Law",
            "CA & CS",
            "Foreign Language",
            "Science",
            "Competitive Exams",
            "Management",
            "Arts",
            "Medical",
            "Literature",
            "Journalism",
            "School Notes"

    };
    private NotesAdapter notesAdapter;



    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER


    public notes_fragment() {
        // Required empty public constructor
    }



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View v = inflater.inflate(R.layout.notes_fragment, container, false);
        notesRecycler = (RecyclerView)v.findViewById(R.id.notesRecycler);
        notesAdapter = new NotesAdapter(categories,getContext());
        notesRecycler.setAdapter(notesAdapter);
        notesRecycler.setLayoutManager(new GridLayoutManager(getContext(),2));

        notesRecycler.addOnItemTouchListener(new RecyclerItemClickListener(getContext(),notesRecycler,new RecyclerItemClickListener.OnItemClickListener(){
            @Override
            public void onItemClick(View view, int position) {
                selectedCategory = categories[position];
                Log.d("SelectedCateogry",selectedCategory);
                if(selectedCategory.equalsIgnoreCase("Engineering")){
                    Intent to_branch = new Intent(getContext(),Notes_Branches.class);
                    to_branch.putExtra("selectedCategory",selectedCategory);
                    startActivity(to_branch);
                }
                else{
                    Intent to_notes = new Intent(getContext(), Show_Notes.class);
                    to_notes.putExtra("category",selectedCategory);
                    startActivity(to_notes);
                }

            }

            @Override
            public void onLongItemClick(View view, int position) {

            }
        }));

        return v;
    }

    // TODO: Rename method, update argument and hook method into UI event




    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}
