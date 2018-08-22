package com.idpz.instacity.Home;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Typeface;
import android.location.Location;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.idpz.instacity.Area;
import com.idpz.instacity.R;
import com.idpz.instacity.utils.GPSTracker;
import com.stfalcon.smsverifycatcher.OnSmsCatchListener;
import com.stfalcon.smsverifycatcher.SmsVerifyCatcher;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static android.Manifest.permission.ACCESS_COARSE_LOCATION;
import static android.Manifest.permission.ACCESS_FINE_LOCATION;

public class SmsVerificationActivity extends Activity {

    private static final int REQUEST_ACCESS_LOCATION = 1;
    SmsVerifyCatcher smsVerifyCatcher;
    EditText editCode;
    String REGISTER_URL="",mob="",code="",server="",ctName,state="";
    TextView txtResult;
    ProgressBar progressBar;
    GPSTracker gps;
    Location myLocation,mycity;
    Area myArea;
    Boolean areaFlag=false,smsFlag=false,connected=false,gpsFlag=false,remain=true;
    String AREA_URL = "";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ruydad);

        smsFlag=requestSmsPermission();
        REGISTER_URL=getString(R.string.server)+"/i/smssend.php";
        AREA_URL = getString(R.string.server)+"/i/getarea.php";
        SharedPreferences SP1;
        SP1 = PreferenceManager.getDefaultSharedPreferences(SmsVerificationActivity.this);
        state = SP1.getString("state", "0");

        Typeface yekan = Typeface.createFromAsset(SmsVerificationActivity.this.getAssets(), "fonts/YEKAN.TTF");
        myArea=new Area();
        mob=getIntent().getStringExtra("mob");
        gpsFlag=getIntent().getBooleanExtra("gps",true);
        server=getIntent().getStringExtra("server");
        myArea.setServer(server);
        myArea.setAfname(getIntent().getStringExtra("ctname"));
        myArea.setAename(getIntent().getStringExtra("aename"));
        myArea.setAlat(getIntent().getFloatExtra("alat",35.711f));
        myArea.setAlng(getIntent().getFloatExtra("alng",50.912f));

