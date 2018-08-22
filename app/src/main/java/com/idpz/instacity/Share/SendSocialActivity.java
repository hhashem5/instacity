package com.idpz.instacity.Share;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.provider.MediaStore;

import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;

import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.Spinner;

import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.androidquery.AQuery;

import com.bumptech.glide.Glide;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import com.idpz.instacity.R;

import com.idpz.instacity.models.Govcat;

import com.idpz.instacity.utils.AndyUtils;
import com.idpz.instacity.utils.CustomTextView;
import com.idpz.instacity.utils.ParseContent;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import java.util.Hashtable;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ExecutionException;

import static android.Manifest.permission.ACCESS_FINE_LOCATION;
import static android.Manifest.permission.CAMERA;

public class SendSocialActivity extends AppCompatActivity implements OnMapReadyCallback,GoogleMap.OnMapClickListener,
        GoogleMap.OnMapLongClickListener, GoogleMap.OnMarkerDragListener  {

    private static final String TAG = "activity_send_social";
    String mstatus="",receiver="",receiverName="فرهنگی",msfa="تشکر",
            msg="",lat="0",lng="0",ServerUploadPath ="",mobile=""
            ,aename="",afname="",state="",myname="",path="";
    Bitmap bitmap;
    private String image_name="",encoded_string="";
    private File file;
    private Uri file_Uri;
    String ImageName = "image_name";
    String ImagePath = "image_path";
    ArrayList<String> ctNames=new ArrayList<>();
    Spinner spnReceivers;
    Boolean kansel=false,showMap=true,
            connected=false,remain=true,hasImage=false,fileTransferOK=false;
    CustomTextView tvPreview;
    EditText edtMessage;
    SupportMapFragment mapFragment;
    ArrayList<Govcat>govs=new ArrayList<>();
    LatLng shopLoc;
    private GoogleMap mMap;
    View focusView = null;
    String REGISTER_URL="";
    ArrayAdapter<String> spinnerArrayAdapter;
    ProgressBar progressBar;
    ImageView imgRetry,imgSoial;
    LinearLayout mapLayout;
    int netState=0, qlty =40;
    Uri contentURI;
    private static final int PERMISSION_REQUEST_CODE = 200;
    private View view;
    private static final int SELECT_FILE = 7;
    private static final int CAMERA_REQUEST = 1888;
    private final int GALLERY = 1;
    private ParseContent parseContent;
    private AQuery aQuery;
    Button btnSend,btnOk,btnGallery,btnCamera;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send_social);
// wigets
        REGISTER_URL=getString(R.string.server)+"/j/getdata.php";

        tvPreview=(CustomTextView)findViewById(R.id.txtSocialPre);
        btnSend=(Button)findViewById(R.id.btnSocialSend);
        btnOk=(Button)findViewById(R.id.btnFinalSocial);
        btnGallery=(Button)findViewById(R.id.btnGallery);
        btnCamera=(Button)findViewById(R.id.btnCamera);
        mapLayout=(LinearLayout) findViewById(R.id.mapSocial);

        edtMessage=(EditText)findViewById(R.id.edtSocialSend);
        progressBar=(ProgressBar) findViewById(R.id.progressSendSocial);
        imgRetry=(ImageView) findViewById(R.id.imgMsgRetry);
        imgSoial=(ImageView) findViewById(R.id.imgSocialImageSend);

        SharedPreferences SP = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
        mobile=SP.getString("mobile", "0");
        lat=SP.getString("lat", "0");
        lng=SP.getString("lng", "0");
        aename=SP.getString("aename", "0");
        afname=SP.getString("ctname", "0");
        state=SP.getString("state", "050001");
        myname=SP.getString("myname", "-");
        ServerUploadPath =getString(R.string.server)+"/j/img3.php" ;

        mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.social_fragment);
        shopLoc=new LatLng(Double.valueOf(lat),
                Double.valueOf(lng));
        mapFragment.getMapAsync(this);

        if (showMap) {
           mapLayout.setVisibility(View.GONE);

            showMap=false;
        }


