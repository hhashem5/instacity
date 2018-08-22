package com.idpz.instacity.utils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.net.http.SslError;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.util.JsonReader;
import android.util.JsonToken;
import android.util.Log;
import android.webkit.SslErrorHandler;
import android.webkit.ValueCallback;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.idpz.instacity.R;
import com.idpz.instacity.MapCity.StoreRegActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.StringReader;
import java.util.HashMap;
import java.util.Map;


public class Payment extends AppCompatActivity {

    private static final String TAG = "Payment";
    String LinkSend = "http://idpz.ir/j/zarrinpay/send.php";

    WebView webView ;
    private static final String REG_USER_URL = "http://myzibadasht.ir/food/foodreg.php";
    String Amount = "5000" ;
    String Description = "ثبت فروشگاه در سامانه دهکده هوشمند" ;
    String Email = "" ;
    String Mobile = "" ;
    String Mahsol = "" ;
    String ans="";


//    Developer : Mohammad Mokhles ----- WebSite : http://smaartapp.ir ------------ Email : info@smaartapp.ir


    @SuppressLint("JavascriptInterface")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pay);

        Amount = getIntent().getExtras().getString("Amount");
        Description = getIntent().getExtras().getString("Description");
        Email = getIntent().getExtras().getString("Email");
        Mobile = getIntent().getExtras().getString("Mobile");
        Mahsol = getIntent().getExtras().getString("Mahsol");

        webView = (WebView)findViewById(R.id.webView);

        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setDomStorageEnabled(true);


        webView.addJavascriptInterface(new JsClass(this),"HtmlViewer");


        webView.setWebViewClient(new WebViewClient(){

            @Override
            public void onPageFinished(WebView view, String url) {

                if(Build.VERSION.SDK_INT >= 19){

                    webView.evaluateJavascript("document.getElementsByTagName('html')[0].innerHTML", new ValueCallback<String>() {
                        @Override
                        public void onReceiveValue(String value) {

                            JsonReader reader = new JsonReader(new StringReader(value));
                            reader.setLenient(true);
                            Log.d(TAG, "onReceiveValue: webview closed");
                            try {

                                if(reader.peek() != JsonToken.NULL){
                                    if(reader.peek() == JsonToken.STRING){
                                        String msg = reader.nextString();
                                        if(msg != null) {

                                            String result = Html.fromHtml(msg).toString();

                                            final String[] s = result.split(",");

                                            if(s[0].equals("NULL")){
                                                ans="مقادیر ارسالی خالی مبباشد !";
                                                Toast.makeText(getApplicationContext(),ans, Toast.LENGTH_LONG).show();
                                                finish();
                                            }else if(s[0].equals("ERROR")){
                                                ans="خطایی رخ داده است . کد خطا : ";
                                                Toast.makeText(getApplicationContext(),ans+s[1], Toast.LENGTH_LONG).show();
                                                finish();
                                            }else if(s[0].equals("CANCELL")){
                                                ans= "شما از خرید کالا منصرف شده اید ! انشالا سری بعد ...";
                                                Toast.makeText(getApplicationContext(),ans, Toast.LENGTH_LONG).show();
                                                finish();
                                            }else if(s[0].equals("OK")){
                                                runOnUiThread(new Runnable() {
                                                    @Override
                                                    public void run() {
                                                        ans= "پرداخت شما با موفقیت انجام شد . شماره تراکنش شما : ";
                                                        Toast.makeText(getApplicationContext(),ans+s[1], Toast.LENGTH_LONG).show();
                                                        Intent intent = new Intent(getApplicationContext(), StoreRegActivity.class);
                                                        intent.putExtra("transid", s[1]);
                                                        intent.putExtra("ok", "1");
                                                        intent.putExtra("msg", s[0]);
                                                        intent.putExtra("ans", ans);
                                                        startActivity(intent);
                                                        finish();
                                                    }
                                                });
                                            }else if(s[0].equals("OK1")){
                                                runOnUiThread(new Runnable() {
                                                    @Override
                                                    public void run() {
                                                        ans= "پرداخت شما با موفقیت صورت گرفت . محصول شماره "+"1"+"برای شما ثبت شد . شماره تراکنش شما برابر است با : "+s[1];
//                                                        OrderFoodActivity.txt_status.setText("پرداخت شما با موفقیت صورت گرفت . محصول شماره "+"1"+"برای شما ثبت شد . شماره تراکنش شما برابر است با : "+s[1]);
                                                        Toast.makeText(getApplicationContext(),ans, Toast.LENGTH_LONG).show();
                                                        finish();
                                                    }
                                                });
                                            }else if(s[0].equals("OK2")){
                                                runOnUiThread(new Runnable() {
                                                    @Override
                                                    public void run() {
                                                        ans="پرداخت شما با موفقیت صورت گرفت . محصول شماره "+"2"+"برای شما ثبت شد . شماره تراکنش شما برابر است با : "+s[1];
//                                                        OrderFoodActivity.txt_status.setText("پرداخت شما با موفقیت صورت گرفت . محصول شماره "+"2"+"برای شما ثبت شد . شماره تراکنش شما برابر است با : "+s[1]);
                                                        Toast.makeText(getApplicationContext(),ans, Toast.LENGTH_LONG).show();
                                                        finish();
                                                    }
                                                });
                                            }else if(s[0].equals("OK3")){
                                                runOnUiThread(new Runnable() {
                                                    @Override
                                                    public void run() {
                                                        ans="پرداخت شما با موفقیت صورت گرفت . محصول شماره "+"3"+"برای شما ثبت شد . شماره تراکنش شما برابر است با : "+s[1];
//                                                        OrderFoodActivity.txt_status.setText("پرداخت شما با موفقیت صورت گرفت . محصول شماره "+"3"+"برای شما ثبت شد . شماره تراکنش شما برابر است با : "+s[1]);
                                                        Toast.makeText(getApplicationContext(),ans, Toast.LENGTH_LONG).show();
                                                        finish();
                                                    }
                                                });
                                            }else if(s[0].equals("OK4")){
                                                runOnUiThread(new Runnable() {
                                                    @Override
                                                    public void run() {
                                                        ans= "پرداخت شما با موفقیت صورت گرفت . محصول شماره "+"4"+"برای شما ثبت شد . شماره تراکنش شما برابر است با : "+s[1];
//                                                        OrderFoodActivity.txt_status.setText("پرداخت شما با موفقیت صورت گرفت . محصول شماره "+"4"+"برای شما ثبت شد . شماره تراکنش شما برابر است با : "+s[1]);
                                                        Toast.makeText(getApplicationContext(),ans, Toast.LENGTH_LONG).show();
                                                        finish();
                                                    }
                                                });
                                            }else if(s[0].equals("OK5")){
                                                runOnUiThread(new Runnable() {
                                                    @Override
                                                    public void run() {
                                                        ans= "پرداخت شما با موفقیت صورت گرفت . محصول شماره "+"5"+"برای شما ثبت شد . شماره تراکنش شما برابر است با : "+s[1];
//                                                        OrderFoodActivity.txt_status.setText("پرداخت شما با موفقیت صورت گرفت . محصول شماره "+"5"+"برای شما ثبت شد . شماره تراکنش شما برابر است با : "+s[1]);
                                                        Toast.makeText(getApplicationContext(),ans, Toast.LENGTH_LONG).show();
                                                        finish();
                                                    }
                                                });
                                            }
                                            Log.d(TAG, "onReceiveValue: Answare"+s[0]);
                                        }
                                    }
                                }
                            } catch (IOException e){
                                e.printStackTrace();
                            } finally {
                                try {
                                    reader.close();
                                }catch (IOException e){
                                    e.printStackTrace();
                                }
                            }
                        }
                    });

                }else {
                    webView.loadUrl("javascript:window.HtmlViewer.get(document.getElementsByTagName('html')[0].innerHTML);");
                }

            }


            @Override
            public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
                handler.proceed();
            }

        });


        webView.loadUrl(LinkSend+"?Amount="+Amount+"&Description="+Description+"&Email="+Email+"&Mobile="+Mobile+"&Mahsol="+Mahsol);
        Toast.makeText(getApplicationContext(),"صفحه در حال بارگذاری میباشد . لطفا صبر کنید ...", Toast.LENGTH_LONG).show();



    }

