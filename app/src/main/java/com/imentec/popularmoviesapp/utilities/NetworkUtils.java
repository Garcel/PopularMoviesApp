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
package com.imentec.popularmoviesapp.utilities;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.imentec.popularmoviesapp.api.TMDBApi;

import java.net.MalformedURLException;
import java.net.URL;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * NetworkUtils.java -
 *
 * @author jagarcel
 * @date 03/02/2017
 */
public class NetworkUtils {

    private static final String OPEN_DB_URL = "https://api.themoviedb.org/3/movie/";
    private static final String OPEN_DB_MOVIE_URL = "http://image.tmdb.org/t/p/w500//";

    public final static String POPULAR_MOVIES = "popular";
    public final static String TOP_RATED = "top_rated";

    /**
     * Return the Retrofit API service.
     *
     * @return
     */
    public static TMDBApi getService () {
        Gson gson = new GsonBuilder().setLenient().excludeFieldsWithoutExposeAnnotation().create();

        HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
        OkHttpClient httpClient = new OkHttpClient.Builder().addInterceptor(logging).build();
        logging.setLevel(HttpLoggingInterceptor.Level.BODY);

        Retrofit retrofit = new Retrofit.Builder()
                .addConverterFactory(GsonConverterFactory.create(gson))
                .baseUrl(OPEN_DB_URL)
                .client(httpClient)
                .build();

        return retrofit.create(TMDBApi.class);
    }

    /**
     * Return the url for the given movie id.
     *
     * @param movieId
     * @return
     */
    public static URL buildImageURL(String movieId) {
        Uri uri = Uri.parse(OPEN_DB_MOVIE_URL).buildUpon()
                .appendEncodedPath(movieId)
                .build();

        return convertToURL(uri);
    }

    /**
     * Turns an uri into an url.
     *
     * @param uri
     * @return
     */
    private static URL convertToURL (Uri uri) {
        URL url = null;
        try {
            url = new URL(uri.toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        Log.v(NetworkUtils.class.getName(), "Built URI " + url);

        return url;
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

    /**
     * Used to play the given trailer either with the Youtube app or with the web browser.
     */
    public static void watchYoutubeVideo(String id, Context context){
        Intent appIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("vnd.youtube:" + id));
        Intent webIntent = new Intent(Intent.ACTION_VIEW,
                Uri.parse("http://www.youtube.com/watch?v=" + id));
        try {
            context.startActivity(appIntent);
        } catch (ActivityNotFoundException ex) {
            context.startActivity(webIntent);
        }
    }
}
