package com.idpz.instacity.Home;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
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

/**
 * A login screen that offers login via email/password.
 */
public class LoginActivity extends AppCompatActivity {
    private static final String TAG = "Login activity";
    String requrl="";
    TextView txtInfo;

    String mob="",pas="",server="",ctName="",ctDesc="",ctpic="";
    Boolean areaFlag=false,connected=false;

    ArrayList<Area>areaArrayList=new ArrayList<>();

    private static final String AREA_URL = "http://idpz.ir/i/getarea.php";
    /**
     * Id to identity READ_CONTACTS permission request.
     */
    private static final int REQUEST_READ_CONTACTS = 0;

    /**
     * A dummy authentication store containing known user names and passwords.
     * TODO: remove after connecting to a real authentication system.
     */
    private static final String[] DUMMY_CREDENTIALS = new String[]{
            "foo@example.com:hello", "bar@example.com:world"
    };
    /**
     * Keep track of the login task to ensure we can cancel it if requested.
     */


    // UI references.
    private EditText mEmailView;
    private EditText mPasswordView;
    private View mProgressView;
    private View mLoginFormView;
    Button btnRegister;
    Spinner spCity;
    Float homelat,homelng;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login2);
        // Set up the login form.
        SharedPreferences SP1;
        SP1 = PreferenceManager.getDefaultSharedPreferences(getBaseContext());

        mEmailView = (EditText) findViewById(R.id.txtLoginMobile);
        spCity=(Spinner)findViewById(R.id.spnLoginSelCity);
        txtInfo=(TextView)findViewById(R.id.txtLoginInfo);
        btnRegister=(Button)findViewById(R.id.btn_sign_up_Login) ;
        btnRegister.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(LoginActivity.this,SignupActivity.class);
                startActivity(intent);
            }
        });

        spCity.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                server=areaArrayList.get(position).getServer();
                ctName=areaArrayList.get(position).getAfname();
                homelat=areaArrayList.get(position).getAlat();
                homelng=areaArrayList.get(position).getAlng();
                ctDesc=areaArrayList.get(position).getDescription();
                ctpic=areaArrayList.get(position).getPic();
