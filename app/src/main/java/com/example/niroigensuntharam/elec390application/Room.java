package com.example.niroigensuntharam.elec390application;

import android.os.AsyncTask;
import android.view.View;
import android.widget.ProgressBar;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;


public class Room {

    // Storing the list of different time slots for a certain room
    ArrayList<String> TimeList = new ArrayList<>();
    
    // Storing the list of classes for a certain room
    ArrayList<String> ClassList = new ArrayList<>();
    
    // The room number of the room
    String roomNumber;
    
    // The number of people that can be within a certain room
    String capacity;

    String nextClass;

    String currentClass;

    public ArrayList<String> getTimeList() {
        return TimeList;
    }

    public ArrayList<String> getClassList() {
        return ClassList;
    }

    public String getRoomNumber() {
        return roomNumber;
    }

    public String getCapacity() {
        return capacity;
    }

    public String getCurrentClass() {return currentClass;}

    public String getNextClass() {return nextClass;}

    public void setNextClass(String next_class) {nextClass = next_class;}

    public void setCurrentClass(String currentCourse) {currentClass = currentCourse;}

    Room (String room, String cap)
    {
        // Creating an object of the async class
        GetRoomInfoAsync getRoomInfoAsync = new GetRoomInfoAsync();

        // The url will depend on the room number being provided
        // Executing the async task
        getRoomInfoAsync.execute("https://calendar.encs.concordia.ca/month.php?user=_NUC_LAB_H" + room);

        // Setting the properties
        roomNumber = room;
        capacity = cap;

        // Retrieving the current date
        DateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");
        Date date = new Date();

        final String dateString = dateFormat.format(date);

        try {
            // Getting the information of the async task
            Document document = getRoomInfoAsync.get();

            // Getting the all the days which the lab is open
            Elements elements = document.select("a[class=layerentry]");

            // Finding all the courses that will be held on the dateString
            for (int i = 0 ; i < elements.select("a[href*=" + dateString + "]").size(); i++)
            {
                Element element = elements.select("a[href*=" + dateString + "]").get(i);

                String eventid = element.attributes().get("id");

                Element event = document.select("dl#eventinfo-" + eventid).first();

                String temp = event.childNode(7).toString().split("\n")[1];

                TimeList.add(temp);

                temp = element.childNode(1).toString().split(";")[1];

                ClassList.add(temp);
            }
        }
        catch (Exception ex)
        {
        }
    }
}

// The AsyncTask is used since the application is doing a web call
class GetRoomInfoAsync extends AsyncTask<String, Void, Document> {
    private ProgressBar progressBar;

    @Override
    protected Document doInBackground(String... params) {

        try {
            // Connecting to the website and retrieving the room information
            Document doc = Jsoup.connect(params[0]).get();

            return doc;
        }
        catch (IOException ex)
        {
        }

        return null;
    }

    @Override
    protected void onPreExecute(){

    }
}

