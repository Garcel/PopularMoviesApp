package com.imentec.popularmoviesapp.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.Arrays;

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
 * Movie.java - Used to model the Movie abstraction.
 *
 * @author jagarcel
 * @date 03/02/2017
 */
@Table(name = "movie")
public class Movie extends Model implements Parcelable {

    @Column(name = "poster_path")
    @Expose @SerializedName("poster_path")
    private String posterPath;
    @Column(name = "overview")
    @Expose
    private String overview;
    @Column(name = "release_date")
    @Expose @SerializedName("release_date")
    private String releaseDate;
    @Column(name = "genre_ids")
    @Expose @SerializedName("genre_ids")
    private int[] genreIds;
    @Column(name = "movie_id", index = true)
    @Expose @SerializedName("id")
    private int movieId;
    @Column(name = "original_title")
    @Expose @SerializedName("original_title")
    private String originalTitle;
    @Column(name = "original_language")
    @Expose @SerializedName("original_language")
    private String originalLanguage;
    @Column(name = "title")
    @Expose
    private String title;
    @Column(name = "backdrop_path")
    @Expose @SerializedName("backdrop_path")
    private String backdropPath;
    @Column(name = "popularity")
    @Expose
    private double popularity;
    @Column(name = "vote_count")
    @Expose @SerializedName("vote_count")
    private long voteCount;
    @Column(name = "has_video")
    @Expose @SerializedName("has_video")
    private boolean hasVideo;
    @Column(name = "vote_average")
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
}