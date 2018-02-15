package com.idpz.instacity.Profile;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
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
import com.idpz.instacity.Home.LoginActivity;
import com.idpz.instacity.R;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

import javax.net.ssl.HttpsURLConnection;

import de.hdodenhof.circleimageview.CircleImageView;

public class MyProfileActivity extends AppCompatActivity {

    private static final String TAG = "MyProfileChangePicture";
    TextView txtCitizenName, txtCtNumber,txtEduB,txtJobB,txtCtBirth,txtCtMelliid,txtczstatus;
    RadioButton rbZan, rbMard;
    Spinner spEdu,spJob;
    CheckBox chkFav1,chkFav2,chkFav3,chkFav4,chkFav5,chkFav6,chkFav7,chkFav8;
    Button btnreg,btnexit;
    ProgressDialog pd;
    private DisplayImageOptions options;
    CircleImageView imgProfile;
    Boolean userFlag=false,check=true;
    Bitmap bitmap=null;
    String ImageName = "image_name";
    String ImagePath = "image_path";
    ProgressDialog progressDialog;
    private static final int REQUEST_CAMERA = 6;
    private static final int SELECT_FILE = 7;
    String requrl = "",profileImgUrl="";
    String REGISTER_URL="",server="";
    String ServerUploadPath ="";
    String eduTitle="0",jobTitle="0",serial="0.jpg",lat="0",lng="0",melliid="0",myname="0";
    String name="0",pas="", mobile="0", birth="0", gender="0", edu="0", edub="0", job="0", jobb="0", fav="0", money="0";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_profile);

         requrl =  getString(R.string.server)+   "/i/srchprofile.php";
         REGISTER_URL=getString(R.string.server)+"/i/profile.php";
         server=getString(R.string.server);

        txtCitizenName=(TextView)findViewById(R.id.txtCitizenName);
        txtCtNumber=(TextView)findViewById(R.id.txtCtNumber);
        txtCtNumber.setEnabled(false);
        txtEduB=(TextView)findViewById(R.id.txtEduB);
        txtJobB=(TextView)findViewById(R.id.txtJobB);
        txtCtBirth=(TextView)findViewById(R.id.txtCtBirth);
        txtCtMelliid=(TextView)findViewById(R.id.txtCtmelliid);
        txtczstatus = (TextView) findViewById(R.id.txtczStatus);
        rbMard=(RadioButton)findViewById(R.id.rbMard);
        rbZan=(RadioButton)findViewById(R.id.rbZan);
        spEdu=(Spinner) findViewById(R.id.spEdu);
        spJob=(Spinner) findViewById(R.id.spJob);
        chkFav1 = (CheckBox) findViewById(R.id.chkFav1);
        chkFav2 = (CheckBox) findViewById(R.id.chkFav2);
        chkFav3 = (CheckBox) findViewById(R.id.chkFav3);
        chkFav4 = (CheckBox) findViewById(R.id.chkFav4);
        chkFav5 = (CheckBox) findViewById(R.id.chkFav5);
        chkFav6 = (CheckBox) findViewById(R.id.chkFav6);
        chkFav7 = (CheckBox) findViewById(R.id.chkFav7);
        chkFav8 = (CheckBox) findViewById(R.id.chkFav8);
        btnreg=(Button)findViewById(R.id.btnCzReg);
        btnexit=(Button)findViewById(R.id.btnCzExit);
        imgProfile=(CircleImageView)findViewById(R.id.myProfile_photoLikes);
        ServerUploadPath =getString(R.string.server)+"/i/imgprofile.php" ;

        imgProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(
                        Intent.ACTION_PICK,
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(intent,SELECT_FILE);
            }
        });

        SharedPreferences SP1;
        SP1 = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
        myname = SP1.getString("myname", "0");
        mobile = SP1.getString("mobile", "0");
        melliid = SP1.getString("melliid", "0");
        lat=SP1.getString("lat", "0");
        lng=SP1.getString("lng", "0");
        birth=SP1.getString("age", "0");
        pas=SP1.getString("pass", "0");
        txtCtBirth.setText(birth);
        fav=SP1.getString("fav", "0");
        profileImgUrl = SP1.getString("pic", "0");
        Toast.makeText(this, "pic-url"+profileImgUrl, Toast.LENGTH_LONG).show();
        options = new DisplayImageOptions.Builder()
                .showImageOnLoading(R.drawable.ic_stub)
                .showImageForEmptyUri(R.drawable.ic_empty)
                .showImageOnFail(R.drawable.ic_error)
                .cacheInMemory(true)
                .cacheOnDisk(true)
                .considerExifParams(true)
                .bitmapConfig(Bitmap.Config.RGB_565)
                .build();
        ImageLoader.getInstance().displayImage(getString(R.string.server)+"/assets/images/users/"+profileImgUrl,imgProfile,options);
        txtCitizenName.setText(name);
        txtCtNumber.setText(mobile);
        txtCtBirth.setText(birth);
        txtEduB.setText(edub);
        txtJobB.setText(jobb);
        txtczstatus.setText(money);

        if (fav.contains("1"))chkFav1.setChecked(true);
        if (fav.contains("2"))chkFav2.setChecked(true);
        if (fav.contains("3"))chkFav3.setChecked(true);
        if (fav.contains("4"))chkFav4.setChecked(true);
        if (fav.contains("5"))chkFav5.setChecked(true);
        if (fav.contains("6"))chkFav6.setChecked(true);
        if (fav.contains("7"))chkFav7.setChecked(true);

        spEdu.setSelection(Integer.valueOf(edu));
        spJob.setSelection(Integer.valueOf(job));

        gender=SP1.getString("gender", "1");
        if(gender.equals("1")){
            rbMard.setChecked(true);
            gender="1";
        }else {
            gender="0";
            rbZan.setChecked(true);
        }

        txtCitizenName.setText(myname);
        txtCtNumber.setText(mobile);
        txtCtMelliid.setText(melliid);

        reqUserInfo();
        txtczstatus.setText("درخواست اطلاعات از سرور");

        btnexit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                AlertDialog.Builder builder = new AlertDialog.Builder(MyProfileActivity.this);

                builder.setTitle("از حساب کاربری خارج می شوید");
                builder.setMessage("نیاز به ورود مجدد هست آیا مطمئن هستید ?");

                builder.setPositiveButton("خارج می شوم", new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int which) {
                        // Yes selected but close the dialog

                        SharedPreferences.Editor SP2 = PreferenceManager.getDefaultSharedPreferences(getBaseContext()).edit();
                        SP2.putString("myname", "0");
                        SP2.putString("mobile", "0");
                        SP2.putString("melliid", "0");
                        SP2.putString("lat", "0");
                        SP2.putString("lng", "0");
                        SP2.putString("birth", "0");
                        SP2.putString("gen", "0");
                        SP2.putString("fav", "0");
                        SP2.putString("edu", "0");
                        SP2.putString("pic", "0");
                        SP2.putString("numPosts", "0");
                        SP2.putString("edub", "0");
                        SP2.putString("job", "0");
                        SP2.putString("jobb", "0");
                        SP2.apply();
                        finishAffinity();
                        Intent intent=new Intent(MyProfileActivity.this,LoginActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent);
                        dialog.dismiss();
                    }
                });

                builder.setNegativeButton("انصراف", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        // No selected
                        dialog.dismiss();
                    }
                });

                AlertDialog alert = builder.create();
                alert.show();




            }
        });

        btnreg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(rbZan.isChecked()) {
                    gender = "0";
                }else{
                    gender = "1";
                }

                eduTitle=String.valueOf(spEdu.getSelectedItemPosition());
                jobTitle=String.valueOf(spJob.getSelectedItemPosition());
                melliid=txtCtMelliid.getText().toString();
                mobile=txtCtNumber.getText().toString();
                myname=txtCitizenName.getText().toString();
                birth=txtCtBirth.getText().toString();


                fav="0";
                if(chkFav1.isChecked())fav="1";
                if(chkFav2.isChecked())fav=fav+"2";
                if(chkFav3.isChecked())fav=fav+"3";
                if(chkFav4.isChecked())fav=fav+"4";
                if(chkFav5.isChecked())fav=fav+"5";
                if(chkFav6.isChecked())fav=fav+"6";
                if(chkFav7.isChecked())fav=fav+"7";
                if(chkFav8.isChecked())fav=fav+"8";

                if(fav.length()>0&&txtCitizenName.length()>3&&mobile.length()==11&&txtCtBirth.getText().toString().length()==4&&
                        txtJobB.getText().toString().length()>1&&txtEduB.getText().toString().length()>1)
                {
                    money="100";
                }

                SharedPreferences.Editor SP2 = PreferenceManager.getDefaultSharedPreferences(getBaseContext()).edit();
                SP2.putString("myname", myname);
                SP2.putString("mobile", mobile);
                SP2.putString("melliid", melliid);
                SP2.putString("lat", lat);
                SP2.putString("lng", lng);
                SP2.putString("birth", birth);
                SP2.putString("gen", gender);
                SP2.putString("fav", fav);
                SP2.putString("edu", edu);
                SP2.putString("edub", edub);
                SP2.putString("job", job);
                SP2.putString("jobb", jobb);
                SP2.apply();


                Toast.makeText(MyProfileActivity.this, "سرور:"+server+" jobTitle:"+jobTitle, Toast.LENGTH_SHORT).show();
                txtczstatus.setText("درحال ارسال...");
                regUserInfo();
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

                        txtczstatus.setText(response.toString());
                    }
                },
                new Response.ErrorListener()
                {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // TODO Auto-generated method stub
                        Log.d("ERROR","error => "+error.toString());
                        txtczstatus.setText(error.toString()+"مشکلی در ارسال داده پیش آمده دوباره تلاش کنید");
                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String>params = new HashMap<String,String>();

                params.put("name", txtCitizenName.getText().toString());
                params.put("mob", txtCtNumber.getText().toString());
                params.put("pas", pas);
                params.put("birth", txtCtBirth.getText().toString());
                params.put("gen", gender);
                params.put("edu", eduTitle);
                params.put("eb", txtEduB.getText().toString());
                params.put("job", jobTitle);
                params.put("jb", txtJobB.getText().toString());
                params.put("fav", fav);
                params.put("sn", serial);
                params.put("meli",melliid);
                params.put("mny", money);
                params.put("lat", lat);
                params.put("lng", lng);
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


    public void reqUserInfo() {
        RequestQueue queue = Volley.newRequestQueue(this);

        StringRequest postRequest = new StringRequest(Request.Method.POST, requrl,
                new Response.Listener<String>()
                {
                    @Override
                    public void onResponse(String response) {
                        userFlag = true;
//                        pd.dismiss();
                        txtczstatus.setText(response);
                        JSONArray jsonArray = null;
                        try {
                            jsonArray = new JSONArray(response);




                            JSONObject jsonObject = jsonArray.getJSONObject(0);



                            for (int i = 0; i < jsonArray.length(); i++) {
                                jsonObject = jsonArray.getJSONObject(i);

                                name = jsonObject.getString("name");
                                mobile = jsonObject.getString("mobile");
                                birth = jsonObject.getString("birth");
                                gender = jsonObject.getString("gender");
                                edu = jsonObject.getString("edu");
                                edub = jsonObject.getString("edub");
                                job = jsonObject.getString("job");
                                jobb = jsonObject.getString("jobb");
                                fav = jsonObject.getString("fav");
                                money = jsonObject.getString("money");

                                txtCitizenName.setText(name);
                                txtCtNumber.setText(mobile);
                                txtCtBirth.setText(birth);
                                txtEduB.setText(edub);
                                txtJobB.setText(jobb);
                                txtczstatus.setText(money);

                                if (gender.equals("0")){
                                    rbZan.setChecked(true);
                                }else {
                                    rbMard.setChecked(true);
                                }
                                if (fav.contains("1"))chkFav1.setChecked(true);
                                if (fav.contains("2"))chkFav2.setChecked(true);
                                if (fav.contains("3"))chkFav3.setChecked(true);
                                if (fav.contains("4"))chkFav4.setChecked(true);
                                if (fav.contains("5"))chkFav5.setChecked(true);
                                if (fav.contains("6"))chkFav6.setChecked(true);
                                if (fav.contains("7"))chkFav7.setChecked(true);

                                spEdu.setSelection(Integer.valueOf(edu));
                                spJob.setSelection(Integer.valueOf(job));

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

                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String>params = new HashMap<String,String>();
                params.put("mob", mobile);
                params.put("pas", pas);
                return params;
            }
        };
        queue.add(postRequest);

    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_CANCELED) {
            return;
        }
        if (requestCode==SELECT_FILE) {
            if (data != null) {
                Uri contentURI = data.getData();
                try {
                    bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), contentURI);
//                    Log.e("The image", imageToString(bitmap));
                    imgProfile.setImageBitmap(bitmap);
                    ImageUploadToServerFunction();

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        if(requestCode == REQUEST_CAMERA){
            Log.d(TAG, "onActivityResult: done taking a photo.");

             bitmap = (Bitmap) data.getExtras().get("data");
            imgProfile.setImageBitmap(bitmap);
            ImageUploadToServerFunction();
        }
    }



    public void ImageUploadToServerFunction(){


        ByteArrayOutputStream byteArrayOutputStreamObject ;

        byteArrayOutputStreamObject = new ByteArrayOutputStream();

        bitmap.compress(Bitmap.CompressFormat.JPEG, 20, byteArrayOutputStreamObject);

        byte[] byteArrayVar = byteArrayOutputStreamObject.toByteArray();

        final String ConvertImage = Base64.encodeToString(byteArrayVar, Base64.DEFAULT);

        class AsyncTaskUploadClass extends AsyncTask<Void,Void,String> {

            @Override
            protected void onPreExecute() {

                super.onPreExecute();

                progressDialog = ProgressDialog.show(MyProfileActivity.this,"درحال ارسال عکس","کمی صبر کنید",false,false);
            }

            @Override
            protected void onPostExecute(String string1) {

                super.onPostExecute(string1);

                // Dismiss the progress dialog after done uploading.
                progressDialog.dismiss();
                SharedPreferences.Editor SP2 = PreferenceManager.getDefaultSharedPreferences(getBaseContext()).edit();
                SP2.putString("pic", string1);
                SP2.apply();
                // Printing uploading success message coming from server on android app.
                Intent intent =new Intent(MyProfileActivity.this, ProfileActivity.class);
                startActivity(intent);
                finish();
                // Setting image as transparent after done uploading.
//                imageView.setImageResource(android.R.color.transparent);


            }

            @Override
            protected String doInBackground(Void... params) {

                MyProfileActivity.ImageProcessClass imageProcessClass = new MyProfileActivity.ImageProcessClass();

                HashMap<String,String> HashMapParams = new HashMap<String,String>();

                HashMapParams.put(ImageName, mobile);

                HashMapParams.put(ImagePath, ConvertImage);

                HashMapParams.put("mob", mobile);
                HashMapParams.put("pas", pas);


                String FinalData = imageProcessClass.ImageHttpRequest(ServerUploadPath, HashMapParams);

                return FinalData;
            }
        }
        AsyncTaskUploadClass AsyncTaskUploadClassOBJ = new AsyncTaskUploadClass();

        AsyncTaskUploadClassOBJ.execute();
    }

    public class ImageProcessClass{

        public String ImageHttpRequest(String requestURL,HashMap<String, String> PData) {

            StringBuilder stringBuilder = new StringBuilder();

            try {

                URL url;
                HttpURLConnection httpURLConnectionObject ;
                OutputStream OutPutStream;
                BufferedWriter bufferedWriterObject ;
                BufferedReader bufferedReaderObject ;
                int RC ;

                url = new URL(requestURL);

                httpURLConnectionObject = (HttpURLConnection) url.openConnection();

                httpURLConnectionObject.setReadTimeout(19000);

                httpURLConnectionObject.setConnectTimeout(19000);

                httpURLConnectionObject.setRequestMethod("POST");

                httpURLConnectionObject.setDoInput(true);

                httpURLConnectionObject.setDoOutput(true);

                OutPutStream = httpURLConnectionObject.getOutputStream();

                bufferedWriterObject = new BufferedWriter(

                        new OutputStreamWriter(OutPutStream, "UTF-8"));

                bufferedWriterObject.write(bufferedWriterDataFN(PData));

                bufferedWriterObject.flush();

                bufferedWriterObject.close();

                OutPutStream.close();

                RC = httpURLConnectionObject.getResponseCode();

                if (RC == HttpsURLConnection.HTTP_OK) {

                    bufferedReaderObject = new BufferedReader(new InputStreamReader(httpURLConnectionObject.getInputStream()));

                    stringBuilder = new StringBuilder();

                    String RC2;

                    while ((RC2 = bufferedReaderObject.readLine()) != null){

                        stringBuilder.append(RC2);
                    }
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
            return stringBuilder.toString();
        }

        private String bufferedWriterDataFN(HashMap<String, String> HashMapParams) throws UnsupportedEncodingException {

            StringBuilder stringBuilderObject;

            stringBuilderObject = new StringBuilder();

            for (Map.Entry<String, String> KEY : HashMapParams.entrySet()) {

                if (check)

                    check = false;
                else
                    stringBuilderObject.append("&");

                stringBuilderObject.append(URLEncoder.encode(KEY.getKey(), "UTF-8"));

                stringBuilderObject.append("=");

                stringBuilderObject.append(URLEncoder.encode(KEY.getValue(), "UTF-8"));
            }

            return stringBuilderObject.toString();
        }

    }

}
