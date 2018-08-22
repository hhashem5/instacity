package com.idpz.instacity.utils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

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
import com.idpz.instacity.Home.LikersActivity;
import com.idpz.instacity.R;
import com.idpz.instacity.models.Post;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class PostAdapter extends BaseAdapter {
    private Activity activity;
    private LayoutInflater inflater;
    private List<Post> postList;
    String lk="0",usrph="",soid="0";
    Context context;
    String mtext;
    String ph;
    String state="";

//    GestureDetector detector;

    public PostAdapter(Activity activity, List<Post> postList) {
        this.activity = activity;
        this.postList = postList;
    }

    @Override
    public int getCount() {
        return postList.size();
    }

    @Override
    public Object getItem(int location) {
        return postList.get(location);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        SharedPreferences SP1;
        SP1 = PreferenceManager.getDefaultSharedPreferences(activity);
        state = SP1.getString("state", "0");
        ph=SP1.getString("mobile", "0");
        if (inflater == null)
            inflater = (LayoutInflater) activity
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        if (convertView == null)
            convertView = inflater.inflate(R.layout.content_post, null);



        ImageView thumbNail = convertView
                .findViewById(R.id.imgPostImage);
        CircleImageView userImg= convertView.findViewById(R.id.imgPostUserImage);
        TextView userName = convertView.findViewById(R.id.txtPostUser);
        CustomTextView postComment = convertView.findViewById(R.id.txtPostComment);
        TextView postDetail = convertView.findViewById(R.id.txtPostdetail);
        final TextView editPostComment= convertView.findViewById(R.id.edtPostComment);
        final TextView postView = convertView.findViewById(R.id.txtPostView);
        final ImageView imglike = convertView.findViewById(R.id.imgPostLike);
        ImageView imgComment = convertView.findViewById(R.id.imgPostComment);
        ImageView imgPostSend = convertView.findViewById(R.id.imgPostSend);


        // getting Food data for the row
        Post m = postList.get(position);
//        detector = new GestureDetector(context, new GestureListener(convertView));
        soid=String.valueOf(m.getId());
        final String usr=m.getUserName();

        // thumbnail image
//        thumbNail.setImageUrl(m.getPostImageUrl(), imageLoader);
        Glide.with(activity).load(m.getPostImageUrl())
                .thumbnail(0.5f)

                .into(thumbNail);
        Glide.with(activity).load(m.getUserImg())
                .thumbnail(0.5f)

                .into(userImg);

        editPostComment.setText(Html.fromHtml("پاسخ:"+m.getPostAnswer()));
        // title
        userName.setText(m.getUserName());

        // rating
        postComment.setText( m.getPostComment());
        postView.setText("افراد موافق : "+ m.getPostLike());
        postView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(v.getContext(),LikersActivity.class);
                Bundle e4 = new Bundle();
                e4.putString("soid",String.valueOf(postList.get(position).getId())); //Your id
                intent.putExtras(e4); //Put your id to your next Intent
                v.getContext().startActivity(intent);
            }
        });

        if(m.getPostLK().equals("1")) {
            imglike.setImageResource(R.drawable.liked);
            imglike.setTag("1");
        }else {
            imglike.setImageResource(R.drawable.likei);
            imglike.setTag("0");
        }

        imglike.setTag(position);
        imglike.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                usrph=postList.get(position).getUserPhone();
                soid=String.valueOf(postList.get(position).getId());
                String lik=postList.get(position).getPostLK();
                if(lik.equals("1")) {
                    imglike.setImageResource(R.drawable.likei);
                    imglike.setTag("0");
                    lk="0";
                    postList.get(position).setPostLK("0");
                    int a=Integer.valueOf(postList.get(position).getPostLike())-1;
                    postList.get(position).setPostLike(String.valueOf(a));
                    postView.setText("افراد موافق:"+ String.valueOf(a));

                }else {
                    imglike.setImageResource(R.drawable.liked);
                    imglike.setTag("1");
                    lk="1";
                    postList.get(position).setPostLK("1");
                    int a=Integer.valueOf(postList.get(position).getPostLike())+1;
                    postList.get(position).setPostLike(String.valueOf(a));
                    postView.setText("افراد موافق:"+ String.valueOf(a));

                }
                RequestQueue queue = Volley.newRequestQueue(v.getContext());
                String url = "http://idpz.ir/j/like.php";
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
//                                Log.d("ERROR","error => "+error.toString());
                            }
                        }
                ) {
                    @Override
                    protected Map<String, String> getParams() throws AuthFailureError {
                        Map<String,String>params = new HashMap<String,String>();
                        params.put("state", state);
                        params.put("id",soid );
                        params.put("ph", ph);
                        params.put("lk", lk);
                        return params;
                    }
                };
                queue.add(postRequest);


            }
        });

        imgComment.setImageResource(R.drawable.commenti);
        imgComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent4 = new Intent(v.getContext() , CommentActivity.class);
                Bundle e4 = new Bundle();
                e4.putString("soid",String.valueOf(postList.get(position).getId())); //Your id
                intent4.putExtras(e4); //Put your id to your next Intent
                v.getContext().startActivity(intent4);


            }
        });



        postDetail.setText(m.getPostDetail());



        return convertView;
    }

    public void reqlike() {

    }

//    public class GestureListener extends GestureDetector.SimpleOnGestureListener{
//
//        View mHolder;
//        public GestureListener(View holder) {
//            mHolder = holder;
//        }
//
//        @Override
//        public boolean onDown(MotionEvent e) {
//            return true;
//        }
//
//        @Override
//        public boolean onDoubleTap(MotionEvent e) {
//            Toast.makeText(activity, "لایک شد", Toast.LENGTH_SHORT).show();
//            return true;
//        }
//    }


}