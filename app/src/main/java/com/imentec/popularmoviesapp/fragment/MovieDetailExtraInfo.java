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

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.imentec.popularmoviesapp.R;
import com.imentec.popularmoviesapp.adapter.ReviewAdapter;
import com.imentec.popularmoviesapp.adapter.TrailerAdapter;
import com.imentec.popularmoviesapp.callback.FetchMovieExtraInfoCallback;
import com.imentec.popularmoviesapp.model.Movie;
import com.imentec.popularmoviesapp.model.ReviewResponse;
import com.imentec.popularmoviesapp.model.TrailerResponse;
import com.imentec.popularmoviesapp.utilities.NetworkUtils;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * MovieDetailExtraInfo.java - Fragment used to handle the movie extra detail activity logic.
 *
 * @author jagarcel
 * @date 20/03/2017
 */
public class MovieDetailExtraInfo extends Fragment implements FetchMovieExtraInfoCallback {

    private static final String MOVIE = "movie";

    @BindView(R.id.trailers) ListView trailersView;
    @BindView(R.id.reviews) ListView reviewsView;

    public MovieDetailExtraInfo() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_movie_detail_extra_info, container, false);

        ButterKnife.bind(this, rootView);

        return rootView;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        if (!NetworkUtils.isOnline(getActivity())) {
            Toast toast = Toast.makeText(getActivity(), R.string.connectivity_fail, Toast.LENGTH_LONG);
            toast.show();
            return;
        }

        Intent parentActivity = getActivity().getIntent();

        // if intent doesn't have info about a movie then skip this! ("should never happen")
        Movie movie = null;
        if (parentActivity != null && parentActivity.hasExtra(MOVIE)) {
           movie = parentActivity.getParcelableExtra(MOVIE);
        }

        if (movie == null) return;

        int movieId = movie.getMovieId();
        NetworkUtils.getService().fetchTrailers(movieId, getString(R.string.THE_MOVIE_DB_API_TOKEN)).enqueue(onTrailersCallback());
        NetworkUtils.getService().fetchReviews(movieId, getString(R.string.THE_MOVIE_DB_API_TOKEN)).enqueue(onReviewsCallback());
    }

    @Override
    public Callback<TrailerResponse> onTrailersCallback() {
        return new Callback<TrailerResponse>() {
            @Override
            public void onResponse(Call<TrailerResponse> call, Response<TrailerResponse> response) {
                if (response != null && response.isSuccessful()) {
                    TrailerResponse trailerResponse = response.body();
                    trailersView.setAdapter(new TrailerAdapter(trailerResponse.getTrailers()));
                    setListViewHeightBasedOnChildren(trailersView);

                } else showErrorMessage(getString(R.string.fetch_trailers_error_string));
            }

            @Override
            public void onFailure(Call<TrailerResponse> call, Throwable t) {
                showErrorMessage(getString(R.string.fetch_trailers_error_string));
            }
        };
    }

    @Override
    public Callback<ReviewResponse> onReviewsCallback() {
        return new Callback<ReviewResponse>() {
            @Override
            public void onResponse(Call<ReviewResponse> call, Response<ReviewResponse> response) {
                if (response != null && response.isSuccessful()) {
                    ReviewResponse reviewResponse = response.body();
                    reviewsView.setAdapter(new ReviewAdapter(reviewResponse.getReviews()));
                    setListViewHeightBasedOnChildren(reviewsView);

                } else showErrorMessage(getString(R.string.fetch_reviews_error_string));
            }

            @Override
            public void onFailure(Call<ReviewResponse> call, Throwable t) {
                showErrorMessage(getString(R.string.fetch_reviews_error_string));
            }
        };
    }

    /*
     * Shows an error message using a toast.
     */
    private void showErrorMessage(String error) {
        Toast.makeText(getActivity(), error, Toast.LENGTH_LONG).show();
    }

    /**** Method for Setting the Height of the ListView dynamically.
     **** Hack to fix the issue of not showing all the items of the ListView
     **** when placed inside a ScrollView  ****/
    public static void setListViewHeightBasedOnChildren(ListView listView) {
        ListAdapter listAdapter = listView.getAdapter();
        if (listAdapter == null)
            return;

        int desiredWidth = View.MeasureSpec.makeMeasureSpec(listView.getWidth(), View.MeasureSpec.UNSPECIFIED);
        int totalHeight = 0;
        View view = null;
        for (int i = 0; i < listAdapter.getCount(); i++) {
            view = listAdapter.getView(i, view, listView);
            if (i == 0)
                view.setLayoutParams(new ViewGroup.LayoutParams(desiredWidth, ViewGroup.LayoutParams.WRAP_CONTENT));

            view.measure(desiredWidth, View.MeasureSpec.UNSPECIFIED);
            totalHeight += view.getMeasuredHeight();
        }
        ViewGroup.LayoutParams params = listView.getLayoutParams();
        params.height = totalHeight + (listView.getDividerHeight() * (listAdapter.getCount() - 1));
        listView.setLayoutParams(params);
    }
}