//    Developer : Mohammad Mokhles ----- WebSite : http://smaartapp.ir ------------ Email : info@smaartapp.ir

    class JsClass {

        Context context ;


        JsClass(Context c){
            this.context = c ;
        }

        public void get (String html){
            Log.d(TAG, "get: from webSite"+html);
            String result = Html.fromHtml(html).toString();

            final String[] s = result.split(",");
            Toast.makeText(context, result, Toast.LENGTH_SHORT).show();
            if(s[0].equals("NULL")){
                Toast.makeText(context,"مقادیر ارسالی خالی مبباشد !", Toast.LENGTH_LONG).show();
                finish();
            }else if(s[0].equals("ERROR")){
                Toast.makeText(context,"خطایی رخ داده است . کد خطا : "+s[1], Toast.LENGTH_LONG).show();
                finish();
            }else if(s[0].equals("CANCELL")){
                Toast.makeText(context,"شما از خرید کالا منصرف شده اید ! انشالا سری بعد ...", Toast.LENGTH_LONG).show();
                finish();
            }else if(s[0].equals("OK")){
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
//                        OrderFoodActivity.txt_status.setText("پرداخت انجام شد . شماره تراکنش : "+s[1]);
                        Toast.makeText(getApplicationContext(),"پرداخت شما با موفقیت انجام شد . شماره تراکنش شما : "+s[1]+" \n به زودی با شما تماس میگیریم", Toast.LENGTH_LONG).show();
//                        MySingleton.getInstance(getApplicationContext()).addToRequestQueue(foodRequest);
                        Intent intent = new Intent(getApplicationContext(), StoreRegActivity.class);
                        intent.putExtra("transid", s[1]);
                        intent.putExtra("ok", "1");
                        startActivity(intent);
                        finish();
                    }
                });
            }else if(s[0].equals("OK1")){
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
//                        OrderFoodActivity.txt_status.setText("پرداخت موفق: سفارش غذا ثبت شد. شماره پیگیری"+s[1]);
                        Toast.makeText(context,"پرداخت موفق: سفارش غذا ثبت شد. شماره پیگیری"+s[1]+" \n به زودی با شما تماس میگیریم", Toast.LENGTH_LONG).show();
                        finish();
                    }
                });
            }else if(s[0].equals("OK2")){
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
//                        OrderFoodActivity.txt_status.setText("پرداخت شما با موفقیت صورت گرفت . محصول شماره "+"2"+"برای شما ثبت شد . شماره تراکنش شما برابر است با : "+s[1]);
                        Toast.makeText(context,"پرداخت شما با موفقیت صورت گرفت . محصول شماره "+"2"+"برای شما ثبت شد . شماره تراکنش شما برابر است با : "+s[1], Toast.LENGTH_LONG).show();
                        finish();
                    }
                });
            }else if(s[0].equals("OK3")){
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
//                        OrderFoodActivity.txt_status.setText("پرداخت شما با موفقیت صورت گرفت . محصول شماره "+"3"+"برای شما ثبت شد . شماره تراکنش شما برابر است با : "+s[1]);
                        Toast.makeText(context,"پرداخت شما با موفقیت صورت گرفت . محصول شماره "+"3"+"برای شما ثبت شد . شماره تراکنش شما برابر است با : "+s[1], Toast.LENGTH_LONG).show();
                        finish();
                    }
                });
            }else if(s[0].equals("OK4")){
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
//                        OrderFoodActivity.txt_status.setText("پرداخت شما با موفقیت صورت گرفت . محصول شماره "+"4"+"برای شما ثبت شد . شماره تراکنش شما برابر است با : "+s[1]);
                        Toast.makeText(context,"پرداخت شما با موفقیت صورت گرفت . محصول شماره "+"4"+"برای شما ثبت شد . شماره تراکنش شما برابر است با : "+s[1], Toast.LENGTH_LONG).show();
                        finish();
                    }
                });
            }else if(s[0].equals("OK5")){
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
//                        OrderFoodActivity.txt_status.setText("پرداخت شما با موفقیت صورت گرفت . محصول شماره "+"5"+"برای شما ثبت شد . شماره تراکنش شما برابر است با : "+s[1]);
                        Toast.makeText(context,"پرداخت شما با موفقیت صورت گرفت . محصول شماره "+"5"+"برای شما ثبت شد . شماره تراکنش شما برابر است با : "+s[1], Toast.LENGTH_LONG).show();
                        finish();
                    }
                });
            }

        }


    }

