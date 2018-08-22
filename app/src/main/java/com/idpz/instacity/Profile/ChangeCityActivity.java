package com.idpz.instacity.Profile;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.Spinner;
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
import com.idpz.instacity.AlarmService;
import com.idpz.instacity.Area;
import com.idpz.instacity.Home.AddCityActivity;
import com.idpz.instacity.Home.HomeActivity;
import com.idpz.instacity.Home.LoginActivity;
import com.idpz.instacity.R;
import com.idpz.instacity.Share.FilterCityActivity;
import com.idpz.instacity.utils.DBAreaHandler;
import com.idpz.instacity.utils.GPSTracker;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ChangeCityActivity extends AppCompatActivity implements OnMapReadyCallback {

    private static final String TAG ="change ct";
    Spinner spnCity,spnOstan;
    Area myArea,myNearCity;
    ArrayList<String> ostanNames =new ArrayList<>();
    ArrayList<Area> ostanArrayList=new ArrayList<>();
    List<String> cities=new ArrayList<>();
    List<Float> distances=new ArrayList<Float>();
    private GoogleMap mMap;
    String server="",lat,lng,filterOstan="";
    Location myLocation, mycity;
    Boolean areaFlag=false,connected=false,mapFlag=false;
    ArrayList<Area> areaArrayList=new ArrayList<>();
    ArrayList<Area> areaAllList=new ArrayList<>();
    ArrayList<Area> areaSelectList=new ArrayList<>();
    String AREA_URL="",OSTAN_URL="";
    Button btnChangeCT,btnCityReg;
//    TextView txtMyNearCT;
    Float homelat,homelng;
    ArrayAdapter<String> adapterOstan;
    ArrayAdapter<String> spinnerArrayAdapter;
    ArrayList<String> villages=new ArrayList<>();
    ArrayList<String> allVillages=new ArrayList<>();
    ArrayList<String> searchModels=new ArrayList<>();
    ArrayList<String> ctNames;
//    ListView lvCities;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_city);

        myLocation = new Location("myloc");
        mycity = new Location("city");
        AREA_URL=getString(R.string.server)+"/i/getarea.php";
        OSTAN_URL=getString(R.string.server)+"/j/getostan.php";
        Typeface yekan = Typeface.createFromAsset(ChangeCityActivity.this.getAssets(), "fonts/YEKAN.TTF");

//        lvCities=(ListView)findViewById(R.id.lvChangeCTCities);
        btnChangeCT=(Button) findViewById(R.id.btnChangeCityChange);
//        txtMyNearCT=(TextView)findViewById(R.id.txtNearestCity);
        btnChangeCT.setTypeface(yekan);

        btnCityReg=(Button) findViewById(R.id.btnCityReg);
        btnCityReg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent in1=new Intent(ChangeCityActivity.this,AddCityActivity.class);
                startActivity(in1);
            }
        });


        SharedPreferences SP1;
        SP1 = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
        lat=SP1.getString("lat", "0");
        lng=SP1.getString("lng", "0");
        server=SP1.getString("server", "0");

        if (!lat.equals("0.0")){
            myLocation.setLatitude(Float.valueOf(lat));
            myLocation.setLongitude(Float.valueOf(lng));
        }
