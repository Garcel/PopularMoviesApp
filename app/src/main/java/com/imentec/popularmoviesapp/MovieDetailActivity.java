package com.imentec.popularmoviesapp;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.imentec.popularmoviesapp.callback.FetchMovieExtraInfoCallback;
import com.imentec.popularmoviesapp.db.MovieDao;
import com.imentec.popularmoviesapp.model.Movie;
import com.imentec.popularmoviesapp.model.Review;
import com.imentec.popularmoviesapp.model.ReviewResponse;
import com.imentec.popularmoviesapp.model.Trailer;
import com.imentec.popularmoviesapp.model.TrailerResponse;
import com.imentec.popularmoviesapp.utilities.NetworkUtils;
import com.imentec.popularmoviesapp.utilities.Utilities;
import com.squareup.picasso.Picasso;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static butterknife.ButterKnife.findById;

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
 * MovieDetailActivity, used to show the movie detail when a movie poster is clicked into the {@link MainActivity).
 *
 * @author jagarcel
 * @date 03/02/2017
 */
public class MovieDetailActivity extends AppCompatActivity implements FetchMovieExtraInfoCallback, View.OnClickListener {

    private static final String MOVIE = "movie";
    private static final String MOVIE_ID = "movieId";

    @BindView(R.id.ll_movie_detail) ViewGroup movieDetailViewGroup;
    @BindView(R.id.tv_movie_release_date) TextView releaseDate;
    @BindView(R.id.tv_move_vote_average) TextView voteAverage;
    @BindView(R.id.tv_movie_synopsis) TextView synopsis;
    @BindView(R.id.tv_movie_title) TextView title;
    @BindView(R.id.iv_movie_poster) ImageView poster;
    @BindView(R.id.favorite_btn) ImageView favoriteBtn;
    @BindView(R.id.pb_loading_indicator) ProgressBar searchProgressBar;

