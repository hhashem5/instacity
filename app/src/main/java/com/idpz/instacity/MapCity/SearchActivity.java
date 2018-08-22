package com.idpz.instacity.MapCity;

import android.annotation.TargetApi;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.Typeface;
import android.location.Location;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.SeekBar;
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
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.idpz.instacity.AlarmService;
import com.idpz.instacity.R;
import com.idpz.instacity.models.Car;
import com.idpz.instacity.models.Rcvsrv;
import com.idpz.instacity.models.Shop;
import com.idpz.instacity.utils.BottomNavigationViewHelper;
import com.idpz.instacity.utils.GPSTracker;
import com.ittianyu.bottomnavigationviewex.BottomNavigationViewEx;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static android.Manifest.permission.ACCESS_COARSE_LOCATION;
import static android.Manifest.permission.ACCESS_FINE_LOCATION;

public class SearchActivity extends AppCompatActivity implements OnMapReadyCallback {

    private static final String TAG = "SearchActivity";
    private static final int ACTIVITY_NUM=1;
    private static final String TAG_SUCCESS = "success";
    private static final String TAG_MESSAGE = "msg";
    private GoogleMap mMap;
    ArrayList<Shop> shops,showShops;
    ArrayList<Rcvsrv> nodes;
    ArrayList<Car> cars;
    SeekBar seekBar;
    LatLng    cityLatLng;
    Float lat=35.711f,lng=50.912f,myDist;
    int speed=20,alarmDist=100;
    Location myLocation,carLocation;
    String SHOP_URL="",dbLastData="mscity.ir",mySnipet="موقیت",myTitle="خانه";
    Boolean mapFlag=false,placesFlag=false,reqPlaces=false,connected=false;
    GPSTracker gps;
    private Context context;
    private static final int REQUEST_ACCESS_LOCATION = 0;
    CheckBox chkTrafic,chkSat;
    CheckBox chkCars,chkCarAlarm;
    Spinner spnCarSelect;
    ProgressDialog pDialog;
    Boolean Icar=false,driverFlag=false,remain=true,rcvFlag=false;
    TextView txtcat,txtDistance,txtMydist;

    String state="",REGISTER_URL="",CarDrv_url="",wantCode="0";
    Double homelat,homelng;
    ArrayList<String> carlist=new ArrayList<String>();
    Car mycar;
    SharedPreferences SP1;
    ArrayAdapter<String> spinnerArrayAdapter;
    int failCount=0,mapCount;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        Log.d(TAG, "onCreate: strating");

        myLocation=new Location("");
        carLocation=new Location("");
        Typeface yekan = Typeface.createFromAsset(SearchActivity.this.getAssets(), "fonts/YEKAN.TTF");

        SP1 = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
        state=SP1.getString("state", "0");
        lat=Float.valueOf(SP1.getString("lat", "0"));
        lng=Float.valueOf(SP1.getString("lng", "0"));
        homelat=Double.valueOf(SP1.getString("homelat", "0"));
        homelng=Double.valueOf(SP1.getString("homelng", "0"));
        alarmDist=Integer.valueOf(SP1.getString("alarm_len","300"));
        mapCount=SP1.getInt("map_count",0);
        mapCount++;
        mapOpenCount();
            myLocation.setLatitude(lat);
            myLocation.setLongitude(lng);
        if (myLocation.getLatitude()==0){
            myLocation.setLatitude(homelat);
            myLocation.setLongitude(homelng);
        }



        SHOP_URL=getString(R.string.server)+"/j/shoprec.php";
        REGISTER_URL=getString(R.string.server)+"/j/rcv.php";
        CarDrv_url = getString(R.string.server)+"/j/d.php";
        shops=new ArrayList<>();
        nodes=new ArrayList<>();
        cars=new ArrayList<>();
        showShops=new ArrayList<>();
        setupBottomNavigationView();


