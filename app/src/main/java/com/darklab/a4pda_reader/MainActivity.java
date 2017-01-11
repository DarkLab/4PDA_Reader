package com.darklab.a4pda_reader;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private static final String BASE_URL = "http://4pda.ru/feed/rss/";
    private static final String SAVED_URL = "saved_url";
    private boolean isLandscape;

    private RssItemAdapter rssItemAdapter;
    private RssWebView webView;
    private String currentUrl;
    private SwipeRefreshLayout swipeRefreshLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        rssItemAdapter = new RssItemAdapter();
        RecyclerView rssItemListRecycler = (RecyclerView) findViewById(R.id.rss_item_list);
        rssItemListRecycler.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        rssItemListRecycler.setAdapter(rssItemAdapter);

        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swiperefresh);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                new RSSDownloader().execute(BASE_URL);
            }
        });

        webView = (RssWebView) findViewById(R.id.web_view);
        if (webView != null) {
            isLandscape = true;

            if (savedInstanceState != null) {
                webView.loadUrl(savedInstanceState.getString(SAVED_URL));
            }
        }

        // TODO: 11.01.17 Можно добавить проверку подключения к сети

        new RSSDownloader().execute(BASE_URL);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (currentUrl != null) {
            outState.putString(SAVED_URL, currentUrl);
        }
    }

    private class RSSDownloader extends AsyncTask<String, Void, List<A4PDAItem>> {
        @Override
        protected List<A4PDAItem> doInBackground(String... params) {
            try {
                return loadFromNetwork(params[0]);
            } catch (IOException e) {
                return new ArrayList<>(0);
            } catch (XmlPullParserException e) {
                return new ArrayList<>(0);
            }
        }

        @Nullable
        private List<A4PDAItem> loadFromNetwork(String url) throws IOException, XmlPullParserException {
            InputStream stream = null;
            A4PDAXmlParser a4PDAXmlParser = new A4PDAXmlParser();
            List<A4PDAItem> items = null;
            try {
                stream = downloadUrl(url);
                items = a4PDAXmlParser.parse(stream);
            } finally {
                if (stream != null) {
                    stream.close();
                }
            }

            return items;
        }

        @Override
        protected void onPostExecute(List<A4PDAItem> items) {
            super.onPostExecute(items);

            rssItemAdapter.setItemList(items);
            rssItemAdapter.notifyDataSetChanged();

            swipeRefreshLayout.setRefreshing(false);
        }

        private InputStream downloadUrl(String urlString) throws IOException {
            URL url = new URL(urlString);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setReadTimeout(10000 /* milliseconds */);
            conn.setConnectTimeout(15000 /* milliseconds */);
            conn.setRequestMethod("GET");
            conn.setDoInput(true);
            conn.connect();
            return conn.getInputStream();
        }
    }

    private class RssItemAdapter extends RecyclerView.Adapter<RssItemViewHolder> {
        private List<A4PDAItem> itemList;

        RssItemAdapter() {
            itemList = new ArrayList<>();
        }

        void setItemList(List<A4PDAItem> itemList) {
            this.itemList = itemList;
        }

        @Override
        public RssItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutInflater inflater = LayoutInflater.from(parent.getContext());
            View view = inflater.inflate(R.layout.rss_item, parent, false);
            return new RssItemViewHolder(view);
        }

        @Override
        public void onBindViewHolder(RssItemViewHolder holder, int position) {
            holder.tvTitle.setText(itemList.get(position).title);
            holder.tvLink.setText(itemList.get(position).link);
            holder.tvDescription.setText(itemList.get(position).description);
            final int itemPosition = position;
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    currentUrl = itemList.get(itemPosition).link;
                    if (isLandscape) {
                        webView.loadUrl(currentUrl);
                    } else {
                        Intent intent = new Intent(MainActivity.this, DetailsActivity.class);
                        intent.putExtra(DetailsActivity.NEWS_LINK, currentUrl);
                        startActivity(intent);
                    }
                }
            });
        }

        @Override
        public int getItemCount() {
            return itemList.size();
        }
    }

    private static class RssItemViewHolder extends RecyclerView.ViewHolder {
        TextView tvTitle;
        TextView tvLink;
        TextView tvDescription;

        RssItemViewHolder(View itemView) {
            super(itemView);
            tvTitle = (TextView) itemView.findViewById(R.id.rss_title);
            tvLink = (TextView) itemView.findViewById(R.id.rss_link);
            tvDescription = (TextView) itemView.findViewById(R.id.rss_description);
        }
    }
}
