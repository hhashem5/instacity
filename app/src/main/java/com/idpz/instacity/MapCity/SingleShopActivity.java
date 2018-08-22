package com.idpz.instacity.MapCity;

import android.content.Intent;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.idpz.instacity.Area;
import com.idpz.instacity.Profile.ChangeCityActivity;
import com.idpz.instacity.R;

public class SingleShopActivity extends AppCompatActivity implements OnMapReadyCallback {

    boolean mapFlag=false;
    private GoogleMap mMap;
    String name,ownerName,tel,mobile,pic,address,lat,lng,tag,jkey,memo;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_single_shop);



        TextView textPlaceNameRow=findViewById(R.id.textPlaceNameRow);
        TextView textPlaceOwnerRow=findViewById(R.id.textPlaceOwnerRow);
        ImageView imgPlaceRow=findViewById(R.id.imgPlaceRow);
        TextView textPlaceTag=findViewById(R.id.textPlaceTag);
        TextView textPlaceMobile=findViewById(R.id.textPlaceMobile);
        TextView textPlaceKeys=findViewById(R.id.textPlaceKeys);
        TextView textPlaceTel=findViewById(R.id.textPlaceTel);
        TextView textPlaceAddress=findViewById(R.id.textPlaceAddress);
        TextView textPlaceMemo=findViewById(R.id.textPlaceMemo);

        Intent intentex = getIntent();
        if(intentex.hasExtra("name")) {
             name = intentex.getStringExtra("name");
             ownerName = intentex.getStringExtra("ownerName");
             tel = intentex.getStringExtra("tel");
             mobile = intentex.getStringExtra("mobile");
             pic = intentex.getStringExtra("pic");
             address = intentex.getStringExtra("address");
             lat = intentex.getStringExtra("jlat");
             lng = intentex.getStringExtra("jlng");
             tag = intentex.getStringExtra("tag");
             jkey = intentex.getStringExtra("jkey");
             memo = intentex.getStringExtra("memo");

             textPlaceNameRow.setText(name);
             textPlaceOwnerRow.setText(ownerName);
             textPlaceTag.setText(tag);
             textPlaceTel.setText(tel);
             textPlaceMobile.setText(mobile);
             textPlaceKeys.setText(jkey);
             textPlaceAddress.setText(address);
             textPlaceMemo.setText(memo);
            Glide.with(this).load(pic)
                    .thumbnail(0.5f)
                    .into(imgPlaceRow);
        }

        try {
            // Loading map
            initilizeMap();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    @Override
    public void onMapReady(GoogleMap googleMap) {

        mMap = googleMap;
        if (mMap == null) {
            SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                    .findFragmentById(R.id.map);
            mapFragment.getMapAsync(SingleShopActivity.this);}
        // Add a marker in Sydney, Australia, and move the camera.
        LatLng cityLatLng = new LatLng(Double.valueOf(lat), Double.valueOf(lng));

        mMap.addMarker(new MarkerOptions().position(cityLatLng).title(name+"("+ownerName+")").snippet("تلفن:"+tel+" موبایل:"+mobile));
//        CircleOptions circleOptions = new CircleOptions().center(cityLatLng)
//                .radius(500)
//                .strokeColor(Color.BLUE)
//                .fillColor(0x30ff0000)
//                .strokeWidth(2);
//
//        mMap.addCircle(circleOptions);
//        mMap.addCircle(circleOptions);
//        mMap.setTrafficEnabled(true);

        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(cityLatLng, 15));
        mapFlag = true;
        Toast.makeText(SingleShopActivity.this, "نقشه آماده شد", Toast.LENGTH_SHORT).show();
    }

    private void initilizeMap() {

        if (mMap == null) {
            SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                    .findFragmentById(R.id.map);
            mapFragment.getMapAsync(SingleShopActivity.this);
            // check if map is created successfully or not



//            LatLngBounds.Builder builder = new LatLngBounds.Builder();
//            for (Area area:areaArrayList){
//                LatLng node = new LatLng(area.getAlat(),area.getAlng());
//                MarkerOptions markerOptions = new MarkerOptions();
//                markerOptions.position(node).title(area.getAfname());
//                mMap.addMarker(markerOptions);
//                builder.include(node);
//            }
//            LatLngBounds bounds = builder.build();
//            int padding = 0; // offset from edges of the map in pixels
//            CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, padding);
//            mMap.animateCamera(cu);
            LatLng loc=new LatLng(Double.valueOf(lat),Double.valueOf(lng));
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(loc, 17));
            mMap.addMarker(new MarkerOptions().position(loc).title(name+"("+ownerName+")").snippet("تلفن:"+tel+" موبایل:"+mobile));

        }
    }
}
