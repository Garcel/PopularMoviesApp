package com.imentec.popularmoviesapp;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.imentec.popularmoviesapp.model.Movie;

import java.util.ArrayList;
import java.util.List;

/**
 * @author jagarcel
 * @date 03/02/2017
 */
public class MoviesAdapter extends RecyclerView.Adapter<MoviesAdapter.MovieViewHolder> {

        final private GridItemClickListener mOnClickListener;
        final private GridItemBindListener mOnBindListener;

        private List<Movie> movies;

        public interface GridItemClickListener {
            void onGridItemClick(int clickedItemIndex);
        }

        public interface GridItemBindListener {
            void onGridItemBind(int itemIndex, ImageView gridItemMovieView);
        }


        public MoviesAdapter(GridItemClickListener clicklistener, GridItemBindListener bindListener) {
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
            ImageView gridItemMovieView;

            /**
             * Constructor for our ViewHolder. Within this constructor, we get a reference to our
             * ImageViews and set an onClickListener to listen for clicks. Those will be handled in the
             * onClick method below.
             * @param itemView The View that you inflated in
             *                 {@link MoviesAdapter#onCreateViewHolder(ViewGroup, int)}
             */
            public MovieViewHolder(View itemView) {
                super(itemView);

                gridItemMovieView = (ImageView) itemView.findViewById(R.id.iv_item_movie);

                itemView.setOnClickListener(this);
            }

            /**
             * A method we wrote for convenience. This method will take an integer as input and
             * use that integer to display the appropriate text within a list item.
             * @param listIndex Position of the item in the list
             */
            void bind(int listIndex) {
                mOnBindListener.onGridItemBind(listIndex, gridItemMovieView);
            }

            /**
             * Called whenever a user clicks on an item in the grid.
             * @param v The View that was clicked
             */
            @Override
            public void onClick(View v) {
                int clickedPosition = getAdapterPosition();
                mOnClickListener.onGridItemClick(clickedPosition);
            }
        }

        public List<Movie> getMovies() {
            return movies;
        }

        public void setMovies(List<Movie> movies) {
            this.movies = movies;
        }
    }
