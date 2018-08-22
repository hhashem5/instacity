package com.idpz.instacity.Home;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.idpz.instacity.R;

/**
 * Created by h on 2018/05/03.
 */

public class GreenActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_green);

        String openURL = getIntent().getStringExtra("openURL");


        Button onBackButton = (Button) findViewById(R.id.back_button);
        onBackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), HomeActivity.class);
                startActivity(intent);
            }
        });

        WebView webView = (WebView) findViewById(R.id.webview);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.setWebViewClient(new WebViewClient() {

            public void onPageFinished(WebView view, String url) {
                // do your stuff here
                ProgressBar progressBar=(ProgressBar)findViewById(R.id.progressBar);
                progressBar.setVisibility(View.GONE);
            }
        });
        webView.setWebViewClient(new WebViewClient());
        if (openURL == null)
            webView.loadUrl("https://google.com");
        else
            webView.loadUrl(openURL);

    }
}