        if (mapCount<5) {
            final ImageView imgRoute=(ImageView) findViewById(R.id.imgRoute);
            imgRoute.setVisibility(View.VISIBLE);
//            tvNotice.setTypeface(yekan);
            new Handler().postDelayed(new Runnable() {

                @Override
                public void run() {
//                    tvNotice.setVisibility(View.GONE);
                    imgRoute.setVisibility(View.GONE);
                }
            }, 5000);



        }
        seekBar=(SeekBar) findViewById(R.id.sbarCarDistance);
        chkCarAlarm=(CheckBox) findViewById(R.id.chkCarAlarm);
        chkCarAlarm.setChecked(SP1.getBoolean("alarm",false));
        spnCarSelect=(Spinner) findViewById(R.id.spnCars);
        chkSat=(CheckBox) findViewById(R.id.chkShowSat);
        chkTrafic=(CheckBox) findViewById(R.id.chkShowTraffic);

        chkCars=(CheckBox) findViewById(R.id.chkShowCar);
        txtcat=(TextView) findViewById(R.id.txtCat);
        txtDistance=(TextView) findViewById(R.id.txtCarDistance);
        txtMydist=(TextView) findViewById(R.id.txtMyDistance);


        seekBar.setProgress(alarmDist);
        txtDistance.setText(SP1.getString("alarm_len","0")+"متر");
//        chkPlaces.setText("دریافت اماکن");
//        chkCars.setText("دریافت خودروها");

        txtcat.setVisibility(View.GONE);

        chkCars.setEnabled(false);
        seekBar.setVisibility(View.GONE);
        spnCarSelect.setVisibility(View.GONE);
        chkCarAlarm.setVisibility(View.GONE);
        txtDistance.setVisibility(View.GONE);
        txtMydist.setVisibility(View.GONE);
        SHOP_URL=getString(R.string.server)+"/j/shoprec.php";
        chkTrafic.setEnabled(false);
        chkTrafic.setChecked(false);
        chkSat.setEnabled(false);

        spinnerArrayAdapter = new ArrayAdapter<String>
                (SearchActivity.this, android.R.layout.simple_spinner_item,
                        carlist); //selected item will look like a spinner set from XML
        spinnerArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spnCarSelect.setAdapter(spinnerArrayAdapter);

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                txtDistance.setText(progress+"متر");
                alarmDist=progress;
                mMap.clear();
                CircleOptions circleOptions=new CircleOptions().center(new LatLng(myLocation.getLatitude(),myLocation.getLongitude()))
                        .radius(alarmDist)
                        .strokeColor(Color.BLUE)
                        .fillColor(0x30ff0000)
                        .strokeWidth(2);

                mMap.addCircle(circleOptions);
                mMap.addMarker(new MarkerOptions().position(new LatLng(myLocation.getLatitude(),myLocation.getLongitude())).title("خانه"));
                SharedPreferences.Editor SP;
                SP = PreferenceManager.getDefaultSharedPreferences(getBaseContext()).edit();

