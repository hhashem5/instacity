package com.idpz.instacity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * Created by h on 2017/05/26.
 */

public class MyBroadcastReceiver extends BroadcastReceiver {


    @Override

    public void onReceive(Context context, Intent intent) {

        if (intent.getAction().equalsIgnoreCase(Intent.ACTION_BOOT_COMPLETED)) {

            Intent serviceIntent = new Intent(context, AlarmService.class);

            context.startService(serviceIntent);

        }

    }


}