    List<Trailer> trailers;
    List<Review> reviews;
    Movie movie;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_detail);
        ButterKnife.bind(this);

        // let's display back button
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(R.string.movie_detail_title);

        Intent parentActivity = getIntent();

        // if intent doesn't have info about a movie then skip this! ("should never happen")
        if (parentActivity != null && parentActivity.hasExtra(MOVIE)) {
            movie = parentActivity.getParcelableExtra(MOVIE);
        } else if (parentActivity != null && parentActivity.hasExtra(MOVIE_ID)) {
            int movieId = parentActivity.getIntExtra(MOVIE_ID, 0);
            movie = MovieDao.getMovie(movieId);
        }

        if (movie == null) return;

        title.setText(movie.getTitle());
        releaseDate.setText(movie.getReleaseDate());
        voteAverage.setText(String.valueOf(movie.getVoteAverage()));
        synopsis.setText(movie.getOverview());
        synopsis.setMovementMethod(new ScrollingMovementMethod());

        String url = NetworkUtils.buildImageURL(movie.getPosterPath()).toString();
        Picasso.with(this)
                .load(url)
                .placeholder(R.drawable.image_placeholder)
                .error(R.drawable.image_placeholder)
                .into(poster);

        // set favorite info
        favoriteBtn.setOnClickListener(this);

        // load trailers and reviews
        loadMovieExtraData(movie.getMovieId());
    }

    /**
     * Used to handle the return button from the menu. When clicked, the parent activity
     * (@link com.imentec.popularmoviesapp.MainActivity) is invoked.
     *
     * @param item
     * @return
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            return returnToPreviousActivity();

        } else return super.onOptionsItemSelected(item);
    }

    private boolean returnToPreviousActivity () {
        Intent intent = new Intent();
        intent.setClass(this, MainActivity.class);

        startActivity(intent);

        return true;
    }

    /**
     * Initialize the task used to fetch the movies from internet.
     *
     * @param movieId
     */
    private void loadMovieExtraData(int movieId) {
        if (!NetworkUtils.isOnline(this)) {
            Toast toast = Toast.makeText(this, R.string.connectivity_fail, Toast.LENGTH_LONG);
            toast.show();
            return;
        }

        NetworkUtils.getService().fetchTrailers(movieId, getString(R.string.THE_MOVIE_DB_API_TOKEN)).enqueue(onTrailersCallback());
    }

    @Override
    public Callback<TrailerResponse> onTrailersCallback() {
        return new Callback<TrailerResponse>() {
            @Override
            public void onResponse(Call<TrailerResponse> call, Response<TrailerResponse> response) {
                if (response != null && response.isSuccessful()) {
                    TrailerResponse trailerResponse = response.body();
                    showTrailers(trailerResponse);
                    NetworkUtils.getService().fetchReviews(movie.getMovieId(), getString(R.string.THE_MOVIE_DB_API_TOKEN)).enqueue(onReviewsCallback());

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
                    showReviews(reviewResponse);
                    searchProgressBar.setVisibility(View.INVISIBLE);
                    movieDetailViewGroup.setVisibility(View.VISIBLE);

                } else showErrorMessage(getString(R.string.fetch_reviews_error_string));
            }

            @Override
            public void onFailure(Call<ReviewResponse> call, Throwable t) {
                showErrorMessage(getString(R.string.fetch_reviews_error_string));
            }
        };
    }

    /*
     * Adds the trailers content.
     */
    private void showTrailers(TrailerResponse trailerResponse) {
        trailers = trailerResponse.getTrailers();

        for (Trailer trailer : trailerResponse.getTrailers()) {

            View trailerView = this.getLayoutInflater().inflate(R.layout.trailer_item, movieDetailViewGroup, false);
            TextView trailerIdView = findById(trailerView, R.id.tv_trailer_id);

            // set content
            int index = trailerResponse.getTrailers().indexOf(trailer);
            trailerIdView.append(" " + (index + 1));

            // set listeners
            trailerIdView.setOnClickListener(onTrailerItemClick(index, this));

            // add views to viewgroup
            movieDetailViewGroup.addView(trailerView);
        }
    }

    /*
     * Adds the reviews content.
     */
    private void showReviews(ReviewResponse reviewResponse) {
        reviews = reviewResponse.getReviews();

        for (Review review : reviewResponse.getReviews()) {

            View reviewView = this.getLayoutInflater().inflate(R.layout.review_item, movieDetailViewGroup, false);
            TextView reviewIdView = findById(reviewView, R.id.iv_review_id);
            TextView reviewContentView = findById(reviewView, R.id.iv_review_text);

            // set content
            int index = reviewResponse.getReviews().indexOf(review);
            reviewIdView.append(" " + (index + 1));
            reviewContentView.setText(review.getContent());

            // add views to viewgroup
            movieDetailViewGroup.addView(reviewView);
        }
    }

    /*
     * Shows an error message using a toast.
     */
    private void showErrorMessage(String error) {
        Toast.makeText(this, error, Toast.LENGTH_LONG).show();
    }

    public View.OnClickListener onTrailerItemClick(final int clickedItemIndex, final Context context) {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Trailer trailer = trailers.get(clickedItemIndex);
                Log.i(this.getClass().getName(), "Clicked trailer: " + trailer);

                Utilities.watchYoutubeVideo(trailer.getKey(), context);
            }
        };
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.favorite_btn: {

                if (movie.getId() == null) {
                    Log.e(this.getClass().getName(), "Saving movie " + movie.toString() + " to favourites");
                    Toast.makeText(this, "Added " + movie.getTitle() + " to favourites!", Toast.LENGTH_SHORT).show();
                    movie.save();

                } else {
                    Log.e(this.getClass().getName(), "Removing movie " + movie.toString() + " from favourites");
                    Toast.makeText(this, "Removed " + movie.getTitle() + " from favourite!", Toast.LENGTH_SHORT).show();
                    movie.delete();
                }
            }
        }
    }
}
