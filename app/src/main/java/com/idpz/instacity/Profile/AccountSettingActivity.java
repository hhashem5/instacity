package com.idpz.instacity.Profile;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;

import com.idpz.instacity.Home.LoginActivity;
import com.idpz.instacity.R;

import java.util.ArrayList;

/**
 * Created by h on 2018/01/01.
 */

public class AccountSettingActivity extends AppCompatActivity {
    private static final String TAG = "AccountSettingActivity";
    ProgressBar progressBar;
    private Context mContext;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_acount_setting);
        Log.d(TAG, "onCreate: start");
        mContext = AccountSettingActivity.this;
        setupSettingList();


        ImageView imageView = (ImageView) findViewById(R.id.backArrow);
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
//                Intent intent=new Intent(mContext,ProfileActivity.class);
//                startActivity(intent);
            }
        });
    }


    private void setupSettingList(){
        Log.d(TAG, "setupSettingList: init acc lsit");
        ListView listView=(ListView)findViewById(R.id.lvAccountSetting);
        ArrayList<String> options=new ArrayList<>();
        options.add(getString(R.string.edit_profile));
        options.add(getString(R.string.sign_out));
        options.add("بازگشت");

        ArrayAdapter adapter=new ArrayAdapter(mContext,android.R.layout.simple_list_item_1,options);
        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                switch (position){
                    case 0:
                        Intent intent=new Intent(AccountSettingActivity.this,MyProfileActivity.class);
                        startActivity(intent);
                        finish();
                        break;
                    case 1:

                        AlertDialog.Builder builder = new AlertDialog.Builder(AccountSettingActivity.this);

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
                                SP2.putString("pic", "0");
                                SP2.putString("edu", "0");
                                SP2.putString("edub", "0");
                                SP2.putString("job", "0");
                                SP2.putString("jobb", "0");
                                SP2.apply();
                                finishAffinity();
                                Intent intent=new Intent(AccountSettingActivity.this,LoginActivity.class);
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



                        break;
                    case 2:
                        finish();
                        break;

                }
            }
        });

    }
}
