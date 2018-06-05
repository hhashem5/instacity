package com.idpz.instacity.utils;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.idpz.instacity.R;

import java.util.ArrayList;

/**
 * Created by h on 2018/03/12.
 */
public class HorizontalPhotosAdapter extends RecyclerView.Adapter<HorizontalPhotosAdapter.MyViewHolder> {

    private Context context;
    private LayoutInflater inflater;
    private ArrayList<String> bitmapList;

    public class MyViewHolder extends RecyclerView.ViewHolder {

        private ImageView riv;

        public MyViewHolder(View view) {
            super(view);

            riv = view.findViewById(R.id.horizontal_item_view_image);

        }
    }


    public HorizontalPhotosAdapter(Context context, ArrayList<String> bitmapList) {
        this.context = context;
        this.bitmapList = bitmapList;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_pic_text, parent, false);

        if (itemView.getLayoutParams ().width == RecyclerView.LayoutParams.MATCH_PARENT)
            itemView.getLayoutParams ().width = RecyclerView.LayoutParams.WRAP_CONTENT;

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {

        Glide.with(context).load(bitmapList.get(position))
                .thumbnail(0.5f)
                .crossFade()
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .placeholder(R.drawable.nopic)
                .into(holder.riv);
    }


    @Override
    public int getItemCount() {
        return bitmapList.size();
    }
}