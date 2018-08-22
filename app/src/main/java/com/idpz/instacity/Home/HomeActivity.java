package com.idpz.instacity.Home;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Typeface;
import android.location.Location;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.StrictMode;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.support.design.widget.Snackbar;
import android.support.v4.content.FileProvider;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkError;
import com.android.volley.NoConnectionError;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.ServerError;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.downloader.Error;
import com.downloader.OnCancelListener;
import com.downloader.OnDownloadListener;
import com.downloader.OnPauseListener;
import com.downloader.OnProgressListener;
import com.downloader.OnStartOrResumeListener;
import com.downloader.PRDownloader;
import com.downloader.PRDownloaderConfig;
import com.downloader.Progress;
import com.idpz.instacity.Area;
import com.idpz.instacity.BuildConfig;
import com.idpz.instacity.Travel.TourismActivity;
import com.idpz.instacity.Profile.ChangeCityActivity;
import com.idpz.instacity.R;
import com.idpz.instacity.MapCity.PlacesActivity;
import com.idpz.instacity.MapCity.SearchActivity;
import com.idpz.instacity.Share.SendSocialActivity;
import com.idpz.instacity.models.Evnt;
import com.idpz.instacity.models.VisitPlace;
import com.idpz.instacity.utils.AndyUtils;
import com.idpz.instacity.utils.BottomNavigationViewHelper;
import com.idpz.instacity.utils.CalendarTool;
import com.idpz.instacity.utils.DBAreaHandler;
import com.idpz.instacity.utils.DBEventHandler;
import com.idpz.instacity.utils.EvntRvAdapter;
import com.idpz.instacity.utils.GPSTracker;
import com.ittianyu.bottomnavigationviewex.BottomNavigationViewEx;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


import co.ronash.pushe.Pushe;
import rest.bef.BefrestMessage;
import rest.bef.BefrestPushReceiver;

import static android.Manifest.permission.ACCESS_COARSE_LOCATION;
import static android.Manifest.permission.ACCESS_FINE_LOCATION;

//import com.onesignal.OneSignal;

public class HomeActivity extends Activity implements EvntRvAdapter.ItemClickListener {

    ArrayList<String> urls=new ArrayList<String>();
    String AREA_URL ="";
    private static final String TAG = "HomeActivity";
    private static final int ACTIVITY_NUM = 0;
    private static final int HOME_FRAGMENT = 1;
    private static final int REQUEST_ACCESS_LOCATION = 0;
    private Context mContext = HomeActivity.this;
    Area myArea;
    ArrayList<Area> areaArrayList;
    ArrayList<VisitPlace> visitPlaces=new ArrayList<>();
    List<Evnt> evnts=new ArrayList<>();
    Location myLocation, mycity, myHome;
    List<String> cities=new ArrayList<>();
    List<Float> distances=new ArrayList<Float>();
    String lat="",lng="",aename="";
    String REG_USER_URL="",REGISTER_PROFILE="";
    String myname="0",melliid="0",pas="", mobile="0", birth="0",pic="", gender="0", edu="0", edub="0", job="0", jobb="0", fav="0", money="0";
    String upStatus="start",sm="";
    Boolean userRegFlag=false,areaFlag=false,userProfileFlag=false,gpsCheck=false,remain=true;
    boolean doubleBackToExitPressedOnce = false;
    public static volatile boolean showSplashFlag=true;
    RelativeLayout relLayout1;
    EvntRvAdapter evntRvAdapter;

    //widgets
    GPSTracker gps,gps2;
    private ViewPager mViewPager;
    private FrameLayout mFrameLayout;
    private RelativeLayout mRelativeLayout,relNetError;
    SharedPreferences SP1;
    DBAreaHandler dbAreaHandler;
    ArrayList<VisitPlace> dataModels=new ArrayList<>();
    DBEventHandler dbEventHandler;
    private RecyclerView recyclerView;
    private static final String API = "https://api.darksky.net/forecast/674956b88e8f6ab8cf03d9533490ff87/";
    String coords = "",darkCoords = "", extra = "?lang=en&units=si&exclude=hourly,flags,daily";
    TextView txtTodayWeather,txtTempreture;
    String server = "", fullServer = "",REGISTER_URL="",VERSION_URL="",
            homeLat = "", homeLng = "", ctDesc = "", uid = "", ctname = "",state="";
    int lim1 = 0, lim2 = 20,webVersion=0;
    public static volatile int netState=3;
    Boolean visitFlag = false, connected = false, firstTime = true, weatherFlag = false,eventFlag=true;
    int failCount=0,appVer=0;
    Button btnUpdate;
    ProgressBar progressBar;
    ImageView imgRetry,imgError,imgWeather,imgBackcity;
    Handler mHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        Log.d(TAG, "onCreate: starting.");

//        lv= findViewById(R.id.lvHomeContent);
        AREA_URL= getString(R.string.server)+"/j/getarea.php";
        REGISTER_URL=getString(R.string.server)+"/j/getdata.php";
        VERSION_URL=getString(R.string.server)+"/j/appver.php";
        dbAreaHandler=new DBAreaHandler(this);
        dbEventHandler=new DBEventHandler(this);

        areaArrayList = new ArrayList<>();
        myLocation = new Location("myloc");
        mycity = new Location("city");
        myHome = new Location("home");

        SP1 = PreferenceManager.getDefaultSharedPreferences(HomeActivity.this);
        myname = SP1.getString("myname", "0");
        mobile = SP1.getString("mobile", "0");
        melliid = SP1.getString("melliid", "0");
        lat=SP1.getString("lat", "0.0");
        lng=SP1.getString("lng", "0.0");
        birth=SP1.getString("birth", "0");
        edu=SP1.getString("edu", "0");
        edub=SP1.getString("edub", "0");
        gender=SP1.getString("gender", "0");
        job=SP1.getString("job", "0");
        jobb=SP1.getString("jobb", "0");
        fav=SP1.getString("fav", "0");
        pas=SP1.getString("pass", "0");
        server=SP1.getString("server", "0");
        pic=SP1.getString("pic", "0");
        ctname=SP1.getString("ctname", "");
        state=SP1.getString("state", "");
        fullServer = getString(R.string.server)+"/j/getvisitplace.php";
        homeLat = SP1.getString("homelat", "0");
        homeLng = SP1.getString("homelng", "0");

        Typeface yekan = Typeface.createFromAsset(HomeActivity.this.getAssets(), "fonts/YEKAN.TTF");

        btnUpdate=findViewById(R.id.btnUpdate);

