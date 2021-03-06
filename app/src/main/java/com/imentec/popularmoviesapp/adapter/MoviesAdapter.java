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

package com.imentec.popularmoviesapp.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.imentec.popularmoviesapp.R;
import com.imentec.popularmoviesapp.listener.ItemBindListener;
import com.imentec.popularmoviesapp.listener.ItemClickListener;
import com.imentec.popularmoviesapp.model.Movie;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * MoviesAdapter.java - Adapter used by {@link com.imentec.popularmoviesapp.fragment.RecyclerViewFragment}
 * to map the movies into the recycler view.
 *
 * @author jagarcel
 * @date 03/02/2017
 */
public class MoviesAdapter extends RecyclerView.Adapter<MoviesAdapter.MovieViewHolder> {

        final private ItemClickListener mOnClickListener;
        final private ItemBindListener mOnBindListener;

        private List<Movie> movies;

        public MoviesAdapter(ItemClickListener clicklistener, ItemBindListener bindListener) {
            this.movies = new ArrayList<>();
            this.mOnClickListener = clicklistener;
            this.mOnBindListener = bindListener;
        }

        @Override
        public MovieViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.movies_item, parent, false);

            return new MovieViewHolder(view);
        }

        @Override
        public void onBindViewHolder(MovieViewHolder holder, int position) {
            holder.bind(position);
        }

        @Override
        public int getItemCount() {
            return movies.size();
        }

        /**
         * Cache of the children views for a list item.
         */
        class MovieViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

            // Will display the position in the grid, ie 0 through getItemCount() - 1
            @BindView(R.id.iv_item_movie) ImageView gridItemMovieView;

            /**
             * Constructor for our ViewHolder. Within this constructor, we get a reference to our
             * ImageViews and set an onClickListener to listen for clicks. Those will be handled in the
             * onClick method below.
             * @param itemView The View that you inflated in
             *                 {@link MoviesAdapter#onCreateViewHolder(ViewGroup, int)}
             */
            public MovieViewHolder(View itemView) {
                super(itemView);

                itemView.setOnClickListener(this);
                ButterKnife.bind(this, itemView);
            }

            /**
             * A method we wrote for convenience. This method will take an integer as input and
             * use that integer to display the appropriate text within a list item.
             * @param listIndex Position of the item in the list
             */
            void bind(int listIndex) {
                mOnBindListener.onItemBind(movies.get(listIndex).getPosterPath(), gridItemMovieView);
            }

            /**
             * Called whenever a user clicks on an item in the grid.
             * @param v The View that was clicked
             */
            @Override
            public void onClick(View v) {
                int clickedPosition = getAdapterPosition();
                mOnClickListener.onItemClick(getMovies().get(clickedPosition));
            }
        }

        public List<Movie> getMovies() {
            return movies;
        }

        public void setMovies(List<Movie> movies) {
            this.movies = movies;
        }
    }
