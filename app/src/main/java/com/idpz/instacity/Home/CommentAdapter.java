package com.idpz.instacity.Home;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.android.volley.toolbox.NetworkImageView;
import com.idpz.instacity.R;
import com.idpz.instacity.models.Comment;

import java.util.List;

public class CommentAdapter extends BaseAdapter {
    private Activity activity;
    private LayoutInflater inflater;
    private List<Comment> postList;
    Context context;


    public CommentAdapter(Activity activity, List<Comment> postList) {
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
            convertView = inflater.inflate(R.layout.content_comment, null);


        NetworkImageView thumbNail = (NetworkImageView) convertView
                .findViewById(R.id.imgCommentUserImg);
        TextView userName = (TextView) convertView.findViewById(R.id.txtCommentUserName);
        TextView postComment = (TextView) convertView.findViewById(R.id.txtCommentText);
        TextView commentDate = (TextView) convertView.findViewById(R.id.txtCommentDate);

        // getting Food data for the row
        Comment m = postList.get(position);

        // thumbnail image
//        thumbNail.setImageUrl("http://mscity.ir/assets/images/users/1.jpg", imageLoader);

        // title
        userName.setText(m.getUsrName());
        commentDate.setText(m.getCmtime());

        // rating
        postComment.setText( m.getMycomment());

        return convertView;
    }

}