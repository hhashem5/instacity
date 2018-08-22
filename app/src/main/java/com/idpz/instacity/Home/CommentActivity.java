package com.idpz.instacity.Home;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
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
import com.idpz.instacity.models.Comment;
import com.idpz.instacity.utils.CalendarTool;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class CommentActivity extends Activity {

    ListView lvComments;
    ProgressDialog pd;
    ArrayList<Comment> dataModels;
    //    DBLastData dbLastData;
    private CommentAdapter commentAdapter;
    String server="",fullServer="",soid="",user="",SocialSend_URL="";
    String myname="",phone="",mtext;
    int lim1=0,lim2=20;
    Boolean reqFlag=true,sendingflag=false;
    ImageView imgSend;
    EditText txtcommentSend;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comment);

        SharedPreferences SP1;
        SP1 = PreferenceManager.getDefaultSharedPreferences(CommentActivity.this);
        server=SP1.getString("server", "0");
        fullServer = getString(R.string.server)+"/j/comment.php";
        SocialSend_URL=getString(R.string.server)+"/j/regcmnt.php";
        imgSend= (ImageView) findViewById(R.id.ivCommentPostSend);
        txtcommentSend=(EditText) findViewById(R.id.txtCommentSend);
        Bundle b = getIntent().getExtras();
        soid = "1"; // or other values
        if(b != null) {
            soid = b.getString("soid");
        }
        SharedPreferences sp1 = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
        myname = sp1.getString("myname", "");
        phone=sp1.getString("mobile", "");
        lvComments =(ListView) findViewById(R.id.lvComments);
        pd = new ProgressDialog(this);
        dataModels = new ArrayList<>();
//        dbLastData = new DBLastData(this);
        commentAdapter = new CommentAdapter(this, dataModels);
        pd.setMessage("دریافت اطلاعات...");
        pd.setCancelable(true);
        pd.show();

        imgSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!sendingflag&&txtcommentSend.getText().toString().length()>0){
                    socialSend();
                    sendingflag = true;
                    mtext=txtcommentSend.getText().toString();
                    txtcommentSend.setText("");
                }

            }
        });

//        server = dbLastData.getLastData(1).getValue();

        lvComments.setAdapter(commentAdapter);
        SharedPreferences.Editor SP = PreferenceManager.getDefaultSharedPreferences(CommentActivity.this).edit();
        SP.putString("tabpos", "0");
        SP.apply();
        reqPosts();

    }



    public void reqPosts() {
        RequestQueue queue = Volley.newRequestQueue(CommentActivity.this);
        String url = fullServer;
        StringRequest postRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>()
                {
                    @Override
                    public void onResponse(String response) {
                        reqFlag=true;
                        pd.dismiss();
                        int count = 0;
                        pd.dismiss();

                        JSONArray jsonArray = null;
                        try {
                            jsonArray = new JSONArray(response);
                            Comment comment;
                            JSONObject jsonObject;
                            count = 0;
                            dataModels.clear();
                            for (int i = jsonArray.length(); i > 0; i--) {
                                jsonObject = jsonArray.getJSONObject(i - 1);
                                comment=new Comment();
                                comment.setId(jsonObject.getInt("soid"));
                                comment.setUsrName(jsonObject.getString("username"));
                                comment.setMycomment(jsonObject.getString("comment"));
//                                comment.setPostImageUrl("http://mscity.ir/assets/images/137/"+jsonObject.getString("pic"));
//                                comment.setPostLike("Likes:"+jsonObject.getString("seen"));
                                String dtl = jsonObject.getString("cmtime");
                                if (dtl.equals("null")||dtl.isEmpty())dtl="0";
                                Calendar mydate = Calendar.getInstance();
                                mydate.setTimeInMillis(Long.parseLong(dtl+"000"));

                                int myear=mydate.get(Calendar.YEAR);
                                int mmonth=mydate.get(Calendar.MONTH)+1;
                                int mday=mydate.get(Calendar.DAY_OF_MONTH);
                                CalendarTool calt=new CalendarTool(myear,mmonth,mday);
                                comment.setCmtime(calt.getIranianDate());

                                dataModels.add(comment);


                                count++;
                            }
                            commentAdapter.notifyDataSetChanged();

                        } catch (JSONException e) {
                            e.printStackTrace();

                        }



                    }
                },
                new Response.ErrorListener()
                {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        //  Auto-generated method stub
                        Log.d("ERROR","error => "+error.toString());
                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String>params = new HashMap<String,String>();
                params.put("id", soid);
                params.put("ph", phone);
                return params;
            }
        };
        queue.add(postRequest);

    }


    public void socialSend() {
//        Toast.makeText(MainActivity.this, " reqcdrv", Toast.LENGTH_SHORT).show();
        RequestQueue queue = Volley.newRequestQueue(CommentActivity.this);
        String url = SocialSend_URL;
        StringRequest postRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>()
                {
                    @Override
                    public void onResponse(String response) {
                        sendingflag=false;
                        reqPosts();
                    }
                },
                new Response.ErrorListener()
                {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        //  Auto-generated method stub
                        Toast.makeText(CommentActivity.this, "پیام ارسال نشد", Toast.LENGTH_SHORT).show();
                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String>params = new HashMap<String,String>();
                params.put("id",soid);
                params.put("phone",phone);
                params.put("name",myname);
                params.put("text",mtext);
                return params;
            }
        };
        queue.add(postRequest);

    }

//    @Override
//    public void onBackPressed() {
//        super.onBackPressed();
//        Intent intent0 = new Intent(CommentActivity.this, HomeActivity.class);
//        intent0.addFlags(FLAG_ACTIVITY_REORDER_TO_FRONT);
//        intent0.putExtra("position", 0); //          137
//        startActivity(intent0);
//
//    }

}
