package com.idpz.instacity.Home;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.idpz.instacity.Profile.ChangeCityActivity;
import com.idpz.instacity.R;
import com.idpz.instacity.MapCity.SearchActivity;

public class SingleAdsActivity extends Activity {

    private Intent intent;

    String name,lat,lng,pyear,ticket,pdays,phours,tel,address,pic="",memo,alat,alng,aename,afname;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.single_visit_place);

        ImageView thumbNail =(ImageView) findViewById(R.id.imgPlace);
        TextView txttitle =(TextView) findViewById(R.id.textPlaceTitle);
        TextView txtplaceYear =(TextView) findViewById(R.id.textPlaceYear);
        TextView txtticket = (TextView)findViewById(R.id.textPlaceTypeRow);
        TextView txtplaceDays =(TextView) findViewById(R.id.textPlaceDays);
        TextView txtplaceHours =(TextView) findViewById(R.id.textPlaceHours);
        TextView txttel =(TextView) findViewById(R.id.textPlaceTel);
        TextView txtaddress =(TextView) findViewById(R.id.textPlaceAddress);
        TextView txtmemo =(TextView) findViewById(R.id.textPlaceMemo);
        TextView txtArea =(TextView) findViewById(R.id.txtVisitArea);

        intent = getIntent();
        name = intent.getStringExtra("name");
        lat = intent.getStringExtra("lat");
        lng = intent.getStringExtra("lng");
        pyear = intent.getStringExtra("pyear");
        ticket = intent.getStringExtra("ticket");
        pdays = intent.getStringExtra("pdays");
        phours = intent.getStringExtra("phours");
        tel = intent.getStringExtra("tel");
        address = intent.getStringExtra("address");
        pic = intent.getStringExtra("pic");
        memo = intent.getStringExtra("memo");
        alat = intent.getStringExtra("alat");
        alng = intent.getStringExtra("alng");
        aename = intent.getStringExtra("aename");
        afname = intent.getStringExtra("afname");

        Glide.with(this).load(pic)
                .thumbnail(0.5f)
                .into(thumbNail);

         txttitle.setText(name);
         txtplaceYear.setText(pyear);
         txtticket.setText(ticket);
         txtplaceDays.setText(pdays);
         txtplaceHours.setText(phours);
         txttel.setText(tel);
         txtaddress.setText(address);
         txtmemo.setText(memo);
         txtArea.setText("منطقه:"+afname);

        Button btnMap=(Button) findViewById(R.id.btnVisitMap);
        btnMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent=new Intent(SingleAdsActivity.this, SearchActivity.class);
                intent.putExtra("name", name);
                if (lat.length()<5 ){
                    lat=alat;
                    lng=alng;
                }
                intent.putExtra("lat", lat);
                intent.putExtra("lng", lng);
                intent.putExtra("memo", memo);
                intent.putExtra("aename", aename);
                startActivity(intent);
            }
        });



        Button btnTransfer=(Button) findViewById(R.id.btnVisitTransfer);
        btnTransfer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent=new Intent(SingleAdsActivity.this, ChangeCityActivity.class);
                intent.putExtra("aename", aename);
                startActivity(intent);
            }
        });
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

    }


    public void onBackPressed() {
        Log.d("CDA", "onBackPressed Called");
        finish();
    }
}