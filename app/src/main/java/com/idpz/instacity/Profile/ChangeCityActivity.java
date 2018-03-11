package com.idpz.instacity.Profile;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
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
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.idpz.instacity.Area;
import com.idpz.instacity.Home.HomeActivity;
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

public class ChangeCityActivity extends AppCompatActivity implements OnMapReadyCallback {

    Spinner spnCity;
    Area myArea,myNearCity;

    List<String> cities=new ArrayList<>();
    List<Float> distances=new ArrayList<Float>();
    private GoogleMap mMap;
    String server="",lat,lng;
    Location myLocation, mycity;
    Boolean areaFlag=false,connected=false,mapFlag=false;
    ArrayList<Area> areaArrayList=new ArrayList<>();
    String AREA_URL="http://idpz.ir/i/getarea.php";
    Button btnChangeCT;
    TextView txtMyNearCT;
    Float homelat,homelng;

//    ListView lvCities;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_city);
        myLocation = new Location("myloc");
        mycity = new Location("city");

//        lvCities=(ListView)findViewById(R.id.lvChangeCTCities);
        btnChangeCT=(Button)findViewById(R.id.btnChangeCityChange);
        txtMyNearCT=(TextView)findViewById(R.id.txtNearestCity);


        SharedPreferences SP1;
        SP1 = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
        lat=SP1.getString("lat", "0");
        lng=SP1.getString("lng", "0");
        server=SP1.getString("server", "0");

