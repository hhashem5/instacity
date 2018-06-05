package com.idpz.instacity.Profile;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.RelativeLayout;
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
import com.idpz.instacity.Area;
import com.idpz.instacity.R;
import com.idpz.instacity.models.Post;
import com.idpz.instacity.utils.BottomNavigationViewHelper;
import com.idpz.instacity.utils.CalendarTool;
import com.idpz.instacity.utils.galleryAdapter;
import com.ittianyu.bottomnavigationviewex.BottomNavigationViewEx;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class ProfileActivity extends AppCompatActivity {

    private static final String TAG = "ProfileActivity";
    String fullServer = "",ctName;
    private static final int ACTIVITY_NUM = 4;
    ArrayList<Post> dataModels;
    String mny="0",spend="0";
    galleryAdapter galAdapter;
    Context mContext;
    Boolean areaFlag=false,connected=false;
    Boolean reqFlag = false,moneyFlag=false;
    ArrayList<Area> areaArrayList=new ArrayList<>();
    private static final String AREA_URL = "http://idpz.ir/i/getarea.php";
//    ProgressBar progressBar;

    //widgets
    private ViewPager mViewPager;
    private FrameLayout mFrameLayout;
    private RelativeLayout mRelativeLayout;
    GridView grid;
    TextView txtNumPosts,txtbonus,txtDisplayName,txtDescription,txtMobile,txtEditPrifile,txtTabUserName,txtMosharekat,txtCurCt;
    ImageView imgPostMenu;
    String mob="0",profileImgUrl="", REG_USER_LAT ="";
    CircleImageView imgProfile;
    String server="";
    Button btnChangeCT,btnUseGift;
    boolean remain=true;
    int failcount=0,netState=3;
    SharedPreferences SP1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        Log.d(TAG, "onCreate: strating");
//        progressBar=(ProgressBar)findViewById(R.id.profileProgressBar);
//        progressBar.setVisibility(View.GONE);

        SP1 = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
        server=SP1.getString("server", "0");
        ctName=SP1.getString("ctname", "0");
        String numposts=SP1.getString("numPosts", "0");
        String spends=SP1.getString("spend", "0");
        String moneys=SP1.getString("money", "100");
        String myname=SP1.getString("myname", "0");
        String melliid=SP1.getString("melliid", "0");
        mob=SP1.getString("mobile", "0");
        fullServer = server+"/i/socialgal.php";

        if (myname.toLowerCase().equals("null"))myname="بی نام";

        mViewPager = findViewById(R.id.viewpager_container);
        mFrameLayout = findViewById(R.id.container);
        mRelativeLayout = findViewById(R.id.relLayoutParent);
        dataModels = new ArrayList<>();
        galAdapter = new galleryAdapter(ProfileActivity.this, dataModels);
        txtbonus= findViewById(R.id.txtBonus);
        txtNumPosts= findViewById(R.id.tvPosts);
        txtDisplayName= findViewById(R.id.txtDisplay_name);
        txtDescription= findViewById(R.id.txtDescription);
        txtMosharekat= findViewById(R.id.txtMosharekat);
        txtMobile= findViewById(R.id.website);
        txtCurCt= findViewById(R.id.txtCurrentCTProfile);
        txtEditPrifile= findViewById(R.id.textEditProfile);
        txtTabUserName= findViewById(R.id.txtTabUsername);
        imgPostMenu= findViewById(R.id.imgProfileMenu);
        btnChangeCT= findViewById(R.id.btnChangeCTPofile);
        btnUseGift= findViewById(R.id.btnUseGift);
        txtCurCt.setText(ctName);

        btnChangeCT.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(ProfileActivity.this,ChangeCityActivity.class);
                startActivity(intent);
            }
        });

        btnUseGift.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(ProfileActivity.this,LikesActivity.class);
                startActivity(intent);
            }
        });

        profileImgUrl = SP1.getString("pic", "0");




        txtNumPosts.setText(numposts);
        txtMosharekat.setText(spends);
        int bonus=Integer.valueOf(moneys);
        txtbonus.setText(String.valueOf(bonus));
        txtDisplayName.setText(myname);
        txtTabUserName.setText(myname);
        txtDescription.setText(melliid);

        txtMobile.setText(mob);

        imgProfile= findViewById(R.id.profile_photoProfile);

        Glide.with(this).load(getString(R.string.server)+"/assets/images/users/"+profileImgUrl)
                .thumbnail(0.5f)
                .crossFade()
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .placeholder(R.drawable.nopic)
                .into(imgProfile);



        txtEditPrifile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(ProfileActivity.this,MyProfileActivity.class);
                startActivity(intent);
                finish();
            }
        });


        imgPostMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(ProfileActivity.this,MyProfileActivity.class);
                startActivity(intent);
            }
        });


        setupBottomNavigationView();
        setupToolbar();

        grid = findViewById(R.id.gridView);
        grid.setAdapter(galAdapter);


        new Thread() {
            @Override
            public void run() {
                while (remain&(!areaFlag||!moneyFlag)) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
                            if (connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState() == NetworkInfo.State.CONNECTED ||
                                    connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState() == NetworkInfo.State.CONNECTED) {
                                //we are connected to a network
                                connected = true;
                                netState=SP1.getInt("net_status",3);
                                if (netState==0)remain=false;
                            } else {
                                connected = false;
//                                Toast.makeText(ProfileActivity.this, "اینترنت وصل نیست", Toast.LENGTH_SHORT).show();
                                if (failcount>4) remain=false;
                                failcount++;

                            }


                            if (connected &&!reqFlag){
                                reqPosts();
                            }
                            if (connected &&!moneyFlag){
                                reqScore();
                            }



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


    }

    private void setupToolbar() {
        Toolbar toolbar = findViewById(R.id.profileToolBar);
        setSupportActionBar(toolbar);
        ImageView profileimageView = findViewById(R.id.profile_photoProfile);
        profileimageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: go to the  profile");
                Intent intent = new Intent(ProfileActivity.this, AccountSettingActivity.class);
                startActivity(intent);
            }
        });
    }

    // تنظیم نوار پایین برنامه
    private void setupBottomNavigationView() {
        Log.d(TAG, "seting up bottom navigation view");
        BottomNavigationViewEx bottomNavigationViewEx = findViewById(R.id.bottomNavViewBar);
        bottomNavigationViewEx.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        BottomNavigationViewHelper.setupBottomNavigationView(bottomNavigationViewEx);
        BottomNavigationViewHelper.enableNavigation(ProfileActivity.this, bottomNavigationViewEx);
        Menu menu = bottomNavigationViewEx.getMenu();
        MenuItem menuItem = menu.getItem(ACTIVITY_NUM);
        menuItem.setChecked(true);

    }