// msg type Dialog
        final Dialog dialog = new Dialog(SendSocialActivity.this);
        dialog.setContentView(R.layout.opinion_popup);
        dialog.setTitle("نوع پیام");
        dialog.setCancelable(true);
        // there are a lot of settings, for dialog, check them all out!
        // set up radiobutton
        final RadioButton rd0 = dialog.findViewById(R.id.rd_0);
        final RadioButton rd1 = dialog.findViewById(R.id.rd_1);
        final RadioButton rd2 = dialog.findViewById(R.id.rd_2);
        final RadioButton rd3 = dialog.findViewById(R.id.rd_3);
        Button btnDialog= dialog.findViewById(R.id.btnOpinion);
        rd0.setSelected(true);
        dialog.show();

        rd0.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (rd0.isChecked())
                    msfa="پیشنهاد";
                tvPreview.setText(" ارسال پیام "+msfa+" به "+receiverName+" "+afname);
                    mstatus="1";
            }
        });
        rd1.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (rd1.isChecked())
                    msfa="ایده";
                tvPreview.setText(" ارسال پیام "+msfa+" به "+receiverName+" "+afname);
                    mstatus="2";
            }
        });
        rd2.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (rd2.isChecked())
                    msfa="انتقاد";
                tvPreview.setText(" ارسال پیام "+msfa+" به "+receiverName+" "+afname);
                    mstatus="3";
            }
        });
        rd3.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (rd3.isChecked())
                    msfa="شکایت";
                tvPreview.setText(" ارسال پیام "+msfa+" به "+receiverName+" "+afname);
                    mstatus="4";
            }
        });

        btnDialog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
// end msg type Dialog

// Spinner define

        spnReceivers=(Spinner) findViewById(R.id.spnSocialReceiver);

         spinnerArrayAdapter = new ArrayAdapter<String>
                (SendSocialActivity.this, android.R.layout.simple_spinner_item,
                        ctNames); //selected item will look like a spinner set from XML
        spinnerArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spnReceivers.setAdapter(spinnerArrayAdapter);

        spnReceivers.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Toast.makeText(SendSocialActivity.this, ctNames.get(position), Toast.LENGTH_SHORT).show();
                receiver=position+"";
                receiverName=govs.get(position).getName();
                tvPreview.setText(" ارسال پیام "+msfa+" به "+govs.get(position).getName()+" "+afname);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
// end spinner define

        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Toast.makeText(StoreRegActivity.this, "lat="+lat+" lng="+lng, Toast.LENGTH_LONG).show();
//                if (isConnected()) {
                    kansel = false;
                    msg = edtMessage.getText().toString();
                    edtMessage.setError(null);


                    if (msg.length() < 3) {
                        edtMessage.setError("متن پیام  کوتاه است");
                        focusView = edtMessage;
                        kansel = true;
                    }


                    if (!kansel) {

                        mapLayout.setVisibility(View.VISIBLE);
                        btnSend.setVisibility(View.INVISIBLE);

                        mMap.clear();
                        mMap.addMarker(new MarkerOptions().position(shopLoc).title("موقعیت شما").snippet("برای تغییر روی موقعیت دیگری کلیک کنید"));
                        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(shopLoc, 16));
                        showMap = true;


                    } else {
                        focusView.requestFocus();
                    }
