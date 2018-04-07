package com.idpz.instacity.utils;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.idpz.instacity.R;
import com.idpz.instacity.models.Villa;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

import java.util.List;

public class VillaAdapter extends BaseAdapter {
    private Activity activity;
    private LayoutInflater inflater;
    private List<Villa> visitPlaces;


    public VillaAdapter(Activity activity, List<Villa> visitList) {
        this.activity = activity;
        this.visitPlaces = visitList;
    }

    @Override
    public int getCount() {
        return visitPlaces.size();
    }

    @Override
    public Object getItem(int location) {
        return visitPlaces.get(location);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        if (inflater == null)
            inflater = (LayoutInflater) activity
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        if (convertView == null)
            convertView = inflater.inflate(R.layout.row_tourist_home, null);

        ImageView thumbNail = (ImageView) convertView
                .findViewById(R.id.imgPlace);
        TextView title = (TextView) convertView.findViewById(R.id.textPlaceTitle);
        TextView placeYear = (TextView) convertView.findViewById(R.id.textPlaceYear);
        TextView ticket = (TextView) convertView.findViewById(R.id.textPlaceTicket);
        TextView placeDays = (TextView) convertView.findViewById(R.id.textPlaceDays);
        TextView placeHours = (TextView) convertView.findViewById(R.id.textPlaceHours);
        TextView tel = (TextView) convertView.findViewById(R.id.textPlaceTel);
        TextView address = (TextView) convertView.findViewById(R.id.textPlaceAddress);
        TextView memo = (TextView) convertView.findViewById(R.id.textPlaceMemo);
//        ImageView thumbNail = (ImageView) convertView.findViewById(R.id.thumbnail);
        ImageLoader.getInstance().init(ImageLoaderConfiguration.createDefault(activity));
        DisplayImageOptions options;
        options = new DisplayImageOptions.Builder()
                .showImageOnLoading(R.drawable.blur)
                .showImageForEmptyUri(R.drawable.blur)
                .showImageOnFail(R.drawable.blur)
                .cacheInMemory(true)
                .cacheOnDisk(true)
                .considerExifParams(true)
                .bitmapConfig(Bitmap.Config.RGB_565)
                .build();

        // getting Food data for the row
        Villa m = visitPlaces.get(position);
        // title
        title.setText(m.getName());
        // year
        placeYear.setText( m.getArea());
        ticket.setText(m.getPrice());
        placeDays.setText(m.getRoom());
        placeHours.setText(m.getFacility());
        tel.setText(m.getTel());
        address.setText(m.getAddress());
        memo.setText(m.getMemo());

        // thumbnail image


        // thumbnail image
//        thumbNail.setImageUrl(m.getPostImageUrl(), imageLoader);
        ImageLoader.getInstance().displayImage(m.getPic(),thumbNail,options);

        return convertView;
    }

}