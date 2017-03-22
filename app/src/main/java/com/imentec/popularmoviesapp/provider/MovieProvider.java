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

package com.imentec.popularmoviesapp.provider;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.support.annotation.Nullable;

import com.imentec.popularmoviesapp.db.MovieContract;
import com.imentec.popularmoviesapp.db.MovieDbHelper;

/**
 * MovieProvider.java - ContentProvider used to interact with the DB.
 *
 * @author jagarcel
 * @date 12/03/2017
 */
public class MovieProvider extends ContentProvider {

    private MovieDbHelper mHelper;

    private static final String AUTHORITY = "com.imentec.popularmoviesapp.provider";
    private static final UriMatcher mURIMatcher = new UriMatcher(UriMatcher.NO_MATCH);
    private static final String BASE_PATH = "movies";
    public static final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY  + "/" + BASE_PATH);

    public static final int ALL_ROWS = 1;
    public static final int SINGLE_ROW = 2;

    public static String[] PROJECTION_FIELDS = new String[] {
            MovieContract.MovieEntry.COLUMN_NAME_ID,
            MovieContract.MovieEntry.COLUMN_NAME_MOVIE_ID,
            MovieContract.MovieEntry.COLUMN_NAME_POSTER_PATH,
            MovieContract.MovieEntry.COLUMN_NAME_TITLE,
            MovieContract.MovieEntry.COLUMN_NAME_OVERVIEW,
            MovieContract.MovieEntry.COLUMN_NAME_RELEASE_DATE,
            MovieContract.MovieEntry.COLUMN_NAME_ORIGINAL_TITLE,
            MovieContract.MovieEntry.COLUMN_NAME_ORIGINAL_LANGUAGE,
            MovieContract.MovieEntry.COLUMN_NAME_BACKDROP_PATH,
            MovieContract.MovieEntry.COLUMN_NAME_POPULARITY,
            MovieContract.MovieEntry.COLUMN_NAME_VOTE_COUNT,
            MovieContract.MovieEntry.COLUMN_NAME_HAS_VIDEO,
            MovieContract.MovieEntry.COLUMN_NAME_VOTE_AVERAGE};


    static {
        mURIMatcher.addURI(AUTHORITY, BASE_PATH, ALL_ROWS);
        mURIMatcher.addURI(AUTHORITY, BASE_PATH + "/#", SINGLE_ROW);
    }

    @Override
    public boolean onCreate() {
        mHelper = new MovieDbHelper(getContext());

        return true;
    }

    @Nullable
    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
         SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder();

        // Set the table
        queryBuilder.setTables(MovieContract.MovieEntry.TABLE_NAME);

        int uriType = mURIMatcher.match(uri);
        switch (uriType) {
            case ALL_ROWS:
                break;
            case SINGLE_ROW:
                queryBuilder.appendWhere(MovieContract.MovieEntry.COLUMN_NAME_MOVIE_ID + "=" + uri.getLastPathSegment());
                break;
            default:
                throw new IllegalArgumentException("Unknown URI: " + uri);
        }

        SQLiteDatabase db = mHelper.getWritableDatabase();
        Cursor cursor = queryBuilder.query(db, projection, selection,
                selectionArgs, null, null, sortOrder);

        cursor.setNotificationUri(getContext().getContentResolver(), uri);

        return cursor;
    }

    @Nullable
    @Override
    public String getType(Uri uri) {
        return null;
    }

    @Nullable
    @Override
    public Uri insert(Uri uri, ContentValues values) {
        int uriType = mURIMatcher.match(uri);
        SQLiteDatabase sqlDB = mHelper.getWritableDatabase();
        long id = 0;

        switch (uriType) {
            case ALL_ROWS:
                id = sqlDB.insert(MovieContract.MovieEntry.TABLE_NAME, null, values);
                break;
            default:
                throw new IllegalArgumentException("Unknown URI: " + uri);
        }

        getContext().getContentResolver().notifyChange(uri, null);

        return Uri.parse(BASE_PATH + "/" + id);
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        int uriType = mURIMatcher.match(uri);
        SQLiteDatabase sqlDB = mHelper.getWritableDatabase();
        int rowsDeleted = 0;

        switch (uriType) {
            case ALL_ROWS:
                rowsDeleted = sqlDB.delete(MovieContract.MovieEntry.TABLE_NAME, selection, selectionArgs);
                break;
            case SINGLE_ROW:
                String id = uri.getLastPathSegment();
                rowsDeleted = sqlDB.delete(
                            MovieContract.MovieEntry.TABLE_NAME,
                            MovieContract.MovieEntry.COLUMN_NAME_MOVIE_ID + "=" + id,
                            null);
                break;
            default:
                throw new IllegalArgumentException("Unknown URI: " + uri);
        }

        getContext().getContentResolver().notifyChange(uri, null);

        return rowsDeleted;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        int uriType = mURIMatcher.match(uri);
        SQLiteDatabase sqlDB = mHelper.getWritableDatabase();
        int rowsUpdated = 0;

        switch (uriType) {
            case ALL_ROWS:
                rowsUpdated = sqlDB.update(MovieContract.MovieEntry.TABLE_NAME,
                            values,
                            selection,
                            selectionArgs);
                break;
            case SINGLE_ROW:
                String id = uri.getLastPathSegment();
                rowsUpdated = sqlDB.update(MovieContract.MovieEntry.TABLE_NAME,
                            values,
                            MovieContract.MovieEntry.COLUMN_NAME_MOVIE_ID + "=" + id,
                            null);

                break;
            default:
                throw new IllegalArgumentException("Unknown URI: " + uri);
        }

        getContext().getContentResolver().notifyChange(uri, null);

        return rowsUpdated;
    }
}
