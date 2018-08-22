package com.idpz.instacity.Share;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ProgressBar;
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
import com.idpz.instacity.Area;
import com.idpz.instacity.Home.AddCityActivity;
import com.idpz.instacity.Home.HomeActivity;
import com.idpz.instacity.Home.VisitSearchActivity;
import com.idpz.instacity.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static android.content.Intent.FLAG_ACTIVITY_REORDER_TO_FRONT;

public class FilterCityActivity extends AppCompatActivity {

    private static final String TAG = "FilterCityActivity";
    SharedPreferences SP1;
    Spinner spnCity,spnOstan;
    ArrayList<String> ostanNames =new ArrayList<>();
    ArrayList<Area> ostanArrayList=new ArrayList<>();
    ArrayList<String> ctNames =new ArrayList<>();
    ArrayList<String> AllCtNames =new ArrayList<>();
    TextView title;
    ArrayList<Area> areaArrayList=new ArrayList<>();
    ArrayList<Area> areaSelectList=new ArrayList<>();
    ProgressBar progressBar;
    boolean areaFlag=false;

    String filterOstan ="",filterCity="",filterVillage="",OSTAN_URL="",AREA_URL="",filter="";
    ArrayAdapter<String> adapterOstan;
    ArrayAdapter<String> adapterCity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_filter_city);

        SP1 = PreferenceManager.getDefaultSharedPreferences(FilterCityActivity.this);
        filter = SP1.getString("ftrostan", "");

        OSTAN_URL=getString(R.string.server)+"/j/getostan.php";
        AREA_URL=getString(R.string.server)+"/i/getarea.php";
        title=(TextView)findViewById(R.id.txtFilterStatus);
        Button btnApply=findViewById(R.id.btnFilterApply);
        progressBar=findViewById(R.id.progressBar);




        adapterCity = new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1, android.R.id.text1, AllCtNames);

        spnCity=(Spinner) findViewById(R.id.spnCity);
        spnCity.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (ostanArrayList.size()>0) {
                    if (areaArrayList.size()>0) {
                        if (position==0){
                            filter=filterOstan;
                            title.setText(filter);
                        }else {
                            filterCity = areaArrayList.get(position).getState();
                            filter=filterCity;
                            title.setText(filter);
                        }
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        adapterOstan = new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1, android.R.id.text1, ostanNames);

        spnOstan=(Spinner) findViewById(R.id.spnOstan);
        spnOstan.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (ostanArrayList.size()>0) {
                    if (ostanArrayList.size()>0) {
                        if (position == 0) {
                            filter="";
                            title.setText(filter);
                        } else {
                            filterOstan = ostanArrayList.get(position).getAename();
                            ctNames.clear();
                            areaArrayList.clear();
                            ctNames.add("همه شهر ها");
                            areaArrayList.add(new Area(1,"0","0",0,0,1,"",0,0,"","","همه شهر/روستاها"));
                            for (Area myar : areaSelectList) {
                                if (myar.getState().startsWith(filterOstan)) {
                                    areaArrayList.add(myar);
                                    ctNames.add(myar.getAfname());
                                }
                            }
                            adapterCity.notifyDataSetChanged();
                            filter=filterOstan;
                            title.setText(filter);
                        }

                        title.setText(filter);
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        btnApply.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences.Editor SP = PreferenceManager.getDefaultSharedPreferences(FilterCityActivity.this).edit();
                SP.putString("filter", filter);
                SP.apply();
                Intent intent1=new Intent(FilterCityActivity.this, VisitSearchActivity.class);
                intent1.putExtra("filter", filter);
                intent1.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
//                intent1.addFlags(FLAG_ACTIVITY_REORDER_TO_FRONT);
                startActivity(intent1);
                finish();
            }
        });

        DisplayMetrics dm =new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);

        int width=dm.widthPixels;
        int height=dm.heightPixels;
        getWindow().setLayout((int)(width*.8),(int)(height*.5));

        WindowManager.LayoutParams params=getWindow().getAttributes();
        params.gravity= Gravity.CENTER;
        params.x=0;
        params.y=-20;
        getWindow().setAttributes(params);

        reqArea();
        reqOstan();
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
                            ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<String>
                                    (FilterCityActivity.this, android.R.layout.simple_spinner_item,
                                            ostanNames); //selected item will look like a spinner set from XML
                            spinnerArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                            spnOstan.setAdapter(spinnerArrayAdapter);




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
                        ctNames.add("همه شهر/روستاها");
                        areaArrayList.add(new Area(1,"all","0",0,0,1,"",0,0,"","","همه شهر/روستاها"));
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
                            areaSelectList.addAll(areaArrayList);
                            AllCtNames.addAll(ctNames);

                            ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<String>
                                    (FilterCityActivity.this, android.R.layout.simple_spinner_item,
                                            ctNames); //selected item will look like a spinner set from XML
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

//                params.put("name", );

                return params;
            }
        };
        queue.add(postRequest);
    }

}
