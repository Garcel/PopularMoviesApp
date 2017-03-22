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

package com.imentec.popularmoviesapp.fragment;

import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.imentec.popularmoviesapp.R;
import com.imentec.popularmoviesapp.adapter.MoviesAdapter;
import com.imentec.popularmoviesapp.callback.FetchMoviesCallback;
import com.imentec.popularmoviesapp.model.Movie;
import com.imentec.popularmoviesapp.model.MovieResponse;
import com.imentec.popularmoviesapp.utilities.NetworkUtils;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * RecyclerViewFragment.java - Fragment used to handle the top rated / popular movies activity logic.
 *
 * @author jagarcel
 * @date 17/03/2017
 */
public class RecyclerViewFragment extends BaseFragment implements FetchMoviesCallback {

    @BindView(R.id.rv_movies)
    RecyclerView mMoviesList;

    private MoviesAdapter mAdapter;
    private GridLayoutManager layoutManager;

    public RecyclerViewFragment() {
        // empty...
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mAdapter = new MoviesAdapter(this, this);
    }

    private void initLayout (List<Movie> movies) {
        mAdapter.setMovies(movies);
        mMoviesList.setAdapter(mAdapter);

        searchProgressBar.setVisibility(View.INVISIBLE);
        mMoviesList.setVisibility(View.VISIBLE);
        errorTextView.setVisibility(View.INVISIBLE);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_recycler_view, container, false);

        ButterKnife.bind(this, rootView);

        // set different number of columns based on the device orientation
        int gridColumns = calculateGridColumns();
        layoutManager = new GridLayoutManager (getActivity(), gridColumns);
        mMoviesList.setLayoutManager(layoutManager);

        // Restore previous state (including selected item index and scroll position)
        if (savedInstanceState != null && savedInstanceState.get(STATE) != null) {
            Log.d(this.getClass().getName(), "Trying to restore state..");
            mMoviesList.getLayoutManager().onRestoreInstanceState(savedInstanceState.getParcelable(STATE));
        }

        loadMoviesData(getTag());
        return rootView;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable(STATE, mMoviesList.getLayoutManager().onSaveInstanceState());
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
     * Initialize the task used to fetch the movies from internet.
     *
     * @param filter
     */
    private void loadMoviesData(String filter) {
        if (!NetworkUtils.isOnline(getActivity())) {
            Toast toast = Toast.makeText(getActivity(), R.string.connectivity_fail, Toast.LENGTH_LONG);
            toast.show();
            return;
        }

        errorTextView.setVisibility(View.INVISIBLE);
        searchProgressBar.setVisibility(View.VISIBLE);

        NetworkUtils.getService().fetchMovies(filter, getString(R.string.THE_MOVIE_DB_API_TOKEN)).enqueue(onMoviesCallback());
    }

    @Override
    protected void showErrorMessage() {
        super.showErrorMessage();
        mMoviesList.setVisibility(View.INVISIBLE);
    }
}
