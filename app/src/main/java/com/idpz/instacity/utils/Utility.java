package com.idpz.instacity.utils;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import com.idpz.instacity.R;

import org.json.JSONObject;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.InetAddress;
import java.net.URL;

/**
 * Created by exshinigami on 9/30/15.
 */
public class Utility {

    private static final String TAG = Utility.class.getSimpleName();

    public static void showToast(final Context context, Handler handler, final String message) {
        handler.post(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
            }
        });
    }

    public static void showProgressDialog(final ProgressDialog progressDialog, Handler handler, String message) {
        progressDialog.setCancelable(false);
        progressDialog.setMessage(message);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.setProgress(0);
        progressDialog.setMax(100);

        handler.post(new Runnable() {
            @Override
            public void run() {
                progressDialog.show();
            }
        });
    }

    public static void dissmissProgressDialog(final ProgressDialog progressDialog, Handler handler) {
        handler.post(new Runnable() {
            @Override
            public void run() {
                progressDialog.dismiss();
            }
        });

    }

    public static void isConnectingToInternet(Context context) throws InterruptedException, IOException {
        boolean wifiDataAvailable = false;
        boolean mobileDataAvailable = false;
        ConnectivityManager connectivity = null;
        connectivity = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivity != null)
        {
            NetworkInfo[] networkInfos = connectivity.getAllNetworkInfo();
            if (networkInfos != null) {
                for (NetworkInfo networkInfo : networkInfos) {
                    if(networkInfo.getTypeName().equalsIgnoreCase("WIFI") && networkInfo.isConnected()) {
                        wifiDataAvailable = true;
                    }
                    else if (networkInfo.getTypeName().equalsIgnoreCase("MOBILE") && networkInfo.isConnected()) {
                        mobileDataAvailable = true;
                    }
                }
                if (wifiDataAvailable || mobileDataAvailable) {
                    testPing(context) ;
                    return;
                }
            }
        }
        throw new IllegalAccessError(context.getString(R.string.no_internet));
    }


    private static void testPing(Context context) throws InterruptedException, IOException {
        Log.d(TAG, "pinging 8.8.8.8");
        Runtime runtime = Runtime.getRuntime();
        try
        {
            Process mIpAddrProcess = runtime.exec("/system/bin/ping -c 1 8.8.8.8");
            int mExitValue = mIpAddrProcess.waitFor();
            System.out.println(" mExitValue "+mExitValue);
            if(mExitValue==0){

            }else{
                throw new IOException(context.getString(R.string.no_internet));
            }
        }
        catch (InterruptedException ignore)
        {
            throw new InterruptedException(context.getString(R.string.no_internet));
        }
        catch (IOException e)
        {
            throw new IOException(context.getString(R.string.no_internet));
        }
    }


    private static boolean testPing2() {
        try {
            InetAddress.getByName("http://www.google.com").isReachable(5000);
            return true;
        }catch (Exception e) {
            Log.d(TAG, "err --> " + e.getLocalizedMessage());
        }
        return false;
    }

    private static boolean testNet() {
        try
        {
            HttpURLConnection urlc = (HttpURLConnection) (new URL("http://www.google.com").openConnection());
            urlc.setRequestProperty("User-Agent", "Test");
            urlc.setRequestProperty("Connection", "close");
            urlc.setConnectTimeout(3000); //choose your own timeframe
            urlc.setReadTimeout(4000); //choose your own timeframe
            urlc.connect();
            int code = urlc.getResponseCode();
            urlc.disconnect();
            return (code == 200);
        } catch (IOException e) { }
        return (false);  //connectivity exists, but no internet.
    }

    public static void hideKeyboard(Context ctx) {
        InputMethodManager inputManager = (InputMethodManager) ctx
                .getSystemService(Context.INPUT_METHOD_SERVICE);

        // check if no view has focus:
        View v = ((Activity) ctx).getCurrentFocus();
        if (v == null)
            return;

        inputManager.hideSoftInputFromWindow(v.getWindowToken(), 0);
    }

    public static String prettifyJson(String json) {
        try {
            final JSONObject s = new JSONObject(json);
            final String niceFormattedJson = s.toString(2);
            return niceFormattedJson;
        }catch (Exception e) {

        }
        return null;
    }

    public static final String CUSTOM_CONTETN_PREFERENCES = "app_is_in_foreground";
    public static final String FOREGROUND = "foreground";
    public static final String FILLVIEW = "fillView";

    public static void setForeground(Context context, boolean value) {

        SharedPreferences sharedpreferences = context.getSharedPreferences(CUSTOM_CONTETN_PREFERENCES, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedpreferences.edit();
        editor.putBoolean(FOREGROUND, value);
        editor.commit();
    }


    public static boolean isForeground(Context context) {
        SharedPreferences sharedpreferences = context.getSharedPreferences(CUSTOM_CONTETN_PREFERENCES, Context.MODE_PRIVATE);
        return sharedpreferences.getBoolean(FOREGROUND, false);
    }

    public static void resetFillView(Context context, boolean value) {
        SharedPreferences sharedpreferences = context.getSharedPreferences(CUSTOM_CONTETN_PREFERENCES, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedpreferences.edit();
        editor.putBoolean(FILLVIEW, value);
        editor.commit();
    }

    public static boolean isResetFillView(Context context) {
        SharedPreferences sharedpreferences = context.getSharedPreferences(CUSTOM_CONTETN_PREFERENCES, Context.MODE_PRIVATE);
        return sharedpreferences.getBoolean(FILLVIEW, false);
    }

    public static boolean isContainNonLatin(String input) {
        return input.matches("\\w+");
    }
}
