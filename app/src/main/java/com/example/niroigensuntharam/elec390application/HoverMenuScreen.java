/*
 * Copyright 2016 Google Inc. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.example.niroigensuntharam.elec390application;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import java.sql.Time;
import java.util.ArrayList;


import io.mattcarroll.hover.Content;

public class HoverMenuScreen implements Content {

    private final Context mContext;
    private final String mPageTitle;
    private final View mWholeScreen;

    HoverMenuScreen(@NonNull Context context, @NonNull String pageTitle) {
        mContext = context.getApplicationContext();
        mPageTitle = pageTitle;
        mWholeScreen = createScreenView();
    }

    @TargetApi(23)
    @NonNull
    private View createScreenView() {

        RecyclerView recyclerView = new RecyclerView(mContext);

        if (mPageTitle.equals("Available Rooms")) {
            ArrayList<Room> availableRooms = new ArrayList<>();

            for (Room room : MainActivity.Rooms) {
                if (room.getIsAvailable()) {
                    availableRooms.add(room);
                }
            }

            RoomsAdapter customAdapter = new RoomsAdapter(mContext, MainActivity.TIME_COMPARATOR);

            recyclerView.setAdapter(customAdapter);
            recyclerView.setForegroundGravity(Gravity.CENTER);
        }
            return recyclerView;

    }

    // Make sure that this method returns the SAME View.  It should NOT create a new View each time
    // that it is invoked.
    @NonNull
    @Override
    public View getView() {
        return mWholeScreen;
    }

    @Override
    public boolean isFullscreen() {
        return true;
    }

    @Override
    public void onShown() {
        // No-op.
    }

    @Override
    public void onHidden() {
        // No-op.
    }
}
