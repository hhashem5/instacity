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
import com.idpz.instacity.Travel.AdsAddActivity;
import com.idpz.instacity.R;
import com.idpz.instacity.models.Ads;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MyAdsAdapter extends BaseAdapter {


    private Activity activity;
    private LayoutInflater inflater;
    private List<Ads> adsList;
    String state="",delArtPath="";
    private Context context;

    public MyAdsAdapter(Activity activity, List<Ads> adsList,Context context) {
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
        state=SP1.getString("state", "050001");
        delArtPath=context.getString(R.string.server)+"/j/delart.php";
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
        Ads m = adsList.get(position);


        Glide.with(activity).load(m.getPic())
                .thumbnail(0.5f)
                .into(thumbNail);

        // title
        name.setText("کد:"+m.getId()+" "+m.getTitle());

        // rating
        memo.setText( m.getMemo());

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
                                delArt(adsList.get(position).getId()+"",adsList.get(position).getOwner()+"");
                                if(context instanceof AdsAddActivity){
                                    ((AdsAddActivity)context).reqAds();
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
                    @Override
                    public void onResponse(String response) {

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

                params.put("state", state);
                params.put("id", id);
                params.put("owner", mobile);
                params.put("table", "ads");
                return params;
            }
        };
        queue.add(postRequest);


    }

}