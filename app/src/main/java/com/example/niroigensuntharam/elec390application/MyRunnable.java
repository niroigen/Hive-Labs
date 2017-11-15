package com.example.niroigensuntharam.elec390application;

import android.graphics.Paint;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by niroigensuntharam on 2017-11-10.
 */

public class MyRunnable implements Runnable{
    private final String url;
    private String RoomNumber;
    private String Capacity;
    private String Date;

    MyRunnable(String url, String RoomNumber, String Capacity, String Date) {
        this.url = url;
        this.RoomNumber = RoomNumber;
        this.Capacity = Capacity;
        this.Date = Date;
    }

    @Override
    public void run() {

        try {
            // The url will depend on the room number being provided
            // Executing the async task
            // Connecting to the website and retrieving the room information
            Document doc = Jsoup.connect(url).get();

            // Passing in the room number, its capacity and the date
            Room room = new Room(RoomNumber, Capacity, Date, doc);

            // Verifying whether the room is currently available or not
            Room.VerifyIfAvalaible(room);

            // Adding the room to the list of rooms
            MainActivity.Rooms.add(room);
        } catch (Exception e) {
        }
    }
}
