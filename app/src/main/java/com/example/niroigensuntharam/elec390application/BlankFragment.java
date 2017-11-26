package com.example.niroigensuntharam.elec390application;

import android.app.PendingIntent;
import android.os.Bundle;
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

import static com.example.niroigensuntharam.elec390application.MainActivity.dateChanged;
import static com.example.niroigensuntharam.elec390application.MainActivity.earliestTime;

public class BlankFragment extends Fragment {

    private int position;

    public BlankFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        position = getArguments().getInt("position");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_blank, container, false);

        RecyclerView rv = rootView.findViewById(R.id.rooms);
        rv.setHasFixedSize(true);

        ArrayList<Room> rooms = new ArrayList<>();

        switch (position) {
            case 0: {
                for (Room room : MainActivity.Rooms) {
                    if (room.getIsAvailable() && room.getNextClass() == null) {
                        rooms.add(room);
                    }
                }
            }
            break;
            case 1: {
                for (Room room : MainActivity.Rooms) {
                    if (room.getIsAvailable() && room.getNextClass() != null) {
                        rooms.add(room);
                    }
                }
            }
            break;
            case 2: {
                for (Room room : MainActivity.Rooms) {
                    if (!room.getIsAvailable()) {
                        rooms.add(room);
                    }
                }
            }
            break;
        }

        RoomsAdapter adapter = new RoomsAdapter(rooms, position);
        rv.setAdapter(adapter);

        LinearLayoutManager llm = new LinearLayoutManager(getActivity());
        rv.setLayoutManager(llm);

        return rootView;
    }
}