        ImageView btnCars =(ImageView) findViewById(R.id.imgcarPanel);
        ImageView btntourism =(ImageView) findViewById(R.id.imgTourismPanel);
        ImageView btnHandyCraft =(ImageView) findViewById(R.id.imgHandy1Panel);
        TextView txtChangeCity =(TextView) findViewById(R.id.txtChangeCityPanel);
        progressBar=(ProgressBar) findViewById(R.id.progressFirst);
        ImageView imgWeather =(ImageView) findViewById(R.id.imgWeatherTop);
        ImageView imgNewNews=(ImageView)findViewById(R.id.imgNewsPanel);
        ImageView imgNew137=(ImageView)findViewById(R.id.imgSocialPanel);
        ImageView imgSocialGuy=(ImageView)findViewById(R.id.imgSocial2Panel);
        ImageView imgChangeCity=(ImageView)findViewById(R.id.imgChangeCity);
        ImageView imgPlaces=(ImageView)findViewById(R.id.imgJobPanel);
        ImageView imgErrorConnect=(ImageView)findViewById(R.id.imgErrorConnect);
        ImageView imgAds1Panel=(ImageView) findViewById(R.id.imgAds1Panel);
        ImageView imgUp= findViewById(R.id.imgUp);
        imgBackcity=(ImageView) findViewById(R.id.imgMainCityBack);
        TextView txtcarPanel=(TextView) findViewById(R.id.txtcarPanel);
        TextView txtSocialPanel=(TextView)findViewById(R.id.txtSocialPanel);
        TextView txtNewsPanel=(TextView)findViewById(R.id.txtNewsPanel);
        TextView txtTourismPanel=(TextView)findViewById(R.id.txtTourismPanel);
        TextView txtHandyPanel=(TextView)findViewById(R.id.txtHandyPanel);
        TextView txtAdsPanel=(TextView)findViewById(R.id.txtAdsPanel);
        TextView txtdate=(TextView)findViewById(R.id.txtDatePanel);
        TextView txtPlaces=(TextView)findViewById(R.id.txtJob2Panel);
        TextView txtPlaces2=(TextView)findViewById(R.id.txtJobPanel);
        txtTodayWeather=(TextView)findViewById(R.id.txtTodayWeather);
        txtTempreture=(TextView)findViewById(R.id.txtTempreture);
        LinearLayout linearLayout=(LinearLayout) findViewById(R.id.linSendNewSocial);
        LinearLayout linearMenu=(LinearLayout) findViewById(R.id.line1);
        RelativeLayout linAds=(RelativeLayout) findViewById(R.id.relAds);
        RelativeLayout relNews=(RelativeLayout) findViewById(R.id.relNews);
        RelativeLayout relCars=(RelativeLayout) findViewById(R.id.relCars);
        RelativeLayout relHandyCraft=(RelativeLayout) findViewById(R.id.relHandyCraft);
        RelativeLayout relTour=(RelativeLayout) findViewById(R.id.relTour);
        RelativeLayout relSocial=(RelativeLayout) findViewById(R.id.rel1);
        RelativeLayout relAllMenu=(RelativeLayout) findViewById(R.id.relAllMenu);
        RelativeLayout rel137=(RelativeLayout) findViewById(R.id.rel137);


