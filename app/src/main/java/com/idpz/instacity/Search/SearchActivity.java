package com.idpz.instacity.Search;

import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
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
    private GoogleMap mMap;
    ArrayList<Shop> shops,showShops;
    ArrayList<Rcvsrv> nodes;
    ArrayList<Car> cars;
    SeekBar seekBar;
    LatLng    cityLatLng;
    Float lat=35.711f,lng=50.912f,myDist;
    int speed=20,alarmDist=100;
    Location myLocation,carLocation;
    String SHOP_URL="",dbLastData="mscity.ir";
    Boolean mapFlag=false,placesFlag=false,connected=false;
    GPSTracker gps;
    private Context context;
    private static final int REQUEST_ACCESS_LOCATION = 0;
    CheckBox chkTrafic,chkPlaces,chkSat;
    CheckBox chkIEdu,chkIShop,chkISport,chkIService,chkIRepaire,chkIHealth,chkIReligion,chkIFood,chkCars,
    chkCarAlarm;
    Spinner spnCarSelect;

    Boolean Iedu=true,Ishop=false,Isport=false,Iservice=false,Irepair=false,Ihealth=false,Ireligion=false,Ifood=false
            ,Icar=false,driverFlag=false,remain=true,rcvFlag=false;
    TextView txtcat,txtDistance,txtMydist;
    Button btnRegStore;
    String server="",REGISTER_URL="",CarDrv_url="",wantCode="0";
    Double homelat,homelng;
    ArrayList<String> carlist=new ArrayList<String>();
    Car mycar;
    SharedPreferences SP1;
    ArrayAdapter<String> spinnerArrayAdapter;
    int failCount=0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        Log.d(TAG, "onCreate: strating");

        myLocation=new Location("");
        carLocation=new Location("");

        SP1 = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
        server=SP1.getString("server", "0");
        lat=Float.valueOf(SP1.getString("lat", "0"));
        lng=Float.valueOf(SP1.getString("lng", "0"));
        homelat=Double.valueOf(SP1.getString("homelat", "0"));
        homelng=Double.valueOf(SP1.getString("homelng", "0"));
        alarmDist=Integer.valueOf(SP1.getString("alarm_len","300"));

            myLocation.setLatitude(lat);
            myLocation.setLongitude(lng);
        if (myLocation.getLatitude()==0){
            myLocation.setLatitude(homelat);
            myLocation.setLongitude(homelng);
        }
        Log.d(TAG, "onCreate: locations lat&lat:"+lat+","+lng+" homlat:"+homelat+","+homelng+"mylocation="+myLocation.getLatitude());
        SHOP_URL=server+"/i/shoprec.php";
        REGISTER_URL=server+"/i/rcv.php";
        CarDrv_url = server+"/i/d.php";
        shops=new ArrayList<>();
        nodes=new ArrayList<>();
        cars=new ArrayList<>();
        showShops=new ArrayList<>();
        setupBottomNavigationView();

        seekBar=(SeekBar)findViewById(R.id.sbarCarDistance);
        chkCarAlarm=(CheckBox)findViewById(R.id.chkCarAlarm);
        chkCarAlarm.setChecked(SP1.getBoolean("alarm",false));
        spnCarSelect=(Spinner)findViewById(R.id.spnCars);
        chkSat=(CheckBox)findViewById(R.id.chkShowSat);
        chkPlaces=(CheckBox)findViewById(R.id.chkShowPlaces);
        chkTrafic=(CheckBox)findViewById(R.id.chkShowTraffic);
        chkIEdu=(CheckBox)findViewById(R.id.chkIEdu);
        chkIShop=(CheckBox)findViewById(R.id.chkIShop);
        chkISport=(CheckBox)findViewById(R.id.chkISport);
        chkIService=(CheckBox)findViewById(R.id.chkIService);
//        chkIRepaire=(CheckBox)findViewById(R.id.chkIRepair);
        chkIHealth=(CheckBox)findViewById(R.id.chkIHealth);
        chkIReligion=(CheckBox)findViewById(R.id.chkIReligion);
        chkIFood=(CheckBox)findViewById(R.id.chkIfood);
        chkCars=(CheckBox)findViewById(R.id.chkShowCar);
        txtcat=(TextView) findViewById(R.id.txtCat);
        txtDistance=(TextView) findViewById(R.id.txtCarDistance);
        txtMydist=(TextView) findViewById(R.id.txtMyDistance);
        btnRegStore=(Button)findViewById(R.id.btnMapStoreReg);

        seekBar.setProgress(alarmDist);
        txtDistance.setText(SP1.getString("alarm_len","0")+"متر");
        chkPlaces.setText("دریافت اماکن");
        chkCars.setText("دریافت خودروها");
        chkIEdu.setVisibility(View.GONE);
        chkIShop.setVisibility(View.GONE);
        chkISport.setVisibility(View.GONE);
        chkIService.setVisibility(View.GONE);
