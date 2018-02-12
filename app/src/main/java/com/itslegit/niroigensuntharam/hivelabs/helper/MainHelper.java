package com.itslegit.niroigensuntharam.hivelabs.helper;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.itslegit.niroigensuntharam.hivelabs.Application;
import com.itslegit.niroigensuntharam.hivelabs.Room;
import com.itslegit.niroigensuntharam.hivelabs.activities.MainActivity;

import java.util.ArrayList;

import static com.itslegit.niroigensuntharam.hivelabs.activities.MainActivity.AllRooms;
import static com.itslegit.niroigensuntharam.hivelabs.activities.MainActivity.Applications;
import static com.itslegit.niroigensuntharam.hivelabs.activities.MainActivity.Rooms;

/**
 * Created by niro on 2018-02-12.
 */

public class MainHelper {
    private Context context;

    public MainHelper(Context context) {
        this.context = context;
    }

    public boolean isConnected() {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

        if (cm != null) {
            NetworkInfo netInfo = cm.getActiveNetworkInfo();
            if (netInfo != null && netInfo.isConnected()) {

                android.net.NetworkInfo wifi = cm.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
                android.net.NetworkInfo mobile = cm.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);

                return (mobile != null && mobile.isConnectedOrConnecting()) || (wifi.isConnectedOrConnecting());
            } else
                return false;
        } else
            return false;
    }

    public void verifyQueryRoom(String query) {

        if (!query.isEmpty()) {

            ArrayList<Room> roomsWithNumber = new ArrayList<>();

            for (Room room : AllRooms) {
                if (room.getRoomNumber().contains(query)) {
                    roomsWithNumber.add(room);
                }
            }

            Rooms.clear();
            Rooms.addAll(roomsWithNumber);
        } else {
            Rooms.clear();
            Rooms.addAll(AllRooms);

        }

        MainActivity.notifyAdapter();
    }

    public void verifyQueryApplication(String query) {
        if (!query.isEmpty()) {
            ArrayList<String> rooms = new ArrayList<>();

            for (Application app : Applications) {
                if (app.getApplication().toUpperCase().contains(query.toUpperCase())) {
                    rooms.addAll(app.getRoomsToUse());
                }
            }

            ArrayList<Room> roomsWithApp = new ArrayList<>();

            for (String _room : rooms) {
                for (Room room : AllRooms) {
                    if (room.getRoomNumber().contains(_room.substring(3, 6))) {
                        roomsWithApp.add(room);
                        break;
                    }
                }
            }

            Rooms.clear();
            Rooms.addAll(roomsWithApp);
        } else {
            Rooms.clear();
            Rooms.addAll(AllRooms);
        }

        MainActivity.notifyAdapter();
    }
}
