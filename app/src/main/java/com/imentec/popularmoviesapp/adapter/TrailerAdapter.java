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

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
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
        LayoutInflater inflater = (LayoutInflater) parent.getContext()
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        // A holder will hold the references
        // to your views.
        ViewHodler holder;
        View rowView = convertView;

        if(rowView == null) {
            rowView = inflater.inflate(R.layout.trailer_item, parent, false);
            holder = new ViewHodler();
            holder.trailerId = (TextView) rowView.findViewById(R.id.tv_trailer_id);
            holder.playIcon = (ImageView) rowView.findViewById(R.id.iv_trailer_icon);
            rowView.setTag(holder);

            rowView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Trailer trailer = trailers.get(position);
                    Log.v(this.getClass().getName(), "Clicked trailer: " + trailer);

                    NetworkUtils.watchYoutubeVideo(trailer.getKey(), parent.getContext());
                }
            });
        }
        else {
            holder = (ViewHodler) rowView.getTag();
        }

        // set content
        holder.trailerId.setText(parent.getContext().getString(R.string.trailer, String.valueOf(position + 1)));

        return rowView;
    }

    private class ViewHodler {
        // declare your views here
        TextView trailerId;
        ImageView playIcon;
    }
}