//        chkIRepaire.setVisibility(View.GONE);
        chkIHealth.setVisibility(View.GONE);
        chkIReligion.setVisibility(View.GONE);
        chkIFood.setVisibility(View.GONE);
        txtcat.setVisibility(View.GONE);
        chkPlaces.setEnabled(false);
        chkCars.setEnabled(false);
        seekBar.setVisibility(View.GONE);
        spnCarSelect.setVisibility(View.GONE);
        chkCarAlarm.setVisibility(View.GONE);
        txtDistance.setVisibility(View.GONE);
        txtMydist.setVisibility(View.GONE);
        SHOP_URL=server+"/i/shoprec.php";
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
                SP.commit();
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
                    chkPlaces.setChecked(false);
                    seekBar.setVisibility(View.VISIBLE);
                    spnCarSelect.setVisibility(View.VISIBLE);
                    chkCarAlarm.setVisibility(View.VISIBLE);
                    txtDistance.setVisibility(View.VISIBLE);
                    txtMydist.setVisibility(View.VISIBLE);
                    Iedu=false;Ishop=false;Isport=false;Iservice=false;Irepair=false;Ihealth=false;Ireligion=false;Ifood=false;
                    chkIEdu.setChecked(false);
                    chkIShop.setChecked(false);
                    chkISport.setChecked(false);
                    chkIService.setChecked(false);
                    chkIHealth.setChecked(false);
                    chkIReligion.setChecked(false);
                    chkIFood.setChecked(false);
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
                    SharedPreferences.Editor SP;
                    SP = PreferenceManager.getDefaultSharedPreferences(getBaseContext()).edit();
                    SP.putBoolean("alarm",false);
                    SP.commit();
                }else {
                    SharedPreferences.Editor SP;
                    SP = PreferenceManager.getDefaultSharedPreferences(getBaseContext()).edit();
                    SP.putBoolean("alarm",true);
                    SP.commit();
                }
                drawMap();

            }
        });

        spnCarSelect.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (driverFlag) {

                    mycar = cars.get(position);
                    nodes.clear();

                    SharedPreferences.Editor SP;
                    SP = PreferenceManager.getDefaultSharedPreferences(getBaseContext()).edit();
                    SP.putString("carcode",String.valueOf(mycar.getCode()));
                    SP.commit();

                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        new Thread() {
            @Override
            public void run() {
                while (remain) {
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
                            }
                            if (connected ){
                                if (driverFlag&&Icar){
                                    rcvRequest();
                                }
                                if(!driverFlag){
                                    reqcdrv();
                                    chkTrafic.setEnabled(true);
                                    chkSat.setEnabled(true);
                                }
                                if(connected&&failCount>3){
                                    AlertDialog alertDialog = new AlertDialog.Builder(SearchActivity.this).create();
                                    alertDialog.setTitle("اینترنت وصل نیست!");
                                    alertDialog.setMessage("لطفا از اتصال اینترنت مطمئن شوید!");
                                    alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "خروج",
                                            new DialogInterface.OnClickListener() {
                                                public void onClick(DialogInterface dialog, int which) {
                                                    remain=false;
                                                    finish();
                                                    dialog.dismiss();
                                                }
                                            });
                                    alertDialog.show();
                                }

                                if (!placesFlag) reqShop();


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


        btnRegStore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent in2=new Intent(SearchActivity.this,StoreRegActivity.class);
                startActivity(in2);
            }
        });

        chkISport.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (!isChecked){
                    Isport=false;
                }else {
                    Isport=true;
                }
                drawMap();

            }
        });
        chkIService.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (!isChecked){
                   Iservice=false;
                }else {
                   Iservice=true;
                }
                drawMap();

            }
        });

        chkIShop.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (!isChecked){
                    Ishop=false;
                }else {
                    Ishop=true;
                }

                drawMap();
            }
        });
