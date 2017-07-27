package com.example.android.nouvelles;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.text.TextUtils;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

/**
 * Helper methods related to requesting and receiving articles data from The Guardian API
 */

public class NewsQueryUtils {

    // Tag for the log messages
    static final String LOG_TAG = NewsQueryUtils.class.getSimpleName();
    // Keys for JSON parsing
    private static final String KEY_RESPONSE = "response";
    private static final String KEY_RESULTS = "results";
    private static final String KEY_TITLE = "webTitle";
    private static final String KEY_SECTION = "sectionName";
    private static final String KEY_DATE = "webPublicationDate";
    private static final String KEY_IMAGE_FIELDS = "fields";
    private static final String KEY_IMAGE_THUMBNAIL = "thumbnail";
    private static final String KEY_WEB_URL = "webUrl";

    /**
     * Create a private constructor because no one should ever create a {@link NewsQueryUtils} object.
     * This class is only meant to hold static variables and methods, which can be accessed
     * directly from the class name NewsQueryUtils (and an object instance of NewsQueryUtils is not needed).
     */
    private NewsQueryUtils() {
    }

    /**
     * Query the API (in this case The Guardian API) data set and return a list of {@link News} objects.
     */
    public static List<News> fetchNewsData(String requestUrl) {
        // Create Url Object
        URL url = createURL(requestUrl);

        // Perform HTTP request to the url and get a JSON response
        String jsonResponse = null;
        try {
            jsonResponse = makeHTTPRequest(url);
        } catch (IOException e) {
            Log.e(LOG_TAG, "Error closing input stream", e);
        }

        /** Extract relevant fields from the JSON response and create a list of {@link News} */
        List<News> newsListObject = extractNewsData(jsonResponse);

        // Return the list of News
        return newsListObject;
    }

    // Returns new URL object from the given string URL.
    private static URL createURL(String requestUrl) {
        URL url = null;
        try {
            url = new URL(requestUrl);
        } catch (MalformedURLException e) {
            Log.e(LOG_TAG, "Error Creating URL", e);
        }
        return url;
    }

    // Make an HTTP request to the given URL and return a String as the response.
    private static String makeHTTPRequest(URL url) throws IOException {
        // If the url is empty, return early
        String jsonResponse = null;
        if (url == null) {
            return jsonResponse;
        }

        HttpURLConnection urlConnection = null;
        InputStream inputStream = null;

        try {
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setReadTimeout(1000/* milliseconds */);
            urlConnection.setConnectTimeout(1500/* milliseconds */);
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();

            // If the request is successful (response code 200) then read the input stream
            // and parse the response
            if (urlConnection.getResponseCode() == 200) {
                inputStream = urlConnection.getInputStream();
                jsonResponse = readFromInputStream(inputStream);
            } else {
                Log.e(LOG_TAG, "Error response code " + urlConnection.getResponseCode());
            }
        } catch (IOException e) {
            Log.e(LOG_TAG, " Problem retrieving the JSON results", e);
        } finally {
            // Close the connection
            if (urlConnection != null) {
                urlConnection.disconnect();
            }

            // Close stream
            if (inputStream != null) {
                inputStream.close();
            }
        }
        return jsonResponse;
    }

