package com.idpz.instacity.Profile;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.idpz.instacity.R;
import com.idpz.instacity.models.GiftPlace;
import com.idpz.instacity.utils.BottomNavigationViewHelper;
import com.idpz.instacity.utils.GiftPlacesAdapter;
import com.ittianyu.bottomnavigationviewex.BottomNavigationViewEx;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class LikesActivity extends AppCompatActivity {

    private static final String TAG = "LikesActivity";
    private static final int ACTIVITY_NUM=3;
    ArrayList<GiftPlace> dataModels;
    ListView lvGiftPlaces;
    String mny="0",spend="0";
    GiftPlacesAdapter giftPlacesAdapter;
    String fullServer="",profileImgUrl="",mob="";
    TextView txtDisplayName,txtDescription, txtScore,txtTabUserName,txtMyMobile;
    CircleImageView imgProfile;

    Boolean giftFlag=false,connected=false,moneyFlag=false,remain=true;
    String state="";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_like);
        Log.d(TAG, "onCreate: strating");

//        setupBottomNavigationView();
        SharedPreferences SP1;
        SP1 = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
        state=SP1.getString("state", "0");
        fullServer=getString(R.string.server)+"/j/gift.php";

        profileImgUrl = SP1.getString("pic", "0");

        txtDisplayName=(TextView) findViewById(R.id.txtDisplay_name);
        txtDescription=(TextView) findViewById(R.id.txtDescription);
        txtScore =(TextView) findViewById(R.id.txtMyScore);
        txtMyMobile =(TextView) findViewById(R.id.txtMyMobile);
        txtTabUserName=(TextView) findViewById(R.id.txtTabUsername);
        imgProfile=(CircleImageView) findViewById(R.id.profile_photoLikes);

        imgProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent in1=new Intent(LikesActivity.this, MyProfileActivity.class);
                startActivity(in1);
            }
        });


        Glide.with(this).load(getString(R.string.server)+"/assets/images/users/"+profileImgUrl)
                .thumbnail(0.5f)
                .into(imgProfile);

        dataModels = new ArrayList<>();
        dataModels.add(new GiftPlace(0,"مرکزی موجود نیست"," "," "," "," "," "));
        lvGiftPlaces=(ListView) findViewById(R.id.listPlaces);
        giftPlacesAdapter=new GiftPlacesAdapter(this,dataModels);
        lvGiftPlaces.setAdapter(giftPlacesAdapter);


        SharedPreferences SP = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
        String melli=SP.getString("pass", "0");
        if (melli.equals("0"))melli="کد دریافت جایزه:"+melli;
        txtDescription.setText("کد دریافت جایزه:"+melli);

        int bonus=Integer.valueOf(SP.getString("money", "100"));
        txtScore.setText("امتیاز="+String.valueOf(bonus));
        txtDisplayName.setText(SP.getString("myname", "هاشم عابدی"));
        txtTabUserName.setText(SP.getString("myname", ""));
        mob=SP.getString("mobile", "");
        txtMyMobile.setText("شماره موبایل:"+mob);


            reqGifts();



    }
    // تنظیم نوار پایین برنامه
    private void setupBottomNavigationView(){
        Log.d(TAG,"seting up bottom navigation view");
        BottomNavigationViewEx bottomNavigationViewEx=(BottomNavigationViewEx) findViewById(R.id.bottomNavViewBar);
        BottomNavigationViewHelper.setupBottomNavigationView(bottomNavigationViewEx);
        BottomNavigationViewHelper.enableNavigation(LikesActivity.this,bottomNavigationViewEx);
        Menu menu=bottomNavigationViewEx.getMenu();
        MenuItem menuItem=menu.getItem(ACTIVITY_NUM);
        menuItem.setChecked(true);
    }

    public void reqGifts() {
        RequestQueue queue = Volley.newRequestQueue(this);
        String url = fullServer;
        StringRequest postRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>()
                {
                    @Override
                    public void onResponse(String response) {

                        giftFlag=true;
                        dataModels.clear();
                        Log.d(TAG, "onResponse: Gifts="+response);

                        JSONArray jsonArray = null;
                        try {
                            jsonArray = new JSONArray(response);
                            GiftPlace giftPlace;

                            JSONObject jsonObject = jsonArray.getJSONObject(0);


                            for (int i = jsonArray.length(); i > 0; i--) {
                                jsonObject = jsonArray.getJSONObject(i - 1);
                                giftPlace=new GiftPlace();
                                giftPlace.setName(jsonObject.getString("name"));
                                giftPlace.setTel(jsonObject.getString("tel"));
                                giftPlace.setAddress(jsonObject.getString("address"));
                                giftPlace.setScore(jsonObject.getString("discount"));
                                giftPlace.setDiscount(jsonObject.getString("score"));
                                giftPlace.setPic(getString(R.string.server)+"/assets/images/places/"+jsonObject.getString("pic"));
                                giftPlace.setId(jsonObject.getInt("id"));


                                dataModels.add(giftPlace);



                            }
                            giftPlacesAdapter.notifyDataSetChanged();

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
                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String>params = new HashMap<String,String>();
                params.put("state", state);
//                params.put("limit", "20");
                return params;
            }
        };
        queue.add(postRequest);

    }
    public void reqScore() {
        RequestQueue queue = Volley.newRequestQueue(LikesActivity.this);
        String url = getString(R.string.server)+"/j/score.php";
        StringRequest postRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        moneyFlag = true;


                        JSONArray jsonArray = null;
                        try {
                            jsonArray = new JSONArray(response);

                            JSONObject jsonObject = jsonArray.getJSONObject(0);

                            for (int i = jsonArray.length(); i > 0; i--) {
                                jsonObject = jsonArray.getJSONObject(i - 1);

                                mny =jsonObject.getString("money");
                                spend =jsonObject.getString("spend");

                            }

                            SharedPreferences.Editor SP2 = PreferenceManager.getDefaultSharedPreferences(getBaseContext()).edit();
                            SP2.putString("money", mny);
                            SP2.putString("spend", spend);
                            SP2.apply();
                            txtScore.setText(String.valueOf(Integer.valueOf(mny)-Integer.valueOf(spend)));

                        } catch (JSONException e) {
                            e.printStackTrace();

                        }


                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // TODO Auto-generated method stub
                        Log.d("ERROR", "error => " + error.toString());
                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("ph", mob);
                params.put("state", state);
                return params;
            }
        };
        queue.add(postRequest);

    }


}
