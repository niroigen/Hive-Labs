package com.itslegit.niroigensuntharam.hivelabs;

import com.itslegit.niroigensuntharam.hivelabs.activities.MainActivity;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

public class MyRunnable implements Runnable{
    private final String url;
    private String RoomNumber;
    private String Date;

    MyRunnable(String url, String RoomNumber, String Capacity, String Date) {
        this.url = url;
        this.RoomNumber = RoomNumber;
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
            Room room = new Room(RoomNumber, Date, doc);

            // Verifying whether the room is currently available or not
            Room.VerifyIfAvalaible(room);

            // Adding the room to the list of rooms
            MainActivity.Rooms.add(room);
        } catch (Exception e) {
            // Exception being thrown
        }
    }
}
