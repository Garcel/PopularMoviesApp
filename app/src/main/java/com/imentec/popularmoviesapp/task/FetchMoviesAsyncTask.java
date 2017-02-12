package com.imentec.popularmoviesapp.task;

import android.os.AsyncTask;
import android.util.Log;

import com.imentec.popularmoviesapp.model.Movie;
import com.imentec.popularmoviesapp.utilities.NetworkUtils;
import com.imentec.popularmoviesapp.utilities.OpenDBJsonUtils;

import java.net.URL;
import java.util.List;

/**
 * Used to perform the API request asynchronously.
 *
 * @author jagarcel
 * @date 03/02/2017
 */
public class FetchMoviesAsyncTask extends AsyncTask<String, Void, List<Movie>> {

    private OnPostExecuteListener listener;
    private String apiKey;

    public FetchMoviesAsyncTask (OnPostExecuteListener listener, String apiKey) {
        this.listener = listener;
        this.apiKey = apiKey;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected List<Movie> doInBackground(String... params) {

        if (params.length == 0) {
            return null;
        }

        String moviesQuery = params[0];
        URL moviesRequestUrl = NetworkUtils.buildMoviesUrl(moviesQuery, apiKey);

        try {
            String jsonMoviesResponse = NetworkUtils.getResponseFromHttpUrl(moviesRequestUrl);

            return OpenDBJsonUtils.getPopularMoviesIds(jsonMoviesResponse);

        } catch (Exception e) {
            Log.e(this.getClass().getName(), e.toString());
            return null;
        }
    }

    @Override
    protected void onPostExecute(List<Movie> movies) {
        listener.onPostExecute(movies);
    }

    public interface OnPostExecuteListener {
        void onPostExecute(List<Movie> movies);
    }
}