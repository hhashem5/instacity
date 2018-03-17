package com.idpz.instacity.Like;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
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

public class AdsAddActivity extends AppCompatActivity {

    String ImageName = "image_name",mobile="0";
    String ImagePath = "image_path";
    ProgressDialog progressDialog;
    Bitmap bitmap=null;
    final int SELECT_FILE=1;
    EditText edtTitle,edtMemo,edtTel,edtAddress;
    ImageView imgAds;
    Button btnSend,btnAdsImgSelect;
    String REGISTER_URL="",server="",ServerUploadPath="";
    String title="",memo="",tel="",address="";
    View focusView = null;
    Boolean kansel=false,check=true;
    private Intent intent;
    private String imgUrl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ads_add);

        SharedPreferences SP1;
        SP1 = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
        server=SP1.getString("server", "0");
        ServerUploadPath=server+"/i/setads.php";

        imgAds=(ImageView)findViewById(R.id.imgAdsSave);
        btnSend=(Button)findViewById(R.id.btnAdsSend);
        btnAdsImgSelect=(Button)findViewById(R.id.btnAdsImgSelect);
        edtTitle=(EditText)findViewById(R.id.txtAdsTitle);
        edtMemo=(EditText)findViewById(R.id.txtAdsMemo);
        edtTel=(EditText)findViewById(R.id.txtAdsTel);
        edtAddress=(EditText)findViewById(R.id.txtAdsAddress);

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
            imgAds.setImageBitmap(bitmap);
        }
        else if(intent.hasExtra(getString(R.string.selected_bitmap))){
            bitmap = (Bitmap) intent.getParcelableExtra(getString(R.string.selected_bitmap));

            imgAds.setImageBitmap(bitmap);

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
                    imgAds.setImageBitmap(bitmap);


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
                String name = mobile;
                //Creating parameters
                Map<String,String> params = new Hashtable<String, String>();
                params.put(ImageName, mobile);

                params.put(ImagePath, image);

                params.put("title", title);
                params.put("memo", memo);
                params.put("tel", tel);
                params.put("adrs", address);
                params.put("owner", mobile);


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
