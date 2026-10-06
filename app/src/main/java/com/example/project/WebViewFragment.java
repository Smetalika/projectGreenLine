package com.example.project;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;
import androidx.fragment.app.Fragment;

public class WebViewFragment extends Fragment {

    private WebView webView;
    private ProgressBar progressBar;
    private String currentUrl = "https://perm-300.ru/gorod-menyetsy/turisticheskie-marshruty/zelenaya-liniya";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_webview, container, false);

        webView = view.findViewById(R.id.webView);
        progressBar = view.findViewById(R.id.progressBar);

        // Настройка WebView
        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setLoadWithOverviewMode(true);
        webView.getSettings().setUseWideViewPort(true);

        webView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                progressBar.setVisibility(View.GONE);
            }

            @Override
            public void onPageStarted(WebView view, String url, android.graphics.Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
                progressBar.setVisibility(View.VISIBLE);
            }
        });

        // Загружаем URL, если он передан
        if (currentUrl != null && !currentUrl.isEmpty()) {
            webView.loadUrl(currentUrl);
        }

        return view;
    }

    // Метод для загрузки новой страницы
    public void loadUrl(String url) {
        currentUrl = url;
        if (webView != null && url != null && !url.isEmpty()) {
            webView.loadUrl(url);
        }
    }

    // Можно ли нажать "Назад" внутри WebView
    public boolean canGoBack() {
        return webView != null && webView.canGoBack();
    }

    public void goBack() {
        if (webView != null) {
            webView.goBack();
        }
    }
}