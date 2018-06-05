package com.idpz.instacity.utils;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.idpz.instacity.R;
import com.idpz.instacity.models.MyWeather;

import java.util.List;

public class WeatherAdapter extends BaseAdapter {
    private Activity activity;
    private LayoutInflater inflater;
    private List<MyWeather> weatherList;

    Context context;


//    GestureDetector detector;

    public WeatherAdapter(Activity activity, List<MyWeather> weatherList) {
        this.activity = activity;
        this.weatherList = weatherList;
    }

    @Override
    public int getCount() {
        return weatherList.size();
    }

    @Override
    public Object getItem(int location) {
        return weatherList.get(location);
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
            convertView = inflater.inflate(R.layout.row_weather, null);


        TextView txtMyday = convertView.findViewById(R.id.txtMyDay);
//        TextView txtMySummary = (TextView) convertView.findViewById(R.id.txtMySum);
        TextView txtMyWind = convertView.findViewById(R.id.txtMyWind);
        TextView txtTempHi= convertView.findViewById(R.id.txtTempHi);
        TextView txtTempLo= convertView.findViewById(R.id.txtTempLo);
        ImageView imgSun = convertView.findViewById(R.id.imgMyWeather);


        // getting Food data for the row
        MyWeather m = weatherList.get(position);


        // weather image
        imgSun.setImageDrawable(activity.getBaseContext().getResources().getDrawable(Integer.valueOf(m.getIcon())));

        txtTempHi.setText("↑ "+m.getMaxTemp()+"°");
        txtTempLo.setText("↓ "+m.getMintemp()+"°");
        // name of day
        txtMyday.setText(m.getDay());

        // weather condition
//        txtMySummary.setText( m.getSummary());

        //wind speed
        txtMyWind.setText(m.getWind());

        return convertView;
    }


}