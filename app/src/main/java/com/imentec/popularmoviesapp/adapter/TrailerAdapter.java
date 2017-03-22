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

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.imentec.popularmoviesapp.R;
import com.imentec.popularmoviesapp.model.Trailer;
import com.imentec.popularmoviesapp.utilities.NetworkUtils;

import java.util.List;

/**
 * TrailerAdapter.java - Adapter used by {@link com.imentec.popularmoviesapp.fragment.MovieDetailExtraInfo}
 * to map the trailers into a listview.
 *
 * @author jagarcel
 * @date 20/03/2017
 */
public class TrailerAdapter extends BaseAdapter {
    private List<Trailer> trailers;

    public TrailerAdapter(List<Trailer> trailers) {
        this.trailers = trailers;
    }

    @Override
    public int getCount() { return trailers.size(); }
    @Override
    public Object getItem(int pos) { return trailers.get(pos); }
    @Override
    public long getItemId(int pos) { return pos; }

    @Override
    public View getView(final int position, View convertView, final ViewGroup parent) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.trailer_item, null);

        TextView id  = (TextView) itemView.findViewById(R.id.tv_trailer_id);
        id.append(" " + (position + 1));

        itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Trailer trailer = trailers.get(position);
                Log.v(this.getClass().getName(), "Clicked trailer: " + trailer);

                NetworkUtils.watchYoutubeVideo(trailer.getKey(), parent.getContext());
            }
        });

        return itemView;
    }
}