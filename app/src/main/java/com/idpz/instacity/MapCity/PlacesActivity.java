package com.idpz.instacity.MapCity;

import android.annotation.TargetApi;
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
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
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
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.idpz.instacity.R;
import com.idpz.instacity.models.Shop;
import com.idpz.instacity.utils.BottomNavigationViewHelper;
import com.idpz.instacity.utils.CustomTextView;
import com.idpz.instacity.utils.GPSTracker;
import com.idpz.instacity.utils.MapShopAdapter;
import com.ittianyu.bottomnavigationviewex.BottomNavigationViewEx;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static android.Manifest.permission.ACCESS_COARSE_LOCATION;
import static android.Manifest.permission.ACCESS_FINE_LOCATION;

public class PlacesActivity extends AppCompatActivity  {

    private static final String TAG = "SearchActivity";
    private static final int ACTIVITY_NUM = 1;
//    private GoogleMap mMap;
    ArrayList<Shop> shops, showShops;
    List<Float> distances = new ArrayList<Float>();

    LatLng cityLatLng;
    Float lat = 35.711f, lng = 50.912f, myDist;
    ;
    Location myLocation, shopLocation;
    String SHOP_URL = "", mySnipet = "موقیت", myTitle = "خانه";
    Boolean mapFlag = false, placesFlag = true, reqPlaces = false, connected = false;
    GPSTracker gps;
    private Context context;
    private static final int REQUEST_ACCESS_LOCATION = 0;
//    CheckBox chkIEdu, chkIShop, chkISport, chkIService, chkIRepaire, chkIHealth, chkIReligion, chkIFood;
    CustomTextView txtResult;
    Boolean Iedu = true, Ishop = true, Isport = true, Iservice = true, Irepair = true, Ihealth = true, Ireligion = true, Ifood = true, remain = true;
    TextView txtcat, txtMydist;
    ProgressBar progressBar;
    Button btnRegStore;
    String state = "";
    Double homelat, homelng;
    ListView lvshops;
    MapShopAdapter adapter;
    SharedPreferences SP1;
    ArrayAdapter<String> spinnerArrayAdapter;
    int failCount = 0, mapCount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_places);
        Log.d(TAG, "onCreate: strating");

        myLocation = new Location("");
        shopLocation = new Location("");
        Typeface yekan = Typeface.createFromAsset(PlacesActivity.this.getAssets(), "fonts/YEKAN.TTF");
        showShops = new ArrayList<>();
        shops = new ArrayList<>();

        lvshops = (ListView) findViewById(R.id.lvShops);
        adapter = new MapShopAdapter(this, R.layout.myshop_row, showShops);
        lvshops.setAdapter(adapter);
        SP1 = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
        state = SP1.getString("state", "0");
        lat = Float.valueOf(SP1.getString("lat", "0"));
        lng = Float.valueOf(SP1.getString("lng", "0"));
        homelat = Double.valueOf(SP1.getString("homelat", "0"));
        homelng = Double.valueOf(SP1.getString("homelng", "0"));
        myLocation.setLatitude(lat);
        myLocation.setLongitude(lng);
        if (myLocation.getLatitude() == 0) {
            myLocation.setLatitude(homelat);
            myLocation.setLongitude(homelng);
        }

//        mapCount = SP1.getInt("map_count", 0);
//        mapCount++;
//        mapOpenCount();
        progressBar=findViewById(R.id.progressBar);

        SHOP_URL = getString(R.string.server)+"/j/shoprec.php";


        setupBottomNavigationView();


