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
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.idpz.instacity.R;
import com.idpz.instacity.models.Villa;
import com.idpz.instacity.utils.MyVillaAdapter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;

public class VillaAddActivity extends AppCompatActivity  implements SwipeRefreshLayout.OnRefreshListener{
    private SwipeRefreshLayout swipeRefreshLayout;
    String ImageName = "image_name",mobile="0";
    String ImagePath = "image_path",GET_VILLA_URL="";
    ProgressDialog progressDialog;
    Bitmap bitmap=null;
    final int SELECT_FILE=1;
    EditText edtName,edtMemo,edtTel,edtAddress,edtArea,edtPrice,edtRoom,edtFacility;
    ImageView imgVilla;
    Button btnSend,btnAdsImgSelect,btnUpdate;
    String REGISTER_URL="",server="",ServerUploadPath="",UpdateVillaPath="";
    String name="",area="",price="",room="",facility="",memo="",tel="",address="";
    View focusView = null;
    Boolean kansel=false,check=true,villaFlag=false,imageFlag=false;
    private Intent intent;
    private String imgUrl,lat,lng;
    MyVillaAdapter adapter;
    ArrayList<Villa> dataModels;
    ListView listView;
    TextView tvCode;
    ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_villa_add);

        progressBar= findViewById(R.id.progressBar);

        swipeRefreshLayout = findViewById(R.id.addVillasRefresh);
        swipeRefreshLayout.setOnRefreshListener(this);

        SharedPreferences SP1;
        SP1 = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
        server=SP1.getString("server", "0");
        lat=SP1.getString("lat", "0");
        lng=SP1.getString("lng", "0");
        mobile=SP1.getString("mobile", "0");
        ServerUploadPath=server+"/i/setvilla.php";
        UpdateVillaPath=server+"/i/updatevilla.php";
        GET_VILLA_URL =  server+"/i/getvillas.php";

        btnUpdate= findViewById(R.id.btnVillaEdit);
        btnUpdate.setVisibility(View.INVISIBLE);
        imgVilla= findViewById(R.id.imgVillaSave);
        btnSend= findViewById(R.id.btnVillaSend);
        btnAdsImgSelect= findViewById(R.id.btnVillaImgSelect);
        edtName= findViewById(R.id.txtVillaName);
        edtMemo= findViewById(R.id.txtVillaMemo);
        edtPrice= findViewById(R.id.txtVillaPrice);
        edtTel= findViewById(R.id.txtVillaTel);
        edtAddress= findViewById(R.id.txtVillaAddress);
        edtArea= findViewById(R.id.txtVillaArea);
        edtRoom= findViewById(R.id.txtVillaRoom);
        edtFacility= findViewById(R.id.txtVillaFacility);
        listView= findViewById(R.id.lvAddVillasContent);
        tvCode= findViewById(R.id.villaCode);

        dataModels= new ArrayList<>();
        adapter= new MyVillaAdapter(VillaAddActivity.this,dataModels,this);
        listView.setAdapter(adapter);

        btnAdsImgSelect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(
                        Intent.ACTION_PICK,
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(intent,SELECT_FILE);
            }
        });

        btnUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                kansel=false;
                name=edtName.getText().toString();
                memo=edtMemo.getText().toString();
                tel=edtTel.getText().toString();
                address=edtAddress.getText().toString();
                area=edtArea.getText().toString();
                room=edtRoom.getText().toString();
                facility=edtFacility.getText().toString();
                price=edtPrice.getText().toString();
                if(name.length()<3){
                    edtName.setError("نام کوتاه است");
                    focusView=edtName;
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


                if (!kansel)
                    updateVilla();
            }
        });

        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                name=edtName.getText().toString();
                memo=edtMemo.getText().toString();
                tel=edtTel.getText().toString();
                address=edtAddress.getText().toString();
                area=edtArea.getText().toString();
                room=edtRoom.getText().toString();
                facility=edtFacility.getText().toString();
                price=edtPrice.getText().toString();
                kansel=false;

                if(name.length()<3){
                    edtName.setError("نام کوتاه است");
                    focusView=edtName;
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
                    Toast.makeText(VillaAddActivity.this, "لطفا یک عکس انتخاب کنید!", Toast.LENGTH_LONG).show();
                    kansel=true;
                }

                if (!kansel){
                    btnSend.setEnabled(false);
                    ServerUploadPath=server+"/i/setvilla.php";
                    uploadImage();}
            }
        });



        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                btnUpdate.setVisibility(View.VISIBLE);
                tvCode.setText(""+dataModels.get(position).getId());
                edtName.setText(dataModels.get(position).getName());
                edtMemo.setText(dataModels.get(position).getMemo());
                edtPrice.setText(dataModels.get(position).getPrice());
                edtAddress.setText(dataModels.get(position).getAddress());
                edtRoom.setText(dataModels.get(position).getRoom());
                edtFacility.setText(dataModels.get(position).getFacility());
                edtTel.setText(dataModels.get(position).getTel());
                edtArea.setText(dataModels.get(position).getArea());

                Glide.with(VillaAddActivity.this).load(dataModels.get(position).getPic())
                        .thumbnail(0.5f)
                        .crossFade()
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .placeholder(R.drawable.nopic)
                        .into(imgVilla);

            }
        });

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        reqVillas();


    }



    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_CANCELED) {
            return;
        }
        if (requestCode==SELECT_FILE) {
            imageFlag=true;
            if (data != null) {
                Uri contentURI = data.getData();
                try {
                    bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), contentURI);
//                    Log.e("The image", imageToString(bitmap));
                    imgVilla.setImageBitmap(bitmap);

                    if (tvCode.getText().toString().length()>0){
                        AlertDialog.Builder alertbox = new AlertDialog.Builder(VillaAddActivity.this);
                        alertbox.setMessage("عکس جدید اقامتگاه، جایگزین شود");
                        alertbox.setTitle("تغییر عکس اقامتگاه");
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
                    public static final String TAG = "add villa upload image";

                    @Override
                    public void onResponse(String s) {
                        btnSend.setEnabled(true);
                        //Disimissing the progress dialog
                        loading.dismiss();
                        reqVillas();
                        //Showing toast message of the response
                        Log.d(TAG, "onResponse: "+s+"  "+tvCode.getText().toString()+" - "+mobile);
//                        Toast.makeText(VillaAddActivity.this, "با موفقیت ارسال شد" , Toast.LENGTH_LONG).show();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        //Dismissing the progress dialog
                        btnSend.setEnabled(true);
                        //Showing toast
//                        Toast.makeText(VillaAddActivity.this, ""+volleyError, Toast.LENGTH_LONG).show();
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
                params.put("area", area);
                params.put("tel", tel);
                params.put("adrs", address);
                params.put("owner", mobile);
                params.put("price", price);
                params.put("room", room);
                params.put("facility", facility);
                params.put("lat", lat);
                params.put("lng", lng);
                params.put("table", "villas");
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

    private void updateVilla(){
        //Showing the progress dialog
        final ProgressDialog loading = ProgressDialog.show(this,"ارسال...","کمی صبرکنید...",false,true);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, UpdateVillaPath,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String s) {
                        //Disimissing the progress dialog
                        btnSend.setEnabled(true);
                        loading.dismiss();
                        reqVillas();
                        //Showing toast message of the response
//                        Toast.makeText(VillaAddActivity.this, "با موفقیت ارسال شد" , Toast.LENGTH_LONG).show();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        //Dismissing the progress dialog
                        btnSend.setEnabled(true);
                        //Showing toast
//                        Toast.makeText(VillaAddActivity.this, ""+volleyError, Toast.LENGTH_LONG).show();
                    }
                }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                //Converting Bitmap to String
                //Creating parameters
                Map<String,String> params = new Hashtable<String, String>();
                params.put("name", name);
                params.put("memo", memo);
                params.put("area", area);
                params.put("tel", tel);
                params.put("adrs", address);
                params.put("owner", mobile);
                params.put("price", price);
                params.put("room", room);
                params.put("facility", facility);
                params.put("lat", lat);
                params.put("lng", lng);
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
    public void reqVillas() {
        progressBar.setVisibility(View.VISIBLE);
        RequestQueue queue = Volley.newRequestQueue(VillaAddActivity.this);
        String url = GET_VILLA_URL;
        StringRequest postRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>()
                {
                    @Override
                    public void onResponse(String response) {
                        progressBar.setVisibility(View.GONE);
                        JSONArray jsonArray= null;
                        if (response.length()<10){
                            dataModels.clear();
                            adapter.notifyDataSetChanged();
                        }
                        swipeRefreshLayout.setRefreshing(false);
                        try {
                            jsonArray = new JSONArray(response);
                            villaFlag=true;
                            JSONObject jsonObject=jsonArray.getJSONObject(0);
                            dataModels.clear();
                            String all="";
                            for (int i=jsonArray.length();i>0;i--) {
                                jsonObject = jsonArray.getJSONObject(i-1);

                                Villa villa=new Villa();
                                villa.setId(jsonObject.getInt("id"));
                                villa.setName(jsonObject.getString("name"));
                                villa.setMemo(jsonObject.getString("memo"));
                                villa.setArea(jsonObject.getString("area"));
                                villa.setPrice(jsonObject.getString("price"));
                                villa.setRoom(jsonObject.getString("room"));
                                villa.setFacility(jsonObject.getString("facility"));
                                villa.setTel(jsonObject.getString("tel"));
                                villa.setAddress(jsonObject.getString("adrs"));
                                villa.setLat(jsonObject.getString("lat"));
                                villa.setLng(jsonObject.getString("lng"));
                                villa.setOwner(jsonObject.getString("owner"));
                                villa.setPic(server+"/assets/images/villas/"+jsonObject.getString("pic"));

                                dataModels.add(villa);

                            }
//                            adapter.notifyDataSetChanged();


                        } catch (JSONException e) {
                            e.printStackTrace();

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
//                        txtczstatus.setText(error.toString()+"مشکلی در ارسال داده پیش آمده دوباره تلاش کنید");

                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String>params = new HashMap<String,String>();

                params.put("owner", mobile);

                return params;
            }
        };
        queue.add(postRequest);
    }

    @Override
    public void onRefresh() {
        swipeRefreshLayout.setRefreshing(true);
        reqVillas();
    }
}
