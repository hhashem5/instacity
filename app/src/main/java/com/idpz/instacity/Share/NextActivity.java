package com.idpz.instacity.Share;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.idpz.instacity.Home.HomeActivity;
import com.idpz.instacity.R;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

import javax.net.ssl.HttpsURLConnection;

/**
 * Created by User on 7/24/2017.
 */

public class NextActivity extends AppCompatActivity {

    private static final String TAG = "NextActivity";


        String ImageName = "image_name" ;

        String ImagePath = "image_path",mstatus="0",gov="6",myname="",myphone="",mytext="" ;

        String ServerUploadPath ="" ;
        //widgets
        private EditText mCaption;
        boolean check = true;
        ProgressDialog progressDialog;
        //vars
        private String mAppend = "file:/";
        private int imageCount = 0,cmprs=50;
        private String imgUrl,lat="",lng="";
        private Bitmap bitmap;
        private Intent intent;
        RadioButton radioTashakor,radioPishnahad,radioEnteghad,radioShekayat;
        Spinner spnMoavenat;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_next);

        ServerUploadPath =getString(R.string.server)+"/i/img1.php" ;

        mCaption = (EditText) findViewById(R.id.caption) ;

        radioTashakor=(RadioButton)findViewById(R.id.radioButton);
        radioPishnahad=(RadioButton)findViewById(R.id.radioButton2);
        radioEnteghad=(RadioButton)findViewById(R.id.radioButton3);
        radioShekayat=(RadioButton)findViewById(R.id.radioButton4);
        spnMoavenat=(Spinner)findViewById(R.id.spnMoavenat);

        mstatus="0";
        radioTashakor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mstatus="1";
            }
        });
        radioPishnahad.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mstatus="2";
            }
        });
        radioEnteghad.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mstatus="3";
            }
        });
        radioShekayat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mstatus="4";
            }
        });

        spnMoavenat.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                switch (position){
                    case 1:
                        gov="6";
                        break;
                    case 2:
                        gov="7";
                        break;
                    case 3:
                        gov="5";
                        break;
                    case 4:
                        gov="2";
                        break;
                    case 5:
                        gov="3";
                        break;
                    case 6:
                        gov="1";
                        break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        ImageView backArrow = (ImageView) findViewById(R.id.ivBackArrow);
        backArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: closing the activity");
                finish();
            }
        });


        TextView share = (TextView) findViewById(R.id.tvShare);
        share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: navigating to the final share screen.");
                //upload the image to 
                Toast.makeText(NextActivity.this, "Attempting to upload new photo", Toast.LENGTH_SHORT).show();
                 mytext = mCaption.getText().toString();
                SharedPreferences SP = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
                myname=SP.getString("myname", "0");
                myphone=SP.getString("mobile", "0");
                ImageUploadToServerFunction();


            }
        });

        try {
            setImage();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void someMethod(){
        /*
            Step 1)
            Create a data model for Photos

            Step 2)
            Add properties to the Photo Objects (caption, date, imageUrl, photo_id, tags, user_id)

            Step 3)
            Count the number of photos that the user already has.

            Step 4)
            a) Upload the photo to  Storage
            b) insert into 'photos' node
            c) insert into 'user_photos' node

         */

    }


    /**
     * gets the image url from the incoming intent and displays the chosen image
     */
    private void setImage() throws IOException {
        intent = getIntent();
        ImageView image = (ImageView) findViewById(R.id.imageShare);

        if(intent.hasExtra(getString(R.string.selected_image))){
            imgUrl = intent.getStringExtra(getString(R.string.selected_image));
            lat=intent.getStringExtra("lat");
            lng=intent.getStringExtra("lng");
            Log.d(TAG, "setImage: got new image url: " + imgUrl+lat);
//            UniversalImageLoader.setImage(imgUrl, image, null, mAppend);
            image.setImageBitmap(bitmap);
            cmprs=70;
            FileInputStream in = null;
            try {
                in = new FileInputStream(imgUrl);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
            BufferedInputStream buf = new BufferedInputStream(in);
            byte[] bMapArray= new byte[buf.available()];
            buf.read(bMapArray);
            bitmap = BitmapFactory.decodeByteArray(bMapArray, 0, bMapArray.length);
        }
        else if(intent.hasExtra(getString(R.string.selected_bitmap))){
            bitmap = (Bitmap) intent.getParcelableExtra(getString(R.string.selected_bitmap));
            lat=intent.getStringExtra("lat");
            lng=intent.getStringExtra("lng");
            Log.d(TAG, "setImage: got new bitmap"+lat+lng);
            image.setImageBitmap(bitmap);
            cmprs=70;
        }else {
            finish();
        }
    }

     /*
     ------------------------------------ send post to server ---------------------------------------------
     */



    public void ImageUploadToServerFunction(){

        ByteArrayOutputStream byteArrayOutputStreamObject ;

        byteArrayOutputStreamObject = new ByteArrayOutputStream();

        bitmap.compress(Bitmap.CompressFormat.JPEG, cmprs, byteArrayOutputStreamObject);

        byte[] byteArrayVar = byteArrayOutputStreamObject.toByteArray();

        final String ConvertImage = Base64.encodeToString(byteArrayVar, Base64.DEFAULT);

        class AsyncTaskUploadClass extends AsyncTask<Void,Void,String> {

            @Override
            protected void onPreExecute() {

                super.onPreExecute();

                progressDialog = ProgressDialog.show(NextActivity.this,"Image is Uploading","Please Wait",false,false);
            }

            @Override
            protected void onPostExecute(String string1) {

                super.onPostExecute(string1);

                // Dismiss the progress dialog after done uploading.
                progressDialog.dismiss();

                // Printing uploading success message coming from server on android app.
                Intent intent =new Intent(NextActivity.this, HomeActivity.class);
                startActivity(intent);

                // Setting image as transparent after done uploading.
//                imageView.setImageResource(android.R.color.transparent);


            }

            @Override
            protected String doInBackground(Void... params) {

                ImageProcessClass imageProcessClass = new ImageProcessClass();

                HashMap<String,String> HashMapParams = new HashMap<String,String>();

                HashMapParams.put(ImageName, "camera_take");

                HashMapParams.put(ImagePath, ConvertImage);
                HashMapParams.put("status", mstatus);
                HashMapParams.put("gov", gov);
                HashMapParams.put("lat", lat);
                HashMapParams.put("lng", lng);
                HashMapParams.put("name", myname);
                HashMapParams.put("phone", myphone);
                HashMapParams.put("text", mytext);


                String FinalData = imageProcessClass.ImageHttpRequest(ServerUploadPath, HashMapParams);

                return FinalData;
            }
        }
        AsyncTaskUploadClass AsyncTaskUploadClassOBJ = new AsyncTaskUploadClass();

        AsyncTaskUploadClassOBJ.execute();
    }

    public class ImageProcessClass{

        public String ImageHttpRequest(String requestURL,HashMap<String, String> PData) {

            StringBuilder stringBuilder = new StringBuilder();

            try {

                URL url;
                HttpURLConnection httpURLConnectionObject ;
                OutputStream OutPutStream;
                BufferedWriter bufferedWriterObject ;
                BufferedReader bufferedReaderObject ;
                int RC ;

                url = new URL(requestURL);

                httpURLConnectionObject = (HttpURLConnection) url.openConnection();

                httpURLConnectionObject.setReadTimeout(19000);

                httpURLConnectionObject.setConnectTimeout(19000);

                httpURLConnectionObject.setRequestMethod("POST");

                httpURLConnectionObject.setDoInput(true);

                httpURLConnectionObject.setDoOutput(true);

                OutPutStream = httpURLConnectionObject.getOutputStream();

                bufferedWriterObject = new BufferedWriter(

                        new OutputStreamWriter(OutPutStream, "UTF-8"));

                bufferedWriterObject.write(bufferedWriterDataFN(PData));

                bufferedWriterObject.flush();

                bufferedWriterObject.close();

                OutPutStream.close();

                RC = httpURLConnectionObject.getResponseCode();

                if (RC == HttpsURLConnection.HTTP_OK) {

                    bufferedReaderObject = new BufferedReader(new InputStreamReader(httpURLConnectionObject.getInputStream()));

                    stringBuilder = new StringBuilder();

                    String RC2;

                    while ((RC2 = bufferedReaderObject.readLine()) != null){

                        stringBuilder.append(RC2);
                    }
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
            return stringBuilder.toString();
        }

        private String bufferedWriterDataFN(HashMap<String, String> HashMapParams) throws UnsupportedEncodingException {

            StringBuilder stringBuilderObject;

            stringBuilderObject = new StringBuilder();

            for (Map.Entry<String, String> KEY : HashMapParams.entrySet()) {

                if (check)

                    check = false;
                else
                    stringBuilderObject.append("&");

                stringBuilderObject.append(URLEncoder.encode(KEY.getKey(), "UTF-8"));

                stringBuilderObject.append("=");

                stringBuilderObject.append(URLEncoder.encode(KEY.getValue(), "UTF-8"));
            }

            return stringBuilderObject.toString();
        }

    }

    public String getRealPathFromURI(Context context, Uri contentUri) {
        Cursor cursor = null;
        try {
            String[] proj = { MediaStore.Images.Media.DATA };
            cursor = context.getContentResolver().query(contentUri,  proj, null, null, null);
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            cursor.moveToFirst();
            return cursor.getString(column_index);
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }


}
