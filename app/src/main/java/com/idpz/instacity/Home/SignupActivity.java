package com.idpz.instacity.Home;

import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.idpz.instacity.R;

import java.util.HashMap;
import java.util.Map;

public class SignupActivity extends AppCompatActivity {

    EditText txtName,txtMelli,txtMobile,txtYear,txtPass;
    RadioButton rbMard;
    Button btnReg,btnSignIn;
    String myname,melli,mob,sal,pass,gender;
    TextView txtmsg;
    String REGISTER_URL="";
    Boolean kansel=false;
    View focusView = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        REGISTER_URL=getString(R.string.server)+"/i/profile.php";
        txtName=(EditText)findViewById(R.id.txtSignupNmae);
        txtMelli=(EditText)findViewById(R.id.txtSignupMelli);
        txtMobile=(EditText)findViewById(R.id.txtSignupMobile);
        txtYear=(EditText)findViewById(R.id.txtSignupYear);
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


        btnSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(SignupActivity.this,HomeActivity.class);
                startActivity(intent);
                finish();
            }
        });




        btnReg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                myname=txtName.getText().toString();
                melli=txtMelli.getText().toString();
                mob=txtMobile.getText().toString();
                sal=txtYear.getText().toString();
                pass=txtPass.getText().toString();

                txtName.setError(null);
                txtPass.setError(null);
                txtYear.setError(null);
                txtMobile.setError(null);
                txtMelli.setError(null);

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
                if(melli.length()!=10){
                    txtMelli.setError("شماره ملی 10 رقمی است");
                    focusView=txtMelli;
                    kansel=true;
                }
                if(sal.length()!=4){
                    txtYear.setError("سال تولد 4 رقمی است");
                    focusView=txtYear;
                    kansel=true;
                }
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
                        SP2.putString("server", "http://servicefz.ir");
                        SP2.putString("numPosts", "0");
                        SP2.putString("gen", gender);
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
        return;
    }

}
