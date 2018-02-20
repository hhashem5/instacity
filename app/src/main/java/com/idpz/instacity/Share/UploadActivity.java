package com.idpz.instacity.Share;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.idpz.instacity.R;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by User on 2/13/2017.
 */

public class UploadActivity extends AppCompatActivity {

    private static final String TAG = "UploadActivity";

    //declare variables
    String REGISTER_URL="",server;
    String myUrl="",myname="0",mob="0";
    private String filepath = null;
    private ImageView image;
    private EditText txtimageName;
    private Button btnUpload,btnNext,btnBack,btnGallerySel;
    private ProgressDialog mProgressDialog;
    private TextView txtMessage;
    private final static int mWidth = 512;
    private final static int mLength = 512;

    private ArrayList<String> pathArray;
    private int array_position;



    public UploadActivity() {
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.upload_layout);
        SharedPreferences SP = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
        myname=SP.getString("myname", "0");
        mob=SP.getString("mobile", "0");
        server=SP.getString("server", "0");
        REGISTER_URL=server+"/input/social.php";

        image = (ImageView) findViewById(R.id.uploadImage);
        btnBack = (Button) findViewById(R.id.btnBackImage);
        btnNext = (Button) findViewById(R.id.btnNextImage);
        btnUpload = (Button) findViewById(R.id.btnUploadImage);
        btnGallerySel = (Button) findViewById(R.id.btnGallery);
        txtimageName = (EditText) findViewById(R.id.imageName);
        txtMessage = (TextView) findViewById(R.id.txtMessage);
        pathArray = new ArrayList<>();
        mProgressDialog = new ProgressDialog(UploadActivity.this);


        checkFilePermissions();

        addFilePaths();

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(array_position > 0){
                    Log.d(TAG, "onClick: Back an Image.");
                    array_position = array_position - 1;
                    loadImageFromStorage();
                }
            }
        });

        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(array_position < pathArray.size() - 1){
                    Log.d(TAG, "onClick: Next Image.");
                    array_position = array_position + 1;
                    loadImageFromStorage();
                }
            }
        });

        btnGallerySel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                // intent.setType("video/*");
                // intent.setType("audio/*");
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(
                        Intent.createChooser(intent, "Complete action using"), 1);


            }
        });

        btnUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG, "onClick: Uploading Image.");
                mProgressDialog.setMessage("Uploading Image...");
                mProgressDialog.show();

                //get the signed in user


            }
        });

    }

    /**
     * Add the file paths you want to use into the array
     */
    private void addFilePaths(){
        Log.d(TAG, "addFilePaths: Adding file paths.");
        String path = "/storage/extSdCard/";
        Toast.makeText(this, path, Toast.LENGTH_LONG).show();
        pathArray.add(path+"/Pictures/Photo/IMG1.jpg");
        pathArray.add(path+"/Pictures/Photo/IMG2.jpg");
        pathArray.add(path+"/Pictures/Photo/IMG3.jpg");
        loadImageFromStorage();
    }

    private void loadImageFromStorage()
    {
        try{
            String path = pathArray.get(array_position);
            File f=new File(path, "");
            Bitmap b = BitmapFactory.decodeStream(new FileInputStream(f));
            image.setImageBitmap(b);
        }catch (FileNotFoundException e){
            Log.e(TAG, "loadImageFromStorage: FileNotFoundException: " + e.getMessage() );
        }

    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private void checkFilePermissions() {
        if(Build.VERSION.SDK_INT > Build.VERSION_CODES.M){
            int permissionCheck = UploadActivity.this.checkSelfPermission("Manifest.permission.READ_EXTERNAL_STORAGE");
            permissionCheck += UploadActivity.this.checkSelfPermission("Manifest.permission.WRITE_EXTERNAL_STORAGE");
            if (permissionCheck != 0) {
                this.requestPermissions(new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE,android.Manifest.permission.READ_EXTERNAL_STORAGE}, 1001); //Any number
            }
        }else{
            Log.d(TAG, "checkBTPermissions: No need to check permissions. SDK version < LOLLIPOP.");
        }
    }

    /**
     * customizable toast
     * @param message
     */
    private void toastMessage(String message){
        Toast.makeText(this,message,Toast.LENGTH_SHORT).show();
    }

    public void reqcdrv() {
//        Toast.makeText(MainActivity.this, " reqcdrv", Toast.LENGTH_SHORT).show();
        RequestQueue queue = Volley.newRequestQueue(this);
        String url = REGISTER_URL;
        StringRequest postRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>()
                {
                    @Override
                    public void onResponse(String response) {txtMessage.setTextColor(Color.GREEN);
                        txtMessage.setText("پیام با موفقیت ارسال شد");

                    }
                },
                new Response.ErrorListener()
                {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // TODO Auto-generated method stub
                        txtMessage.setTextColor(Color.RED);
                        txtMessage.setText("پیام ارسال نشد");
                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String>params = new HashMap<String,String>();
                params.put("name",myname);
                params.put("phone",mob);
                params.put("text",txtimageName.getText().toString());
                params.put("serial","123456");
                params.put("status","1");
                params.put("pic",myUrl);
                return params;
            }
        };
        queue.add(postRequest);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Toast.makeText(this, "req-cod:"+requestCode+" rslt:"+resultCode+" resaultok="+RESULT_OK, Toast.LENGTH_LONG).show();
        if (requestCode == 1 && resultCode == RESULT_OK) {
            Uri selectedImageUri = data.getData();
            filepath = getPath(selectedImageUri);

                Bitmap bitmap = BitmapFactory.decodeFile(filepath);
                image.setImageBitmap(bitmap);

            txtMessage.setText("Uploading file path:" + filepath);

        }
    }

    @SuppressWarnings("deprecation")
    public String getPath(Uri uri) {
        String[] projection = { MediaStore.Images.Media.DATA };
        Cursor cursor = managedQuery(uri, projection, null, null, null);
        int column_index = cursor
                .getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();
        return cursor.getString(column_index);
    }


}
