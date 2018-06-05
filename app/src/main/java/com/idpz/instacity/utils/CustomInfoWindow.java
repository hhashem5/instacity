package com.idpz.instacity.utils;

/**
 * Created by h on 2018/02/06.
 */

import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.widget.TextView;

import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Marker;
import com.idpz.instacity.R;
import com.idpz.instacity.models.Shop;


public class CustomInfoWindow implements GoogleMap.InfoWindowAdapter {

    private Context context;
    ImageLoader imageLoader;
    public CustomInfoWindow(Context ctx){
        context = ctx;
    }

    @Override
    public View getInfoWindow(Marker marker) {
        return null;
    }

    @Override
    public View getInfoContents(Marker marker) {
        View view = ((Activity)context).getLayoutInflater()
                .inflate(R.layout.custom_info_marker, null);
        if (imageLoader == null)
            imageLoader = AppController.getInstance().getImageLoader();
        NetworkImageView img = view.findViewById(R.id.imgPLaceMaker);
        TextView txtName = view.findViewById(R.id.txtMarkerPlaceName);
        TextView txtOwner = view.findViewById(R.id.txtMarkerPlaceOwner);
        TextView txtKey = view.findViewById(R.id.txtMarkerPlaceKey);
        TextView txtTel = view.findViewById(R.id.txtMarkerPlacetel);
        TextView txtAddress = view.findViewById(R.id.txtMarkerPlaceAddress);

        Shop m = (Shop) marker.getTag();
        // thumbnail image

//        img.setImageUrl(R.string.server+"/img/places/0.jpg", imageLoader);

// title
        txtName.setText(marker.getTitle());

// rating
        txtOwner.setText( m.getOwner());
//        txtKey.setText(m.getJkey());
//        txtTel.setText(m.getMobile()+" "+m.getTel());
        txtAddress.setText(marker.getSnippet());


        return view;
    }
}
