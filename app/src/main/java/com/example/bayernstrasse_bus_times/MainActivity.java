package com.example.bayernstrasse_bus_times;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Base64;
import android.webkit.JavascriptInterface;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MainActivity extends AppCompatActivity {

    private WebView myWebView;
    private final ExecutorService executorService = Executors.newSingleThreadExecutor();

    @SuppressLint("SetJavaScriptEnabled")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        myWebView = findViewById(R.id.transitWebView);
        WebSettings webSettings = myWebView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setDomStorageEnabled(true);

        // This links the Java background thread directly to your HTML's JavaScript environment
        myWebView.addJavascriptInterface(new AndroidTransitBridge(), "AndroidBridge");

        myWebView.setWebViewClient(new WebViewClient());

        // FORCE TARGETING LOCAL FILE SYSTEM
        myWebView.loadUrl("file:///android_asset/index.html");

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

    public class AndroidTransitBridge {
        @JavascriptInterface
        public void fetchTransitData(final String stationId) {
            executorService.execute(() -> {
                String result;
                try {
                    // Querying the official secure RMV server infrastructure directly via native Java
                    URL url = new URL("https://www.rmv.de/hapi/departureBoard?accessId=fc28a2e4-e61b-48f6-a27f-a9888cf1c8ce&id=" + stationId + "&duration=60&format=json");
                    HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                    urlConnection.setRequestMethod("GET");
                    urlConnection.setConnectTimeout(8000);
                    urlConnection.setReadTimeout(8000);

                    try {
                        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
                        StringBuilder stringBuilder = new StringBuilder();
                        String line;
                        while ((line = bufferedReader.readLine()) != null) {
                            stringBuilder.append(line).append("\n");
                        }
                        bufferedReader.close();
                        result = stringBuilder.toString();
                    } finally {
                        urlConnection.disconnect();
                    }
                } catch (Exception e) {
                    result = "ERROR: " + e.getMessage();
                }

                final String finalResult = result;
                runOnUiThread(() -> {
                    try {
                        String encodedJson = Base64.encodeToString(finalResult.getBytes(), Base64.NO_WRAP);
                        myWebView.evaluateJavascript("window.onNativeDataReceived('" + stationId + "', '" + encodedJson + "');", null);
                    } catch (Exception ex) {
                        // Catch tracking bugs during evaluation handoffs
                    }
                });
            });
        }
    }
}