package com.idpz.instacity.Share;

import android.Manifest;
import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.idpz.instacity.R;
import com.idpz.instacity.utils.GridImageAdapter;


import java.io.File;
import java.util.ArrayList;
import java.util.SortedSet;
import java.util.TreeSet;

public class GalleryActivity extends AppCompatActivity {


    //constants
    private static final int NUM_GRID_COLUMNS = 3;
    private static final int REQUEST_PERMISSIONS = 100;
    private static final String TAG = "GalleryFragment";
    //widgets
    private GridView gridView;
    private ImageView galleryImage;
    private ProgressBar mProgressBar;
    boolean boolean_folder;

    //vars
    private ArrayList<String> directories;
    private String mAppend = "file:/";
    private String mSelectedImage,mstatus="1";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gallery);


        galleryImage =(ImageView) findViewById(R.id.galleryImageView);
        gridView =(GridView) findViewById(R.id.gridView);

        mProgressBar =(ProgressBar) findViewById(R.id.progressBar);
        mProgressBar.setVisibility(View.GONE);
        directories = new ArrayList<>();
        Log.d(TAG, "onCreateView: started.");

        final Dialog dialog = new Dialog(GalleryActivity.this);
        dialog.setContentView(R.layout.opinion_popup);
        dialog.setTitle("نوع پیام");
        dialog.setCancelable(true);
        // there are a lot of settings, for dialog, check them all out!
        // set up radiobutton
        final RadioButton rd0 = dialog.findViewById(R.id.rd_0);
        final RadioButton rd1 = dialog.findViewById(R.id.rd_1);
        final RadioButton rd2 = dialog.findViewById(R.id.rd_2);
        final RadioButton rd3 = dialog.findViewById(R.id.rd_3);
        Button btnDialog= dialog.findViewById(R.id.btnOpinion);
        rd0.setSelected(true);
        // now that the dialog is set up, it's time to show it
        dialog.show();

        rd0.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (rd0.isChecked())
                mstatus="1";
            }
        });
        rd1.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (rd1.isChecked())
                mstatus="2";
            }
        });
        rd2.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (rd2.isChecked())
                mstatus="3";
            }
        });
        rd3.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (rd3.isChecked())mstatus="4";
            }
        });
        ImageView shareClose =(ImageView) findViewById(R.id.ivCloseShare);
        shareClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: closing the gallery fragment.");
                finish();
            }
        });
        btnDialog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        TextView nextScreen =(TextView) findViewById(R.id.tvNext);
        nextScreen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: navigating to the final share screen.");
                Toast.makeText(GalleryActivity.this, "لطفا کمی صبر کنید", Toast.LENGTH_SHORT).show();

                    Intent intent = new Intent(GalleryActivity.this, ShareActivity.class);
                    intent.putExtra(getString(R.string.selected_image), mSelectedImage);
                    intent.putExtra("status", mstatus);
                    startActivity(intent);
                finish();
            }
        });

        TextView tvNopic =(TextView) findViewById(R.id.tvNoPic);
        tvNopic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: navigating to the final share screen.");

                Intent intent = new Intent(GalleryActivity.this, ShareActivity.class);
                intent.putExtra("status", mstatus);
                startActivity(intent);

            }
        });
        if ((ContextCompat.checkSelfPermission(getApplicationContext(),
                Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) && (ContextCompat.checkSelfPermission(getApplicationContext(),
                Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)) {
            if ((ActivityCompat.shouldShowRequestPermissionRationale(GalleryActivity.this,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE)) && (ActivityCompat.shouldShowRequestPermissionRationale(GalleryActivity.this,
                    Manifest.permission.READ_EXTERNAL_STORAGE))) {

            } else {
                ActivityCompat.requestPermissions(GalleryActivity.this,
                        new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE},
                        REQUEST_PERMISSIONS);
            }
        }else {
            Log.e("Else","Else");
            setupGridView();
        }

        SharedPreferences.Editor SP = PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).edit();
        SP.putString("tabpos", "0");
        SP.apply();

    }



    private void setupGridView(){

        // edit


        final ArrayList<String> imgURLs = getFilePaths();



        //end edit


        //set the grid column width
        int gridWidth = getResources().getDisplayMetrics().widthPixels;
        int imageWidth = gridWidth/NUM_GRID_COLUMNS;
        gridView.setColumnWidth(imageWidth);

        //use the grid adapter to adapter the images to gridview
        GridImageAdapter adapter = new GridImageAdapter(GalleryActivity.this, R.layout.layout_grid_imageview, mAppend, imgURLs);
        gridView.setAdapter(adapter);

        //set the first image to be displayed when the activity fragment view is inflated
        try{
            setImage(imgURLs.get(0), galleryImage, mAppend);
            mSelectedImage = imgURLs.get(0);
        }catch (ArrayIndexOutOfBoundsException e){
            Log.e(TAG, "setupGridView: ArrayIndexOutOfBoundsException: " +e.getMessage() );
        }

        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Log.d(TAG, "onItemClick: selected an image: " + imgURLs.get(position));
//                String[] projection = {MediaStore.Images.Media.DATA};
//                Cursor cursor;
//                int columnIndex=0;
//                cursor = getContentResolver().query( MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
//                        projection, // Which columns to return
//                        null,       // Return all rows
//                        null,
//                        null);
//                columnIndex = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
//                cursor.moveToPosition(position);
//                // Get image filename
//                String imagePath = cursor.getString(columnIndex);
//                if (cursor != null) {
//                    cursor.close();
//                }
                mSelectedImage = imgURLs.get(position);
                setImage(mSelectedImage, galleryImage, mAppend);

//                galleryImage.setImageURI(Uri.parse(mSelectedImage));

            }
        });

    }


    private void setImage(String imgURL, ImageView image, String append){
        Log.d(TAG, "setImage: setting image");
        Glide.with(GalleryActivity.this).load(imgURL)
                .thumbnail(0.5f)
                .crossFade()
                .placeholder(R.drawable.nopic)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(image);

    }

    public ArrayList<String> getFilePaths()
    {


        Uri u = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
        String[] projection = {MediaStore.Images.ImageColumns.DATA};
        Cursor c = null;
        SortedSet<String> dirList = new TreeSet<String>();
        ArrayList<String> resultIAV = new ArrayList<String>();
        String orderBy = MediaStore.Images.ImageColumns.DATE_TAKEN + " ASC";
        String[] directories = null;
        if (u != null)
        {
            c = getContentResolver().query(u, projection, null, null, orderBy);
        }

        if ((c != null) && (c.moveToFirst()))
        {
            do
            {
                String tempDir = c.getString(0);
                tempDir = tempDir.substring(0, tempDir.lastIndexOf("/"));
                try{
                    dirList.add(tempDir);
                }
                catch(Exception e)
                {

                }
            }
            while (c.moveToNext());
            directories = new String[dirList.size()];
            dirList.toArray(directories);

        }
        if (c != null) {
            c.close();
        }
        for(int i=0;i<dirList.size();i++)
        {
            File imageDir = new File(directories[i]);
            File[] imageList = imageDir.listFiles();
            if(imageList == null)
                continue;
            for (File imagePath : imageList) {
                try {

                    if(imagePath.isDirectory())
                    {
                        imageList = imagePath.listFiles();

                    }
                    if ( imagePath.getName().contains(".jpg")|| imagePath.getName().contains(".JPG")
                            || imagePath.getName().contains(".jpeg")|| imagePath.getName().contains(".JPEG")
                            || imagePath.getName().contains(".png") || imagePath.getName().contains(".PNG")
                            || imagePath.getName().contains(".bmp") || imagePath.getName().contains(".BMP")
                            )
                    {



                        String path= imagePath.getAbsolutePath();
                        resultIAV.add(path);

                    }
                }
                //  }
                catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

        return resultIAV;


    }
    public static ArrayList<String> al_images = new ArrayList<>();
    public ArrayList<String> fn_imagespath() {
        al_images.clear();
        Log.d(TAG, "fn_imagespath: started");
        int int_position = 0;
        Uri uri;
        Cursor cursor;
        int column_index_data, column_index_folder_name;

        String absolutePathOfImage = null;
        uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;

        String[] projection = {MediaStore.MediaColumns.DATA, MediaStore.Images.Media.BUCKET_DISPLAY_NAME};

        final String orderBy = MediaStore.Images.Media.DATE_TAKEN;
        cursor = getApplicationContext().getContentResolver().query(uri, projection, null, null, orderBy + " DESC");

        assert cursor != null;
        column_index_data = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DATA);
        column_index_folder_name = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.BUCKET_DISPLAY_NAME);
        while (cursor.moveToNext()) {
            absolutePathOfImage = cursor.getString(column_index_data);
            Log.e("Column", absolutePathOfImage);
            Log.e("Folder", cursor.getString(column_index_folder_name));

            for (int i = 0; i < al_images.size(); i++) {
                if (al_images.get(i).equals(cursor.getString(column_index_folder_name))) {
                    boolean_folder = true;
                    int_position = i;
                    break;
                } else {
                    boolean_folder = false;
                    al_images.add(absolutePathOfImage);
                    Log.d(TAG, "fn_imagespath: "+absolutePathOfImage);
                }
            }








        }

        cursor.close();

        return al_images;
    }


}

