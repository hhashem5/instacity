package com.idpz.instacity.utils;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.idpz.instacity.R;
import com.idpz.instacity.models.GiftPlace;

import java.util.List;

public class GiftPlacesAdapter extends BaseAdapter {
    private Activity activity;
    private LayoutInflater inflater;
    private List<GiftPlace> giftList;
    ImageLoader imageLoader = AppController.getInstance().getImageLoader();

    public GiftPlacesAdapter(Activity activity, List<GiftPlace> govList) {
        this.activity = activity;
        this.giftList = govList;
    }

    @Override
    public int getCount() {
        return giftList.size();
    }

    @Override
    public Object getItem(int location) {
        return giftList.get(location);
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
            convertView = inflater.inflate(R.layout.gift_place_row, null);

        if (imageLoader == null)
            imageLoader = AppController.getInstance().getImageLoader();
        NetworkImageView thumbNail = (NetworkImageView) convertView
                .findViewById(R.id.imgGiftPLace);
        TextView name = (TextView) convertView.findViewById(R.id.txtGiftPlaceName);
        TextView txtDiscount = (TextView) convertView.findViewById(R.id.txtGiftDiscount);
//        ImageView thumbNail = (ImageView) convertView.findViewById(R.id.thumbnail);

        // getting Food data for the row
        GiftPlace m = giftList.get(position);

        // thumbnail image
        thumbNail.setImageUrl(m.getPic(), imageLoader);

        // title
        name.setText(m.getName());

        // rating
        txtDiscount.setText( m.getDiscount());



        return convertView;
    }

}