//        chkIRepaire.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
//            @Override
//            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
//                if (!isChecked){
//                    Irepair=false;
//                }else {
//                    Irepair=true;
//                }
//
//                drawMap();
//            }
//        });
        chkIReligion.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (!isChecked){
                    Ireligion=false;
                }else {
                    Ireligion=true;
                }
                drawMap();

            }
        });
        chkIHealth.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (!isChecked){
                    Ihealth=false;
                }else {
                    Ihealth=true;
                }
                drawMap();

            }
        });
        chkIEdu.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (!isChecked){
                    Iedu=false;
                }else {
                    Iedu=true;
                }
                drawMap();

            }
        });
        chkIFood.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (!isChecked){
                    Ifood=false;
                }else {
                    Ifood=true;
                }
                drawMap();

                }
            });


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

        chkPlaces.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    chkCars.setChecked(false);
                    Icar=false;
                    chkIEdu.setVisibility(View.VISIBLE);
                    chkIShop.setVisibility(View.VISIBLE);
                    chkISport.setVisibility(View.VISIBLE);
                    chkIService.setVisibility(View.VISIBLE);
//                    chkIRepaire.setVisibility(View.VISIBLE);
                    chkIHealth.setVisibility(View.VISIBLE);
                    chkIReligion.setVisibility(View.VISIBLE);
                    chkIFood.setVisibility(View.VISIBLE);
                    txtcat.setVisibility(View.VISIBLE);
                    placesFlag=true;
                    Toast.makeText(SearchActivity.this,shops.size()+ "نمایش فروشگاه ها", Toast.LENGTH_SHORT).show();
                    drawMap();
