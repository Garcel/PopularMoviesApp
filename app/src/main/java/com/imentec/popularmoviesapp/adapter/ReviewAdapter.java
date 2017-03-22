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

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.imentec.popularmoviesapp.R;
import com.imentec.popularmoviesapp.model.Review;

import java.util.List;

/**
 * ReviewAdapter.java - Adapter used by {@link com.imentec.popularmoviesapp.fragment.MovieDetailExtraInfo}
 * to map the reviews into a listview.
 *
 *
 * @author jagarcel
 * @date 20/03/2017
 */
public class ReviewAdapter extends BaseAdapter {
    private List<Review> reviews;

    public ReviewAdapter(List<Review> reviews) {
        this.reviews = reviews;
    }

    @Override
    public int getCount() { return reviews.size(); }
    @Override
    public Object getItem(int pos) { return reviews.get(pos); }
    @Override
    public long getItemId(int pos) { return pos; }

    @Override
    public View getView(final int position, View convertView, final ViewGroup parent) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.review_item, null);

        TextView reviewIdView = (TextView) itemView.findViewById(R.id.iv_review_id);
        TextView reviewContentView = (TextView) itemView.findViewById(R.id.iv_review_text);
        TextView reviewAuthor = (TextView) itemView.findViewById(R.id.iv_review_author);

        // set content
        reviewIdView.append(" " + (position + 1));
        reviewContentView.setText(reviews.get(position).getContent());
        reviewAuthor.append(" " + reviews.get(position).getAuthor());

        return itemView;
    }
}