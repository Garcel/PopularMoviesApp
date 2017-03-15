package com.imentec.popularmoviesapp;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Parcelable;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.imentec.popularmoviesapp.adapter.MoviesAdapter;
import com.imentec.popularmoviesapp.callback.FetchMoviesCallback;
import com.imentec.popularmoviesapp.db.MovieDao;
import com.imentec.popularmoviesapp.model.Movie;
import com.imentec.popularmoviesapp.model.MovieResponse;
import com.imentec.popularmoviesapp.utilities.NetworkUtils;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/*
 * Copyright 2017 José Antonio Garcel
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

/**
 * Main activity.java -
 *
 * @author jagarcel
 * @date 03/02/2017
 */
public class MainActivity extends AppCompatActivity implements MoviesAdapter.GridItemClickListener, MoviesAdapter.GridItemBindListener, FetchMoviesCallback, AdapterView.OnItemSelectedListener {

    private static final String MOVIE = "movie";
    private static final String MOVIE_ID = "movieId";
    private static final String MOVIES = "movies";

    @BindView(R.id.rv_movies) RecyclerView mMoviesList;
    private MoviesAdapter mAdapter;

    // shows a progress bar until data has been collected from the API
    @BindView(R.id.pb_loading_indicator) ProgressBar searchProgressBar;
    @BindView(R.id.tv_error_message_display) TextView errorTextView;

    private Spinner spinner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        // set different number of columns based on the device orientation
        int gridColumns = calculateGridColumns();
        GridLayoutManager layoutManager = new GridLayoutManager (this, gridColumns);
        mMoviesList.setLayoutManager(layoutManager);

        mAdapter = new MoviesAdapter(this, this);
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

        MenuItem item = menu.findItem(R.id.movies_spinner_item);
        spinner = (Spinner) MenuItemCompat.getActionView(item);

        // start the spinner
        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.movies_source, android.R.layout.simple_spinner_item);

        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // attach listeners
        spinner.setOnItemSelectedListener(this);

        // Apply the adapter to the spinner
        spinner.setAdapter(adapter);

        // fetch the movies and start the app!!
        int previousPosition = readState();
        spinner.setSelection(previousPosition);
        return true;
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

        NetworkUtils.getService().fetchMovies(filter, getString(R.string.THE_MOVIE_DB_API_TOKEN)).enqueue(onMoviesCallback());
    }

    /**
     * This is where we receive our callback from
     * {@link MoviesAdapter.GridItemClickListener}
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
        int currentPos = spinner.getSelectedItemPosition();
        saveState(currentPos);
        if (currentPos == 2) intent.putExtra(MOVIE_ID, mAdapter.getMovies().get(clickedItemIndex).getMovieId());
        else intent.putExtra(MOVIE, mAdapter.getMovies().get(clickedItemIndex));
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
        Log.i(this.getClass().getName(), "Loading image: " + mAdapter.getMovies().get(itemIndex).getPosterPath());

        String posterPath = mAdapter.getMovies().get(itemIndex).getPosterPath();
        String url = NetworkUtils.buildImageURL(posterPath).toString();

        Picasso.with(this)
                .load(url)
                .placeholder(R.drawable.image_placeholder)
                .error(R.drawable.image_placeholder)
                .into(imageView);
    }

    @Override
    protected void onSaveInstanceState (Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putParcelableArrayList(MOVIES, (ArrayList<? extends Parcelable>) mAdapter.getMovies());
    }

    /**
     * Callback used to recover the List<Movie> form the HTTP response.
     */
    @Override
    public Callback<MovieResponse> onMoviesCallback() {
        return new Callback<MovieResponse>() {
            @Override
            public void onResponse(Call<MovieResponse> call, Response<MovieResponse> response) {
                if (response != null && response.isSuccessful()) {
                    MovieResponse movieResponse = response.body();
                    initLayout(movieResponse.getMovies());

                } else showErrorMessage();
            }

            @Override
            public void onFailure(Call<MovieResponse> call, Throwable t) {
                showErrorMessage();
            }
        };
    }


    /**
     * Handle the menu items to fetch the most popular or top rated movies according to the user desire ^^.
     *
     * @param parent
     * @param view
     * @param position
     * @param id
     */
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        String[] sources = getResources().getStringArray(R.array.movies_source);
        if (position > sources.length - 1) return;

        if (sources[position].equals(getString(R.string.action_sort_popular_movies))) {
            Log.v(this.getClass().getName(), "User requested to see the most popular movies");
            errorTextView.setVisibility(View.INVISIBLE);
            searchProgressBar.setVisibility(View.VISIBLE);
            loadMoviesData(NetworkUtils.POPULAR_MOVIES);

        } else if (sources[position].equals(getString(R.string.action_sort_top_rated_movies))) {
            Log.v(this.getClass().getName(), "User requested to see the top rated movies");
            errorTextView.setVisibility(View.INVISIBLE);
            searchProgressBar.setVisibility(View.VISIBLE);
            loadMoviesData(NetworkUtils.TOP_RATED);

        } else if (sources[position].equals(getString(R.string.action_sort_favorites_movies))) {
            Log.v(this.getClass().getName(), "User requested to see the favourites movies");
            Log.v(this.getClass().getName(), "DB contains " + MovieDao.getAllMovies().size() + " records!");
            Log.v(this.getClass().getName(), MovieDao.getAllMovies().toString());
            initLayout(MovieDao.getAllMovies());
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
        // should never happen!
    }

    private void saveState(int position) {
        SharedPreferences sharedPreferences = this.getSharedPreferences("MoviePreferences", Context.MODE_PRIVATE);
        SharedPreferences.Editor SharedPreferencesEdit = sharedPreferences.edit();
        SharedPreferencesEdit.putInt("spinnerPosition", position);
        SharedPreferencesEdit.commit();
    }

    private int readState() {
        SharedPreferences sharedPreferences = this.getSharedPreferences("MoviePreferences", Context.MODE_PRIVATE);
        return sharedPreferences.getInt("spinnerPosition", 0);
    }
}