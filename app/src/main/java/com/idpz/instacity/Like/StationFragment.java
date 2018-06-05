package com.idpz.instacity.Like;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.idpz.instacity.R;
import com.idpz.instacity.models.Villa;
import com.idpz.instacity.utils.VillaAdapter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by h on 2017/12/31.
 */

public class StationFragment extends Fragment  implements SwipeRefreshLayout.OnRefreshListener{
    private SwipeRefreshLayout swipeRefreshLayout;

    private static final String TAG = "StationFragment";


    ArrayList<Villa> dataModels;
    ListView listView;
//    ImageView btnVillaReg;
    VillaAdapter adapter;
    Boolean artsFlag=false,connected=false;
    String GET_VILLA_URL ="",server="";

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.fragment_station,container,false);

        swipeRefreshLayout = view.findViewById(R.id.villasRefresh);
        swipeRefreshLayout.setOnRefreshListener(this);

        listView= view.findViewById(R.id.lvVillaContent);
//        btnVillaReg=(ImageView) view.findViewById(R.id.imgAddVilla);

//        btnVillaReg.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent intent=new Intent(getContext(),VillaAddActivity.class);
//                getActivity().startActivity(intent);
//            }
//        });

        dataModels= new ArrayList<>();

        listView.setAdapter(adapter);
        SharedPreferences SP1;
        SP1 = PreferenceManager.getDefaultSharedPreferences(getContext());
        server=SP1.getString("server", "0");
        GET_VILLA_URL =  server+"/i/getvillas.php";
        dataModels= new ArrayList<>();

        adapter= new VillaAdapter(getActivity(),dataModels);

        listView.setAdapter(adapter);

        reqArts();

        FloatingActionButton fab = view.findViewById(R.id.fab);
        fab.setAlpha(0.65f);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(getActivity(),VillaAddActivity.class);
                startActivity(intent);
            }
        });

        return view;
    }


    public void reqArts() {
        RequestQueue queue = Volley.newRequestQueue(getContext());
        String url = GET_VILLA_URL;
        StringRequest postRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>()
                {
                    @Override
                    public void onResponse(String response) {
                        JSONArray jsonArray= null;
                        swipeRefreshLayout.setRefreshing(false);
                        try {
                            jsonArray = new JSONArray(response);
                            artsFlag=true;
                            JSONObject jsonObject=jsonArray.getJSONObject(0);
                            dataModels.clear();
                            String all="";
                            for (int i=jsonArray.length();i>0;i--) {
                                jsonObject = jsonArray.getJSONObject(i-1);

                                Villa villa=new Villa();
                                villa.setId(jsonObject.getInt("id"));
                                villa.setName(jsonObject.getString("name"));
                                villa.setMemo(jsonObject.getString("memo"));
                                villa.setArea(jsonObject.getString("area"));
                                villa.setPrice(jsonObject.getString("price"));
                                villa.setRoom(jsonObject.getString("room"));
                                villa.setFacility(jsonObject.getString("facility"));
                                villa.setTel(jsonObject.getString("tel"));
                                villa.setAddress(jsonObject.getString("adrs"));
                                villa.setLat(jsonObject.getString("lat"));
                                villa.setLng(jsonObject.getString("lng"));

                                villa.setPic(server+"/assets/images/villas/"+jsonObject.getString("pic"));

                                dataModels.add(villa);

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

    @Override
    public void onRefresh() {
        reqArts();
    }
}
