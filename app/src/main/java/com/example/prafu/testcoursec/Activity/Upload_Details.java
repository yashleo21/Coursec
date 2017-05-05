package com.example.prafu.testcoursec.Activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.prafu.testcoursec.MainActivity;
import com.example.prafu.testcoursec.R;
import com.example.prafu.testcoursec.other.SendMail;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;

public class Upload_Details extends AppCompatActivity implements AdapterView.OnItemSelectedListener, View.OnClickListener{


    private Spinner noteType;
    private Spinner branchName;
    private Spinner categoryName;

    private String CATEGORY="";
    private String BRANCH="";
    private RelativeLayout ley;

    private EditText docSubject;
    private EditText docTitle;
    private Button bSubmit;

    private String SUBJECT;
    private String TITLE;
    private String NoteType="Select Document Type";

    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;
    private FirebaseStorage firebaseStorage;
    private StorageReference storageReference;
    private FirebaseAuth mAuth;

    private ProgressDialog pDialog;

    private void hideKeyboard() {
        View view = getCurrentFocus();
        if (view != null) {
            ((InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE)).
                    hideSoftInputFromWindow(view.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload__details);
        hideKeyboard();
        firebaseStorage = FirebaseStorage.getInstance();
        storageReference = firebaseStorage.getReference();
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference();
        mAuth = FirebaseAuth.getInstance();

        pDialog = new ProgressDialog(Upload_Details.this);


        ley = (RelativeLayout) findViewById(R.id.ley);
        ley.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                hideKeyboard();
                return true;

            }
        });
        docSubject = (EditText) findViewById(R.id.docSubject);
        docTitle = (EditText) findViewById(R.id.docTitle);
        bSubmit = (Button) findViewById(R.id.btnSubmit);
        bSubmit.setEnabled(true);
        bSubmit.setOnClickListener(this);


        ArrayList<String> arrayList = new ArrayList<>();
        arrayList.addAll(Arrays.asList("Select Document Type","Notes","Question Paper","Ebook"));
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this,android.R.layout.simple_dropdown_item_1line,arrayList);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        final String[] categories = {
                "Select Category",
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

        ArrayList<String> broadCategories = new ArrayList<>();
        broadCategories.addAll(Arrays.asList(categories));
        ArrayAdapter<String> categoryAdapter = new ArrayAdapter<String>(this,android.R.layout.simple_dropdown_item_1line,broadCategories);
        categoryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);


        ArrayList<String> branchList = new ArrayList<>();
        branchList.addAll(Arrays.asList("Select Branch","Computer Science & Engineering (CSE)","Electrical and Electronics Engineering (ECE)","Mechanical Engineering","Electrical Engineering","Information & Technology(IT)"));
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,android.R.layout.simple_dropdown_item_1line,branchList);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        noteType = (Spinner) findViewById(R.id.noteType);
        branchName = (Spinner) findViewById(R.id.branchName);
        branchName.setVisibility(View.GONE);
        categoryName= (Spinner) findViewById(R.id.categoryName);
        categoryName.setVisibility(View.GONE);


        noteType.setAdapter(dataAdapter);
        branchName.setAdapter(adapter);
        categoryName.setAdapter(categoryAdapter);


        noteType.setOnItemSelectedListener(this);
        branchName.setOnItemSelectedListener(this);
        categoryName.setOnItemSelectedListener(this);


    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {


        switch (adapterView.getId()){
            case R.id.noteType:
                NoteType = adapterView.getItemAtPosition(i).toString();
                Log.d("XXX","Listener loaded atleast");
                Log.d("Notes",NoteType);
                if (NoteType.equalsIgnoreCase("Notes")){
                    categoryName.setVisibility(View.VISIBLE);
                    CATEGORY = "hold";
                }
                else if(NoteType.equalsIgnoreCase("Question Paper")){
                    branchName.setVisibility(View.VISIBLE);
                    categoryName.setVisibility(View.GONE);
                    CATEGORY="";
                    BRANCH="hold";
                }
                else{
                    BRANCH="";
                    CATEGORY="";
                    branchName.setVisibility(View.GONE);
                    categoryName.setVisibility(View.GONE);
                }
                break;

            case R.id.branchName:
                BRANCH = adapterView.getItemAtPosition(i).toString();
                branchName.setVisibility(View.VISIBLE);
                break;

            case R.id.categoryName:
                CATEGORY = adapterView.getItemAtPosition(i).toString();
                if(CATEGORY.equalsIgnoreCase("Engineering")){
                    branchName.setVisibility(View.VISIBLE);
                    BRANCH="hold";
                }
                else{
                    branchName.setVisibility(View.GONE);
                    BRANCH="";
                }

        }



    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }

    @Override
    public void onClick(View view) {

        view.setEnabled(false);
        if(!validate()){
            Log.d("Recheck","Your entries");
            Toast.makeText(this, "Recheck your entries", Toast.LENGTH_SHORT).show();
            view.setEnabled(true);
            return;
        }
        SUBJECT = docSubject.getText().toString();
        TITLE = docTitle.getText().toString();

        pDialog.setMax(100);
        pDialog.setMessage("Uploading File...");
        pDialog.setTitle("Upload Status");
        pDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        pDialog.show();
        doTheDeed(TITLE,NoteType,SUBJECT);

    }

    private void doTheDeed(final String title, final String desc, final String subj) {
        Bundle bundle = getIntent().getExtras();
        final String path = bundle.getString("filepath");
        Log.d("Path",path);
        final Uri file = Uri.fromFile(new File(path));
        Log.d("URIPATH",file.toString());
        Log.d("LASTSEGMENT",file.getLastPathSegment());
        StorageReference newFile = storageReference.child("images/"+file.getLastPathSegment());


            UploadTask uploadTask = newFile.putFile(file);
        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d("Upload","UploadFailure"+e.getMessage());
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                final Uri downloadUrl = taskSnapshot.getDownloadUrl();
                final DatabaseReference dRef = databaseReference.child("Temp").child("Pending").push();              //MODIFIED 12-3-2017
                dRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        dRef.child("title").setValue(title);
                        dRef.child("notetype").setValue(desc);
                        dRef.child("subject").setValue(subj);
                        dRef.child("user").setValue(mAuth.getCurrentUser().getUid());
                        dRef.child("category").setValue(CATEGORY);
                        dRef.child("branch").setValue(BRANCH);
                        dRef.child("link").setValue(downloadUrl.toString());
                        dRef.child("approved").setValue(0);
                        dRef.child("filetype").setValue(path.substring(path.lastIndexOf(".")+1,path.length()));
                        Log.d("Upload","Success");

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        Log.d("Database","Cancelled"+databaseError.getDetails());
                    }
                });
                Log.d("DownloadUrl",downloadUrl.toString());
            }
        }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                double progress = (100.0 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount();
                Log.d("Progress", Double.toString(progress));
                int currentprogress = (int) progress;
                pDialog.setProgress(currentprogress);
            }
        }).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                pDialog.dismiss();
                String email = mAuth.getCurrentUser().getEmail();
                SendMail sm = new SendMail(Upload_Details.this,email,"Your file is under review",SUBJECT);
                sm.execute();
                startActivity(new Intent(Upload_Details.this,MainActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
            }
        });
    }

    public boolean validate(){

        if(CATEGORY.equalsIgnoreCase("hold") || CATEGORY.equalsIgnoreCase("Select Category")){
            return false;
        }

        if(BRANCH.equalsIgnoreCase("hold") || BRANCH.equalsIgnoreCase("Select Branch")){
            return false;
        }


        if(NoteType.equalsIgnoreCase("Select Document Type")){
            return false;
        }

        if(TextUtils.isEmpty(docSubject.getText().toString())){
            docSubject.setError("Please enter the subject");
            return false;
        }
        else{
            docSubject.setError(null);
        }

        if(TextUtils.isEmpty(docTitle.getText().toString())){
            docTitle.setError("Please enter the subject");
            return false;
        }
        else{
            docTitle.setError(null);
        }
        return true;
    }
}
