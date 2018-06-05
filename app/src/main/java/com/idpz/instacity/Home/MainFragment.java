package com.idpz.instacity.Home;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.StrictMode;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.TimeoutError;
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
import com.idpz.instacity.models.MyWeather;
import com.idpz.instacity.models.VisitPlace;
import com.idpz.instacity.models.Weather;
import com.idpz.instacity.utils.BuilderManager;
import com.idpz.instacity.utils.JSONWeatherParser;
import com.idpz.instacity.utils.VideoPostAdapter;
import com.idpz.instacity.utils.VisitPlacesAdapter;
import com.idpz.instacity.utils.WeatherAdapter;
import com.idpz.instacity.utils.WeatherHttpClient;
import com.nightonke.boommenu.BoomButtons.ButtonPlaceEnum;
import com.nightonke.boommenu.BoomButtons.OnBMClickListener;
import com.nightonke.boommenu.BoomMenuButton;
import com.nightonke.boommenu.ButtonEnum;
import com.nightonke.boommenu.Piece.PiecePlaceEnum;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import static android.content.Intent.FLAG_ACTIVITY_REORDER_TO_FRONT;

/**
 * Created by h on 2017/12/31.
 */

public class MainFragment extends Fragment {
    private SwipeRefreshLayout swipeRefreshLayout;
    private static final String TAG = "MainFragment";

    ArrayList<MyWeather> weatherModels;
    ArrayList<VisitPlace> dataModels;
    //    DBLastData dbLastData;
    VideoPostAdapter videoPostAdapter;
    private static final String API = "https://api.darksky.net/forecast/674956b88e8f6ab8cf03d9533490ff87/";
    String coords = "",darkCoords = "", extra = "?lang=en&units=si&exclude=hourly,flags";

    String server = "", fullServer = "", homeLat = "", homeLng = "", ctDesc = "", ctpic = "", ctname = "";
    int lim1 = 0, lim2 = 20;
    public static volatile int netState=3;
    Boolean visitFlag = false, connected = false, firstTime = true, weatherFlag = false;

    ImageButton btnCars, btntourism, btnHandyCraft, btnChangeCity;
    int failCount=0;
    TextView cityField, detailsField, currentTemperatureField, humidity_field, pressure_field, weatherIcon, updatedField;
    TextView txtWind, txtMax;
    ImageView imgWeather,imgError;
    Typeface weatherFont;
    ListView lvVisitPlaces, lvForecast;
    VisitPlacesAdapter visitPlacesAdapter;
    WeatherAdapter weatherAdapter;
    ProgressBar progressBar;
    ImageView imgRetry;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_main, container, false);

        Typeface yekan = Typeface.createFromAsset(getActivity().getAssets(), "fonts/YEKAN.TTF");


        final SharedPreferences SP1;
        SP1 = PreferenceManager.getDefaultSharedPreferences(getContext());
        server = SP1.getString("server", "0");
        fullServer = server + "/i/getvisitplace.php";
        homeLat = SP1.getString("homelat", "0");
        homeLng = SP1.getString("homelng", "0");
        ctpic = SP1.getString("ctpic", "0");
        ctDesc = SP1.getString("ctdesc", "0");
        ctname = SP1.getString("ctname", "");
//        remain = SP1.getBoolean("connected", false);
        firstTime = SP1.getBoolean("firsttime", true);
        btnCars = view.findViewById(R.id.btnCars);
        btntourism = view.findViewById(R.id.btnTourism);
        btnHandyCraft = view.findViewById(R.id.btnHandyCarft);
        btnChangeCity = view.findViewById(R.id.btnChangCity);
        progressBar= view.findViewById(R.id.progressFirst);
        imgError= view.findViewById(R.id.imgErrorConnect);
        imgRetry= view.findViewById(R.id.imgVRetry);
        coords = "lat="+homeLat + "&lon=" + homeLng+"&appid=0b9a46a0c6f55b8795adcfd9d3e28d36&lang=fa&units=metric";
        darkCoords=homeLat+","+homeLng;
        txtMax = view.findViewById(R.id.max_field);
//        txtMin=(TextView)view.findViewById(R.id.min_field);
        txtWind = view.findViewById(R.id.speed_field);
        weatherFont = Typeface.createFromAsset(getContext().getAssets(), "fonts/weathericons-regular-webfont.ttf");
        imgWeather = view.findViewById(R.id.imgWeather);
        cityField = view.findViewById(R.id.city_field);
        updatedField = view.findViewById(R.id.updated_field);
