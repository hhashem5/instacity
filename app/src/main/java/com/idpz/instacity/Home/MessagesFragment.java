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
import com.idpz.instacity.utils.NewsAdapter;

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

public class MessagesFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener{
    private SwipeRefreshLayout swipeRefreshLayout;
    private static final String TAG = "MessagesFragment";



        ListView lvContentPost;
        ProgressDialog pd;
        ArrayList<Post> dataModels;
        //    DBLastData dbLastData;
        NewsAdapter newsAdapter;
        String server="",fullServer="";
        int lim1=0,lim2=20;
        Boolean reqFlag=false,connected=false;
        Context context;

        @Nullable
        @Override
        public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
            View view=inflater.inflate(R.layout.fragment_messages,container,false);

            swipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.refresh);
            swipeRefreshLayout.setOnRefreshListener(this);

            lvContentPost = (ListView) view.findViewById(R.id.lvMsgContent);
            pd = new ProgressDialog(view.getContext());
            dataModels = new ArrayList<>();
//        dbLastData = new DBLastData(this);
            newsAdapter = new NewsAdapter(getActivity(), dataModels);


            SharedPreferences SP1;
            SP1 = PreferenceManager.getDefaultSharedPreferences(getContext());
            server=SP1.getString("server","0");
            fullServer = server+"/i/newsread.php";
            lvContentPost.setAdapter(newsAdapter);


            new Thread() {
                @Override
                public void run() {
                    while (!reqFlag) {
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

                                }

                                if (connected && !reqFlag) {
                                    pd.setMessage("دریافت اطلاعات...");
                                    pd.setCancelable(true);
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
                        pd.dismiss();
                        Log.d(TAG, "onResponse: MSG received");
                        swipeRefreshLayout.setRefreshing(false);
                        JSONArray jsonArray = null;
                        try {
                            jsonArray = new JSONArray(response);
                            Post post;
                            JSONObject jsonObject = jsonArray.getJSONObject(0);


                            for (int i =0;i< jsonArray.length(); i++) {
                                jsonObject = jsonArray.getJSONObject(i);
                                post=new Post();
                                post.setId(jsonObject.getInt("id"));
                                post.setUserName(jsonObject.getString("sender"));
                                post.setUserPhone(jsonObject.getString("title"));
                                post.setPostComment(jsonObject.getString("body"));
                                String pic=jsonObject.getString("pic");
                                if(pic.equals("null")|| pic.isEmpty())pic="blur.jpg";
                                post.setPostImageUrl(server+"/assets/images/news/"+pic);

                                String dtl = jsonObject.getString("ndate");
                                Calendar mydate = Calendar.getInstance();
                                mydate.setTimeInMillis(Long.parseLong(dtl+"000"));

                                int myear=mydate.get(Calendar.YEAR);
                                int mmonth=mydate.get(Calendar.MONTH)+1;
                                int mday=mydate.get(Calendar.DAY_OF_MONTH);
                                CalendarTool calt=new CalendarTool(myear,mmonth,mday);
                                post.setPostDetail(calt.getIranianDate());

                                dataModels.add(post);



                            }
                            newsAdapter.notifyDataSetChanged();

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

    @Override
    public void onRefresh() {
        lim1=0;lim2=20;
        dataModels.clear();
        swipeRefreshLayout.setRefreshing(true);
        reqPosts();
    }

}

