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

package com.imentec.popularmoviesapp.activity;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.SharedPreferences;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import com.imentec.popularmoviesapp.R;
import com.imentec.popularmoviesapp.fragment.DBFragment;
import com.imentec.popularmoviesapp.fragment.RecyclerViewFragment;
import com.imentec.popularmoviesapp.utilities.NetworkUtils;

import butterknife.ButterKnife;

/**
 * Main activity.java - Main activity movies used to show movies sorted by different filters.
 *
 * @author jagarcel
 * @date 03/02/2017
 */
public class MainActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    private Spinner spinner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        if (savedInstanceState == null) {
            // Do your oncreate stuff because there is no bundle
        }
    }

    /**
     * Used to add the items to sort the movies when the user click'em.
     *
     * @param menu
     * @return
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);

        MenuItem item = menu.findItem(R.id.movies_spinner_item);
        spinner = (Spinner) MenuItemCompat.getActionView(item);

        // start the spinner
        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.movies_source, android.R.layout.simple_spinner_item);

        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // Apply the adapter to the spinner
        spinner.setAdapter(adapter);

        // fetch the movies and start the app!!
        int previousPosition = readSpinnerPosition();
        spinner.setSelection(previousPosition);

        // attach listeners
        spinner.setOnItemSelectedListener(this);

        return true;
    }

    /**
     * Handle the menu items to fetch the most popular or top rated movies according to the user desire ^^.
     *
     * @param parent
     * @param view
     * @param position
     * @param id
     */
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        Fragment f;

        String[] sources = getResources().getStringArray(R.array.movies_source);
        if (position > sources.length - 1) return;

        if (sources[position].equals(getString(R.string.action_sort_popular_movies))) {
            Log.v(this.getClass().getName(), "User requested to see the most popular movies");
            String filter = NetworkUtils.POPULAR_MOVIES;
            saveState(position);

            f = getFragmentManager().findFragmentByTag(filter);
            if (f != null) return;
            f = new RecyclerViewFragment();
            transaction.replace(R.id.activity_main, f, filter);
            transaction.commit();


        } else if (sources[position].equals(getString(R.string.action_sort_top_rated_movies))) {
            Log.v(this.getClass().getName(), "User requested to see the top rated movies");
            String filter = NetworkUtils.TOP_RATED;
            saveState(position);

            f = getFragmentManager().findFragmentByTag(filter);
            if (f != null) return;
            f = new RecyclerViewFragment();
            transaction.replace(R.id.activity_main, f, filter);
            transaction.commit();

        } else if (sources[position].equals(getString(R.string.action_sort_favourite_movies))) {
            Log.v(this.getClass().getName(), "User requested to see the favourite movies");
            String filter = getString(R.string.action_sort_favourite_movies);
            saveState(position);

            f = getFragmentManager().findFragmentByTag(filter);
            if (f != null) return;
            f = new DBFragment();
            transaction.replace(R.id.activity_main, f);
            transaction.commit();
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
        // do nothing
    }

    private void saveState(int position) {
        SharedPreferences sharedPreferences = this.getSharedPreferences("MoviePreferences", Context.MODE_PRIVATE);
        SharedPreferences.Editor sharedPreferencesEdit = sharedPreferences.edit();
        sharedPreferencesEdit.putInt("position", position);
        sharedPreferencesEdit.apply();
    }

    private int readSpinnerPosition() {
        SharedPreferences sharedPreferences = getSharedPreferences("MoviePreferences", Context.MODE_PRIVATE);
        return sharedPreferences.getInt("position", 0);
    }

    @Override
    public void onBackPressed() {
        if(getFragmentManager().getBackStackEntryCount() == 1) {
            moveTaskToBack(false);
        }
        else {
            super.onBackPressed();
        }
    }
}