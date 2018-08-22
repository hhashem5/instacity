package com.idpz.instacity.Share;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import com.idpz.instacity.Home.HomeFragment;
import com.idpz.instacity.R;

public class PopupActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_popup);

        TextView title=(TextView)findViewById(R.id.title1);
        TextView body=(TextView)findViewById(R.id.body1);
        TextView score=(TextView)findViewById(R.id.body4);

        Intent intent = getIntent();
        if(intent.hasExtra("title")) {
            String txttitle = intent.getStringExtra("title");
            String txtbody = intent.getStringExtra("body");
            String txtscore = intent.getStringExtra("score");

            title.setText(txttitle);
            body.setText(txtbody);
            score.setText(txtscore);
        }


        Button button =(Button)findViewById(R.id.btnPopupClose);
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
