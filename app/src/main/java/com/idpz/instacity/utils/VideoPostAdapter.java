package com.idpz.instacity.utils;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.net.Uri;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.VideoView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.idpz.instacity.R;
import com.idpz.instacity.models.Video;

import java.util.List;

public class VideoPostAdapter extends BaseAdapter {
    private Activity activity;
    private LayoutInflater inflater;
    private List<Video> videoList;
    String lk="0",usrph="",soid="0";
    Context context;
    private VideoView videoView;


    public VideoPostAdapter(Activity activity, List<Video> videoList) {
        this.activity = activity;
        this.videoList = videoList;
    }

    @Override
    public int getCount() {
        return videoList.size();
    }

    @Override
    public Object getItem(int location) {
        return videoList.get(location);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {


        if (inflater == null)
            inflater = (LayoutInflater) activity
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        if (convertView == null)
            convertView = inflater.inflate(R.layout.content_video, null);

        ImageView imgPlay = convertView.findViewById(R.id.imgPlayVideo);
        ImageView thumbNail = convertView.findViewById(R.id.imgVideoLogo);
        TextView txtTitle = convertView.findViewById(R.id.txtVideoTitle);
        TextView txtComment = convertView.findViewById(R.id.txtVideoComment);
        TextView txtVideoDate = convertView.findViewById(R.id.txtVideoDetail);

        // getting Food data for the row
        Video m = videoList.get(position);
//        detector = new GestureDetector(context, new GestureListener(convertView));
        soid = String.valueOf(m.getId());
        Glide.with(activity).load(m.getPic())
                .thumbnail(0.5f)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .placeholder(R.drawable.ic_empty)
                .into(imgPlay);
        thumbNail.setImageResource(R.drawable.ic_video);
        // title
        txtTitle.setText(m.getTitle());

        txtComment.setText(m.getComment());

        txtVideoDate.setText(m.getDetail());

        return convertView;
    }



}