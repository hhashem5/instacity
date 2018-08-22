package com.idpz.instacity.Home;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.idpz.instacity.R;
import com.idpz.instacity.models.Comment;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class LikersAdapter extends BaseAdapter {
    private Activity activity;
    private LayoutInflater inflater;
    private List<Comment> postList;
    Context context;


    public LikersAdapter(Activity activity, List<Comment> postList) {
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
    public View getView(int position, View convertView, ViewGroup parent) {

        if (inflater == null)
            inflater = (LayoutInflater) activity
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        if (convertView == null)
            convertView = inflater.inflate(R.layout.content_likers, null);


        CircleImageView thumbNail = convertView
                .findViewById(R.id.imgLikersUserImg);

        TextView userName = convertView.findViewById(R.id.txtCommentUserName);
        Comment m = postList.get(position);
        Glide.with(activity).load(m.getMycomment())
                .thumbnail(0.5f)

                .into(thumbNail);


        // getting Food data for the row


       //  thumbnail image
//        thumbNail.setImageURI("http://mscity.ir/assets/images/users/1.jpg", imageLoader);


        userName.setText(m.getUsrName());



        return convertView;
    }

}