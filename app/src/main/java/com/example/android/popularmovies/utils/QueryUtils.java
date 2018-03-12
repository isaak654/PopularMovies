package com.example.android.popularmovies.utils;

import android.text.TextUtils;
import android.util.Log;

import com.example.android.popularmovies.BuildConfig;
import com.example.android.popularmovies.model.Movie;

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
 * Helper methods related to requesting and receiving movie data from The Movie Database API.
 */
public class QueryUtils {

    /**
     * Tag for the log messages
     */
    private static final String LOG_TAG = QueryUtils.class.getSimpleName();

    /**
     * Base URL for movies data
     */
    public static final String MOVIE_BASE_URL = "https://api.themoviedb.org/3/";

    /**
     * Endpoint for popular movies
     */
    public static final String MOVIE_POPULAR_ENDPOINT = "movie/popular";

    /**
     * Endpoint for top rated movies
     */
    public static final String MOVIE_TOP_RATED_ENDPOINT = "movie/top_rated";

    /**
     * API Key variable
     */
    public static final String API_KEY_VARIABLE = "?api_key=" + BuildConfig.API_KEY;







    /**
     * Create a private constructor because no one should ever create a {@link QueryUtils} object.
     * This class is only meant to hold static variables and methods, which can be accessed
     * directly from the class name QueryUtils (and an object instance of QueryUtils is not needed).
     */
    private QueryUtils() {
    }

    /**
     * Query the Movie Database dataset and return a list of {@link Movie} objects.
     */
    public static List<Movie> fetchMovieData(String requestUrl) {
        // Create URL object
        URL url = createUrl(requestUrl);

        // Perform HTTP request to the URL and receive a JSON response back
        String jsonResponse = null;
        try {
            jsonResponse = makeHttpRequest(url);
        } catch (IOException e) {
            Log.e(LOG_TAG, "Problem making the HTTP request.", e);
        }

        // Return the list of {@link Movie}s
        return extractMovieFromJson(jsonResponse);
    }

    /**
     * Returns new URL object from the given string URL.
     */
    private static URL createUrl(String stringUrl) {
        URL url = null;
        try {
            url = new URL(stringUrl);
        } catch (MalformedURLException e) {
            Log.e(LOG_TAG, "Problem building the URL ", e);
        }
        return url;
    }

    /**
     * Make an HTTP request to the given URL and return a String as the response.
     */
    private static String makeHttpRequest(URL url) throws IOException {
        String jsonResponse = "";

        // If the URL is null, then return early.
        if (url == null) {
            return jsonResponse;
        }

        HttpURLConnection urlConnection = null;
        InputStream inputStream = null;
        int connectionReadTimeout = 10000; /* milliseconds */
        int connectionTimeout = 15000; /* milliseconds */

        try {
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setReadTimeout(connectionReadTimeout);
            urlConnection.setConnectTimeout(connectionTimeout);
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();

            // If the request was successful (response code 200),
            // then read the input stream and parse the response.
            if (urlConnection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                inputStream = urlConnection.getInputStream();
                jsonResponse = readFromStream(inputStream);
            } else {
                Log.e(LOG_TAG, "Error response code: " + urlConnection.getResponseCode());
            }
        } catch (IOException e) {
            Log.e(LOG_TAG, "Problem retrieving the JSON results.", e);
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (inputStream != null) {
                // Closing the input stream could throw an IOException, which is why
                // the makeHttpRequest(URL url) method signature specifies than an IOException
                // could be thrown.
                inputStream.close();
            }
        }
        return jsonResponse;
    }

    /**
     * Convert the {@link InputStream} into a String which contains the
     * whole JSON response from the server.
     */
    private static String readFromStream(InputStream inputStream) throws IOException {
        StringBuilder output = new StringBuilder();
        if (inputStream != null) {
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream, Charset.forName("UTF-8"));
            BufferedReader reader = new BufferedReader(inputStreamReader);
            String line = reader.readLine();
            while (line != null) {
                output.append(line);
                line = reader.readLine();
            }
        }
        return output.toString();
    }

    /**
     * Return a list of {@link Movie} objects that has been built up from
     * parsing the given JSON response.
     */
    public static List<Movie> extractMovieFromJson(String movieJSON) {
        // If the JSON string is empty or null, then return early.
        if (TextUtils.isEmpty(movieJSON)) {
            return null;
        }

        // List of JSON constants
        final String JSON_RESULTS_ARRAY = "results";
        final String JSON_TITLE_KEY = "title";
        final String JSON_RELEASE_DATE = "release_date";
        final String JSON_POSTER_PATH = "poster_path";
        final String JSON_VOTE_AVERAGE = "vote_average";
        final String JSON_OVERVIEW = "overview";


        // Create an empty ArrayList that we can start adding movies
        List<Movie> movies = new ArrayList<>();

        // Try to parse the JSON response string. If there's a problem with the way the JSON
        // is formatted, a JSONException exception object will be thrown.
        // Catch the exception so the app doesn't crash, and print the error message to the logs.
        try {

            // Create a JSONObject from the JSON response string
            JSONObject baseJsonResponse = new JSONObject(movieJSON);

            // Extract the JSONArray associated with the key called "movies",
            // which represents a list of movies.
            JSONArray movieArray = baseJsonResponse.getJSONArray(JSON_RESULTS_ARRAY);

            //For each movie in the movieArray, create an {@link Movie} object
            for (int i = 0; i < movieArray.length(); i++) {
                //Get a single movie and position it within the list of movies
                JSONObject currentMovie = movieArray.getJSONObject(i);
                // Extract the value for the key called "title"
                String title = currentMovie.optString(JSON_TITLE_KEY);
                // Extract the value for the key called "release_date" if it exists
                String date;
                if (currentMovie.has(JSON_RELEASE_DATE)) {
                    date = currentMovie.optString(JSON_RELEASE_DATE);
                } else {
                    date = "No date";
                }
                // Extract the value for the key called "poster_path" if it exists
                String image;
                if (currentMovie.has(JSON_POSTER_PATH)) {
                    image = currentMovie.optString(JSON_POSTER_PATH);
                } else {
                    image = "No poster";
                }
                // Extract the value for the key called "vote_average" if it exists
                Double vote;
                if (currentMovie.has(JSON_VOTE_AVERAGE)) {
                    vote = currentMovie.optDouble(JSON_VOTE_AVERAGE);
                } else {
                    vote = currentMovie.optDouble("0");
                }
                // Extract the value for the key called "overview" if it exists
                String plot;
                if (currentMovie.has(JSON_OVERVIEW)) {
                    plot = currentMovie.optString(JSON_OVERVIEW);
                } else {
                    plot = "No plot";
                }

                // Create a new {@link Movie} object with the title, date, image,
                // vote and plot from the JSON response.
                Movie movie = new Movie(title, date, image, vote, plot);
                movies.add(movie);
            }

        } catch (JSONException e) {
            // If an error is thrown when executing any of the above statements in the "try" block,
            // catch the exception here, so the app doesn't crash. Print a log message
            // with the message from the exception.
            Log.e("QueryUtils", "Problem parsing the JSON results", e);
        }

        // Return the list of movies
        return movies;
    }

}
