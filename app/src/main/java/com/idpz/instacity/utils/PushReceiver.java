package com.idpz.instacity.utils;

import android.app.NotificationManager;
import android.content.Context;
import android.support.v4.app.NotificationCompat;

import com.idpz.instacity.R;

import rest.bef.BefrestMessage;
import rest.bef.BefrestPushReceiver;

/**
 * Created by h on 08/04/2018.
 */

public class PushReceiver extends BefrestPushReceiver {
    @Override
    public void onPushReceived(Context context, BefrestMessage[] messages) {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context)
                .setSmallIcon(R.drawable.mlogo64) //replace with your app icon if it is not correct
                .setTicker("پیام از بفرست!")
                .setContentText(messages[0].getData())
                .setAutoCancel(true);

        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(0, builder.build());
    }
}