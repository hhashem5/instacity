package com.idpz.instacity.utils;

/**
 * Created by h on 2018/03/12.
 */

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
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
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.idpz.instacity.Like.VillaAddActivity;
import com.idpz.instacity.R;
import com.idpz.instacity.models.Villa;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MyVillaAdapter extends BaseAdapter {


    private Activity activity;
    private LayoutInflater inflater;
    private List<Villa> adsList;
    String server="",delArtPath="";
    private Context context;

    public MyVillaAdapter(Activity activity, List<Villa> adsList,Context context) {
        this.activity = activity;
        this.adsList = adsList;
        this.context=context;
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


        ImageView thumbNail = convertView
                .findViewById(R.id.imgAds);
        ImageView imgDelArt = convertView.findViewById(R.id.imgDelArt);
        ImageView imgEditArt = convertView.findViewById(R.id.imgEditArt);
        TextView name = convertView.findViewById(R.id.textAdsTitle);
        TextView memo = convertView.findViewById(R.id.textAdsMemo);


        // getting Food data for the row
        Villa m = adsList.get(position);

        // thumbnail image
        Glide.with(activity).load(m.getPic())
                .thumbnail(0.5f)
                .crossFade()
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .placeholder(R.drawable.nopic)
                .into(thumbNail);

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
                                if(context instanceof VillaAddActivity){
                                    ((VillaAddActivity)context).reqVillas();
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