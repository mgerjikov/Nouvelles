package com.example.android.nouvelles;

import android.content.AsyncTaskLoader;
import android.content.Context;

import java.util.List;

/**
 * Loads a list of news/articles by using an AsyncTaskLoader<List<News>> to perform the
 * network request to the given URL.
 */

public class NewsLoader extends AsyncTaskLoader<List<News>> {

    // Query url
    private String mUrl;

    /**
     * CONSTRUCTOR
     * <p>
     * Constructs a new {@link NewsLoader}
     *
     * @param context is the context of the activity
     * @param url     is the url that the loader uses to load data from
     */
    public NewsLoader(Context context, String url) {
        super(context);
        mUrl = url;
    }

    @Override
    protected void onStartLoading() {
        forceLoad();
    }

    /**
     * This is on a background thread.
     */
    @Override
    public List<News> loadInBackground() {
        if (mUrl == null) {
            return null;
        }

        // Perform the network request, parse the response, and extract a list of news/articles.
        List<News> newsItemList = NewsQueryUtils.fetchNewsData(mUrl);
        return newsItemList;
    }
}
