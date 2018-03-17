package com.idpz.instacity.Like;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
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

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Hashtable;
import java.util.Map;

public class AddArtActivity extends AppCompatActivity {

    String ImageName = "image_name",mobile="0";
    String ImagePath = "image_path";
    ProgressDialog progressDialog;
    Bitmap bitmap=null;
    final int SELECT_FILE=1;
    EditText edtName,edtAddress,edtType,edtWeight,edtMat,edtColor,edtPrice;
    ImageView imgAds;
    Button btnSend,btnAdsImgSelect;
    String REGISTER_URL="",server="",ServerUploadPath="";
    String memo="",name="",type="",weight="",material="",mcolor="",price="";
    View focusView = null;
    Boolean kansel=false,check=true;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_art);

        SharedPreferences SP1;
        SP1 = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
        server=SP1.getString("server", "0");
        ServerUploadPath=server+"/i/setart.php";

        imgAds=(ImageView)findViewById(R.id.imgArtSave);
        btnSend=(Button)findViewById(R.id.btnArtSend);
        btnAdsImgSelect=(Button)findViewById(R.id.btnArtImgSelect);
        edtName=(EditText)findViewById(R.id.txtArtName);
        edtType=(EditText)findViewById(R.id.txtArtType);
        edtWeight=(EditText)findViewById(R.id.txtArtWeight);
        edtMat=(EditText)findViewById(R.id.txtArtMat);
        edtColor=(EditText)findViewById(R.id.txtArtColor);
        edtPrice=(EditText)findViewById(R.id.txtArtPrice);
        edtAddress=(EditText)findViewById(R.id.txtArtAddress);

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
                if(weight.length()<3){
                    edtAddress.setError("وزن کامل وارد شود");
                    focusView=edtAddress;
                    kansel=true;
                }

                if (!kansel)
                    uploadImage();
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


                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

    }

    private void uploadImage(){
        //Showing the progress dialog
        final ProgressDialog loading = ProgressDialog.show(this,"Uploading...","Please wait...",false,false);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, ServerUploadPath,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String s) {
                        //Disimissing the progress dialog

                        //Showing toast message of the response
                        Toast.makeText(AddArtActivity.this, s , Toast.LENGTH_LONG).show();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        //Dismissing the progress dialog

                        //Showing toast
                        Toast.makeText(AddArtActivity.this, ""+volleyError, Toast.LENGTH_LONG).show();
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
                params.put("name", name);
                params.put("memo", memo);
                params.put("type", type);
                params.put("weight", weight);
                params.put("mat", material);
                params.put("color", mcolor);
                params.put("price", price);
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
