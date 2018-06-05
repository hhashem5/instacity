package com.idpz.instacity.utils;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ProgressBar;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.idpz.instacity.R;

import java.util.ArrayList;

/**
 * Created by User on 6/4/2017.
 */

public class GridImageAdapter extends ArrayAdapter<String>{

    private Context mContext;
    private LayoutInflater mInflater;
    private int layoutResource;
    private String mAppend;
    private ArrayList<String> imgURLs;

    public GridImageAdapter(Context context, int layoutResource, String append, ArrayList<String> imgURLs) {
        super(context, layoutResource, imgURLs);
        mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mContext = context;
        this.layoutResource = layoutResource;
        mAppend = append;
        this.imgURLs = imgURLs;
    }

    private static class ViewHolder{
        SquareImageView image;
        ProgressBar mProgressBar;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        /*
        Viewholder build pattern (Similar to recyclerview)
         */
        final ViewHolder holder;
        if(convertView == null){
            convertView = mInflater.inflate(layoutResource, parent, false);
            holder = new ViewHolder();
//            holder.mProgressBar = (ProgressBar) convertView.findViewById(R.id.gridImageProgressbar);
            holder.image = convertView.findViewById(R.id.gridImageView);

            convertView.setTag(holder);
        }
        else{
            holder = (ViewHolder) convertView.getTag();
        }

        String imgURL = getItem(position);

        Glide.with(getContext()).load(imgURL)
                .thumbnail(0.5f)
                .crossFade()
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .placeholder(R.drawable.nopic)
                .into(holder.image);

        return convertView;
    }
}



















