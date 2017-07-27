package com.example.android.nouvelles;

import android.graphics.Bitmap;

/**
 * Created by Martin on 24.6.2017 г..
 * <p>
 * This {@link News} represent a news article object
 * It contains a the title of the article, the section of the article (like politics, music, etc.),
 * the web url, image of the article, date and time of the article.
 */

public class News {

    // Title of the article
    private String mTitle;
    // Topic/section of the article
    private String mSection;
    // Web url for the article
    private String mWebUrl;
    // Published date of the article
    private String mPublishedDate;
    // Image of the article
    private Bitmap mNewsImage;

    /**
     * CONSTRUCTOR
     * <p>
     * Construct a new {@link News} object.
     *
     * @param title         is the title of the article
     * @param section       is the topic of the article
     * @param webUrl        is the web url of the article
     * @param publishedDate is the date that the article was published
     * @param image         is the image of the article (if there is one)
     */
    public News(String title, String section, String webUrl, String publishedDate, Bitmap image) {
        mTitle = title;
        mSection = section;
        mWebUrl = webUrl;
        mPublishedDate = publishedDate;
        mNewsImage = image;
    }

    // Returns the bitmap image of the article
    public Bitmap getmNewsImage() {
        return mNewsImage;
    }

    // Returns the date and time of the article
    public String getmPublishedDate() {
        return mPublishedDate;
    }

    // Returns the topic of the article
    public String getmSection() {
        return mSection;
    }

    // Retuns the title of the article
    public String getmTitle() {
        return mTitle;
    }

    // Returns the web url of the article
    public String getmWebUrl() {
        return mWebUrl;
    }
}
