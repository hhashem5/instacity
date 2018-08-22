package com.idpz.instacity.Share;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Looper;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.idpz.instacity.R;
import com.idpz.instacity.utils.BottomNavigationViewHelper;
import com.idpz.instacity.utils.GPSTracker;
import com.ittianyu.bottomnavigationviewex.BottomNavigationViewEx;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import static android.Manifest.permission.ACCESS_COARSE_LOCATION;
import static android.Manifest.permission.ACCESS_FINE_LOCATION;

public class ShareActivity extends AppCompatActivity {
    private static final String TAG = "ShareActivity";

    //constants
    private static final int ACTIVITY_NUM = 2;
    private static final int VERIFY_PERMISSIONS_REQUEST = 1;
    private static final int REQUEST_ACCESS_LOCATION = 0;
    private static final int REQUEST_CAMERA = 6;
    private static final int SELECT_FILE = 7;
    private int PROFILE_PIC_COUNT=0;
//    private ViewPager mViewPager;



    String ImageName = "image_name" ,mobile,pas;

    String ImagePath = "image_path",mstatus="0",gov="1",myname="",myphone="",mytext="" ;

    String ServerUploadPath ="", REG_USER_LAT ="",state="" ;
    //widgets
    private EditText mCaption;
    boolean check = true,nopicFlag=true;
    ProgressDialog progressDialog;
    //vars
    private String mAppend = "file:/";
    private int imageCount = 0,cmprs=70;
    private String imgUrl,lat="",lng="",oldLat="0",oldLng="0",govtxt="";
    private Bitmap bitmap;
    private Intent intent;
    Button share,btnShareCamera;
//    Spinner spnMoavenat;
    TextView tvMessage,tvShare,tvStatusSend;
    ImageView image,ivBackArrow;
    private Context mContext = ShareActivity.this;



    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_share);
        Log.d(TAG, "onCreate: started.");

        SharedPreferences SP = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
        mobile=SP.getString("mobile", "0");
        pas=SP.getString("pass", "0");
        state=SP.getString("state", "050001");

        REG_USER_LAT=getString(R.string.server)+"/j/latprofile.php";


        ServerUploadPath =getString(R.string.server)+"/j/img1.php" ;

        mCaption =(EditText) findViewById(R.id.textBody);

//        spnMoavenat=(Spinner)findViewById(R.id.spnMoavenat);
        image=(ImageView) findViewById(R.id.imgSharePic);
        ivBackArrow=(ImageView) findViewById(R.id.ivBackArrow);
        tvMessage=(TextView) findViewById(R.id.shareTextMessage);
        tvShare=(TextView) findViewById(R.id.tvShare);
        tvStatusSend=(TextView) findViewById(R.id.tvStatusSend);

        mstatus="0";

//        share=(Button)findViewById(R.id.btnShareSocial);


