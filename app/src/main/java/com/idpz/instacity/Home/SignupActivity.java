package com.idpz.instacity.Home;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Spinner;
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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static android.Manifest.permission.ACCESS_COARSE_LOCATION;
import static android.Manifest.permission.ACCESS_FINE_LOCATION;

public class SignupActivity extends AppCompatActivity {

    private static final String TAG = "sign up ";
    private static final int REQUEST_ACCESS_LOCATION = 1;
    EditText txtName,txtMobile,txtPass;
    RadioButton rbMard;
    Button btnReg,btnSignIn;
    String myname,melli,mob,sal,pass,gender,ctName;
    TextView txtmsg;
    String REGISTER_URL="",server="",ctDesc="",ctpic="";
    Boolean kansel=false;
    View focusView = null;
    Boolean areaFlag=false,connected=false;
    Spinner spCity;
    GPSTracker gps;
    Location myLocation,mycity;
    Float homelat,homelng;

    ArrayList<Area> areaArrayList=new ArrayList<>();
    private static final String AREA_URL = "http://idpz.ir/i/getarea.php";
    private static final String STATIC_SERVER = "http://idpz.ir";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        myLocation = new Location("myloc");
        mycity = new Location("city");
        REGISTER_URL=STATIC_SERVER+"/i/profile.php";
        txtName= findViewById(R.id.txtSignupNmae);
        txtMobile= findViewById(R.id.txtSignupMobile);
        txtPass= findViewById(R.id.txtSignupPass);
        btnReg= findViewById(R.id.btnSignupReg);
        btnSignIn= findViewById(R.id.btnSigninReg);
        txtmsg= findViewById(R.id.txtSignupMsg);
        rbMard= findViewById(R.id.rbSignupMard);

        populateGPS();

        SharedPreferences SP1;
        SP1 = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
        String mobile = SP1.getString("mobile", "0");
        if (mobile.length()==11)
        {
            Intent intent=new Intent(SignupActivity.this,HomeActivity.class);
            startActivity(intent);
            finish();
        }

