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

    String nextTime;

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

    public String getNextTime() {return nextTime;}

    public void setNextClass(String next_class) {nextClass = next_class;}

    public String getNextClassTime() {return nextClassTime;}

    public void setNextClassTime(String next_class_time) {nextClassTime = next_class_time;}

    public void setCurrentClass(String currentCourse) {currentClass = currentCourse;}

    public void setNextTime(String next_time) {nextTime = next_time;}

    Room (String room, String cap, String datee, Document doc)
    {
        // Setting the properties
        roomNumber = room;
        capacity = cap;

        // Retrieving the current date
        DateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");
        Date date = new Date();

        final String dateString = datee;//"20171101";//dateFormat.format(date);

        try {
            // Getting the information of the async task
            Document document = doc;

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

    // Will be used to verify whether a room is currently available
    // and if it is, then it will be added to the RoomsNowAvailable list
    public static void VerifyIfAvalaible(Room room)
    {
        // The availability of a certain lab
        boolean isAvailable = true;

        boolean isNextClass = false;

        for (int i = 0; i < room.TimeList.size(); i++)
        {
            // Getting the time of a certain course
            // Ex: 12:45 - 13:55
            String[] time = room.TimeList.get(i).split("-");

            // Retrieving the start time
            // Ex: 12:45
            String[] startTime = time[0].split(":");

            // Retrieving the end time
            // Ex: 13:55
            String[] endTime = time[1].split(":");

            // Getting an integer value for the timeString
            // Ex: 1500
            int TimeNow = Integer.parseInt(MainActivity.timeString);

            // Getting an integer value for the startTime
            // Ex: 1245
            int StartTime = Integer.parseInt(startTime[0].trim() + startTime[1].trim());

            // Getting an integer value for the endTime
            // Ex: 1355
            int EndTime = Integer.parseInt(endTime[0].trim() + endTime[1].trim());

            if (TimeNow < StartTime && !isNextClass)
            {
                room.setNextClass(room.getClassList().get(i));
                room.setNextTime(room.getTimeList().get(i));
                isNextClass = true;
            }

            // Verifying whether the room is currently unavailable
            if (TimeNow >= StartTime && TimeNow < EndTime)
            {
                // Set the availability to false
                isAvailable = false;

                room.setCurrentClass(room.getClassList().get(i));

                // Break from the loop
                break;
            }
        }

        // If the room is available add it to the list of available rooms
        if (isAvailable) {
            MainActivity.RoomsNowAvailable.add(room);
        }
    }
}

