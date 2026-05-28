package com.example.bayernstrasse_bus_times;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    private WebView myWebView;
    private LinearLayout logContainer; // Holds the header and the green terminal panel

    @SuppressLint("SetJavaScriptEnabled")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Bind the interface components
        myWebView = findViewById(R.id.transitWebView);
        logContainer = findViewById(R.id.logContainer);

        // Hide the log window automatically when the app first launches
        if (logContainer != null) {
            logContainer.setVisibility(View.GONE);
        }

        myWebView.setWebViewClient(new WebViewClient());

        WebSettings webSettings = myWebView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setDomStorageEnabled(true);

        myWebView.loadUrl("file:///android_asset/index.html");

        // Modern lifecycle back navigation callback
        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                if (myWebView.canGoBack()) {
                    myWebView.goBack();
                } else {
                    setEnabled(false);
                    getOnBackPressedDispatcher().onBackPressed();
                }
            }
        });
    }

    // Inflate our custom dropdown menu structure inside the top action bar
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    // Handle clicks on the menu options
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.action_toggle_log) {
            if (logContainer != null) {
                // If log is visible, hide it. If it is hidden, show it.
                if (logContainer.getVisibility() == View.VISIBLE) {
                    logContainer.setVisibility(View.GONE);
                } else {
                    logContainer.setVisibility(View.VISIBLE);
                }
            }
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}