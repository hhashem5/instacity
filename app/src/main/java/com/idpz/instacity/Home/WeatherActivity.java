package com.idpz.instacity.Home;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.idpz.instacity.R;
import com.idpz.instacity.models.MyWeather;
import com.idpz.instacity.models.VisitPlace;
import com.idpz.instacity.utils.CustomTextView;
import com.idpz.instacity.utils.WeatherAdapter;

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

public class WeatherActivity extends AppCompatActivity {

    private static final String TAG = "activity_weather";
    ArrayList<MyWeather> weatherModels;
    ArrayList<VisitPlace> visitPlaces=new ArrayList<>();
    ListView  lvForecast;
    WeatherAdapter weatherAdapter;
    CustomTextView cityField, pressure_field, updatedField,txtSummary, detailsField;
    TextView txtWind, txtMax,txtMin, currentTemperatureField, humidity_field;
    ImageView imgWeather,imgRetry,imgBackcity;
    Typeface weatherFont;
    ProgressBar progressBar;
    private static final String API = "https://api.darksky.net/forecast/674956b88e8f6ab8cf03d9533490ff87/";
    String coords = "35.711,50.912",darkCoords = "", extra = "?lang=en&units=si&exclude=hourly,flags";
    String server = "",state, fullServer = "", homeLat = "", homeLng = "", ctDesc = "", ctpic = "", ctname = "",REGISTER_URL="";
    boolean weatherFlag=false,connected=false,visitFlag=false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weather);

        REGISTER_URL=getString(R.string.server)+"/j/getdata.php";
        SharedPreferences SP1;
        SP1 = PreferenceManager.getDefaultSharedPreferences(WeatherActivity.this);
        ctname=SP1.getString("ctname", "");
        homeLat = SP1.getString("homelat", "0");
        homeLng = SP1.getString("homelng", "0");
        state=SP1.getString("state", "");
        server=SP1.getString("server", "0");
        imgBackcity=(ImageView) findViewById(R.id.imgMainCityBack);

        Typeface yekan = Typeface.createFromAsset(WeatherActivity.this.getAssets(), "fonts/YEKAN.TTF");

        coords = "lat="+homeLat + "&lon=" + homeLng+"&appid=0b9a46a0c6f55b8795adcfd9d3e28d36&lang=fa&units=metric";
        darkCoords=homeLat+","+homeLng;
        txtMax =(TextView) findViewById(R.id.max_field);
        txtMin =(TextView) findViewById(R.id.min_field);
        txtWind =(TextView) findViewById(R.id.speed_field);
//        weatherFont = Typeface.createFromAsset(this.getAssets(), "fonts/weathericons-regular-webfont.ttf");
        imgWeather =(ImageView) findViewById(R.id.imgWeather);
        cityField =(CustomTextView) findViewById(R.id.city_field);
        updatedField =(CustomTextView) findViewById(R.id.updated_field);
        currentTemperatureField =(TextView) findViewById(R.id.current_temperature_field);
        humidity_field =(TextView) findViewById(R.id.humidity_field);
        detailsField=(CustomTextView) findViewById(R.id.details_field);
        txtSummary=(CustomTextView) findViewById(R.id.txtWeatherSummary);
        progressBar=(ProgressBar)findViewById(R.id.progressBar);
