package com.example.android.popularmovies;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.app.Fragment;
import android.support.v4.content.res.ResourcesCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.android.popularmovies.model.Movie;
import com.squareup.picasso.Picasso;

import org.parceler.Parcels;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * A fragment representing a single Movie detail screen.
 * This fragment is either contained in a {@link MovieListActivity}
 * in two-pane mode (on tablets) or a {@link MovieDetailActivity}
 * on handsets.
 */
public class MovieDetailFragment extends Fragment {
    /**
     * The fragment argument representing the item ID that this fragment
     * represents.
     */
    public static final String ARG_ITEM_ID = "MovieDetailFragment";

    public static final String MOVIE_OBJ = "MovieObj";
    public static final String MOVIES_LIST = "MoviesList";

    private Movie movie;
    Context mContext = getContext();

    private CollapsingToolbarLayout appBarLayout;


    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public MovieDetailFragment() {

    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Activity activity = this.getActivity();
        if (activity != null) {
            appBarLayout = activity.findViewById(R.id.toolbar_layout);
            movie = Parcels.unwrap(activity.getIntent().getParcelableExtra(MovieDetailFragment.MOVIE_OBJ));
            if (appBarLayout != null) {
                appBarLayout.setTitle(movie.getTitle());
            }
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_movie_detail, container, false);

        // Display title in details
        ((TextView) rootView.findViewById(R.id.movie_title)).setText(movie.getTitle());

        // Display average vote in details
        ((TextView) rootView.findViewById(R.id.vote_average)).setText(String.valueOf(movie.getVote()));

        // Display released date in details
        try {
            Date releaseDate = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).parse(movie.getDate());
            DateFormat formatter = DateFormat.getDateInstance(DateFormat.LONG, Locale.getDefault());
            String formattedDate = formatter.format(releaseDate);
            ((TextView) rootView.findViewById(R.id.release_date)).setText(formattedDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        // Display poster image in details
        ImageView posterImage;
        posterImage = rootView.findViewById(R.id.movie_poster);

        /****************
         * Poster Image *
         ****************/
        String imageBaseUrl = "https://image.tmdb.org/t/p/w300";
        String imageFinalUrl = movie.getImage();
        String finalImageUrl = imageBaseUrl + imageFinalUrl;

        // Initialize placeholder drawable once
        Drawable mPlaceholderDrawable = ResourcesCompat.getDrawable(getResources(),R.drawable.ic_placeholder_black, null);

        Picasso.with(mContext)
                .load(finalImageUrl)
                .placeholder(mPlaceholderDrawable)
                .error(R.drawable.ic_error_black)
                .into(posterImage);

        // Display plot in details
        ((TextView) rootView.findViewById(R.id.overview)).setText(movie.getPlot());

        return rootView;
    }
}