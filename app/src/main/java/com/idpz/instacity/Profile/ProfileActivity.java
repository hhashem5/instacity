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
import android.widget.ProgressBar;
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
import com.idpz.instacity.Home.HomeActivity;
import com.idpz.instacity.Home.VisitSearchActivity;
import com.idpz.instacity.R;
import com.idpz.instacity.Share.PopupActivity;
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
    String AREA_URL = "";
//    ProgressBar progressBar;
    ProgressBar progressBar;
    //widgets
    private ViewPager mViewPager;
    private FrameLayout mFrameLayout;
    private RelativeLayout mRelativeLayout;
    GridView grid;
    TextView txtNumPosts,txtbonus,txtEditPrifile,txtTabUserName,txtMosharekat;
    ImageView imgPostMenu,imgRetry;
    String mob="0",profileImgUrl="", REG_USER_LAT ="";
    CircleImageView imgProfile;
    String server="";
    Button btnUseGift;
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
        AREA_URL = getString(R.string.server)+"/i/getarea.php";
        SP1 = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
        server=SP1.getString("server", "0");
        ctName=SP1.getString("ctname", "0");
        String numposts=SP1.getString("numPosts", "0");
        String spends=SP1.getString("spend", "0");
        String moneys=SP1.getString("money", "100");
        String myname=SP1.getString("myname", "0");
        String melliid=SP1.getString("melliid", "0");
        mob=SP1.getString("mobile", "0");

        fullServer = getString(R.string.server)+"/j/socialgal.php";

        if (myname.toLowerCase().equals("null"))myname="بی نام";

        mViewPager =(ViewPager) findViewById(R.id.viewpager_container);
        mFrameLayout = (FrameLayout)findViewById(R.id.container);
        mRelativeLayout =(RelativeLayout) findViewById(R.id.relLayoutParent);
        dataModels = new ArrayList<>();
        galAdapter = new galleryAdapter(ProfileActivity.this, dataModels);
        txtbonus=(TextView) findViewById(R.id.txtBonus);
        txtNumPosts=(TextView) findViewById(R.id.tvPosts);

        txtMosharekat=(TextView) findViewById(R.id.txtMosharekat);
//        txtMobile=(TextView) findViewById(R.id.website);
//        txtCurCt=(TextView) findViewById(R.id.txtCurrentCTProfile);
        txtEditPrifile=(TextView) findViewById(R.id.textEditProfile);
        txtTabUserName=(TextView) findViewById(R.id.txtTabUsername);
        imgPostMenu=(ImageView) findViewById(R.id.imgProfileMenu);
//        btnChangeCT=(Button) findViewById(R.id.btnChangeCTPofile);
        Button btnComlpain=(Button) findViewById(R.id.btnComplain);
        Button btnContactUs=(Button) findViewById(R.id.btnContactUs);
        btnUseGift=(Button) findViewById(R.id.btnUseGift);
        progressBar=(ProgressBar) findViewById(R.id.progressBar);
//        txtCurCt.setText(ctName);


        btnComlpain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(ProfileActivity.this,ContactUsActivity.class);
                intent.putExtra("title", "ارسال شکایت به دهکده هوشمند");
                intent.putExtra("type", "0");

                startActivity(intent);
            }
        });
        btnContactUs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(ProfileActivity.this,ContactUsActivity.class);
                intent.putExtra("title", "ارتباط با دهکده هوشمند");
                intent.putExtra("type", "1");
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
        imgRetry=(ImageView)findViewById(R.id.imgRetry);
        imgRetry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: ");
                if (isConnected()) {
                    imgRetry.setVisibility(View.GONE);
                    progressBar.setVisibility(View.VISIBLE);
                    reqPosts();
                }
            }
        });



        txtNumPosts.setText(numposts);
        txtMosharekat.setText(spends);
        int bonus=Integer.valueOf(moneys);
        txtbonus.setText(String.valueOf(bonus));

        txtTabUserName.setText(myname);


        imgProfile=(CircleImageView) findViewById(R.id.profile_photoProfile);
        Log.d(TAG, "onCreate: profile_pic"+profileImgUrl);
        Glide.with(this).load(getString(R.string.server)+"/assets/images/users/"+profileImgUrl)
                .thumbnail(0.5f)
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

        grid =(GridView) findViewById(R.id.gridView);
        grid.setAdapter(galAdapter);
        failcount=0;



                reqPosts();
                reqScore();


        Intent intentex = getIntent();
        if(intentex.hasExtra("body")) {
            String body = intentex.getStringExtra("body");

            Intent popup=new Intent(ProfileActivity.this,PopupActivity.class);
            popup.putExtra("title", "پیام های شهروندی شما");
            popup.putExtra("body", body);
            popup.putExtra("score", "تشکر");
            startActivity(popup);
        }

    }

    private void setupToolbar() {
        Toolbar toolbar =(Toolbar) findViewById(R.id.profileToolBar);
        setSupportActionBar(toolbar);
        ImageView profileimageView =(ImageView) findViewById(R.id.profile_photoProfile);
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
        BottomNavigationViewEx bottomNavigationViewEx =(BottomNavigationViewEx) findViewById(R.id.bottomNavViewBar);
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
        progressBar.setVisibility(View.VISIBLE);
        Log.d(TAG, "reqPosts: start");
        StringRequest postRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        reqFlag = true;
                        progressBar.setVisibility(View.GONE);
                        imgRetry.setVisibility(View.GONE);
                        Log.d(TAG, "onResponse: posts received");
                        int count = 0;


                        JSONArray jsonArray = null;
                        try {
                            jsonArray = new JSONArray(response);
                            Post post;
                            dataModels.clear();
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
                                String myUrl=jsonObject.getString("server");
                                if (jsonObject.getString("pic").equals("null")||jsonObject.getString("pic").equals(" ")
                                        ||jsonObject.getString("pic").equals("")){
                                    post.setPostImageUrl(myUrl+"/assets/images/137/blur.jpg");
                                }else {
                                    post.setPostImageUrl(myUrl+"/assets/images/137/" + jsonObject.getString("pic"));
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
                            failcount++;
                        }


                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        failcount++;
                        progressBar.setVisibility(View.GONE);
                        imgRetry.setVisibility(View.VISIBLE);
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
        Log.d(TAG, "reqScore: start");
        StringRequest postRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        moneyFlag = true;

                        Log.d(TAG, "onResponse: score received");

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
                            failcount++;
                        }


                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        failcount++;
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

    public boolean isConnected(){
        ConnectivityManager connectivityManager = (ConnectivityManager) ProfileActivity.this.getSystemService(Context.CONNECTIVITY_SERVICE);
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

    @Override
    protected void onPostResume() {
        super.onPostResume();
        reqPosts();
    }
}



