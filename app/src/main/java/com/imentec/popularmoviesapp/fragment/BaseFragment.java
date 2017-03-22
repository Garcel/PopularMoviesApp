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

import android.app.Fragment;
import android.content.Intent;
import android.os.Parcelable;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.imentec.popularmoviesapp.R;
import com.imentec.popularmoviesapp.activity.MovieDetailActivity;
import com.imentec.popularmoviesapp.listener.ItemBindListener;
import com.imentec.popularmoviesapp.listener.ItemClickListener;
import com.imentec.popularmoviesapp.model.Movie;
import com.imentec.popularmoviesapp.utilities.NetworkUtils;
import com.squareup.picasso.Picasso;

import butterknife.BindView;

/**
 * BaseFragment.java - Base fragment which implements shared functionalities.
 *
 * @author jagarcel
 * @date 17/03/2017
 */
public abstract class BaseFragment extends Fragment implements ItemClickListener, ItemBindListener {

    protected static final String STATE = "state";
    protected static final String POSITION = "position";
    protected static final String MOVIE = "movie";

    // shows a progress bar until data has been collected from the API
    @BindView(R.id.pb_loading_indicator)
    ProgressBar searchProgressBar;

    @BindView(R.id.tv_error_message_display)
    TextView errorTextView;

    /**
     * This is where we receive our callback from
     * {@link ItemClickListener}
     *
     * This callback is invoked when you click on an item in the grid.
     *
     * @param movie The movie clicked.
     */
    @Override
    public void onItemClick(Movie movie) {
        Log.v(this.getClass().getName(), "Clicked item: " + movie.toString());

        Intent intent = new Intent();
        intent.setClass(getActivity(), MovieDetailActivity.class);

        // set the clicked item id to the intent, so we can recover it "on the other side" to recover
        // the movie details
        intent.putExtra(MOVIE, movie);
        startActivity(intent);
    }

    /**
     * This callback is invoked when the adapter wants to bind a view to one of its grids.
     *
     * @param posterPath
     * @param imageView
     */
    @Override
    public void onItemBind(String posterPath, ImageView imageView) {
        String url = NetworkUtils.buildImageURL(posterPath).toString();
        Log.v(this.getClass().getName(), "Setting poster " + posterPath + " + INTO imageview " + imageView.toString());

        Picasso.with(getActivity()).cancelRequest(imageView);

        Picasso.with(getActivity())
                .load(url)
                .fit()
                .placeholder(R.drawable.image_placeholder)
                .error(R.drawable.image_placeholder)
                .into(imageView);
    }

    /**
     * Shows the error message.
     *
     * Should only happen when internet connectivity fails.
     */
    protected void showErrorMessage() {
        searchProgressBar.setVisibility(View.INVISIBLE);
        errorTextView.setVisibility(View.VISIBLE);
    }

    /**
     * Dynamically calculates the number of columns for the grid layout according to the device screen,
     * and orientation.
     */
    public int calculateGridColumns () {
        DisplayMetrics displayMetrics = this.getResources().getDisplayMetrics();
        float dpWidth = displayMetrics.widthPixels / displayMetrics.density;
        int numberOfColumns = (int) (dpWidth / 180);
        return numberOfColumns;
    }
}
