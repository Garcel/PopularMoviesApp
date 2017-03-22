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

package com.imentec.popularmoviesapp.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * MovieDbHelper.java - DB helper used to create and to interact with the DB.
 *
 * @author jagarcel
 * @date 12/03/2017
 */
public class MovieDbHelper extends SQLiteOpenHelper {

    private static final String TEXT_TYPE = " TEXT";
    private static final String INTEGER_TYPE = " INTEGER";
    private static final String PRIMARY_KEY_CLAUSE = " PRIMARY KEY";

    private static final String COMMA_SEP = ",";
    private static final String SQL_CREATE_ENTRIES =
            "CREATE TABLE " + MovieContract.MovieEntry.TABLE_NAME + " (" +
                    MovieContract.MovieEntry.COLUMN_NAME_ID + INTEGER_TYPE + PRIMARY_KEY_CLAUSE + COMMA_SEP +
                    MovieContract.MovieEntry.COLUMN_NAME_MOVIE_ID + INTEGER_TYPE + COMMA_SEP +
                    MovieContract.MovieEntry.COLUMN_NAME_ORIGINAL_TITLE + TEXT_TYPE + COMMA_SEP +
                    MovieContract.MovieEntry.COLUMN_NAME_BACKDROP_PATH + TEXT_TYPE + COMMA_SEP +
                    MovieContract.MovieEntry.COLUMN_NAME_HAS_VIDEO + INTEGER_TYPE + COMMA_SEP +
                    MovieContract.MovieEntry.COLUMN_NAME_ORIGINAL_LANGUAGE + TEXT_TYPE + COMMA_SEP +
                    MovieContract.MovieEntry.COLUMN_NAME_OVERVIEW + TEXT_TYPE + COMMA_SEP +
                    MovieContract.MovieEntry.COLUMN_NAME_POSTER_PATH + TEXT_TYPE + COMMA_SEP +
                    MovieContract.MovieEntry.COLUMN_NAME_POPULARITY + INTEGER_TYPE + COMMA_SEP +
                    MovieContract.MovieEntry.COLUMN_NAME_RELEASE_DATE + TEXT_TYPE + COMMA_SEP +
                    MovieContract.MovieEntry.COLUMN_NAME_VOTE_AVERAGE + INTEGER_TYPE + COMMA_SEP +
                    MovieContract.MovieEntry.COLUMN_NAME_VOTE_COUNT + INTEGER_TYPE + COMMA_SEP +
                    MovieContract.MovieEntry.COLUMN_NAME_TITLE + TEXT_TYPE + ")";

    private static final String SQL_DELETE_ENTRIES =
            "DROP TABLE IF EXISTS " + MovieContract.MovieEntry.TABLE_NAME;

    // If you change the database schema, you must increment the database version.
    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "Movie.db";

    public MovieDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_ENTRIES);
    }

    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // This database is only a cache for online data, so its upgrade policy is
        // to simply to discard the data and start over
        db.execSQL(SQL_DELETE_ENTRIES);
        onCreate(db);
    }
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onUpgrade(db, oldVersion, newVersion);
    }
}
