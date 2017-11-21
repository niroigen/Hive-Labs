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
import android.view.Gravity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

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

        ListView roomListView = new ListView(mContext);

        if (mPageTitle.equals("Available Rooms")) {
            ArrayList<Room> availableRooms = new ArrayList<>();

            for (Room room : MainActivity.Rooms) {
                if (room.getIsAvailable()) {
                    availableRooms.add(room);
                }
            }

            ListViewAdapter customAdapter = new ListViewAdapter(mContext, android.R.layout.simple_list_item_1, availableRooms);

            roomListView.setAdapter(customAdapter);
            roomListView.setCacheColorHint(Color.WHITE);
            roomListView.setForegroundGravity(Gravity.CENTER);

            roomListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    Intent intent = new Intent(mContext, LabDetail.class);
                    intent.putExtra("position", position);
                    mContext.startActivity(intent);

                    Intent stopHoverIntent = new Intent(mContext, SingleSectionHoverMenuService.class);
                    mContext.stopService(stopHoverIntent);
                }
            });
        }
            return roomListView;

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
