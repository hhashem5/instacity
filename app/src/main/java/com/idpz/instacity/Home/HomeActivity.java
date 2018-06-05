package com.idpz.instacity.Home;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
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
import com.idpz.instacity.Area;
import com.idpz.instacity.R;
import com.idpz.instacity.utils.BottomNavigationViewHelper;
import com.idpz.instacity.utils.DBAreaHandler;
import com.idpz.instacity.utils.GPSTracker;

import com.idpz.instacity.utils.SectionsPagerAdapter;
import com.ittianyu.bottomnavigationviewex.BottomNavigationViewEx;
import com.onesignal.OSNotification;
import com.onesignal.OSNotificationAction;
import com.onesignal.OSNotificationOpenResult;
import com.onesignal.OneSignal;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static android.Manifest.permission.ACCESS_COARSE_LOCATION;
import static android.Manifest.permission.ACCESS_FINE_LOCATION;

//import com.onesignal.OneSignal;

public class HomeActivity extends AppCompatActivity {


    private static final String AREA_URL = "http://idpz.ir/i/getarea.php";
    private static final String TAG = "HomeActivity";
    private static final int ACTIVITY_NUM = 0;
    private static final int HOME_FRAGMENT = 1;
    private static final int REQUEST_ACCESS_LOCATION = 0;
    private Context mContext = HomeActivity.this;
    Area myArea;
    ArrayList<Area> areaArrayList;
    Location myLocation, mycity, myHome;
    List<String> cities=new ArrayList<>();
    List<Float> distances=new ArrayList<Float>();
    String lat="",lng="",server="",aename="";
    String REG_USER_URL="",REGISTER_PROFILE="";
    String myname="0",melliid="0",pas="", mobile="0", birth="0",pic="", gender="0", edu="0", edub="0", job="0", jobb="0", fav="0", money="0";
    String upStatus="start",ctname="";
    Boolean userRegFlag=false,areaFlag=false,connected=false,userProfileFlag=false,gpsCheck=false,remain=true;
    boolean doubleBackToExitPressedOnce = false;

    public static volatile boolean showSplashFlag=true;
    int failCount=0,netState=3;
    // declare for popup window

    RelativeLayout relLayout1;

    ListView lv;
    //
    TabLayout tabLayout;

    //widgets
    GPSTracker gps,gps2;
    private ViewPager mViewPager;
    private FrameLayout mFrameLayout;
    private RelativeLayout mRelativeLayout;
    SharedPreferences SP1;
    DBAreaHandler dbAreaHandler;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        Log.d(TAG, "onCreate: starting.");

        lv= findViewById(R.id.lvHomeContent);

        dbAreaHandler=new DBAreaHandler(this);

        areaArrayList = new ArrayList<>();
        myLocation = new Location("myloc");
        mycity = new Location("city");
        myHome = new Location("home");

        SP1 = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
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

        boolean firstTime=!(SP1.getBoolean("firsttime", true));
        boolean splash=SP1.getBoolean("splash", false);
        Log.d(TAG, "onCreate: mobile="+mobile+" myname="+SP1.getString("myname", "0"));
        if (mobile.length() <10)
        {
            Intent intent=new Intent(HomeActivity.this,LoginActivity.class);
            startActivity(intent);
            finish();
        }
        OneSignal.startInit(this)
                .setNotificationReceivedHandler(new ExampleNotificationReceivedHandler())
                .setNotificationOpenedHandler(new ExampleNotificationOpenedHandler())
                .inFocusDisplaying(OneSignal.OSInFocusDisplayOption.Notification)
                .unsubscribeWhenNotificationsAreDisabled(true)
                .init();
        Log.d(TAG, "onCreate: oneSignal Started");
        if(firstTime&&splash) {
            Intent intent = new Intent(HomeActivity.this,SplashScreenActivity.class);
            startActivity(intent);
        }
        SharedPreferences.Editor SP = PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).edit();
        SP.putInt("net_status", 3); // set net status to waiting
        SP.putBoolean("splash",true); // show splash Screen next time
        SP.apply();
        if(server.equals("0"))server=getString(R.string.server);

        if (!lat.equals("0.0")){
            myLocation.setLatitude(Float.valueOf(lat));
            myLocation.setLongitude(Float.valueOf(lng));
        }
        Log.d(TAG, "onCreate: GPS FROM SP:"+lat+" : "+lng);

        populateGPS();

        REG_USER_URL=server+"/i/usereg.php";
        REGISTER_PROFILE=server+"/i/profile.php";



