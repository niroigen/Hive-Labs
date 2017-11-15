package com.example.niroigensuntharam.elec390application;

import android.app.IntentService;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.ProgressBar;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;


public class Room implements Comparable<Room> {

    // Storing the list of different time slots for a certain room
    private ArrayList<String> TimeList = new ArrayList<>();
    
    // Storing the list of classes for a certain room
    private ArrayList<String> ClassList = new ArrayList<>();
    
    // The room number of the room
    String roomNumber;
    
    // The number of people that can be within a certain room
    String capacity;

    String nextClass;

    int nextTime = 0;

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

    public int getNextTime() {return nextTime;}

    public void setNextClass(String next_class) {nextClass = next_class;}

    public void setCurrentClass(String currentCourse) {currentClass = currentCourse;}

    public void setNextTime(int next_time) {nextTime = next_time;}

    public void setRoomNumber(String room_Number) {roomNumber = room_Number;}

    public void setClassList(ArrayList<String> class_List) {ClassList = class_List;}

    public void setTimeList(ArrayList<String> timeList) {TimeList = timeList;}

    public void setCapacity(String _capacity) {capacity = _capacity;}

    Room (String room, String cap, String datee, Document doc)
    {
        // Setting the properties
        roomNumber = room;
        capacity = cap;

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

    public static void EarliestAvailableTime()
    {
        for (int i = 0; i < MainActivity.RoomsNowAvailable.size(); i++) {

            if (MainActivity.RoomsNowAvailable.get(i).TimeList.size() > 0) {

                String nowTime = MainActivity.timeString;// SimpleDateFormat("HHmm").format(new Date());

                // Getting an integer value for the startTime
                // Ex: 1245
                int StartTime = MainActivity.RoomsNowAvailable.get(i).getNextTime();

                int NowTime = Integer.parseInt(nowTime);

                if (NowTime < StartTime && MainActivity.earliestTime == null) {
                    MainActivity.earliestTime = Integer.toString(StartTime);
                } else if (MainActivity.earliestTime != null
                        && StartTime < Integer.parseInt(MainActivity.earliestTime)
                        && NowTime < StartTime) {
                    MainActivity.earliestTime = Integer.toString(StartTime);
                }
            }
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
                room.setNextTime(StartTime);
                isNextClass = true;
            }
            else if (!isNextClass){
                room.setNextClass(null);
            }

            // Verifying whether the room is currently unavailable
            if (TimeNow >= StartTime && TimeNow <= EndTime) {
                // Set the availability to false
                isAvailable = false;

                room.setCurrentClass(room.getClassList().get(i));

                room.nextTime = -1;

                // Break from the loop
                break;
            }
            else if(!isNextClass) {
                room.setCurrentClass(null);
            }
        }

        if (room.nextClass == null && room.currentClass == null)
        {
            room.nextTime = 2400;
        }

        // If the room is available add it to the list of available rooms
        if (isAvailable) {
            MainActivity.RoomsNowAvailable.add(room);
        }
    }

    public static void SortRooms() {
        Collections.sort(MainActivity.Rooms);
    }

    @Override
    public int compareTo(Room room) {

            int compareTime = room.getNextTime();
            int diff = compareTime - this.getNextTime();

            return diff;
    }
}

