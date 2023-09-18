package com.example.webview;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash_screen);
        final long DELAY_MILLIS = 500;
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                setContentView(R.layout.activity_main);
                final WebView webView;
                webView = (WebView)findViewById(R.id.webView);
                WebSettings webSettings = webView.getSettings();
                webSettings.setJavaScriptEnabled(true);
                webSettings.setDomStorageEnabled(true);
                /* not necessary */
                webView.setWebChromeClient(new WebChromeClient());
                webSettings.setRenderPriority(WebSettings.RenderPriority.HIGH);
                webSettings.setPluginState(WebSettings.PluginState.ON_DEMAND);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                    webSettings.setAllowUniversalAccessFromFileURLs(true);
                }
                /* not necessary */
                webView.loadUrl("https://azhaanexpress.web.app/login");
                // Prevent opening URLs outside of the app
                webView.setWebViewClient(new WebViewClient() {
                    public boolean shouldOverrideUrlLoading(WebView view, String url){
                        view.loadUrl(url);
                        return false;
                    }
                });
            }
        }, DELAY_MILLIS);

    }
    @Override
    public void onBackPressed(){
        WebView wv = (WebView)findViewById(R.id.webView);
        if(wv.canGoBack()){
            wv.goBack();
        } else {
            super.onBackPressed();
        }
    }
}