//        Intent in1 = new Intent(HomeActivity.this, AlarmService.class);
//        startService(in1);

        mViewPager = findViewById(R.id.viewpager_container);
        mFrameLayout = findViewById(R.id.container);
        mRelativeLayout = findViewById(R.id.relLayoutParent);

//        Toast.makeText(HomeActivity.this, "درحال موقعیت یابی و یافتن منطقه شما", Toast.LENGTH_LONG).show();
        aename=SP1.getString("aename", "zibadasht");

        JSONObject tags = new JSONObject();
        try {
            tags.put("area", aename);
            tags.put("user_mobile", mobile);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        OneSignal.sendTags(tags);

        setupBottomNavigationView();
        setupViewPager();
//        hideLayout();
//        showPopup();


        new Thread() {
            @Override
            public void run() {
                while (remain&(!userRegFlag||!userRegFlag)) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
                            if (connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState() == NetworkInfo.State.CONNECTED ||
                                    connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState() == NetworkInfo.State.CONNECTED) {

                                connected = true;

                                if (netState==0)remain=false;
                                Log.d(TAG, "Home Act run: net State="+netState);
                            } else {
//                                SharedPreferences.Editor SP = PreferenceManager.getDefaultSharedPreferences(getBaseContext()).edit();
//                                SP.putBoolean("connected", false);
//                                SP.apply();
                                connected = false;

                            }

                            if (failCount>3){
                                MessagesFragment.netState=0;
                                HomeFragment.netState=0;
                                MainFragment.netState=0;
                                Toast.makeText(getApplicationContext(), "اتصال اینترنت را بررسی کنید و دوباره امتحان کنید", Toast.LENGTH_SHORT).show();
                                remain=false;
                                Log.d(TAG, "run: fail count succeed  exit");
                            }

                            if (connected && !areaFlag) {
                                reqArea();   //یافتن منطقه کاربر و اتصال به سوور هماه منطقه
                            }
                            if (!userRegFlag&&connected){
                                reqUser();  //register user on server
                                showLayout();
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


//        showPopupBtn = (Button) findViewById(R.id.showPopupBtn);
        relLayout1 = findViewById(R.id.relLayout1);


                //instantiate the popup.xml layout file


        //end oncreate
    }





    public void hideLayout(){
        Log.d(TAG, "hideLayout: hiding layout");
        mRelativeLayout.setVisibility(View.GONE);
        mFrameLayout.setVisibility(View.VISIBLE);
    }


    public void showLayout(){
        Log.d(TAG, "hideLayout: showing layout");
        mRelativeLayout.setVisibility(View.VISIBLE);
        mFrameLayout.setVisibility(View.GONE);
    }

    @Override
    public void onBackPressed() {

        if (doubleBackToExitPressedOnce) {
            super.onBackPressed();
            finishAffinity();
            System.exit(0);
        }
//        if (!SP1.getString("tabpos","1").equals("1")) {
            SharedPreferences.Editor SP = PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).edit();
            SP.putString("tabpos", "1");
            SP.apply();
            tabLayout.getTabAt(1).select();
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
     * Responsible for adding the 3 tabs: Camera, Home, Messages
     */
    private void setupViewPager(){
        SectionsPagerAdapter adapter = new SectionsPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(new HomeFragment()); //index 0
        adapter.addFragment(new MainFragment()); //index 1
        adapter.addFragment(new MessagesFragment()); //index 2
        mViewPager.setAdapter(adapter);
        mViewPager.setOffscreenPageLimit(2);

        tabLayout = findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(mViewPager);

//        tabLayout.getTabAt(0).setIcon(R.drawable.ic_video);
        tabLayout.getTabAt(0).setText("پیام شهروندی");
//        tabLayout.getTabAt(1).setIcon(R.drawable.ic_instagram_black);
        tabLayout.getTabAt(1).setText(ctname);
//        tabLayout.getTabAt(2).setIcon(R.drawable.ic_news);
        tabLayout.getTabAt(2).setText("اخبارمحلی");
        String tabpos= SP1.getString("tabpos","1");
        tabLayout.getTabAt(Integer.valueOf(tabpos)).select();

    }

    @Override
    public void onStart() {
        super.onStart();
        String tabpos= SP1.getString("tabpos","1");
        tabLayout.getTabAt(Integer.valueOf(tabpos)).select();


    }



    public void tabselect(){
        String tabpos= SP1.getString("tabpos","1");
        tabLayout.getTabAt(Integer.valueOf(tabpos)).select();
    }

    /**
     * BottomNavigationView setup
     */
    private void setupBottomNavigationView(){
        Log.d(TAG, "setupBottomNavigationView: setting up BottomNavigationView");
        BottomNavigationViewEx bottomNavigationViewEx = findViewById(R.id.bottomNavViewBar);
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
        String url = server+"/i/usereg.php";
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
                Toast.makeText(HomeActivity.this, "نزدیکترین منطقه شما:"+myArea.getAfname(), Toast.LENGTH_LONG).show();

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
            Toast.makeText(HomeActivity.this, "موقعیت جدید یافت نشد  انتقال به منطقه قبلی شما:"+myArea.getAfname(), Toast.LENGTH_LONG).show();
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
                Toast.makeText(HomeActivity.this, "جی پی اس روشن نشد موقعیت پیشفرض انتخاب شد:"+myArea.getAfname(), Toast.LENGTH_LONG).show();

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


    private class ExampleNotificationReceivedHandler implements OneSignal.NotificationReceivedHandler {
        @Override
        public void notificationReceived(OSNotification notification) {
            JSONObject data = notification.payload.additionalData;
            String notificationID = notification.payload.notificationID;
            String title = notification.payload.title;
            String body = notification.payload.body;
            String smallIcon = notification.payload.smallIcon;
            String largeIcon = notification.payload.largeIcon;
            String bigPicture = notification.payload.bigPicture;
            String smallIconAccentColor = notification.payload.smallIconAccentColor;
            String sound = notification.payload.sound;
            String ledColor = notification.payload.ledColor;
            int lockScreenVisibility = notification.payload.lockScreenVisibility;
            String groupKey = notification.payload.groupKey;
            String groupMessage = notification.payload.groupMessage;
            String fromProjectNumber = notification.payload.fromProjectNumber;
            String rawPayload = notification.payload.rawPayload;

            String customKey;

            Log.i("OneSignalExample", "NotificationID received: " + notificationID);

            if (data != null) {
                customKey = data.optString("customkey", null);
                if (customKey != null)
                    Log.i("OneSignalExample", "customkey set with value: " + customKey);
            }
        }
    }


    private class ExampleNotificationOpenedHandler implements OneSignal.NotificationOpenedHandler {
        // This fires when a notification is opened by tapping on it.
        @Override
        public void notificationOpened(OSNotificationOpenResult result) {
            OSNotificationAction.ActionType actionType = result.action.type;
            JSONObject data = result.notification.payload.additionalData;
            String launchUrl = result.notification.payload.launchURL; // update docs launchUrl

            String customKey;
            String openURL = null;
            Object activityToLaunch = HomeActivity.class;

            if (data != null) {
                customKey = data.optString("customkey", null);
                openURL = data.optString("openURL", null);

                if (customKey != null)
                    Log.i("OneSignalExample", "customkey set with value: " + customKey);

                if (openURL != null)
                    Log.i("OneSignalExample", "openURL to webview with URL value: " + openURL);
            }

            if (actionType == OSNotificationAction.ActionType.ActionTaken) {
                Log.i("OneSignalExample", "Button pressed with id: " + result.action.actionID);

                if (result.action.actionID.equals("id1")) {
                    Log.i("OneSignalExample", "button id called: " + result.action.actionID);
                    activityToLaunch = GreenActivity.class;
                } else
                    Log.i("OneSignalExample", "button id called: " + result.action.actionID);
            }
            // The following can be used to open an Activity of your choice.
            // Replace - getApplicationContext() - with any Android Context.
            // Intent intent = new Intent(getApplicationContext(), YourActivity.class);
            Intent intent = new Intent(getApplicationContext(), (Class<?>) activityToLaunch);
            // intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT | Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT | Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.putExtra("openURL", openURL);
            Log.i("OneSignalExample", "openURL = " + openURL);
            // startActivity(intent);
            startActivity(intent);

            // Add the following to your AndroidManifest.xml to prevent the launching of your main Activity
            //   if you are calling startActivity above.
        /*
           <application ...>
             <meta-data android:name="com.onesignal.NotificationOpened.DEFAULT" android:value="DISABLE" />
           </application>
        */
        }
    }


}