//        weatherIcon = findViewById(R.id.weather_icon);


        reqVisitPlaces();
        updatedField.setTypeface(yekan);
        cityField.setTypeface(yekan);

        imgRetry=(ImageView) findViewById(R.id.imgVSRetry);
        imgRetry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: ");
                if (isConnected()) {
                    imgRetry.setVisibility(View.GONE);
                    progressBar.setVisibility(View.VISIBLE);
                    WeatherActivity.AsyncTaskRunner runner = new WeatherActivity.AsyncTaskRunner();
                    runner.execute();
                }

            }
        });

        cityField.setText(ctname);

        // weather forecast list view
        weatherModels = new ArrayList<>();
        lvForecast =(ListView) findViewById(R.id.lvForecastWeather);
        weatherAdapter = new WeatherAdapter(this, weatherModels);
        lvForecast.setAdapter(weatherAdapter);

        lvForecast.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Toast.makeText(WeatherActivity.this, "وضعیت هوا:"+weatherModels.get(i).getSummary(), Toast.LENGTH_SHORT).show();
            }
        });

        WeatherActivity.AsyncTaskRunner runner = new WeatherActivity.AsyncTaskRunner();
        runner.execute();


    }

    private  class AsyncTaskRunner extends AsyncTask<String, String, JSONObject> {

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

                weatherFlag = false;
                return null;
            }
        }


        @Override
        protected void onPostExecute(JSONObject result) {

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

        private void renderWeather(JSONObject json) {
            weatherFlag = true;

            try {
//                detailsField.setText("");
                //cityField.setText("");
                Calendar calendar = Calendar.getInstance();
                int today = calendar.get(Calendar.DAY_OF_WEEK);
                JSONObject today_array = json.getJSONObject("currently");

                findSky(today_array.getString("summary"));
//                txtMax.setText("↑ " + today_array.getString("temperatureMax") + " ");
//                txtMin.setText("↓ "+today_array.getString("temperatureMin")+" ");
//                txtSummary.setText( today_array.getString("uvIndex") + " ");
                detailsField.setText("فرابنفش:" + today_array.getString("uvIndex") + " ");
                txtWind.setText(today_array.getString("windSpeed") + "km");
                updatedField.setText(tarjomeh(today_array.getString("summary")));
                currentTemperatureField.setText(today_array.getString("temperature"));
                humidity_field.setText(today_array.getString("humidity").substring(2, 4) + "%");

                String[] pdays = {"SUN", "MON", "TUE", "WED", "THU", "FRI", "SAT"};
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

                    Log.d(TAG, "renderWeather:0 " + weather.getSummary() + " icon= " + weather.getIcon() + " temp hi:" + weather.getMaxTemp() + " Lo:" + weather.getMintemp() + weather.getWind() + weather.getHumidity());

                }
                weatherAdapter.notifyDataSetChanged();
                progressBar.setVisibility(View.GONE);
            } catch (Exception e) {
                Log.e("SimpleWeather", "One or more fields not found in the JSON data \n" + API + darkCoords + extra + "\n" + e.toString());
                progressBar.setVisibility(View.GONE);

            }
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
                    "starting", "throughout", "mostly", "the", "day", "partly", "mostly", "continuing", "until", "haze", "drizzle", "breezy", "and","over"};
            String[] replace = {"مه", "برف", "طوفان", "مه", "باران", "بارش آرام", "شدید", "تکه ای", "ابری", "صاف", "آسمان", "امروز", "صبح", "بعدظهر", "شب", "غروب",
                    "بعد", "فردا", "در", "طول", "سنگین", "متوسط", "احتمال", "زیاد", "بوران", "سانت", "باد", "رطوبت", "مه", "کمتراز", "کم",
                    "زیاد", "شروع", "تمام", "بیشتر", "", "روز", "قسمتی", "بیشتر", "ادامه دارد", "تا", "مه خفیف", "باران خفیف", "باد ملایم", "و","تمام "};

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

    public boolean isConnected(){
        ConnectivityManager connectivityManager = (ConnectivityManager) WeatherActivity.this.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState() == NetworkInfo.State.CONNECTED ||
                connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState() == NetworkInfo.State.CONNECTED) {
            //we are connected to a network
            connected = true;

            Log.d(TAG, "visit search : connected="+connected);

            return true;

        } else {

            return false;
        }
    }

    public void reqVisitPlaces() {
//        Toast.makeText(MainActivity.this, " reqEvent", Toast.LENGTH_SHORT).show();
        RequestQueue queue = Volley.newRequestQueue(this);
        String url = REGISTER_URL;
//        final ProgressBar progressEvnt=findViewById(R.id.progressEvent);
//        final ImageView imgEventRetry=findViewById(R.id.imgEventRetry);
//        progressEvnt.setVisibility(View.VISIBLE);
        StringRequest postRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>()
                {
                    @Override
                    public void onResponse(String response) {
                        visitFlag = true;
                        Log.d(TAG, "onResponse: Visit Places Recieved"+response);
//                        progressEvnt.setVisibility(View.GONE);
//                        imgEventRetry.setVisibility(View.GONE);
                        JSONArray jsonArray = null;
                        visitFlag=true;
                        try {
                            jsonArray = new JSONArray(response);
                            if (response.length()>4){
                                visitPlaces.clear();
                            }

                            JSONObject jsonObject = jsonArray.getJSONObject(0);

                            for (int i = 0; i < jsonArray.length(); i++) {
                                jsonObject = jsonArray.getJSONObject(i);
                                VisitPlace visit=new VisitPlace();
                                visit.setName(jsonObject.getString("name"));
                                visit.setLat(jsonObject.getString("lat"));
                                visit.setLng(jsonObject.getString("lng"));
                                visit.setYear(jsonObject.getString("pyear"));
                                visit.setTicket(jsonObject.getString("ticket"));
                                visit.setDays(jsonObject.getString("pdays"));
                                visit.setHours(jsonObject.getString("phours"));
                                visit.setTel(jsonObject.getString("tel"));
                                visit.setAddress(jsonObject.getString("address"));
                                visit.setPic(jsonObject.getString("pic"));
                                visit.setMemo(jsonObject.getString("memo"));
                                visitPlaces.add(visit);

                            }
                            if (visitPlaces.size()>0) {
                                String imgPath=server+ "/assets/images/places/" + visitPlaces.get(0).getPic();
                                Glide.with(WeatherActivity.this).load(imgPath)
                                        .thumbnail(0.5f)
                                        .into(imgBackcity);
                                Log.d(TAG, "onResponse: ok back ground set "+imgPath);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
//                            progressEvnt.setVisibility(View.GONE);
//                            imgEventRetry.setVisibility(View.VISIBLE);
                        }



                    }
                },
                new Response.ErrorListener()
                {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // TODO Auto-generated method stub
                        Log.d("ERROR","error => "+error.toString());
//                        progressEvnt.setVisibility(View.GONE);
//                        imgEventRetry.setVisibility(View.VISIBLE);
                        visitFlag=false;
//                        Toast.makeText(MainActivity.this, "Error reqEvent", Toast.LENGTH_SHORT).show();
                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String>params = new HashMap<String,String>();
                params.put("db", "visitplaces");
                params.put("state", state);
                return params;
            }
        };
        queue.add(postRequest);

    }




}
