package com.idpz.instacity.Like;

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
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.idpz.instacity.R;
import com.idpz.instacity.models.Arts;
import com.idpz.instacity.utils.MyArtAdapter;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;

public class AddArtActivity extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener{
    private SwipeRefreshLayout swipeRefreshLayout;

    int artId;
    String ImageName = "image_name",mobile="0";
    String ImagePath = "image_path",GET_ART_URL="",UpdateArtPath="",delArtPath="";
    ProgressDialog progressDialog;
    Bitmap bitmap=null;
    final int SELECT_FILE=1;
    EditText edtName,edtAddress,edtType,edtWeight,edtMat,edtColor,edtPrice;
    ImageView imgAds;
    Button btnSend,btnAdsImgSelect,btnUpdate;
    String REGISTER_URL="",server="",ServerUploadPath="";
    String memo="",name="",type="",weight="",material="",mcolor="",price="";
    View focusView = null;
    Boolean kansel=false,check=true,artsFlag=false;
    MyArtAdapter adapter;
    ArrayList<Arts> dataModels;
    ListView listView;
    TextView tvCode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_art);

        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.addArtsRefresh);
        swipeRefreshLayout.setOnRefreshListener(this);
        SharedPreferences SP1;
        SP1 = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
        server=SP1.getString("server", "0");
        ServerUploadPath=server+"/i/setart.php";
        GET_ART_URL =  server+"/i/getarts.php";
        UpdateArtPath=server+"/i/updateart.php";
        mobile=SP1.getString("mobile", "0");


        imgAds=(ImageView)findViewById(R.id.imgArtSave);
        btnSend=(Button)findViewById(R.id.btnArtSend);
        btnUpdate=(Button)findViewById(R.id.btnArtEdit);
        btnAdsImgSelect=(Button)findViewById(R.id.btnArtImgSelect);
        edtName=(EditText)findViewById(R.id.txtArtName);
        edtType=(EditText)findViewById(R.id.txtArtType);
        edtWeight=(EditText)findViewById(R.id.txtArtWeight);
        edtMat=(EditText)findViewById(R.id.txtArtMat);
        edtColor=(EditText)findViewById(R.id.txtArtColor);
        edtPrice=(EditText)findViewById(R.id.txtArtPrice);
        edtAddress=(EditText)findViewById(R.id.txtArtAddress);
        listView=(ListView)findViewById(R.id.lvArtsContent);
        tvCode=(TextView)findViewById(R.id.artCode);

        dataModels= new ArrayList<>();

        adapter= new MyArtAdapter(AddArtActivity.this,dataModels);

        listView.setAdapter(adapter);

        reqMyArts();
        com.nostra13.universalimageloader.core.ImageLoader.getInstance().init(ImageLoaderConfiguration.createDefault(AddArtActivity.this));
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1,int position, long arg3)
            {
//                Toast.makeText(AddArtActivity.this, "id=" + position, Toast.LENGTH_SHORT).show();
                btnUpdate.setEnabled(true);
                edtName.setText(dataModels.get(position).getName());
                edtType.setText(dataModels.get(position).getType());
                edtWeight.setText(dataModels.get(position).getWeight());
                edtMat.setText(dataModels.get(position).getMaterial());
                edtColor.setText(dataModels.get(position).getColor());
                edtPrice.setText(dataModels.get(position).getPrice());
                edtAddress.setText(dataModels.get(position).getMemo());
                tvCode.setText(""+dataModels.get(position).getId());
                com.nostra13.universalimageloader.core.ImageLoader.getInstance().displayImage(dataModels.get(position).getPic(),imgAds);

            }
        });

        btnUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                kansel=false;
                name=edtName.getText().toString();
                memo=edtAddress.getText().toString();
                type=edtType.getText().toString();
                weight=edtWeight.getText().toString();
                material=edtMat.getText().toString();
                mcolor=edtColor.getText().toString();
                price=edtPrice.getText().toString();

                if(name.length()<3){
                    edtName.setError("نام کوتاه است");
                    focusView=edtName;
                    kansel=true;
                }

                if(memo.length()<5){
                    edtAddress.setError("آدرس کوتاه است");
                    focusView=edtAddress;
                    kansel=true;
                }
                if(price.length()<4){
                    edtPrice.setError("قیمت کامل وارد شود");
                    focusView=edtPrice;
                    kansel=true;
                }
                if(weight.length()<1){
                    edtWeight.setError("وزن کامل وارد شود");
                    focusView=edtWeight;
                    kansel=true;
                }

                if (!kansel)
                    updateArt();
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
                kansel=false;
                name=edtName.getText().toString();
                memo=edtAddress.getText().toString();
                type=edtType.getText().toString();
                weight=edtWeight.getText().toString();
                material=edtMat.getText().toString();
                mcolor=edtColor.getText().toString();
                price=edtPrice.getText().toString();

                if(name.length()<3){
                    edtName.setError("نام کوتاه است");
                    focusView=edtName;
                    kansel=true;
                }

                if(memo.length()<5){
                    edtAddress.setError("آدرس کوتاه است");
                    focusView=edtAddress;
                    kansel=true;
                }
                if(price.length()<4){
                    edtPrice.setError("قیمت کامل وارد شود");
                    focusView=edtPrice;
                    kansel=true;
                }
                if(weight.length()<2){
                    edtWeight.setError("وزن کامل وارد شود");
                    focusView=edtWeight;
                    kansel=true;
                }

                if (!kansel) {
                    ServerUploadPath = server + "/i/setart.php";
                    uploadImage();
                }
            }
        });


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

                    if (tvCode.getText().toString().length()>0){
                        AlertDialog.Builder alertbox = new AlertDialog.Builder(AddArtActivity.this);
                        alertbox.setMessage("عکس جدید محصول، جایگزین شود");
                        alertbox.setTitle("تغییر عکس محصول");
                        alertbox.setIcon(R.drawable.ic_del);

                        alertbox.setPositiveButton("بله",
                                new DialogInterface.OnClickListener() {

                                    public void onClick(DialogInterface arg0,
                                                        int arg1) {
                                        ServerUploadPath=server+"/i/updpic.php";
                                        uploadImage();

                                    }
                                });
                        alertbox.setNegativeButton("خیر",new DialogInterface.OnClickListener() {

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
        final ProgressDialog loading = ProgressDialog.show(this,"ارسال...","کمی صبرکنید...",false,true);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, ServerUploadPath,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String s) {
                        //Disimissing the progress dialog
                        loading.dismiss();
                        //Showing toast message of the response
//                        Toast.makeText(AddArtActivity.this, s , Toast.LENGTH_LONG).show();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        //Dismissing the progress dialog

                        //Showing toast
//                        Toast.makeText(AddArtActivity.this, ""+volleyError, Toast.LENGTH_LONG).show();
                    }
                }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                //Converting Bitmap to String
                String image = getStringImage(bitmap);
                //Creating parameters
                Map<String,String> params = new Hashtable<String, String>();
                params.put(ImageName, mobile);
                params.put(ImagePath, image);
                params.put("name", name);
                params.put("memo", memo);
                params.put("type", type);
                params.put("weight", weight);
                params.put("mat", material);
                params.put("color", mcolor);
                params.put("price", price);
                params.put("owner", mobile);
                params.put("table", "arts");
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

    private void updateArt(){
        //Showing the progress dialog
        final ProgressDialog loading = ProgressDialog.show(this,"ارسال...","کمی صبرکنید...",false,true);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, UpdateArtPath,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String s) {
                        //Disimissing the progress dialog
                        loading.dismiss();
                        //Showing toast message of the response
//                        Toast.makeText(AddArtActivity.this, s , Toast.LENGTH_LONG).show();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        //Dismissing the progress dialog

                        //Showing toast
//                        Toast.makeText(AddArtActivity.this, ""+volleyError, Toast.LENGTH_LONG).show();
                    }
                }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                //Converting Bitmap to String

                //Creating parameters
                Map<String,String> params = new Hashtable<String, String>();
                params.put("name", name);
                params.put("memo", memo);
                params.put("type", type);
                params.put("weight", weight);
                params.put("mat", material);
                params.put("color", mcolor);
                params.put("price", price);
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
        bmp.compress(Bitmap.CompressFormat.JPEG, 30, baos);
        byte[] imageBytes = baos.toByteArray();
        String encodedImage = Base64.encodeToString(imageBytes, Base64.DEFAULT);
        return encodedImage;

    }



    public void reqMyArts() {
        RequestQueue queue = Volley.newRequestQueue(AddArtActivity.this);
        String url = GET_ART_URL;
        StringRequest postRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>()
                {
                    public static final String TAG = "add art";

                    @Override
                    public void onResponse(String response) {
                        JSONArray jsonArray= null;
                        Log.d(TAG, "onResponse: "+response);
                        try {
                            jsonArray = new JSONArray(response);
                            artsFlag=true;
                            swipeRefreshLayout.setRefreshing(false);
                            JSONObject jsonObject=jsonArray.getJSONObject(0);
                            dataModels.clear();
                            String all="";
                            for (int i=jsonArray.length();i>0;i--) {
                                jsonObject = jsonArray.getJSONObject(i-1);

                                Arts area=new Arts();
                                area.setId(jsonObject.getInt("id"));
                                area.setName(jsonObject.getString("name"));
                                area.setMemo(jsonObject.getString("memo"));
                                area.setOwner(jsonObject.getString("owner"));
                                area.setCode(jsonObject.getString("code"));
                                area.setColor(jsonObject.getString("color"));
                                area.setMaterial(jsonObject.getString("material"));
                                area.setPrice(jsonObject.getString("price"));
                                area.setType(jsonObject.getString("type"));
                                area.setWeight(jsonObject.getString("weight"));
                                area.setPic(server+"/assets/images/arts/"+jsonObject.getString("pic"));

                                dataModels.add(area);

                            }
                            adapter.notifyDataSetChanged();


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

                params.put("owner",mobile);

                return params;
            }
        };
        queue.add(postRequest);
    }



    @Override
    public void onRefresh() {
        swipeRefreshLayout.setRefreshing(true);
        reqMyArts();
    }
}
