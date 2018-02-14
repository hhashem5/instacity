package com.idpz.instacity.utils;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.idpz.instacity.R;
import com.idpz.instacity.models.Post;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.List;

public class NewsAdapter extends BaseAdapter {
    private Activity activity;
    private LayoutInflater inflater;
    private List<Post> postList;
    String lk="0",usrph="",soid="0";
    Context context;

    ImageLoader imageLoader;

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

        imageLoader = ImageLoader.getInstance(); // Get singleton instance

        ImageView thumbNail = (ImageView) convertView
                .findViewById(R.id.imgPostImage);
        TextView userName = (TextView) convertView.findViewById(R.id.txtPostUser);
        TextView postComment = (TextView) convertView.findViewById(R.id.txtPostComment);
        TextView postDetail = (TextView) convertView.findViewById(R.id.txtPostdetail);
        final TextView editPostComment=(TextView) convertView.findViewById(R.id.edtPostComment);
        final TextView postView = (TextView) convertView.findViewById(R.id.txtPostView);
        final ImageView imglike = (ImageView) convertView.findViewById(R.id.imgPostLike);
        ImageView imgComment = (ImageView) convertView.findViewById(R.id.imgPostComment);
        ImageView imgPostSend = (ImageView) convertView.findViewById(R.id.imgPostSend);
        // getting Food data for the row
        Post m = postList.get(position);

        soid=String.valueOf(m.getId());
        final String usr=m.getUserName();
        DisplayImageOptions options;
        options = new DisplayImageOptions.Builder()
                .showImageOnLoading(R.drawable.blur)
                .showImageForEmptyUri(R.drawable.blur)
                .showImageOnFail(R.drawable.blur)
                .cacheInMemory(true)
                .cacheOnDisk(true)
                .considerExifParams(true)
                .bitmapConfig(Bitmap.Config.RGB_565)
                .build();
        // thumbnail image
//        thumbNail.setImageUrl(m.getPostImageUrl(), imageLoader);
        imageLoader.displayImage(m.getPostImageUrl(), thumbNail,options);




        // title
        userName.setText(m.getUserName());

        // rating
        postComment.setText( m.getPostComment());
        postDetail.setText(m.getPostDetail());

        return convertView;
    }


}