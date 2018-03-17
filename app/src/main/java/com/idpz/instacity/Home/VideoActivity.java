package com.idpz.instacity.Home;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.preference.PreferenceManager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.AbsListView;
import android.widget.ListView;
import android.widget.VideoView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.idpz.instacity.R;
import com.idpz.instacity.models.Video;
import com.idpz.instacity.utils.VideoPostAdapter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class VideoActivity extends AppCompatActivity  implements SwipeRefreshLayout.OnRefreshListener{
    private SwipeRefreshLayout swipeRefreshLayout;
    private static final String TAG = "VideoFragment";
    ListView lvVideoPost;
    ProgressDialog pd;
    ArrayList<Video> dataModels;
    //    DBLastData dbLastData;
    VideoPostAdapter videoPostAdapter;
    String server="",fullServer="";
    int lim1=0,lim2=20;
    Boolean reqVideoFlag =false,connected=false;
    Context context;
    VideoView videoView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video);
        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.refresh);
        swipeRefreshLayout.setOnRefreshListener(this);

        lvVideoPost = (ListView) findViewById(R.id.lvVideoContent);
        pd = new ProgressDialog(this);
        dataModels = new ArrayList<>();
//        dbLastData = new DBLastData(this);
        videoPostAdapter = new VideoPostAdapter(this, dataModels);
        videoView = (VideoView)findViewById(R.id.vidPostVideo);
//        pd.show();
        SharedPreferences SP1;
        SP1 = PreferenceManager.getDefaultSharedPreferences(this);
        server=SP1.getString("server", "0");
        fullServer = server+"/i/videoread.php";
        lvVideoPost.setAdapter(videoPostAdapter);


        new Thread() {
            @Override
            public void run() {
                while (!reqVideoFlag) {
                    VideoActivity.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            ConnectivityManager connectivityManager = (ConnectivityManager) getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
                            if (connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState() == NetworkInfo.State.CONNECTED ||
                                    connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState() == NetworkInfo.State.CONNECTED) {
                                //we are connected to a network
                                connected = true;
                            } else {
                                connected = false;

                            }

                            if (connected && !reqVideoFlag) {
                                pd.setMessage("دریافت اطلاعات...");
                                pd.setCancelable(true);
                                reqVideos();
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



        lvVideoPost.setOnScrollListener(new AbsListView.OnScrollListener() {

            public void onScrollStateChanged(AbsListView view, int scrollState) {


            }

            public void onScroll(AbsListView view, int firstVisibleItem,
                                 int visibleItemCount, int totalItemCount) {

                if(firstVisibleItem+visibleItemCount == totalItemCount && totalItemCount!=0)
                {
                    if (reqVideoFlag) {
                        reqVideoFlag =false;
                        lim1 += 20;

                        reqVideos();
                    }
                }
            }
        });

    }



    public void reqVideos() {
        RequestQueue queue = Volley.newRequestQueue(this);
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

//                                String dtl = jsonObject.getString("ndate");
//                                Calendar mydate = Calendar.getInstance();
//                                mydate.setTimeInMillis(Long.parseLong(dtl+"000"));
//
//                                int myear=mydate.get(Calendar.YEAR);
//                                int mmonth=mydate.get(Calendar.MONTH)+1;
//                                int mday=mydate.get(Calendar.DAY_OF_MONTH);
//                                CalendarTool calt=new CalendarTool(myear,mmonth,mday);
//                                video.setPostDetail(calt.getIranianDate());

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



    @Override
    public void onRefresh() {
        lim1=0;lim2=20;
        dataModels.clear();
        swipeRefreshLayout.setRefreshing(true);
        reqVideos();
    }

}
