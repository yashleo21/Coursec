package com.example.prafu.testcoursec.Activity;

import android.app.NotificationManager;
import android.content.Intent;
import android.graphics.Canvas;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.NotificationCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
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
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.mancj.slideup.SlideUp;

import java.io.File;
import java.io.IOException;

public class Notes_PdfViewing extends AppCompatActivity  {

    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;
    private FirebaseStorage firebaseStorage;

    private ValueEventListener value;

    private RelativeLayout slider;
    private CoordinatorLayout containers;
    private SlideUp slideUp;
    private FloatingActionButton fab;

    private String pdfLink;
    private PDFView pdfView;
    private String key;
    private LinearLayout mLayout;
    private String category;

    private TextView pdfInfoTotal;
    private TextView pdfInfo;
    private String PDF = "Page No. : ";
    private String TOTAL_PDF ="Total Pages: ";

    private TextView tSubject;
    private TextView tTitle;


    private String subject;
    private String title;

    private NotificationManager mNotifyManager;
    private NotificationCompat.Builder mBuilder;
    int id = 1;

    final long ONE_MEGABYTE = 1024 * 1024 * 5;



    private ProgressBar pBar_view_content;

    public void setProgressVisibility(boolean b){
        if(b==true)
            pBar_view_content.setVisibility(View.VISIBLE);
        else
            pBar_view_content.setVisibility(View.GONE);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pdf_viewing);
        setTitle("Document");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        mLayout = (LinearLayout) findViewById(R.id.pdfInfo);
        pdfInfo = (TextView) findViewById(R.id.textPdfCount);
        pdfInfoTotal = (TextView) findViewById(R.id.textPdfTotal);

        slider = (RelativeLayout) findViewById(R.id.slider);
        containers = (CoordinatorLayout) findViewById(R.id.containers);
        fab = (FloatingActionButton) findViewById(R.id.fab);

        tSubject = (TextView) findViewById(R.id.tSubject);
        tTitle = (TextView) findViewById(R.id.tTitle);
//        webV = (WebView) findViewById(R.id.webV);
//        webV.getSettings().setJavaScriptEnabled(true);

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

        firebaseDatabase = Utils.getDatabase();
        firebaseStorage = FirebaseStorage.getInstance();

        Bundle b = getIntent().getExtras();
        key = b.getString("key");
        category = b.getString("branch");

        databaseReference = firebaseDatabase.getReference().child("Temp").child("Category").child("Notes").child(category).child(key);

        pdfView = (PDFView) findViewById(R.id.pdfView);



        pBar_view_content = (ProgressBar) findViewById(R.id.pBar_view_content);
        setProgressVisibility(true);

        value = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {


                title = dataSnapshot.child("title").getValue().toString();
                subject = dataSnapshot.child("subject").getValue().toString();
                pdfLink = dataSnapshot.child("link").getValue().toString();

                tTitle.setText(title);
                tSubject.setText(subject);
//                webV.loadUrl("http://view.officeapps.live.com/op/view.aspx?src="+pdfLink);

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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.pdfviewing_menu,menu);
        return true;
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
            Toast.makeText(Notes_PdfViewing.this, "Error while loading file.", Toast.LENGTH_SHORT).show();
        }
    };

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                Intent homeIntent = new Intent(this, MainActivity.class);
                homeIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(homeIntent);
                return true;

            case R.id.mDownload:
                Toast.makeText(Notes_PdfViewing.this, "Download Started", Toast.LENGTH_SHORT).show();
                mBuilder = new NotificationCompat.Builder(Notes_PdfViewing.this);
                mBuilder.setContentTitle("Download")
                        .setContentText("Download in progress")
                        .setOngoing(true)
                        .setSmallIcon(R.drawable.ic_like);
                Downloader downloader = new Downloader();
                downloader.execute(pdfLink);
                item.setEnabled(false);

                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public class Downloader extends AsyncTask<String,Integer,Integer> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            mBuilder.setProgress(100,0,false);
            mNotifyManager.notify(id,mBuilder.build());
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            mBuilder.setProgress(100,values[0],false);
            mNotifyManager.notify(1,mBuilder.build());
            super.onProgressUpdate(values);
        }

        @Override
        protected Integer doInBackground(String... strings) {
            String pdfLink = strings[0];
            StorageReference mRef = firebaseStorage.getReferenceFromUrl(pdfLink);
            String dirpath=android.os.Environment.getExternalStorageDirectory().toString();
            File folder = new File(dirpath+"/Coursec/Downloads");
            boolean success = true;
            if (!folder.exists()) {
                success = folder.mkdir();
            }
            if(success){
                try {
                    File fak = File.createTempFile("test","pdf",folder);
                    mRef.getFile(fak).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                            Log.d("Complete","Download");
                        }
                    }).addOnProgressListener(new OnProgressListener<FileDownloadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(FileDownloadTask.TaskSnapshot taskSnapshot) {
                            double progress = (100.0 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount();
                            Log.d("Progress",Double.toString(progress));
                            publishProgress((int)progress);
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.d("Download","Failed");
                        }
                    });
                } catch (IOException e) {
                    e.printStackTrace();

                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(Integer integer) {
            super.onPostExecute(integer);
            mBuilder.setContentText("Download Complete");
            mBuilder.setProgress(0,0,false);
            mBuilder.setVibrate(new long[] { 1000, 1000});
            mBuilder.setOngoing(false);
            mNotifyManager.notify(1,mBuilder.build());
            Toast.makeText(Notes_PdfViewing.this, "Download Complete", Toast.LENGTH_SHORT).show();

        }
    }



}
