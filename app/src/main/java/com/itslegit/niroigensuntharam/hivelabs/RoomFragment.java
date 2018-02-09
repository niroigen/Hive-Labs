package com.itslegit.niroigensuntharam.hivelabs;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import static com.itslegit.niroigensuntharam.hivelabs.MainActivity.earliestTime;

public class RoomFragment extends Fragment {
    private View view;
    SwipeRefreshLayout swipeRefreshLayout;

    public RoomFragment() {
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_room, container, false);

        setRecyclerView(getArguments().getString("Type"));

        setSwipeRefresh();

        return view;
    }

    @Override
    public void onPause() {
        super.onPause();

        if (swipeRefreshLayout!=null) {
            swipeRefreshLayout.setRefreshing(false);
            swipeRefreshLayout.destroyDrawingCache();
            swipeRefreshLayout.clearAnimation();
        }
    }

    private void setSwipeRefresh(){
        swipeRefreshLayout = view.findViewById(R.id.swiperefresh);

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                RefreshRooms(getContext());
            }
        });
    }

    // Will refresh to show the rooms the user can currently go to
    public void RefreshRooms(Context context) {
        // If after the user refreshes, and there is a change in the date
        // all the rooms will be initialized again

        if (MainActivity.dateChanged ||
                (earliestTime != null && Integer.parseInt(earliestTime) < Integer.parseInt(new SimpleDateFormat("HHmm", Locale.CANADA).format(new Date())))) {

            MainActivity.Rooms.clear();

            MainActivity.timeString = new SimpleDateFormat("HHmm", Locale.CANADA).format(new Date());

            GetRoomInfoAsync getRoomInfoAsync = new GetRoomInfoAsync(context);

            getRoomInfoAsync.execute(new SimpleDateFormat("yyyyMMdd", Locale.CANADA).format(new Date()));

            MainActivity.dateChanged = false;
        }

        swipeRefreshLayout.setRefreshing(false);
    }


    //Setting recycler view
    private void setRecyclerView(String type) {

        RecyclerView recyclerView = (RecyclerView) view
                .findViewById(R.id.rooms);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));//Linear Items

        ArrayList<Room> rooms = new ArrayList<>();

        switch (type){
            case "Available": {
                for (Room room: MainActivity.Rooms){
                    if (room.getIsAvailable() && room.getNextClass() == null){
                        rooms.add(room);
                    }
                }
            }
                break;
            case "Upcoming": {
                for (Room room: MainActivity.Rooms){
                    if (room.getIsAvailable() && room.getNextClass() != null){
                        rooms.add(room);
                    }
                }
            }
                break;
            case "In Progress": {
                for (Room room: MainActivity.Rooms){
                    if (!room.getIsAvailable()){
                        rooms.add(room);
                    }
                }
            }
                break;
        }

        Room.SortRooms(rooms);

        RoomsAdapter adapter = new RoomsAdapter(getActivity(), rooms);
        recyclerView.setAdapter(adapter);// set adapter on recyclerview
    }
}