//
//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//
//        getMenuInflater().inflate(R.menu.profile_menu, menu);
//        return true;
//    }


    public void reqPosts() {
        RequestQueue queue = Volley.newRequestQueue(ProfileActivity.this);
        String url = fullServer;
        StringRequest postRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        reqFlag = true;
                        int count = 0;


                        JSONArray jsonArray = null;
                        try {
                            jsonArray = new JSONArray(response);
                            Post post;
                            JSONObject jsonObject = jsonArray.getJSONObject(0);
                            count = 0;

                            for (int i = jsonArray.length(); i > 0; i--) {
                                jsonObject = jsonArray.getJSONObject(i - 1);
                                post = new Post();
                                post.setId(jsonObject.getInt("soid"));
                                post.setUserName(jsonObject.getString("soname"));
                                String anss=jsonObject.getString("soincharge");
                                if(anss.equals("null")|| anss.isEmpty())anss="...";
                                post.setPostAnswer(anss);
                                post.setUserPhone(jsonObject.getString("sophone"));
                                post.setPostComment(jsonObject.getString("sotext"));
                                if (jsonObject.getString("pic").equals("null")||jsonObject.getString("pic").equals(" ")
                                        ||jsonObject.getString("pic").equals("")){
                                    post.setPostImageUrl(server+"/assets/images/137/blur.jpg");
                                }else {
                                    post.setPostImageUrl(server+"/assets/images/137/" + jsonObject.getString("pic"));
                                }
                                post.setPostLike(jsonObject.getString("seen"));

                                String tmp1=jsonObject.getString("lk");
                                if (tmp1.isEmpty()|| tmp1.equals("null")|| tmp1.equals("0")){
                                    tmp1="0";
                                }else {tmp1="1";}
                                post.setPostLK(tmp1);

                                String dtl = jsonObject.getString("sotime");
                                Calendar mydate = Calendar.getInstance();
                                mydate.setTimeInMillis(Long.parseLong(dtl + "000"));

                                int myear = mydate.get(Calendar.YEAR);
                                int mmonth = mydate.get(Calendar.MONTH) + 1;
                                int mday = mydate.get(Calendar.DAY_OF_MONTH);
                                CalendarTool calt = new CalendarTool(myear, mmonth, mday);
                                post.setPostDetail(calt.getIranianDate());

                                dataModels.add(post);


                                count++;
                            }
                            txtNumPosts.setText(String.valueOf(jsonArray.length()));
                            SharedPreferences.Editor SP2 = PreferenceManager.getDefaultSharedPreferences(getBaseContext()).edit();
                            SP2.putString("numPosts", String.valueOf(jsonArray.length()));
                            SP2.apply();
                            galAdapter.notifyDataSetChanged();
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
                params.put("usr", mob);
                return params;
            }
        };
        queue.add(postRequest);

    }

    public void reqScore() {
        RequestQueue queue = Volley.newRequestQueue(ProfileActivity.this);
        String url = server+"/i/score.php";
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
                            txtNumPosts.setText(String.valueOf(jsonArray.length()));
                            SharedPreferences.Editor SP2 = PreferenceManager.getDefaultSharedPreferences(getBaseContext()).edit();
                            SP2.putString("money", mny);
                            SP2.putString("spend", spend);
                            SP2.apply();
                            txtbonus.setText(mny);
                            txtMosharekat.setText(spend);

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
                return params;
            }
        };
        queue.add(postRequest);

    }

}



