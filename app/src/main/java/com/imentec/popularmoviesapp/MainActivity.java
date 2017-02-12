package com.imentec.popularmoviesapp;

import android.content.Intent;
import android.os.Parcelable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.imentec.popularmoviesapp.model.Movie;
import com.imentec.popularmoviesapp.task.FetchMoviesAsyncTask;
import com.imentec.popularmoviesapp.utilities.NetworkUtils;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

/**
 * Main activity.
 *
 * @author jagarcel
 * @date 03/02/2017
 */
public class MainActivity extends AppCompatActivity implements MoviesAdapter.GridItemClickListener, MoviesAdapter.GridItemBindListener, FetchMoviesAsyncTask.OnPostExecuteListener {

    private static final String MOVIE = "movie";
    private static final String MOVIES = "movies";

    private MoviesAdapter mAdapter;
    private RecyclerView mMoviesList;

    // shows a progress bar until data has been collected from the API
    private ProgressBar searchProgressBar;
    private TextView errorTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // init views
        errorTextView = (TextView) findViewById(R.id.tv_error_message_display);
        searchProgressBar = (ProgressBar) findViewById(R.id.pb_loading_indicator);
        mMoviesList = (RecyclerView) findViewById(R.id.rv_movies);

        // set different number of columns based on the device orientation
        int gridColumns = calculateGridColumns();
        GridLayoutManager layoutManager = new GridLayoutManager (this, gridColumns);
        mMoviesList.setLayoutManager(layoutManager);

        mAdapter = new MoviesAdapter(this, this);

        // fetch the movies and start the app!!
        if (savedInstanceState != null) {
            // on device orientation change
            Log.v(this.getClass().getName(), "Recovering previous app state.");
            List<Movie> movies = (List<Movie>) savedInstanceState.get(MOVIES);
            initLayout(movies);

        } else {
            Log.v(this.getClass().getName(), "No previous state found, creating a new one.");
            loadMoviesData(NetworkUtils.POPULAR_MOVIES);
        }
    }

    private void initLayout (List<Movie> movies) {

        mAdapter.setMovies(movies);
        mMoviesList.setAdapter(mAdapter);

        searchProgressBar.setVisibility(View.INVISIBLE);
        mMoviesList.setVisibility(View.VISIBLE);
        errorTextView.setVisibility(View.INVISIBLE);
    }

    /*
     * Dynamically calculates the number of columns for the grid layout according to the device screen,
     * and orientation.
     */
    private int calculateGridColumns () {
        DisplayMetrics displayMetrics = this.getResources().getDisplayMetrics();
        float dpWidth = displayMetrics.widthPixels / displayMetrics.density;
        int numberOfColumns = (int) (dpWidth / 180);
        return numberOfColumns;
    }

    /**
     * Used to add the items to sort the movies when ther user click'em.
     *
     * @param menu
     * @return
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);

        menu.findItem(R.id.action_sort_popular).setChecked(true);

        return true;
    }

    /**
     * Handle the menu items to fetch the most popular or top rated movies according to the user desire ^^
     *
     * @param item
     * @return
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_sort_popular) {
            Log.v(this.getClass().getName(), "User requested to see the most popular movies");
            errorTextView.setVisibility(View.INVISIBLE);
            searchProgressBar.setVisibility(View.VISIBLE);
            loadMoviesData(NetworkUtils.POPULAR_MOVIES);
            item.setChecked(true);
            return true;

        } else if (id == R.id.action_sort_top_rated) {
            Log.v(this.getClass().getName(), "User requested to see the top rated movies");
            errorTextView.setVisibility(View.INVISIBLE);
            searchProgressBar.setVisibility(View.VISIBLE);
            loadMoviesData(NetworkUtils.TOP_RATED);
            item.setChecked(true);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * Shows the error message.
     *
     * Should only happen when internet connectivity fails.
     */
    void showErrorMessage() {
        searchProgressBar.setVisibility(View.INVISIBLE);
        mMoviesList.setVisibility(View.INVISIBLE);
        errorTextView.setVisibility(View.VISIBLE);
    }

    /**
     * Initialize the task used to fetch the movies from internet.
     *
     * @param filter
     */
    private void loadMoviesData(String filter) {
        if (!NetworkUtils.isOnline(this)) {
            Toast toast = Toast.makeText(this, R.string.connectivity_fail, Toast.LENGTH_LONG);
            toast.show();
            return;
        }

        FetchMoviesAsyncTask task = new FetchMoviesAsyncTask(this, getString(R.string.THE_MOVIE_DB_API_TOKEN));
        task.execute(filter);
    }

    /**
     * This is where we receive our callback from
     * {@link com.imentec.popularmoviesapp.MoviesAdapter.GridItemClickListener}
     *
     * This callback is invoked when you click on an item in the grid.
     *
     * @param clickedItemIndex Index in the grid of the item that was clicked.
     */
    @Override
    public void onGridItemClick(int clickedItemIndex) {
        Log.i(this.getClass().getName(), "Clicked item: " + clickedItemIndex);

        Intent intent = new Intent();
        intent.setClass(this, MovieDetailActivity.class);

        // set the clicked item id to the intent, so we can recover it "on the other side" to revoer
        // the movie details
        intent.putExtra(MOVIE, mAdapter.getMovies().get(clickedItemIndex));

        startActivity(intent);
    }

    /**
     * This callback is invoked when the adapter wants to bind a view to one of its grids.
     *
     * @param itemIndex
     * @param imageView
     */
    @Override
    public void onGridItemBind(int itemIndex, ImageView imageView) {
        Log.i(this.getClass().getName(), "Loading image: " + mAdapter.getMovies().get(itemIndex).getPoster_path());

        String posterPath = mAdapter.getMovies().get(itemIndex).getPoster_path();
        String url = NetworkUtils.buildImageURL(posterPath).toString();

        Picasso.with(this).load(url).into(imageView);
    }

    /**
     * Used as callback when {@link FetchMoviesAsyncTask} has finished its work.
     *
     * @param movies
     */
    @Override
    public void onPostExecute(List<Movie> movies) {

        if (movies != null) {
            initLayout(movies);

        } else showErrorMessage();
    }

    @Override
    protected void onSaveInstanceState (Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putParcelableArrayList(MOVIES, (ArrayList<? extends Parcelable>) mAdapter.getMovies());
    }
}