        try {
            PackageInfo pInfo = this.getPackageManager().getPackageInfo(getPackageName(), 0);
            int appVer = pInfo.versionCode;
//            Toast.makeText(mContext, "AppVer="+appVer, Toast.LENGTH_SHORT).show();
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
//        new Thread(new Runnable(){
//            public void run(){
//
//                try {
//                    // Create a URL for the desired page
//                    URL url = new URL("http://idpz.ir/apk/ver.txt"); //My text file location
//                    //First open the connection
//                    HttpURLConnection conn=(HttpURLConnection) url.openConnection();
//                    conn.setConnectTimeout(60000); // timing out in a minute
//                    BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
//                    //t=(TextView)findViewById(R.id.TextView1); // ideally do this in onCreate()
//                    String str;
//                    while ((str = in.readLine()) != null) {
//                        urls.add(str);
//                    }
//                    in.close();
//                } catch (Exception e) {
//                    Log.d("MyTag",e.toString());
//                }
//                //since we are in background thread, to post results we have to go back to ui thread. do the following for that
//                runOnUiThread(new Runnable() {
//                    @Override
//                    public void run() {
//                        webVersion=Integer.parseInt(urls.get(0)); // My TextFile has 1 lines
//
//                    }
//                });
//
//            }
//        }).start();

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        readVersion();

// Setting timeout globally for the download network requests:
        PRDownloader.initialize(getApplicationContext());
        PRDownloaderConfig config = PRDownloaderConfig.newBuilder()
                .setDatabaseEnabled(true)
                .setReadTimeout(30_000)
                .setConnectTimeout(30_000)
                .build();
        PRDownloader.initialize(getApplicationContext(), config);




//        AndyUtils.expand(imgUp);
//        Load animation
        Animation slide_down = AnimationUtils.loadAnimation(getApplicationContext(),
                R.anim.slide_down);

        Animation slide_up = AnimationUtils.loadAnimation(getApplicationContext(),
                R.anim.shake);

        imgUp.startAnimation(slide_up);


        Calendar mydate = Calendar.getInstance();
                                int myear=mydate.get(Calendar.YEAR);
                                int mmonth=mydate.get(Calendar.MONTH)+1;
                                int mday=mydate.get(Calendar.DAY_OF_MONTH);
                                CalendarTool calt=new CalendarTool(myear,mmonth,mday);
                                txtdate.setText(calt.getIranianDate()+" "+calt.getIranianWeekDayStr());


        imgRetry= (ImageView)findViewById(R.id.imgVRetry);
        txtdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent in =new Intent(HomeActivity.this,AddCityActivity.class);
                startActivity(in);
            }
        });
        coords = "lat="+homeLat + "&lon=" + homeLng+"&appid=0b9a46a0c6f55b8795adcfd9d3e28d36&lang=fa&units=metric";
        darkCoords=homeLat+","+homeLng;

        linAds.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent1 = new Intent(HomeActivity.this, TourismActivity.class);
                Bundle b = new Bundle();
                b.putString("tab", "1"); //          ads
                intent1.putExtras(b); //Put your id to your next Intent
                startActivity(intent1);
            }
        });
        relNews.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent1 = new Intent(HomeActivity.this, MessagesFragment.class);
                Bundle b = new Bundle();
                b.putString("tab", "1"); //          ads
                intent1.putExtras(b); //Put your id to your next Intent
                startActivity(intent1);
            }
        });
        relCars.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent1 = new Intent(HomeActivity.this, SearchActivity.class);
                Bundle b = new Bundle();
                b.putString("tab", "1"); //          ads
                intent1.putExtras(b); //Put your id to your next Intent
                startActivity(intent1);
            }
        });
        relHandyCraft.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent1 = new Intent(HomeActivity.this, TourismActivity.class);
                Bundle b = new Bundle();
                b.putString("tab", "2"); //          ads
                intent1.putExtras(b); //Put your id to your next Intent
                startActivity(intent1);
            }
        });
        relTour.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent1 = new Intent(HomeActivity.this, TourismActivity.class);
                Bundle b = new Bundle();
                b.putString("tab", "0"); //          ads
                intent1.putExtras(b); //Put your id to your next Intent
                startActivity(intent1);
            }
        });
        relSocial.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent1 = new Intent(HomeActivity.this, HomeFragment.class);
                Bundle b = new Bundle();
                b.putString("tab", "0"); //          ads
                intent1.putExtras(b); //Put your id to your next Intent
                startActivity(intent1);
            }
        });

        rel137.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent1 = new Intent(HomeActivity.this, HomeFragment.class);
                Bundle b = new Bundle();
                b.putString("tab", "0"); //          ads
                intent1.putExtras(b); //Put your id to your next Intent
                startActivity(intent1);
            }
        });
        txtAdsPanel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent1 = new Intent(HomeActivity.this, TourismActivity.class);
                Bundle b = new Bundle();
                b.putString("tab", "1"); //          ads
                intent1.putExtras(b); //Put your id to your next Intent
                startActivity(intent1);
            }
        });
        imgAds1Panel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent1 = new Intent(HomeActivity.this, TourismActivity.class);
                Bundle b = new Bundle();
                b.putString("tab", "1"); //          ads
                intent1.putExtras(b); //Put your id to your next Intent
                startActivity(intent1);
            }
        });
        imgPlaces.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent1 = new Intent(HomeActivity.this, PlacesActivity.class);
                Bundle b = new Bundle();
                b.putString("key", "weather"); //          Job Map
                intent1.putExtras(b); //Put your id to your next Intent
                startActivity(intent1);
            }
        });

        txtPlaces.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent1 = new Intent(HomeActivity.this, PlacesActivity.class);
                Bundle b = new Bundle();
                b.putString("key", "weather"); //          Job Map
                intent1.putExtras(b); //Put your id to your next Intent
                startActivity(intent1);
            }
        });

        txtPlaces2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent1 = new Intent(HomeActivity.this, PlacesActivity.class);
                Bundle b = new Bundle();
                b.putString("key", "places"); //          Places Map
                intent1.putExtras(b); //Put your id to your next Intent
                startActivity(intent1);
            }
        });

        RelativeLayout relMedia=findViewById(R.id.relLayoutMedia);
        relMedia.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openVideo();
            }
        });

        RelativeLayout relPlaces=findViewById(R.id.relLayoutJob);
        relPlaces.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent1 = new Intent(HomeActivity.this, PlacesActivity.class);
                Bundle b = new Bundle();
                b.putString("key", "places"); //          Places Map
                intent1.putExtras(b); //Put your id to your next Intent
                startActivity(intent1);
            }
        });



        imgNew137.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent1 = new Intent(HomeActivity.this, HomeFragment.class);
                Bundle b = new Bundle();
                b.putString("key", "weather"); //          city Map
                intent1.putExtras(b); //Put your id to your next Intent
                startActivity(intent1);
            }
        });
        imgSocialGuy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent1 = new Intent(HomeActivity.this, HomeFragment.class);
                startActivity(intent1);
            }
        });
        linearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent1 = new Intent(HomeActivity.this, SendSocialActivity.class);
                Bundle b = new Bundle();
                b.putString("key", "1"); //          social guy
                intent1.putExtras(b); //Put your id to your next Intent
                startActivity(intent1);
            }
        });
        txtSocialPanel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent1 = new Intent(HomeActivity.this, HomeFragment.class);
                Bundle b = new Bundle();
                b.putString("key", "weather"); //          city Map
                intent1.putExtras(b); //Put your id to your next Intent
                startActivity(intent1);
            }
        });

        imgNewNews.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent1 = new Intent(HomeActivity.this, MessagesFragment.class);
                Bundle b = new Bundle();
                b.putString("key", "weather"); //          city Map
                intent1.putExtras(b); //Put your id to your next Intent
                startActivity(intent1);
            }
        });
        txtNewsPanel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent1 = new Intent(HomeActivity.this, MessagesFragment.class);
                Bundle b = new Bundle();
                b.putString("key", "weather"); //          city Map
                intent1.putExtras(b); //Put your id to your next Intent
                startActivity(intent1);
            }
        });

        imgWeather.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent1 = new Intent(HomeActivity.this, WeatherActivity.class);
                Bundle b = new Bundle();
                b.putString("key", "weather"); //          city Map
                intent1.putExtras(b); //Put your id to your next Intent
                startActivity(intent1);
            }
        });
        btnCars.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent1 = new Intent(HomeActivity.this, SearchActivity.class);
                Bundle b = new Bundle();
                b.putString("key", "weather"); //          city Map
                intent1.putExtras(b); //Put your id to your next Intent
                startActivity(intent1);
            }
        });
        txtcarPanel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent1 = new Intent(HomeActivity.this, SearchActivity.class);
                Bundle b = new Bundle();
                b.putString("key", "weather"); //          city Map
                intent1.putExtras(b); //Put your id to your next Intent
                startActivity(intent1);
            }
        });

        btntourism.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent1 = new Intent(HomeActivity.this, TourismActivity.class);
                Bundle b = new Bundle();
                b.putString("tab", "0"); //          city Map
                intent1.putExtras(b); //Put your id to your next Intent
                startActivity(intent1);
            }
        });
        txtTourismPanel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent1 = new Intent(HomeActivity.this, TourismActivity.class);
                Bundle b = new Bundle();
                b.putString("tab", "0"); //          city Map
                intent1.putExtras(b); //Put your id to your next Intent
                startActivity(intent1);
            }
        });

        btnHandyCraft.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent1 = new Intent(HomeActivity.this, TourismActivity.class);
                Bundle b = new Bundle();
                b.putString("tab", "2"); //          handy carft
                intent1.putExtras(b); //Put your id to your next Intent
                startActivity(intent1);
            }
        });
        txtHandyPanel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent1 = new Intent(HomeActivity.this, TourismActivity.class);
                Bundle b = new Bundle();
                b.putString("tab", "2"); //          handy carft
                intent1.putExtras(b); //Put your id to your next Intent
                startActivity(intent1);
            }
        });

        txtChangeCity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent1 = new Intent(HomeActivity.this, ChangeCityActivity.class);
                Bundle b = new Bundle();
                b.putString("key", "weather"); //          city Map
                intent1.putExtras(b); //Put your id to your next Intent
                startActivity(intent1);
            }
        });
        imgChangeCity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent1 = new Intent(HomeActivity.this, ChangeCityActivity.class);
                Bundle b = new Bundle();
                b.putString("key", "weather"); //          city Map
                intent1.putExtras(b); //Put your id to your next Intent
                startActivity(intent1);
            }
        });





        TextView txtMyCity=(TextView)findViewById(R.id.txtMyCity);
        txtMyCity.setText(ctname);
        txtMyCity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent8 = new Intent(HomeActivity.this, ChangeCityActivity.class);
                Bundle b8 = new Bundle();
                b8.putString("key", "changecity"); //Your id
                intent8.putExtras(b8); //Put your id to your next Intent
                startActivity(intent8);
            }
        });


//        remain = SP1.getBoolean("connected", false);
        firstTime = SP1.getBoolean("firsttime", true);
        boolean firstTime=!(SP1.getBoolean("firsttime", true));
        boolean splash=SP1.getBoolean("splash", false);
        Log.d(TAG, "onCreate: mobile="+mobile+" myname="+SP1.getString("myname", "0"));
        if (mobile.length() <10)
        {
            Intent intent=new Intent(HomeActivity.this,LoginActivity.class);
            startActivity(intent);
            finish();
        }
//        OneSignal.startInit(this)
//                .setNotificationReceivedHandler(new ExampleNotificationReceivedHandler())
//                .setNotificationOpenedHandler(new ExampleNotificationOpenedHandler())
//                .inFocusDisplaying(OneSignal.OSInFocusDisplayOption.Notification)
//                .unsubscribeWhenNotificationsAreDisabled(true)
//                .init();
        Log.d(TAG, "onCreate: oneSignal Started");
        if(firstTime&&splash) {
            Intent intent = new Intent(HomeActivity.this,SplashScreenActivity.class);
            startActivity(intent);
        }
        SharedPreferences.Editor SP = PreferenceManager.getDefaultSharedPreferences(this).edit();
        SP.putInt("net_status", 3); // set net status to waiting
        SP.putBoolean("splash",true); // show splash Screen next time
        SP.apply();

