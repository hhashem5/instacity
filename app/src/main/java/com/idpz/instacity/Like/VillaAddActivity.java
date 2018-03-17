package com.idpz.instacity.Like;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.idpz.instacity.R;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Hashtable;
import java.util.Map;

public class VillaAddActivity extends AppCompatActivity {
    String ImageName = "image_name",mobile="0";
    String ImagePath = "image_path";
    ProgressDialog progressDialog;
    Bitmap bitmap=null;
    final int SELECT_FILE=1;
    EditText edtName,edtMemo,edtTel,edtAddress,edtArea,edtPrice,edtRoom,edtFacility;
    ImageView imgVilla;
    Button btnSend,btnAdsImgSelect;
    String REGISTER_URL="",server="",ServerUploadPath="";
    String name="",area="",price="",room="",facility="",memo="",tel="",address="";
    View focusView = null;
    Boolean kansel=false,check=true;
    private Intent intent;
    private String imgUrl,lat,lng;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_villa_add);

        SharedPreferences SP1;
        SP1 = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
        server=SP1.getString("server", "0");
        lat=SP1.getString("lat", "0");
        lng=SP1.getString("lng", "0");
        mobile=SP1.getString("mobile", "0");
        ServerUploadPath=server+"/i/setvilla.php";

        imgVilla=(ImageView)findViewById(R.id.imgVillaSave);
        btnSend=(Button)findViewById(R.id.btnVillaSend);
        btnAdsImgSelect=(Button)findViewById(R.id.btnVillaImgSelect);
        edtName=(EditText)findViewById(R.id.txtVillaName);
        edtMemo=(EditText)findViewById(R.id.txtVillaMemo);
        edtTel=(EditText)findViewById(R.id.txtVillaTel);
        edtAddress=(EditText)findViewById(R.id.txtVillaAddress);
        edtArea=(EditText)findViewById(R.id.txtVillaArea);
        edtRoom=(EditText)findViewById(R.id.txtVillaRoom);
        edtFacility=(EditText)findViewById(R.id.txtVillaFacility);


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
                name=edtName.getText().toString();
                memo=edtMemo.getText().toString();
                tel=edtTel.getText().toString();
                address=edtAddress.getText().toString();
                area=edtArea.getText().toString();
                room=edtRoom.getText().toString();
                facility=edtFacility.getText().toString();

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

                if (!kansel){
                    try {
                        setImage();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    uploadImage();}
            }
        });


    }

    /**
     * gets the image url from the incoming intent and displays the chosen image
     */
    private void setImage() throws IOException {
        intent = getIntent();


        if(intent.hasExtra(getString(R.string.selected_image))){
            imgUrl = intent.getStringExtra(getString(R.string.selected_image));

//            UniversalImageLoader.setImage(imgUrl, image, null, mAppend);


            FileInputStream in = null;
            try {
                in = new FileInputStream(imgUrl);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
            BufferedInputStream buf = new BufferedInputStream(in);
            byte[] bMapArray= new byte[buf.available()];
            buf.read(bMapArray);
            bitmap = BitmapFactory.decodeByteArray(bMapArray, 0, bMapArray.length);
            imgVilla.setImageBitmap(bitmap);
        }
        else if(intent.hasExtra(getString(R.string.selected_bitmap))){
            bitmap = (Bitmap) intent.getParcelableExtra(getString(R.string.selected_bitmap));

            imgVilla.setImageBitmap(bitmap);

        }else {
            finish();
        }
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
                    imgVilla.setImageBitmap(bitmap);


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

                        //Showing toast message of the response
                        Toast.makeText(VillaAddActivity.this, "با موفقیت ارسال شد" , Toast.LENGTH_LONG).show();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        //Dismissing the progress dialog

                        //Showing toast
                        Toast.makeText(VillaAddActivity.this, ""+volleyError, Toast.LENGTH_LONG).show();
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
                params.put("tel", tel);
                params.put("adrs", address);
                params.put("owner", mobile);
                params.put("price", price);
                params.put("room", room);
                params.put("facility", facility);
                params.put("lat", lat);
                params.put("lng", lng);
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





}
