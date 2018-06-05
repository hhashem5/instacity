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
import com.idpz.instacity.models.Arts;
import com.idpz.instacity.utils.ArtAdapter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by h on 2017/12/31.
 */

public class JobFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener{
private SwipeRefreshLayout swipeRefreshLayout;
    private static final String TAG = "jobFragment";


    ArtAdapter adapter;
    ArrayList<Arts> dataModels;
    ListView listView;

    Boolean artsFlag=false,connected=false;
    String GET_ADS_URL="",server="";

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.fragment_job,container,false);
        listView= view.findViewById(R.id.lvJobContent);
//        btnAdsReg=(ImageView) view.findViewById(R.id.imgAddJob);

        swipeRefreshLayout = view.findViewById(R.id.jobRefresh);
        swipeRefreshLayout.setOnRefreshListener(this);


        dataModels= new ArrayList<>();

        listView.setAdapter(adapter);
        SharedPreferences SP1;
        SP1 = PreferenceManager.getDefaultSharedPreferences(getContext());
        server=SP1.getString("server", "0");
        GET_ADS_URL =  server+"/i/getarts.php";
        dataModels= new ArrayList<>();


        adapter= new ArtAdapter(getActivity(),dataModels);

        listView.setAdapter(adapter);

         reqArts();

//        btnAdsReg.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent in =new Intent(getContext(),AddArtActivity.class);
//                getActivity().startActivity(in);
//            }
//        });

        FloatingActionButton fab = view.findViewById(R.id.fab);
        fab.setAlpha(0.65f);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(getActivity(),AddArtActivity.class);
                startActivity(intent);
            }
        });

        return view;
    }

    public void reqArts() {
        RequestQueue queue = Volley.newRequestQueue(getContext());
        String url = GET_ADS_URL;
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

                                Arts area=new Arts();
                                area.setId(jsonObject.getInt("id"));
                                area.setName(jsonObject.getString("name"));
                                area.setMemo(jsonObject.getString("memo"));
                                area.setOwner(jsonObject.getString("owner"));
                                area.setCode(jsonObject.getString("code"));
                                area.setColor(jsonObject.getString("color"));
                                area.setMaterial(jsonObject.getString("material"));
                                area.setPrice(jsonObject.getString("price"));
                                area.setType(jsonObject.getString("type"));
                                area.setWeight(jsonObject.getString("weight"));
                                area.setPic(server+"/assets/images/arts/"+jsonObject.getString("pic"));

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

    @Override
    public void onRefresh() {
        reqArts();
    }

    //`id`, `owner`, `code`, `name`, `type`, `weight`, `material`, `color`, `price`, `memo`, `pic`, `pub`, `stamp
}