//        detailsField = (TextView)view.findViewById(R.id.details_field);
        currentTemperatureField = view.findViewById(R.id.current_temperature_field);
        humidity_field = view.findViewById(R.id.humidity_field);
//        pressure_field = (TextView)view.findViewById(R.id.pressure_field);
        weatherIcon = view.findViewById(R.id.weather_icon);
//        weatherIcon.setTypeface(weatherFont);
//        txtMax.setTypeface(yekan);
//        txtWind.setTypeface(yekan);
//        cityField.setTypeface(yekan);
//        updatedField.setTypeface(yekan);
//        currentTemperatureField.setTypeface(yekan);
//        humidity_field.setTypeface(yekan);
//        weatherIcon.setTypeface(weatherFont);
//        txtMax.setTypeface(yekan);
//        txtMax.setTypeface(yekan);
        cityField.setText(ctname);

        imgRetry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                netState=3;
                Log.d(TAG, "onClick: Msg");
                if (isConnected()) {
                    imgRetry.setVisibility(View.GONE);
                    progressBar.setVisibility(View.VISIBLE);
                    reqVisitPlace();
                }

            }
        });

        btnCars.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent4 = new Intent(getContext(), SearchActivity.class);
                intent4.addFlags(FLAG_ACTIVITY_REORDER_TO_FRONT);
                getContext().startActivity(intent4);

            }
        });

        btnChangeCity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent4 = new Intent(getContext(), ChangeCityActivity.class);
                intent4.addFlags(FLAG_ACTIVITY_REORDER_TO_FRONT);
                getContext().startActivity(intent4);

            }
        });
        btntourism.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent4 = new Intent(getContext(), TourismActivity.class);
                intent4.addFlags(FLAG_ACTIVITY_REORDER_TO_FRONT);
                intent4.putExtra("position", 0);
                getContext().startActivity(intent4);
            }
        });
        btnHandyCraft.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent4 = new Intent(getContext(), TourismActivity.class);
                intent4.addFlags(FLAG_ACTIVITY_REORDER_TO_FRONT);
                intent4.putExtra("position", 2);
                getContext().startActivity(intent4);
            }
        });

        // tourist visit places list view
        dataModels = new ArrayList<>();
        lvVisitPlaces = view.findViewById(R.id.lvVisitPlaces);
        visitPlacesAdapter = new VisitPlacesAdapter(getActivity(), dataModels);
        lvVisitPlaces.setAdapter(visitPlacesAdapter);

        // weather forecast list view
        weatherModels = new ArrayList<>();
        lvForecast = view.findViewById(R.id.lvForecast);
        weatherAdapter = new WeatherAdapter(getActivity(), weatherModels);
        lvForecast.setAdapter(weatherAdapter);

        lvForecast.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Toast.makeText(getContext(), "وضعیت هوا:"+weatherModels.get(position).getSummary(), Toast.LENGTH_SHORT).show();
            }
        });

        TextView txtForecast = view.findViewById(R.id.txtForecast);
        txtForecast.setText("↓ پیش بینی هوای روزهای آینده ↓");

        SharedPreferences.Editor SP = PreferenceManager.getDefaultSharedPreferences(getContext()).edit();
        SP.putString("tabpos", "1");
        SP.apply();
        BoomMenuButton bmb;
        bmb = view.findViewById(R.id.bmb);
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
                            Intent intent = new Intent(getContext(), VisitSearchActivity.class);
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
                            Intent intent6 = new Intent(getContext(), VisitSearchActivity.class);
                            startActivity(intent6);
                            break;
                        case 7:
//                            Toast.makeText(getActivity(), "7", Toast.LENGTH_SHORT).show();
                            Intent intent7 = new Intent(getContext(), SmsVerificationActivity.class);
                            Bundle b7 = new Bundle();
                            b7.putString("key", "sport"); //Your id
                            intent7.putExtras(b7); //Put your id to your next Intent
                            startActivity(intent7);
                            break;
                        case 0:
//                            Toast.makeText(getActivity(), "0", Toast.LENGTH_SHORT).show();
//                            Intent intent0 = new Intent(getContext(), HomeActivity.class);
//                            intent0.addFlags(FLAG_ACTIVITY_REORDER_TO_FRONT);
                            SharedPreferences.Editor SP = PreferenceManager.getDefaultSharedPreferences(getContext()).edit();
                            SP.putString("tabpos", "0");
                            SP.apply();
