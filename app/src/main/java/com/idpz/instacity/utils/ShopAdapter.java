package com.idpz.instacity.utils;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
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
import com.idpz.instacity.Search.StoreRegActivity;
import com.idpz.instacity.models.Shop;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by h on 3/15/2017.
 */

public class ShopAdapter extends ArrayAdapter {
    private int resource;
    private Activity activity;
    private ArrayList<Shop> shopArrayList;
    String server="",delArtPath="";

    public ShopAdapter(Activity activity, int resource, ArrayList object) {
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
        server=SP1.getString("server", "0");
        delArtPath=server+"/i/delart.php";
        View view=convertView;
        view=this.activity.getLayoutInflater().inflate(this.resource,null);
        ImageView img= view.findViewById(R.id.imgShop);
        TextView txtShopName= view.findViewById(R.id.textShopName);
        TextView txtShopOwner= view.findViewById(R.id.textShopOwner);
        TextView txtShopKey= view.findViewById(R.id.textShopKey);
        TextView txtShopTel= view.findViewById(R.id.textShopTell);
        TextView txtShopAddress= view.findViewById(R.id.textShopAddress);
        ImageView imgDelArt = view.findViewById(R.id.imgDelShop);
        ImageView imgEditArt = view.findViewById(R.id.imgEditShop);

        Shop shop = shopArrayList.get(position);
        txtShopName.setText(shop.getName());
        txtShopOwner.setText("مالک: "+shop.getOwner());
        txtShopKey.setText(""+shop.getJkey());
        txtShopTel.setText("تلفن:"+shop.getTel()+" موبایل:"+shop.getMobile());
//        switch (shop.getTag().toString()){
//            case "store":
//                img.setImageResource(R.drawable.ishop);
//                break;
//            case "services":
//                img.setImageResource(R.drawable.iservices);
//                break;
//            case "food":
//                img.setImageResource(R.drawable.ifood);
//                break;
//            case "health":
//                img.setImageResource(R.drawable.ihealth);
//                break;
//            case "religion":
//                img.setImageResource(R.drawable.ireligion);
//                break;
//            case "sport":
//                img.setImageResource(R.drawable.isport);
//                break;
//            case "edu":
//                img.setImageResource(R.drawable.iedu);
//                break;
//            default:img.setImageResource(R.drawable.ishop);
//        }

        Glide.with(activity).load(shop.getPic())
                .thumbnail(0.5f)
                .crossFade()
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .placeholder(R.drawable.nopic)
                .into(img);

        imgEditArt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder alertbox = new AlertDialog.Builder(v.getRootView().getContext());
                alertbox.setMessage("آگهی مورد نظر حذف شود");
                alertbox.setTitle("حذف آگهی");
                alertbox.setIcon(R.drawable.ic_del);

                alertbox.setPositiveButton("حذف",
                        new DialogInterface.OnClickListener() {

                            public void onClick(DialogInterface arg0,
                                                int arg1) {
//                                Toast.makeText(activity, "id="+adsList.get(position).getId()+" owner="+adsList.get(position).getOwner(), Toast.LENGTH_SHORT).show();
                                delArt(shopArrayList.get(position).getId()+"",shopArrayList.get(position).getMemo()+"");
                                if(activity instanceof StoreRegActivity){
                                    ((StoreRegActivity)activity).reqMyShop();
                                }
                            }
                        });
                alertbox.setNegativeButton("خیر",new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface arg0,
                                        int arg1) {

                    }
                });
                alertbox.show();
            }
        });


        int dist=0;
//        Location myLoc,shopLoc;
//        shopLoc=null;
//        myLoc=null;
//        SharedPreferences SP = PreferenceManager.getDefaultSharedPreferences(getContext());
//        myLoc.setLatitude(Double.valueOf( SP.getString("lat", "35.711")));
//        myLoc.setLatitude(Double.valueOf( SP.getString("lng", "50.912")));
//
//        shopLoc.setLatitude(Double.valueOf(shop.getJlat()));
//        shopLoc.setLongitude(Double.valueOf(shop.getJlng()));
//
//        dist=Math.round(myLoc.distanceTo(shopLoc));

        txtShopAddress.setText(shop.getAddress());

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

                params.put("id", id);
                params.put("owner", mobile);
                params.put("table", "shops");
                return params;
            }
        };
        queue.add(postRequest);


    }

}
