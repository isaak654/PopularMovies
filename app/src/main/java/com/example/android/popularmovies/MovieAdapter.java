package com.example.android.popularmovies;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.example.android.popularmovies.model.Movie;
import com.squareup.picasso.Picasso;

import org.parceler.Parcels;

import java.util.List;

/**
 * A {@link MovieAdapter} knows how to create a list item layout for each movie
 * in the data source (a list of {@link Movie} objects).
 * These list item layouts will be provided to an adapter view like RecyclerView
 * to be displayed to the user.
 */

public class MovieAdapter
        extends RecyclerView.Adapter<MovieAdapter.ViewHolder> {

    private final MovieListActivity mParentActivity;
    private List<Movie> mMovies;
    private Context mContext;
    private final boolean mTwoPane;
    //private Movie mMovie;
    //private CollapsingToolbarLayout appBarLayout;
    //public static final String MOVIE_KEY = "movie_label";


    // Adapter constructor
    MovieAdapter(MovieListActivity parent,
                 List<Movie> movies,
                 boolean twoPane, @NonNull Activity context) {
        mParentActivity = parent;
        mMovies = movies;
        mTwoPane = twoPane;
        mContext = context;
        //this.mMovie = movie;
        //setHasStableIds(true);
    }


    @Override
    @NonNull
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.movie_list_item, parent, false);

        return new ViewHolder(view);
    }


    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {

        // Get position
        Movie movie = mMovies.get(position);

        /****************
         * Poster Image *
         ****************/
        String imageBaseUrl = "https://image.tmdb.org/t/p/w185";
        String imageFinalUrl = movie.getImage();
        String finalImageUrl = imageBaseUrl + imageFinalUrl;

        // Initialize placeholder drawable once
        Drawable mPlaceholderDrawable = ResourcesCompat.getDrawable(
                mContext.getResources(),
                R.drawable.ic_placeholder_black, null);

        Picasso.with(mContext)
                .load(finalImageUrl)
                .placeholder(mPlaceholderDrawable)
                .error(R.drawable.ic_error_black)
                .into(holder.mImageMovie);
    }

    @Override
    public int getItemCount() {
        if (null == mMovies) return 0;
        return mMovies.size();
    }

    @Override
    public long getItemId(int position) {
        return position;
    }


    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        ImageView mImageMovie;

        ViewHolder(View view) {
            super(view);
            mImageMovie = view.findViewById(R.id.movie_image);
            view.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {

            if (mTwoPane) {
                Bundle args = new Bundle();
                MovieDetailFragment fragment = new MovieDetailFragment();
                args.putParcelable(MovieDetailFragment.MOVIE_OBJ, Parcels.wrap(mMovies.get(getAdapterPosition())));
                fragment.setArguments(args);
                mParentActivity.getSupportFragmentManager().beginTransaction()
                        .replace(R.id.movie_detail_container, fragment)
                        .commit();
            } else {
                Context context = view.getContext();
                Intent intent = new Intent(context, MovieDetailActivity.class);
                intent.putExtra(MovieDetailFragment.MOVIE_OBJ, Parcels.wrap(mMovies.get(getAdapterPosition())));
                context.startActivity(intent);
            }
        }
    }
}