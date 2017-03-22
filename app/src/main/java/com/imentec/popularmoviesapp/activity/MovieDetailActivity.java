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

package com.imentec.popularmoviesapp.activity;

import android.content.Intent;
import android.database.Cursor;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.imentec.popularmoviesapp.R;
import com.imentec.popularmoviesapp.db.MovieContract;
import com.imentec.popularmoviesapp.model.Movie;
import com.imentec.popularmoviesapp.provider.MovieProvider;
import com.imentec.popularmoviesapp.utilities.NetworkUtils;

import com.squareup.picasso.Picasso;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * MovieDetailActivity, used to show the movie detail when a movie poster is clicked into the {@link MainActivity).
 *
 * @author jagarcel
 * @date 03/02/2017
 */
public class MovieDetailActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String MOVIE = "movie";

    @BindView(R.id.ll_movie_detail) ViewGroup movieDetailViewGroup;
    @BindView(R.id.tv_movie_release_date) TextView releaseDate;
    @BindView(R.id.tv_move_vote_average) TextView voteAverage;
    @BindView(R.id.tv_movie_synopsis) TextView synopsis;
    @BindView(R.id.tv_movie_title) TextView title;
    @BindView(R.id.iv_movie_poster) ImageView poster;
    @BindView(R.id.favorite_btn) ImageView favoriteBtn;
    @BindView(R.id.pb_loading_indicator) ProgressBar searchProgressBar;
    @BindView(R.id.iv_movie_backPoster) ImageView backPoster;
    @BindView(R.id.scrollView) ScrollView scrollView;

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
        }

        if (movie == null) return;

        String url = NetworkUtils.buildImageURL(movie.getPosterPath()).toString();
        Picasso.with(this)
                .load(url)
                .placeholder(R.drawable.image_placeholder)
                .error(R.drawable.image_placeholder)
                .into(poster);

        String backUrl = NetworkUtils.buildImageURL(movie.getBackdropPath()).toString();
        Picasso.with(this)
                .load(backUrl)
                .placeholder(R.drawable.image_placeholder)
                .error(R.drawable.image_placeholder)
                .into(backPoster);

        title.setText(movie.getTitle());
        releaseDate.append(" " + movie.getReleaseDate());
        voteAverage.append(" " + String.valueOf(movie.getVoteAverage()));
        synopsis.setText(movie.getOverview());
        synopsis.setMovementMethod(new ScrollingMovementMethod());

        // set favorite info
        favoriteBtn.setOnClickListener(this);
        setFavouriteButton(isFavourite(movie));

        searchProgressBar.setVisibility(View.INVISIBLE);
        movieDetailViewGroup.setVisibility(View.VISIBLE);
    }

    private void setFavouriteButton (Boolean isFavourite) {
        if (isFavourite) favoriteBtn.setImageDrawable(getResources().getDrawable(android.R.drawable.btn_star_big_on));
        else favoriteBtn.setImageDrawable(getResources().getDrawable(android.R.drawable.btn_star_big_off));
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.favorite_btn: {

                if (isFavourite(movie)) {
                    // already inserted, so it must be deleted
                    Log.v(this.getClass().getName(), "Removing movie " + movie.toString() + " from favourites");
                    Toast.makeText(this, "Removed " + movie.getTitle() + " from favourite!", Toast.LENGTH_SHORT).show();
                    getContentResolver().delete(MovieProvider.CONTENT_URI, MovieContract.MovieEntry.COLUMN_NAME_MOVIE_ID + " = " + movie.getMovieId(), null);
                    setFavouriteButton(false);

                } else {
                    // row does not exist, so it must be added
                    Log.v(this.getClass().getName(), "Saving movie " + movie.toString() + " to favourites");
                    Toast.makeText(this, "Added " + movie.getTitle() + " to favourites!", Toast.LENGTH_SHORT).show();
                    getContentResolver().insert(MovieProvider.CONTENT_URI, movie.getContentValues());
                    setFavouriteButton(true);
                }
            }
        }
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
            // recover previous parent activity state
            Intent parentIntent = NavUtils.getParentActivityIntent(this);
            parentIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            NavUtils.navigateUpTo(this, parentIntent);

            return true;

        } else return super.onOptionsItemSelected(item);
    }

    private boolean isFavourite (Movie movie) {
        String [] projection = new String [] {MovieContract.MovieEntry.COLUMN_NAME_MOVIE_ID};
        Cursor c = getContentResolver().query(MovieProvider.CONTENT_URI, projection, MovieContract.MovieEntry.COLUMN_NAME_MOVIE_ID + " = " + movie.getMovieId(), null, null);

        return c.getCount() == 1;
    }
}
