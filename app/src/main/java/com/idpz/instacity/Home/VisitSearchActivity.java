package com.idpz.instacity.Home;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.idpz.instacity.R;
import com.idpz.instacity.models.VisitPlace;
import com.idpz.instacity.utils.BottomNavigationViewHelper;
import com.idpz.instacity.utils.VisitSearchAdapter;
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
    String fullServer="http://idpz.ir/i/coni.php";
    boolean visitFlag=false;
    GridView grid;
    VisitSearchAdapter visitSearchAdapter;
    ProgressBar progressBar;
    ImageView imgRetry;
    boolean connected=false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_visit_places);

        imgRetry= findViewById(R.id.imgVSRetry);
        setupBottomNavigationView();
        dataModels = new ArrayList<>();
        searchModels = new ArrayList<>();
        visitSearchAdapter = new VisitSearchAdapter(VisitSearchActivity.this, dataModels);
        grid = findViewById(R.id.gridView);
        grid.setAdapter(visitSearchAdapter);
        reqVisitPlace();

        progressBar= findViewById(R.id.progressVisitSearch);

        EditText edtSearch= findViewById(R.id.txtTabUsername);
        edtSearch.addTextChangedListener(new TextWatcher() {

            @Override
            public void afterTextChanged(Editable s) {
                if (s.length()>0){
                    searchModels.clear();
                    for (VisitPlace visitPlace:dataModels){
                        if (visitPlace.getMemo().contains(s)){
                            searchModels.add(visitPlace);
                        }
                    }
                    visitSearchAdapter = new VisitSearchAdapter(VisitSearchActivity.this, searchModels);
                    grid.setAdapter(visitSearchAdapter);
                }else if (s.length()==0){
                    searchModels.clear();
                    visitSearchAdapter = new VisitSearchAdapter(VisitSearchActivity.this, dataModels);
                    grid.setAdapter(visitSearchAdapter);
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

        imgRetry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: ");
                if (isConnected()) {
                    imgRetry.setVisibility(View.GONE);
                    progressBar.setVisibility(View.VISIBLE);
                    reqVisitPlace();
                }

            }
        });





        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

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

                                String server=jsonObject.getString("server");

                                visitPlace.setPic(server + "/assets/images/places/" + jsonObject.getString("pic"));
                                dataModels.add(visitPlace);

                            }
                            visitSearchAdapter.notifyDataSetChanged();
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
//                params.put("lim1", String.valueOf(lim1));
//                params.put("lim2", String.valueOf(lim2));
                return params;
            }
        };
        queue.add(postRequest);

    }

    // تنظیم نوار پایین برنامه
    private void setupBottomNavigationView(){
        Log.d(TAG,"seting up bottom navigation view");
        BottomNavigationViewEx bottomNavigationViewEx= findViewById(R.id.bottomNavViewBar);
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


}
