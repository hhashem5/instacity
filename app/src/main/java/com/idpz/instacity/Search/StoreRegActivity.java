package com.idpz.instacity.Search;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Looper;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.support.design.widget.Snackbar;
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
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.idpz.instacity.R;
import com.idpz.instacity.models.Shop;
import com.idpz.instacity.utils.GPSTracker;
import com.idpz.instacity.utils.ShopAdapter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import static android.Manifest.permission.ACCESS_COARSE_LOCATION;
import static android.Manifest.permission.ACCESS_FINE_LOCATION;

public class StoreRegActivity extends AppCompatActivity implements OnMapReadyCallback,GoogleMap.OnMapClickListener,
        GoogleMap.OnMapLongClickListener, GoogleMap.OnMarkerDragListener {

    private static final int SELECT_FILE = 7;
    private static final String TAG = "ShopRegActivity";
    private static final int REQUEST_ACCESS_LOCATION = 0;
    private static final int REQUEST_CAMERA = 6;
    TextView tvCode;
    private GoogleMap mMap;
    SupportMapFragment mapFragment;
    LatLng shopLoc;
    Boolean showMap=true;
    EditText txtShopName,txtShopOwner,txtShopTel,txtShopMobile,txtShopAddress,txtShopKey;
    Button btnShopPic,btnShopReg,btnFinalShop;
    ImageView imgShop;
    TextView txtJobStatus;
    Spinner spnShopTag;
    String REGISTER_URL="";
    String ServerUploadPath ="",SHOP_URL="";
    String shopName,owner,tel,mobile,address,shopKey,shoptag,lat="0",lng="0",mymobile="";
    View focusView = null;
    boolean kansel=false,check=true,hasImage=false,placesFlag=false;
    Bitmap bitmap=null;
    String ImageName = "image_name";
    String ImagePath = "image_path",server;
    ArrayList<Shop> shops;
    ListView listView;
    ShopAdapter adapter;
    ProgressBar progressBar;
    Uri contentURI;
    SharedPreferences SP1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_store_reg);

        progressBar= findViewById(R.id.progressBar);
        tvCode= findViewById(R.id.storeCode);
        tvCode.setVisibility(View.INVISIBLE);
        txtShopName= findViewById(R.id.txtShopName);
        txtShopOwner= findViewById(R.id.txtShopOwner);
        txtShopTel= findViewById(R.id.txtShopTel);
        txtShopMobile= findViewById(R.id.txtShopMobile);
        txtShopAddress= findViewById(R.id.txtShopAddress);
        txtShopKey= findViewById(R.id.txtShopKey);
//        txtShopMemo=(EditText) findViewById(R.id.txtShopMemo);
        btnShopPic= findViewById(R.id.btnShopPic);
        btnShopReg= findViewById(R.id.btnShopReg);
        btnFinalShop= findViewById(R.id.btnFinalShop);
        imgShop= findViewById(R.id.imgShop);
        txtJobStatus= findViewById(R.id.txtJobStatus);
        spnShopTag= findViewById(R.id.spnShopTag);

        populateGPS();

        SP1 = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
        server=SP1.getString("server", "0");
        mymobile=SP1.getString("mobile", "0");
        lat=SP1.getString("lat","35.711");
        lng=SP1.getString("lng","35.711");

        REGISTER_URL=server+"/i/imgplace.php";
        ServerUploadPath =server+"/i/imgplace.php" ;
        SHOP_URL=server+"/i/shoprec.php";

        btnShopPic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(
                        Intent.ACTION_PICK,
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(intent,SELECT_FILE);
            }
        });

        mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.shop_fragment);
        shopLoc=new LatLng(Double.valueOf(lat),
                Double.valueOf(lng));
        mapFragment.getMapAsync(this);

        if (showMap) {
            mapFragment.getView().setVisibility(View.GONE);
            btnFinalShop.setVisibility(View.GONE);
            showMap=false;
        }


        listView= findViewById(R.id.lvMyShops);
        shops= new ArrayList<>();
        adapter= new ShopAdapter(StoreRegActivity.this,R.layout.shop_row,shops);
        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                btnAdsUpdate.setEnabled(true);
                txtShopName.setText(shops.get(position).getName());
                txtShopOwner.setText(shops.get(position).getOwner());
                txtShopTel.setText(shops.get(position).getTel());
                txtShopMobile.setText(shops.get(position).getMobile());
                txtShopAddress.setText(shops.get(position).getAddress());
                txtShopKey.setText(shops.get(position).getJkey());
                tvCode.setText(""+shops.get(position).getId());
                Log.d(TAG, "onItemClick: id:"+shops.get(position).getId()+" owner:"+shops.get(position).getOwner()+" ");
                String[]tags={"edu","store","health","religion","services","sport","food","tourist"};
                int index =0;
                for (int i=0;i<tags.length;i++) {
                    if (tags[i].equals(shops.get(position).getTag())) {
                        index = i;
                        break;
                    }
                }
                spnShopTag.setSelection(index);

                Glide.with(StoreRegActivity.this).load(shops.get(position).getPic())
                        .thumbnail(0.5f)
                        .crossFade()
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .placeholder(R.drawable.nopic)
                        .into(imgShop);
            }
        });

        btnShopReg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Toast.makeText(StoreRegActivity.this, "lat="+lat+" lng="+lng, Toast.LENGTH_LONG).show();
                shopName=txtShopName.getText().toString();
                owner=txtShopOwner.getText().toString();
                tel=txtShopTel.getText().toString();
                mobile=txtShopMobile.getText().toString();
                address=txtShopAddress.getText().toString();
                shopKey=txtShopKey.getText().toString();
