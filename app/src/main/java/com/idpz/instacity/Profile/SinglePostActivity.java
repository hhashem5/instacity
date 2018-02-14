package com.idpz.instacity.Profile;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.idpz.instacity.Home.CommentActivity;
import com.idpz.instacity.R;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class SinglePostActivity extends AppCompatActivity {

    int soid;
    private Intent intent;



    String fullServer;
    CircleImageView cimgUser;
    TextView txtusername,txtLikes,txtTime,txtComment;
    ImageView sImgPost;
    ImageView imgLike,imgComment;
    ImageLoader imageLoader;

    String lk,usrname,comment,likes,detail,imgurl,mob,ans;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_single_post);

        cimgUser=(CircleImageView)findViewById(R.id.imgPostUserImage);
        txtusername=(TextView)findViewById(R.id.txtPostUser);
        sImgPost=(ImageView)findViewById(R.id.imgPostImage);
        imgLike=(ImageView) findViewById(R.id.imgPostLike);
        imgComment=(ImageView) findViewById(R.id.imgPostComment);
        txtLikes=(TextView)findViewById(R.id.txtPostView);
        txtTime=(TextView)findViewById(R.id.txtPostdetail);
        txtComment=(TextView)findViewById(R.id.txtPostComment);
         TextView editPostComment=(TextView)findViewById(R.id.edtPostComment);

        imageLoader = ImageLoader.getInstance(); // Get singleton instance

        intent = getIntent();

            soid = intent.getIntExtra("id", 0);
            usrname= intent.getStringExtra("usrname");
            lk=intent.getStringExtra("lk");
            comment=intent.getStringExtra("comment");
            likes=intent.getStringExtra("likes");
            detail=intent.getStringExtra("detail");
            imgurl=intent.getStringExtra("url");
            mob=intent.getStringExtra("mob");
        ans=intent.getStringExtra("ans");

        editPostComment.setText("پاسخ:"+ans);
        if(lk.equals("1")) {
            imgLike.setImageResource(R.drawable.liked);

            imgLike.setTag("1");
        }else {
            imgLike.setImageResource(R.drawable.like);
            likes=String.valueOf(Integer.valueOf(likes)+1);
            imgLike.setTag("0");
        }

        fullServer =getString(R.string.server)+ "/i/socialpost.php";




        txtusername.setText(usrname);

        txtComment.setText(comment);
        txtLikes.setText("موافق:"+likes);

        txtTime.setText(detail);

        ImageLoader.getInstance().displayImage(imgurl, sImgPost);


        imgLike.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(lk.equals("1")) {
                    imgLike.setImageResource(R.drawable.like);
                    imgLike.setTag("0");
                    lk="0";
                    likes=String.valueOf(Integer.valueOf(likes)-1);
                    txtLikes.setText("موافق:"+ likes);
                }else {
                    imgLike.setImageResource(R.drawable.liked);
                    imgLike.setTag("1");
                    lk="1";
                    likes=String.valueOf(Integer.valueOf(likes)+1);
                    txtLikes.setText("موافق:"+ likes);

                }
                RequestQueue queue = Volley.newRequestQueue(v.getContext());
                String url = getString(R.string.server)+"/i/like.php";
                StringRequest postRequest = new StringRequest(Request.Method.POST, url,
                        new Response.Listener<String>()
                        {
                            @Override
                            public void onResponse(String response) {

                            }
                        },
                        new Response.ErrorListener()
                        {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                // TODO Auto-generated method stub
                                Log.d("ERROR","error => "+error.toString());
                            }
                        }
                ) {
                    @Override
                    protected Map<String, String> getParams() throws AuthFailureError {
                        Map<String,String>params = new HashMap<String,String>();
                        params.put("id",String.valueOf(soid) );
                        params.put("ph", mob);
                        return params;
                    }
                };
                queue.add(postRequest);


            }
        });

        imgComment.setImageResource(R.drawable.comment);
        imgComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent4 = new Intent(v.getContext() , CommentActivity.class);
                Bundle e4 = new Bundle();
                e4.putString("user", usrname); //Your id
                e4.putString("soid",String.valueOf(soid)); //Your id
                intent4.putExtras(e4); //Put your id to your next Intent
                v.getContext().startActivity(intent4);

            }
        });



    }



}