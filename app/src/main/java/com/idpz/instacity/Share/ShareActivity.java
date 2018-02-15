package com.idpz.instacity.Share;

import android.annotation.TargetApi;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.idpz.instacity.Home.HomeActivity;
import com.idpz.instacity.R;
import com.idpz.instacity.utils.BottomNavigationViewHelper;
import com.idpz.instacity.utils.GPSTracker;
import com.idpz.instacity.utils.Permissions;
import com.ittianyu.bottomnavigationviewex.BottomNavigationViewEx;

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

import static android.Manifest.permission.ACCESS_COARSE_LOCATION;
import static android.Manifest.permission.ACCESS_FINE_LOCATION;

public class ShareActivity extends AppCompatActivity {
    private static final String TAG = "ShareActivity";

    //constants
    private static final int ACTIVITY_NUM = 2;
    private static final int VERIFY_PERMISSIONS_REQUEST = 1;
    private static final int REQUEST_ACCESS_LOCATION = 0;
    private static final int  CAMERA_REQUEST_CODE = 5;
    private static final int REQUEST_CAMERA = 6;
    private static final int SELECT_FILE = 7;
    private int PROFILE_PIC_COUNT=0;
//    private ViewPager mViewPager;



    String ImageName = "image_name" ,mobile,pas;

    String ImagePath = "image_path",mstatus="0",gov="6",myname="",myphone="",mytext="" ;

    String ServerUploadPath ="", REG_USER_LAT ="" ;
    //widgets
    private EditText mCaption;
    boolean check = true;
    ProgressDialog progressDialog;
    //vars
    private String mAppend = "file:/";
    private int imageCount = 0,cmprs=70;
    private String imgUrl,lat="",lng="",oldLat="0",oldLng="0";
    private Bitmap bitmap;
    private Intent intent;
    Button share,btnShareCamera;
    RadioButton radioTashakor,radioPishnahad,radioEnteghad,radioShekayat;
    Spinner spnMoavenat;
    TextView tvMessage;
    ImageView image;
    private Context mContext = ShareActivity.this;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_share);
        Log.d(TAG, "onCreate: started.");
        REG_USER_LAT=getString(R.string.server)+"/i/latprofile.php";
        if (checkPermissionsArray(Permissions.PERMISSIONS)) {

        } else {
            verifyPermissions(Permissions.PERMISSIONS);
        }

        ServerUploadPath =getString(R.string.server)+"/i/img1.php" ;

        mCaption = (EditText) findViewById(R.id.textBody) ;

        radioTashakor=(RadioButton)findViewById(R.id.radioButton);
        radioPishnahad=(RadioButton)findViewById(R.id.radioButton2);
        radioEnteghad=(RadioButton)findViewById(R.id.radioButton3);
        radioShekayat=(RadioButton)findViewById(R.id.radioButton4);
        spnMoavenat=(Spinner)findViewById(R.id.spnMoavenat);
        image=(ImageView) findViewById(R.id.imgSharePic);
        tvMessage=(TextView) findViewById(R.id.shareTextMessage);

        SharedPreferences SP = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
        mobile=SP.getString("mobile", "0");
        pas=SP.getString("pass", "0");

        btnShareCamera=(Button)findViewById(R.id.btnShareCamera);

        btnShareCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final CharSequence[] items = {"دوربین", "گالری", "انصراف"};
                android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(ShareActivity.this);
                builder.setTitle("Add Photo!");
                builder.setItems(items, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int item) {

                        if (items[item].equals("دوربین")) {
                            PROFILE_PIC_COUNT = 1;
                            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                            startActivityForResult(intent, REQUEST_CAMERA);
                        } else if (items[item].equals("گالری")) {
                            PROFILE_PIC_COUNT = 1;
                            Intent intent = new Intent(
                                    Intent.ACTION_PICK,
                                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                            startActivityForResult(intent,SELECT_FILE);
                        } else if (items[item].equals("انصراف")) {
                            PROFILE_PIC_COUNT = 0;
                            dialog.dismiss();
                        }
                    }
                });
                builder.show();

            }
        });



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

        share=(Button)findViewById(R.id.btnShareSocial);