//                Toast.makeText(LoginActivity.this, "select:"+position+" "+server, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        String mobile = SP1.getString("mobile", "0");
        if (mobile.length()==11)
        {
            Intent intent=new Intent(LoginActivity.this,HomeActivity.class);
            startActivity(intent);
        }

        mPasswordView = (EditText) findViewById(R.id.password);
        mPasswordView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                if (id == R.id.login || id == EditorInfo.IME_NULL) {
                    attemptLogin();
                    return true;
                }
                return false;
            }
        });

        Button mEmailSignInButton = (Button) findViewById(R.id.email_sign_in_button);
        mEmailSignInButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptLogin();
            }
        });

        mLoginFormView = findViewById(R.id.login_form);
        mProgressView = findViewById(R.id.login_progress);



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
                                Toast.makeText(LoginActivity.this, "اینترنت وصل نیست", Toast.LENGTH_SHORT).show();
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
    }





    /**
     * Attempts to sign in or register the account specified by the login form.
     * If there are form errors (invalid email, missing fields, etc.), the
     * errors are presented and no actual login attempt is made.
     */
    private void attemptLogin() {


        // Reset errors.
        mEmailView.setError(null);
        mPasswordView.setError(null);

        // Store values at the time of the login attempt.
        String email = mEmailView.getText().toString();
        String password = mPasswordView.getText().toString();

        boolean cancel = false;
        View focusView = null;

        // Check for a valid password, if the user entered one.
        if (!TextUtils.isEmpty(password) && !isPasswordValid(password)) {
            mPasswordView.setError(getString(R.string.error_invalid_password));
            focusView = mPasswordView;
            cancel = true;
        }

        // Check for a valid email address.
        if (TextUtils.isEmpty(email)) {
            mEmailView.setError(getString(R.string.error_field_required));
            focusView = mEmailView;
            cancel = true;
        } else if (!isEmailValid(email)) {
            mEmailView.setError(getString(R.string.error_invalid_email));
            focusView = mEmailView;
            cancel = true;
        }

        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            focusView.requestFocus();
        } else {
            // Show a progress spinner, and kick off a background task to
            // perform the user login attempt.
            showProgress(true);
            mob=email;pas=password;
            Log.d(TAG, "attemptLogin: requested ");
            reqUserInfo();
        }
    }

    private boolean isEmailValid(String email) {
        //TODO: Replace this with your own logic
        return email.contains("09");
    }

    private boolean isPasswordValid(String password) {
        //TODO: Replace this with your own logic
        return password.length() > 4;
    }

    /**
     * Shows the progress UI and hides the login form.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private void showProgress(final boolean show) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
            mLoginFormView.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
                }
            });

            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mProgressView.animate().setDuration(shortAnimTime).alpha(
                    show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
                }
            });
        } else {
            // The ViewPropertyAnimator APIs are not available, so simply show
            // and hide the relevant UI components.
            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }


    public void reqUserInfo() {
        RequestQueue queue = Volley.newRequestQueue(this);

        StringRequest postRequest = new StringRequest(Request.Method.POST, server+"/i/srchprofile.php",
                new Response.Listener<String>()
                {
                    @Override
                    public void onResponse(String response) {
//                        userFlag = true;
//                        pd.dismiss();
//                        txtczstatus.setText(response);
                        JSONArray jsonArray = null;
                        try {
                            jsonArray = new JSONArray(response);

                            if (response.length()<4){
                                showProgress(false);
                                txtInfo.setText("نام کاربری یا کلمه عبور اشتباه است.");
                            }


                            JSONObject jsonObject = jsonArray.getJSONObject(0);
                            Log.d(TAG, "onResponse: data receive "+response);


                            for (int i = 0; i < jsonArray.length(); i++) {
                                jsonObject = jsonArray.getJSONObject(i);

                                String name = jsonObject.getString("name");
                                String  mobile = jsonObject.getString("mobile");
                                String birth = jsonObject.getString("birth");
                                String gender = jsonObject.getString("gender");
                                String edu = jsonObject.getString("edu");
                                String edub = jsonObject.getString("edub");
                                String job = jsonObject.getString("job");
                                String jobb = jsonObject.getString("jobb");
                                String fav = jsonObject.getString("fav");
                                String money = jsonObject.getString("money");
                                String melliid = jsonObject.getString("melliid");
                                String lat = jsonObject.getString("lat");
                                String lng = jsonObject.getString("lng");
                                String img=jsonObject.getString("pic");
                                if (img.equals("null")||img.isEmpty()||img.length()<4){
                                    img="0.jpg";
                                }
                                String pic= img;

                                SharedPreferences.Editor SP2 = PreferenceManager.getDefaultSharedPreferences(getBaseContext()).edit();
                                SP2.putString("myname", name);
                                SP2.putString("mobile", mobile);
                                SP2.putString("melliid", melliid);
                                SP2.putString("pass", pas);
                                SP2.putString("lat", lat);
                                SP2.putString("lng", lng);
                                SP2.putString("homelat",String.valueOf(homelat));
                                SP2.putString("homelng",String.valueOf(homelng));
                                SP2.putString("server", server);
                                SP2.putString("ctname", ctName);
                                SP2.putString("birth", birth);
                                SP2.putString("gen", gender);
                                SP2.putString("fav", fav);
                                SP2.putString("edu", edu);
                                SP2.putString("edub", edub);
                                SP2.putString("job", job);
                                SP2.putString("jobb", jobb);
                                SP2.putString("pic", pic);
                                SP2.putString("money", money);
                                SP2.putString("ctpic",String.valueOf(ctpic));
                                SP2.putString("ctdesc",String.valueOf(ctDesc));
                                SP2.apply();
                                Intent intent=new Intent(LoginActivity.this,HomeActivity.class);
                                startActivity(intent);
                            }

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
                        txtInfo.setText("شماره موبایل یا کلمه عبور اشتباه است");
                        showProgress(false);
                        if (error.toString().equals("com.android.volley.TimeoutError")){
                            reqUserInfo();
                        }
                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String>params = new HashMap<String,String>();
                params.put("mob", mob);
                params.put("pas", pas);
                return params;
            }
        };
        queue.add(postRequest);

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

                            String all="";
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
                                        (LoginActivity.this, android.R.layout.simple_spinner_item,
                                                cityNames); //selected item will look like a spinner set from XML
                                spinnerArrayAdapter.setDropDownViewResource(android.R.layout
                                        .simple_spinner_dropdown_item);
                                spCity.setAdapter(spinnerArrayAdapter);
                            server=areaArrayList.get(0).getServer();
                            ctName=areaArrayList.get(0).getAfname();







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

