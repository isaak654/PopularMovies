package com.example.android.popularmovies;

import android.app.LoaderManager;
import android.content.Context;
import android.content.Intent;
import android.content.Loader;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.example.android.popularmovies.model.Movie;

import java.util.ArrayList;
import java.util.List;

import static com.example.android.popularmovies.utils.QueryUtils.API_KEY_VARIABLE;
import static com.example.android.popularmovies.utils.QueryUtils.MOVIE_BASE_URL;
import static com.example.android.popularmovies.utils.QueryUtils.MOVIE_POPULAR_ENDPOINT;
import static com.example.android.popularmovies.utils.QueryUtils.MOVIE_TOP_RATED_ENDPOINT;

/**
 * An activity representing a list of Movies. This activity
 * has different presentations for handset and tablet-size devices. On
 * handsets, the activity presents a list of items, which when touched,
 * lead to a {@link MovieDetailActivity} representing
 * item details. On tablets, the activity presents the list of items and
 * item details side-by-side using two vertical panes.
 */
public class MovieListActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<List<Movie>> {

    public static final String LOG_TAG = MovieListActivity.class.getName();

    /**
     * Whether or not the activity is in two-pane mode, i.e. running on a tablet
     * device.
     */
    private boolean mTwoPane;

    /**
     * TextView that is displayed when the list is empty
     */
    private TextView mEmptyStateTextView;

    /**
     * Constant value for the movies loader ID. We can choose any integer.
     * This really only comes into play if you're using multiple loaders.
     */
    private static final int MOVIE_LOADER_ID = 1;

    private List<Movie> mMovies = new ArrayList<>();
    private MovieAdapter mMovieAdapter;
    private RecyclerView mRecyclerView;
    //private Movie mMovie;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_list);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitle(getTitle());

        // Empty view
        mEmptyStateTextView = findViewById(R.id.empty_view);


        if (findViewById(R.id.movie_detail_container) != null) {
            // The detail container view will be present only in the
            // large-screen layouts (res/values-w900dp).
            // If this view is present, then the
            // activity should be in two-pane mode.
            mTwoPane = true;
        }

        /*
         * Using findViewById, we get a reference to our RecyclerView from xml. This allows us to
         * do things like set the adapter of the RecyclerView and toggle the visibility.
         */

        mRecyclerView = findViewById(R.id.movie_list);

        // setLayoutManager to GridLayoutManager using 2 columns
        int numberOfColumns = 2;
        mRecyclerView.setLayoutManager(new GridLayoutManager(this, numberOfColumns));

        /*
         * Use this setting to improve performance if you know that changes in content do not
         * change the child layout size in the RecyclerView
         */
        mRecyclerView.setHasFixedSize(true);

        /*
         * The MovieAdapter is responsible for linking our movies data with the View that
         * will end up displaying our movie data.
         */
        mMovieAdapter = new MovieAdapter(this, mMovies, mTwoPane, this);

        /* Setting the adapter attaches it to the RecyclerView in our layout. */
        mRecyclerView.setAdapter(mMovieAdapter);

        // Clear the adapter of previous movies data
        mMovies.clear();
        // Notify the adapter
        mMovieAdapter.notifyItemRangeRemoved(0, mMovieAdapter.getItemCount());

        // If there is a network connection, fetch data
        if (isConnected()) {

            // Get a reference to the LoaderManager, in order to interact with loaders.
            LoaderManager loaderManager = getLoaderManager();

            // Initialize the loader. Pass in the int ID constant defined above and pass in null for
            // the bundle. Pass in this activity for the LoaderCallbacks parameter (which is valid
            // because this activity implements the LoaderCallbacks interface).
            loaderManager.initLoader(MOVIE_LOADER_ID, null, this);

        } else {


            // Otherwise, display error
            // First, hide loading indicator so error message will be visible
            View loadingIndicator = findViewById(R.id.loading_indicator);
            loadingIndicator.setVisibility(View.GONE);

            // Update empty state with no connection error message
            mEmptyStateTextView.setText(R.string.no_internet_connection);

        }
    }


    // Helper method to check network connection
    public boolean isConnected() {
        ConnectivityManager connMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        return (networkInfo != null && networkInfo.isConnectedOrConnecting());
    }


    /**
     * This is where we inflate and set up the menu for this Activity.
     *
     * @param menu The options menu in which you place your items.
     * @return You must return true for the menu to be displayed;
     * if you return false it will not be shown.
     * @see #onPrepareOptionsMenu
     * @see #onOptionsItemSelected
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //Clears the menu items already inflated in the activity.
        menu.clear();
        /* Use the inflater's inflate method to inflate our menu layout to this menu */
        getMenuInflater().inflate(R.menu.movie_list, menu);
        /* Return true so that the menu is displayed in the Toolbar */
        return true;
    }

    /**
     * Callback invoked when a menu item was selected from this Activity's menu.
     *
     * @param item The menu item that was selected by the user
     * @return true if you handle the menu click here, false otherwise
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            startActivity(new Intent(this, SettingsActivity.class));
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public Loader<List<Movie>> onCreateLoader(int i, Bundle bundle) {

        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(this);
        String key = sharedPrefs.getString(getString(R.string.pref_order_by_key), "0");

        // Network API implementation to retrieve popular or top rated movies
        // for the sort criteria specified in the settings menu.
        Uri baseUri = Uri.parse(MOVIE_BASE_URL);
        Uri.Builder uriBuilder = baseUri.buildUpon();

        String mostPopular = getResources().getStringArray(R.array.pref_order_by_values)[0];
        if (key.equals(mostPopular)) {
            uriBuilder.appendEncodedPath(MOVIE_POPULAR_ENDPOINT + API_KEY_VARIABLE).build();
        } else {
            String topRated = getResources().getStringArray(R.array.pref_order_by_values)[1];
            if (key.equals(topRated)) {
                uriBuilder.appendEncodedPath(MOVIE_TOP_RATED_ENDPOINT + API_KEY_VARIABLE).build();
            }
        }
        return new MovieLoader(this, uriBuilder.toString());
    }

    // Called when a Loader has finished loading its data.
    @Override
    public void onLoadFinished(Loader<List<Movie>> loader, List<Movie> movies) {

        // Notify the adapter at the end of the array.
//        int position = mMovieAdapter.getItemCount() - 1;
//        if (position != 0) {
//            mMovieAdapter.notifyItemChanged(position);
//        } else {
//
//        }


        if (movies != null || !movies.isEmpty()) {
            // Hide loading indicator because the data has been loaded
            View loadingIndicator = findViewById(R.id.loading_indicator);
            loadingIndicator.setVisibility(View.GONE);
        } else {
            // Set empty state text to display "No movies found."
            mEmptyStateTextView.setText(R.string.no_movies);
        }

        // Clear the adapter of previous movies data
        mMovies.clear();
        // Add movies into a list
        mMovies.addAll(movies);
        // Notify the adapter
        mMovieAdapter.notifyItemRangeInserted(mMovieAdapter.getItemCount() + 1, mMovieAdapter.getItemCount());
    }

    @Override
    public void onLoaderReset(Loader<List<Movie>> loader) {
        // Clear the adapter of previous movies data
        mMovies.clear();
        // Notify the adapter
        mMovieAdapter.notifyItemRangeRemoved(0, mMovieAdapter.getItemCount());
    }
}