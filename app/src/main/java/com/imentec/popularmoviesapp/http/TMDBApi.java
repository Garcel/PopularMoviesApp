package com.imentec.popularmoviesapp.http;

import com.imentec.popularmoviesapp.model.MovieResponse;
import com.imentec.popularmoviesapp.model.ReviewResponse;
import com.imentec.popularmoviesapp.model.TrailerResponse;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

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
 * Utilities.java -
 *
 * @author jagarcel
 * @date 14/02/2017
 */
public interface TMDBApi {

    @GET("{filter}")
    Call<MovieResponse> fetchMovies(@Path("filter") String filter, @Query("api_key") String apiKey);

    @GET("{movieId}/videos")
    Call<TrailerResponse> fetchTrailers(@Path("movieId") int movieID, @Query("api_key") String apiKey);

    @GET("{movieId}/reviews")
    Call<ReviewResponse> fetchReviews(@Path("movieId") int movieID, @Query("api_key") String apiKey);
}