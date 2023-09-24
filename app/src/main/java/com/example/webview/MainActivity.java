package com.example.webview;

import static android.app.PendingIntent.getActivity;

import static androidx.core.content.ContextCompat.getSystemService;

import androidx.appcompat.app.AppCompatActivity;

import android.content.ActivityNotFoundException;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.webkit.JavascriptInterface;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {
    private Context getActivity() {
        return getApplicationContext();
    }
    private ValueCallback<Uri> mUploadMessage;
    public ValueCallback<Uri[]> uploadMessage;
    public static final int REQUEST_SELECT_FILE = 100;
    private final static int FILECHOOSER_RESULTCODE = 1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash_screen);
        final long DELAY_MILLIS = 750;
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                setContentView(R.layout.activity_main);
                final WebView webView;
                webView = (WebView)findViewById(R.id.webView);
                WebSettings webSettings = webView.getSettings();
                webSettings.setJavaScriptEnabled(true);
                webSettings.setDomStorageEnabled(true);
                webView.setWebViewClient(new WebViewClient());
                webView.setWebChromeClient(new WebChromeClient(){
                    // For 3.0+ Devices (Start)
                    // onActivityResult attached before constructor
                    protected void openFileChooser(ValueCallback uploadMsg, String acceptType) {
                        mUploadMessage = uploadMsg;
                        Intent i = new Intent(Intent.ACTION_GET_CONTENT);
                        i.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
                        i.addCategory(Intent.CATEGORY_OPENABLE);
                        i.setType("image/*");
                        startActivityForResult(Intent.createChooser(i, "File Browser"), FILECHOOSER_RESULTCODE);
                    }
                    // For Lollipop 5.0+ Devices
                    public boolean onShowFileChooser(WebView mWebView, ValueCallback<Uri[]> filePathCallback, WebChromeClient.FileChooserParams fileChooserParams) {
                        if (uploadMessage != null) {
                            uploadMessage.onReceiveValue(null);
                            uploadMessage = null;
                        }
                        uploadMessage = filePathCallback;
                        Intent intent = null;
                        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
                            intent = fileChooserParams.createIntent();
                        }
                        try {
                            startActivityForResult(intent, REQUEST_SELECT_FILE);
                        } catch (ActivityNotFoundException e) {
                            uploadMessage = null;
                            Toast.makeText(getActivity().getApplicationContext(), "Cannot Open File Chooser", Toast.LENGTH_LONG).show();
                            return false;
                        }
                        return true;
                    }
                    //For Android 4.1 only
                    protected void openFileChooser(ValueCallback<Uri> uploadMsg, String acceptType, String capture) {
                        mUploadMessage = uploadMsg;
                        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
                        intent.addCategory(Intent.CATEGORY_OPENABLE);
                        intent.setType("image/*");
                        startActivityForResult(Intent.createChooser(intent, "File Browser"), FILECHOOSER_RESULTCODE);
                    }

                    protected void openFileChooser(ValueCallback<Uri> uploadMsg) {
                        mUploadMessage = uploadMsg;
                        Intent i = new Intent(Intent.ACTION_GET_CONTENT);
                        i.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
                        i.addCategory(Intent.CATEGORY_OPENABLE);
                        i.setType("image/*");
                        startActivityForResult(Intent.createChooser(i, "File Chooser"), FILECHOOSER_RESULTCODE);
                    }
                });
                class WebAppInterface {
                    @JavascriptInterface
                    public void copyToClipboard(String text) {
                        ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                        ClipData clip = ClipData.newPlainText("demo", text);
                        clipboard.setPrimaryClip(clip);
                    }
                }
                webView.addJavascriptInterface(new WebAppInterface(), "NativeAndroid");
                /* not necessary start */
                webSettings.setRenderPriority(WebSettings.RenderPriority.HIGH);
                webSettings.setPluginState(WebSettings.PluginState.ON_DEMAND);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                    webSettings.setAllowUniversalAccessFromFileURLs(true);
                }
                /* not necessary end */
                webView.loadUrl("https://azhaanexpress.web.app");
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
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            if (requestCode == REQUEST_SELECT_FILE) {
                if (uploadMessage == null)
                    return;
                uploadMessage.onReceiveValue(WebChromeClient.FileChooserParams.parseResult(resultCode, intent));
                uploadMessage = null;
            }
        }
        else if (requestCode == FILECHOOSER_RESULTCODE) {
            if (null == mUploadMessage) return;
            Uri result = intent == null || resultCode != MainActivity.RESULT_OK ? null : intent.getData();
            mUploadMessage.onReceiveValue(result);
            mUploadMessage = null;
        } else
            Toast.makeText(getActivity().getApplicationContext(), "Failed to Upload Image", Toast.LENGTH_LONG).show();
    }
}

