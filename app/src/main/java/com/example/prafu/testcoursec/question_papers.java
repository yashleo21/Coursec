package com.example.prafu.testcoursec;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.prafu.testcoursec.Activity.Show_QuestionPaper;
import com.example.prafu.testcoursec.other.RecyclerItemClickListener;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 *
 * to handle interaction events.
 *
 * create an instance of this fragment.
 */
public class question_papers extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER

    RecyclerView recyclerView;

    Context context;

    RecyclerView.Adapter recyclerView_Adapter;

    RecyclerView.LayoutManager recyclerViewLayoutManager;

    private String selectedBranch;

    String[] categories = {
            "Computer Science & Engineering (CSE)","Electrical and Electronics Engineering (ECE)","Mechanical Engineering","Electrical Engineering","Information & Technology(IT)"

    };

 int[] notes_icon={
        R.drawable.engineering,
        R.drawable.law,
//        R.drawable.commerce,
//        R.drawable.Foreignlanguage,
//        R.drawable.Science,
//        R.drawable.Competive_exams,
//        R.drawable.Management,
//        R.drawable.Arts,
//        R.drawable.Medical,
//        R.drawable.Literature,
//        R.drawable.Journalism,
//        R.drawable.SchoolNotes,
};



    public question_papers() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     *
     * @return A new instance of fragment question_papers.
     */
    // TODO: Rename and change types and number of parameters


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);





    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v= inflater.inflate(R.layout.fragment_question_papers, container, false);
        recyclerView = (RecyclerView) v.findViewById(R.id.recycler_view1);


        //Change 2 to your choice because here 2 is the number of Grid layout Columns in each row.
        recyclerViewLayoutManager = new GridLayoutManager(getContext(), 2);

        recyclerView.setLayoutManager(recyclerViewLayoutManager);

        recyclerView_Adapter = new QuestionpapersAdapter(getContext(),categories);

        recyclerView.setAdapter(recyclerView_Adapter);

        recyclerView.addOnItemTouchListener(new RecyclerItemClickListener(getContext(),recyclerView,new RecyclerItemClickListener.OnItemClickListener(){
            @Override
            public void onItemClick(View view, int position) {
                selectedBranch = categories[position];
                Intent show_question = new Intent(getContext(), Show_QuestionPaper.class);
                show_question.putExtra("branch",selectedBranch);
                startActivity(show_question);
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

}