//        if (mapCount < 5) {
//            final ImageView imgRoute = (ImageView) findViewById(R.id.imgRoute);
////            imgRoute.setVisibility(View.VISIBLE);
////            tvNotice.setTypeface(yekan);
//            new Handler().postDelayed(new Runnable() {
//
//                @Override
//                public void run() {
////                    tvNotice.setVisibility(View.GONE);
//                    imgRoute.setVisibility(View.GONE);
//                }
//            }, 5000);
//
//
//        }


        AutoCompleteTextView textView = (AutoCompleteTextView) findViewById(R.id.acTxtView);
        txtResult=(CustomTextView)findViewById(R.id.txtSearchResult);
        textView.setTypeface(yekan);
        textView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (editable.length() > 0) {
                    int i = 0;
                    showShops.clear();
//                    mMap.clear();
//                    MarkerOptions markerOp = new MarkerOptions();
                    for (Shop shop : shops) {
                        i++;
                        shopLocation.setLatitude(Double.parseDouble(shop.getJlat()));
                        shopLocation.setLongitude(Double.parseDouble(shop.getJlng()));
                        float myDistance = Math.round(myLocation.distanceTo(myLocation));
                        distances.add(myDistance);
                        if (shop.getName().contains(editable.toString())
                                || shop.getJkey().contains(editable.toString())
                                || shop.getOwnerName().contains(editable.toString())
                                ) {
//                            spnCity.setSelection(i-1);
                            showShops.add(shop);
                            adapter.notifyDataSetChanged();
                            txtResult.setText(showShops.size()+" "+getResources().getString(R.string.result));
//                            drawMap();
//                            markerOp
//                                    .position(new LatLng(Double.parseDouble(shop.getJlat()), Double.parseDouble(shop.getJlng())))
//                                    .title(shop.getName()+" "+shop.getTel())
//                                    .snippet(" فاصله:"+myDistance+"km")
//                                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.garbagepin));
//                            mMap.addMarker(markerOp);
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
                } else if (editable.length() == 0) {
                    showShops.clear();
                    showShops.addAll(shops);
                    adapter.notifyDataSetChanged();
                    txtResult.setText(getResources().getString(R.string.result)+":"+showShops.size());
//                    ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<String>
//                            (ChangeCityActivity.this, android.R.layout.simple_spinner_item,
//                                    ctNames);
                }



            }
        });


        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

        lvshops.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                MarkerOptions markerOp = new MarkerOptions();
//                LatLng shopLtlg=new LatLng(Double.parseDouble(showShops.get(position).getJlat()),
//                        Double.parseDouble(showShops.get(position).getJlng()));
//                markerOp
//                        .position(shopLtlg)
//                        .title(showShops.get(position).getName() + " (" + showShops.get(position).getOwner() + ")")
//                        .snippet(showShops.get(position).getMobile() + " - " + showShops.get(position).getTel())
//                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_check));
//                mMap.addMarker(markerOp);
//                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(shopLtlg,20));
                Intent shopint=new Intent(PlacesActivity.this,SingleShopActivity.class);
                shopint.putExtra("name",showShops.get(position).getName());
                shopint.putExtra("ownerName",showShops.get(position).getOwnerName());
                shopint.putExtra("tel",showShops.get(position).getTel());
                shopint.putExtra("mobile",showShops.get(position).getMobile());
                shopint.putExtra("pic",showShops.get(position).getPic());
                shopint.putExtra("address",showShops.get(position).getAddress());
                shopint.putExtra("jlat",showShops.get(position).getJlat());
                shopint.putExtra("jlng",showShops.get(position).getJlng());
                shopint.putExtra("tag",showShops.get(position).getTag());
                shopint.putExtra("jkey",showShops.get(position).getJkey());
                shopint.putExtra("memo",showShops.get(position).getMemo());
                startActivity(shopint);

            }
        });




//
//        chkIEdu=(CheckBox) findViewById(R.id.chkIEdu);
//        chkIShop=(CheckBox) findViewById(R.id.chkIShop);
//        chkISport=(CheckBox) findViewById(R.id.chkISport);
//        chkIService=(CheckBox) findViewById(R.id.chkIService);
////        chkIRepaire=(CheckBox)findViewById(R.id.chkIRepair);
//        chkIHealth=(CheckBox) findViewById(R.id.chkIHealth);
//        chkIReligion=(CheckBox) findViewById(R.id.chkIReligion);
//        chkIFood=(CheckBox) findViewById(R.id.chkIfood);
//        txtcat=(TextView) findViewById(R.id.txtCat);
//        txtMydist=(TextView) findViewById(R.id.txtMyDistance);
        btnRegStore=(Button) findViewById(R.id.btnMapStoreReg);

