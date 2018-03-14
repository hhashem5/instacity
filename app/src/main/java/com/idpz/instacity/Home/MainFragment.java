package com.idpz.instacity.Home;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.maps.MapFragment;
import com.idpz.instacity.Like.TourismActivity;
import com.idpz.instacity.R;
import com.idpz.instacity.Search.SearchActivity;
import com.idpz.instacity.models.Video;
import com.idpz.instacity.utils.BuilderManager;
import com.idpz.instacity.utils.VideoPostAdapter;
import com.idpz.instacity.utils.WeatherFunction;
import com.nightonke.boommenu.BoomButtons.ButtonPlaceEnum;
import com.nightonke.boommenu.BoomButtons.OnBMClickListener;
import com.nightonke.boommenu.BoomMenuButton;
import com.nightonke.boommenu.ButtonEnum;
import com.nightonke.boommenu.Piece.PiecePlaceEnum;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static android.content.Intent.FLAG_ACTIVITY_REORDER_TO_FRONT;

/**
 * Created by h on 2017/12/31.
 */

public class MainFragment extends Fragment {
    private SwipeRefreshLayout swipeRefreshLayout;
    private static final String TAG = "CameraFragment";
    ProgressDialog pd;
    ArrayList<Video> dataModels;
    //    DBLastData dbLastData;
    VideoPostAdapter videoPostAdapter;
    String server="",fullServer="",homeLat="",homeLng="";
    int lim1=0,lim2=20;
    Boolean reqVideoFlag =false,connected=false;
    private BoomMenuButton bmb;
    ImageButton btnCars, btntourism, btnHandyCraft;

    TextView cityField, detailsField, currentTemperatureField, humidity_field, pressure_field, weatherIcon, updatedField;
    ImageView imgWeather;
    Typeface weatherFont;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.fragment_main,container,false);

        btnCars = (ImageButton) view.findViewById(R.id.btnCars);
        btntourism = (ImageButton) view.findViewById(R.id.btnTourism);
        btnHandyCraft = (ImageButton) view.findViewById(R.id.btnHandyCarft);
        SharedPreferences SP1;
        SP1 = PreferenceManager.getDefaultSharedPreferences(getContext());
        homeLat=SP1.getString("homelat", "0");
        homeLng=SP1.getString("homelng", "0");

        Toast.makeText(getContext(), homeLat+":"+homeLng, Toast.LENGTH_LONG).show();

        btnCars.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent4=new Intent(getContext(), SearchActivity.class);
                intent4.addFlags(FLAG_ACTIVITY_REORDER_TO_FRONT);
                getContext().startActivity(intent4);

            }
        });
        btntourism.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent4=new Intent(getContext(), TourismActivity.class);
                intent4.addFlags(FLAG_ACTIVITY_REORDER_TO_FRONT);
                getContext().startActivity(intent4);
            }
        });
        btnHandyCraft.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent4=new Intent(getContext(), TourismActivity.class);
                intent4.addFlags(FLAG_ACTIVITY_REORDER_TO_FRONT);
                getContext().startActivity(intent4);
            }
        });

        bmb = (BoomMenuButton) view.findViewById(R.id.bmb);
        assert bmb != null;
        bmb.setButtonEnum(ButtonEnum.TextOutsideCircle);
        bmb.setPiecePlaceEnum(PiecePlaceEnum.DOT_8_1);
        bmb.setButtonPlaceEnum(ButtonPlaceEnum.SC_8_3);

        for (int i = 0; i < bmb.getPiecePlaceEnum().pieceNumber(); i++)
            bmb.addBuilder(BuilderManager.getTextOutsideCircleButtonBuilder().listener(new OnBMClickListener() {
                @Override
                public void onBoomButtonClick(int index) {
                    switch (index) {
                        case 1:
                            Intent intent1 = new Intent(getContext(), MapFragment.class);
                            Bundle b = new Bundle();
                            b.putString("key", "food"); //          city Map
                            intent1.putExtras(b); //Put your id to your next Intent
                            startActivity(intent1);
                            break;
                        case 2:
                            Intent intent2 = new Intent(getContext(), MapFragment.class);
                            Bundle c2 = new Bundle();
                            c2.putString("key", "health"); //Your id
                            intent2.putExtras(c2); //Put your id to your next Intent
                            startActivity(intent2);
                            break;
                        case 3:
                            Intent intent3 = new Intent(getContext(), MapFragment.class);
                            Bundle d3 = new Bundle();
                            d3.putString("key", "religion"); //Your id
                            intent3.putExtras(d3); //Put your id to your next Intent
                            startActivity(intent3);
                            break;
                        case 4:
                            Intent intent = new Intent(getContext(), MapFragment.class);
                            startActivity(intent);
                            break;
                        case 5:
                            Intent intent4 = new Intent(getContext(), MapFragment.class);
                            Bundle e4 = new Bundle();
                            e4.putString("key", "services"); //Your id
                            intent4.putExtras(e4); //Put your id to your next Intent
                            startActivity(intent4);
                            break;
                        case 6:
                            Intent intent6 = new Intent(getContext(), MapFragment.class);

                            startActivity(intent6);
                            break;
                        case 7:
                            Intent intent7 = new Intent(getContext(), MapFragment.class);
                            Bundle b7 = new Bundle();
                            b7.putString("key", "sport"); //Your id
                            intent7.putExtras(b7); //Put your id to your next Intent
                            startActivity(intent7);
                            break;
                        case 0:
                            Intent intent0 = new Intent(getContext(), MapFragment.class);
                            Bundle b0 = new Bundle();
                            b0.putString("key", "edu"); //          137
                            intent0.putExtras(b0); //Put your id to your next Intent
                            startActivity(intent0);
                            break;

                    }
                }
            }).normalTextRes(textResources(i))
                    .normalText("سلام")
                    .highlightedTextRes(R.string.text_ham_button_sub_text_normal));


        weatherFont = Typeface.createFromAsset(getContext().getAssets(), "fonts/weathericons-regular-webfont.ttf");
        imgWeather=(ImageView)view.findViewById(R.id.imgWeather);
        cityField = (TextView)view.findViewById(R.id.city_field);
        updatedField = (TextView)view.findViewById(R.id.updated_field);
