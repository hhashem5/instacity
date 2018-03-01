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
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.idpz.instacity.AlarmService;
import com.idpz.instacity.Area;
import com.idpz.instacity.R;
import com.idpz.instacity.utils.BottomNavigationViewHelper;
import com.idpz.instacity.utils.GPSTracker;
import com.idpz.instacity.utils.MainfeedListAdapter;
import com.idpz.instacity.utils.SectionsPagerAdapter;
import com.ittianyu.bottomnavigationviewex.BottomNavigationViewEx;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

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

public class HomeActivity extends AppCompatActivity implements
        MainfeedListAdapter.OnLoadMoreItemsListener{

    @Override
    public void onLoadMoreItems() {
        Log.d(TAG, "onLoadMoreItems: displaying more photos");

    }
    private static final String AREA_URL = "http://mscity.ir/i/getarea.php";
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
    String lat="",lng="",server="";
    String REG_USER_URL="",REGISTER_PROFILE="";
    String myname="0",melliid="0",pas="", mobile="0", birth="0",pic="", gender="0", edu="0", edub="0", job="0", jobb="0", fav="0", money="0";
    String upStatus="start";
    Boolean userRegFlag=false,areaFlag=false,connected=false,userProfileFlag=false,gpsCheck=false;
    // declare for popup window
    Button showPopupBtn, closePopupBtn;
    PopupWindow popupWindow;
    RelativeLayout relLayout1;
    TextView tvPopup;
    ListView lv;
    //


    //widgets
    GPSTracker gps,gps2;
    private ViewPager mViewPager;
    private FrameLayout mFrameLayout;
    private RelativeLayout mRelativeLayout;
    SharedPreferences SP1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        Log.d(TAG, "onCreate: starting.");
        lv=(ListView)findViewById(R.id.lvHomeContent);

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

        if(server.equals("0"))server=getString(R.string.server);

        if (!lat.equals("0.0")){
            myLocation.setLatitude(Float.valueOf(lat));
            myLocation.setLongitude(Float.valueOf(lng));
        }
        Log.d(TAG, "onCreate: GPS FROM SP:"+lat+" : "+lng);

        populateGPS();

        REG_USER_URL=server+"/i/usereg.php";
        REGISTER_PROFILE=server+"/i/profile.php";

        if (mobile.equals("0"))
        {
            Intent intent=new Intent(HomeActivity.this,LoginActivity.class);
            startActivity(intent);
            finish();
        }
        Intent in1 = new Intent(HomeActivity.this, AlarmService.class);
        startService(in1);

        mViewPager = (ViewPager) findViewById(R.id.viewpager_container);
        mFrameLayout = (FrameLayout) findViewById(R.id.container);
        mRelativeLayout = (RelativeLayout) findViewById(R.id.relLayoutParent);

//        Toast.makeText(HomeActivity.this, "درحال موقعیت یابی و یافتن منطقه شما", Toast.LENGTH_LONG).show();

        initImageLoader();
        setupBottomNavigationView();
        setupViewPager();
//        hideLayout();
//        showPopup();


        new Thread() {
            @Override
            public void run() {
                while (!userRegFlag||!userProfileFlag) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
                            if (connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState() == NetworkInfo.State.CONNECTED ||
                                    connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState() == NetworkInfo.State.CONNECTED) {
                                //we are connected to a network
                                connected = true;
                            } else {
                                connected = false;
//                                txtNews.setText("اینترنت وصل نیست");
                            }


//                            if (connected && !areaFlag) {
//                                reqArea();   //یافتن منطقه کاربر و اتصال به سوور هماه منطقه
//                            }
                            if (!userRegFlag&&connected){
                                reqUser();  //register user on server
                                showLayout();
                            }
                            if (!userProfileFlag&&connected)
                                regUserProfile();  // save user info on server
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
        relLayout1 = (RelativeLayout) findViewById(R.id.relLayout1);


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
        super.onBackPressed();
        finishAffinity();
        System.exit(0);

    }


    private void initImageLoader(){

        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(this)

			.build();
        ImageLoader.getInstance().init(config);

    }

    /**
     * Responsible for adding the 3 tabs: Camera, Home, Messages
     */
    private void setupViewPager(){
        SectionsPagerAdapter adapter = new SectionsPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(new CameraFragment()); //index 0
        adapter.addFragment(new HomeFragment()); //index 1
        adapter.addFragment(new MessagesFragment()); //index 2
        mViewPager.setAdapter(adapter);
        mViewPager.setOffscreenPageLimit(2);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(mViewPager);

        tabLayout.getTabAt(0).setIcon(R.drawable.ic_video);
        tabLayout.getTabAt(1).setIcon(R.drawable.ic_instagram_black);
        tabLayout.getTabAt(2).setIcon(R.drawable.ic_news);
        tabLayout.getTabAt(1).select();
    }

    /**
     * BottomNavigationView setup
     */
    private void setupBottomNavigationView(){
        Log.d(TAG, "setupBottomNavigationView: setting up BottomNavigationView");
        BottomNavigationViewEx bottomNavigationViewEx = (BottomNavigationViewEx) findViewById(R.id.bottomNavViewBar);
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
            Snackbar.make(mViewPager, "لطفا دسترسی به جی پی اس را فعال کنید.", Snackbar.LENGTH_INDEFINITE)
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
                        userRegFlag=true;
                    }
                },
                new Response.ErrorListener()
                {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // TODO Auto-generated method stub

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

    public void regUserProfile() {
        RequestQueue queue = Volley.newRequestQueue(this);
        String url =server+"/i/profile.php";
        StringRequest postRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>()
                {
                    @Override
                    public void onResponse(String response) {
                            userProfileFlag=true;
//                        txtczstatus.setText(response.toString());
                    }
                },
                new Response.ErrorListener()
                {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // TODO Auto-generated method stub
                        Log.d("ERROR","error => "+error.toString());
//                        txtczstatus.setText(error.toString()+"مشکلی در ارسال داده پیش آمده دوباره تلاش کنید");
                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String>params = new HashMap<String,String>();

                params.put("name", myname);
                params.put("mob", mobile);
                params.put("pas", pas);
                params.put("birth", birth);
                params.put("gen", gender);
                params.put("edu", edu);
                params.put("eb", edub);
                params.put("job", job);
                params.put("jb", jobb);
                params.put("pic", pic);
                params.put("fav", fav);
                params.put("meli",melliid);
                params.put("mny", money);
                params.put("lat", lat);
                params.put("lng", lng);
                return params;
            }
        };
        queue.add(postRequest);


    }
    public void reqArea() {
        RequestQueue queue = Volley.newRequestQueue(this);
        String url = AREA_URL;
        StringRequest postRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>()
                {
                    @Override
                    public void onResponse(String response) {
                        JSONArray jsonArray= null;

                        try {
                            jsonArray = new JSONArray(response);
                            areaFlag=true;
                            JSONObject jsonObject=jsonArray.getJSONObject(0);
                            areaArrayList.clear();
                            cities.clear();
                            String all="";
                            for (int i=jsonArray.length();i>0;i--) {
                                jsonObject = jsonArray.getJSONObject(i-1);
                                float myDistance=0;
                                Area area=new Area();
                                area.setId(jsonObject.getInt("aid"));
                                area.setAename(jsonObject.getString("aename"));
                                area.setAfname(jsonObject.getString("afname"));
                                area.setAlat(Float.valueOf(jsonObject.getString("alat")));
                                area.setAlng(Float.valueOf(jsonObject.getString("alng")));
                                area.setAdiameter(jsonObject.getInt("adiameter"));
                                area.setServer(jsonObject.getString("server"));
                                area.setZoom(jsonObject.getInt("azoom"));

                                mycity.setLatitude(area.getAlat());
                                mycity.setLongitude(area.getAlng());
                                myDistance=Math.round(myLocation.distanceTo(mycity));
                                all=all+area.getAfname()+" فاصله با شما= "+myDistance+"\n";
//                                distances.add(myDistance);


                                cities.add(area.getAfname());
//                                adapter.notifyDataSetChanged();
                                areaArrayList.add(area);

                            }
//                                tvPopup.setText(all);
//                        initiatePopupWindow(all);
//                            Toast.makeText(mContext, all, Toast.LENGTH_LONG).show();
                            findArea();





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
//                        txtczstatus.setText(error.toString()+"مشکلی در ارسال داده پیش آمده دوباره تلاش کنید");
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

//    private void initiatePopupWindow(String msg) {
//        try {
//            //We need to get the instance of the LayoutInflater, use the context of this activity
//            LayoutInflater inflater = (LayoutInflater) HomeActivity.this
//                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
//            //Inflate the view from a predefined XML layout
//            View layout = inflater.inflate(R.layout.layout_pupup,
//                    (ViewGroup) findViewById(R.id.popup_rel));
//            // create a 300px width and 470px height PopupWindow
//            PopupWindow pw = new PopupWindow(layout, 300, 470, true);
//            // display the popup in the center
//            pw.showAtLocation(getCurrentFocus(), Gravity.CENTER, 0, 0);
//
//            TextView mResultText = (TextView) layout.findViewById(R.id.tvPopup);
//            mResultText.setText(msg);
////            Button cancelButton = (Button) layout.findViewById(R.id.end_data_send_button);
////            cancelButton.setOnClickListener(cancel_button_click_listener);
//
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }

//    public void showPopup(){
////        LayoutInflater layoutInflater = (LayoutInflater) HomeActivity.this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
////        View customView = layoutInflater.inflate(R.layout.layout_pupup,null);
////
////        closePopupBtn = (Button) customView.findViewById(R.id.closePopupBtn);
////        tvPopup=(TextView)customView.findViewById(R.id.tvPopup);
////
////
////        //instantiate popup window
////        popupWindow = new PopupWindow(customView, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
////
////        //display the popup window
////        popupWindow.showAtLocation(lv, Gravity.CENTER, 0, 0);
////
////        //close the popup window on button click
////        closePopupBtn.setOnClickListener(new View.OnClickListener() {
////            @Override
////            public void onClick(View v) {
////                popupWindow.dismiss();
////            }
////        });
//        LayoutInflater inflater = (LayoutInflater)
//                this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
//        PopupWindow pw = new PopupWindow(
//                inflater.inflate(R.layout.layout_pupup, null, false),
//                100,
//                100,
//                true);
//        // The code below assumes that the root container has an id called 'main'
//        pw.showAtLocation(lv, Gravity.CENTER, 0, 0);
//    }
}
