package com.idpz.instacity.utils;

/**
 * Created by h on 2018/03/12.
 */

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.toolbox.ImageLoader;
import com.idpz.instacity.R;
import com.idpz.instacity.models.Ads;
import com.nostra13.universalimageloader.core.DisplayImageOptions;

import java.util.List;
public class AdsAdapter extends BaseAdapter {


    private Activity activity;
    private LayoutInflater inflater;
    private List<Ads> adsList;
    String server="";
    ImageLoader imageLoader = AppController.getInstance().getImageLoader();

    public AdsAdapter(Activity activity, List<Ads> adsList) {
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

        if (imageLoader == null)
            imageLoader = AppController.getInstance().getImageLoader();
        ImageView thumbNail = (ImageView) convertView
                .findViewById(R.id.imgAds);
        TextView name = (TextView) convertView.findViewById(R.id.textAdsTitle);
        TextView memo = (TextView) convertView.findViewById(R.id.textAdsMemo);
        TextView tel = (TextView) convertView.findViewById(R.id.textAdsTel);
        TextView address = (TextView) convertView.findViewById(R.id.textAdsAddress);
//        ImageView thumbNail = (ImageView) convertView.findViewById(R.id.thumbnail);

        // getting Food data for the row
        Ads m = adsList.get(position);

        // thumbnail image
        DisplayImageOptions options;
        options = new DisplayImageOptions.Builder()
                .showImageOnLoading(R.drawable.ic_stub)
                .showImageForEmptyUri(R.drawable.noimage)
                .showImageOnFail(R.drawable.noimage)
                .cacheInMemory(true)
                .cacheOnDisk(true)
                .considerExifParams(true)
                .bitmapConfig(Bitmap.Config.RGB_565)
                .build();
        com.nostra13.universalimageloader.core.ImageLoader.getInstance().displayImage(m.getPic(),thumbNail,options);

        // title
        name.setText(m.getTitle());

        // rating
        memo.setText( m.getMemo());


        // release year
        tel.setText(m.getTel());
        address.setText(m.getAddress());
        return convertView;
    }

}