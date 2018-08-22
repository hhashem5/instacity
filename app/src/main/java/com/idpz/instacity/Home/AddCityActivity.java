package com.idpz.instacity.Home;

import android.annotation.TargetApi;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Typeface;
import android.os.Build;
import android.preference.PreferenceManager;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.Spinner;
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
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.idpz.instacity.Area;
import com.idpz.instacity.R;
import com.idpz.instacity.Share.PopupActivity;
import com.idpz.instacity.utils.AndyUtils;
import com.idpz.instacity.utils.GPSTracker;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static android.Manifest.permission.ACCESS_COARSE_LOCATION;
import static android.Manifest.permission.ACCESS_FINE_LOCATION;

public class AddCityActivity extends AppCompatActivity implements OnMapReadyCallback,GoogleMap.OnMapClickListener,
        GoogleMap.OnMapLongClickListener, GoogleMap.OnMarkerDragListener {

    private static final int REQUEST_ACCESS_LOCATION = 0;
    private static final String TAG = "AddCityActivity";
    Area myArea;
    String AREA_URL = "",OSTAN_URL="",lat="0",lng="0";
    Spinner spnCity;
    ArrayList<String> ostanNames =new ArrayList<>();
    ArrayList<String> ctNames =new ArrayList<>();
    ArrayList<String> AllCtNames =new ArrayList<>();
    ArrayList<Area> ostanArrayList=new ArrayList<>();
    ArrayList<Area> areaArrayList=new ArrayList<>();
    ProgressBar progressBar;
    private GoogleMap mMap;
    SupportMapFragment mapFragment;
    boolean areaFlag=false,showMap=true,kansel=true;
    AutoCompleteTextView textView;
    ListView lvAreas;
    ArrayAdapter<String> adapter;
    ArrayAdapter<String> lvadapter;
    Button btnFinalCity,btnOk;
    EditText edtEname,edtShahrestan,edtMobile;
    RadioButton rdVillage,rdCitizen;
    View focusView = null;
    LatLng latLng;
    String isCity="0",isGov="0",OSTAN_REG="",mobile="",ename="",fname="",shrstn="";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_city);

        latLng=new LatLng(35.711,50.912);

        AREA_URL=getString(R.string.server)+"/i/getarea.php";
        OSTAN_URL=getString(R.string.server)+"/j/getostan.php";
        OSTAN_REG=getString(R.string.server)+"/j/setcity.php";
        progressBar=(ProgressBar)findViewById(R.id.progressBar);
        Typeface yekan = Typeface.createFromAsset(AddCityActivity.this.getAssets(), "fonts/YEKAN.TTF");
        btnFinalCity =(Button) findViewById(R.id.btnFinalCity);
        lvAreas=(ListView) findViewById(R.id.lvMyCities);
        edtEname=(EditText) findViewById(R.id.txtAeName);
        edtMobile=(EditText) findViewById(R.id.txtCityzenMobile);
        edtShahrestan=(EditText) findViewById(R.id.txtShahrestan);
        textView=(AutoCompleteTextView) findViewById(R.id.txtCityName);
        rdCitizen=(RadioButton) findViewById(R.id.radioCityzen);
        rdVillage=(RadioButton) findViewById(R.id.radioVillage);
        btnOk=(Button) findViewById(R.id.btnCityReg);


        adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1, android.R.id.text1, AllCtNames);
        lvadapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1, android.R.id.text1, ctNames);
        lvAreas.setAdapter(lvadapter);
        spnCity=(Spinner) findViewById(R.id.spnOstan);
        spnCity.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (ostanArrayList.size()>0) {
                    myArea = ostanArrayList.get(position);
                    Toast.makeText(AddCityActivity.this, "city" + myArea.getAfname(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        textView.setTypeface(yekan);
        textView.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {
                if (s.length() > 0) {
                    int i = 0;
                    ctNames.clear();
                    for (Area marea : areaArrayList) {

                        if (marea.getAfname().contains(s.toString())) {

                            ctNames.add(marea.getAfname());
                            lvadapter.notifyDataSetChanged();
                        }
                    }


                } else if (s.length() == 0) {
                    ctNames.clear();
                    ctNames.addAll(AllCtNames);

                    adapter.notifyDataSetChanged();
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
        mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.shop_fragment);
        mapFragment.getMapAsync(this);

        if (showMap) {
            mapFragment.getView().setVisibility(View.GONE);
            btnFinalCity.setVisibility(View.GONE);
            showMap=false;
        }

        if (AndyUtils.isNetworkAvailable(this)){
            reqArea();
            reqOstan();
        }else {
            Toast.makeText(this, "اینترنت وصل نیست", Toast.LENGTH_SHORT).show();
        }

        btnOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Toast.makeText(StoreRegActivity.this, "lat="+lat+" lng="+lng, Toast.LENGTH_LONG).show();
                kansel = false;
                if (rdCitizen.isChecked()){
                    isGov="0";
                }else {
                    isGov="1";
                }
                if (rdVillage.isChecked()){
                    isCity="0";
                }else {
                    isCity="1";
                }
                edtEname.setError(null);
                edtMobile.setError(null);
                edtShahrestan.setError(null);
                textView.setError(null);

                if (textView.getText().toString().length() < 1) {
                    textView.setError("نام کوتاه است");
                    focusView = textView;
                    kansel = true;
                }

                if (edtEname.getText().toString().length() < 1) {
                    edtEname.setError("نام لاتین کوتاه است");
                    focusView = edtEname;
                    kansel = true;
                }

                if (edtMobile.getText().toString().length() != 11) {
                    edtMobile.setError("شماره موبایل 11رقمی است");
                    focusView = edtMobile;
                    kansel = true;
                }
                if (edtShahrestan.getText().toString().length() < 1) {
                    edtShahrestan.setError("شماره ثابت حداقل 4 رقمی است");
                    focusView = edtShahrestan;
                    kansel = true;
                }

                if (!kansel) {
                    btnOk.setVisibility(View.GONE);
                    mapFragment.getView().setVisibility(View.VISIBLE);
                    btnFinalCity.setVisibility(View.VISIBLE);
                    mMap.clear();
                    mMap.addMarker(new MarkerOptions().position(latLng).title("موقعیت شما").snippet("روی نقشه کلیک کنید"));
                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 16));
                    showMap = true;
                    getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

                } else {
                    focusView.requestFocus();
                }



            }
        });

        btnFinalCity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btnOk.setVisibility(View.VISIBLE);
                mapFragment.getView().setVisibility(View.GONE);
                btnFinalCity.setVisibility(View.GONE);
                btnOk.setEnabled(false);
                mobile=edtMobile.getText().toString();
                fname=textView.getText().toString() ;
                ename=edtEname.getText().toString();
                shrstn=edtShahrestan.getText().toString();

                registerOstan();
            }
        });


        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        populateGPS();
    }

    public void registerOstan() {
        RequestQueue queue = Volley.newRequestQueue(this);
        String url = OSTAN_REG;
        progressBar.setVisibility(View.VISIBLE);
        Log.d(TAG, "registerOstan: Sent"+lat+lng+" mob:"+mobile);
        StringRequest postRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>()
                {
                    public static final String TAG = "change city";

                    @Override
                    public void onResponse(String response) {
                        JSONArray jsonArray= null;
                        Log.d(TAG, "onResponse: response from Ostan "+response);
                        progressBar.setVisibility(View.GONE);
                            Intent popup=new Intent(AddCityActivity.this,PopupActivity.class);
                            popup.putExtra("title", "درخواست شهر/روستای جدید");
                            popup.putExtra("body", "درخواست شما با موفقیت ثبت گردید بعد از تایید مسئول مربوطه حداکثر تا 2 روز کاری منتشر خواهد شد.");
                            popup.putExtra("score", "تشکر");
                            startActivity(popup);
                        finish();
                    }
                },
                new Response.ErrorListener()
                {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        progressBar.setVisibility(View.GONE);
                        btnOk.setEnabled(true);
                        Log.d("ERROR","Login area error => "+error.toString());
                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String>params = new HashMap<String,String>();

                params.put("fname",fname+"");
                params.put("ename",ename+"" );
                params.put("lat",lat+"" );
                params.put("lng",lng+"" );
                params.put("ostan",myArea.getState()+"" );
                params.put("shrstn",shrstn+"" );
                params.put("mobile",mobile+"" );
                params.put("iscity",isCity+"" );
                params.put("isgov",isGov+"" );



                return params;
            }
        };
        queue.add(postRequest);
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
                            ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<String>
                                    (AddCityActivity.this, android.R.layout.simple_spinner_item,
                                            ostanNames); //selected item will look like a spinner set from XML
                            spinnerArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                            spnCity.setAdapter(spinnerArrayAdapter);




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

    public void reqArea() {
        RequestQueue queue = Volley.newRequestQueue(this);
        String url = AREA_URL;
        progressBar.setVisibility(View.VISIBLE);
        StringRequest postRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>()
                {
                    public static final String TAG = "change city";

                    @Override
                    public void onResponse(String response) {
                        JSONArray jsonArray= null;
                        Log.d(TAG, "onResponse: response from area"+response);
                        ctNames.clear();
                        progressBar.setVisibility(View.GONE);
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
                                ctNames.add(jsonObject.getString("afname"));

                            }
                            AllCtNames.addAll(ctNames);

                            ArrayAdapter<String> adapter = new ArrayAdapter<String>(AddCityActivity.this,
                                    android.R.layout.simple_dropdown_item_1line, AllCtNames);
                            textView.setAdapter(adapter);


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

//                params.put("name", );

                return params;
            }
        };
        queue.add(postRequest);
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
        // Add a marker in  the camera.
        LatLng sydney = new LatLng(Double.valueOf(lat),Double.valueOf(lng));

        mMap.addMarker(new MarkerOptions().position(sydney).title(" موقعیت شما "));
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(sydney,13));
        mMap.setOnMapClickListener(this);
        mMap.setOnMapLongClickListener(this);
        mMap.setOnMarkerDragListener(this);
    }


    @Override
    public void onMapClick(LatLng point) {
        mMap.clear();
        mMap.animateCamera(CameraUpdateFactory.newLatLng(point));
        mMap.addMarker(new MarkerOptions()
                .position(point)
                .draggable(true));
        lat=String.valueOf(point.latitude);
        lng=String.valueOf(point.longitude);
    }

    @Override
    public void onMapLongClick(LatLng point) {
        mMap.clear();
        mMap.addMarker(new MarkerOptions()
                .position(point)
                .draggable(true));
        lat=String.valueOf(point.latitude);
        lng=String.valueOf(point.longitude);
    }

    @Override
    public void onMarkerDrag(Marker marker) {
    }

    @Override
    public void onMarkerDragEnd(Marker marker) {
        lat=marker.getPosition().latitude+"";
        lng=marker.getPosition().longitude+"";
    }

    @Override
    public void onMarkerDragStart(Marker marker) {
//        tvLocInfo.setText("Marker " + marker.getId() + " DragStart");
//        Toast.makeText(getApplicationContext(), "drag start", Toast.LENGTH_SHORT).show();
    }

    private void populateGPS() {
        if (!mayRequestLocation()) {
            return;
        }
        GPSTracker gps =new GPSTracker(this);
        lat=gps.getLatitude()+"";
        lng=gps.getLongitude()+"";
        if (!lat.equals("0.0")) {
            SharedPreferences.Editor SP = PreferenceManager.getDefaultSharedPreferences(getBaseContext()).edit();
            lat=gps.getLatitude()+"";
            lng=gps.getLongitude()+"";
            SP.putString("lat", lat);
            SP.putString("lng", lng);
            SP.apply();
            latLng=new LatLng(gps.getLatitude(),gps.getLongitude());
        }else{
            final CharSequence[] items = {"تایید", "انصراف"};
            android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(AddCityActivity.this);
            builder.setTitle("لطفا جی پی اس را روشن کنید");
            builder.setItems(items, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int item) {

                    if (items[item].equals("تایید")) {
                        Intent i = new
                                Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                        startActivity(i);
                        dialog.dismiss();
                    } else if (items[item].equals("انصراف")) {
                        SharedPreferences SP1;
                        SP1 = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
                        lat=SP1.getString("lat","35.711");
                        lng=SP1.getString("lng","50.912");
                        dialog.dismiss();
                        latLng=new LatLng(35.711,50.912);
                        finish();
                    }
                }
            });
            builder.show();

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
            Snackbar.make(btnOk, "لطفا دسترسی به جی پی اس را فعال کنید.", Snackbar.LENGTH_INDEFINITE)
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

}
