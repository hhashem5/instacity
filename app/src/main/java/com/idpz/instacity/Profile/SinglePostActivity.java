package com.idpz.instacity.Profile;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.idpz.instacity.Home.CommentActivity;
import com.idpz.instacity.R;

import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class SinglePostActivity extends AppCompatActivity {

    int soid;
    private Intent intent;



    String fullServer,urlEditPost,mytext;
    CircleImageView cimgUser;
    TextView txtusername,txtLikes,txtTime;
    ImageView sImgPost,imgDelPost;
    ImageView imgLike,imgComment,imgPostSend;

    EditText txtComment;
    String lk,usrname,comment,likes,detail,imgurl,mob,ans,server,delSoUrl,profileImgUrl="";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_single_post);
        SharedPreferences SP1;
        SP1 = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
        server=SP1.getString("server", "0");
        profileImgUrl = SP1.getString("pic", "0");
        urlEditPost=server+"/i/socialedit.php";

        cimgUser= findViewById(R.id.imgPostUserImage);
        txtusername= findViewById(R.id.txtPostUser);
        sImgPost= findViewById(R.id.imgPostImage);
        imgLike= findViewById(R.id.imgPostLike);
        imgDelPost= findViewById(R.id.imgDelSinglePost);
        imgComment= findViewById(R.id.imgPostComment);
        imgPostSend= findViewById(R.id.imgPostSend);
        txtLikes= findViewById(R.id.txtPostView);
        txtTime= findViewById(R.id.txtPostdetail);
        txtComment= findViewById(R.id.txtPostComment);
         TextView editPostComment= findViewById(R.id.edtPostComment);



        imgDelPost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder;
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    builder = new AlertDialog.Builder(SinglePostActivity.this, android.R.style.Theme_Material_Dialog_Alert);
                } else {
                    builder = new AlertDialog.Builder(SinglePostActivity.this);
                }
                builder.setTitle("حذف پست")
                        .setMessage("آیا از حذف مطلب اطمینان دارید?")
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                // continue with delete
                                delSocial();
                                finish();
                            }
                        })
                        .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                // do nothing
                            }
                        })
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .show();

            }
        });

        imgPostSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mytext=txtComment.getText().toString();
                regSocial();
            }
        });

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

        fullServer =server+ "/i/socialpost.php";
        delSoUrl =server+ "/i/socialdel.php";

        Glide.with(this).load(getString(R.string.server)+"/assets/images/users/"+profileImgUrl)
                .thumbnail(0.5f)
                .crossFade()
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .placeholder(R.drawable.nopic)
                .into(cimgUser);

        txtusername.setText(usrname);

        txtComment.setText(comment);
        txtLikes.setText("موافق:"+likes);

        txtTime.setText(detail);

        Glide.with(this).load(imgurl)
                .thumbnail(0.5f)
                .crossFade()
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .placeholder(R.drawable.nopic)
                .into(sImgPost);

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
                String url = server+"/i/like.php";
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

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

    }

    public void regSocial() {
//        Toast.makeText(MainActivity.this, " reqUser", Toast.LENGTH_SHORT).show();
        RequestQueue queue = Volley.newRequestQueue(this);
        String url = urlEditPost;
        StringRequest postRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>()
                {
                    @Override
                    public void onResponse(String response) {
                        Toast.makeText(SinglePostActivity.this, "مطلب ویرایش شد.", Toast.LENGTH_SHORT).show();
                    }
                },
                new Response.ErrorListener()
                {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // TODO Auto-generated method stub

                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String>params = new HashMap<String,String>();
                params.put("id", String.valueOf(soid));
                params.put("txt", mytext);
                return params;
            }
        };
        queue.add(postRequest);

    }

    public void delSocial() {
//        Toast.makeText(MainActivity.this, " reqUser", Toast.LENGTH_SHORT).show();
        RequestQueue queue = Volley.newRequestQueue(this);
        String url = delSoUrl;
        StringRequest postRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>()
                {
                    @Override
                    public void onResponse(String response) {
//                        tvMessage.setText("پیام با موفقیت ارسال شد");
                    }
                },
                new Response.ErrorListener()
                {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // TODO Auto-generated method stub

                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String>params = new HashMap<String,String>();
                params.put("id", String.valueOf(soid));
                return params;
            }
        };
        queue.add(postRequest);

    }
}