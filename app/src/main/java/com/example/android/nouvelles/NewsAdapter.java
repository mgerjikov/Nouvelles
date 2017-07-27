package com.example.android.nouvelles;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * Custom Adapter that knows how to create a list item layout for each news/article
 * in the data source which is a list of {@link News} objects.
 * ViewHolder supporting the use of the RecyclerView
 */

public class NewsAdapter extends RecyclerView.Adapter<NewsAdapter.ViewHolder> {

    // Tag for the log messages
    public static final String LOG_TAG = NewsAdapter.class.getSimpleName();
    private List<News> mNews;
    private Context mContext;
    private String mUrl;

    // Pass in the contact array into the constructor
    public NewsAdapter(Context context, List<News> newsList) {
        mContext = context;
        mNews = newsList;
    }

    // Involves inflating a layout from XML and returning the holder
    @Override
    public NewsAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        // Inflate the custom layout
        View view = inflater.inflate(R.layout.list_item, parent, false);

        // Return a new ViewHolder instance
        ViewHolder viewHolder = new ViewHolder(mContext, view);
        return viewHolder;
    }

    // Involves populating data into the item through the holder
    @Override
    public void onBindViewHolder(NewsAdapter.ViewHolder holder, int position) {
        // Get the data model based on position
        News newsItem = mNews.get(position);

        // Set item views based on views and data model
        TextView newsTitleTV = holder.newsTitle;
        TextView newsSectionTV = holder.newsSection;
        TextView newsPubDate = holder.pubDate;
        TextView newsPubTime = holder.pubTime;
        ImageView newsImageV = holder.bitmapImage;

        newsTitleTV.setText(newsItem.getmTitle());
        newsSectionTV.setText(newsItem.getmSection());
        newsPubTime.setText(convertTimeFormat(newsItem.getmPublishedDate()));
        newsPubDate.setText(convertDateFormat(newsItem.getmPublishedDate()));
        newsImageV.setImageBitmap(newsItem.getmNewsImage());
    }

    // Convert json DateTime to Date and Time
    public String convertDateFormat(String input) {
        input = input.substring(0, input.length() - 1);
        String oldFormat = "yyyy-MM-dd'T'HH:mm:ss";
        String newFormat = "dd/MM/yyyy";
        SimpleDateFormat inputFormat = new SimpleDateFormat(oldFormat);
        SimpleDateFormat outputFormat = new SimpleDateFormat(newFormat);
        Date date = null;
        String output = "";
        try {
            date = inputFormat.parse(input);
            output = outputFormat.format(date);
        } catch (ParseException e) {
            Log.e(LOG_TAG, "DateTime parse exception: " + e);
        }
        return output;
    }

    public String convertTimeFormat(String input) {
        input = input.substring(0, input.length() - 1);
        String oldFormat = "yyyy-MM-dd'T'HH:mm:ss";
        String newFormat = "HH:mm";
        SimpleDateFormat inputFormat = new SimpleDateFormat(oldFormat);
        SimpleDateFormat outputFormat = new SimpleDateFormat(newFormat);
        Date date = null;
        String output = "";
        try {
            date = inputFormat.parse(input);
            output = outputFormat.format(date);
        } catch (ParseException e) {
            Log.e(LOG_TAG, "DateTime parse exception: " + e);
        }
        return output;
    }

    // Returns the total count of items in the list
    @Override
    public int getItemCount() {
        return mNews.size();
    }

    // Adds new items to mNews and refreshes the layout
    public void addAll(List<News> newsList) {
        mNews.clear();
        mNews.addAll(newsList);
        notifyDataSetChanged();
    }

    // Clears mNews
    public void clearAll() {
        mNews.clear();
        notifyDataSetChanged();
    }

    /**
     * Provide a direct refrence to each of the views within a list_item
     * Used to cache the views within the item layout for fast access
     */
    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        // The ViewHolder holds a variable for every View that will be used
        public TextView newsTitle;
        public TextView newsSection;
        public TextView pubDate;
        public TextView pubTime;
        public ImageView bitmapImage;
        private Context context;

        /**
         * CONSTRUCTOR
         * <p>
         * Accepts the entire item row and does the view lookups
         * to find each subview
         */
        public ViewHolder(Context context, View itemView) {
            // Stores the itemView in a public final member variable that can be used
            // to access the context from any ViewHolder instance.
            super(itemView);
            // Store the context
            this.context = context;
            // Attach a click listener to the entire row view
            itemView.setOnClickListener(this);

            newsTitle = (TextView) itemView.findViewById(R.id.title);
            newsSection = (TextView) itemView.findViewById(R.id.section);
            pubDate = (TextView) itemView.findViewById(R.id.date);
            pubTime = (TextView) itemView.findViewById(R.id.time);
            bitmapImage = (ImageView) itemView.findViewById(R.id.news_thumbnail);

        }

        @Override
        public void onClick(View v) {
            int position = getAdapterPosition();
            News news = mNews.get(position);

            // Get the Url from the current News/article
            mUrl = news.getmWebUrl();

            // Convert the String URL into a URI object (to pass into the Intent constructor)
            Uri uri = Uri.parse(mUrl);
            // Create new intent to view the article's URL
            Intent webIntent = new Intent(Intent.ACTION_VIEW, uri);
            // Start the intent
            context.startActivity(webIntent);

        }
    }

}
