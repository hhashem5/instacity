package com.idpz.instacity.utils;

import android.app.Activity;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.idpz.instacity.R;
import com.idpz.instacity.models.Shop;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by h on 3/15/2017.
 */

public class MapShopAdapter extends ArrayAdapter {
    private int resource;
    private Activity activity;
    private ArrayList<Shop> shopArrayList;
    String state="",delArtPath="";

    public MapShopAdapter(Activity activity, int resource, ArrayList object) {
        super(activity, resource, object);
        this.activity=activity;
        this.resource=resource;
        this.shopArrayList =object;
    }

    @NonNull
    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        SharedPreferences SP1;
        SP1 = PreferenceManager.getDefaultSharedPreferences(activity);
        state=SP1.getString("state", "050001");
        delArtPath=getContext().getString(R.string.server)+"/j/delart.php";
        View view=convertView;
        view=this.activity.getLayoutInflater().inflate(this.resource,null);
        ImageView img= view.findViewById(R.id.imgShop);
        TextView txtShopName= view.findViewById(R.id.textShopName);
        TextView txtShopOwner= view.findViewById(R.id.textShopOwner);
        TextView txtShopKey= view.findViewById(R.id.textShopKey);
        TextView txtShopTel= view.findViewById(R.id.textShopTell);
        TextView txtShopAddress= view.findViewById(R.id.textShopAddress);


        Shop shop = shopArrayList.get(position);
        txtShopName.setText(shop.getName());
        txtShopOwner.setText("مالک: "+shop.getOwnerName());
        txtShopKey.setText(""+shop.getJkey());
        txtShopTel.setText("تلفن:"+shop.getTel()+" موبایل:"+shop.getMobile());
        int drw=0;
        switch (shop.getTag()){
            case "store":
                drw=R.drawable.ishop;
                break;
            case "services":
                drw=R.drawable.iservices;
                break;
            case "food":
                drw=R.drawable.ifood;
                break;
            case "health":
                drw=R.drawable.ihealth;
                break;
            case "religion":
                drw=R.drawable.ireligion;
                break;
            case "sport":
                drw=R.drawable.isport;
                break;
            case "edu":
                drw=R.drawable.iedu;
                break;
            default:drw=R.drawable.ishop;
        }

        Glide.with(activity).load(shop.getPic())
                .thumbnail(0.5f)
                .placeholder(R.drawable.iloading)
                .error(drw)
                .into(img);



        int dist=0;
        Location myLoc,shopLoc;
        Double lat = 35.711, lng = 50.912, homelat,homelng;
        shopLoc=new Location("");;
        myLoc=new Location("");;
        SharedPreferences SP = PreferenceManager.getDefaultSharedPreferences(getContext());
        lat = Double.valueOf(SP1.getString("lat", "0"));
        lng = Double.valueOf(SP1.getString("lng", "0"));
        homelat = Double.valueOf(SP1.getString("homelat", "0"));
        homelng = Double.valueOf(SP1.getString("homelng", "0"));
        myLoc.setLatitude(lat);
        myLoc.setLongitude(lng);
        if (myLoc.getLatitude() == 0) {
            myLoc.setLatitude(homelat);
            myLoc.setLongitude(homelng);
        }
        shopLoc.setLatitude(Double.valueOf(shop.getJlat()));
        shopLoc.setLongitude(Double.valueOf(shop.getJlng()));

        dist=Math.round(myLoc.distanceTo(shopLoc)/1000);

        txtShopAddress.setText(shop.getAddress()+" (فاصله باشما"+dist+"km)");


        return  view;
    }

    public void delArt(final String id,final String mobile) {
        RequestQueue queue = Volley.newRequestQueue(activity);
        StringRequest postRequest = new StringRequest(Request.Method.POST, delArtPath,
                new Response.Listener<String>()
                {
                    @Override
                    public void onResponse(String response) {
                        Toast.makeText(activity, response, Toast.LENGTH_SHORT).show();
                    }
                },
                new Response.ErrorListener()
                {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // TODO Auto-generated method stub
                        Log.d("ERROR","error => "+error.toString());
                        Toast.makeText(activity, error.toString(), Toast.LENGTH_SHORT).show();
                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String>params = new HashMap<String,String>();

                params.put("state", state);
                params.put("id", id);
                params.put("owner", mobile);
                params.put("table", "shops");
                return params;
            }
        };
        queue.add(postRequest);


    }

}
