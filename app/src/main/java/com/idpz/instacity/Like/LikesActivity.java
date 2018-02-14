package com.idpz.instacity.Like;

import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.idpz.instacity.R;
import com.idpz.instacity.models.GiftPlace;
import com.idpz.instacity.utils.BottomNavigationViewHelper;
import com.idpz.instacity.utils.GiftPlacesAdapter;
import com.ittianyu.bottomnavigationviewex.BottomNavigationViewEx;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

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
    GiftPlacesAdapter giftPlacesAdapter;
    String fullServer="",profileImgUrl="";
    TextView txtDisplayName,txtDescription, txtScore,txtTabUserName,txtMyMobile;
    CircleImageView imgProfile;
    private DisplayImageOptions options;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_like);
        Log.d(TAG, "onCreate: strating");

        fullServer=getString(R.string.server)+"/i/gift.php";
        setupBottomNavigationView();
        SharedPreferences SP1;
        SP1 = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
        profileImgUrl = SP1.getString("pic", "0");
        options = new DisplayImageOptions.Builder()
                .showImageOnLoading(R.drawable.ic_stub)
                .showImageForEmptyUri(R.drawable.ic_empty)
                .showImageOnFail(R.drawable.ic_error)
                .cacheInMemory(true)
                .cacheOnDisk(true)
                .considerExifParams(true)
                .bitmapConfig(Bitmap.Config.RGB_565)
                .build();
        txtDisplayName=(TextView)findViewById(R.id.txtDisplay_name);
        txtDescription=(TextView)findViewById(R.id.txtDescription);
        txtScore =(TextView)findViewById(R.id.txtMyScore);
        txtMyMobile =(TextView)findViewById(R.id.txtMyMobile);
        txtTabUserName=(TextView)findViewById(R.id.txtTabUsername);
        imgProfile=(CircleImageView)findViewById(R.id.profile_photoLikes);

        ImageLoader.getInstance().displayImage(profileImgUrl,imgProfile,options);

        dataModels = new ArrayList<>();
        lvGiftPlaces=(ListView)findViewById(R.id.listPlaces);
        giftPlacesAdapter=new GiftPlacesAdapter(this,dataModels);
        lvGiftPlaces.setAdapter(giftPlacesAdapter);


        SharedPreferences SP = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
        String melli=SP.getString("melliid", "0");
        if (melli.equals("0"))melli="بدون شماره ملی امکان دریافت جایزه نیست";
        txtDescription.setText("شماره ملی:"+melli);

        int bonus=Integer.valueOf(SP.getString("numPosts", "1"))*10+100;
        txtScore.setText("امتیاز="+String.valueOf(bonus));
        txtDisplayName.setText(SP.getString("myname", "هاشم عابدی"));
        txtTabUserName.setText(SP.getString("myname", ""));
        txtMyMobile.setText("شماره موبایل:"+SP.getString("mobile", ""));


    reqGifts();



    }
    // تنظیم نوار پایین برنامه
    private void setupBottomNavigationView(){
        Log.d(TAG,"seting up bottom navigation view");
        BottomNavigationViewEx bottomNavigationViewEx=(BottomNavigationViewEx)findViewById(R.id.bottomNavViewBar);
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


                        int count = 0;

                        JSONArray jsonArray = null;
                        try {
                            jsonArray = new JSONArray(response);
                            GiftPlace giftPlace;
                            JSONObject jsonObject = jsonArray.getJSONObject(0);
                            count = 0;

                            for (int i = jsonArray.length(); i > 0; i--) {
                                jsonObject = jsonArray.getJSONObject(i - 1);
                                giftPlace=new GiftPlace();
                                giftPlace.setName(jsonObject.getString("name"));
                                giftPlace.setDiscount(jsonObject.getString("discount"));
                                giftPlace.setPic(getString(R.string.server)+"/img/places/"+jsonObject.getString("pic"));
                                giftPlace.setId(jsonObject.getInt("id"));


                                dataModels.add(giftPlace);


                                count++;
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
//                params.put("table", "govs");
//                params.put("limit", "20");
                return params;
            }
        };
        queue.add(postRequest);

    }


}
