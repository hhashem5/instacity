package com.idpz.instacity.utils;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.util.Log;
import android.view.MenuItem;

import com.idpz.instacity.Home.HomeActivity;
import com.idpz.instacity.Home.VisitSearchActivity;
import com.idpz.instacity.Travel.TourismActivity;
import com.idpz.instacity.Profile.ProfileActivity;
import com.idpz.instacity.R;
import com.idpz.instacity.MapCity.SearchActivity;
import com.ittianyu.bottomnavigationviewex.BottomNavigationViewEx;

import static android.content.Intent.FLAG_ACTIVITY_REORDER_TO_FRONT;

/**
 * Created by h on 2017/12/30.
 */

public class BottomNavigationViewHelper {
    private static final String TAG="BottomNavigationViewHel";

    public static void setupBottomNavigationView(BottomNavigationViewEx bottomNavigationViewEx){
        Log.d(TAG,"setting up bottom-ex helper");
        bottomNavigationViewEx.enableAnimation(false);
        bottomNavigationViewEx.enableItemShiftingMode(false);
        bottomNavigationViewEx.enableShiftingMode(false);
        bottomNavigationViewEx.setTextVisibility(true);

    }

    public static void enableNavigation(final Context context, BottomNavigationViewEx view){
        view.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()){

                    case R.id.ic_house:
                        Intent intent1=new Intent(context, HomeActivity.class);
                        intent1.addFlags(FLAG_ACTIVITY_REORDER_TO_FRONT);
                        context.startActivity(intent1);
                        break;
                    case R.id.ic_search:
                        Intent intent2=new Intent(context, SearchActivity.class);
                        intent2.addFlags(FLAG_ACTIVITY_REORDER_TO_FRONT);
                        context.startActivity(intent2);
                        break;
                    case R.id.ic_share:
                        Intent intent3=new Intent(context, VisitSearchActivity.class);
                        intent3.addFlags(FLAG_ACTIVITY_REORDER_TO_FRONT);
                        context.startActivity(intent3);
                        break;
                    case R.id.ic_like:
                        Intent intent4=new Intent(context, TourismActivity.class);
                        intent4.addFlags(FLAG_ACTIVITY_REORDER_TO_FRONT);
                        context.startActivity(intent4);
                        break;
                    case R.id.ic_profile:
                        Intent intent5=new Intent(context, ProfileActivity.class);
                        intent5.addFlags(FLAG_ACTIVITY_REORDER_TO_FRONT);
                        context.startActivity(intent5);
                        break;
                }



                return false;
            }
        });
    }
}
