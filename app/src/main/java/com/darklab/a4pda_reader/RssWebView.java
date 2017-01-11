package com.darklab.a4pda_reader;

import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;

/**
 * Created by aleksandrlihovidov on 11.01.17.
 */

public class RssWebView extends WebView {
    public RssWebView(Context context) {
        this(context, null);
    }

    public RssWebView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public RssWebView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.M) {
            //noinspection deprecation
            setWebViewClient(new WebViewClient() {
                @Override
                public boolean shouldOverrideUrlLoading(WebView view, String url) {
                    return true;
                }
            });
        } else {
            setWebViewClient(new WebViewClient() {
                @Override
                public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
                    return true;
                }
            });
        }
    }
}
