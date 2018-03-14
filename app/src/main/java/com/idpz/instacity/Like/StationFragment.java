package com.idpz.instacity.Like;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.idpz.instacity.R;
import com.idpz.instacity.utils.HorizontalPhotosAdapter;

import java.util.ArrayList;

/**
 * Created by h on 2017/12/31.
 */

public class StationFragment extends Fragment {

    private static final String TAG = "StationFragment";

    HorizontalPhotosAdapter horizontalAdapter;
    RecyclerView horizontal_recycler_view;
    ArrayList<String>bitmapList=new ArrayList<>();
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.fragment_station,container,false);

        bitmapList.add("0.jpg");
        bitmapList.add("0.jpg");
        horizontal_recycler_view=(RecyclerView)view.findViewById(R.id.horizontal_recycler_view);
        horizontalAdapter=new HorizontalPhotosAdapter(getContext(),bitmapList);
        horizontal_recycler_view.setAdapter(horizontalAdapter);
        horizontalAdapter.notifyDataSetChanged();

        LinearLayoutManager horizontalLayoutManagaer = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
        horizontal_recycler_view.setLayoutManager(horizontalLayoutManagaer);
        horizontal_recycler_view.setAdapter(horizontalAdapter);
        return view;
    }

}
