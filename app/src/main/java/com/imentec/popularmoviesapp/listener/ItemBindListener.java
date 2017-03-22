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

package com.imentec.popularmoviesapp.listener;

import android.widget.ImageView;

/**
 * ItemBindListener.java - Interface to be implemented by the activities/ fragments handling item bindings.
 *
 * @author jagarcel
 * @date 20/03/2017
 */
public interface ItemBindListener {
    void onItemBind(String posterPath, ImageView gridItemMovieView);
}