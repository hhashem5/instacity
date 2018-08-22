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
import com.idpz.instacity.models.Arts;
import com.idpz.instacity.models.VisitPlace;

import java.util.List;

public class ArtSearchAdapter extends BaseAdapter {
    private Activity activity;
    private LayoutInflater inflater;
    private List<Arts> postList;

    Context context;
    private ProgressBar mProgressBar;

    public ArtSearchAdapter(Activity activity, List<Arts> postList) {
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

        Arts m = postList.get(position);

        // thumbnail image
        Glide.with(activity).load(m.getPic())
                .thumbnail(0.5f)
                .placeholder(R.drawable.iloading)
                .into(image);

        image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(activity, SingleArtActivity.class);
                intent.putExtra("name", postList.get(position).getName());
                intent.putExtra("lat", postList.get(position).getAlat());
                intent.putExtra("lng", postList.get(position).getAlng());
                intent.putExtra("pyear", "صنایع دستی");
                intent.putExtra("ticket","قیمت: "+ postList.get(position).getPrice());
                intent.putExtra("pdays","جنس: "+ postList.get(position).getMaterial());
                intent.putExtra("phours","وزن: "+ postList.get(position).getWeight());
                intent.putExtra("tel", postList.get(position).getOwner());
                intent.putExtra("address","رنگ: "+ postList.get(position).getColor());
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