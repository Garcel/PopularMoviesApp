package com.imentec.popularmoviesapp;

import android.content.Intent;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import com.imentec.popularmoviesapp.model.Movie;
import com.imentec.popularmoviesapp.utilities.NetworkUtils;
import com.squareup.picasso.Picasso;

/**
 * MovieDetailActivity, used to show the movie detail when a movie poster is clicked into the {@link MainActivity)
 * @author jagarcel
 * @date 03/02/2017
 */
public class MovieDetailActivity extends AppCompatActivity {

    private static final String MOVIE = "movie";

    private TextView releaseDate;
    private TextView voteAverage;
    private TextView synopsis;
    private TextView title;
    private ImageView poster;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_detail);

        // let's display back button
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(R.string.movie_detail_title);

        releaseDate = (TextView) findViewById(R.id.tv_movie_release_date);
        voteAverage = (TextView) findViewById(R.id.tv_move_vote_average);
        synopsis = (TextView) findViewById(R.id.tv_movie_synopsis);
        title = (TextView) findViewById(R.id.tv_movie_title);
        poster = (ImageView) findViewById(R.id.iv_movie_poster);

        Intent parentActivity = getIntent();

        // if intent doesn't have info about a movie then skip this! ("should never happen")
        if (parentActivity != null && parentActivity.hasExtra(MOVIE)) {
            Movie movie = parentActivity.getParcelableExtra(MOVIE);

            title.setText(movie.getTitle());
            releaseDate.setText(movie.getRelease_date());
            voteAverage.setText(String.valueOf(movie.getVote_average()));
            synopsis.setText(movie.getOverview());

            String url = NetworkUtils.buildImageURL(movie.getPoster_path()).toString();
            Picasso.with(this).load(url).into(poster);
            Picasso.with(this)
                    .load(url)
                    .placeholder(R.drawable.user_placeholder)
                    .error(R.drawable.user_placeholder_error)
                    .into(poster);
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
}