//        Toast.makeText(this, "lat="+myLocation.getLatitude()+" lng="+myLocation.getLongitude(), Toast.LENGTH_SHORT).show();
        btnChangeCT.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (server.equals(myArea.getServer())){
                    Toast.makeText(ChangeCityActivity.this, "شهر تغییر نکرده است", Toast.LENGTH_SHORT).show();
                }else {
//                    stopService(new Intent(ChangeCityActivity.this, AlarmService.class));
                    server=myArea.getServer();
                    homelat=myArea.getAlat();
                    homelng=myArea.getAlng();
                    SharedPreferences.Editor SP2 = PreferenceManager.getDefaultSharedPreferences(getBaseContext()).edit();
                    SP2.putString("server", server);
                    SP2.putString("aename", myArea.getAename());
                    SP2.putString("ctname", myArea.getAfname());
                    SP2.putString("homelat",String.valueOf(homelat));
                    SP2.putString("homelng",String.valueOf(homelng));
                    SP2.putString("state",myArea.getState());
                    SP2.putString("ctpic",myArea.getPic());
                    SP2.putString("ctdesc",myArea.getDescription());
                    SP2.putBoolean("splash",false);
                    SP2.putString("notification","1");
                    AlarmService.msgid="1";
                    SP2.apply();
                    finishAffinity();
                    Intent intent=new Intent(ChangeCityActivity.this,HomeActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);

                }
            }
        });
         ctNames=new ArrayList<>();
        DBAreaHandler dbAreaHandler=new DBAreaHandler(this);
        for (Area dArea:dbAreaHandler.getAllAreas()){
            float myDistance=0;
            mycity.setLatitude(dArea.getAlat());
            mycity.setLongitude(dArea.getAlng());
            myDistance=Math.round((myLocation.distanceTo(mycity)/1000));
            String h=myDistance+"";
            h=h.substring(0,h.length()-2);
            dArea.setDistance(Integer.valueOf(h));
            areaArrayList.add(dArea);
            villages.add(dArea.getAfname());
//            ctNames.add(dArea.getAfname()+" (فاصله  "+myDistance+"متر)");
        }


        Collections.sort(areaArrayList, new AgeComparator());
        areaAllList.addAll(areaArrayList);
        for (Area pArea :areaArrayList){
            ctNames.add(pArea.getAfname()+" (فاصله  "+pArea.getDistance()+"km)");
        }
        allVillages.addAll(villages);
        final ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_dropdown_item_1line, villages);

        spnCity=(Spinner) findViewById(R.id.spnLoginSelCity);

        spinnerArrayAdapter = new ArrayAdapter<String>
                (ChangeCityActivity.this, android.R.layout.simple_spinner_item,
                        ctNames); //selected item will look like a spinner set from XML
        spinnerArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spnCity.setAdapter(spinnerArrayAdapter);

        adapterOstan = new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1, android.R.id.text1, ostanNames);

        spnOstan=(Spinner) findViewById(R.id.spnOstan);
        spnOstan.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                    if (ostanArrayList.size()>0) {
                        filterOstan = ostanArrayList.get(position).getAename();
                        if (filterOstan.equals("0")){
                            areaArrayList.clear();
                            areaArrayList.addAll(areaAllList);
                            villages.clear();
                            villages.addAll(allVillages);
                            ctNames.clear();
                            for (Area pArea :areaArrayList){
                                ctNames.add(pArea.getAfname()+" (فاصله  "+pArea.getDistance()+"km)");
                            }
                        }else {
                            ctNames.clear();
                            villages.clear();
                            areaArrayList.clear();
                            Log.i(TAG, "onItemSelected: Ostan Select"+filterOstan);
                            for (Area uArea : areaAllList) {
                                if (uArea.getState().startsWith(filterOstan)) {
                                    float myDistance = 0;
                                    mycity.setLatitude(uArea.getAlat());
                                    mycity.setLongitude(uArea.getAlng());
                                    myDistance = Math.round((myLocation.distanceTo(mycity) / 1000));
                                    String h = myDistance + "";
                                    h = h.substring(0, h.length() - 2);
                                    uArea.setDistance(Integer.valueOf(h));
                                    areaArrayList.add(uArea);
                                    villages.add(uArea.getAfname());
                                }
                            }
                            for (Area pArea :areaArrayList){
                                ctNames.add(pArea.getAfname()+" (فاصله  "+pArea.getDistance()+"km)");
                            }
                        }
                        spinnerArrayAdapter.notifyDataSetChanged();
                        adapter.notifyDataSetChanged();

                    Log.i(TAG, "onItemSelected: "+filterOstan+" cities="+villages.size());
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        reqOstan();

        Log.d(TAG, "onCreate: "+ctNames.size());




        findArea();
        spnCity.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (areaArrayList.size()>0) {
                    myArea = areaArrayList.get(position);
                    if (mapFlag) {
                        initilizeMap();
                        LatLng cityLatLng = new LatLng(myArea.getAlat(), myArea.getAlng());
                        mMap.addMarker(new MarkerOptions().position(cityLatLng).title(myArea.getAfname()));
                        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(cityLatLng, 15));
                    }
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

        Intent intent = getIntent();
        if(intent.hasExtra("aename")) {
            String findName = intent.getStringExtra("aename");
            int i=0;
            Log.d(TAG, "onCreate: my findloc"+findName);
            for (Area area:areaArrayList){
                i++;
                mycity.setLatitude(area.getAlat());
                mycity.setLongitude(area.getAlng());
                float myDistance=Math.round(myLocation.distanceTo(mycity));
                distances.add(myDistance);
                if (findName.equals(area.getAename())){
                    spnCity.setSelection(i-1);
                }
            }
        }


        AutoCompleteTextView textView =(AutoCompleteTextView) findViewById(R.id.acTxtView);
        textView.setAdapter(adapter);
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
                        float myDistance=Math.round(myLocation.distanceTo(mycity));
                        distances.add(myDistance);
                        if (s.toString().contains(area.getAfname())){
                            spnCity.setSelection(i-1);
                        }
                    }

                }else if (s.length()==0){
                    searchModels.clear();

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

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

    }

    class AgeComparator implements Comparator<Area> {
        @Override
        public int compare(Area employee1, Area employee2) {
            int employee1Age = employee1.getDistance();
            int employee2Age = employee2.getDistance();

            if (employee1Age > employee2Age) {
                return 1;
            } else if (employee1Age < employee2Age) {
                return -1;
            } else {
                return 0;
            }
        }
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
        Toast.makeText(ChangeCityActivity.this, "نقشه آماده شد", Toast.LENGTH_SHORT).show();
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

    @Override
    protected void onStart() {
        super.onStart();
        Intent intent = getIntent();
        if(intent.hasExtra("aename")) {
            String findName = intent.getStringExtra("aename");
            int i=0;
            Log.d(TAG, "onCreate: my findloc"+findName);
            for (Area area:areaArrayList){
                i++;
                mycity.setLatitude(area.getAlat());
                mycity.setLongitude(area.getAlng());
                float myDistance=Math.round(myLocation.distanceTo(mycity));
                distances.add(myDistance);
                if (findName.equals(area.getAename())){
                    spnCity.setSelection(i-1);
                }
            }
        }
    }

    @Override
    protected void onPostResume() {
        super.onPostResume();
        Intent intent = getIntent();
        if(intent.hasExtra("aename")) {
            String findName = intent.getStringExtra("aename");
            int i=0;
            Log.d(TAG, "onCreate: my findloc"+findName);
            for (Area area:areaArrayList){
                i++;
                mycity.setLatitude(area.getAlat());
                mycity.setLongitude(area.getAlng());
                float myDistance=Math.round(myLocation.distanceTo(mycity));
                distances.add(myDistance);
                if (findName.equals(area.getAename())){
                    spnCity.setSelection(i-1);
                }
            }
        }
    }


    public void reqOstan() {
        RequestQueue queue = Volley.newRequestQueue(this);
        String url = OSTAN_URL;
        StringRequest postRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>()
                {
                    public static final String TAG = "change city";

                    @Override
                    public void onResponse(String response) {
                        JSONArray jsonArray= null;
                        Log.d(TAG, "onResponse: response from Ostan"+response);
                        ostanNames.clear();
                        ostanArrayList.clear();

                        ostanNames.add("همه استان ها");
                        ostanArrayList.add(new Area(1,"0","0",0,0,1,"",0,0,"","","همه شهر/روستاها"));
                        areaFlag=true;

                        try {
                            jsonArray = new JSONArray(response);

                            JSONObject jsonObject=jsonArray.getJSONObject(0);

                            for (int i=jsonArray.length();i>0;i--) {
                                jsonObject = jsonArray.getJSONObject(i-1);
                                float myDistance=0;
                                Area area=new Area();
                                area.setId(jsonObject.getInt("id"));
                                area.setAename(jsonObject.getString("code"));
                                area.setAfname(jsonObject.getString("fname"));

                                ostanArrayList.add(area);
                                ostanNames.add(area.getAfname());

                            }
                            ArrayAdapter<String> OstanArrayAdapter = new ArrayAdapter<String>
                                    (ChangeCityActivity.this, android.R.layout.simple_spinner_item,
                                            ostanNames); //selected item will look like a spinner set from XML
                            OstanArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                            spnOstan.setAdapter(OstanArrayAdapter);




                        } catch (JSONException e) {
                            e.printStackTrace();

                        }

                    }
                },
                new Response.ErrorListener()
                {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                        Log.d("ERROR","Login area error => "+error.toString());
                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String>params = new HashMap<String,String>();

                params.put("db","states" );

                return params;
            }
        };
        queue.add(postRequest);
    }


}
