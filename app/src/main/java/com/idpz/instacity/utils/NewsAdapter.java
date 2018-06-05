package com.idpz.instacity.utils;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.idpz.instacity.R;
import com.idpz.instacity.models.Post;

import java.util.List;

public class NewsAdapter extends BaseAdapter {
    private Activity activity;
    private LayoutInflater inflater;
    private List<Post> postList;
    String lk="0",usrph="",soid="0";
    Context context;


    public NewsAdapter(Activity activity, List<Post> postList) {
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
            convertView = inflater.inflate(R.layout.content_news, null);



        ImageView thumbNail = convertView
                .findViewById(R.id.imgPostImage);
        TextView userName = convertView.findViewById(R.id.txtPostUser);
        TextView postComment = convertView.findViewById(R.id.txtPostComment);
        TextView postDetail = convertView.findViewById(R.id.txtPostdetail);
        final TextView editPostComment= convertView.findViewById(R.id.edtPostComment);
        final TextView postView = convertView.findViewById(R.id.txtPostView);
        final ImageView imglike = convertView.findViewById(R.id.imgPostLike);
        ImageView imgComment = convertView.findViewById(R.id.imgPostComment);
        ImageView imgPostSend = convertView.findViewById(R.id.imgPostSend);
        // getting Food data for the row
        Post m = postList.get(position);

        soid=String.valueOf(m.getId());
        final String usr=m.getUserName();

        // thumbnail image
        Glide.with(activity).load(m.getPostImageUrl())
                .thumbnail(0.5f)
                .crossFade()
                .placeholder(R.drawable.nopic)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(thumbNail);


        // title
        userName.setText(m.getUserName());

        // rating
        postComment.setText( m.getPostComment());
        postDetail.setText(m.getPostDetail());

        return convertView;
    }


}