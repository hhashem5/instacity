package com.idpz.instacity.utils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ProgressBar;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.idpz.instacity.Profile.SinglePostActivity;
import com.idpz.instacity.R;
import com.idpz.instacity.models.Post;

import java.util.List;

public class galleryAdapter extends BaseAdapter {
    private Activity activity;
    private LayoutInflater inflater;
    private List<Post> postList;
    String lk="0",usrph="",soid="0";
    Context context;
    private ProgressBar mProgressBar;

    public galleryAdapter(Activity activity, List<Post> postList) {
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


        if (inflater == null)
            inflater = (LayoutInflater) activity
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        if (convertView == null)
            convertView = inflater.inflate(R.layout.grid_image_text, null);

        SquareImageView image= convertView.findViewById(R.id.gridImageView);
//        TextView userName = (TextView) convertView.findViewById(R.id.textGridImage);

//        mProgressBar.setVisibility(View.GONE);
        // getting Food data for the row
        Post m = postList.get(position);

//        soid=String.valueOf(m.getId());


        // thumbnail image
        Glide.with(activity).load(m.getPostImageUrl())
                .thumbnail(0.5f)
                .placeholder(R.drawable.iloading)
                .into(image);

        image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(activity, SinglePostActivity.class);
                intent.putExtra("usrname", postList.get(position).getUserName());
                intent.putExtra("lk", postList.get(position).getPostLK());
                intent.putExtra("mob", postList.get(position).getUserPhone());
                intent.putExtra("ans", postList.get(position).getPostAnswer());
                intent.putExtra("comment", postList.get(position).getPostComment());
                intent.putExtra("likes", postList.get(position).getPostLike());
                intent.putExtra("detail", postList.get(position).getPostDetail());
                intent.putExtra("url", postList.get(position).getPostImageUrl());
                intent.putExtra("id", postList.get(position).getId());
                activity.startActivity(intent);
            }
        });


        // title
//        userName.setText(m.getPostComment());




        return convertView;
    }


}