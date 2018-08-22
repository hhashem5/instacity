package com.idpz.instacity.Home;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.MediaController;
import android.widget.RelativeLayout;
import android.widget.VideoView;

import com.idpz.instacity.R;

/**
 * Created by h on 08/20/2018.
 */

public class SingleVideoActivity extends AppCompatActivity {

    String title,pic,url,comment,detail;
    VideoView videoView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.content_video);

        LinearLayout lin1=findViewById(R.id.linTop);
        LinearLayout lin2=findViewById(R.id.relButtom);
        lin1.setVisibility(View.GONE);
        lin2.setVisibility(View.GONE);
        Intent intentex = getIntent();
        if(intentex.hasExtra("title")) {
             title = intentex.getStringExtra("title");
             pic = intentex.getStringExtra("pic");
             url = intentex.getStringExtra("url");
             comment = intentex.getStringExtra("comment");
             detail = intentex.getStringExtra("detail");
        }
        ImageView iv=findViewById(R.id.imgPlayVideo);
        iv.setVisibility(View.GONE);
            videoView = findViewById(R.id.vidPostVideo);
        videoView.setVisibility(View.VISIBLE);
        MediaController mediaController = new
                MediaController(this);
        mediaController.setAnchorView(videoView);
        videoView.setMediaController(mediaController);
        Uri uri = Uri.parse( url);
        videoView.setVideoURI(uri);
        videoView.start();


        DisplayMetrics dm =new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);

        int width=dm.widthPixels;
        int height=dm.heightPixels;
        getWindow().setLayout((int)(width),(int)(height*.5));

        WindowManager.LayoutParams params=getWindow().getAttributes();
        params.gravity= Gravity.CENTER;
        params.x=0;
        params.y=-20;
        getWindow().setAttributes(params);
    }
}
