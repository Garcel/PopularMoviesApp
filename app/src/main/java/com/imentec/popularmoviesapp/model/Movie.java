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
package com.imentec.popularmoviesapp.model;

import android.content.ContentValues;
import android.database.Cursor;
import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.imentec.popularmoviesapp.db.MovieContract;

import java.util.Arrays;

/**
 * Movie.java - Models the Movie abstraction.
 *
 * @author jagarcel
 * @date 03/02/2017
 */
public class Movie implements Parcelable {

    @Expose @SerializedName("poster_path")
    private String posterPath;

    @Expose
    private String overview;

    @Expose @SerializedName("release_date")
    private String releaseDate;

    @Expose @SerializedName("genre_ids")
    private int[] genreIds;

    @Expose @SerializedName("id")
    private int movieId;

    @Expose @SerializedName("original_title")
    private String originalTitle;

    @Expose @SerializedName("original_language")
    private String originalLanguage;

    @Expose
    private String title;

    @Expose @SerializedName("backdrop_path")
    private String backdropPath;

    @Expose
    private double popularity;

    @Expose @SerializedName("vote_count")
    private long voteCount;

    @Expose @SerializedName("has_video")
    private boolean hasVideo;

    @Expose @SerializedName("vote_average")
    private double voteAverage;

    public Movie() {
        super ();
    }

    protected Movie(Parcel in) {
        posterPath = in.readString();
        overview = in.readString();
        releaseDate = in.readString();
        genreIds = in.createIntArray();
        movieId = in.readInt();
        originalTitle = in.readString();
        originalLanguage = in.readString();
        title = in.readString();
        backdropPath = in.readString();
        popularity = in.readDouble();
        voteCount = in.readLong();
        hasVideo = in.readByte() != 0;
        voteAverage = in.readDouble();
    }

    public Movie(Cursor cursor) {

        // not stored
        long movieDbId = cursor.getLong(0);
        setMovieId(cursor.getInt(1));
        setPosterPath(cursor.getString(2));
        setTitle(cursor.getString(3));
        setOverview(cursor.getString(4));
        setReleaseDate(cursor.getString(5));
        setOriginalTitle(cursor.getString(6));
        setOriginalLanguage(cursor.getString(7));
        setBackdropPath(cursor.getString(8));
        setPopularity(cursor.getInt(9));
        setVoteCount(cursor.getInt(10));
        setHasVideo(cursor.getInt(11) == 0);
        setVoteAverage(cursor.getInt(12));
    }

    public ContentValues getContentValues () {
        ContentValues values = new ContentValues();
        values.put(MovieContract.MovieEntry.COLUMN_NAME_MOVIE_ID, getMovieId());
        values.put(MovieContract.MovieEntry.COLUMN_NAME_ORIGINAL_TITLE, getOriginalTitle());
        values.put(MovieContract.MovieEntry.COLUMN_NAME_BACKDROP_PATH, getBackdropPath());
        values.put(MovieContract.MovieEntry.COLUMN_NAME_HAS_VIDEO, isHasVideo());
        values.put(MovieContract.MovieEntry.COLUMN_NAME_ORIGINAL_LANGUAGE, getOriginalLanguage());
        values.put(MovieContract.MovieEntry.COLUMN_NAME_OVERVIEW, getOverview());
        values.put(MovieContract.MovieEntry.COLUMN_NAME_POSTER_PATH, getPosterPath());
        values.put(MovieContract.MovieEntry.COLUMN_NAME_POPULARITY, getPopularity());
        values.put(MovieContract.MovieEntry.COLUMN_NAME_RELEASE_DATE, getReleaseDate());
        values.put(MovieContract.MovieEntry.COLUMN_NAME_VOTE_AVERAGE, getVoteAverage());
        values.put(MovieContract.MovieEntry.COLUMN_NAME_VOTE_COUNT, getVoteCount());
        values.put(MovieContract.MovieEntry.COLUMN_NAME_TITLE, getTitle());

        return values;
    }


    public String getPosterPath() {
        return posterPath;
    }

    public String getOverview() {
        return overview;
    }

    public String getReleaseDate() {
        return releaseDate;
    }

    public int[] getGenreIds() {
        return genreIds;
    }

    public int getMovieId() {
        return movieId;
    }

    public String getOriginalTitle() {
        return originalTitle;
    }

    public String getOriginalLanguage() {
        return originalLanguage;
    }

    public String getTitle() {
        return title;
    }

    public String getBackdropPath() {
        return backdropPath;
    }

    public double getPopularity() {
        return popularity;
    }

    public long getVoteCount() {
        return voteCount;
    }

    public boolean isHasVideo() {
        return hasVideo;
    }

    public double getVoteAverage() {
        return voteAverage;
    }



    @Override
    public String toString() {
        return "Movie{" +
                "posterPath='" + posterPath + '\'' +
                ", overview='" + overview + '\'' +
                ", releaseDate='" + releaseDate + '\'' +
                ", genreIds=" + Arrays.toString(genreIds) +
                ", movieId=" + movieId +
                ", originalTitle='" + originalTitle + '\'' +
                ", originalLanguage='" + originalLanguage + '\'' +
                ", title='" + title + '\'' +
                ", backdropPath='" + backdropPath + '\'' +
                ", popularity=" + popularity +
                ", voteCount=" + voteCount +
                ", hasVideo=" + hasVideo +
                ", voteAverage=" + voteAverage +
                '}';
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(posterPath);
        dest.writeString(overview);
        dest.writeString(releaseDate);
        dest.writeIntArray(genreIds);
        dest.writeInt(movieId);
        dest.writeString(originalTitle);
        dest.writeString(originalLanguage);
        dest.writeString(title);
        dest.writeString(backdropPath);
        dest.writeDouble(popularity);
        dest.writeLong(voteCount);
        dest.writeByte((byte) (hasVideo ? 1 : 0));
        dest.writeDouble(voteAverage);
    }

    public static final Creator<Movie> CREATOR = new Creator<Movie>() {
        @Override
        public Movie createFromParcel(Parcel in) {
            return new Movie(in);
        }

        @Override
        public Movie[] newArray(int size) {
            return new Movie[size];
        }
    };

    public void setPosterPath(String posterPath) {
        this.posterPath = posterPath;
    }

    public void setOverview(String overview) {
        this.overview = overview;
    }

    public void setReleaseDate(String releaseDate) {
        this.releaseDate = releaseDate;
    }

    public void setGenreIds(int[] genreIds) {
        this.genreIds = genreIds;
    }

    public void setMovieId(int movieId) {
        this.movieId = movieId;
    }

    public void setOriginalTitle(String originalTitle) {
        this.originalTitle = originalTitle;
    }

    public void setOriginalLanguage(String originalLanguage) {
        this.originalLanguage = originalLanguage;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setBackdropPath(String backdropPath) {
        this.backdropPath = backdropPath;
    }

    public void setPopularity(double popularity) {
        this.popularity = popularity;
    }

    public void setVoteCount(long voteCount) {
        this.voteCount = voteCount;
    }

    public void setHasVideo(boolean hasVideo) {
        this.hasVideo = hasVideo;
    }

    public void setVoteAverage(double voteAverage) {
        this.voteAverage = voteAverage;
    }
}