        if (!lat.equals("0.0")){
            myLocation.setLatitude(Float.valueOf(lat));
            myLocation.setLongitude(Float.valueOf(lng));
        }else {
            final CharSequence[] items = {"روشن کردن جی پی اس", "انتخاب دستی شهر"};
            android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(getBaseContext());
            builder.setTitle("امکان موقعیت یابی شما نیست!");
            builder.setItems(items, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int item) {

                    if (items[item].equals("روشن کردن جی پی اس")) {
                        startActivityForResult(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS), 1);
                        dialog.dismiss();
                    } else if (items[item].equals("انتخاب دستی شهر")) {

                    }
                }
            });
            builder.show();
        }
        Toast.makeText(this, "lat="+myLocation.getLatitude()+" lng="+myLocation.getLongitude(), Toast.LENGTH_SHORT).show();
        btnChangeCT.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (server.equals(myArea.getServer())){
                    Toast.makeText(ChangeCityActivity.this, "شهر تغییر نکرده است", Toast.LENGTH_SHORT).show();
                    return;
                }else {
                    server=myArea.getServer();
                    homelat=myArea.getAlat();
                    homelng=myArea.getAlng();
                    SharedPreferences.Editor SP2 = PreferenceManager.getDefaultSharedPreferences(getBaseContext()).edit();
                    SP2.putString("server", server);
                    SP2.putString("ctname", myArea.getAfname());
                    SP2.putString("homelat",String.valueOf(homelat));
                    SP2.putString("homelng",String.valueOf(homelng));
                    SP2.apply();
                    finishAffinity();
                    Intent intent=new Intent(ChangeCityActivity.this,HomeActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                }
            }
        });

        new Thread() {
            @Override
            public void run() {
                while (!areaFlag||!mapFlag) {
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
                                Toast.makeText(ChangeCityActivity.this, "اینترنت وصل نیست", Toast.LENGTH_SHORT).show();
                            }


                            if (connected && !areaFlag) {
                                reqArea();   //یافتن منطقه کاربر و اتصال به سوور هماه منطقه
                            }
                            if (mapFlag)initilizeMap();



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

        spnCity=(Spinner)findViewById(R.id.spnLoginSelCity);
        spnCity.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                myArea=areaArrayList.get(position);
                if (mapFlag){
                    initilizeMap();
                    LatLng cityLatLng=new LatLng(myArea.getAlat(),myArea.getAlng());
                    mMap.addMarker(new MarkerOptions().position(cityLatLng).title(myArea.getAfname()));
                    mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(cityLatLng,15));
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        try {
            // Loading map
            initilizeMap();

        } catch (Exception e) {
            e.printStackTrace();
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
                        ArrayList<String> cityNames=new ArrayList<>();
                        ArrayList<String> ctNames = new ArrayList<>();

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
                                area.setAdiameter(jsonObject.getInt("adiameter"));
                                area.setServer(jsonObject.getString("server"));
                                area.setZoom(jsonObject.getInt("azoom"));

                                areaArrayList.add(area);
                                mycity.setLatitude(area.getAlat());
                                mycity.setLongitude(area.getAlng());
                                myDistance=Math.round(myLocation.distanceTo(mycity));
                                ctNames.add(jsonObject.getString("afname")+" (فاصله  "+myDistance+"متر)");
                            }
                            ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<String>
                                    (ChangeCityActivity.this, android.R.layout.simple_spinner_item,
                                            ctNames); //selected item will look like a spinner set from XML
                            spinnerArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                            spnCity.setAdapter(spinnerArrayAdapter);

//                            ArrayAdapter<String> adapter = new ArrayAdapter<String>(ChangeCityActivity.this,
//                                    android.R.layout.simple_list_item_1, cityNames);
//
//                            lvCities.setAdapter(adapter);

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
        txtMyNearCT.setText(" نزدیکترین شهر: "+myNearCity.getAfname()+" فاصله از مرکز "+distances.get(minIndex)+"متر");
        txtMyNearCT.setTextColor(Color.GREEN);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == 1) {
            final LocationManager manager = (LocationManager) getSystemService( Context.LOCATION_SERVICE );

            if ( manager.isProviderEnabled( LocationManager.GPS_PROVIDER ) ) {
                // Call your Alert message
                GPSTracker gps2 =new GPSTracker(ChangeCityActivity.this);
                lat=String.valueOf(gps2.getLatitude());
                lng=String.valueOf(gps2.getLongitude());
                findArea();
            }else {
                Toast.makeText(ChangeCityActivity.this, "جی پی اس روشن نشد موقعیت پیشفرض انتخاب شد:"+myArea.getAfname(), Toast.LENGTH_LONG).show();

            }

        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {

        mMap = googleMap;
        if (mMap == null) {
            SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                    .findFragmentById(R.id.map);
            mapFragment.getMapAsync(ChangeCityActivity.this);}
            // Add a marker in Sydney, Australia, and move the camera.
            LatLng cityLatLng = new LatLng(Double.valueOf(lat), Double.valueOf(lng));

            mMap.addMarker(new MarkerOptions().position(cityLatLng).title("موقعیت شما-500متر"));
            CircleOptions circleOptions = new CircleOptions().center(cityLatLng)
                    .radius(500)
                    .strokeColor(Color.BLUE)
                    .fillColor(0x30ff0000)
                    .strokeWidth(2);

            mMap.addCircle(circleOptions);
            mMap.addCircle(circleOptions);
            mMap.setTrafficEnabled(true);

            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(cityLatLng, 15));
            mapFlag = true;

        }

    private void initilizeMap() {

        if (mMap == null) {
            SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                    .findFragmentById(R.id.map);
            mapFragment.getMapAsync(ChangeCityActivity.this);
            // check if map is created successfully or not



            LatLngBounds.Builder builder = new LatLngBounds.Builder();


            for (Area area:areaArrayList){
                LatLng node = new LatLng(area.getAlat(),area.getAlng());
                MarkerOptions markerOptions = new MarkerOptions();
                markerOptions.position(node).title(area.getAfname());
                mMap.addMarker(markerOptions);
                builder.include(node);
            }
            LatLngBounds bounds = builder.build();
                int padding = 0; // offset from edges of the map in pixels
                CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, padding);
            mMap.animateCamera(cu);
//            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(Double.valueOf(lat),Double.valueOf(lng)), 14));
            for (Area area:areaArrayList){
                LatLng node = new LatLng(area.getAlat(),area.getAlng());
                mMap.addMarker(new MarkerOptions().position(node).title(area.getAfname()));
            }
        }
    }

}
