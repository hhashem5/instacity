package com.idpz.instacity.Like;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;

import com.idpz.instacity.R;
import com.idpz.instacity.utils.BottomNavigationViewHelper;
import com.idpz.instacity.utils.SectionsPagerAdapter;
import com.ittianyu.bottomnavigationviewex.BottomNavigationViewEx;

public class TourismActivity extends AppCompatActivity {
    private static final String TAG = "TourismActivity";
    private static final int ACTIVITY_NUM = 3;
    private static final int HOME_FRAGMENT = 1;
    private static final int REQUEST_ACCESS_LOCATION = 0;


    RelativeLayout relLayout1;


    private ViewPager mViewPager;
    private FrameLayout mFrameLayout;
    private RelativeLayout mRelativeLayout;
    TabLayout tabLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tourism);


        Log.d(TAG, "onCreate: starting.");


        mViewPager = findViewById(R.id.viewpager_container);
        mFrameLayout = findViewById(R.id.container);
        mRelativeLayout = findViewById(R.id.relLayoutParent);

        setupBottomNavigationView();
        setupViewPager();
//        hideLayout();
//        showPopup();

        relLayout1 = findViewById(R.id.relLayout1);

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
        finish();

    }



    /**
     * Responsible for adding the 3 tabs: Camera, Home, Messages
     */
    private void setupViewPager(){
        SectionsPagerAdapter adapter = new SectionsPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(new StationFragment()); //index 0
        adapter.addFragment(new AdsFragment()); //index 1
        adapter.addFragment(new JobFragment()); //index 2
        mViewPager.setAdapter(adapter);
        mViewPager.setOffscreenPageLimit(2);

        tabLayout = findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(mViewPager);

//        tabLayout.getTabAt(0).setIcon(R.drawable.ic_video);
        tabLayout.getTabAt(0).setText("گردشگری");
//        tabLayout.getTabAt(1).setIcon(R.drawable.ic_instagram_black);
        tabLayout.getTabAt(1).setText("نیازمندیها");
//        tabLayout.getTabAt(2).setIcon(R.drawable.ic_news);
        tabLayout.getTabAt(2).setText("صنایع دستی");
        tabLayout.getTabAt(1).select();
    }

    @Override
    public void onStart() {
        super.onStart();
        tabLayout.getTabAt(getIntent().getIntExtra("position", 1)).select();
    }

    // تنظیم نوار پایین برنامه
    private void setupBottomNavigationView(){
        Log.d(TAG,"seting up bottom navigation view");
        BottomNavigationViewEx bottomNavigationViewEx= findViewById(R.id.bottomNavViewBar);
        BottomNavigationViewHelper.setupBottomNavigationView(bottomNavigationViewEx);
        BottomNavigationViewHelper.enableNavigation(TourismActivity.this,bottomNavigationViewEx);
        Menu menu=bottomNavigationViewEx.getMenu();
        MenuItem menuItem=menu.getItem(ACTIVITY_NUM);
        menuItem.setChecked(true);
    }

}