//                }else {
//                    Toast.makeText(SendSocialActivity.this, "اینترنت وصل نیست. لطفا بعد از اتصال؛ دوباره تلاش کنید", Toast.LENGTH_SHORT).show();
//                }



            }
        });

        imgRetry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Log.d(TAG, "onClick: Msg");
                if (isConnected()) {
                    imgRetry.setVisibility(View.GONE);
                    progressBar.setVisibility(View.VISIBLE);
                    reqCat();
                }

            }
        });

        btnGallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(
                        Intent.ACTION_PICK,
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(intent,SELECT_FILE);
            }
        });

        btnCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (checkPermission()){
                    Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    getFileUri();
                    cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT,file_Uri);
                    startActivityForResult(cameraIntent, CAMERA_REQUEST);
                }else {
                    requestPermission();
                }

            }
        });

        btnOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mapLayout.setVisibility(View.INVISIBLE);
                btnSend.setVisibility(View.VISIBLE);
                if (AndyUtils.isNetworkAvailable(SendSocialActivity.this)){
                    uploadImage();
                }else {
                    Toast.makeText(SendSocialActivity.this, "اینترنت وصل نیست. لطفا بعد از اتصال، دوباره تلاش کنید", Toast.LENGTH_SHORT).show();
                }


            }
        });


        reqCat();
    }



    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_CANCELED) {
            return;
        }
        if (requestCode==SELECT_FILE) {
            if (data != null) {
                file_Uri = data.getData();

//                    bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), contentURI);
//                    Log.e("The image", imageToString(bitmap));
//                    imgSoial.setImageBitmap(bitmap);
                    qlty =35;
                    new Encode_image().execute();
//                    imageFlag=true;

            }

        }
        if (requestCode==CAMERA_REQUEST ){
            qlty =30;
            new Encode_image().execute();
        }

    }


    private void uploadImage(){
        //Showing the progress dialog
//        progressBar.setVisibility(View.VISIBLE);
        AndyUtils.showSimpleProgressDialog(this,"درحال ارسال","کمی صبر کنید",true);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, ServerUploadPath,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String s) {
                        //Disimissing the progress dialog
//                        progressBar.setVisibility(View.GONE);
                        AndyUtils.removeSimpleProgressDialog();
                        //Showing toast message of the response
                        finish();
//                        Toast.makeText(SendSocialActivity.this, "با موفقیت ارسال شد"+s , Toast.LENGTH_LONG).show();
                        Intent i =new Intent(SendSocialActivity.this,PopupActivity.class);
                        startActivity(i);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        //Dismissing the progress dialog
                        AndyUtils.removeSimpleProgressDialog();
                        //Showing toast
                        Toast.makeText(SendSocialActivity.this, "پیام ارسال نشد.دوباره تلاش کنید ", Toast.LENGTH_LONG).show();
                    }
                }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                //Converting Bitmap to String