//        chkIEdu.setVisibility(View.GONE);
//        chkIShop.setVisibility(View.GONE);
//        chkISport.setVisibility(View.GONE);
//        chkIService.setVisibility(View.GONE);
////        chkIRepaire.setVisibility(View.GONE);
//        chkIHealth.setVisibility(View.GONE);
//        chkIReligion.setVisibility(View.GONE);
//        chkIFood.setVisibility(View.GONE);
//        txtcat.setVisibility(View.GONE);
//        txtMydist.setVisibility(View.GONE);
        SHOP_URL=getString(R.string.server)+"/j/shoprec.php";


        new Thread() {
            @Override
            public void run() {
                while (!reqPlaces) {
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
                                if (failCount>4)remain=false;
                                failCount++;
                            }
                            if (connected ){

                                if(connected&&failCount>4){

                                                    remain=false;
                                                    finish();


                                }
                                Log.d(TAG, "run: loop of request connected reqplaces="+reqPlaces);
                                if (!reqPlaces) reqShop();


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

//
//        Intent intent = getIntent();
//        if(intent.hasExtra("lat")) {
//            remain=false;
//            btnRegStore.setVisibility(View.GONE);
//            homelat = Double.valueOf(intent.getStringExtra("lat"));
//            homelng = Double.valueOf(intent.getStringExtra("lng"));
//            myTitle = intent.getStringExtra("name");
//            mySnipet = intent.getStringExtra("memo");
//            Log.d(TAG, "onCreate: lat:"+homelat+" lng:"+homelng+" incoming  lat:"+intent.getStringExtra("lat"));
//        }
//
//        try {
//            // Loading map
//            initilizeMap();
//
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//
        btnRegStore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent in2=new Intent(PlacesActivity.this,StoreRegActivity.class);
                startActivity(in2);
            }
        });
//
//        chkISport.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
//            @Override
//            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
//                Isport = isChecked;
////                drawMap();
//
//            }
//        });
//        chkIService.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
//            @Override
//            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
//                Iservice = isChecked;
////                drawMap();
//
//            }
//        });
//
//        chkIShop.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
//            @Override
//            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
//                Ishop = isChecked;
//
////                drawMap();
//            }
//        });
////        chkIRepaire.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
////            @Override
////            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
////                if (!isChecked){
////                    Irepair=false;
////                }else {
////                    Irepair=true;
////                }
////
////                drawMap();
////            }
////        });
//        chkIReligion.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
//            @Override
//            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
//                Ireligion = isChecked;
////                drawMap();
//
//            }
//        });
//        chkIHealth.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
//            @Override
//            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
//                Ihealth = isChecked;
////                drawMap();
//
//            }
//        });
//        chkIEdu.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
//            @Override
//            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
//                Iedu = isChecked;
////                drawMap();
//
//            }
//        });
//        chkIFood.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
//            @Override
//            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
//                Ifood = isChecked;
////                drawMap();
//
//                }
//            });



//
//        float tlat=Float.valueOf(SP1.getString("lat", "0"));
//        float tlng=Float.valueOf(SP1.getString("lng", "0"));
//        if(tlat==0) {
//            myLocation.setLatitude(lat);
//            myLocation.setLongitude(lng);
//        }else if(lat==0){
//            populateGPS();
//        }
//





    }
//
//        public void onResetMap(View v){
//
//
//    }



    // تنظیم نوار پایین برنامه
    private void setupBottomNavigationView(){
        Log.d(TAG,"seting up bottom navigation view");
        BottomNavigationViewEx bottomNavigationViewEx=(BottomNavigationViewEx) findViewById(R.id.bottomNavViewBar);
        BottomNavigationViewHelper.setupBottomNavigationView(bottomNavigationViewEx);
        BottomNavigationViewHelper.enableNavigation(PlacesActivity.this,bottomNavigationViewEx);
        Menu menu=bottomNavigationViewEx.getMenu();
        MenuItem menuItem=menu.getItem(ACTIVITY_NUM);
        menuItem.setChecked(true);
    }
