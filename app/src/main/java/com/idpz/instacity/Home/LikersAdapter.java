package com.idpz.instacity.Home;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.idpz.instacity.R;
import com.idpz.instacity.models.Comment;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

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


        CircleImageView thumbNail = (CircleImageView) convertView
                .findViewById(R.id.imgLikersUserImg);
        ImageLoader.getInstance().init(ImageLoaderConfiguration.createDefault(activity));
        TextView userName = (TextView) convertView.findViewById(R.id.txtCommentUserName);
        Comment m = postList.get(position);
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
        ImageLoader.getInstance().displayImage(m.getMycomment(),thumbNail,options);
        // getting Food data for the row


       //  thumbnail image
//        thumbNail.setImageURI("http://mscity.ir/assets/images/users/1.jpg", imageLoader);
//        ImageLoader imageLoader = ImageLoader.getInstance();
//        imageLoader.displayImage(m.getMycomment(),thumbNail,options);

        userName.setText(m.getUsrName());



        return convertView;
    }

}