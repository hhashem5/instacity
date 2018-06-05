package com.idpz.instacity.Home;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Point;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
import com.idpz.instacity.Share.GalleryActivity;
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
//    ProgressDialog pd;
    ArrayList<Post> dataModels;
    //    DBLastData dbLastData;
    PostAdapter postAdapter;
    String server="",fullServer="",mob="0";
    int lim1=0,lim2=5;
    Boolean reqFlag=true,connected=false,remain=true;
    Context context;
    Boolean postRcvFlag=false,videoRcvFlag=false,refreshFlag=false;
    int failCount=0;
    public static volatile int netState=3;
    ImageView imgRetry;
    ProgressBar progressBar;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.fragment_home,container,false);

        SharedPreferences SP = PreferenceManager.getDefaultSharedPreferences(getContext());
        mob=SP.getString("mobile", "0");
        swipeRefreshLayout = view.findViewById(R.id.refresh);
        swipeRefreshLayout.setOnRefreshListener(this);
        imgRetry= view.findViewById(R.id.imgPostRetry);
        progressBar= view.findViewById(R.id.progressPost);

        FloatingActionButton fab = view.findViewById(R.id.fab);
        fab.setAlpha(0.65f);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(getActivity(),GalleryActivity.class);
                startActivity(intent);
            }
        });

        Display display = getActivity().getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        int width = size.x;
        int height = size.y;
        DisplayMetrics metrics = getResources().getDisplayMetrics();
        Log.d(TAG, "onCreateView: height:"+height+" width:"+width+" density:"+metrics.density);

        lvContentPost = view.findViewById(R.id.lvHomeContent);

        dataModels = new ArrayList<>();
//        dbLastData = new DBLastData(this);
        postAdapter = new PostAdapter(getActivity(), dataModels);

        final SharedPreferences SP1;
        SP1 = PreferenceManager.getDefaultSharedPreferences(getContext());
        server=SP1.getString("server", "0");
        fullServer = server+"/i/social2.php";

//        remain = SP1.getBoolean("connected", false);
        lvContentPost.setAdapter(postAdapter);


            if (isConnected()) {
                reqPosts();
            }

        imgRetry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                netState=3;
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
        final StringRequest postRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>()
                {
                    @Override
                    public void onResponse(String response) {
                        progressBar.setVisibility(View.GONE);
                        imgRetry.setVisibility(View.GONE);
                        reqFlag=true;
                        if (!postRcvFlag)dataModels.clear();
                        postRcvFlag=true;

                        if (refreshFlag){
                            refreshFlag=false;
                            dataModels.clear();
                        }

                        swipeRefreshLayout.setRefreshing(false);

                        JSONArray jsonArray = null;
                        try {
                            jsonArray = new JSONArray(response);
                            Post post;
                            Log.d(TAG, "onResponse: socials"+response);
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
                                post.setPostImageUrl(server+"/assets/images/137/"+pic);
                                pic=jsonObject.getString("usrimg");
                                if(pic.equals("null")|| pic.isEmpty())pic="0.jpg";
                                post.setUserImg(getString(R.string.server)+"/assets/images/users/"+pic);
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
                            failCount++;
                        }



                    }
                },
                new Response.ErrorListener()
                {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // TODO Auto-generated method stub
                        failCount++;
                        progressBar.setVisibility(View.GONE);
                        imgRetry.setVisibility(View.VISIBLE);
                        if (error instanceof NetworkError) {
                            remain = false;
                        }
                        Log.d("ERROR","error => "+error.toString()+failCount);
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
        lim1=0;lim2=5;
        refreshFlag=true;
        swipeRefreshLayout.setRefreshing(true);
        reqPosts();
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
                imgRetry.setVisibility(View.VISIBLE);
                connected=false;
                remain=false;
                return false;
            }
            return true;

        } else {
            progressBar.setVisibility(View.GONE);
            imgRetry.setVisibility(View.VISIBLE);
            return false;
        }
    }


}