//                            intent0.putExtra("position", 0); //          137
//                            startActivity(intent0);
                            ((HomeActivity) getActivity()).tabselect();
                            break;

                    }
                }
            }).normalTextRes(textResources(i))
                    .normalText("سلام")
                    .highlightedTextRes(R.string.text_ham_button_sub_text_normal));


        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);



                            if (isConnected()) {
                                 reqVisitPlace();
//                                JSONWeatherTask task = new JSONWeatherTask();
//                                task.execute(new String[]{coords});
                            }

        imgError.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                netState=3;
                if (isConnected()) {
                    imgError.setVisibility(View.GONE);
                    progressBar.setVisibility(View.VISIBLE);
                    reqVisitPlace();
                }

            }
        });

//        WeatherFunction.placeIdTask asyncTask =new WeatherFunction.placeIdTask(new WeatherFunction.AsyncResponse() {
//            public void processFinish(String weather_city, String weather_description, String weather_temperature,
//                                      String weather_humidity, String weather_pressure, String weather_updatedOn,
//                                      String weather_iconText, String sun_rise,String min,String max,String wind) {
//                Log.d(TAG, "processFinish: "+weather_city+" "+weather_description);
//                switch (weather_description.toLowerCase()){
//                    case "clear sky":
//                        imgWeather.setImageResource(R.drawable.sunny);
//                        updatedField.setText("آسمان صاف");
//                        break;
//                    case "few clouds":
//                        imgWeather.setImageResource(R.drawable.partlycloudy);
//                        updatedField.setText("کمی ابری");
//                        break;
//                    case "scattered clouds":
//                        imgWeather.setImageResource(R.drawable.scatteredclouds);
//                        updatedField.setText("ابری");
//                        break;
//                    case "broken clouds":
//                        imgWeather.setImageResource(R.drawable.scatteredclouds);
//                        updatedField.setText("ابرهای پراکنده");
//                        break;
//                    case "shower rain":
//                        imgWeather.setImageResource(R.drawable.rainshower);
//                        updatedField.setText(" بارانی");
//                        break;
//                    case "light rain":
//                        imgWeather.setImageResource(R.drawable.rainshower);
//                        updatedField.setText(" بارانی");
//                        break;
//                    case "rain":
//                        imgWeather.setImageResource(R.drawable.rainshower);
//                        updatedField.setText("بارانی");
//                        break;
//                    case "thunderstorm":
//                        imgWeather.setImageResource(R.drawable.thunderstorm);
//                        updatedField.setText("طوفان و رعدبرق");
//                        break;
//                    case "snow":
//                        imgWeather.setImageResource(R.drawable.snow);
//                        updatedField.setText("برفی");
//                        break;
//                    case "mist":
//                        imgWeather.setImageResource(R.drawable.mist);
//                        updatedField.setText("مه و غبار");
//                        break;
//                    default:
//                        imgWeather.setImageResource(R.drawable.mist);
//                        updatedField.setText("مه و غبار");
//                }
//                cityField.setText("مرکز: "+weather_city);
////                updatedField.setText(weather_updatedOn);
//
//                currentTemperatureField.setText(weather_temperature);
//                humidity_field.setText(weather_humidity);
////                pressure_field.setText("فشار: "+weather_pressure);
////                weatherIcon.setText(Html.fromHtml(weather_iconText));
////                txtMin.setText("↓ "+min+" ");
//                txtMax.setText("↑ "+max);
//                txtWind.setText(wind+"km");
//
//            }
//        });
//        asyncTask.execute(homeLat, homeLng); //  asyncTask.execute("Latitude", "Longitude")



