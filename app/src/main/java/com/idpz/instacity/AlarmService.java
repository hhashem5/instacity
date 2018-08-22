package com.idpz.instacity;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.media.MediaPlayer;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.IBinder;
import android.preference.PreferenceManager;

import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.idpz.instacity.MapCity.SearchActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;


public class AlarmService extends Service {

    private static final String TAG = "AlarmService";
    String myLat,myLng;
    Float myDist=0f;

    Location myLocation,carLocation;
    public static volatile boolean isRunning = false;
    public static volatile int alarmDist = 300;
    Boolean connected=false;
    Context context;
    public static volatile String msgid="1";
    SharedPreferences SP;
    String lastNewsId="1";
    String server="",REGISTER_URL="",state="",carcode="0";
    @Override
    public void onCreate() {

        SP = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
        server="http://idpz.ir";
        state=SP.getString("state","050001");
        carcode=SP.getString("carcode","1010");
        isRunning=SP.getBoolean("alarm",false);
        alarmDist = Integer.valueOf(SP.getString("alarm_len", "300"));
        REGISTER_URL=server+"/j/rcv.php";
//        Toast.makeText(AlarmService.this, "سرویس هشدار شروع شد", Toast.LENGTH_SHORT).show();


        myLocation=new Location("");
        carLocation=new Location("");

        myLat = SP.getString("lat", "0.0");
        myLng = SP.getString("lng", "0.0");
        myLocation.setLatitude(Double.valueOf(myLat));
        myLocation.setLongitude(Double.valueOf(myLng));
        lastNewsId=SP.getString("notification","1");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

//        Log.i(TAG, "Service onStartCommand");

        //Creating new thread for my service
        //Always write your long running tasks in a separate thread, to avoid ANR
        new Thread(new Runnable() {
            @Override
            public void run() {

                while (isRunning ){
                    //Your logic that service will perform will be placed here
                    ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
                    //we are connected to a network
//                                txtNews.setText("اینترنت وصل نیست");
                    connected = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState() == NetworkInfo.State.CONNECTED ||
                            connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState() == NetworkInfo.State.CONNECTED;

                    try {
//                        REGISTER_URL=server+"/j/rcv.php";


                        if (connected) {
                            if (isRunning)reqRcv();

                        }

                        Thread.sleep(10000);

                    } catch (Exception e) {
                        Log.e(TAG, "run: loop", e);
                    }

            }
                //Stop service once it finishes its task
                stopSelf();
            }
        }).start();

        return Service.START_STICKY;
    }


    @Override
    public IBinder onBind(Intent arg0) {
//        Log.i(TAG, "Service onBind");
        return null;
    }

    @Override
    public void onDestroy() {

        isRunning = true;

//        Log.i(TAG, "Service onDestroy");
    }


    public void reqRcv() {
        RequestQueue queue = Volley.newRequestQueue(this);
        String url = REGISTER_URL;
        StringRequest postRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>()
                {
                    @Override
                    public void onResponse(String response) {
//                        Toast.makeText(AlarmService.this," alarm distance="+alarmDist+ "response="+response+" car="+carcode+" dist="+myDist, Toast.LENGTH_SHORT).show();
                        if (SP.getBoolean("alarm",false)) {
                            int count = 0, code = 0;
                            Double lat = 0.0, lng = 0.0, speed = 0.0;
                            JSONArray jsonArray = null;
                            try {
                                jsonArray = new JSONArray(response);

                                myLat = SP.getString("lat", "0.0");
                                myLng = SP.getString("lng", "0.0");

                                myLocation.setLatitude(Double.valueOf(myLat));
                                myLocation.setLongitude(Double.valueOf(myLng));
                                JSONObject jsonObject = jsonArray.getJSONObject(0);
                                count = 0;
                                for (int i = jsonArray.length(); i > 0; i--) {
                                    jsonObject = jsonArray.getJSONObject(i - 1);

                                    code = jsonObject.getInt("rcode");
                                    lat = jsonObject.getDouble("rlat");
                                    lng = jsonObject.getDouble("rlng");
                                    speed = jsonObject.getDouble("speed");
                                    carLocation.setLatitude(lat);
                                    carLocation.setLongitude(lng);
                                    myDist = Float.valueOf(Math.round(myLocation.distanceTo(carLocation)));
                                    if (myDist < alarmDist) {
                                        final MediaPlayer mp = MediaPlayer.create(AlarmService.this, R.raw.ticktac);
                                        try {
                                            if (mp.isPlaying()) {
                                                mp.stop();
                                                mp.release();
                                            }

                                            mp.start();
                                            mp.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                                                public void onCompletion(MediaPlayer mp) {
                                                    mp.release();
                                                }
                                            });
                                            Intent intent = new Intent(AlarmService.this, SearchActivity.class);
                                            Bundle c2 = new Bundle();
                                            c2.putString("key", msgid); //Your id
                                            intent.putExtras(c2); //Put your id to your next Intent
                                            intent.addFlags(Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT);


                                            PendingIntent contentIntent = PendingIntent.getActivity(AlarmService.this, 0, intent, 0);


                                            NotificationCompat.Builder b = new NotificationCompat.Builder(AlarmService.this);

                                            b.setAutoCancel(true)
                                                    .setDefaults(Notification.DEFAULT_ALL)
                                                    .setWhen(System.currentTimeMillis())
                                                    .setSmallIcon(R.drawable.mlogo64)
                                                    .setTicker("پیام از:دهکده هوشمند")
                                                    .setContentTitle("خودروی شهری")
                                                    .setContentText("خودرو به شما نزدیک است")
                                                    .setDefaults(Notification.DEFAULT_LIGHTS | Notification.DEFAULT_SOUND)
                                                    .setContentIntent(contentIntent)
                                                    .setContentInfo("");


                                            NotificationManager notificationManager = (NotificationManager) AlarmService.this.getSystemService(Context.NOTIFICATION_SERVICE);
                                            notificationManager.notify(1, b.build());

                                            Log.d(TAG, "alarm playing: ");
                                            stopSelf();
                                        } catch (Exception e) {
                                            e.printStackTrace();
                                        }
                                        isRunning = false;
                                        SharedPreferences.Editor SP = PreferenceManager.getDefaultSharedPreferences(getBaseContext()).edit();
                                        SP.putBoolean("alarm", false);
                                        SP.putString("tabpos", "1");
                                        SP.commit();
                                    }


//                            Toast.makeText(CarAlarmService.this," alarm distance="+alarmDist+ "Lat="+lat+" Lng="+lng+" Speed="+speed+" dist="+myDist, Toast.LENGTH_SHORT).show();
                                }


                            } catch (JSONException e) {
                                e.printStackTrace();

                            }
                        }

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
                params.put("code",carcode);
                params.put("state",state);
                params.put("limit","1");
                return params;
            }
        };
        queue.add(postRequest);

    }
}
