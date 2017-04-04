package com.example.prafu.testcoursec.Activity;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.prafu.testcoursec.Adapters.MyAdapter;
import com.example.prafu.testcoursec.R;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.pdf.PdfWriter;
import com.theartofdev.edmodo.cropper.CropImage;
import com.vlk.multimager.activities.GalleryActivity;
import com.vlk.multimager.activities.MultiCameraActivity;
import com.vlk.multimager.utils.Constants;
import com.vlk.multimager.utils.Image;
import com.vlk.multimager.utils.Params;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.UUID;

public class UploadSelector extends AppCompatActivity {

    private ImageButton bSelect;
    private ImageButton bCapture;
    private ImageButton bDocuments;
    private ArrayList<Image> imageList;
    private final int FILE_SELECT_CODE = 9;
    private TextView emptyView;
    private ArrayList<String> urls;
    private RecyclerView.AdapterDataObserver mObserver;
    MyAdapter adapter;
    private  RecyclerView recyclerView;
    private Button buttonF;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload_selector);

        urls = new ArrayList<String>();
        emptyView = (TextView)findViewById(R.id.emptyView);
        bSelect = (ImageButton)findViewById(R.id.bSelect);
        bCapture = (ImageButton)findViewById(R.id.bCapture);
        bDocuments = (ImageButton)findViewById(R.id.bDocument);


        buttonF = (Button) findViewById(R.id.buttonF);
        buttonF.setEnabled(false);

        buttonF.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String dirpath=android.os.Environment.getExternalStorageDirectory().toString();
                File folder = new File(dirpath+"/Coursec");
                boolean success = true;
                if (!folder.exists()) {
                    success = folder.mkdir();
                }
                if (success) {
                    try {
                        String path = dirpath+"/Coursec";
                        String filepath = path+"/pdf_"+ UUID.randomUUID().toString()+".pdf";
                        createPdf(filepath);
                        Intent to_upload_details = new Intent(UploadSelector.this,Upload_Details.class);
                        to_upload_details.putExtra("filepath",filepath);
                        to_upload_details.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(to_upload_details);
                    } catch (IOException e) {
                        e.printStackTrace();
                    } catch (DocumentException e) {
                        e.printStackTrace();
                    }
                } else {
                    // Do something else on failure
                }

            }
        });

        recyclerView = (RecyclerView) findViewById(R.id.imagegallery);
        recyclerView.setHasFixedSize(true);

        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(this,2);
        recyclerView.setLayoutManager(layoutManager);
        adapter = new MyAdapter(urls,this);
        recyclerView.setAdapter(adapter);

        if (urls.isEmpty()) {
            recyclerView.setVisibility(View.GONE);
            emptyView.setVisibility(View.VISIBLE);
        }
        else {
            recyclerView.setVisibility(View.VISIBLE);
            emptyView.setVisibility(View.GONE);
        }

        mObserver = new RecyclerView.AdapterDataObserver() {
            @Override
            public void onItemRangeInserted(int positionStart, int itemCount) {
                emptyView.setVisibility(View.GONE);
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onItemRangeRemoved(int positionStart, int itemCount) {
                if(adapter.getItemCount()<1){
                    emptyView.setVisibility(View.VISIBLE);
                    adapter.notifyDataSetChanged();
                }
            }
        };

        adapter.registerAdapterDataObserver(mObserver);


        bSelect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(UploadSelector.this, GalleryActivity.class);
                Params params = new Params();
                params.setCaptureLimit(10);
                params.setPickerLimit(10);
                params.setToolbarColor(R.color.colorPrimary);
                params.setActionButtonColor(R.color.colorPrimaryDark);
                params.setButtonTextColor(R.color.white);
                intent.putExtra(Constants.KEY_PARAMS, params);
                startActivityForResult(intent, Constants.TYPE_MULTI_PICKER);
            }
        });
        bCapture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(UploadSelector.this, MultiCameraActivity.class);
                Params params = new Params();
                params.setCaptureLimit(10);
                params.setToolbarColor(R.color.colorPrimary);
                params.setActionButtonColor(R.color.colorPrimaryDark);
                params.setButtonTextColor(R.color.white);
                intent.putExtra(Constants.KEY_PARAMS, params);
                startActivityForResult(intent, Constants.TYPE_MULTI_CAPTURE);
            }
        });
        bDocuments.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showFileChooser();
            }
        });
    }

    private void copyFile(String inputPath, String inputFile, String outputPath,String file_path) {

        InputStream in = null;
        OutputStream out = null;
        try {

            //create output directory if it doesn't exist
            File dir = new File (outputPath);
            if (!dir.exists())
            {
                dir.mkdirs();
            }


            in = new FileInputStream(inputPath);
            out = new FileOutputStream(file_path);

            byte[] buffer = new byte[1024];
            int read;
            while ((read = in.read(buffer)) != -1) {
                out.write(buffer, 0, read);
            }
            in.close();
            in = null;

            // write the output file (You have now copied the file)
            out.flush();
            out.close();
            out = null;

        }  catch (FileNotFoundException fnfe1) {
            Log.e("tag", fnfe1.getMessage());
        }
        catch (Exception e) {
            Log.e("tag", e.getMessage());
        }

    }

    @Override
    public void onActivityResult(final int requestCode, int resultCode, final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != RESULT_OK) {
            return;
        }

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                Uri resultUri = result.getUri();
                Log.d("result",resultUri.toString());
                recyclerView.invalidate();
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
                Log.d("error",error.toString());
            }
        }

        switch (requestCode) {
            case Constants.TYPE_MULTI_CAPTURE:
            case Constants.TYPE_MULTI_PICKER:
                imageList = data.getParcelableArrayListExtra(Constants.KEY_BUNDLE_LIST);

                String root = Environment.getExternalStorageDirectory().getAbsolutePath()+"/";
                for(Image p: imageList){
                    try {
                        String path = p.imagePath;
                        String file_path = root + "TempCoursec" + File.separator +"IMG_"+ UUID.randomUUID().toString()+".jpg";
                        copyFile(path,path,root+"TempCoursec"+File.separator,file_path);
                        Log.d("PATH",path);
                        urls.add(file_path);
                        adapter.notifyDataSetChanged();
                        mObserver.onChanged();
                    }
                    catch (Exception e){
                        Log.d("Creation:",e.toString());
                    }
                }
                buttonF.setEnabled(true);
                recyclerView.setVisibility(View.VISIBLE);
                emptyView.setVisibility(View.GONE);
                break;

            case FILE_SELECT_CODE:
                Uri myUri = data.getData();
                String filepath = getPath(UploadSelector.this,myUri);
                Intent to_upload_details = new Intent(UploadSelector.this,Upload_Details.class);
                to_upload_details.putExtra("filepath",filepath);
                to_upload_details.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(to_upload_details);
                break;

        }

    }

    public static String getPath(final Context context, final Uri uri) {                            //copied from stackoverflow

        final boolean isKitKat = Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT;

        // DocumentProvider
        if (isKitKat && DocumentsContract.isDocumentUri(context, uri)) {
            // ExternalStorageProvider
            if (isExternalStorageDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];

                if ("primary".equalsIgnoreCase(type)) {
                    return Environment.getExternalStorageDirectory() + "/" + split[1];
                }

                // TODO handle non-primary volumes
            }
            // DownloadsProvider
            else if (isDownloadsDocument(uri)) {

                final String id = DocumentsContract.getDocumentId(uri);
                final Uri contentUri = ContentUris.withAppendedId(
                        Uri.parse("content://downloads/public_downloads"), Long.valueOf(id));

                return getDataColumn(context, contentUri, null, null);
            }
            // MediaProvider
            else if (isMediaDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];

                Uri contentUri = null;
                if ("image".equals(type)) {
                    contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                } else if ("video".equals(type)) {
                    contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                } else if ("audio".equals(type)) {
                    contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                }

                final String selection = "_id=?";
                final String[] selectionArgs = new String[] {
                        split[1]
                };

                return getDataColumn(context, contentUri, selection, selectionArgs);
            }
        }
        // MediaStore (and general)
        else if ("content".equalsIgnoreCase(uri.getScheme())) {
            return getDataColumn(context, uri, null, null);
        }
        // File
        else if ("file".equalsIgnoreCase(uri.getScheme())) {
            return uri.getPath();
        }

        return null;
    }

    public static String getDataColumn(Context context, Uri uri, String selection,
                                       String[] selectionArgs) {

        Cursor cursor = null;
        final String column = "_data";
        final String[] projection = {
                column
        };

        try {
            cursor = context.getContentResolver().query(uri, projection, selection, selectionArgs,
                    null);
            if (cursor != null && cursor.moveToFirst()) {
                final int column_index = cursor.getColumnIndexOrThrow(column);
                return cursor.getString(column_index);
            }
        } finally {
            if (cursor != null)
                cursor.close();
        }
        return null;
    }


    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is ExternalStorageProvider.
     */
    public static boolean isExternalStorageDocument(Uri uri) {
        return "com.android.externalstorage.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is DownloadsProvider.
     */
    public static boolean isDownloadsDocument(Uri uri) {
        return "com.android.providers.downloads.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is MediaProvider.
     */
    public static boolean isMediaDocument(Uri uri) {
        return "com.android.providers.media.documents".equals(uri.getAuthority());
    }

    public static String getMimeType(Context context, Uri uri) {
        String extension;

        //Check uri format to avoid null
        if (uri.getScheme().equals(ContentResolver.SCHEME_CONTENT)) {
            //If scheme is a content
            final MimeTypeMap mime = MimeTypeMap.getSingleton();
            extension = mime.getExtensionFromMimeType(context.getContentResolver().getType(uri));
        } else {
            //If scheme is a File
            //This will replace white spaces with %20 and also other special characters. This will avoid returning null values on file name with spaces and special characters.
            extension = MimeTypeMap.getFileExtensionFromUrl(Uri.fromFile(new File(uri.getPath())).toString());

        }

        return extension;
    }

    public void createPdf(String dest) throws IOException, DocumentException {                                  //MODIFIED 18-3-2017
        com.itextpdf.text.Image img = com.itextpdf.text.Image.getInstance(urls.get(0));
        Document document = new Document(img);
        PdfWriter.getInstance(document,new FileOutputStream(dest));
        document.open();
        for (String image : urls) {
            img = com.itextpdf.text.Image.getInstance(image);
            document.setPageSize(img);
            document.newPage();

//            document.setMargins(10,10,10,10);
            img.setAbsolutePosition(0, 0);

            document.add(img);
        }
        document.close();
        String root = Environment.getExternalStorageDirectory().getAbsolutePath()+"/";
        File tempFolder = new File(root+"TempCoursec"+File.separator);
        deleteRecursive(tempFolder);
    }

    public void deleteRecursive(File fileOrDirectory) {

        if (fileOrDirectory.isDirectory()) {
            for (File child : fileOrDirectory.listFiles()) {
                deleteRecursive(child);
            }
        }

        fileOrDirectory.delete();
    }

    private void showFileChooser() {
        String[] mimetypes = {"text/plain","application/msword","application/vnd.openxmlformats-officedocument.wordprocessingml.document","application/vnd.ms-powerpoint","application/vnd.openxmlformats-officedocument.presentationml.presentation","application/pdf"};
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.setType("*/*");      //all files
//        intent.setType("application/pdf");  //XML file only
        intent.putExtra(Intent.EXTRA_MIME_TYPES,mimetypes);
        intent.addCategory(Intent.CATEGORY_OPENABLE);

        try {
            startActivityForResult(Intent.createChooser(intent, "Select a File to Upload"), FILE_SELECT_CODE);
        } catch (android.content.ActivityNotFoundException ex) {
            // Potentially direct the user to the Market with a Dialog
            Toast.makeText(UploadSelector.this, "Please install a File Manager.", Toast.LENGTH_SHORT).show();
        }
    }


}
