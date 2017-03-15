package com.imentec.popularmoviesapp.db;

import com.activeandroid.query.Select;
import com.imentec.popularmoviesapp.model.Movie;

import java.util.List;

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
 * MovieDao.java -
 *
 * @author jagarcel
 * @date 03/02/2017
 */
public class MovieDao {

    public static Movie getMovie(int movieId) {
        return new Select()
                .from(Movie.class)
                .where("movie_id = ?", movieId)
                .executeSingle();
    }

    public static List<Movie> getAllMovies() {
        return new Select().all()
                .from(Movie.class)
                .execute();
    }
}