//                memo=txtShopMemo.getText().toString();
                selectTag();

                txtShopName.setError(null);
                txtShopOwner.setError(null);
                txtShopTel.setError(null);
                txtShopMobile.setError(null);
                txtShopAddress.setError(null);
                txtShopKey.setError(null);

//                txtShopMemo.setError(null);


                if(shopName.length()<2){
                    txtShopName.setError("نام کوتاه است");
                    focusView=txtShopName;
                    kansel=true;
                }

                if(owner.length()<2){
                    txtShopOwner.setError("نام کوتاه است");
                    focusView=txtShopOwner;
                    kansel=true;
                }

                if(mobile.length()!=11){
                    txtShopMobile.setError("شماره موبایل 11رقمی است");
                    focusView=txtShopMobile;
                    kansel=true;
                }
                if(tel.length()<4){
                    txtShopTel.setError("شماره ثابت حداقل 4 رقمی است");
                    focusView=txtShopTel;
                    kansel=true;
                }
                if(address.length()<4){
                    txtShopAddress.setError("آدرس کوتاه است");
                    focusView=txtShopAddress;
                    kansel=true;
                }
                if(shopKey.length()<3){
                    txtShopKey.setError("کلید واژه کوتاه است و برای شناسایی بهتر خدمات توسط مردم است");
                    focusView=txtShopKey;
                    kansel=true;
                }


                if (!kansel) {

                        mapFragment.getView().setVisibility(View.VISIBLE);
                        btnFinalShop.setVisibility(View.VISIBLE);
                        btnShopPic.setVisibility(View.INVISIBLE);
                        btnShopReg.setVisibility(View.INVISIBLE);
                        mMap.clear();
                        mMap.addMarker(new MarkerOptions().position(shopLoc).title("موقعیت شما").snippet(shopName+"-"+tel+"("+owner+")"));
                        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(shopLoc,16));
                        showMap = true;



                }else {
                    focusView.requestFocus();
                }




            }
        });


        btnFinalShop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btnShopPic.setVisibility(View.VISIBLE);
                btnShopReg.setVisibility(View.VISIBLE);
                mapFragment.getView().setVisibility(View.GONE);
                btnFinalShop.setVisibility(View.GONE);

                if (hasImage){
                    ServerUploadPath =server+"/i/imgplace.php" ;
                    try {
                        setImage();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }else {
                    regShopInfo();

                }

            }
        });

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
reqMyShop();
//        Toast.makeText(this, mymobile, Toast.LENGTH_SHORT).show();
    }



    public void regShopInfo() {
        RequestQueue queue = Volley.newRequestQueue(this);
        String url = REGISTER_URL;
        StringRequest postRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>()
                {
                    @Override
                    public void onResponse(String response) {

                        txtJobStatus.setText(response);
                        reqMyShop();
                        Toast.makeText(StoreRegActivity.this, "با موفقیت ارسال شد" , Toast.LENGTH_LONG).show();
                    }
                },
                new Response.ErrorListener()
                {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // TODO Auto-generated method stub
                        Log.d("ERROR","error => "+error.toString());
                        txtJobStatus.setText(error.toString()+"مشکلی در ارسال داده پیش آمده دوباره تلاش کنید");
                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String>params = new HashMap<String,String>();

                params.put("name", shopName);
                params.put("mob", mobile);
                params.put("owner", mymobile);
                params.put("oname", owner);
                params.put("tel",tel);
                params.put("adr", address);
                params.put("key", shopKey);
                params.put("tag", shoptag);
                params.put("lat", lat);
                params.put("lng", lng);
//                params.put("memo", memo);
                return params;
            }
        };
        queue.add(postRequest);


    }

    public void onBackPressed() {
        Log.d("CDA", "onBackPressed Called");
        finish();
    }

    @SuppressLint("StaticFieldLeak")
    private void setImage() throws IOException {
    new AsyncTask<Void, Void, Void>() {
        @Override
        protected Void doInBackground(Void... params) {
            Log.d(TAG, "doInBackground: Upload: 1 setimage before glide to bitmap");
            Looper.prepare();
            try {
                bitmap = Glide.
                        with(StoreRegActivity.this).
                        load(contentURI).
                        asBitmap().
                        into(500,500).
                        get();
            } catch (final ExecutionException e) {
                Log.e(TAG, e.getMessage());
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            return null;
        }
        @Override
        protected void onPostExecute(Void dummy) {
            if (null != bitmap) {
                // The full bitmap should be available here
                Log.d(TAG, "doInBackground: Upload: 2  onPostExecute");
                imgShop.setImageBitmap(bitmap);
                uploadImage();
                Log.d(TAG, "Image loaded");
            }
        }
    }.execute();

}



    private void populateGPS() {
        if (!mayRequestLocation()) {
            return;
        }
        GPSTracker gps =new GPSTracker(this);
        lat=gps.getLatitude()+"";
        lng=gps.getLongitude()+"";
        if (!lat.equals("0.0")) {
            SharedPreferences.Editor SP = PreferenceManager.getDefaultSharedPreferences(getBaseContext()).edit();
            lat=gps.getLatitude()+"";
            lng=gps.getLongitude()+"";
            SP.putString("lat", lat);
            SP.putString("lng", lng);
            SP.apply();
            btnShopReg.setTextColor(Color.WHITE);
            btnShopReg.setEnabled(true);
            txtJobStatus.setText("");
            txtJobStatus.setTextColor(Color.BLACK);
        }else{
            btnShopReg.setTextColor(Color.RED);
            btnShopReg.setEnabled(false);
            txtJobStatus.setText("جی چی اس خاموش است امکان ثبت مکان نیست");
            txtJobStatus.setTextColor(Color.RED);
            final CharSequence[] items = {"بله", "خیر"};
            android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(StoreRegActivity.this);
            builder.setTitle("آیا جی پی اس را روشن می کنید");
            builder.setItems(items, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int item) {

                    if (items[item].equals("بله")) {
                        Intent i = new
                                Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                        startActivity(i);
                        dialog.dismiss();
                    } else if (items[item].equals("خیر")) {
                        lat=SP1.getString("lat","35.711");
                        lng=SP1.getString("lng","35.711");
                        dialog.dismiss();
                    }
                }
            });
            builder.show();

        }


        }



    private boolean mayRequestLocation() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            return true;
        }
        if (checkSelfPermission(ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED ||
                checkSelfPermission(ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED   ) {
            return true;
        }
        if (shouldShowRequestPermissionRationale(ACCESS_FINE_LOCATION)) {
            Snackbar.make(btnShopReg, "لطفا دسترسی به جی پی اس را فعال کنید.", Snackbar.LENGTH_INDEFINITE)
                    .setAction(android.R.string.ok, new View.OnClickListener() {
                        @Override
                        @TargetApi(Build.VERSION_CODES.M)
                        public void onClick(View v) {
                            requestPermissions(new String[]{ACCESS_FINE_LOCATION},REQUEST_ACCESS_LOCATION);
                        }
                    });
        } else {
            requestPermissions(new String[]{ACCESS_FINE_LOCATION}, REQUEST_ACCESS_LOCATION);
        }
        return false;
    }

    public void reqMyShop() {
        progressBar.setVisibility(View.VISIBLE);
        RequestQueue queue = Volley.newRequestQueue(this);
        String url = SHOP_URL;
        StringRequest postRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>()
                {
                    @Override
                    public void onResponse(String response) {
                        progressBar.setVisibility(View.GONE);
                        JSONArray jsonArray= null;
                        placesFlag=true;
                        shops.clear();
                        try {

                            jsonArray = new JSONArray(response);

                            JSONObject jsonObject;
//                        dbSocialHandler.removeAll();

                            for (int i=0 ;i<jsonArray.length();i++) {
                                jsonObject=jsonArray.getJSONObject(i);
                                Shop shop=new Shop();
                                shop.setId(jsonObject.getInt("id"));
                                shop.setName(jsonObject.getString("name"));
                                shop.setOwner(jsonObject.getString("ownername")) ;
                                shop.setTel (jsonObject.getString("tel"));
                                shop.setMobile (jsonObject.getString("mobile")) ;
                                shop.setAddress ( jsonObject.getString("address"));
                                shop.setJlat (jsonObject.getString("jlat"));
                                shop.setJlng (jsonObject.getString("jlng"));
                                shop.setTag(jsonObject.getString("tag"));
                                shop.setJkey (jsonObject.getString("jkey"));
                                shop.setPic(server+"/assets/images/shops/"+jsonObject.getString("pic"));
                                shop.setMemo(jsonObject.getString("owner"));
//                                if (value.equals(tag)) {
//                                    shops.add(new Shop(id, name, owner,
//                                            tel, mobile, "", address, jlat, jlng, tag, jkey, "", ""));
//                                    showShops.add(new Shop(id, name, owner,
//                                            tel, mobile, "", address, jlat, jlng, tag, jkey, "", ""));
//                                }
//                                Log.d(TAG, "onResponse: receive lat"+shop.getJlat());
                                shops.add(shop);

                            }
                            adapter.notifyDataSetChanged();


                        } catch (JSONException e) {
                            e.printStackTrace();
                            progressBar.setVisibility(View.GONE);

                        }




                    }
                },
                new Response.ErrorListener()
                {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // TODO Auto-generated method stub

                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String>params = new HashMap<String,String>();
                params.put("owner",mymobile);
                return params;
            }
        };
        queue.add(postRequest);

    }



    @Override
    protected void onPostResume() {
        super.onPostResume();
        populateGPS();
    }



    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_CANCELED) {
            return;
        }
        if (requestCode==SELECT_FILE) {
            if (data != null) {
                contentURI = data.getData();


                    Glide.with(StoreRegActivity.this).load(contentURI)
                            .thumbnail(0.5f)
                            .crossFade()
                            .diskCacheStrategy(DiskCacheStrategy.ALL)
                            .placeholder(R.drawable.nopic)
                            .into(imgShop);
                    hasImage=true;

                    if (tvCode.getText().toString().length()>0){
                        AlertDialog.Builder alertbox = new AlertDialog.Builder(StoreRegActivity.this);
                        alertbox.setMessage("عکس جدید محصول، جایگزین شود");
                        alertbox.setTitle("تغییر عکس محصول");
                        alertbox.setIcon(R.drawable.ic_del);

                        alertbox.setPositiveButton("بله",
                                new DialogInterface.OnClickListener() {

                                    public void onClick(DialogInterface arg0,
                                                        int arg1) {
                                        shopName=txtShopName.getText().toString();
                                        owner=txtShopOwner.getText().toString();
                                        tel=txtShopTel.getText().toString();
                                        mobile=txtShopMobile.getText().toString();
                                        address=txtShopAddress.getText().toString();
                                        shopKey=txtShopKey.getText().toString();
                                        selectTag();
                                        ServerUploadPath=server+"/i/updpic.php";
                                        try {
                                            setImage();
                                        } catch (IOException e) {
                                            e.printStackTrace();
                                        }

                                    }
                                });
                        alertbox.setNegativeButton("خیر",new DialogInterface.OnClickListener() {

                            public void onClick(DialogInterface arg0,
                                                int arg1) {

                            }
                        });
                        alertbox.show();
                    }

            }
        }

    }

    private void uploadImage(){
        Log.d(TAG, "doInBackground: Upload: 3 uploadImage ");
        //Showing the progress dialog
        final ProgressDialog loading = ProgressDialog.show(this,"ارسال ...","لطفا صبر کنید...",false,false);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, ServerUploadPath,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String s) {
                        //Disimissing the progress dialog
                        loading.dismiss();
                        reqMyShop();
                        //Showing toast message of the response
                        Toast.makeText(StoreRegActivity.this, "با موفقیت ارسال شد" , Toast.LENGTH_LONG).show();
                        Log.d(TAG, "doInBackground: Upload: 4 uploadImage Success ");
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        //Dismissing the progress dialog
                        Log.d(TAG, "doInBackground: Upload: 3 On uploadImage error");
                        loading.dismiss();

                        //Showing toast
                        Toast.makeText(StoreRegActivity.this, "خطا در ثبت اطلاعات دوباره تلاش کنید", Toast.LENGTH_LONG).show();
                    }
                }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                //Converting Bitmap to String
                String image = getStringImage(bitmap);
                //Creating parameters
                Map<String,String> params = new Hashtable<String, String>();

                params.put(ImageName, mymobile);
                params.put(ImagePath, image);

                params.put("name", shopName);
                params.put("mob", mobile);
                params.put("owner", mymobile);
                params.put("oname", owner);
                params.put("tel",tel);
                params.put("adr", address);
                params.put("key", shopKey);
                params.put("tag", shoptag);
                params.put("lat", lat);
                params.put("lng", lng);
                params.put("id", tvCode.getText().toString());
                params.put("table", "shops");
                //returning parameters
                Log.d(TAG, "getParams: id:"+tvCode.getText().toString()+" owner="+mymobile);
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
        bmp.compress(Bitmap.CompressFormat.JPEG, 40, baos);
        byte[] imageBytes = baos.toByteArray();
        String encodedImage = Base64.encodeToString(imageBytes, Base64.DEFAULT);
        return encodedImage;

    }

    private void selectTag(){
        int pos=spnShopTag.getSelectedItemPosition();
        switch (pos){
            case 0:
                shoptag="edu";
                break;
            case 1:
                shoptag="store";
                break;
            case 2:
                shoptag="health";
                break;
            case 3:
                shoptag="region";
                break;
            case 4:
                shoptag="services";
                break;
            case 5:
                shoptag="sport";
                break;
            case 6:
                shoptag="food";
                break;
            case 7:
                shoptag="tourist";
                break;
        }
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Add a marker in Sydney, Australia, and move the camera.
        LatLng sydney = new LatLng(Double.valueOf(lat),Double.valueOf(lng));

        mMap.addMarker(new MarkerOptions().position(sydney).title(" موقعیت شما "));
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(sydney,16));
        mMap.setOnMapClickListener(this);
        mMap.setOnMapLongClickListener(this);
        mMap.setOnMarkerDragListener(this);
    }


    @Override
    public void onMapClick(LatLng point) {
//        tvLocInfo.setText(point.toString());
        mMap.clear();
        mMap.animateCamera(CameraUpdateFactory.newLatLng(point));
        mMap.addMarker(new MarkerOptions()
                .position(point)
                .draggable(true));
//        Toast.makeText(getApplicationContext(), "click", Toast.LENGTH_SHORT).show();
//        markerClicked = false;
    }

    @Override
    public void onMapLongClick(LatLng point) {
//        tvLocInfo.setText("New marker added@" + point.toString());
        mMap.clear();
        mMap.addMarker(new MarkerOptions()
                .position(point)
                .draggable(true));
//        Toast.makeText(getApplicationContext(), "Long click", Toast.LENGTH_SHORT).show();
//        markerClicked = false;
    }

    @Override
    public void onMarkerDrag(Marker marker) {
//        tvLocInfo.setText("Marker " + marker.getId() + " Drag@" + marker.getPosition());
//        Toast.makeText(getApplicationContext(), "draged", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onMarkerDragEnd(Marker marker) {
//        tvLocInfo.setText("Marker " + marker.getId() + " DragEnd");
        lat=marker.getPosition().latitude+"";
        lng=marker.getPosition().longitude+"";
//        Toast.makeText(getApplicationContext(), "drag end", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onMarkerDragStart(Marker marker) {
//        tvLocInfo.setText("Marker " + marker.getId() + " DragStart");
//        Toast.makeText(getApplicationContext(), "drag start", Toast.LENGTH_SHORT).show();
    }

}
