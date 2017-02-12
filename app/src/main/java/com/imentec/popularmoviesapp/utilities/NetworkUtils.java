package com.imentec.popularmoviesapp.utilities;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Scanner;

/**
 * @author jagarcel
 * @date 03/02/2017
 */
public class NetworkUtils {

    private static final String TAG = NetworkUtils.class.getSimpleName();

    private static final String OPEN_DB_URL = "https://api.themoviedb.org/3/movie/";
    private static final String OPEN_DB_MOVIE_URL = "http://image.tmdb.org/t/p/w500//";

    private static final String APY_KEY_PARAM = "api_key";

    /* popular movies path */
    public final static String POPULAR_MOVIES = "popular";
    public final static String TOP_RATED = "top_rated";

    public static URL buildMoviesUrl(String path, String apiKey) {
        Uri uri = Uri.parse(OPEN_DB_URL).buildUpon()
                .appendPath(path)
                .appendQueryParameter(APY_KEY_PARAM, apiKey)
                .build();

        return convertToURL(uri);
    }

    public static URL buildImageURL(String movieId) {
        Uri uri = Uri.parse(OPEN_DB_MOVIE_URL).buildUpon()
                .appendEncodedPath(movieId)
                .build();

        return convertToURL(uri);
    }

    private static URL convertToURL (Uri uri) {
        URL url = null;
        try {
            url = new URL(uri.toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        Log.v(TAG, "Built URI " + url);

        return url;
    }

    /**
     * This method returns the entire result from the HTTP response.
     *
     * @param url The URL to fetch the HTTP response from.
     * @return The contents of the HTTP response.
     * @throws IOException Related to network and stream reading
     */
    public static String getResponseFromHttpUrl(URL url) throws IOException {
        HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
        try {
            InputStream in = urlConnection.getInputStream();

            Scanner scanner = new Scanner(in);
            scanner.useDelimiter("\\A");

            boolean hasInput = scanner.hasNext();
            if (hasInput) {
                return scanner.next();
            } else {
                return null;
            }
        } finally {
            urlConnection.disconnect();
        }
    }

    /**
     * Return true when internet connectivity is available.
     *
     * Else return false.
     *
     * @return
     */
    public static boolean isOnline(Context ctx) {
        ConnectivityManager cm =
                (ConnectivityManager) ctx.getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnectedOrConnecting();
    }
}
