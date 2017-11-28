package com.example.niroigensuntharam.elec390application;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import java.util.ArrayList;

import static com.example.niroigensuntharam.elec390application.MainActivity.TIME_COMPARATOR;
import static com.example.niroigensuntharam.elec390application.MainActivity.mAdapter;

public class BlankFragment extends Fragment {

    private int position;

    public static SwipeRefreshLayout swipeRefreshLayout;

    private View view;

    private static RecyclerView recyclerView;

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

        swipeRefreshLayout = rootView.findViewById(R.id.swiperefresh);

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                MainActivity.RefreshRooms(getContext());
            }
        });

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
                if (mAdapter == null)
                    mAdapter = new RoomsAdapter(getContext(), TIME_COMPARATOR, position);
                else
                    mAdapter.replaceAll(rooms);
                rv.setAdapter(mAdapter);
            }
            break;
            case 1: {
                for (Room room : MainActivity.Rooms) {
                    if (room.getIsAvailable() && room.getNextClass() != null) {
                        rooms.add(room);
                    }
                }
                if (mAdapter == null)
                    mAdapter = new RoomsAdapter(getContext(), TIME_COMPARATOR, position);
                else
                    mAdapter.replaceAll(rooms);
                rv.setAdapter(mAdapter);

            }
            break;
            case 2: {
                for (Room room : MainActivity.Rooms) {
                    if (!room.getIsAvailable()) {
                        rooms.add(room);
                    }
                }
                if (mAdapter == null)
                    mAdapter = new RoomsAdapter(getContext(), TIME_COMPARATOR, position);
                else
                    mAdapter.replaceAll(rooms);

                rv.setAdapter(mAdapter);
            }
            break;
        }

        LinearLayoutManager llm = new LinearLayoutManager(getActivity());
        rv.setLayoutManager(llm);

        return rootView;
    }
}
