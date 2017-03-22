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
package com.imentec.popularmoviesapp.fragment;

import android.app.LoaderManager;
import android.content.Context;
import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.ImageView;

import com.imentec.popularmoviesapp.R;
import com.imentec.popularmoviesapp.adapter.MovieCursorAdapter;
import com.imentec.popularmoviesapp.model.Movie;
import com.imentec.popularmoviesapp.provider.MovieProvider;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * DBFragment.java - Fragment used to handle the favourite movies logic.
 *
 * @author jagarcel
 * @date 17/03/2017
 */
public class DBFragment extends BaseFragment implements LoaderManager.LoaderCallbacks<Cursor> {

    // Bind grid to adapter
    @BindView(R.id.gridview)
    GridView gvMovies ;

    private MovieCursorAdapter adapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_db, container, false);

        ButterKnife.bind(this, rootView);

        if (adapter == null) {
            adapter = new MovieCursorAdapter(getActivity()) {
                @Override
                public void bindView(View view, Context context, Cursor cursor) {
                    Log.v(this.getClass().getName(), "Number of rows -> " + cursor.getCount());
                    Log.v(this.getClass().getName(), "Cursor position -> " + cursor.getPosition());
                    final Movie movie = new Movie(cursor);

                    ImageView iv = (ImageView) view.findViewById(R.id.iv_item_movie);
                    if (iv != null) {
                        onItemBind(movie.getPosterPath(), iv);
                        iv.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                onItemClick(movie);
                            }
                        });
                    }
                }
            };

            gvMovies.setAdapter(adapter);
            getLoaderManager().initLoader(0, null, this);
        }

        // set different number of columns based on the device orientation
        int gridColumns = calculateGridColumns();
        gvMovies.setNumColumns(gridColumns);

        // Restore previous state (including selected item index and scroll position)
        if (savedInstanceState != null) {
            Log.v(this.getClass().getName(), "Trying to restore state..");
            gvMovies.onRestoreInstanceState(savedInstanceState.getParcelable(STATE));

            int position = savedInstanceState.getInt(POSITION);
            Log.d(this.getClass().getName(), "Trying to scroll to position..." + position);
            gvMovies.requestFocus();
            gvMovies.setSelection(position);
        }

        return rootView;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putParcelable(STATE, gvMovies.onSaveInstanceState());
        outState.putInt(POSITION, gvMovies.getFirstVisiblePosition());

        super.onSaveInstanceState(outState);
    }

    @Override
    protected void showErrorMessage() {
        super.showErrorMessage();
        gvMovies.setVisibility(View.INVISIBLE);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new CursorLoader(
                getActivity(),
                MovieProvider.CONTENT_URI,
                MovieProvider.PROJECTION_FIELDS,
                null,
                null,
                null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        adapter.swapCursor(data);

        searchProgressBar.setVisibility(View.INVISIBLE);
        gvMovies.setVisibility(View.VISIBLE);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        adapter.swapCursor(null);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        try {
            getLoaderManager().destroyLoader(0);
            if (adapter != null) {
                adapter.changeCursor(null);
                adapter = null;
            }
        } catch (Throwable localThrowable) {
            Log.e(getClass().getName(), localThrowable.getMessage());
        }
    }
}