//        SharedPreferences SP = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
        myname=SP.getString("myname", "0");
        myphone=SP.getString("mobile", "0");
        oldLng=SP.getString("lng", "0");
        oldLat=SP.getString("lat", "0");

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
        share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: navigating to the final share screen.");
                //upload the image to


                mytext = mCaption.getText().toString();
                if (mytext.length()>4) {
                    Toast.makeText(ShareActivity.this, "درحال ارسال اطلاعات", Toast.LENGTH_SHORT).show();
                    ImageUploadToServerFunction();
                }

            }
        });

        populateGPS();

        setupBottomNavigationView();
    }



    /**
     * verifiy all the permissions passed to the array
     * @param permissions
     */
    public void verifyPermissions(String[] permissions){
        Log.d(TAG, "verifyPermissions: verifying permissions.");

        ActivityCompat.requestPermissions(
                ShareActivity.this,
                permissions,
                VERIFY_PERMISSIONS_REQUEST
        );
    }

    /**
     * Check an array of permissions
     * @param permissions
     * @return
     */
    public boolean checkPermissionsArray(String[] permissions){
        Log.d(TAG, "checkPermissionsArray: checking permissions array.");

        for(int i = 0; i< permissions.length; i++){
            String check = permissions[i];
            if(!checkPermissions(check)){
                return false;
            }
        }
        return true;
    }

    /**
     * Check a single permission is it has been verified
     * @param permission
     * @return
     */
    public boolean checkPermissions(String permission){
        Log.d(TAG, "checkPermissions: checking permission: " + permission);

        int permissionRequest = ActivityCompat.checkSelfPermission(ShareActivity.this, permission);

        if(permissionRequest != PackageManager.PERMISSION_GRANTED){
            Log.d(TAG, "checkPermissions: \n Permission was not granted for: " + permission);
            return false;
        }
        else{
            Log.d(TAG, "checkPermissions: \n Permission was granted for: " + permission);
            return true;
        }
    }

    /**
     * BottomNavigationView setup
     */
    private void setupBottomNavigationView(){
        Log.d(TAG, "setupBottomNavigationView: setting up BottomNavigationView");
        BottomNavigationViewEx bottomNavigationViewEx = (BottomNavigationViewEx) findViewById(R.id.bottomNavViewBar);
        BottomNavigationViewHelper.setupBottomNavigationView(bottomNavigationViewEx);
        BottomNavigationViewHelper.enableNavigation(mContext, bottomNavigationViewEx);
        Menu menu = bottomNavigationViewEx.getMenu();
        MenuItem menuItem = menu.getItem(ACTIVITY_NUM);
        menuItem.setChecked(true);
    }



    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == this.RESULT_CANCELED) {
            return;
        }

        if(requestCode == REQUEST_CAMERA){
            Log.d(TAG, "onActivityResult: done taking a photo.");

            bitmap = (Bitmap) data.getExtras().get("data");
            image.setImageBitmap(bitmap);

        }else if (requestCode==SELECT_FILE){
            Log.d(TAG, "onActivityResult: done taking a photo.");
            if (data != null) {
                Uri contentURI = data.getData();
                try {
                    bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), contentURI);
//                    Log.e("The image", imageToString(bitmap));
                    image.setImageBitmap(bitmap);


                } catch (IOException e) {
                    e.printStackTrace();
                }
            }


        }
    }


    /**
     * gets the image url from the incoming intent and displays the chosen image
     */
    private void setImage() throws IOException {
        intent = getIntent();
//        ImageView image = (ImageView) findViewById(R.id.imgSharePic);

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

        if (PROFILE_PIC_COUNT==0){
            regSocial();
            return;
        }

        ByteArrayOutputStream byteArrayOutputStreamObject ;

        byteArrayOutputStreamObject = new ByteArrayOutputStream();

        bitmap.compress(Bitmap.CompressFormat.JPEG, cmprs, byteArrayOutputStreamObject);

        byte[] byteArrayVar = byteArrayOutputStreamObject.toByteArray();

        final String ConvertImage = Base64.encodeToString(byteArrayVar, Base64.DEFAULT);

        class AsyncTaskUploadClass extends AsyncTask<Void,Void,String> {

            @Override
            protected void onPreExecute() {

                super.onPreExecute();

                progressDialog = ProgressDialog.show(ShareActivity.this,"Image is Uploading","Please Wait",false,false);
            }

            @Override
            protected void onPostExecute(String string1) {

                super.onPostExecute(string1);

                // Dismiss the progress dialog after done uploading.
                progressDialog.dismiss();

                // Printing uploading success message coming from server on android app.
                Intent intent =new Intent(ShareActivity.this, HomeActivity.class);
                startActivity(intent);
                finish();
                // Setting image as transparent after done uploading.
//                imageView.setImageResource(android.R.color.transparent);


            }

            @Override
            protected String doInBackground(Void... params) {

                ShareActivity.ImageProcessClass imageProcessClass = new ShareActivity.ImageProcessClass();

                HashMap<String,String> HashMapParams = new HashMap<String,String>();

                HashMapParams.put(ImageName, myphone);

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

    private void populateGPS() {
        if (!mayRequestLocation()) {
            return;
        }
        GPSTracker gps =new GPSTracker(this);

        lat=String.valueOf(gps.getLatitude());
        lng=String.valueOf(gps.getLongitude());
        if (oldLat.equals("0")){
            SharedPreferences.Editor SP = PreferenceManager.getDefaultSharedPreferences(getBaseContext()).edit();
            SP.putString("lat", String.valueOf(lat));
            SP.putString("lng",String.valueOf(lat));
            SP.commit();
            regLat();
        }

//        Toast.makeText(MainActivity.this, "location ="+myLocation.getLatitude()+","+myLocation.getLongitude(), Toast.LENGTH_SHORT).show();

    }



    private boolean mayRequestLocation() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            return true;
        }
        if (checkSelfPermission(ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED ||
                checkSelfPermission(ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED   ) {
            return true;
        }
        if (shouldShowRequestPermissionRationale(ACCESS_FINE_LOCATION)) {
            Snackbar.make(share, "لطفا دسترسی به جی پی اس را فعال کنید.", Snackbar.LENGTH_INDEFINITE)
                    .setAction(android.R.string.ok, new View.OnClickListener() {
                        @Override
                        @TargetApi(Build.VERSION_CODES.M)
                        public void onClick(View v) {
                            requestPermissions(new String[]{ACCESS_FINE_LOCATION},REQUEST_ACCESS_LOCATION);
                        }
                    });
        } else {
            requestPermissions(new String[]{ACCESS_FINE_LOCATION}, REQUEST_ACCESS_LOCATION);
        }
        return false;
    }

    /**
     * Callback received when a permissions request has been completed.
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        if (requestCode == REQUEST_ACCESS_LOCATION) {
            if (grantResults.length == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                populateGPS();
            }
        }

    }

    public void regSocial() {
//        Toast.makeText(MainActivity.this, " reqUser", Toast.LENGTH_SHORT).show();
        RequestQueue queue = Volley.newRequestQueue(this);
        String url = ServerUploadPath;
        StringRequest postRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>()
                {
                    @Override
                    public void onResponse(String response) {
                        tvMessage.setText("پیام با موفقیت ارسال شد");
                    }
                },
                new Response.ErrorListener()
                {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // TODO Auto-generated method stub

                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String>params = new HashMap<String,String>();

                params.put("status", mstatus);
                params.put("gov", gov);
                params.put("lat", lat);
                params.put("lng", lng);
                params.put("name", myname);
                params.put("phone", myphone);
                params.put("text", mytext);
                return params;
            }
        };
        queue.add(postRequest);

    }

    public void regLat() {
//        Toast.makeText(MainActivity.this, " reqUser", Toast.LENGTH_SHORT).show();
        RequestQueue queue = Volley.newRequestQueue(this);
        String url = REG_USER_LAT;
        StringRequest postRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>()
                {
                    @Override
                    public void onResponse(String response) {
//                        userRegFlag=true;
                    }
                },
                new Response.ErrorListener()
                {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // TODO Auto-generated method stub

                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String>params = new HashMap<String,String>();
                params.put("lat",lat);
                params.put("lng",lng);
                params.put("mob",mobile);
                params.put("pas",pas);
                return params;
            }
        };
        queue.add(postRequest);

    }
}