//        BoomMenuButton bmb;
//        bmb = findViewById(R.id.bmb);
//        assert bmb != null;
//        bmb.setButtonEnum(ButtonEnum.TextOutsideCircle);
//        bmb.setPiecePlaceEnum(PiecePlaceEnum.DOT_9_1);
//        bmb.setButtonPlaceEnum(ButtonPlaceEnum.SC_9_1);
//
//        for (int i = 0; i < bmb.getPiecePlaceEnum().pieceNumber(); i++)
//            bmb.addBuilder(BuilderManager.getTextOutsideCircleButtonBuilder().listener(new OnBMClickListener() {
//                @Override
//                public void onBoomButtonClick(int index) {
//                    switch (index) {
//                        case 1://cars
//                            Toast.makeText(HomeActivity.this, "1", Toast.LENGTH_SHORT).show();
//                            Intent intent1 = new Intent(HomeActivity.this, SearchActivity.class);
//                            Bundle b = new Bundle();
//                            b.putString("key", "food"); //          city Map
//                            intent1.putExtras(b); //Put your id to your next Intent
//                            startActivity(intent1);
//                            break;
//                        case 2:// places
////                            Toast.makeText(getActivity(), "2", Toast.LENGTH_SHORT).show();
//                            Intent intent2 = new Intent(HomeActivity.this, PlacesActivity.class);
//                            Bundle c2 = new Bundle();
//                            c2.putString("key", "health"); //Your id
//                            intent2.putExtras(c2); //Put your id to your next Intent
//                            startActivity(intent2);
//                            break;
//                        case 3://tourism
////                            Toast.makeText(getActivity(), "3", Toast.LENGTH_SHORT).show();
//                            Intent intent3 = new Intent(HomeActivity.this, LikesActivity.class);
//                            Bundle d3 = new Bundle();
//                            d3.putString("key", "religion"); //Your id
//                            intent3.putExtras(d3); //Put your id to your next Intent
//                            startActivity(intent3);
//                            break;
//                        case 4:// 137
////                            Toast.makeText(getActivity(), "4", Toast.LENGTH_SHORT).show();
//                            Intent intent = new Intent(HomeActivity.this, HomeFragment.class);
//                            startActivity(intent);
//                            break;
//                        case 5://ads
////                            Toast.makeText(getActivity(), "5", Toast.LENGTH_SHORT).show();
//                            Intent intent4 = new Intent(HomeActivity.this, LikesActivity.class);
//                            Bundle e4 = new Bundle();
//                            e4.putString("key", "services"); //Your id
//                            intent4.putExtras(e4); //Put your id to your next Intent
//                            startActivity(intent4);
//                            break;
//                        case 6://news
////                            Toast.makeText(getActivity(), "6", Toast.LENGTH_SHORT).show();
//                            Intent intent6 = new Intent(HomeActivity.this, MessagesFragment.class);
//                            startActivity(intent6);
//                            break;
//                        case 7://media video
////                            Toast.makeText(getActivity(), "7", Toast.LENGTH_SHORT).show();
//                            Intent intent7 = new Intent(HomeActivity.this, EventActivity.class);
//                            Bundle b7 = new Bundle();
//                            b7.putString("key", "sport"); //Your id
//                            intent7.putExtras(b7); //Put your id to your next Intent
//                            startActivity(intent7);
//                            break;
//                        case 8://change city
////                            Toast.makeText(getActivity(), "7", Toast.LENGTH_SHORT).show();
//                            Intent intent8 = new Intent(HomeActivity.this, ChangeCityActivity.class);
//                            Bundle b8 = new Bundle();
//                            b8.putString("key", "changecity"); //Your id
//                            intent8.putExtras(b8); //Put your id to your next Intent
//                            startActivity(intent8);
//                            break;
//                        case 0://handy craft
//                            Toast.makeText(HomeActivity.this, "0", Toast.LENGTH_SHORT).show();
//                            Intent intent0 = new Intent(HomeActivity.this, LikesActivity.class);
////                            intent0.addFlags(FLAG_ACTIVITY_REORDER_TO_FRONT);
//                            SharedPreferences.Editor SP = PreferenceManager.getDefaultSharedPreferences(HomeActivity.this).edit();
//                            SP.putString("tabpos", "0");
//                            SP.apply();
////                            intent0.putExtra("position", 0); //          137
////                            startActivity(intent0);
////                            ((HomeActivity) getActivity()).tabselect();
//                            break;
//
//                    }
//                }
//            }).normalTextRes(textResources(i))
//                    .normalText("سلام")
//                    .highlightedTextRes(R.string.text_ham_button_sub_text_normal));
//



        if(server.equals("0"))server=getString(R.string.server);

        if (!lat.equals("0.0")){
            myLocation.setLatitude(Float.valueOf(lat));
            myLocation.setLongitude(Float.valueOf(lng));
        }
        Log.d(TAG, "onCreate: GPS FROM SP:"+lat+" : "+lng);

        HomeActivity.AsyncTaskRunner runner = new HomeActivity.AsyncTaskRunner();
        runner.execute();


        REG_USER_URL=getString(R.string.server)+"/j/usereg.php";
        REGISTER_PROFILE=getString(R.string.server)+"/j/profile.php";
//        imgRetry.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                netState=3;
//                Log.d(TAG, "onClick: Msg");
//                if (isConnected()) {
//                    imgRetry.setVisibility(View.GONE);
//                    progressBar.setVisibility(View.VISIBLE);
////                    reqVisitPlace();
//                }
//
//            }
//        });


//        Intent in1 = new Intent(HomeActivity.this, AlarmService.class);
//        startService(in1);
//
//        mViewPager = findViewById(R.id.viewpager_container);
//        mFrameLayout = findViewById(R.id.container);
        mRelativeLayout =(RelativeLayout) findViewById(R.id.relLayoutParent);
        relNetError = (RelativeLayout)findViewById(R.id.relNetError);

//        Toast.makeText(HomeActivity.this, "درحال موقعیت یابی و یافتن منطقه شما", Toast.LENGTH_LONG).show();
        aename=SP1.getString("aename", "zibadasht");

        JSONObject tags = new JSONObject();
        try {
            tags.put("area", aename);
            tags.put("user_mobile", mobile);
        } catch (JSONException e) {
            e.printStackTrace();
        }
//        OneSignal.sendTags(tags);

//        Befrest befrestInstance = BefrestFactory.getInstance(HomeActivity.this);
//        befrestInstance
//                .setAuth("UnsZbt5ohFqPiymLSfA5_Q")
//                .setChId(mobile)
//                .setUId(11660);
//        befrestInstance.start();
        Pushe.initialize(this,true);
        uid=Pushe.getPusheId(this);
//        Toast.makeText(this, "yourId:"+Pushe.getPusheId(this), Toast.LENGTH_SHORT).show();
//        Pushe.subscribe(this,aename);
        setupBottomNavigationView();
