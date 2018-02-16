package com.idpz.instacity.Home;

import android.app.ProgressDialog;
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
import android.widget.AbsListView;
import android.widget.ListView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.idpz.instacity.R;
import com.idpz.instacity.models.Post;
import com.idpz.instacity.utils.CalendarTool;
import com.idpz.instacity.utils.PostAdapter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by h on 2017/12/31.
 */

public class HomeFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener{
    private SwipeRefreshLayout swipeRefreshLayout;
    private static final String TAG = "HomeFragment";

    ListView lvContentPost;
    ProgressDialog pd;
    ArrayList<Post> dataModels;
    //    DBLastData dbLastData;
    PostAdapter postAdapter;
    String server="",fullServer="",mob="0";
    int lim1=0,lim2=20;
    Boolean reqFlag=true,connected=false;
    Context context;
    Boolean postRcvFlag=false,videoRcvFlag=false;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.fragment_home,container,false);
        SharedPreferences SP = PreferenceManager.getDefaultSharedPreferences(getContext());
        mob=SP.getString("mobile", "0");
        swipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.refresh);
        swipeRefreshLayout.setOnRefreshListener(this);



        lvContentPost = (ListView) view.findViewById(R.id.lvHomeContent);
        pd = new ProgressDialog(view.getContext());
        dataModels = new ArrayList<>();
//        dbLastData = new DBLastData(this);
        postAdapter = new PostAdapter(getActivity(), dataModels);


//        server = dbLastData.getLastData(1).getValue();
        fullServer = getString(R.string.server)+"/i/social2.php";
        lvContentPost.setAdapter(postAdapter);

        new Thread() {
            @Override
            public void run() {
                while (!postRcvFlag) {
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
                                Toast.makeText(getActivity().getApplicationContext(), "اینترنت وصل نیست!", Toast.LENGTH_SHORT).show();
                            }

                            if (connected && !postRcvFlag) {
                                pd.setMessage("دریافت اطلاعات...");
                                pd.setCancelable(true);
                                pd.show();
                                reqPosts();
                            }

                        }
                    });
                    try {
                        sleep(5000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }.start();







        lvContentPost.setOnScrollListener(new AbsListView.OnScrollListener() {

            public void onScrollStateChanged(AbsListView view, int scrollState) {


            }

            public void onScroll(AbsListView view, int firstVisibleItem,
                                 int visibleItemCount, int totalItemCount) {

                if(firstVisibleItem+visibleItemCount == totalItemCount && totalItemCount!=0)
                {
                    if (reqFlag) {
                        reqFlag=false;
                        lim1 += 20;

                        reqPosts();
                    }
                }
            }
        });

        return view;
    }




    public void reqPosts() {
        RequestQueue queue = Volley.newRequestQueue(getActivity());
        String url = fullServer;
        StringRequest postRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>()
                {
                    @Override
                    public void onResponse(String response) {
                        reqFlag=true;
                        postRcvFlag=true;
                        pd.dismiss();
                        swipeRefreshLayout.setRefreshing(false);

                        JSONArray jsonArray = null;
                        try {
                            jsonArray = new JSONArray(response);
                            Post post;

                            JSONObject jsonObject = jsonArray.getJSONObject(0);


                            for (int i =0; i<jsonArray.length(); i++) {
                                jsonObject = jsonArray.getJSONObject(i);
                                post=new Post();
                                post.setId(jsonObject.getInt("soid"));
                                post.setUserName(jsonObject.getString("soname"));
                                post.setUserPhone(jsonObject.getString("sophone"));
                                post.setPostComment(jsonObject.getString("sotext"));
                                String anss=jsonObject.getString("soincharge");
                                if(anss.equals("null")|| anss.isEmpty())anss="...";
                                post.setPostAnswer(anss);
                                String lk="0";
                                if (jsonObject.getString("lk").equals("null")||jsonObject.getString("lk").isEmpty()
                                        ||jsonObject.getString("lk").equals("0")){
                                    lk="0";
                                }else {lk="1";}
                                post.setPostLK(lk);
                                String pic=jsonObject.getString("pic");
                                if(pic.equals("null")|| pic.isEmpty())pic="blur.jpg";
                                post.setPostImageUrl(getString(R.string.server)+"/assets/images/137/"+pic);
                                post.setPostLike(jsonObject.getString("seen"));
                                String dtl = jsonObject.getString("sotime");
                                Calendar mydate = Calendar.getInstance();
                                mydate.setTimeInMillis(Long.parseLong(dtl+"000"));

                                int myear=mydate.get(Calendar.YEAR);
                                int mmonth=mydate.get(Calendar.MONTH)+1;
                                int mday=mydate.get(Calendar.DAY_OF_MONTH);
                                CalendarTool calt=new CalendarTool(myear,mmonth,mday);
                                post.setPostDetail(calt.getIranianDate());

                                dataModels.add(post);



                            }
                            postAdapter.notifyDataSetChanged();

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
                params.put("table", "socials");
                params.put("usr", mob);
                params.put("lim1", String.valueOf(lim1));
                params.put("lim2", String.valueOf(lim2));
                return params;
            }
        };
        queue.add(postRequest);

    }


    @Override
    public void onRefresh() {
        lim1=0;lim2=20;
        dataModels = new ArrayList<>();
        swipeRefreshLayout.setRefreshing(true);
        reqPosts();
    }
}