//        SharedPreferences SP = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
        myname=SP.getString("myname", "0");
        myphone=SP.getString("mobile", "0");
        oldLng=SP.getString("lng", "0");
        oldLat=SP.getString("lat", "0");

        final Dialog dialog = new Dialog(ShareActivity.this);
        dialog.setContentView(R.layout.moavenat_popup);
        dialog.setTitle("گیرنده");
        dialog.setCancelable(true);
        // there are a lot of settings, for dialog, check them all out!
        // set up radiobutton
        final RadioButton rd0 = dialog.findViewById(R.id.mrd_0);
        final RadioButton rd1 = dialog.findViewById(R.id.mrd_1);
        final RadioButton rd2 = dialog.findViewById(R.id.mrd_2);
        final RadioButton rd3 = dialog.findViewById(R.id.mrd_3);
        final RadioButton rd4 = dialog.findViewById(R.id.mrd_4);
        final RadioButton rd5 = dialog.findViewById(R.id.mrd_5);
        final RadioButton rd6 = dialog.findViewById(R.id.mrd_6);
        Button btnDialog= dialog.findViewById(R.id.btnMov);
        rd0.setSelected(true);
        // now that the dialog is set up, it's time to show it
        dialog.show();

        rd0.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (rd0.isChecked()){
                gov="1";
                govtxt=rd0.getText().toString();}
            }
        });
        rd1.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (rd1.isChecked()){gov="2";
                govtxt=rd1.getText().toString();}
                Toast.makeText(mContext, gov+" "+govtxt, Toast.LENGTH_SHORT).show();
            }
        });
        rd2.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (rd2.isChecked()){gov="3";
                govtxt=rd2.getText().toString();}
            }
        });
        rd3.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (rd3.isChecked()){gov="4";
                govtxt=rd3.getText().toString();}
            }
        });
        rd4.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (rd4.isChecked()){gov="5";
                govtxt=rd4.getText().toString();}
            }
        });
        rd5.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (rd5.isChecked()){gov="6";
                govtxt=rd5.getText().toString();}
            }
        });
        rd6.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (rd6.isChecked()){gov="7";
                govtxt=rd6.getText().toString();}
            }
        });
        btnDialog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String[] govrs=getApplicationContext().getResources().getStringArray(R.array.moavenat);
                String[] titles=getApplicationContext().getResources().getStringArray(R.array.pmType);
                mstatus=intent.getStringExtra("status");
                tvStatusSend.setText("ارسال "+titles[Integer.valueOf(mstatus)-1]+" به واحد "+govrs[Integer.valueOf(gov)-1]);
                dialog.dismiss();
            }
        });

        ivBackArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        tvShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: navigating to the final share screen.");
                //upload the image to
                mytext = mCaption.getText().toString();

                if (mytext.length()>4) {
                    Toast.makeText(ShareActivity.this, "درحال ارسال اطلاعات", Toast.LENGTH_LONG).show();
                    if (nopicFlag){
                        regSocial();
                    }else{
                        try {
                            setImage();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                    }

                }else {
                    Toast.makeText(mContext, "لطفا متنی برای پیام بنویسید", Toast.LENGTH_LONG).show();
                }

            }
        });

        populateGPS();

        setupBottomNavigationView();
        intent = getIntent();
        if(intent.hasExtra(getString(R.string.selected_image))) {
            nopicFlag=false;
            imgUrl = intent.getStringExtra(getString(R.string.selected_image));
            Glide.with(ShareActivity.this).load(imgUrl)
                    .thumbnail(0.5f)
                    .crossFade()
                    .placeholder(R.drawable.nopic)
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .into(image);


        }else {
            nopicFlag=true;
        }

    }




    /**
     * BottomNavigationView setup
     */
    private void setupBottomNavigationView(){
        Log.d(TAG, "setupBottomNavigationView: setting up BottomNavigationView");
        BottomNavigationViewEx bottomNavigationViewEx =(BottomNavigationViewEx) findViewById(R.id.bottomNavViewBar);
        BottomNavigationViewHelper.setupBottomNavigationView(bottomNavigationViewEx);
        BottomNavigationViewHelper.enableNavigation(mContext, bottomNavigationViewEx);
        Menu menu = bottomNavigationViewEx.getMenu();
        MenuItem menuItem = menu.getItem(ACTIVITY_NUM);
        menuItem.setChecked(true);
    }



    /**
     * gets the image url from the incoming intent and displays the chosen image
     */
    @SuppressLint("StaticFieldLeak")
    private void setImage() throws IOException {
        intent = getIntent();


        if(intent.hasExtra(getString(R.string.selected_image))){
            imgUrl = intent.getStringExtra(getString(R.string.selected_image));
            lat=intent.getStringExtra("lat");
            lng=intent.getStringExtra("lng");
            Log.d(TAG, "setImage: got new image url: " + imgUrl);





            new AsyncTask<Void, Void, Void>() {
                @Override
                protected Void doInBackground(Void... params) {
                    Looper.prepare();
                    try {
                        bitmap = Glide.
                                with(ShareActivity.this).
                                load(imgUrl).
                                asBitmap().
                                into(500,500).
                                get();
                    } catch (final ExecutionException e) {
                        Log.e(TAG, e.getMessage());
                    } catch (final InterruptedException e) {
                        Log.e(TAG, e.getMessage());
                    }
                    uploadImage();
                    return null;
                }
                @Override
                protected void onPostExecute(Void dummy) {
                    if (null != bitmap) {
                        // The full bitmap should be available here
                        image.setImageBitmap(bitmap);
                        Log.d(TAG, "Image loaded");
                    }
                }
            }.execute();

//            Glide.with(ShareActivity.this).load(imgUrl)
//                    .thumbnail(0.5f)
//                    .crossFade()
//                    .diskCacheStrategy(DiskCacheStrategy.ALL)
//                    .into(image);

        }
    }


     /*
     ------------------------------------ send post to server ---------------------------------------------
     */


    private void uploadImage(){
        //Showing the progress dialog
        final ProgressDialog loading = ProgressDialog.show(this,"Uploading...","Please wait...",false,false);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, ServerUploadPath,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String s) {
                        //Disimissing the progress dialog
                        loading.dismiss();
                        //Showing toast message of the response
                        Toast.makeText(ShareActivity.this, "با موفقیت ارسال شد" , Toast.LENGTH_LONG).show();
                        finish();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        //Dismissing the progress dialog
                        loading.dismiss();
                        finish();
                        //Showing toast
//                        Toast.makeText(ShareActivity.this, ""+volleyError, Toast.LENGTH_LONG).show();
                    }
                }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                //Converting Bitmap to String
                String image = getStringImage(bitmap);
                //Getting Image Name
                String name = mobile;
                //Creating parameters
                Map<String,String> params = new Hashtable<String, String>();
                params.put(ImageName, mobile);

                params.put(ImagePath, image);
                params.put("status", mstatus);
                params.put("gov", gov);
                params.put("state", state);
                params.put("lat", oldLat);
                params.put("lng", oldLng);
                params.put("name", myname);
                params.put("phone", myphone);
                params.put("text", mytext);
                params.put("uploadFile", image);


                //returning parameters
                return params;
            }
        };

        //Creating a Request Queue
        RequestQueue requestQueue = Volley.newRequestQueue(this);

        //Adding request to the queue
        requestQueue.add(stringRequest);
    }


    public String getStringImage(Bitmap bmp) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.JPEG, 30, baos);
        byte[] imageBytes = baos.toByteArray();
        String encodedImage = Base64.encodeToString(imageBytes, Base64.DEFAULT);
        return encodedImage;

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
            SP.apply();
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
                        finish();
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
                params.put("state", state);
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
