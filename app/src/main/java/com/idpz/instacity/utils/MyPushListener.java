package com.idpz.instacity.utils;

import android.content.Intent;
import android.util.Log;

import com.idpz.instacity.Home.GreenActivity;
import com.idpz.instacity.Home.MessagesFragment;
import com.idpz.instacity.Profile.ProfileActivity;
import com.idpz.instacity.Share.PopupActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import co.ronash.pushe.PusheListenerService;

/**
 * Created by h on 08/05/2018.
 */

public class MyPushListener extends PusheListenerService {
    @Override
    public void onMessageReceived(JSONObject message, JSONObject content){
        android.util.Log.i("Pushe","Custom json Message: "+ message.toString());
        android.util.Log.i("Pushe","Custom json conteent: "+ content.toString());
        if (message.length() == 0)
            return; //json is empty
        android.util.Log.i("Pushe","Custom json Message: "+ message.toString()); //print json to logCat

        //your code
        try{
            String s1 = message.getString("title");
            String openURL = message.getString("openurl");
            String s2 = message.getString("content");
            String customKey= message.getString("customkey");
            if (customKey != null){
                    Log.i("Pushe", "customkey set with value: " + customKey);
                    switch (customKey){
                        case "profile":
                            Intent intent=new Intent(getApplicationContext(),ProfileActivity.class);
                            intent.putExtra("body", s2);
                            intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT | Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(intent);
                            break;
                        case "news":
                            Intent intent2=new Intent(getApplicationContext(),MessagesFragment.class);
                            intent2.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT | Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(intent2);
                            break;
                        case "web":
                            Intent intent3=new Intent(getApplicationContext(),GreenActivity.class);
                            intent3.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT | Intent.FLAG_ACTIVITY_NEW_TASK);
                            intent3.putExtra("openURL", openURL);
                            startActivity(intent3);
                            break;

                        default:
                            Intent popup=new Intent(getApplicationContext(),PopupActivity.class);
                            popup.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT | Intent.FLAG_ACTIVITY_NEW_TASK);
                            popup.putExtra("title", s1);
                            popup.putExtra("body", s2);
                            popup.putExtra("score", "تشکر");
                            startActivity(popup);
                            break;
                    }
                }


            android.util.Log.i("Pushe","Json Message\n Titr: " + s1 + "\n Matn: " + s2);
        } catch (JSONException e) {
            android.util.Log.e("","Exception in parsing json" ,e);
        }

    }
}