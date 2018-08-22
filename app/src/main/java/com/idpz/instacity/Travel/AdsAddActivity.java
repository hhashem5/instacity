package com.idpz.instacity.Travel;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.idpz.instacity.R;
import com.idpz.instacity.models.Ads;
import com.idpz.instacity.utils.MyAdsAdapter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;

public class AdsAddActivity extends AppCompatActivity  implements SwipeRefreshLayout.OnRefreshListener{
    private static final String TAG = "AdsAddActivity";
    private SwipeRefreshLayout swipeRefreshLayout;

    String ImageName = "image_name",mobile="0";
    String ImagePath = "image_path";
    ProgressDialog progressDialog;
    Bitmap bitmap=null;
    final int SELECT_FILE=1;
    EditText edtTitle,edtMemo,edtTel,edtAddress;
    TextView tvCode;
    ImageView imgAds;
    Button btnSend,btnAdsImgSelect,btnAdsUpdate;
    String REGISTER_URL="",server="",ServerUploadPath="";
    String GET_ADS_URL="",UpdateAdsPath="",state="";
    String title="",memo="",tel="",address="";
    View focusView = null;
    Boolean kansel=false,check=true;



    ArrayList<Ads> dataModels;
    ListView listView;
    MyAdsAdapter adapter;
    Boolean adsFlag=false,connected=false,imageFlag=false;
    ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ads_add);

        progressBar=(ProgressBar) findViewById(R.id.progressBar);

        swipeRefreshLayout =(SwipeRefreshLayout) findViewById(R.id.addAdsRefresh);
        swipeRefreshLayout.setOnRefreshListener(this);

        SharedPreferences SP1;
        SP1 = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
        server=SP1.getString("server", "0");
        mobile=SP1.getString("mobile", "0");
        state=SP1.getString("state", "0");
        ServerUploadPath=getString(R.string.server)+"/j/setads.php";
        GET_ADS_URL = getString(R.string.server)+"/j/getads.php";
        UpdateAdsPath=getString(R.string.server)+"/j/updateads.php";

        listView=(ListView) findViewById(R.id.lvAdsContent);
        dataModels= new ArrayList<>();
        adapter= new MyAdsAdapter(AdsAddActivity.this,dataModels,this);
        listView.setAdapter(adapter);

        imgAds= (ImageView)findViewById(R.id.imgAdsSave);
        btnSend= (Button)findViewById(R.id.btnAdsSend);
        btnAdsUpdate=(Button) findViewById(R.id.btnAdsEdit);
        btnAdsUpdate.setVisibility(View.INVISIBLE);
        btnAdsImgSelect= (Button)findViewById(R.id.btnAdsImgSelect);
        edtTitle=(EditText) findViewById(R.id.txtAdsTitle);
        edtMemo=(EditText) findViewById(R.id.txtAdsMemo);
        edtTel=(EditText) findViewById(R.id.txtAdsTel);
        edtAddress=(EditText) findViewById(R.id.txtAdsAddress);
        tvCode=(TextView) findViewById(R.id.adsCode);


        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                btnAdsUpdate.setVisibility(View.VISIBLE);
                edtTitle.setText(dataModels.get(position).getTitle());
                edtMemo.setText(dataModels.get(position).getMemo());
                edtTel.setText(dataModels.get(position).getTel());
                edtAddress.setText(dataModels.get(position).getAddress());
                tvCode.setText(""+dataModels.get(position).getId());
                Glide.with(AdsAddActivity.this).load(dataModels.get(position).getPic())
                        .thumbnail(0.5f)

                        .into(imgAds);
            }
        });



        btnAdsUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                kansel=false;
                title=edtTitle.getText().toString();
                memo=edtMemo.getText().toString();
                tel=edtTel.getText().toString();
                address=edtAddress.getText().toString();


                if(title.length()<3){
                    edtTitle.setError("عنوان کوتاه است");
                    focusView=edtTitle;
                    kansel=true;
                }

                if(memo.length()<5){
                    edtMemo.setError("متن آگهی کوتاه است");
                    focusView=edtMemo;
                    kansel=true;
                }
                if(address.length()<5){
                    edtAddress.setError("آدرس کوتاه است");
                    focusView=edtAddress;
                    kansel=true;
                }
                if(tel.length()<4){
                    edtTel.setError("تلفن کامل وارد شود");
                    focusView=edtTel;
                    kansel=true;
                }


                if (!kansel){
                    ServerUploadPath=getString(R.string.server)+"/j/setads.php";
                    Toast.makeText(AdsAddActivity.this, "درحال ثبت تغییرات", Toast.LENGTH_LONG).show();
                    updateAds();

                }

            }
        });

        btnAdsImgSelect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(
                        Intent.ACTION_PICK,
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(intent,SELECT_FILE);
            }
        });

        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                title=edtTitle.getText().toString();
                memo=edtMemo.getText().toString();
                tel=edtTel.getText().toString();
                address=edtAddress.getText().toString();
                kansel=false;
                if(title.length()<4){
                    edtTitle.setError("عنوان کوتاه است");
                    focusView=edtTitle;
                    kansel=true;
                }

                if(memo.length()<5){
                    edtMemo.setError("متن کوتاه است");
                    focusView=edtMemo;
                    kansel=true;
                }
                if(tel.length()<7){
                    edtTel.setError("شماره تلفن کوتاه است");
                    focusView=edtTel;
                    kansel=true;
                }
                if(address.length()<7){
                    edtAddress.setError("آدرس کوتاه است");
                    focusView=edtAddress;
                    kansel=true;
                }
                if (!imageFlag){
                    Toast.makeText(AdsAddActivity.this, "لطفا یک عکس انتخاب کنید!", Toast.LENGTH_LONG).show();
                    kansel=true;
                }
                if (!kansel){
                    Toast.makeText(AdsAddActivity.this, "درحال ثبت اطلاعات", Toast.LENGTH_LONG).show();
                    uploadImage();}
            }
        });
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        reqAds();

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
                    imgAds.setImageBitmap(bitmap);
                    imageFlag=true;
                    if (tvCode.getText().toString().length()>0) {
                        AlertDialog.Builder alertbox = new AlertDialog.Builder(AdsAddActivity.this);
                        alertbox.setMessage("عکس جدید آگهی، جایگزین شود");
                        alertbox.setTitle("تغییر عکس آگهی");
                        alertbox.setIcon(R.drawable.ic_del);

                        alertbox.setPositiveButton("بله",
                                new DialogInterface.OnClickListener() {

                                    public void onClick(DialogInterface arg0,
                                                        int arg1) {
                                        ServerUploadPath = getString(R.string.server)+"/j/updpic.php";
                                        uploadImage();

                                    }
                                });
                        alertbox.setNegativeButton("خیر", new DialogInterface.OnClickListener() {

                            public void onClick(DialogInterface arg0,
                                                int arg1) {

                            }
                        });
                        alertbox.show();
                    }

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

    }


    private void uploadImage(){
        //Showing the progress dialog

        StringRequest stringRequest = new StringRequest(Request.Method.POST, ServerUploadPath,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String s) {
                        //Disimissing the progress dialog
                        reqAds();
                        //Showing toast message of the response
                        Toast.makeText(AdsAddActivity.this, "با موفقیت ارسال شد" , Toast.LENGTH_LONG).show();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        //Dismissing the progress dialog

                        //Showing toast
                        Toast.makeText(AdsAddActivity.this, ""+volleyError, Toast.LENGTH_LONG).show();
                    }
                }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                //Converting Bitmap to String
                String image = getStringImage(bitmap);
                //Getting Image Name

                //Creating parameters
                Map<String,String> params = new Hashtable<String, String>();
                params.put(ImageName, mobile);

                params.put(ImagePath, image);
                params.put("table", "ads");
                params.put("state", state);
                params.put("title", title);
                params.put("memo", memo);
                params.put("tel", tel);
                params.put("adrs", address);
                params.put("owner", mobile);
                params.put("id", tvCode.getText().toString());

                //returning parameters
                return params;
            }
        };

        //Creating a Request Queue
        RequestQueue requestQueue = Volley.newRequestQueue(this);

        //Adding request to the queue
        requestQueue.add(stringRequest);
    }

    private void updateAds(){
        //Showing the progress dialog

        StringRequest stringRequest = new StringRequest(Request.Method.POST, UpdateAdsPath,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String s) {
                        //Disimissing the progress dialog
                        reqAds();
                        //Showing toast message of the response
                        Toast.makeText(AdsAddActivity.this, "با موفقیت ارسال شد" , Toast.LENGTH_LONG).show();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        //Dismissing the progress dialog

                        //Showing toast
//                        Toast.makeText(AdsAddActivity.this, ""+volleyError, Toast.LENGTH_LONG).show();
                    }
                }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                //Converting Bitmap to String


                //Creating parameters
                Map<String,String> params = new Hashtable<String, String>();
                params.put("title", title);
                params.put("memo", memo);
                params.put("tel", tel);
                params.put("adrs", address);
                params.put("owner", mobile);
                params.put("id", tvCode.getText().toString());

                //returning parameters
                return params;
            }
        };

        //Creating a Request Queue
        RequestQueue requestQueue = Volley.newRequestQueue(this);

        //Adding request to the queue
        requestQueue.add(stringRequest);
    }

    public String getStringImage(Bitmap bmp) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.JPEG, 20, baos);
        byte[] imageBytes = baos.toByteArray();
        String encodedImage = Base64.encodeToString(imageBytes, Base64.DEFAULT);
        return encodedImage;

    }

    public void reqAds() {
        progressBar.setVisibility(View.VISIBLE);
        RequestQueue queue = Volley.newRequestQueue(AdsAddActivity.this);
        String url = GET_ADS_URL;
        StringRequest postRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>()
                {
                    @Override
                    public void onResponse(String response) {
                        progressBar.setVisibility(View.GONE);
                        Log.d(TAG, "onResponse: "+response);
                        if (response.length()<10){
                            dataModels.clear();
                            adapter.notifyDataSetChanged();
                        }
                        JSONArray jsonArray= null;
                        swipeRefreshLayout.setRefreshing(false);
                        try {
                            jsonArray = new JSONArray(response);
                            adsFlag=true;
                            JSONObject jsonObject=jsonArray.getJSONObject(0);
                            dataModels.clear();
                            String all="";
                            for (int i=jsonArray.length();i>0;i--) {
                                jsonObject = jsonArray.getJSONObject(i-1);

                                Ads area=new Ads();
                                area.setId(jsonObject.getInt("id"));
                                area.setTitle(jsonObject.getString("title"));
                                area.setMemo(jsonObject.getString("memo"));
                                area.setAddress(jsonObject.getString("address"));
                                area.setTel(jsonObject.getString("tel"));
                                area.setOwner(jsonObject.getString("owner"));
                                area.setPic(server+"/assets/images/ads/"+jsonObject.getString("pic"));

                                dataModels.add(area);

                            }

                            adapter.notifyDataSetChanged();

                        } catch (JSONException e) {
                            e.printStackTrace();
                            Log.d("ERROR","error => "+e.toString());
                        }
                        adapter.notifyDataSetChanged();
                    }
                },
                new Response.ErrorListener()
                {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // TODO Auto-generated method stub
                        Log.d("ERROR","error => "+error.toString());
                        adapter.notifyDataSetChanged();
//                        txtczstatus.setText(error.toString()+"مشکلی در ارسال داده پیش آمده دوباره تلاش کنید");
                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String>params = new HashMap<String,String>();

                params.put("owner",mobile );

                return params;
            }
        };
        queue.add(postRequest);
    }


    @Override
    public void onRefresh() {
        swipeRefreshLayout.setRefreshing(true);
        reqAds();
    }
}
