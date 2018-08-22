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
import com.idpz.instacity.Home.SingleArtActivity;
import com.idpz.instacity.Home.SingleVisitActivity;
import com.idpz.instacity.R;
import com.idpz.instacity.models.Ads;


import java.util.List;

public class AdsSearchAdapter extends BaseAdapter {
    private Activity activity;
    private LayoutInflater inflater;
    private List<Ads> postList;

    Context context;
    private ProgressBar mProgressBar;

    public AdsSearchAdapter(Activity activity, List<Ads> postList) {
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

        Ads m = postList.get(position);

        // thumbnail image
        Glide.with(activity).load(m.getPic())
                .thumbnail(0.5f)
                .placeholder(R.drawable.iloading)
                .into(image);

        image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(activity, SingleVisitActivity.class);
                intent.putExtra("name", postList.get(position).getTitle());
                intent.putExtra("lat", postList.get(position).getAlat());
                intent.putExtra("lng", postList.get(position).getAlng());
                intent.putExtra("pyear", "نیازمندی");
                intent.putExtra("ticket","-");
                intent.putExtra("pdays","-");
                intent.putExtra("phours","-");
                intent.putExtra("tel", postList.get(position).getTel());
                intent.putExtra("address", postList.get(position).getAddress());
                intent.putExtra("pic", postList.get(position).getPic());
                intent.putExtra("memo", postList.get(position).getMemo());
                intent.putExtra("alat", postList.get(position).getAlat());
                intent.putExtra("alng", postList.get(position).getAlng());
                intent.putExtra("aename", postList.get(position).getAename());
                intent.putExtra("afname", postList.get(position).getAfname());
                activity.startActivity(intent);
            }
        });


        return convertView;
    }


}