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
import android.widget.ListView;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.getkeepsafe.taptargetview.TapTarget;
import com.getkeepsafe.taptargetview.TapTargetView;
import com.idpz.instacity.Like.TourismActivity;
import com.idpz.instacity.Profile.ChangeCityActivity;
import com.idpz.instacity.Profile.LikesActivity;
import com.idpz.instacity.Profile.MyProfileActivity;
import com.idpz.instacity.R;
import com.idpz.instacity.Search.SearchActivity;
import com.idpz.instacity.Share.GalleryActivity;
import com.idpz.instacity.models.VisitPlace;
import com.idpz.instacity.utils.BuilderManager;
import com.idpz.instacity.utils.VideoPostAdapter;
import com.idpz.instacity.utils.VisitPlacesAdapter;
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
    private static final String TAG = "MainFragment";
    ProgressDialog pd;
    ArrayList<VisitPlace> dataModels;
    //    DBLastData dbLastData;
    VideoPostAdapter videoPostAdapter;
    String server="",fullServer="",homeLat="",homeLng="",ctDesc="",ctpic="",ctname="";
    int lim1=0,lim2=20;
    Boolean reqVideoFlag =false,connected=false,firstTime=true;
    private BoomMenuButton bmb;
    ImageButton btnCars, btntourism, btnHandyCraft,btnChangeCity;

    TextView cityField, detailsField, currentTemperatureField, humidity_field, pressure_field, weatherIcon, updatedField;
    TextView txtWind,txtMin,txtMax;
    ImageView imgWeather;
    Typeface weatherFont;
    ListView lvVisitPlaces;
    VisitPlacesAdapter visitPlacesAdapter;



    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.fragment_main,container,false);

        SharedPreferences SP1;
        SP1 = PreferenceManager.getDefaultSharedPreferences(getContext());
        server=SP1.getString("server", "0");
        fullServer =  server+"/i/getvisitplace.php";
        homeLat=SP1.getString("homelat", "0");
        homeLng=SP1.getString("homelng", "0");
        ctpic=SP1.getString("ctpic", "0");
        ctDesc=SP1.getString("ctdesc", "0");
        ctname=SP1.getString("ctname", "");
        connected=SP1.getBoolean("connected", false);
        firstTime=SP1.getBoolean("firsttime", true);
        btnCars = (ImageButton) view.findViewById(R.id.btnCars);
        btntourism = (ImageButton) view.findViewById(R.id.btnTourism);
        btnHandyCraft = (ImageButton) view.findViewById(R.id.btnHandyCarft);
        btnChangeCity = (ImageButton) view.findViewById(R.id.btnChangCity);

        txtMax=(TextView)view.findViewById(R.id.max_field);
        txtMin=(TextView)view.findViewById(R.id.min_field);
        txtWind=(TextView)view.findViewById(R.id.speed_field);

        btnCars.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent4=new Intent(getContext(), SearchActivity.class);
                intent4.addFlags(FLAG_ACTIVITY_REORDER_TO_FRONT);
                getContext().startActivity(intent4);

            }
        });

        btnChangeCity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent4=new Intent(getContext(), ChangeCityActivity.class);
                intent4.addFlags(FLAG_ACTIVITY_REORDER_TO_FRONT);
                getContext().startActivity(intent4);

            }
        });
        btntourism.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent4=new Intent(getContext(), TourismActivity.class);
                intent4.addFlags(FLAG_ACTIVITY_REORDER_TO_FRONT);
                intent4.putExtra("position", 0);
                getContext().startActivity(intent4);
            }
        });
        btnHandyCraft.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent4=new Intent(getContext(), TourismActivity.class);
                intent4.addFlags(FLAG_ACTIVITY_REORDER_TO_FRONT);
                intent4.putExtra("position", 2);
                getContext().startActivity(intent4);
            }
        });

        // tourist visit places list view
        dataModels = new ArrayList<>();
        lvVisitPlaces=(ListView)view.findViewById(R.id.lvVisitPlaces);
        visitPlacesAdapter=new VisitPlacesAdapter(getActivity(),dataModels);
        lvVisitPlaces.setAdapter(visitPlacesAdapter);



        bmb = (BoomMenuButton) view.findViewById(R.id.bmb);
        assert bmb != null;
        bmb.setButtonEnum(ButtonEnum.TextOutsideCircle);
        bmb.setPiecePlaceEnum(PiecePlaceEnum.DOT_6_1);
        bmb.setButtonPlaceEnum(ButtonPlaceEnum.SC_6_3);

        for (int i = 0; i < bmb.getPiecePlaceEnum().pieceNumber(); i++)
            bmb.addBuilder(BuilderManager.getTextOutsideCircleButtonBuilder().listener(new OnBMClickListener() {
                @Override
                public void onBoomButtonClick(int index) {
                    switch (index) {
                        case 1:
//                            Toast.makeText(getActivity(), "1", Toast.LENGTH_SHORT).show();
                            Intent intent1 = new Intent(getContext(), GalleryActivity.class);
                            Bundle b = new Bundle();
                            b.putString("key", "food"); //          city Map
                            intent1.putExtras(b); //Put your id to your next Intent
                            startActivity(intent1);
                            break;
                        case 2:
//                            Toast.makeText(getActivity(), "2", Toast.LENGTH_SHORT).show();
                            Intent intent2 = new Intent(getContext(), MyProfileActivity.class);
                            Bundle c2 = new Bundle();
                            c2.putString("key", "health"); //Your id
                            intent2.putExtras(c2); //Put your id to your next Intent
                            startActivity(intent2);
                            break;
                        case 3:
//                            Toast.makeText(getActivity(), "3", Toast.LENGTH_SHORT).show();
                            Intent intent3 = new Intent(getContext(), LikesActivity.class);
                            Bundle d3 = new Bundle();
                            d3.putString("key", "religion"); //Your id
                            intent3.putExtras(d3); //Put your id to your next Intent
                            startActivity(intent3);
                            break;
                        case 4:
//                            Toast.makeText(getActivity(), "4", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(getContext(), SearchActivity.class);
                            startActivity(intent);
                            break;
                        case 5:
//                            Toast.makeText(getActivity(), "5", Toast.LENGTH_SHORT).show();
                            Intent intent4 = new Intent(getContext(), VideoActivity.class);
                            Bundle e4 = new Bundle();
                            e4.putString("key", "services"); //Your id
                            intent4.putExtras(e4); //Put your id to your next Intent
                            startActivity(intent4);
                            break;
                        case 6:
//                            Toast.makeText(getActivity(), "6", Toast.LENGTH_SHORT).show();
                            Intent intent6 = new Intent(getContext(), KaryabiActivity.class);
                            startActivity(intent6);
                            break;
                        case 7:
//                            Toast.makeText(getActivity(), "7", Toast.LENGTH_SHORT).show();
                            Intent intent7 = new Intent(getContext(), RuydadActivity.class);
                            Bundle b7 = new Bundle();
                            b7.putString("key", "sport"); //Your id
                            intent7.putExtras(b7); //Put your id to your next Intent
                            startActivity(intent7);
                            break;
                        case 0:
//                            Toast.makeText(getActivity(), "0", Toast.LENGTH_SHORT).show();
                            Intent intent0 = new Intent(getContext(), HomeActivity.class);
//                            intent0.addFlags(FLAG_ACTIVITY_REORDER_TO_FRONT);
                            intent0.putExtra("position", 0); //          137
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

        if (connected)reqVisitPlace();

        WeatherFunction.placeIdTask asyncTask =new WeatherFunction.placeIdTask(new WeatherFunction.AsyncResponse() {
            public void processFinish(String weather_city, String weather_description, String weather_temperature,
                                      String weather_humidity, String weather_pressure, String weather_updatedOn,
                                      String weather_iconText, String sun_rise,String min,String max,String wind) {
                Log.d(TAG, "processFinish: "+weather_city+" "+weather_description);
                switch (weather_description.toLowerCase()){
                    case "clear sky":
                        imgWeather.setImageResource(R.drawable.sunny);
                        updatedField.setText("آسمان صاف");
                        break;
                    case "few clouds":
                        imgWeather.setImageResource(R.drawable.partlycloudy);
                        updatedField.setText("کمی ابری");
                        break;
                    case "scattered clouds":
                        imgWeather.setImageResource(R.drawable.scatteredclouds);
                        updatedField.setText("ابری");
                        break;
                    case "broken clouds":
                        imgWeather.setImageResource(R.drawable.scatteredclouds);
                        updatedField.setText("ابرهای پراکنده");
                        break;
                    case "shower rain":
                        imgWeather.setImageResource(R.drawable.rainshower);
                        updatedField.setText(" بارانی");
                        break;
                    case "light rain":
                        imgWeather.setImageResource(R.drawable.rainshower);
                        updatedField.setText(" بارانی");
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
                        imgWeather.setImageResource(R.drawable.mist);
                        updatedField.setText("مه و غبار");
                        break;
                    default:
                        imgWeather.setImageResource(R.drawable.mist);
                        updatedField.setText("مه و غبار");
                }
                cityField.setText("مرکز: "+weather_city);
//                updatedField.setText(weather_updatedOn);

                currentTemperatureField.setText(weather_temperature);
                humidity_field.setText(weather_humidity);
//                pressure_field.setText("فشار: "+weather_pressure);
//                weatherIcon.setText(Html.fromHtml(weather_iconText));
                txtMin.setText("↓ "+min+" ");
                txtMax.setText("↑ "+max);
                txtWind.setText(wind+"km");

            }
        });
        asyncTask.execute(homeLat, homeLng); //  asyncTask.execute("Latitude", "Longitude")

        if (firstTime)targetView(view);

        return view;
    }


    public void reqVisitPlace() {
        RequestQueue queue = Volley.newRequestQueue(getActivity());
        String url = fullServer;
        StringRequest postRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>()
                {
                    @Override
                    public void onResponse(String response) {
                        reqVideoFlag =true;
//                        pd.dismiss();
//                        swipeRefreshLayout.setRefreshing(false);
                        dataModels.clear();
                        Log.d(TAG, "onResponse: visit places recived");

                        JSONArray jsonArray = null;
                        try {
                            jsonArray = new JSONArray(response);
                            VisitPlace visitPlace;
                            JSONObject jsonObject = jsonArray.getJSONObject(0);


                            for (int i = jsonArray.length(); i > 0; i--) {
                                jsonObject = jsonArray.getJSONObject(i - 1);
                                visitPlace=new VisitPlace();
                                visitPlace.setId(jsonObject.getInt("id"));
                                visitPlace.setName(jsonObject.getString("name"));
                                visitPlace.setYear(jsonObject.getString("pyear"));
                                visitPlace.setTicket(jsonObject.getString("ticket"));
                                visitPlace.setDays(jsonObject.getString("pdays"));
                                visitPlace.setHours(jsonObject.getString("phours"));
                                visitPlace.setTel(jsonObject.getString("tel"));
                                visitPlace.setAddress(jsonObject.getString("address"));
                                visitPlace.setMemo(jsonObject.getString("memo"));
                                visitPlace.setPic(server+"/assets/images/places/"+jsonObject.getString("pic"));
                                dataModels.add(visitPlace);

                            }
                            visitPlacesAdapter.notifyDataSetChanged();
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
                        reqVisitPlace();
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
    public void doSomething(){
        SharedPreferences.Editor SP = PreferenceManager.getDefaultSharedPreferences(getActivity()).edit();
        SP.putBoolean("firsttime", false);
        SP.apply();
    }

    public void targetView(View v){
        TapTargetView.showFor(getActivity(),                 // `this` is an Activity
                TapTarget.forView(v.findViewById(R.id.btnChangCity), "تغییر منطقه", "از اطلاعات مناطق دیگر دیدن کنید جاذبه ها، امکانات و رویدادهای مناطق دیگر را ببینید")
                        // All options below are optional
                        .outerCircleColor(R.color.darkblue)      // Specify a color for the outer circle
                        .outerCircleAlpha(0.76f)            // Specify the alpha amount for the outer circle
                        .targetCircleColor(R.color.white)   // Specify a color for the target circle
                        .titleTextSize(25)                  // Specify the size (in sp) of the title text
                        .titleTextColor(R.color.white)      // Specify the color of the title text
                        .descriptionTextSize(20)            // Specify the size (in sp) of the description text
                        .descriptionTextColor(R.color.black)  // Specify the color of the description text
                        .textColor(R.color.white)            // Specify a color for both the title and description text
                        .textTypeface(Typeface.SANS_SERIF)  // Specify a typeface for the text
                        .dimColor(R.color.black)            // If set, will dim behind the view with 30% opacity of the given color
                        .drawShadow(true)                   // Whether to draw a drop shadow or not
                        .cancelable(false)                  // Whether tapping outside the outer circle dismisses the view
                        .tintTarget(true)                   // Whether to tint the target view's color
                        .transparentTarget(false)           // Specify whether the target is transparent (displays the content underneath)
//                        .icon(Drawable)                     // Specify a custom drawable to draw as the target
                        .targetRadius(60) ,                 // Specify the target radius (in dp)
                new TapTargetView.Listener() {          // The listener can listen for regular clicks, long clicks or cancels
                    @Override
                    public void onTargetClick(TapTargetView view) {
                        super.onTargetClick(view);      // This call is optional
                        doSomething();
                    }
                }
        );
    }
}