        spCity= findViewById(R.id.spnLoginSelCity);
        spCity.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                server=areaArrayList.get(position).getServer();
                ctName=areaArrayList.get(position).getAfname();
                homelat=areaArrayList.get(position).getAlat();
                homelng=areaArrayList.get(position).getAlng();
                ctDesc=areaArrayList.get(position).getDescription();
                ctpic=areaArrayList.get(position).getPic();


//                Toast.makeText(SignupActivity.this, "select:"+position+" "+server, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        btnSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(SignupActivity.this,LoginActivity.class);
                startActivity(intent);
                finish();
            }
        });



        new Thread() {
            @Override
            public void run() {
                while (!areaFlag) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
                            //we are connected to a network
//                                Toast.makeText(SignupActivity.this, "اینترنت وصل نیست", Toast.LENGTH_SHORT).show();
                            connected = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState() == NetworkInfo.State.CONNECTED ||
                                    connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState() == NetworkInfo.State.CONNECTED;


                            if (connected && !areaFlag) {
                                reqArea();   //یافتن منطقه کاربر و اتصال به سوور هماه منطقه
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

        btnReg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                myname=txtName.getText().toString();
                melli="";
                mob=txtMobile.getText().toString();
                sal="";
                pass=txtPass.getText().toString();

                txtName.setError(null);
                txtPass.setError(null);
                txtMobile.setError(null);
//                txtMelli.setError(null);

                if(myname.length()<2){
                    txtName.setError("نام کوتاه است");
                    focusView=txtName;
                    kansel=true;
                }
                if(mob.length()!=11){
                    txtMobile.setError("شماره موبایل 11رقمی است");
                    focusView=txtMobile;
                    kansel=true;
                }
//                if(melli.length()!=10){
//                    txtMelli.setError("شماره ملی 10 رقمی است");
//                    focusView=txtMelli;
//                    kansel=true;
//                }
//                if(sal.length()!=4){
//                    txtYear.setError("سال تولد 4 رقمی است");
//                    focusView=txtYear;
//                    kansel=true;
//                }
                if(pass.length()<4){
                    txtPass.setError("کلمه عبور حداقل 5 رقم");
                    focusView=txtPass;
                    kansel=true;
                }

                if (rbMard.isChecked()){
                    gender="1";
                }else {
                    gender="0";
                }
                if (!kansel) {
                    regUserInfo();
                }else {
                    focusView.requestFocus();
                }
            }
        });

    }

    public void regUserInfo() {
        RequestQueue queue = Volley.newRequestQueue(this);
        String url = REGISTER_URL;
        StringRequest postRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>()
                {
                    @Override
                    public void onResponse(String response) {

                        txtmsg.setText("با موفقیت ثبت نام شدید");
                        SharedPreferences.Editor SP2 = PreferenceManager.getDefaultSharedPreferences(getBaseContext()).edit();
                        SP2.putString("myname", myname);
                        SP2.putString("mobile", mob);
//                        SP2.putString("melliid", melli);
                        SP2.putString("pass", pass);
                        SP2.putString("birth", sal);
                        SP2.putString("server", server);
                        SP2.putString("numPosts", "0");
                        SP2.putString("gen", gender);
                        SP2.putString("ctname", ctName);
                        SP2.putString("homelat",String.valueOf(homelat));
                        SP2.putString("homelng",String.valueOf(homelng));
                        SP2.putString("ctpic",String.valueOf(ctpic));
                        SP2.putString("ctdesc",String.valueOf(ctDesc));
                        SP2.apply();
                        Intent intent=new Intent(SignupActivity.this,HomeActivity.class);
                        startActivity(intent);
                        finish();

                    }
                },
                new Response.ErrorListener()
                {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // TODO Auto-generated method stub
                        Log.d("ERROR","error => "+error.toString());
                        txtmsg.setText(error.toString()+"مشکلی در ارسال داده پیش آمده دوباره تلاش کنید");
                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String>params = new HashMap<String,String>();

                params.put("name", myname);
                params.put("mob", mob);
                params.put("birth", sal);
                params.put("server", server);
                params.put("gen", gender);
                params.put("meli",melli);
                params.put("pas", pass);
                return params;
            }
        };
        queue.add(postRequest);


    }

    public void onBackPressed() {
        Log.d("CDA", "onBackPressed Called");
        finish();
    }

    public void reqArea() {
        RequestQueue queue = Volley.newRequestQueue(this);
        String url = AREA_URL;
        StringRequest postRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>()
                {
                    @Override
                    public void onResponse(String response) {
                        JSONArray jsonArray= null;

                        ArrayList<String> cityNames=new ArrayList<>();
                        try {
                            jsonArray = new JSONArray(response);
                            areaFlag=true;
                            JSONObject jsonObject=jsonArray.getJSONObject(0);

                            for (int i=jsonArray.length();i>0;i--) {
                                jsonObject = jsonArray.getJSONObject(i-1);
                                float myDistance=0;
                                Area area=new Area();
                                area.setId(jsonObject.getInt("aid"));
                                area.setAename(jsonObject.getString("aename"));
                                area.setAfname(jsonObject.getString("afname"));
                                cityNames.add(jsonObject.getString("afname"));
                                area.setAlat(Float.valueOf(jsonObject.getString("alat")));
                                area.setAlng(Float.valueOf(jsonObject.getString("alng")));
//                                area.setAdiameter(jsonObject.getString("adiameter"));
                                area.setServer(jsonObject.getString("server"));
                                area.setZoom(jsonObject.getInt("azoom"));
                                area.setPic(jsonObject.getString("pic"));
                                area.setDescription(jsonObject.getString("memo"));

                                areaArrayList.add(area);
                            }
                            ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<String>
                                    (SignupActivity.this, android.R.layout.simple_spinner_item,
                                            cityNames); //selected item will look like a spinner set from XML
                            spinnerArrayAdapter.setDropDownViewResource(android.R.layout
                                    .simple_spinner_dropdown_item);
                            spCity.setAdapter(spinnerArrayAdapter);
                            server=areaArrayList.get(0).getServer();
                            ctName=areaArrayList.get(0).getAfname();


                            findArea();
                        } catch (JSONException e) {
                            e.printStackTrace();

                        }

                    }
                },
                new Response.ErrorListener()
                {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // TODO Auto-generated method stub
                        Log.d("ERROR","error => "+error.toString());
//                        txtczstatus.setText(error.toString()+"مشکلی در ارسال داده پیش آمده دوباره تلاش کنید");
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


    private void populateGPS() {
        if (!mayRequestLocation()) {
            return;
        }
        gps =new GPSTracker(this);
        Log.d(TAG, "onCreate: GPS FROM populate gps_before Check:"+String.valueOf(gps.getLatitude())+" : "+String.valueOf(gps.getLongitude()));
        String upStatus="";
        if (gps.getLatitude()!=0.0) {
            SharedPreferences.Editor SP = PreferenceManager.getDefaultSharedPreferences(getBaseContext()).edit();
            SP.putString("lat", String.valueOf(gps.getLatitude()));
            SP.putString("lng", String.valueOf(gps.getLongitude()));
            SP.apply();

            Log.d(TAG, "onCreate: GPS FROM populate gps_ok:"+String.valueOf(gps.getLatitude())+" : "+String.valueOf(gps.getLongitude()));
            myLocation.setLatitude(gps.getLatitude());
            myLocation.setLongitude(gps.getLongitude());
            upStatus="gps_ok";

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
            Snackbar.make(spCity, "لطفا دسترسی به جی پی اس را فعال کنید.", Snackbar.LENGTH_INDEFINITE)
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

    public void findArea(){
        List<Float> distances=new ArrayList<Float>();
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
        spCity.setSelection(minIndex);
    }

}