//if (isConnected()){
//    renderWeather(getJSON());
//}
        AsyncTaskRunner runner = new AsyncTaskRunner();
        runner.execute();

        if (firstTime) {
            targetView(view);
        }

        return view;

    }


    private void renderWeather(JSONObject json) {
        weatherFlag = true;

        try {
            //detailsField.setText("");
            //cityField.setText("");
            Calendar calendar = Calendar.getInstance();
            int today = calendar.get(Calendar.DAY_OF_WEEK);
            JSONObject today_array = json.getJSONObject("currently");
//            txtMin.setText("↓ "+today_array.getString("summary")+" ");
            findSky(today_array.getString("summary"));
            txtMax.setText("UV:" + today_array.getString("uvIndex") + " ");
            txtWind.setText(today_array.getString("windSpeed") + "km");
            updatedField.setText(tarjomeh(today_array.getString("summary")));
            currentTemperatureField.setText(today_array.getString("temperature"));
            humidity_field.setText(today_array.getString("humidity").substring(2, 4) + "%");

//            String[] pdays = {"SUN", "MON", "TUE", "WED", "THU", "FRI", "SAT"};
            String[] days = {"یکشنبه", "دوشنبه", "سه شنبه", "چهارشنبه", "پنجشنبه", "جمعه", "شنبه"};
            JSONArray data_array = json.getJSONObject("daily").getJSONArray("data");

            for (int i = 0; i < 7; i++) {

                JSONObject item = data_array.getJSONObject(i);
                MyWeather weather = new MyWeather();
                weather.setDay(days[(today + i) % 7]);
                weather.setSummary(tarjomeh(item.getString("summary")));
                weather.setIcon(String.valueOf(findIco(item.getString("icon"))));
                weather.setMaxTemp(item.getString("temperatureHigh").substring(0, 2));
                weather.setMintemp(item.getString("temperatureLow").substring(0, 2));
                weather.setWind(item.getString("windSpeed").substring(0, 3) + "km");
                weather.setHumidity(item.getString("humidity"));

                weatherModels.add(weather);
                weatherAdapter.notifyDataSetChanged();
//                Log.d(TAG, "renderWeather:0 "+weather.getSummary()+" icon= "+weather.getIcon()+" temp hi:"+weather.getMaxTemp()+" Lo:"+weather.getMintemp()+weather.getWind()+weather.getHumidity() );

            }


        } catch (Exception e) {
            Log.e("SimpleWeather", "One or more fields not found in the JSON data \n" + API + darkCoords + extra + "\n" + e.toString());
            failCount++;


        }
    }

