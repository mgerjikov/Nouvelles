package com.example.android.nouvelles;


import android.app.LoaderManager;
import android.app.LoaderManager.LoaderCallbacks;
import android.content.Context;
import android.content.Intent;
import android.content.Loader;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements LoaderCallbacks<List<News>> {

    /**
     * Constant value for the book loader ID
     */
    private static final int NEWS_LOADER_ID = 1;
    /**
     * Initial Query which will be combined with the user's input
     */
    private static final String INITIAL_QUERY = "https://content.guardianapis.com/search?";
    /**
     * Loader for background thread
     */
    private static LoaderManager loaderManager;
    /**
     * Adapter for the List
     */
    private static NewsAdapter newsAdapter;
    /**
     * TextView displaying messages to the user
     */
    TextView emptyState;
    /**
     * Progress bar displayed to user
     */
    ProgressBar progressBar;
    /**
     * Refresh Layout
     */
    SwipeRefreshLayout swipeRefreshLayout;
    /**
     * Network info to check for internet connection
     */
    NetworkInfo networkInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize the empty text view and progress bar
        emptyState = (TextView) findViewById(R.id.empty_state_text);
        progressBar = (ProgressBar) findViewById(R.id.progress_bar);

        // Initialize the refresh layout and assign refresh listener to it
        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_refresh);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                recyclerViewRefresh();
            }
        });
        // Connectivity manager to check state of network connectivity
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        // Get details on the currently active default data network
        networkInfo = connectivityManager.getActiveNetworkInfo();
        // If there is a network connection, fetch data
        if (networkInfo != null && networkInfo.isConnected()) {
            // Notify the user that fetching data is in progress
            emptyState.setText(getString(R.string.loading_articles));
            // Helper method that initialize Loader and News Adapter
            initLoaderAndAdapter();
        } else {
            // If there is no internet connection
            // Hide the progress bar
            progressBar.setVisibility(View.GONE);
            // and inform the user
            emptyState.setText(getString(R.string.no_internet_connection));
        }
    }

    private void initLoaderAndAdapter() {
        // Get a reference to the LoaderManager, in order to interact with loaders.
        loaderManager = getLoaderManager();
        // Initialize the loader. Pass in the int ID constant defined above and pass in null for
        // the bundle. Pass in this activity for the LoaderCallbacks parameter (which is valid
        // because this activity implements the LoaderCallbacks interface).
        loaderManager.initLoader(NEWS_LOADER_ID, null, this);

        // Lookup the recyclerView in activity layout
        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        // Create adapter passing the data
        newsAdapter = new NewsAdapter(this, new ArrayList<News>());
        recyclerView.setHasFixedSize(true);
        // Attach the adapter to the recyclerView to populate items
        recyclerView.setAdapter(newsAdapter);
        // Set layout manager to position the items
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

    }

    private void recyclerViewRefresh() {
        // Get a reference to the ConnectivityManager to check state of network connectivity
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        // Get details on the currently active default data network
        networkInfo = cm.getActiveNetworkInfo();

        if (networkInfo != null && networkInfo.isConnected()) {
            // Notify the user that the Layout is being refreshed
            emptyState.setText(getString(R.string.refreshing));
            // and show the progress bar
            progressBar.setVisibility(View.VISIBLE);

            // Check if newsAdapter is not null (which will happen if on launch there was no
            // connection)
            if (newsAdapter != null) {
                // Clear the adapter
                newsAdapter.clearAll();
            }
            if (loaderManager != null) {
                // Restart Loader
                loaderManager.restartLoader(NEWS_LOADER_ID, null, this);
                // Inform SwipeRefreshLayout that loading is complete so it can hide its progress bar
                swipeRefreshLayout.setRefreshing(false);
            } else {
                initLoaderAndAdapter();
                // Inform SwipeRefreshLayout that loading is complete so it can hide its progress bar
                swipeRefreshLayout.setRefreshing(false);
            }
        } else {
            // Hide progress bar
            progressBar.setVisibility(View.GONE);

            // Check if newsAdapter is not null (which will happen if on launch there was no
            // connection)
            if (newsAdapter != null) {
                // Clear the adapter
                newsAdapter.clearAll();
            }
            // Display error
            emptyState.setText(getString(R.string.no_internet_connection));
            swipeRefreshLayout.setRefreshing(false);
        }

    }

    @Override
    public Loader<List<News>> onCreateLoader(int id, Bundle args) {
        // Get an instance of SharedPreferences
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);

        // Get search query preference
        String searchQuery = sharedPreferences.getString(getString(R.string.settings_search_query_key), getString(R.string.settings_search_query_default));

        // Get order by preference
        String orderBy = sharedPreferences.getString(getString(R.string.settings_order_by_list_key),
                getString(R.string.settings_order_by_list_default));

        // Build the Uri based on the preferences
        Uri baseUri = Uri.parse(INITIAL_QUERY);
        Uri.Builder uriBuilder = baseUri.buildUpon();

        uriBuilder.appendQueryParameter("q", searchQuery);
        uriBuilder.appendQueryParameter("order-by", orderBy);
        uriBuilder.appendQueryParameter("api-key", "test");
        uriBuilder.appendQueryParameter("show-fields", "thumbnail");
        Log.v("MainActivity.java", "Uri: " + uriBuilder);

        // Create a new loader with the supplied Url
        return new NewsLoader(this, uriBuilder.toString());
    }

    @Override
    public void onLoadFinished(Loader<List<News>> loader, List<News> data) {
        // If there is a valid list of {@link BookItem}s, then add them to the adapter's
        if (data != null && !data.isEmpty()) {
            newsAdapter.addAll(data);
            // Hide loading indicator because the data has been loaded
            progressBar.setVisibility(View.GONE);
            // Hide message text
            emptyState.setText(null);
        } else {
            // Hide loading indicator because the data has been loaded
            progressBar.setVisibility(View.GONE);
            // Set message text to display "No articles found!"
            emptyState.setText(getString(R.string.no_articles_found));
        }
    }

    @Override
    public void onLoaderReset(Loader<List<News>> loader) {
        newsAdapter.clearAll();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            Intent settingsIntent = new Intent(this, SettingsActivity.class);
            startActivity(settingsIntent);
            return true;
        } else if (id == R.id.menu_refresh) {
            recyclerViewRefresh();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
