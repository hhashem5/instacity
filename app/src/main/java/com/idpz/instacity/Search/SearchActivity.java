package com.idpz.instacity.Search;

import android.annotation.TargetApi;
import android.content.Context;
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
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
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
import com.idpz.instacity.R;
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
    LatLng    cityLatLng;
    Float lat=35.711f,lng=50.912f;
    int speed=20,alarmDist=100;
    Location myLocation;
    String SHOP_URL="",dbLastData="mscity.ir";
    Boolean mapFlag=false,placesFlag=false,connected=false;
    GPSTracker gps;
    private Context context;
    private static final int REQUEST_ACCESS_LOCATION = 0;
    CheckBox chkTrafic,chkPlaces,chkSat;
    CheckBox chkIEdu,chkIShop,chkISport,chkIService,chkIRepaire,chkIHealth,chkIReligion,chkIFood,chkCars;
    Boolean Iedu=true,Ishop=false,Isport=true,Iservice=false,Irepair=false,Ihealth=true,Ireligion=false,Ifood=false,Icar=false;
    TextView txtcat;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        Log.d(TAG, "onCreate: strating");

        SHOP_URL=getString(R.string.server)+"/i/shoprec.php";

        shops=new ArrayList<>();
        showShops=new ArrayList<>();
        setupBottomNavigationView();

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

        chkPlaces.setText("دریافت اماکن");
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

        SHOP_URL=getString(R.string.server)+"/i/shoprec.php";

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
                }
            }
        });


        myLocation=new Location("");
        SharedPreferences SP = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
        float tlat=Float.valueOf(SP.getString("lat", "0"));
        float tlng=Float.valueOf(SP.getString("lng", "0"));
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

        new Thread() {
            @Override
            public void run() {
                while (!placesFlag) {
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

                            if (connected && !placesFlag) {
                                reqShop();
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
            cityLatLng = new LatLng(lat, lng);

        mMap.addMarker(new MarkerOptions().position(cityLatLng).title("موقعیت شما"));
        CircleOptions circleOptions=new CircleOptions().center(new LatLng(myLocation.getLatitude(),myLocation.getLongitude()))
                .radius(alarmDist);
        mMap.addCircle(circleOptions);
        mMap.setTrafficEnabled(true);

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
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(lat, lng), 17));
            mapFlag=true;
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
                                Log.d(TAG, "onResponse: receive lat"+shop.getJlat());
                                shops.add(shop);


                            }
//                            adapter.notifyDataSetChanged();
                            chkPlaces.setText("نمایش اماکن");
                            chkPlaces.setEnabled(true);

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
//                        CustomInfoWindow info =new CustomInfoWindow(SearchActivity.this);
//                        mMap.setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter() {
//                            @Override
//                            public View getInfoWindow(Marker marker) {
//                                return null;
//                            }
//
//                            @Override
//                            public View getInfoContents(Marker marker) {
//                                LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
//                                View view = inflater.inflate(R.layout.custom_info_marker, null);
//
//                                if (imageLoader == null)
//                                    imageLoader = AppController.getInstance().getImageLoader();
//                                NetworkImageView img = (NetworkImageView) view.findViewById(R.id.imgPLaceMaker);
//                                TextView txtName = (TextView) view.findViewById(R.id.txtMarkerPlaceName);
//                                TextView txtOwner = (TextView) view.findViewById(R.id.txtMarkerPlaceOwner);
//                                TextView txtKey = (TextView) view.findViewById(R.id.txtMarkerPlaceKey);
//                                TextView txtTel = (TextView) view.findViewById(R.id.txtMarkerPlacetel);
//                                TextView txtAddress = (TextView) view.findViewById(R.id.txtMarkerPlaceAddress);
//
////                                Shop m = (Shop) marker.getTag();
//                                // thumbnail image
//
//                                img.setImageUrl("http://mscity.ir/img/places/0.jpg", imageLoader);
//
//// title
//                                txtName.setText(marker.getTitle());
//
//// rating
//                                txtOwner.setText( mshop.getOwner());
////        txtKey.setText(m.getJkey());
////        txtTel.setText(m.getMobile()+" "+m.getTel());
//                                txtAddress.setText(marker.getSnippet());
//
//
//                                return view;
//                            }
//                        });
//                        mMap.addMarker(markerOp);
//                        Marker mk = mMap.addMarker(markerOp);
//                        mk.setTag(mshop);
//                        m.showInfoWindow();
            if (Icar){

            }
        }
    }
}
