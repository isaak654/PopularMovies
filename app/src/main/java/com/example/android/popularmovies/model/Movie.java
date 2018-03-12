package com.example.android.popularmovies.model;

import org.parceler.Parcel;

/**
 * A {@link Movie} object contains information related to a single movie.
 */
@Parcel
public class Movie {

    //Title of the movie
    String mTitle;

    /**
     * Date of the movie
     */
    String mDate;

    //Image of the movie
    String mImage;

    /**
     * Vote of the movie
     */
    Double mVote;

    /**
     * Plot of the movie
     */
    String mPlot;


    public Movie() {
        // empty constructor
    }


    /**
     * Constructs a new {@link Movie} object.
     *
     * @param title  is the title of the movie
     * @param date  is the date of the movie
     * @param image  is the image of the movie
     * @param vote  is the vote of the movie
     * @param plot is the plot of the movie
     */
    public Movie(String title, String date, String image, Double vote, String plot) {
        mTitle = title;
        mDate = date;
        mImage = image;
        mVote = vote;
        mPlot = plot;

    }

    /**
     * Returns the title of the movie
     */
    public String getTitle() {
        return mTitle;
    }

    /**
     * Returns the date of the movie.
     */
    public String getDate() {
        return mDate;
    }

    /**
     * Returns the image of the movie
     */
    public String getImage() {
        return mImage;
    }

    /**
     * Returns the vote of the movie
     */
    public Double getVote() {
        return mVote;
    }

    /**
     * Returns the plot of the movie.
     */
    public String getPlot() {
        return mPlot;
    }


}