//        detailsField = (TextView)view.findViewById(R.id.details_field);
        currentTemperatureField = (TextView)view.findViewById(R.id.current_temperature_field);
        humidity_field = (TextView)view.findViewById(R.id.humidity_field);
//        pressure_field = (TextView)view.findViewById(R.id.pressure_field);
        weatherIcon = (TextView)view.findViewById(R.id.weather_icon);
//        weatherIcon.setTypeface(weatherFont);


        WeatherFunction.placeIdTask asyncTask =new WeatherFunction.placeIdTask(new WeatherFunction.AsyncResponse() {
            public void processFinish(String weather_city, String weather_description, String weather_temperature, String weather_humidity, String weather_pressure, String weather_updatedOn, String weather_iconText, String sun_rise) {
                Log.d(TAG, "processFinish: "+weather_city+" "+weather_updatedOn);
                switch (weather_description){
                    case "clear sky":
                        imgWeather.setImageResource(R.drawable.sunny);
                        updatedField.setText("آسمان صاف");
                        break;
                    case "few clouds":
                        imgWeather.setImageResource(R.drawable.partlycloudy);
                        updatedField.setText("کمی ابری");
                        break;
                    case "scatteredclouds":
                        imgWeather.setImageResource(R.drawable.scatteredclouds);
                        updatedField.setText("ابری");
                        break;
                    case "broken clouds":
                        imgWeather.setImageResource(R.drawable.scatteredclouds);
                        updatedField.setText("ابرهای پراکنده");
                        break;
                    case "shower rain":
                        imgWeather.setImageResource(R.drawable.rainshower);
                        updatedField.setText("کمی بارانی");
                        break;
                    case "rain":
                        imgWeather.setImageResource(R.drawable.rainshower);
                        updatedField.setText("بارانی");
                        break;
                    case "thunderstorm":
                        imgWeather.setImageResource(R.drawable.thunderstorm);
                        updatedField.setText("طوفان و رعدبرق");
                        break;
                    case "snow":
                        imgWeather.setImageResource(R.drawable.snow);
                        updatedField.setText("برفی");
                        break;
                    case "mist":
                        imgWeather.setImageResource(R.drawable.sunny);
                        updatedField.setText("آفتابی");
                        break;
                    default:
                        imgWeather.setImageResource(R.drawable.mist);
                        updatedField.setText("مه و غبار");
                }
                cityField.setText(weather_city);
//                updatedField.setText(weather_updatedOn);

                currentTemperatureField.setText("دمای هوا:"+weather_temperature);
                humidity_field.setText("رطوبت: "+weather_humidity);
//                pressure_field.setText("فشار: "+weather_pressure);
//                weatherIcon.setText(Html.fromHtml(weather_iconText));

            }
        });
        asyncTask.execute(homeLat, homeLng); //  asyncTask.execute("Latitude", "Longitude")



        return view;
    }


    public void reqVideos() {
        RequestQueue queue = Volley.newRequestQueue(getActivity());
        String url = fullServer;
        StringRequest postRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>()
                {
                    @Override
                    public void onResponse(String response) {
                        reqVideoFlag =true;
                        pd.dismiss();
                        swipeRefreshLayout.setRefreshing(false);
                        Log.d(TAG, "onResponse: videos recived");

                        JSONArray jsonArray = null;
                        try {
                            jsonArray = new JSONArray(response);
                            Video video;
                            JSONObject jsonObject = jsonArray.getJSONObject(0);


                            for (int i = jsonArray.length(); i > 0; i--) {
                                jsonObject = jsonArray.getJSONObject(i - 1);
                                video=new Video();
                                video.setId(jsonObject.getInt("id"));
                                video.setTitle(jsonObject.getString("title"));
                                video.setVideoUrl(jsonObject.getString("url"));
                                video.setComment(jsonObject.getString("comment"));
                                video.setDetail(jsonObject.getString("vdate"));

                                dataModels.add(video);

                            }
                            videoPostAdapter.notifyDataSetChanged();
                        } catch (JSONException e) {
                            e.printStackTrace();

                        }



                    }
                },
                new Response.ErrorListener()
                {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // TODO Auto-generated method stub
                        Log.d("ERROR","error => "+error.toString());
                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String>params = new HashMap<String,String>();
                params.put("lim1", String.valueOf(lim1));
                params.put("lim2", String.valueOf(lim2));
                return params;
            }
        };
        queue.add(postRequest);

    }

    private int textResources(int i) {
        switch (i){

            case 1:
                return R.string.text1;

            case 2:
                return R.string.text2;

            case 3:
                return R.string.text3;

            case 4:
                return R.string.text4;

            case 5:
                return R.string.text5;

            case 6:
                return R.string.text6;

            case 7:
                return R.string.text7;

            case 8:
                return R.string.text8;


            default:
                return R.string.text9;


        }

    }

}
