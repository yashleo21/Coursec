package com.example.prafu.testcoursec;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import de.hdodenhof.circleimageview.CircleImageView;

public class CompleteProfile extends AppCompatActivity {

    private Button setupSubmitBtn;
    private EditText setupNameField;
    private EditText setupCollegeField;
    private CircleImageView setupProfilePic;
    private static final int GALLERY_REQ_CODE = 1;
    private Uri profileUri = null;
    private DatabaseReference mDBUsers;
    private FirebaseAuth mAuth;
    private StorageReference mRef;
    private ProgressDialog mDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_complete_profile);
        setTitle("Complete your Profile");

        setupNameField = (EditText) findViewById(R.id.setupNameField);
        setupCollegeField = (EditText) findViewById(R.id.setupCollegeField);
        setupSubmitBtn = (Button) findViewById(R.id.setupSubmitBtn);
        setupProfilePic = (CircleImageView) findViewById(R.id.setupProfilePic);
        mDBUsers = FirebaseDatabase.getInstance().getReference().child("Users");
        mAuth = FirebaseAuth.getInstance();
        mRef = FirebaseStorage.getInstance().getReference().child("User_images");
        mDialog = new ProgressDialog(this);


        setupProfilePic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent pickImg = new Intent(Intent.ACTION_GET_CONTENT);
                pickImg.setType("image/*");
                startActivityForResult(pickImg,GALLERY_REQ_CODE);
            }
        });

        setupSubmitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!isNetworkConnected())
                    Toast.makeText(CompleteProfile.this, "No internet connection. Try again!", Toast.LENGTH_SHORT).show();
                else
                    startSetupAccount();
            }
        });
    }

    private void startSetupAccount() {
        final String name = setupNameField.getText().toString();
        final String collegeName = setupCollegeField.getText().toString();
        final String user_id = mAuth.getCurrentUser().getUid();
        if(!TextUtils.isEmpty(name) && profileUri !=null && !TextUtils.isEmpty(collegeName)){
            mDialog.setMessage("Setting up...");
            mDialog.show();
            mRef.child(profileUri.getLastPathSegment()).putFile(profileUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    String downLink = taskSnapshot.getDownloadUrl().toString();
                    mDBUsers.child(user_id).child("username").setValue(name);
                    mDBUsers.child(user_id).child("profilepic").setValue(downLink);
                    Uri link = Uri.parse(downLink);
                    UserProfileChangeRequest userProfileChangeRequest = new UserProfileChangeRequest.Builder()
                            .setPhotoUri(link)
                            .build();
                    mAuth.getCurrentUser().updateProfile(userProfileChangeRequest);
                    mDialog.dismiss();
                    startActivity(new Intent(CompleteProfile.this,MainActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    mDialog.dismiss();
                    new AlertDialog.Builder(CompleteProfile.this)
                            .setMessage("Could not complete the task. Please try again.")
                            .setPositiveButton("OK",null)
                            .show();
                }
            });

        }
        else
            new AlertDialog.Builder(this)
                    .setMessage("Please enter data in all fields.")
                    .setNeutralButton("OK",null)
                    .show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode==GALLERY_REQ_CODE && resultCode == RESULT_OK && data!=null){
            profileUri = data.getData();
            CropImage.activity(profileUri)
                    .setGuidelines(CropImageView.Guidelines.ON)
                    .setAspectRatio(1,1)
                    .start(this);
        }
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                profileUri = result.getUri();
                setupProfilePic.setImageURI(profileUri);

            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
            }
        }
    }

    private boolean isNetworkConnected() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

        return cm.getActiveNetworkInfo() != null;
    }
}
