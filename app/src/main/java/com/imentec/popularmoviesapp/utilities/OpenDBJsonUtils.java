package com.imentec.popularmoviesapp.utilities;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.imentec.popularmoviesapp.model.Movie;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.util.List;

/**
 * @author jagarcel
 * @date 03/02/2017
 */
public class OpenDBJsonUtils {
    public static List<Movie> getPopularMoviesIds(String popularMoviesJson)
            throws JSONException {

        final String RESULTS = "results";
        final String STATUS_CODE = "status_code";
        final String ID = "id";

        JSONObject moviesJson = new JSONObject(popularMoviesJson);

            /* Is there an error? */
        if (!moviesJson.has(RESULTS)) {
            int errorCode = moviesJson.getInt(STATUS_CODE);

            switch (errorCode) {
                case HttpURLConnection.HTTP_OK:
                    break;
                case HttpURLConnection.HTTP_NOT_FOUND:
                        /* Location invalid */
                    return null;
                default:
                        /* Server probably down */
                    return null;
            }
        }

        JSONArray movies = moviesJson.getJSONArray(RESULTS);

        Gson gson = new Gson();
        Type listType = new TypeToken<List<Movie>>() {}.getType();
        List<Movie> parsedPopularMoviesData = gson.fromJson(movies.toString(), listType);

        return parsedPopularMoviesData;
    }
}