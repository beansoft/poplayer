package com.github.beansoftapp.android.poplayer;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.webkit.JavascriptInterface;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import com.helijia.webviewtester.R;

import static android.view.View.GONE;


public class MainActivity extends Activity {
    private long pageStartTime = 0;
    private WebView wv;
    private ImageButton newActivityBtn;
    private EditText et;
    private TextView pageLoadTime;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        wv = (WebView) findViewById(R.id.wv);
        newActivityBtn = (ImageButton) findViewById(R.id.new_activity);
        pageLoadTime = (TextView) findViewById(R.id.page_load_time);
        et = (EditText) findViewById(R.id.et);

        // setup edit text
        et.setSelected(false);
        if (getIntent().getStringExtra("url") != null) {
            et.setText(getIntent().getStringExtra("url"));
        } else {
//            et.setText("http://www.qq.com");
//            et.setText("file:///android_asset/test.html");
        }

        // setup wv
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            WebView.setWebContentsDebuggingEnabled(true);
        }
        wv.setWebChromeClient(new WebChromeClient());
//        wv.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
        WebSettings settings = wv.getSettings();
        wv.setBackgroundColor(0);
        wv.getBackground().setAlpha(0);
//        settings.setAllowUniversalAccessFromFileURLs(true);
        settings.setJavaScriptEnabled(true);
//        settings.setCacheMode(WebSettings.LOAD_NO_CACHE);
        settings.setAppCacheEnabled(false);
        settings.setDomStorageEnabled(true);
        wv.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
                et.setText(url);
                pageStartTime = System.currentTimeMillis();
                pageLoadTime.setText("0ms");
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                if (pageStartTime == 0) {
                } else {
                    long loadTime = (System.currentTimeMillis() - pageStartTime);
                    pageLoadTime.setText(String.format("%sms", loadTime));
                    System.out.println(String.format("page load time: %sms", loadTime));
                }
            }
        });
        handleLoadUrl();

        // setup events
        newActivityBtn.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    newActivityBtn.setColorFilter(getResources().getColor(android.R.color.holo_blue_dark));
                    return false;
                } else if (event.getAction() == MotionEvent.ACTION_UP) {
                    newActivityBtn.setColorFilter(null);
                    return false;
                }
                return false;
            }
        });
        newActivityBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, MainActivity.class);
                intent.putExtra("url", et.getText().toString());
                startActivity(intent);
            }
        });
        et.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (event.getAction() == KeyEvent.ACTION_DOWN && keyCode == KeyEvent.KEYCODE_ENTER) {
                    handleLoadUrl();
                    return true;
                } else {
                    return false;
                }
            }
        });
        wv.loadUrl("file:///android_asset/index.html");
        wv.addJavascriptInterface(new Object() {

            @JavascriptInterface
            public void hidePopLayer() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        wv.setVisibility(GONE);
                    }
                });
            }
        }, "poplayer");
    }

    private void handleLoadUrl() {
        String url = et.getText().toString();
        if (url.startsWith("http://")) {
        } else if (url.startsWith("https://")) {
        } else {
            url = String.format("http://%s", url);
        }
        wv.loadUrl(url);
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(et.getWindowToken(), 0);
    }

}