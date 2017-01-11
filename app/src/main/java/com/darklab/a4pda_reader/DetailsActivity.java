package com.darklab.a4pda_reader;

import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

public class DetailsActivity extends AppCompatActivity {
    public static final String NEWS_LINK = "news_link";

    private RssWebView webView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);

        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            finish();
            return;
        }

        String link = getIntent().getStringExtra(NEWS_LINK);
        getSupportActionBar().setTitle(link);

        webView = (RssWebView) findViewById(R.id.web_view);
        webView.loadUrl(link);
    }
}
