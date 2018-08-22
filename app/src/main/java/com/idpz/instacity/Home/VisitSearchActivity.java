package com.idpz.instacity.Home;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.idpz.instacity.Profile.ProfileActivity;
import com.idpz.instacity.R;
import com.idpz.instacity.Share.FilterCityActivity;
import com.idpz.instacity.Share.PopupActivity;
import com.idpz.instacity.models.Ads;
import com.idpz.instacity.models.Arts;
import com.idpz.instacity.models.Villa;
import com.idpz.instacity.models.VisitPlace;
import com.idpz.instacity.utils.AdsAdapter;
import com.idpz.instacity.utils.AdsSearchAdapter;
import com.idpz.instacity.utils.BottomNavigationViewHelper;
import com.idpz.instacity.utils.VillaAdapter;
import com.idpz.instacity.utils.VillaSearchAdapter;
import com.idpz.instacity.utils.VisitSearchAdapter;
import com.idpz.instacity.utils.ArtSearchAdapter;
import com.ittianyu.bottomnavigationviewex.BottomNavigationViewEx;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class VisitSearchActivity extends AppCompatActivity {

    private static final String TAG = "VisitSearchActivity";

    ArrayList<VisitPlace> dataModels;
    ArrayList<VisitPlace> searchModels;
    AdsSearchAdapter adsAdapter;
    ArrayList<Ads> adsModels;
    ArrayList<Ads> adsSrchModels;
    ArtSearchAdapter artAdapter;
    ArrayList<Arts> artModels;
    ArrayList<Arts> artSrchModels;
    ArrayList<Villa> villaModels;
    ArrayList<Villa> villaSrchModels;
    VillaSearchAdapter villaAdapter;
    String fullServer="",filter="0";
    boolean visitFlag=false,adsFlag=false,artsFlag=false,villaFlag=false;
    GridView grid;
    VisitSearchAdapter visitSearchAdapter;
    ProgressBar progressBar;
    ImageView imgRetry;
    Spinner spnVisitCats;
    boolean connected=false;
    int cat=1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_visit_places);
        SharedPreferences SP1;
        SP1 = PreferenceManager.getDefaultSharedPreferences(VisitSearchActivity.this);
        filter = SP1.getString("filter", "");

        fullServer=getString(R.string.server)+"/j/coni.php";
        setupBottomNavigationView();
        dataModels = new ArrayList<>();
        searchModels = new ArrayList<>();
        visitSearchAdapter = new VisitSearchAdapter(VisitSearchActivity.this, dataModels);
        grid =(GridView) findViewById(R.id.gridView);
        grid.setAdapter(visitSearchAdapter);

        adsModels= new ArrayList<>();
        adsSrchModels= new ArrayList<>();
        artSrchModels= new ArrayList<>();
        villaSrchModels= new ArrayList<>();

        adsAdapter= new AdsSearchAdapter(VisitSearchActivity.this,adsModels);
        artModels= new ArrayList<>();
        artAdapter= new ArtSearchAdapter(VisitSearchActivity.this,artModels);
        villaModels= new ArrayList<>();
        villaAdapter= new VillaSearchAdapter(VisitSearchActivity.this,villaModels);

        ImageView imgFilter=findViewById(R.id.imgSearchVisit);
        if (filter.length()>1){
            imgFilter.setImageDrawable(getResources().getDrawable(R.drawable.ic_filter_green));
        }else {
            imgFilter.setImageDrawable(getResources().getDrawable(R.drawable.ic_filter));
        }
        imgFilter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent in1=new Intent(VisitSearchActivity.this, FilterCityActivity.class);
                startActivity(in1);
            }
        });

        reqVisitPlace();

        progressBar=(ProgressBar) findViewById(R.id.progressVisitSearch);
        spnVisitCats=(Spinner) findViewById(R.id.spnVisitCats);

        EditText edtSearch=(EditText) findViewById(R.id.txtTabUsername);
        edtSearch.addTextChangedListener(new TextWatcher() {

            @Override
            public void afterTextChanged(Editable s) {
                if (s.length()>0){
//                    Toast.makeText(VisitSearchActivity.this, "cat="+cat, Toast.LENGTH_SHORT).show();
                    switch (cat){
                        case 1:
                            Log.d(TAG, "afterTextChanged: visit Search");
                            searchModels.clear();
                            for (VisitPlace visitPlace:dataModels){
                                if (visitPlace.getMemo().contains(s)&&visitPlace.getState().startsWith(filter)){
                                    searchModels.add(visitPlace);
                                }
                            }
                            visitSearchAdapter = new VisitSearchAdapter(VisitSearchActivity.this, searchModels);
                            grid.setAdapter(visitSearchAdapter);
                            break;
                        case 2:
                            Log.d(TAG, "afterTextChanged: ART Search");
                            artSrchModels.clear();
                            for (Arts visitPlace:artModels){
                                if (visitPlace.getName().contains(s)||visitPlace.getMaterial().contains(s)){
                                    artSrchModels.add(visitPlace);
                                }
                            }

                            artAdapter = new ArtSearchAdapter(VisitSearchActivity.this, artSrchModels);
                            grid.setAdapter(artAdapter);
                            break;
                        case 3:
                            Log.d(TAG, "afterTextChanged: VILLA Search");
                            villaSrchModels.clear();
                            for (Villa visitPlace:villaModels){
                                if (visitPlace.getMemo().contains(s)||visitPlace.getAddress().contains(s)||visitPlace.getFacility().contains(s)){
                                    villaSrchModels.add(visitPlace);
                                }
                            }
                            villaAdapter = new VillaSearchAdapter(VisitSearchActivity.this, villaSrchModels);
                            grid.setAdapter(villaAdapter);
                            break;
                        case 4:
                            Log.d(TAG, "afterTextChanged: ADS Search");
                            adsSrchModels.clear();
                            for (Ads visitPlace:adsModels){
                                if (visitPlace.getMemo().contains(s)){
                                    adsSrchModels.add(visitPlace);
                                }
                            }
                            adsAdapter = new AdsSearchAdapter(VisitSearchActivity.this, adsSrchModels);
                            grid.setAdapter(adsAdapter);
                            break;
//                        default:
//                            searchModels.clear();
//                            for (VisitPlace visitPlace:dataModels){
//                                if (visitPlace.getMemo().contains(s)){
//                                    searchModels.add(visitPlace);
//                                }
//                            }
//                            visitSearchAdapter = new VisitSearchAdapter(VisitSearchActivity.this, searchModels);
//                            grid.setAdapter(visitSearchAdapter);
//                            break;
                    }

                }else if (s.length()==0){
                    searchModels.clear();
                    visitSearchAdapter = new VisitSearchAdapter(VisitSearchActivity.this, dataModels);
                    adsAdapter= new AdsSearchAdapter(VisitSearchActivity.this,adsModels);
                    artAdapter= new ArtSearchAdapter(VisitSearchActivity.this,artModels);
                    villaAdapter= new VillaSearchAdapter(VisitSearchActivity.this,villaModels);
                    setFilter(filter);
                    switch (cat){
                        case 1:
                            grid.setAdapter(visitSearchAdapter);
                            break;
                        case 2:
                            grid.setAdapter(artAdapter);
                            break;
                        case 3:
                            grid.setAdapter(villaAdapter);
                            break;
                        case 4:
                            grid.setAdapter(adsAdapter);
                            break;
//                        default:
//                            grid.setAdapter(visitSearchAdapter);
//                            break;
                    }

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

        imgRetry=(ImageView) findViewById(R.id.imgVSRetry);
        imgRetry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: ");
                if (isConnected()) {
                    imgRetry.setVisibility(View.GONE);
                    progressBar.setVisibility(View.VISIBLE);
                    switch (cat){
                        case 1:
                            reqVisitPlace();
                            cat=1;
                            grid.setAdapter(visitSearchAdapter);
                            break;
                        case 2:
                            reqArts();
                            cat=2;
                            grid.setAdapter(artAdapter);
                            break;
                        case 3:
                            reqVillas();
                            cat=3;
                            grid.setAdapter(villaAdapter);
                            break;
                        case 4:
                            reqAds();
                            cat=4;
                            grid.setAdapter(adsAdapter);
                            break;
                        default:
                            reqVisitPlace();
                            cat=1;
                            grid.setAdapter(visitSearchAdapter);
                            break;
                    }
                }

            }
        });

        spnVisitCats.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                switch (position){
                    case 0:
                        if (!visitFlag)
                        reqVisitPlace();
                        cat=1;
                        grid.setAdapter(visitSearchAdapter);
                        break;
                    case 1:
                        if (!artsFlag)
                        reqArts();
                        cat=2;
                        grid.setAdapter(artAdapter);
                        break;
                    case 2:
                        if (!villaFlag)
                        reqVillas();
                        cat=3;
                        grid.setAdapter(villaAdapter);
                        break;
                    case 3:
                        if (!adsFlag)
                        reqAds();
                        cat=4;
                        grid.setAdapter(adsAdapter);
                        break;
                        default:
                            reqVisitPlace();
                            cat=1;
                            grid.setAdapter(visitSearchAdapter);
                            break;
                }
//                Toast.makeText(VisitSearchActivity.this, "select:"+position, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });




        if (filter.length()>0){

            setFilter(filter);
            Log.d(TAG, "onCreate: filter="+filter);
        }

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

    }

    @Override
    protected void onPostResume() {
        super.onPostResume();
        Intent intentex = getIntent();
        if(intentex.hasExtra("filter")) {

            filter = intentex.getStringExtra("filter");
            Log.d(TAG, "onResume: filter="+filter);
            setFilter(filter);
        }

    }

    private void setFilter( String filter)
    {
        Log.d(TAG, "setFilter: Started="+filter);
        if (dataModels.size()>0) {
            Log.d(TAG, "setFilter:  On Visit="+filter);
            searchModels.clear();
            for (VisitPlace visitPlace : dataModels) {
                if (visitPlace.getState().startsWith(filter)) {
                    searchModels.add(visitPlace);
                }
            }

        visitSearchAdapter = new VisitSearchAdapter(VisitSearchActivity.this, searchModels);
//        grid.setAdapter(visitSearchAdapter);
    }
    if (artModels.size()>0) {
        artSrchModels.clear();
        for (Arts visitPlace : artModels) {
            if (visitPlace.getState().startsWith(filter)) {
                artSrchModels.add(visitPlace);
            }
        }
        artAdapter = new ArtSearchAdapter(VisitSearchActivity.this, artSrchModels);
//        grid.setAdapter(artAdapter);
    }
    if (villaModels.size()>0) {
        villaSrchModels.clear();
        for (Villa visitPlace : villaModels) {
            if (visitPlace.getState().startsWith(filter)) {
                villaSrchModels.add(visitPlace);
            }
        }
        villaAdapter = new VillaSearchAdapter(VisitSearchActivity.this, villaSrchModels);
//        grid.setAdapter(villaAdapter);
    }
    if (adsModels.size()>0) {
        adsSrchModels.clear();
        for (Ads visitPlace : adsModels) {
            if (visitPlace.getState().startsWith(filter)) {
                adsSrchModels.add(visitPlace);
            }
        }
        adsAdapter = new AdsSearchAdapter(VisitSearchActivity.this, adsSrchModels);
//        grid.setAdapter(adsAdapter);
    }
        switch (cat){
            case 1:
                grid.setAdapter(visitSearchAdapter);
                break;
            case 2:
                grid.setAdapter(artAdapter);
                break;
            case 3:
                grid.setAdapter(villaAdapter);
                break;
            case 4:
                grid.setAdapter(adsAdapter);
                break;
//                        default:
//                            grid.setAdapter(visitSearchAdapter);
//                            break;
        }

    }

    public void reqVisitPlace() {
        RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
        String url = fullServer;
        StringRequest postRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        progressBar.setVisibility(View.GONE);
                        imgRetry.setVisibility(View.GONE);
                        visitFlag = true;
//                        pd.dismiss();
//                        swipeRefreshLayout.setRefreshing(false);
                        dataModels.clear();
                        Log.d(TAG, "onResponse: visit places recived");

                        JSONArray jsonArray ;
                        try {
                            jsonArray = new JSONArray(response);
                            VisitPlace visitPlace;
                            JSONObject jsonObject;
                            JSONArray jsonArray1=jsonArray.getJSONArray(0);

                            for (int i = jsonArray1.length(); i > 0; i--) {
                                jsonObject = jsonArray1.getJSONObject(i-1);

                                visitPlace = new VisitPlace();
                                visitPlace.setId(jsonObject.getInt("id"));
                                visitPlace.setName(jsonObject.getString("name"));
                                visitPlace.setYear(jsonObject.getString("pyear"));
                                visitPlace.setTicket(jsonObject.getString("ticket"));
                                visitPlace.setDays(jsonObject.getString("pdays"));
                                visitPlace.setLat(jsonObject.getString("lat"));
                                visitPlace.setLng(jsonObject.getString("lng"));
                                visitPlace.setHours(jsonObject.getString("phours"));
                                visitPlace.setTel(jsonObject.getString("tel"));
                                visitPlace.setAddress(jsonObject.getString("address"));
                                visitPlace.setMemo(jsonObject.getString("memo"));
                                visitPlace.setAlat(jsonObject.getString("alat"));
                                visitPlace.setAlng(jsonObject.getString("alng"));
                                visitPlace.setAename(jsonObject.getString("aename"));
                                visitPlace.setAfname(jsonObject.getString("afname"));
                                visitPlace.setState(jsonObject.getString("state"));

                                String server=jsonObject.getString("server");

                                visitPlace.setPic(server + "/assets/images/places/" + jsonObject.getString("pic"));
                                dataModels.add(visitPlace);

                            }
                            visitSearchAdapter.notifyDataSetChanged();
                            setFilter(filter);
                        } catch (JSONException e) {
                            e.printStackTrace();
                            progressBar.setVisibility(View.GONE);
                        }


                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        progressBar.setVisibility(View.GONE);
                        imgRetry.setVisibility(View.VISIBLE);
                        Log.d("ERROR", "error => " + error.toString());
//                        reqVisitPlace();
//                        failCount++;
                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("tbl", "visitplaces");
//                params.put("lim2", String.valueOf(lim2));
                return params;
            }
        };
        queue.add(postRequest);

    }

    // تنظیم نوار پایین برنامه
    private void setupBottomNavigationView(){
        Log.d(TAG,"seting up bottom navigation view");
        BottomNavigationViewEx bottomNavigationViewEx=(BottomNavigationViewEx) findViewById(R.id.bottomNavViewBar);
        BottomNavigationViewHelper.setupBottomNavigationView(bottomNavigationViewEx);
        BottomNavigationViewHelper.enableNavigation(VisitSearchActivity.this,bottomNavigationViewEx);
        Menu menu=bottomNavigationViewEx.getMenu();
        MenuItem menuItem=menu.getItem(2);
        menuItem.setChecked(true);
    }

    public boolean isConnected(){
        ConnectivityManager connectivityManager = (ConnectivityManager) VisitSearchActivity.this.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState() == NetworkInfo.State.CONNECTED ||
                connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState() == NetworkInfo.State.CONNECTED) {
            //we are connected to a network
            connected = true;

            Log.d(TAG, "visit search : connected="+connected);

            return true;

        } else {

            return false;
        }
    }


    public void reqAds() {
        RequestQueue queue = Volley.newRequestQueue(VisitSearchActivity.this);
        String url = fullServer;
        progressBar.setVisibility(View.VISIBLE);
        imgRetry.setVisibility(View.GONE);
        StringRequest postRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>()
                {
                    @Override
                    public void onResponse(String response) {
                        JSONArray jsonArray= null;
                        progressBar.setVisibility(View.GONE);
                        imgRetry.setVisibility(View.GONE);
//                        swipeRefreshLayout.setRefreshing(false);
                        try {
                            jsonArray = new JSONArray(response);
                            adsFlag=true;
                            JSONObject jsonObject;
                            adsModels.clear();
                            String all="";
                            JSONArray jsonArray1=jsonArray.getJSONArray(0);

                            for (int i=jsonArray1.length();i>0;i--) {
                                jsonObject = jsonArray1.getJSONObject(i-1);

                                Ads area=new Ads();
                                area.setId(jsonObject.getInt("id"));
                                area.setTitle(jsonObject.getString("title"));
                                area.setMemo(jsonObject.getString("memo"));
                                area.setAddress(jsonObject.getString("address"));
                                area.setTel(jsonObject.getString("tel"));
                                String server=jsonObject.getString("server");
                                area.setAlat(jsonObject.getString("alat"));
                                area.setAlng(jsonObject.getString("alng"));
                                area.setAename(jsonObject.getString("aename"));
                                area.setAfname(jsonObject.getString("afname"));
                                area.setState(jsonObject.getString("state"));
                                area.setPic(server+"/assets/images/ads/"+jsonObject.getString("pic"));

                                adsModels.add(area);

                            }
                            adsAdapter.notifyDataSetChanged();
                            setFilter(filter);

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
                        progressBar.setVisibility(View.GONE);
                        imgRetry.setVisibility(View.VISIBLE);
//                        txtczstatus.setText(error.toString()+"مشکلی در ارسال داده پیش آمده دوباره تلاش کنید");
                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String>params = new HashMap<String,String>();

                params.put("tbl","ads" );

                return params;
            }
        };
        queue.add(postRequest);
    }

    public void reqArts() {
        RequestQueue queue = Volley.newRequestQueue(VisitSearchActivity.this);
        String url = fullServer;
        progressBar.setVisibility(View.VISIBLE);
        StringRequest postRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>()
                {
                    @Override
                    public void onResponse(String response) {
                        JSONArray jsonArray= null;
                        Log.d(TAG, "onResponse: arts="+response);
                        progressBar.setVisibility(View.GONE);
                        imgRetry.setVisibility(View.GONE);
//                        swipeRefreshLayout.setRefreshing(false);
                        try {
                            jsonArray = new JSONArray(response);
                            artsFlag=true;
                            JSONObject jsonObject;
                            artModels.clear();
                            String all="";
                            JSONArray jsonArray1=jsonArray.getJSONArray(0);

                            for (int i=jsonArray1.length();i>0;i--) {
                                jsonObject = jsonArray1.getJSONObject(i-1);

                                Arts area=new Arts();
                                area.setId(jsonObject.getInt("id"));
                                area.setName(jsonObject.getString("name"));
                                area.setMemo(jsonObject.getString("memo"));
                                area.setOwner(jsonObject.getString("owner"));
                                area.setCode(jsonObject.getString("code"));
                                area.setColor(jsonObject.getString("color"));
                                area.setMaterial(jsonObject.getString("material"));
                                area.setPrice(jsonObject.getString("price"));
                                area.setType(jsonObject.getString("type"));
                                area.setWeight(jsonObject.getString("weight"));
                                String server=jsonObject.getString("server");
                                area.setAlat(jsonObject.getString("alat"));
                                area.setAlng(jsonObject.getString("alng"));
                                area.setAename(jsonObject.getString("aename"));
                                area.setAfname(jsonObject.getString("afname"));
                                area.setState(jsonObject.getString("state"));
                                area.setPic(server+"/assets/images/arts/"+jsonObject.getString("pic"));

                                artModels.add(area);

                            }
                            artAdapter.notifyDataSetChanged();
                            setFilter(filter);

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
                        progressBar.setVisibility(View.GONE);
                        imgRetry.setVisibility(View.VISIBLE);
//                        txtczstatus.setText(error.toString()+"مشکلی در ارسال داده پیش آمده دوباره تلاش کنید");

                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String>params = new HashMap<String,String>();

                params.put("tbl","arts" );

                return params;
            }
        };
        queue.add(postRequest);
    }


    public void reqVillas() {
        RequestQueue queue = Volley.newRequestQueue(VisitSearchActivity.this);
        String url = fullServer;
        progressBar.setVisibility(View.VISIBLE);
        StringRequest postRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>()
                {
                    @Override
                    public void onResponse(String response) {
                        JSONArray jsonArray= null;
                        progressBar.setVisibility(View.GONE);
                        imgRetry.setVisibility(View.GONE);
//                        swipeRefreshLayout.setRefreshing(false);
                        try {
                            jsonArray = new JSONArray(response);
                            villaFlag=true;
                            JSONObject jsonObject;
                            villaModels.clear();
                            String all="";
                            JSONArray jsonArray1=jsonArray.getJSONArray(0);

                            for (int i=jsonArray1.length();i>0;i--) {
                                jsonObject = jsonArray1.getJSONObject(i-1);

                                Villa villa=new Villa();
                                villa.setId(jsonObject.getInt("id"));
                                villa.setName(jsonObject.getString("name"));
                                villa.setMemo(jsonObject.getString("memo"));
                                villa.setArea(jsonObject.getString("area"));
                                villa.setPrice(jsonObject.getString("price"));
                                villa.setRoom(jsonObject.getString("room"));
                                villa.setFacility(jsonObject.getString("facility"));
                                villa.setTel(jsonObject.getString("tel"));
                                villa.setAddress(jsonObject.getString("adrs"));
                                villa.setLat(jsonObject.getString("lat"));
                                villa.setLng(jsonObject.getString("lng"));
                                villa.setAlat(jsonObject.getString("alat"));
                                villa.setAlng(jsonObject.getString("alng"));
                                villa.setAename(jsonObject.getString("aename"));
                                villa.setAfname(jsonObject.getString("afname"));
                                villa.setState(jsonObject.getString("state"));
                                String server=jsonObject.getString("server");

                                villa.setPic(server+"/assets/images/villas/"+jsonObject.getString("pic"));

                                villaModels.add(villa);
                                setFilter(filter);
                            }
                            villaAdapter.notifyDataSetChanged();


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
                        progressBar.setVisibility(View.GONE);
                        imgRetry.setVisibility(View.VISIBLE);
//                        txtczstatus.setText(error.toString()+"مشکلی در ارسال داده پیش آمده دوباره تلاش کنید");

                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String>params = new HashMap<String,String>();

                params.put("tbl", "villas");

                return params;
            }
        };
        queue.add(postRequest);
    }


}
