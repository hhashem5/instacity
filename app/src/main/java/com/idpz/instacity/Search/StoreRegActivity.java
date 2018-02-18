package com.idpz.instacity.Search;

import android.annotation.TargetApi;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
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
import com.idpz.instacity.R;
import com.idpz.instacity.utils.GPSTracker;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
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

public class StoreRegActivity extends AppCompatActivity {

    private static final int SELECT_FILE = 7;
    private static final String TAG = "ShopRegActivity";
    private static final int REQUEST_ACCESS_LOCATION = 0;
    private static final int REQUEST_CAMERA = 6;

    ProgressDialog progressDialog;
    EditText txtShopName,txtShopOwner,txtShopTel,txtShopMobile,txtShopAddress,txtShopKey,txtShopMemo;
    Button btnShopPic,btnShopReg;
    ImageView imgShop;
    TextView txtJobStatus;
    Spinner spnShopTag;
    String REGISTER_URL="";
    String ServerUploadPath ="";
    String shopName,owner,tel,mobile,address,shopKey,memo,shoptag,lat="0",lng="0";
    View focusView = null;
    boolean kansel=false,check=true,hasImage=false;
    Bitmap bitmap=null;
    String ImageName = "image_name";
    String ImagePath = "image_path";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_store_reg);
        txtShopName=(EditText) findViewById(R.id.txtShopName);
        txtShopOwner=(EditText) findViewById(R.id.txtShopOwner);
        txtShopTel=(EditText) findViewById(R.id.txtShopTel);
        txtShopMobile=(EditText) findViewById(R.id.txtShopMobile);
        txtShopAddress=(EditText) findViewById(R.id.txtShopAddress);
        txtShopKey=(EditText) findViewById(R.id.txtShopKey);
        txtShopMemo=(EditText) findViewById(R.id.txtShopMemo);
        btnShopPic=(Button) findViewById(R.id.btnShopPic);
        btnShopReg=(Button) findViewById(R.id.btnShopReg);
        imgShop=(ImageView) findViewById(R.id.imgShop);
        txtJobStatus=(TextView) findViewById(R.id.txtJobStatus);
        spnShopTag=(Spinner) findViewById(R.id.spnShopTag);

        populateGPS();

        REGISTER_URL=getString(R.string.server)+"/i/shopreg.php";
        ServerUploadPath =getString(R.string.server)+"/i/imgplace.php" ;

        btnShopPic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(
                        Intent.ACTION_PICK,
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(intent,SELECT_FILE);
            }
        });

        btnShopReg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(StoreRegActivity.this, "lat="+lat+" lng="+lng, Toast.LENGTH_LONG).show();
                shopName=txtShopName.getText().toString();
                owner=txtShopOwner.getText().toString();
                tel=txtShopTel.getText().toString();
                mobile=txtShopMobile.getText().toString();
                address=txtShopAddress.getText().toString();
                shopKey=txtShopKey.getText().toString();
                memo=txtShopMemo.getText().toString();

                int pos=spnShopTag.getSelectedItemPosition();
                switch (pos){
                    case 0:
                        shoptag="edu";
                        break;
                    case 1:
                        shoptag="shop";
                        break;
                    case 2:
                        shoptag="health";
                        break;
                    case 3:
                        shoptag="region";
                        break;
                    case 4:
                        shoptag="services";
                        break;
                    case 5:
                        shoptag="sport";
                        break;
                    case 6:
                        shoptag="food";
                        break;
                }

                txtShopName.setError(null);
                txtShopOwner.setError(null);
                txtShopTel.setError(null);
                txtShopMobile.setError(null);
                txtShopAddress.setError(null);
                txtShopKey.setError(null);
                txtShopMemo.setError(null);


                if(shopName.length()<2){
                    txtShopName.setError("نام کوتاه است");
                    focusView=txtShopName;
                    kansel=true;
                }

                if(owner.length()<2){
                    txtShopOwner.setError("نام کوتاه است");
                    focusView=txtShopOwner;
                    kansel=true;
                }

                if(mobile.length()!=11){
                    txtShopMobile.setError("شماره موبایل 11رقمی است");
                    focusView=txtShopMobile;
                    kansel=true;
                }
                if(tel.length()<4){
                    txtShopTel.setError("شماره ثابت حداقل 4 رقمی است");
                    focusView=txtShopTel;
                    kansel=true;
                }
                if(address.length()<4){
                    txtShopAddress.setError("آدرس کوتاه است");
                    focusView=txtShopAddress;
                    kansel=true;
                }
                if(shopKey.length()<4){
                    txtShopKey.setError("کلید واژه کوتاه است و برای شناسایی بهتر خدمات توسط مردم است");
                    focusView=txtShopKey;
                    kansel=true;
                }


                if (!kansel) {
                    if (hasImage){
                        ImageUploadToServerFunction();
                    }else {
                        regShopInfo();
                    }

                }else {
                    focusView.requestFocus();
                }




            }
        });

    }



    public void regShopInfo() {
        RequestQueue queue = Volley.newRequestQueue(this);
        String url = REGISTER_URL;
        StringRequest postRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>()
                {
                    @Override
                    public void onResponse(String response) {

                        txtJobStatus.setText(response.toString());
                    }
                },
                new Response.ErrorListener()
                {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // TODO Auto-generated method stub
                        Log.d("ERROR","error => "+error.toString());
                        txtJobStatus.setText(error.toString()+"مشکلی در ارسال داده پیش آمده دوباره تلاش کنید");
                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String>params = new HashMap<String,String>();

                params.put("name", shopName);
                params.put("mob", mobile);
                params.put("owner", owner);
                params.put("tel",tel);
                params.put("adr", address);
                params.put("key", shopKey);
                params.put("tag", shoptag);
                params.put("lat", lat);
                params.put("lng", lng);
                return params;
            }
        };
        queue.add(postRequest);


    }

    public void onBackPressed() {
        Log.d("CDA", "onBackPressed Called");
        finish();
        return;
    }



    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_CANCELED) {
            return;
        }
        if (requestCode==SELECT_FILE) {
            if (data != null) {
                Uri contentURI = data.getData();
                try {
                    bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), contentURI);
//                    Log.e("The image", imageToString(bitmap));
                    imgShop.setImageBitmap(bitmap);
                    hasImage=true;

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        if(requestCode == REQUEST_CAMERA){
            Log.d(TAG, "onActivityResult: done taking a photo.");

            bitmap = (Bitmap) data.getExtras().get("data");
            imgShop.setImageBitmap(bitmap);
            ImageUploadToServerFunction();
        }
    }



    public void ImageUploadToServerFunction(){


        ByteArrayOutputStream byteArrayOutputStreamObject ;

        byteArrayOutputStreamObject = new ByteArrayOutputStream();

        bitmap.compress(Bitmap.CompressFormat.JPEG, 20, byteArrayOutputStreamObject);

        byte[] byteArrayVar = byteArrayOutputStreamObject.toByteArray();

        final String ConvertImage = Base64.encodeToString(byteArrayVar, Base64.DEFAULT);

        class AsyncTaskUploadClass extends AsyncTask<Void,Void,String> {

            @Override
            protected void onPreExecute() {

                super.onPreExecute();

                progressDialog = ProgressDialog.show(StoreRegActivity.this,"درحال ارسال عکس","کمی صبر کنید",false,false);
            }

            @Override
            protected void onPostExecute(String string1) {

                super.onPostExecute(string1);

                // Dismiss the progress dialog after done uploading.
                progressDialog.dismiss();
                SharedPreferences.Editor SP2 = PreferenceManager.getDefaultSharedPreferences(getBaseContext()).edit();
                SP2.putString("pic", string1);
                SP2.apply();
                // Printing uploading success message coming from server on android app.
                Intent intent =new Intent(StoreRegActivity.this, SearchActivity.class);
                startActivity(intent);
                finish();
                // Setting image as transparent after done uploading.
//                imageView.setImageResource(android.R.color.transparent);


            }

            @Override
            protected String doInBackground(Void... params) {

                StoreRegActivity.ImageProcessClass imageProcessClass = new StoreRegActivity.ImageProcessClass();

                HashMap<String,String> HashMapParams = new HashMap<String,String>();

                HashMapParams.put(ImageName, mobile);

                HashMapParams.put(ImagePath, ConvertImage);
                HashMapParams.put("name", shopName);
                HashMapParams.put("mob", mobile);
                HashMapParams.put("owner", owner);
                HashMapParams.put("tel",tel);
                HashMapParams.put("adr", address);
                HashMapParams.put("key", shopKey);
                HashMapParams.put("tag", shoptag);
                HashMapParams.put("lat", lat);
                HashMapParams.put("lng", lng);


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

    private void populateGPS() {
        if (!mayRequestLocation()) {
            return;
        }
        GPSTracker gps =new GPSTracker(this);
        lat=String.valueOf(gps.getLatitude());
        lng=String.valueOf(gps.getLongitude());
        if (!lat.equals("0.0")) {
            SharedPreferences.Editor SP = PreferenceManager.getDefaultSharedPreferences(getBaseContext()).edit();
            SP.putString("lat", String.valueOf(gps.getLatitude()));
            SP.putString("lng", String.valueOf(gps.getLongitude()));
            SP.commit();
            btnShopReg.setTextColor(Color.BLACK);
            btnShopReg.setEnabled(true);
            txtJobStatus.setText("");
            txtJobStatus.setTextColor(Color.BLACK);
        }else{
            btnShopReg.setTextColor(Color.RED);
            btnShopReg.setEnabled(false);
            txtJobStatus.setText("جی چی اس خاموش است امکان ثبت مکان نیست");
            txtJobStatus.setTextColor(Color.RED);
            final CharSequence[] items = {"بله", "خیر"};
            android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(StoreRegActivity.this);
            builder.setTitle("آیا جی پی اس را روشن می کنید");
            builder.setItems(items, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int item) {

                    if (items[item].equals("بله")) {
                        Intent i = new
                                Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                        startActivity(i);
                    } else if (items[item].equals("خیر")) {
                        dialog.dismiss();
                    }
                }
            });
            builder.show();

        }


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
            Snackbar.make(btnShopReg, "لطفا دسترسی به جی پی اس را فعال کنید.", Snackbar.LENGTH_INDEFINITE)
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

    @Override
    protected void onPostResume() {
        super.onPostResume();
        populateGPS();
    }
}
