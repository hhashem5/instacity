package com.idpz.instacity.Home;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import com.idpz.instacity.R;
import com.idpz.instacity.utils.CustomTextView;

public class EventActivity extends Activity {


    CustomTextView tvName;
    CustomTextView tvOwner;
    CustomTextView tvContact;
    CustomTextView tvPlace;
    CustomTextView tvDate;
    CustomTextView tvTime;
    CustomTextView tvInfo;
    CustomTextView tvMemo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event);

         tvName=(CustomTextView)findViewById(R.id.txtEventName);
         tvOwner=(CustomTextView)findViewById(R.id.txtEventOwner);
         tvContact=(CustomTextView)findViewById(R.id.txtEventContact);
         tvPlace=(CustomTextView)findViewById(R.id.txtEventPlace);
         tvDate=(CustomTextView)findViewById(R.id.txtEventDate);
         tvTime=(CustomTextView)findViewById(R.id.txtEventTime);
         tvInfo=(CustomTextView)findViewById(R.id.txtEventInfo);
         tvMemo=(CustomTextView)findViewById(R.id.txtEventMemo);



        Intent intent = getIntent();
        if(intent.hasExtra("name")) {
            String  name= intent.getStringExtra("name");
            String  owner= intent.getStringExtra("owner");
            String  contact= intent.getStringExtra("contact");
            String  place= intent.getStringExtra("place");
            String  edate= intent.getStringExtra("edate");
            String  etime= intent.getStringExtra("etime");
            String  info= intent.getStringExtra("info");
            String  memo= intent.getStringExtra("memo");

            tvName.setText(name);
            tvOwner.setText(owner);
            tvContact.setText(contact);
            tvPlace.setText(place);
            tvDate.setText(edate);
            tvTime.setText(etime);
            tvInfo.setText(info);
            tvMemo.setText(memo);

        }

        tvContact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Uri number = Uri.parse("tel:"+tvContact.getText().toString());
                Intent callIntent = new Intent(Intent.ACTION_DIAL, number);
                startActivity(callIntent);
            }
        });

        Button button =(Button)findViewById(R.id.btnEventOk);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        DisplayMetrics dm =new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);

        int width=dm.widthPixels;
        int height=dm.heightPixels;
        getWindow().setLayout((int)(width*.8),(int)(height*.5));

        WindowManager.LayoutParams params=getWindow().getAttributes();
        params.gravity= Gravity.CENTER;
        params.x=0;
        params.y=-20;
        getWindow().setAttributes(params);

    }
}
