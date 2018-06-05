package com.idpz.instacity.Home;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.ListView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.idpz.instacity.R;
import com.idpz.instacity.models.Comment;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class LikersActivity extends AppCompatActivity {

    ListView lvLikers;
    ProgressDialog pd;
    ArrayList<Comment> dataModels;
    //    DBLastData dbLastData;
    private LikersAdapter likersAdapter;
    String server="",fullServer="",soid="",user="";
    String myname="",phone="",mtext;
    int lim1=0,lim2=20;
    Boolean reqFlag=false,sendingflag=false,connected=false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_likers);


        SharedPreferences SP1;
        SP1 = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
        server=SP1.getString("server", "0");
        fullServer = server+"/i/likers.php";


        Bundle b = getIntent().getExtras();
        soid = "1"; // or other values
        if(b != null) {
            soid = b.getString("soid");
        }
        SharedPreferences sp1 = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
        myname = sp1.getString("myname", "");
        phone=sp1.getString("mobile", "");
        lvLikers = findViewById(R.id.lvLikers);
        pd = new ProgressDialog(this);
        dataModels = new ArrayList<>();
//        dbLastData = new DBLastData(this);
        likersAdapter = new LikersAdapter(this, dataModels);
        pd.setMessage("دریافت اطلاعات...");
        pd.setCancelable(true);
        pd.show();

        SharedPreferences.Editor SP = PreferenceManager.getDefaultSharedPreferences(this).edit();
        SP.putString("tabpos", "0");
        SP.apply();

        lvLikers.setAdapter(likersAdapter);
//        Toast.makeText(this, fullServer, Toast.LENGTH_SHORT).show();


        new Thread() {
            @Override
            public void run() {
                while (!reqFlag) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
                            //we are connected to a network
//                                txtNews.setText("اینترنت وصل نیست");
                            connected = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState() == NetworkInfo.State.CONNECTED ||
                                    connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState() == NetworkInfo.State.CONNECTED;


                            if (!reqFlag&&connected)
                                reqPosts();  // save user info on server
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


    }



    public void reqPosts() {
        RequestQueue queue = Volley.newRequestQueue(this);
        String url = fullServer;
        StringRequest postRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>()
                {
                    @Override
                    public void onResponse(String response) {
                        reqFlag=true;
                        pd.dismiss();

                        pd.dismiss();

                        JSONArray jsonArray = null;
                        try {
                            jsonArray = new JSONArray(response);
                            Comment comment;
                            JSONObject jsonObject;

                            dataModels.clear();
                            for (int i = jsonArray.length(); i > 0; i--) {
                                jsonObject = jsonArray.getJSONObject(i - 1);
                                comment=new Comment();

                                comment.setUsrName(jsonObject.getString("name"));
                                String pic=jsonObject.getString("pic");
                                if(pic.equals("null")|| pic.isEmpty())pic="0.jpg";
                                comment.setMycomment(getString(R.string.server)+"/assets/images/users/"+pic);

                                dataModels.add(comment);



                            }
                            likersAdapter.notifyDataSetChanged();

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
                params.put("id", soid);
                return params;
            }
        };
        queue.add(postRequest);

    }


}
