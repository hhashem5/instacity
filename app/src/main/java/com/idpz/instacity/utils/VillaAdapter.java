package com.idpz.instacity.utils;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.idpz.instacity.R;
import com.idpz.instacity.models.Villa;

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

        ImageView thumbNail = convertView
                .findViewById(R.id.imgPlace);
        TextView title = convertView.findViewById(R.id.textPlaceTitle);
        TextView placeYear = convertView.findViewById(R.id.textPlaceYear);
        TextView ticket = convertView.findViewById(R.id.textPlaceTicket);
        TextView placeDays = convertView.findViewById(R.id.textPlaceDays);
        TextView placeHours = convertView.findViewById(R.id.textPlaceHours);
        TextView tel = convertView.findViewById(R.id.textPlaceTel);
        TextView address = convertView.findViewById(R.id.textPlaceAddress);
        TextView memo = convertView.findViewById(R.id.textPlaceMemo);
//        ImageView thumbNail = (ImageView) convertView.findViewById(R.id.thumbnail);


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
        Glide.with(activity).load(m.getPic())
                .thumbnail(0.5f)
                .crossFade()
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .placeholder(R.drawable.nopic)
                .into(thumbNail);
        return convertView;
    }

}