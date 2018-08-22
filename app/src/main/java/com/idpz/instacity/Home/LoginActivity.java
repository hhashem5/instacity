package com.idpz.instacity.Home;

import android.Manifest;
import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Typeface;
import android.location.Location;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
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
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStates;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.idpz.instacity.Area;
import com.idpz.instacity.R;
import com.idpz.instacity.utils.GPSTracker;
import com.stfalcon.smsverifycatcher.SmsVerifyCatcher;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * A login screen that offers login via email/password.
 */
public class LoginActivity extends Activity implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    private static final String TAG = "Login activity";
    String AREA_URL = "";
    TextView txtInfo;
    Spinner spnCity;
    Area myArea,myNearCity;
    List<Float> distances=new ArrayList<Float>();
    String mob="",REGISTER_URL="";
    Boolean connected=false,smsFlag=true,gpsFlag=true,areaFlag=false,remain=true;
    Location myLocation, mycity;
    Button btnMobileReg,btnCityReg;
    // UI references.
    private EditText mEmailView;
    int failCount=0;
    private View mProgressView;
    SmsVerifyCatcher smsVerifyCatcher;

    GPSTracker gps;

    ArrayList<String> ctNames=new ArrayList<>();
    ArrayList<Area> areaArrayList=new ArrayList<>();
    AutoCompleteTextView textView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login2);
        // Set up the login form.
        Button btnChangeCT;
        Typeface yekan = Typeface.createFromAsset(LoginActivity.this.getAssets(), "fonts/YEKAN.TTF");
        myLocation = new Location("myloc");
        mycity = new Location("city");
        AREA_URL=getString(R.string.server)+"/i/getarea.php";
        gps =new GPSTracker(this);

        SharedPreferences SP1;
        SP1 = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
        String mobile = SP1.getString("mobile", "0");

        if (mobile.length()==11)
        {
            Intent intent=new Intent(LoginActivity.this,HomeActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_SINGLE_TOP);
            startActivity(intent);
            remain=false;
            finish();
        }
        if(!hasPermissions(this, PERMISSIONS)){
            ActivityCompat.requestPermissions(this, PERMISSIONS, PERMISSION_ALL);
        }else {
            if (gps.canGetLocation()){
                populateGPS();
            }else {
                EnableGPSAutoMatically();
            }


        }

        REGISTER_URL=getString(R.string.server)+"/i/VerificationCode.php";

        mEmailView = (EditText)findViewById(R.id.txtLoginMobile);

        TextView txtSelect=(TextView) findViewById(R.id.txtSelectCity);
        txtInfo=(TextView) findViewById(R.id.txtLoginInfo);
        btnMobileReg=(Button) findViewById(R.id.btnMobileReg);

        mEmailView.setTypeface(yekan);
        btnMobileReg.setTypeface(yekan);
        txtInfo.setTypeface(yekan);
        txtSelect.setTypeface(yekan);

        btnMobileReg.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptLogin();
            }
        });

        mProgressView = findViewById(R.id.login_progress);
        btnCityReg=(Button) findViewById(R.id.btnCityReg);
        btnCityReg.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent in1=new Intent(LoginActivity.this,AddCityActivity.class);
                startActivity(in1);
            }
        });




        spnCity=(Spinner) findViewById(R.id.spnLoginSelCity);



        spnCity.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                myArea=areaArrayList.get(position);
                textView.setText("");
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });




        new Thread() {
            @Override
            public void run() {
                while (remain&!areaFlag) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                           if (isConnected()) {

                               if (failCount > 3) {

                                   txtInfo.setText("بعد از اتصال اینترنت دوباره وارد برنامه شوید!");
                                   remain=false;
                                   Log.d(TAG, "run: fail count succeed  exit");
                               }


                               reqArea();   //یافتن منطقه کاربر و اتصال به سوور هماه منطقه
                           }else {
                               txtInfo.setText("خطا لطفا از اتصال اینترنت مطمئن شوید!");
                           }
                        }
                    });
                    try {
                        sleep(7000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }.start();





        textView =(AutoCompleteTextView) findViewById(R.id.acTxtView);

        textView.setTypeface(yekan);
        textView.addTextChangedListener(new TextWatcher() {
        @Override
        public void afterTextChanged(Editable s) {
            if (s.length()>0){
                int i=0;
                for (Area area:areaArrayList){
                    i++;
                    mycity.setLatitude(area.getAlat());
                    mycity.setLongitude(area.getAlng());
                    if (s.toString().contains(area.getAfname())){
                        spnCity.setSelection(i-1);
                    }
                }


//                    searchModels.clear();
//                    for (Area area:areaArrayList){
//                        if (area.getAfname().contains(s)){
//                            searchModels.add(area.getAfname());
//                        }
//                    }
//                    ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<String>
//                            (ChangeCityActivity.this, android.R.layout.simple_spinner_item,
//                                    searchModels); //selected item will look like a spinner set from XML
            }

        }

        @Override
        public void beforeTextChanged(CharSequence s, int start,
        int count, int after) {
        }

        @Override
        public void onTextChanged(CharSequence s, int start,
        int before, int count) {
//                if(s.length() != 0)
//                    field2.setText("");
        }
    });

    }

    /**
     * Attempts to sign in or register the account specified by the login form.
     * If there are form errors (invalid email, missing fields, etc.), the
     * errors are presented and no actual login attempt is made.
     */
    private void attemptLogin() {


        // Reset errors.
        mEmailView.setError(null);


        // Store values at the time of the login attempt.
        String email = mEmailView.getText().toString();


        boolean cancel = false;
        View focusView = null;


        // Check for a valid email address.
        if (TextUtils.isEmpty(email)) {
            mEmailView.setError(getString(R.string.error_field_required));
            focusView = mEmailView;
            cancel = true;
        } else if (!isEmailValid(email)) {
            mEmailView.setError(getString(R.string.error_invalid_email));
            focusView = mEmailView;
            cancel = true;
        }else if (!(email.length()==11)) {
            mEmailView.setError("شماره موبایل را  11 رقم وارد کنید");
            focusView = mEmailView;
            cancel = true;
        }else if (!areaFlag) {
            textView.setError("لیست شهرها دریافت نشده");
            focusView = textView;
            cancel = true;
        }

        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            focusView.requestFocus();
        } else {
            // Show a progress spinner, and kick off a background task to
            // perform the user login attempt.
            showProgress(true);
            mob=email;
            Log.d(TAG, "attemptLogin: requested ");
            if (isConnected()){
                reqVerify();
//                Intent intent=new Intent(LoginActivity.this,SmsVerificationActivity.class);
//                intent.putExtra("mob",mob);
//                intent.putExtra("sms",smsFlag);
//                intent.putExtra("gps",gpsFlag);
//                intent.putExtra("ctname",myArea.getAfname());
//                intent.putExtra("server",myArea.getServer());
//                intent.putExtra("alat",myArea.getAlat());
//                intent.putExtra("alng",myArea.getAlng());
//                intent.putExtra("aename",myArea.getAename());

                SharedPreferences.Editor SP = PreferenceManager.getDefaultSharedPreferences(getBaseContext()).edit();
                SP.putString("state", myArea.getState());
                SP.apply();
//                startActivity(intent);
            }else {
                txtInfo.setText( "اینترنت وصل نیست ارسال پیامک انجام نشد");
            }

        }
    }

    private boolean isEmailValid(String email) {
        //TODO: Replace this with your own logic
        return email.startsWith("09");
    }



    /**
     * Shows the progress UI and hides the login form.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private void showProgress(final boolean show) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.

            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);


            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mProgressView.animate().setDuration(shortAnimTime).alpha(
                    show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
                }
            });

    }

    public void reqVerify() {
        RequestQueue queue = Volley.newRequestQueue(this);

        StringRequest postRequest = new StringRequest(Request.Method.POST, REGISTER_URL,
                new Response.Listener<String>()
                {
                    public static final String TAG = "code verify";

                    @Override
                    public void onResponse(String response) {
                        Log.d(TAG, "onResponse: "+response);
                        smsFlag=requestSmsPermission();
                        Intent intent=new Intent(LoginActivity.this,SmsVerificationActivity.class);
                        intent.putExtra("mob",mob);
                        intent.putExtra("sms",smsFlag);
                        intent.putExtra("gps",gpsFlag);
                        intent.putExtra("ctname",myArea.getAfname());
                        intent.putExtra("server",myArea.getServer());
                        intent.putExtra("alat",myArea.getAlat());
                        intent.putExtra("alng",myArea.getAlng());
                        intent.putExtra("aename",myArea.getAename());

                        SharedPreferences.Editor SP = PreferenceManager.getDefaultSharedPreferences(getBaseContext()).edit();
                        SP.putString("state", myArea.getState());
                        SP.apply();

                         startActivity(intent);

                    }
                },
                new Response.ErrorListener()
                {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // TODO Auto-generated method stub
                        Log.d("ERROR","error => "+error.toString());
                        showProgress(false);
                        txtInfo.setText("خطا در ارسال پیامک دوباره امتحان کنید");
                        if (error.toString().equals("com.android.volley.TimeoutError")){
                            reqVerify();
                        }
                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String>params = new HashMap<String,String>();
                params.put("mobile", mob);
                return params;
            }
        };
        queue.add(postRequest);

    }


    int PERMISSION_ALL = 1;
    String[] PERMISSIONS = {Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.RECEIVE_SMS};

    public static boolean hasPermissions(Context context, String... permissions) {
        if (context != null && permissions != null) {
            for (String permission : permissions) {
                if (ActivityCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                    Log.d(TAG, "hasPermissions: "+permission);

                    return false;
                }

            }
        }
        return true;
    }

    private Boolean requestSmsPermission() {
        String permission = Manifest.permission.RECEIVE_SMS;
        int grant = ContextCompat.checkSelfPermission(this, permission);
        if ( grant != PackageManager.PERMISSION_GRANTED) {
            return false;
        }else return true;
    }


    @Override
    public void onRequestPermissionsResult(int requestCode,String permissions[], int[] grantResults) {
        switch (requestCode) {
            case 1: {

                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    EnableGPSAutoMatically();
                    Log.d(TAG, "onRequestPermissionsResult: gps ok");
                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                    Toast.makeText(LoginActivity.this, "دسترسی جی پی اس تایید نشد شهر را جستجو کنید", Toast.LENGTH_SHORT).show();
                }
                if (grantResults.length > 0 && grantResults[2] == PackageManager.PERMISSION_GRANTED) {
                    Log.d(TAG, "onRequestPermissionsResult: Storage ok");
                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.
                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                    Toast.makeText(LoginActivity.this, "برای ارسال عکس نیاز به دسترسی حافظه است", Toast.LENGTH_SHORT).show();
                }
                if (grantResults.length > 0 && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                    Log.d(TAG, "onRequestPermissionsResult: Storage Sms");
                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.
                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                    Toast.makeText(LoginActivity.this, "برای ارسال عکس نیاز به دسترسی حافظه است", Toast.LENGTH_SHORT).show();
                }

                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request
        }
    }


    private void populateGPS() {

//        Log.d(TAG, "onCreate: GPS FROM populate gps_before Check:"+String.valueOf(gps.getLatitude())+" : "+String.valueOf(gps.getLongitude()));
        String upStatus="";
        GPSTracker gps2=new GPSTracker(this);
        if (gps2.getLatitude()!=0.0) {
            SharedPreferences.Editor SP = PreferenceManager.getDefaultSharedPreferences(getBaseContext()).edit();
            SP.putString("lat", String.valueOf(gps2.getLatitude()));
            SP.putString("lng", String.valueOf(gps2.getLongitude()));
            SP.apply();

//            Log.d(TAG, "onCreate: GPS FROM populate gps_ok:"+String.valueOf(gps.getLatitude())+" : "+String.valueOf(gps.getLongitude()));
            myLocation.setLatitude(gps2.getLatitude());
            myLocation.setLongitude(gps2.getLongitude());
            upStatus="gps_ok";
        }else {
            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                public void run() {
                    // Actions to do after 10 seconds

                    if (areaFlag)populateGPS();
                }
            }, 1000);

        }

//        Toast.makeText(LoginActivity.this, "location ="+myLocation.getLatitude()+","+myLocation.getLongitude(), Toast.LENGTH_SHORT).show();
        gpsFlag=true;
        if (areaFlag)
        findArea();
        Log.d(TAG, "populateGPS: "+myLocation.getLatitude()+","+myLocation.getLongitude());
    }

    private void EnableGPSAutoMatically() {
        GoogleApiClient googleApiClient = null;
        if (googleApiClient == null) {
            googleApiClient = new GoogleApiClient.Builder(this)
                    .addApi(LocationServices.API).addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this).build();
            googleApiClient.connect();
            LocationRequest locationRequest = LocationRequest.create();
            locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
            locationRequest.setInterval(30 * 1000);
            locationRequest.setFastestInterval(5 * 1000);
            LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                    .addLocationRequest(locationRequest);

            // **************************
            builder.setAlwaysShow(true); // this is the key ingredient
            // **************************

            PendingResult<LocationSettingsResult> result = LocationServices.SettingsApi
                    .checkLocationSettings(googleApiClient, builder.build());
            result.setResultCallback(new ResultCallback<LocationSettingsResult>() {
                @Override
                public void onResult(LocationSettingsResult result) {
                    final Status status = result.getStatus();
                    final LocationSettingsStates state = result
                            .getLocationSettingsStates();
                    switch (status.getStatusCode()) {
                        case LocationSettingsStatusCodes.SUCCESS:
//                            toast("جی پی اس روشن است");

                            // All location settings are satisfied. The client can
                            // initialize location
                            // requests here.
                            break;
                        case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                            toast("جی پی اس روشن نیست");
                            // Location settings are not satisfied. But could be
                            // fixed by showing the user
                            // a dialog.
                            try {
                                // Show the dialog by calling
                                // startResolutionForResult(),
                                // and check the result in onActivityResult().
                                status.startResolutionForResult(LoginActivity.this, 1000);

                            } catch (IntentSender.SendIntentException e) {
                                // Ignore the error.
                            }
                            break;
                        case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                            toast("امکان تغییر تنظیمات برای روشن کردن جی پی اس نیست");
                            // Location settings are not satisfied. However, we have
                            // no way to fix the
                            // settings so we won't show the dialog.
                            break;
                    }
                }
            });
        }
    }



    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == 1000) {
            if(resultCode == Activity.RESULT_OK){
                String result=data.getStringExtra("result");
                Toast.makeText(this, result, Toast.LENGTH_SHORT).show();
                populateGPS();
            }
            if (resultCode == Activity.RESULT_CANCELED) {
                //Write your code if there's no result
            }
        }

        if (requestCode==110 && resultCode==1){

                    if (areaFlag)populateGPS();

        }
    }
    @Override
    public void onConnected(@Nullable Bundle bundle) {
        if (areaFlag)populateGPS();
    }

    @Override
    public void onConnectionSuspended(int i) {
        toast("Suspended");
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
//        toast("Failed");
        final CharSequence[] items = {"بله", "خیر"};
        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(LoginActivity.this);
        builder.setTitle("آیا جی پی اس را روشن می کنید");
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {

                if (items[item].equals("بله")) {
                    Intent i = new
                            Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                    startActivityForResult(i,110);
                    dialog.dismiss();
                } else if (items[item].equals("خیر")) {

                    dialog.dismiss();
                }
            }
        });
        builder.show();
    }
    private void toast(String message) {
        try {
            Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
        } catch (Exception ex) {

        }
    }



public Boolean isConnected(){
    ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
    if (connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState() == NetworkInfo.State.CONNECTED ||
            connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState() == NetworkInfo.State.CONNECTED) {
        //we are connected to a network
//                                SharedPreferences.Editor SP = PreferenceManager.getDefaultSharedPreferences(getBaseContext()).edit();
//                                SP.putBoolean("connected", true);
//                                SP.apply();
        connected = true;
        return true;
    } else {
//                                SharedPreferences.Editor SP = PreferenceManager.getDefaultSharedPreferences(getBaseContext()).edit();
//                                SP.putBoolean("connected", false);
//                                SP.apply();
        connected = false;
        return false;

//                                txtNews.setText("اینترنت وصل نیست");
    }
}


    public void reqArea() {
        RequestQueue queue = Volley.newRequestQueue(this);
        String url = AREA_URL;
        StringRequest postRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>()
                {
                    public static final String TAG = "change city";

                    @Override
                    public void onResponse(String response) {
                        JSONArray jsonArray= null;
                        Log.d(TAG, "onResponse: response from area"+response);
                        ctNames.clear();
                        areaArrayList.clear();
                        areaFlag=true;

                        try {
                            jsonArray = new JSONArray(response);

                            JSONObject jsonObject=jsonArray.getJSONObject(0);

                            for (int i=jsonArray.length();i>0;i--) {
                                jsonObject = jsonArray.getJSONObject(i-1);
                                float myDistance=0;
                                Area area=new Area();
                                area.setId(jsonObject.getInt("aid"));
                                area.setAename(jsonObject.getString("aename"));
                                area.setAfname(jsonObject.getString("afname"));
                                area.setState(jsonObject.getString("state"));
                                area.setAlat(Float.valueOf(jsonObject.getString("alat")));
                                area.setAlng(Float.valueOf(jsonObject.getString("alng")));
                                area.setServer(jsonObject.getString("server"));
                                area.setZoom(jsonObject.getInt("azoom"));
                                area.setPic(jsonObject.getString("pic"));
                                area.setDescription(jsonObject.getString("memo"));

                               areaArrayList.add(area);
                               ctNames.add(area.getAfname());

                            }
                            ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<String>
                                    (LoginActivity.this, android.R.layout.simple_spinner_item,
                                            ctNames); //selected item will look like a spinner set from XML
                            spinnerArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                            spnCity.setAdapter(spinnerArrayAdapter);

                            ArrayAdapter<String> adapter = new ArrayAdapter<String>(LoginActivity.this,
                                    android.R.layout.simple_dropdown_item_1line, ctNames);
                            textView.setAdapter(adapter);

                            if (gpsFlag)findArea();

                        } catch (JSONException e) {
                            e.printStackTrace();
                            failCount++;
                        }

                    }
                },
                new Response.ErrorListener()
                {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        failCount++;
                        Log.d("ERROR","Login area error => "+error.toString()+failCount);
                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String>params = new HashMap<String,String>();

//                params.put("name", );

                return params;
            }
        };
        queue.add(postRequest);
    }

    public void findArea(){
        float myDistance=0;
        distances.clear();
//        myLocation.setLatitude(Double.valueOf(lat));
//        myLocation.setLongitude(Double.valueOf(lng));

//        Log.d(TAG, "findArea: gps mylocation="+myLocation.getLatitude());
        for (Area area:areaArrayList){

            mycity.setLatitude(area.getAlat());
            mycity.setLongitude(area.getAlng());
            myDistance=Math.round(myLocation.distanceTo(mycity));
            distances.add(myDistance);
        }

        int minIndex = distances.indexOf(Collections.min(distances));
        myNearCity=areaArrayList.get(minIndex);
//        txtMyNearCT.setText(" نزدیکترین شهر: "+myNearCity.getAfname()+" فاصله از مرکز "+distances.get(minIndex)+"متر");
//        txtMyNearCT.setTextColor(Color.GREEN);
        spnCity.setSelection(minIndex);
    }


}