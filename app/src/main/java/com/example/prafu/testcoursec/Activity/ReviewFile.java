package com.example.prafu.testcoursec.Activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Canvas;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.prafu.testcoursec.MainActivity;
import com.example.prafu.testcoursec.R;
import com.example.prafu.testcoursec.other.Utils;
import com.github.barteksc.pdfviewer.PDFView;
import com.github.barteksc.pdfviewer.listener.OnDrawListener;
import com.github.barteksc.pdfviewer.listener.OnErrorListener;
import com.github.barteksc.pdfviewer.listener.OnLoadCompleteListener;
import com.github.barteksc.pdfviewer.listener.OnPageChangeListener;
import com.github.barteksc.pdfviewer.listener.OnPageScrollListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.mancj.slideup.SlideUp;

import java.io.File;
import java.io.IOException;

public class ReviewFile extends AppCompatActivity {

    private RelativeLayout slider;
    private SlideUp slideUp;
    private FloatingActionButton fab;
    private CoordinatorLayout container;

    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;
    private FirebaseStorage firebaseStorage;
    private DatabaseReference rootRef;
    private DatabaseReference ref;

    private ValueEventListener value;

    private String pdfLink;
    private PDFView pdfView;
    private String key;
    private LinearLayout mLayout;

    private TextView pdfInfoTotal;
    private TextView pdfInfo;
    private String PDF = "Page No. : ";
    private String TOTAL_PDF ="Total Pages: ";

    private Button mApprove;
    private Button mReject;

    private TextView tBranch;
    private TextView tSubject;
    private TextView tTitle;

    private String branch;
    private String subject;
    private String title;
    private String notetype;
    private String user;
    private String category;

    private String email;

    final long ONE_MEGABYTE = 1024 * 1024 * 10;

    private ProgressBar pBar_view_content;

    public void setProgressVisibility(boolean b){
        if(b==true)
            pBar_view_content.setVisibility(View.VISIBLE);
        else
            pBar_view_content.setVisibility(View.GONE);
    }

    public int increment(int val){

        return val+1;

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_review_file);

        firebaseDatabase = Utils.getDatabase();
        firebaseStorage = FirebaseStorage.getInstance();

        mLayout = (LinearLayout) findViewById(R.id.pdfInfo);
        pdfInfo = (TextView) findViewById(R.id.textPdfCount);
        pdfInfoTotal = (TextView) findViewById(R.id.textPdfTotal);

        mApprove = (Button) findViewById(R.id.mApprove);
        mReject = (Button) findViewById(R.id.mReject);
        mApprove.setEnabled(false);
        mReject.setEnabled(false);

        mApprove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setProgressVisibility(true);
                if(notetype.equalsIgnoreCase("Notes"))
                {
                    if(category.equalsIgnoreCase("Engineering")){
                        ref = firebaseDatabase.getReference().child("Temp").child("Category").child(notetype).child(category).child(branch).push();
                    }
                    else {
                        ref = firebaseDatabase.getReference().child("Temp").child("Category").child(notetype).child(category).push();
                    }
                }

                else if(notetype.equalsIgnoreCase("Question Paper")){
                    ref = firebaseDatabase.getReference().child("Temp").child("Category").child(notetype).child(branch).push();
                }

                else{
                    ref = firebaseDatabase.getReference().child("Temp").child("Category").child(notetype).push();
                }

                ref.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        ref.child("title").setValue(title);
                        ref.child("user").setValue(user);
                        ref.child("subject").setValue(subject);
                        ref.child("link").setValue(pdfLink);
                        ref.child("user").setValue(user);

                        final DatabaseReference uRef = firebaseDatabase.getReference().child("Users").child(user);
                        uRef.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                try {                                                                                               // CODE FOR COUNTER 5-MAY-17 YASH
                                    int val = (Integer) dataSnapshot.child("postcount").getValue();
                                    val = increment(val);
                                    uRef.child("postcount").setValue(val);
                                }
                                catch (Exception e){
                                    int val = 1;
                                    uRef.child("postcount").setValue(val);
                                }

                                finally {
                                    uRef.child("Uploads").push().child("title").setValue(title);
                                }
                                }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });
//                        SendMail sm = new SendMail(ReviewFile.this,email,"Your document is approved","Your file has been approved.");
//                        sm.execute();
                        setProgressVisibility(false);

                        new AlertDialog.Builder(ReviewFile.this)
                                .setMessage("The document has been approved.")
                                .setNeutralButton("OK", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
//                                modifyViews();
                                        databaseReference.removeEventListener(value);
                                        databaseReference.setValue(null);
                                        startActivity(new Intent(ReviewFile.this,MainActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));

                                    }
                                })
                                .show();
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        setProgressVisibility(false);
                        Toast.makeText(ReviewFile.this, "Error"+ databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });

        mReject.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {
                setProgressVisibility(true);
                StorageReference locOfFile = firebaseStorage.getReferenceFromUrl(pdfLink);
                locOfFile.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        setProgressVisibility(false);
//                        SendMail sm = new SendMail(ReviewFile.this,email,"Your document is rejected","Your file has been rejected due to improper format.");
//                        sm.execute();
                        new AlertDialog.Builder(ReviewFile.this)
                                .setMessage("The document has been removed.")
                                .setNeutralButton("OK", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        databaseReference.removeEventListener(value);
                                        databaseReference.setValue(null);
                                        startActivity(new Intent(ReviewFile.this,MainActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
                                    }
                                })
                                .show();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        setProgressVisibility(false);
                        Log.d("File","Deletion error."+e.getMessage());
                    }
                });

            }
        });

        tBranch = (TextView) findViewById(R.id.tBranch);
        tSubject = (TextView) findViewById(R.id.tSubject);
        tTitle = (TextView) findViewById(R.id.tTitle);


        slider = (RelativeLayout) findViewById(R.id.slider);
        container = (CoordinatorLayout) findViewById(R.id.containers);
        fab = (FloatingActionButton) findViewById(R.id.fab);

        slideUp = new SlideUp.Builder(slider)
                .withListeners(new SlideUp.Listener() {
                    @Override
                    public void onSlide(float percent) {
                        slider.setAlpha(1 - (percent / 100));
                    }

                    @Override
                    public void onVisibilityChanged(int visibility) {
                        if (visibility == View.GONE) {
//                            container.setAlpha(1);
                            fab.show();
                        }
                    }
                })
                .withStartGravity(Gravity.BOTTOM)
                .withLoggingEnabled(true)
                .withStartState(SlideUp.State.HIDDEN)
                .build();

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                slideUp.show();
//                container.setAlpha(0.4f);
                fab.hide();
            }
        });




        Bundle b = getIntent().getExtras();
        key = b.getString("key");

        databaseReference = firebaseDatabase.getReference().child("Temp").child("Pending").child(key);
        rootRef = firebaseDatabase.getReference();


        pdfView = (PDFView) findViewById(R.id.pdfView);

        pBar_view_content = (ProgressBar) findViewById(R.id.pBar_view_content);
        setProgressVisibility(true);

        value = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {


                pdfLink = dataSnapshot.child("link").getValue().toString();
                title = dataSnapshot.child("title").getValue().toString();
                subject = dataSnapshot.child("subject").getValue().toString();
                user = dataSnapshot.child("user").getValue().toString();
                branch = dataSnapshot.child("branch").getValue().toString();
                category = dataSnapshot.child("category").getValue().toString();
                notetype = dataSnapshot.child("notetype").getValue().toString();



                tBranch.setText(branch);
                tTitle.setText(title);
                tSubject.setText(subject);


                StorageReference locOfFile = firebaseStorage.getReferenceFromUrl(pdfLink);
                try {
                    final File localFile = File.createTempFile("tempdf", "pdf");
                    locOfFile.getFile(localFile).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                            pdfView.fromFile(localFile)
                                    .enableSwipe(true)
                                    .swipeHorizontal(true)
                                    .enableDoubletap(true)
                                    .defaultPage(0)
                                    .onDraw(onDrawListener)
                                    .onLoad(onLoadCompleteListener)
                                    .onPageChange(onPageChangeListener)
                                    .onPageScroll(onPageScrollListener)
                                    .onError(onErrorListener)
                                    .enableAnnotationRendering(false)
                                    .password(null)
                                    .scrollHandle(null)
                                    .load();
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.d("Failed to render",e.getMessage());
                        }
                    });
                } catch (IOException e) {
                    e.printStackTrace();
                }

//                locOfFile.getBytes(ONE_MEGABYTE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
//                    @Override
//                    public void onSuccess(byte[] bytes) {
//                        pdfView.fromBytes(bytes)
//                                .enableSwipe(true)
//                                .swipeHorizontal(true)
//                                .enableDoubletap(true)
//                                .defaultPage(0)
//                                .onDraw(onDrawListener)
//                                .onLoad(onLoadCompleteListener)
//                                .onPageChange(onPageChangeListener)
//                                .onPageScroll(onPageScrollListener)
//                                .onError(onErrorListener)
//                                .enableAnnotationRendering(false)
//                                .password(null)
//                                .scrollHandle(null)
//                                .load();
//                    }
//                });

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };

        databaseReference.addValueEventListener(value);

    }


    OnPageChangeListener onPageChangeListener = new OnPageChangeListener() {
        @Override
        public void onPageChanged(int page, int pageCount) {
            pdfInfo.setText(PDF+page+"/"+pageCount);
        }
    };

    OnDrawListener onDrawListener = new OnDrawListener() {
        @Override
        public void onLayerDrawn(Canvas canvas, float pageWidth, float pageHeight, int displayedPage) {

        }
    };

    OnLoadCompleteListener onLoadCompleteListener = new OnLoadCompleteListener() {
        @Override
        public void loadComplete(int nbPages) {
            setProgressVisibility(false);
            mApprove.setEnabled(true);
            mReject.setEnabled(true);


//            DatabaseReference emailId = firebaseDatabase.getReference().child("Users").child(user);
//            emailId.addListenerForSingleValueEvent(new ValueEventListener() {
//                @Override
//                public void onDataChange(DataSnapshot dataSnapshot) {
//                    email = dataSnapshot.child("e-mail").getValue().toString();
//                }
//
//                @Override
//                public void onCancelled(DatabaseError databaseError) {
//
//                }
//            });
            mApprove.setAlpha(1);
            mReject.setAlpha(1);
            pdfInfo.setText(PDF+("1"));
            pdfInfoTotal.setText(TOTAL_PDF+ nbPages);
            mLayout.setVisibility(View.VISIBLE);

        }
    };

    OnPageScrollListener onPageScrollListener = new OnPageScrollListener() {
        @Override
        public void onPageScrolled(int page, float positionOffset) {
            pdfInfo.setText(PDF+(page+1));
        }
    };

    OnErrorListener onErrorListener = new OnErrorListener() {
        @Override
        public void onError(Throwable t) {
            setProgressVisibility(false);
            Toast.makeText(ReviewFile.this, "Error while loading file.", Toast.LENGTH_SHORT).show();
        }
    };
}