//                String image = getStringImage(bitmap);
                //Getting Image Name

                //Creating parameters
                Map<String,String> params = new Hashtable<String, String>();
                params.put(ImageName, mobile);
                params.put(ImagePath, encoded_string);

                params.put("status", mstatus);
                params.put("gov", receiver);
                params.put("ae", aename);
                params.put("state", state);
                params.put("lat", lat);
                params.put("lng", lng);
                params.put("name", myname);
                params.put("phone", mobile);
                params.put("text", msg);

                //returning parameters
                return params;
            }
        };

        //Creating a Request Queue
        RequestQueue requestQueue = Volley.newRequestQueue(this);

        //Adding request to the queue
        requestQueue.add(stringRequest);
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
        lat=String.valueOf(point.latitude);
        lng=String.valueOf(point.longitude);

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
        lat=String.valueOf(point.latitude);
        lng=String.valueOf(point.longitude);
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



    public void reqCat() {
        RequestQueue queue = Volley.newRequestQueue(this);
        String url = REGISTER_URL;
        StringRequest postRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>()
                {
                    @Override
                    public void onResponse(String response) {
                        JSONArray jsonArray= null;
                        progressBar.setVisibility(View.GONE);
                        try {

                            jsonArray = new JSONArray(response);
                            JSONObject jsonObject;
//                        dbSocialHandler.removeAll();
                            for (int i=0 ;i<jsonArray.length();i++) {
                                jsonObject=jsonArray.getJSONObject(i);
                                Govcat gov=new Govcat();
                                gov.setId(jsonObject.getInt("id"));
                                gov.setName(jsonObject.getString("name"));
                                gov.setCode(jsonObject.getString("code"));
                                ctNames.add(jsonObject.getString("name"));
                                govs.add(gov);

                            }
                            spinnerArrayAdapter.notifyDataSetChanged();


                        } catch (JSONException e) {
                            e.printStackTrace();
                            progressBar.setVisibility(View.GONE);
                            imgRetry.setVisibility(View.VISIBLE);
                        }

                    }
                },
                new Response.ErrorListener()
                {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // TODO Auto-generated method stub
                        Log.d("ERROR","error => "+error.toString());
                        imgRetry.setVisibility(View.VISIBLE);
                        progressBar.setVisibility(View.GONE);
                        tvPreview.setText("مشکلی در دریافت داده پیش آمده اینترنت را بررسی کنید");
                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String>params = new HashMap<String,String>();

                params.put("state", "1");
                params.put("db", "govcats");

                return params;
            }
        };
        queue.add(postRequest);


    }



    public boolean isConnected(){
        ConnectivityManager connectivityManager = (ConnectivityManager) SendSocialActivity.this.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState() == NetworkInfo.State.CONNECTED ||
                connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState() == NetworkInfo.State.CONNECTED) {
            //we are connected to a network
            connected = true;

            Log.d(TAG, "Msg fr: net State="+netState+" connected="+connected);
            if (netState==0){
                Log.d(TAG, "Msg fr: net State="+netState);
                progressBar.setVisibility(View.GONE);
//                imgRetry.setVisibility(View.VISIBLE);
                connected=false;
                remain=false;
                return false;
            }
            return true;

        } else {
            progressBar.setVisibility(View.GONE);
//            imgRetry.setVisibility(View.VISIBLE);
            return false;
        }
    }

    private boolean checkPermission() {
        int result = ContextCompat.checkSelfPermission(getApplicationContext(), ACCESS_FINE_LOCATION);
        int result1 = ContextCompat.checkSelfPermission(getApplicationContext(), CAMERA);

        return result == PackageManager.PERMISSION_GRANTED && result1 == PackageManager.PERMISSION_GRANTED;
    }

    private void requestPermission() {

        ActivityCompat.requestPermissions(this, new String[]{ACCESS_FINE_LOCATION, CAMERA}, PERMISSION_REQUEST_CODE);

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_REQUEST_CODE:
                if (grantResults.length > 0) {

                    boolean locationAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                    boolean cameraAccepted = grantResults[1] == PackageManager.PERMISSION_GRANTED;

                    if (locationAccepted && cameraAccepted)
                        Toast.makeText(this, "دسترسی به دوربین تایید شد!", Toast.LENGTH_SHORT).show();
                    else {
                        Toast.makeText(this, "دسترسی به دوربین رد شد!", Toast.LENGTH_SHORT).show();


                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                            if (shouldShowRequestPermissionRationale(ACCESS_FINE_LOCATION)) {
                                showMessageOKCancel("You need to allow access to both the permissions",
                                        new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                                    requestPermissions(new String[]{ACCESS_FINE_LOCATION, CAMERA},
                                                            PERMISSION_REQUEST_CODE);
                                                }
                                            }
                                        });
                                return;
                            }
                        }

                    }
                }


                break;
        }
    }


    private void showMessageOKCancel(String message, DialogInterface.OnClickListener okListener) {
        new AlertDialog.Builder(SendSocialActivity.this)
                .setMessage(message)
                .setPositiveButton("OK", okListener)
                .setNegativeButton("Cancel", null)
                .create()
                .show();
    }



    public void getFileUri() {
        image_name= UUID.randomUUID().toString()+mobile+".jpg";
        file=new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)
        + File.separator+image_name
        );
        file_Uri=Uri.fromFile(file);
    }

    private class Encode_image extends AsyncTask<Void,Void,Void> {
        @Override
        protected Void doInBackground(Void... voids) {

            try {
                bitmap = Glide.
                        with(SendSocialActivity.this).
                        load(file_Uri).
                        asBitmap().
                        into(500,500).
                        get();

                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 30, baos);
                    byte[] imageBytes = baos.toByteArray();
                    encoded_string = Base64.encodeToString(imageBytes, Base64.DEFAULT);

            } catch (final ExecutionException e) {
                Log.e(TAG, e.getMessage());
            } catch (final InterruptedException e) {
                Log.e(TAG, e.getMessage());
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            Log.d(TAG, "onPostExecute: "+encoded_string);
            imgSoial.setImageBitmap(bitmap);
        }
    }


}