//        setupViewPager();
//        hideLayout();
//        showPopup();


        new Thread() {
            @Override
            public void run() {
                while (remain&(!userRegFlag||!eventFlag)) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
                            if (connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState() == NetworkInfo.State.CONNECTED ||
                                    connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState() == NetworkInfo.State.CONNECTED) {

                                connected = true;
                                reqEvent();
                                if (netState==0)remain=false;
                                Log.d(TAG, "Home Act run: net State="+netState);
                            } else {
                                connected = false;
                                failCount++;
                                if (failCount>2)remain=false;
                                noInternet();
                            }

                            if (failCount>3){

                                Toast.makeText(HomeActivity.this, "اتصال اینترنت را بررسی کنید و دوباره امتحان کنید", Toast.LENGTH_SHORT).show();
                                remain=false;
                                Log.d(TAG, "run: fail count succeed  exit");
                            }
                            if (connected) {
                                if (!areaFlag) {
                                    reqArea();   //یافتن منطقه کاربر و اتصال به سوور هماه منطقه
                                }
                                if (!userRegFlag) {
                                    reqUser();  //register user on server
                                    hasInternet();
                                }
                                if (!visitFlag){
                                    reqVisitPlaces();
                                }
                                if (weatherFlag)findSky(sm);


                            }
                        }
                    });
                    try {
                        sleep(15000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }.start();



        if (AndyUtils.isNetworkAvailable(this)) {
            ImageView imgcommercial = (ImageView) findViewById(R.id.imgCommercial);



            Glide.with(this).load(getString(R.string.server)+"/assets/images/commercial.png")
                    .thumbnail(0.5f)
                    .into(imgcommercial);
        }
        relLayout1 =(RelativeLayout) findViewById(R.id.relMain);


                //instantiate the popup.xml layout file

        evnts.add(new Evnt(1,"هیچ رویدادی نیست","","","","تاریخ:--:--","","",""));
        //end oncreate
        // set up the RecyclerView
        RecyclerView recyclerView = findViewById(R.id.recycleView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        evntRvAdapter = new EvntRvAdapter(this, evnts);
        evntRvAdapter.setClickListener(this);
        recyclerView.setAdapter(evntRvAdapter);

    }

    @Override
    public void onItemClick(View view, int position) {
        Intent evntI = new Intent(HomeActivity.this,EventActivity.class);
        evntI.putExtra("name", evnts.get(position).getName());
        evntI.putExtra("owner", evnts.get(position).getOwner());
        evntI.putExtra("contact", evnts.get(position).getContact());
        evntI.putExtra("place", evnts.get(position).getPlace());
        evntI.putExtra("edate", evnts.get(position).getEdate());
        evntI.putExtra("etime", evnts.get(position).getEtime());
        evntI.putExtra("info", evnts.get(position).getInfo());
        evntI.putExtra("memo", evnts.get(position).getMemo());
        startActivity(evntI);
    }


    private void openVideo(){
        Intent intent1 = new Intent(HomeActivity.this, VideoActivity.class);
        Bundle b = new Bundle();
        b.putString("key", "video"); //          video content
        intent1.putExtras(b); //Put your id to your next Intent
        startActivity(intent1);
    }


    public void noInternet(){
        Log.d(TAG, "hideLayout: hiding layout");
        mRelativeLayout.setVisibility(View.GONE);
        relNetError.setVisibility(View.VISIBLE);
    }

    public void hasInternet(){
        Log.d(TAG, "hideLayout: hiding layout");
        mRelativeLayout.setVisibility(View.VISIBLE);
        relNetError.setVisibility(View.GONE);
    }


    @Override
    public void onBackPressed() {



        if (doubleBackToExitPressedOnce) {
            super.onBackPressed();
            finishAffinity();
            System.exit(0);
        }
//        if (!SP1.getString("tabpos","1").equals("1")) {
//            SharedPreferences.Editor SP = PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).edit();
//            SP.putString("tabpos", "1");
//            SP.apply();
//            tabLayout.getTabAt(1).select();
//        }
        doubleBackToExitPressedOnce = true;
        Toast.makeText(this, "برای خروج دوباره دکمه بازگشت را بزنید", Toast.LENGTH_SHORT).show();

        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                doubleBackToExitPressedOnce=false;
            }
        }, 2000);
    }


    /**
     * BottomNavigationView setup
     */
    private void setupBottomNavigationView(){
        Log.d(TAG, "setupBottomNavigationView: setting up BottomNavigationView");
        BottomNavigationViewEx bottomNavigationViewEx =(BottomNavigationViewEx) findViewById(R.id.bottomNavViewBar);
        BottomNavigationViewHelper.setupBottomNavigationView(bottomNavigationViewEx);
        BottomNavigationViewHelper.enableNavigation(mContext,bottomNavigationViewEx);

        Menu menu = bottomNavigationViewEx.getMenu();
        MenuItem menuItem = menu.getItem(ACTIVITY_NUM);
        menuItem.setChecked(true);
    }

    private void populateGPS() {
        if (!mayRequestLocation()) {
            return;
        }
        gps =new GPSTracker(this);
        Log.d(TAG, "onCreate: GPS FROM populate gps_before Check:"+String.valueOf(gps.getLatitude())+" : "+String.valueOf(gps.getLongitude()));
        if (gps.getLatitude()!=0.0) {
            SharedPreferences.Editor SP = PreferenceManager.getDefaultSharedPreferences(getBaseContext()).edit();
            SP.putString("lat", String.valueOf(gps.getLatitude()));
            SP.putString("lng", String.valueOf(gps.getLongitude()));
            SP.apply();

            Log.d(TAG, "onCreate: GPS FROM populate gps_ok:"+String.valueOf(gps.getLatitude())+" : "+String.valueOf(gps.getLongitude()));
            myLocation.setLatitude(gps.getLatitude());
            myLocation.setLongitude(gps.getLongitude());
            upStatus="gps_ok";
        }else {
            upStatus="gps_no";
            if (lat.equals("0.0")){
                Log.d(TAG, "onCreate: GPS FROM populate gps_NO:"+lat+" : "+lng);
            }
        }


//        Toast.makeText(MainActivity.this, "location ="+myLocation.getLatitude()+","+myLocation.getLongitude(), Toast.LENGTH_SHORT).show();

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
            Snackbar.make(findViewById(R.id.viewpager_container), "لطفا دسترسی به جی پی اس را فعال کنید.", Snackbar.LENGTH_INDEFINITE)
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



    public void reqUser() {
//        Toast.makeText(MainActivity.this, " reqUser", Toast.LENGTH_SHORT).show();
        RequestQueue queue = Volley.newRequestQueue(this);
        String url = getString(R.string.server)+"/j/usereg.php";
        StringRequest postRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>()
                {
                    @Override
                    public void onResponse(String response) {
                        failCount=0;
                        userRegFlag=true;
                    }
                },
                new Response.ErrorListener()
                {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        failCount++;

                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String>params = new HashMap<String,String>();
                params.put("lat",lat);
                params.put("lng",lng);
                params.put("state",state);
                params.put("uid",uid);
                params.put("ph",mobile);

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

        Log.d(TAG, "findArea: gps mylocation="+myLocation.getLatitude());
        for (Area area:areaArrayList){

            mycity.setLatitude(area.getAlat());
            mycity.setLongitude(area.getAlng());
            myDistance=myLocation.distanceTo(mycity);
            distances.add(myDistance);
        }

        int minIndex = distances.indexOf(Collections.min(distances));
        myArea=areaArrayList.get(minIndex);
        server=areaArrayList.get(minIndex).getServer();
        Log.d(TAG, "findArea: gps area-find="+myArea.getAfname()+" "+server);
        areaFlag=true;
        if (upStatus.equals("gps_ok")){

            if (server.equals(SP1.getString("server", "0"))){
                server=SP1.getString("server", "0");
            }else {
//                Toast.makeText(HomeActivity.this, "نزدیکترین منطقه شما:"+myArea.getAfname(), Toast.LENGTH_LONG).show();

            }


        }else if(upStatus.equals("gps_no")){

            server=SP1.getString("server", "0");
            if (server.equals("0")){
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
        }else {
//            Toast.makeText(HomeActivity.this, "موقعیت جدید یافت نشد  انتقال به منطقه قبلی شما:"+myArea.getAfname(), Toast.LENGTH_LONG).show();
            server=myArea.getServer();
        }
    }



    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == 1) {
            final LocationManager manager = (LocationManager) getSystemService( Context.LOCATION_SERVICE );

            if ( manager.isProviderEnabled( LocationManager.GPS_PROVIDER ) ) {
                // Call your Alert message
                gps2 =new GPSTracker(HomeActivity.this);
                lat=String.valueOf(gps2.getLatitude());
                lng=String.valueOf(gps2.getLongitude());
                findArea();
            }else {
//                Toast.makeText(HomeActivity.this, "جی پی اس روشن نشد موقعیت پیشفرض انتخاب شد:"+myArea.getAfname(), Toast.LENGTH_LONG).show();

            }

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
                        if(dbAreaHandler.getnumofrow()>0)
                        dbAreaHandler.removeAll();
                        failCount=0;
                        areaFlag=true;

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
                                area.setState(jsonObject.getString("state"));
                                area.setAlat(Float.valueOf(jsonObject.getString("alat")));
                                area.setAlng(Float.valueOf(jsonObject.getString("alng")));
//                                area.setAdiameter(jsonObject.getInt("adiameter"));
                                area.setServer(jsonObject.getString("server"));
                                area.setZoom(jsonObject.getInt("azoom"));
                                area.setPic(jsonObject.getString("pic"));
                                area.setDescription(jsonObject.getString("memo"));

                                dbAreaHandler.addJob(area);
                            }

                            netState=1;
                            MessagesFragment.netState=1;
                            HomeFragment.netState=1;
                            MainFragment.netState=1;

                        } catch (JSONException e) {
                            e.printStackTrace();
                            failCount++;
                        }

                    }
                },
                new Response.ErrorListener()
                {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        failCount++;

                        String message = null;
                        if (error instanceof NetworkError) {
                            remain=false;
                            message = "Cannot connect to Internet...Please check your connection!";
                        } else if (error instanceof ServerError) {
                            remain = SP1.getBoolean("connected", false);
                            message = "The server could not be found. Please try again after some time!!";
                        } else if (error instanceof AuthFailureError) {
                            remain=false;
                            message = "Cannot connect to Internet...Please check your connection!";
                        } else if (error instanceof ParseError) {
                            message = "Parsing error! Please try again after some time!!";
                        } else if (error instanceof NoConnectionError) {
                            remain=false;
                            message = "Cannot connect to Internet...Please check your connection!";
                        } else if (error instanceof TimeoutError) {
                            remain=false;
                            message = "Connection TimeOut! Please check your internet connection.";
                        }
                        failCount++;
                        netState=0;
                        MessagesFragment.netState=0;
                        HomeFragment.netState=0;
                        MainFragment.netState=0;
                        Log.d("ERROR","Home Activity area error => "+error.toString()+" fail="+failCount+" net_state"+netState);
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

//
//    private class ExampleNotificationReceivedHandler implements OneSignal.NotificationReceivedHandler {
//        @Override
//        public void notificationReceived(OSNotification notification) {
//            JSONObject data = notification.payload.additionalData;
//            String notificationID = notification.payload.notificationID;
//            String title = notification.payload.title;
//            String body = notification.payload.body;
//            String smallIcon = notification.payload.smallIcon;
//            String largeIcon = notification.payload.largeIcon;
//            String bigPicture = notification.payload.bigPicture;
//            String smallIconAccentColor = notification.payload.smallIconAccentColor;
//            String sound = notification.payload.sound;
//            String ledColor = notification.payload.ledColor;
//            int lockScreenVisibility = notification.payload.lockScreenVisibility;
//            String groupKey = notification.payload.groupKey;
//            String groupMessage = notification.payload.groupMessage;
//            String fromProjectNumber = notification.payload.fromProjectNumber;
//            String rawPayload = notification.payload.rawPayload;
//
//            String customKey;
//            Log.i("OneSignalExample", "NotificationID received: " + notificationID);
//
//            if (data != null) {
//                customKey = data.optString("activity", null);
//                if (customKey != null){
//                    Log.i("OneSignalExample", "customkey set with value: " + customKey);
//                    String openURL = data.optString("openURL", null);
////                    Toast.makeText(mContext, customKey, Toast.LENGTH_SHORT).show();
//                    switch (customKey){
//                        case "profile":
//                            Intent intent=new Intent(HomeActivity.this,ProfileActivity.class);
//                            intent.putExtra("body", body);
//                            startActivity(intent);
//
//                            break;
//                        case "news":
//                            Intent intent2=new Intent(HomeActivity.this,MessagesFragment.class);
//                            startActivity(intent2);
//
//                            break;
//                        case "web":
//                            Intent intent3=new Intent(HomeActivity.this,GreenActivity.class);
//                            intent3.putExtra("openURL", openURL);
//                            startActivity(intent3);
//
//                            break;
//
//                        default:
//                            Intent popup=new Intent(HomeActivity.this,PopupActivity.class);
//                            popup.putExtra("title", title);
//                            popup.putExtra("body", body);
//                            popup.putExtra("score", "تشکر");
//                            startActivity(popup);
//
//                            break;
//                    }
//                }
//
//            }
//        }
//    }
//
//
//    private class ExampleNotificationOpenedHandler implements OneSignal.NotificationOpenedHandler {
//        // This fires when a notification is opened by tapping on it.
//        @Override
//        public void notificationOpened(OSNotificationOpenResult result) {
//            OSNotificationAction.ActionType actionType = result.action.type;
//            JSONObject data = result.notification.payload.additionalData;
//            String launchUrl = result.notification.payload.launchURL; // update docs launchUrl
//            String title = result.notification.payload.title;
//            String body = result.notification.payload.body;
//            String customKey="";
//            String openURL = null;
//            Object activityToLaunch = HomeActivity.class;
//
//            if (data != null) {
//                customKey = data.optString("activity", null);
//                openURL = data.optString("openURL", null);
//
//                if (customKey != null){
//                    Log.i("OneSignalExample", "customkey set with value: " + customKey);
//                    switch (customKey){
//                        case "profile":
//                            Intent intent=new Intent(HomeActivity.this,ProfileActivity.class);
//                            intent.putExtra("body", body);
//                            startActivity(intent);
//                            activityToLaunch = ProfileActivity.class;
//                            break;
//                        case "news":
//                            Intent intent2=new Intent(HomeActivity.this,MessagesFragment.class);
//                            startActivity(intent2);
//                            activityToLaunch = MessagesFragment.class;
//                            break;
//                        case "web":
//                            Intent intent3=new Intent(HomeActivity.this,GreenActivity.class);
//                            intent3.putExtra("openURL", openURL);
//                            startActivity(intent3);
//                            activityToLaunch = MessagesFragment.class;
//                            break;
//
//                        default:
//                            Intent popup=new Intent(HomeActivity.this,PopupActivity.class);
//                            popup.putExtra("title", title);
//                            popup.putExtra("body", body);
//                            popup.putExtra("score", "تشکر");
//                            startActivity(popup);
//                            activityToLaunch = MessagesFragment.class;
//                            break;
//                    }
//                }
//
//
//                if (openURL != null)
//                    Log.i("OneSignalExample", "openURL to webview with URL value: " + openURL);
//            }
//
//
//            if (actionType == OSNotificationAction.ActionType.ActionTaken) {
//                Log.i("OneSignalExample", "Button pressed with id: " + result.action.actionID);
//
//                if (result.action.actionID.equals("id1")) {
//                    Log.i("OneSignalExample", "button id called: " + result.action.actionID);
//                    activityToLaunch = GreenActivity.class;
//                } else
//                    Log.i("OneSignalExample", "button id called: " + result.action.actionID);
//            }
//            // The following can be used to open an Activity of your choice.
//            // Replace - getApplicationContext() - with any Android Context.
//            // Intent intent = new Intent(getApplicationContext(), YourActivity.class);
////            Intent intent = new Intent(getApplicationContext(), (Class<?>) activityToLaunch);
//            // intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT | Intent.FLAG_ACTIVITY_NEW_TASK);
////            o
////            intent.putExtra("openURL", openURL);
////            Log.i("OneSignalExample", "openURL = " + openURL);
//            // startActivity(intent);
////            startActivity(intent);
//
//            // Add the following to your AndroidManifest.xml to prevent the launching of your main Activity
//            //   if you are calling startActivity above.
//        /*
//           <application ...>
//             <meta-data android:name="com.onesignal.NotificationOpened.DEFAULT" android:value="DISABLE" />
//           </application>
//        */
//        }
//    }





    private class AsyncTaskRunner extends AsyncTask<String, String, JSONObject> {

        private String resp;
//                ProgressDialog progressDialog;

        @Override
        protected JSONObject doInBackground(String... params) {
            publishProgress("Sleeping..."); // Calls onProgressUpdate()
            try {
                //coord = "40.7127,-74.0059";//debug
                URL url = new URL(String.format((API + darkCoords + extra)));

                HttpURLConnection connection =
                        (HttpURLConnection) url.openConnection();
                connection.getInputStream();

                System.out.print("CONNECTION:::" + connection.getInputStream());

                BufferedReader reader = new BufferedReader(
                        new InputStreamReader(connection.getInputStream()));

                System.out.print("url:::");
                StringBuffer json = new StringBuffer(1024);
                String tmp = "";
                while ((tmp = reader.readLine()) != null)
                    json.append(tmp).append("\n");
                reader.close();

                JSONObject data = new JSONObject(json.toString());
                Log.d("receive", "getJSON: " + data.toString());
                return data;
            } catch (Exception e) {
                e.printStackTrace();
                failCount++;
                weatherFlag = false;
                return null;
            }
        }


        @Override
        protected void onPostExecute(JSONObject result) {
            // execution of result of Long time consuming operation
//                    progressDialog.dismiss();
            renderWeather(result);
        }


        @Override
        protected void onPreExecute() {
//                    progressDialog = ProgressDialog.show(MainActivity.this,
//                            "ProgressDialog",
//                            "Wait for "+time.getText().toString()+ " seconds");
        }


        @Override
        protected void onProgressUpdate(String... text) {
//                    finalResult.setText(text[0]);

        }
    }


    private void renderWeather(JSONObject json) {
        weatherFlag = true;

        try {
            JSONObject today_array = json.getJSONObject("currently");
            sm=today_array.getString("summary");
            txtTodayWeather.setText(tarjomeh(sm));
            txtTempreture.setText(today_array.getString("temperature").substring(0, 2)+ "°");
            findSky(sm);

        } catch (Exception e) {
            Log.e("SimpleWeather", "One or more fields not found in the JSON data \n" + API + darkCoords + extra + "\n" + e.toString());
            failCount++;


        }

    }


    private int findIco(String trans) {
        trans = trans.toLowerCase();


        if (trans.contains("scattered"))
            return R.drawable.iscloudy;

        if (trans.contains("partly cloudy"))
            return R.drawable.ipcloudy;

        if (trans.contains("mostly cloudy"))
            return R.drawable.imcloudy;

        if (trans.contains("shower"))
            return R.drawable.ishower;

        if (trans.contains("light rain"))
            return R.drawable.irain;

        if (trans.contains("rain"))
            return R.drawable.irain;

        if (trans.contains("storm"))
            return R.drawable.ithunder;

        if (trans.contains("snow"))
            return R.drawable.isnow;

        if (trans.contains("clear"))
            return R.drawable.isunny;

        if (trans.contains("mist")) {
            return R.drawable.imcloudy;
        } else {

            return R.drawable.imcloudy;

        }


    }

    public String tarjomeh(String str1) {
        str1 = str1.toLowerCase();
        String[] find = {"mist", "snow", "thunderstorm", "fogy", "rain", "light", "shower", "broken", "cloudy", "clear", "sky", "today", "morning", "afternoon", "night", "evening",
                "later", "tomorrow", "in the", "during", "heavy", "medium", "possible", "very", "sleet", "centimeters", "wind", "humidity", "fog", "less-than", "low", "high",
                "starting", "throughout", "mostly", "the", "day", "partly", "mostly", "continuing", "until", "haze","drizzle","breezy","and","overcast","humid"};
        String[] replace = {"مه", "برف", "طوفان", "مه", "باران", "بارش آرام", "شدید", "تکه ای", "ابری", "صاف", "آسمان", "امروز", "صبح", "بعدظهر", "شب", "غروب",
                "بعد", "فردا", "در", "طول", "سنگین", "متوسط", "احتمال", "زیاد", "بوران", "سانت", "باد", "رطوبت", "مه", "کمتراز", "کم",
                "زیاد", "شروع", " تمام_", "بیشتر", " ", "روز", "قسمتی", "بیشتر", "ادامه دارد", "تا", "مه خفیف","باران خفیف","باد ملایم","و","","شرجی"};

        for (int i = 0; i < find.length; i++) {
            str1 = str1.replace(find[i], replace[i]);
        }
        return str1;
    }

    public void findSky(String trans) {
        trans = trans.toLowerCase();

        if (trans.contains("scattered"))
            imgWeather.setImageResource(R.drawable.ipcloudy);

        if (trans.contains("partly cloudy"))
            imgWeather.setImageResource(R.drawable.ipcloudy);

        if (trans.contains("mostly cloudy"))
            imgWeather.setImageResource(R.drawable.imcloudy);

        if (trans.contains("shower"))
            imgWeather.setImageResource(R.drawable.ishower);

        if (trans.contains("light rain"))
            imgWeather.setImageResource(R.drawable.irain);

        if (trans.contains("rain"))
            imgWeather.setImageResource(R.drawable.irain);

        if (trans.contains("storm"))
            imgWeather.setImageResource(R.drawable.ithunder);

        if (trans.contains("snow"))
            imgWeather.setImageResource(R.drawable.isnow);

        if (trans.contains("clear"))
            imgWeather.setImageResource(R.drawable.isunny);

        if (trans.contains("mist")) {
            imgWeather.setImageResource(R.drawable.imcloudy);
        } else {

            imgWeather.setImageResource(R.drawable.ipcloudy);

        }

    }


    private int textResources(int i) {
        switch (i) {

            case 1:
                return R.string.text1;

            case 2:
                return R.string.text2;

            case 3:
                return R.string.text3;

            case 4:
                return R.string.text4;

            case 5:
                return R.string.text5;

            case 6:
                return R.string.text6;

            case 7:
                return R.string.text7;

            case 8:
                return R.string.text8;


            default:
                return R.string.text9;


        }

    }



    public void reqEvent() {
//        Toast.makeText(MainActivity.this, " reqEvent", Toast.LENGTH_SHORT).show();
        RequestQueue queue = Volley.newRequestQueue(this);
        String url = REGISTER_URL;
        final ProgressBar progressEvnt=findViewById(R.id.progressEvent);
        final ImageView imgEventRetry=findViewById(R.id.imgEventRetry);
        progressEvnt.setVisibility(View.VISIBLE);
        StringRequest postRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>()
                {
                    @Override
                    public void onResponse(String response) {
                        eventFlag = true;
                        Log.d(TAG, "onResponse: Event Recieved"+response);
                        progressEvnt.setVisibility(View.GONE);
                        imgEventRetry.setVisibility(View.GONE);
                        JSONArray jsonArray = null;
                        try {
                            jsonArray = new JSONArray(response);
                            if (response.length()>4){
                                evnts.clear();
                                dbEventHandler.removeAll();
                            }

                            JSONObject jsonObject = jsonArray.getJSONObject(0);

                            for (int i = 0; i < jsonArray.length(); i++) {
                                jsonObject = jsonArray.getJSONObject(i);
                                Evnt evnt=new Evnt();
                                evnt.setName(jsonObject.getString("name"));
                                evnt.setOwner(jsonObject.getString("owner"));
                                evnt.setContact(jsonObject.getString("contact"));
                                evnt.setPlace(jsonObject.getString("place"));
                                evnt.setEdate(jsonObject.getString("edate"));
                                evnt.setEtime(jsonObject.getString("etime"));
                                evnt.setInfo(jsonObject.getString("info"));
                                evnt.setMemo(jsonObject.getString("memo"));
                                evnts.add(evnt);
                                dbEventHandler.addEvent(evnt);
                            }
                            evntRvAdapter.notifyDataSetChanged();

                        } catch (JSONException e) {
                            e.printStackTrace();
                            progressEvnt.setVisibility(View.GONE);
                            imgEventRetry.setVisibility(View.VISIBLE);
                        }



                    }
                },
                new Response.ErrorListener()
                {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // TODO Auto-generated method stub
                        Log.d("ERROR","error => "+error.toString());
                        progressEvnt.setVisibility(View.GONE);
                        imgEventRetry.setVisibility(View.VISIBLE);
//                        Toast.makeText(MainActivity.this, "Error reqEvent", Toast.LENGTH_SHORT).show();
                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String>params = new HashMap<String,String>();
                params.put("db", "events");
                params.put("state", state);
                return params;
            }
        };
        queue.add(postRequest);

    }

    public void reqVisitPlaces() {
//        Toast.makeText(MainActivity.this, " reqEvent", Toast.LENGTH_SHORT).show();
        RequestQueue queue = Volley.newRequestQueue(this);
        String url = REGISTER_URL;
        final ProgressBar progressEvnt=findViewById(R.id.progressEvent);
        final ImageView imgEventRetry=findViewById(R.id.imgEventRetry);
        progressEvnt.setVisibility(View.VISIBLE);
        StringRequest postRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>()
                {
                    @Override
                    public void onResponse(String response) {
                        visitFlag = true;
                        Log.d(TAG, "onResponse: Visit Places Recieved"+response);
                        progressEvnt.setVisibility(View.GONE);
                        imgEventRetry.setVisibility(View.GONE);
                        JSONArray jsonArray = null;
                        visitFlag=true;
                        try {
                            jsonArray = new JSONArray(response);
                            if (response.length()>4){
                                visitPlaces.clear();
                            }

                            JSONObject jsonObject = jsonArray.getJSONObject(0);

                            for (int i = 0; i < jsonArray.length(); i++) {
                                jsonObject = jsonArray.getJSONObject(i);
                                VisitPlace visit=new VisitPlace();
                                visit.setName(jsonObject.getString("name"));
                                visit.setLat(jsonObject.getString("lat"));
                                visit.setLng(jsonObject.getString("lng"));
                                visit.setYear(jsonObject.getString("pyear"));
                                visit.setTicket(jsonObject.getString("ticket"));
                                visit.setDays(jsonObject.getString("pdays"));
                                visit.setHours(jsonObject.getString("phours"));
                                visit.setTel(jsonObject.getString("tel"));
                                visit.setAddress(jsonObject.getString("address"));
                                visit.setPic(jsonObject.getString("pic"));
                                visit.setMemo(jsonObject.getString("memo"));
                                visitPlaces.add(visit);

                            }
                            if (visitPlaces.size()>0) {
                                String imgPath=server+ "/assets/images/places/" + visitPlaces.get(0).getPic();
                                Glide.with(HomeActivity.this).load(imgPath)
                                        .thumbnail(0.5f)
                                        .into(imgBackcity);
                                Log.d(TAG, "onResponse: ok back ground set "+imgPath);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            progressEvnt.setVisibility(View.GONE);
                            imgEventRetry.setVisibility(View.VISIBLE);
                        }



                    }
                },
                new Response.ErrorListener()
                {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // TODO Auto-generated method stub
                        Log.d("ERROR","error => "+error.toString());
                        progressEvnt.setVisibility(View.GONE);
                        imgEventRetry.setVisibility(View.VISIBLE);
                        visitFlag=false;
//                        Toast.makeText(MainActivity.this, "Error reqEvent", Toast.LENGTH_SHORT).show();
                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String>params = new HashMap<String,String>();
                params.put("db", "visitplaces");
                params.put("state", state);
                return params;
            }
        };
        queue.add(postRequest);

    }


    public class StaticPushReceiver extends BefrestPushReceiver {
        @Override
        public void onPushReceived(Context context, BefrestMessage[] messages) {

            for (BefrestMessage msg:messages)
            {
                Log.d(TAG, "onPushReceived: befrest"+msg.toString());
            }
        }
    }

