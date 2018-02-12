package com.itslegit.niroigensuntharam.hivelabs.helper;

import android.content.Context;
import android.widget.Toast;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.itslegit.niroigensuntharam.hivelabs.Application;
import com.itslegit.niroigensuntharam.hivelabs.Coordinate;
import com.itslegit.niroigensuntharam.hivelabs.Room;
import com.itslegit.niroigensuntharam.hivelabs.activities.MainActivity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static com.itslegit.niroigensuntharam.hivelabs.activities.MainActivity.AllRooms;
import static com.itslegit.niroigensuntharam.hivelabs.activities.MainActivity.Applications;
import static com.itslegit.niroigensuntharam.hivelabs.activities.MainActivity.Rooms;
import static com.itslegit.niroigensuntharam.hivelabs.activities.MainActivity.adapter;
import static com.itslegit.niroigensuntharam.hivelabs.activities.MainActivity.areApplicationsInitialized;
import static com.itslegit.niroigensuntharam.hivelabs.activities.MainActivity.areCoordinatesInitialized;
import static com.itslegit.niroigensuntharam.hivelabs.activities.MainActivity.areRoomsInitialized;
import static com.itslegit.niroigensuntharam.hivelabs.activities.MainActivity.dateString;

/**
 * Created by niroigensuntharam on 2018-02-11.
 */

public class FirebaseHelper {

    private FirebaseDatabase database = FirebaseDatabase.getInstance();
    private DatabaseReference mRootRef = database.getReference();
    private DatabaseReference mRoomRef = mRootRef.child("rooms");
    private DatabaseReference mAppRef = mRootRef.child("apps");
    private DatabaseReference mDateRef = mRootRef.child("date");
    private DatabaseReference mCoordRef = mRootRef.child("coordinates");
    private Context context;

    public FirebaseHelper(Context context) {
        this.context = context;
    }

    public enum FirebaseDataType {
        Room,
        App,
        Date,
        Coordinate
    }

    public void initializeDatabase() {

        mAppRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                if (!areApplicationsInitialized) {

                    Map<String, Application> apps = new HashMap<>();

                    for (DataSnapshot jobSnapshot : dataSnapshot.getChildren()) {
                        Application app = jobSnapshot.getValue(Application.class);
                        apps.put(jobSnapshot.getKey(), app);
                    }

                    ArrayList<Application> _apps = new ArrayList<>(apps.values());

                    Applications.clear();

                    Applications.addAll(_apps);

                    areApplicationsInitialized = true;
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        mRoomRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                if (!areRoomsInitialized) {

                    Map<String, Room> td = new HashMap<>();

                    for (DataSnapshot jobSnapshot : dataSnapshot.getChildren()) {
                        Room room = jobSnapshot.getValue(Room.class);
                        td.put(jobSnapshot.getKey(), room);
                    }

                    Rooms = new ArrayList<>(td.values());

                    for (Room room : Rooms) {
                        Room.VerifyIfAvalaible(room);
                    }

                    MainActivity.notifyAdapter();

                    Room.EarliestAvailableTime();

                    AllRooms.addAll(Rooms);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        mCoordRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (!areCoordinatesInitialized) {

                    for (DataSnapshot jobSnapshot : dataSnapshot.getChildren()) {
                        Coordinate coordinate = jobSnapshot.getValue(Coordinate.class);
                        MainActivity.coordinates.put(jobSnapshot.getKey(), coordinate);
                    }

                    areCoordinatesInitialized = true;
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        mRoomRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

                Room room = dataSnapshot.getValue(Room.class);

                for (int i = 0; i < Rooms.size(); i++) {
                    assert room != null;
                    if (Rooms.get(i).getRoomNumber().equals(room.getRoomNumber())) {
                        Rooms.set(i, room);
                    }
                }
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        mDateRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String dateStored = dataSnapshot.getValue(String.class);

                if (dateStored != null && !dateStored.equals(dateString)) { // Get all the data for the current date

                    Toast.makeText(context, "Date Changed Please Update", Toast.LENGTH_SHORT).show();

                    dateString = dateStored;

                    MainActivity.dateChanged = true;
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public void setDatabaseValue(Object value, FirebaseDataType type) {
        switch (type) {
            case Room: {
                mRoomRef.setValue(value);

                break;
            }
            case Date: {
                mDateRef.setValue(value);
                break;
            }
            case App: {
                mAppRef.setValue(value);
                break;
            }
            case Coordinate: {
                mCoordRef.setValue(value);
                break;
            }
            default: {
                break;
            }
        }
    }
}