//
//    @Override
//    public void onMapReady(GoogleMap googleMap) {
//
//        Intent intent = getIntent();
//        if(intent.hasExtra("lat")) {
//            remain=false;
//            btnRegStore.setVisibility(View.GONE);
//            homelat = Double.valueOf(intent.getStringExtra("lat"));
//            homelng = Double.valueOf(intent.getStringExtra("lng"));
//            myTitle = intent.getStringExtra("name");
//            mySnipet = intent.getStringExtra("memo");
//            Log.d(TAG, "onCreate: lat:"+homelat+" lng:"+homelng+" incoming  lat:"+intent.getStringExtra("lat"));
//        }
//
//        mMap = googleMap;
//
//        // Add a marker in Sydney, Australia, and move the camera.
//            cityLatLng = new LatLng(homelat, homelng);
//
//        mMap.addMarker(new MarkerOptions().position(cityLatLng).title(myTitle).snippet(mySnipet));
//
//        mMap.setTrafficEnabled(false);
//
//        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(cityLatLng,15));
////        drawMap();
//    }
//
//    private void initilizeMap() {
//
//        if (mMap == null) {
//            SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
//                    .findFragmentById(R.id.map);
//            mapFragment.getMapAsync(this);
//            // check if map is created successfully or not
//            if (mMap == null) {
//                Toast.makeText(getApplicationContext(),
//                        "لطفا کمی صبر کنید...", Toast.LENGTH_SHORT)
//                        .show();
//            }
//            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(homelat, homelng), 17));
//            mapFlag=true;
//            Log.d(TAG, "initilizeMap: "+homelat+" "+homelng);
//
//        }
//    }


    public void reqShop() {
        RequestQueue queue = Volley.newRequestQueue(this);
        String url = SHOP_URL;
        progressBar.setVisibility(View.VISIBLE);
        StringRequest postRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>()
                {
                    @Override
                    public void onResponse(String response) {
                        if (response.length()>10) {
                            JSONArray jsonArray = null;
                            Log.d(TAG, "onResponse: shops received" + response);
                            reqPlaces = true;
                            progressBar.setVisibility(View.GONE);
                            failCount = 0;
                            try {
                                Shop shop = new Shop();
                                jsonArray = new JSONArray(response);

                                JSONObject jsonObject;
//                        dbSocialHandler.removeAll();

                                for (int i = 0; i < jsonArray.length(); i++) {
                                    jsonObject = jsonArray.getJSONObject(i);
                                    shop = new Shop();
                                    shop.setId(jsonObject.getInt("id"));
                                    shop.setName(jsonObject.getString("name"));
                                    shop.setOwner(jsonObject.getString("owner"));
                                    shop.setOwnerName(jsonObject.getString("ownername"));
                                    ;
                                    shop.setTel(jsonObject.getString("tel"));
                                    shop.setMobile(jsonObject.getString("mobile"));
                                    shop.setAddress(jsonObject.getString("address"));
                                    shop.setJlat(jsonObject.getString("jlat"));
                                    shop.setJlng(jsonObject.getString("jlng"));
                                    shop.setTag(jsonObject.getString("tag"));
                                    shop.setJkey(jsonObject.getString("jkey"));
                                    shop.setPic(getString(R.string.server) + "/assets/images/places/" + jsonObject.getString("pic"));
//                                if (value.equals(tag)) {
//                                    shops.add(new Shop(id, name, owner,
//                                            tel, mobile, "", address, jlat, jlng, tag, jkey, "", ""));
//                                    showShops.add(new Shop(id, name, owner,
//                                            tel, mobile, "", address, jlat, jlng, tag, jkey, "", ""));
//                                }
//                                Log.d(TAG, "onResponse: receive lat"+shop.getJlat());
                                    shops.add(shop);

                                }
                                showShops.addAll(shops);
                                adapter.notifyDataSetChanged();
                                txtResult.setText(getResources().getString(R.string.result) + ":" + showShops.size());
//                                drawMap();
                            } catch (JSONException e) {
                                e.printStackTrace();
                                failCount++;
                                progressBar.setVisibility(View.GONE);
                            }


                        }
                    }
                },
                new Response.ErrorListener()
                {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // TODO Auto-generated method stub
                        failCount++;
                        progressBar.setVisibility(View.GONE);
                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String>params = new HashMap<String,String>();
                params.put("id","0");
                params.put("state",state);
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
        SP.apply();
        myLocation.setLatitude(gps.getLatitude());
        myLocation.setLongitude(gps.getLongitude());


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
            Snackbar.make(btnRegStore, "لطفا دسترسی به جی پی اس را فعال کنید.", Snackbar.LENGTH_INDEFINITE)
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
//
//    private void drawMap() {
//        mMap.clear();
//        MarkerOptions markerOp = new MarkerOptions();
//        if (placesFlag){
//        for (Shop mshop : showShops) {
//
//            LatLng node = new LatLng(Double.valueOf(mshop.getJlat()), Double.valueOf(mshop.getJlng()));
//
//            switch (mshop.getTag()) {
//                case "store":
//                    if (Ishop) {
//                        markerOp
//                                .position(node)
//                                .title(mshop.getName() + " (" + mshop.getOwner() + ")")
//                                .snippet(mshop.getMobile() + " - " + mshop.getTel())
//                                .icon(BitmapDescriptorFactory.fromResource(R.drawable.ishop));
//                        mMap.addMarker(markerOp);
////                        showShops.add(mshop);
//                    }
//                    break;
//                case "food":
//                    if (Ifood) {
//                        markerOp
//                                .position(node)
//                                .title(mshop.getName() + " (" + mshop.getOwner() + ")")
//                                .snippet(mshop.getMobile() + " - " + mshop.getTel())
//                                .icon(BitmapDescriptorFactory.fromResource(R.drawable.ifood));
//                        mMap.addMarker(markerOp);
////                        showShops.add(mshop);
//                    }
//                    break;
//                case "health":
//                    if (Ihealth) {
//                        markerOp
//                                .position(node)
//                                .title(mshop.getName() + " (" + mshop.getOwner() + ")")
//                                .snippet(mshop.getMobile() + " - " + mshop.getTel())
//                                .icon(BitmapDescriptorFactory.fromResource(R.drawable.ihealth));
//                        mMap.addMarker(markerOp);
////                        showShops.add(mshop);
//                    }
//                    break;
//                case "services":
//                    if (Iservice) {
//                        markerOp
//                                .position(node)
//                                .title(mshop.getName() + " (" + mshop.getOwner() + ")")
//                                .snippet(mshop.getMobile() + " - " + mshop.getTel())
//                                .icon(BitmapDescriptorFactory.fromResource(R.drawable.iservices));
//                        mMap.addMarker(markerOp);
////                        showShops.add(mshop);
//                    }
//                    break;
//                case "sport":
//                    if (Isport) {
//                        markerOp
//                                .position(node)
//                                .title(mshop.getName() + " (" + mshop.getOwner() + ")")
//                                .snippet(mshop.getMobile() + " - " + mshop.getTel())
//                                .icon(BitmapDescriptorFactory.fromResource(R.drawable.isport));
//                        mMap.addMarker(markerOp);
////                        showShops.add(mshop);
//                    }
//                    break;
//                case "religion":
//                    if (Ireligion) {
//                        markerOp
//                                .position(node)
//                                .title(mshop.getName() + " (" + mshop.getOwner() + ")")
//                                .snippet(mshop.getMobile() + " - " + mshop.getTel())
//                                .icon(BitmapDescriptorFactory.fromResource(R.drawable.ireligion));
//                        mMap.addMarker(markerOp);
////                        showShops.add(mshop);
//                    }
//                    break;
//                case "edu":
//                    if (Iedu) {
//                        markerOp
//                                .position(node)
//                                .title(mshop.getName() + " (" + mshop.getOwner() + ")")
//                                .snippet(mshop.getMobile() + " - " + mshop.getTel())
//                                .icon(BitmapDescriptorFactory.fromResource(R.drawable.iedu));
//                        mMap.addMarker(markerOp);
////                        showShops.add(mshop);
//                    }
//                    break;
//
//                default:
//                    markerOp
//                            .position(node)
//                            .title(mshop.getName() + " (" + mshop.getOwner() + ")")
//                            .snippet(mshop.getMobile() + " - " + mshop.getTel())
//                            .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE));
//                    mMap.addMarker(markerOp);
////                    showShops.add(mshop);
//                    break;
//            }
//
//        }
//        }
//
//
//    }



//    @Override
//    public void onStart() {
//        super.onStart();
//        Log.d(TAG, "onStart: شروع شد");
//
//    }

//    public void mapOpenCount() {
//
//        if (mapCount<7) {
//            SharedPreferences.Editor SP = PreferenceManager.getDefaultSharedPreferences(PlacesActivity.this).edit();
//            SP.putInt("map_count", mapCount);
//            SP.apply();
//        }
//    }

}
