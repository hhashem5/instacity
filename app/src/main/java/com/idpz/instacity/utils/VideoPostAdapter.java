package com.idpz.instacity.utils;

import android.app.Activity;
import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.VideoView;

import com.idpz.instacity.R;
import com.idpz.instacity.models.Video;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.List;

public class VideoPostAdapter extends BaseAdapter {
    private Activity activity;
    private LayoutInflater inflater;
    private List<Video> videoList;
    String lk="0",usrph="",soid="0";
    Context context;

    ImageLoader imageLoader;
//    GestureDetector detector;

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

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {


        if (inflater == null)
            inflater = (LayoutInflater) activity
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        if (convertView == null)
            convertView = inflater.inflate(R.layout.content_video, null);

        imageLoader = ImageLoader.getInstance(); // Get singleton instance

        ImageView thumbNail = (ImageView) convertView.findViewById(R.id.imgVideoLogo);
        TextView txtTitle = (TextView) convertView.findViewById(R.id.txtVideoTitle);
        final VideoView videoView = (VideoView) convertView.findViewById(R.id.vidPostVideo);
        TextView txtComment = (TextView) convertView.findViewById(R.id.txtVideoComment);
        TextView txtVideoDate = (TextView) convertView.findViewById(R.id.txtVideoDetail);

        // getting Food data for the row
        Video m = videoList.get(position);
//        detector = new GestureDetector(context, new GestureListener(convertView));
        soid=String.valueOf(m.getId());

        thumbNail.setImageResource(R.drawable.ic_video);
        // title
        txtTitle.setText(m.getTitle());

        // rating
        Uri uri= Uri.parse(m.getVideoUrl());
        videoView.setVideoURI(uri);
        videoView.start();
        videoView.pause();
//        videoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
//            @Override
//            public void onPrepared(MediaPlayer mp) {
//                mp.setLooping(true);
////                videoView.start();
////                videoView.pause();
//            }
//        });



        txtComment.setText(m.getComment());

        txtVideoDate.setText(m.getDetail());

//        videoView.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
//            @Override
//            public void onCompletion(MediaPlayer mp) {
////                mp.start();
////                mp.pause();
//            }
//        });

       videoView.setOnTouchListener(new View.OnTouchListener() {
           @Override
           public boolean onTouch(View v, MotionEvent event) {
               if (videoView.isPlaying()){
                   videoView.pause();
               }else {
                   videoView.start();
               }
               return true;
           }
       });




        return convertView;
    }



}