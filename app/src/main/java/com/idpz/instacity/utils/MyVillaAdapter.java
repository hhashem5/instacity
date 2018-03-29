package com.idpz.instacity.utils;

/**
 * Created by h on 2018/03/12.
 */

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.idpz.instacity.R;
import com.idpz.instacity.models.Villa;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MyVillaAdapter extends BaseAdapter {


    private Activity activity;
    private LayoutInflater inflater;
    private List<Villa> adsList;
    String server="",delArtPath="";
    ImageLoader imageLoader = AppController.getInstance().getImageLoader();

    public MyVillaAdapter(Activity activity, List<Villa> adsList) {
        this.activity = activity;
        this.adsList = adsList;
    }

    @Override
    public int getCount() {
        return adsList.size();
    }

    @Override
    public Object getItem(int location) {
        return adsList.get(location);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        SharedPreferences SP1;
        SP1 = PreferenceManager.getDefaultSharedPreferences(activity);
        server=SP1.getString("server", "0");
        delArtPath=server+"/i/delart.php";
        if (inflater == null)
            inflater = (LayoutInflater) activity
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        if (convertView == null)
            convertView = inflater.inflate(R.layout.row_arts, null);

        if (imageLoader == null)

            imageLoader = AppController.getInstance().getImageLoader();
        ImageView thumbNail = (ImageView) convertView
                .findViewById(R.id.imgAds);
        ImageView imgDelArt = (ImageView) convertView.findViewById(R.id.imgDelArt);
        ImageView imgEditArt = (ImageView) convertView.findViewById(R.id.imgEditArt);
        TextView name = (TextView) convertView.findViewById(R.id.textAdsTitle);
        TextView memo = (TextView) convertView.findViewById(R.id.textAdsMemo);

//        ImageView thumbNail = (ImageView) convertView.findViewById(R.id.thumbnail);
        com.nostra13.universalimageloader.core.ImageLoader.getInstance().init(ImageLoaderConfiguration.createDefault(activity));
        // getting Food data for the row
        Villa m = adsList.get(position);

        // thumbnail image

        DisplayImageOptions options;
        options = new DisplayImageOptions.Builder()
                .showImageOnLoading(R.drawable.loading)
                .showImageForEmptyUri(R.drawable.noimage)
                .showImageOnFail(R.drawable.noimage)
                .cacheInMemory(true)
                .cacheOnDisk(true)
                .considerExifParams(true)
                .bitmapConfig(Bitmap.Config.RGB_565)
                .build();
        com.nostra13.universalimageloader.core.ImageLoader.getInstance().displayImage(m.getPic(),thumbNail,options);

        // title
        name.setText(m.getName());

        // rating
        memo.setText( m.getMemo());

        imgEditArt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder alertbox = new AlertDialog.Builder(v.getRootView().getContext());
                alertbox.setMessage("محصول مورد نظر حذف شود");
                alertbox.setTitle("حذف محصول");
                alertbox.setIcon(R.drawable.ic_del);

                alertbox.setPositiveButton("حذف",
                        new DialogInterface.OnClickListener() {

                            public void onClick(DialogInterface arg0,
                                                int arg1) {

                                delArt(adsList.get(position).getId()+"",adsList.get(position).getOwner()+"");

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

        // release year

        return convertView;
    }

    public void delArt(final String id,final String mobile) {
        RequestQueue queue = Volley.newRequestQueue(activity);
        StringRequest postRequest = new StringRequest(Request.Method.POST, delArtPath,
                new Response.Listener<String>()
                {
                    public static final String TAG = "delete answare in villas";

                    @Override
                    public void onResponse(String response) {
                        Log.d(TAG, "onResponse: "+response);
                    }
                },
                new Response.ErrorListener()
                {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // TODO Auto-generated method stub
                        Log.d("ERROR","error => "+error.toString());
//                        txtczstatus.setText(error.toString()+"مشکلی در ارسال داده پیش آمده دوباره تلاش کنید");
                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String>params = new HashMap<String,String>();

                params.put("id", id);
                params.put("owner", mobile);
                params.put("table", "villas");
                return params;
            }
        };
        queue.add(postRequest);


    }

}