//                        Log.d(TAG, "onCheckedChanged: lat lng"+Double.valueOf(mshop.getJlat()));



                }else {
                    placesFlag=false;
                    mMap.clear();
                    chkIEdu.setVisibility(View.GONE);
                    chkIShop.setVisibility(View.GONE);
                    chkISport.setVisibility(View.GONE);
                    chkIService.setVisibility(View.GONE);
//                    chkIRepaire.setVisibility(View.GONE);
                    chkIHealth.setVisibility(View.GONE);
                    chkIReligion.setVisibility(View.GONE);
                    chkIFood.setVisibility(View.GONE);
                    txtcat.setVisibility(View.GONE);
                    txtDistance.setVisibility(View.GONE);
                    txtMydist.setVisibility(View.GONE);
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
        try {
            // Loading map
            initilizeMap();

        } catch (Exception e) {
            e.printStackTrace();
        }




    }

        public void onResetMap(View v){


    }



    // تنظیم نوار پایین برنامه
    private void setupBottomNavigationView(){
        Log.d(TAG,"seting up bottom navigation view");
        BottomNavigationViewEx bottomNavigationViewEx=(BottomNavigationViewEx)findViewById(R.id.bottomNavViewBar);
        BottomNavigationViewHelper.setupBottomNavigationView(bottomNavigationViewEx);
        BottomNavigationViewHelper.enableNavigation(SearchActivity.this,bottomNavigationViewEx);
        Menu menu=bottomNavigationViewEx.getMenu();
        MenuItem menuItem=menu.getItem(ACTIVITY_NUM);
        menuItem.setChecked(true);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {

        mMap = googleMap;

        // Add a marker in Sydney, Australia, and move the camera.
            cityLatLng = new LatLng(homelat, homelng);

        mMap.addMarker(new MarkerOptions().position(cityLatLng).title("موقعیت شما"));

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
                Toast.makeText(getApplicationContext(),
                        "لطفا کمی صبر کنید...", Toast.LENGTH_SHORT)
                        .show();
            }
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(homelat, homelng), 17));
            mapFlag=true;
            Log.d(TAG, "initilizeMap: "+homelat+" "+homelng);
        }
    }


    public void reqShop() {
        RequestQueue queue = Volley.newRequestQueue(this);
        String url = SHOP_URL;
        StringRequest postRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>()
                {
                    @Override
                    public void onResponse(String response) {
                        JSONArray jsonArray= null;
                        placesFlag=true;
                        failCount=0;
                        try {
                            Shop shop=new Shop();
                            jsonArray = new JSONArray(response);

                            JSONObject jsonObject;
//                        dbSocialHandler.removeAll();

                            for (int i=0 ;i<jsonArray.length();i++) {
                                jsonObject=jsonArray.getJSONObject(i);
                                shop=new Shop();
                                shop.setId(jsonObject.getInt("id"));
                                shop.setName(jsonObject.getString("name"));
                                shop.setOwner(jsonObject.getString("owner")) ;
                                shop.setTel (jsonObject.getString("tel"));
                                shop.setMobile (jsonObject.getString("mobile")) ;
                                shop.setAddress ( jsonObject.getString("address"));
                                shop.setJlat (jsonObject.getString("jlat"));
                                shop.setJlng (jsonObject.getString("jlng"));
                                shop.setTag(jsonObject.getString("tag"));
                                shop.setJkey (jsonObject.getString("jkey"));
                                shop.setPic(jsonObject.getString("pic"));
//                                if (value.equals(tag)) {
//                                    shops.add(new Shop(id, name, owner,
//                                            tel, mobile, "", address, jlat, jlng, tag, jkey, "", ""));
//                                    showShops.add(new Shop(id, name, owner,
//                                            tel, mobile, "", address, jlat, jlng, tag, jkey, "", ""));
//                                }
//                                Log.d(TAG, "onResponse: receive lat"+shop.getJlat());
                                shops.add(shop);


                            }
//                            adapter.notifyDataSetChanged();
                            chkPlaces.setText("نمایش اماکن");
                            chkPlaces.setEnabled(true);

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
                        // TODO Auto-generated method stub

                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String>params = new HashMap<String,String>();
                params.put("id","0");
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
        SharedPreferences.Editor SP = PreferenceManager.getDefaultSharedPreferences(getBaseContext()).edit();
        SP.putString("lat", String.valueOf(gps.getLatitude()));
        SP.putString("lng",String.valueOf(gps.getLongitude()));
        SP.commit();
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

        for (final Shop mshop : shops) {

            LatLng node = new LatLng(Double.valueOf(mshop.getJlat()), Double.valueOf(mshop.getJlng()));

            switch (mshop.getTag()) {
                case "store":
                    if (Ishop) {
                        markerOp
                                .position(node)
                                .title(mshop.getName() + " (" + mshop.getOwner() + ")")
                                .snippet(mshop.getMobile() + " - " + mshop.getTel())
                                .icon(BitmapDescriptorFactory.fromResource(R.drawable.ishop));
                        mMap.addMarker(markerOp);
                    }
                    break;
                case "food":
                    if (Ifood) {
                        markerOp
                                .position(node)
                                .title(mshop.getName() + " (" + mshop.getOwner() + ")")
                                .snippet(mshop.getMobile() + " - " + mshop.getTel())
                                .icon(BitmapDescriptorFactory.fromResource(R.drawable.ifood));
                        mMap.addMarker(markerOp);
                    }
                    break;
                case "health":
                    if (Ihealth) {
                        markerOp
                                .position(node)
                                .title(mshop.getName() + " (" + mshop.getOwner() + ")")
                                .snippet(mshop.getMobile() + " - " + mshop.getTel())
                                .icon(BitmapDescriptorFactory.fromResource(R.drawable.ihealth));
                        mMap.addMarker(markerOp);
                    }
                    break;
                case "services":
                    if (Iservice) {
                        markerOp
                                .position(node)
                                .title(mshop.getName() + " (" + mshop.getOwner() + ")")
                                .snippet(mshop.getMobile() + " - " + mshop.getTel())
                                .icon(BitmapDescriptorFactory.fromResource(R.drawable.iservices));
                        mMap.addMarker(markerOp);
                    }
                    break;
                case "sport":
                    if (Isport) {
                        markerOp
                                .position(node)
                                .title(mshop.getName() + " (" + mshop.getOwner() + ")")
                                .snippet(mshop.getMobile() + " - " + mshop.getTel())
                                .icon(BitmapDescriptorFactory.fromResource(R.drawable.isport));
                        mMap.addMarker(markerOp);
                    }
                    break;
                case "religion":
                    if (Ireligion) {
                        markerOp
                                .position(node)
                                .title(mshop.getName() + " (" + mshop.getOwner() + ")")
                                .snippet(mshop.getMobile() + " - " + mshop.getTel())
                                .icon(BitmapDescriptorFactory.fromResource(R.drawable.ireligion));
                        mMap.addMarker(markerOp);
                    }
                    break;
                case "edu":
                    if (Iedu) {
                        markerOp
                                .position(node)
                                .title(mshop.getName() + " (" + mshop.getOwner() + ")")
                                .snippet(mshop.getMobile() + " - " + mshop.getTel())
                                .icon(BitmapDescriptorFactory.fromResource(R.drawable.iedu));
                        mMap.addMarker(markerOp);
                    }
                    break;

                default:
                    markerOp
                            .position(node)
                            .title(mshop.getName() + " (" + mshop.getOwner() + ")")
                            .snippet(mshop.getMobile() + " - " + mshop.getTel())
                            .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE));
                    mMap.addMarker(markerOp);
                    break;
            }

        }
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

                        JSONArray jsonArray= null;
                        rcvFlag=true;
                        failCount=0;
                        nodes.clear();
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

                        } catch (JSONException e) {
                            e.printStackTrace();

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
//            params.put("limit","30");
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
}