    /**
     * Convert the {@link InputStream} into a String which contains the
     * whole JSON response from the server.
     */
    private static String readFromInputStream(InputStream inputStream) throws IOException {
        StringBuilder streamOutput = new StringBuilder();
        if (inputStream != null) {
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream, Charset.forName("UTF-8"));
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
            String line = bufferedReader.readLine();
            while (line != null) {
                streamOutput.append(line);
                line = bufferedReader.readLine();
            }
        }
        return streamOutput.toString();

    }

    /**
     * Return a list of {@link News} objects that has been built up from
     * parsing the given JSON response.
     */
    private static List<News> extractNewsData(String jsonResponse) {


        // If the JSON string is empty or null, then return early.
        if (TextUtils.isEmpty(jsonResponse)) {
            return null;
        }

        // Create an empty List<News> that we can start adding news to
        List<News> newsList = new ArrayList<>();

        // Try to parse the JSON response string. If there's a problem with the way the JSON
        // is formatted, a JSONException exception object will be thrown.
        // Catch the exception so the app doesn't crash, and print the error message to the logs.
        try {
            // Create a JSONObject from the JSON response string
            JSONObject baseJsonObject = new JSONObject(jsonResponse);
            JSONObject responseJsonObject = baseJsonObject.getJSONObject(KEY_RESPONSE);
            // Extract the JSONArray associated with the key called "results",
            // which represents a list of features (or earthquakes).
            JSONArray newsArray = responseJsonObject.getJSONArray(KEY_RESULTS);

            String title; // title of the article
            String section; // topic of the article
            String pubDate; // date of publish
            String webUrl; // web url of the article
            String thumbnailUrl; // thumbnail image of the article
            Bitmap image = null;

            // For each article in the earthquakeArray, create an {@link News} object
            for (int i = 0; i < newsArray.length(); i++) {
                // Get a single news(article) at position i within the list of news(articles)
                JSONObject currentNewsItem = newsArray.getJSONObject(i);
                // Check if a title exists
                if (currentNewsItem.has(KEY_TITLE)) {
                    // Extract the value for the key called "webTitle"
                    title = currentNewsItem.getString(KEY_TITLE);
                } else {
                    title = null;
                }
                // Check if section exists
                if (currentNewsItem.has(KEY_SECTION)) {
                    // Extract the value for the key called "sectionName"
                    section = currentNewsItem.getString(KEY_SECTION);
                } else {
                    section = null;
                }
                // Check if date exists
                if (currentNewsItem.has(KEY_DATE)) {
                    // Extract the value for the key called "webPublicationDate"
                    pubDate = currentNewsItem.getString(KEY_DATE);
                } else {
                    pubDate = null;
                }
                // Check if url exists
                if (currentNewsItem.has(KEY_WEB_URL)) {
                    // Extract the value for the key called "webUrl"
                    webUrl = currentNewsItem.getString(KEY_WEB_URL);
                } else {
                    webUrl = null;
                }
                // Check if image exists
                if (currentNewsItem.has(KEY_IMAGE_FIELDS)) {
                    // For a given news/article, extract the JSONObject associated with the
                    // key called "fields", which represents a list of relative information
                    // about the image/thumbnail
                    JSONObject imageFields = currentNewsItem.getJSONObject(KEY_IMAGE_FIELDS);
                    // Extract the value for the key called "thumbnail"
                    thumbnailUrl = imageFields.getString(KEY_IMAGE_THUMBNAIL);
                    // Decode the image
                    URL imgUrl = new URL(thumbnailUrl);
                    image = BitmapFactory.decodeStream(imgUrl.openConnection().getInputStream());
                } else {
                    thumbnailUrl = null;
                }

                /**
                 * Create a new {@link News} object with article title, topic, web url,
                 * date of publish and image from the JSON response
                 */
                News newsItem = new News(title, section, webUrl, pubDate, image);
                // Add the new News object to the list of News Objects
                newsList.add(newsItem);
            }
        } catch (JSONException e) {
            // If an error is thrown when executing any of the above statements in the "try" block,
            // catch the exception here, so the app doesn't crash. Print a log message
            // with the message from the exception.
            Log.e(LOG_TAG, "Problem parsing the JSON results", e);
        } catch (MalformedURLException e) {
            // If an error is thrown when executing any of the above statements in the "try" block,
            // catch the exception here, so the app doesn't crash. Print a log message
            // with the message from the exception.
            Log.e(LOG_TAG, "Problem making the image URL", e);
        } catch (IOException e) {
            // If an error is thrown when executing any of the above statements in the "try" block,
            // catch the exception here, so the app doesn't crash. Print a log message
            // with the message from the exception.
            Log.e(LOG_TAG, "Problem connecting to retrieve the image", e);
        }

        // Return the list of News objects
        return newsList;
    }
}