//    public JSONObject getJSON() {
//
//        try {
//            //coord = "40.7127,-74.0059";//debug
//            URL url = new URL(String.format((API + darkCoords + extra)));
//
//            HttpURLConnection connection =
//                    (HttpURLConnection) url.openConnection();
//            connection.getInputStream();
//
//            System.out.print("CONNECTION:::" + connection.getInputStream());
//
//            BufferedReader reader = new BufferedReader(
//                    new InputStreamReader(connection.getInputStream()));
//
//            System.out.print("url:::");
//            StringBuffer json = new StringBuffer(1024);
//            String tmp = "";
//            while ((tmp = reader.readLine()) != null)
//                json.append(tmp).append("\n");
//            reader.close();
//
//            JSONObject data = new JSONObject(json.toString());
//            Log.d("receive", "getJSON: " + data.toString());
//            return data;
//        } catch (Exception e) {
//            e.printStackTrace();
//            failCount++;
//            weatherFlag = false;
//            return null;
//        }
//    }


            private class AsyncTaskRunner extends AsyncTask<String, String, JSONObject> {

                private String resp;
//                ProgressDialog progressDialog;

                @Override
                protected JSONObject doInBackground(String... params) {
                    publishProgress("Sleeping..."); // Calls onProgressUpdate()
                    try {
                        //coord = "40.7127,-74.0059";//debug
                        URL url = new URL(String.format((API + darkCoords + extra)));

                        HttpURLConnection connection =
                                (HttpURLConnection) url.openConnection();
                        connection.getInputStream();

                        System.out.print("CONNECTION:::" + connection.getInputStream());

                        BufferedReader reader = new BufferedReader(
                                new InputStreamReader(connection.getInputStream()));

                        System.out.print("url:::");
                        StringBuffer json = new StringBuffer(1024);
                        String tmp = "";
                        while ((tmp = reader.readLine()) != null)
                            json.append(tmp).append("\n");
                        reader.close();

                        JSONObject data = new JSONObject(json.toString());
                        Log.d("receive", "getJSON: " + data.toString());
                        return data;
                    } catch (Exception e) {
                        e.printStackTrace();
                        failCount++;
                        weatherFlag = false;
                        return null;
                    }
                }


                @Override
                protected void onPostExecute(JSONObject result) {
                    // execution of result of Long time consuming operation
//                    progressDialog.dismiss();
                    renderWeather(result);
                }


                @Override
                protected void onPreExecute() {
//                    progressDialog = ProgressDialog.show(MainActivity.this,
//                            "ProgressDialog",
//                            "Wait for "+time.getText().toString()+ " seconds");
                }


                @Override
                protected void onProgressUpdate(String... text) {
//                    finalResult.setText(text[0]);

                }
            }




    public void reqVisitPlace() {
        final RequestQueue queue = Volley.newRequestQueue(getActivity());
        String url = fullServer;
        final StringRequest postRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        visitFlag = true;
                        progressBar.setVisibility(View.GONE);
                        imgError.setVisibility(View.GONE);
                        imgRetry.setVisibility(View.GONE);
//                        pd.dismiss();
//                        swipeRefreshLayout.setRefreshing(false);
                        dataModels.clear();
                        Log.d(TAG, "onResponse: visit places recived");

                        JSONArray jsonArray ;
                        try {
                            jsonArray = new JSONArray(response);
                            VisitPlace visitPlace;
                            JSONObject jsonObject;


                            for (int i = jsonArray.length(); i > 0; i--) {
                                jsonObject = jsonArray.getJSONObject(i - 1);
                                visitPlace = new VisitPlace();
                                visitPlace.setId(jsonObject.getInt("id"));
                                visitPlace.setName(jsonObject.getString("name"));
                                visitPlace.setYear(jsonObject.getString("pyear"));
                                visitPlace.setTicket(jsonObject.getString("ticket"));
                                visitPlace.setDays(jsonObject.getString("pdays"));
                                visitPlace.setHours(jsonObject.getString("phours"));
                                visitPlace.setTel(jsonObject.getString("tel"));
                                visitPlace.setAddress(jsonObject.getString("address"));
                                visitPlace.setMemo(jsonObject.getString("memo"));
                                visitPlace.setPic(server + "/assets/images/places/" + jsonObject.getString("pic"));
                                dataModels.add(visitPlace);

                            }
                            visitPlacesAdapter.notifyDataSetChanged();


//                                renderWeather(getJSON());


                        } catch (JSONException e) {
                            e.printStackTrace();
                        }


                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // TODO Auto-generated method stub
                        if (error instanceof NetworkError) {

                            netState=0;
                            MessagesFragment.netState=0;
                            HomeFragment.netState=0;
                        }else if (error instanceof TimeoutError) {

                            netState=0;
                            MessagesFragment.netState=0;
                            HomeFragment.netState=0;
                        }
                        failCount++;

                        progressBar.setVisibility(View.GONE);
                        imgError.setVisibility(View.VISIBLE);

                        Log.d("ERROR","error => "+error.toString()+failCount);
                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("lim1", String.valueOf(lim1));
                params.put("lim2", String.valueOf(lim2));
                return params;
            }
        };
        queue.add(postRequest);

    }

    private int textResources(int i) {
        switch (i) {

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

    public void doSomething() {
        SharedPreferences.Editor SP = PreferenceManager.getDefaultSharedPreferences(getActivity()).edit();
        SP.putBoolean("firsttime", false);
        SP.apply();
    }

    public void targetView(View v) {
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
                        .targetRadius(60),                 // Specify the target radius (in dp)
                new TapTargetView.Listener() {          // The listener can listen for regular clicks, long clicks or cancels
                    @Override
                    public void onTargetClick(TapTargetView view) {
                        super.onTargetClick(view);      // This call is optional
                        doSomething();
                    }
                }
        );
    }


    private int findIco(String trans) {
        trans = trans.toLowerCase();


        if (trans.contains("scattered"))
            return R.drawable.iscloudy;

        if (trans.contains("partly cloudy"))
            return R.drawable.ipcloudy;

        if (trans.contains("mostly cloudy"))
            return R.drawable.imcloudy;

        if (trans.contains("shower"))
            return R.drawable.ishower;

        if (trans.contains("light rain"))
            return R.drawable.irain;

        if (trans.contains("rain"))
            return R.drawable.irain;

        if (trans.contains("storm"))
            return R.drawable.ithunder;

        if (trans.contains("snow"))
            return R.drawable.isnow;

        if (trans.contains("clear"))
            return R.drawable.isunny;

        if (trans.contains("mist")) {
            return R.drawable.imcloudy;
        } else {

            return R.drawable.imcloudy;

        }


    }

    public String tarjomeh(String str1) {
        str1 = str1.toLowerCase();
        String[] find = {"mist", "snow", "thunderstorm", "fogy", "rain", "light", "shower", "broken", "cloudy", "clear", "sky", "today", "morning", "afternoon", "night", "evening",
                "later", "tomorrow", "in the", "during", "heavy", "medium", "possible", "very", "sleet", "centimeters", "wind", "humidity", "fog", "less-than", "low", "high",
                "starting", "throughout", "mostly", "the", "day", "partly", "mostly", "continuing", "until", "haze","drizzle","breezy","and"};
        String[] replace = {"مه", "برف", "طوفان", "مه", "باران", "بارش آرام", "شدید", "تکه ای", "ابری", "صاف", "آسمان", "امروز", "صبح", "بعدظهر", "شب", "غروب",
                "بعد", "فردا", "در", "طول", "سنگین", "متوسط", "احتمال", "زیاد", "بوران", "سانت", "باد", "رطوبت", "مه", "کمتراز", "کم",
                "زیاد", "شروع", "تمام", "بیشتر", "", "روز", "قسمتی", "بیشتر", "ادامه دارد", "تا", "مه خفیف","باران خفیف","باد ملایم","و"};

        for (int i = 0; i < find.length; i++) {
            str1 = str1.replace(find[i], replace[i]);
        }
        return str1;
    }

public void findSky(String trans) {
    trans = trans.toLowerCase();

    if (trans.contains("scattered"))
        imgWeather.setImageResource(R.drawable.partlycloudy);

    if (trans.contains("partly cloudy"))
        imgWeather.setImageResource(R.drawable.partlycloudy);

    if (trans.contains("mostly cloudy"))
        imgWeather.setImageResource(R.drawable.scatteredclouds);

    if (trans.contains("shower"))
        imgWeather.setImageResource(R.drawable.rainshower);

    if (trans.contains("light rain"))
        imgWeather.setImageResource(R.drawable.rainshower);

    if (trans.contains("rain"))
        imgWeather.setImageResource(R.drawable.rainshower);

    if (trans.contains("storm"))
        imgWeather.setImageResource(R.drawable.thunderstorm);

    if (trans.contains("snow"))
        imgWeather.setImageResource(R.drawable.snow);

    if (trans.contains("clear"))
        imgWeather.setImageResource(R.drawable.sunny);

    if (trans.contains("mist")) {
        imgWeather.setImageResource(R.drawable.mist);
    } else {

        imgWeather.setImageResource(R.drawable.partlycloudy);

    }

}

//    public void reqWeather() {
//
//        new Thread(new Runnable() {
//            @Override
//            public void run() {
//
//            }
//        }).start();
//    }


    private class JSONWeatherTask extends AsyncTask<String, Void, Weather> {

        @Override
        protected Weather doInBackground(String... params) {
            Weather weather = new Weather();
            String data = ( (new WeatherHttpClient()).getWeatherData(params[0]));

            try {
                weather = JSONWeatherParser.getWeather(data);

                // Let's retrieve the icon
                weather.iconData = ( (new WeatherHttpClient()).getImage(weather.currentCondition.getIcon()));

            } catch (JSONException e) {
                e.printStackTrace();
            }
            return weather;

        }




        @Override
        protected void onPostExecute(Weather weather) {
            super.onPostExecute(weather);

            if (weather.iconData != null && weather.iconData.length > 0) {
                Bitmap img = BitmapFactory.decodeByteArray(weather.iconData, 0, weather.iconData.length);
                imgWeather.setImageBitmap(img);
            }

//            cityField.setText(weather.location.getCity() + "," + weather.location.getCountry());
                updatedField.setText(weather.currentCondition.getCondition() + "(" + weather.currentCondition.getDescr() + ")");
            currentTemperatureField.setText("" + Math.round((weather.temperature.getTemp() - 273.15)) + "�C");
            humidity_field.setText("" + weather.currentCondition.getHumidity() + "%");
//            pressure_field.setText("" + weather.currentCondition.getPressure() + " hPa");
            txtWind.setText("" + weather.wind.getSpeed() + " km");
            txtMax.setText("" + weather.wind.getDeg() + "�");

        }







    }




public boolean isConnected(){
    ConnectivityManager connectivityManager = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
    if (connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState() == NetworkInfo.State.CONNECTED ||
            connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState() == NetworkInfo.State.CONNECTED) {
        //we are connected to a network
        connected = true;

        Log.d(TAG, "Main run: net State="+netState+" connected="+connected);
        if (netState==0){
            Log.d(TAG, "Main run: net State="+netState);
            progressBar.setVisibility(View.GONE);
            imgError.setVisibility(View.VISIBLE);
            connected=false;
            return false;
        }
        return true;

    } else {
        progressBar.setVisibility(View.GONE);
        imgError.setVisibility(View.VISIBLE);
        imgRetry.setVisibility(View.VISIBLE);
        return false;
    }
}


}
