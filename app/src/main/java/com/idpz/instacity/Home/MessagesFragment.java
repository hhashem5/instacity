package com.idpz.instacity.Home;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.AbsListView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkError;
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

public class MessagesFragment extends Activity implements SwipeRefreshLayout.OnRefreshListener{
    private SwipeRefreshLayout swipeRefreshLayout;
    private static final String TAG = "MessagesFragment";

        ListView lvContentPost;

        ArrayList<Post> dataModels;
        //    DBLastData dbLastData;
        NewsAdapter newsAdapter;
        String state="",fullServer="",server="";
        int lim1=0,lim2=5;
        public static volatile int netState=3;
        Boolean reqFlag=false,connected=false,refreshFlag=false,remain=true;
        Context context;
        int failCount=0;
        ImageView imgRetry;
        ProgressBar progressBar;
        SharedPreferences SP1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_messages);

            swipeRefreshLayout =(SwipeRefreshLayout) findViewById(R.id.refresh);
            swipeRefreshLayout.setOnRefreshListener(this);
                imgRetry=(ImageView) findViewById(R.id.imgMsgRetry);
            progressBar=(ProgressBar) findViewById(R.id.progressMsg);

            lvContentPost =(ListView) findViewById(R.id.lvMsgContent);

            dataModels = new ArrayList<>();
//        dbLastData = new DBLastData(this);
            newsAdapter = new NewsAdapter(this, dataModels);

            SP1 = PreferenceManager.getDefaultSharedPreferences(this);
            state=SP1.getString("state","0");
            server=SP1.getString("server","0");
            fullServer = getString(R.string.server)+"/j/newsread.php";
//            remain = SP1.getBoolean("connected", false);
            lvContentPost.setAdapter(newsAdapter);

            if (isConnected()) {
                reqPosts();
            }

            imgRetry.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    netState=3;
                    Log.d(TAG, "onClick: Msg");
                    if (isConnected()) {
                        imgRetry.setVisibility(View.GONE);
                        progressBar.setVisibility(View.VISIBLE);
                        reqPosts();
                    }

                }
            });


            lvContentPost.setOnScrollListener(new AbsListView.OnScrollListener() {

                public void onScrollStateChanged(AbsListView view, int scrollState) {


                }

                public void onScroll(AbsListView view, int firstVisibleItem,
                                     int visibleItemCount, int totalItemCount) {

                    if(firstVisibleItem+visibleItemCount == totalItemCount && totalItemCount!=0)
                    {
                        if (reqFlag&isConnected()) {
                            reqFlag=false;
                            lim1 += 5;

                            reqPosts();
                        }
                    }
                }
            });

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        }


    public void reqPosts() {
        RequestQueue queue = Volley.newRequestQueue(MessagesFragment.this);
        String url = fullServer;
        StringRequest postRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>()
                {
                    @Override
                    public void onResponse(String response) {
                        reqFlag=true;
                        imgRetry.setVisibility(View.GONE);
                        progressBar.setVisibility(View.GONE);
                        netState=1;
                        if (refreshFlag){
                            refreshFlag=false;
                            dataModels.clear();
                        }
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
//                                if(pic.equals("null")|| pic.isEmpty())pic="blur.jpg";
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

                            failCount++;
                        }



                    }
                },
                new Response.ErrorListener()
                {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        failCount++;
                        progressBar.setVisibility(View.GONE);
                        imgRetry.setVisibility(View.VISIBLE);
                        Log.d("ERROR","error => "+error.toString()+failCount);
                        if (error instanceof NetworkError) {
                            remain = false;
                        }
                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String>params = new HashMap<String,String>();
                params.put("lim1", String.valueOf(lim1));
                params.put("lim2", String.valueOf(lim2));
                params.put("state", state);
                return params;
            }
        };
        queue.add(postRequest);

    }

    @Override
    public void onRefresh() {
        lim1=0;lim2=5;
//        dataModels.clear();
        refreshFlag=true;
        swipeRefreshLayout.setRefreshing(true);
        reqPosts();
    }

    public boolean isConnected(){
        ConnectivityManager connectivityManager = (ConnectivityManager) MessagesFragment.this.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState() == NetworkInfo.State.CONNECTED ||
                connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState() == NetworkInfo.State.CONNECTED) {
            //we are connected to a network
            connected = true;

            Log.d(TAG, "Msg fr: net State="+netState+" connected="+connected);
            if (netState==0){
                Log.d(TAG, "Msg fr: net State="+netState);
                progressBar.setVisibility(View.GONE);
//                imgRetry.setVisibility(View.VISIBLE);
                connected=false;
                remain=false;
                return false;
            }
            return true;

        } else {
            progressBar.setVisibility(View.GONE);
//            imgRetry.setVisibility(View.VISIBLE);
            return false;
        }
    }


}