                SP.putString("alarm_len",String.valueOf(progress));
                SP.apply();
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                LatLng homeLoc=new LatLng(myLocation.getLatitude(),myLocation.getLongitude());
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(homeLoc,15));
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });


        chkCars.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    if (remain)rcvRequest();
                    Toast.makeText(SearchActivity.this, "دریافت اطلاعات خودرو. کمی صبر کنید", Toast.LENGTH_SHORT).show();

                    seekBar.setVisibility(View.VISIBLE);
                    spnCarSelect.setVisibility(View.VISIBLE);
                    chkCarAlarm.setVisibility(View.VISIBLE);
                    txtDistance.setVisibility(View.VISIBLE);
                    txtMydist.setVisibility(View.VISIBLE);
                    Icar=true;

                }else {
                    seekBar.setVisibility(View.GONE);
                    spnCarSelect.setVisibility(View.GONE);
                    chkCarAlarm.setVisibility(View.GONE);
                    txtDistance.setVisibility(View.GONE);
                    txtMydist.setVisibility(View.GONE);
                    Icar=false;
                }
            }
        });

        chkCarAlarm.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                if (!isChecked){
                    stopService(new Intent(SearchActivity.this, AlarmService.class));
                    SharedPreferences.Editor SP;
                    SP = PreferenceManager.getDefaultSharedPreferences(getBaseContext()).edit();
                    SP.putBoolean("alarm",false);
                    SP.apply();
//                    startService(new Intent(SearchActivity.this, AlarmService.class));
                }else {
                    stopService(new Intent(SearchActivity.this, AlarmService.class));
                    SharedPreferences.Editor SP;
                    SP = PreferenceManager.getDefaultSharedPreferences(getBaseContext()).edit();
                    SP.putBoolean("alarm",true);
                    SP.apply();
                    startService(new Intent(SearchActivity.this, AlarmService.class));
                }
                drawMap();

            }
        });

        spnCarSelect.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (driverFlag) {

                    mycar = cars.get(position);
                    rcvRequest();
                    SharedPreferences.Editor SP;
                    SP = PreferenceManager.getDefaultSharedPreferences(getBaseContext()).edit();
                    SP.putString("carcode",String.valueOf(mycar.getCode()));
                    SP.apply();
//                    new AttemptJson ().execute();
                    failCount=0;
                    remain=true;
                    refreshRcv();

                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });



        new Thread() {
            @Override
            public void run() {
                while (!driverFlag&&failCount<2) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {

                            ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
                            if (connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState() == NetworkInfo.State.CONNECTED ||
                                    connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState() == NetworkInfo.State.CONNECTED) {
                                //we are connected to a network
                                connected = true;
                            } else {
                                connected = false;
                                failCount++;
                            }
                            if (connected ){

                                if(!driverFlag){
                                    reqcdrv();
                                    chkTrafic.setEnabled(true);
                                    chkSat.setEnabled(true);

                                }

                            }

                        }
                    });
                    try {
                        sleep(9000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }.start();

        if (connected){
            chkTrafic.setEnabled(true);
            chkSat.setEnabled(true);

        }
        Intent intent = getIntent();
        if(intent.hasExtra("lat")) {
            remain=false;

            chkTrafic.setEnabled(true);
            chkSat.setEnabled(true);
            homelat = Double.valueOf(intent.getStringExtra("lat"));
            homelng = Double.valueOf(intent.getStringExtra("lng"));
            myTitle = intent.getStringExtra("name");
            mySnipet = intent.getStringExtra("memo");
            Log.d(TAG, "onCreate: lat:"+homelat+" lng:"+homelng+" incoming  lat:"+intent.getStringExtra("lat"));
        }

        try {
            // Loading map
            initilizeMap();

        } catch (Exception e) {
            e.printStackTrace();
        }





        chkTrafic.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked){
                    mMap.setTrafficEnabled(true);
                }else {
                    mMap.setTrafficEnabled(false);
                }


            }
        });

        chkSat.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    mMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
                }else {
                    mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
                }
            }
        });




        float tlat=Float.valueOf(SP1.getString("lat", "0"));
        float tlng=Float.valueOf(SP1.getString("lng", "0"));
        if(tlat==0) {
            myLocation.setLatitude(lat);
            myLocation.setLongitude(lng);
        }else if(lat==0){
            populateGPS();
        }
    }

    private void refreshRcv(){

        new Thread() {
            @Override
            public void run() {
                while (!driverFlag&&failCount<2&&remain) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {

                            ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
                            if (connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState() == NetworkInfo.State.CONNECTED ||
                                    connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState() == NetworkInfo.State.CONNECTED) {
                                //we are connected to a network
                                connected = true;
                                if (driverFlag&&Icar){
                                    rcvRequest();
                                    drawMap();
//                                    Toast.makeText(SearchActivity.this, "درخواست شد", Toast.LENGTH_SHORT).show();
                                }
                            } else {
                                connected = false;
                                failCount++;
                            }

                        }
                    });
                    try {
                        sleep(5000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }.start();
    }


        public void onResetMap(View v){


    }


    // تنظیم نوار پایین برنامه
    private void setupBottomNavigationView(){
        Log.d(TAG,"seting up bottom navigation view");
        BottomNavigationViewEx bottomNavigationViewEx=(BottomNavigationViewEx) findViewById(R.id.bottomNavViewBar);
        BottomNavigationViewHelper.setupBottomNavigationView(bottomNavigationViewEx);
        BottomNavigationViewHelper.enableNavigation(SearchActivity.this,bottomNavigationViewEx);
        Menu menu=bottomNavigationViewEx.getMenu();
        MenuItem menuItem=menu.getItem(ACTIVITY_NUM);
        menuItem.setChecked(true);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {

        Intent intent = getIntent();
        if(intent.hasExtra("lat")) {
            remain=false;

            chkTrafic.setEnabled(true);
            chkSat.setEnabled(true);
            homelat = Double.valueOf(intent.getStringExtra("lat"));
            homelng = Double.valueOf(intent.getStringExtra("lng"));
            myTitle = intent.getStringExtra("name");
            mySnipet = intent.getStringExtra("memo");
            Log.d(TAG, "onCreate: lat:"+homelat+" lng:"+homelng+" incoming  lat:"+intent.getStringExtra("lat"));
        }

        mMap = googleMap;

        // Add a marker in Sydney, Australia, and move the camera.
            cityLatLng = new LatLng(homelat, homelng);

        mMap.addMarker(new MarkerOptions().position(cityLatLng).title(myTitle).snippet(mySnipet));

        mMap.setTrafficEnabled(false);

        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(cityLatLng,15));

    }

    private void initilizeMap() {

        if (mMap == null) {
            SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                    .findFragmentById(R.id.map);
            mapFragment.getMapAsync(this);
            // check if map is created successfully or not
            if (mMap == null) {
//                Toast.makeText(getApplicationContext(),
//                        "لطفا کمی صبر کنید...", Toast.LENGTH_SHORT)
//                        .show();
            }
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(homelat, homelng), 17));
            mapFlag=true;
            Log.d(TAG, "initilizeMap: "+homelat+" "+homelng);
        }
    }




    private void populateGPS() {
        if (!mayRequestLocation()) {
            return;
        }
        gps =new GPSTracker(this);
        SharedPreferences.Editor SP = PreferenceManager.getDefaultSharedPreferences(getBaseContext()).edit();
        SP.putString("lat", String.valueOf(gps.getLatitude()));
        SP.putString("lng",String.valueOf(gps.getLongitude()));
        SP.apply();
        myLocation.setLatitude(gps.getLatitude());
        myLocation.setLongitude(gps.getLongitude());
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
            Snackbar.make(chkSat, "لطفا دسترسی به جی پی اس را فعال کنید.", Snackbar.LENGTH_INDEFINITE)
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

    private void drawMap() {
        mMap.clear();
        MarkerOptions markerOp = new MarkerOptions();


            if (Icar){

                chkCarAlarm.setChecked(SP1.getBoolean("alarm",false));
                LatLng carloc=new LatLng(0.0,0.0);

                PolylineOptions polyLines=new PolylineOptions()
                        .color(Color.GREEN)
                        .width(5);
                for (Rcvsrv nod:nodes) {

                    if (Integer.valueOf(mycar.getCode()) == nod.getId()) {
                        if (!(Float.valueOf(nod.getLat())==0)) {
                            if (Float.valueOf(nod.getLat())<1){

                                Float lt,lg;
                                lt=Float.valueOf(SP1.getString("homelat", "0"));
                                lg=Float.valueOf(SP1.getString("homelng", "0"));
                                carloc = new LatLng(lt,lg );
                                carLocation.setLatitude(lt);
                                carLocation.setLongitude(lg);
                                markerOp
                                        .position(new LatLng(lt, lg))
                                        .title(mycar.getFname()+" "+mycar.getDriver()+" "+mycar.getPelak())
                                        .snippet("سرعت: "+nod.getCode()+"km")
                                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.garbagepin));
                            }else {
                                carLocation.setLatitude(Float.valueOf(nod.getLat()));
                                carLocation.setLongitude(Float.valueOf(nod.getLng()));
                                carloc = new LatLng(Float.valueOf(nod.getLat()), Float.valueOf(nod.getLng()));
                                markerOp
                                        .position(new LatLng(Float.valueOf(nod.getLat()),Float.valueOf(nod.getLng())))
                                        .title(mycar.getFname()+" "+mycar.getDriver()+" "+mycar.getPelak())
                                        .snippet("سرعت: "+nod.getCode()+"km")
                                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.garbagepin));
                            }


                            polyLines.add(new LatLng(Float.valueOf(nod.getLat()), Float.valueOf(nod.getLng())));
                        }
                    }


                }
                if (nodes.size()>0){
                    mMap.addMarker(markerOp);
                    mMap.addPolyline(polyLines);
                    mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(carloc, 17));
                    txtMydist.setText("فاصله خودرو:"+Math.round(myLocation.distanceTo(carLocation))+"متر");
                    txtDistance.setText(SP1.getString("alarm_len","0")+"متر");
                    CircleOptions circleOptions=new CircleOptions().center(new LatLng(myLocation.getLatitude(),myLocation.getLongitude()))
                            .radius(alarmDist)
                            .strokeColor(Color.BLUE)
                            .fillColor(0x30ff0000)
                            .strokeWidth(2);

                    mMap.addCircle(circleOptions);
                }

            }

    }


    public void rcvRequest() {
        RequestQueue queue = Volley.newRequestQueue(this);
        String url = REGISTER_URL;
        StringRequest postRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>()
                {
                    @Override
                    public void onResponse(String response) {
//                        Toast.makeText(SearchActivity.this, "دریافت شد", Toast.LENGTH_SHORT).show();
                        Log.d(TAG, "onResponse: car code="+response);
                        rcvFlag=true;
                        failCount=0;
                        nodes.clear();
                        JSONArray jsonArray= null;
                        try {
                            jsonArray = new JSONArray(response);

                            JSONObject jsonObject;

                            for (int i=jsonArray.length();i>0;i--) {
                                jsonObject = jsonArray.getJSONObject(i-1);
                                Rcvsrv rcvsrv=new Rcvsrv();
                                rcvsrv.setId(jsonObject.getInt("rcode"));
                                rcvsrv.setLat(jsonObject.getString("rlat"));
                                rcvsrv.setLng(jsonObject.getString("rlng"));
                                rcvsrv.setCode(jsonObject.getString("speed"));
                                nodes.add(rcvsrv);
                            }

                            drawMap();

                        } catch (JSONException e) {
                            e.printStackTrace();
                            failCount++;
                        }
//                    Toast.makeText(MapActivity.this, "فاصله:"+myDist+" تعداد:"+db.getnumofcol(), Toast.LENGTH_LONG).show();


                    }
                },
                new Response.ErrorListener()
                {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        failCount++;
                        Log.d("ERROR","error => "+error.toString());
                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String>params = new HashMap<String,String>();
                params.put("code",mycar.getCode());
                params.put("limit","30");
                params.put("state",state);
                return params;
            }
        };
        queue.add(postRequest);

    }

    public void reqcdrv() {
//        Toast.makeText(MainActivity.this, " reqcdrv", Toast.LENGTH_SHORT).show();
        RequestQueue queue = Volley.newRequestQueue(this);
        String url = CarDrv_url;
        StringRequest postRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>()
                {
                    @Override
                    public void onResponse(String response) {
                        Log.d(TAG, "onResponse: "+response);
                        driverFlag=true;
                        cars.clear();
                        failCount=0;
                        carlist.clear();
                        JSONArray jsonArray= null;
                        try {
                            jsonArray = new JSONArray(response);

                            JSONObject jsonObject=jsonArray.getJSONObject(0);

                            for (int i=0 ;i<jsonArray.length();i++) {
                                jsonObject=jsonArray.getJSONObject(i);
                                Car car=new Car(
                                        jsonObject.getString("ccod"),
                                        jsonObject.getString("fname"),
                                        jsonObject.getString("cplk"),
                                        jsonObject.getString("drv"));

                                carlist.add(car.getFname());
                                cars.add(car);
                            }

                            spinnerArrayAdapter.notifyDataSetChanged();


                            chkCars.setText("خودروهای شهری");
                            chkCars.setEnabled(true);
                            chkCars.setChecked(true);

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
                        Log.d("ERROR","error => "+error.toString());
//                        Toast.makeText(MainActivity.this, "Error reqcdrv", Toast.LENGTH_SHORT).show();
                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String>params = new HashMap<String,String>();
//                params.put("area","0");
            params.put("state",state);
                return params;
            }
        };
        queue.add(postRequest);

    }

    @Override
    public void onStart() {
        super.onStart();
        Log.d(TAG, "onStart: شروع شد");
        driverFlag=false;
        nodes.clear();
    }

    public void mapOpenCount() {

        if (mapCount<7) {
            SharedPreferences.Editor SP = PreferenceManager.getDefaultSharedPreferences(SearchActivity.this).edit();
            SP.putInt("map_count", mapCount);
            SP.apply();
        }
    }

}