//    Developer : Mohammad Mokhles ----- WebSite : http://smaartapp.ir ------------ Email : info@smaartapp.ir
StringRequest foodRequest = new StringRequest(Request.Method.POST, REG_USER_URL,
        new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {

                JSONArray jsonArray= null;
                try {
                    jsonArray = new JSONArray(response);


                    String alltext,soname,sotext,soincharge,sodone,soans,sostatus;
                    Toast.makeText(getApplicationContext(), "سفارش ثبت شد", Toast.LENGTH_SHORT).show();
                    JSONObject jsonObject=jsonArray.getJSONObject(0);


//                            txtNews.setText("کاربر ثبت شد");

                } catch (JSONException e) {
                    e.printStackTrace();
                }


            }
        }
        , new Response.ErrorListener() {
    @Override
    public void onErrorResponse(VolleyError error) {
        Toast.makeText(getApplicationContext(), error.toString(), Toast.LENGTH_SHORT).show();
    }
}  )
{
    @Override
    protected Map<String, String> getParams() throws AuthFailureError {
        Map<String,String> params = new HashMap<String,String>();
        params.put("Amount",Amount);
        params.put("Description",Description);
        params.put("Email",Email);
        params.put("Mobile",Mobile);
        params.put("Mahsol",Mahsol);
        return params;
    }
};


}
