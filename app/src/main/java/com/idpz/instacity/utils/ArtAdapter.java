package com.idpz.instacity.utils;

/**
 * Created by h on 2018/03/12.
 */

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.idpz.instacity.R;
import com.idpz.instacity.models.Arts;

import java.util.List;

public class ArtAdapter extends BaseAdapter {


    private Activity activity;
    private LayoutInflater inflater;
    private List<Arts> adsList;
    String server="";


    public ArtAdapter(Activity activity, List<Arts> adsList) {
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
    public View getView(int position, View convertView, ViewGroup parent) {
        SharedPreferences SP1;
        SP1 = PreferenceManager.getDefaultSharedPreferences(activity);
        server=SP1.getString("server", "0");
        if (inflater == null)
            inflater = (LayoutInflater) activity
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        if (convertView == null)
            convertView = inflater.inflate(R.layout.row_ads, null);

        ImageView thumbNail = convertView
                .findViewById(R.id.imgAds);
        TextView name = convertView.findViewById(R.id.textAdsTitle);
        TextView memo = convertView.findViewById(R.id.textAdsMemo);
        TextView tel = convertView.findViewById(R.id.textAdsTel);
        TextView address = convertView.findViewById(R.id.textAdsAddress);
//        ImageView thumbNail = (ImageView) convertView.findViewById(R.id.thumbnail);

        // getting Food data for the row
        Arts m = adsList.get(position);

        // thumbnail image
        Glide.with(activity).load(m.getPic())
                .thumbnail(0.5f)
                .into(thumbNail);
        // title
        name.setText(m.getName());

        // rating
        memo.setText( m.getMemo());


        // release year
        tel.setText("کد:"+m.getId()+" جنس:"+m.getMaterial()+" رنگ:"+m.getColor());
        address.setText(m.getPrice());
        return convertView;
    }

}