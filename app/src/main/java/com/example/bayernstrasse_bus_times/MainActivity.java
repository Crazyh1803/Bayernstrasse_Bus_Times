package com.example.bayernstrasse_bus_times;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    private WebView myWebView;

    @SuppressLint("SetJavaScriptEnabled")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Bind the interface view to our layout container id
        myWebView = findViewById(R.id.transitWebView);

        // Force URL links to stay embedded inside our app window
        myWebView.setWebViewClient(new WebViewClient());

        // Configure fundamental web components
        WebSettings webSettings = myWebView.getSettings();
        webSettings.setJavaScriptEnabled(true); // Required for live local loop fetch functions
        webSettings.setDomStorageEnabled(true);  // Allows proper layout stream parsing

        // Direct layout mapping to the local assets folder index
        myWebView.loadUrl("file:///android_asset/index.html");

        // MODERN REPLACEMENT: Register a modern callback handler for the back button
        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                if (myWebView.canGoBack()) {
                    myWebView.goBack(); // Navigate back in the HTML history if possible
                } else {
                    // If there's no web history, disable this callback and trigger the default system back behavior (closes the app)
                    setEnabled(false);
                    getOnBackPressedDispatcher().onBackPressed();
                }
            }
        });
    }
}