//        Toast.makeText(getApplicationContext(), " mob="+mob, Toast.LENGTH_SHORT).show();
        txtResult=(TextView) findViewById(R.id.txtResult);
        editCode=(EditText) findViewById(R.id.edtCode);
        final Button btnVerify =(Button) findViewById(R.id.btnVerify);
        progressBar=(ProgressBar) findViewById(R.id.progressReceiveCode);

        txtResult.setTypeface(yekan);
        editCode.setTypeface(yekan);

        myLocation = new Location("myloc");
        mycity = new Location("city");

       
        if (gpsFlag)populateGPS();



        if (smsFlag) {
            smsVerifyCatcher = new SmsVerifyCatcher(this, new OnSmsCatchListener<String>() {
                @Override
                public void onSmsCatch(String message) {
                    progressBar.setVisibility(View.INVISIBLE);
                    String code = parseCode(message);//Parse verification code
                    editCode.setText(code);//set code in edit text
                    //then you can send verification code to server
                }
            });
        }else {
            txtResult.setText(" دسترسی به اس ام اس را تایید نشد. کد دریافتی را درقسمت بالا بنویسید.");
        }
        //button for sending verification code manual
        btnVerify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                code=editCode.getText().toString();
                if (isConnected()){
                    if (code.length()==6){

                        reqData();
                    }else {
                        editCode.setError("کد باید 6 رقم باشد");
                        editCode.requestFocus();
                    }
                }else {
                    txtResult.setText("اینترنت متصل نیست!");
                }

                //send verification code to server
            }
        });


        new Thread() {
            @Override
            public void run() {
                while (remain&!areaFlag) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            isConnected();
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



        editCode.addTextChangedListener(new TextWatcher() {

            @Override
            public void afterTextChanged(Editable s) {
                if (s.length()==6){
                    code=editCode.getText().toString();
                    reqData();
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
    private String parseCode(String message) {
        Pattern p = Pattern.compile("\\b\\d{6}\\b");
        Matcher m = p.matcher(message);
        String code = "";
        while (m.find()) {
            code = m.group(0);
        }
        return code;
    }


    @Override
    protected void onStart() {
        super.onStart();
        smsFlag=requestSmsPermission();
        if (smsFlag)smsVerifyCatcher.onStart();
    }

    @Override
    protected void onStop() {
        super.onStop();
        smsFlag=requestSmsPermission();
        if (smsFlag)smsVerifyCatcher.onStop();
    }

    /**
     * need for Android 6 real time permissions
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (smsFlag)smsVerifyCatcher.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    private Boolean requestSmsPermission() {
        String permission = Manifest.permission.RECEIVE_SMS;
        int grant = ContextCompat.checkSelfPermission(this, permission);
        if ( grant != PackageManager.PERMISSION_GRANTED) {
            return false;
        }else return true;
    }

    public void reqData() {
        RequestQueue queue = Volley.newRequestQueue(this);

        StringRequest postRequest = new StringRequest(Request.Method.POST, REGISTER_URL,
                new Response.Listener<String>()
                {
                    public static final String TAG = "code verify";

                    @Override
                    public void onResponse(String response) {
                        Log.d(TAG, "onResponse: "+response);

                        if (response.length()==9){
                            SharedPreferences.Editor SP2 = PreferenceManager.getDefaultSharedPreferences(getBaseContext()).edit();
                            SP2.putString("mobile", mob);
                            SP2.putString("pass", editCode.getText().toString());
                            SP2.putString("lat", String.valueOf(myLocation.getLatitude()));
                            SP2.putString("lng", String.valueOf(myLocation.getLongitude()));
                            SP2.putString("pic", "0.jpg");
                            SP2.putString("myname", "-");
                            SP2.putString("homelat", String.valueOf(myArea.getAlat()));
                            SP2.putString("homelng", String.valueOf(myArea.getAlng()));
                            SP2.putString("server", myArea.getServer());
                            SP2.putString("ctname", myArea.getAfname());
                            SP2.putString("aename", myArea.getAename());
                            SP2.putString("birth", "0");
                            SP2.putString("gen", "1");
                            SP2.putString("fav", "0");
                            SP2.putString("edu", "0");
                            SP2.putString("edub", "0");
                            SP2.putString("job", "0");
                            SP2.putString("jobb", "0");
                            SP2.putString("money", "100");
                            SP2.putString("notification","0");
                            SP2.putString("numPosts", "0");
                            SP2.putString("melliid", "0");
                            SP2.apply();
                            remain=false;
                            regUserProfile();
                            Intent intent = new Intent(SmsVerificationActivity.this, HomeActivity.class);
                            intent.putExtra("mob", mob);
                            intent.putExtra("cod", editCode.getText().toString());
                            startActivity(intent);
                            finish();
                        }else if (response.length()>9){
                            remain=false;
                            JSONArray jsonArray = null;
                        try {
                            jsonArray = new JSONArray(response);
                            JSONObject jsonObject = jsonArray.getJSONObject(0);
                            Log.d(TAG, "onResponse: data receive "+response);


                            for (int i = 0; i < jsonArray.length(); i++) {
                                jsonObject = jsonArray.getJSONObject(i);

                                String name = jsonObject.getString("name");
                                String  mobile = jsonObject.getString("mobile");
                                String birth = jsonObject.getString("birth");
                                String gender = jsonObject.getString("gender");
                                String edu = jsonObject.getString("edu");
                                String edub = jsonObject.getString("edub");
                                String job = jsonObject.getString("job");
                                String jobb = jsonObject.getString("jobb");
                                String fav = jsonObject.getString("fav");
                                String money = jsonObject.getString("money");
//                                String server = jsonObject.getString("server");
                                String lat = jsonObject.getString("lat");
                                String lng = jsonObject.getString("lng");
                                String img=jsonObject.getString("pic");
                                if (img.equals("null")||img.isEmpty()||img.length()<4){
                                    img="0.jpg";
                                }
                                String pic= img;

                                SharedPreferences.Editor SP2 = PreferenceManager.getDefaultSharedPreferences(getBaseContext()).edit();
                                SP2.putString("myname", name);
                                SP2.putString("mobile", mobile);
//                                SP2.putString("melliid", melliid);
                                SP2.putString("pass", code);
                                SP2.putString("lat", String.valueOf(myLocation.getLatitude()));
                                SP2.putString("lng", String.valueOf(myLocation.getLongitude()));
                                SP2.putString("homelat",String.valueOf(myArea.getAlat()));
                                SP2.putString("homelng",String.valueOf(myArea.getAlng()));
                                SP2.putString("server", myArea.getServer());
                                SP2.putString("ctname", myArea.getAfname());
                                SP2.putString("aename", myArea.getAename());
                                SP2.putString("birth", birth);
                                SP2.putString("gen", gender);
                                SP2.putString("fav", fav);
                                SP2.putString("edu", edu);
                                SP2.putString("edub", edub);
                                SP2.putString("job", job);
                                SP2.putString("jobb", jobb);
                                SP2.putString("pic", pic);
                                SP2.putString("money", money);
//                                SP2.putString("ctpic",String.valueOf(ctpic));
//                                SP2.putString("ctdesc",String.valueOf(ctDesc));
                                SP2.apply();

                            Intent intent=new Intent(SmsVerificationActivity.this,HomeActivity.class);
                            intent.putExtra("mob",mob);

                            startActivity(intent);
                            }

                        } catch (JSONException e) {

                            e.printStackTrace();

                        }

                        }else {
                            txtResult.setText("کد  اشتباه است");
                        }

                    }
                },
                new Response.ErrorListener()
                {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // TODO Auto-generated method stub
                        Log.d("ERROR","error => "+error.toString());
                        txtResult.setText("خطایی رخ داده دوباره تایید را بزنید");

                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String>params = new HashMap<String,String>();
                params.put("mob", mob);
                params.put("state", state);
                params.put("cod", editCode.getText().toString());
                params.put("lat", String.valueOf(myLocation.getLatitude()));
                params.put("lng", String.valueOf(myLocation.getLongitude()));


                return params;
            }
        };
        queue.add(postRequest);

    }




    private void populateGPS() {
        if (!mayRequestLocation()) {
            myLocation.setLatitude(myArea.getAlat());
            myLocation.setLongitude(myArea.getAlng());
            return;
        }
        gps =new GPSTracker(this);
//        Log.d(TAG, "onCreate: GPS FROM populate gps_before Check:"+String.valueOf(gps.getLatitude())+" : "+String.valueOf(gps.getLongitude()));
        String upStatus="";
        if (gps.getLatitude()!=0.0) {
            SharedPreferences.Editor SP = PreferenceManager.getDefaultSharedPreferences(getBaseContext()).edit();
            SP.putString("lat", String.valueOf(gps.getLatitude()));
            SP.putString("lng", String.valueOf(gps.getLongitude()));
            SP.apply();

//            Log.d(TAG, "onCreate: GPS FROM populate gps_ok:"+String.valueOf(gps.getLatitude())+" : "+String.valueOf(gps.getLongitude()));
            myLocation.setLatitude(gps.getLatitude());
            myLocation.setLongitude(gps.getLongitude());
            upStatus="gps_ok";
        }else {
            myLocation.setLatitude(myArea.getAlat());
            myLocation.setLongitude(myArea.getAlng());
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
            Snackbar.make(editCode, "لطفا دسترسی به جی پی اس را فعال کنید.", Snackbar.LENGTH_INDEFINITE)
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


    public void regUserProfile() {
        RequestQueue queue = Volley.newRequestQueue(this);
        String url =getString(R.string.server)+"/j/profile.php";
        StringRequest postRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>()
                {
                    @Override
                    public void onResponse(String response) {

//                        txtczstatus.setText(response.toString());
                    }
                },
                new Response.ErrorListener()
                {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                        Log.d("ERROR","error => "+error.toString());
//                        txtczstatus.setText(error.toString()+"مشکلی در ارسال داده پیش آمده دوباره تلاش کنید");
                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String>params = new HashMap<String,String>();

                params.put("name", "-");
                params.put("mob", mob);
                params.put("pas", code);
                params.put("birth", "0");
                params.put("gen", "1");
                params.put("edu", "1");
                params.put("eb", "1");
                params.put("job", "1");
                params.put("jb", "1");
                params.put("pic", "0.jpg");
                params.put("fav", "0");
                params.put("meli","0");
                params.put("mny", "100");
                params.put("lat", myLocation.getLatitude()+"");
                params.put("lng", myLocation.getLongitude()+"");
                return params;
            }
        };
        queue.add(postRequest);


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

}