//    // File url to download
    private static String file_url = "http://www.idpz.ir/apk/app.apk";

    public void readVersion() {
//        Toast.makeText(MainActivity.this, " reqEvent", Toast.LENGTH_SHORT).show();
        RequestQueue queue = Volley.newRequestQueue(this);
        String url = VERSION_URL;
        final ProgressBar progressEvnt=findViewById(R.id.progressEvent);
        final ImageView imgEventRetry=findViewById(R.id.imgEventRetry);
        progressEvnt.setVisibility(View.VISIBLE);
        StringRequest postRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>()
                {
                    @Override
                    public void onResponse(String response) {
                        webVersion = Integer.parseInt(response); // My TextFile has 1 lines
                        if (webVersion > appVer) {
                            Toast.makeText(HomeActivity.this, "نسخه جدید برنامه موجود است", Toast.LENGTH_SHORT).show();
                            int downloadId = PRDownloader.download(file_url, Environment.getExternalStorageDirectory().toString()
                                    , "smartcity.apk")
                                    .build()
                                    .setOnStartOrResumeListener(new OnStartOrResumeListener() {
                                        @Override
                                        public void onStartOrResume() {

                                        }
                                    })
                                    .setOnPauseListener(new OnPauseListener() {
                                        @Override
                                        public void onPause() {

                                        }
                                    })
                                    .setOnCancelListener(new OnCancelListener() {
                                        @Override
                                        public void onCancel() {

                                        }
                                    })
                                    .setOnProgressListener(new OnProgressListener() {
                                        @Override
                                        public void onProgress(Progress progress) {

                                        }
                                    })
                                    .start(new OnDownloadListener() {
                                        @Override
                                        public void onDownloadComplete() {

                                            File toInstall = new File(Environment
                                                    .getExternalStorageDirectory().toString(), "smartcity.apk");
                                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                                                Uri apkUri = FileProvider.getUriForFile(HomeActivity.this, BuildConfig.APPLICATION_ID + ".provider", toInstall);
                                                Intent intent = new Intent(Intent.ACTION_INSTALL_PACKAGE);
                                                intent.setData(apkUri);
                                                intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                                                HomeActivity.this.startActivity(intent);
                                            } else {
                                                Uri apkUri = Uri.fromFile(toInstall);
                                                Intent intent = new Intent(Intent.ACTION_VIEW);
                                                intent.setDataAndType(apkUri, "application/vnd.android.package-archive");
                                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                                HomeActivity.this.startActivity(intent);
                                            }

                                        }

                                        @Override
                                        public void onError(Error error) {

                                        }


                                    });
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
        ) ;
        queue.add(postRequest);

    }

}