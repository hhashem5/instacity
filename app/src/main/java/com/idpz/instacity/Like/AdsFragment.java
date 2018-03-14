package com.idpz.instacity.Like;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.idpz.instacity.R;
import com.idpz.instacity.models.Ads;
import com.idpz.instacity.utils.AdsAdapter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by h on 2017/12/31.
 */

public class AdsFragment extends Fragment{
    private SwipeRefreshLayout swipeRefreshLayout;
    private static final String TAG = "AdsFragment";
    List<Ads> allAds;
    ArrayList<Ads> dataModels;
    ListView listView;
    ImageView btnAdsReg;
    AdsAdapter adapter;
    Boolean adsFlag=false,connected=false;
    String GET_ADS_URL="",server="";
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.fragment_ads,container,false);
        listView=(ListView)view.findViewById(R.id.lvAdsContent);
        btnAdsReg=(ImageView) view.findViewById(R.id.imgAddAds);
        SharedPreferences SP1;
        SP1 = PreferenceManager.getDefaultSharedPreferences(getContext());
        server=SP1.getString("server", "0");
        GET_ADS_URL =  server+"/i/getads.php";
        dataModels= new ArrayList<>();

        adapter= new AdsAdapter(getActivity(),dataModels);

        listView.setAdapter(adapter);


        new Thread() {
            @Override
            public void run() {
                while (!adsFlag) {
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            ConnectivityManager connectivityManager = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
                            if (connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState() == NetworkInfo.State.CONNECTED ||
                                    connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState() == NetworkInfo.State.CONNECTED) {
                                //we are connected to a network
                                connected = true;
                            } else {
                                connected = false;
//                                Toast.makeText(getActivity().getApplicationContext(), "اینترنت وصل نیست!", Toast.LENGTH_SHORT).show();
                            }

                            if (connected && !adsFlag) {
                                reqAds();
                            }

                        }
                    });
                    try {
                        sleep(7000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }.start();






        return view;
    }

    public void reqAds() {
        RequestQueue queue = Volley.newRequestQueue(getContext());
        String url = GET_ADS_URL;
        StringRequest postRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>()
                {
                    @Override
                    public void onResponse(String response) {
                        JSONArray jsonArray= null;

                        try {
                            jsonArray = new JSONArray(response);
                            adsFlag=true;
                            JSONObject jsonObject=jsonArray.getJSONObject(0);
                            dataModels.clear();
                            String all="";
                            for (int i=jsonArray.length();i>0;i--) {
                                jsonObject = jsonArray.getJSONObject(i-1);

                                Ads area=new Ads();
                                area.setId(jsonObject.getInt("id"));
                                area.setTitle(jsonObject.getString("title"));
                                area.setMemo(jsonObject.getString("memo"));
                                area.setAddress(jsonObject.getString("address"));
                                area.setTel(jsonObject.getString("tel"));
                                area.setPic(jsonObject.getString("pic"));

                                dataModels.add(area);

                            }
                            adapter.notifyDataSetChanged();


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
//                        txtczstatus.setText(error.toString()+"مشکلی در ارسال داده پیش آمده دوباره تلاش کنید");
                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String>params = new HashMap<String,String>();

//                params.put("name", );

                return params;
            }
        };
        queue.add(postRequest);
    }

}
