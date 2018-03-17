package com.idpz.instacity.Home;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.idpz.instacity.Area;
import com.idpz.instacity.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class SignupActivity extends AppCompatActivity {

    EditText txtName,txtMobile,txtPass;
    RadioButton rbMard;
    Button btnReg,btnSignIn;
    String myname,melli,mob,sal,pass,gender,ctName;
    TextView txtmsg;
    String REGISTER_URL="",server="",ctDesc="",ctpic="";
    Boolean kansel=false;
    View focusView = null;
    Boolean areaFlag=false,connected=false;
    Spinner spCity;
    Float homelat,homelng;
    ArrayList<Area> areaArrayList=new ArrayList<>();
    private static final String AREA_URL = "http://idpz.ir/i/getarea.php";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        txtName=(EditText)findViewById(R.id.txtSignupNmae);
        txtMobile=(EditText)findViewById(R.id.txtSignupMobile);
        txtPass=(EditText)findViewById(R.id.txtSignupPass);
        btnReg=(Button) findViewById(R.id.btnSignupReg);
        btnSignIn=(Button) findViewById(R.id.btnSigninReg);
        txtmsg=(TextView) findViewById(R.id.txtSignupMsg);
        rbMard=(RadioButton)findViewById(R.id.rbSignupMard);

        SharedPreferences SP1;
        SP1 = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
        String mobile = SP1.getString("mobile", "0");
        if (mobile.length()==11)
        {
            Intent intent=new Intent(SignupActivity.this,HomeActivity.class);
            startActivity(intent);
            finish();
        }

        spCity=(Spinner)findViewById(R.id.spnLoginSelCity);
        spCity.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                server=areaArrayList.get(position).getServer();
                ctName=areaArrayList.get(position).getAfname();
                homelat=areaArrayList.get(position).getAlat();
                homelng=areaArrayList.get(position).getAlng();
                ctDesc=areaArrayList.get(position).getDescription();
                ctpic=areaArrayList.get(position).getPic();


//                Toast.makeText(SignupActivity.this, "select:"+position+" "+server, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        btnSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(SignupActivity.this,LoginActivity.class);
                startActivity(intent);
                finish();
            }
        });



        new Thread() {
            @Override
            public void run() {
                while (!areaFlag) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
                            if (connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState() == NetworkInfo.State.CONNECTED ||
                                    connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState() == NetworkInfo.State.CONNECTED) {
                                //we are connected to a network
                                connected = true;
                            } else {
                                connected = false;
                                Toast.makeText(SignupActivity.this, "اینترنت وصل نیست", Toast.LENGTH_SHORT).show();
                            }


                            if (connected && !areaFlag) {
                                reqArea();   //یافتن منطقه کاربر و اتصال به سوور هماه منطقه
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

        btnReg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                myname=txtName.getText().toString();
                melli="";
                mob=txtMobile.getText().toString();
                sal="";
                pass=txtPass.getText().toString();

                txtName.setError(null);
                txtPass.setError(null);
                txtMobile.setError(null);
//                txtMelli.setError(null);

                if(myname.length()<2){
                    txtName.setError("نام کوتاه است");
                    focusView=txtName;
                    kansel=true;
                }
                if(mob.length()!=11){
                    txtMobile.setError("شماره موبایل 11رقمی است");
                    focusView=txtMobile;
                    kansel=true;
                }
//                if(melli.length()!=10){
//                    txtMelli.setError("شماره ملی 10 رقمی است");
//                    focusView=txtMelli;
//                    kansel=true;
//                }
//                if(sal.length()!=4){
//                    txtYear.setError("سال تولد 4 رقمی است");
//                    focusView=txtYear;
//                    kansel=true;
//                }
                if(pass.length()<4){
                    txtPass.setError("کلمه عبور حداقل 5 رقم");
                    focusView=txtPass;
                    kansel=true;
                }

                if (rbMard.isChecked()){
                    gender="1";
                }else {
                    gender="0";
                }
                if (!kansel) {
                    regUserInfo();
                }else {
                    focusView.requestFocus();
                }
            }
        });

    }

    public void regUserInfo() {
        RequestQueue queue = Volley.newRequestQueue(this);
        String url = REGISTER_URL;
        StringRequest postRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>()
                {
                    @Override
                    public void onResponse(String response) {

                        txtmsg.setText("با موفقیت ثبت نام شدید");
                        SharedPreferences.Editor SP2 = PreferenceManager.getDefaultSharedPreferences(getBaseContext()).edit();
                        SP2.putString("myname", myname);
                        SP2.putString("mobile", mob);
                        SP2.putString("melliid", melli);
                        SP2.putString("pass", pass);
                        SP2.putString("birth", sal);
                        SP2.putString("server", server);
                        SP2.putString("numPosts", "0");
                        SP2.putString("gen", gender);
                        SP2.putString("ctname", ctName);
                        SP2.putString("homelat",String.valueOf(homelat));
                        SP2.putString("homelng",String.valueOf(homelng));
                        SP2.putString("ctpic",String.valueOf(ctpic));
                        SP2.putString("ctdesc",String.valueOf(ctDesc));
                        SP2.apply();
                        Intent intent=new Intent(SignupActivity.this,HomeActivity.class);
                        startActivity(intent);
                        finish();

                    }
                },
                new Response.ErrorListener()
                {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // TODO Auto-generated method stub
                        Log.d("ERROR","error => "+error.toString());
                        txtmsg.setText(error.toString()+"مشکلی در ارسال داده پیش آمده دوباره تلاش کنید");
                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String>params = new HashMap<String,String>();

                params.put("name", myname);
                params.put("mob", mob);
                params.put("birth", sal);
                params.put("server", server);
                params.put("gen", gender);
                params.put("meli",melli);
                params.put("pas", pass);
                return params;
            }
        };
        queue.add(postRequest);


    }

    public void onBackPressed() {
        Log.d("CDA", "onBackPressed Called");
        finish();
    }

    public void reqArea() {
        RequestQueue queue = Volley.newRequestQueue(this);
        String url = AREA_URL;
        StringRequest postRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>()
                {
                    @Override
                    public void onResponse(String response) {
                        JSONArray jsonArray= null;

                        ArrayList<String> cityNames=new ArrayList<>();
                        try {
                            jsonArray = new JSONArray(response);
                            areaFlag=true;
                            JSONObject jsonObject=jsonArray.getJSONObject(0);

                            for (int i=jsonArray.length();i>0;i--) {
                                jsonObject = jsonArray.getJSONObject(i-1);
                                float myDistance=0;
                                Area area=new Area();
                                area.setId(jsonObject.getInt("aid"));
                                area.setAename(jsonObject.getString("aename"));
                                area.setAfname(jsonObject.getString("afname"));
                                cityNames.add(jsonObject.getString("afname"));
                                area.setAlat(Float.valueOf(jsonObject.getString("alat")));
                                area.setAlng(Float.valueOf(jsonObject.getString("alng")));
                                area.setAdiameter(jsonObject.getInt("adiameter"));
                                area.setServer(jsonObject.getString("server"));
                                area.setZoom(jsonObject.getInt("azoom"));
                                area.setPic(jsonObject.getString("pic"));
                                area.setDescription(jsonObject.getString("memo"));

                                areaArrayList.add(area);
                            }
                            ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<String>
                                    (SignupActivity.this, android.R.layout.simple_spinner_item,
                                            cityNames); //selected item will look like a spinner set from XML
                            spinnerArrayAdapter.setDropDownViewResource(android.R.layout
                                    .simple_spinner_dropdown_item);
                            spCity.setAdapter(spinnerArrayAdapter);
                            server=areaArrayList.get(0).getServer();
                            ctName=areaArrayList.get(0).getAfname();
                            REGISTER_URL=server+"/i/profile.php";







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
