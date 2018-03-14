package com.idpz.instacity.Like;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;

import com.idpz.instacity.R;
import com.idpz.instacity.models.Ads;
import com.idpz.instacity.utils.AdsAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by h on 2017/12/31.
 */

public class JobFragment extends Fragment{
    private SwipeRefreshLayout swipeRefreshLayout;
    private static final String TAG = "jobFragment";

    List<Ads> allAds;
    ArrayList<Ads> dataModels;
    ListView listView;
    Button btnAdsReg;
    AdsAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.fragment_job,container,false);
        listView=(ListView)view.findViewById(R.id.lvJobContent);
        btnAdsReg=(Button)view.findViewById(R.id.btnJobReg);

        dataModels= new ArrayList<>();
        dataModels.add(new Ads(1,"دوخت لباس محلی","سفارشات انواع لباس های آذری ، نیشابوری و دوخت از روی مدل های آماده با پارچه های با کیفیت ارسال به تمام نقاط ایران پذیرفته میشود","تلفن:25154879","خیابان امام جنب آموزشگاه هنر",""));
//        for (Ads job:allAds){
//            dataModels.add( job);
//        }
        adapter= new AdsAdapter(getActivity(),dataModels);

        listView.setAdapter(adapter);

        return view;
    }


    //`id`, `owner`, `code`, `name`, `type`, `weight`, `material`, `color`, `price`, `memo`, `pic`, `pub`, `stamp
}
