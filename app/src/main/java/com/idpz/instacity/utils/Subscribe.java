package com.idpz.instacity.utils;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Handler;
import android.util.Log;

import com.idpz.instacity.R;

import java.io.IOException;
import java.util.List;

import co.ronash.pushe.Pushe;


/**
 * Created by exshinigami on 9/21/15.
 */
public class Subscribe implements Runnable {

    private static final String TAG = "SubscribeRunnable";
    Context context;
    String channel;
    Handler handler;
    ProgressDialog progressDialog;


    public Subscribe(Context context, ProgressDialog progressDialog, Handler handler, String channel) {
        this.channel = channel.trim().replaceAll("\\s", "-");
        this.context = context;
        this.progressDialog = progressDialog;
        this.handler = handler;

    }

    @Override
    public void run() {
        Utility.showProgressDialog(progressDialog, handler, context.getString(R.string.subscribe_progress_dialog_message));

        try {
            Utility.isConnectingToInternet(context);
            subscribeToChannel(channel);

            Log.d(TAG, String.format("Subscribed to %s successfully", channel));
            Utility.showToast(context, handler, context.getString(R.string.subscribe_successfully));
        }catch (IllegalArgumentException e) {
            Utility.showToast(context, handler, e.getMessage());
            Log.e(TAG, e.getMessage());
        }catch (IOException e) {
            Utility.showToast(context, handler, e.getMessage());
            Log.e(TAG, e.getMessage());
        }catch (InterruptedException e) {
            Utility.showToast(context, handler, e.getMessage());
            Log.e(TAG, e.getMessage());
        }catch (IllegalAccessError e) {
            Utility.showToast(context, handler, e.getMessage());
            Log.e(TAG, e.getMessage());
        }

        Utility.dissmissProgressDialog(progressDialog, handler);
    }

    private void subscribeToChannel(String channel) throws IllegalArgumentException, IllegalAccessError {

        final String newChannel = channel + "-1";



        try {
            Pushe.subscribe(context, newChannel);
        }catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("کانال موجود نیست093");
        }



    }
}
