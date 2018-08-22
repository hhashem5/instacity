package com.idpz.instacity.utils;

import android.app.Application;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.idpz.instacity.Home.HomeActivity;

import rest.bef.BefrestFactory;

/**
 * Created by h on 08/04/2018.
 */

public class BefrestApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        SharedPreferences SP1 = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        String aename = SP1.getString("aename", "0");
        String mobile = SP1.getString("mobile", "0");
        BefrestFactory.getInstance(this)
                .init(11660, "UnsZbt5ohFqPiymLSfA5_Q", "quickstartchannel")
                .addTopic(aename)
                .addTopic(mobile)
                .start();
    }
}